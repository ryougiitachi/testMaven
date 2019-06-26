package per.itachi.test.springboot.bean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportAware;
import org.springframework.context.weaving.LoadTimeWeaverAware;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.instrument.classloading.LoadTimeWeaver;
import org.springframework.jmx.export.notification.NotificationPublisher;
import org.springframework.jmx.export.notification.NotificationPublisherAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringValueResolver;
import org.springframework.web.context.ServletConfigAware;
import org.springframework.web.context.ServletContextAware;

/**
 * 注解Bean和Component都可以在初始化bean的时候执行各种生命周期方法，并且不会冲突；如果Bean和Component同时使用需要指定bean名称以免冲突； <br/>
 * 其中Component可通过直接在方法上进行注解Bean的方式执行init-method和destroy-method； <br/>
 * 只是顺序有所不同—— <br/>
 * Bean的执行顺序：@PostConstruct-> afterPropertiesSet -> init-method ->@PreDestroy -> destroy -> destroy-method <br/>
 * 其中，3个初始化方法都在main线程中执行，后面3个销毁方法在销毁线程（Thread-1）中执行<br/>
 * <del>Component的执行顺序：@PostConstruct-> afterPropertiesSet -> init-method -> destroy-method ->@PreDestroy -> destroy </del><br/>
 * <del>其中，3个初始化方法和destroy-method都在main线程中执行， 另外两个销毁方法在销毁线程（Thread-1）中执行</del><br/>
 * <i>注：Component应该没有办法直接执行init-method与destroy-method方法</i><br/>
 * <br/>
 * 如果没有特殊需求，使用PostConstruct和PreDestroy就能够满足大部分初始化与清理的任务；<br/>
 * <br/>
 * Aware的各种接口set方法在@PostConstruct之前执行，应该是算到了成员变量初始化阶段；<br/>
 * Aware的各种接口各自执行顺序：
 * BeanNameAware, BeanClassLoaderAware, BeanFactoryAware, EnvironmentAware, EmbeddedValueResolverAware, ResourceLoaderAware, ApplicationEventPublisherAware, 
 * MessageSourceAware, ApplicationContextAware, ServletContextAware <br/>
 * 这种执行顺序与implements中的声明顺序无关；<br/>
 * */
@Component("TestSpringLifeCycleComponent.")
public class TestSpringLifeCycleBean 
		implements 
				// 这是执行了的Aware
				BeanNameAware, BeanClassLoaderAware, BeanFactoryAware, EnvironmentAware, EmbeddedValueResolverAware, ResourceLoaderAware, ApplicationEventPublisherAware, MessageSourceAware, ApplicationContextAware, ServletContextAware, 
				// 这是没执行的Aware
				ImportAware, LoadTimeWeaverAware, NotificationPublisherAware, ServletConfigAware, 
				//bean生命周期接口 
				InitializingBean, DisposableBean {
	
	private final Logger logger = LoggerFactory.getLogger(TestSpringLifeCycleBean.class);
	
	@Value("${placeholder.property.test}")
	private String placeholderPropertyTest;
	
	@Value("${placeholder.env.test}")
	private String placeholderEnvTest;

	@Override
	public void setBeanName(String name) {
		logger.info("TestSpringLifeCycleBean has a bean instance named as {}. ", name);
	}

	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		logger.info("TestSpringLifeCycleBean has ClassLoader {}. ", classLoader);
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		logger.info("TestSpringLifeCycleBean has BeanFactory {}. ", beanFactory);
	}

	@Override
	public void setEnvironment(Environment environment) {
		logger.info("TestSpringLifeCycleBean has Environment {}. ", environment);
	}

	@Override
	public void setEmbeddedValueResolver(StringValueResolver resolver) {
		logger.info("TestSpringLifeCycleBean has StringValueResolver {}. ", resolver);
	}

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		logger.info("TestSpringLifeCycleBean has ResourceLoader {}. ", resourceLoader);
	}

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		logger.info("TestSpringLifeCycleBean has ApplicationEventPublisher {}. ", applicationEventPublisher);
	}

	@Override
	public void setMessageSource(MessageSource messageSource) {
		logger.info("TestSpringLifeCycleBean has MessageSource {}. ", messageSource);
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		logger.info("TestSpringLifeCycleBean has ApplicationContext {}. ", applicationContext);
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		logger.info("TestSpringLifeCycleBean has ServletContext {}. ", servletContext);
	}
	
	
	//以下的几个set的Aware方法没有执行
	@Override
	public void setServletConfig(ServletConfig servletConfig) {
		logger.info("TestSpringLifeCycleBean has ServletConfig {}. ", servletConfig);
	}

	@Override
	public void setNotificationPublisher(NotificationPublisher notificationPublisher) {
		logger.info("TestSpringLifeCycleBean has NotificationPublisher {}. ", notificationPublisher);
	}

	@Override
	public void setLoadTimeWeaver(LoadTimeWeaver loadTimeWeaver) {
		logger.info("TestSpringLifeCycleBean has LoadTimeWeaver {}. ", loadTimeWeaver);
	}

	@Override
	public void setImportMetadata(AnnotationMetadata importMetadata) {
		logger.info("TestSpringLifeCycleBean has AnnotationMetadata {}. ", importMetadata);
	}
	
	//bean生命周期方法
	@PostConstruct
	private void executePostConstruct() {
		logger.info("Execute @PostConstruct in spring lifecycle. ");
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		logger.info("Execute afterPropertiesSet of InitializingBean in spring lifecycle. ");
		logger.info("placeholderPropertyTest is {}, placeholderEnvTest is {}", placeholderPropertyTest, placeholderEnvTest);
		logger.info("placeholderPropertyTest is {}, placeholderEnvTest is {}", placeholderPropertyTest, placeholderEnvTest);
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
	
	/**
	 * 用法应该有误
	 * */
	@Bean(destroyMethod="executeDestroyMethod")
	protected void executeDestroyMethod() {
		logger.info("Execute destroy-method of <bean/> in spring lifecycle. ");
	}
}
