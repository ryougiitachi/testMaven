package per.itachi.test.gallery.conf;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GalleryDomains {
	
	private List<String> domains;
	
	public GalleryDomains() {
		domains = new ArrayList<>();
	}
	
	public Iterator<String> iterateDomains() {
		return domains.iterator();
	}
	
	public void addDomain(String domain) {
		domains.add(domain);
	}
	
	public void clearDomains() {
		domains.clear();
	}
}
