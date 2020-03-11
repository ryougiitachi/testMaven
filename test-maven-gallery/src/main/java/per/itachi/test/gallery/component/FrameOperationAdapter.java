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
import per.itachi.test.gallery.thread.GalleryItemStateRunnable;
import per.itachi.test.gallery.thread.GalleryParserRunnable;
import per.itachi.test.gallery.window.GalleryParserListener;

@Component
public class FrameOperationAdapter {
	
	private final Logger logger = LoggerFactory.getLogger(FrameOperationAdapter.class);
	
	private BlockingQueue<FrameGalleryItemEntity> links;
	
	private List<ControllableRunnable> runnables;
	
	private ExecutorService executorService;
	
	private GalleryParserListener galleryParserListener;
	
	@PostConstruct
	private void init() {
		logger.info("Initialising blocking queues... ");
		BlockingQueue<FrameGalleryItemEntity> queueUrlLink = new LinkedBlockingQueue<>();
		BlockingQueue<FrameGalleryItemEntity> queueStateChanged = new LinkedBlockingQueue<>();
		logger.info("blocking queues have been ready. ");
		logger.info("Initialising relevant threads... ");
		ControllableRunnable runnableParser = new GalleryParserRunnable(queueUrlLink, queueStateChanged);
		ControllableRunnable runnableState = new GalleryItemStateRunnable(galleryParserListener, queueStateChanged);
		ExecutorService executorService = Executors.newFixedThreadPool(2);
		executorService.execute(runnableParser);
		executorService.execute(runnableState);
		List<ControllableRunnable> listRunnables = new ArrayList<>();
		listRunnables.add(runnableParser);
		listRunnables.add(runnableState);
		this.links = queueUrlLink;
		this.runnables = listRunnables;
		this.executorService = executorService;
		logger.info("relevant threads have been ready. ");
		
	}
	
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

	public void setGalleryParserListener(GalleryParserListener galleryParserListener) {
		this.galleryParserListener = galleryParserListener;
	}
}
