package per.itachi.test.gallery.persist;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import per.itachi.test.gallery.persist.entity.GalleryHistory;

@Repository
public class DBAccessRepository {
	
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
	
	private final Logger logger =LoggerFactory.getLogger(DBAccessRepository.class);
	
	@Autowired
	private DataSource hikariDataSource;
	
	public GalleryHistory getGalleryHistoryByLink(String link) throws SQLException {
		try(Connection connection = hikariDataSource.getConnection();
				PreparedStatement statement = connection.prepareStatement(SQL_GET_HIS_BY_LINK);) {
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
	}
	
	public GalleryHistory getGalleryHistoryByWebPath(String webPath, int websiteID) throws SQLException {
		try(Connection connection = hikariDataSource.getConnection();
				PreparedStatement statement = connection.prepareStatement(SQL_GET_HIS_BY_WEB_PATH);) {
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
	}
	
	public int insertGalleryHistory(GalleryHistory history) throws SQLException {
		try(Connection connection = hikariDataSource.getConnection();
				PreparedStatement statement = connection.prepareStatement(SQL_INS_HIS, Statement.RETURN_GENERATED_KEYS);) {
			ResultSet rs = null;
			int id;
			Date dateTime = null;
			int paramIndex = 1;
			dateTime = new Date(history.getCdate().getTime());
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
			rs.close();
			history.setId(id);
			return id;
		}
	}
	
	public int updateGalleryHistoryByID(GalleryHistory history) throws SQLException {
		try(Connection connection = hikariDataSource.getConnection();
				PreparedStatement statement = connection.prepareStatement(SQL_UPD_HIS_BY_ID);) {
			int paramIndex = 1;
			Date dateTime = new Date(history.getEdate().getTime());
			statement.setString(paramIndex++, history.getTitle());
			statement.setInt(paramIndex++, history.getStatus());
			statement.setString(paramIndex++, history.getEditor());
			statement.setDate(paramIndex++, dateTime);
			statement.setInt(paramIndex++, history.getId());
			int iResult = statement.executeUpdate();
			return iResult;
		}
	}
	
	public int updateGalleryHistoryByLink(GalleryHistory history) throws SQLException {
		try(Connection connection = hikariDataSource.getConnection();
				PreparedStatement statement = connection.prepareStatement(SQL_UPD_HIS_BY_LINK);) {
			int paramIndex = 1;
			Date dateTime  = new Date(history.getEdate().getTime());
			statement.setInt(paramIndex++, history.getStatus());
			statement.setString(paramIndex++, history.getEditor());
			statement.setDate(paramIndex++, dateTime);
			statement.setString(paramIndex++, history.getGalleryLink());
			int iResult = statement.executeUpdate();
			return iResult;
			
		}
	}
	
	public int updateGalleryHistoryByWebPath(GalleryHistory history) throws SQLException {
		try(Connection connection = hikariDataSource.getConnection();
				PreparedStatement statement = connection.prepareStatement(SQL_UPD_HIS_BY_WEB_PATH);) {
			int paramIndex = 1;
			Date dateTime = new Date(history.getEdate().getTime());
			statement.setInt(paramIndex++, history.getStatus());
			statement.setString(paramIndex++, history.getEditor());
			statement.setDate(paramIndex++, dateTime);
			statement.setString(paramIndex++, history.getWebPath());
			statement.setInt(paramIndex++, history.getWebsiteID());
			int iResult = statement.executeUpdate();
			return iResult;
		}
	}
}
