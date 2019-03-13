package per.itachi.test.gallery.downloader;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import per.itachi.test.gallery.GalleryConstants;
import per.itachi.test.gallery.conf.GalleryWebsiteConfig;
import per.itachi.test.gallery.conf.HttpHeader;
import per.itachi.test.gallery.entity.WebsiteAddress;
import per.itachi.test.gallery.util.BufferUtils;
import per.itachi.test.gallery.util.GalleryUtils;

public class JdkDownloaderStrategy implements DownloaderStrategy {
	
	private final Logger logger = LoggerFactory.getLogger(JdkDownloaderStrategy.class);
	
	private String url;
	
	private String outputDir;
	
	private String outputFileName;
	
	private GalleryWebsiteConfig config;
	
	public JdkDownloaderStrategy(String url, String outputDir, String outputFileName, GalleryWebsiteConfig config) {
		this.url = url;
		this.outputDir = outputDir;
		this.outputFileName = outputFileName;
		this.config = config;
	}

	@Override
	public String download() {
		String strFilePath = Paths.get(this.outputDir, this.outputFileName).toString();//TODO: path to string ?
		byte[] buffer = BufferUtils.getLocalBufferBytes();
		URL url = null;
		HttpURLConnection connection = null;
		int count = 0;
		boolean isSuccessful = false;
		try {
			url = new URL(this.url);
			WebsiteAddress address = GalleryUtils.parseWebsiteAddressByURL(this.url);
			connection = (HttpURLConnection)url.openConnection(Proxy.NO_PROXY);
			connection.setRequestMethod(GalleryConstants.HTTP_METHOD_GET);
			setHttpRequestHeaders(connection, address);
			connection.connect();
//			logResponseHeaders(connection);
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
			logger.error("There is something with URL {}. ", this.url, e);
			isSuccessful = false;
		}
		catch (IOException e) {
			logger.error("Error occured when initialising connection {}. ", this.url, e);
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
	
	private void setHttpRequestHeaders(HttpURLConnection connection, WebsiteAddress address) {
		for (HttpHeader header : config.getGlobalHttpHeaders()) {
			connection.setRequestProperty(header.getName(), header.getName());
		}
		connection.setRequestProperty(GalleryConstants.HTTP_HEADER_HOST, address.getDomain());
	}
	
	private boolean handle200Ok() {
		return false;
	}
}
