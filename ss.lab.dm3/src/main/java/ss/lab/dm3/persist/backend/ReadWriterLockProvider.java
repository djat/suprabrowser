package ss.lab.dm3.persist.backend;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class ReadWriterLockProvider {

	private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	
	public void readLock() {
		readWriteLock.readLock().lock();
	}

	public void readUnlock() {
		readWriteLock.readLock().unlock();
	}

	public void writeLock() {
		readWriteLock.writeLock().lock();
	}

	public void writeUnlock() {
		readWriteLock.writeLock().unlock();
	}

}
