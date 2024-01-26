package no.ding.pk.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "HomeController", description = "Home controller for health check")
@RestController
@RequestMapping("/")
public class HomeController {

    /**
     * Health check for the applicaiton.
     * @return OK if the application is reachable.
     */
    @Operation(summary = "Check application health", tags = { "HomeController"})
    @GetMapping("/health")
    public String healthCheck() {
        return "OK!";
    }

    @Operation(summary = "Check SSL protocol", tags = { "HomeController"})
    @GetMapping(value = "/ssl-test")
    public String inbound(){
        return "Inbound TLS is working!!";
    }
}
