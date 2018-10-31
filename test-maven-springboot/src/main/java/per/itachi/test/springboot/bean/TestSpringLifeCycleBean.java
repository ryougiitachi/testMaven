package per.itachi.test.springboot.bean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * 注解Bean和Component都可以在初始化bean的时候执行各种生命周期方法，并且不会冲突；如果Bean和Component同时使用需要指定bean名称以免冲突； <br/>
 * 其中Component可通过直接在方法上进行注解Bean的方式执行init-method和destroy-method； <br/>
 * 只是顺序有所不同—— <br/>
 * Bean的执行顺序：@PostConstruct-> afterPropertiesSet -> init-method ->@PreDestroy -> destroy -> destroy-method <br/>
 * 其中，3个初始化方法都在main线程中执行，后面3个销毁方法在销毁线程（Thread-1）中执行<br/>
 * Component的执行顺序：@PostConstruct-> afterPropertiesSet -> init-method -> destroy-method ->@PreDestroy -> destroy <br/>
 * 其中，3个初始化方法和destroy-method都在main线程中执行， 另外两个销毁方法在销毁线程（Thread-1）中执行<br/>
 * 如果没有特殊需求，使用PostConstruct和PreDestroy就能够满足大部分初始化与清理的任务；<br/>
 * */
@Component("TestSpringLifeCycleComponent.")
public class TestSpringLifeCycleBean implements InitializingBean, DisposableBean {
	
	private final Logger logger = LoggerFactory.getLogger(TestSpringLifeCycleBean.class);
	
	@PostConstruct
	private void executePostConstruct() {
		logger.info("Execute @PostConstruct in spring lifecycle. ");
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		logger.info("Execute afterPropertiesSet of InitializingBean in spring lifecycle. ");
	}
	
	@Bean(initMethod="executeInitMethod")
	protected void executeInitMethod() {
		logger.info("Execute init-method of <bean/> in spring lifecycle. ");
	}
	
	@PreDestroy
	private void executePreDestroy() {
		logger.info("Execute @PreDestroy in spring lifecycle. ");
	}

	@Override
	public void destroy() throws Exception {
		logger.info("Execute destroy of DisposableBean in spring lifecycle. ");
	}
	
	@Bean(destroyMethod="executeDestroyMethod")
	protected void executeDestroyMethod() {
		logger.info("Execute destroy-method of <bean/> in spring lifecycle. ");
	}
}
