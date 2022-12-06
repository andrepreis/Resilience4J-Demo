package br.com.andrepreis.resilience4jslowcalls.controller;

import java.util.Random;

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
	Random random = new Random(-6732303926L);
		
    @Autowired
    RestTemplate restTemplate;
    
    private String cache = null;

    @GetMapping("/getSlowCalls")
    @CircuitBreaker(name = "circuitSlowMonitor", fallbackMethod = "fallBack")
    public ResponseEntity<String> callApi(@RequestParam(value = "name", defaultValue = "circuitSlowMonitor") String name) {
            	    	
    	ResponseEntity<String> responseEntity = restTemplate.getForEntity(SUPERAPI + random.nextInt(12), String.class);
    	
    	logger.info("Sucesso no processamento da requisição : "+SUPERAPI + name);
    	logger.info(responseEntity.getBody().toString());
    	logger.info("");
            	    
    	//Atualiza Cache
        cache = responseEntity.getBody().toString();
        
        return responseEntity;
       
    }

  
    /**
     * Método invocado sempre que o circuito esta aberto
     * 
     * @param name : String enviada como parâmetro para a request
     * @param ex : Exception lançada quando o circuito esta aberto
     * 
     * @return : Resposta alternativa para a requisição original
     */
    public ResponseEntity<String>  fallBack(String name, io.github.resilience4j.circuitbreaker.CallNotPermittedException ex) {
    	
    	logger.info("<Circuitbreaker is open!> : "+SUPERAPI + name);
    	logger.info("Valor retornado do cache : " + cache);
    	logger.info("");
        
        //retornando último valor atualizado no cache
        return ResponseEntity.ok().body(cache);
    }

    /**
     * Método invocado sempre que a o serviço monitorado lançar alguma Exception
     * 
     * @param name : String enviada como parâmetro para a request
     * @param ex :
     * 
     * @return : Resposta alternativa para a requisição original
     */
    public ResponseEntity <String> fallBack(String name, HttpServerErrorException ex) {
        
    	logger.info("<Erro a invocar Api!> : "+SUPERAPI + name);
    	logger.info(cache);
    	logger.info("");
    	
    	//retornando último valor atualizado no cache
        return ResponseEntity.ok().body(cache);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }


}
