# 11 — Métricas, capacidad y seguridad

## Objetivo

Leé las secciones 16, 18–19 y 37–39 de la [guía](../../guias/Glosario-Kafka.md).
Diagnosticá si el flujo avanza y dónde está el cuello de botella.

## Consigna

Generá una carga reproducible de eventos y ralentizá intencionalmente el
consumidor de Estadísticas. Medí `CURRENT-OFFSET`, `LOG-END-OFFSET`, `LAG`
y edad de los eventos pendientes. Luego devolvé el consumidor a velocidad
normal y observá la recuperación. Probá con una, dos y más de tres instancias
del mismo grupo. Registrá throughput, tiempo de procesamiento y rebalances;
indicá qué recurso se satura primero.

Añadí métricas de listeners y de publicación con Actuator/Micrometer o una
instrumentación equivalente. Ampliá la exposición de Actuator de `health` a
las métricas que vayas a consultar y, si usás Prometheus, añadí su registry.
El endpoint de salud de la base comprueba que
hay broker disponible, **no** que los grupos estén al día. Configurá
`bootstrap-servers` mediante variable de entorno para otro entorno de prueba.
Diseñá los permisos mínimos de productor, Auditoría y Estadísticas sobre
tópicos y grupos. TLS/SASL/ACL se pueden ensayar en un clúster local aislado
como extensión; el Compose básico no los activa.

## Experimentos y aceptación

- Entregás una tabla de mediciones antes, durante y después del atraso.
- Explicás por qué lag de 100 eventos puede ser tolerable y lag de uno puede
  ser grave si ese evento es antiguo.
- Mostrás que más miembros que particiones no aumenta paralelismo útil del grupo.
- Diferenciás un broker caído, un grupo sin miembros, una DLT creciente y un
  consumidor lento; no los reducís todos a «Kafka no funciona».
- La configuración de seguridad propuesta no contiene secretos versionados.

## Para explicar

Si `P-42` concentra la mayoría de los eventos, ¿por qué añadir particiones
puede no ayudar? ¿Qué regla de orden habría que revisar?

Siguiente: [12 — Integración](12-integracion.md).
