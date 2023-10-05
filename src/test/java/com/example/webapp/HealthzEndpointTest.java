package com.example.webapp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class HealthzEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testHealtzEndpoint() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/healtz"))
                .andExpect(status().isOk());
    }
}
