package net.projectsync.springboot.beanscopes2.service;

import java.util.Map;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class BeanScopeService {

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

	@Autowired
	@Qualifier("notificationApplication")
	private NotificationService notificationApplication;
	
	public Map<String, Object> singletonScope() {
		return Map.of(
				"scope", notificationSingleton.getScopeName(),
				"uuid", notificationSingleton.getUUID(), 
				"hashcode", notificationSingleton.getRealHashCode());
	}

	public Map<String, Object> prototypeScope() {
		NotificationService notificationPrototype = notificationPrototypeProvider.getObject();
		return Map.of(
				"scope", notificationPrototype.getScopeName(),
				"uuid", notificationPrototype.getUUID(), 
				"hashcode", notificationPrototype.getRealHashCode());
	}

	public Map<String, Object> requestScope() {
		return Map.of(
				"scope", notificationRequest.getScopeName(),
				"uuid", notificationRequest.getUUID(), 
				"hashcode", notificationRequest.getRealHashCode());
	}

	public Map<String, Object> sessionScope() {
		return Map.of(
				"scope", notificationSession.getScopeName(),
				"uuid", notificationSession.getUUID(), 
				"hashcode", notificationSession.getRealHashCode());
	}

	public Map<String, Object> applicationScope() {
		return Map.of(
				"scope", notificationApplication.getScopeName(),
				"uuid", notificationApplication.getUUID(), 
				"hashcode", notificationApplication.getRealHashCode());
	}
}
