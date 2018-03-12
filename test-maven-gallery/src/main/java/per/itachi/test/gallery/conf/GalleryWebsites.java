package per.itachi.test.gallery.conf;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GalleryWebsites {
	
	private List<GalleryWebsite> websites;
	
	public GalleryWebsites() {
		websites = new ArrayList<>();
	}
	
	public void addGalleryWebsite(GalleryWebsite website) {
		websites.add(website);
	}
	
	public Iterator<GalleryWebsite> iterateGalleryWebsite() {
		return websites.iterator();
	}
}
