package per.itachi.test.netty.server;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

@Component
public class ServerChildChannelInitialzer extends ChannelInitializer<SocketChannel> {
	
	private final Logger logger = LoggerFactory.getLogger(ServerChildChannelInitialzer.class);
	
	@Autowired
	private ChannelInboundHandler emptyOpInboundHandler1;
	
	@Autowired
	private ChannelInboundHandler emptyOpInboundHandler2;
	
	@PostConstruct
	private void init() {
		logger.info("emptyOpInboundHandler1 has been @Autowired, {}. ", emptyOpInboundHandler1);
		logger.info("emptyOpInboundHandler2 has been @Autowired, {}. ", emptyOpInboundHandler2);
	}

	@Override
	protected void initChannel(SocketChannel channel) throws Exception {
		ChannelPipeline pipeline = channel.pipeline();
		//When a ChannelHandler without @Sharable is tried to addLast, an exception will be thrown. 
		pipeline.addLast(emptyOpInboundHandler1)
				.addLast(emptyOpInboundHandler2);
		logger.info("The child handler initialised socket channel {}. ", channel);
	}
}
