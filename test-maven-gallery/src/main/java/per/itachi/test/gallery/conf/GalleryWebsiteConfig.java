package per.itachi.test.gallery.conf;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.apache.commons.digester3.Digester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import per.itachi.test.gallery.util.WebUtils;

public class GalleryWebsiteConfig {
	
	private static final Logger logger = LoggerFactory.getLogger(GalleryWebsiteConfig.class);
	
	private String filePath;
	
	private List<HttpHeader> globalHttpHeaders;
	
	private Map<String, Class<?>> mapUrlToClass;
	
	private Map<String, GalleryWebsite> mapUrlToWebsite;
	
	public GalleryWebsiteConfig() {
		globalHttpHeaders = new ArrayList<>();
		mapUrlToClass = new HashMap<>();
		mapUrlToWebsite = new HashMap<>();
	}
	
	@PostConstruct
	public void init() throws IOException, SAXException, ClassNotFoundException {
		//initialise digester paths. 
		Digester digester = new Digester();
		digester.setValidating(false);
		digester.addObjectCreate("websites", GalleryWebsites.class);
		digester.addObjectCreate("websites/global-http-headers/header", HttpHeader.class);
		digester.addSetProperties("websites/global-http-headers/header");
//		digester.addBeanPropertySetter("websites/global-http-headers/header/name", "name");
		digester.addBeanPropertySetter("websites/global-http-headers/header", "value");
		digester.addSetNext("websites/global-http-headers/header", "addGlobalHttpHeader");
		digester.addObjectCreate("websites/website", GalleryWebsite.class);
		digester.addBeanPropertySetter("websites/website/id", "id");
		digester.addBeanPropertySetter("websites/website/name", "name");
		digester.addBeanPropertySetter("websites/website/charset", "charset");
		digester.addBeanPropertySetter("websites/website/parser-class-name", "parserClassName");
		digester.addBeanPropertySetter("websites/website/load-html-interval-base", "loadHtmlIntervalBase");
		digester.addBeanPropertySetter("websites/website/load-html-interval-offset", "loadHtmlIntervalOffset");
		digester.addBeanPropertySetter("websites/website/load-pic-interval-base", "loadPicIntervalBase");
		digester.addBeanPropertySetter("websites/website/load-pic-interval-offset", "loadPicIntervalOffset");
		digester.addBeanPropertySetter("websites/website/main-directory-name", "mainDirectoryName");
		digester.addObjectCreate("websites/website/domains", GalleryDomains.class);
		digester.addCallMethod("websites/website/domains/value", "addDomain", 1);
		digester.addCallParam("websites/website/domains/value", 0);
		digester.addSetNext("websites/website/domains", "setDomains");
		digester.addSetNext("websites/website", "addGalleryWebsite");
		//read configuration file data stream. 
		File file = new File(filePath);
		GalleryWebsites websites = null;
		try(InputStream fis = new FileInputStream(file)) {
			websites = digester.<GalleryWebsites>parse(fis);
		} 
		catch (IOException | SAXException e) {
			logger.error("Error occurs when reading {}", filePath, e);
			throw e;
		}
		// websites/global-http-headers 
		HttpHeader httpHeader = null;
		Iterator<HttpHeader> iteratorHttpHeader = websites.iterateGlobalHttpHeader();
		while (iteratorHttpHeader.hasNext()) {
			httpHeader = iteratorHttpHeader.next();
			this.globalHttpHeaders.add(httpHeader);
		}
		// websites/website 
		GalleryWebsite website = null;
		Class<?> clazzParser = null;
		Iterator<GalleryWebsite> iteratorWebsite = websites.iterateGalleryWebsite();
		Iterator<String> iteratorDomain = null;
		String strDomain = null;
		Pattern patternBaseUrl = Pattern.compile(WebUtils.REGEX_WEBSITE_BASE_URL);
		Matcher matcherBaseUrl = null;
		try {
			while (iteratorWebsite.hasNext()) {
				website = iteratorWebsite.next();
				clazzParser = Class.forName(website.getParserClassName());
				iteratorDomain = website.getDomains().iterateDomains();
				while (iteratorDomain.hasNext()) {
					strDomain = iteratorDomain.next();
					matcherBaseUrl = patternBaseUrl.matcher(strDomain);
					if (matcherBaseUrl.matches()) {
						strDomain = matcherBaseUrl.group(1);
						if (this.mapUrlToClass.get(strDomain) == null) {
							this.mapUrlToClass.put(strDomain, clazzParser);
						}//conf.mapUrlToClass.get(strDomain) == null
						if (this.mapUrlToWebsite.get(strDomain) == null) {
							this.mapUrlToWebsite.put(strDomain, website);
						}
					}//matcherBaseUrl.matches()
				}//iteratorDomain.hasNext()
			}
		} 
		catch (ClassNotFoundException e) {
			logger.error("Error occurs when initialising configurations about webistes.", e);
			throw e;
		}
	}
	
	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public List<HttpHeader> getGlobalHttpHeaders() {
		return globalHttpHeaders;
	}
	
	public Class<?> getPraserClass(String domain) {
		return mapUrlToClass.get(domain);
	}
	
	public GalleryWebsite getWebsite(String domain) {
		return mapUrlToWebsite.get(domain);
	}
	
	public static void main(String[] args) throws ClassNotFoundException, IOException, SAXException {
		if (args.length <= 0) {
			logger.info("No conf found.");
			return;
		}
		GalleryWebsiteConfig config = new GalleryWebsiteConfig();
		config.setFilePath(args[0]);
		config.init();
		logger.info("Global HTTP Header is {}. ", config.globalHttpHeaders);
	}
}
