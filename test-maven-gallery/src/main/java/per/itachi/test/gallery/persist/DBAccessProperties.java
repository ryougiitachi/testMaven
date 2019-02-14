package per.itachi.test.gallery.persist;

import java.util.HashMap;
import java.util.Map;

public class DBAccessProperties {
	
	private String username;
	
	private String password;
	
	private String jdbcDriverClass;
	
	private String jdbcUrl;
	
	private int initNum;
	
	private Map<String, Object> customs;
	
	public DBAccessProperties() {
		customs = new HashMap<>();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getJdbcDriverClass() {
		return jdbcDriverClass;
	}

	public void setJdbcDriverClass(String jdbcDriverClass) {
		this.jdbcDriverClass = jdbcDriverClass;
	}

	public String getJdbcUrl() {
		return jdbcUrl;
	}

	public void setJdbcUrl(String jdbcUrl) {
		this.jdbcUrl = jdbcUrl;
	}

	public int getInitNum() {
		return initNum;
	}

	public void setInitNum(int initNum) {
		this.initNum = initNum;
	}

	public Map<String, Object> getCustoms() {
		return customs;
	}
	
	public void setCustomProperty(String name, Object value) {
		customs.put(name, value);
	}
}
