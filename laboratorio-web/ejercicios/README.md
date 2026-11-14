# Consignas de Spring Boot Web

| Etapa | Consigna | Secciones de la guía |
|---|---|---|
| 01 | [HTTP y recorrido MVC](01-http-mvc.md) | 1–3 |
| 02 | [CRUD, capas y mapeos](02-crud.md) | 4, 6–7, 19 |
| 03 | [Parámetros, filtros y paginación](03-entradas-listados.md) | 5, 7 |
| 04 | [Binding y validación](04-validacion.md) | 5, 8 |
| 05 | [Errores consistentes con ProblemDetail](05-errores.md) | 9 |
| 06 | [JSON, DTO y actualización parcial](06-json-patch.md) | 5–6, 11 |
| 07 | [ETag y operaciones condicionales](07-condicionales.md) | 6–7 |
| 08 | [Filtros, interceptores y CORS](08-filtros-cors.md) | 10 |
| 09 | [Vistas, formularios y recursos estáticos](09-vistas-formularios.md) | 12–13 |
| 10 | [Multipart y descarga segura](10-archivos.md) | 5–6, 13 |
| 11 | [Cliente HTTP y respuestas asíncronas](11-http-asincrono.md) | 6, 14 |
| 12 | [Configuración y seguridad web](12-config-seguridad.md) | 3, 15–16 |
| 13 | [OpenAPI y documentación del contrato](13-openapi.md) | 17 |
| 14 | [Testing web y recorrido final](14-integracion.md) | 18–20 |

## Método

Leé el [caso del catálogo](../caso/catalogo.md). Las soluciones quedan a cargo del estudiante.
La base solo contiene arranque, configuración mínima y su smoke test.

1. Leé las secciones y escribí el contrato de la operación.
2. Implementá la parte indicada y verificá con tests.
3. Observá método, headers, status y body en Postman o navegador.
4. Probá errores y ausencia de efectos parciales.
5. Guardá decisiones y evidencia en ejercicios/respuestas/NN.md.

## Convenciones

- Java 21, Boot 3.5.16, MVC y paquete com.estudio.web.
- DTO HTTP separados del modelo; controllers finos y reglas en servicios.
- Repositorio en memoria con operaciones atómicas para slug/versiones.
- UUID, Instant, Clock inyectable y listas con límites.
- API /api/v1, HTML /web y recurso público /public/info.
- Tests unitarios, MockMvc y HTTP real según lo que se desea demostrar.
- Puertos aleatorios, directorios temporales y datos ficticios en pruebas.
- No crear clases o plantillas vacías por anticipado: agregalas al resolver cada etapa.
- No exponer archivos de práctica ni configuración interna como recursos estáticos.

## Evolución y dependencias

La base contiene Web, Validation, Actuator y starter-test.
Thymeleaf se incorpora en 09; herramienta de stub HTTP en 11; Security y security-test
en 12; springdoc compatible en 13. No se necesita JPA ni Docker.

Antes de 07 las escrituras no requieren ETag. Desde 07 actualizá pruebas y Postman
con If-Match para PUT/PATCH. Desde 12 agregá identidad y CSRF donde corresponda.
No interpretes como regresión inesperada un cambio explícito del contrato didáctico.

## Cobertura

Las veinte secciones de la guía tienen etapas asociadas. Matrix variables, Feign,
RestTemplate, SSE y WebFlux completo se comparan o exploran sin exigir implementarlos
todos. El cliente principal es RestClient y la aplicación sigue siendo MVC.

Contrastá [errores ProblemDetail](https://docs.spring.io/spring-framework/reference/6.2/web/webmvc/mvc-ann-rest-exceptions.html)
y [multipart](https://docs.spring.io/spring-framework/reference/6.2/web/webmvc/mvc-controller/ann-methods/multipart-forms.html)
con la documentación compatible. Para API docs consultá la [matriz de springdoc](https://springdoc.org/).

