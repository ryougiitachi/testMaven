package per.itachi.test.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

import per.itachi.test.springboot.bean.TestSpringLifeCycleBean;

/**
 * 远程调试所用 -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=8098
 * */
@SpringBootApplication(scanBasePackages = {"per.itachi.test.springboot"})
@PropertySource({
		"file:${APP_CONF_HOME}/config-springmvc.properties", 
		"file:${APP_CONF_HOME}/config-springsecurity.properties"})
public class Application {

//	@Bean(initMethod="executeInitMethod", destroyMethod="executeDestroyMethod")
	public TestSpringLifeCycleBean testSpringLifeCycleBean() {
		return new TestSpringLifeCycleBean();
	}

	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
