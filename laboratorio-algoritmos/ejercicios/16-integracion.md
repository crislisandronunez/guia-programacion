# 16 — Integración y elección fundamentada

## Lectura

Secciones 1–16 de la [guía](../../guias/Glosario-Tipos-de-Algoritmos-Programadores.md).

## Consigna

Implementá la API mínima del caso llamando a algoritmos ya probados. Validá tamaños
antes de ejecutar trabajo costoso y completá Postman. Mantené algoritmos puros sin
dependencias de controllers ni HTTP.

Prepará una matriz problema → estructura → precondiciones → algoritmo → tiempo →
memoria → prueba. Para tres decisiones compará una solución simple y otra optimizada.
Registrá un benchmark reproducible con calentamiento y varias muestras; JMH es una
extensión recomendable para profundizar, no una prueba de corrección.

## Experimentos y aceptación

- Cruce, Top K y ruta mínima cumplen contrato HTTP, resultados y límites.
- Datos inválidos no disparan trabajo ilimitado.
- Tests incluyen oráculos pequeños, invariantes y entradas generadas con semilla.
- mvn verify pasa y el JAR arranca.
- Separás tiempo de preparación, algoritmo y serialización al medir.
- Toda cifra incluye entorno, tamaño y distribución; no copiás tiempos garantizados de la guía.
- Identificás extensiones no implementadas: motor B-Tree/LSM, clusters y criptografía avanzada.
- Justificás cuándo mantener una solución sencilla aunque tenga peor complejidad asintótica.

## Entrega

Implementación, pruebas, análisis de tiempo/memoria y respuestas en
`ejercicios/respuestas/16.md`. Incluí precondiciones, casos límite e invariantes.

## Para explicar

¿Qué evidencia te haría cambiar de estructura al crecer el volumen o variar la distribución?

Volvé al [índice](README.md) para revisar el recorrido.

