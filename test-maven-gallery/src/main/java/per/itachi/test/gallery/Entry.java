package per.itachi.test.gallery;

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
//		String strHtmlPath = loadHtmlByURL(strWebLink);
		String strBaseUrl = GalleryUtils.getBaseUrl(strWebLink);
		logger.debug("{}", strBaseUrl);
	}

}
