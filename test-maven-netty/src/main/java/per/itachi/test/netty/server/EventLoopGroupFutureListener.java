package per.itachi.test.netty.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

@Component
public class EventLoopGroupFutureListener implements GenericFutureListener<Future<Object>> {
	
	private final Logger logger = LoggerFactory.getLogger(EventLoopGroupFutureListener.class);

	@Override
	public void operationComplete(Future<Object> future) throws Exception {
		logger.info("The EventLoopGroup future {} has been completed");
	}
}
