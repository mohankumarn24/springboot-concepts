package net.projectsync.springboot.concepts.model;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class Transaction {

    // Spring creates a new Transaction each time (prototype).
    // Inside that Transaction, Spring wires in the same singleton AuditLogger.
    @Autowired
    private AuditLogger auditLogger; // singleton injected inside prototype

    private String fromAccount;
    private String toAccount;
    private double amount;

    public void setFromAccount(String fromAccount) {
        this.fromAccount = fromAccount;
    }

    public void setToAccount(String toAccount) {
        this.toAccount = toAccount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void process() {
        auditLogger.log("Processing transaction: " + amount + " from " + fromAccount + " to " + toAccount);
    }
}
