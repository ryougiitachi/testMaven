package per.itachi.test.gallery.entity;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

public class FrameOperationEntity {
	
	private BlockingQueue<String> links;
	
	private ExecutorService executorService;
	
	public void putUrlLink(String urlLink) throws InterruptedException {
		links.put(urlLink);
	}
	
	public void shutdownExecutorService() {
		executorService.shutdown();
	}
	
	public void setLinks(BlockingQueue<String> links) {
		this.links = links;
	}

	public void setExecutorService(ExecutorService executorService) {
		this.executorService = executorService;
	}
}
