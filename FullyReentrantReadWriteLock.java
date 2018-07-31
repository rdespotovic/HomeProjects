import java.util.HashMap;
import java.util.Map;

public class FullyReentrantReadWriteLock {

	class ReadWriteLock {

		private Map<Thread, Integer> readingThreads = new HashMap<Thread, Integer>();

		// how many write accesses really occurred
		private int writeAccesses = 0;
		private int writeRequests = 0;
		private Thread writingThread = null;

		public synchronized void lockRead() throws InterruptedException {
			Thread callingThread = Thread.currentThread();
			while (!canGrantReadAccess(callingThread))
				wait();

			readingThreads.put(callingThread, (getReadAccessCount(callingThread) + 1));
		}

		private boolean canGrantReadAccess(Thread callingThread) {
			// "demotion" from Write to Read!
			if (isOneAndOnlyWriter(callingThread))
				return true;
			// read re-entrance is only granted if no threads are currently writing to the
			// resource
			if (hasWriter())
				return false;
			// to support re-entrance, if the calling thread already has read access this
			// takes precedence over any
			// writeRequests
			if (isActiveReader(callingThread))
				return true;
			if (hasWriteRequests())
				return false;
			return true;
		}

		public synchronized void unlockRead() {
			Thread callingThread = Thread.currentThread();
			if (!isActiveReader(callingThread))
				throw new IllegalMonitorStateException(
						"Calling Thread does not" + " hold a read lock on this ReadWriteLock");
			int accessCount = getReadAccessCount(callingThread);
			if (accessCount == 1)
				readingThreads.remove(callingThread);
			else
				readingThreads.put(callingThread, (accessCount - 1));
			// notify more than 1 thread; could be either multiple readers or another single
			// writer
			notifyAll();
		}

		public synchronized void lockWrite() throws InterruptedException {
			writeRequests++;
			Thread callingThread = Thread.currentThread();
			while (!canGrantWriteAccess(callingThread))
				wait();
			writeRequests--;
			// should never exceed one!
			writeAccesses++;
			// could also be solved with a single entry map similar to readingThreads
			writingThread = callingThread;
		}

		public synchronized void unlockWrite() throws InterruptedException {
			if (!isOneAndOnlyWriter(Thread.currentThread()))
				throw new IllegalMonitorStateException(
						"Calling Thread does not" + " hold the write lock on this ReadWriteLock");
			writeAccesses--;
			if (writeAccesses == 0)
				writingThread = null;
			notifyAll();
		}

		// only private methods below
		private boolean canGrantWriteAccess(Thread callingThread) {
			// promotion from Read to Write, provided there are no other readers (because
			// Write not supposed to work if reads are in progress - pretty strict actually)
			if (isOnlyReader(callingThread))
				return true;
			// cannot be other readers
			if (hasReaders())
				return false;
			// cannot be other writers
			if (writingThread == null)
				return true;
			// for reentrant writing
			if (!isOneAndOnlyWriter(callingThread))
				return false;
			return true;
		}

		private int getReadAccessCount(Thread callingThread) {
			Integer accessCount = readingThreads.get(callingThread);
			if (accessCount == null)
				return 0;
			return accessCount.intValue();
		}

		private boolean hasReaders() {
			return readingThreads.size() > 0;
		}

		private boolean isActiveReader(Thread callingThread) {
			return readingThreads.get(callingThread) != null;
		}

		private boolean isOnlyReader(Thread callingThread) {
			return readingThreads.size() == 1 && readingThreads.get(callingThread) != null;
		}

		private boolean hasWriter() {
			return writingThread != null;
		}

		private boolean isOneAndOnlyWriter(Thread callingThread) {
			return writingThread == callingThread;
		}

		private boolean hasWriteRequests() {
			return this.writeRequests > 0;
		}

	}

}
