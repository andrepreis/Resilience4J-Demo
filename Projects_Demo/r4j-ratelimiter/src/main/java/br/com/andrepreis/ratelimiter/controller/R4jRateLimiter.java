package br.com.andrepreis.ratelimiter.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

public class R4jRateLimiter {
	private String cache = null;
	public static final Logger logger = LoggerFactory.getLogger(R4jRateLimiter.class);
	public static String SUPERAPI = "http://localhost:9090/superapi/v1/getRateLimiterCalls?name=";

    @Autowired
    RestTemplate restTemplate;

    @GetMapping("/getRateLimiter")
    @CircuitBreaker(name = "errorRateLimiterMonitor", fallbackMethod = "fallBack")
    public ResponseEntity<String> callApi(@RequestParam(value = "name", defaultValue = "rateLimiterCallsMonitor") String name) {
        
    	ResponseEntity<String> responseEntity = restTemplate.getForEntity(SUPERAPI + name, String.class);
        
        //update cache
    	logger.info("Processamento executado com sucesso : "+responseEntity.getBody().toString());
        cache = responseEntity.getBody().toString();
        return responseEntity;
    }



    public ResponseEntity<String> fallBack(String name, io.github.resilience4j.bulkhead.BulkheadFullException ex) {
        
    	logger.info("Limite atingido! Nenhuma chamada sera atendida");
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Retry-After", "1"); //Header de retorno

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .headers(responseHeaders) //Adicionano parametro no header indicando para tentar novamente apos um certo tempo
                .body("Too many concurrent requests- Please try after some time");
    }
    

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
