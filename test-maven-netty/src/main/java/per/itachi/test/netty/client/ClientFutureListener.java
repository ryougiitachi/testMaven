package per.itachi.test.netty.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelPromise;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

@Component
public class ClientFutureListener implements GenericFutureListener<Future<Void>> {
	
	private final Logger logger = LoggerFactory.getLogger(ClientFutureListener.class);
	
	@Override
	public void operationComplete(Future<Void> future) throws Exception {
		logger.info("The channel future {} has been completed, isCancellable={}, isCancelled={}, isDone={}, isSuccess={}. ", 
				future, future.isCancellable(), future.isCancelled(), future.isDone(), future.isSuccess());
		if (future.isSuccess()) {
			ChannelPromise promise = (ChannelPromise)future;
			promise.channel().writeAndFlush(getClass().getName());
		}
		else {
			logger.info("The channel future {} caused exception. ", future, future.cause());
		}
	}
	
}
