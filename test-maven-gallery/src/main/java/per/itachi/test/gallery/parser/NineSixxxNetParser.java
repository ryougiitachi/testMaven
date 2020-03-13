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
		List<NineSixxxNetPage> listPageInfo = new ArrayList<>();
		String strPicDirPath = null;
		try {
			loadPageInfoList(strUrlLink, listPageInfo, mapHeaders);// download html files 
			strPicDirPath = generatePicDirectory(listPageInfo.get(0).getTmpFilePath());//Given list of html is not null. 
			if (strPicDirPath != null) {
//				loadImageLinkList(listTmpHtmlPath, listImageLink);// load image links
				downloadImages(listPageInfo, mapHeaders, strPicDirPath);//download imagaes
			}
		} 
		catch (IOException e) {
			logger.error("Error occured when downloading pictures from {}. ", strUrlLink, e);
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
		logger.info("The title is {}. ", strTitle);
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
	
	private void loadPageInfoList(String urlLink, List<NineSixxxNetPage> htmlFilePaths, Map<String, String> headers) throws IOException {
		logger.info("Start downloading html files from {}.", urlLink);
		StringBuilder builder = new StringBuilder();
		int countHtml = 0;
		Document document = null;
		String strCurrUrlLink = urlLink;
		String strCurrHtmlPath = null;
		List<String> listImageCompleteUrlPerPage = null;
		NineSixxxNetPage pageInfo = null;
		File fileHtml = null;
		Elements elementsImg = null;
		Elements elementsNextPage = null;
		
		Random random = new Random(System.currentTimeMillis());
		do {
			strCurrHtmlPath = GalleryUtils.loadHtmlByURL(strCurrUrlLink, headers);
			fileHtml = new File(strCurrHtmlPath);
			document = Jsoup.parse(fileHtml, this.websiteConfig.getCharset());//GBK
			// load image url 
			listImageCompleteUrlPerPage = new ArrayList<>();
			elementsImg = document.select(SELECTOR_IMG);
			for (Element elementImg : elementsImg) {
				listImageCompleteUrlPerPage.add(WebUtils.getCompleteUrlLink(builder, elementImg.attr(GalleryConstants.HTML_ATTR_IMG_SRC), this.baseUrl, strCurrHtmlPath));
			}
			//put page info into NineSixxxNetPage 
			pageInfo = new NineSixxxNetPage();
			pageInfo.setCurrUrlLink(strCurrUrlLink);
			pageInfo.setTmpFilePath(strCurrHtmlPath);
			pageInfo.setImageCompleteUrlList(listImageCompleteUrlPerPage);
			htmlFilePaths.add(pageInfo);
			++countHtml;
			logger.info("Downloaded html file {}", fileHtml.getName());
			//for next page
			elementsNextPage = document.select(SELECTOR_NEXT_PAGE);
			if (!elementsNextPage.isEmpty()) {
				//for next page
				strCurrUrlLink = WebUtils.getCompleteUrlLink(builder, elementsNextPage.first().attr(GalleryConstants.HTML_ATTR_A_HREF), this.baseUrl, strCurrUrlLink);
			}
			//anti-prohibit
			antiProhibitForHtml(random);
		} 
		while (!elementsNextPage.isEmpty());//redundant
		logger.info("Finish downloading {} html files from {}.", countHtml, urlLink);
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
	
	private void downloadImages(List<NineSixxxNetPage> pageInfos, Map<String, String> headers, String picPath) {
		logger.info("Start downloading images.");
		StringBuilder builder = new StringBuilder();
		List<String> listImageCompleteUrl = new ArrayList<>(100);
		for (NineSixxxNetPage page : pageInfos) {
			for (String strImageCompleteUrl : page.getImageCompleteUrlList()) {
				listImageCompleteUrl.add(strImageCompleteUrl);
			}
		}
		int i = 0;
		byte[] buffer = new byte[GalleryConstants.DEFAULT_BUFFER_BYTE_SIZE];
		Random random = new Random(System.currentTimeMillis());
		File fileImg = null;
		String strImgPath = null;
		String strFixedPath = null;
		for (String strImgLink : listImageCompleteUrl) {
			// download it and than rename it. 
			++i;
			strImgPath = GalleryUtils.loadFileByURL(strImgLink, headers, buffer, picPath, builder);
			fileImg = new File(strImgPath);
			strFixedPath = String.format("%s%05d-%s", picPath, i, fileImg.getName());
			fileImg.renameTo(new File(strFixedPath));
			logger.info("{}/{} image files have been downloaded.", i, listImageCompleteUrl.size());
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
