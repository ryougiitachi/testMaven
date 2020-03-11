package per.itachi.test.gallery;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import per.itachi.test.gallery.window.MainFrame;

public class MainFrameEntry {
	
	private static final Logger logger = LoggerFactory.getLogger(MainFrameEntry.class);
	
	private static final String MAIN_TITLE = "Gallery";

	public static void main(String[] args) {
		logger.info("Starting {}", MAIN_TITLE);

		logger.info("Initialising UI look-and-feel... ");
		initialiseUILookAndFeel();
		logger.info("UI look-and-feel is ready. ");
		
		logger.info("Initialising Spring Framework... ");
		ApplicationContext applicationContext = new FileSystemXmlApplicationContext("conf/application-context*.xml");
		logger.info("Spring Framework is ready. ");
		
		logger.info("Initialising Main Frame... ");
		JFrame mainFrame = applicationContext.getBean(MainFrame.class);
		logger.info("Main Frame is ready. ");
		
		logger.info("Showing main frame... ");
		mainFrame.setVisible(true);
		mainFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		mainFrame.setTitle(MAIN_TITLE);
		
		logger.info("Started {}", MAIN_TITLE);
	}
	
	/**
	 * UI look-and-feel is required to execute before initialising frame. 
	 * Otherwise, UI look-and-feel won't work. 
	 * 
	 * */
	private static void initialiseUILookAndFeel() {
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
	}
}
