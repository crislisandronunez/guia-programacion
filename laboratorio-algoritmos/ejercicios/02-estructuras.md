# 02 — Colecciones y contratos de igualdad

## Lectura

Secciones 2 de la [guía](../../guias/Glosario-Tipos-de-Algoritmos-Programadores.md).

## Consigna

Modelá los datos del caso y compará ArrayList, HashSet, LinkedHashSet, HashMap,
TreeMap y ArrayDeque. Implementá deduplicación conservando orden y una búsqueda de
tramos con floorEntry. Definí claves inmutables y reglas de equals/hashCode.

## Experimentos y aceptación

- LinkedHashSet conserva la primera aparición según el contrato de igualdad elegido.
- Duplicados conflictivos no se pierden por una conversión silenciosa a Map.
- Tramos cubren mínimo, frontera exacta y valor fuera del dominio.
- FIFO y LIFO se comprueban con la misma secuencia.
- Un experimento muestra el problema de mutar una clave ya insertada.
- ConcurrentHashMap no convierte automáticamente una secuencia get-then-put en operación atómica.

## Entrega

Implementación, pruebas, análisis de tiempo/memoria y respuestas en
`ejercicios/respuestas/02.md`. Incluí precondiciones, casos límite e invariantes.

## Para explicar

¿Qué información perdés si elegís Set cuando necesitabas contar ocurrencias?

Siguiente: [Cruce de pedidos y comprobantes](03-cruces.md).

