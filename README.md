# Criando APIs REST resilientes utilizando Spring Boot e Resilience4J

O que acontece quando a comunicação entre suas REST APIs apresenta falhas?

Em ambientes distribuídos, as chamadas para serviços e recursos externos podem falhar devido a instabilidades transitórias, como conexões de rede lentas, limites de tempo, recursos sobrecarregados ou temporariamente indisponíveis. Estas falhas normalmente são corrigidas automaticamente após um curto período de tempo e uma aplicação robusta deve estar preparada para manipulá-las.

No entanto, pode também haver situações em que as falhas se devem a eventos imprevistos, como erros (HTTP 5xx ou 4xx) e podem demorar muito mais tempo para serem corrigidas. Estas falhas podem variar em termos de gravidade, de uma perda parcial de conectividade à uma falha total de um serviço. 

Nada disso é aceitável e precisamos de uma solução que impeça que problemas pontuais, em portes específicas de nosso sistema, se propaguem e causem a queda de todo o sistema.

Existem alguns padrões de projeto que ajudam a mitigar estes problemas, por exemplo, o padrão *circuit breaker*. Neste repositório você encontra alguns exemplos de implementação do padrão *circuit breaker* utilizando a biblioteca Resilience4j em um projeto Java / Maven / SpringBoot.

Confira os exemplos abaixo:

* [API fake que simula um suposto serviço externo que passa por problemas](https://github.com/andrepreis/Resilience4J-Demo/tree/main/Projects_Demo/superapi)
* [Prevenindo lentidão em cascata na chamada de APIs REST utilizando o padrão Circuit Breaker com a biblioteca Resilience4J.](https://github.com/andrepreis/Resilience4J-Demo/tree/main/Projects_Demo/resilience4j-slow-calls)
* [Prevenindo propagação de erros em cascata na chamada de APIs REST utilizando o padrão Circuit Breaker com a biblioteca Resilience4J.](https://github.com/andrepreis/Resilience4J-Demo/tree/main/Projects_Demo/resilience4j-error-calls)

# Sugestões de leitura

* [Circuit Breaker por Martinfowler](https://martinfowler.com/bliki/CircuitBreaker.html)
* [Post antigo, porem bastante atual :) onde a Netflix fala sobre resiliência de APIs](https://netflixtechblog.com/making-the-netflix-api-more-resilient-a8ec62159c2d)
* [Outro post onde a Netflix fala sobre tolerância a falha em aplicações distribuidas](https://netflixtechblog.com/fault-tolerance-in-a-high-volume-distributed-system-91ab4faae74a)
* [Documentação bastante didática sobre o pattern circuit breaker](https://learn.microsoft.com/en-us/azure/architecture/patterns/circuit-breaker)
