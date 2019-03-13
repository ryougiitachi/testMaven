package per.itachi.test.gallery.conf;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GalleryWebsites {
	
	private List<HttpHeader> globalHttpHeaders;
	
	private List<GalleryWebsite> websites;
	
	public GalleryWebsites() {
		globalHttpHeaders = new ArrayList<>();
		websites = new ArrayList<>();
	}
	
	public void addGlobalHttpHeader(HttpHeader header) {
		globalHttpHeaders.add(header);
	}
	
	public Iterator<HttpHeader> iterateGlobalHttpHeader() {
		return globalHttpHeaders.iterator();
	}
	
	public void addGalleryWebsite(GalleryWebsite website) {
		websites.add(website);
	}
	
	public Iterator<GalleryWebsite> iterateGalleryWebsite() {
		return websites.iterator();
	}
	
}
