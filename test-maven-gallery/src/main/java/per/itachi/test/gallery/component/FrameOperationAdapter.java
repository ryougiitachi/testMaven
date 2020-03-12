package per.itachi.test.gallery.component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import per.itachi.test.gallery.entity.FrameGalleryItemEntity;
import per.itachi.test.gallery.thread.ControllableRunnable;
import per.itachi.test.gallery.thread.GalleryParserRunnable;

@Component
public class FrameOperationAdapter {
	
	private final Logger logger = LoggerFactory.getLogger(FrameOperationAdapter.class);
	
	private BlockingQueue<FrameGalleryItemEntity> addedItems;
	
	private BlockingQueue<FrameGalleryItemEntity> updatedItems;
	
	private List<ControllableRunnable> runnables;
	
	private ExecutorService executorService;
	
	@PostConstruct
	private void init() {
		logger.info("Initialising blocking queues... ");
		BlockingQueue<FrameGalleryItemEntity> queueAddedItems = new LinkedBlockingQueue<>();
		BlockingQueue<FrameGalleryItemEntity> queueUpdatedItems = new LinkedBlockingQueue<>();
		logger.info("blocking queues have been ready. ");
		logger.info("Initialising relevant threads... ");
		ControllableRunnable runnableParser = new GalleryParserRunnable(queueAddedItems, queueUpdatedItems);
		ExecutorService executorService = Executors.newFixedThreadPool(2);
		executorService.execute(runnableParser);
		List<ControllableRunnable> listRunnables = new ArrayList<>();
		listRunnables.add(runnableParser);
		this.addedItems = queueAddedItems;
		this.updatedItems = queueUpdatedItems;
		this.runnables = listRunnables;
		this.executorService = executorService;
		logger.info("relevant threads have been ready. ");
		
	}
	
	public void addItem(FrameGalleryItemEntity entity) throws InterruptedException {
		addedItems.put(entity);
	}
	
	public FrameGalleryItemEntity getUpdatedItem() throws InterruptedException  {
		return updatedItems.take();
	}
	
	public void shutdownControllableRunnable() {
		for (ControllableRunnable runnable : runnables) {
			runnable.shutdown();
		}
	}
	
	public void shutdownExecutorService() {
		executorService.shutdown();
	}
	
	public void setControllableRunnable(List<ControllableRunnable> runnables) {
		this.runnables = runnables;
	}
	
	public void setExecutorService(ExecutorService executorService) {
		this.executorService = executorService;
	}
}
