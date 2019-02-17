package per.itachi.test.gallery.thread;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import per.itachi.test.gallery.GalleryConstants;
import per.itachi.test.gallery.conf.GalleryWebsite;
import per.itachi.test.gallery.conf.GalleryWebsiteConf;
import per.itachi.test.gallery.entity.FrameGalleryItemEntity;
import per.itachi.test.gallery.entity.GalleryHistory;
import per.itachi.test.gallery.parser.Parser;
import per.itachi.test.gallery.persist.DBAccessConnUtils;
import per.itachi.test.gallery.util.GalleryUtils;
import per.itachi.test.gallery.util.WebUtils;

public class GalleryParserRunnable implements ControllableRunnable {
	
	private final Logger logger = LoggerFactory.getLogger(GalleryParserRunnable.class);
	
	private BlockingQueue<FrameGalleryItemEntity> links;
	
	private BlockingQueue<FrameGalleryItemEntity> states;
	
	private GalleryWebsiteConf confGalleryWebsite;
	
	public GalleryParserRunnable(BlockingQueue<FrameGalleryItemEntity> links, BlockingQueue<FrameGalleryItemEntity> states) {
		this.links = links;
		this.states = states;
	}
	
	@Override
	public void run() {
		boolean isRunning = true;
		String strUrlLink = null;
//		long lSleepMillis = 10l * 1000l;
		FrameGalleryItemEntity entity = null;
		initialise();
		try {
			while (isRunning) {
				entity = links.take();
				strUrlLink = entity.getUrlLink();
				logger.info("Start parsing {}. ", strUrlLink);
				entity.setStatus("Processing...");
				states.put(entity);
				String strBaseUrl = WebUtils.getBaseUrl(strUrlLink);
				if (strBaseUrl == null) {
					logger.error("{} is not a valid website link.", strUrlLink);
					entity.setStatus("Invalid");
					states.put(entity);
					continue;
				}
				Class<?> clazzParser = confGalleryWebsite.getPraserClass(strBaseUrl);
				GalleryWebsite website = confGalleryWebsite.getWebsite(strBaseUrl);
				if (clazzParser == null || website == null) {
					logger.error("{} has been not included in gallery.", strUrlLink);
					entity.setStatus("Non-included");
					states.put(entity);
					continue;
				}
				String strWebPath = strUrlLink.substring(strBaseUrl.length());
				boolean exit = false;
				GalleryHistory history = null;
				try {
					DBAccessConnUtils.connect();
					history = DBAccessConnUtils.getGalleryHistoryByWebPath(strWebPath, website.getId());
					if (history != null && history.getStatus() == GalleryConstants.PASER_STATUS_COMPLETED) {
						logger.info("{} downloaded before, {}.", strUrlLink, history.getTitle());
						exit = true;
					}
					else {
						if (history == null) {
							history = GalleryUtils.getNewGalleryHistory(strBaseUrl, strWebPath, website.getId());
							DBAccessConnUtils.insertGalleryHistory(history);
						}
						history.setStatus(GalleryConstants.PASER_STATUS_PROCESSING);
						DBAccessConnUtils.updateGalleryHistoryByID(history);
						logger.info("Added {} to history record, and start download it.", strUrlLink);
						exit = false;
					}
				} 
				catch (ClassNotFoundException | SQLException e) {
					logger.error("Error occurs when checking history record {}. ", strUrlLink, e);
					exit = true;
				}
				finally {
					DBAccessConnUtils.close();
				}
				if (exit) {
					entity.setStatus("Downloaded");
					states.put(entity);
					continue;
				}
				
				Parser parser = null;
				long lStartPoint, lEndPoint;
				try {
					parser = instantiateNewParser(clazzParser, strUrlLink);
					parser.setBaseUrl(strBaseUrl);
					parser.setGalleryWebsiteConf(website);
					lStartPoint = System.currentTimeMillis();
					parser.execute();
					lEndPoint = System.currentTimeMillis();
					logger.info("It took {} milliseconds to download gallery from {}. ", lEndPoint - lStartPoint, strUrlLink);
				} 
				catch (NoSuchMethodException | SecurityException | InvocationTargetException | IllegalAccessException
						| InstantiationException e) {
					logger.error("Error occurs when parsing {}. ", strUrlLink, e);
					exit = true;
				}
				if (exit) {
					entity.setStatus("Error");
					states.put(entity);
					continue;
				}
				
				try {
					DBAccessConnUtils.connect();
					history.setTitle(parser.getTitle());
					history.setStatus(GalleryConstants.PASER_STATUS_COMPLETED);
					DBAccessConnUtils.updateGalleryHistoryByID(history);
					logger.info("Completed downloading {}, {}", strUrlLink, history.getTitle());
				} 
				catch (ClassNotFoundException | SQLException e) {
					logger.error("Error occurs when updating history record {}. ", strUrlLink, e);
				}
				finally {
					DBAccessConnUtils.close();
				}
				entity.setStatus("Finish");
				states.put(entity);
				logger.info("Finish parsing {}, and {} left in queue. ", strUrlLink, links.size());
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
	
	private void initialise() {
		confGalleryWebsite = GalleryWebsiteConf.load(GalleryConstants.DEFAULT_WEBSITE_CONF_PATH);
	}

	private Parser instantiateNewParser(Class<?> clazzParser, String link) 
			throws NoSuchMethodException, SecurityException, InvocationTargetException, IllegalAccessException, InstantiationException {
		Constructor<?> constructor = clazzParser.getConstructor(String.class);
		Parser parser = (Parser)constructor.newInstance(link);
		return parser;
	}
}
