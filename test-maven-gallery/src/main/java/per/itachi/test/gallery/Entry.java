package per.itachi.test.gallery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import per.itachi.test.gallery.parser.NineSixxxNetParser;
import per.itachi.test.gallery.parser.Parser;

public class Entry {
	
	private static final Logger logger = LoggerFactory.getLogger(Entry.class);

	public static void main(String[] args) {
		if (args.length <= 0) {
			return;
		}
		Parser nineSixxxNet = new NineSixxxNetParser(args[0]);
		nineSixxxNet.execute();
	}

}
