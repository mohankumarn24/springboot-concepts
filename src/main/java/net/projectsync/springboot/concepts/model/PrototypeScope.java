package net.projectsync.springboot.concepts.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class PrototypeScope {

    // Spring creates a new Transaction each time (prototype).
    // Inside that Transaction, Spring wires in the same singleton AuditLogger.
    @Autowired
    private AuditLogger auditLogger; // singleton injected inside prototype

    public void process(String fromAccount, String toAccount, double amount) {
        auditLogger.log("Processing transaction: " + amount + " from " + fromAccount + " to " + toAccount);
    }
}
