package per.itachi.test.gallery.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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
import per.itachi.test.gallery.entity.NineSixxxNetPage;
import per.itachi.test.gallery.util.GalleryUtils;

public class NineSixxxNetParser implements Parser {
	
	private static final String WEBSITE_CHARSET = "GBK";
	
	private static final String SELECTOR_TITLE = 
			"section.container div.content-wrap div.content header.article-header h1.article-title";
	private static final String SELECTOR_INTRODUCTION = 
			"section.container div.content-wrap div.content article.article-content p";
	private static final String SELECTOR_NEXT_PAGE = 
			"section.container div.content-wrap div.content div.pagination.pagination-multi ul li.next-page a";
	private static final String SELECTOR_IMG = 
			"section.container div.content-wrap div.content article.article-content p>img";
	
	private final Logger logger = LoggerFactory.getLogger(NineSixxxNetParser.class);
	
	private String urlLink;
	
	private String baseUrl;
	
	private String title;
	
	public NineSixxxNetParser(String urlLink) {
		this.urlLink = urlLink;
		this.baseUrl = GalleryUtils.getBaseUrl(urlLink);
	}

	@Override
	public void execute() {
		String strUrlLink = this.urlLink;
		logger.info("Start parsing {}", strUrlLink);
		Map<String, String> mapHeaders = GalleryUtils.getDefaultRequestHeaders();
		String strTmpHtmlPath = GalleryUtils.loadHtmlByURL(strUrlLink, mapHeaders);
		List<NineSixxxNetPage> listTmpFilePath = new ArrayList<>();
		List<String> listImageLink = new ArrayList<>();
		String strPicDirPath = null;
		NineSixxxNetPage page = null;
		try {
			page = new NineSixxxNetPage();
			page.setCurrUrlLink(strUrlLink);
			page.setTmpFilePath(strTmpHtmlPath);
			listTmpFilePath.add(page);
			strPicDirPath = generatePicDirectory(strTmpHtmlPath);
			if (strPicDirPath != null) {
				loadTmpHtmlList(strTmpHtmlPath, listTmpFilePath, mapHeaders);// download html
				loadImageLinkList(listTmpFilePath, listImageLink);// load image links
				downloadImages(listImageLink, mapHeaders, strPicDirPath);//download imagaes
			}
		} 
		catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		logger.info("Finish parsing {}", strUrlLink);
	}
	
	private String generatePicDirectory(String tmpHtmlPath) throws IOException {
		StringBuilder builder = new StringBuilder();
		File fileTmpHtmlPath = new File(tmpHtmlPath);
		Document document = Jsoup.parse(fileTmpHtmlPath, WEBSITE_CHARSET);
		Element elementTitle = document.selectFirst(SELECTOR_TITLE);
		String strTitle = this.title = elementTitle.text();
		String strPicDirPath = GalleryUtils.joinStrings(builder, 
				GalleryConstants.DEFAULT_PICTURE_PATH, File.separator, 
				strTitle, File.separator);
		File filePicDirPath = new File(strPicDirPath);
		if (filePicDirPath.exists()) {
			logger.info("{} has been downloaded before", this.urlLink);
			return null;
		}
		filePicDirPath.mkdir();
		Elements elementsArticle = document.select(SELECTOR_INTRODUCTION);
		String strIntroFilePath = GalleryUtils.joinStrings(builder, strPicDirPath, "readme.txt");
		File fileIntro = new File(strIntroFilePath);
		try(PrintWriter pw = new PrintWriter(fileIntro)) {
			pw.println(this.urlLink);
			pw.println();
			for (Element elementArticle : elementsArticle) {
				if (elementArticle.hasText()) {
					pw.println(elementArticle.text());
				}
			}
		} 
		catch (FileNotFoundException e) {
			logger.error(e.getMessage(), e);
		}
		return strPicDirPath;
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
		long lInterval = 0;
		File fileTmpHtmlPath = new File(strTmpFilePath);
		Document document = Jsoup.parse(fileTmpHtmlPath, WEBSITE_CHARSET);
		for(elementsNextPage = document.select(SELECTOR_NEXT_PAGE); 
				elementsNextPage.size() > 0;
				elementsNextPage = document.select(SELECTOR_NEXT_PAGE)) {
			elementNextPage = elementsNextPage.first();
			strNextLink = GalleryUtils.getCompleteUrlLink(builder, elementNextPage.attr("href"), this.baseUrl, strCurrUrl);
			strTmpFilePath = GalleryUtils.loadHtmlByURL(strNextLink, headers);
			fileTmpHtmlPath = new File(strTmpFilePath);
			document = Jsoup.parse(fileTmpHtmlPath, "UTF-8");
			strCurrUrl = strNextLink;
			page = new NineSixxxNetPage();
			page.setCurrUrlLink(strCurrUrl);
			page.setTmpFilePath(strTmpFilePath);
			tmpFilePaths.add(page);
			try {
				lInterval = random.nextInt(500) + 1000;
				logger.info("The current thread will sleep {} milliseconds.", lInterval);
				Thread.sleep(lInterval);
			} 
			catch (InterruptedException e) {
				logger.error(e.getMessage(), e);
			}
		}
		logger.info("Finish downloading html files.");
	}
	
	private void loadImageLinkList(List<NineSixxxNetPage> tmpFilePaths, List<String> imageLinks) throws IOException {
		logger.info("Start filling list of image link.");
		StringBuilder builder = new StringBuilder();
		File fileTmpHtmlPath = null;
		Document document = null;
		Elements elementsImg = null;
		for (NineSixxxNetPage page : tmpFilePaths) {
			fileTmpHtmlPath = new File(page.getTmpFilePath());
			document = Jsoup.parse(fileTmpHtmlPath, WEBSITE_CHARSET);
			elementsImg = document.select(SELECTOR_IMG);
			for (Element elementImg : elementsImg) {
				imageLinks.add(GalleryUtils.getCompleteUrlLink(builder, elementImg.attr("src"), this.baseUrl, page.getCurrUrlLink()));
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
		long lInterval = 0;
		for (String strImgLink : imageLinks) {
			++i;
			strImgPath = GalleryUtils.loadFileByURL(strImgLink, headers, buffer, picPath, builder);
			fileImg = new File(strImgPath);
			strFixedPath = String.format("%s%05d-%s", picPath, i, fileImg.getName());
			fileImg.renameTo(new File(strFixedPath));
			logger.info("{}/{} image files have been downloaded.", i, imageLinks.size());
			try {
				lInterval = random.nextInt(350) + 300;
				logger.info("The current thread will sleep {} milliseconds.", lInterval);
				Thread.sleep(lInterval);
			} 
			catch (InterruptedException e) {
				logger.error(e.getMessage(), e);
			}
		}
		logger.info("Finish downloading images.");
	}

	@Override
	public String getTitle() {
		return this.title;
	}
}
