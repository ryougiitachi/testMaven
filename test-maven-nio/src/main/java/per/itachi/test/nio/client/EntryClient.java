package per.itachi.test.nio.client;

import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import per.itachi.test.nio.client.config.TotalConfig;
import per.itachi.test.nio.client.thread.ClientBusinessHandle;
import per.itachi.test.nio.client.thread.ClientRunnable;
import per.itachi.test.nio.client.thread.ClientUncaughtExceptionHandler;

public class EntryClient {

	private static final Logger log = LoggerFactory.getLogger(EntryClient.class);
	
	/** 
	 * 〈Simple description of the method〉  
	 * 〈Function of the method〉 
	 * 
	 * @param [parameter type]     [description of the parameter]  
	 * @param [parameter type]     [description of the parameter] 
	 * @return  [return type] 
	 * @exception/throws [exception type] [description of exception]  
	 * @see [relate method or delegation method]
	 */
	public static void main(String[] args) {
		log.info("Starting to launch clients...");
		log.info("Launching main thread...");
		
		TotalConfig config = TotalConfig.load();
		if (config.getBasic() == null) {
			log.error("Failed to load configurations, and terminate launch. ");
			return;
		}
		
		Thread.setDefaultUncaughtExceptionHandler(new ClientUncaughtExceptionHandler());
		
		int i, count;
		List<ClientRunnable> listClient;
		ClientRunnable client;
		Thread thrClient;
		count = config.getBasic().getNumConn();
		listClient = new Vector<>(count);
		for (i = 0; i < count; ++i) {
			client = new ClientBusinessHandle(config);
//			thrClient = new Thread(client, client.toString());
			thrClient = new Thread(client);
			thrClient.start();
			listClient.add(client);
		}
	}
}
