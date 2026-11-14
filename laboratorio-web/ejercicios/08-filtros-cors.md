# 08 — Filtros, interceptores y CORS

## Lectura

Secciones 10 de la [guía](../../guias/Glosario-Springboot-web.md).

## Consigna

Implementá un filtro de correlationId con formato/longitud acotados y generación
cuando falte. Agregá un interceptor para medir el handler y compará su alcance con
el filtro, incluyendo rutas no mapeadas y errores.

Configurá CORS para http://localhost:5173, métodos y headers necesarios.
Incluí If-Match y exponé ETag/Location/correlationId cuando el cliente de otro origen
necesite leerlos. No uses CORS como autenticación.

## Experimentos y aceptación

- CorrelationId se devuelve y el MDC se limpia incluso ante excepción.
- Un ID con saltos de línea o longitud excesiva no se copia a logs/headers.
- Preflight permitido funciona y no modifica datos.
- Origen no permitido no recibe permiso CORS.
- Probás desde navegador: Postman no aplica las restricciones del navegador.
- Allow-Credentials no se combina con origen comodín.
- Al incorporar Security se preserva el procesamiento adecuado del preflight.

## Entrega

Código, pruebas, solicitudes Postman y respuestas en
`ejercicios/respuestas/08.md`. Para vistas, agregá evidencia del comportamiento en navegador.

## Para explicar

¿Qué observa un filtro que un interceptor MVC puede no llegar a observar?

Siguiente: [Vistas, formularios y recursos estáticos](09-vistas-formularios.md).

