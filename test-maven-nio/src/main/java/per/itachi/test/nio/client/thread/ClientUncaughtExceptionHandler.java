package per.itachi.test.nio.client.thread;

import java.lang.Thread.UncaughtExceptionHandler;
import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientUncaughtExceptionHandler implements UncaughtExceptionHandler {
	
	private final Logger log = LoggerFactory.getLogger(ClientUncaughtExceptionHandler.class);

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		log.error(MessageFormat.format("Error occurs in {0}.", t.getName()), e);
	}

}
