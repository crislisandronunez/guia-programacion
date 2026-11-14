# Caso: operaciones de la distribuidora Horizonte

Horizonte necesita cruzar pedidos con comprobantes, priorizar despachos, planificar
rutas y analizar actividad. Los ejercicios son módulos de un mismo caso, no una
obligación de construir un sistema empresarial completo.

## Convenciones

- Datos ficticios, IDs de texto y fechas Instant UTC.
- Importes en centavos con long no negativo; detectá overflow en suma y multiplicación.
- Cantidades y pesos enteros positivos; unidades y límites explícitos por algoritmo.
- Ningún algoritmo modifica sus entradas salvo que el contrato lo anuncie.
- Colecciones vacías tienen resultado definido; null y registros inválidos se rechazan.
- Empates se resuelven de forma determinista y se documentan.
- Datasets de rendimiento se generan con semilla y tamaño registrados.
- Estado en memoria; cachés y límites de tasa son locales a una instancia.

## Cruce de datos

pedidos.csv tiene IDs únicos. comprobantes.csv puede tener filas duplicadas por
comprobanteId: una repetición idéntica cuenta una vez; un mismo ID con datos distintos
es error. Varios comprobantes distintos de un pedido se suman. Reportá SIN_PAGO,
PARCIAL, COMPLETO o EXCEDIDO comparando contra el importe. Comprobantes de un pedido
ausente se reportan como huérfanos, sin descartarlos silenciosamente.
Conservá el orden original de pedidos en el resultado. No hay devoluciones negativas.

## Despacho

Orden: VIP primero, prioridad numérica ascendente (1 es la mayor), importe descendente,
fecha ascendente y finalmente ID lexicográfico. Todos los campos son obligatorios.
Para estudiar estabilidad, usá una variante sin el desempate final y elementos etiquetados.

## Series temporales y grafos

eventos.csv contiene un solo cliente y está ordenado por instante.
La ventana es (t - 10 minutos, t]: un evento exactamente diez minutos anterior queda
afuera. Si varios tienen el mismo instante, evaluá el grupo completo.
Una alerta ocurre cuando la suma es estrictamente mayor al umbral.

rutas.csv describe un grafo dirigido de costos enteros no negativos; costos cero son
válidos. El costo de una ruta es aditivo. Una ruta de menos aristas puede costar más.
dependencias.csv usa antes,despues: ambos extremos son tareas, no una secuencia de archivo.
Los casos con nodos aislados, ciclos y rutas imposibles se agregan en tests.

## Alcance algorítmico

La mochila usa cada pedido como máximo una vez (0/1), con capacidad entera acotada.
Caché, rate limiting, balanceo y partición se simulan localmente; no prueban garantías
de un cluster. B-Tree/LSM se comparan mediante trazas de operaciones como exploración.
La criptografía se practica usando bibliotecas existentes, sin reimplementar primitivas.

## Integración final

Implementá al final:
- POST /api/conciliaciones: pedidos y comprobantes, resultado del cruce.
- POST /api/rankings: ventas y un entero k, ranking por importe e ID.
- POST /api/rutas/minima: nodos, aristas, origen y destino; ruta y costo.

HTTP 200 para resultado, 400 para datos inválidos y 422 si un grafo válido no tiene
ruta entre los extremos existentes. Un origen/destino no declarado es dato inválido.
Límites iniciales: 10000 registros por lista, 1000 nodos y 5000 aristas por petición.
No expongas backtracking sin límites ni ejecutes benchmarks pesados desde un controller.

