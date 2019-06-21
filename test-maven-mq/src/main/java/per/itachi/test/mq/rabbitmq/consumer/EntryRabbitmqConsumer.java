package per.itachi.test.mq.rabbitmq.consumer;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource({"classpath:config/consumer-cfg.properties"})
public class EntryRabbitmqConsumer {
	
	/**
	 * 若直接set到Template中，接收消息的时候会报MessageConversionException转换消息的错误；
	 * 但是用@Bean的方式却没问题；
	 * 不知道为什么
	 * */
	@Bean ("jsonMessageConverter")
	public MessageConverter initJsonMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}
	
//	@Bean("rabbitTemplate1")
//	public RabbitTemplate initRabbitTemplate(final ConnectionFactory connectionFactory) {
//		RabbitTemplate template = new RabbitTemplate(connectionFactory);
//		template.setEncoding("UTF-8");
//		template.setMessageConverter(new Jackson2JsonMessageConverter());
//		return template;
//	}

	public static void main(String[] args) {
		SpringApplication.run(EntryRabbitmqConsumer.class, args);
	}
}
