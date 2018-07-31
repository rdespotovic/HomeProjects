package com.instrumental.util.counter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 * @author Rada Despotovic<br>
 *         This is a library that allows clients to record non-specific events
 *         of any kind. It also returns an estimated number of events that
 *         happened over a user-specified amount of time. The events are stored
 *         in memory only and NOT persisted in a database or file system. Usage
 *         of this library requires client application restart in order to
 *         change the following initialization parameters: <br>
 *         <br>
 *         -maxPeriodInSeconds = maximum amount of time the events will be
 *         stored in memory. Default value is 86400 seconds (1 day). <br>
 *         <br>
 *         -strongWriteConsistencyEnabled = whether strong data consistency with
 *         pessimistic locking is required for write requests. When false,
 *         requests may get occasionally dropped due to race conditions (1-5 in
 *         100,000 requests). Usage of strongWriteConsistencyEnabled is suitable
 *         in the following conditions: <br>
 *         -write-heavy applications<br>
 *         -when maxPeriodInSeconds is very high<br>
 *         -when clients (only) require an approximate number of events to be
 *         known
 * 
 */
public enum EventCounter {

	INSTANCE;

	private static final Logger logger = LogManager.getLogger(EventCounter.class);
	private static Map<Integer, Events> eventCounts;

	private static Object[] locks;

	private static class Events {

		@Override
		public String toString() {
			return "Events [timestamp=" + timestamp + ", count=" + count + "]";
		}

		// actual event time in seconds
		long timestamp;
		// how many times it occurred
		AtomicInteger count;

		Events(long timestamp, AtomicInteger count) {
			this.timestamp = timestamp;
			this.count = count;

		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((count == null) ? 0 : count.hashCode());
			result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Events other = (Events) obj;
			if (count == null) {
				if (other.count != null)
					return false;
			} else if (!(count.get() == other.count.get()))
				return false;
			if (timestamp != other.timestamp)
				return false;
			return true;
		}

	}

	private static int maxPeriodInSeconds;
	private static boolean strongWriteConsistencyEnabled;

	private static int conflicts;
	private static final Calendar serverCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

	static {
		// set server time to midnight of the day the library was initialized i.e. the
		// program started
		serverCalendar.set(Calendar.HOUR_OF_DAY, 0);
		serverCalendar.set(Calendar.MINUTE, 0);
		serverCalendar.set(Calendar.SECOND, 0);
		serverCalendar.set(Calendar.MILLISECOND, 0);

		// load properties from an external config file
		Properties prop = new Properties();
		try (InputStream input = new FileInputStream("config.properties");) {
			prop.load(input);

			maxPeriodInSeconds = Integer.valueOf(prop.getProperty("maxPeriodInSeconds"));
			strongWriteConsistencyEnabled = Boolean.valueOf(prop.getProperty("strongWriteConsistencyEnabled"));

		} catch (IOException e) {
			// recover from errors but issue a warning
			if (logger.isWarnEnabled())
				logger.warn(
						"Unable to load the properties from the configuration file config.properties, using defaults: "
								+ e.getMessage());
			maxPeriodInSeconds = 86400;
			strongWriteConsistencyEnabled = false;

		}
		if (strongWriteConsistencyEnabled) {
			// fixed-size map that should never be resized
			eventCounts = new HashMap<Integer, Events>(maxPeriodInSeconds, 1f);
			locks = new Object[maxPeriodInSeconds];
			// for manual lock management/synchronization
			for (int i = 0; i < locks.length; i++)
				locks[i] = new Object();
		} else
			eventCounts = new ConcurrentHashMap<Integer, Events>(maxPeriodInSeconds, 1f);
	}

	/**
	 * Record an event i.e. signal that a single event happened.
	 */
	public void recordEvent() {

		long start = System.nanoTime();
		// Use time difference between midnight and now as the request's timeStamp. Used
		// midnight instead of standard January 1, 1970 UTC to reduce storage
		// requirements (it usually means a difference of 4 bytes less per request)
		long offset = System.currentTimeMillis() - serverCalendar.getTimeInMillis();
		long timestamp = offset / 1000L;
		final int index = (int) (timestamp % maxPeriodInSeconds);
		if (strongWriteConsistencyEnabled)
			synchronized (locks[index]) {
				Events event = eventCounts.get(index);
				if (event == null) {
					event = new Events(timestamp, new AtomicInteger(0));
					eventCounts.put(index, event);
				}
				incrementOrReset(true, index, timestamp, event);
			}
		else
			concurrentInsertOrReplace(index, timestamp);

		long duration = System.nanoTime() - start;
		if (logger.isDebugEnabled())
			logger.debug("Event recording duration: " + duration
					+ " nanoseconds, current number of buckets/placeholders: " + eventCounts.size());

	}

	/**
	 * Insert or replace in optimistic locking mode i.e. retry until the write
	 * succeeded. puIfAbsent and replace methods below are thread-safe by
	 * themselves, but the entire "transaction" is not (entirely)
	 * synchronized/thread-safe.
	 * 
	 * @param index
	 *            The key of the value that needs to be modified
	 * @param timestamp
	 *            Request's actual timeStamp.
	 */
	private void concurrentInsertOrReplace(Integer index, long timestamp) {
		for (int i = 0;; i++) {
			if (i > 0)
				if (logger.isDebugEnabled())
					logger.debug("Write attempt number " + i + " for index " + index + " and timestamp " + timestamp);
			Events oldEvent = eventCounts.putIfAbsent(index, new Events(timestamp, new AtomicInteger(0)));
			if (oldEvent == null) {
				if (logger.isDebugEnabled())
					logger.warn("Stored event + " + eventCounts.get(index));
				return;
			}
			final Events newEvent = incrementOrReset(false, index, timestamp, oldEvent);
			// replace will succeed if oldValue was unchanged by another thread, otherwise
			// need to try again
			if (eventCounts.replace(index, oldEvent, newEvent))
				return;
		}
	}

	/**
	 * Increment the count for the timeStamp at key/index. If the timeStamp is
	 * expired, changed the count to 1
	 * 
	 * @param strongWriteConsistencyEnabled
	 * @param index
	 * @param timestamp
	 * @param oldEvent
	 * @return
	 */
	private Events incrementOrReset(boolean strongWriteConsistencyEnabled, int index, long timestamp, Events oldEvent) {

		Events newEvent = null;
		if (strongWriteConsistencyEnabled)
			// directly update referenced hashMap entry
			newEvent = oldEvent;
		else
			// otherwise prepare a new object that will "optimistically" replace the old
			// object
			newEvent = new Events(oldEvent.timestamp, oldEvent.count);
		if (timestamp - oldEvent.timestamp >= maxPeriodInSeconds) {
			newEvent.timestamp = timestamp;
			// reset the count
			newEvent.count.set(1);
		} else { // timeStamp still not too old
			if (timestamp != oldEvent.timestamp) {
				// rarely, different timeStamps correspond to the same bucket when reduced to
				// the size of the main hashMap data structure. Keep track of these conflicts
				// but increment the count nevertheless
				conflicts++;
				if (logger.isDebugEnabled())
					logger.debug("Conflict at bucket " + index + ", total number of conflicts so far = " + conflicts);
			}
			newEvent.timestamp = timestamp;
			// increment the count
			newEvent.count.getAndIncrement();
		}
		return newEvent;

	}

	/**
	 * A non-blocking lock-free method that returns an estimated number of events
	 * that happened over a user-specified amount of time until current time. Events
	 * that are still in progress are ignored.
	 * 
	 * @param interval
	 *            Number of minutes in the past.
	 * @return Number of occurred events.
	 */
	public long getEventCount(int interval) { // interval only 1 to 5
		if (interval <= 0)
			throw new IllegalArgumentException("Enter a positive number of minutes");
		if (interval > maxPeriodInSeconds / 60)
			throw new IllegalArgumentException("Maximum number of minutes allowed is: " + maxPeriodInSeconds / 60);
		long offset = System.currentTimeMillis() - serverCalendar.getTimeInMillis();
		long timestamp = offset / 1000L;
		AtomicLong total = new AtomicLong(0);
		// a debugging counter
		AtomicInteger totalEmptyBuckets = new AtomicInteger(0);
		// Check all buckets; timeStamp must be within the requested interval.
		// There is no guarantee that consecutive requests will always be stored in
		// consecutive buckets
		int index = (int) (timestamp % maxPeriodInSeconds);
		// start search closer to where current timeStamp would fall in the map. Newer
		// events usually found closer to the end of the map
		boolean searchBackwards = false;
		if (index >= maxPeriodInSeconds / 2)
			searchBackwards = true;
		int i;
		if (searchBackwards)
			for (i = maxPeriodInSeconds - 1; i >= 0; i--)
				updateEventTotal(i, interval, timestamp, total, totalEmptyBuckets);
		else
			for (i = 0; i < maxPeriodInSeconds; i++)
				updateEventTotal(i, interval, timestamp, total, totalEmptyBuckets);

		if (logger.isDebugEnabled())
			logger.debug("Total number of empty buckets: " + totalEmptyBuckets);
		return total.get();

	}

	/**
	 * Update the total number of events.
	 * 
	 * @param index
	 * @param interval
	 * @param timestamp
	 * @param total
	 * @param totalEmptyBuckets
	 */

	private void updateEventTotal(int index, int interval, long timestamp, AtomicLong total,
			AtomicInteger totalEmptyBuckets) {
		Events event = eventCounts.get(index);
		if (event != null) {
			if (timestamp - event.timestamp <= 60 * interval)
				total.getAndAdd(event.count.get());
			else if (logger.isDebugEnabled())
				logger.debug("An event with timestamp " + timestamp + " was ignored because time difference is: "
						+ (timestamp - event.timestamp));
		} else
			totalEmptyBuckets.getAndIncrement();
	}

	/**
	 * 
	 * @return Returns true when strong consistency is enabled for write requests.
	 */

	public static boolean isStrongWriteConsistencyEnabled() {
		return strongWriteConsistencyEnabled;
	}

	/**
	 * 
	 * @return Returns maximum time window that can be inspected for event count.
	 */

	public static int getMaxPeriodInSeconds() {
		return maxPeriodInSeconds;
	}
}