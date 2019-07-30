package per.itachi.test.springboot.lock;

public interface DistributedLock {
	
	void lock();
	
	void unlock();
}
