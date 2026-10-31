
package com.example.cicd_demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CicdDemoApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void homeEndpointTest() throws Exception {

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                .value("Welcome to CI/CD Demo Application"));
    }

    @Test
    void statusEndpointTest() throws Exception {

        mockMvc.perform(get("/api/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.application")
                .value("CICD-Demo"))
                .andExpect(jsonPath("$.status")
                .value("Running"));
    }
}
