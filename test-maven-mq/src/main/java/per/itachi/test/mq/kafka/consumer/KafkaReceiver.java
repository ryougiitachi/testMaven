package per.itachi.test.mq.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaReceiver {
	
	private final Logger logger = LoggerFactory.getLogger(KafkaReceiver.class);
	
	@KafkaListener(topics = { "mykafka" })
	public void listen(ConsumerRecord<Object, Object> record) {
		logger.info("The content is {}. ", record);
	}
}
