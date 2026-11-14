# 09 — Migración de planillas y sistema legado

## Objetivo
Leé la sección 13 de la [guía](../../guias/Resolucion-de-Problemas.md).

## Consigna
Usá los CSV de datos/ como exportaciones ficticias de una planilla. Implementá
POST /api/importaciones/clientes y GET /api/importaciones/{id}.
Validá encabezados, UTF-8, tipos, obligatorios, duplicados y tamaño máximo.
Procesá por filas; reportá número de línea, columna y causa sin devolver stack traces.

Ofrecé dry-run sin escrituras y confirmación del mismo contenido por su huella.
Elegí importación parcial de filas válidas con resultado por fila. En el archivo de
errores, la primera aparición válida de C-004 se acepta y la repetida se rechaza.
Reimportar el mismo lote no vuelve a crear clientes. Un archivo cambiado es otro lote.

Construí un adaptador que traduzca COD_CLI_X, NOM_RAZ_SOC, EST_HAB_01 y SAL_CTA_CTE
del ERP simulado al dominio. Definí un formato decimal preciso; rechazá valores ambiguos
en lugar de reemplazar separadores a ciegas.

Implementá una fachada que derive un caso de uso al módulo nuevo con un feature flag.
Documentá autoridad de cada dato antes y después del corte. El CSV migra datos maestros;
la deuda vigente sigue siendo autoridad del ERP simulado según el caso.

## Aceptación
- Archivo válido importa tres clientes; inválido informa filas sin HTTP 500 genérico.
- Dry-run no modifica tablas.
- Repetición y recuperación tras caída no duplican filas.
- Códigos legados no aparecen en entidades del dominio.
- Volver al flujo anterior tiene un plan de compatibilidad de datos, no solo apagar el flag.
- Prepará una lista de chequeos de corte y reversión.

Siguiente: [10](10-incidentes.md).
