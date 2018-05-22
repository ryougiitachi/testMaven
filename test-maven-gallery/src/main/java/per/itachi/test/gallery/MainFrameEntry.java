package per.itachi.test.gallery;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import per.itachi.test.gallery.entity.FrameOperationEntity;
import per.itachi.test.gallery.window.MainFrame;

public class MainFrameEntry {
	
	private static final Logger logger = LoggerFactory.getLogger(MainFrameEntry.class);

	public static void main(String[] args) {
		BlockingQueue<String> queueUrlLink = new LinkedBlockingQueue<>();
		FrameOperationEntity entity = new FrameOperationEntity();
		entity.setLinks(queueUrlLink);
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
		JFrame mainFrame = new MainFrame();
		mainFrame.setVisible(true);
		mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}

}
