# 13 — OpenAPI y documentación del contrato

## Lectura

Secciones 17 de la [guía](../../guias/Glosario-Springboot-web.md).

## Consigna

Agregá springdoc-openapi-starter-webmvc-ui con versión explícita compatible con
Boot 3.5, consultando su matriz; no copies sin revisar la versión de la guía ni la
línea de Boot 4. Documentá rutas, DTO, paginación, PATCH propio, errores y multipart.

Incluí Location, ETag, If-Match y respuestas 304/412/428 donde correspondan.
Definí acceso a Swagger y /v3/api-docs en un perfil lab local, coherente con Security.
Las credenciales y tokens no se incluyen en ejemplos exportados.

## Experimentos y aceptación

- El documento generado coincide con métodos y campos realmente implementados.
- DTO de escritura no permite campos calculados por el servidor.
- Errores usan esquema ProblemDetail con extensiones documentadas.
- Multipart describe metadata JSON y archivo con sus límites.
- Las respuestas sin body no anuncian un DTO obligatorio.
- Documentar Basic o sesión no sustituye configurar autenticación real.
- Swagger funciona en el perfil previsto y el acceso fuera de él está definido.

## Entrega

Código, pruebas, solicitudes Postman y respuestas en
`ejercicios/respuestas/13.md`. Para vistas, agregá evidencia del comportamiento en navegador.

## Para explicar

¿Qué partes del contrato no puede deducir una herramienta solo leyendo anotaciones?

Siguiente: [Testing web y recorrido final](14-integracion.md).

