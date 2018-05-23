package per.itachi.test.gallery.entity;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

import per.itachi.test.gallery.thread.ControllableRunnable;

public class FrameOperationEntity {
	
	private BlockingQueue<FrameGalleryItemEntity> links;
	
	private List<ControllableRunnable> runnables;
	
	private ExecutorService executorService;
	
	public void putUrlLink(FrameGalleryItemEntity entity) throws InterruptedException {
		links.put(entity);
	}
	
	public void shutdownControllableRunnable() {
		for (ControllableRunnable runnable : runnables) {
			runnable.shutdown();
		}
	}
	
	public void shutdownExecutorService() {
		executorService.shutdown();
	}
	
	public void setLinks(BlockingQueue<FrameGalleryItemEntity> links) {
		this.links = links;
	}
	
	public void setControllableRunnable(List<ControllableRunnable> runnables) {
		this.runnables = runnables;
	}
	
	public void setExecutorService(ExecutorService executorService) {
		this.executorService = executorService;
	}
}
