package per.itachi.test.gallery;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import per.itachi.test.gallery.conf.GalleryWebsite;
import per.itachi.test.gallery.conf.GalleryWebsiteConfig;
import per.itachi.test.gallery.entity.GalleryHistory;
import per.itachi.test.gallery.parser.Parser;
import per.itachi.test.gallery.persist.DBAccessConnUtils;
import per.itachi.test.gallery.util.GalleryUtils;
import per.itachi.test.gallery.util.WebUtils;

public class Entry {
	
	private static final Logger logger = LoggerFactory.getLogger(Entry.class);
	
	private static GalleryWebsiteConfig confGalleryWebsite;

	public static void main(String[] args) {
		if (args.length <= 0) {
			logger.info("No website link found.");
			return;
		}
		initialise();
		
		String strLink = args[0];
		String strBaseUrl = WebUtils.getBaseUrl(strLink);
		if (strBaseUrl == null) {
			logger.error("{} is not a valid website link.", strLink);
			return;
		}
		Class<?> clazzParser = confGalleryWebsite.getPraserClass(strBaseUrl);
		GalleryWebsite website = confGalleryWebsite.getWebsite(strBaseUrl);
		if (clazzParser == null || website == null) {
			logger.error("{} has been not included in gallery.", strLink);
			return;
		}
		String strWebPath = strLink.substring(strBaseUrl.length());

		boolean exit = false;
		GalleryHistory history = null;
		try {
			DBAccessConnUtils.connect();
			history = DBAccessConnUtils.getGalleryHistoryByWebPath(strWebPath, website.getId());
			if (history != null) {
				logger.info("{} has been downloaded before, {}.", strLink, history.getTitle());
				exit = true;
			}
			else {
				history = GalleryUtils.getNewGalleryHistory(strBaseUrl, strWebPath, website.getId());
				DBAccessConnUtils.insertGalleryHistory(history);
				history.setStatus(GalleryConstants.PASER_STATUS_PROCESSING);
				DBAccessConnUtils.updateGalleryHistoryByID(history);
				logger.info("Added {} to history record, and start download it.", strLink);
				exit = false;
			}
		} 
		catch (ClassNotFoundException | SQLException e) {
			logger.error("Error occurs when checking history record {}. ", strLink, e);
			exit = true;
		}
		finally {
			DBAccessConnUtils.close();
		}
		if (exit) {
			return;
		}
		
		Parser parser = null;
		long lStartPoint, lEndPoint;
		try {
			parser = instantiateNewParser(clazzParser, strLink);
			lStartPoint = System.currentTimeMillis();
			parser.execute();
			lEndPoint = System.currentTimeMillis();
			logger.info("It took {} milliseconds to download gallery from {}. ", lEndPoint - lStartPoint, strLink);
		} 
		catch (NoSuchMethodException | SecurityException | InvocationTargetException | IllegalAccessException
				| InstantiationException e) {
			logger.error("Error occurs when parsing {}. ", strLink, e);
			exit = true;
		}
		if (exit) {
			return;
		}
		
		try {
			DBAccessConnUtils.connect();
			history.setTitle(parser.getTitle());
			history.setStatus(GalleryConstants.PASER_STATUS_COMPLETED);
			DBAccessConnUtils.updateGalleryHistoryByID(history);
			logger.info("Completed downloading {}, {}", strLink, history.getTitle());
		} 
		catch (ClassNotFoundException | SQLException e) {
			logger.error("Error occurs when updating history record {}. ", strLink, e);
		}
		finally {
			DBAccessConnUtils.close();
		}
	}
	
	private static void initialise() {
		confGalleryWebsite = GalleryWebsiteConfig.load(GalleryConstants.DEFAULT_WEBSITE_CONF_PATH);
	}

	private static Parser instantiateNewParser(Class<?> clazzParser, String link) 
			throws NoSuchMethodException, SecurityException, InvocationTargetException, IllegalAccessException, InstantiationException {
		Constructor<?> constructor = clazzParser.getConstructor(String.class);
		Parser parser = (Parser)constructor.newInstance(link);
		return parser;
	}
	
}
