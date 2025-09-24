package net.projectsync.springboot.concepts.controller;

import jakarta.servlet.http.HttpServletRequest;
import net.projectsync.springboot.concepts.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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
    public String getrequestUUID() {

        return "Request UUID: " + paymentService.getRequestUUID();
    }

    @GetMapping("/session")
    public String getSessionId(HttpServletRequest request) {

    	// logs are printed twice for second request and onwards. Reason:
    	// Logging happens twice because Spring MVC may internally call the method again during response processing (proxy forwards each call to the same bean).
    	// Duplicate logs do not indicate a new session bean.
        return "Session UUID: " + paymentService.getSessionUUID() + ", JSESSIONID: " + request.getSession().getId();
    }
}

