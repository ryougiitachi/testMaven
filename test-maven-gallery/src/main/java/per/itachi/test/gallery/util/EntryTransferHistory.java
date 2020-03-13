package per.itachi.test.gallery.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import per.itachi.test.gallery.persist.DBAccessConnUtils;
import per.itachi.test.gallery.persist.DBConstants;
import per.itachi.test.gallery.persist.entity.GalleryHistory;

public class EntryTransferHistory {

	private static final Logger logger = LoggerFactory.getLogger(EntryTransferHistory.class);
	
	public static void main(String[] args) {
		Charset charset = Charset.forName("UTF-8");
		GalleryHistory history = null;
		List<GalleryHistory> listReadme = new ArrayList<>(100);
		List<GalleryHistory> listNonReadme = new ArrayList<>(100);
		Path dirPicRoot = Paths.get("pic");
		Path fileReadme = null;
		String strLink = null;
		Date dateFileTime = null;
		try(DirectoryStream<Path> directoryStream = Files.newDirectoryStream(dirPicRoot);) {
			for (Path path : directoryStream) {
				fileReadme = Paths.get(path.toString(), "readme.txt");
				history = new GalleryHistory();
				history.setTitle(path.getFileName().toString());
				if (Files.exists(fileReadme)) {
					dateFileTime = new Date(Files.getLastModifiedTime(path).toMillis());
					history.setStatus(2);
					history.setCreator(DBConstants.DEFAULT_OPERATOR);
					history.setCdate(dateFileTime);
					history.setEditor(DBConstants.DEFAULT_OPERATOR);
					history.setEdate(dateFileTime);
					try(BufferedReader br = Files.newBufferedReader(fileReadme, charset)) {
						if ((strLink = br.readLine()) != null) {
							history.setGalleryLink(strLink);
						}
					} 
					catch (IOException e) {
						logger.error("{} ", fileReadme, e);
					}
					listReadme.add(history);
//					logger.info("{} has readme.txt", path.toString());
				}
				else {
					listNonReadme.add(history);
				}
			}
		} 
		catch (IOException e) {
			logger.error("", e);
		}

		logger.info("The following files have readme.txt:");
		for (GalleryHistory item : listReadme) {
			logger.info("{} has readme.txt", item.toString());
		}
		logger.info("The following files have no readme.txt:");
		for (GalleryHistory item : listNonReadme) {
			logger.info("{} has readme.txt", item.toString());
		}
		
		int id;
		int count = 0;
		try {
			DBAccessConnUtils.connect();
			for (GalleryHistory item : listReadme) {
				if (DBAccessConnUtils.getGalleryHistoryByLink(item.getGalleryLink()) == null) {
					id = DBAccessConnUtils.insertGalleryHistory(item);
					logger.info("{} has id {} into history.", item.getGalleryLink(), id);
					++count;
				}
				if (count >= 10) {
					DBAccessConnUtils.commit();
				}
			}
			DBAccessConnUtils.commit();
		} 
		catch (ClassNotFoundException | SQLException e) {
			logger.error("Error occurs when inserting history record. ", e);
		}
		finally {
			DBAccessConnUtils.close();
		}
	}

}
