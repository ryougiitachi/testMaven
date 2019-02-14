package per.itachi.test.gallery.util;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import per.itachi.test.gallery.GalleryConstants;
import per.itachi.test.gallery.entity.GalleryHistory;
import per.itachi.test.gallery.persist.DBConstants;

public class GalleryUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(GalleryUtils.class);
	
	public static final String EMPTY_STRING = "";
	
	public static final String REGEX_URL_PARAMS = "/?[\\w-\\.]+\\?([\\w-\\.%=]+(&[\\w-\\.%=]+)*)$";
	
	public static String joinStrings(StringBuilder builder, Object... args) {
		if (builder == null || args == null) {
			return EMPTY_STRING;
		}
		String strResult;
		for (Object item : args) {
			builder.append(item);
		}
		strResult = builder.toString();
		builder.setLength(0);
		return strResult;
	}
	
	public static String slashDirectoryPath(String path) {
		if (path == null || path.trim().length() == 0) {
			return path;
		}
		if (!path.endsWith("/")) {
			return path + "/";
		}
		else {
			return path;
		}
	}
	
	public static Map<String, String> getDefaultRequestHeaders() {
		Map<String, String> mapHeaders = new HashMap<>();
		mapHeaders.put(GalleryConstants.HTTP_HEADER_USER_AGENT, "Agent:Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.81 Safari/537.36");
		mapHeaders.put(GalleryConstants.HTTP_HEADER_ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		mapHeaders.put(GalleryConstants.HTTP_HEADER_ACCEPT_ENCODING, "gzip, deflate, sdch, br");
		mapHeaders.put(GalleryConstants.HTTP_HEADER_CACHE_CONTROL, "no-cache");
		return mapHeaders;
	}
	
	public static final String loadHtmlByURL(String weblink, Map<String, String> headers) {
		StringBuilder builder = new StringBuilder();
		String strURL = weblink;
		String strInitialTmpFilePath = null;
		URL url = null;
		HttpURLConnection connection = null;
		byte[] buffer = new byte[8192];
		int count = 0;
		boolean isSuccessful = false;
		String strCompressSuffix = null;
		try {
			url = new URL(strURL);
			connection = (HttpURLConnection)url.openConnection(Proxy.NO_PROXY);
			connection.setRequestMethod(GalleryConstants.HTTP_METHOD_GET);
			setHttpRequestHeaders(connection, headers);
			connection.connect();
			logResponseHeaders(connection);
			strCompressSuffix = getContentEncoding(connection);
			strInitialTmpFilePath = getInitialTmpFilePath(builder, weblink, strCompressSuffix);
			try(InputStream is = connection.getInputStream();
					OutputStream os = new BufferedOutputStream(new FileOutputStream(strInitialTmpFilePath));) {
				while ((count = is.read(buffer)) > 0) {
					os.write(buffer, 0, count);
				}
				isSuccessful = true;
			} 
			catch (IOException e) {
				logger.error("Error occured when writing stream into {}. ", strInitialTmpFilePath, e);
				isSuccessful = false;
			}
		} 
		catch (MalformedURLException e) {
			logger.error("There is something with URL {}. ", strURL, e);
			isSuccessful = false;
		}
		catch (IOException e) {
			logger.error("Error occured when initialising connection {}. ", strURL, e);
			isSuccessful = false;
		}
		finally {
			if (connection != null) {
				connection.disconnect();
				connection = null;
			}
		}
		if (!isSuccessful) {
			return null;
		}
		if (strCompressSuffix == null) {
			return strInitialTmpFilePath;
		}
		
		isSuccessful = false;
		String strUncompressFilePath = getUncompressTmpFilePath(builder, weblink);
		try(InputStream gis = new GZIPInputStream(new FileInputStream(strInitialTmpFilePath)); 
				OutputStream bos = new BufferedOutputStream(new FileOutputStream(strUncompressFilePath))) {
			while ((count = gis.read(buffer)) > 0) {
				bos.write(buffer, 0, count);
			}
			isSuccessful = true;
		} 
		catch (FileNotFoundException e) {
			logger.error(e.getMessage(), e);
		} 
		catch (IOException e) {
			logger.error(e.getMessage(), e);
		} 
		if (isSuccessful) {
			return strUncompressFilePath;
		} 
		else {
			return null;
		}
	}
	
	public static String loadFileByURL(String urlLink, Map<String, String> headers, byte[] buffer, String dir, StringBuilder builder) {
		return loadFileByURL(urlLink, headers, getFileNameViaUrl(urlLink), buffer, dir, builder);
	}
	
	public static String loadFileByURL(String urlLink, Map<String, String> headers, String outputFileName, byte[] buffer, String dir, StringBuilder builder) {
		String strFilePath = GalleryUtils.joinStrings(builder, dir, outputFileName);
		URL url = null;
		HttpURLConnection connection = null;
		int count = 0;
		boolean isSuccessful = false;
		try {
			url = new URL(urlLink);
			connection = (HttpURLConnection)url.openConnection(Proxy.NO_PROXY);
			connection.setRequestMethod(GalleryConstants.HTTP_METHOD_GET);
			setHttpRequestHeaders(connection, headers);
			connection.connect();
			logResponseHeaders(connection);
			try(InputStream is = connection.getInputStream();
					OutputStream os = new BufferedOutputStream(new FileOutputStream(strFilePath));) {
				while ((count = is.read(buffer)) > 0) {
					os.write(buffer, 0, count);
				}
				isSuccessful = true;
			} 
			catch (IOException e) {
				logger.error("Error occured when writing stream into {}. ", strFilePath, e);
				isSuccessful = false;
			}
		} 
		catch (MalformedURLException e) {
			logger.error(e.getMessage(), e);
			isSuccessful = false;
		}
		catch (IOException e) {
			logger.error(e.getMessage(), e);
			isSuccessful = false;
		}
		finally {
			if (connection != null) {
				connection.disconnect();
				connection = null;
			}
		}
		if (isSuccessful) {
			return strFilePath;
		} 
		else {
			return null;
		}
	
		
	}
	
	private static void setHttpRequestHeaders(HttpURLConnection connection, Map<String, String> headers) {
		connection.setRequestProperty(GalleryConstants.HTTP_HEADER_USER_AGENT, headers.get(GalleryConstants.HTTP_HEADER_USER_AGENT));
		connection.setRequestProperty(GalleryConstants.HTTP_HEADER_ACCEPT, headers.get(GalleryConstants.HTTP_HEADER_ACCEPT));
		connection.setRequestProperty(GalleryConstants.HTTP_HEADER_ACCEPT_ENCODING, headers.get(GalleryConstants.HTTP_HEADER_ACCEPT_ENCODING));
		connection.setRequestProperty(GalleryConstants.HTTP_HEADER_CACHE_CONTROL, headers.get(GalleryConstants.HTTP_HEADER_CACHE_CONTROL));
	}
	
	private static void logResponseHeaders(URLConnection connection) {
		Map<String, List<String>> mapHeader = connection.getHeaderFields();
		for (String key : mapHeader.keySet()) {
			for (String value : mapHeader.get(key)) {
				logger.debug("{}: {}", key, value);
			}
		}
		logger.debug("The content length of response is {}", connection.getContentLength());
	}
	
	private static String getContentEncoding(URLConnection connection) {
		String strHeaderContentEncoding = connection.getHeaderField("Content-Encoding");
		if (strHeaderContentEncoding == null) {
			return null;
		}
		if (strHeaderContentEncoding.equals("gzip") || strHeaderContentEncoding.equals("deflate") 
				|| strHeaderContentEncoding.equals("sdch") || strHeaderContentEncoding.equals("br")) {
			return ".gz";
		} 
		else {
			return null;
		}
	}
	
	private static String getInitialTmpFilePath(StringBuilder builder, String weblink, String compressedSuffix) {
		String strInitialTmpFilePath = null;
		int iSlash = weblink.lastIndexOf("/");
		String strTmpFileName = weblink.substring(iSlash + 1);
		if (strTmpFileName.length() == 0) {
			int iSlashTmp = weblink.lastIndexOf("/", iSlash - 1);
			strTmpFileName = weblink.substring(iSlashTmp + 1, iSlash);
		}
		if (compressedSuffix == null) {
			strInitialTmpFilePath = GalleryUtils.joinStrings(builder, GalleryUtils.slashDirectoryPath("html"), strTmpFileName, ".html");
		}
		else {
			strInitialTmpFilePath = GalleryUtils.joinStrings(builder, GalleryUtils.slashDirectoryPath("html"), strTmpFileName, compressedSuffix);
		}
		return strInitialTmpFilePath;
	}
	
	private static String getUncompressTmpFilePath(StringBuilder builder, String weblink) {
		String strUncompressTmpFilePath = null;
		int iSlash = weblink.lastIndexOf("/");
		String strTmpFileName = weblink.substring(iSlash + 1);
		if (strTmpFileName.length() == 0) {
			int iSlashTmp = weblink.lastIndexOf("/", iSlash - 1);
			strTmpFileName = weblink.substring(iSlashTmp + 1, iSlash);
		}
		if (strTmpFileName.endsWith(".html")) {
			strUncompressTmpFilePath = GalleryUtils.joinStrings(builder, GalleryUtils.slashDirectoryPath("html"), strTmpFileName);
		} 
		else {
			strUncompressTmpFilePath = GalleryUtils.joinStrings(builder, GalleryUtils.slashDirectoryPath("html"), strTmpFileName, ".html");
		}
		return strUncompressTmpFilePath;
	}
	
	public static String getFileNameViaUrl(String urlLink) {
		String strFileName = urlLink.substring(urlLink.lastIndexOf("/") + 1);
		return strFileName;
	}
	
	public static GalleryHistory getNewGalleryHistory(String baseUrl, String webPath, int websiteID) {
		GalleryHistory history = new GalleryHistory();
		Date date = new Date();
		history.setBaseUrl(baseUrl);
		history.setWebPath(webPath);
		history.setWebsiteID(websiteID);
		history.setStatus(GalleryConstants.PASER_STATUS_INITIAL);//0-initial 1-processing 2-completed 3-failed
		history.setCreator(DBConstants.DEFAULT_OPERATOR);
		history.setCdate(date);
		history.setEditor(DBConstants.DEFAULT_OPERATOR);
		history.setEdate(date);
		return history;
	}
	
	/**
	 * testing temporary method. 
	 * */
	public static void testFinal() {
		final Object object = null;//if object is modified as static, it says, "only final is permitted".
		if (object == null) {
//			object = new Object();//if object is modified as final, error occurs.
		}
		logger.debug("{}", object);
	}
}
