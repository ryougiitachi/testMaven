package per.itachi.test.springboot.zookeeper.watcher;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DistributedLockWatcher implements Watcher {
	
	private final Logger logger = LoggerFactory.getLogger(DistributedLockWatcher.class);
	
	private CountDownLatch latch;
	
	public DistributedLockWatcher(CountDownLatch latch) {
		this.latch = latch;
	}

	@Override
	public void process(WatchedEvent event) {
		logger.info("WatchedEvent: {}", event);
		switch (event.getState()) {
		case SyncConnected:
			switch (event.getType()) {
			case None:
				this.latch.countDown();
				logger.info("Connected. ");
				break;
			case NodeChildrenChanged:
			case NodeCreated:
			case NodeDataChanged:
			case NodeDeleted:
				logger.info("Some znode changed {} {}. ", event.getType(), event.getPath());
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
		if (event.getState() == KeeperState.SyncConnected && event.getType() == EventType.None) {
			this.latch.countDown();
		}
	}
}
