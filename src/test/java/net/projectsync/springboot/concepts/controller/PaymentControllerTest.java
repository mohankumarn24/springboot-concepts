package net.projectsync.springboot.concepts.controller;

import net.projectsync.springboot.concepts.service.PaymentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class) // Only loads controller layer
public class PaymentControllerTest {

    @MockBean
    private PaymentService paymentService; // mock service, required by controller

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Prototype endpoint should create new bean each time")
    void prototypeShouldCreateNewBeanEachTime() throws Exception {
        // Mock transfer() to do nothing
        doNothing().when(paymentService).transfer(anyString(), anyString(), anyDouble());

        String response = mockMvc.perform(get("/prototype"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(response).contains("2 transactions completed");
    }

    @Test
    @DisplayName("Request endpoint should give different IDs per request")
    void requestScopeShouldGiveDifferentIdsPerRequest() throws Exception {
        // Mock getRequestInfo() to return different IDs per call
        when(paymentService.getRequestId())
                .thenReturn("request-id-1")
                .thenReturn("request-id-2");

        String id1 = mockMvc.perform(get("/request"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String id2 = mockMvc.perform(get("/request"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(id1).isNotEqualTo(id2);
    }

    @Test
    @DisplayName("Session endpoint should remain same within same session")
    void sessionScopeShouldRemainSameWithinSameSession() throws Exception {
        MockHttpSession session = new MockHttpSession();

        when(paymentService.getSessionId()).thenReturn("session-id-123");

        String id1 = mockMvc.perform(get("/session").session(session))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String id2 = mockMvc.perform(get("/session").session(session))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(id1).isEqualTo(id2);
    }

    @Test
    @DisplayName("Session endpoint should change across different sessions")
    void sessionScopeShouldChangeAcrossDifferentSessions() throws Exception {
        // Mock getSessionInfo() to return different IDs per call
        when(paymentService.getSessionId())
                .thenReturn("session-id-1")
                .thenReturn("session-id-2");

        String id1 = mockMvc.perform(get("/session"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String id2 = mockMvc.perform(get("/session"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(id1).isNotEqualTo(id2);
    }
}
