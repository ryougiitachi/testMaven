package per.itachi.test.nio.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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
	
	private static final int MAX_CONN = 100;
	
	private final Logger logger = LoggerFactory.getLogger(SelectorServerHandler.class);
	
	private volatile boolean terminated = false;
	private volatile boolean running = false;

	private Selector selector;
	
	private ConcurrentMap<SocketAddress, SocketChannel> clients;
	
	@Override
	public void run() {
		terminated = false;
		running = true;
		
		ServerSocketChannel serverSocketChannel = null;
		ServerSocket serverSocket = null;
		
		Selector selector = null;
		Set<SelectionKey> selectionKeys = null;
		Iterator<SelectionKey> iteratorKey = null;
		SelectionKey key = null;
		int readyChannels = 0;
		
		int thresholdTimeWrite = 12;//TODO: testing
		int countTimeWrite = 0;//TODO: testing 
		try {
			logger.info("Starting server handler...");
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.configureBlocking(false);
			serverSocket = serverSocketChannel.socket();
			serverSocket.bind(new InetSocketAddress("127.0.0.1", 20001));
			
			selector = this.selector = Selector.open();
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
			
			logger.debug("The server socket channel is {}", System.identityHashCode(serverSocketChannel));
			
			clients = new ConcurrentHashMap<>();
			
			while (!terminated) {
				readyChannels = selector.select();
				if (readyChannels == 0) {
					continue;
				}
				logger.debug("{} new keys comes in. ", readyChannels);
				selectionKeys = selector.selectedKeys();
				iteratorKey = selectionKeys.iterator();
				while (iteratorKey.hasNext()) {
					key = iteratorKey.next();
					iteratorKey.remove();
					//key.isConnectable is available only when program runs as client. 
					if (key.isConnectable()) {
						logger.debug("The key {} with {} is connectable", key, key.channel());
					}
					if (key.isAcceptable()) {
						handleAcceptable(key);
					}
					//如果远程客户端突然断开连接，这边是检测不到对方已断开连接了的；这时SelectionKey.isValid()依然为true，并且SelectionKey.isReadable()将变为true；
					//再次调用SocketChannel.read()抛出异常后才会把valid标志位置为false；
					//这是一下这段代码可能会报错的原因，即key.isReadable()不报错并可以进入分支，而key.isWritable()却抛异常的原因；
					//好像正常情况下SelectionKey.isWritable()一直是true的，SelectionKey.isReadable()则是在客户端返回的情况下是true，这就容易造成如下情况：
					//readable在有返回的时候可用，而writable一直是可用的，那么writable可以一直写，如果client是写收写收的模式，有可能会导致不是所有的数据包都能收到；
					//这篇文章有一些关于Selector的个人理解，写的比较浅显易懂https://blog.csdn.net/billluffy/article/details/78036998
					if (key.isReadable()) {
						//如果注册了SelectionKey.OP_READ并且缓冲区中有未读取的数据，则每次循环都会进入这个isReadable分支；
						handleReadable(key);
					}
//					logger.debug("{}", key.channel());//测试异常点；
					if (key.isWritable()) {
						//如果注册了SelectionKey.OP_WRITE并且没写过，则每次循环都会进入这个isWritable分支(?)
						if (countTimeWrite <= thresholdTimeWrite) { // TODO: testing
							handleWritable(key);
							++countTimeWrite;
						}
						else {
							if ((key.interestOps() & SelectionKey.OP_WRITE) > 0) {
								key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
							}
						}
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
	
	private void handleAcceptable(SelectionKey key) {
		ServerSocketChannel serverSocketChannel = (ServerSocketChannel)key.channel();//should be the same one as the previous server channel
		try {
			SocketChannel socketChannel = serverSocketChannel.accept();
			socketChannel.configureBlocking(false);
			SocketAddress clientAddress = socketChannel.getRemoteAddress();
			SelectionKey keyTmp = socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
			if (clients.size() >= MAX_CONN) {
				
			}
			else if (clients.get(clientAddress) != null) {
				
			}
			else if (clients.get(clientAddress) == null) {
				clients.put(clientAddress, socketChannel);
			}
			else {
				// TODO: 
			}
			logger.debug("The key {} with {} is acceptable, and channel key {}", key, System.identityHashCode(key.channel()), keyTmp);
		} 
		catch (IOException e) {
			logger.error("Exception occurs when trying to accept new client. ", e);
			handleAcceptableException(serverSocketChannel, e);
		}
	}
	
	private void handleAcceptableException(ServerSocketChannel serverSocketChannel, IOException e) {
		
	}
	
	private void handleReadable(SelectionKey key) {
		SocketChannel socketChannel = null;
		try {
			socketChannel = (SocketChannel)key.channel();
			ByteBuffer buffer = ByteBuffer.allocate(LENGTH_SEGMENT);
			socketChannel.read(buffer);
			logger.debug("The key {} with {} is readable: {}", key, key.channel(), buffer);
		} 
		catch (IOException e) {
			logger.error("Exception occured when reading data from {} using key {}. ", socketChannel, key, e);
			handleReadableException(socketChannel, e);
		}
		catch (RuntimeException e) {
			logger.error("Other exception occured when reading data from {} using key {}. ", socketChannel, key, e);
		}
	}
	
	private void handleReadableException(SocketChannel socketChannel, Exception e) {
		try {
			if (socketChannel != null) {
				socketChannel.close();
			}
		} 
		catch (IOException ioe) {
			logger.error("Exception occured when trying to close channel for .", e);
		}
	}
	
	private void handleWritable(SelectionKey key) {
		SocketChannel socketChannel = null;
		try {
//			if (key.isValid()) {
				socketChannel = (SocketChannel)key.channel();
				ByteBuffer buffer = ByteBuffer.allocate(LENGTH_SEGMENT);
				buffer.putLong(111l);
				int countBytesWrite = socketChannel.write(buffer);
				logger.debug("The key {} with {} is writable: {}, {}", key, key.channel(), countBytesWrite, buffer);
//			}
		} 
		catch (IOException e) {
			logger.error("Exception occured when writing data into {} using key {}. ", socketChannel, key, e);
		}
		catch (RuntimeException e) {
			logger.error("Other exception occured when writing data into {} using key {}. ", socketChannel, key, e);
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
