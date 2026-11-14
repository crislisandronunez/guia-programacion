# 16 — Integración y defensa del laboratorio

## Lectura

Secciones 1–30, especialmente 29–30 de la [guía](../../guias/Glosario-Spring-Core-Boot-Core.md).

## Consigna

Integrá el recorrido con el caso original y completá Postman. Prepará una tabla
requisito → bean responsable → configuración → prueba → evidencia.
Documentá el recorrido de una petición desde el controller hasta canal, repositorio,
evento y métricas, indicando dónde intervienen proxies.

Ejecutá la aplicación empaquetada con configuración base, dev y prod local.
Documentá extensiones sin presentarlas como resueltas: XML y jerarquías de contextos,
@Lookup, scopes personalizados, hooks EnvironmentPostProcessor/ApplicationContextInitializer,
starter en módulos independientes, WAR, buildpacks, AOT/native y CDS.

## Experimentos y aceptación

- Crear, consultar y enviar funcionan con pruebas de éxito, validación, ausencia, conflicto y fallo simulado.
- La concurrencia no duplica el envío local y los tests son repetibles.
- Configuración inválida falla temprano y no deja una aplicación parcialmente lista.
- Probás perfiles, reemplazo del bean auto-configurado y límites de los proxies.
- mvn verify pasa; JAR y Postman se comprueban con puertos documentados.
- Cerrar libera recursos y reiniciar demuestra la pérdida de los datos en memoria.
- Todas las etapas tienen respuestas y evidencia; diferenciás lo implementado de las extensiones.

## Entrega

Código y pruebas del ejercicio, evidencia y respuestas en `ejercicios/respuestas/16.md`.
Agregá solicitudes a Postman cuando haya comportamiento HTTP.

## Para explicar

¿Qué garantías nuevas harían falta para persistencia, varios nodos y entrega durable de avisos?

Volvé al [índice](README.md) para revisar la cobertura.

