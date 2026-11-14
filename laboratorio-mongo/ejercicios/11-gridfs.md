# 11 — Archivos con GridFS

## Lectura
Secciones 17 de la [guía](../../guias/Glosario-Spring-MongoDB.md).

## Consigna
Implementá carga, consulta de metadatos, descarga y eliminación de adjuntos.
Usá GridFsTemplate o GridFSBucket, streams y metadatos de propietario/recurso.
Límite didáctico 20 MiB; aceptá tipos de archivo definidos y nombre saneado.
La API retorna ID, tamaño y tipo, nunca rutas del sistema.

Probá un archivo pequeño y uno mayor al límite de documento BSON de 16 MiB.
No cargues todo el archivo en un byte[] ni lo incrustes en Producto.
GridFS divide el archivo en chunks; inspeccioná fs.files y fs.chunks.

## Experimentos y aceptación
- Hash del archivo descargado coincide con el original.
- El límite de la aplicación produce 413 sin dejar carga presentada como completa.
- Descargar ID inexistente devuelve 404.
- Eliminar por ID borra metadatos y chunks correspondientes.
- Dos archivos con igual nombre tienen IDs diferentes.
- Fallo de subida y adjuntos huérfanos tienen estrategia de limpieza.
- No asumas transacción multi-documento de GridFS: coordiná vínculo con pedido mediante
  estado pendiente/confirmado y recuperación.

## Para explicar
¿Cómo impedirías que un usuario descargue adjuntos ajenos al agregar autenticación?

Siguiente: [12](12-integracion.md).
