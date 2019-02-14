package per.itachi.test.gallery.persist;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import javax.sql.DataSource;

public class DBAccessDataSource implements DataSource {
	
	private static final int DEFAULT_CONN_SIZE = 10;
	
	private DBAccessProperties properties;
	
	private BlockingQueue<Connection> pool;
	
	public DBAccessDataSource(DBAccessProperties properties) {
		this.properties = properties;
	}
	
	public void init() throws ClassNotFoundException, SQLException {
		Class.forName(properties.getJdbcDriverClass());
		int connSize = 0;
		if (properties.getInitNum() > 0) {
			connSize = properties.getInitNum();
		}
		else {
			connSize = DEFAULT_CONN_SIZE;
		}
		// set properties 
		Properties properties = new Properties();
		properties.put("username", this.properties.getUsername());
		properties.put("password", this.properties.getPassword());
		properties.putAll(this.properties.getCustoms());
		// initialise connection pool 
		Connection connection = null;
		pool = new LinkedBlockingQueue<>(connSize);
		for (int i = 0; i < connSize; i++) {
			connection = DriverManager.getConnection(this.properties.getJdbcUrl(), properties);
			connection.setAutoCommit(false);
			pool.add(new DBAccessConnectionWrapper(pool, connection));
		}
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return null;
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {

	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {

	}

	@Override
	public int getLoginTimeout() throws SQLException {
		return 0;
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return null;
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return null;
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return false;
	}

	@Override
	public Connection getConnection() throws SQLException {
		try {
			return pool.take();
		} 
		catch (InterruptedException e) {
			//TODO
			return null;
		}
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		return null;
	}
}
