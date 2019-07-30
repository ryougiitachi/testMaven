package per.itachi.test.jasypt;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.EnvironmentStringPBEConfig;

public class EntryJasypt {

	public static void main(String[] args) {
		if (args.length <= 1) {
			return;
		}
		EnvironmentStringPBEConfig config = new EnvironmentStringPBEConfig();
		config.setAlgorithm("PBEWithMD5AndDES");
		config.setPassword("itachi");
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setConfig(config);
		String strDecryption = encryptor.decrypt(args[0]);
		String strEncryption = encryptor.encrypt(args[1]);
		System.out.println(strDecryption);
		System.out.println(strEncryption);
		int charN = '\n';
		int charR = '\r';
		System.out.printf("%04X %04X\n", charN, charR);
	}

}
