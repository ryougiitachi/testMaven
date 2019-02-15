package per.itachi.test.gallery.conf;

public class GalleryWebsite {
	
	private int id;
	
	private String name;
	
	private String charset;
	
	private String parserClassName;
	
	private int loadHtmlIntervalBase;
	
	private int loadHtmlIntervalOffset;
	
	private int loadPicIntervalBase;
	
	private int loadPicIntervalOffset;
	
	private String mainDirectoryName;

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

	public int getLoadHtmlIntervalBase() {
		return loadHtmlIntervalBase;
	}

	public void setLoadHtmlIntervalBase(int loadHtmlIntervalBase) {
		this.loadHtmlIntervalBase = loadHtmlIntervalBase;
	}

	public int getLoadHtmlIntervalOffset() {
		return loadHtmlIntervalOffset;
	}

	public void setLoadHtmlIntervalOffset(int loadHtmlIntervalOffset) {
		this.loadHtmlIntervalOffset = loadHtmlIntervalOffset;
	}

	public int getLoadPicIntervalBase() {
		return loadPicIntervalBase;
	}

	public void setLoadPicIntervalBase(int loadPicIntervalBase) {
		this.loadPicIntervalBase = loadPicIntervalBase;
	}

	public int getLoadPicIntervalOffset() {
		return loadPicIntervalOffset;
	}

	public void setLoadPicIntervalOffset(int loadPicIntervalOffset) {
		this.loadPicIntervalOffset = loadPicIntervalOffset;
	}

	public String getMainDirectoryName() {
		return mainDirectoryName;
	}

	public void setMainDirectoryName(String mainDirectoryName) {
		this.mainDirectoryName = mainDirectoryName;
	}

	public GalleryDomains getDomains() {
		return domains;
	}

	public void setDomains(GalleryDomains domains) {
		this.domains = domains;
	}
}
