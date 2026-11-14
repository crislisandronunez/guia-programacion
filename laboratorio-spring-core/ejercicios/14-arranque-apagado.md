# 14 — Hooks de arranque y cierre

## Lectura

Secciones 7, 19, 27 de la [guía](../../guias/Glosario-Spring-Core-Boot-Core.md).

## Consigna

Registrá una secuencia observable de construcción, inicialización, eventos de contexto,
ApplicationStartedEvent, runners ordenados y ApplicationReadyEvent. Para eventos muy
tempranos, registrá el listener antes de SpringApplication.run.

Agregá un ApplicationRunner de diagnóstico activado por perfil y comparalo con
CommandLineRunner. Implementá un componente didáctico SmartLifecycle que libere sus
recursos al cerrar. Explorá el modo no web en una ejecución separada.

## Experimentos y aceptación

- Distinguís argumentos de opción y argumentos sueltos.
- Los runners terminan antes de ApplicationReadyEvent; un runner fallido impide llegar a ready.
- No asumís que el puerto abierto implica que todos los runners terminaron.
- El cierre normal invoca limpieza y respeta tiempos acotados.
- Un ensayo de apagado graceful observa qué sucede con una petición en curso.
- El modo no web cierra explícitamente el contexto y los ejecutores; no presupone salida automática.
- Documentás que una terminación forzada puede impedir callbacks de limpieza.

## Entrega

Código y pruebas del ejercicio, evidencia y respuestas en `ejercicios/respuestas/14.md`.
Agregá solicitudes a Postman cuando haya comportamiento HTTP.

## Para explicar

¿Por qué @Order en los runners no ordena la construcción de todos los beans?

Siguiente: [DevTools, empaquetado y diagnóstico](15-diagnostico.md).

