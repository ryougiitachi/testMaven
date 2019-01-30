package per.itachi.test.springboot.controller;

import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.PongMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

//@Component
@ServerEndpoint(value="/websocket")
public class TestWebSocketServer {
	
	private final Logger logger = LoggerFactory.getLogger(TestWebSocketServer.class);
	
	public TestWebSocketServer() {
		logger.info("Created a new TestWebSocketServer with {}. ", this);
	}
	
	@OnOpen
	public void onOpen(Session session, EndpointConfig endpointConfig) {
		logger.info("The session {} has established in {}. ", session, this);
	}
	
	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		logger.info("The session {} has disconnected in {}. ", session, this);
	}
	
	@OnMessage
	public void onMessage(PongMessage message) {
		logger.info("The session  sent PongMessage {} in {}. ", message, this);
	}
	
	@OnMessage
	public void onMessage(byte[] message) {
		logger.info("The session  sent byte[] {} in {}. ", message, this);
	}
	
	@OnError
	public void onError(Session session, Throwable throwable) {
		logger.error("Error occured when session {} is handling sth. in {} ", session, this, throwable);
	}
}
