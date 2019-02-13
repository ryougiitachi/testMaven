package per.itachi.test.gallery.conf;

public class GalleryWebsite {
	
	private int id;
	
	private String name;
	
	private String charset;
	
	private String parserClassName;

	private GalleryDomains domains;
	
	public GalleryWebsite() {
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public String getParserClassName() {
		return parserClassName;
	}

	public void setParserClassName(String praserClassName) {
		this.parserClassName = praserClassName;
	}

	public GalleryDomains getDomains() {
		return domains;
	}

	public void setDomains(GalleryDomains domains) {
		this.domains = domains;
	}
}
