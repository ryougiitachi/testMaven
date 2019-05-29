package per.itachi.test.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import per.itachi.test.springboot.bean.TestSpringLifeCycleBean;

/**
 * 远程调试所用 -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=8098
 * */
@SpringBootApplication
@ComponentScan({"per.itachi.test.springboot"})
@PropertySource({"file:${APP_CONF_HOME}/config-springmvc.properties"})
public class Application {

//	@Bean(initMethod="executeInitMethod", destroyMethod="executeDestroyMethod")
	public TestSpringLifeCycleBean testSpringLifeCycleBean() {
		return new TestSpringLifeCycleBean();
	}
	
	/**
	 * ServerEndpointExporter是SpringBoot启用websocket的关键；
	 * */
	@Bean
	public ServerEndpointExporter serverEndpointExporter() {
		return new ServerEndpointExporter();
	}

	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
