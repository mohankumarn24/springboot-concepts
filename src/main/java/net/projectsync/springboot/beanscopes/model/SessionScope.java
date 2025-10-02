package net.projectsync.springboot.beanscopes.model;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SessionScope {

    private final String sessionUUID = java.util.UUID.randomUUID().toString();

    public String getSessionUUID() {
        return sessionUUID;
    }

    public int getRealHashCode() {
    	return System.identityHashCode(this);
    }
}