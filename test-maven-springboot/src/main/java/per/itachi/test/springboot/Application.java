package per.itachi.test.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

import per.itachi.test.springboot.bean.TestSpringLifeCycleBean;

@SpringBootApplication
@ComponentScan({"per.itachi.test.springboot"})
@PropertySource({"file:${APP_CONF_HOME}/config-springmvc.properties"})
public class Application {

//	@Bean(initMethod="executeInitMethod", destroyMethod="executeDestroyMethod")
	public TestSpringLifeCycleBean testSpringLifeCycleBean() {
		return new TestSpringLifeCycleBean();
	}
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
