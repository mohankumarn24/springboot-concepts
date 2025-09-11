package net.projectsync.springboot.concepts.controller;

import jakarta.servlet.http.HttpServletRequest;
import net.projectsync.springboot.concepts.model.Transaction;
import net.projectsync.springboot.concepts.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/prototype")
    public String transfer() {

        paymentService.transfer("ACC1", "ACC2", 1000);
        paymentService.transfer("ACC3", "ACC4", 2000);

        return "2 transactions completed at: " + Instant.now();
    }

    @GetMapping("/request")
    public String getRequestId() {

        return "Request ID: " + paymentService.getRequestId();
    }

    @GetMapping("/session")
    public String getSessionId(HttpServletRequest request) {

        return "Session ID: " + paymentService.getSessionId() + ", JSESSIONID: " + request.getSession().getId();
    }
}

