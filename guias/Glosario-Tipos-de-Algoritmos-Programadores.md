# Glosario y Guía de Tipos de Algoritmos y Estructuras de Datos para Resolver Sistemas

Este documento reúne todos los **tipos de algoritmos, estructuras de datos, análisis de complejidad (Big-O) y algoritmos de sistemas distribuidos** explicados desde un enfoque **100% práctico para la ingeniería de software real** (en **Java y Spring Boot**): no solo cómo funcionan en entrevistas técnicas, sino **en qué parte de un sistema empresarial se usa cada uno** para resolver problemas de velocidad, memoria y escala.

---

## Índice
1. [Complejidad Algorítmica (Notación Big-O): Cómo saber si tu código va a explotar](#1-complejidad-algorítmica-notación-big-o-cómo-saber-si-tu-código-va-a-explotar)
2. [Mapa de Estructuras de Datos en Java (Cuál elegir según el problema)](#2-mapa-de-estructuras-de-datos-en-java-cuál-elegir-según-el-problema)
3. [El Truco #1 en Sistemas Empresariales: Pasar de `O(n²)` a `O(n)` con `HashMap` / `HashSet`](#3-el-truco-1-en-sistemas-empresariales-pasar-de-on²-a-on-con-hashmap--hashset)
4. [Algoritmos de Búsqueda (Lineal, Binaria e Índices)](#4-algoritmos-de-búsqueda-lineal-binaria-e-índices)
5. [Algoritmos de Ordenamiento (*Sorting*: `TimSort`, `QuickSort`, `MergeSort` y `Comparator`)](#5-algoritmos-de-ordenamiento-sorting-timsort-quicksort-mergesort-y-comparator)
6. [Técnica de Dos Punteros (*Two Pointers*) y Ventana Deslizante (*Sliding Window*)](#6-técnica-de-dos-punteros-two-pointers-y-ventana-deslizante-sliding-window)
7. [Recursividad, *Divide & Conquer* y *Backtracking* (Árboles de Categorías y Permisos)](#7-recursividad-divide--conquer-y-backtracking-árboles-de-categorías-y-permisos)
8. [Algoritmos Voraces (*Greedy*) vs Programación Dinámica (*Dynamic Programming / Memoization*)](#8-algoritmos-voraces-greedy-vs-programación-dinámica-dynamic-programming--memoization)
9. [Algoritmos de Grafos y Árboles (`BFS`, `DFS`, `Dijkstra`, Orden Topológico)](#9-algoritmos-de-grafos-y-árboles-bfs-dfs-dijkstra-orden-topológico)
10. [Colas de Prioridad (*Heaps* / `PriorityQueue`) y Planificación de Tareas](#10-colas-de-prioridad-heaps--priorityqueue-y-planificación-de-tareas)
11. [Algoritmos de Caché y Desalojo (`LRU`, `LFU`, `TTL`)](#11-algoritmos-de-caché-y-desalojo-lru-lfu-ttl)
12. [Algoritmos de *Rate Limiting* (Control de Tráfico: *Token Bucket*, *Sliding Window*)](#12-algoritmos-de-rate-limiting-control-de-tráfico-token-bucket-sliding-window)
13. [Algoritmos de Redes y Resiliencia (*Exponential Backoff + Jitter*, *Load Balancing*)](#13-algoritmos-de-redes-y-resiliencia-exponential-backoff--jitter-load-balancing)
14. [Algoritmos de Sistemas Distribuidos y Base de Datos (`B-Tree`, `LSM-Tree`, *Consistent Hashing*, *Bloom Filter*)](#14-algoritmos-de-sistemas-distribuidos-y-base-de-datos-b-tree-lsm-tree-consistent-hashing-bloom-filter)
15. [Algoritmos de Validación, Texto y Criptografía (`Luhn`, `HMAC`, `BCrypt`, `Tries`)](#15-algoritmos-de-validación-texto-y-criptografía-luhn-hmac-bcrypt-tries)
16. [Árbol de Decisión Rápido: "¿Qué Algoritmo o Estructura Uso para mi Problema?"](#16-árbol-de-decisión-rápido-qué-algoritmo-o-estructura-uso-para-mi-problema)

---

## 1. Complejidad Algorítmica (Notación Big-O): Cómo saber si tu código va a explotar

La **Notación Big-O** mide cómo crece el **tiempo de ejecución (*Time Complexity*)** o el **uso de memoria RAM (*Space Complexity*)** a medida que aumenta la cantidad de datos `n` (por ejemplo, cuando una empresa pasa de tener 100 clientes a tener 1.000.000).

| Complejidad | Nombre | Si `n = 10.000` registros, ¿cuántas operaciones hace? | Cómo se ve en el código | Ejemplo en Sistemas Reales |
|---|---|---|---|---|
| **`O(1)`** | **Constante** | **1 operación** (instantáneo, sin importar si hay 10 o 100 millones). | `map.get(id)` o `set.contains(clave)` | Buscar un usuario en un `HashMap` por su ID o leer una clave en Redis. |
| **`O(log n)`** | **Logarítmica** | **~14 operaciones** (divide el problema a la mitad en cada paso). | `Collections.binarySearch(...)` o búsqueda en `TreeMap` | Buscar una fila por un **Índice `B-Tree` en PostgreSQL/MySQL**. |
| **`O(n)`** | **Lineal** | **10.000 operaciones** (recorre la lista una sola vez). | Un único bucle `for (var item : lista)` o `.stream().filter(...)` | Sumar el total de todas las facturas del mes o buscar en una lista sin índice. |
| **`O(n log n)`** | **Linealítmica** | **~140.000 operaciones** (el costo óptimo de ordenar una lista). | `lista.sort(...)` / `Collections.sort(...)` | Ordenar un listado de productos por precio de menor a mayor. |
| **`O(n²)`** | **Cuadrática** | **100.000.000 operaciones** (¡Peligro! Cuelga la CPU cuando `n` supera los pocos miles). | Un bucle `for` adentro de otro `for` (`for` anidado) o un `.stream().filter(x -> otraLista.contains(x))`. | Cruzar 10.000 pedidos contra 10.000 pagos usando `List.contains()` adentro de un `for`. |
| **`O(2ⁿ)` / `O(n!)`** | **Exponencial / Factorial** | Infinitas (imposible para `n > 30` sin optimización). | Recursividad sin memoria que prueba todas las combinaciones posibles. | Ruteo de camiones (*Traveling Salesman Problem*) por fuerza bruta. |

**Ejemplo visual de la diferencia entre `O(1)`, `O(n)` y `O(n²)` en Java:**
```java
// ✅ O(1) - Tiempo Constante: Tarda 0.001 ms tenga 10 o 10 millones de elementos
public Producto buscarEnMapa(Map<Long, Producto> mapaPorId, Long idBuscado) {
    return mapaPorId.get(idBuscado);
}

// ⚠️ O(n) - Tiempo Lineal: Recorre uno por uno hasta encontrarlo
public Producto buscarEnLista(List<Producto> lista, Long idBuscado) {
    for (Producto p : lista) {
        if (p.getId().equals(idBuscado)) return p;
    }
    return null;
}

// ❌ O(n²) - Tiempo Cuadrático: Un bucle dentro de otro. Con 50.000 registros hace 2.500 MILLONES de iteraciones
public List<Pedido> cruzarPedidosConPagosLento(List<Pedido> pedidos, List<Pago> pagos) {
    List<Pedido> pagados = new ArrayList<>();
    for (Pedido pedido : pedidos) {           // n veces
        for (Pago pago : pagos) {             // * n veces = n²
            if (pago.getPedidoId().equals(pedido.getId())) {
                pagados.add(pedido);
            }
        }
    }
    return pagados;
}
```

---

## 2. Mapa de Estructuras de Datos en Java (Cuál elegir según el problema)

Antes de elegir un algoritmo, debes elegir la **estructura de datos correcta** de `java.util`. Elegir bien la colección resuelve el 70% de los problemas de rendimiento:

| Estructura en Java | Qué es internamente | Costo de Búsqueda | Cuándo usarla en un Sistema | Ejemplo de Código |
|---|---|---|---|---|
| **`ArrayList<T>`** | Arreglo dinámico contiguo en memoria. | Por índice `get(i)`: **`O(1)`**<br>Por valor `contains(x)`: **`O(n)`** | El 90% de las veces que solo necesitas guardar y recorrer una lista de elementos en orden. | `List<ItemDto> items = new ArrayList<>();` |
| **`HashSet<T>`** | Conjunto sin duplicados basado en tabla Hash. | `contains(x)`: **`O(1)`** | Eliminar duplicados rápidamente o **verificar al instante si un ID/código ya existe** en una colección grande. | `Set<String> skusVistos = new HashSet<>();` |
| **`LinkedHashSet<T>`** | `HashSet` que además recuerda el orden en que insertaste los elementos. | `contains(x)`: **`O(1)`** | Quitar duplicados de una lista pero **manteniendo el orden original exacto**. | `new LinkedHashSet<>(listaConDuplicados);` |
| **`HashMap<K, V>`** | Diccionario clave-valor basado en Hash. | `get(clave)`: **`O(1)`** | Indexar objetos en memoria por su ID, DNI o código para cruzarlos al instante sin usar bucles anidados. | `Map<Long, Cliente> porId = new HashMap<>();` |
| **`TreeMap<K, V>` / `TreeSet<T>`** | Árbol Rojo-Negro (*Red-Black Tree*) que mantiene las claves siempre ordenadas. | `get(k)` / `floorKey(k)`: **`O(log n)`** | Buscar por **rangos** (ej. *"dame el tramo de impuesto inmediatamente inferior a este sueldo"*). | `treeMap.floorEntry(sueldoBruto);` |
| **`ArrayDeque<T>`** | Cola de doble punta (*Double-Ended Queue*). Mucho más rápida que `LinkedList` y `Stack`. | Insertar/Sacar de las puntas: **`O(1)`** | Implementar una **Cola FIFO** (`offer`/`poll` para procesar en orden de llegada) o una **Pila LIFO** (`push`/`pop` para historial Deshacer/Rehacer). | `Queue<Tarea> cola = new ArrayDeque<>();` |
| **`PriorityQueue<T>`** | Montículo binario (*Min-Heap* / *Max-Heap*). | Sacar el más prioritario (`poll`): **`O(log n)`** | Procesar siempre primero el pedido más urgente (VIP) o obtener el "Top K" más grande. | `new PriorityQueue<>(Comparator.comparing(Tarea::prioridad))` |
| **`ConcurrentHashMap<K, V>`** | Mapa seguro para múltiples hilos sin bloquear toda la tabla. | `get` / `compute`: **`O(1)`** | Cachés en memoria o contadores compartidos dentro de un `@Service` Singleton en Spring Boot. | `private final Map<String, Token> cache = new ConcurrentHashMap<>();` |

**Ejemplo de uso de `TreeMap` para resolver escalas o tramos tarifarios en `O(log n)` sin 20 `if/else`:**
```java
@Service
public class CalculadoraComisionPorTramosService {

    // TreeMap guarda los pisos de cada tramo ordenados y permite buscar el escalón exacto con floorEntry()
    private final NavigableMap<BigDecimal, BigDecimal> escalaComisiones = new TreeMap<>();

    public CalculadoraComisionPorTramosService() {
        escalaComisiones.put(new BigDecimal("0"),       new BigDecimal("0.02")); // De $0 a $99.999 -> 2%
        escalaComisiones.put(new BigDecimal("100000"),  new BigDecimal("0.05")); // De $100k a $499.999 -> 5%
        escalaComisiones.put(new BigDecimal("500000"),  new BigDecimal("0.08")); // De $500k en adelante -> 8%
    }

    public BigDecimal obtenerPorcentajeComision(BigDecimal montoVenta) {
        // floorEntry busca en O(log n) la mayor clave que sea menor o igual al montoVenta
        return escalaComisiones.floorEntry(montoVenta).getValue();
    }
}
```

---

## 3. El Truco #1 en Sistemas Empresariales: Pasar de `O(n²)` a `O(n)` con `HashMap` / `HashSet`

El problema de rendimiento más repetido en código de empresas ocurre al cruzar dos listas en memoria (por ejemplo, cruzar 20.000 pedidos de nuestra base de datos contra 20.000 pagos de un archivo CSV del banco).

- **Si usas un `for` dentro de otro `for` (o `.stream().filter(p -> lista.contains(p))`)**: Haces `20.000 × 20.000 = 400.000.000` de comparaciones (`O(n²)`). Tarda varios segundos y bloquea la CPU.
- **Si primero indexas la segunda lista en un `HashMap` o `HashSet` y luego recorres la primera buscando en `O(1)`**: Haces `20.000 + 20.000 = 40.000` operaciones (`O(n)`). **Es 10.000 veces más rápido (tarda 2 milisegundos).**

```java
// ✅ SOLUCIÓN O(n): Indexación en HashMap para cruzar grandes volúmenes de datos al instante
@Service
public class ConciliacionRapidaService {

    public List<ResultadoCruceDto> cruzarPedidosConPagosEnTiempoLineal(
            List<Pedido> pedidos,
            List<PagoBancario> pagosBanco) {

        // Paso 1: Convertimos la lista de pagos en un HashMap indexado por pedidoId -> Costo: O(n)
        Map<Long, PagoBancario> mapaPagosPorPedidoId = pagosBanco.stream()
                .collect(Collectors.toMap(
                        PagoBancario::getPedidoId,
                        Function.identity(),
                        (existente, duplicado) -> existente // En caso de duplicados, nos quedamos con el primero
                ));

        List<ResultadoCruceDto> resultados = new ArrayList<>(pedidos.size());

        // Paso 2: Recorremos los pedidos una sola vez y buscamos en el mapa en O(1) -> Costo total: O(n)
        for (Pedido pedido : pedidos) {
            PagoBancario pagoAsociado = mapaPagosPorPedidoId.get(pedido.getId()); // Búsqueda instantánea O(1)
            boolean estaPagado = (pagoAsociado != null);
            resultados.add(new ResultadoCruceDto(pedido.getId(), estaPagado));
        }

        return resultados;
    }
}
```

---

## 4. Algoritmos de Búsqueda (Lineal, Binaria e Índices)

| Algoritmo | Complejidad | Requisito Previo | Cómo funciona | Cuándo se usa en la vida real |
|---|---|---|---|---|
| **Búsqueda Lineal (*Linear Search*)** | `O(n)` | Ninguno (la lista puede estar desordenada). | Revisa elemento por elemento desde el índice `0` hasta el final. | Listas pequeñas en memoria (< 100 elementos) donde no vale la pena crear un índice. |
| **Búsqueda Binaria (*Binary Search*)** | `O(log n)` | **La colección DEBE estar ordenada.** | Mira el elemento del medio: si el valor buscado es mayor, descarta de golpe toda la mitad izquierda; si es menor, descarta la mitad derecha. Repite hasta encontrarlo. | Buscar en listas ordenadas de millones de elementos, buscar en qué versión se rompió un código (`git bisect`) o buscar por rangos de fechas. |
| **Búsqueda por Hash (*Hashing*)** | `O(1)` promedio | Clave con `equals()` y `hashCode()` bien implementados. | Calcula una función matemática (`hashCode`) sobre la clave para saltar directo al casillero (*bucket*) de memoria donde está el dato. | `HashMap`, `HashSet`, cachés en Redis y búsquedas por igualdad exacta. |

**Ejemplo de Búsqueda Binaria en Java (Implementación manual + `Collections.binarySearch`):**
```java
public class BusquedaBinariaEjemplo {

    // Supongamos una lista ordenada de millones de códigos postales o IDs bloqueados
    public boolean existeIdEnListaOrdenada(List<Long> idsOrdenados, Long objetivo) {
        int izquierda = 0;
        int derecha = idsOrdenados.size() - 1;

        while (izquierda <= derecha) {
            // (izquierda + derecha) >>> 1 o izquierda + (derecha - izquierda) / 2 evita desbordamiento de enteros (Integer Overflow)
            int medio = izquierda + (derecha - izquierda) / 2;
            Long valorMedio = idsOrdenados.get(medio);

            int comparacion = valorMedio.compareTo(objetivo);
            if (comparacion == 0) {
                return true; // ¡Encontrado en O(log n)!
            } else if (comparacion < 0) {
                izquierda = medio + 1; // Está en la mitad derecha
            } else {
                derecha = medio - 1; // Está en la mitad izquierda
            }
        }
        return false;
    }

    // En el día a día en Java puedes usar directamente la utilidad nativa:
    public boolean existeConJdk(List<Long> idsOrdenados, Long objetivo) {
        return Collections.binarySearch(idsOrdenados, objetivo) >= 0;
    }
}
```

---

## 5. Algoritmos de Ordenamiento (*Sorting*: `TimSort`, `QuickSort`, `MergeSort` y `Comparator`)

Aunque en el trabajo diario no escribes `QuickSort` desde cero porque usas `list.sort(...)`, es fundamental entender qué hace Java por detrás y cómo armar criterios de ordenamiento multicampo:

| Algoritmo | Complejidad Promedio / Peor Caso | ¿Es Estable (*Stable*)? | Qué significa y dónde lo usa Java |
|---|---|---|---|
| **MergeSort** | `O(n log n)` siempre | **Sí** (respeta el orden previo de elementos empatados). | Divide la lista a la mitad recursivamente y luego fusiona (*merge*) las mitades ordenadas. |
| **QuickSort (Dual-Pivot)** | `O(n log n)` / `O(n²)` | **No** | Elige pivotes y particiona menores a la izquierda y mayores a la derecha sin usar memoria extra (`In-place`). **Java lo usa en `Arrays.sort(int[])` para tipos primitivos.** |
| **TimSort** *(Híbrido MergeSort + InsertionSort)* | `O(n log n)` (y `O(n)` si ya venía casi ordenada) | **Sí** | **Es el algoritmo oficial que usa Java en `Collections.sort()` y `List.sort()` para objetos.** Aprovecha tramos que ya vienen ordenados de la BD. |

**Ejemplo de Ordenamiento Empresarial Multicriterio en Java con `Comparator`:**
```java
// Requerimiento de negocio:
// Ordenar los pedidos: 1° Los clientes VIP primero, 2° Por prioridad de urgencia, 3° Por monto de mayor a menor,
// 4° En caso de empate, los más antiguos primero (manejando posibles valores nulos sin lanzar NullPointerException).
public List<Pedido> ordenarPedidosParaDespacho(List<Pedido> pedidos) {
    Comparator<Pedido> criterioDespacho = Comparator
            .comparing(Pedido::isClienteVip).reversed() // true (VIP) va antes que false
            .thenComparing(Pedido::getPrioridad)
            .thenComparing(Pedido::getMontoTotal, Comparator.nullsLast(Comparator.reverseOrder()))
            .thenComparing(Pedido::getFechaCreacion);

    return pedidos.stream()
            .sorted(criterioDespacho) // Ejecuta TimSort en O(n log n)
            .toList();
}
```

---

## 6. Técnica de Dos Punteros (*Two Pointers*) y Ventana Deslizante (*Sliding Window*)

Estas dos técnicas permiten procesar secuencias ordenadas, series de tiempo o flujos de eventos en **una sola pasada `O(n)`** en lugar de usar bucles anidados `O(n²)`:

| Técnica | Cómo funciona | Casos de Uso Reales en Empresas |
|---|---|---|
| **Dos Punteros (*Two Pointers*)** | Usas dos índices (`izquierda` y `derecha`) que se mueven hacia el centro o en la misma dirección sobre una o dos listas ordenadas. | - **Conciliar dos archivos ordenados por fecha/ID** (estilo `MERGE JOIN`).<br>- Validar palíndromos o formatos.<br>- Encontrar pares de facturas que sumen exactamente el monto de una transferencia. |
| **Ventana Deslizante (*Sliding Window*)** | Mantienes una "ventana" de tamaño fijo o dinámico `[inicio, fin]` que avanza sumando el elemento nuevo que entra por la derecha y restando el viejo que sale por la izquierda. | - **Detección de fraude:** *"Alertar si un usuario gasta más de $500.000 en cualquier ventana móvil de 5 minutos"*.<br>- Calcular el promedio móvil de ventas de los últimos 7 días. |

**Ejemplo Real de *Sliding Window* (Detector de Fraude por Volumen Acumulado en Ventana de Tiempo):**
```java
public record Transaccion(Instant fecha, BigDecimal monto) {}

@Service
public class DetectorFraudeVentanaDeslizanteService {

    // Detecta en O(n) si en algún período continuo de 10 minutos el usuario superó el límite máximo acumulado
    public boolean superoLimiteEnVentanaDeDiezMinutos(List<Transaccion> transaccionesOrdenadas,
                                                      BigDecimal limiteMaximo) {
        BigDecimal sumaEnVentanaActual = BigDecimal.ZERO;
        int inicioVentana = 0;

        for (int finVentana = 0; finVentana < transaccionesOrdenadas.size(); finVentana++) {
            Transaccion actual = transaccionesOrdenadas.get(finVentana);
            // 1. Entra la nueva transacción por la derecha de la ventana
            sumaEnVentanaActual = sumaEnVentanaActual.add(actual.monto());

            // 2. Sacamos por la izquierda las transacciones que quedaron más viejas que 10 minutos respecto a la actual
            while (Duration.between(transaccionesOrdenadas.get(inicioVentana).fecha(), actual.fecha()).toMinutes() > 10) {
                sumaEnVentanaActual = sumaEnVentanaActual.subtract(transaccionesOrdenadas.get(inicioVentana).monto());
                inicioVentana++;
            }

            // 3. Verificamos si la suma dentro de la ventana de 10 minutos rompió el umbral
            if (sumaEnVentanaActual.compareTo(limiteMaximo) > 0) {
                return true; // ¡Alerta de fraude detectada!
            }
        }
        return false;
    }
}
```

---

## 7. Recursividad, *Divide & Conquer* y *Backtracking* (Árboles de Categorías y Permisos)

| Concepto | Qué es | Dónde aparece en Sistemas Empresariales |
|---|---|---|
| **Recursividad (*Recursion*)** | Un método que se llama a sí mismo para resolver un subproblema más pequeño hasta llegar a un **Caso Base (*Base Case*)** que detiene la recursión. | - Recorrer **árboles de categorías con subcategorías infinitas** (Electrónica -> Computación -> Notebooks -> Gamer).<br>- Organigramas de empresas (Jefe -> Gerentes -> Empleados).<br>- Listas de materiales (*Bill of Materials - BOM*) en fábricas. |
| **Divide y Vencerás (*Divide & Conquer*)** | Dividir un problema grande en subproblemas independientes, resolverlos (incluso en paralelo con `ForkJoinPool`) y combinar los resultados. | Procesar un archivo de 1 millón de líneas partiéndolo en 8 partes independientes. |
| **Backtracking (Vuelta Atrás)** | Explorar soluciones paso a paso y, cuando un camino no cumple las restricciones, retroceder un paso y probar otra rama. | Asignación automática de turnos de empleados, armado de cajas en logística (*Bin Packing*) o resolución de horarios sin superposiciones. |

> **Cuidado en Producción con la Recursividad:** Si un árbol tiene miles de niveles de profundidad o un ciclo por error de datos (A es padre de B y B es padre de A), la recursividad lanza **`StackOverflowError`**. Para estructuras profundas, prefiere usar un bucle `while` con una pila (`ArrayDeque`) y un `Set<Long> visitados` para evitar ciclos infinitos.

**Ejemplo: Calcular el precio total de un Paquete/Combo de Productos anidados (Recursivo seguro con detector de ciclos):**
```java
public class NodoComponenteProducto {
    private Long id;
    private BigDecimal precioBase;
    private List<NodoComponenteProducto> subComponentes = new ArrayList<>();
    // Getters...
}

@Service
public class CalculadoraCostoComboService {

    public BigDecimal calcularCostoTotalArbol(NodoComponenteProducto raiz) {
        return calcularRecursivo(raiz, new HashSet<>());
    }

    private BigDecimal calcularRecursivo(NodoComponenteProducto nodo, Set<Long> visitadosEnRama) {
        // 1. Protección contra referencias circulares en la BD
        if (!visitadosEnRama.add(nodo.getId())) {
            throw new IllegalStateException("Se detectó un ciclo infinito en el componente ID: " + nodo.getId());
        }

        // 2. Caso Base + Paso Recursivo
        BigDecimal total = nodo.getPrecioBase();
        for (NodoComponenteProducto hijo : nodo.getSubComponentes()) {
            total = total.add(calcularRecursivo(hijo, visitadosEnRama));
        }

        visitadosEnRama.remove(nodo.getId()); // Limpiamos al retroceder (Backtrack)
        return total;
    }
}
```

---

## 8. Algoritmos Voraces (*Greedy*) vs Programación Dinámica (*Dynamic Programming / Memoization*)

Cuando una empresa necesita **optimizar recursos** (dar vuelto con la menor cantidad de billetes, elegir de qué almacenes despachar un pedido para gastar menos en envío, o aplicar la mejor combinación de cupones de descuento), entran en juego estos dos enfoques:

| Enfoque | Cómo toma las decisiones | Cuándo usarlo | Ejemplo |
|---|---|---|---|
| **Algoritmo Voraz (*Greedy*)** | En cada paso elige **la opción que parece mejor en este instante (óptimo local)** sin mirar atrás ni recalcular combinaciones futuras. Es rapidísimo (`O(n log n)`). | Cuando elegir siempre el mayor/mejor primero garantiza el óptimo global (ej. asignar primero los depósitos con más stock o dar vuelto con monedas estándar). | Descontar una deuda empezando siempre por la factura más antigua vencida (*FIFO Allocation*). |
| **Programación Dinámica (*Dynamic Programming - DP*) y *Memoization*** | Divide el problema en subproblemas que se repiten y **guarda en una caché/tabla los resultados ya calculados** para no volver a calcularlos dos veces. | Cuando un algoritmo voraz se equivoca y necesitas encontrar la combinación matemática perfecta sin caer en tiempo exponencial `O(2ⁿ)`. | *Problema de la Mochila (Knapsack)*: elegir qué pedidos entran en un camión de 1.000 kg maximizando el valor asegurado. |

**Ejemplo Empresarial de Algoritmo *Greedy*: Imputación Automática de un Pago a Múltiples Facturas Vencidas:**
```java
@Service
public class ImputadorPagosGreedyService {

    // Regla de negocio: Cuando un cliente transfiere un monto parcial, se debe cancelar primero
    // la deuda de las facturas más antiguas hasta agotar el dinero transferido.
    public void imputarPagoAFacturasPendientes(List<Factura> facturasPendientes, BigDecimal montoRecibido) {
        // 1. Ordenamos por fecha de vencimiento (de la más vieja a la más nueva)
        facturasPendientes.sort(Comparator.comparing(Factura::getFechaVencimiento));

        BigDecimal saldoRestante = montoRecibido;

        // 2. Enfoque Greedy: En cada paso tomamos todo lo que podamos para saldar la factura más urgente
        for (Factura factura : facturasPendientes) {
            if (saldoRestante.compareTo(BigDecimal.ZERO) == 0) {
                break;
            }
            BigDecimal deudaFactura = factura.getSaldoPendiente();
            BigDecimal montoAAplicar = saldoRestante.min(deudaFactura); // Toma el menor entre lo que queda y lo que debe

            factura.registrarPagoParcialOTotal(montoAAplicar);
            saldoRestante = saldoRestante.subtract(montoAAplicar);
        }
    }
}
```

---

## 9. Algoritmos de Grafos y Árboles (`BFS`, `DFS`, `Dijkstra`, Orden Topológico)

Muchos problemas de empresas son en realidad **Grafos** (nodos conectados por aristas): redes de sucursales y rutas de envío, dependencias entre tareas de un proyecto, o transferencias sospechosas entre cuentas bancarias.

| Algoritmo de Grafo | Cómo funciona | Para qué sirve en Sistemas Reales | Estructura que usa |
|---|---|---|---|
| **BFS (*Breadth-First Search* - Búsqueda en Anchura)** | Explora por niveles (primero todos los vecinos a distancia 1, luego los de distancia 2, etc.). | - Encontrar el **camino con menos saltos** entre dos nodos.<br>- Redes de referidos (contactos de 1° y 2° grado).<br>- Detectar anillos de lavado de dinero a pocos pasos de una cuenta fraudulenta. | **Cola FIFO (`ArrayDeque`)** |
| **DFS (*Depth-First Search* - Búsqueda en Profundidad)** | Se mete hasta el fondo por una rama antes de retroceder y probar la siguiente. | - **Detectar ciclos** (ej. dos tareas o facturas que dependen una de la otra).<br>- Evaluar árboles de expresiones o reglas de negocio. | **Pila LIFO o Recursión** |
| **Algoritmo de Dijkstra** | Encuentra el camino de **menor costo total** desde un origen hacia los demás nodos cuando las conexiones tienen pesos positivos (kilómetros, precio, tiempo). | - **Logística:** Calcular la ruta más barata o rápida para enviar mercadería entre centros de distribución.<br>- **Fintech:** Encontrar la ruta de conversión de divisas con menor comisión (`ARS -> USD -> EUR`). | **`PriorityQueue` (Min-Heap)** |
| **Ordenamiento Topológico (*Topological Sort* - Algoritmo de Kahn)** | Ordena nodos que tienen dependencias entre sí (`A` debe ejecutarse antes que `B`) asegurando que ninguna tarea corra antes que sus requisitos. | - Motores de Workflows empresariales.<br>- Saber en qué orden migrar tablas con claves foráneas.<br>- Cómo Maven o Spring calculan en qué orden inicializar los `@Bean`. | Grados de entrada (`in-degree`) + Cola |

**Ejemplo de BFS en Java (Recorrer jerarquía por niveles sin usar recursividad):**
```java
public List<Long> obtenerTodosLosSubordinadosPorNivel(Empleado gerenteRaiz) {
    List<Long> resultado = new ArrayList<>();
    Queue<Empleado> cola = new ArrayDeque<>();
    Set<Long> visitados = new HashSet<>();

    cola.offer(gerenteRaiz);
    visitados.add(gerenteRaiz.getId());

    while (!cola.isEmpty()) {
        Empleado actual = cola.poll(); // Saca el primero de la fila
        resultado.add(actual.getId());

        for (Empleado subordinado : actual.getReportesDirectos()) {
            if (visitados.add(subordinado.getId())) {
                cola.offer(subordinado); // Pone a los hijos al final de la fila para el siguiente nivel
            }
        }
    }
    return resultado;
}
```

---

## 10. Colas de Prioridad (*Heaps* / `PriorityQueue`) y Planificación de Tareas

### El Problema de Negocio
1. Tienes una cola de soporte o de procesamiento de pedidos donde los clientes `PLAN_ENTERPRISE` deben ser atendidos antes que los `PLAN_GRATIS`, aunque hayan llegado después.
2. O necesitas obtener los **10 productos más vendidos entre 5 millones de productos** sin ordenar los 5 millones enteros en memoria.

### La Solución: Estructura *Heap* (`PriorityQueue`)
Un *Heap* inserta y extrae siempre el elemento mínimo o máximo en **`O(log n)`** (y para el problema del "Top K", solo gasta memoria para `K` elementos `O(n log k)` en lugar de `O(n log n)`).

```java
// Ejemplo: Obtener el Top K (ej. las 5 ventas más grandes del día) entre millones de ventas
// gastando memoria solo para 5 elementos con un Min-Heap:
@Service
public class RankingVentasService {

    public List<Venta> obtenerTopKVentasMasAltas(Iterable<Venta> millonesDeVentas, int k) {
        // Min-Heap de tamaño K: en la cima (peek) siempre está la MÁS CHICA de las K mejores vistas hasta ahora
        PriorityQueue<Venta> topKHeap = new PriorityQueue<>(k, Comparator.comparing(Venta::getMonto));

        for (Venta venta : millonesDeVentas) {
            if (topKHeap.size() < k) {
                topKHeap.offer(venta);
            } else if (venta.getMonto().compareTo(topKHeap.peek().getMonto()) > 0) {
                // Si la nueva venta supera a la más chica del Top K, sacamos esa y metemos la nueva
                topKHeap.poll();
                topKHeap.offer(venta);
            }
        }

        List<Venta> resultado = new ArrayList<>(topKHeap);
        resultado.sort(Comparator.comparing(Venta::getMonto).reversed());
        return resultado;
    }
}
```

---

## 11. Algoritmos de Caché y Desalojo (`LRU`, `LFU`, `TTL`)

La memoria RAM es limitada. Cuando guardas datos en una caché (Caffeine, Redis o en memoria local) y la caché se llena, un **algoritmo de desalojo (*Eviction Policy*)** decide qué elemento borrar para hacer lugar al nuevo:

| Algoritmo | Sigla en Inglés | Cómo decide a quién borrar | Ejemplo de Uso |
|---|---|---|---|
| **LRU** | *Least Recently Used* (El menos usado recientemente) | Borra el elemento que **hace más tiempo que nadie consulta**. Es el estándar de oro en sistemas. | Cachear perfiles de usuarios activos o productos visitados hoy. |
| **LFU / W-TinyLFU** | *Least Frequently Used* (El menos frecuentemente usado) | Lleva un contador de cuántas veces se pidió cada clave y conserva las más populares históricamente (usado por la librería **Caffeine** en Spring Boot). | Catálogo de e-commerce donde el Top 500 de productos concentra el 80% de las visitas. |
| **TTL** | *Time-To-Live* (Tiempo de vida) | Cada entrada tiene un reloj de vencimiento fijo (ej. 5 minutos) tras el cual se invalida sola. | Cotización del dólar, tokens de sesión o códigos OTP por SMS. |

**Ejemplo de implementación de una Caché LRU en `O(1)` usando `LinkedHashMap` nativo de Java:**
```java
// LinkedHashMap en Java tiene soporte nativo para el algoritmo LRU pasando accessOrder = true en el constructor:
public class LruCacheSimple<K, V> extends LinkedHashMap<K, V> {

    private final int capacidadMaxima;

    public LruCacheSimple(int capacidadMaxima) {
        // 16 = capacidad inicial, 0.75f = factor de carga, true = ordenar por último acceso (LRU)
        super(capacidadMaxima, 0.75f, true);
        this.capacidadMaxima = capacidadMaxima;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        // Cuando el tamaño supera el máximo, borra automáticamente el que hace más tiempo no se usa
        return size() > capacidadMaxima;
    }
}
```

---

## 12. Algoritmos de *Rate Limiting* (Control de Tráfico: *Token Bucket*, *Sliding Window*)

Para evitar que un cliente malicioso o un bot con un bucle infinito sature tu API o te vacíe la base de datos, los API Gateways, Redis y librerías como **Bucket4j** o **Resilience4j** implementan estos algoritmos:

| Algoritmo de Rate Limiting | Cómo funciona | Ventajas y Desventajas |
|---|---|---|
| **Token Bucket (Cubo de Fichas)** | Imagina un balde con capacidad para 10 fichas. Cada petición gasta 1 ficha. Si el balde se vacía, rechaza con `429 Too Many Requests`. El balde se recarga a razón de 1 ficha por segundo. | **El más usado en la industria (AWS, Stripe).** Permite ráfagas cortas (*bursts*) legítimas sin dejar de limitar el promedio por segundo. |
| **Leaky Bucket (Cubo con Goteo)** | Las peticiones entran a un balde y salen por un agujero inferior a una velocidad estrictamente constante (ej. 1 cada 100ms). | Suaviza totalmente los picos de tráfico hacia sistemas viejos que no toleran ráfagas. |
| **Fixed Window Counter (Ventana Fija)** | Cuenta peticiones en bloques de reloj fijos (ej. de `12:00:00` a `12:00:59`). | Muy simple en Redis (`INCR` + `EXPIRE`), pero si un usuario manda 100 peticiones a las `12:00:59` y 100 a las `12:01:00`, metió 200 en 2 segundos. |
| **Sliding Window Log / Counter** | Calcula el límite sobre los últimos 60 segundos exactos móviles respecto al instante actual. | Evita la trampa del borde de minuto de la ventana fija. |

**Ejemplo del Algoritmo *Token Bucket* en Java puro (Thread-Safe):**
```java
public class TokenBucketRateLimiter {

    private final long capacidadMaxima;
    private final long tokensPorSegundo;
    private double tokensDisponibles;
    private long ultimoRefillNanoTime;

    public TokenBucketRateLimiter(long capacidadMaxima, long tokensPorSegundo) {
        this.capacidadMaxima = capacidadMaxima;
        this.tokensPorSegundo = tokensPorSegundo;
        this.tokensDisponibles = capacidadMaxima;
        this.ultimoRefillNanoTime = System.nanoTime();
    }

    public synchronized boolean permitirPeticion() {
        recargarTokensSegunTiempoTranscurrido();
        if (tokensDisponibles >= 1.0) {
            tokensDisponibles -= 1.0;
            return true; // Petición permitida
        }
        return false; // 429 Too Many Requests
    }

    private void recargarTokensSegunTiempoTranscurrido() {
        long ahora = System.nanoTime();
        double segundosTranscurridos = (ahora - ultimoRefillNanoTime) / 1_000_000_000.0;
        double tokensNuevos = segundosTranscurridos * tokensPorSegundo;
        if (tokensNuevos > 0) {
            tokensDisponibles = Math.min(capacidadMaxima, tokensDisponibles + tokensNuevos);
            ultimoRefillNanoTime = ahora;
        }
    }
}
```

---

## 13. Algoritmos de Redes y Resiliencia (*Exponential Backoff + Jitter*, *Load Balancing*)

### 1. *Exponential Backoff with Jitter* (Retroceso Exponencial con Aleatoriedad)
Cuando un servidor externo se cae y tienes 1.000 clientes intentando reconectarse:
- Si todos reintentan cada 2 segundos fijos, los 1.000 golpean al servidor exactamente al mismo tiempo y lo vuelven a tumbar (**Problema de la Estampida / *Thundering Herd***).
- **Solución Algorítmica**: Duplicar el tiempo de espera en cada intento (`2^intento`) y sumarle un **número aleatorio (*Jitter*)** para desparramar los reintentos en el tiempo:

```java
public class CalculadoraBackoffConJitter {

    private static final long BASE_MS = 500;
    private static final long MAX_ESPERA_MS = 30_000;

    // Fórmula "Full Jitter" recomendada por AWS Architecture:
    public long calcularEsperaAntesDeReintentarMs(int numeroIntento) {
        // Crecimiento exponencial: 500ms, 1000ms, 2000ms, 4000ms, 8000ms... con tope en 30 seg
        long topeExponencial = Math.min(MAX_ESPERA_MS, BASE_MS * (1L << numeroIntento));
        // Jitter aleatorio entre la mitad del tope y el tope para evitar sincronización de estampidas
        return ThreadLocalRandom.current().nextLong(topeExponencial / 2, topeExponencial + 1);
    }
}
```

### 2. Algoritmos de Balanceo de Carga (*Load Balancing*)
Cuando tienes 4 instancias de tu microservicio detrás de un balanceador (Nginx, AWS ALB, Spring Cloud LoadBalancer):
- **Round Robin**: Turno rotativo (`Servidor 1 -> 2 -> 3 -> 4 -> 1...`). Ideal cuando todas las peticiones tardan parecido.
- **Weighted Round Robin**: Le manda el doble de tráfico al servidor que tiene el doble de CPU/RAM.
- **Least Connections (Menos Conexiones Activas)**: Envía la nueva petición al servidor que en este instante tiene menos peticiones en vuelo procesándose. Ideal cuando hay peticiones lentas mezcladas con rápidas.
- **IP / Header Hash**: Calcula un hash del ID del usuario para mandarlo siempre al mismo servidor (*Sticky Session* o afinidad de caché).

---

## 14. Algoritmos de Sistemas Distribuidos y Base de Datos (`B-Tree`, `LSM-Tree`, *Consistent Hashing*, *Bloom Filter*)

Estos son los algoritmos internos que hacen funcionar a PostgreSQL, MongoDB, Cassandra, Kafka y Redis:

| Algoritmo / Estructura | Dónde vive | Qué problema empresarial resuelve |
|---|---|---|
| **Árbol B / `B+Tree`** | Índices de **PostgreSQL, MySQL, Oracle** y MongoDB. | Mantiene los datos ordenados en bloques de disco permettant buscar, insertar y hacer consultas por rango (`BETWEEN`, `<`, `>`) en **`O(log n)`** con poquísimas lecturas de disco. |
| **Árbol LSM (*Log-Structured Merge-Tree*)** | **Cassandra, RocksDB, ScyllaDB** (Bases de alta escritura). | En vez de modificar un índice en disco en cada `INSERT`, escribe todo secuencialmente al final de un archivo (*Append-Only*) y en un `MemTable` en RAM, logrando soportar **cientos de miles de escrituras por segundo** (logs, IoT, auditoría). |
| **Consistent Hashing (Hashing Consistente)** | Clústeres de **Redis, Cassandra, Balanceadores y CDNs**. | Si repartes datos entre `N` servidores haciendo `hash(clave) % N` y se cae 1 servidor, cambia `N` y se invalida el 90% de las cachés. El *Consistent Hashing* ubica servidores y claves en un anillo virtual para que si se agrega o cae 1 nodo, solo se mueva `1/N` de los datos. |
| **Filtro de Bloom (*Bloom Filter*)** | **Redis, Chrome, Bases de datos, Antifraude**. | Estructura probabilística ultra compacta en bits que responde en `O(1)` con ínfima memoria: **"Definitivamente NO existe"** (100% seguro) o **"Probablemente sí existe"**. Evita ir a consultar al disco o base de datos por IDs o usuarios que no existen. |
| **Generador Snowflake ID** | **Twitter/X, Discord, Instagram, Microservicios**. | Algoritmo que genera IDs numéricos de 64 bits (`long`) únicos en sistemas distribuidos **sin coordinar con una base de datos central** y que además **quedan ordenados cronológicamente por fecha/hora** (41 bits de timestamp + 10 bits de ID de máquina + 12 bits de secuencia). |

**Ejemplo de Generador de IDs Ordenables por Tiempo (Estilo *Snowflake* simplificado en Java):**
```java
@Component
public class SnowflakeIdGenerator {

    private static final long EPOCA_PERSONALIZADA = 1704067200000L; // 2024-01-01T00:00:00Z
    private final long nodoId = 1L; // ID de esta instancia del microservicio (0 a 1023)
    private long ultimoTimestamp = -1L;
    private long secuencia = 0L;

    public synchronized long siguienteId() {
        long ahora = System.currentTimeMillis();
        if (ahora == ultimoTimestamp) {
            secuencia = (secuencia + 1) & 4095; // Hasta 4096 IDs por cada milisegundo en el mismo servidor
            if (secuencia == 0) {
                while (ahora <= ultimoTimestamp) {
                    ahora = System.currentTimeMillis();
                }
            }
        } else {
            secuencia = 0L;
        }
        ultimoTimestamp = ahora;
        return ((ahora - EPOCA_PERSONALIZADA) << 22) | (nodoId << 12) | secuencia;
    }
}
```

---

## 15. Algoritmos de Validación, Texto y Criptografía (`Luhn`, `HMAC`, `BCrypt`, `Tries`)

| Algoritmo | Para qué se usa en Sistemas Empresariales | Característica Clave |
|---|---|---|
| **Algoritmo de Luhn (Módulo 10)** | Validar en el acto (antes de llamar al banco) si el número de una **tarjeta de crédito/débito** o IMEI está bien escrito o tiene un dígito mal tipeado. | Operación matemática rápida en `O(n)` sobre los dígitos. |
| **Algoritmo Módulo 11** | Validar **CUIT / CUIL** (Argentina), RUT (Chile), CBU bancario e ISBN. | Calcula un dígito verificador multiplicando cada posición por una serie de pesos. |
| **Árbol de Prefijos (*Trie*)** | **Autocompletado instantáneo** en buscadores (cuando el usuario escribe `"not"` y en 1ms le sugieres `"notebook"`, `"nota"`, `"notificación"`). | Busca por prefijo en `O(longitud_palabra)` sin importar si el diccionario tiene 10 millones de palabras. |
| **SHA-256 / HMAC-SHA256** | **Integridad y Firma de Webhooks / JWT**: Verifica que un archivo o JSON no fue alterado en el camino. Es un hash **rápido**. | Unidireccional (no se puede desencriptar). |
| **BCrypt / Argon2id** | **Guardar contraseñas de usuarios en la BD**. A diferencia de SHA-256, son algoritmos **intencionalmente lentos** y con *Salt* aleatorio para que un atacante con placas de video no pueda probar millones de claves por segundo. | `new BCryptPasswordEncoder()` en Spring Security. |
| **AES-256-GCM** | **Encriptación Simétrica Reversible**: Guardar en la base de datos un dato sensible (ej. una clave de API de un cliente o un CBU) que tu sistema **sí necesita poder desencriptar** más adelante para usarlo. | Usa una llave secreta para encriptar y desencriptar. |

**Ejemplo del Algoritmo de Luhn en Java (Para validar números de tarjeta antes de llamar a la pasarela):**
```java
public class ValidadorLuhnTarjeta {

    public static boolean esNumeroTarjetaValido(String numeroTarjeta) {
        if (numeroTarjeta == null || !numeroTarjeta.matches("\\d{13,19}")) {
            return false;
        }
        int suma = 0;
        boolean duplicarDigito = false;

        // Se recorre de derecha a izquierda duplicando uno de cada dos dígitos
        for (int i = numeroTarjeta.length() - 1; i >= 0; i--) {
            int digito = numeroTarjeta.charAt(i) - '0';
            if (duplicarDigito) {
                digito *= 2;
                if (digito > 9) {
                    digito -= 9; // Equivale a sumar los dos dígitos del resultado (ej: 16 -> 1+6 = 7)
                }
            }
            suma += digito;
            duplicarDigito = !duplicarDigito;
        }
        // Es válido si y solo si la suma total es múltiplo exacto de 10
        return (suma % 10 == 0);
    }
}
```

---

## 16. Árbol de Decisión Rápido: "¿Qué Algoritmo o Estructura Uso para mi Problema?"

Cuando estés diseñando una funcionalidad en el trabajo, busca tu necesidad en la columna izquierda y aplica la solución de la derecha:

| Si tu problema en el sistema es... | Usa esta Estructura o Algoritmo | Complejidad |
|---|---|---|
| *"Tengo que cruzar dos listas grandes sin que el servidor se cuelgue"* | Indexa una lista en un **`HashMap<ID, Objeto>`** o **`HashSet<ID>`** y recorre la otra. | `O(n)` |
| *"Necesito eliminar duplicados de una lista respetando el orden en que venían"* | **`new ArrayList<>(new LinkedHashSet<>(lista))`** | `O(n)` |
| *"Necesito saber a qué escalón de impuestos, envío o descuento pertenece un monto"* | **`TreeMap.floorEntry(monto)`** (Árbol Rojo-Negro). | `O(log n)` |
| *"Necesito atender pedidos o tareas según su urgencia y no solo por orden de llegada"* | **`PriorityQueue`** (*Heap*) con un `Comparator`. | `O(log n)` |
| *"Necesito detectar actividad inusual en los últimos X minutos móviles"* | **Ventana Deslizante (*Sliding Window*)**. | `O(n)` |
| *"Necesito recorrer categorías hijas, carpetas o permisos heredados sin límite de niveles"* | **BFS con `ArrayDeque`** (o DFS con un `Set` de visitados para evitar ciclos). | `O(V + E)` |
| *"Necesito calcular en qué orden ejecutar tareas que dependen unas de otras"* | **Ordenamiento Topológico (*Topological Sort*)**. | `O(V + E)` |
| *"Necesito guardar en memoria los datos más consultados sin quedarme sin RAM"* | **Caché con desalojo `LRU` o `W-TinyLFU` (Caffeine / Redis)**. | `O(1)` |
| *"Necesito proteger un endpoint público para que no hagan más de 60 requests por minuto"* | **Algoritmo *Token Bucket* (Bucket4j / Redis)**. | `O(1)` |
| *"Necesito reintentar llamadas a un banco o API caída sin causarle una estampida"* | **Retroceso Exponencial con Aleatoriedad (*Exponential Backoff + Jitter*)**. | `O(1)` |
| *"Necesito validar en el acto si un número de tarjeta o CUIT está bien tipeado"* | **Algoritmo de Luhn (Módulo 10)** para tarjetas / **Módulo 11** para CUIT/CBU. | `O(n)` |
| *"Necesito guardar contraseñas vs firmar un webhook vs encriptar un secreto recuperable"* | Contraseñas: **`BCrypt`/`Argon2`** \| Firma Webhook: **`HMAC-SHA256`** \| Secreto reversible: **`AES-256-GCM`**. | — |

> **Tip de lectura:** Cuando revises un método lento en una aplicación Java/Spring Boot, busca siempre **bucles `for` o `.stream()` que adentro llamen a `list.contains(...)`, `list.indexOf(...)` o hagan una consulta a la base de datos (`repository.findBy...`)**. Esos dos patrones (búsqueda lineal repetida `O(n²)` en memoria y el problema de las `N+1` queries en base de datos) son la causa del 95% de los problemas de rendimiento en sistemas empresariales, y ambos se resuelven agrupando en lote e indexando en un **`HashMap` / `HashSet`**.

