# 05 — MongoTemplate y filtros paginados

## Lectura
Secciones 10, 13 de la [guía](../../guias/Glosario-Spring-MongoDB.md).

## Consigna
Implementá GET /api/productos con filtros opcionales combinados mediante Query/Criteria.
Validá filtros y sort permitido; size máximo 100. Usá precio y _id como orden estable.
Compará Page y Slice, midiendo si se realiza conteo.

Agregá búsqueda por cursor con el último precio/ID recibido. Definí dirección y validá
que el cursor corresponda a filtros/orden del request. Mantené DTO de salida.
No confundas cursor de paginación HTTP con un cursor de streaming del driver.

## Experimentos y aceptación
- Cada filtro y combinaciones tienen tests sobre dataset conocido.
- Empates se ordenan de manera determinista.
- Sobre datos estables, recorrer páginas no repite ni omite documentos.
- skip/limit profundo se compara con keyset usando índices apropiados.
- Inserciones concurrentes tienen una semántica documentada; no prometas snapshot implícito.
- El endpoint rechaza página negativa, size excesivo y campos de sort no permitidos.

## Para explicar
¿Cuándo conviene evitar count exacto para mostrar solamente si hay una página siguiente?

Siguiente: [06](06-updates.md).
