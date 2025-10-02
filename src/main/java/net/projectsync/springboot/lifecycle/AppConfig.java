package net.projectsync.springboot.lifecycle;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
class AppConfig {

	@Bean
	public static MyBeanPostProcessor beanPostProcessor() {
		return new MyBeanPostProcessor();
	}

	@Bean(initMethod = "customInit", destroyMethod = "customDestroy")
	@Scope("singleton")
	public MyBean singletonBean() {
		return new MyBean();
	}

	@Bean
	@Scope("prototype")
	public MyBean prototypeBean() {
		return new MyBean();
	}
}
