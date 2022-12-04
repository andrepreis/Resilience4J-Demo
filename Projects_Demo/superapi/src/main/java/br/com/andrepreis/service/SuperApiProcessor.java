package br.com.andrepreis.service;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Classe responsável pela implementação da lógica de negócio executada pelo serviço
 * 
 * @author andrereis
 *
 */
@Service
public class SuperApiProcessor {

	public static final Logger logger = LoggerFactory.getLogger(SuperApiProcessor.class);
	
	Random random = new Random(-9735303121L);
	
	/**
	 * Simula chamada de API
	 * 
	 * @param sParameter
	 * @return
	 */
	public String getSuperApiValue(String sParameter) throws Exception {
		
		//Verifica se ja é hora de começar com os problemas
		if(ControlVars.sucessRequestsNumber > ControlVars.requestsNumberBeforeError) {
						
			//Verifica se devo adicionar Throttling na requisição 
			if(applyThrottling()) {												
				Thread.sleep(ControlVars.throttlingTime);				
			}
			
			//Aplica Throttling no processamento para simular timeout
			if(applyLongThrottling()) {												
				Thread.sleep(ControlVars.errorTime);															
			}
		
		}else {			
			logger.info("Sucesso! [Total Requições:"+ControlVars.sucessRequestsNumber+" ,Total de erros:"+ControlVars.errorRequestsNumber+"]");			
		}

		ControlVars.sucessRequestsNumber++;
		return ControlVars.returnSucessMsg;
	}
	
	/**
	 * Simula atraso no processamnto durante a chamada de um serviço
	 * @param name
	 * @return
	 */
	public String geraAtrasoSimulado(String nome) throws Exception {
        
		int i = random.nextInt(2);
        if (i == 0) Thread.sleep(2*1500);            
        
        return "Ola " + nome + "!" + " Sua requisição por processada com sucesso!";
    }
	
	/**
	 * Simula erros de processamento durante a chamada de um serviço
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public String geraErrosSimulados(String nome) throws Exception {
        
		int i = random.nextInt(2);
        if (i == 0) i=i/0;                    
       
        return "Ola " + nome + "!" + " Sua requisição por processada com sucesso!";
        
    }

	public String geraMensagemQualquer(String nome) throws Exception {
		return "Ola " + nome + "!" + " Sua requisição por processada com sucesso!";
	}
	
	
	/**
	 * Verifica se devo aplicar Throttling a requisição
	
	 * @return
	 */
	private boolean applyThrottling() {
		
		if(ControlVars.errorRequestsNumber <= (ControlVars.requestsNumberBeforeError + ControlVars.requestInThrottling)) {
			//Incrementa registro de error
			ControlVars.errorRequestsNumber++;
			logger.info("Atraso! [Total Requições:"+ControlVars.sucessRequestsNumber+" ,Total de erros:"+ControlVars.errorRequestsNumber+"]");
			return true;
		}		
		return false;
	}
	
	
	/**
	 * Verifica aplicação de um  Throttling maior para simular queda do serviço
	 * @return
	 */
	private boolean applyLongThrottling() {
		
		if(ControlVars.errorRequestsNumber > (ControlVars.requestsNumberBeforeError + ControlVars.requestInThrottling)) {
			//Incrementa registro de error			
			ControlVars.sucessRequestsNumber = 0;
			ControlVars.errorRequestsNumber = 0;
			logger.info("Grande Atraso! [Total Requições:"+ControlVars.sucessRequestsNumber+" ,Total de erros:"+ControlVars.errorRequestsNumber+"]");
			return true;
		}		
		return false;		
		
	}
	
	


}
