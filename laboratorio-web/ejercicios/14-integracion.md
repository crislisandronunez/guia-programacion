# 14 — Testing web y recorrido final

## Lectura

Secciones 18–20 de la [guía](../../guias/Glosario-Springboot-web.md).

## Consigna

Completá pruebas unitarias del servicio, @WebMvcTest con dependencias simuladas y
una integración RANDOM_PORT con repositorio real en memoria. Usá el stub solo para
el proveedor externo y un directorio temporal para archivos.

Completá Postman y un recorrido en navegador: crear, listar, editar condicionalmente,
publicar, subir/descargar adjunto y borrar. Prepará tabla operación → método → entrada →
status → headers → prueba. Ejecutá mvn verify y el JAR empaquetado.

## Experimentos y aceptación

- MockMvc verifica binding, JSON, headers y errores; el servidor real verifica transporte y multipart.
- El flujo funciona con y sin context-path según la configuración.
- Dos editores con ETag antiguo no pierden actualizaciones.
- Sin permiso o con entrada inválida no se escriben cambios.
- Vistas escapan contenido y mantienen CSRF.
- Postman tiene casos negativos además del camino feliz.
- El JAR sirve templates/estáticos desde classpath.
- Documentás memoria efímera, archivos huérfanos y extensiones no implementadas
  como JPA, WebFlux completo, WebSocket, TLS real o JWT.

## Entrega

Código, pruebas, solicitudes Postman y respuestas en
`ejercicios/respuestas/14.md`. Para vistas, agregá evidencia del comportamiento en navegador.

## Para explicar

¿Qué comportamiento puede pasar en un slice y fallar al ejecutar el servidor completo?

Volvé al [índice](README.md) para revisar cobertura.

