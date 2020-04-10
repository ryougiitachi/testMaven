package per.itachi.test.netty.server;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.NettyRuntime;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

@Component
public class NettyServer {
	
	private final Logger logger = LoggerFactory.getLogger(NettyServer.class);
	
	@Autowired
	private ChannelInitializer<SocketChannel> serverChildChannelInitialzer;
	
	@Autowired
	private GenericFutureListener<Future<Void>> serverFutureListener;
	
	@Autowired
	private GenericFutureListener<Future<Void>> serverCloseFutureListener;
	
	@Autowired
	private GenericFutureListener<Future<Object>> eventLoopGroupFutureListener;
	
	private int port = 9091;
	
	private EventLoopGroup loopgrpBoss;
	
	private EventLoopGroup loopgrpWorker;
	
	@PostConstruct
	private void init() {
		logger.info("Starting up netty server using port {}... ", port);
		loopgrpBoss = new NioEventLoopGroup();
		loopgrpWorker = new NioEventLoopGroup();
		logger.info("loopgrpBoss={}, loopgrpWorker={}", loopgrpBoss, loopgrpWorker);
		ServerBootstrap serverBootstrap = new ServerBootstrap();
		serverBootstrap.group(loopgrpBoss, loopgrpWorker)
				.channel(NioServerSocketChannel.class)
//				.option(option, value)//set boss EventLoop
//				.childOption(option, value)//set worker EventLoop, and set client itself as for client. 
				.childHandler(serverChildChannelInitialzer);//子handler是初始化必须设置的，否则绑定端口时抛异常提示childHandler not set；
		logger.info("Binding port {}...", port);
		//这种异步形式，如果绑定期间发生异常可能程序不会因此停止，依然正常启动；
		serverBootstrap.bind(port).addListener(serverFutureListener)
				.channel().closeFuture().addListener(serverCloseFutureListener);
		logger.info("Netty server started up, with {} threads. ", NettyRuntime.availableProcessors() * 2);
	}
	
	@PreDestroy
	private void destroy() {
		loopgrpBoss.shutdownGracefully().addListener(eventLoopGroupFutureListener);
		loopgrpWorker.shutdownGracefully().addListener(eventLoopGroupFutureListener);
	}
}
