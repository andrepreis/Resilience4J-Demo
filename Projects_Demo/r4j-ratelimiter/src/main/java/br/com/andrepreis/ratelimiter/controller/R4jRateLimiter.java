package br.com.andrepreis.ratelimiter.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;

@RestController
@CrossOrigin
@RequestMapping("/v1")
public class R4jRateLimiter {

	
	public static final Logger logger = LoggerFactory.getLogger(R4jRateLimiter.class);
	public static String SUPERAPI = "http://localhost:9090/superapi/v1/getRateLimiterCalls?name=";

    @Autowired
    RestTemplate restTemplate;

    @GetMapping("/getRateLimiter")
    @RateLimiter(name = "errorRateLimiterMonitor", fallbackMethod = "fallBack")
    public ResponseEntity<String> greeting(@RequestParam(value = "name", defaultValue = "errorRateLimiterMonitor") String name) {
    	
    	//Chama API  qualquer
    	logger.info("Camando API Externa");
    	
    	return ResponseEntity.ok().body("Hello World: " + name);
    }


    /**
     * 
     * @param name
     * @param ex
     * @return
     */
    public ResponseEntity<String> fallBack(String name, io.github.resilience4j.ratelimiter.RequestNotPermitted ex) {
        
    	logger.info("Limite de conexões atingido");
    	
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Retry-After", "1"); //retry after one second

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .headers(responseHeaders) //send retry header
                .body("Too many request - No further calls are accepted");
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
