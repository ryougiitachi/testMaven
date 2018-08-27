package per.itachi.test.commons.crypto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EntryTestCrypto {

	public static void main(String[] args) {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD2");
		} 
		catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
