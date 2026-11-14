# 03 — Datos, parametrización y fixtures

## Lectura

Secciones 5, 18 de la [guía](../../guias/Glosario-Testing.md).

## Consigna

Convertí los casos de descuento en @ParameterizedTest con @CsvSource y @MethodSource.
Agregá nulos/vacíos donde el contrato los permita como entradas a rechazar.
Prepará un builder de pedidos válido por defecto, con cambios explícitos por test.

Explorá @TestFactory para una colección dinámica y @RepeatedTest para repetición acotada.
No uses repetir hasta pasar como corrección de flakiness.

## Experimentos y aceptación

- Cada fila tiene un esperado independiente de la implementación.
- La tabla cubre debajo, igual y encima del umbral.
- Se prueba HALF_EVEN con un empate decimal verificable a mano.
- Cambiar un builder no modifica objetos ya usados en otro test.
- Test dinámico y parametrizado tienen su ciclo de vida distinguido.
- Los fixtures no contienen fechas actuales ni azar sin semilla.

## Entrega

Pruebas y código mínimo del comportamiento, más evidencia y respuestas en
`ejercicios/respuestas/03.md`. Indicá qué falla si rompés la regla que estás verificando.

## Para explicar

¿Cuándo una tabla enorme de parámetros hace más difícil entender qué comportamiento falla?

Siguiente: [Dobles, stubbing y spies](04-mockito.md).

