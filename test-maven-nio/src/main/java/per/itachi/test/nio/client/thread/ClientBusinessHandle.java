package per.itachi.test.nio.client.thread;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import per.itachi.test.nio.client.config.TotalConfig;
import per.itachi.test.nio.client.constant.TransactionDataConst;
import per.itachi.test.nio.client.data.TransactionData;

public class ClientBusinessHandle implements ClientRunnable {
	
	private final Logger log = LoggerFactory.getLogger(ClientBusinessHandle.class);
	
	private String name;
	private TotalConfig config;
	private Socket socket;
	
	private boolean running;
	
	public ClientBusinessHandle(TotalConfig config) {
		this.config = config;
		this.socket = new Socket();
	}

	@Override
	public void run() {
		InetAddress serverIP = config.getBasic().getServerIP();
		int serverPort = config.getBasic().getServerPort();
		try {
			log.info(MessageFormat.format("Connecting to {0}:{1,number,#}", serverIP, serverPort));
			socket.connect(new InetSocketAddress(serverIP, serverPort));
			log.info(MessageFormat.format("Connected to {0}:{1,number,#}", serverIP, serverPort));
			log.info(MessageFormat.format("Local socket address {0}", socket.getLocalSocketAddress()));
		} 
		catch (IOException e) {
			log.error("Failed to connect to server, exit current thread.", e);
			terminate();
			return;
		}
		
		OutputStream sos = null;
		InputStream sis = null;
		try {
			sos = socket.getOutputStream();
			sis = socket.getInputStream();
		} 
		catch (IOException e) {
			log.error("Error occurs when initialising I/O stream, exit current thread.", e);
			terminate();
			return;
		}
		
		running = true;
		
		Map<String, String> mapRequest = new HashMap<String, String>();
		TransactionData dataRequest = new TransactionData(config, 8003);
		
		byte[] bytesResponse = new byte[TransactionDataConst.LENGTH_SEGMENT];
		try {
			while (running) {
				dataRequest = new TransactionData(config, 8003);
				handleBusinessPulse(mapRequest);
				dataRequest.parseParamsToBytes(mapRequest);
				dataRequest.write(sos);
				log.debug("write");
				
				//Just be lazy
				sis.read(bytesResponse);
				log.debug("read");
				Thread.sleep(config.getBasic().getInterval());
			}
		} 
		catch (IOException e) {
			log.error("Error occurs when initialising I/O stream, exit current thread.", e);
		} 
		catch (InterruptedException e) {
			log.error("Current thread is interrupted.", e);
		}
		finally {
			if (sis != null) {
				try {
					sis.close();
				} 
				catch (IOException e) {
					log.error(e.getMessage(), e);
				}
			}
			if (sos != null) {
				try {
					sos.close();
				} 
				catch (IOException e) {
					log.error(e.getMessage(), e);
				}
			}
			if (socket != null && !socket.isClosed()) {
				try {
					log.info(MessageFormat.format("Disconnecting to {0}:{1,number,#}", serverIP, serverPort));
					socket.close();
					log.info(MessageFormat.format("Disconnected to {0}:{1,number,#}", serverIP, serverPort));
				} 
				catch (IOException e) {
					log.error("", e);
				}
			}
		}
		//it is better to add a method to notify server disconnect.
	}
	
	private void handleBusinessPulse(Map<String, String> request) {
		request.put("businessID", "0");
		request.put("serialNumber", "0");
	}

	/**
	 * close socket / interrupt thread / jump out of while loop
	 * */
	@Override
	public void terminate() {
//		if (!socket.isClosed()) {
//			try {
//				socket.close();
//			} 
//			catch (IOException e) {
//				log.error(e.toString(), e);
//			}
//		}
//		Thread.currentThread().interrupt();
		running = false;
	}

	@Override
	public String toString() {
		if (!socket.isClosed()) {
			return "Idle Business Thread";
		}
		else if (name == null) {
			name = MessageFormat.format("Business Thread {0}", socket.getLocalAddress());
		}
		return name;
	}
}
