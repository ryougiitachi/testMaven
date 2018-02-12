package per.itachi.test.gallery.parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import per.itachi.test.gallery.util.GalleryUtils;

public class NeteaseParser implements Parser {
	
	private static final String SELECTOR_NEXT_PAGE = "";
	private static final String SELECTOR_IMG = "";
	
	private final Logger logger = LoggerFactory.getLogger(NeteaseParser.class);
	
	private String urlLink;
	
	private String baseUrl;
	
	public NeteaseParser(String urlLink) {
		this.urlLink = urlLink;
		this.baseUrl = GalleryUtils.getBaseUrl(urlLink);
	}
	
	@Override
	public void execute() {
		String strUrlLink = this.urlLink;
		Map<String, String> mapHeaders = GalleryUtils.getDefaultRequestHeaders();
		String strTmpHtmlPath = GalleryUtils.loadHtmlByURL(strUrlLink, mapHeaders);
		List<String> listTmpFilePath = new ArrayList<>();
		listTmpFilePath.add(strTmpHtmlPath);
		try {
			loadTmpHtmlList(strTmpHtmlPath, listTmpFilePath);
		} 
		catch (IOException e) {
			logger.debug(e.getMessage(), e);
		}
	}
	
	private void loadTmpHtmlList(String initTmpHtmlPath, List<String> tmpFilePaths) throws IOException {
		StringBuilder builder = new StringBuilder();
		Elements elementsNextPage = null;
		Element elementNextPage = null;
		String strNextPageLink = null;
		String strCurrUrl = this.urlLink;
		File fileTmpHtmlPath = new File(initTmpHtmlPath);
		Document document = Jsoup.parse(fileTmpHtmlPath, "UTF-8");
		for(elementsNextPage = document.select(SELECTOR_NEXT_PAGE); elementsNextPage.size() > 0;
				elementsNextPage = document.select(SELECTOR_NEXT_PAGE)) {
			elementNextPage = elementsNextPage.first();
			strNextPageLink = GalleryUtils.getCompleteUrlLink(builder, elementNextPage.attr("href"), this.baseUrl, strCurrUrl);
		}
	}

	@Override
	public String getTitle() {
		return null;
	}
}
