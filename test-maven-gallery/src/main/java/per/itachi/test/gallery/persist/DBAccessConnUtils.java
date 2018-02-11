package per.itachi.test.gallery.persist;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import per.itachi.test.gallery.entity.GalleryHistory;

public class DBAccessConnUtils {
	
	private static final Logger logger =LoggerFactory.getLogger(DBAccessConnUtils.class);
	
	private static final String SQL_GET_HIS_BY_LINK = 
			"SELECT ID, GALLERY_LINK, WEBSITE, STATUS, CEATOR, CDATE FROM T_GALLERY_HISTORY WHERE GALLERY_LINK = ?";
	
	private static Connection connection = null;
	
	public static void connect() throws ClassNotFoundException, SQLException {
		Class.forName("com.hxtt.sql.access.AccessDriver");
		connection = DriverManager.getConnection(DBConstants.ACCESS_HXTT_URL);
	}
	
	public static GalleryHistory getGalleryHistoryByLink(String link) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(SQL_GET_HIS_BY_LINK);
		statement.setString(1, link);
		ResultSet rs = statement.executeQuery();
		if (rs.next()) {
			GalleryHistory history = new GalleryHistory();
			history.setId(rs.getInt("ID"));
			history.setGalleryLink(rs.getString("GALLERY_LINK"));
			history.setStatus(rs.getInt("STATUS"));
			history.setCreator(rs.getString("CEATOR"));
			history.setCdate(rs.getDate("CDATE"));
			return history;
		}
		else {
			return null;
		}
	}
	
	public static void close() {
		if (connection != null) {
			try {
				connection.close();
			} 
			catch (SQLException e) {
				logger.error("", e);
			}
			finally {
				connection = null;
			}
		}
	}
}
