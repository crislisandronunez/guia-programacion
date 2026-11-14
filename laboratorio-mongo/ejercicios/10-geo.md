# 10 — Consultas geoespaciales

## Lectura
Secciones 16 de la [guía](../../guias/Glosario-Spring-MongoDB.md).

## Consigna
Modelá Sucursal con GeoJsonPoint e índice 2dsphere. Implementá POST /api/sucursales
y GET /api/sucursales/cercanas con longitud, latitud, radio en metros y límite máximo.
Usá las coordenadas del caso y validá rangos [-180,180] y [-90,90].

Consultá cercanía con operadores apropiados o $geoNear, especificando unidades y
comportamiento de resultados. Si usás $geoNear, respetá su posición requerida en el
pipeline y colocá filtros compatibles en su opción query.

## Experimentos y aceptación
- El punto se almacena como [longitud, latitud], no al revés.
- El índice está creado y la búsqueda devuelve orden por distancia.
- Punto coincidente tiene distancia aproximadamente cero, usando tolerancia.
- Un radio pequeño excluye la sucursal lejana; un radio amplio incluye ambas.
- Radio negativo, enorme o coordenadas inválidas devuelve 400.
- Resultados y unidades coinciden entre mongosh y Java.

## Para explicar
¿Por qué un índice común sobre dos números no sustituye un índice geoespacial?

Siguiente: [11](11-gridfs.md).
