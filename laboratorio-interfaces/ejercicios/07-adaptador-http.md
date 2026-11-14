# 07 — Traducción de un proveedor HTTP

## Lectura

Secciones 6, 8, 11 de la [guía](../../guias/Glosario-Interface-Contratos.md).

## Consigna

Agregá un adaptador de catálogo contra un servidor HTTP de pruebas local e independiente.
Elegí WireMock, MockWebServer u otra herramienta compatible y fijá su dependencia.
El proveedor devuelve codigo_articulo y precio_centavos; tu dominio conserva SKU y
BigDecimal. Los DTO técnicos permanecen dentro del adaptador.

Definí contrato remoto de prueba: 200 válido, 404 de artículo, 503, demora y JSON
inválido. Configurá timeouts finitos. Traducí cada caso a resultado o error del puerto
sin filtrar excepciones del cliente HTTP a la aplicación.

## Experimentos y aceptación

- 1000 centavos se transforma exactamente en ARS 10.00.
- No se aceptan centavos negativos, campos obligatorios ausentes ni moneda incompatible.
- 404 remoto produce producto ausente; 503/timeout produce indisponibilidad.
- JSON inválido produce error de contrato remoto, traducido a 502 en HTTP de entrada.
- Tests del caso de uso siguen iguales al cambiar el adaptador.
- El test de adapter usa HTTP real local y verifica ruta y transformación.
- El dominio no importa el DTO, RestClient ni HttpStatus del proveedor.

## Entrega

Código, pruebas y respuestas en `ejercicios/respuestas/07.md`.
Agregá documentación del contrato y solicitudes Postman cuando corresponda.

## Para explicar

¿Por qué devolver un Optional vacío ante cualquier excepción destruye información del contrato?

Siguiente: [Persistencia JPA sin contaminar el núcleo](08-adaptador-jpa.md).

