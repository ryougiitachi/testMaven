package per.itachi.test.gallery.persist;

import java.io.File;
import java.io.IOException;

import com.healthmarketscience.jackcess.CryptCodecProvider;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;

import net.ucanaccess.jdbc.JackcessOpenerInterface;

public class GalleryJackcessOpener implements JackcessOpenerInterface {

	@Override
	public Database open(File fl, String pwd) throws IOException {
		DatabaseBuilder builder = new DatabaseBuilder(fl);
		builder.setAutoSync(false);
		builder.setCodecProvider(new CryptCodecProvider(pwd));
		builder.setReadOnly(false);
		return builder.open();
	}
}
