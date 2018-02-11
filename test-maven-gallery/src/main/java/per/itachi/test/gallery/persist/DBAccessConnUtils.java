package per.itachi.test.gallery.persist;

import java.sql.Connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBAccessConnUtils {
	
	private static final Logger logger =LoggerFactory.getLogger(DBAccessConnUtils.class);
	
	private static Connection connection = null;
	
	public static void connect() throws ClassNotFoundException {
		Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
	}

	public static void main(String[] args) {
		try {
			connect();
		} 
		catch (ClassNotFoundException e) {
			logger.error(e.getMessage(), e);
		}
	}
}
