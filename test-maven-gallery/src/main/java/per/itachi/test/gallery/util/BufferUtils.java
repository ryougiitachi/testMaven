package per.itachi.test.gallery.util;

import per.itachi.test.gallery.GalleryConstants;

public class BufferUtils {
	
	private static ThreadLocal<byte[]> threadLocalBytes = new ThreadLocal<>();
	
	private static ThreadLocal<StringBuilder> threadLocalBuilder = new ThreadLocal<>();
	
	public static byte[] getLocalBufferBytes() {
		byte[] buffer = threadLocalBytes.get();
		if (buffer == null) {
			buffer = new byte[GalleryConstants.DEFAULT_BUFFER_BYTE_SIZE];
		}
		return buffer;
	}
	
	public static StringBuilder getLocalStringBuilder() {
		StringBuilder builder = threadLocalBuilder.get();
		if (builder == null) {
			builder = new StringBuilder(512);
		}
		return builder;
	}
}
