//package com.example.webapp;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//public class HealthzEndpointTest {
//
//    @Autowired
//    private TestRestTemplate restTemplate;
//
//    @Test
//    public void checkHealthReturns200() {
//        ResponseEntity<Void> response = restTemplate.getForEntity("/healthz", Void.class);
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//    }
//}
