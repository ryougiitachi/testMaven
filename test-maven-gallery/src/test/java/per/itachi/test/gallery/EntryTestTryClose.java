package per.itachi.test.gallery;

import java.io.Closeable;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	}
}
