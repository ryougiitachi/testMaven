package per.itachi.test.netty.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

@Component
public class ClientOutboundHandlerAdapter1 extends ChannelOutboundHandlerAdapter {
	
	private final Logger logger = LoggerFactory.getLogger(ClientOutboundHandlerAdapter1.class);

	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		logger.info("ctx={}, msg={}, promise={}. ", ctx, msg, promise);
		ctx.write(msg);
	}

}
