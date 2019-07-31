package per.itachi.test.springboot.zookeeper;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import per.itachi.test.springboot.lock.DistributedLock;
import per.itachi.test.springboot.lock.ZookeeperDistributedLock;
import per.itachi.test.springboot.zookeeper.watcher.DistributedLockWatcher;

public class EntryZookeeper {
	
	private static final Logger logger = LoggerFactory.getLogger(EntryZookeeper.class);

	public static void main(String[] args) {
		CountDownLatch latch = new CountDownLatch(1);
		ZooKeeper zk = null;
		try {
			logger.info("Initialising zookeeper client... ");
			Watcher watcher = new DistributedLockWatcher(latch);
			zk = new ZooKeeper("127.0.0.1:2181", 10 * 1000, watcher);// this watcher is required, otherwise NullPointerException will be thrown. 
//			zk.register(watcher);
			latch.await();
			logger.info("Finish!");
			Stat stat = zk.exists("/fate", true);
			if (stat == null) {
				logger.info("The znode {} doesn't exist. ", "/fate");
			}
			else {
				logger.info("The znode {} is {}. ", "/fate", stat);
				logger.info("The znode's ACL {} is {}. ", "/fate", zk.getACL("/fate", stat));
			}
			//to test a variety of zookeeper methods. 
			List<String> listZnode = zk.getChildren("/", false);
			showACL(zk.getACL("/fate", stat), "/fate");
			testDistributedLock(zk);
			
			Thread.sleep(10 * 1000);
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
	
	private static void showACL(List<ACL> acls, String path) {
		for (ACL acl : acls) {
			logger.info("The znode under {} contains {}/{}/{}/{}. ", path, acl, acl.getId().getId(), acl.getId().getScheme(), acl.getPerms());
		}
	}
	
	private static void testDistributedLock(ZooKeeper zk) throws InterruptedException {
		DistributedLock lock = new ZookeeperDistributedLock(zk, "user");
		lock.lock();
		try {
			Thread.sleep(10 * 1000);
		} 
		finally {
			lock.unlock();
		}
	}
}
