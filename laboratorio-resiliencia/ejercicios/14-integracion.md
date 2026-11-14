# 14 — Integración y defensa de decisiones

## Lectura

Secciones 1–16 de la [guía](../../guias/Glosario-Reciliencia-Programacion.md).

## Consigna

Completá el caso y una matriz escenario → política → estado/datos → HTTP → métrica.
Ejecutá éxito, 503 intermitente, lentitud, saturación, circuito abierto, caché caída,
respuesta perdida, conflicto de stock y mensaje venenoso. Presentá evidencia antes
y después con la misma carga y entorno.

Completá Postman para el flujo funcional y pruebas concurrentes automatizadas para
capacidad y carreras. Documentá dependencias, perfiles y comandos de arranque.
Revisá la checklist de la guía indicando qué afirmaciones verificaste y cuáles
requieren condiciones adicionales.

## Experimentos y aceptación

- mvn verify pasa con las dependencias requeridas para las etapas resueltas.
- Tests de infraestructura realmente se ejecutan con Docker; un test omitido se reporta como omitido.
- Ningún fallback duplica reservas ni confirma trabajo que no fue registrado.
- Reintentos, colas, pools, TTL y tiempos tienen límites explícitos.
- Los datos persistentes sobreviven reinicios según el contrato.
- Catálogo sigue disponible bajo el fallo secundario probado.
- Comparás intentos, latencia p50/p95/p99, rechazos y trabajo pendiente, indicando tamaño de muestra.
- Delimitás extensiones: clusters/failover real, límites globales, Kafka y garantías entre varios nodos.

## Entrega

Código, pruebas y evidencia en `ejercicios/respuestas/14.md`.
Agregá solicitudes Postman cuando corresponda y anotá límites de lo demostrado.

## Para explicar

¿Qué fallos sigue sin tolerar tu solución y qué evidencia necesitarías antes de cambiar sus límites?

Volvé al [índice](README.md) para revisar cobertura.

