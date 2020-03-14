package per.itachi.test.gallery.component;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import per.itachi.test.gallery.GalleryConstants;
import per.itachi.test.gallery.GalleryItemConstants;
import per.itachi.test.gallery.conf.GalleryWebsite;
import per.itachi.test.gallery.conf.GalleryWebsiteConfig;
import per.itachi.test.gallery.entity.FrameGalleryItemEntity;
import per.itachi.test.gallery.parser.Parser;
import per.itachi.test.gallery.persist.DBAccessConnUtils;
import per.itachi.test.gallery.persist.entity.GalleryHistory;
import per.itachi.test.gallery.util.GalleryUtils;
import per.itachi.test.gallery.util.WebUtils;

@Component
public class GalleryParserProcessor {
	
	private final Logger logger = LoggerFactory.getLogger(GalleryParserProcessor.class);
	
	@Autowired
	private GalleryWebsiteConfig confGalleryWebsite;

	@PostConstruct
	private void init() {
//		confGalleryWebsite = GalleryWebsiteConfig.load(GalleryConstants.DEFAULT_WEBSITE_CONF_PATH);
	}
	
	public void processGalleryParser(FrameGalleryItemEntity entity) {
		String strUrlLink = entity.getUrlLink();
		String strBaseUrl = WebUtils.getBaseUrl(strUrlLink);
		if (strBaseUrl == null) {
			logger.error("{} is not a valid website link.", strUrlLink);
			entity.setStatus(GalleryItemConstants.STATUS_NAME_INVALID);
			return;
		}
		Class<?> clazzParser = confGalleryWebsite.getPraserClass(strBaseUrl);
		GalleryWebsite website = confGalleryWebsite.getWebsite(strBaseUrl);
		if (clazzParser == null || website == null) {
			logger.error("{} has been not included in gallery.", strUrlLink);
			entity.setStatus(GalleryItemConstants.STATUS_NAME_NON_INCLUDED);
			return;
		}
		String strWebPath = strUrlLink.substring(strBaseUrl.length());
		boolean exit = false;
		GalleryHistory history = null;
		try {
			DBAccessConnUtils.connect();
			history = DBAccessConnUtils.getGalleryHistoryByWebPath(strWebPath, website.getId());
			if (history != null && history.getStatus() == GalleryConstants.PASER_STATUS_COMPLETED) {
				logger.info("Previously downloaded {} before, {}.", strUrlLink, history.getTitle());
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
			entity.setStatus(GalleryItemConstants.STATUS_NAME_DOWNLOADED);
			return;
		}
		
		Parser parser = null;
		long lStartPoint, lEndPoint;
		try {
			parser = instantiateNewParser(clazzParser, strUrlLink);
			parser.setBaseUrl(strBaseUrl);
			parser.setGalleryWebsiteConfig(confGalleryWebsite);
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
			entity.setStatus(GalleryItemConstants.STATUS_NAME_ERROR);
			return;
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
		entity.setStatus(GalleryItemConstants.STATUS_NAME_FINISH);
	}

	private Parser instantiateNewParser(Class<?> clazzParser, String link) 
			throws NoSuchMethodException, SecurityException, InvocationTargetException, IllegalAccessException, InstantiationException {
		Constructor<?> constructor = clazzParser.getConstructor(String.class);
		Parser parser = (Parser)constructor.newInstance(link);
		return parser;
	}
}
