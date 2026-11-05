
package com.example.cicd_demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, String> home() {

        return Map.of(
            "message",
            "Welcome to CI/CD Demo Application"
        );
    }

    @GetMapping("/api/status")
    public Map<String, String> status() {

        return Map.of(
            "application",
            "2nd deployment",
            "status",
            "Running",
            "version",
            "1.0"
        );
    }
}
