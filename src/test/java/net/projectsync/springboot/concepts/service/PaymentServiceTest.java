package net.projectsync.springboot.concepts.service;

import net.projectsync.springboot.concepts.model.AuditLogger;
import net.projectsync.springboot.concepts.model.RequestTracker;
import net.projectsync.springboot.concepts.model.SessionTracker;
import net.projectsync.springboot.concepts.model.Transaction;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectFactory;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private AuditLogger auditLogger;

    @Mock
    private ObjectFactory<Transaction> transactionFactory;

    @Mock
    private Transaction transaction;

    @Mock
    private RequestTracker requestTracker;

    @Mock
    private SessionTracker sessionTracker;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    @DisplayName("Prototype scope: each Transaction should be a new instance")
    void prototypeScopeTest() {
        // Stub prototype factory to return transaction mock
        Mockito.when(transactionFactory.getObject()).thenReturn(transaction);

        // Call transfer twice
        paymentService.transfer("A", "B", 100);
        paymentService.transfer("C", "D", 200);

        // Verify factory called twice, process called twice
        Mockito.verify(transactionFactory, Mockito.times(2)).getObject();
        Mockito.verify(transaction, Mockito.times(2)).process();
    }

    @Test
    @DisplayName("Request scope: each request should get a different RequestTracker")
    void requestScopeTest() {
        // Stub RequestTracker to simulate two different requests
        Mockito.when(requestTracker.getRequestId())
                .thenReturn("request-id-1")
                .thenReturn("request-id-2");

        String id1 = paymentService.getRequestId();
        String id2 = paymentService.getRequestId();

        Assertions.assertThat(id1).isNotEqualTo(id2);
    }

    @Test
    @DisplayName("Session scope: same session should return same SessionTracker")
    void sessionScopeSameSessionTest() {
        // Stub SessionTracker to simulate same session
        Mockito.when(sessionTracker.getSessionId()).thenReturn("same-session-id");

        String id1 = paymentService.getSessionId();
        String id2 = paymentService.getSessionId();

        Assertions.assertThat(id1).isEqualTo(id2);
    }

    @Test
    @DisplayName("Session scope: different sessions should return different SessionTracker")
    void sessionScopeDifferentSessionsTest() {
        // Stub SessionTracker to simulate two different sessions
        Mockito.when(sessionTracker.getSessionId())
                .thenReturn("session-id-1")
                .thenReturn("session-id-2");

        String id1 = paymentService.getSessionId();
        String id2 = paymentService.getSessionId();

        Assertions.assertThat(id1).isNotEqualTo(id2);
    }
}
