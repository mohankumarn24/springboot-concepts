package net.projectsync.springboot.concepts.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectFactory;

import net.projectsync.springboot.beanscopes.model.AuditLogger;
import net.projectsync.springboot.beanscopes.model.PrototypeScope;
import net.projectsync.springboot.beanscopes.model.RequestScope;
import net.projectsync.springboot.beanscopes.model.SessionScope;
import net.projectsync.springboot.beanscopes.service.PaymentService;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private AuditLogger auditLogger;

    @Mock
    private ObjectFactory<PrototypeScope> PrototypeFactory;

    @Mock
    private PrototypeScope prototypeScope;

    @Mock
    private RequestScope requestScope;

    @Mock
    private SessionScope sessionScope;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    @DisplayName("Prototype scope: each Transaction should be a new instance")
    void prototypeScopeTest() {
        // Stub prototype factory to return transaction mock
        Mockito.when(PrototypeFactory.getObject()).thenReturn(prototypeScope);

        // Call transfer twice
        paymentService.transfer("A", "B", 100);
        paymentService.transfer("C", "D", 200);

        // Verify factory called twice, process called twice
        Mockito.verify(PrototypeFactory, Mockito.times(2)).getObject();
        Mockito.verify(prototypeScope, Mockito.times(2)).process(Mockito.anyString(), Mockito.anyString(), Mockito.anyDouble());
    }

    @Test
    @DisplayName("Request scope: each request should get a different RequestTracker")
    void requestScopeTest() {
        // Stub RequestTracker to simulate two different requests
        Mockito.when(requestScope.getRequestUUID())
                .thenReturn("request-id-1")
                .thenReturn("request-id-2");

        String id1 = paymentService.getRequestUUID();
        String id2 = paymentService.getRequestUUID();

        Assertions.assertThat(id1).isNotEqualTo(id2);
    }

    @Test
    @DisplayName("Session scope: same session should return same SessionTracker")
    void sessionScopeSameSessionTest() {
        // Stub SessionTracker to simulate same session
        Mockito.when(sessionScope.getSessionUUID()).thenReturn("same-session-id");

        String id1 = paymentService.getSessionUUID();
        String id2 = paymentService.getSessionUUID();

        Assertions.assertThat(id1).isEqualTo(id2);
    }

    @Test
    @DisplayName("Session scope: different sessions should return different SessionTracker")
    void sessionScopeDifferentSessionsTest() {
        // Stub SessionTracker to simulate two different sessions
        Mockito.when(sessionScope.getSessionUUID())
                .thenReturn("session-id-1")
                .thenReturn("session-id-2");

        String id1 = paymentService.getSessionUUID();
        String id2 = paymentService.getSessionUUID();

        Assertions.assertThat(id1).isNotEqualTo(id2);
    }
}
