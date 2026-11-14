# 07 — Contrato web con MockMvc

## Lectura

Secciones 10–11 de la [guía](../../guias/Glosario-Testing.md).

## Consigna

Implementá controllers, DTO validados y errores consistentes. Usá @WebMvcTest
con el controller real y @MockitoBean del caso de uso. Reutilizá los fixtures JSON.
Probá contenido válido, sintaxis rota, campos inválidos y excepciones de negocio.

El propietario viene del colaborador de identidad de práctica, no del cuerpo HTTP.
Compará standaloneSetup con el slice y documentá qué configuración no carga.

## Experimentos y aceptación

- Crear devuelve 201, Location y cuerpo con valores esperados.
- Errores 400,404,409,502,503 tienen código y mensaje sin stacktrace.
- Content-Type incorrecto se distingue de JSON inválido.
- Se valida pagina/tamanio y contrato de listados vacíos.
- Capturás el comando recibido por el caso de uso.
- Para entrada inválida el caso de uso no se invoca.
- No afirmás persistencia porque un controller con servicio simulado respondió 201.

## Entrega

Pruebas y código mínimo del comportamiento, más evidencia y respuestas en
`ejercicios/respuestas/07.md`. Indicá qué falla si rompés la regla que estás verificando.

## Para explicar

¿Qué prueba detectaría un error del servicio que el slice web no puede ver?

Siguiente: [Integración HTTP y estado real](08-http-real.md).

