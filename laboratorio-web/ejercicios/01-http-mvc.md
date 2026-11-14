# 01 — HTTP y recorrido MVC

## Lectura

Secciones 1–3 de la [guía](../../guias/Glosario-Springboot-web.md).

## Consigna

Ejecutá la base y observá una petición/respuesta cruda con Postman o curl.exe.
Implementá GET /public/info con nombre y versión del catálogo. Compará @Controller,
@RestController y @ResponseBody en ejemplos pequeños sin rutas ambiguas.

Dibujá cliente → filtros → DispatcherServlet → mapping/adapter → interceptor →
controller → servicio. Distinguí ViewResolver y HttpMessageConverter.

## Experimentos y aceptación

- Health y /public/info responden 200 con Content-Type y cuerpo esperados.
- Identificás path, query, headers y body sin mezclarlos.
- Una ruta inexistente no se confunde con servidor apagado.
- La prueba usa puerto aleatorio, no depende de 8092.
- No agregás @EnableWebMvc sin analizar qué auto-configuración de Boot reemplaza.

## Entrega

Código, pruebas, solicitudes Postman y respuestas en
`ejercicios/respuestas/01.md`. Para vistas, agregá evidencia del comportamiento en navegador.

## Para explicar

¿Por qué devolver un String desde @Controller puede intentar resolver una vista?

Siguiente: [CRUD, capas y mapeos](02-crud.md).

