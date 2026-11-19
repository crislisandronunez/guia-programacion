# Consignas de lógica de programación

Leé el [caso del centro de envíos](../caso/centro-de-envios.md) antes de empezar. Las implementaciones y las pruebas de negocio todavía no existen: son parte de tu trabajo.

| Etapa | Consigna | Secciones de la guía 02 |
|---|---|---|
| 01 | [Contrato y traza](01-contrato.md) | 1–5, 11, 13 |
| 02 | [Condiciones y rangos](02-condiciones.md) | 6–7, 13 |
| 03 | [Bucle y acumulador](03-acumulador.md) | 5, 8–9 |
| 04 | [Contador con umbral](04-contador.md) | 3, 7–9 |
| 05 | [Primera coincidencia](05-primera-coincidencia.md) | 8–9, 11–12, 17 |
| 06 | [Todos cumplen y validación](06-todos-cumplen.md) | 6, 8–9, 13 |
| 07 | [Mayor sin valor inventado](07-mayor.md) | 5, 9, 13 |
| 08 | [Resumen de un día](08-resumen.md) | 4, 9–10, 12, 18 |
| 09 | [Diagnóstico de fallos](09-diagnostico.md) | 11, 13–14 |
| 10 | [Invariante y terminación](10-invariante.md) | 8, 11–12 |
| 11 | [Corrección y costo](11-costo.md) | 12, 15 |
| 12 | [Integración del caso](12-integracion.md) | 1–20 |

## Forma de trabajo

1. Repasá las secciones indicadas de la [guía 02](../../guias/02-Glosario-Logica-de-Programacion-y-Resolucion-de-Problemas.md) y el contrato de la etapa.
2. Antes del código, escribí un ejemplo pequeño con resultado esperado, pseudocódigo y una traza. En etapas posteriores agregá la invariante y el costo cuando se pida.
3. Implementá el método en `src/main/java/com/estudio/logica` y sus pruebas JUnit en `src/test/java/com/estudio/logica`.
4. Ejecutá `mvn verify` o el objetivo **verify** de Maven en IntelliJ.
5. Guardá las explicaciones y los contraejemplos en `ejercicios/respuestas/NN.md`.

Las pruebas son entregables del estudiante. Un ejemplo de la consigna indica el comportamiento esperado, pero no sustituye pruebas para límites, ausencia y entradas inválidas. Los ejemplos de código deliberadamente defectuoso aparecen solo en las consignas: no forman parte del proyecto que compila.

## Convenciones

- Java 21 y JUnit 5, sin Spring ni servicios externos.
- Usá `IllegalArgumentException` para entradas inválidas según el [caso](../caso/centro-de-envios.md).
- Usá `long` para totales de paquetes; para el mayor de una secuencia posiblemente vacía usá `OptionalInt`.
- No alteres los arreglos recibidos ni sustituyas la lógica pedida por `Stream`, `Arrays` u otra operación que esconda el recorrido. Podés usarlos en pruebas como comparación.
- En las etapas con retorno temprano, validá el arreglo completo antes de devolver un resultado. Así la política de errores no depende de la posición de un dato inválido.
- Evitá pruebas con tiempos de ejecución rígidos. El costo se fundamenta contando recorridos y comparaciones.

