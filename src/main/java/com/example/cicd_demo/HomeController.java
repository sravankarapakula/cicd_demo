package com.example.cicd_demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/api/status")
    public Map<String, String> status() {
        return Map.of(
            "version", "1.0",
            "status", "Running",
            "application", "Calculator App"
        );
    }
}