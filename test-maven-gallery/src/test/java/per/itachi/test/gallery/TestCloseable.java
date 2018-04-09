package per.itachi.test.gallery;

import java.io.Closeable;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestCloseable implements Closeable {
	
	private final Logger logger = LoggerFactory.getLogger(TestCloseable.class);
	
	private int id;
	
	public TestCloseable(int id) {
		this.id = id;
	}

	@Override
	public void close() throws IOException {
		logger.info("{}", id);
	}

}
