package per.itachi.test.nio.server;

import per.itachi.test.nio.ControllableRunnable;

/**
 * -Dlogback.configurationFile=classpath:logback-server.xml
 * */
public class Entry {

	public static void main(String[] args) {
		ControllableRunnable serverHandler = new SelectorServerHandler();
		Thread thdServer = new Thread(serverHandler, "Server Selector Thread");
		thdServer.start();
	}
}
