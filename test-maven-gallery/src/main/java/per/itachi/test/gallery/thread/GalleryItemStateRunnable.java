package per.itachi.test.gallery.thread;

import java.util.EventObject;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import per.itachi.test.gallery.entity.FrameGalleryItemEntity;
import per.itachi.test.gallery.window.GalleryParserListener;

public class GalleryItemStateRunnable implements ControllableRunnable {
	
	private final Logger logger = LoggerFactory.getLogger(GalleryItemStateRunnable.class);
	
	private GalleryParserListener listener;
	
	private BlockingQueue<FrameGalleryItemEntity> states;
	
	public GalleryItemStateRunnable(BlockingQueue<FrameGalleryItemEntity> states) {
		this.states = states;
	}
	
	public GalleryItemStateRunnable(GalleryParserListener listener, BlockingQueue<FrameGalleryItemEntity> states) {
		this.listener = listener;
		this.states = states;
	}

	@Override
	public void run() {
		boolean isRunning = true;
		FrameGalleryItemEntity entity = null;
		EventObject event = null;
		try {
			while (isRunning) {
				entity = states.take();
				event = new EventObject(entity);
				listener.galleryStateChanged(event);
			}
		} 
		catch (InterruptedException e) {
			logger.error("The Gallery Item State thread has been interruputed. ", e);
		}
		finally {
			// test whether or not theard in pool will interrupt. 
//			if (Thread.interrupted()) {
//				isRunning = false;
//			}
		}
	}

	@Override
	public void shutdown() {
		logger.info("Try to interrupt the Gallery Item State thread. ");
		Thread.currentThread().interrupt();
	}

	public GalleryParserListener getListener() {
		return listener;
	}

	public void setListener(GalleryParserListener listener) {
		this.listener = listener;
	}
}
