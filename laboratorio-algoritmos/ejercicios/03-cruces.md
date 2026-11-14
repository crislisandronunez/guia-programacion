# 03 — Cruce de pedidos y comprobantes

## Lectura

Secciones 3 de la [guía](../../guias/Glosario-Tipos-de-Algoritmos-Programadores.md).

## Consigna

Implementá conciliación ingenua e indexada respetando todas las reglas del caso.
Deduplicá por comprobanteId, sumá comprobantes diferentes del mismo pedido y reportá
huérfanos. Compará ambas implementaciones con datos pequeños y generados.

## Experimentos y aceptación

- Ambas producen los mismos estados y montos para entradas válidas.
- Un comprobante idéntico repetido cuenta una vez; uno conflictivo se rechaza.
- Se mantiene el orden de pedidos y se informa cada huérfano deduplicado.
- Listas vacías, importes excedidos y suma que desborda long están cubiertos.
- Explicás costo esperado O(n+m) y memoria del índice.
- La mejora no consiste en descartar datos para que el algoritmo sea más rápido.

## Entrega

Implementación, pruebas, análisis de tiempo/memoria y respuestas en
`ejercicios/respuestas/03.md`. Incluí precondiciones, casos límite e invariantes.

## Para explicar

¿Por qué quedarse siempre con el primer duplicado puede ocultar un error de datos?

Siguiente: [Búsqueda binaria y rangos](04-busqueda.md).

