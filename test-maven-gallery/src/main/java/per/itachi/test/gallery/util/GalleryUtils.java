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
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import per.itachi.test.gallery.GalleryConstants;
import per.itachi.test.gallery.entity.GalleryHistory;
import per.itachi.test.gallery.entity.WebsiteAddress;
import per.itachi.test.gallery.persist.DBConstants;

public class GalleryUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(GalleryUtils.class);
	
	public static final String EMPTY_STRING = "";
	
	public static final String REGEX_WEBSITE_ADDRESS = "(https?://([\\w-]+(\\.[\\w-]+)*(:\\d+)?))((/[\\w-_\\.]+)*)(\\?([\\w-\\.%=]+(&[\\w-\\.%=]+)*))?";
	
	public static final int MATCHER_IDX_WEBSITE_ADDRESS_URL = 1;
	
	public static final int MATCHER_IDX_WEBSITE_ADDRESS_DOMAIN = 2;
	
	public static final int MATCHER_IDX_WEBSITE_ADDRESS_PORT = 4;//format ":9090"
	
	public static final int MATCHER_IDX_WEBSITE_ADDRESS_PATHS = 5;
	
	public static final int MATCHER_IDX_WEBSITE_ADDRESS_PARAMS = 7;
	
	public static final String REGEX_URL_PATH_AND_PARAMS = "/?(([\\w-_]+)(\\.[\\w-_\\.]+)*)(\\?([\\w-\\.%=]+(&[\\w-\\.%=]+)*))?$";
	
	public static String joinStrings(Object... args) {
		StringBuilder builder = BufferUtils.getLocalStringBuilder();
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
		mapHeaders.put(GalleryConstants.HTTP_HEADER_PRAGMA, "no-cache");
		return mapHeaders;
	}
	
	public static final String loadHtmlByURL(String weblink, Map<String, String> headers) {
		String strOutputFileName = getUrlLastPathWithSuffix(weblink);
		return loadHtmlByURL(weblink, headers, strOutputFileName);
	}
	
	public static final String loadHtmlByURL(String weblink, Map<String, String> headers, String outputFileName) {
		String strURL = weblink;
		String strInitialTmpFilePath = null;
		URL url = null;
		HttpURLConnection connection = null;
		Map<String, List<String>> mapHeaders = null;
		byte[] buffer = BufferUtils.getLocalBufferBytes();
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
			mapHeaders = connection.getHeaderFields();
			strCompressSuffix = getContentEncoding(connection);
			strInitialTmpFilePath = getInitialTmpFilePath(outputFileName, strCompressSuffix);
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
		String strUncompressFilePath = getUncompressTmpFilePath(outputFileName);
		try(InputStream gis = new GZIPInputStream(new FileInputStream(strInitialTmpFilePath)); 
				OutputStream bos = new BufferedOutputStream(new FileOutputStream(strUncompressFilePath))) {
			while ((count = gis.read(buffer)) > 0) {
				bos.write(buffer, 0, count);
			}
			isSuccessful = true;
		} 
		catch (FileNotFoundException e) {
			logger.error("Failed to uncompress {} because file doesn't exist. ", strInitialTmpFilePath, e);
		} 
		catch (IOException e) {
			logger.error("Failed to uncompress {}. ", strInitialTmpFilePath, e);
		} 
		if (isSuccessful) {
			return strUncompressFilePath;
		} 
		else {
			return null;
		}
	}
	
	public static String loadFileByURL(String urlLink, Map<String, String> headers, String outputFileName, String dir) {
		return loadFileByURL(urlLink, headers, outputFileName, BufferUtils.getLocalBufferBytes(), dir, BufferUtils.getLocalStringBuilder());
	}
	
	public static String loadFileByURL(String urlLink, Map<String, String> headers, byte[] buffer, String dir, StringBuilder builder) {
		return loadFileByURL(urlLink, headers, getFileNameViaUrl(urlLink), buffer, dir, builder);
	}
	
	public static String loadFileByURL(String urlLink, Map<String, String> headers, String outputFileName, byte[] buffer, String dir, StringBuilder builder) {
		String strFilePath = Paths.get(dir, outputFileName).toString();//TODO: path to string ? 
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
			logger.error("There is something with URL {}. ", urlLink, e);
			isSuccessful = false;
		}
		catch (IOException e) {
			logger.error("Error occured when initialising connection {}. ", urlLink, e);
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
		logResponseHeaders(connection.getHeaderFields());
		logger.debug("The content length of response is {}", connection.getContentLength());
	}
	
	private static void logResponseHeaders(Map<String, List<String>> headers) {
		for (String key : headers.keySet()) {
			for (String value : headers.get(key)) {
				logger.debug("{}: {}", key, value);
			}
		}
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
	
	/**
	 * @param fileName	
	 * */
	private static String getInitialTmpFilePath(String fileName, String compressedSuffix) {
		String strInitialTmpFilePath = null;
		if (compressedSuffix == null) {
			strInitialTmpFilePath = GalleryUtils.joinStrings(GalleryUtils.slashDirectoryPath("html"), fileName, ".html");
		}
		else {
			strInitialTmpFilePath = GalleryUtils.joinStrings(GalleryUtils.slashDirectoryPath("html"), fileName, compressedSuffix);
		}
		return strInitialTmpFilePath;
	}
	
	/**
	 * @param fileName	
	 * */
	private static String getUncompressTmpFilePath(String fileName) {
		String strUncompressTmpFilePath = null;
		if (fileName.endsWith(".html")) {
			strUncompressTmpFilePath = GalleryUtils.joinStrings(GalleryUtils.slashDirectoryPath("html"), fileName);
		} 
		else {
			strUncompressTmpFilePath = GalleryUtils.joinStrings(GalleryUtils.slashDirectoryPath("html"), fileName, ".html");
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
	
	public static String getUrlLastPathWithSuffix(String url) {
		Pattern patternUrl = Pattern.compile(REGEX_URL_PATH_AND_PARAMS);
		Matcher matcherUrl = patternUrl.matcher(url);
		String strLastPath = EMPTY_STRING;
		if (matcherUrl.find()) {
			strLastPath = matcherUrl.group(1);
		}
		return strLastPath;
	}
	
	public static String getUrlLastPathWithoutSuffix(String url) {
		Pattern patternUrl = Pattern.compile(REGEX_URL_PATH_AND_PARAMS);
		Matcher matcherUrl = patternUrl.matcher(url);
		String strLastPath = EMPTY_STRING;
		if (matcherUrl.find()) {
			strLastPath = matcherUrl.group(2);
		}
		return strLastPath;
	}
	
	public static String getUrlLastPathSuffix(String url) {
		Pattern patternUrl = Pattern.compile(REGEX_URL_PATH_AND_PARAMS);
		Matcher matcherUrl = patternUrl.matcher(url);
		String strLastPath = EMPTY_STRING;
		if (matcherUrl.find()) {
			strLastPath = matcherUrl.group(3);
		}
		return strLastPath;
	}
	
	public static Map<String, String> getUrlQueryParam(String url) {
		Pattern patternUrl = Pattern.compile(REGEX_URL_PATH_AND_PARAMS);
		Matcher matcherUrl = patternUrl.matcher(url);
		Map<String, String> mapParams = new HashMap<>();
		if (matcherUrl.find()) {
			String strParams = matcherUrl.group(5);
			String[] arrayParams = strParams.split("&");
			for (String strParam : arrayParams) {
				String[] arrayKeyValue = strParam.split("=");
				if (arrayKeyValue.length >= 2) {
					mapParams.put(arrayKeyValue[0], arrayKeyValue[1]);
				}
				else {
					mapParams.put(strParam, EMPTY_STRING);
				}
			}
		}
		return mapParams;
	}
	
	public static WebsiteAddress parseWebsiteAddressByURL(String url) {
		if (url == null) {
			return null;
		}
		WebsiteAddress entity = new WebsiteAddress();
		Pattern pattern = Pattern.compile(REGEX_WEBSITE_ADDRESS);
		Matcher matcher = pattern.matcher(url);
		if (matcher.find()) {
			//domain with protocal 
			String strDomainWithProtocal = matcher.group(MATCHER_IDX_WEBSITE_ADDRESS_URL);
			//domain 
			String strDomain = matcher.group(MATCHER_IDX_WEBSITE_ADDRESS_DOMAIN);
			//port
			String strPort = matcher.group(MATCHER_IDX_WEBSITE_ADDRESS_PORT);
			int iPort = 0;
			if (strPort != null) {
				try {
					iPort = Integer.parseInt(strPort.substring(1));
				} 
				catch (NumberFormatException e) {
					logger.error("Failed to convert {} as integer", strPort, e);
				}
			}
			//paths
			String strPaths = matcher.group(MATCHER_IDX_WEBSITE_ADDRESS_PATHS);
			List<String> listPath = new ArrayList<>();
			if (strPaths != null) {
				for (String strPath : strPaths.split("/")) {
					listPath.add(strPath);
				}
			}
			//parameters
			String strParams = matcher.group(MATCHER_IDX_WEBSITE_ADDRESS_PARAMS);
			Map<String, String> mapParams = new HashMap<>();
			if (strParams != null) {
				String[] arrayParams = strParams.split("&");
				for (String strParam : arrayParams) {
					String[] arrayKeyValue = strParam.split("=");
					if (arrayKeyValue.length >= 2) {
						mapParams.put(arrayKeyValue[0], arrayKeyValue[1]);
					}
					else {
						mapParams.put(strParam, EMPTY_STRING);
					}
				}
			}
			//fill 
			entity.setDomainWithProtocal(strDomainWithProtocal);
			entity.setDomain(strDomain);
			entity.setPort(iPort);
			entity.setPaths(listPath);
			entity.setQueryParams(mapParams);
		}
		return entity;
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
