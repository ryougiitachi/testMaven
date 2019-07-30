package per.itachi.test.springboot.zookeeper;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import per.itachi.test.springboot.zookeeper.watcher.DistributedLockWatcher;

public class EntryZookeeper {
	
	private static final Logger logger = LoggerFactory.getLogger(EntryZookeeper.class);

	public static void main(String[] args) {
		CountDownLatch latch = new CountDownLatch(1);
		ZooKeeper zk = null;
		try {
			logger.info("Initialising zookeeper client... ");
			Watcher watcher = new DistributedLockWatcher(latch);
			zk = new ZooKeeper("127.0.0.1:2181", 10 * 1000, watcher);
//			zk.register(watcher);
			latch.await();
			logger.info("Finish!");
			Stat stat = zk.exists("/fate", true);
			if (stat == null) {
				logger.info("The znode {} doesn't exist. ", "/fate");
			}
			else {
				logger.info("The znode {} is {}. ", "/fate", stat);
			}
			//to test a variety of zookeeper methods. 
			List<String> listZnode = zk.getChildren("/", false);
			logger.info("The znode under {} contains {}. ", "/", listZnode);
			Thread.sleep(20 * 1000);
		} 
		catch (IOException | InterruptedException e) {
			logger.error("Error occurs when initialising zookeeper. ", e);
		} 
		catch (KeeperException e) {
			logger.error("Error occurs when executing zookeeper, code {}, path {}. ", e.code(), e.getPath(), e);
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
