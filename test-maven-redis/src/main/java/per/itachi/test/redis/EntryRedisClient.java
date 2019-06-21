package per.itachi.test.redis;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

public class EntryRedisClient {
	
	private static final Logger logger = LoggerFactory.getLogger(EntryRedisClient.class);

	public static void main(String[] args) {
		String strIP = "127.0.0.1";
		int iPort = 10001;
		try(Jedis jedis = new Jedis(strIP, iPort);
				Pipeline pipeline = jedis.pipelined();) {
			logger.info("Start setting. ");
			for (int i = 1; i <= 5; i++) {
				pipeline.set("string:test-jedis:" + i, String.valueOf(i));
				pipeline.expire("string:test-jedis:" + i, 30);
			}
//			pipeline.sync();
//			pipeline.exec();
			List<Object> listResult = pipeline.syncAndReturnAll();
			for (Object objResult : listResult) {
				logger.info("listResult - {}", objResult);
			}
			
			//sleep
			TimeUnit.SECONDS.sleep(15l);
			
			logger.info("Finish setting. ");
		}
		catch (InterruptedException e) {
			logger.error("Interrupted. ", e);
		}
		catch (IOException e) {
			logger.error("", e);
		}
	}
}
