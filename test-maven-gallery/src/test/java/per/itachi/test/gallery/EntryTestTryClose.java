package per.itachi.test.gallery;

import java.io.Closeable;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import per.itachi.test.gallery.util.WebUtils;

public class EntryTestTryClose {
	
	private static final Logger logger = LoggerFactory.getLogger(EntryTestTryClose.class);

	/**
	 * 17:05:52.442 [main] INFO per.itachi.test.gallery.TestCloseable - 2
	 * 17:05:52.458 [main] INFO per.itachi.test.gallery.TestCloseable - 1
	 * 17:05:52.458 [main] INFO per.itachi.test.gallery.EntryTestTryClose - finally
	 * */
	public static void main(String[] args) {
		try(Closeable closeable1 = new TestCloseable(1);
				Closeable closeable2 = new TestCloseable(2);) {
//			closeable2 = new TestCloseable(2);
//			Closeable closeable3 = new TestCloseable(3);//closeable3 will be not used
		} 
		catch (IOException e) {
			logger.error("", e);
		}
		finally {
			logger.info("finally");
		}
		
		Pattern pattern = Pattern.compile(WebUtils.REGEX_URL_FILENAME);
		Matcher matcher = pattern.matcher("https://ss3.baidu.com/9fo3dSag_xI4khGko9WTAnF6hhy/baike/wh%3D1000%2C1000/sign=bfeef43909087bf47db95fe8c2e36700/d058ccbf6c81800abba3427cba3533fa828b477a.jpg");
		while (matcher.find()) {
			logger.info("{}", matcher.group());
		}
	}
}
