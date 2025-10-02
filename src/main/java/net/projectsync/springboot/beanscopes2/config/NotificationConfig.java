package net.projectsync.springboot.beanscopes2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

import net.projectsync.springboot.beanscopes2.service.NotificationService;

@Configuration
public class NotificationConfig {

	// One instance per Spring ApplicationContext (default).
	@Bean
	@Scope("singleton")
	public NotificationService notificationSingleton() {
		return new NotificationService("singleton");
	}

	// New instance every time it’s requested from the context.
	@Bean
	@Scope("prototype")
	public NotificationService notificationPrototype() {
		return new NotificationService("prototype");
	}

	/*
	 * I am injecting Request scoped bean 'notificationRequest' inside Singleton scoped bean 'BeanScopeController'. So, inject a singleton proxy instead of the real bean
	 * 
	 * Explanation: Why we use proxyMode = ScopedProxyMode.TARGET_CLASS
	 * 
	 * 1️. Why you need it
	 * 	- Request and session beans only exist during an active HTTP request or session.
	 * 	- Singleton beans (like @RestController, @Service) are created at application startup, long before any HTTP request exists.
	 * 	- Injecting a request/session bean directly into a singleton causes:
	 *   		Scope 'request' is not active for the current thread
	 * 
	 * 2️. What proxyMode = ScopedProxyMode.TARGET_CLASS does
	 * 	- Spring injects a CGLIB proxy instead of the real bean.
	 * 	- When you call a method on the proxy during a request/session, it delegates to the real bean for the current HTTP request/session.
	 * 
	 * Effectively:
	 * 	[Singleton Controller/Service] --> [Proxy] --> [Real Request/Session Bean]
	 * 	- The proxy is singleton; the bean it delegates to is scoped per request/session.
	 * 
	 * 3️. How to use it
	 * 	@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
	 * 	
	 * 4️. What to expect
	 * 	- /request endpoint: Each HTTP request gets a new bean instance. Proxy stays the same.
	 * 	- /session endpoint: Same bean instance within the same session. Proxy stays the same.
	 * 
	 * 💡 Tip:
	 * 	- ScopedProxyMode.INTERFACES is an alternative if your bean implements an interface.
	 * 	- TARGET_CLASS works for concrete classes and is usually simpler in Spring Boot.
	*/
	// One instance per HTTP request (web-aware scope).
	@Bean
	@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
	public NotificationService notificationRequest() {
		return new NotificationService("request");
	}

	// One instance per HTTP session (web-aware scope).
	@Bean
	@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
	public NotificationService notificationSession() {
		return new NotificationService("session");
	}
	
	// One instance per ServletContext (i.e., per web application), shared across all sessions and requests
	// It’s like a global singleton but scoped to the web application, not just the Spring context.
	// application scope is only available in a Spring Web Application (servlet-based). If you try it in a plain Spring Boot app without spring-boot-starter-web, it won’t work.
	@Bean
	@Scope(value = "application", proxyMode = ScopedProxyMode.TARGET_CLASS)
	public NotificationService notificationApplication() {
	    return new NotificationService("application");
	}	
}

/** Prototype vs Request:
 * Prototype:
 *  - When you need a fresh object every time, independent of requests or sessions
 *  - Works in any Spring app, web or not
 *  - Use prototype if you want a new object every time you explicitly request it
 *  - Memory: Prototype: can explode if you call it too often.
 *  
 * Request: (web-aware) 
 *  - Bean instance is created once per HTTP request
 *  - Works only in a web application
 *  - Use request if you want one object per HTTP request, automatically managed by Spring
 *  - Memory: only lives for the lifetime of a request, so safe for per-request data.
 *  - Spring uses a proxy if you inject a request-scoped bean into a singleton so that each HTTP request gets the correct instance
 *  - When you need per-request state, for example:
 *   	- Tracking request-specific info (like headers, parameters, user info).
 *   	- Logging objects that track request lifecycle.
 *   	- Temporary objects that are only valid for the duration of the HTTP request.
 *   
 *   
 * Singleton vs Application:
 * Singleton:
 *  - Definition: One instance per Spring ApplicationContext. (In Spring Boot there is only one application context)
 *  - Scope of sharing: Shared across the entire Spring application, regardless of requests or sessions.
 *  - Web app vs non-web app: Works in both. Doesn’t care about HTTP requests.
 *  - Lifecycle: Lives as long as the ApplicationContext lives (usually the whole app).
 *  - Multiple users, multiple requests → same bean instance.
 *  - No need for a web environment.
 *  
 *  Application Scope (web-aware singleton):
 *   - Definition: One instance per ServletContext.
 *   - Scope of sharing: Shared across all HTTP requests and all sessions for that web application.
 *   - Web app only: Must be a web application (servlet-based).
 *   - Lifecycle: Lives as long as the web app (ServletContext) is active.
 *   - Multiple users, multiple requests → same bean instance, just like singleton.
 *   - But you cannot use this outside a web app, unlike singleton
 */

/*
| Scope           | Instance Per                                                          | Requires Web app? | Inject into Singleton?            | Lifecycle / Management                                                 | When to Use                                                 | UUID / Hashcode Behavior                                | Notes                                                        |
| --------------- | --------------------------------------------------------------------- | ----------------- | --------------------------------- | ---------------------------------------------------------------------- | ----------------------------------------------------------- | ------------------------------------------------------- | ------------------------------------------------------------ |
| **singleton**   | Spring **ApplicationContext**                                         | No                | Directly                          | Spring creates once; lives as long as the context                      | Shared services, DAOs, config objects                       | Same across app                                         | Works in web and non-web apps                                |
| **prototype**   | Every request to Spring (`getBean()` or `ObjectProvider.getObject()`) | No                | Use `ObjectProvider` or `@Lookup` | Spring creates new instance each time; does **not** manage destruction | Temporary objects, DTOs, helpers, calculations              | Different every request                                 | Lifecycle not managed; no `@PreDestroy` called automatically |
| **request**     | One per **HTTP request**                                              | Yes               | Use `proxyMode = TARGET_CLASS`    | Spring creates per request via proxy; destroyed after request          | Request-specific state, logging, per-request temporary data | Same within one HTTP request; different across requests | Only works in web apps                                       |
| **session**     | One per **HTTP session**                                              | Yes               | Use `proxyMode = TARGET_CLASS`    | Spring creates per session via proxy; destroyed after session ends     | User session-specific data (shopping cart, login info)      | Same within one session; different across sessions      | Only works in web apps                                       |
| **application** | One per **ServletContext** (web app)                                  | Yes               | Use `proxyMode = TARGET_CLASS`    | Spring creates per web app; destroyed when web app stops               | Global web app state, shared caches                         | Same across all requests and sessions                   | Web-global singleton; only works in web apps                 |
*/



/*
Use case:
1. Prototype Scope: You want a fresh object every time because it will hold temporary state for a specific operation.
   Example: Processing tasks in a service

	@Component
	@Scope("prototype")
	public class TaskProcessor {
	    private String taskName;
	
	    public void process(String taskName) {
	        this.taskName = taskName;
	        System.out.println("Processing task: " + taskName);
	    }
	}


	@Component
	public class TaskService {
	    @Autowired
	    private ApplicationContext context;
	
	    public void runTasks() {
	        TaskProcessor task1 = context.getBean(TaskProcessor.class);
	        task1.process("Task 1");
	
	        TaskProcessor task2 = context.getBean(TaskProcessor.class);
	        task2.process("Task 2");
	    }
	}


	Why prototype?
	 - Each TaskProcessor instance is independent.
	 - State is temporary and should not be shared.
	 - Works outside of web apps too.
 
2. Request Scope: You want to store request-specific data that will last throughout the lifecycle of that request (from controller -> service -> repository) but not beyond.
   Example: Tracking user request
   
	@Component
	@Scope("request")
	public class RequestData {
	    private String userId;
	
	    public void setUserId(String userId) { this.userId = userId; }
	    public String getUserId() { return userId; }
	}
	
	@RestController
	public class UserController {
	
	    @Autowired
	    private RequestData requestData;
	
	    @GetMapping("/login")
	    public String login(@RequestParam String userId) {
	        requestData.setUserId(userId);
	        return "Request ID stored for user: " + requestData.getUserId();
	    }
	}

	Why request?
	 - If two users make requests at the same time, each has their own RequestData instance.
	 - The bean dies when the request ends.
	 - Perfect for storing request-specific info like headers, user tokens, or session-related temporary data without polluting other requests.
	 
3. Singleton Scope: Stateless services that can safely be shared across the entire Spring context.
	@Service
	public class UserService {
	    // Singleton by default
	    public void createUser(String name) {
	        System.out.println("User created: " + name);
	    }
	}

	Key point: 
	 - Singleton is per Spring container. If you have multiple contexts (rare in web apps), each context has its own singleton.   	 
	 
4. Application Scope (Web Only): Shared resources across all requests and sessions in a web application.
	@Component
	@Scope("application")
	public class AppConfig {
	    private Map<String, String> settings = new HashMap<>();
	
	    public void setSetting(String key, String value) {
	        settings.put(key, value);
	    }
	    public String getSetting(String key) {
	        return settings.get(key);
	    }
	}

	Key point: 
	 - Application-scoped beans are useful for global data in a web app like configuration, caches, or counters.
*/