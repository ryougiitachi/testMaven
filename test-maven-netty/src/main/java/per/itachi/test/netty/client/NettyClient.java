package per.itachi.test.netty.client;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

@Component
public class NettyClient {
	
	private final Logger logger = LoggerFactory.getLogger(NettyClient.class);
	
	@Autowired
	private ChannelInitializer<SocketChannel> clientChannelInitializer;
	
	@Autowired
	private GenericFutureListener<Future<Void>> clientFutureListener;
	
	private String remoteIP = "127.0.0.1";
	
	private int remotePort = 9091;
	
	private EventLoopGroup loopgrp;
	
	@PostConstruct
	private void init() {
		loopgrp = new NioEventLoopGroup();
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(loopgrp).channel(NioSocketChannel.class)
				.handler(clientChannelInitializer);
		logger.info("Start connecting to remote {}:{}. ", remoteIP, remotePort);
		try {
			ChannelFuture channelFuture = bootstrap.connect(remoteIP, remotePort).sync();
			logger.info("Start sending to remote host. ");
			channelFuture.channel().writeAndFlush(ByteBufAllocator.DEFAULT.buffer().writeBytes(getClass().getName().getBytes())).sync();
			logger.info("Try to close. ");
			channelFuture.channel().closeFuture().sync();
		} 
		catch (InterruptedException e) {
			logger.error("", e);
		}
	}
	
	@PreDestroy
	private void close() {
		loopgrp.shutdownGracefully();
	}

}
