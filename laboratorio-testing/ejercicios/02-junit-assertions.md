# 02 — JUnit, ciclo de vida y aserciones

## Lectura

Secciones 3–4 de la [guía](../../guias/Glosario-Testing.md).

## Consigna

Ampliá la calculadora con los importes y redondeo del caso. Usá @Nested, nombres
descriptivos y AAA. Compará assertEquals, assertThrows y AssertJ con mensajes útiles.
Elegí igualdad numérica o escala exacta para BigDecimal según lo que prometa cada método.

Explorá @BeforeEach, @BeforeAll y PER_CLASS con fixtures aisladas.
Usá @TempDir para una pequeña exportación de resultados que vos implementarás,
sin escribir en carpetas personales.

## Experimentos y aceptación

- Se prueban subtotal inválido, frontera de descuento y envío negativo.
- La excepción comprobada es específica y no un catch genérico que deja pasar todo.
- Una prueba no necesita que otra haya corrido antes.
- Los archivos se crean en el directorio temporal y se verifica su contenido.
- Aserciones agrupadas corresponden a un mismo comportamiento.
- Una prueba anidada no comparte estado mutable accidentalmente.

## Entrega

Pruebas y código mínimo del comportamiento, más evidencia y respuestas en
`ejercicios/respuestas/02.md`. Indicá qué falla si rompés la regla que estás verificando.

## Para explicar

¿Cuándo comparar 10.0 y 10.00 como números ocultaría un incumplimiento del formato prometido?

Siguiente: [Datos, parametrización y fixtures](03-parametrizados.md).

