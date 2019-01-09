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
	protected void initChannel(SocketChannel ch) throws Exception {
		logger.info("The local address {} accepted a new address {} with channel {}. ", ch.localAddress(), ch.remoteAddress(), ch);
		SocketAddress remoteAddress = ch.remoteAddress();
		SocketChannel channel = clients.get(remoteAddress);
		if (channel == null) {
			clients.put(remoteAddress, ch);
		}
		else if (channel.equals(ch)) {
			
		}
		else {
			
		}
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
}
