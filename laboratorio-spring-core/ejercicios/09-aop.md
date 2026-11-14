# 09 — AOP y límites de los proxies

## Lectura

Secciones 12, 16 de la [guía](../../guias/Glosario-Spring-Core-Boot-Core.md).

## Consigna

Agregá spring-boot-starter-aop sin fijar una versión independiente del parent.
Creá una anotación para medir operaciones del servicio y un aspecto @Around que
registre duración, éxito o error. Preservá el resultado y la excepción original.

Compará llamada desde otro bean, self-invocation y objeto creado con new. En contextos
acotados observá un proxy JDK y uno por subclase; no asumas que todas las configuraciones
de Spring y Boot eligen el mismo tipo.

## Experimentos y aceptación

- Una llamada externa registra una medición y ejecuta el método una sola vez.
- El aspecto no transforma excepciones en éxitos ni expone datos del aviso.
- La llamada interna evita la interceptación basada en proxy; refactorizás a otro bean.
- Demostrás las limitaciones de métodos private/final y clases final con proxies por subclase.
- El pointcut no captura todos los beans de infraestructura.
- Si hay varios aspectos, verificás su orden explícitamente.

## Entrega

Código y pruebas del ejercicio, evidencia y respuestas en `ejercicios/respuestas/09.md`.
Agregá solicitudes a Postman cuando haya comportamiento HTTP.

## Para explicar

¿Por qué poner una anotación en un método no garantiza que la llamada pase por el proxy?

Siguiente: [Ejecución asíncrona y tareas programadas](10-asincronia.md).

