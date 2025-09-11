package net.projectsync.springboot.concepts.model;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SessionTracker {

    private final String sessionId = UUID.randomUUID().toString();

    public String getSessionId() {
        return sessionId;
    }

    @Override
    public String toString() {
        return sessionId;
    }
}

