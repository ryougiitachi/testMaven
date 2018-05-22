package per.itachi.test.gallery.thread;

import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GalleryParserRunnable implements ControllableRunnable {
	
	private final Logger logger = LoggerFactory.getLogger(GalleryParserRunnable.class);
	
	private BlockingQueue<String> links;
	
	public GalleryParserRunnable(BlockingQueue<String> links) {
		this.links = links;
	}
	
	@Override
	public void run() {
		boolean isRunning = true;
		String strUrlLink = null;
		try {
			while (isRunning) {
				strUrlLink = links.take();
			}
		} 
		catch (InterruptedException e) {
			logger.error("The Gallery Parser thread has been interruputed. ", e);
		}
	}

	@Override
	public void shutdown() {
		Thread.currentThread().interrupt();
	}
}
