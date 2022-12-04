package br.com.andrepreis.r4jbulkhead.controller;


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

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@RestController
@CrossOrigin
@RequestMapping("/v1")
public class R4jbulkheadcontroller {
	
	private String cache = null;
	public static final Logger logger = LoggerFactory.getLogger(R4jbulkheadcontroller.class);
	public static String SUPERAPI = "http://localhost:9090/superapi/v1/getBulkheadCalls?name=";

    @Autowired
    RestTemplate restTemplate;

    @GetMapping("/getBulkheadCalls")
    @CircuitBreaker(name = "errorBulkheadCallsMonitor", fallbackMethod = "fallBack")
    public ResponseEntity<String> callApi(@RequestParam(value = "name", defaultValue = "bulkheadCallsMonitor") String name) {
        
    	ResponseEntity<String> responseEntity = restTemplate.getForEntity(SUPERAPI + name, String.class);
        
        //update cache
    	logger.info("Processamento executado com sucesso : "+responseEntity.getBody().toString());
        cache = responseEntity.getBody().toString();
        return responseEntity;
    }



    public ResponseEntity<String> fallBack(String name, io.github.resilience4j.bulkhead.BulkheadFullException ex) {
        
    	logger.info("BulkHead aplicado nenhuma nova chamada é aceita");
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Retry-After", "10"); //Header de retorno

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .headers(responseHeaders) //Adicionano parametro no header indicando para tentar novamente apos um certo tempo
                .body("Too many concurrent requests- Please try after some time");
    }
    

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
