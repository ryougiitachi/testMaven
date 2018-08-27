package per.itachi.test.commons.crypto;

public interface Crypto {
	
	byte[] digest();
	
	byte[] digest(byte[] data);
	
	byte[] digest(byte[] data, int offset, int len);
	
	void update(byte[] data);
	
	void update(byte[] data, int offset, int len);
	
	void reset();
	
	String getHexStringDigest();
	
	String toString();
}
