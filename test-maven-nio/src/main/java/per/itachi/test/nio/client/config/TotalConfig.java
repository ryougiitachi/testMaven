package per.itachi.test.nio.client.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TotalConfig {
	
	private static final Logger log = LoggerFactory.getLogger(TotalConfig.class);
	
	private BasicConfig basic;
	
	public static TotalConfig load() {
		TotalConfig config = new TotalConfig();
		log.info("Starting to load all configuration. ");
		config.basic = BasicConfig.load("config/BasicClient.xml");
		if (config.basic == null) {
			log.info("Failed to load client basic configuration. ");
		}
		else {
			log.info("Complete loading all configuration. ");
		}
		return config;
	}
	
	protected TotalConfig() {}

	public BasicConfig getBasic() {
		return basic;
	}
}
