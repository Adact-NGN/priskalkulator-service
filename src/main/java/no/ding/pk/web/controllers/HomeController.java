package no.ding.pk.web.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class HomeController {

    /**
     * Health check for the applicaiton.
     * @return OK if the application is reachable.
     */
    @GetMapping("/health")
    public String healthCheck() {
        return "OK!";
    }

    @GetMapping(value = "/ssl-test")
    public String inbound(){
        return "Inbound TLS is working!!";
    }
}
