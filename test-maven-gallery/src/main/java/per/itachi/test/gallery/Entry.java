package per.itachi.test.gallery;

import java.sql.SQLException;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import per.itachi.test.gallery.entity.GalleryHistory;
import per.itachi.test.gallery.parser.NineSixxxNetParser;
import per.itachi.test.gallery.parser.Parser;
import per.itachi.test.gallery.persist.DBAccessConnUtils;
import per.itachi.test.gallery.persist.DBConstants;

public class Entry {
	
	private static final Logger logger = LoggerFactory.getLogger(Entry.class);

	public static void main(String[] args) {
		if (args.length <= 0) {
			logger.info("No website link found.");
			return;
		}
		
		boolean exit = false;
		String strLink = args[0];
		GalleryHistory history = null;
		try {
			DBAccessConnUtils.connect();
			history = DBAccessConnUtils.getGalleryHistoryByLink(strLink);
			if (history != null) {
				logger.info("{} has been downloaded before.", strLink);
				exit = true;
			}
			else {
				history = getNewGalleryHistory(strLink);
				DBAccessConnUtils.insertGalleryHistory(history);
				history.setStatus(1);
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
		Parser nineSixxxNet = new NineSixxxNetParser(strLink);
		nineSixxxNet.execute();
		try {
			DBAccessConnUtils.connect();
			history.setTitle(nineSixxxNet.getTitle());
			history.setStatus(2);
			DBAccessConnUtils.updateGalleryHistoryByID(history);
			logger.info("Completed downloading {}", strLink);
		} 
		catch (ClassNotFoundException | SQLException e) {
			logger.error("Error occurs when updating history record {}. ", strLink, e);
		}
		finally {
			DBAccessConnUtils.close();
		}
	}

	private static GalleryHistory getNewGalleryHistory(String link) {
		GalleryHistory history = new GalleryHistory();
		Date date = new Date();
		history.setGalleryLink(link);
		history.setWebsite(StringUtils.EMPTY);
		history.setStatus(0);//0-initial 1-processing 2-completed 3-failed
		history.setCreator(DBConstants.DEFAULT_OPERATOR);
		history.setCdate(date);
		history.setEditor(DBConstants.DEFAULT_OPERATOR);
		history.setEdate(date);
		return history;
	}
}
