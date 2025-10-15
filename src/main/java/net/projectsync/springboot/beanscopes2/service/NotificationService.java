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


/*
| Use case                                                                   | Method                         |
| -------------------------------------------------------------------------- | ------------------------------ |
| Normal object equality (e.g. in HashMap, HashSet)                          | `obj.hashCode()`               |
| You need to differentiate instances regardless of equals/hashCode override | `System.identityHashCode(obj)` |
| You’re debugging or want to print a unique identifier                      | `System.identityHashCode(obj)` |


Think of it like:
 - obj.hashCode() 				= logical identity
 - System.identityHashCode(obj) = physical identity
*/