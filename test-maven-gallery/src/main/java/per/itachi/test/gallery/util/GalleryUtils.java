package per.itachi.test.gallery.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GalleryUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(GalleryUtils.class);
	
	public static final String EMPTY_STRING = "";
	
	public static String joinStrings(StringBuilder builder, Object... args) {
		if (builder == null || args == null) {
			return EMPTY_STRING;
		}
		String strResult;
		for (Object item : args) {
			builder.append(item);
		}
		strResult = builder.toString();
		builder.setLength(0);
		return strResult;
	}
	
	public static void testFinal() {
		final Object object = null;//if object is modified as static, it says, "only final is permitted".
		if (object == null) {
//			object = new Object();//if object is modified as final, error occurs.
		}
		logger.debug("{}", object);
	}
}
