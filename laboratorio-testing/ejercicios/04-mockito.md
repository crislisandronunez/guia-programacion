# 04 — Dobles, stubbing y spies

## Lectura

Secciones 6, 8 de la [guía](../../guias/Glosario-Testing.md).

## Consigna

Implementá el servicio de creación con dependencias por constructor: catálogo,
repositorio, cotizador y Clock. Usá MockitoExtension y dobles solo para colaboradores,
manteniendo reales calculadora y objetos simples de dominio.

Probá éxito, producto ausente, tarifa no disponible y guardado fallido.
Compará mock, stub y fake por su función. Explorá un spy aislado y doReturn sin
aplicar mocking parcial al servicio que intentás comprobar.

## Experimentos y aceptación

- El servicio se instancia sin Spring y recibe todas sus dependencias.
- Un stub coincide con los argumentos reales y no devuelve null por accidente.
- El error del proveedor no se transforma en pedido exitoso.
- No queda stubbing innecesario oculto por lenient global.
- Un spy demuestra cuándo se ejecuta el método real al prepararlo.
- Un mock estático, si lo explorás, se cierra; preferís Clock inyectado para el caso real.

## Entrega

Pruebas y código mínimo del comportamiento, más evidencia y respuestas en
`ejercicios/respuestas/04.md`. Indicá qué falla si rompés la regla que estás verificando.

## Para explicar

¿Por qué mockear el método del servicio bajo prueba puede producir una prueba sin valor?

Siguiente: [Captura, interacciones y pruebas de capas](05-verificaciones.md).

