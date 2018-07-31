package com.instrumental.util.counter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 * @author Rada Despotovic Demonstration tool for the event counter library in
 *         EventCounter.java
 *
 */
public class EventCounterDemo {

	private static int maxTestReaders;
	private static int maxTestWriters;
	private static int maxPeriodInSeconds;
	private static final Logger logger = LogManager.getLogger(EventCounterDemo.class);

	static {

		Properties prop = new Properties();

		try (InputStream input = new FileInputStream("config.properties");) {
			prop.load(input);
			maxPeriodInSeconds = Integer.valueOf(prop.getProperty("maxPeriodInSeconds"));
			maxTestReaders = Integer.valueOf(prop.getProperty("maxTestReaders"));
			maxTestWriters = Integer.valueOf(prop.getProperty("maxTestWriters"));

		} catch (IOException e) {
			maxTestReaders = 100000;
			maxTestWriters = 100000;
		}
	}

	public static void main(String args[]) {

		long start = System.nanoTime();
		long beforeUsedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		long count;
		EventCounterUtil.launchReaderAndWriterThreads(maxTestReaders, maxTestWriters);
		long afterUsedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		long actualMemUsed = afterUsedMem - beforeUsedMem;
		if (logger.isInfoEnabled())
			logger.info("Demo used " + actualMemUsed + " bytes of memory.");
		// check number of events in the last 10 minutes
		count = EventCounter.INSTANCE.getEventCount(Math.min(10, maxPeriodInSeconds / 60));
		long duration = System.nanoTime() - start;
		if (logger.isInfoEnabled())
			logger.info("Demo duration: " + duration / 1000000000 + " seconds, total number of events: " + count);
	}

}
