package per.itachi.test.netty.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

@Component
public class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {
	
	@Autowired
	private ChannelInboundHandlerAdapter clientInboundHandlerAdapter1;
	
	@Autowired
	private ChannelInboundHandlerAdapter clientInboundHandlerAdapter2;
	
	@Autowired
	private ChannelOutboundHandlerAdapter clientOutboundHandlerAdapter1;

	@Override
	protected void initChannel(SocketChannel channel) throws Exception {
		ChannelPipeline pipeline = channel.pipeline();
		pipeline
				.addLast(clientInboundHandlerAdapter1)
				.addLast(clientInboundHandlerAdapter2)
				.addLast(clientOutboundHandlerAdapter1);
	}
}
