package per.itachi.test.redis.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;

@Configuration
public class MvcConfig {
	
	@Bean("fastJsonHttpMessageConverter")
	public HttpMessageConverters initFastJsonHttpMessageConverter() {
		FastJsonConfig fastJsonConfig = new FastJsonConfig();
		fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat);
		List<MediaType> fastJsonMediaTypes = new ArrayList<>();
		fastJsonMediaTypes.add(MediaType.APPLICATION_JSON);
		fastJsonMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
		FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
		fastConverter.setFastJsonConfig(fastJsonConfig);
		fastConverter.setSupportedMediaTypes(fastJsonMediaTypes);
		HttpMessageConverters converts = new HttpMessageConverters(fastConverter);
		return converts;
	}
}
