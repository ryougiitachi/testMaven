package per.itachi.test.springboot.lock;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZookeeperDistributedLock implements DistributedLock {
	
	private static final byte[] CONSTANT_NODE_DATE = new byte[0]; 
	
	private final Logger logger = LoggerFactory.getLogger(ZookeeperDistributedLock.class);
	
	private ZooKeeper client;
	
	private String path;
	
	private String name;
	
	private String lockID;
	
	private CountDownLatch latch;
	
	private Watcher watcher;
	
	public ZookeeperDistributedLock(ZooKeeper client, String name) {
		this.client = client;
		this.name = name;
		this.latch = new CountDownLatch(1);
		this.watcher = new DistributedLockWatcher();
		try {
			this.path = "/lock-"+name;
			Stat stat = client.exists(this.path, watcher);
			if (stat == null) {
				client.create(this.path, CONSTANT_NODE_DATE, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			}
		} 
		catch (KeeperException | InterruptedException e) {
			logger.warn("", e);
		}
	}
	
	@Override
	public void lock() {
		boolean acquired = false;
		try {
			if (lockID == null) {
				String strZnodePath = client.create(this.path + "/" + this.name, CONSTANT_NODE_DATE, Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
				String strLockID = strZnodePath.substring(strZnodePath.lastIndexOf("/") + 1);
				this.lockID = strLockID;
				logger.debug("Created a lock znode {} {}", strZnodePath, strLockID);
			}
			while(!acquired) {
				List<String> listZnodeLock = client.getChildren(this.path, this.watcher);
				logger.debug("All current mutex lock contain {}. ", listZnodeLock);
				if (hasAcquiredLock(listZnodeLock)) {
					logger.debug("Acquired lock with id {}. ", this.lockID);
					acquired = true;
				}
				else {
					logger.debug("Failed to acquire {} and retrying. ", this.lockID);
					latch.await();
				}
			}
		} 
		catch (KeeperException | InterruptedException e) {
			logger.error("", e);
		}
	}

	@Override
	public void unlock() {
		try {
			List<String> listZnodeLock = client.getChildren(this.path, null);
			logger.debug("All current mutex lock contain {}. ", listZnodeLock);
			if (hasAcquiredLock(listZnodeLock)) {
				logger.debug("Releasing lock {}. ", this.lockID);
				client.delete(this.path + "/" + this.lockID, -1); 
				logger.debug("Released lock {}. ", this.lockID);
			}
			else {
				logger.info("The current client doesn't acquire lock. ");
			}
		} 
		catch (KeeperException | InterruptedException e) {
			logger.error("", e);
		}
	}
	
	private boolean hasAcquiredLock(List<String> lockList) {
		for (String strItem : lockList) {
			if (strItem.startsWith(this.name) && strItem.compareTo(this.lockID) < 0) {
				return false;
			}
		}
		return true;
	}
	
	private class DistributedLockWatcher implements Watcher {
		
		public DistributedLockWatcher() {
		}
		
		@Override
		public void process(WatchedEvent event) {
			switch (event.getState()) {
			case SyncConnected:
				switch (event.getType()) {
				case None:
					ZookeeperDistributedLock.this.latch.countDown();
					break;
				case NodeChildrenChanged:
				case NodeCreated:
				case NodeDataChanged:
				case NodeDeleted:
					logger.debug("Some znode changed {} {}. ", event.getType(), event.getPath());
					ZookeeperDistributedLock.this.latch.countDown();
					break;
				default:
					break;
				}
				break;
			case Disconnected:
				break;
			case ConnectedReadOnly:
				break;
			case Expired:
				break;
			case SaslAuthenticated:
				break;
			case AuthFailed:
				break;
			default:
				break;
			}
		}
	}
}
