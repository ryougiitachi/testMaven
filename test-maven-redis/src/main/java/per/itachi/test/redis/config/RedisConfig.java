package per.itachi.test.redis.config;

import java.io.Serializable;
import java.nio.charset.Charset;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;

import io.lettuce.core.RedisClient;

@Configuration
public class RedisConfig {
	
	/**
	 * Error will occur if no redisConnectionFactory. 
	 * */
	@Bean("redisTemplate")
	public RedisTemplate<String, Serializable> initRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<String, Serializable> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisConnectionFactory);
		//序列化转换器，如果键值不加StringRedisSerializer，默认转换器会用ObjectOutputStream写入流，
		//结果会增加一堆类似\xAC\xED\x00\x05t\x00的二进制数值，疑似java对象结构数据；
		RedisSerializer<String> redisKeySerializer = new StringRedisSerializer(Charset.forName("UTF-8"));
		redisTemplate.setKeySerializer(redisKeySerializer);
		redisTemplate.setHashKeySerializer(redisKeySerializer);
		redisTemplate.setValueSerializer(new FastJsonRedisSerializer<>(Serializable.class));
		redisTemplate.setEnableTransactionSupport(true);//Exception will throw when executing multi/exec if not set. 
		return redisTemplate;
	}
	
	@Bean("lettuceRedisClient")
	public RedisClient initLettuceRedisClient() {
		RedisClient client = RedisClient.create("redis://127.0.0.1:10001");
		return client;
	}
}
