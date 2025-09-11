package net.projectsync.springboot.concepts.model;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton") // default, but explicit for clarity
public class AuditLogger {

    public void log(String message) {
        System.out.println("AUDIT: " + message);
    }
}
