package per.itachi.test.nio.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import per.itachi.test.nio.ControllableRunnable;

public class ServerRunnable implements ControllableRunnable {
	
	private final Logger logger = LoggerFactory.getLogger(ServerRunnable.class);
	
	private volatile boolean terminated = false;
	private boolean running = false;

	@Override
	public void run() {
		terminated = false;
		running = true;
		
		ServerSocketChannel serverSocketChannel = null;
		ServerSocket serverSocket = null;
		SocketChannel socketChannel = null;
		try {
			serverSocketChannel = ServerSocketChannel.open();
			serverSocket = serverSocketChannel.socket();
			serverSocket.bind(new InetSocketAddress("127.0.0.1", 20001));
			while (!terminated) {
				socketChannel = serverSocketChannel.accept();
			}
		} 
		catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		finally {
			if (serverSocketChannel != null) {
				try {
					serverSocketChannel.close();
				} 
				catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
			running = false;
		}
	}

	@Override
	public void tryToTerminate() {
		terminated = true;
	}

	@Override
	public boolean isTerminated() {
		return terminated;
	}

	@Override
	public boolean isRunning() {
		return running;
	}

}
