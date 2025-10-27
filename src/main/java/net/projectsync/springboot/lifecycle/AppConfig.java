package net.projectsync.springboot.lifecycle;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
class AppConfig {

	// Purpose: Intercepts all beans during creation, allowing you to
	// 		- Modify properties before initialization (postProcessBeforeInitialization)
	//		- Modify or wrap beans after initialization (postProcessAfterInitialization)
	//	Static @Bean method: Declaring it static ensures it’s created very early, before any non-static @Beans — useful for certain lifecycle needs.
	@Bean
	public static MyBeanPostProcessor beanPostProcessor() {
		return new MyBeanPostProcessor();
	}
	
	// Scope: singleton (default in Spring) → Spring creates one instance per container
	// Lifecycle hooks:
	// 		- initMethod = "customInit" → Spring calls customInit() after properties are set.
	// 		- destroyMethod = "customDestroy" → Spring calls customDestroy() when the context shuts down
	@Bean(initMethod = "customInit", destroyMethod = "customDestroy")
	@Scope("singleton")
	public MyBean singletonBean() {
		return new MyBean();
	}

	// Scope: prototype → Spring creates a new instance every time you request it (via getBean())
	// Important: 
	// 		- Spring does not manage the full lifecycle of prototype beans. 
	//		- initMethod is called, but destroyMethod is not called automatically; the caller is responsible for cleanup.
	@Bean
	@Scope("prototype")
	public MyBean prototypeBean() {
		return new MyBean();
	}
}

/*
| Bean Type | Init Method        | Destroy Method          | BeanPostProcessor Invoked? |
| --------- | ------------------ | ----------------------- | -------------------------- |
| Singleton | Yes (`customInit`) | Yes (`customDestroy`)   | Yes (before & after init)  |
| Prototype | Yes (`customInit`) | No (caller responsible) | Yes (before & after init)  |
*/
