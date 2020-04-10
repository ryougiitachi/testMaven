package per.itachi.test.netty.server.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelHandler.Sharable;

@Component
@Sharable
public class EmptyOpInboundHandler2 extends ChannelInboundHandlerAdapter {
	
	private final Logger logger = LoggerFactory.getLogger(EmptyOpInboundHandler2.class);

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		logger.info("EmptyOpInboundHandler2 fireChannelRead(Object) start with context {}, msg={}. ", ctx, msg);
		ctx.fireChannelRead(msg);
		logger.info("EmptyOpInboundHandler2 fireChannelRead(Object) finished. ");
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		logger.info("EmptyOpInboundHandler2 fireChannelReadComplete() start with context {}. ", ctx);
		ctx.fireChannelReadComplete();
		logger.info("EmptyOpInboundHandler2 fireChannelReadComplete() finished. ");
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.info("EmptyOpInboundHandler2 fireExceptionCaught(Throwable) start with context {}. ", ctx, cause);
		ctx.fireExceptionCaught(cause);
		logger.info("EmptyOpInboundHandler2 fireExceptionCaught(Throwable) finished. ");
	}

}
