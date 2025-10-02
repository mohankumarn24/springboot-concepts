package net.projectsync.springboot.beanscopes2.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import net.projectsync.springboot.beanscopes2.service.BeanScopeService;

@RestController
public class BeanScopeController {

	@Autowired
	private BeanScopeService beanScopeService;

	@GetMapping("/singleton")
	public Map<String, Object> singletonScope() {
		return beanScopeService.singletonScope();

	}

	@GetMapping("/prototype")
	public Map<String, Object> prototypeScope() {
		return beanScopeService.prototypeScope();
	}

	@GetMapping("/request")
	public Map<String, Object> requestScope() {
		return beanScopeService.requestScope();
	}

	@GetMapping("/session")
	public Map<String, Object> sessionScope() {
		return beanScopeService.sessionScope();
	}
}


/*
	- Can we use AnnotationConfigApplicationContext? It will not work for Request and Session scopes beans as it is web-aware context
	- Key Takeaways
		- Singleton: Only one instance in the context → UUID/hashCode always same.
		- Prototype: New instance every time getBean() is called → UUID changes.
		- Request/Session: Cannot be demonstrated with AnnotationConfigApplicationContext; require web-aware context.
		- To simulate web scopes, you need Spring Boot with @RestController or a WebApplicationContext.
		- Using WebApplicationContext is automatic in Spring Boot when you run the application as a web app (@RestController)
*/

/*
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class BeanScopeDemo {

    public static void main(String[] args) {
    
        AnnotationConfigApplicationContext context =new AnnotationConfigApplicationContext(NotificationConfig.class);

        // Singleton demonstration
        NotificationService singleton1 = context.getBean("notificationSingleton", NotificationService.class);
        NotificationService singleton2 = context.getBean("notificationSingleton", NotificationService.class);

        System.out.println("Singleton 1 UUID: " + singleton1.getInstanceId());
        System.out.println("Singleton 2 UUID: " + singleton2.getInstanceId()); // same as singleton1

        // Prototype demonstration
        NotificationService prototype1 = context.getBean("notificationPrototype", NotificationService.class);
        NotificationService prototype2 = context.getBean("notificationPrototype", NotificationService.class);

        System.out.println("Prototype 1 UUID: " + prototype1.getInstanceId());
        System.out.println("Prototype 2 UUID: " + prototype2.getInstanceId()); // different from prototype1

        // Optional: simulate multiple prototype calls
        for (int i = 0; i < 3; i++) {
            NotificationService proto = context.getBean("notificationPrototype", NotificationService.class);
            System.out.println("Call " + i + " Prototype UUID: " + proto.getInstanceId());
        }

        context.close();
    }
}
*/
