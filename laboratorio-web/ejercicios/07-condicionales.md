# 07 — ETag y operaciones condicionales

## Lectura

Secciones 6–7 de la [guía](../../guias/Glosario-Springboot-web.md).

## Consigna

Agregá ETag al GET individual y soporte If-None-Match. Definí un validador fuerte
que cambie cuando cambie la representación; elegí serialización y variante coherentes.
Agregá If-Match a PUT/PATCH con comparación y escritura atómicas.

Mantené no-op sin incrementar versión. Probá dos clientes editando a partir del mismo
ETag y documentá el flujo de recargar tras conflicto.

## Experimentos y aceptación

- GET con ETag coincidente responde 304 sin body.
- Cambio real devuelve un ETag nuevo.
- Falta If-Match da 428 y versión antigua da 412 sin escribir.
- Dos escrituras concurrentes con el mismo validador no se pisan silenciosamente.
- Comprobás formatos válidos, listas de tags y wildcard según semántica HTTP soportada.
- Cache-Control privado evita tratar contenido autenticado como público.
- ETag no se confunde con una autorización para acceder al recurso.

## Entrega

Código, pruebas, solicitudes Postman y respuestas en
`ejercicios/respuestas/07.md`. Para vistas, agregá evidencia del comportamiento en navegador.

## Para explicar

¿Qué problema evita If-Match que no resuelve validar únicamente el DTO?

Siguiente: [Filtros, interceptores y CORS](08-filtros-cors.md).

