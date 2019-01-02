package per.itachi.test.nio.client;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntryClient {

	private static final Logger logger = LoggerFactory.getLogger(EntryClient.class);
	
	public static void main(String[] args) {
		try(SocketChannel channel = SocketChannel.open();) {
			
		} 
		catch (IOException e) {
			logger.error("Error occured when running channel. ", e);
		}
	}

}
