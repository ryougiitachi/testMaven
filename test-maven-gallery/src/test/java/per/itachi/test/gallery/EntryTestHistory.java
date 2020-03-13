package per.itachi.test.gallery;

import java.sql.SQLException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import per.itachi.test.gallery.persist.DBAccessConnUtils;
import per.itachi.test.gallery.persist.entity.GalleryHistory;

public class EntryTestHistory {
	
	private static final Logger logger = LoggerFactory.getLogger(EntryTestHistory.class);

	public static void main(String[] args) {
		if (args.length <= 0) {
			logger.info("No website link found.");
			return;
		}
		String strWebsiteLink = args[0];
		GalleryHistory history = null;
		int id = 0;
		int count = 0;
		try {
			DBAccessConnUtils.connect();
			history = DBAccessConnUtils.getGalleryHistoryByLink(strWebsiteLink);
			logger.info("DBAccessConnUtils.getGalleryHistoryByLink is {}", history);
			history = getGalleryHistory(strWebsiteLink);
			id = DBAccessConnUtils.insertGalleryHistory(history);
			logger.info("DBAccessConnUtils.insertGalleryHistory is {}", id);
		} 
		catch (ClassNotFoundException | SQLException e) {
			logger.error("", e);
		}
		finally {
			DBAccessConnUtils.close();
		}
	}
	
	private static GalleryHistory getGalleryHistory(String link) {
		GalleryHistory history = new GalleryHistory();
		Date dateCurrent = new Date();
		history.setGalleryLink(link);
		history.setStatus(0);
		history.setCreator("system");
		history.setCdate(new Date());
		history.setEditor("system");
		history.setEdate(dateCurrent);
		return history;
	}

}
