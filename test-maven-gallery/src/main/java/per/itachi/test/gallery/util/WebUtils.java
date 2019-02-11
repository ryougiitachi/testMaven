package per.itachi.test.gallery.util;

import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebUtils {
	
	public static final String REGEX_WEBSITE_BASE_URL = "(https?://[\\w-]+(\\.[\\w-]+)*(:\\d+)?)/?";
	
	public static final String REGEX_WEBSITE_LINK = "(https?://[\\w-]+(\\.[\\w-]+)*(:\\d+)?)(/[\\w-\\.]+)*";
	
	public static String getBaseUrl(String urlPath) {
		Pattern pattern = Pattern.compile(REGEX_WEBSITE_LINK);
		Matcher matcher = pattern.matcher(urlPath);
		String strBaseUrl = null;
		if (matcher.find()) {
			strBaseUrl = matcher.group(1);
		}
		return strBaseUrl;
	}
	
	public static boolean isCompleteUrlLink(String urlPath) {
		return Pattern.matches(REGEX_WEBSITE_LINK, urlPath);
	}
	
	public static String getCompleteUrlLink(StringBuilder builder, String urlPath, String baseUrl, String currUrl) {
		if (isCompleteUrlLink(urlPath)) {
			return urlPath;
		} 
		else if (urlPath.startsWith("/")) {
			return GalleryUtils.joinStrings(builder, baseUrl, urlPath);
		}
		else {
			String strCurrPath = currUrl.substring(0, currUrl.lastIndexOf("/") + 1);
			return GalleryUtils.joinStrings(builder, strCurrPath, urlPath);
		}
	}
	
	public static String getCompleteUrlLink(String urlPath, String baseUrl, String currUrl) {
		if (isCompleteUrlLink(urlPath)) {
			return urlPath;
		} 
		else if (urlPath.startsWith("/")) {
			return MessageFormat.format("{0}{1}", baseUrl, urlPath);
		}
		else {
			String strCurrPath = currUrl.substring(0, currUrl.lastIndexOf("/") + 1);
			return MessageFormat.format("{0}{1}", strCurrPath, urlPath);
		}
	}
}
