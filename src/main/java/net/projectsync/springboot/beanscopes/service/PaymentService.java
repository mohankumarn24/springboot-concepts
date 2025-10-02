package net.projectsync.springboot.beanscopes.service;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.projectsync.springboot.beanscopes.model.AuditLogger;
import net.projectsync.springboot.beanscopes.model.PrototypeScope;
import net.projectsync.springboot.beanscopes.model.RequestScope;
import net.projectsync.springboot.beanscopes.model.SessionScope;

@Service
public class PaymentService {

    // Singleton
    // AuditLogger is always the same, consistent across the whole app
    @Autowired
    private AuditLogger auditLogger;

    // prototype --> incorrect way
    @Autowired
    private PrototypeScope prototypeScope;  // this will be fixed object, not new each time. Use

    // Prototype (via factory, so we get fresh instances)
    // Transaction is new on every call, even within the same HTTP request
    @Autowired
    private ObjectFactory<PrototypeScope> prototypeFactory;

    // Request-scoped bean (proxy injected)
    // RequestTracker is stable only for the lifetime of a single request, then swapped out.
    @Autowired
    private RequestScope requestScope;

    // Session-scoped bean (proxy injected)
    // Same bean across multiple requests, as long as it’s the same session
    @Autowired
    private SessionScope sessionScope;

    public void transfer(String fromAcc, String toAcc, double amount) {

        System.out.println();
        PrototypeScope prototypeScope = prototypeFactory.getObject();
        auditLogger.log("Singleton Bean hash code: " + auditLogger.hashCode());
        auditLogger.log("Prototype Bean hash code: " + prototypeScope.hashCode());
        prototypeScope.process(fromAcc, toAcc, amount);

        /* Output:
        AUDIT: Singleton Bean hash code: 760925533
        AUDIT: Prototype Bean hash code: 991305095
        AUDIT: Processing transaction: 1000.0 from ACC1 to ACC2

        AUDIT: Singleton Bean hash code: 760925533
        AUDIT: Prototype Bean hash code: 1792308514
        AUDIT: Processing transaction: 2000.0 from ACC3 to ACC4
        */
    }

    public String getRequestUUID() {

        System.out.println();
        auditLogger.log("Request bean proxy hashCode: " + requestScope.hashCode());
        auditLogger.log("Request bean real hashCode: "  + requestScope.getRealHashCode()); // Instead of logging hashCode() on the proxy, log something from inside the bean itself to “see” the real bean hashcode
        
        String requestUUID = requestScope.getRequestUUID();
        auditLogger.log("Request UUID: " + requestUUID);
        return requestUUID;

        /*
        // Same proxy object each time, but underneath it fetches a new real bean with a new ID per request.
        AUDIT: Request bean proxy hashCode: 71352732
        AUDIT: Request bean real hashCode: 1659601019
        AUDIT: Request UUID: 59e74712-c955-452e-b8a0-0e9db594f441

        AUDIT: Request bean proxy hashCode: 71352732
        AUDIT: Request bean real hashCode: 1584430440
        AUDIT: Request UUID: 2525e1e7-aaec-40e0-9393-013c4c60e167
        */
    }

    public String getSessionUUID() {

        System.out.println();
        auditLogger.log("Session bean proxy hashCode: " + sessionScope.hashCode());
        auditLogger.log("Session bean real hashCode : " + sessionScope.getRealHashCode());
        
        String sessionUUID = sessionScope.getSessionUUID();
        auditLogger.log("Session UUID: " + sessionUUID);
        return sessionUUID;

        /*
        Browser Tab 1:
        AUDIT: Session bean proxy hashCode: -1918165515
        AUDIT: Session bean real hashCode : 1418358366
        AUDIT: Session UUID: f096d7ae-dce0-49a4-b6e0-9712ddd7d617

        Browser Tab 2:
        AUDIT: Session bean proxy hashCode: -1918165515
        AUDIT: Session bean real hashCode : 1418358366
        AUDIT: Session UUID: f096d7ae-dce0-49a4-b6e0-9712ddd7d617

        Browser Tab 1 in incognito mode:
        AUDIT: Session bean proxy hashCode: -1918165515
        AUDIT: Session bean real hashCode : 1072325543
        AUDIT: Session UUID: f45ce9fc-87c7-4acf-8937-a8208b7376f6
        */
    }
}

/*
| Aspect           | **Prototype Scope**                                                                                         | **Request Scope**                                                                               |
| ---------------- | ----------------------------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------- |
| **Lifecycle**    | New bean every time you *explicitly ask Spring for it* (via `getBean()`, `ObjectFactory`, `Provider`, etc.) | New bean automatically created *once per HTTP request* and destroyed at the end of that request |
| **Owner**        | You (the developer) control when to fetch a new instance                                                    | Spring Web context manages it for each incoming request                                         |
| **Use case**     | Independent objects, often created on demand inside services (e.g., `Invoice`, `Transaction`)               | Request-specific state (e.g., `ShoppingCart`, `RequestContext`, `UserSession`)                  |
| **Availability** | Works in **any Spring context** (core, desktop, batch, etc.)                                                | Works **only in a web-aware Spring context** (Spring MVC / WebFlux)                             |
| **Destruction**  | Spring doesn’t manage lifecycle beyond creation (you must clean up if needed)                               | Destroyed automatically at the end of HTTP request                                              |
*/