package per.itachi.test.gallery.entity;

import java.util.List;
import java.util.Map;

public class WebsiteAddress {
	
	private String domainWithProtocal;
	
	private String domain;
	
	private int port;
	
	private List<String> paths;
	
	private Map<String, String> queryParams;

	public String getDomainWithProtocal() {
		return domainWithProtocal;
	}

	public void setDomainWithProtocal(String domainWithProtocal) {
		this.domainWithProtocal = domainWithProtocal;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public List<String> getPaths() {
		return paths;
	}

	public void setPaths(List<String> paths) {
		this.paths = paths;
	}

	public Map<String, String> getQueryParams() {
		return queryParams;
	}

	public void setQueryParams(Map<String, String> queryParams) {
		this.queryParams = queryParams;
	}
}
