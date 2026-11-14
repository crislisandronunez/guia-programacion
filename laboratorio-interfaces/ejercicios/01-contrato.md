# 01 — Firma y comportamiento del contrato

## Lectura

Secciones 1, 8, 12 de la [guía](../../guias/Glosario-Interface-Contratos.md).

## Consigna

Leé el caso y escribí una ficha de contrato antes de programar: cliente del contrato,
operación, entradas, precondiciones, resultado, postcondiciones, errores y efectos.
Elegí la política de descuento como primer ejemplo. Definí nulabilidad, precisión y
si se permite modificar los objetos recibidos.

Creá una interfaz y una implementación sencilla con Java puro. Invocala mediante
una variable del tipo de la interfaz. Compará interfaz, clase concreta y clase abstracta.
Explorá @FunctionalInterface con un contrato pequeño de un solo método abstracto,
sin forzar la política final a tener esa forma.

## Experimentos y aceptación

- El compilador verifica firmas, pero una prueba detecta una implementación que devuelve un descuento negativo.
- Las precondiciones y errores están documentados y probados.
- El resultado no es null y no altera las entradas.
- Distinguís implements de extends y contrato sintáctico de comportamiento.
- Comparás métodos abstractos, default y static mediante un experimento pequeño.
- Una clase no se considera implementación solo por tener métodos con los mismos nombres.

## Entrega

Código, pruebas y respuestas en `ejercicios/respuestas/01.md`.
Agregá documentación del contrato y solicitudes Postman cuando corresponda.

## Para explicar

¿Qué parte del contrato no puede comprobar el compilador de Java?

Siguiente: [Políticas puras de dominio](02-politicas.md).

