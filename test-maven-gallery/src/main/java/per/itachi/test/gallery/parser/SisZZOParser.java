package per.itachi.test.gallery.parser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import per.itachi.test.gallery.GalleryConstants;
import per.itachi.test.gallery.entity.SisZZOThreadPage;
import per.itachi.test.gallery.util.GalleryUtils;
import per.itachi.test.gallery.util.WebUtils;

public class SisZZOParser implements Parser {
	
	private static final String WEBSITE_CHARSET = "GBK";//TODO
	
	private static final String WEBSITE_DIRECTORY_NAME = "SisZZO";//TODO
	
	private static final String SELECTOR_TITLE_LIST = //TODO
			"div#wrapper div div.mainbox.threadlist form table#forum_143 tbody";
	
	private static final String SELECTOR_THREAD_LINK = "tr th.new span a";
	
	private static final String SELECTOR_THREAD_CREATOR = "tr td.author cite a";
	
	private static final String SELECTOR_NEXT_PAGE = "div#wrapper div div.pages_btns div.pages a.next";
	
	private static final String SELECTOR_THREAD_CDATE = 
			"div#wrapper div div.mainbox.viewthread table tbody tr td.postcontent div.postinfo";
	
	private static final String SELECTOR_THREAD_CONTENT = "";//TODO 
	
	private static final String SELECTOR_THREAD_VIDEO_TITLE = "";//TODO 
	
	private static final String SELECTOR_THREAD_VIDEO_SNAPSHOT = "";//TODO 
	
	private static final String SPECIFIC_CREATOR_UID = "";
	
	private static final String PATTERN_TITLE = "";
	
	private static final String PATTERN_HREF_UID = "[\\w-/]+\\?([\\w=]+\\&)*uid=([0-9]+)(\\&[\\w=]+)*";
	
	private static final String FORMAT_THREAD_CDATE = "发表于 yyyy-M-d HH:mm:ss";//TODO
	
	private final Logger logger = LoggerFactory.getLogger(SisZZOParser.class);
	
	private String urlLink;
	
	private String baseUrl;
	
	public SisZZOParser(String urlLink) {
		this.urlLink = urlLink;
		this.baseUrl = WebUtils.getBaseUrl(urlLink);
	}

	@Override
	public void execute() {
		logger.info("Start parsing SisZZO URL {}", this.urlLink);
		List<SisZZOThreadPage> listThreadHtml = new ArrayList<>(1000);
		loadTitleListHtml(listThreadHtml);//load title pages and thread url
		loadThreadHtml(listThreadHtml);//load thread pages and thread pictures. 
	}
	
	/**
	 * download title pages and load thread links. 
	 * */
	private void loadTitleListHtml(List<SisZZOThreadPage> threadHtmls) {
		String strNextPageUrlLink = this.urlLink;
		Elements elementsNextPage = null;
		String strUID = SPECIFIC_CREATOR_UID;//TODO
		Map<String, String> mapHeaders = GalleryUtils.getDefaultRequestHeaders();
		try {
			do {
				//load relevant thread title into list. 
				String strTitleListHtmlPath = GalleryUtils.loadHtmlByURL(strNextPageUrlLink, mapHeaders);
				Document document = Jsoup.parse(new File(strTitleListHtmlPath), WEBSITE_CHARSET);
				Elements elementsTitleList = document.select(SELECTOR_TITLE_LIST);
				for (Element elementTitle : elementsTitleList) {
					Element elementLink = elementTitle.selectFirst(SELECTOR_THREAD_LINK);
					Element elementCreator = elementTitle.selectFirst(SELECTOR_THREAD_CREATOR);
					if (elementLink != null && elementCreator != null && isSpecificUser(elementCreator, strUID)) {//TODO: post creator 
						SisZZOThreadPage page = new SisZZOThreadPage();
						page.setTitle(elementLink.text());
						page.setUrlLink(elementLink.attr("href"));
						threadHtmls.add(page);
					}
				}
				//check whether or not there is next page. 
				elementsNextPage = document.select(SELECTOR_NEXT_PAGE);
				if (elementsNextPage != null && elementsNextPage.size() > 0) {
					Element elementNextPage = elementsNextPage.first();
					strNextPageUrlLink = WebUtils.getCompleteUrlLink(elementNextPage.attr("href"), this.baseUrl, strNextPageUrlLink);
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
	private boolean isSpecificUser(Element creator, String uid) {
		String strLink = creator.attr("href");
		Map<String, String> mapParams = getUrlQueryParam(strLink);
		String strUID = "uid";
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
	private void loadThreadHtml(List<SisZZOThreadPage> threadHtmls) {
		Map<String, String> mapHeaders = GalleryUtils.getDefaultRequestHeaders();
		byte[] buffer = new byte[8196];
		StringBuilder builder = new StringBuilder(512);
		try {
			Path dirWebsite = createMainDirectory();
			for (SisZZOThreadPage page : threadHtmls) {
				String strThreadHtmlPath = GalleryUtils.loadHtmlByURL(page.getUrlLink(), mapHeaders);
				page.setHtmlFilePath(strThreadHtmlPath);
				Document document = Jsoup.parse(new File(strThreadHtmlPath), WEBSITE_CHARSET);
				Elements elementsTitle = document.select(SELECTOR_THREAD_VIDEO_TITLE);//TODO 
				Elements elementsPic = document.select(SELECTOR_THREAD_VIDEO_SNAPSHOT);//TODO 
				Path dirThreadTitle = createThreadDirectory(page.getTitle(), dirWebsite);
				createReadmeFile(document, dirThreadTitle);
				if (elementsTitle.size() > 0 && elementsPic.size() > 0) {//TODO 
					for (int i = 0; i < elementsTitle.size(); i++) {
						Element elementPic = elementsPic.get(i);
						String strPicUrl = WebUtils.getCompleteUrlLink(builder, elementPic.text(), baseUrl, page.getUrlLink());
						String strImgPath = GalleryUtils.loadFileByURL(strPicUrl, mapHeaders, String.format("%02d-%s", i + 1, getPicFileName(strPicUrl)), buffer, dirThreadTitle.toString(), builder);
					}
				}
			}
		} 
		catch (IOException e) {
			logger.error("Error occured when parsing list of thread. ", e);
		}
	}
	
	private Path createMainDirectory() throws IOException {
		Path dirWebsite = Paths.get(GalleryConstants.DEFAULT_PICTURE_PATH, WEBSITE_DIRECTORY_NAME);
		return Files.createDirectory(dirWebsite);
	}
	
	private Path createThreadDirectory(String threadTitle, Path mainDir) throws IOException {
		Path dirThreadTitle = Paths.get(mainDir.toString(), threadTitle);
		return Files.createDirectory(dirThreadTitle);
	}
	
	private void createReadmeFile(Document document, Path threadDir) {
		try {
			Path fileReadme = Files.createFile(Paths.get(threadDir.toString(), "reademe.txt"));
			Element elementContent = document.selectFirst(SELECTOR_THREAD_CONTENT);
			if (elementContent != null) {
				try(BufferedWriter bw = Files.newBufferedWriter(fileReadme, Charset.defaultCharset())) {
					bw.write(elementContent.text());
					bw.flush();
				} 
				catch (IOException e) {
					logger.error("Failed to write readme.txt for {}", threadDir, e);
				}
			}
		} 
		catch (IOException e) {
			logger.error("Failed to create readme.txt for {}", threadDir, e);
		}
	}
	
	private String getPicFileName(String urlPicture) {
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

	@Override
	public String getTitle() {
		return null;
	}
	
	public static void main(String[] args) {
		String strUrl = "http://38.103.161.130/forum/attachment.php?aid=2890054&clickDownload=1";
		Map<String, String> mapParams = new HashMap<>();
		Pattern patternUrlParams = Pattern.compile(GalleryUtils.REGEX_URL_PARAMS);
		Matcher matcherUrlParams = patternUrlParams.matcher(strUrl);
		if (matcherUrlParams.find()) {
			String strParams = matcherUrlParams.group(1);
			System.out.println(strParams);
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
