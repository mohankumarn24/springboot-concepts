package net.projectsync.springboot.beanscopes2.controller;

import java.util.Map;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import net.projectsync.springboot.beanscopes2.service.NotificationService;

@RestController
public class BeanScopeController {

	@Autowired
	@Qualifier("notificationSingleton")
	private NotificationService notificationSingleton;

	@Autowired
	@Qualifier("notificationPrototype")
	private ObjectProvider<NotificationService> notificationPrototypeProvider;

	@Autowired
	@Qualifier("notificationRequest")
	private NotificationService notificationRequest;

	@Autowired
	@Qualifier("notificationSession")
	private NotificationService notificationSession;

	@GetMapping("/singleton")
	public Map<String, Object> singletonScope() {
		return Map.of("uuid", notificationSingleton.getUUID(), "hashcode", notificationSingleton.getRealHashCode());

	}

	@GetMapping("/prototype")
	public Map<String, Object> prototypeScope() {
		NotificationService notificationPrototype = notificationPrototypeProvider.getObject();
		return Map.of("uuid", notificationPrototype.getUUID(), "hashcode", notificationPrototype.getRealHashCode());
	}

	@GetMapping("/request")
	public Map<String, Object> requestScope() {
		return Map.of("uuid", notificationRequest.getUUID(), "hashcode", notificationRequest.getRealHashCode());
	}

	@GetMapping("/session")
	public Map<String, Object> sessionScope() {
		return Map.of("uuid", notificationSession.getUUID(), "hashcode", notificationSession.getRealHashCode());
	}
}
