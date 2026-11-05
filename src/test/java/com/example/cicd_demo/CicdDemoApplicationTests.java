
package com.example.cicd_demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;

import org.springframework.http.MediaType;
@SpringBootTest
@AutoConfigureMockMvc
class CicdDemoApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void homeEndpointTest() throws Exception {
        mockMvc.perform(get("/"))
            .andExpect(status().isOk());
    }

    @Test
    void statusEndpointTest() throws Exception {

        mockMvc.perform(get("/api/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.application")
                .value("Calculator App"))
                .andExpect(jsonPath("$.status")
                .value("Running"));
    }
}
