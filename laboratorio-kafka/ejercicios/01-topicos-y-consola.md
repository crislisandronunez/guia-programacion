# 01 — Tópicos y consola

## Objetivo

Leé las secciones 1–4, 8, 19 y 27–29 de la [guía](../../guias/Glosario-Kafka.md).
Observá un log que se conserva y una posición de lectura que pertenece a un grupo.

## Consigna

Levantá el Compose y describí `pedidos-confirmados` después de iniciar la
aplicación. Anotá particiones, factor de replicación, líder e ISR. Con un productor
de consola publicá tres registros. Consumilos con `--from-beginning` y el grupo
`auditoria-lab`; después consumilos con `estadisticas-lab`. Explicá por qué el
segundo grupo puede leer los mismos registros aunque el primero ya los leyó.

Comandos de partida:

```shell
docker compose exec kafka /opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --describe --topic pedidos-confirmados
docker compose exec kafka /opt/kafka/bin/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic pedidos-confirmados
docker compose exec kafka /opt/kafka/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic pedidos-confirmados --from-beginning --group auditoria-lab
```

Abrí productor y consumidor en terminales distintas. Detenelos con `Ctrl+C`.
La primera publicación puede ser texto de laboratorio; desde la etapa 06 el
valor deberá seguir el contrato JSON del caso.

## Experimentos y aceptación

- Los tres registros siguen disponibles para un grupo diferente.
- Describís `auditoria-lab` con `kafka-consumer-groups.sh` y distinguís
  `CURRENT-OFFSET`, `LOG-END-OFFSET` y `LAG`.
- Explicás por qué factor de replicación `1` no tolera la caída del único broker.
- Detenés y reiniciás el mismo grupo: indicás qué registros reaparecen y qué
  depende del momento de confirmación del consumidor de consola.
- Guardás comandos y resultados en `ejercicios/respuestas/01.md`.

## Para explicar

¿Qué evento borra Kafka al leerlo? ¿Qué limita el tiempo durante el cual otro
grupo podrá volver a leerlo?

Siguiente: [02 — Claves y orden](02-claves-y-orden.md).
