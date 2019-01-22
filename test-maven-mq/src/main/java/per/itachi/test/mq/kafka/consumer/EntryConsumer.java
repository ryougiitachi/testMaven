package per.itachi.test.mq.kafka.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource({"file:${APP_HOME_CONF}/consumer-cfg.properties"})
public class EntryConsumer {
	
	private static final Logger logger = LoggerFactory.getLogger(EntryConsumer.class);

	public static void main(String[] args) {
		SpringApplication.run(EntryConsumer.class, args);
	}

}
