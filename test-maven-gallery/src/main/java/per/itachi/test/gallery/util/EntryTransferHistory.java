package per.itachi.test.gallery.util;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import per.itachi.test.gallery.entity.GalleryHistory;
import per.itachi.test.gallery.persist.DBConstants;

public class EntryTransferHistory {

	private static final Logger logger = LoggerFactory.getLogger(EntryTransferHistory.class);
	
	public static void main(String[] args) {
		Path dirPicRoot = Paths.get("src");
		Path dirPicture = null;
		Iterator<Path> iterator = dirPicRoot.iterator();
		while (iterator.hasNext()) {
			dirPicture = iterator.next();
			logger.info("{}", dirPicture);
		}
		try(DirectoryStream<Path> directoryStream = Files.newDirectoryStream(dirPicRoot);) {
			for (Path path : directoryStream) {
				logger.info("{}", path);
			}
		} 
		catch (IOException e) {
			logger.error("", e);
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
