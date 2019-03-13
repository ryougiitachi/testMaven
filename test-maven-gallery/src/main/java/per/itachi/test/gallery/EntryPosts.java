package per.itachi.test.gallery;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import per.itachi.test.gallery.conf.GalleryWebsite;
import per.itachi.test.gallery.conf.GalleryWebsiteConfig;
import per.itachi.test.gallery.parser.Parser;
import per.itachi.test.gallery.util.WebUtils;

public class EntryPosts {
	
	private static final Logger logger = LoggerFactory.getLogger(EntryPosts.class);
	
	private static GalleryWebsiteConfig confGalleryWebsite;

	public static void main(String[] args) {
		if (args.length <= 0) {
			logger.info("No argument found");
			return;
		}
		initialise();
		String strUrlLink = args[0];
		String strBaseUrl = WebUtils.getBaseUrl(strUrlLink);
		if (strBaseUrl == null) {
			logger.error("{} is not a valid website link.", strUrlLink);
			return;
		}
		Class<?> clazzParser = confGalleryWebsite.getPraserClass(strBaseUrl);
		GalleryWebsite website = confGalleryWebsite.getWebsite(strBaseUrl);
		if (clazzParser == null || website == null) {
			logger.error("{} has been not included in gallery.", strUrlLink);
			return;
		}

		boolean exit = false;
		Parser parser = null;
		long lStartPoint, lEndPoint;
		try {
			parser = instantiateNewParser(clazzParser, strUrlLink);
			parser.setBaseUrl(strBaseUrl);
			parser.setGalleryWebsiteConfig(confGalleryWebsite);
			lStartPoint = System.currentTimeMillis();
			parser.execute();
			lEndPoint = System.currentTimeMillis();
			logger.info("It took {} milliseconds to download gallery from {}. ", lEndPoint - lStartPoint, strUrlLink);
		} 
		catch (NoSuchMethodException | SecurityException | InvocationTargetException | IllegalAccessException
				| InstantiationException e) {
			logger.error("Error occurs when parsing {}. ", strUrlLink, e);
			exit = true;
		}
		if (exit) {
			logger.error("exit");
			return;
		}
	}
	
	private static void initialise() {
		confGalleryWebsite = GalleryWebsiteConfig.load(GalleryConstants.DEFAULT_WEBSITE_CONF_PATH);
	}

	private static Parser instantiateNewParser(Class<?> clazzParser, String link) 
			throws NoSuchMethodException, SecurityException, InvocationTargetException, IllegalAccessException, InstantiationException {
		Constructor<?> constructor = clazzParser.getConstructor(String.class);
		Parser parser = (Parser)constructor.newInstance(link);
		return parser;
	}
}
