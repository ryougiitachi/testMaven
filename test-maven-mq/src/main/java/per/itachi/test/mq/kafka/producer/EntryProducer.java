package per.itachi.test.mq.kafka.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource({"file:${APP_HOME_CONF}/producer-cfg.properties"})
public class EntryProducer {
	
	private static final Logger logger = LoggerFactory.getLogger(EntryProducer.class);

	public static void main(String[] args) {
		SpringApplication.run(EntryProducer.class, args);
	}

}
