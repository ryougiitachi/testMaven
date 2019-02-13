package per.itachi.test.gallery;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import per.itachi.test.gallery.entity.FrameGalleryItemEntity;
import per.itachi.test.gallery.entity.FrameOperationEntity;
import per.itachi.test.gallery.thread.ControllableRunnable;
import per.itachi.test.gallery.thread.GalleryItemStateRunnable;
import per.itachi.test.gallery.thread.GalleryParserRunnable;
import per.itachi.test.gallery.window.GalleryParserListener;
import per.itachi.test.gallery.window.MainFrame;

public class MainFrameEntry {
	
	private static final Logger logger = LoggerFactory.getLogger(MainFrameEntry.class);
	
	private static final String MAIN_TITLE = "Gallery";

	public static void main(String[] args) {
		logger.info("Starting {}", MAIN_TITLE);
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} 
		catch (ClassNotFoundException e) {
			logger.error("Error occured when initialising main frame. ", e);
		} 
		catch (InstantiationException e) {
			logger.error("Error occured when initialising main frame. ", e);
		} 
		catch (IllegalAccessException e) {
			logger.error("Error occured when initialising main frame. ", e);
		} 
		catch (UnsupportedLookAndFeelException e) {
			logger.error("Error occured when initialising main frame. ", e);
		}
		
		logger.info("Initialising blocking queue... ");
		BlockingQueue<FrameGalleryItemEntity> queueUrlLink = new LinkedBlockingQueue<>();
		BlockingQueue<FrameGalleryItemEntity> queueStateChanged = new LinkedBlockingQueue<>();
		FrameOperationEntity entity = new FrameOperationEntity();
		
		logger.info("Initialising main frame... ");
		JFrame mainFrame = new MainFrame(entity);
		
		logger.info("Initialising relevant threads... ");
		ControllableRunnable runnableParser = new GalleryParserRunnable(queueUrlLink, queueStateChanged);
		ControllableRunnable runnableState = new GalleryItemStateRunnable((GalleryParserListener)mainFrame, queueStateChanged);
		ExecutorService executorService = Executors.newFixedThreadPool(2);
		executorService.execute(runnableParser);
		executorService.execute(runnableState);
		List<ControllableRunnable> listRunnables = new ArrayList<>();
		listRunnables.add(runnableParser);
		listRunnables.add(runnableState);
		entity.setLinks(queueUrlLink);
		entity.setControllableRunnable(listRunnables);
		entity.setExecutorService(executorService);
		
		logger.info("Showing main frame... ");
		mainFrame.setVisible(true);
		mainFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		mainFrame.setTitle(MAIN_TITLE);
		
		logger.info("Started {}", MAIN_TITLE);
	}
}
