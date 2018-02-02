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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import per.itachi.test.gallery.GalleryConstants;

public class GalleryUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(GalleryUtils.class);
	
	public static final String EMPTY_STRING = "";
	
	public static final String REGEX_WEBSITE_LINK = "(https?://[\\w-]+(\\.[\\w-]+)*(:\\d+)?)(/[\\w-\\.]+)*";
	
//	public static final Pattern REGEX_WEBSITE_LINK = Pattern.compile("(https?://[\\w-]+(\\.[\\w-]+)+)(/[\\w-\\.]+)+");
	
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
		InputStream is = null;
		OutputStream os = null;
		byte[] buffer = new byte[8192];
		int count = 0;
		boolean isSuccessful = false;
		String strCompressSuffix = null;
		try {
			url = new URL(strURL);
			connection = (HttpURLConnection)url.openConnection(Proxy.NO_PROXY);
			connection.setRequestMethod(GalleryConstants.HTTP_METHOD_GET);
			connection.setRequestProperty(GalleryConstants.HTTP_HEADER_USER_AGENT, headers.get(GalleryConstants.HTTP_HEADER_USER_AGENT));
			connection.setRequestProperty(GalleryConstants.HTTP_HEADER_ACCEPT, headers.get(GalleryConstants.HTTP_HEADER_ACCEPT));
			connection.setRequestProperty(GalleryConstants.HTTP_HEADER_ACCEPT_ENCODING, headers.get(GalleryConstants.HTTP_HEADER_ACCEPT_ENCODING));
			connection.setRequestProperty(GalleryConstants.HTTP_HEADER_CACHE_CONTROL, headers.get(GalleryConstants.HTTP_HEADER_CACHE_CONTROL));
			connection.connect();
			logResponseHeaders(connection);
			strCompressSuffix = getContentEncoding(connection);
			strInitialTmpFilePath = getInitialTmpFilePath(builder, weblink, strCompressSuffix);
			is = connection.getInputStream();
			os = new BufferedOutputStream(new FileOutputStream(strInitialTmpFilePath));
			while ((count = is.read(buffer)) > 0) {
				os.write(buffer, 0, count);
			}
			isSuccessful = true;
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
			if (is != null) {
				try {
					is.close();
					is = null;
				} 
				catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
			if (os != null) {
				try {
					os.close();
					os = null;
				} 
				catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
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
		String strFilePath = GalleryUtils.joinStrings(builder, dir, getFileNameViaUrl(urlLink));
		URL url = null;
		HttpURLConnection connection = null;
		InputStream is = null;
		OutputStream os = null;
		int count = 0;
		boolean isSuccessful = false;
		try {
			url = new URL(urlLink);
			connection = (HttpURLConnection)url.openConnection(Proxy.NO_PROXY);
			connection.setRequestMethod(GalleryConstants.HTTP_METHOD_GET);
			connection.setRequestProperty(GalleryConstants.HTTP_HEADER_USER_AGENT, headers.get(GalleryConstants.HTTP_HEADER_USER_AGENT));
			connection.setRequestProperty(GalleryConstants.HTTP_HEADER_ACCEPT, headers.get(GalleryConstants.HTTP_HEADER_ACCEPT));
			connection.setRequestProperty(GalleryConstants.HTTP_HEADER_ACCEPT_ENCODING, headers.get(GalleryConstants.HTTP_HEADER_ACCEPT_ENCODING));
			connection.setRequestProperty(GalleryConstants.HTTP_HEADER_CACHE_CONTROL, headers.get(GalleryConstants.HTTP_HEADER_CACHE_CONTROL));
			connection.connect();
			logResponseHeaders(connection);
			is = connection.getInputStream();
			os = new BufferedOutputStream(new FileOutputStream(strFilePath));
			while ((count = is.read(buffer)) > 0) {
				os.write(buffer, 0, count);
			}
			isSuccessful = true;
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
			if (is != null) {
				try {
					is.close();
					is = null;
				} 
				catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
			if (os != null) {
				try {
					os.close();
					os = null;
				} 
				catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
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
	
	public static String getBaseUrl(String urlPath) {
		Pattern pattern = Pattern.compile(REGEX_WEBSITE_LINK);
		Matcher matcher = pattern.matcher(urlPath);
		String strBaseUrl = null;
		if (matcher.find()) {
			strBaseUrl = matcher.group(1);
		}
		return strBaseUrl;
	}
	
	public static boolean isCompleteUrlLink(String urlPath) {
		return Pattern.matches(REGEX_WEBSITE_LINK, urlPath);
	}
	
	public static String getCompleteUrlLink(StringBuilder builder, String urlPath, String baseUrl, String currUrl) {
		if (isCompleteUrlLink(urlPath)) {
			return urlPath;
		} 
		else if (urlPath.startsWith("/")) {
			return joinStrings(builder, baseUrl, urlPath);
		}
		else {
			String strCurrPath = currUrl.substring(0, currUrl.lastIndexOf("/") + 1);
			return joinStrings(builder, strCurrPath, urlPath);
		}
	}
	
	public static String getFileNameViaUrl(String urlLink) {
		String strFileName = urlLink.substring(urlLink.lastIndexOf("/") + 1);
		return strFileName;
	}
	
	public static void testFinal() {
		final Object object = null;//if object is modified as static, it says, "only final is permitted".
		if (object == null) {
//			object = new Object();//if object is modified as final, error occurs.
		}
		logger.debug("{}", object);
	}
}
