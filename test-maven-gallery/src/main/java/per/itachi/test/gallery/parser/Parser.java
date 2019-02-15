package per.itachi.test.gallery.parser;

import per.itachi.test.gallery.conf.GalleryWebsite;

public interface Parser {
	
	void execute();
	
	void setBaseUrl(String baseUrl);
	
	void setGalleryWebsiteConf(GalleryWebsite conf);

	String getTitle();
}
