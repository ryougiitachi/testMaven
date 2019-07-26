package per.itachi.test.redis.controller;

import java.io.Serializable;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.types.RedisClientInfo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.lettuce.core.RedisClient;
import per.itachi.test.redis.entity.RedisRequestEntity;
import per.itachi.test.redis.entity.RedisResponseEntity;
import per.itachi.test.redis.lock.DistributedLock;
import per.itachi.test.redis.lock.RedisDistributedLock;

@Controller
@RequestMapping("/")
public class RedisController {
	
	private final Logger logger = LoggerFactory.getLogger(RedisController.class);
	
//	@Autowired
	private CacheManager cacheMgr;
	
	@Autowired
	private RedisTemplate<String, Serializable> redisTemplate;
	
	@Autowired
	private RedisClient connLettuce;
	
	@RequestMapping(path="/spring-redis-template", method={RequestMethod.POST})
	@ResponseBody
	public RedisResponseEntity useRedisTemplate(@RequestBody RedisRequestEntity requestBody) {
		switch (requestBody.getDataType()) {
		case 1://string
			ValueOperations<String, Serializable> operations = redisTemplate.opsForValue();
			//关于分隔符，redis desktop mananger里面只识别冒号(:)，其他的符号不识别，包括不限于波浪号(~)、点(.)、竖线(|)；
			operations.set("string:redisTemplate:" + requestBody.getKey(), requestBody.getValue());
			break;
		default:
			break;
		}
		RedisResponseEntity responseBody = new RedisResponseEntity();
		responseBody.setResultCode(0);
		return responseBody;
	}
	
	@RequestMapping(path="/spring-redis-template/info", method={RequestMethod.POST})
	@ResponseBody
	public RedisResponseEntity showRedisTemplateInfo() {
		logger.info("RedisTemplate RedisConnectionFactory is {}", redisTemplate.getConnectionFactory());
		logger.info("RedisTemplate RedisConnection is {}", redisTemplate.getConnectionFactory().getConnection());
		logger.info("RedisTemplate RedisClientInfo are:");
		for (RedisClientInfo item : redisTemplate.getClientList()) {
			logger.info("RedisTemplate RedisClientInfo item is {}. ", item);
		}
		RedisResponseEntity responseBody = new RedisResponseEntity();
		responseBody.setResultCode(0);
		return responseBody;
	}
	
	@RequestMapping(path="/jedis", method={RequestMethod.POST})
	@ResponseBody
	public RedisResponseEntity useJedis(@RequestBody RedisRequestEntity requestBody) {
		RedisResponseEntity responseBody = null;
		return responseBody;
	}
	
	@RequestMapping(path="/lettuce", method={RequestMethod.POST})
	@ResponseBody
	public RedisResponseEntity useLettuce(@RequestBody RedisRequestEntity requestBody) {
		RedisResponseEntity responseBody = null;
		return responseBody;
	}
	
	@RequestMapping(path="/lettuce/info", method={RequestMethod.POST})
	@ResponseBody
	public RedisResponseEntity showLettuceInfo() {
		RedisResponseEntity responseBody = null;
		return responseBody;
	}
	
	@RequestMapping(path="/lock", method={RequestMethod.GET})
	@ResponseBody
	public RedisResponseEntity testRedisLock() {
		DistributedLock lock = new RedisDistributedLock(UUID.randomUUID().toString(), redisTemplate);
		lock.lock(1000);
		RedisResponseEntity responseBody = null;
		return responseBody;
	}
	
	@RequestMapping(path="/unlock", method={RequestMethod.GET})
	@ResponseBody
	public RedisResponseEntity testRedisUnlock(@RequestParam String lockID) {
		DistributedLock lock = new RedisDistributedLock(lockID, redisTemplate);
		lock.unlock();
		RedisResponseEntity responseBody = null;
		return responseBody;
	}
}
