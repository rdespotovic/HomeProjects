package com.instrumental.util.counter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EventCounterUtil {

	private static final Logger logger = LogManager.getLogger(EventCounterUtil.class);

	public static void launchReaderAndWriterThreads(int maxTestReaders, int maxTestWriters) {
		EventCounter eventCounter = EventCounter.INSTANCE;
		ExecutorService writerExecutor = Executors.newFixedThreadPool(maxTestWriters / 200);

		// start writers
		for (int i = 0; i < maxTestWriters; i++) {
			Runnable thread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(5);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					eventCounter.recordEvent();
				}
			});
			// execute at some time in the future
			writerExecutor.execute(thread);
		}
		// start readers
		ExecutorService readerExecutor = Executors.newFixedThreadPool(500);
		for (int i = 0; i < maxTestReaders; i++) {
			Runnable thread = new Thread(new Runnable() {

				@Override
				public void run() {
					if (logger.isInfoEnabled())
						logger.info("Number of events in the last 1 minute: " + eventCounter.getEventCount(1));

				}
			});
			// execute at some time in the future
			readerExecutor.execute(thread);
		}
		// initiate orderly shutdown while waiting for the threads to complete
		writerExecutor.shutdown();
		// it takes some time to fully terminate the thread pool
		while (!writerExecutor.isTerminated()) {

		}
		readerExecutor.shutdown();
		while (!readerExecutor.isTerminated()) {

		}
	}
}