package per.itachi.test.redis.lock;

public interface DistributedLock {
	
	/**
	 * @param timeout	milliseconds;
	 * */
	boolean lock(int timeout);
	
	void unlock();
}
