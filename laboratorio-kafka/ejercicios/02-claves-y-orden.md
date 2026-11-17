# 02 — Claves, particiones y orden

## Objetivo

Leé las secciones 4–6, 9, 29 y 38 de la [guía](../../guias/Glosario-Kafka.md).
Relacioná una regla de orden del negocio con la clave y la partición.

## Consigna

Publicá al menos ocho hechos de consola con claves `P-42`, `P-50` y `P-60`.
Usá `parse.key=true` y separador `:`; por ejemplo,
`P-42:{"eventoId":"E-91","pedidoId":"P-42"}`. Creá un pequeño lector Java
que imprima `key`, `partition`, `offset` y `value` de cada registro. No deduzcas
la partición solo por el valor del hash; medila en Kafka.

Después publicá dos hechos del **mismo pedido** con claves distintas. Compará
el orden observable con el caso en que ambos usan `P-42`. Diseñá una clave para
pedidos y otra para un hipotético contador por cliente; explicá qué entidad
quedaría ordenada en cada diseño.

## Experimentos y aceptación

- Los registros de una clave estable van a una partición bajo la configuración
  utilizada; documentás la partición observada y los offsets.
- Mostrás que `offset=0` puede existir en más de una partición.
- Explicás que no hay un orden total entre particiones.
- Simulás una clave muy frecuente y razonás por qué más consumidores no aceleran
  el procesamiento de **esa** clave sin cambiar la regla de orden.
- Tu lector no confunde `pedidoId`, `eventoId` y offset.

## Para explicar

¿Qué podría cambiar si aumentás de tres a seis particiones un tópico con claves
hash? ¿Cómo conservarías el orden que exige el negocio durante ese cambio?

Siguiente: [03 — Productores](03-productores.md).
