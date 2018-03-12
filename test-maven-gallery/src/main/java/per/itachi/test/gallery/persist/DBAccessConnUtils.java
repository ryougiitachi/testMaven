package per.itachi.test.gallery.persist;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import per.itachi.test.gallery.entity.GalleryHistory;

public class DBAccessConnUtils {
	
	private static final Logger logger =LoggerFactory.getLogger(DBAccessConnUtils.class);
	
	private static final String SQL_GET_HIS_BY_LINK = //TODO
			"SELECT ID, GALLERY_LINK, WEBSITE, TITLE, STATUS, CREATOR, CDATE, EDITOR, EDATE FROM T_GALLERY_HISTORY WHERE GALLERY_LINK = ?";
	
	private static final String SQL_GET_HIS_BY_WEB_PATH = 
			"SELECT ID, BASE_URL, WEB_PATH, WEBSITE_ID, TITLE, STATUS, CREATOR, CDATE, EDITOR, EDATE FROM T_GALLERY_HISTORY WHERE WEB_PATH = ? AND WEBSITE_ID = ?";
	
	private static final String SQL_INS_HIS = 
			"INSERT INTO T_GALLERY_HISTORY(BASE_URL, WEB_PATH, WEBSITE_ID, TITLE, STATUS, CREATOR, CDATE, EDITOR, EDATE) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	private static final String SQL_UPD_HIS_BY_ID = 
			"UPDATE T_GALLERY_HISTORY SET TITLE = ?, STATUS = ?, EDITOR = ?, EDATE = ? WHERE ID = ?";
	
	private static final String SQL_UPD_HIS_BY_LINK = //TODO
			"UPDATE T_GALLERY_HISTORY SET STATUS = ?, EDITOR = ?, EDATE = ? WHERE GALLERY_LINK = ?";
	
	private static final String SQL_UPD_HIS_BY_WEB_PATH = 
			"UPDATE T_GALLERY_HISTORY SET STATUS = ?, EDITOR = ?, EDATE = ? WHERE WEB_PATH = ? AND WEBSITE_ID = ?";
	
	private static Connection connection = null;
	
	private static boolean commited = true;
	
	public static void connect() throws ClassNotFoundException, SQLException {
		Class.forName(DBConstants.ACCESS_UCANACCESS_CLASS);
		Properties properties = new Properties();
		properties.put("password", DBConstants.ACCESS_PASSWORD);
		properties.put("jackcessOpener", "per.itachi.test.gallery.persist.GalleryJackcessOpener");
//		properties.put("newDatabaseVersion", "V2010");
//		properties.put("charset", "GBK");
		connection = DriverManager.getConnection(DBConstants.ACCESS_UCANACCESS_URL, properties);
		connection.setAutoCommit(false);
	}
	
	public static GalleryHistory getGalleryHistoryByLink(String link) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(SQL_GET_HIS_BY_LINK);
		statement.setString(1, link);
		ResultSet rs = statement.executeQuery();
		if (rs.next()) {
			GalleryHistory history = new GalleryHistory();
			history.setId(rs.getInt(GalleryHistory.COL_NAME_ID));
			history.setGalleryLink(rs.getString(GalleryHistory.COL_NAME_GALLERY_LINK));
			history.setWebsite(rs.getString(GalleryHistory.COL_NAME_WEBSITE));
			history.setTitle(rs.getString(GalleryHistory.COL_NAME_TITLE));
			history.setStatus(rs.getInt(GalleryHistory.COL_NAME_STATUS));
			history.setCreator(rs.getString(GalleryHistory.COL_NAME_CREATOR));
			history.setCdate(rs.getDate(GalleryHistory.COL_NAME_CDATE));
			history.setEditor(rs.getString(GalleryHistory.COL_NAME_EDITOR));
			history.setEdate(rs.getDate(GalleryHistory.COL_NAME_EDATE));
			return history;
		}
		else {
			return null;
		}
	}
	
	public static GalleryHistory getGalleryHistoryByWebPath(String webPath, int websiteID) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(SQL_GET_HIS_BY_WEB_PATH);
		statement.setString(1, webPath);
		statement.setInt(2, websiteID);
		ResultSet rs = statement.executeQuery();
		if (rs.next()) {
			GalleryHistory history = new GalleryHistory();
			history.setId(rs.getInt(GalleryHistory.COL_NAME_ID));
			history.setBaseUrl(rs.getString(GalleryHistory.COL_NAME_BASE_URL));
			history.setWebPath(rs.getString(GalleryHistory.COL_NAME_WEB_PATH));
			history.setWebsiteID(rs.getInt(GalleryHistory.COL_NAME_WEBSITE_ID));
			history.setTitle(rs.getString(GalleryHistory.COL_NAME_TITLE));
			history.setStatus(rs.getInt(GalleryHistory.COL_NAME_STATUS));
			history.setCreator(rs.getString(GalleryHistory.COL_NAME_CREATOR));
			history.setCdate(rs.getDate(GalleryHistory.COL_NAME_CDATE));
			history.setEditor(rs.getString(GalleryHistory.COL_NAME_EDITOR));
			history.setEdate(rs.getDate(GalleryHistory.COL_NAME_EDATE));
			return history;
		}
		else {
			return null;
		}
	}
	
	public static int insertGalleryHistory(GalleryHistory history) throws SQLException {
		PreparedStatement statement = null;
		ResultSet rs = null;
		int id;
		Date dateTime = null;
		int paramIndex = 1;
		try {
			dateTime = new Date(history.getCdate().getTime());
			statement = connection.prepareStatement(SQL_INS_HIS, Statement.RETURN_GENERATED_KEYS);
			statement.setString(paramIndex++, history.getBaseUrl());
			statement.setString(paramIndex++, history.getWebPath());
			statement.setInt(paramIndex++, history.getWebsiteID());
			statement.setString(paramIndex++, history.getTitle());
			statement.setInt(paramIndex++, history.getStatus());
			statement.setString(paramIndex++, DBConstants.DEFAULT_OPERATOR);	//creator
			statement.setDate(paramIndex++, dateTime);							//cdate
			statement.setString(paramIndex++, DBConstants.DEFAULT_OPERATOR);	//editor
			statement.setDate(paramIndex++, dateTime);							//edate
			int iResult = statement.executeUpdate();
			rs = statement.getGeneratedKeys();
			if (iResult >= 1 && rs.next()) {
				id = rs.getInt("GENERATED_KEY");
			}
			else {
				id = -1;
			}
			history.setId(id);
			commited = false;
			return id;
		}
		finally {
			if (rs != null) {
				rs.close();
				rs = null;
			}
			if (statement != null) {
				statement.close();
				statement = null;
			}
		}
		
	}
	
	public static int updateGalleryHistoryByID(GalleryHistory history) throws SQLException {
		Date dateTime = null;
		int paramIndex = 1;
		try(PreparedStatement statement = connection.prepareStatement(SQL_UPD_HIS_BY_ID);) {
			dateTime = new Date(history.getEdate().getTime());
			statement.setString(paramIndex++, history.getTitle());
			statement.setInt(paramIndex++, history.getStatus());
			statement.setString(paramIndex++, history.getEditor());
			statement.setDate(paramIndex++, dateTime);
			statement.setInt(paramIndex++, history.getId());
			int iResult = statement.executeUpdate();
			commited = false;
			return iResult;
		}
	}
	
	public static int updateGalleryHistoryByLink(GalleryHistory history) throws SQLException {
		Date dateTime = null;
		int paramIndex = 1;
		try(PreparedStatement statement = connection.prepareStatement(SQL_UPD_HIS_BY_LINK);) {
			dateTime = new Date(history.getEdate().getTime());
			statement.setInt(paramIndex++, history.getStatus());
			statement.setString(paramIndex++, history.getEditor());
			statement.setDate(paramIndex++, dateTime);
			statement.setString(paramIndex++, history.getGalleryLink());
			int iResult = statement.executeUpdate();
			commited = false;
			return iResult;
		}
	}
	
	public static int updateGalleryHistoryByWebPath(GalleryHistory history) throws SQLException {
		Date dateTime = null;
		int paramIndex = 1;
		try(PreparedStatement statement = connection.prepareStatement(SQL_UPD_HIS_BY_WEB_PATH);) {
			dateTime = new Date(history.getEdate().getTime());
			statement.setInt(paramIndex++, history.getStatus());
			statement.setString(paramIndex++, history.getEditor());
			statement.setDate(paramIndex++, dateTime);
			statement.setString(paramIndex++, history.getWebPath());
			statement.setInt(paramIndex++, history.getWebsiteID());
			int iResult = statement.executeUpdate();
			commited = false;
			return iResult;
		}
	}
	
	public static void commit() throws SQLException {
		if (!commited) {
			connection.commit();
			commited = true;
		}
	}
	
	public static void close() {
		if (connection != null) {
			try {
				commit();
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
	
	public static void main(String[] args) {
		if (args.length <= 0) {
			logger.info("No website link found.");
			return;
		}
		Connection connection = null;
		PreparedStatement statement = null;
		try {
			Class.forName(DBConstants.ACCESS_UCANACCESS_CLASS);
			Properties properties = new Properties();
//			properties.put("password", DBConstants.ACCESS_PASSWORD);
//			properties.put("jackcessOpener", "per.itachi.test.gallery.persist.GalleryJackcessOpener");
//			properties.put("newDatabaseVersion", "V2010");
//			properties.put("charset", "GBK");
			connection = DriverManager.getConnection("jdbc:ucanaccess://data/Database3.accdb", properties);
			connection.setAutoCommit(false);
			statement = connection.prepareStatement("INSERT INTO T_TESTING(URL) VALUES(?)");
			statement.setString(1, "a");
			statement.executeUpdate();
			connection.commit();
		} 
		catch (ClassNotFoundException | SQLException e) {
			logger.error(e.getMessage(), e);
		}
		finally {
			if (statement != null) {
				try {
					statement.close();
					statement = null;
				} 
				catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}
			}
			if (connection != null) {
				try {
					connection.close();
					connection = null;
				} 
				catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
	}
	
}
