package net.projectsync.springboot.beanscopes2.service;

import java.util.UUID;

public class NotificationService {

	private final String scopeName;
	private final String uuid;

	public NotificationService(String scopeName) {
		this.scopeName = scopeName;
		this.uuid = UUID.randomUUID().toString(); // unique per instance
	}

	public String getUUID() {
		return uuid;
	}

	public String getScopeName() {
		return scopeName;
	}
	
    public int getRealHashCode() {
    	return System.identityHashCode(this);
    }
}
