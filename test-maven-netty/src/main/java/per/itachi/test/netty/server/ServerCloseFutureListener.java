package per.itachi.test.netty.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

@Component
public class ServerCloseFutureListener implements GenericFutureListener<Future<Void>> {
	
	private final Logger logger = LoggerFactory.getLogger(ServerCloseFutureListener.class);
	
	@Override
	public void operationComplete(Future<Void> future) throws Exception {
		logger.info("The Server has been closed successfully, get={}, getNow={}, isCancellable={}, isCancelled={}, isDone={}, isSuccess={}. ", 
				future, future.get(), future.getNow(), future.isCancellable(), future.isCancelled(), future.isDone(), future.isSuccess());
		System.exit(0);
	}
}
