package br.com.andrepreis.controller;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.andrepreis.service.SuperApiProcessor;

@RestController
@CrossOrigin
@RequestMapping("/v1")
public class SuperApiController {
	
	public static final Logger logger = LoggerFactory.getLogger(SuperApiController.class);
	Random random = new Random(-9735303121L);
	
	@Autowired
	SuperApiProcessor service;
	
	/**
	 * Simula a chamada de serviço que apresenta lentidão no processamento
	 * @param nome
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/getSlowCalls", method = RequestMethod.GET)
	@ResponseBody	
	public ResponseEntity<String> getSlowCalls(@RequestParam(value = "name", defaultValue = "slowCallsTest") String name)throws Exception{
		
		String returnMessage="Sucess";
		long i = random.nextInt(100000);
		
		try {		
			
			returnMessage = service.geraAtrasoSimulado(Long.valueOf(i).toString() + " --> " + name);
			
		}catch (Exception e) {
			logger.error(e.getMessage(),e);
			returnMessage="Error";
			return new ResponseEntity<String>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return new ResponseEntity<String>(returnMessage,HttpStatus.OK);		
	}
	
	/**
	 * Simula chamada de um serviço que gera erros aleatórios
	 * @param nome
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/getErrorCalls", method = RequestMethod.GET)
	@ResponseBody	
	public ResponseEntity<String> getErrorCalls(@RequestParam(value = "name", defaultValue = "errorCallsTest") String name)throws Exception{
		
		String returnMessage="Sucess";
		long i = random.nextInt(100000);
		
		try {
		
			returnMessage = service.geraErrosSimulados(Long.valueOf(i).toString() + " --> " + name);
		
		}catch (Exception e) {
			logger.error(e.getMessage(),e);
			returnMessage="Error";
			return new ResponseEntity<String>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	
		return new ResponseEntity<String>(returnMessage,HttpStatus.OK);		
	}
	

}
