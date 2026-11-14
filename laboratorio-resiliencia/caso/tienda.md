# Caso: tienda Horizonte y proveedores inestables

La tienda ficticia vende cuadernos y libros. Consulta precios a un proveedor, reserva
stock en otro y genera reportes secundarios. Queremos aislar los fallos y recuperar
el servicio sin duplicar reservas ni presentar información falsa.

## Datos a implementar

- CUAD-01: cuaderno; precio ARS 1000.00; stock inicial 10.
- LIB-01: libro; precio ARS 2500.00; stock inicial 1.
- Importes BigDecimal; moneda ARS; cantidades enteras positivas.
- Reserva: UUID, SKU, cantidad, clave idempotente, fecha Instant y estado.
- Estados: PENDIENTE, CONFIRMADA, RECHAZADA o INCIERTA.
- No hay pagos ni envíos reales. La incertidumbre se reconcilia consultando al proveedor.

## Contratos para construir

| Operación | Resultado esperado |
|---|---|
| GET /api/catalogo | 200; datos locales para observar aislamiento |
| GET /api/cotizaciones/{sku} | 200; precio, moneda, obtenidoEn, origen y degradado |
| POST /api/reservas con Idempotency-Key | 201 al confirmar, Location y reserva |
| GET /api/reservas/{id} | 200; estado conocido sin inventar confirmación |
| POST /api/reportes | 200 con reporte síncrono, o rechazo explícito bajo carga |

Datos inválidos: 400; SKU/reserva inexistente: 404; falta de stock o reutilización de
clave con otro payload: 409; cuota entrante superada: 429 con Retry-After;
dependencia indispensable no disponible o capacidad saturada: 503;
espera del proveedor agotada en una lectura sin fallback válido: 504.
Un límite saliente del proveedor se comunica como 503 en este contrato, no como si
el consumidor hubiera excedido su cuota entrante.

Al repetir una reserva ya confirmada con igual clave y payload devolvé 200 con el
mismo identificador, sin descontar stock otra vez. Registrá primero la intención local.
Si el resultado remoto es incierto, devolvé 202, Location e INCIERTA para consulta y
reconciliación. Antes de la etapa 10 ese registro es solo memoria: se pierde al reiniciar.
Desde la etapa 10 persistí intención y clave antes de llamar al proveedor.

## Degradación permitida

Una cotización puede usar el último precio exitoso con antigüedad máxima de 60 segundos,
indicando origen=CACHE y degradado=true. Sin precio previo o con uno más viejo, fallá
explícitamente. El precio informativo no confirma stock ni garantiza el precio final.

El catálogo local debe seguir respondiendo aunque falle el proveedor. La reserva no
puede confirmar mediante un fallback inventado ni cambiar automáticamente de proveedor
después de un timeout. El simulador debe poder haber reservado aunque se pierda su respuesta.

## Escenarios de proveedor a construir

Éxito, error transitorio seguido de éxito, error permanente, respuesta lenta, conexión
interrumpida y reserva aplicada con respuesta perdida. Añadí contador de llamadas y
concurrencia máxima observada. Los escenarios se configuran desde tests o un perfil
local lab; el consumidor normal no puede elegir el fallo mediante un parámetro de negocio.

La etapa 01 usa un servidor HTTP de pruebas en otro puerto, separado de Tomcat.
No simules toda la red llamando un controller del mismo servidor: mezclarías los
recursos que necesitás medir. Sin random no controlado ni proveedores de Internet.

## Presupuesto didáctico

Para la consulta de precio, empezá con un presupuesto total de 1200 ms,
timeout por intento de 250 ms y como máximo tres intentos totales.
Incluí conexión, espera de pools, backoff, cola y margen en el cálculo.
Los valores son una hipótesis de laboratorio; no un SLO medido ni una receta universal.

