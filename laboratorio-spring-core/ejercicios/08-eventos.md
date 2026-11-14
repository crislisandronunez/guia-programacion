# 08 — Eventos de aplicación

## Lectura

Secciones 11 de la [guía](../../guias/Glosario-Spring-Core-Boot-Core.md).

## Consigna

Publicá AvisoCreado con ID y fecha tras guardar un aviso. Agregá listeners síncronos
para estadísticas locales. Usá ApplicationEventPublisher y @EventListener, y compará
con ApplicationListener. Evitá incluir destinatario o texto en el evento.

En un test aislado provocá una excepción del listener. Observá la respuesta al emisor
y el estado ya guardado. Definí cómo tratarás fallos de estadísticas para que el contrato
de creación siga siendo claro.

## Experimentos y aceptación

- Un aviso válido publica un evento; una validación fallida no publica ninguno.
- El listener síncrono se ejecuta en el hilo publicador en la configuración por defecto.
- Su trabajo ocurre antes de que publishEvent retorne.
- Un fallo del listener no deshace mágicamente una escritura en memoria.
- Reiniciar pierde los eventos; no afirmás entrega durable ni exactamente una vez.
- Explicás por qué @TransactionalEventListener no agrega transacciones a este repositorio.

## Entrega

Código y pruebas del ejercicio, evidencia y respuestas en `ejercicios/respuestas/08.md`.
Agregá solicitudes a Postman cuando haya comportamiento HTTP.

## Para explicar

¿Qué necesitarías cambiar si el evento debiera sobrevivir a la caída del proceso?

Siguiente: [AOP y límites de los proxies](09-aop.md).

