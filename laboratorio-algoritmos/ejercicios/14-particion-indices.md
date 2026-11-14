# 14 — Partición, Bloom e IDs

## Lectura

Secciones 14 de la [guía](../../guias/Glosario-Tipos-de-Algoritmos-Programadores.md).

## Consigna

Simulá un anillo de hashing consistente con nodos virtuales y función estable:
definí colisiones, wrap-around y ausencia de nodos. Compará movimiento de claves al
agregar un nodo contra hash % N, sin prometer un porcentaje exacto para toda muestra.

Implementá un Bloom Filter didáctico con BitSet y funciones de hash deterministas.
Explorá un generador Snowflake con reloj controlable y rechazo o espera acotada ante
retroceso o agotamiento de secuencia. Compará B-Tree/B+Tree y LSM con trazas en papel;
no hace falta implementar un motor de almacenamiento.

## Experimentos y aceptación

- Insertados en Bloom no tienen falsos negativos bajo el contrato sin borrado.
- Medís falsos positivos sobre claves nuevas sin exigir tasa exacta por test.
- No implementás borrado apagando bits compartidos.
- Hashing consistente conserva las asignaciones que no corresponden al tramo alterado.
- IDs prueban secuencia agotada, reloj hacia atrás y rango de nodo.
- Documentás colisión si dos generadores comparten identidad de nodo y las limitaciones tras reinicio.
- Distinguís hash slots de Redis Cluster de un anillo de hashing consistente.
- Las trazas de índices incluyen costo de escritura, lectura y compactación, no solo búsqueda.

## Entrega

Implementación, pruebas, análisis de tiempo/memoria y respuestas en
`ejercicios/respuestas/14.md`. Incluí precondiciones, casos límite e invariantes.

## Para explicar

¿Por qué Bloom Filter necesita consultar la fuente cuando responde “posiblemente presente”?

Siguiente: [Trie, checksums e integridad](15-texto-integridad.md).

