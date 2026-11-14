# 13 — Logging, Actuator y disponibilidad

## Lectura

Secciones 23–24, 28 de la [guía](../../guias/Glosario-Spring-Core-Boot-Core.md).

## Consigna

Usá SLF4J con parámetros y niveles por paquete. Incorporá un correlationId por
solicitud, validá o generá su valor y limpiá MDC al finalizar. No registres datos personales.
Comprobá qué ocurre con MDC al cambiar de hilo; propagalo explícitamente si lo necesitás.

Creá un perfil lab con exposición local de health, info y metrics. Agregá un contador
de envíos simulados exitosos y fallidos con etiquetas de cardinalidad acotada.
Explorá un HealthIndicator para un componente simulado y distinguí liveness de readiness.

## Experimentos y aceptación

- Dos requests consecutivos no heredan accidentalmente el correlationId anterior.
- Fuera del perfil lab sigue expuesto únicamente health.
- Un fallo simulado modifica el indicador y la respuesta HTTP de health según el estado configurado.
- Las métricas cambian con las operaciones; no llevan UUID, emails ni textos como etiquetas.
- Un canal no disponible no convierte automáticamente liveness en fallo.
- No exponés env, configprops, heapdump ni todos los endpoints con un comodín.

## Entrega

Código y pruebas del ejercicio, evidencia y respuestas en `ejercicios/respuestas/13.md`.
Agregá solicitudes a Postman cuando haya comportamiento HTTP.

## Para explicar

¿Por qué un health UP inicial no prueba que las reglas de envío sean correctas?

Siguiente: [Hooks de arranque y cierre](14-arranque-apagado.md).

