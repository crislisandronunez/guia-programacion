# 03 — Puertos de salida y adaptadores en memoria

## Lectura

Secciones 3, 6–8 de la [guía](../../guias/Glosario-Interface-Contratos.md).

## Consigna

Diseñá puertos para obtener cliente, consultar catálogo y guardar/buscar/listar
presupuestos. Elegí domain o application para cada contrato según quién lo necesita
y documentá la decisión. Usá Optional para ausencia y excepciones propias para fallos.

Implementá los adaptadores en memoria bajo infrastructure/adapter/out. Definí listado
con pagina, tamanio, elementos y total mediante tipos propios. Para el desempate por
UUID usá el orden lexicográfico de su representación canónica y conservá esa definición
al implementar SQL. La inserción por ID debe ser atómica.

## Experimentos y aceptación

- Guardar y consultar conserva importes, líneas, identidad y fechas.
- Un ID ausente devuelve Optional.empty; un error simulado no se oculta como ausencia.
- Dos inserciones concurrentes con el mismo ID dan un éxito y un conflicto.
- El llamador no modifica datos almacenados mutando una colección devuelta.
- Páginas vacías, límites y desempate tienen resultados definidos.
- Ningún puerto devuelve ResponseEntity, JpaRepository, Page, ResultSet o DTO del proveedor.
- El nombre del puerto expresa una necesidad del negocio, no una marca de tecnología.

## Entrega

Código, pruebas y respuestas en `ejercicios/respuestas/03.md`.
Agregá documentación del contrato y solicitudes Postman cuando corresponda.

## Para explicar

¿Por qué un adaptador en memoria también necesita cumplir reglas de concurrencia y copia?

Siguiente: [Puertos de entrada y orquestación](04-casos-uso.md).

