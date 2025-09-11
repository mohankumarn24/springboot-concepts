package net.projectsync.springboot.concepts.service;

import net.projectsync.springboot.concepts.model.AuditLogger;
import net.projectsync.springboot.concepts.model.RequestTracker;
import net.projectsync.springboot.concepts.model.SessionTracker;
import net.projectsync.springboot.concepts.model.Transaction;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    // Singleton
    // AuditLogger is always the same, consistent across the whole app
    @Autowired
    private AuditLogger auditLogger;

    // prototype --> incorrect way
    @Autowired
    private Transaction directTransaction;  // this will be fixed object, not new each time. Use

    // Prototype (via factory, so we get fresh instances)
    // Transaction is new on every call, even within the same HTTP request
    @Autowired
    private ObjectFactory<Transaction> transactionFactory;

    // Request-scoped bean (proxy injected)
    // RequestTracker is stable only for the lifetime of a single request, then swapped out.
    @Autowired
    private RequestTracker requestTracker;

    // Session-scoped bean (proxy injected)
    // Same bean across multiple requests, as long as it’s the same session
    @Autowired
    private SessionTracker sessionTracker;

    public void transfer(String fromAcc, String toAcc, double amount) {

        System.out.println();
        // New Transaction object for each transfer
        Transaction transaction = transactionFactory.getObject();
        auditLogger.log("Singleton Bean hash code: " + auditLogger.hashCode());
        auditLogger.log("Prototype Bean hash code: " + transaction.hashCode());
        transaction.setFromAccount(fromAcc);
        transaction.setToAccount(toAcc);
        transaction.setAmount(amount);
        transaction.process();

        /* Output:
           AUDIT: Singleton Bean hash code: 1303547057
           AUDIT: Prototype Bean hash code: 2075170189
           AUDIT: Processing transaction: 1000.0 from ACC1 to ACC2

           AUDIT: Singleton Bean hash code: 1303547057
           AUDIT: Prototype Bean hash code: 915280392
           AUDIT: Processing transaction: 2000.0 from ACC3 to ACC4
        */
    }

    public String getRequestId() {

        System.out.println();
        String requestId = requestTracker.getRequestId();
        auditLogger.log("Request bean proxy hashCode: " + requestTracker.hashCode());
        auditLogger.log("Request bean real hashCode: " + requestId.toString()); // Instead of logging hashCode() on the proxy, log something from inside the bean itself to “see” the real bean hashcode
        auditLogger.log("Request ID: " + requestId);

        return requestId;

        /*
        // Same proxy object each time, but underneath it fetches a new real bean with a new ID per request.
        AUDIT: Request bean proxy hashCode: 142869248
        AUDIT: Request bean real hashCode: 1849b675-c036-45e4-9db6-b1c6eab4ea9f
        AUDIT: Request ID: 1849b675-c036-45e4-9db6-b1c6eab4ea9f

        AUDIT: Request bean proxy hashCode: 142869248
        AUDIT: Request bean real hashCode: 7ae17685-c5cd-493c-bcac-99ec7db4c148
        AUDIT: Request ID: 7ae17685-c5cd-493c-bcac-99ec7db4c148
        */
    }

    public String getSessionId() {

        System.out.println();
        String sessionId = sessionTracker.getSessionId();
        auditLogger.log("Session bean proxy hashCode: " + sessionTracker.hashCode());
        auditLogger.log("Session bean real hashCode : " + sessionTracker.toString());
        auditLogger.log("Session ID: " + sessionId);
        return sessionId;

        /*
        Browser Tab 1:
        AUDIT: Session bean proxy hashCode: 1287202254
        AUDIT: Session bean real hashCode : 1ac5adef-4006-47e4-a29f-5ff819b3581a
        AUDIT: Session ID: 1ac5adef-4006-47e4-a29f-5ff819b3581a

        Browser Tab 1:
        AUDIT: Session bean proxy hashCode: 1287202254
        AUDIT: Session bean real hashCode : 1ac5adef-4006-47e4-a29f-5ff819b3581a
        AUDIT: Session ID: 1ac5adef-4006-47e4-a29f-5ff819b3581a

        Browser Tab 2 in incognito mode:
        AUDIT: Session bean proxy hashCode: 1287202254
        AUDIT: Session bean real hashCode : 0546eebe-1506-42a7-89c4-8cdaa2b954e1
        AUDIT: Session ID: 0546eebe-1506-42a7-89c4-8cdaa2b954e1
        */

        /*
         * Notes:
         *  - Session bean proxy hashCode: 1287202254 → The proxy object Spring injected into your singleton service.
         *      This will always be the same because the proxy itself is a singleton.
         *  - Session bean real hashCode : 1ac5adef-4006-47e4-a29f-5ff819b3581a → This is the actual underlying session-scoped bean instance,
         *      tied to your current HTTP session.
         *  - Session ID: ... → Same as the bean’s internal UUID, proving the bean sticks around as long as the session does.
         */
    }
}

/* Output:
Initiating transfer...
Prototype Bean hash code: 1237416868
Processing transaction: 1000.0 from ACC1 to ACC2
Initiating transfer...
Prototype Bean hash code: 2050907347
Processing transaction: 2000.0 from ACC3 to ACC4
 */


/*
| Aspect           | **Prototype Scope**                                                                                         | **Request Scope**                                                                               |
| ---------------- | ----------------------------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------- |
| **Lifecycle**    | New bean every time you *explicitly ask Spring for it* (via `getBean()`, `ObjectFactory`, `Provider`, etc.) | New bean automatically created *once per HTTP request* and destroyed at the end of that request |
| **Owner**        | You (the developer) control when to fetch a new instance                                                    | Spring Web context manages it for each incoming request                                         |
| **Use case**     | Independent objects, often created on demand inside services (e.g., `Invoice`, `Transaction`)               | Request-specific state (e.g., `ShoppingCart`, `RequestContext`, `UserSession`)                  |
| **Availability** | Works in **any Spring context** (core, desktop, batch, etc.)                                                | Works **only in a web-aware Spring context** (Spring MVC / WebFlux)                             |
| **Destruction**  | Spring doesn’t manage lifecycle beyond creation (you must clean up if needed)                               | Destroyed automatically at the end of HTTP request                                              |
*/