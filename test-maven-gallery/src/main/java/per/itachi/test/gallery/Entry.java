package per.itachi.test.gallery;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import per.itachi.test.gallery.util.GalleryUtils;

public class Entry {
	
	private static final Logger logger = LoggerFactory.getLogger(Entry.class);

	public static void main(String[] args) {
		if (args.length <= 0) {
			return;
		}
		String strWebLink = args[0];
		test(strWebLink);
	}
	
	private static final void test(String weblink) {
		StringBuilder builder = new StringBuilder();
		String strURL = weblink;
		String strTmpFileName = strURL.substring(strURL.lastIndexOf("/") + 1);
		String strTmpFilePath1 = null;
		String strResContentEncoding = null;
		URL url = null;
		URLConnection connection = null;
		InputStream sis = null;
		OutputStream sos = null;
		byte[] buffer = new byte[8192];
		int count = 0;
		boolean isSuccessful = false;
		try {
			url = new URL(strURL);
			connection = url.openConnection(Proxy.NO_PROXY);
			fillURLHeaders(connection);
			connection.connect();
			strResContentEncoding = connection.getHeaderField("Content-Encoding");
			strTmpFilePath1 = GalleryUtils.joinStrings(builder, "html/", strTmpFileName, ".", strResContentEncoding);
			logURLHeaders(connection);
			sis = connection.getInputStream();
			sos = new BufferedOutputStream(new FileOutputStream(strTmpFilePath1));
			while ((count = sis.read(buffer)) > 0) {
				sos.write(buffer, 0, count);
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
			if (sis != null) {
				try {
					sis.close();
					sis = null;
				} 
				catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
			if (sos != null) {
				try {
					sos.close();
					sos = null;
				} 
				catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
		if (!isSuccessful) {
			return;
		}
		
		String strTmpFilePath2 = null;
		if (strTmpFileName.endsWith(".html")) {
			strTmpFilePath2 = GalleryUtils.joinStrings(builder, "html/", strTmpFileName);
		} 
		else {
			strTmpFilePath2 = GalleryUtils.joinStrings(builder, "html/", strTmpFileName, ".html");
		}
		InputStream is = null;
		OutputStream os = null;
		try {
			is = new GZIPInputStream(new FileInputStream(strTmpFilePath1));
			os = new BufferedOutputStream(new FileOutputStream(strTmpFilePath2));
			while ((count = is.read(buffer)) > 0) {
				os.write(buffer, 0, count);
			}
		} 
		catch (FileNotFoundException e) {
			logger.error(e.getMessage(), e);
		} 
		catch (IOException e) {
			logger.error(e.getMessage(), e);
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
		}
		//$("section.container div.content-wrap div.content article.article-content p>img")
	}
	
	private static void fillURLHeaders(URLConnection connection) {
		connection.setRequestProperty("User-Agent", "Agent:Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.81 Safari/537.36");
		connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		connection.setRequestProperty("Accept-Encoding", "gzip, deflate, sdch, br");
		connection.setRequestProperty("Cache-Control", "no-cache");
		connection.setRequestProperty("Accept-Encoding", "gzip, deflate, sdch, br");
	}
	
	private static void logURLHeaders(URLConnection connection) {
		Map<String, List<String>> mapHeader = connection.getHeaderFields();
		for (String key : mapHeader.keySet()) {
			for (String value : mapHeader.get(key)) {
				logger.debug("{}: {}", key, value);
			}
		}
		logger.debug("The content length of response is {}", connection.getContentLength());
	}

}
