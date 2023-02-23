package br.com.andrepreis.resilience4jretrycalls.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import io.github.resilience4j.retry.annotation.Retry;



@RestController
@CrossOrigin
@RequestMapping("/v1")
public class R4jRetry {
	
	public static final Logger logger = LoggerFactory.getLogger(R4jRetry.class);
	public static String SUPERAPI = "http://localhost:9090/superapi/v1/getExternaServiceResponse?name=";
	
    @Autowired
    RestTemplate restTemplate;
    
    @GetMapping("/retryCall")
    @Retry(name = "testRetry", fallbackMethod = "fallback")
    public ResponseEntity<String> greeting(@RequestParam(value = "name", defaultValue = "greetingRetry") String name) {
        
    	logger.info("Efetuando chamada a um serviço externo");
        
    	ResponseEntity<String> responseEntity = restTemplate.getForEntity(SUPERAPI + name, String.class);    	
    	
        return responseEntity;
    }

	
    public  ResponseEntity<String>  fallback(Exception e) {
    	String msg = "Erro na chamada do serviço externo";
    	logger.info(msg);
    	 return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(msg);
    
    }
    
    
	@Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
