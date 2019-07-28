package per.itachi.test.springboot.zookeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.zookeeper.ZooKeeper;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import per.itachi.test.springboot.zookeeper.watcher.DistributedLockWatcher;

public class EntryZookeeper {
	
	private static final Logger logger = LoggerFactory.getLogger(EntryZookeeper.class);

	public static void main(String[] args) {
		CountDownLatch latch = new CountDownLatch(1);
		ZooKeeper zk = null;
		try {
			logger.info("Initialising zookeeper client... ");
			zk = new ZooKeeper("127.0.0.1:2181", 10 * 1000, new DistributedLockWatcher(latch));
			latch.await();
			logger.info("Start executing threads !");
			ExecutorService executorService = Executors.newFixedThreadPool(1);
			executorService.execute(new DistributedLockRunnable(zk));
		} 
		catch (IOException|InterruptedException e) {
			logger.error("Error occurs when initialising zookeeper. ", e);
		} 
		finally {
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

}
