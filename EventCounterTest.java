package com.instrumental.util.counter.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.instrumental.util.counter.EventCounter;
import com.instrumental.util.counter.EventCounterUtil;

class EventCounterTest {
	@Test
	// test whether EventCounter is indeed singleton
	final void testEventCounterSingleton() {
		EventCounter eventCounter1 = EventCounter.INSTANCE;
		EventCounter eventCounter2 = EventCounter.INSTANCE;
		// 2 requests for the first
		eventCounter1.recordEvent();
		eventCounter1.recordEvent();
		// 2 requests for the second
		eventCounter2.recordEvent();
		eventCounter2.recordEvent();
		long count1 = eventCounter1.getEventCount(1);
		long count2 = eventCounter1.getEventCount(1);
		assertEquals(count1, count2);
		if (EventCounter.isStrongWriteConsistencyEnabled()) {
			// counts must be the same
			assertTrue(4 == count1);
			assertTrue(4 == count2);
		}
		// "destroy" the first one
		eventCounter2 = null;
		count1 = eventCounter1.getEventCount(1);
		// the count must be unchanged
		if (EventCounter.isStrongWriteConsistencyEnabled())
			// counts must be the same
			assertTrue(4 == count1);
	}

	@Test
	final void testIncorrectInterval() {
		EventCounter eventCounter = EventCounter.INSTANCE;
		try {
			// test with interval > maxPeriodInSeconds/60
			assertEquals(0, eventCounter.getEventCount(Integer.MAX_VALUE));
		} catch (Exception e) {
			// expected to throw an exception...
			assertFalse(false);
		}

	}

	@Test
	final void testNegativeEvents() {
		EventCounter eventCounter = EventCounter.INSTANCE;
		try {
			assertEquals(0, eventCounter.getEventCount(-1));
		} catch (Exception e) {
			assertFalse(false);
		}

	}

	@Test
	final void testGetExpiredEvents() {
		EventCounter eventCounter = EventCounter.INSTANCE;
		try {
			eventCounter.recordEvent();
			// sleep for more than a minute
			Thread.sleep(61000);
			// 0 events qualify
			assertEquals(0, eventCounter.getEventCount(1));
		} catch (Exception e) {
			assertFalse(true);
		}

	}

	@Test
	// simulate write-heavy usage with number of readers 10x lower than number of
	// writers and greater than the main data structure size
	final void testOverwritingOfEvents() {
		try {
			EventCounter eventCounter = EventCounter.INSTANCE;
			EventCounterUtil.launchReaderAndWriterThreads(10000, 100000);
			if (EventCounter.isStrongWriteConsistencyEnabled())
				assertEquals(100000,
						eventCounter.getEventCount(Math.min(5, EventCounter.getMaxPeriodInSeconds() / 60)));
		} catch (Exception e) {
			// should be no unexpected errors and should improve/affect code coverage. Check
			// JUnit execution time; it can vary greatly based on
			// parameters in config.properties
			assertFalse(true);
		}
	}

	@Test
	/**
	 * A proof of concept - general test of hashMap's writing performance of 100K
	 * threads hitting 100K buckets simultaneously. Typically takes between 5 and 7
	 * seconds for all the threads to complete; please check this method's JUnit
	 * statistics in your IDE
	 */
	final void testHashMapWriteLockBehavior() {
		int maxThreads = 100000;
		Map<Integer, Object> events = new HashMap<Integer, Object>(maxThreads, 1f);
		// guard each bucket with a lock
		Object[] locks = new Object[maxThreads];

		for (int i = 0; i < locks.length; i++)
			locks[i] = new Object();
		Thread[] threads = new Thread[maxThreads];
		for (int i = 0; i < maxThreads; i++) {
			threads[i] = new Thread(new Runnable() {
				@Override
				public void run() {
					int index = (int) ((System.currentTimeMillis() / 1000L) % maxThreads);
					synchronized (locks[index]) {
						events.put(index, new Object());
					}
				}

			});
			threads[i].start();
		}
		// wait for all children threads to return to the main thread
		for (int i = 0; i < maxThreads; i++)
			try {
				threads[i].join(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	}

}
