# 10 — Multipart y descarga segura

## Lectura

Secciones 5–6, 13 de la [guía](../../guias/Glosario-Springboot-web.md).

## Consigna

Implementá adjuntos y metadata JSON con @RequestPart. Configurá límites por archivo
y request. Guardá usando un identificador generado en local/uploads; el nombre original
solo sirve como metadata limitada, nunca como ruta. Validá UTF-8 y rechazos del caso.

Descargá por artículo/adjunto, con Resource o streaming, Content-Type y Content-Disposition
construidos de forma segura. No publiques uploads como recursos estáticos.
En tests usá @TempDir y verificá limpieza tras fallos.

## Experimentos y aceptación

- Upload válido devuelve 201 y una descarga con los mismos bytes.
- Vacío da 400; tamaño excesivo 413; formato inválido 415.
- Nombre con ../, rutas absolutas o separadores no escapa del directorio configurado.
- El nombre enviado en Content-Disposition no permite inyección de headers.
- Un adjunto asociado a otro artículo no se descarga.
- Se comprueban límites multipart con servidor real: MockMultipartFile no demuestra el límite del contenedor.
- Fallo de escritura no deja metadata de éxito ni archivos parciales.
- Documentás archivos huérfanos después de perder metadata en memoria.

## Entrega

Código, pruebas, solicitudes Postman y respuestas en
`ejercicios/respuestas/10.md`. Para vistas, agregá evidencia del comportamiento en navegador.

## Para explicar

¿Por qué validar solo extensión o Content-Type enviado por el cliente no verifica el contenido?

Siguiente: [Cliente HTTP y respuestas asíncronas](11-http-asincrono.md).

