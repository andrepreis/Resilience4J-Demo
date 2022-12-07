package br.com.andrepreis.service;

/**
 * Variáveis úteis utilizadas na simulação de erro da API 
 * 
 * @author andrereis
 *
 */
public class ControlVars {
				
	// Numero de requisições antes de falhar
	static final int requestsNumberBeforeError = 5;
	
	//Número de requisições com sucesso antes de começar a degradar o resultado
	static int sucessRequestsNumber = 0;
	
	//Número de requisições com erro antes de reiniciar o contador de requisições
	static int errorRequestsNumber = 0;
	
	//Numero de requisições onde adicionaremos um throttling na requisição
	static final int requestInThrottling = 10;
	
	//Tempo em ms adicionado a requisição
	static final long throttlingTime = 5000;
	
	//Tempo em ms que o sistema ficará inndisponivel antes de voltar a responder 
	static final long errorTime = 25000;
	
	//Menságem de retorno da API
	static final String returnSucessMsg = "SUCESS!";
	
	static final String returnErrorMsg = "ERROR!";

}
