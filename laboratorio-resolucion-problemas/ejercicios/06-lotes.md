# 06 — Lotes, conciliación y auditoría

## Objetivo
Leé las secciones 10 y 11 de la [guía](../../guias/Resolucion-de-Problemas.md).

## Consigna
Creá un cierre diario que compute pedidos despachados e importes por vendedor.
Definí zona America/Argentina/Buenos_Aires y rango [inicio, siguiente inicio), convertido
a UTC para consultar. Usá identificador único de lote por fecha y tipo.

Procesá por bloques con checkpoints. Dos instancias del scheduler deben coordinarse
mediante reclamo persistido; @Scheduled por sí solo no evita duplicados.
Una ejecución interrumpida retoma sin sumar dos veces una fila.

Registrá auditoría append-only con actor, entidad, transición, instante, motivo y
correlationId. El actor procede de una identidad autenticada o de un job identificado;
un header libre no demuestra quién cambió un dato. Incorporá seguridad local al habilitar
operaciones administrativas. Configurá credenciales fuera de Git.

Conciliá el cierre con un CSV ficticio de despacho: detectá faltantes, duplicados y diferencias
de cantidad. Generá incidencias, sin editar stock automáticamente desde un archivo.

## Aceptación
- Ejecución repetida o concurrente deja un solo resultado por fecha.
- Fallo a mitad de lote permite recuperación con totales exactos.
- Casos en los límites de medianoche se asignan al día correcto.
- Cada cambio de estado se reconstruye desde su auditoría; no se borran pedidos para ocultarlo.
- Archivo repetido no duplica incidencias; filas inválidas indican línea y motivo.
- Cliente sin privilegios no ejecuta cierres ni altera auditoría.

## Pregunta
¿Por qué createdAt y updatedAt no alcanzan para explicar una secuencia de cambios?

Siguiente: [07](07-outbox.md).
