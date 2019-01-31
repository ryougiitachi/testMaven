package per.itachi.test.netty.server;

import java.net.SocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

public class TestServerChannelInitializer extends ChannelInitializer<SocketChannel> {
	
	private final Logger logger = LoggerFactory.getLogger(TestServerChannelInitializer.class);
	
	private ConcurrentMap<SocketAddress, SocketChannel> clients;
	
	public TestServerChannelInitializer() {
		this.clients = new ConcurrentHashMap<>();
	}

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		super.handlerAdded(ctx);
		Channel ch = ctx.channel();
		SocketAddress localAddress = ch.localAddress();
		SocketAddress remoteAddress = ch.remoteAddress();
		SocketChannel channel = clients.get(remoteAddress);
		if (channel == null) {
			clients.put(remoteAddress, (SocketChannel)ch);
		}
		else if (channel.equals(ch)) {
		}
		else {
		}
		logger.info("The local address {} has added {} with channel {}. ", localAddress, remoteAddress, ch);
	}

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		logger.info("The local address {} accepted a new address {} with channel {}. ", ch.localAddress(), ch.remoteAddress(), ch);
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		Channel ch = ctx.channel();
		SocketAddress localAddress = ch.localAddress();
		SocketAddress remoteAddress = ch.remoteAddress();
		SocketChannel channel = clients.get(remoteAddress);
		if (channel != null) {
			clients.remove(remoteAddress);
			if (channel.equals(ch)) {
			}
			else {
			}
		}
		else {
		}
		logger.info("The local address {} has removed {} with channel {}. ", localAddress, remoteAddress, ch);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		super.exceptionCaught(ctx, cause);
		Channel ch = ctx.channel();
		SocketAddress localAddress = ch.localAddress();
		SocketAddress remoteAddress = ch.remoteAddress();
		logger.error("The local address {} get an exception from {} with channel {}", localAddress, remoteAddress, ch, cause);
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
		Channel ch = ctx.channel();
		SocketAddress remoteAddress = ch.remoteAddress();
		logger.info("The channel {} of {} is active. ", ch, remoteAddress);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
		Channel ch = ctx.channel();
		SocketAddress remoteAddress = ch.remoteAddress();
		logger.info("The channel {} of {} is inactive. ", ch, remoteAddress);
	}
}
