package br.com.andrepreis.resilience4jerrorcalls.controller;

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
	
	private String cache = null;
	public static final Logger logger = LoggerFactory.getLogger(CircuitBrakerController.class);
	public static String SUPERAPI = "http://localhost:9090/superapi/v1/getErrorCalls?name=";

    @Autowired
    RestTemplate restTemplate;

    @GetMapping("/getErrorCalls")
    @CircuitBreaker(name = "errorCallsMonitor", fallbackMethod = "fallBack")
    public ResponseEntity<String> callApi(@RequestParam(value = "name", defaultValue = "errorCallsMonitor") String name) {
        
    	ResponseEntity<String> responseEntity = restTemplate.getForEntity(SUPERAPI + name, String.class);
        
        //update cache
    	logger.info("Processamento executado com sucesso : "+responseEntity.getBody().toString());
        cache = responseEntity.getBody().toString();
        return responseEntity;
    }

    //Chamado quando o circuito esta aberto
    public ResponseEntity<String> fallBack(String name, io.github.resilience4j.circuitbreaker.CallNotPermittedException ex) {
        
    	logger.info("Circuito errorCallsMonitor: " +name+ " : Aberto! Retornando resultado do cache --> " + ResponseEntity.ok().body(cache));    	
        
        //return data from cache
        return ResponseEntity.ok().body(cache);
    }

    //Invoked when call to serviceB failed
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
