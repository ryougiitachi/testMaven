package per.itachi.test.springboot.zookeeper;

import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DistributedLockRunnable implements Runnable {
	
	private final Logger logger = LoggerFactory.getLogger(DistributedLockRunnable.class);
	
	private ZooKeeper zk;

	public DistributedLockRunnable(ZooKeeper zk) {
		this.zk = zk;
	}
	
	@Override
	public void run() {
		try {
			Thread.sleep(20*1000);
		} 
		catch (InterruptedException e) {
			logger.error("Error occurs when closing zookeeper. ", e);
		}
		if (zk != null) {
			try {
				zk.close();
			} 
			catch (InterruptedException e) {
				logger.error("Error occurs when closing zookeeper. ", e);
			}
		}
	}

}
