package net.projectsync.springboot.concepts.model;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

/**
 * proxyMode = ScopedProxyMode.TARGET_CLASS
 * Meaning: “This bean is request-scoped, but if it’s injected into a bean with a longer lifecycle (like a singleton), inject a proxy instead of the real bean.”
 * 
 * Spring creates a new instance of RequestTracker for each HTTP request.
 * Every instance has its own requestUUID because below line is executed once per bean instance, i.e., once per HTTP request.
 * 	 private final String requestUUID = java.util.UUID.randomUUID().toString();
 *   This line executes once per bean instance, i.e., once per HTTP request.
 */
@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class RequestScope {

    private final String requestUUID = java.util.UUID.randomUUID().toString();

    public String getRequestUUID() {
        return requestUUID;
    }

    public int getRealHashCode() {
        // System.out.println(this.hashCode() == System.identityHashCode(this)); // true
        return System.identityHashCode(this);	// UUID of current request’s real bean
    }
}
