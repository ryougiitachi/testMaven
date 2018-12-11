package per.itachi.test.netty.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class EntryTest {
	
	private static final Logger logger = LoggerFactory.getLogger(EntryTest.class);

	public static void main(String[] args) {
		EventLoopGroup bossGrp = new NioEventLoopGroup();
		EventLoopGroup workerGrp = new NioEventLoopGroup();
		ServerBootstrap server = new ServerBootstrap();
		server.group(bossGrp, workerGrp);
		server.channel(NioServerSocketChannel.class);
		server.childHandler(new ChannelInitializer<SocketChannel>() {
			@Override  
			public void initChannel(SocketChannel ch) throws Exception {
			}
		});
		server.option(ChannelOption.SO_BACKLOG, 128);
		server.childOption(ChannelOption.SO_KEEPALIVE, true);
		ChannelFuture f = null;
		try {
			f = server.bind(10000).sync();
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
}
