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

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} 
		catch (ClassNotFoundException e) {
			logger.error(e.getMessage(), e);
		} 
		catch (InstantiationException e) {
			logger.error(e.getMessage(), e);
		} 
		catch (IllegalAccessException e) {
			logger.error(e.getMessage(), e);
		} 
		catch (UnsupportedLookAndFeelException e) {
			logger.error(e.getMessage(), e);
		}
		
		BlockingQueue<FrameGalleryItemEntity> queueUrlLink = new LinkedBlockingQueue<>();
		BlockingQueue<FrameGalleryItemEntity> queueStateChanged = new LinkedBlockingQueue<>();
		FrameOperationEntity entity = new FrameOperationEntity();
		
		JFrame mainFrame = new MainFrame(entity);
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
		
		mainFrame.setVisible(true);
		mainFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		mainFrame.setTitle("Gallery");
	}

}
