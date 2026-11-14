# 01 — Entender antes de programar

## Objetivo
Estudiá las secciones 1–3, 5, 15 y 17 de la
[guía](../../guias/Resolucion-de-Problemas.md). Separá síntomas, causas e hipótesis.

## Consigna
Con el caso de Horizonte, prepará:
1. Mapa de interesados y cinco preguntas para cada área.
2. Un análisis de cinco porqués sobre la sobreventa. Marcá como hipótesis lo no comprobado.
3. Diagramas As-Is y To-Be con responsables y puntos de fallo.
4. Ocho requisitos funcionales y cuatro no funcionales verificables.
5. Un glosario de diez términos y una tabla de fuentes de verdad por dato.
6. MVP y exclusiones: justificá por qué empezar con un monolito modular.
7. Matriz problema → regla → solución propuesta → prueba → indicador.

## Experimento
Compará tres alternativas para evitar sobreventa: control manual, bloqueo en memoria
y actualización atómica en PostgreSQL. Explicá qué pasa con dos instancias.
No elijas microservicios únicamente por una expectativa vaga de crecimiento.

## Aceptación
- Cada requisito incluye un ejemplo y un caso borde.
- Hay criterios concretos para deuda, stock, duplicados y proveedor caído.
- Los objetivos de rendimiento incluyen carga, dataset y entorno.
- No confundís el SLO interno con un compromiso contractual.
- Las métricas ficticias del caso no se presentan como mediciones reales.
- Entregás un ADR con alternativas, elección, consecuencias y cuándo revisarla.

## Pregunta
¿Una API que responde rápido pero sigue aprobando clientes con deuda resolvió el problema?

Siguiente: [02](02-dominio.md).
