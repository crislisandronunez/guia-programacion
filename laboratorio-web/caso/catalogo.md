# Caso: catálogo de artículos del centro Horizonte

Un centro de formación publica artículos y permite que editores los administren por
API o formulario. Todo el contenido y las identidades son ficticios.

## Modelo a implementar

Artículo: UUID, slug, titulo, resumen, categoria, etiquetas, estado, creadoEn,
actualizadoEn y version. Estados BORRADOR y PUBLICADO. Categorías JAVA, SPRING y DATOS.
Título de 1 a 120 caracteres; resumen de 1 a 2000. Slug de 3 a 80 caracteres ASCII
minúsculos, dígitos y guiones, sin guiones al inicio/final ni repetidos.
Máximo cinco etiquetas, cada una de 1 a 30 caracteres, normalizadas y sin duplicados.
Usá Instant UTC y Clock inyectable. Slug único de forma atómica.

No hay HTML enriquecido: título y resumen son texto plano.
Para crear, el cliente envía los campos editables; el servidor asigna ID, fechas,
estado BORRADOR y versión. Los errores no deben guardar cambios parciales.

## Contrato API

| Operación | Resultado |
|---|---|
| POST /api/v1/articulos | 201, Location y DTO |
| GET /api/v1/articulos/{id} | 200 con DTO; 404 si no existe |
| GET /api/v1/articulos | 200 con contenido y paginación |
| PUT /api/v1/articulos/{id} | 200, reemplazo de todos los campos editables |
| PATCH /api/v1/articulos/{id} | 200, cambio parcial de titulo/resumen |
| DELETE /api/v1/articulos/{id} | 204 sin cuerpo, también si ya no existe |
| POST /api/v1/articulos/{id}/publicacion | 200 con PUBLICADO |

POST de publicación sobre un artículo ya publicado devuelve su representación sin
cambiarlo de nuevo. No se permite crear mediante PUT sobre un ID inexistente.
PUT requiere slug, titulo, resumen, categoria y etiquetas; no cambia estado ni creadoEn.
PATCH usa JSON parcial propio, no JSON Patch estándar: ausencia conserva el valor,
null explícito se rechaza y un objeto vacío no cambia nada.
Campos desconocidos en comandos de escritura se rechazan, incluyendo id y version.

Listado: pagina desde 0, tamanio de 1 a 50 (defecto 10), categoria opcional y q de hasta
100 caracteres. q busca por título sin distinguir mayúsculas, con regla Locale.ROOT.
Orden permitido: titulo,asc o creadoEn,desc; ID canónico ascendente como desempate.
Respuesta con contenido, pagina, tamanio y totalElementos. Página fuera de rango: lista vacía.

Entrada inválida: 400; slug duplicado: 409; media type no soportado: 415;
Accept incompatible: 406; método no permitido: 405. Conservá headers HTTP pertinentes.
Errores propios con ProblemDetail y extensiones codigo, correlationId y errores de campo.

## Versiones HTTP, desde la etapa 07

GET individual incorpora ETag. Si If-None-Match coincide, responde 304 sin cuerpo.
PUT y PATCH requieren If-Match: ausente 428, versión desactualizada 412.
La comparación y actualización son atómicas. Un cambio real incrementa versión;
un no-op conserva versión y actualizadoEn. Publicar también cambia versión la primera vez.
DELETE mantiene el contrato incondicional indicado arriba.
Usá Cache-Control: private, no-cache en las representaciones para esta práctica.

## Vistas y archivos

/web/articulos lista artículos y /web/articulos/nuevo muestra un formulario.
El formulario válido redirige a un GET después de crear; inválido vuelve con errores.
La vista no imprime texto de usuarios sin escape.

Un artículo admite adjuntos de texto UTF-8, máximo 1 MiB cada archivo y 2 MiB por request.
Upload multipart: POST /api/v1/articulos/{id}/adjuntos con parte archivo y metadata JSON
(descripcion opcional hasta 200 caracteres). 201 y Location del adjunto.
Descarga: GET /api/v1/articulos/{id}/adjuntos/{adjuntoId}, por identificadores, como attachment.
No uses el nombre original para decidir la ubicación en disco. Vacío: 400; tamaño excesivo:
413; formato no permitido: 415; adjunto inexistente o de otro artículo: 404.

## Integración externa y seguridad

GET /api/v1/recomendaciones?categoria=JAVA consulta un proveedor local simulado.
Respuesta normal: 200; error de proveedor o conexión: 503; timeout: 504;
respuesta inválida: 502. El consumidor no puede suministrar una URL arbitraria.

Hasta la etapa 12 todo es local sin autenticación. Desde allí:
- health y /public/info son públicos.
- Lecturas de catálogo y descargas requieren LECTOR o EDITOR.
- Escrituras y formulario nuevo requieren EDITOR.
- ana tiene ROLE_LECTOR; editor tiene ROLE_EDITOR.
- Sesión/formulario y Basic de práctica conservan CSRF.
- API anónima devuelve 401 JSON; usuario sin permiso, 403.
Los tests previos se actualizan con identidad y CSRF donde corresponda.
No hace falta un emisor JWT ni registro público para este recorrido.

