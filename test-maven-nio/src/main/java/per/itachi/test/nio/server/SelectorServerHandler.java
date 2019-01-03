package per.itachi.test.nio.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import per.itachi.test.nio.ControllableRunnable;

/**
 * 多selector模式 http://blog.csdn.net/jjzhk/article/details/39553613<br/>
 * nio与io的区别 http://ifeve.com/java-nio-vs-io/<br/>
 * 一个服务端的例子 http://flym.iteye.com/blog/392350<br/>
 * */
public class SelectorServerHandler implements ControllableRunnable {
	
	private static final int LENGTH_SEGMENT = 1536;
	
	private final Logger logger = LoggerFactory.getLogger(SelectorServerHandler.class);
	
	private volatile boolean terminated = false;
	private boolean running = false;

	private Selector selector;
	
	@Override
	public void run() {
		terminated = false;
		running = true;
		
		ServerSocketChannel serverSocketChannel = null;
		ServerSocket serverSocket = null;
		SocketChannel socketChannel = null;
		
		Selector selector = null;
		Set<SelectionKey> selectionKeys = null;
		Iterator<SelectionKey> iteratorKey = null;
		SelectionKey key = null;
		SelectionKey keyTmp = null;
		int readyChannels = 0;
		try {
			logger.info("Starting server handler...");
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.configureBlocking(false);
			serverSocket = serverSocketChannel.socket();
			serverSocket.bind(new InetSocketAddress("127.0.0.1", 20001));
			
			selector = this.selector = Selector.open();
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
			
			logger.debug("The server socket channel is {}", System.identityHashCode(serverSocketChannel));
			while (!terminated) {
				readyChannels = selector.select();
				if (readyChannels == 0) {
					continue;
				}
				logger.debug("{} new keys comes in", readyChannels);
				selectionKeys = selector.selectedKeys();
				iteratorKey = selectionKeys.iterator();
				while (iteratorKey.hasNext()) {
					key = iteratorKey.next();
					iteratorKey.remove();
					if (key.isConnectable()) {
						logger.debug("The key {} with {} is connectable", key, key.channel());
					}
					if (key.isAcceptable()) {
						serverSocketChannel = (ServerSocketChannel)key.channel();//should be the same one as the previous server channel
						socketChannel = serverSocketChannel.accept();
						socketChannel.configureBlocking(false);
						keyTmp = socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
						logger.debug("The key {} with {} is acceptable, and channel key {}", key, System.identityHashCode(key.channel()), keyTmp);
					}
					if (key.isReadable()) {
						//如果注册了SelectionKey.OP_READ并且缓冲区中有未读取的数据，则每次循环都会进入这个isReadable分支；
						socketChannel = (SocketChannel)key.channel();
						ByteBuffer buffer = ByteBuffer.allocate(LENGTH_SEGMENT);
						socketChannel.read(buffer);
						logger.debug("The key {} with {} is readable: {}", key, key.channel(), buffer);
					}
					if (key.isWritable()) {
						//如果注册了SelectionKey.OP_WRITE并且没写过，则每次循环都会进入这个isWritable分支(?)
						socketChannel = (SocketChannel)key.channel();
						logger.debug("The key {} with {} is writable", key, key.channel());
					}
				}
				Thread.sleep(1000l);
			}
		} 
		catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		catch (RuntimeException e) {
			logger.error("Exception occurs at run-time.", e);
		}
		catch (Exception e) {
			logger.error("Exception occurs at run-time.", e);
		}
		finally {
			if (selector != null) {
				try {
					selector.close();
				} 
				catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
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
		if (this.selector != null) {
			selector.wakeup();
		}
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
