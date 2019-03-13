package per.itachi.test.gallery.parser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import per.itachi.test.gallery.GalleryConstants;
import per.itachi.test.gallery.conf.GalleryWebsite;
import per.itachi.test.gallery.conf.GalleryWebsiteConfig;
import per.itachi.test.gallery.entity.NineSixxxNetPage;
import per.itachi.test.gallery.util.GalleryUtils;
import per.itachi.test.gallery.util.WebUtils;

public class NineSixxxNetParser implements Parser {
	
	private static final String SELECTOR_TITLE = 
			"section.container div.content-wrap div.content header.article-header h1.article-title";
	
	private static final String SELECTOR_PUBLISH_DATE = 
			"section.container div.content-wrap div.content header.article-header ul.article-meta>li";
	
	private static final String SELECTOR_INTRODUCTION = 
			"section.container div.content-wrap div.content article.article-content p";
	
	private static final String SELECTOR_NEXT_PAGE = 
			"section.container div.content-wrap div.content div.pagination.pagination-multi ul li.next-page a";
	
	private static final String SELECTOR_IMG = 
			"section.container div.content-wrap div.content article.article-content p>img";
	
	private final Logger logger = LoggerFactory.getLogger(NineSixxxNetParser.class);
	
	private String urlLink;
	
	private String baseUrl;
	
	private GalleryWebsiteConfig config;
	
	private GalleryWebsite websiteConfig;
	
	private String title;
	
	public NineSixxxNetParser(String urlLink) {
		this.urlLink = urlLink;
	}
	
	public NineSixxxNetParser(String urlLink, String baseUrl) {
		this.urlLink = urlLink;
		this.baseUrl = baseUrl;;
	}

	@Override
	public void execute() {
		this.websiteConfig = config.getWebsite(this.baseUrl);
		String strUrlLink = this.urlLink;
		logger.info("Start parsing NineSixxxNet URL {}", strUrlLink);
		Map<String, String> mapHeaders = GalleryUtils.getDefaultRequestHeaders();
		String strTmpHtmlPath = GalleryUtils.loadHtmlByURL(strUrlLink, mapHeaders);
		List<NineSixxxNetPage> listTmpHtmlPath = new ArrayList<>();
		List<String> listImageLink = new ArrayList<>();
		String strPicDirPath = null;
		NineSixxxNetPage page = null;
		try {
			page = new NineSixxxNetPage();
			page.setCurrUrlLink(strUrlLink);
			page.setTmpFilePath(strTmpHtmlPath);
			listTmpHtmlPath.add(page);
			strPicDirPath = generatePicDirectory(strTmpHtmlPath);
			if (strPicDirPath != null) {
				loadTmpHtmlList(strTmpHtmlPath, listTmpHtmlPath, mapHeaders);// download html
				loadImageLinkList(listTmpHtmlPath, listImageLink);// load image links
				downloadImages(listImageLink, mapHeaders, strPicDirPath);//download imagaes
			}
		} 
		catch (IOException e) {
			logger.error("Error occured when downloading pictures from {}. ", strTmpHtmlPath, e);
		}
		logger.info("Finish parsing {}", strUrlLink);
	}
	
	/**
	 * generate the directory where pictures will be placed. 
	 * */
	private String generatePicDirectory(String tmpHtmlPath) throws IOException {
		StringBuilder builder = new StringBuilder();
		File fileTmpHtmlPath = new File(tmpHtmlPath);
		Document document = Jsoup.parse(fileTmpHtmlPath, this.websiteConfig.getCharset());//GBK
		Element elementTitle = document.selectFirst(SELECTOR_TITLE);
		String strTitle = this.title = elementTitle.text();
		String strPicDirPath = GalleryUtils.joinStrings(builder, 
				GalleryConstants.DEFAULT_PICTURE_PATH, File.separator, getDateString(), 
				strTitle, File.separator);
		File filePicDirPath = new File(strPicDirPath);
		if (filePicDirPath.exists()) {
			logger.info("{} downloaded before", this.urlLink);
			return null;
		}
		filePicDirPath.mkdir();
		generateReadme(document, strPicDirPath);
		return strPicDirPath;
	}
	
	private String getDateString() {
		String strDate = null;
		Calendar calendar = Calendar.getInstance();
		DateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		strDate = sdf.format(calendar.getTime());
		return strDate;
	}
	
	private void generateReadme(Document document, String picDirPath) {
		Element elementPublishDate = document.selectFirst(SELECTOR_PUBLISH_DATE);
		Elements elementsArticle = document.select(SELECTOR_INTRODUCTION);
		Path fileReadme = Paths.get(picDirPath, "readme.txt");
		try(BufferedWriter bw = Files.newBufferedWriter(fileReadme);) {
			bw.write(this.urlLink);
			bw.newLine();
			bw.newLine();
			if (elementPublishDate != null) {
				bw.write(elementPublishDate.text());
				bw.newLine();
				bw.newLine();
			}
			for (Element elementArticle : elementsArticle) {
				if (elementArticle.hasText()) {
					bw.write(elementArticle.text());
					bw.newLine();
				}
			}
		} 
		catch (IOException e) {
			logger.error("Error occured when creating readme.txt in {}. ", picDirPath, e);
		}
	}
	
	private void loadTmpHtmlList(String initTmpHtmlPath, List<NineSixxxNetPage> tmpFilePaths, Map<String, String> headers) throws IOException {
		logger.info("Start downloading html files.");
		StringBuilder builder = new StringBuilder();
		Elements elementsNextPage = null;
		Element elementNextPage = null;
		String strTmpFilePath = initTmpHtmlPath;
		String strNextLink = null;
		String strCurrUrl = this.urlLink;
		NineSixxxNetPage page = null;
		
		Random random = new Random(System.currentTimeMillis());
		File fileTmpHtmlPath = new File(strTmpFilePath);
		Document document = Jsoup.parse(fileTmpHtmlPath, this.websiteConfig.getCharset());//GBK
		for(elementsNextPage = document.select(SELECTOR_NEXT_PAGE); 
				elementsNextPage.size() > 0;
				elementsNextPage = document.select(SELECTOR_NEXT_PAGE)) {
			elementNextPage = elementsNextPage.first();
			strNextLink = WebUtils.getCompleteUrlLink(builder, elementNextPage.attr("href"), this.baseUrl, strCurrUrl);
			strTmpFilePath = GalleryUtils.loadHtmlByURL(strNextLink, headers);
			fileTmpHtmlPath = new File(strTmpFilePath);
			document = Jsoup.parse(fileTmpHtmlPath, this.websiteConfig.getCharset());
			strCurrUrl = strNextLink;
			page = new NineSixxxNetPage();
			page.setCurrUrlLink(strCurrUrl);
			page.setTmpFilePath(strTmpFilePath);
			tmpFilePaths.add(page);
			//anti-prohibit
			antiProhibitForHtml(random);
		}
		logger.info("Finish downloading html files.");
	}
	
	/**
	 * avoid website forbidding to parsing. 
	 * */
	private void antiProhibitForHtml(Random random) {
		try {
			long lInterval = random.nextInt(websiteConfig.getLoadHtmlIntervalOffset()) + websiteConfig.getLoadHtmlIntervalBase();
			logger.debug("The current thread will sleep {} milliseconds.", lInterval);
			Thread.sleep(lInterval);
		} 
		catch (InterruptedException e) {
			logger.error("Interrupted when anti-killing for downloading html. ", e);
		}
	}
	
	private void loadImageLinkList(List<NineSixxxNetPage> tmpFilePaths, List<String> imageLinks) throws IOException {
		logger.info("Start filling list of image link.");
		StringBuilder builder = new StringBuilder();
		File fileTmpHtmlPath = null;
		Document document = null;
		Elements elementsImg = null;
		for (NineSixxxNetPage page : tmpFilePaths) {
			fileTmpHtmlPath = new File(page.getTmpFilePath());
			document = Jsoup.parse(fileTmpHtmlPath, this.websiteConfig.getCharset());
			elementsImg = document.select(SELECTOR_IMG);
			for (Element elementImg : elementsImg) {
				imageLinks.add(WebUtils.getCompleteUrlLink(builder, elementImg.attr("src"), this.baseUrl, page.getCurrUrlLink()));
			}
		}
		logger.info("Finish filling list of image link.");
	}
	
	private void downloadImages(List<String> imageLinks, Map<String, String> headers, String picPath) {
		logger.info("Start downloading images.");
		int i = 0;
		StringBuilder builder = new StringBuilder();
		byte[] buffer = new byte[8192];
		File fileImg = null;
		String strImgPath = null;
		String strFixedPath = null;
		Random random = new Random(System.currentTimeMillis());
		for (String strImgLink : imageLinks) {
			// download it and than rename it. 
			++i;
			strImgPath = GalleryUtils.loadFileByURL(strImgLink, headers, buffer, picPath, builder);
			fileImg = new File(strImgPath);
			strFixedPath = String.format("%s%05d-%s", picPath, i, fileImg.getName());
			fileImg.renameTo(new File(strFixedPath));
			logger.info("{}/{} image files have been downloaded.", i, imageLinks.size());
			//anti-prohibit
			antiProhibitForPic(random);
		}
		logger.info("Finish downloading images.");
	}
	
	/**
	 * avoid website forbidding to parsing. 
	 * */
	private void antiProhibitForPic(Random random) {
		try {
			long lInterval = random.nextInt(websiteConfig.getLoadPicIntervalOffset()) + websiteConfig.getLoadPicIntervalBase();
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
	public void setGalleryWebsiteConfig(GalleryWebsiteConfig config) {
		this.config = config;
	}

	@Override
	public String getTitle() {
		return this.title;
	}
}
