# 04 — Scopes y ciclo de vida

## Lectura

Secciones 2, 6–7 de la [guía](../../guias/Glosario-Spring-Core-Boot-Core.md).

## Consigna

Creá una traza de preparación prototype y un consumidor singleton. Compará inyectar
directamente la traza con solicitarla mediante ObjectProvider en cada operación.
Registrá construcción, @PostConstruct y @PreDestroy con eventos de prueba observables.

Compará @Lazy en un bean y en el punto de inyección. En contextos aislados, agregá un
BeanFactoryPostProcessor y un BeanPostProcessor acotados a beans didácticos.
Explorá request y session scopes con pruebas web y scoped proxies.

## Experimentos y aceptación

- El prototype inyectado una sola vez permanece en el singleton; el provider obtiene instancias nuevas.
- Cerrar el contexto destruye los singletons administrados.
- La limpieza del prototype queda a cargo del consumidor; no presupongas @PreDestroy automático.
- Una dependencia eager puede provocar la creación de un bean marcado @Lazy.
- Dos requests tienen instancias request distintas; una sesión conserva su instancia entre requests.
- Explicás por qué singleton no significa thread-safe ni una instancia global en toda la JVM.

## Entrega

Código y pruebas del ejercicio, evidencia y respuestas en `ejercicios/respuestas/04.md`.
Agregá solicitudes a Postman cuando haya comportamiento HTTP.

## Para explicar

¿Qué cambia un postprocesador de definiciones y qué cambia uno de instancias?

Siguiente: [Propiedades tipadas y precedencia](05-propiedades.md).

