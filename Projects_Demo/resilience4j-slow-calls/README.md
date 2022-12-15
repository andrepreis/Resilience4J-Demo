# Prevenindo lentidão em cascata na chamada de APIs REST utilizando o padrão Circuit Breaker com a biblioteca Resilience4J

Neste exemplo, aprenderemos como evitar lentidão em cascata nas chamadas entre APIs REST usando o módulo  Circuit Breaker do Resilience4j .
Imagine que temos dois serviços: *Service1* e *Service2* .  *Service1* depende de execução e resposta de *Service2* para prosseguir com seu processamento.
Por algum motivo, *Service2* está apresentando lentidão. Em vez de chamar repetidamente por *Service2*, *Service1* deve parar momentaneamente de chama-lo até que o *Service2* esteja completamente ou parcialmente recuperado.
Utilizando a biblioteca Resilience4j, essa funcionalidade pode ser facilmente obtida com o uso da anotação @CircuitBreaker.

## Visão geral

* Os usuários fazem chamadas para obter uma mensagem  de Service1.
* *Service1* chama *Service2* para buscar a mensagem.
* O tempo de resposta normal de *Service2* é de ~20 milissegundos. Devido a algum problema aleatório, 50% das chamadas para *Service2* levam mais de 3 segundos.
* Esse comportamento de lentidão de *Service2* não deve afetar *Service1* .
* Sempre que  *Service1* perceber que 50% das chamadas estão demorando mais de 20 milissegundos, ele não chamará *Service2* e retornará a resposta armazenada, por exemplo, em um cahce..

Todas essas funcionalidades citadas acima, podem ser obtidas com o uso do padrão *Circuit Breaker*.

## Código Fonte
Abaixo, segue o exemplo implementado:

* [Código fonte do exemplo](https://github.com/andrepreis/Resilience4J-Demo/tree/main/Projects_Demo/resilience4j-slow-calls)

## Arquitetura

![Arquitetura do exemplo](./img/CB-RF4-SC.png)

A arquitetura acima demonstra um modelo  básico de arquitetura de uma aplicação onde iremos simular lentião na chamada de uma API externa:

1. Varias threads são disparadas via JMesuperapiter ou navegador chamando Service1.
2. Service1 esta encapsulado por um objeto Circuit Breaker. Este circuit breaker monitora todas as chamadas para Service2 originadas a partir de Service1.
3. Sempre que Service2 responder de forma adequada, Service1 retorna a resposta para o solicitante e atualiza o cache da aplicação.
4. Sempre que ocorrer lentidão em Service2, o circuito sera aberto e Service1 retornará o valor mais atual existente no cache.


## Executando o Exemplo

Para executar nosso exemplo, você precisará executar montar e executar as seguintes aplicações :
	
	*superapi : Aplicação que contem os serviços fake que simulam servições externos que tem sua chamada encapsulada dentro do circuit breaker.
	*resilience4j-slow-calls : Aplicação que implementa um circuit breaker que trata lentidão na chamada à um serviço externo.

Para execução dos exemplos você pode utilizar sua IDE favorita(Eclipse, VSCode, IntelliJ), ou compilar e executar as aplicações via linha de comando.
No meu caso em específico, utilizo a  IDE Eclipse juntamente com o plugin do SpringBoot, onde todas as aplicações springboot são detectadas e consigo inicia-las de dentro da IDE.

![Eclipse + Plugin SpringBoot](./img/TelaEclipse.png)

Se preferir executar os exemplos via linha de comando, siga os passos de 1 a 5 que estão definidos a seguir:


1. Devemos dar um build na API que irá simular os problemas. Acesse o diretório "Resilience4J-Demo/Projects_Demo/superapi":

	 > xxxxxxx@aaa-aaaa:~/eclipse-workspace/Resilience4J-Demo/Projects_Demo/superapi$ **mvn clean install**

2. Dentro do diretório "Resilience4J-Demo/Projects_Demo/superapi/target" inicie a aplicação "superapi-1.0-SNAPSHOT.jar":
	
	>	xxxxxxx@aaa-aaaa:~/eclipse-workspace/Resilience4J-Demo/Projects_Demo/superapi/target$ **java -jar superapi-1.0-SNAPSHOT.jar** 


3. Dentro do diretório "Resilience4J-Demo/Projects_Demo/resilience4j-slow-calls" precisamos dar um "build" no projeto que encapsula as chamadas a API monitorada pelo Circuit Breaker:

	 > xxxxxxx@aaa-aaaa:~/eclipse-workspace/Resilience4J-Demo/Projects_Demo/resilience4j-slow-calls$ **mvn clean install**

4. Dentro do diretório "Resilience4J-Demo/Projects_Demo/resilience4j-slow-calls/targe" inicie a aplicação "r4j-slow-calls-0.0.1-SNAPSHOT.jar" :
	
	>	xxxxxxx@aaa-aaaa:~/eclipse-workspace/Resilience4J-Demo/Projects_Demo/resilience4j-slow-calls/target$ **java -jar r4j-slow-calls-0.0.1-SNAPSHOT.jar**
	
5. Com as duas aplicações sendo executadas, teremos dois endpoints:

	* http://localhost:9090/superapi/v1/getSlowCalls : Endpoint da APIs que irá gerar lentidão aleatória e será utilizada para testarmos a implementação de circuit breaker
	
	* http://localhost:9091/superapi/v1/getSlowCalls : Endpoint que encapsula a chamada para a API acima dentro de um circuit breaker.
	
6. Para visualizarmos melhor o atuação do circuit breaker quando a API monitorada começa a apresentar lentidão, utilizaremos o JMeter para simular várias chamadas simultâneas a *Service1* e este, por sua vez, faz chamadas para *Service2*. *Service2* por um motivo qualquer começa a apresentar lentidão no processamento e afeta diretamente "Service1". Você pode baixar o scrip JMeter clicando no link : [Resilience4J.jmx](../../Resilience4J.jmx)

7. Com o sript de testes devidamente importado no Jmeter, vamos verificar dois cenários:

	1. Iremos chamar  diretamente *Service2* e observar o comportamento quando ocorrem lentidão durante sua chamada;
	![Chamada direta sem circuit breaker](./img/R4J-Slow-Calls-TelaSlowCalls2.png)		
	Disparamos 2 ciclos de 20 requisições dentro de um intervalo de 2 segundos e obtemos um tempo médio de processamento de 1278ms por requisição.
	
	2. Iremos chamar *Service1* e este por sua vez, fara chamadas para para *Service2*.
	![Chamada direta com Circuit Breaker](./img/R4J-Slow-Calls-TelaSlowCalls2.png)
	Disparamos 2 ciclos de 20 requisições dentro de um intervalo de 2 segundos e obtemos um tempo médio de processamento de 100ms por requisição.
	
8. Após a execução dos dois cenários, iremos  comparar o resultado das duas execuções:
Note que quando encapsulamos as chamadas para *Service2* dentro do circuit breaker, o tempo médio de resposta de *Service2* foi significativamente mais rápido quando comparado com a chamada direta sem o Circuit Breaker. Porque isso acontece?


## Detalhes do Código
