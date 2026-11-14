# 11 — LRU, LFU y vencimiento

## Lectura

Secciones 11 de la [guía](../../guias/Glosario-Tipos-de-Algoritmos-Programadores.md).

## Consigna

Implementá una caché LRU por composición con capacidad finita y una política TTL.
Definí si expire-after-write o expire-after-access; usá tiempo controlable y no Thread.sleep.
Compará con una LFU didáctica cuya regla de desempate sea explícita.
No presentes esa LFU simple como implementación de W-TinyLFU.

## Experimentos y aceptación

- Secuencia put(A),put(B),get(A),put(C) con capacidad 2 desaloja la clave correcta.
- Cubrís actualización, ausente, capacidad inválida y vencimiento exacto.
- TTL y desalojo por capacidad son mecanismos separados.
- Se retiran entradas expiradas según política documentada de limpieza.
- La referencia devuelta no permite corromper estructuras internas.
- Si prometés thread-safety, probás operaciones compuestas con concurrencia real.
- Informás costo de limpieza y no afirmás O(1) para cualquier operación.

## Entrega

Implementación, pruebas, análisis de tiempo/memoria y respuestas en
`ejercicios/respuestas/11.md`. Incluí precondiciones, casos límite e invariantes.

## Para explicar

¿Puede una caché expirar correctamente al leer y aun así retener memoria innecesaria?

Siguiente: [Token Bucket y ventanas de tasa](12-rate-limiting.md).

