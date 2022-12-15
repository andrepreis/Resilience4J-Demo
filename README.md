# Criando APIs REST resilientes utilizando Spring Boot e Resilience4J

O que acontece quando a comunicação entre suas REST APIs apresenta falhas?

Existe um padrão chamado Circuit Breaker que ajuda a mitigar esses problemas. Neste repositório você encontra alguns exemplos comuns de codificação deste padrão utilizando a biblioteca Resilience4j em um projeto com Spring Boot.

* [API fake que simula um suposto serviço externo que passa por problemas](https://github.com/andrepreis/Resilience4J-Demo/tree/main/Projects_Demo/superapi)
* [Prevenindo lentidão em cascata na chamada de APIs REST utilizando o padrão Circuit Breaker com a biblioteca Resilience4J.](https://github.com/andrepreis/Resilience4J-Demo/tree/main/Projects_Demo/resilience4j-slow-calls)
* [Circuit Breaker aplicado no tratamento de erros que ocorrem na chamada de serviços externos.](https://github.com/andrepreis/Resilience4J-Demo/tree/main/Projects_Demo/resilience4j-error-calls)

# Sugestões de leitura

* [Circuit Breaker por Martinfowler](https://martinfowler.com/bliki/CircuitBreaker.html)
* [Post antigo, porem bastante atual :) onde a Netflix fala sobre resiliência de APIs](https://netflixtechblog.com/making-the-netflix-api-more-resilient-a8ec62159c2d)
* [Outro post onde a Netflix fala sobre tolerância a falha em aplicações distribuidas](https://netflixtechblog.com/fault-tolerance-in-a-high-volume-distributed-system-91ab4faae74a)
* [Documentação bastante didática sobre o pattern circuit breaker](https://learn.microsoft.com/en-us/azure/architecture/patterns/circuit-breaker)
