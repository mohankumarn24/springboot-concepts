package net.projectsync.springboot.beanscopes2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

import net.projectsync.springboot.beanscopes2.service.NotificationService;

@Configuration
public class NotificationConfig {

	@Bean
	@Scope("singleton")
	public NotificationService notificationSingleton() {
		return new NotificationService("singleton");
	}

	@Bean
	@Scope("prototype")
	public NotificationService notificationPrototype() {
		return new NotificationService("prototype");
	}

	/**
	 * I am injecting Request scoped bean 'notificationRequest' inside Singleton scoped bean 'BeanScopeController'. So, inject a singleton proxy instead of the real bean
	 */
	/*
	Explanation: Why we use proxyMode = ScopedProxyMode.TARGET_CLASS

	1️. Why you need it
		- Request and session beans only exist during an active HTTP request or session.
		- Singleton beans (like @RestController) are created at application startup, long before any HTTP request exists.
		- Injecting a request/session bean directly into a singleton causes:
	  		Scope 'request' is not active for the current thread

	2️. What proxyMode = ScopedProxyMode.TARGET_CLASS does
		- Spring injects a CGLIB proxy instead of the real bean.
		- When you call a method on the proxy during a request/session, it delegates to the real bean for the current HTTP request/session.

	Effectively:
		[Singleton Controller] --> [Proxy] --> [Real Request/Session Bean]
		- The proxy is singleton; the bean it delegates to is scoped per request/session.

	3️. How to use it
		@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
		
	4️. What to expect
		- /request endpoint: Each HTTP request gets a new bean instance. Proxy stays the same.
		- /session endpoint: Same bean instance within the same session. Proxy stays the same.

	💡 Tip:
		- ScopedProxyMode.INTERFACES is an alternative if your bean implements an interface.
		- TARGET_CLASS works for concrete classes and is usually simpler in Spring Boot.
	*/	
	@Bean
	@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
	public NotificationService notificationRequest() {
		return new NotificationService("request");
	}

	@Bean
	@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
	public NotificationService notificationSession() {
		return new NotificationService("session");
	}
}
