package per.itachi.test.gallery.parser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import per.itachi.test.gallery.conf.GalleryWebsite;
import per.itachi.test.gallery.entity.SisZZOThreadPage;
import per.itachi.test.gallery.entity.SisZZOTorrentPage;
import per.itachi.test.gallery.util.GalleryUtils;
import per.itachi.test.gallery.util.WebUtils;

/**
 * 写的太乱了，没有什么逻辑，各种private方法；
 * 这种程序也没有一定之规，清理上说怎么安排结构都不会不规矩；
 * 可为了可维护性，防止以后自己看的时候看不懂，还是想写的结构化一些；
 * 可是脑子里面也并没有什么执行模型，想到什么是什么，一会儿觉得这样好，一会儿觉得那样好，过会儿又觉得还是刚才好；
 * 如此反复纠结，不仅写出的成品容易乱，还无意义地浪费了很多宝贵的时间；
 * 为避免浪费更多的时间，在编写类似程序的时候，如果没有具体的模型想法，在能保证可读性的情况下，就按照直觉来吧；
 * 想要借鉴各种设计模式，也需要脑中有一个合理的模型来安排各种执行的先后顺序；
 * */
public class SisZZOParser implements Parser {
	
	private static final String DIR_ROOT = "post";//TODO
	
	private static final String WEBSITE_DIRECTORY_NAME = "SisZZO";
	
	private static final String SELECTOR_TITLE_LIST = //TODO
			"div#wrapper > div > div.mainbox.threadlist > form > table > tbody[id]";
	
	private static final String SELECTOR_TITLE_LINK = 
			"tr th.new,th.lock span a";
	
	private static final String SELECTOR_TITLE_CREATOR = 
			"tr td.author cite a";
	
	private static final String SELECTOR_TITLE_LIST_NEXT_PAGE = "div#wrapper div div.pages_btns div.pages a.next";
	
	private static final String SELECTOR_THREAD_ATTIC = 
			"div#wrapper div form div.mainbox.viewthread table tbody tr td.postcontent";
	
	private static final String SELECTOR_THREAD_ATTIC_TITLE = 
			"div.postmessage.defaultpost h2";//TODO 
	
	private static final String SELECTOR_THREAD_ATTIC_CDATE = 
			"div.postinfo";
	
	private static final String SELECTOR_THREAD_ATTIC_CONTENT = 
			"div.postmessage.defaultpost div.t_msgfont";//TODO 
	
	private static final String SELECTOR_THREAD_ATTIC_PICTURE = 
			"div.postmessage.defaultpost div.t_msgfont img";//TODO 
	
	private static final String SELECTOR_THREAD_ATTIC_ATTACHMENT_LINK = 
			"div.postmessage.defaultpost div.box.postattachlist dl.t_attachlist dt a";//TODO
	
	private static final String SELECTOR_ATTACHMENT_LINK = 
			"div.card > div.card-body > a.btn.btn-danger";//TODO 
	
	private static final String SPECIFIC_CREATOR_UID = "13033972";//TODO
	
	private static final String PATTERN_THREAD_CDATE = "\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}";
	
	private static final String FORMAT_THREAD_CDATE = "yyyy-M-d HH:mm";
	
	private static final String FORMAT_DATE_DISPLAY = "yyyyMMdd";
	
	private final Logger logger = LoggerFactory.getLogger(SisZZOParser.class);
	
	private String urlLink;
	
	private String baseUrl;
	
	private GalleryWebsite conf;
	
	private Random random;
	
	private DateFormat formatCdate;
	
	private DateFormat formatDisplay;
	
	public SisZZOParser(String urlLink) {
		this.urlLink = urlLink;
		init();
	}
	
	public SisZZOParser(String urlLink, String baseUrl) {
		this.urlLink = urlLink;
		this.baseUrl = baseUrl;
		init();
	}
	
	private void init() {
		this.random = new Random(System.currentTimeMillis());
		this.formatCdate = new SimpleDateFormat(FORMAT_THREAD_CDATE);
		this.formatDisplay = new SimpleDateFormat(FORMAT_DATE_DISPLAY);
	}

	@Override
	public void execute() {
		logger.info("Start parsing {} URL {}", this.conf.getName(), this.urlLink);
		List<SisZZOThreadPage> listThreadHtml = new ArrayList<>(1000);
		List<SisZZOTorrentPage> listThreadTorrent = new ArrayList<>(1000);
		loadTitleListHtml(listThreadHtml);//load title pages and thread url
		loadThreadHtml(listThreadHtml, listThreadTorrent);//load thread pages and thread pictures. 
		loadThreadTorrent(listThreadTorrent);
	}
	
	/**
	 * download title pages and load thread links. 
	 * */
	private void loadTitleListHtml(List<SisZZOThreadPage> threadHtmls) {
		logger.info("loadTitleListHtml: Start downloading title pages and loading thread links");
		String strNextPageUrlLink = this.urlLink;
		Elements elementsNextPage = null;
		String strUID = SPECIFIC_CREATOR_UID;//TODO: it is better to modify configurable value.
		int countPage = 0;
		int retryTime = 0;
		Map<String, String> mapHeaders = GalleryUtils.getDefaultRequestHeaders();
		try {
			do {
				//load relevant thread title into list. 
				logger.info("loadTitleListHtml: downloading and parsing the {}th list page. ", countPage + 1);
				String strHtmlFileName = joinFileNameForTitleList(strNextPageUrlLink, countPage + 1 , retryTime);
				String strTitleListHtmlPath = GalleryUtils.loadHtmlByURL(strNextPageUrlLink, mapHeaders, strHtmlFileName);
				Document document = Jsoup.parse(new File(strTitleListHtmlPath), this.conf.getCharset());
				//check whether or not page is valid. 
				if (isValidHtmlPage(document) && isValidTitleListPage(document)) {
					retryTime = 0;
					++countPage;
				} 
				else {
					//retry
					logger.info("loadTitleListHtml: {} is invalid, and trying. ", strHtmlFileName);
					++retryTime;
					antiProhibitForHtml();
					continue;
				}
				//parsing
				Elements elementsTitleList = document.select(SELECTOR_TITLE_LIST);
				int countTitle = 0;
				for (Element elementTitleItem : elementsTitleList) {
					Element elementLink = elementTitleItem.selectFirst(SELECTOR_TITLE_LINK);
					Element elementCreator = elementTitleItem.selectFirst(SELECTOR_TITLE_CREATOR);
					if (elementLink != null && elementCreator != null) { 
						//TODO: more readable? 
						String strCreatorHref = Entities.unescape(elementCreator.attr("href"));
						Map<String, String> mapCreatorParams = GalleryUtils.getUrlQueryParam(strCreatorHref);
						if (isSpecificUser(mapCreatorParams, strUID)) {
							SisZZOThreadPage page = new SisZZOThreadPage();
							page.setTitle(elementLink.text());
							page.setCreatorName(elementCreator.text());
							page.setCreatorUID(mapCreatorParams.get("uid"));
							page.setUrlLink(WebUtils.getCompleteUrlLink(Entities.unescape(elementLink.attr("href")), this.baseUrl, strNextPageUrlLink));
							threadHtmls.add(page);
							++countTitle;
						}
					}
				}
				logger.info("loadTitleListHtml: there are {}/{} posts in the {}th page created by UID {}. ", countTitle, elementsTitleList.size(), countPage, strUID);
				//check whether or not there is next page. 
				elementsNextPage = document.select(SELECTOR_TITLE_LIST_NEXT_PAGE);
				if (elementsNextPage != null && elementsNextPage.size() > 0) {
					Element elementNextPage = elementsNextPage.first();
					strNextPageUrlLink = WebUtils.getCompleteUrlLink(Entities.unescape(elementNextPage.attr("href")), this.baseUrl, strNextPageUrlLink);
					//anti-prohibit
					antiProhibitForHtml();
				} 
				else {
					strNextPageUrlLink = null;
				}
			} 
			while (strNextPageUrlLink != null);
		} 
		catch (IOException e) {
			logger.error("Error occured when parsing title list. ", e);
		}
	}
	
	private String joinFileNameForTitleList(String url, int pageNbr, int retryTime) {
		Map<String, String> mapParams = GalleryUtils.getUrlQueryParam(url);
		String strUrlPathName = GalleryUtils.getUrlLastPathWithoutSuffix(url);
		String strFid = mapParams.get("fid");
		String strTypeId = mapParams.get("typeid");
		return String.format("%s-%s-fid%s-type%s-%05d-r%03d.html", this.conf.getName(), strUrlPathName, strFid, strTypeId, pageNbr, retryTime);
	}
	
	/**
	 * check whether or not it is specific user. 
	 * */
	private boolean isSpecificUser(Map<String, String> params, String uid) {
		String strUID = params.get("uid");
		if (uid.equals(strUID)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * download post pages. 
	 * */
	private void loadThreadHtml(List<SisZZOThreadPage> threadHtmls, List<SisZZOTorrentPage> threadTorrents) {
		logger.info("loadThreadHtml: Start downloading {} post pages and pictures. ", threadHtmls.size());
		Map<String, String> mapHeaders = GalleryUtils.getDefaultRequestHeaders();
		int retryTime = 0;
		try {
			Path dirWebsite = createMainDirectory();
			for (SisZZOThreadPage page : threadHtmls) {
				logger.info("loadThreadHtml: parsing the page with title {} and url {}. ", page.getTitle(), page.getCreatorName());
				String strHtmlFileName =  joinFileNameForThread(page, retryTime);
				String strThreadHtmlPath = GalleryUtils.loadHtmlByURL(page.getUrlLink(), mapHeaders, strHtmlFileName);
				page.setHtmlFilePath(strThreadHtmlPath);
				try {
					Document document = Jsoup.parse(new File(strThreadHtmlPath), this.conf.getCharset());
					//check whether or not page is valid. 
					if (isValidHtmlPage(document) && isValidThreadPage(document)) {
						retryTime = 0;
					} 
					else {
						//retry
						logger.info("loadThreadHtml: {} is invalid, and trying. ", strHtmlFileName);
						++retryTime;
						antiProhibitForHtml();
						continue;
					}
					Path dirThreadTitle = createThreadDirectory(page, dirWebsite);
					Element elementAtticPost = document.selectFirst(SELECTOR_THREAD_ATTIC);
					extractIntroToReadme(elementAtticPost, dirThreadTitle);//readme
					extractSnapshotPic(elementAtticPost, dirThreadTitle, page, mapHeaders);//preview image 
					//attachment page link. 
					extractAttachmentLink(elementAtticPost, dirThreadTitle, page);
				} 
				catch (IOException e) {
					logger.error("Error occured when parsing thread {} html file {}. ", page.getTitle(), strThreadHtmlPath, e);
				}
				antiProhibitForHtml();
			}
		} 
		catch (IOException e) {
			logger.error("Error occured when parsing list of threads. ", e);
		}
	}
	
	private String joinFileNameForThread(SisZZOThreadPage page, int retryTime) {
		String strBasePath = GalleryUtils.getUrlLastPathWithoutSuffix(page.getUrlLink());
		Map<String, String> mapParams = GalleryUtils.getUrlQueryParam(page.getUrlLink());
		return String.format("%s-%s-tid%s-%03d.html", this.conf.getName(), strBasePath, mapParams.get("tid"), retryTime);
	}
	
	private Path createMainDirectory() throws IOException {
		Path dirWebsite = Paths.get(DIR_ROOT, this.conf.getMainDirectoryName());
		if (Files.exists(dirWebsite)) {
			return dirWebsite;
		}
		return Files.createDirectory(dirWebsite);
	}
	
	private Path createThreadDirectory(SisZZOThreadPage page, Path mainDir) throws IOException {
		Path dirThreadTitle = Paths.get(mainDir.toString(), page.getCreatorName() + "-" + page.getTitle());
		if (Files.exists(dirThreadTitle)) {
			return dirThreadTitle;
		}
		return Files.createDirectory(dirThreadTitle);
	}
	
	/**
	 * create readme.txt
	 * */
	private void extractIntroToReadme(Element atticPost, Path threadDir) {
		if (atticPost == null) {
			return;
		}
		Path fileReadme = Paths.get(threadDir.toString(), "readme.txt");
		Element elementTitle = atticPost.selectFirst(SELECTOR_THREAD_ATTIC_TITLE);
		Element elementCdate = atticPost.selectFirst(SELECTOR_THREAD_ATTIC_CDATE);
		Element elementContent = atticPost.selectFirst(SELECTOR_THREAD_ATTIC_CONTENT);
		try(BufferedWriter bw = Files.newBufferedWriter(fileReadme, Charset.defaultCharset())) {
			if (elementTitle != null) {
				bw.write(elementTitle.text());
				bw.newLine();
				bw.newLine();
				bw.flush();
			}
			if (elementCdate != null) {
				bw.write(elementCdate.text());
				bw.newLine();
				bw.newLine();
				bw.flush();
			}
			if (elementContent != null) {
				bw.write(elementContent.html());//using html() because of lots of links.
				bw.newLine();
				bw.newLine();
				bw.flush();
			}
		} 
		catch (IOException e) {
			logger.error("Failed to write readme.txt for {}", threadDir, e);
		}
	}
	
	private void extractAttachmentLink(Element atticPost, Path threadTitleDir, SisZZOThreadPage page) {
		logger.info("loadThreadHtml: for the page {}, parsing attachment page link. ", page.getTitle());
		Element elementCdate = atticPost.selectFirst(SELECTOR_THREAD_ATTIC_CDATE);
		Elements elementsTorrent = atticPost.select(SELECTOR_THREAD_ATTIC_ATTACHMENT_LINK);
		if (elementCdate != null && elementsTorrent.size() >= 3) {
			String strTorrentPageHref = Entities.unescape(elementsTorrent.get(1).attr("href"));
			String strCompleteTorrentPageLink = WebUtils.getCompleteUrlLink(strTorrentPageHref, this.baseUrl, page.getUrlLink());
			SisZZOTorrentPage torrentPage = new SisZZOTorrentPage();
			torrentPage.setTorrentFileName(GalleryUtils.joinStrings(page.getCreatorName(), "-", 
					parseThreadCdateString(elementCdate), "-", page.getTitle()));
			torrentPage.setReferPageLink(strCompleteTorrentPageLink);
			torrentPage.setThreadDirPath(threadTitleDir.toString());
		}
		
	}
	
	/**
	 * download preview image. 
	 * */
	private void extractSnapshotPic(Element atticPost, Path threadDir, SisZZOThreadPage page, Map<String, String> headers) {
		if (atticPost == null) {
			return;
		}
		Elements elementsPic = atticPost.select(SELECTOR_THREAD_ATTIC_PICTURE);//TODO 
		if (elementsPic.size() > 0) {//TODO 
			logger.info("loadSnapshotPic: In the page {}, downloading {} pictures. ", page.getTitle(), elementsPic.size());
			for (int i = 0; i < elementsPic.size(); i++) {
				Element elementPic = elementsPic.get(i);
				String strPicUrl = WebUtils.getCompleteUrlLink(Entities.unescape(elementPic.attr("src")), baseUrl, page.getUrlLink());
				String strImgPath = GalleryUtils.loadFileByURL(strPicUrl, headers, String.format("%05d-%s", i + 1, joinPicFileNameByURL(page, strPicUrl)), threadDir.toString());
				logger.debug("loadSnapshotPic: downloaded {}. ", strImgPath);
				//anti-prohibit
				antiProhibitForPic();
			}
		}
	}
	
	private String joinPicFileNameByURL(SisZZOThreadPage page, String urlPicture) {
		if (urlPicture == null) {
			return GalleryUtils.EMPTY_STRING;
		}
		//without query parameters
		String strFileName = GalleryUtils.getUrlLastPathWithoutSuffix(urlPicture);
		String strSuffix = GalleryUtils.getUrlLastPathSuffix(urlPicture);
		if (".jpg".equalsIgnoreCase(strSuffix) || ".jpeg".equalsIgnoreCase(strSuffix) || ".jpe".equalsIgnoreCase(strSuffix) 
				|| ".png".equalsIgnoreCase(strSuffix) || ".bmp".equalsIgnoreCase(strSuffix) || ".gif".equalsIgnoreCase(strSuffix) 
				|| ".tif".equalsIgnoreCase(strSuffix) || ".icon".equalsIgnoreCase(strSuffix)) {
			return GalleryUtils.joinStrings(strFileName, strSuffix);
		}
		//with query parameters
		Map<String, String> mapParams = GalleryUtils.getUrlQueryParam(urlPicture);
		String strId = mapParams.get("id");
		if (strId != null && mapParams.containsKey("jpg")) {
			return GalleryUtils.joinStrings(strFileName, "-", strId, ".jpg");
		}
		return GalleryUtils.EMPTY_STRING;
	}
	
	private String parseThreadCdateString(Element cdate) {
		String strCdate = cdate.text();
		Pattern pattern = Pattern.compile(PATTERN_THREAD_CDATE);
		Matcher matcher = pattern.matcher(strCdate);
		if (matcher.find()) {
			try {
				return formatDisplay.format(formatCdate.parse(matcher.group()));
			} 
			catch (ParseException e) {
				logger.error("Failed to parse {}. ", strCdate, e);
			}
		}
		return "19700101";
	}
	
	/**
	 * download torrent 
	 * */
	private void loadThreadTorrent(List<SisZZOTorrentPage> threadTorrents) {
		logger.info("loadThreadTorrent: Start downloading {} torrents. ", threadTorrents.size());
		Map<String, String> mapHeaders = GalleryUtils.getDefaultRequestHeaders();
		int retryTime = 0;
		for (SisZZOTorrentPage page : threadTorrents) {
			String strHtmlFileName = joinFileNameForThreadTorrent(page, retryTime);
			String strHtmlFilePath = GalleryUtils.loadHtmlByURL(page.getReferPageLink(), mapHeaders, strHtmlFileName);
			File fileHtml = new File(strHtmlFilePath);
			try {
				Document document = Jsoup.parse(fileHtml, this.conf.getCharset());
				//check whether or not page is valid. 
				if (isValidHtmlPage(document) && isValidAttachmentPage(document)) {
					retryTime = 0;
				} 
				else {
					//retry
					logger.info("loadThreadTorrent: {} is invalid, and trying. ", strHtmlFileName);
					++retryTime;
					antiProhibitForHtml();
					continue;
				}
				Element elementTorrent = document.selectFirst(SELECTOR_ATTACHMENT_LINK);
				if (elementTorrent != null) {
					logger.info("loadThreadTorrent: downloading torrent named as {}. ", page.getTorrentFileName());
					String strCompleteTorrentLink = WebUtils.getCompleteUrlLink(Entities.unescape(elementTorrent.attr("href")), this.baseUrl, page.getReferPageLink());
					String strTorrentFilePath = GalleryUtils.loadFileByURL(strCompleteTorrentLink, mapHeaders, page.getTorrentFileName(), page.getThreadDirPath());
					Files.copy(Paths.get(strTorrentFilePath), Paths.get(".", WEBSITE_DIRECTORY_NAME, page.getTorrentFileName()));//TODO: path is unsafe. 
				}
				antiProhibitForHtml();
			} 
			catch (IOException e) {
				logger.error("Error occured when parsing torrent refer page {}. ", strHtmlFilePath, e);
			}
		}
	}
	
	private String joinFileNameForThreadTorrent(SisZZOTorrentPage page, int retryTime) {
		String strBasePath = GalleryUtils.getUrlLastPathWithoutSuffix(page.getReferPageLink());
		Map<String, String> mapParams = GalleryUtils.getUrlQueryParam(page.getReferPageLink());
		return String.format("%s-%s-aid%s-%03d.html", this.conf.getName(), strBasePath, mapParams.get("aid"), retryTime);
	}
	
	/**
	 * Check whether or not the current page is valid. 
	 * Avoid anti-parsing. 
	 *  
	 * */
	private boolean isValidHtmlPage(Document document) {
		Element head = document.head();
		if (head != null) {
			Elements meta = head.select("meta");
			if (meta.size() > 0) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isValidTitleListPage(Document document) {
		Elements elements = document.select(SELECTOR_TITLE_LIST);
		if (elements.size() > 0) {
			return true;
		}
		return false;
	}
	
	private boolean isValidThreadPage(Document document) {
		Elements elements = document.select(SELECTOR_THREAD_ATTIC);
		if (elements.size() > 0) {
			return true;
		}
		return false;
	}
	
	private boolean isValidAttachmentPage(Document document) {
		Elements elements = document.select(SELECTOR_ATTACHMENT_LINK);
		if (elements.size() > 0) {
			return true;
		}
		return false;
	}
	
	/**
	 * avoid website forbidding to parsing. 
	 * */
	private void antiProhibitForHtml() {
		if (conf.getLoadHtmlIntervalBase() <= 0 || conf.getLoadHtmlIntervalOffset() <= 0) {
			return;
		}
		try {
			long lInterval = conf.getLoadHtmlIntervalBase() + random.nextInt(conf.getLoadHtmlIntervalOffset()); 
			logger.debug("The current thread will sleep {} milliseconds.", lInterval);
			Thread.sleep(lInterval);
		} 
		catch (InterruptedException e) {
			logger.error("Interrupted when anti-killing for downloading html. ", e);
		}
	}
	
	/**
	 * avoid website forbidding to parsing. 
	 * */
	private void antiProhibitForPic() {
		if (conf.getLoadPicIntervalBase() <= 0 || conf.getLoadPicIntervalOffset() <= 0) {
			return;
		}
		try {
			long lInterval = conf.getLoadPicIntervalBase() + random.nextInt(conf.getLoadPicIntervalOffset());//350 + 300 
			logger.debug("The current thread will sleep {} milliseconds.", lInterval);
			Thread.sleep(lInterval);
		} 
		catch (InterruptedException e) {
			logger.error("Interrupted when anti-killing for downloading picutre. ", e);
		}
	}

	@Override
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	@Override
	public void setGalleryWebsiteConf(GalleryWebsite conf) {
		this.conf = conf;
	}

	@Override
	public String getTitle() {
		return null;
	}
	
	public static void main(String[] args) {
		DateFormat sdf = new SimpleDateFormat(FORMAT_THREAD_CDATE);
		try {
			System.out.println(sdf.parse("发表于 2019-1-1 15:00:00"));
		} 
		catch (ParseException e) {
			e.printStackTrace();
		}
	}
}
