# Consignas del laboratorio Kafka

## Recorrido y cobertura

| Etapa | Consigna | Secciones de la guía |
|---|---|---|
| 01 | [Tópicos y consola](01-topicos-y-consola.md) | 1–4, 8, 19, 27–29 |
| 02 | [Claves, particiones y orden](02-claves-y-orden.md) | 4–6, 9, 29, 38 |
| 03 | [Productores Java y Spring](03-productores.md) | 7, 13, 20, 30–32 |
| 04 | [Grupos, offsets y replay](04-grupos-y-offsets.md) | 9–12, 21, 28 |
| 05 | [Listeners, commits y rebalanceos](05-listeners-y-commits.md) | 10–12, 21–22, 33 |
| 06 | [Contrato, JSON y evolución](06-contratos.md) | 5, 17, 31 |
| 07 | [Reintentos y DLT](07-errores-y-dlt.md) | 15, 24, 34 |
| 08 | [Idempotencia y efectos SQL](08-idempotencia-sql.md) | 13–14, 23, 35 |
| 09 | [Outbox y publicación confiable](09-outbox.md) | 23, 32, 35, 41 |
| 10 | [Transacciones dentro de Kafka](10-transacciones-kafka.md) | 13–14, 35, 40 |
| 11 | [Métricas, capacidad y seguridad](11-operacion.md) | 16, 18–19, 37–39 |
| 12 | [Pruebas e integración final](12-integracion.md) | 24–26, 36, 41–43 |

## Reglas comunes

- Java 21 y Spring Boot 3.5.16. Usá la [guía](../../guias/Glosario-Kafka.md)
  como referencia conceptual y la documentación oficial para las opciones del cliente.
- Mantené el tópico `pedidos-confirmados`, clave `pedidoId`, `eventoId` estable y
  contrato versionado. Si cambiás el contrato, explicá qué consumidor lo soporta.
- Los endpoints de práctica van bajo `/api`. No expongas un endpoint que permita
  publicar payloads arbitrarios en cualquier tópico del clúster.
- Separá los grupos `auditoria-lab` y `estadisticas-lab`. No uses un grupo nuevo en
  cada arranque para simular recuperación.
- Cada etapa entrega código, prueba o experimento reproducible, solicitudes
  Postman cuando corresponda y un informe `ejercicios/respuestas/NN.md`.
  Las respuestas son un entregable futuro del estudiante.
- Incluí en los informes **tópico, partición, offset, grupo y `eventoId`** cuando
  esos datos expliquen un resultado. No uses el offset como ID global del evento.
- Hacé los experimentos destructivos de offsets y tópicos solo en el clúster
  local. Primero inspeccioná; después ejecutá. No uses `down -v` si querés
  conservar el volumen de PostgreSQL.
- Un test verde sin Docker puede significar que Testcontainers se omitió.
  Registrá cuántas pruebas se ejecutaron realmente.
- Diferenciá envío aceptado por Kafka, offset confirmado y efecto de negocio.
  Ninguno de los tres demuestra automáticamente los otros dos.

Comenzá por el [caso](../caso/pedidos.md) y [01 — Tópicos y consola](01-topicos-y-consola.md).
