package per.itachi.test.nio.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntryServer {

	private static final Logger logger = LoggerFactory.getLogger(EntryServer.class);

	public static void main(String[] args) {
		try(SocketChannel channel = SocketChannel.open();) {
			SocketAddress socketAddress = new InetSocketAddress("127.0.0.1", 8083);
			channel.bind(socketAddress);
		} 
		catch (IOException e) {
			logger.error("Error occured when running channel. ", e);
		}
	}

}
