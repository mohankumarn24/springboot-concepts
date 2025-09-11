package net.projectsync.springboot.concepts.model;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class RequestTracker {

    private final String requestId = java.util.UUID.randomUUID().toString();

    public String getRequestId() {
        return requestId;
    }

    @Override
    public String toString() {
        return requestId;
    }
}

// proxyMode = ScopedProxyMode.TARGET_CLASS
// “Hey, when I inject a request-scoped bean into a singleton, don’t try to create it at startup.
// Instead, inject a proxy that will fetch the right instance for the current HTTP request.”
