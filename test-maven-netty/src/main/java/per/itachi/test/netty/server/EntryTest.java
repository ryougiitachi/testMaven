package per.itachi.test.netty.server;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class EntryTest {
	
	private static final Logger logger = LoggerFactory.getLogger(EntryTest.class);

	public static void main(String[] args) {
		logger.info("{}", Runtime.getRuntime().availableProcessors());
		
		if (args.length <= 1) {
			return;
		}
		
		SocketAddress bindSocketAddress = null;
		try {
			String strIP = args[0];
			String strPort = args[1];
			int port = Integer.parseInt(strPort);
			bindSocketAddress = getBindSocketAddress(strIP, port);
			logger.info("{}", bindSocketAddress);
		} 
		catch (UnknownHostException e) {
			logger.error(e.getMessage(), e);
			return;
		}
		catch (RuntimeException e) {
			logger.error(e.getMessage(), e);
			return;
		} 
		
		EventLoopGroup bossGrp = new NioEventLoopGroup();
		EventLoopGroup workerGrp = new NioEventLoopGroup();
		ServerBootstrap server = new ServerBootstrap();
		server.group(bossGrp, workerGrp);
		server.channel(NioServerSocketChannel.class);
		server.childHandler(new TestServerChannelInitializer());
		server.option(ChannelOption.SO_BACKLOG, 128);
		server.childOption(ChannelOption.SO_KEEPALIVE, true);
		ChannelFuture f = null;
		try {
			f = server.bind(bindSocketAddress).sync();
		} 
		catch (InterruptedException e) {
			logger.error("", e);
		}
		finally {
			try {
				if (f != null) {
					f.channel().closeFuture().sync();
				}
			} 
			catch (InterruptedException e) {
				logger.error("", e);
			}
			workerGrp.shutdownGracefully();
			bossGrp.shutdownGracefully();
		}
	}
	
	private static SocketAddress getBindSocketAddress(String ip, int port) throws UnknownHostException {
		InetAddress ipAddress = InetAddress.getByName(ip);
		SocketAddress socketAddress = new InetSocketAddress(ipAddress, port);
		return socketAddress;
	}
}
