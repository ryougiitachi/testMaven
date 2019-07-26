package per.itachi.test.redis.lock;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.StringUtils;

public class RedisDistributedLock implements DistributedLock {
	
	private static final String KEY_LOCK_USER = "lock:user";
	
	private final Logger logger = LoggerFactory.getLogger(RedisDistributedLock.class);

	private RedisTemplate<String, Serializable> redisTemplate;
	
	private String identifier;
	
	public RedisDistributedLock(String identifier, RedisTemplate<String, Serializable> redisTemplate) {
		this.identifier = identifier;
		this.redisTemplate = redisTemplate;
	}
	
	@Override
	public boolean lock(int timeout) {
		if (timeout <= 0) {
			return false;
		}
		logger.info("Trying to acquire redis lock with id [{}]. ", identifier);
		ValueOperations<String, Serializable> redis = redisTemplate.opsForValue();
		if (!StringUtils.hasText(identifier)) {
			identifier = UUID.randomUUID().toString();
		}
		long lTimeoutPoint = System.currentTimeMillis() + timeout;
		try {
			while (System.currentTimeMillis() <= lTimeoutPoint) {
				if (redis.setIfAbsent(KEY_LOCK_USER, identifier)) {
					logger.info("Successfully acquired lock with id [{}]. ", identifier);
					return true;
				}
				Thread.sleep(1l);
			}
		} 
		catch (InterruptedException e) {
			logger.warn("Interrupted. ", e);
		}
		logger.info("Failed to acquire lock with id [{}]. ", identifier);
		return false;
	}

	@Override
	public void unlock() {
		logger.info("Trying to release redis lock with id [{}]. ", identifier);
		redisTemplate.watch(KEY_LOCK_USER);
		ValueOperations<String, Serializable> valueOperations = redisTemplate.opsForValue();
		Serializable strCurrentLockID = valueOperations.get(KEY_LOCK_USER);//maybe null 
		if (identifier.equals(strCurrentLockID)) {
			//if transaction support doesn't enable, exception will throw but commands can execute. 
			redisTemplate.multi();
			redisTemplate.delete(KEY_LOCK_USER);
			List<Object> listResult = redisTemplate.exec();
			logger.info("Successfully released lock with id [{}]. ", identifier);
			logger.info("The result of releasing lock is {}", listResult);
		}
		else {
			redisTemplate.unwatch();
			logger.info("Failed to release lock because id [{}] isn't lock owner. ", identifier);
		}
	}

//	//Don't use redisTemplate.executePipelined as pipeline because connection.get will be invalid. 
//	@Override
//	public void unlock() {
//		logger.info("Trying to release redis lock with id [{}]. ", identifier);
//		ValueOperations<String, Serializable> redis = redisTemplate.opsForValue();
//		logger.info("The value of lock:user is {}. ", redis.get(KEY_LOCK_USER));
//		List<Object> listResult = redisTemplate.executePipelined(new RedisCallback<Serializable>() {
//			@Nullable
//			@Override
//			@SuppressWarnings("unchecked")
//			public Serializable doInRedis(RedisConnection connection) throws DataAccessException {
//				logger.info("RedisConnection:{}, Pipelined:{}, Closed:{} ", connection, connection.isPipelined(), connection.isClosed());
//				logger.info("RedisConnection:{}, time:{} ", connection.info(), connection.time());
//				RedisSerializer<Object> serializerKey = (RedisSerializer<Object>)redisTemplate.getKeySerializer();
//				RedisSerializer<Object> serializerValue = (RedisSerializer<Object>)redisTemplate.getValueSerializer();
//				byte[] bytesLockIDKey = serializerKey.serialize(KEY_LOCK_USER);
//				connection.watch(bytesLockIDKey);
//				//Don't use connection.get() in pipeline because this method will return null in this mode.
//				byte[] bytesLockIDValue = connection.get(bytesLockIDKey);
//				String strLockIDValue = serializerValue.deserialize(bytesLockIDValue).toString();
//				if (identifier.equals(strLockIDValue)) {
//					connection.multi();
//					connection.del(bytesLockIDKey);
//					connection.exec();
//					logger.info("Successfully released lock with id [{}]. ", identifier);
//				}
//				else {
//					connection.unwatch();
//					logger.info("Failed to release lock with id [{}]. ", identifier);
//				}
//				return null;
//			}
//		});
//		logger.info("The result of releasing lock is {}", listResult);
//	}
	
}
