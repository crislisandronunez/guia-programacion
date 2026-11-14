# 03 — Configuración Java y creación explícita

## Lectura

Secciones 2–3, 8 de la [guía](../../guias/Glosario-Spring-Core-Boot-Core.md).

## Consigna

Creá una configuración que publique Clock como bean y usalo para fechar avisos cuando
incorpores el dominio. En tests, reemplazalo por un reloj fijo. Separá configuraciones
por responsabilidad y conectalas con @Import.

En una configuración pequeña de prueba, compará llamadas directas entre métodos @Bean
con proxyBeanMethods=true y false. Después usá parámetros de método para expresar
dependencias sin depender de esa interceptación. Explorá un FactoryBean sencillo
y distinguí el producto de la fábrica.

## Experimentos y aceptación

- El reloj de prueba produce una fecha determinista.
- Un objeto de una clase sin @Component puede registrarse mediante @Bean.
- Con configuración completa, la llamada interceptada respeta el singleton.
- Con proxyBeanMethods=false, una llamada Java directa puede crear otro objeto.
- Inyectar el bean por parámetro mantiene la instancia administrada.
- getBean("nombre") y getBean("&nombre") distinguen producto y FactoryBean.

## Entrega

Código y pruebas del ejercicio, evidencia y respuestas en `ejercicios/respuestas/03.md`.
Agregá solicitudes a Postman cuando haya comportamiento HTTP.

## Para explicar

¿Por qué @Configuration(proxyBeanMethods=false) no convierte todos los beans en prototype?

Siguiente: [Scopes y ciclo de vida](04-scopes-ciclo.md).

