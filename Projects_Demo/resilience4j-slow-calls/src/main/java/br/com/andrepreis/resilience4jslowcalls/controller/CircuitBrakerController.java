package br.com.andrepreis.resilience4jslowcalls.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@RestController
@CrossOrigin
@RequestMapping("/v1")
public class CircuitBrakerController {
    
	public static final Logger logger = LoggerFactory.getLogger(CircuitBrakerController.class);
	public static String SUPERAPI = "http://localhost:9090/superapi/v1/getSlowCalls?name=";
		
    @Autowired
    RestTemplate restTemplate;
    
    private String cache = null;

    @GetMapping("/getSlowCalls")
    @CircuitBreaker(name = "slowCallMonitor", fallbackMethod = "fallBack")
    public ResponseEntity<String> callApi(@RequestParam(value = "name", defaultValue = "slowCallMonitor") String name) {
            	    	
    	ResponseEntity<String> responseEntity = restTemplate.getForEntity(SUPERAPI + name, String.class);
            	    
    	//Atualiza Cache
        cache = responseEntity.getBody().toString();
        
        return responseEntity;
    }

    //Método chamado quando o 
    public ResponseEntity<String> fallBack(String name, io.github.resilience4j.circuitbreaker.CallNotPermittedException ex) {
        
    	logger.info("Circuito slowCallMonitor :" +name+ " Aberto! Retornando resultado do cache!");    	
        
    	//return data from cache
        return ResponseEntity.ok().body(cache);
    }

    //Método chamado quando a API que esta sendo monitorada falha
    public ResponseEntity<String> fallBack(String name, HttpServerErrorException ex) {
        
    	logger.info("< ATENÇÂO!! > Problemas com: " + SUPERAPI + name);
        
    	//return data from cache
        return ResponseEntity.ok().body(cache);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }


}
