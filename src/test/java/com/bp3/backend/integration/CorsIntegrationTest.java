package com.bp3.backend.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for CORS configuration.
 * 
 * <p>These tests verify that the backend properly handles CORS requests
 * from the Angular frontend running on localhost:4200.</p>
 */
@SpringBootTest
@AutoConfigureWebMvc
class CorsIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Test
    void testCorsPreflightRequest() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        mockMvc.perform(options("/api/diagramprocess/reduce")
                .header("Origin", "http://localhost:4200")
                .header("Access-Control-Request-Method", "POST")
                .header("Access-Control-Request-Headers", "Content-Type"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:4200"))
                .andExpect(header().string("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS,HEAD,TRACE,CONNECT"))
                .andExpect(header().string("Access-Control-Allow-Headers", "Content-Type"))
                .andExpect(header().string("Access-Control-Allow-Credentials", "true"))
                .andExpect(header().string("Access-Control-Max-Age", "3600"));
    }

    @Test
    void testCorsActualRequest() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        String inputJson = """
            {
              "nodes": [
                { "id": "0", "name": "Start", "type": "Start" },
                { "id": "1", "name": "A", "type": "ServiceTask" },
                { "id": "2", "name": "B", "type": "HumanTask" },
                { "id": "3", "name": "End", "type": "End" }
              ],
              "edges": [
                { "from": "0", "to": "1" },
                { "from": "1", "to": "2" },
                { "from": "2", "to": "3" }
              ]
            }
            """;

        mockMvc.perform(post("/api/diagramprocess/reduce")
                .header("Origin", "http://localhost:4200")
                .contentType(MediaType.APPLICATION_JSON)
                .content(inputJson))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:4200"))
                .andExpect(header().string("Access-Control-Allow-Credentials", "true"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void testCorsWith127001Origin() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        mockMvc.perform(options("/api/diagramprocess/reduce")
                .header("Origin", "http://127.0.0.1:4200")
                .header("Access-Control-Request-Method", "POST")
                .header("Access-Control-Request-Headers", "Content-Type"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://127.0.0.1:4200"));
    }

    @Test
    void testCorsWithUnauthorizedOrigin() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // For unauthorized origins, Spring Boot should return 403 Forbidden
        mockMvc.perform(options("/api/diagramprocess/reduce")
                .header("Origin", "http://malicious-site.com")
                .header("Access-Control-Request-Method", "POST")
                .header("Access-Control-Request-Headers", "Content-Type"))
                .andExpect(status().isForbidden());
    }
}
