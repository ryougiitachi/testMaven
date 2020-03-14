package per.itachi.test.gallery.component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import per.itachi.test.gallery.entity.FrameGalleryItemEntity;
import per.itachi.test.gallery.thread.ControllableRunnable;
import per.itachi.test.gallery.thread.GalleryParserRunnable;

@Component
public class FrameOperationAdapter {
	
	private final Logger logger = LoggerFactory.getLogger(FrameOperationAdapter.class);
	
	@Autowired
	@Qualifier("addedItems")
	private BlockingQueue<FrameGalleryItemEntity> addedItems;
	
	@Autowired
	@Qualifier("updatedItems")//it is required
	private BlockingQueue<FrameGalleryItemEntity> updatedItems;
	
	@Autowired
	private GalleryParserProcessor galleryParserProcessor;
	
	private List<ControllableRunnable> runnables;
	
	private ExecutorService executorService;
	
	@PostConstruct
	private void init() {
		logger.info("Initialising blocking queues... ");
		logger.info("blocking queues have been ready. ");
		logger.info("Initialising relevant threads... ");
		ControllableRunnable runnableParser = new GalleryParserRunnable(this.addedItems, this.updatedItems, this.galleryParserProcessor);
		ExecutorService executorService = Executors.newFixedThreadPool(2);
		executorService.execute(runnableParser);
		List<ControllableRunnable> listRunnables = new ArrayList<>();
		listRunnables.add(runnableParser);
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
