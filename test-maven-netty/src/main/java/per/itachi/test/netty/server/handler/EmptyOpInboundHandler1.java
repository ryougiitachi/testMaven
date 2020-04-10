package per.itachi.test.netty.server.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;

@Component
@Sharable
public class EmptyOpInboundHandler1 implements ChannelInboundHandler {
	
	private final Logger logger = LoggerFactory.getLogger(EmptyOpInboundHandler1.class);

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
	}

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		logger.info("EmptyOpInboundHandler1 fireChannelRegistered() start with context {}. ", ctx);
		ctx.fireChannelRegistered();
		logger.info("EmptyOpInboundHandler1 fireChannelRegistered() finished. ");
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		logger.info("EmptyOpInboundHandler1 fireChannelUnregistered() start with context {}. ", ctx);
		ctx.fireChannelUnregistered();
		logger.info("EmptyOpInboundHandler1 fireChannelRegistered() finished. ");
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		logger.info("EmptyOpInboundHandler1 fireChannelActive() start with context {}. ", ctx);
		ctx.fireChannelActive();
		logger.info("EmptyOpInboundHandler1 fireChannelActive() finished. ");
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		logger.info("EmptyOpInboundHandler1 fireChannelInactive() start with context {}. ", ctx);
		ctx.fireChannelInactive();
		logger.info("EmptyOpInboundHandler1 fireChannelInactive() finished. ");
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		logger.info("EmptyOpInboundHandler1 fireChannelRead(Object) start with context {}, msg={}. ", ctx, msg);
		ctx.fireChannelRead(msg);
		logger.info("EmptyOpInboundHandler1 fireChannelRead(Object) finished. ");
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		logger.info("EmptyOpInboundHandler1 fireChannelReadComplete() start with context {}. ", ctx);
		ctx.fireChannelReadComplete();
		logger.info("EmptyOpInboundHandler1 fireChannelReadComplete() finished. ");
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		logger.info("EmptyOpInboundHandler1 fireUserEventTriggered(Object) start with context {}, event={}. ", ctx, evt);
		ctx.fireUserEventTriggered(evt);
		logger.info("EmptyOpInboundHandler1 fireUserEventTriggered(Object) finished. ");
	}

	@Override
	public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
		logger.info("EmptyOpInboundHandler1 fireChannelWritabilityChanged() start with context {}. ", ctx);
		ctx.fireChannelWritabilityChanged();
		logger.info("EmptyOpInboundHandler1 fireChannelWritabilityChanged() finished. ");
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.info("EmptyOpInboundHandler1 fireExceptionCaught(Throwable) start with context {}. ", ctx, cause);
		ctx.fireExceptionCaught(cause);
		logger.info("EmptyOpInboundHandler1 fireExceptionCaught(Throwable) finished. ");
	}

}
