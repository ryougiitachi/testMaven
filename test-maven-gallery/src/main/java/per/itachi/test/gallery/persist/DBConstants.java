package per.itachi.test.gallery.persist;

public class DBConstants {
	
	public static final String ACCESS_PASSWORD = "testing";
	
	public static final String ACCESS_CHARSET = "UTF-16LE";//Unexpectedly, the default charset of access is UTF-16LE?!
	
	public static final String ACCESS_HXTT_CLASS = "com.hxtt.sql.access.AccessDriver";
	
	public static final String ACCESS_UCANACCESS_CLASS = "net.ucanaccess.jdbc.UcanaccessDriver";
	
	public static final String ACCESS_ODBC_URL = "jdbc:odbc:driver={Microsoft Access Driver(*.mdb,*.accdb)};DBQ=data/gallery.accdb;UID=;PWD=testing";
	
	public static final String ACCESS_HXTT_URL = "jdbc:access://data/gallery.accdb";//It is OK for both single and double slash /.
	
	public static final String ACCESS_UCANACCESS_URL = "jdbc:ucanaccess://data/gallery.accdb";
	
	public static final String DEFAULT_OPERATOR = "system";
}
