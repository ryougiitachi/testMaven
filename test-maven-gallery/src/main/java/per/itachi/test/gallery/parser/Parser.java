package per.itachi.test.gallery.parser;

import per.itachi.test.gallery.conf.GalleryWebsiteConfig;

public interface Parser {
	
	void execute();
	
	void setBaseUrl(String baseUrl);
	
	void setGalleryWebsiteConfig(GalleryWebsiteConfig config);

	String getTitle();
}
