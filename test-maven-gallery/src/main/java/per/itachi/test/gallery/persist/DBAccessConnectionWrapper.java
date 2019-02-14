package per.itachi.test.gallery.persist;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;

public class DBAccessConnectionWrapper implements Connection {
	
	private BlockingQueue<Connection> pool;
	
	private Connection nativeConn;
	
	public DBAccessConnectionWrapper(BlockingQueue<Connection> pool, Connection nativeConn) {
		this.pool = pool;
		this.nativeConn = nativeConn;
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return nativeConn.unwrap(iface);
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return nativeConn.isWrapperFor(iface);
	}

	@Override
	public Statement createStatement() throws SQLException {
		return nativeConn.createStatement();
	}

	@Override
	public PreparedStatement prepareStatement(String sql) throws SQLException {
		return nativeConn.prepareStatement(sql);
	}

	@Override
	public CallableStatement prepareCall(String sql) throws SQLException {
		return nativeConn.prepareCall(sql);
	}

	@Override
	public String nativeSQL(String sql) throws SQLException {
		return nativeConn.nativeSQL(sql);
	}

	@Override
	public void setAutoCommit(boolean autoCommit) throws SQLException {
		nativeConn.setAutoCommit(autoCommit);
	}

	@Override
	public boolean getAutoCommit() throws SQLException {
		return nativeConn.getAutoCommit();
	}

	@Override
	public void commit() throws SQLException {
		nativeConn.commit();
	}

	@Override
	public void rollback() throws SQLException {
		nativeConn.rollback();

	}

	@Override
	public void close() throws SQLException {
		try {
			pool.put(nativeConn);
		} 
		catch (InterruptedException e) {
			//TODO 
		}
	}

	@Override
	public boolean isClosed() throws SQLException {
		return nativeConn.isClosed();
	}

	@Override
	public DatabaseMetaData getMetaData() throws SQLException {
		return nativeConn.getMetaData();
	}

	@Override
	public void setReadOnly(boolean readOnly) throws SQLException {
		nativeConn.setReadOnly(readOnly);
	}

	@Override
	public boolean isReadOnly() throws SQLException {
		return nativeConn.isReadOnly();
	}

	@Override
	public void setCatalog(String catalog) throws SQLException {
		nativeConn.setCatalog(catalog);
	}

	@Override
	public String getCatalog() throws SQLException {
		return nativeConn.getCatalog();
	}

	@Override
	public void setTransactionIsolation(int level) throws SQLException {
		nativeConn.setTransactionIsolation(level);
	}

	@Override
	public int getTransactionIsolation() throws SQLException {
		return nativeConn.getTransactionIsolation();
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		return nativeConn.getWarnings();
	}

	@Override
	public void clearWarnings() throws SQLException {
		nativeConn.clearWarnings();
	}

	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
		return nativeConn.createStatement(resultSetType, resultSetConcurrency);
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
			throws SQLException {
		return nativeConn.prepareStatement(sql, resultSetType, resultSetConcurrency);
	}

	@Override
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		return nativeConn.prepareCall(sql, resultSetType, resultSetConcurrency);
	}

	@Override
	public Map<String, Class<?>> getTypeMap() throws SQLException {
		return nativeConn.getTypeMap();
	}

	@Override
	public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
		nativeConn.setTypeMap(map);
	}

	@Override
	public void setHoldability(int holdability) throws SQLException {
		nativeConn.setHoldability(holdability);
	}

	@Override
	public int getHoldability() throws SQLException {
		return nativeConn.getHoldability();
	}

	@Override
	public Savepoint setSavepoint() throws SQLException {
		return nativeConn.setSavepoint();
	}

	@Override
	public Savepoint setSavepoint(String name) throws SQLException {
		return nativeConn.setSavepoint(name);
	}

	@Override
	public void rollback(Savepoint savepoint) throws SQLException {
		nativeConn.rollback(savepoint);
	}

	@Override
	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		nativeConn.releaseSavepoint(savepoint);
	}

	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		return nativeConn.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency,
			int resultSetHoldability) throws SQLException {
		return nativeConn.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	@Override
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency,
			int resultSetHoldability) throws SQLException {
		return nativeConn.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
		return nativeConn.prepareStatement(sql, autoGeneratedKeys);
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
		return nativeConn.prepareStatement(sql, columnIndexes);
	}

	@Override
	public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
		return nativeConn.prepareStatement(sql, columnNames);
	}

	@Override
	public Clob createClob() throws SQLException {
		return nativeConn.createClob();
	}

	@Override
	public Blob createBlob() throws SQLException {
		return nativeConn.createBlob();
	}

	@Override
	public NClob createNClob() throws SQLException {
		return nativeConn.createNClob();
	}

	@Override
	public SQLXML createSQLXML() throws SQLException {
		return nativeConn.createSQLXML();
	}

	@Override
	public boolean isValid(int timeout) throws SQLException {
		return nativeConn.isValid(timeout);
	}

	@Override
	public void setClientInfo(String name, String value) throws SQLClientInfoException {
		nativeConn.setClientInfo(name, value);
	}

	@Override
	public void setClientInfo(Properties properties) throws SQLClientInfoException {
		nativeConn.setClientInfo(properties);
	}

	@Override
	public String getClientInfo(String name) throws SQLException {
		return nativeConn.getClientInfo(name);
	}

	@Override
	public Properties getClientInfo() throws SQLException {
		return nativeConn.getClientInfo();
	}

	@Override
	public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
		return nativeConn.createArrayOf(typeName, elements);
	}

	@Override
	public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
		return nativeConn.createStruct(typeName, attributes);
	}

	@Override
	public void setSchema(String schema) throws SQLException {
		nativeConn.setSchema(schema);
	}

	@Override
	public String getSchema() throws SQLException {
		return nativeConn.getSchema();
	}

	@Override
	public void abort(Executor executor) throws SQLException {
		nativeConn.abort(executor);
	}

	@Override
	public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
		nativeConn.setNetworkTimeout(executor, milliseconds);
	}

	@Override
	public int getNetworkTimeout() throws SQLException {
		return nativeConn.getNetworkTimeout();
	}

}
