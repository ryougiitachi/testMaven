package per.itachi.test.springboot.controller;

import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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

/**
 * 有没有@Component的区别：有的情况实例生命周期将由Spring管理，没有的情况将由tomcat/jetty等容器管理；
 * 两者都会按照每个session一个实例的规则创建，顾不用考虑线程安全问题（？）；<br/>
 * websocket相关类与接口在多个包中都有，尽量不要让他们重复，否则打包或启动的时候可能会由于包冲突而报错（ServerContainer not available）<br/>
 * message的注解方法有多种形式，入参为String类型的比较常用；
 * 不同类型的send值会调用不同的方法，前台传过来的通常都是String的，需要考虑一下字符集问题；<br/>
 * */
@Component
@ServerEndpoint(value="/websocket")
public class TestWebSocketServer {
	
	private static final Object MUTEX = new Object();
	
	private static final ConcurrentMap<Session, Object> sessions = new ConcurrentHashMap<>();
	
	private final Logger logger = LoggerFactory.getLogger(TestWebSocketServer.class);
	
	public TestWebSocketServer() {
		logger.info("Created a new TestWebSocketServer with {}. ", this);
	}
	
	@OnOpen
	public void onOpen(Session session, EndpointConfig endpointConfig) {
		logger.info("The session {} has established with {} in {}. ", session, endpointConfig, this);
		addSession(session);
	}
	
	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		logger.info("The session {} has disconnected in {}. ", session, this);
		removeSession(session);
	}
	
	@OnMessage
	public void onMessage(Session session, PongMessage message) {
		ByteBuffer buffer = message.getApplicationData();
		logger.info("The session {} sent PongMessage {} in {}. ", session, buffer, this);
		if (buffer.remaining() > 0) {
			byte[] bytesData = new byte[512];
			buffer.get(bytesData, buffer.position(), buffer.remaining());
			logger.debug("The content of PongMessage {} {} is {}. ", buffer, buffer.remaining(), new String(bytesData, 0, buffer.remaining()));
		}
	}
	
	@OnMessage
	public void onMessage(Session session, byte[] message) {
		logger.info("The session {} sent byte[] {} in {}. ", session, message, this);
	}
	
	@OnMessage
	public void onMessage(Session session, String message) {
		logger.info("The session {} sent {} in {}. ", session, message, this);
	}
	
	@OnError
	public void onError(Session session, Throwable throwable) {
		logger.error("Error occured when session {} is handling sth. in {} ", session, this, throwable);
		removeSession(session);
	}
	
	private void addSession(Session session) {
		sessions.put(session, MUTEX);
	}
	
	private void removeSession(Session session) {
		sessions.remove(session);
	}
}
