package per.itachi.test.gallery.thread;

import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import per.itachi.test.gallery.GalleryItemConstants;
import per.itachi.test.gallery.component.GalleryParserProcessor;
import per.itachi.test.gallery.entity.FrameGalleryItemEntity;

public class GalleryParserRunnable implements ControllableRunnable {
	
	private final Logger logger = LoggerFactory.getLogger(GalleryParserRunnable.class);
	
	private BlockingQueue<FrameGalleryItemEntity> addedItem;
	
	private BlockingQueue<FrameGalleryItemEntity> updatedItem;
	
	private GalleryParserProcessor galleryParserProcessor; 
	
	public GalleryParserRunnable(BlockingQueue<FrameGalleryItemEntity> links, 
			BlockingQueue<FrameGalleryItemEntity> states, GalleryParserProcessor galleryParserProcessor) {
		this.addedItem = links;
		this.updatedItem = states;
		this.galleryParserProcessor = galleryParserProcessor;
	}
	
	@Override
	public void run() {
		boolean isRunning = true;
		String strUrlLink = null;
//		long lSleepMillis = 10l * 1000l;
		FrameGalleryItemEntity entity = null;
		try {
			while (isRunning) {
				entity = addedItem.take();
				logger.info("Start parsing {}. ", strUrlLink);
				entity.setStatus(GalleryItemConstants.STATUS_NAME_PROCESSING);
				updatedItem.put(entity);
				galleryParserProcessor.processGalleryParser(entity);
				updatedItem.put(entity);
				logger.info("Finish parsing {}, and {} left in queue. ", strUrlLink, addedItem.size());
			}
		} 
		catch (InterruptedException e) {
			logger.error("The Gallery Parser thread has been interruputed. ", e);
		}
		finally {
//			 test whether or not theard in pool will interrupt. 
//			if (Thread.interrupted()) {
//				isRunning = false;
//			}
		}
	}

	@Override
	public void shutdown() {
		logger.info("Try to interrupt the Gallery Parser thread. ");
		Thread.currentThread().interrupt();
	}
}
