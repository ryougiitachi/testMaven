package per.itachi.test.commons.crypto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageDigestLocal {
	
	private final Logger logger = LoggerFactory.getLogger(MessageDigestLocal.class);
	
	private ThreadLocal<MessageDigest> thdLocalDigest;
	
	private ThreadLocal<StringBuilder> thdLocalStringBuilder;
	
	private String algorithm;
	
	public MessageDigestLocal(String algorithm) throws NoSuchAlgorithmException {
		this.thdLocalDigest = new ThreadLocal<>();
		this.thdLocalStringBuilder = new ThreadLocal<>();
		this.algorithm = algorithm;
		validate();
	} 
	
	private void validate() throws NoSuchAlgorithmException {
		try {
			MessageDigest digest = MessageDigest.getInstance(algorithm);
			thdLocalDigest.set(digest);
		} 
		catch (NoSuchAlgorithmException e) {
			logger.error("Error occurs when validating algorithm {}. ", algorithm, e);
		}
	}
	
	public void update(byte[] data) {
		MessageDigest digest = getLocalMessageDigest();
		if (digest == null) {
			return;
		}
		digest.update(data);
	}
	
	public void update(byte[] data, int offset, int len) {
		MessageDigest digest = getLocalMessageDigest();
		if (digest == null) {
			return;
		}
		digest.update(data, offset, len);
	}
	
	public byte[] digest() {
		MessageDigest digest = getLocalMessageDigest();
		if (digest == null) {
			return null;
		}
		return digest.digest();
	}
	
	public byte[] digest(byte[] data) {
		MessageDigest digest = getLocalMessageDigest();
		if (digest == null) {
			return null;
		}
		return digest.digest(data);
	}
	
	public String getHexStringDigest() {
		MessageDigest digest = getLocalMessageDigest();
		if (digest == null) {
			return null;
		}
		byte[] bytesDataEncrypt = digest.digest();
		StringBuilder builder = getLocalStringBuilder();
		for (byte b : bytesDataEncrypt) {
			builder.append(String.format("%02X", b));
		}
		return builder.toString();
	}
	
	public void reset() {
		MessageDigest digest = getLocalMessageDigest();
		if (digest == null) {
			return;
		}
		digest.reset();
	}
	
	public String getAlgorithmName() {
		return algorithm;
	}
	
	private MessageDigest getLocalMessageDigest() {
		MessageDigest digest = thdLocalDigest.get();
		if (digest == null) {
			try {
				digest = MessageDigest.getInstance(algorithm);
			} 
			catch (NoSuchAlgorithmException e) {
				logger.error("Error occurs when initialising {} MessageDigest in thread {}", algorithm, Thread.currentThread().getName(), e);
			}
		}
		return digest;
	}
	
	private StringBuilder getLocalStringBuilder() {
		StringBuilder builder = thdLocalStringBuilder.get();
		if (builder == null) {
			builder = new StringBuilder();
		}
		builder.setLength(0);
		return builder;
	}
	
	public String getHexStringFromBinary(byte[] data) {
		if (data == null) {
			return null;
		}
		StringBuilder builder = new StringBuilder();
		for (byte b : data) {
			builder.append(String.format("%02X", b));
		}
		return builder.toString();
	}
}
