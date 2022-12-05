package br.com.andrepreis.resilience4jslowcalls.controller;

import java.util.concurrent.CompletableFuture;

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

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;

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
    @TimeLimiter(name = "timelimiterSlowMonitor", fallbackMethod = "fallBack")
    @Bulkhead(name = "bulkheadMonitor", fallbackMethod = "fallBack", type = Bulkhead.Type.THREADPOOL)
    public CompletableFuture<String> callApi(@RequestParam(value = "name", defaultValue = "bulkheadMonitor") String name) {
            	    	
    	ResponseEntity<String> responseEntity = restTemplate.getForEntity(SUPERAPI + name, String.class);
            	    
    	//Atualiza Cache
        cache = responseEntity.getBody().toString();
        
        return CompletableFuture.completedFuture(cache);
       
    }

  
    /**
     * 
     * @param name
     * @param ex
     * @return
     */
    private CompletableFuture<String> fallBack(String name, Exception ex) {

        logger.info("Timeout na chamada do serviço " + SUPERAPI + name);

        return CompletableFuture.completedFuture(cache);
    }

    /**
     * 
     * @param name
     * @param ex
     * @return
     */
    public ResponseEntity<String>  fallBack(String name, io.github.resilience4j.bulkhead.BulkheadFullException ex) {
             
    	logger.info("Bulkhead ativo. Nenhuma nova chamada será aceita");
    	HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Retry-After", "10");

        return ResponseEntity
        		.status(HttpStatus.SERVICE_UNAVAILABLE)
                .headers(responseHeaders)
                .body("Muitas conexões simultâneas! Tente mais tarde");
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }


}
