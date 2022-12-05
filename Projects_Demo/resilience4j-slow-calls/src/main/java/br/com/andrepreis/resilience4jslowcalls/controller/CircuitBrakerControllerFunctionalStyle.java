package br.com.andrepreis.resilience4jslowcalls.controller;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;

@RestController
public class CircuitBrakerControllerFunctionalStyle {
	
	TimeLimiterConfig config = TimeLimiterConfig.custom()
            .timeoutDuration(Duration.ofSeconds(1))
            .cancelRunningFuture(true)
            .build();
    
	private String cache = null;
	
	@Autowired
    RestTemplate restTemplate;
	
	public static final Logger logger = LoggerFactory.getLogger(CircuitBrakerControllerFunctionalStyle.class);
	public static String SUPERAPI = "http://localhost:9090/superapi/v1/getSlowCalls?name=";
	
	@GetMapping("/getSlowCallsFunctional")
    public ResponseEntity<String> greeting(@RequestParam(value = "name", defaultValue = "Monitor") String name) {
        String result = null;
        try {
            TimeLimiter timeLimiter = TimeLimiter.of(config);
            result = timeLimiter.executeFutureSupplier(() -> CompletableFuture.supplyAsync(() -> restTemplate.getForEntity(SUPERAPI + name, String.class).getBody()));
            cache = result;
        } catch (Exception e) {
        	logger.error(e.getMessage(), e);        	
            result = cache;

        }
        return ResponseEntity.ok().body(result);
    }
	
	

}
