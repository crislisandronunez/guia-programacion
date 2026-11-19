# Glosario y Manual de Diagnóstico de Cuellos de Botella (*Bottlenecks*): Redes, Base de Datos, Consumidores y Flujos de Código (Java y Spring Boot)

Este documento es una **herramienta práctica de diagnóstico, monitoreo y optimización** diseñada para ayudarte a **detectar dónde se está frenando un sistema (*Bottleneck*), monitorearlo con métricas exactas y corregirlo en el código** en las 4 áreas críticas: **1) Redes y Llamadas HTTP, 2) Base de Datos y Conexiones, 3) Consumidores de Colas (Kafka/RabbitMQ) y 4) Flujos de Código, Hilos y Memoria JVM**.

---

## Índice
1. [¿Qué es un Cuello de Botella (*Bottleneck*) y la Ley de Amdahl?](#1-qué-es-un-cuello-de-botella-bottleneck-y-la-ley-de-amdahl)
2. [Glosario de Métricas para Detectar Cuellos de Botella (`p95`, `p99`, Método USE y RED)](#2-glosario-de-métricas-para-detectar-cuellos-de-botella-p95-p99-método-use-y-red)
3. [Tabla Diagnóstica Rápida: "Dime el Síntoma y te Diré Dónde Está el Cuello de Botella"](#3-tabla-diagnóstica-rápida-dime-el-síntoma-y-te-diré-dónde-está-el-cuello-de-botella)
4. [Área 1: Cuellos de Botella de RED y Llamadas HTTP (Llamadas en Serie, Pool HTTP, Payloads)](#4-área-1-cuellos-de-botella-de-red-y-llamadas-http-llamadas-en-serie-pool-http-payloads)
5. [Área 2: Cuellos de Botella en BASE DE DATOS — Parte A (Agotamiento del Pool HikariCP)](#5-área-2-cuellos-de-botella-en-base-de-datos--parte-a-agotamiento-del-pool-hikaricp)
6. [Área 2: Cuellos de Botella en BASE DE DATOS — Parte B (El Problema `N+1`, Índices y *Hot Rows*)](#6-área-2-cuellos-de-botella-en-base-de-datos--parte-b-el-problema-n1-índices-y-hot-rows)
7. [Área 3: Cuellos de Botella en CONSUMIDORES (Kafka / Colas, *Consumer Lag* y Particiones)](#7-área-3-cuellos-de-botella-en-consumidores-kafka--colas-consumer-lag-y-particiones)
8. [Área 4: Cuellos de Botella en FLUJOS DE CÓDIGO, CPU y MEMORIA JVM (`O(n²)`, `synchronized`, GC)](#8-área-4-cuellos-de-botella-en-flujos-de-código-cpu-y-memoria-jvm-on²-synchronized-gc)
9. [Cómo Monitorear Cuellos de Botella en Vivo con Spring Boot Actuator y Micrometer](#9-cómo-monitorear-cuellos-de-botella-en-vivo-con-spring-boot-actuator-y-micrometer)
10. [Detector Automático de Métodos y Queries Lentas en Código (AOP + Hibernate Interceptor)](#10-detector-automático-de-métodos-y-queries-lentas-en-código-aop--hibernate-interceptor)
11. [Guía de Supervivencia en Producción: Cómo Leer un *Thread Dump*, *Heap Dump* y `pg_stat_statements`](#11-guía-de-supervivencia-en-producción-cómo-leer-un-thread-dump-heap-dump-y-pg_stat_statements)
12. [Checklist Preventivo Anti-Cuellos de Botella para Revisiones de Código (*Code Review*)](#12-checklist-preventivo-anti-cuellos-de-botella-para-revisiones-de-código-code-review)

---

## 1. ¿Qué es un Cuello de Botella (*Bottleneck*) y la Ley de Amdahl?

En cualquier sistema de software, la velocidad total de una operación está limitada por **su eslabón más lento**. Eso es un **Cuello de Botella (*Bottleneck*)**.

```text
[Entrada: 5.000 req/s] ──> [API Spring Boot (Aguanta 5.000/s)] ──> [Pool BD Mal Configurado (Aguanta 50/s)] ──> [Salida: Solo 50 req/s]
                                                                               ▲
                                                                   ¡AQUÍ ESTÁ EL CUELLO DE BOTELLA!
```

### Las 2 Reglas de Oro de Optimización:
1. **Nunca optimices a ciegas:** Si tu endpoint tarda `1.000 ms` de los cuales `990 ms` se van esperando una consulta SQL sin índice y `10 ms` en código Java, optimizar el código Java no servirá de nada. Siempre ataca primero el paso que consume el mayor porcentaje del tiempo (**Ley de Amdahl**).
2. **Al destrabar un cuello de botella, el cuello de botella se mueve al siguiente eslabón:** Si aceleras tu API con Hilos Virtuales para mandar 10 veces más tráfico hacia la base de datos, el próximo límite será el pool de conexiones o el disco de la base de datos.

---

## 2. Glosario de Métricas para Detectar Cuellos de Botella (`p95`, `p99`, Método USE y RED)

Cuando mires un tablero de **Grafana, Prometheus, Datadog o New Relic**, estas son las palabras clave que te dicen dónde está el problema:

| Término de Monitoreo | Significado Exacto | Por qué es clave para detectar cuellos de botella |
|---|---|---|
| **Promedio (`avg`) vs Percentiles (`p50`, `p95`, `p99`)** | **El promedio miente:** Si 99 usuarios tardan `10 ms` y 1 usuario tarda `50 segundos`, el promedio da `500 ms` y oculta el desastre.<br>- **`p50` (Mediana):** El 50% de los usuarios tarda menos que esto.<br>- **`p95`:** El 95% tarda menos que esto.<br>- **`p99` (*Tail Latency*):** El 1% más lento (donde se ven los cuellos de botella reales). | Si tu `p50` es `20 ms` pero tu `p99` es `8.000 ms`, tienes bloqueos de base de datos, pausas de *Garbage Collector* o el problema `N+1` en los clientes con muchos pedidos. |
| **Método USE (Para Hardware / Recursos: CPU, RAM, Disco, Pool)** | Creado por Brendan Gregg. Para cada recurso mide 3 cosas:<br>1. **U — Utilization (Utilización):** `%` de uso (ej. Pool Hikari al `100%`).<br>2. **S — Saturation (Saturación):** Cuántos esperan en fila (ej. `hikaricp_connections_pending > 0`).<br>3. **E — Errors (Errores):** Timeouts o fallos al pedir el recurso. | Si la **Saturación (`pending` / cola de espera)** es mayor a `0` de forma sostenida, **ese recurso es tu cuello de botella**. |
| **Método RED (Para Servicios / Endpoints HTTP)** | Para cada endpoint o microservicio mide:<br>1. **R — Rate (Tasa):** Peticiones por segundo (`RPS`).<br>2. **E — Errors (Errores):** Peticiones que fallan (`HTTP 5xx`).<br>3. **D — Duration (Duración):** Tiempo que tardan (`p95` / `p99`). | Si la **Duración (`D`)** sube cuando el **Rate (`R`)** sube, tienes contención de bloqueos o falta de hilos/conexiones. |
| **I/O Bound vs CPU Bound** | - **CPU Bound:** El cuello de botella es el procesador al `100%` (ej. un bucle `O(n²)`, comprimir PDFs, `BCrypt`).<br>- **I/O Bound:** La CPU está al `10%`, pero el sistema está lento porque los hilos están **esperando respuesta de red o disco** (SQL, API externa). | El 90% de los cuellos de botella en aplicaciones empresariales Spring Boot son **I/O Bound** (esperando a la BD o a otra API). |
| **Lock Contention (Contención de Bloqueos)** | Varios hilos haciendo fila india porque quieren modificar la misma fila de la BD (`FOR UPDATE`) o entrar al mismo método `synchronized`. | Aunque tengas 32 núcleos de CPU libres, si todos los hilos esperan el mismo bloqueo, el sistema procesa de a 1 por vez. |

---

## 3. Tabla Diagnóstica Rápida: "Dime el Síntoma y te Diré Dónde Está el Cuello de Botella"

Usa esta tabla de bolsillo cuando un sistema ande lento en producción:

| Síntoma que Ves en Producción | Dónde Está el Cuello de Botella | Causa Raíz Más Probable | Solución Directa |
|---|---|---|---|
| **La CPU de la App está baja (`10%`), la CPU de la BD está baja (`15%`), pero la API tarda `5 segundos` o da error `Connection is not available, request timed out after 30000ms`** | **Pool de Conexiones HikariCP agotado** ([Sección 5](#5-área-2-cuellos-de-botella-en-base-de-datos--parte-a-agotamiento-del-pool-hikaricp)). | Tienes un método `@Transactional` que adentro hace una llamada HTTP lenta o tarda mucho, secuestrando las conexiones de la BD. | Sacar las llamadas HTTP y cálculos lentos **afuera** del método `@Transactional`. |
| **Un listado que con 10 filas tardaba `20 ms`, con 500 filas tarda `4 segundos` y ejecuta cientos de `SELECT` chiquitos** | **Problema de las `N+1` Queries en JPA/Hibernate** ([Sección 6](#6-área-2-cuellos-de-botella-en-base-de-datos--parte-b-el-problema-n1-índices-y-hot-rows)). | Estás recorriendo una lista de entidades y accediendo a una relación `LAZY` adentro del bucle o del Mapper. | Usar **`JOIN FETCH`**, **`@EntityGraph`** o una **Proyección directa a DTO (`record`)** para traer todo en `1 sola query`. |
| **La CPU de la Base de Datos se clava al `100%` cada vez que los usuarios buscan o filtran pedidos** | **Falta de Índice en la BD (`Seq Scan`)** ([Sección 6](#6-área-2-cuellos-de-botella-en-base-de-datos--parte-b-el-problema-n1-índices-y-hot-rows)). | Una consulta frecuente filtra (`WHERE`) u ordena (`ORDER BY`) por una columna sin índice o usa `OFFSET` gigante. | Correr `EXPLAIN ANALYZE` y crear el **Índice Compuesto (Regla ESR)**. |
| **Un endpoint llama a 4 microservicios o APIs externas que tardan `300 ms` cada una, y el endpoint total tarda `1.200 ms`** | **Llamadas de Red Secuenciales (en serie)** ([Sección 4](#4-área-1-cuellos-de-botella-de-red-y-llamadas-http-llamadas-en-serie-pool-http-payloads)). | Estás esperando que termine la llamada HTTP 1 para recién disparar la 2, la 3 y la 4. | Disparar las 4 llamadas **en paralelo** con **`CompletableFuture.allOf(...)` + Virtual Threads** (baja de `1.200 ms` a **`300 ms`**). |
| **El `Consumer Lag` de Kafka sube sin parar a miles de mensajes atrasados** | **Consumidor Lento o Falta de Particiones** ([Sección 7](#7-área-3-cuellos-de-botella-en-consumidores-kafka--colas-consumer-lag-y-particiones)). | El `@KafkaListener` procesa e inserta de a 1 mensaje por vez o hace llamadas HTTP lentas bloqueando la partición. | Pasar a **Batch Listener (`batch = "true"`)** con `batchUpdate` y aumentar `concurrency` / particiones. |
| **La CPU de la App Java se clava al `100%` y empieza a responder lento o con pausas congeladas de varios segundos** | **Algoritmo `O(n²)` en memoria o *Garbage Collection Thrashing*** ([Sección 8](#8-área-4-cuellos-de-botella-en-flujos-de-código-cpu-y-memoria-jvm-on²-synchronized-gc)). | Tienes un bucle anidado (`list.contains()` dentro de un `for`) o estás cargando millones de objetos en RAM llenando el *Heap*. | Reemplazar búsqueda en `List` por **`HashMap`/`HashSet` (`O(1)`)** y paginar por lotes (*Keyset*). |

---

## 4. Área 1: Cuellos de Botella de RED y Llamadas HTTP (Llamadas en Serie, Pool HTTP, Payloads)

La red es miles de veces más lenta que la memoria RAM. Estos son los **3 cuellos de botella de red** más frecuentes y cómo eliminarlos en Java:

### 1. Cuello de Botella: Llamadas HTTP Secuenciales vs Paralelas (`CompletableFuture` + Virtual Threads)
Imagina que para armar la pantalla de resumen de un cliente necesitas consultar:
1. API de Saldo (`300 ms`),
2. API de Últimos Movimientos (`400 ms`),
3. API de Puntos de Fidelidad (`300 ms`).

- **Si las llamas una debajo de la otra (Secuencial):** `300 + 400 + 300 = 1.000 ms (1 segundo)`.
- **Si las lanzas en paralelo porque no dependen una de la otra:** Tardan lo que tarde la más lenta de las tres = **`400 ms` (60% más rápido)**.

```java
@Service
public class ResumenClienteParaleloService {

    private final SaldoHttpClient saldoClient;
    private final MovimientosHttpClient movimientosClient;
    private final PuntosHttpClient puntosClient;

    // Usamos un Executor de Virtual Threads de Java 21 (costo casi cero en RAM):
    private final ExecutorService virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor();

    public ResumenClienteParaleloService(SaldoHttpClient saldoClient,
                                         MovimientosHttpClient movimientosClient,
                                         PuntosHttpClient puntosClient) {
        this.saldoClient = saldoClient;
        this.movimientosClient = movimientosClient;
        this.puntosClient = puntosClient;
    }

    // ❌ LENTO (Secuencial): Tarda la SUMA de las 3 llamadas (300 + 400 + 300 = 1000 ms)
    public ResumenCompletoDto obtenerResumenSecuencialLento(Long clienteId) {
        var saldo = saldoClient.obtenerSaldo(clienteId);             // Espera 300ms...
        var movs  = movimientosClient.obtenerUltimos(clienteId);     // Espera 400ms...
        var ptos  = puntosClient.obtenerPuntos(clienteId);           // Espera 300ms...
        return new ResumenCompletoDto(saldo, movs, ptos);
    }

    // ✅ RÁPIDO (Paralelo): Lanza las 3 al mismo tiempo y tarda solo el MÁXIMO de las 3 (400 ms)
    public ResumenCompletoDto obtenerResumenEnParalelo(Long clienteId) {
        CompletableFuture<SaldoDto> fSaldo = CompletableFuture.supplyAsync(
                () -> saldoClient.obtenerSaldo(clienteId), virtualThreadExecutor
        );
        CompletableFuture<List<MovimientoDto>> fMovs = CompletableFuture.supplyAsync(
                () -> movimientosClient.obtenerUltimos(clienteId), virtualThreadExecutor
        );
        CompletableFuture<PuntosDto> fPuntos = CompletableFuture.supplyAsync(
                () -> puntosClient.obtenerPuntos(clienteId), virtualThreadExecutor
        );

        // Espera a que las 3 terminen en paralelo:
        CompletableFuture.allOf(fSaldo, fMovs, fPuntos).join();

        return new ResumenCompletoDto(fSaldo.join(), fMovs.join(), fPuntos.join());
    }
}
```

### 2. Cuello de Botella: Llamar a una API dentro de un Bucle `for` (*Chatty I/O*)
- **El problema:** Tienes 100 productos en un carrito y haces un `for` llamando 100 veces a `GET /precios/{id}`. Si cada viaje de red tarda `20 ms`, 100 viajes tardan **`2.000 ms`**.
- **La solución (*Batch Endpoint*):** Crear un endpoint que reciba la lista de los 100 IDs juntos (`POST /precios/batch` o `GET /precios?ids=1,2,3...`) y devuelva los 100 precios en **1 solo viaje de red de `25 ms`**.

### 3. Cuello de Botella: Crear una Conexión TCP + TLS Nueva en cada Llamada HTTP
Si tu `RestClient` usa `SimpleClientHttpRequestFactory` en alta escala, abre y cierra un socket TCP y hace el saludo criptográfico SSL/TLS en cada petición (gastando `100 ms` extra por llamada).
- **La solución:** En producción, respaldar siempre tu `RestClient` con un **Pool de Conexiones HTTP con `Keep-Alive`** (como `JdkClientHttpRequestFactory` o **Apache HttpClient 5** `PoolingHttpClientConnectionManager`) que reutiliza las conexiones TCP ya abiertas.

---

## 5. Área 2: Cuellos de Botella en BASE DE DATOS — Parte A (Agotamiento del Pool HikariCP)

Este es el **cuello de botella #1 que derriba aplicaciones Spring Boot en producción**.

### ¿Por qué ocurre la "Inanición del Pool de Conexiones" (*Connection Pool Starvation*)?
Tu aplicación tiene un pool de **20 conexiones** hacia PostgreSQL (`hikari.maximum-pool-size = 20`).
Si un programador pone `@Transactional` arriba de un método que:
1. Hace un `SELECT` (`1 ms`),
2. Genera un PDF pesado o llama a la API de un banco o de AFIP (`800 ms`),
3. Hace un `INSERT` (`2 ms`),

Esa conexión de base de datos queda **secuestrada e inútil durante `803 ms`** mientras espera a la API externa (cuando solo se usó para SQL durante `3 ms`). Con apenas 25 usuarios simultáneos, las 20 conexiones se agotan, todos los demás hilos hacen fila en HikariCP y la aplicación explota con:
`SQLTransientConnectionException: HikariPool-1 - Connection is not available, request timed out after 30000ms`.

### Cómo Evitarlo en el Código (Separación de Fases I/O vs Transacción SQL):

```java
// ❌ CUELLO DE BOTELLA GRAVE: @Transactional envuelve llamadas de red y trabajo pesado
@Service
public class EmisionFacturaCuelloBotellaService {

    @Transactional // ¡MAL! Abre conexión a PostgreSQL desde la línea 1 y no la suelta hasta el final
    public FacturaDto emitirFacturaLenta(Long pedidoId) {
        Pedido p = pedidoRepository.findById(pedidoId).orElseThrow(); // Usa la BD 1ms
        // La conexión a PostgreSQL queda SECUESTRADA sin hacer nada durante los 1500ms que tardan estas 2 líneas:
        CaeAfipDto cae = afipHttpClient.solicitarCaeFiscal(p);        // Red externa: 1200ms
        byte[] pdf = generadorPdf.armarFacturaPdf(p, cae);            // CPU pesada:   300ms
        s3Client.subirArchivo(pdf);                                   // Red S3:       200ms

        p.marcarFacturado(cae.numero());
        return mapper.toDto(pedidoRepository.save(p));                // Usa la BD 2ms
    }
}

// ✅ DISEÑO SIN CUELLO DE BOTELLA: La conexión a la BD solo se toca durante 2 milisegundos
@Service
public class EmisionFacturaOptimaService {

    private final PedidoTransaccionalHelper txHelper; // Bean auxiliar con transacciones cortas
    private final AfipHttpClient afipHttpClient;
    private final GeneradorPdf generadorPdf;
    private final S3Client s3Client;

    public EmisionFacturaOptimaService(PedidoTransaccionalHelper txHelper,
                                       AfipHttpClient afipHttpClient,
                                       GeneradorPdf generadorPdf,
                                       S3Client s3Client) {
        this.txHelper = txHelper;
        this.afipHttpClient = afipHttpClient;
        this.generadorPdf = generadorPdf;
        this.s3Client = s3Client;
    }

    // NO lleva @Transactional aquí: el hilo hace todo el trabajo lento SIN ocupar conexiones del pool HikariCP
    public FacturaDto emitirFacturaRapida(Long pedidoId) {
        // 1. Lee el pedido y suelta la conexión de inmediato (tarda 1ms de conexión)
        DatosPedidoDto datos = txHelper.obtenerDatosParaFacturar(pedidoId);

        // 2. Trabajo lento de red y CPU con CERO conexiones de base de datos ocupadas:
        CaeAfipDto cae = afipHttpClient.solicitarCaeFiscal(datos);
        byte[] pdf = generadorPdf.armarFacturaPdf(datos, cae);
        String urlPdf = s3Client.subirArchivo(pdf);

        // 3. Abre transacción, guarda el resultado en 2ms y suelta la conexión de inmediato:
        return txHelper.guardarFacturaConfirmada(pedidoId, cae.numero(), urlPdf);
    }
}
```

---

## 6. Área 2: Cuellos de Botella en BASE DE DATOS — Parte B (El Problema `N+1`, Índices y *Hot Rows*)

### 1. El Problema de las `N+1` Consultas (El enemigo silencioso de JPA/Hibernate)
Ocurre cuando haces **`1 consulta`** para traer una lista de `N` padres (ej. `50 Pedidos`) y luego, al convertir cada pedido a DTO en un bucle o `.stream().map(...)`, llamas a `pedido.getCliente().getNombre()` o `pedido.getItems()`.
Como la relación es `LAZY`, Hibernate dispara **`50 consultas adicionales` una por una** (`1 + 50 = 51 viajes de red a la base de datos`).

#### Las 3 Soluciones al Problema `N+1`:
```java
public interface PedidoSinCuelloBotellaRepository extends JpaRepository<PedidoEntity, Long> {

    // SOLUCIÓN 1: @EntityGraph (Trae el Pedido y su Cliente unido en 1 solo SELECT con LEFT JOIN)
    @EntityGraph(attributePaths = {"cliente", "items"})
    List<PedidoEntity> findTop50ByEstadoOrderByCreadoEnDesc(EstadoPedido estado);

    // SOLUCIÓN 2: JOIN FETCH en JPQL (Trae todo en 1 sola consulta)
    @Query("""
           SELECT p FROM PedidoEntity p
           JOIN FETCH p.cliente
           LEFT JOIN FETCH p.items
           WHERE p.id = :id
           """)
    Optional<PedidoEntity> buscarConTodoCargadoPorId(@Param("id") Long id);

    // SOLUCIÓN 3 (La más rápida para listados): Proyección directa a Record DTO (Trae solo las columnas necesarias en 1 SELECT)
    @Query("""
           SELECT new com.miempresa.dto.PedidoFilaResumenDto(p.id, c.nombre, p.total, p.estado)
           FROM PedidoEntity p
           INNER JOIN p.cliente c
           WHERE p.estado = :estado
           """)
    Slice<PedidoFilaResumenDto> listarResumenSinNMasUno(@Param("estado") EstadoPedido estado, Pageable pageable);
}
```
- **Tip extra en `application.yml`:** Agregar `spring.jpa.properties.hibernate.default_batch_fetch_size: 50`. Con esa sola línea, si en algún rincón del proyecto se escapó una carga `LAZY`, en vez de hacer 50 consultas individuales, Hibernate las agrupa automáticamente en 1 sola consulta con `WHERE id IN (?, ?, ..., ?)`.

### 2. Cuello de Botella por "Fila Caliente" (*Hot Row Lock Contention*)
- **El problema:** Tienes una única fila en una tabla (ej. el saldo de la cuenta recaudadora principal de un gran comercio o el stock de un producto en oferta relámpago) y 500 usuarios por segundo hacen `UPDATE` o `SELECT ... FOR UPDATE` sobre **esa misma fila**. Como PostgreSQL bloquea la fila hasta que termine cada transacción, los 500 usuarios quedan haciendo cola india.
- **La solución:**
  1. Dejar el `UPDATE` para el **último milisegundo** justo antes del final del método `@Transactional` (para reducir el tiempo que la fila está bloqueada de `100 ms` a `1 ms`).
  2. O dividir el contador en **Sub-cuentas / *Sharded Counters*** (ej. 10 filas `sub_cuenta_0` a `sub_cuenta_9` elegidas al azar con `ThreadLocalRandom.current().nextInt(10)` al acreditar, dividiendo por 10 la contención de bloqueos).

---

## 7. Área 3: Cuellos de Botella en CONSUMIDORES (Kafka / Colas, *Consumer Lag* y Particiones)

Cuando usas **Apache Kafka o RabbitMQ**, el cuello de botella se mide con una métrica: el **`Consumer Lag`** (cuántos mensajes se acumulan en la fila sin poder ser leídos a tiempo).

| Cuello de Botella en el Consumidor | Por qué se frena la cola | Solución Exacta en Spring Boot |
|---|---|---|
| **1. Tópico con 1 sola Partición** | Aunque levantes 10 servidores en Kubernetes, como el tópico tiene 1 sola partición, solo 1 hilo trabaja y 9 servidores miran sin hacer nada. | Crear el tópico con **al menos 6 o 12 particiones** y configurar `concurrency: 3` o más en el `@KafkaListener`. |
| **2. Llamada HTTP lenta dentro del hilo del `@KafkaListener`** | El hilo consumidor lee 1 mensaje, espera 500ms a una API externa, y recién ahí pasa al siguiente mensaje (procesa apenas 2 mensajes/seg por partición). | Usar lotes, llamadas asíncronas controladas o aumentar la cantidad de particiones y consumidores concurrentes. |
| **3. Hacer 1 `INSERT` en BD por cada mensaje individual** | Abrir y cerrar 1.000 transacciones SQL por segundo para 1.000 mensajes ahoga a la base de datos. | Activar **`batch = "true"`** en el `@KafkaListener` para recibir de a 500 mensajes juntos y hacer **1 solo `jdbcTemplate.batchUpdate`** ([ver guía de Kafka](file:///Users/rodrigonunez/Downloads/Guia-Programacion/Glosario-kafka.md#11-plantilla-6-c%C3%B3mo-programar-un-consumidor-masivo-por-lotes-batch-listener)). |
| **4. Tormenta de Rebalanceos (*Rebalance Storm*)** | Un lote tarda más de 5 minutos (`max.poll.interval.ms`), Kafka cree que el consumidor se colgó, le quita la partición y otro nodo vuelve a reprocesar el mismo lote desde cero. | Reducir `max.poll.records` (ej. a `100` o `200`) para que cada lote termine siempre en menos de 10 segundos. |

---

## 8. Área 4: Cuellos de Botella en FLUJOS DE CÓDIGO, CPU y MEMORIA JVM (`O(n²)`, `synchronized`, GC)

A veces la red y la base de datos están rápidas, pero **es nuestro propio código Java el que frena al servidor**:

| Cuello de Botella en el Código Java | Qué ocurre en la JVM | Cómo Detectar y Corregir en el Código |
|---|---|---|
| **1. Búsqueda Lineal dentro de un Bucle (`O(n²)` en memoria)** | Hacer `listaB.contains(x)` o `listaB.stream().filter(...)` adentro de un `for` sobre `listaA`. Con 30.000 elementos clava un núcleo de CPU al `100%` durante segundos. | Antes de entrar al `for`, pasar `listaB` a un **`HashSet` o `HashMap`** para que la búsqueda interna cueste **`O(1)`**. |
| **2. Abuso de `synchronized` y *Thread Pinning*** | Poner `public synchronized void procesar()` en un `@Service` Singleton hace que **todas las peticiones de todos los usuarios del país hagan fila india de a 1 por vez**, anulando el paralelismo del servidor. Además, en Java 21 `synchronized` con I/O dentro puede "clavar" (*pin*) el Hilo Virtual al hilo de CPU. | **Nunca poner `synchronized` en métodos de servicios que hacen I/O.** Si necesitas sincronización en Java 21+, usa **`ReentrantLock`** o estructuras sin bloqueo (`ConcurrentHashMap`, `AtomicLong`). |
| **3. Pausas del *Garbage Collector* (*GC Thrashing*) por exceso de objetos en RAM** | Si en cada request cargas listas gigantes de 500.000 objetos en memoria, el *Heap* se llena y la JVM congela todos los hilos (*Stop-The-World*) intentando liberar memoria antes de tirar `OutOfMemoryError`. | Paginar siempre en la base de datos (`Slice` / *Keyset*), usar proyecciones livianas y nunca hacer `.findAll()` sobre tablas grandes. |
| **4. Compilar `Pattern` (Regex) o instanciar `ObjectMapper` dentro de un método muy llamado** | Hacer `new ObjectMapper()` o `Pattern.compile(...)` 10.000 veces por segundo gasta muchísima CPU y memoria. | Declarar `Pattern` como `private static final Pattern PATRON = Pattern.compile(...)` e **inyectar siempre el `ObjectMapper` Singleton de Spring**. |

**Ejemplo de cómo reemplazar `synchronized` bloqueante por `ReentrantLock` compatible con Virtual Threads:**
```java
@Component
public class GestorTokenConLockOptimo {

    // En Java 21 con Virtual Threads, ReentrantLock NO clava (no hace pinning) del hilo físico de la CPU cuando espera I/O:
    private final ReentrantLock lock = new ReentrantLock();
    private String tokenActual;
    private Instant expiraEn = Instant.MIN;

    public String obtenerTokenRenovandoSiExpiro() {
        // Lectura rápida sin bloqueo si el token sigue vigente:
        if (Instant.now().isBefore(expiraEn)) {
            return tokenActual;
        }

        lock.lock();
        try {
            // Double-check dentro del lock por si otro hilo ya lo renovó mientras esperábamos:
            if (Instant.now().isBefore(expiraEn)) {
                return tokenActual;
            }
            this.tokenActual = llamarApiAutenticacionExterna();
            this.expiraEn = Instant.now().plusSeconds(3600);
            return this.tokenActual;
        } finally {
            lock.unlock(); // SIEMPRE liberar en el bloque finally
        }
    }

    private String llamarApiAutenticacionExterna() { return "jwt-nuevo"; }
}
```

---

## 9. Cómo Monitorear Cuellos de Botella en Vivo con Spring Boot Actuator y Micrometer

Spring Boot ya trae instrumentadas de fábrica las métricas exactas para descubrir cuellos de botella en `/actuator/metrics` o `/actuator/prometheus`. Estas son las **6 métricas vitales** que debes mirar y alertar:

| Nombre de la Métrica en Actuator / Prometheus | Qué Mide | Cuándo Dispara Alerta de Cuello de Botella |
|---|---|---|
| **`hikaricp.connections.pending`** | Cuántos hilos de tu app están **haciendo fila esperando** que se libere una conexión a la base de datos. | Si es **`> 0`** durante más de 5 segundos ➔ **Cuello de botella en el Pool de BD**. |
| **`hikaricp.connections.usage`** | Cuántos milisegundos tarda un hilo en usar y devolver la conexión SQL. | Si el `p95` supera **`50 ms`** ➔ Tienes transacciones `@Transactional` demasiado largas o queries lentas. |
| **`http.server.requests` (`p95`, `p99`)** | Tiempo de respuesta de cada endpoint de tus `@RestController`. | Permite ordenar de inmediato cuáles son los 5 endpoints más lentos de toda la empresa. |
| **`http.client.requests`** | Tiempo que tardan las llamadas salientes (`RestClient`) hacia proveedores externos (Stripe, AFIP, Correo). | Te muestra al instante si la lentitud es culpa tuya o de un proveedor externo. |
| **`jvm.gc.pause`** | Cuántos milisegundos estuvo congelada la JVM limpiando basura de la memoria RAM. | Si supera **`200 ms`** frecuentes ➔ Estás cargando demasiados datos en memoria en algún endpoint. |
| **`kafka.consumer.fetch.manager.records.lag.max`** | Cantidad de mensajes acumulados sin procesar en Kafka (*Consumer Lag*). | Si crece sostenidamente ➔ El consumidor es un cuello de botella. |

---

## 10. Detector Automático de Métodos y Queries Lentas en Código (AOP + Hibernate Interceptor)

Si quieres que tu propia aplicación **te avise sola en los logs cada vez que un servicio o repositorio tarda más de `500 ms`** (para cazar cuellos de botella en desarrollo y producción), agrega este Aspecto (`@Aspect`) en tu proyecto:

```java
package com.miempresa.monitoreo;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class DetectorCuelloDeBotellaAspect {

    private static final Logger log = LoggerFactory.getLogger(DetectorCuelloDeBotellaAspect.class);
    private static final long UMBRAL_ALERTA_MS = 500; // Alerta si un método supera los 500 milisegundos

    // Envuelve automáticamente todos los métodos de nuestros @Service y @Repository:
    @Around("within(@org.springframework.stereotype.Service *) || within(@org.springframework.stereotype.Repository *)")
    public Object medirTiempoEjecucion(ProceedingJoinPoint joinPoint) throws Throwable {
        long inicio = System.currentTimeMillis();
        try {
            return joinPoint.proceed();
        } finally {
            long duracionMs = System.currentTimeMillis() - inicio;
            if (duracionMs >= UMBRAL_ALERTA_MS) {
                log.warn("🐢 CUELLO DE BOTELLA DETECTADO: {}.{}() tardó {} ms (Umbral: {} ms)",
                        joinPoint.getSignature().getDeclaringType().getSimpleName(),
                        joinPoint.getSignature().getName(),
                        duracionMs,
                        UMBRAL_ALERTA_MS);
            }
        }
    }
}
```

Y para que **Hibernate te avise solo en consola cuando una consulta SQL tarda más de `300 ms`**, agrega esta sola línea en tu `application.yml`:
```yaml
spring:
  jpa:
    properties:
      hibernate:
        session:
          events:
            log:
              LOG_QUERIES_SLOWER_THAN_MS: 300 # Hibernate imprime un log automático si cualquier SQL tarda > 300ms
```

---

## 11. Guía de Supervivencia en Producción: Cómo Leer un *Thread Dump*, *Heap Dump* y `pg_stat_statements`

Cuando un servidor se pone lento en producción, estas 3 herramientas te dicen la verdad en 1 minuto:

### 1. *Thread Dump* (`jstack <PID>` o `/actuator/threaddump`)
Saca una "foto" de qué línea exacta de código están ejecutando todos los hilos en este instante.
- Si ves 150 hilos en estado `WAITING` o `BLOCKED` apuntando todos a `HikariPool.getConnection()` o a un método tuyo: **allí está exactamente la línea del cuello de botella**.

### 2. Consulta de las 10 Queries que Más Frenan tu Base de Datos PostgreSQL (`pg_stat_statements`)
Ejecuta este SQL en tu base de datos PostgreSQL para ver el ranking de las consultas que más tiempo total y promedio consumen:
```sql
SELECT
    calls AS cantidad_ejecuciones,
    ROUND(total_exec_time::numeric, 2) AS tiempo_total_ms,
    ROUND(mean_exec_time::numeric, 2) AS promedio_por_ejecucion_ms,
    rows AS filas_devueltas,
    query
FROM pg_stat_statements
ORDER BY total_exec_time DESC
LIMIT 10;
```

### 3. Consulta para Ver si Hay Bloqueos Trabados en Vivo en PostgreSQL (`pg_locks`)
Si una pantalla se queda colgada intentando hacer un `UPDATE`, esta consulta te muestra qué transacción está bloqueando a cuál:
```sql
SELECT
    bloqueado.pid AS pid_bloqueado,
    bloqueado.query AS query_que_espera,
    bloqueador.pid AS pid_culpable_del_bloqueo,
    bloqueador.query AS query_del_culpable,
    NOW() - bloqueador.query_start AS tiempo_lleva_bloqueando
FROM pg_stat_activity bloqueado
JOIN pg_locks l1 ON bloqueado.pid = l1.pid AND NOT l1.granted
JOIN pg_locks l2 ON l1.relation = l2.relation AND l2.granted
JOIN pg_stat_activity bloqueador ON l2.pid = bloqueador.pid;
```

---

## 12. Checklist Preventivo Anti-Cuellos de Botella para Revisiones de Código (*Code Review*)

Antes de aprobar o subir cualquier funcionalidad nueva, pasa por estos **8 controles rápidos**:

| # | Control Anti-Cuello de Botella | Área que Protege |
|---|---|---|
| **1** | **¿Hay alguna llamada HTTP (`RestClient`), envío de email o generación de PDF adentro de un método `@Transactional`?** ➔ Si la hay, sácala afuera para no agotar HikariCP. | Pool de BD |
| **2** | **¿Hay una llamada a base de datos o a una API externa adentro de un bucle `for` o `.stream()`?** ➔ Si la hay, cámbiala por una consulta en lote (`WHERE id IN (...)` o `JOIN FETCH`). | Red y BD (`N+1`) |
| **3** | **¿Se hacen 2 o más llamadas HTTP independientes una después de la otra?** ➔ Si no dependen entre sí, lánzalas en paralelo con `CompletableFuture.allOf()`. | Latencia de Red |
| **4** | **¿Todas las columnas nuevas usadas en `WHERE`, `JOIN` y `ORDER BY` tienen su `CREATE INDEX`?** ➔ Recordar que PostgreSQL no indexa las `FOREIGN KEY` automáticamente. | CPU y Disco de BD |
| **5** | **¿Algún endpoint o job usa `findAll()` sin paginar o usa `COUNT(*)` / `OFFSET` sobre tablas millonarias?** ➔ Cambiar por `Slice<T>` o *Keyset Pagination* (`WHERE id > :ultimoId`). | Memoria RAM y BD |
| **6** | **¿Hay algún `list.contains()` o búsqueda lineal adentro de otro bucle sobre listas grandes?** ➔ Pasar primero la lista a un `HashSet` o `HashMap` para que corra en `O(1)`. | CPU de la JVM |
| **7** | **¿El consumidor de Kafka de alto volumen inserta de a 1 fila o usa lotes?** ➔ Para alto caudal usar `batch = "true"` + `jdbcTemplate.batchUpdate`. | Consumidores |
| **8** | **¿Está habilitado `spring.threads.virtual.enabled: true` (Java 21) y el cliente HTTP tiene Pool `Keep-Alive` con Timeouts?** | Hilos y Red |

> **Tip de oro:** El 90% de los cuellos de botella graves en sistemas empresariales no requieren cambiar de lenguaje de programación ni comprar servidores el triple de caros: se solucionan **quitando llamadas de red de adentro de `@Transactional`**, **eliminando queries `N+1` en bucles**, **agregando el índice compuesto faltante** y **cambiando búsquedas lineales en listas por `HashMap`**.

