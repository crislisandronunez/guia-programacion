# 09 — Vistas, formularios y recursos estáticos

## Lectura

Secciones 12–13 de la [guía](../../guias/Glosario-Springboot-web.md).

## Consigna

Agregá Thymeleaf y construí listado y formulario del caso con @Controller,
Model, @ModelAttribute y BindingResult. Usá DTO de formulario acotado.
Aplicá Post/Redirect/Get después de guardar y mensajes flash de confirmación.

Agregá un CSS en static y preferencia de idioma por cookie. Compará recurso estático,
template y endpoint JSON. La vista debe invocar los mismos servicios que la API.

## Experimentos y aceptación

- Un formulario válido crea y redirige a GET sin reenviar el POST al refrescar.
- Uno inválido conserva entradas y muestra errores.
- BindingResult está asociado al objeto correcto.
- Texto con caracteres HTML se muestra escapado.
- Campos de propietario/estado/ID no se asignan por binding masivo.
- Recursos y enlaces funcionan con context-path.
- Tras agregar Security, el formulario incluye CSRF y mantiene su flujo.

## Entrega

Código, pruebas, solicitudes Postman y respuestas en
`ejercicios/respuestas/09.md`. Para vistas, agregá evidencia del comportamiento en navegador.

## Para explicar

¿Por qué una redirección cambia la petición del navegador y un forward no hace lo mismo?

Siguiente: [Multipart y descarga segura](10-archivos.md).

