# 01 — Estrategia, oráculos y primer ciclo TDD

## Lectura

Secciones 1–2, 21 de la [guía](../../guias/Glosario-Testing.md).

## Consigna

Leé el caso y prepará una matriz requisito → riesgo → tipo de prueba → oráculo.
Elegí el cálculo de descuento como primera unidad. Escribí una prueba roja, implementá
lo mínimo para pasarla y refactorizá. Documentá el ciclo sin dejar tests rotos al entregar.

Clasificá el smoke test existente: carga contexto, servidor HTTP real y consulta health.
No lo uses como evidencia de pedidos, persistencia ni autenticación.

## Experimentos y aceptación

- La primera prueba falla por ausencia del comportamiento esperado, no por un error ajeno al requisito.
- La implementación mínima pasa y la refactorización conserva el resultado.
- Identificás casos normales, fronteras y fallos de dependencias.
- Explicás qué decisiones valida cada nivel y cuáles quedan afuera.
- No copiás el algoritmo productivo en el cálculo del esperado.
- El build final queda verde con pruebas efectivamente ejecutadas.

## Entrega

Pruebas y código mínimo del comportamiento, más evidencia y respuestas en
`ejercicios/respuestas/01.md`. Indicá qué falla si rompés la regla que estás verificando.

## Para explicar

¿Qué diferencia hay entre un test que ejecuta una línea y uno que detecta que está mal?

Siguiente: [JUnit, ciclo de vida y aserciones](02-junit-assertions.md).

