# 08 — Transacciones y cambios masivos

## Lectura
Secciones 14 y 15 de la [guía](../../guias/Glosario-Spring-Data-JPA.md).

## Consigna
Implementá confirmar y cancelar pedido en servicios @Transactional.
Confirmar valida cliente, descuenta stock físico de todas las líneas y cambia estado.
Cancelar devuelve stock una sola vez. Fallar en cualquier línea revierte el conjunto.

Prepará casos de RuntimeException, excepción checked y rollbackFor explícito.
Probá self-invocation y refactorizá para cruzar el proxy. Compará REQUIRED con
REQUIRES_NEW mediante una operación de registro separada, identificando commits independientes.

Agregá una actualización masiva de precio con @Modifying. Observá entidades cargadas
antes del bulk: flush/clear o refresh deben impedir leer datos obsoletos.
El bulk no ejecuta automáticamente todos los callbacks ni el control @Version por fila;
definí su política de concurrencia antes de usarlo.

## Aceptación
- Fallo en segundo producto revierte descuento del primero.
- flush seguido de excepción no equivale a commit.
- Cancelación repetida no repone dos veces.
- La política checked/unchecked se demuestra con lectura desde otra transacción.
- REQUIRES_NEW confirma independientemente cuando corresponde; documentá su costo de pool.
- El bulk informa filas afectadas y no deja respuestas basadas en entidades stale.
- readOnly se trata como configuración de lectura, no como autorización de seguridad.

Referencia: [límites transaccionales](https://docs.spring.io/spring-data/jpa/reference/jpa/transactions.html).

Siguiente: [09](09-rendimiento.md).
