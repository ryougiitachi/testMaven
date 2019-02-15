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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import per.itachi.test.gallery.GalleryConstants;
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
	
	private static final String WEBSITE_DIRECTORY_NAME = "SisZZO";//TODO
	
	private static final String SELECTOR_TITLE_LIST = //TODO
			"div#wrapper div div.mainbox.threadlist form table#forum_143 tbody";
	
	private static final String SELECTOR_TITLE_LINK = "tr th.new span a";
	
	private static final String SELECTOR_TITLE_CREATOR = "tr td.author cite a";
	
	private static final String SELECTOR_TITLE_LIST_NEXT_PAGE = "div#wrapper div div.pages_btns div.pages a.next";
	
	private static final String SELECTOR_THREAD_TITLE = "";//TODO 
	
	private static final String SELECTOR_THREAD_CDATE = 
			"div#wrapper div div.mainbox.viewthread table tbody tr td.postcontent div.postinfo";
	
	private static final String SELECTOR_THREAD_CONTENT = "";//TODO 
	
	private static final String SELECTOR_THREAD_VIDEO_TITLE = "";//TODO 
	
	private static final String SELECTOR_THREAD_VIDEO_SNAPSHOT = "";//TODO 
	
	private static final String SELECTOR_THREAD_TORRENT_LINK = "";//TODO
	
	private static final String SELECTOR_TORRENT_LINK = "";//TODO 
	
	private static final String SPECIFIC_CREATOR_UID = "";
	
	private static final String PATTERN_TITLE = "";
	
	private static final String FORMAT_THREAD_CDATE = "发表于 yyyy-M-d HH:mm:ss";//TODO
	
	private static final String FORMAT_DATE_DISPLAY = "yyyyMMdd";//TODO
	
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
		logger.info("Start parsing SisZZO URL {}", this.urlLink);
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
		logger.info("Start downloading title pages and loading thread links");
		String strNextPageUrlLink = this.urlLink;
		Elements elementsNextPage = null;
		String strUID = SPECIFIC_CREATOR_UID;//TODO: it is better to modify configurable value. 
		Map<String, String> mapHeaders = GalleryUtils.getDefaultRequestHeaders();
		try {
			do {
				//load relevant thread title into list. 
				String strTitleListHtmlPath = GalleryUtils.loadHtmlByURL(strNextPageUrlLink, mapHeaders);
				Document document = Jsoup.parse(new File(strTitleListHtmlPath), this.conf.getCharset());
				Elements elementsTitleList = document.select(SELECTOR_TITLE_LIST);
				for (Element elementTitleItem : elementsTitleList) {
					Element elementLink = elementTitleItem.selectFirst(SELECTOR_TITLE_LINK);
					Element elementCreator = elementTitleItem.selectFirst(SELECTOR_TITLE_CREATOR);
					if (elementLink != null && elementCreator != null) { 
						//TODO: more readable? 
						String strCreatorHref = elementCreator.attr("href");
						Map<String, String> mapCreatorParams = getUrlQueryParam(strCreatorHref);
						if (isSpecificUser(mapCreatorParams, strUID)) {
							SisZZOThreadPage page = new SisZZOThreadPage();
							page.setTitle(elementLink.text());
							page.setCreatorName(elementCreator.text());
							page.setCreatorUID(mapCreatorParams.get("uid"));
							page.setUrlLink(WebUtils.getCompleteUrlLink(elementLink.attr("href"), this.baseUrl, strNextPageUrlLink));
							threadHtmls.add(page);
						}
					}
				}
				//check whether or not there is next page. 
				elementsNextPage = document.select(SELECTOR_TITLE_LIST_NEXT_PAGE);
				if (elementsNextPage != null && elementsNextPage.size() > 0) {
					Element elementNextPage = elementsNextPage.first();
					strNextPageUrlLink = WebUtils.getCompleteUrlLink(elementNextPage.attr("href"), this.baseUrl, strNextPageUrlLink);
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
		logger.info("Start downloading post pages and pictures. ");
		Map<String, String> mapHeaders = GalleryUtils.getDefaultRequestHeaders();
		byte[] buffer = new byte[8196];
		StringBuilder builder = new StringBuilder(512);
		try {
			Path dirWebsite = createMainDirectory();
			for (SisZZOThreadPage page : threadHtmls) {
				String strThreadHtmlPath = GalleryUtils.loadHtmlByURL(page.getUrlLink(), mapHeaders);
				page.setHtmlFilePath(strThreadHtmlPath);
				Document document = Jsoup.parse(new File(strThreadHtmlPath), this.conf.getCharset());
				Path dirThreadTitle = createThreadDirectory(page, dirWebsite);
				loadIntroToReadme(document, dirThreadTitle);//readme
				loadSnapshotPic(document, dirThreadTitle, page, mapHeaders, buffer, builder);//preview image 
				//torrent 
				Element elementCdate = document.selectFirst(SELECTOR_THREAD_CDATE);
				Element elementTorrent = document.selectFirst(SELECTOR_THREAD_TORRENT_LINK);
				if (elementCdate != null && elementTorrent != null) {
					String strTorrentPageHref = elementTorrent.attr("href");
					String strCompleteTorrentPageLink = WebUtils.getCompleteUrlLink(strTorrentPageHref, this.baseUrl, page.getUrlLink());
					SisZZOTorrentPage torrentPage = new SisZZOTorrentPage();
					torrentPage.setTorrentFileName(GalleryUtils.joinStrings(builder, page.getCreatorName(), "-", 
							formatDisplay.format(formatCdate.parse(elementCdate.text())), "-", page.getTitle()));
					torrentPage.setReferPageLink(strCompleteTorrentPageLink);
					torrentPage.setThreadDirPath(dirThreadTitle.toString());
				}
			}
		} 
		catch (IOException e) {
			logger.error("Error occured when parsing list of threads. ", e);
		} 
		catch (ParseException e) {
			logger.error("Error occured when joining file name {}. ", e.getErrorOffset(), e);
		}
	}
	
	private Path createMainDirectory() throws IOException {
		Path dirWebsite = Paths.get(GalleryConstants.DEFAULT_PICTURE_PATH, this.conf.getMainDirectoryName());
		return Files.createDirectory(dirWebsite);
	}
	
	private Path createThreadDirectory(SisZZOThreadPage page, Path mainDir) throws IOException {
		Path dirThreadTitle = Paths.get(mainDir.toString(), page.getCreatorName() + "-" + page.getCreatorUID());
		return Files.createDirectory(dirThreadTitle);
	}
	
	/**
	 * create readme.txt
	 * */
	private void loadIntroToReadme(Document document, Path threadDir) {
		try {
			Path fileReadme = Files.createFile(Paths.get(threadDir.toString(), "reademe.txt"));
			Element elementTitle = document.selectFirst(SELECTOR_THREAD_TITLE);
			Element elementCdate = document.selectFirst(SELECTOR_THREAD_CDATE);
			Element elementContent = document.selectFirst(SELECTOR_THREAD_CONTENT);
			try(BufferedWriter bw = Files.newBufferedWriter(fileReadme, Charset.defaultCharset())) {
				if (elementTitle != null) {
					bw.write(elementTitle.text());
					bw.newLine();
					bw.flush();
				}
				if (elementCdate != null) {
					bw.write(elementCdate.text());
					bw.newLine();
					bw.flush();
				}
				if (elementContent != null) {
					bw.write(elementContent.html());//using html() because of lots of links.
					bw.newLine();
					bw.flush();
				}
			} 
			catch (IOException e) {
				logger.error("Failed to write readme.txt for {}", threadDir, e);
			}
		} 
		catch (IOException e) {
			logger.error("Failed to create readme.txt for {}", threadDir, e);
		}
	}
	
	/**
	 * download preview image. 
	 * */
	private void loadSnapshotPic(Document document, Path threadDir, SisZZOThreadPage page, 
			Map<String, String> headers, byte[] buffer, StringBuilder builder) {
		Elements elementsTitle = document.select(SELECTOR_THREAD_VIDEO_TITLE);//TODO 
		Elements elementsPic = document.select(SELECTOR_THREAD_VIDEO_SNAPSHOT);//TODO 
		if (elementsTitle.size() > 0 && elementsPic.size() > 0) {//TODO 
			for (int i = 0; i < elementsTitle.size(); i++) {
				Element elementPic = elementsPic.get(i);
				String strPicUrl = WebUtils.getCompleteUrlLink(builder, elementPic.text(), baseUrl, page.getUrlLink());
				String strImgPath = GalleryUtils.loadFileByURL(strPicUrl, headers, String.format("%05d-%s", i + 1, getPicFileNameByURL(strPicUrl)), buffer, threadDir.toString(), builder);
				//anti-prohibit
				antiProhibitForPic();
			}
		}
	}
	
	private String getPicFileNameByURL(String urlPicture) {
		if (urlPicture == null) {
			return GalleryUtils.EMPTY_STRING;
		}
		//with query parameters
		Pattern patternUrlFileName = Pattern.compile(WebUtils.REGEX_URL_FILENAME);
		Matcher matcherUrlFileName = patternUrlFileName.matcher(urlPicture);
		if (matcherUrlFileName.find()) {
			return matcherUrlFileName.group(1);
		}
		//without query parameters
		Map<String, String> mapParams = getUrlQueryParam(urlPicture);
		return GalleryUtils.EMPTY_STRING;
	}
	
	private Map<String, String> getUrlQueryParam(String url) {
		Map<String, String> mapParams = new HashMap<>();
		Pattern patternUrlParams = Pattern.compile(GalleryUtils.REGEX_URL_PARAMS);
		Matcher matcherUrlParams = patternUrlParams.matcher(url);
		if (matcherUrlParams.find()) {
			String strParams = matcherUrlParams.group(1);
			String[] arrayParams = strParams.split("&");
			for (String strParam : arrayParams) {
				String[] arrayKeyValue = strParam.split("=");
				if (arrayKeyValue.length >= 2) {
					mapParams.put(arrayKeyValue[0], arrayKeyValue[1]);
				}
				else {
					mapParams.put(strParam, GalleryUtils.EMPTY_STRING);
				}
			}
		}
		return mapParams;
	}
	
	/**
	 * download torrent 
	 * */
	private void loadThreadTorrent(List<SisZZOTorrentPage> threadTorrents) {
		logger.info("Start downloading torrents. ");
		Map<String, String> mapHeaders = GalleryUtils.getDefaultRequestHeaders();
		byte[] buffer = new byte[8192];
		StringBuilder builder = new StringBuilder(512);
		for (SisZZOTorrentPage page : threadTorrents) {
			String strHtmlFilePath = GalleryUtils.loadHtmlByURL(page.getReferPageLink(), mapHeaders);
			File fileHtml = new File(strHtmlFilePath);
			try {
				Document document = Jsoup.parse(fileHtml, this.conf.getCharset());
				Element elementTorrent = document.selectFirst(SELECTOR_TORRENT_LINK);
				if (elementTorrent != null) {
					String strCompleteTorrentLink = WebUtils.getCompleteUrlLink(elementTorrent.attr("href"), this.baseUrl, page.getReferPageLink());
					String strTorrentFilePath = GalleryUtils.loadFileByURL(strCompleteTorrentLink, mapHeaders, page.getTorrentFileName(), buffer, page.getThreadDirPath(), builder);
					Files.copy(Paths.get(strTorrentFilePath), Paths.get(".", WEBSITE_DIRECTORY_NAME, page.getTorrentFileName()));//TODO: path is unsafe. 
				}
			} 
			catch (IOException e) {
				logger.error("Error occured when parsing torrent refer page {}. ", strHtmlFilePath, e);
			}
		}
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
		if (args.length <= 0) {
			return;
		}
		String strUrl = args[0];
		Map<String, String> mapParams = new HashMap<>();
		Pattern patternUrlParams = Pattern.compile(GalleryUtils.REGEX_URL_PARAMS);
		Matcher matcherUrlParams = patternUrlParams.matcher(strUrl);
		if (matcherUrlParams.find()) {
			String strParams = matcherUrlParams.group(1);
			System.out.println(strParams);
			String strParamsTest = matcherUrlParams.group(2);
			System.out.println(strParamsTest);
			String[] arrayParams = strParams.split("&");
			for (String strParam : arrayParams) {
				String[] arrayKeyValue = strParam.split("=");
				if (arrayKeyValue.length >= 2) {
					mapParams.put(arrayKeyValue[0], arrayKeyValue[1]);
					System.out.println(arrayKeyValue[0] + " " + arrayKeyValue[1]);
				}
				else {
					mapParams.put(strParam, GalleryUtils.EMPTY_STRING);
				}
			}
		}
	}
}
