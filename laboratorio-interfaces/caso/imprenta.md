# Caso: presupuestos de la imprenta Horizonte

Una imprenta ficticia necesita emitir presupuestos aplicando descuentos según el cliente.
Recibe pedidos por HTTP y por un proceso local por lotes. Quiere poder cambiar cómo
guarda los presupuestos sin cambiar el cálculo ni la orquestación.

## Datos y reglas

- Clientes: C-01, categoría ESTANDAR; C-02, categoría MAYORISTA; C-03, categoría VIP.
- Productos: TARJ-01, tarjeta a ARS 10.00; AFI-01, afiche a ARS 100.00.
- De una a veinte líneas por presupuesto, de 1 a 1000 unidades por línea.
- No se repite SKU dentro del mismo presupuesto; producto o cliente inexistente se rechaza.
- Importes BigDecimal, moneda ARS y escala dos; no se usa double.
- Subtotal: suma de precios unitarios por cantidades, antes del descuento.
- ESTANDAR: sin descuento.
- MAYORISTA: 10% si el subtotal es al menos ARS 1000.00; si no, 0%.
- VIP: 15% con tope de ARS 500.00.
- Redondeá el descuento una vez a dos decimales con HALF_EVEN; total = subtotal - descuento.
- El descuento nunca es negativo ni supera el subtotal.
- Presupuesto: UUID, cliente, líneas con precio histórico, subtotal, descuento, total,
  fecha de creación Instant y vencimiento a los siete días exactos.
- El resultado es una instantánea: cambiar el catálogo no modifica presupuestos anteriores.
- Se emite y consulta; no hay pagos, reservas de stock ni actualización de presupuestos.

## Ejemplos verificables

| Categoría y subtotal | Descuento | Total |
|---|---|---|
| ESTANDAR, 1000.00 | 0.00 | 1000.00 |
| MAYORISTA, 999.00 | 0.00 | 999.00 |
| MAYORISTA, 1000.00 | 100.00 | 900.00 |
| VIP, 2000.00 | 300.00 | 1700.00 |
| VIP, 4000.00 | 500.00 | 3500.00 |

## Puertos y efectos a diseñar

El caso de uso pide cliente y productos mediante puertos, aplica una política pura
y guarda el presupuesto. El reloj se inyecta con Clock. No hace falta crear una
interfaz propia para envolver cada clase estándar o colaborador interno.

El repositorio de presupuestos debe ofrecer inserción de un presupuesto nuevo,
búsqueda por ID y listado paginado con tipos propios. No se sobrescribe un ID existente.
ID ausente produce Optional.empty; error técnico produce una excepción del contrato,
nunca un Optional vacío. Un duplicado tiene una excepción de conflicto definida.

Para ejercitar un adaptador HTTP saliente, el catálogo tendrá una implementación
contra un proveedor local simulado. Sus nombres JSON y errores serán distintos a los
internos. La etapa 07 documenta la traducción.

## API a construir

- POST /api/presupuestos: clienteId y líneas con sku/cantidad; 201 y Location.
- GET /api/presupuestos/{id}: 200 con la instantánea guardada.
- GET /api/presupuestos?pagina=0&tamanio=10: 200, página y total; tamaño entre 1 y 50.
- Orden del listado: creadoEn ascendente y UUID como desempate, definido igual en ambos adaptadores.
- 400 para entrada inválida; 404 para cliente, producto o presupuesto ausente.
- 409 para ID duplicado; 503 para una dependencia no disponible;
  502 para respuesta del proveedor que incumple su contrato.

Los DTO de entrada no permiten fijar precio, descuento, total ni identificador.
No hace falta seguridad ni multitenencia para este caso de datos ficticios.
Cada POST válido crea un presupuesto nuevo: no se promete idempotencia entre POST distintos.

## Límites

El adaptador en memoria pierde datos al reiniciar. JPA debe conservarlos.
Ambos cumplen el mismo contrato lógico mientras están disponibles; durabilidad y
latencia son garantías adicionales documentadas, no capacidades idénticas.
No se envían correos ni se accede a proveedores reales.

