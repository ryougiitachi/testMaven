package per.itachi.test.mq.rabbitmq.consumer;

import java.io.IOException;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.AbstractMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.Channel;

import per.itachi.test.mq.model.SimpleEntity;

@Component
public class RabbitmqConsumerListener {
	
	private final Logger logger = LoggerFactory.getLogger(RabbitmqConsumerListener.class);
	
	@Autowired
	private ConnectionFactory connectionFactory;
	
	//若不明确指定该值是null
	@Autowired(required=false)
	private RabbitAdmin rabbitAdmin;
	
	@Autowired
	private AmqpTemplate ampqTemplate;
	
	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	//若不明确指定该值是null
	@Autowired(required=false)
	private SimpleAsyncTaskExecutor asyncTaskExecutor;
	
	//若不明确指定该值是null
	@Autowired(required=false)
	private AbstractMessageListenerContainer container;

	//若不明确指定该值是null
	@Autowired(required=false)
	private DirectMessageListenerContainer directMsgListenerContainer;

	//若不明确指定该值是null
	@Autowired(required=false)
	private SimpleMessageListenerContainer simpleMsgListenerContainer;
	
	@PostConstruct
	private void init() {
		logger.info("ConnectionFactory: {}", connectionFactory);
		logger.info("AmqpTemplate: {}", ampqTemplate);
		logger.info("RabbitTemplate: {}", rabbitTemplate);
	}
	
	@RabbitListener(queues="queue.test-spring")
	public void listenQueue(SimpleEntity entity, Channel channel, Message message, @Headers Map<String, Object> headers) {
		MessageProperties properties = message.getMessageProperties();
		logger.info("headers={}", JSON.toJSON(headers));
		logger.info("MessageProperties={}", properties);
		logger.info("{}", entity);
		try {
			channel.basicAck(properties.getDeliveryTag(), false);//delivery-tag应该是从Channel连接的时候以1开始的顺序，重启后重新来；
		} 
		catch (IOException e) {
			logger.error("", e);
			try {
				channel.basicNack(properties.getDeliveryTag(), false, false);
			} 
			catch (IOException ioe) {
				logger.error("", ioe);
			}
		}
	}
	
	/**
	 * 想要在应用中声明这些需要初始化RabbitAdmin，但该应用没有显式初始化RabbitAdmin，估计在Container类中有相应的操作只不过不能注入；
	 * */
	@RabbitListener(bindings=@QueueBinding(
				exchange=@Exchange(name="xchg.direct.test-spring.client-declare", type="direct"), 
				value=@Queue(name="queue.test-spring.client-declare", durable="true"), 
				key={"routing.test-spring.client-declare"}
			))
	public void listenDeclareBinding(SimpleEntity entity, Channel channel, Message message) {
		MessageProperties properties = message.getMessageProperties();
		logger.info("Channel={}", channel);
		logger.info("MessageProperties={}", properties);
		logger.info("{}", entity);
		try {
			channel.basicAck(properties.getDeliveryTag(), false);//delivery-tag应该是从Channel连接的时候以1开始的顺序，重启后重新来；
		} 
		catch (IOException e) {
			logger.error("", e);
			try {
				channel.basicNack(properties.getDeliveryTag(), false, false);
			} 
			catch (IOException ioe) {
				logger.error("", ioe);
			}
		}
	}
}
