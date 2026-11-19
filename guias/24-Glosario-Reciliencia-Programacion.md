# Glosario y Guía Completa de Resiliencia y Tolerancia a Fallos en Programación (Java, Spring Boot y Resilience4j)

Este documento reúne **todos los conceptos, patrones de diseño resiliente, configuraciones de `Resilience4j` y `Spring Retry`, manejo de colas muertas (`DLQ`), protección de bases de datos y plantillas de código en Java y Spring Boot** para construir sistemas que **sigan funcionando y vendiendo incluso cuando se caen proveedores externos, redes o servidores**.

---

## Índice
1. [¿Qué es la Resiliencia en Software y por qué los Sistemas se Caen en Cascada?](#1-qué-es-la-resiliencia-en-software-y-por-qué-los-sistemas-se-caen-en-cascada)
2. [Vocabulario Esencial de Resiliencia y Sistemas Distribuidos](#2-vocabulario-esencial-de-resiliencia-y-sistemas-distribuidos)
3. [Los 6 Escudos de Resiliencia (Mapa General de Patrones)](#3-los-6-escudos-de-resiliencia-mapa-general-de-patrones)
4. [Escudo 1: Timeouts (`Connect Timeout`, `Read Timeout` y `@TimeLimiter`)](#4-escudo-1-timeouts-connect-timeout-read-timeout-y-timelimiter)
5. [Escudo 2: Retry con *Exponential Backoff* y *Jitter* (`@Retry` y `@Retryable`)](#5-escudo-2-retry-con-exponential-backoff-y-jitter-retry-y-retryable)
6. [Escudo 3: Circuit Breaker / Disyuntor (`CLOSED`, `OPEN`, `HALF_OPEN`)](#6-escudo-3-circuit-breaker--disyuntor-closed-open-half_open)
7. [Escudo 4: Bulkhead / Mamparo de Aislamiento (Semáforos vs Pool de Hilos)](#7-escudo-4-bulkhead--mamparo-de-aislamiento-semáforos-vs-pool-de-hilos)
8. [Escudo 5: Rate Limiter y *Load Shedding* (Control de Tráfico y Rechazo Temprano)](#8-escudo-5-rate-limiter-y-load-shedding-control-de-tráfico-y-rechazo-temprano)
9. [Escudo 6: Fallback y Degradación Elegante (*Graceful Degradation*)](#9-escudo-6-fallback-y-degradación-elegante-graceful-degradation)
10. [El Orden Exacto de Ejecución al Combinar los Patrones en `Resilience4j`](#10-el-orden-exacto-de-ejecución-al-combinar-los-patrones-en-resilience4j)
11. [Configuración Completa de `Resilience4j` en `application.yml` (Lista para Producción)](#11-configuración-completa-de-resilience4j-en-applicationyml-lista-para-producción)
12. [Resiliencia en Bases de Datos (Reintentos de `@Version`, HikariCP y *Failover*)](#12-resiliencia-en-bases-de-datos-reintentos-de-version-hikaricp-y-failover)
13. [Resiliencia en Caché (*Cache Stampede*, *Cache Penetration* y *Fail-Open*)](#13-resiliencia-en-caché-cache-stampede-cache-penetration-y-fail-open)
14. [Resiliencia en Colas y Mensajería (`DLQ` - Dead Letter Queue, *Poison Pills* y *Backpressure*)](#14-resiliencia-en-colas-y-mensajería-dlq---dead-letter-queue-poison-pills-y-backpressure)
15. [Apagado Elegante (*Graceful Shutdown*), *Health Probes* e Ingeniería del Caos](#15-apagado-elegante-graceful-shutdown-health-probes-e-ingeniería-del-caos)
16. [Checklist Rápido de Resiliencia Antes de Salir a Producción](#16-checklist-rápido-de-resiliencia-antes-de-salir-a-producción)

---

## 1. ¿Qué es la Resiliencia en Software y por qué los Sistemas se Caen en Cascada?

> *"En sistemas distribuidos todo falla todo el tiempo" (Werner Vogels, CTO de Amazon).*

La **Resiliencia** no significa evitar que ocurran fallos (eso es imposible porque las redes se cortan, los discos se llenan y las APIs de terceros se caen); significa **diseñar tu sistema para que soporte el fallo de una pieza sin colapsar por completo y se recupere solo automáticamente**.

### Anatomía de una Caída en Cascada (*Cascading Failure*)
Mira cómo un problema menor en un servicio secundario derriba a toda una empresa cuando **NO** hay resiliencia:
1. Tu aplicación tiene **200 hilos en Tomcat** para atender a todos tus clientes.
2. El endpoint de *Checkout* llama a una API externa de facturación o correo que normalmente tarda `100 ms`.
3. A las 14:00 hs, esa API externa sufre un problema y empieza a tardar **`60 segundos`** en responder (y tu código no tiene *Timeout* corto ni *Circuit Breaker*).
4. En solo 4 segundos entran 200 usuarios a comprar -> **los 200 hilos de tu servidor quedan congelados esperando a la API lenta**.
5. Como se agotaron los 200 hilos de Tomcat, **nadie puede ni siquiera iniciar sesión ni ver el catálogo**, aunque esas pantallas no usen la API caída. **Toda tu empresa quedó fuera de línea por culpa de un tercero.**

```text
❌ SIN RESILIENCIA (Efecto Dominó):
[API Externa Lenta (60s)] ──(Congela 200 hilos)──> [Tu Backend Agotado] ──> [Toda la Empresa Caída (503)]

✅ CON RESILIENCIA (Aislado y Degradado Elegantemente):
[API Externa Lenta] ──(Timeout 2s + Circuit Breaker Abierto)──> [Usa Plan B / Cola Asíncrona] ──> [Tu Empresa Sigue Vendiendo 100%]
```

---

## 2. Vocabulario Esencial de Resiliencia y Sistemas Distribuidos

| Término Técnico | Significado en Español | Ejemplo Práctico en Sistemas |
|---|---|---|
| **Resilience (Resiliencia)** | Capacidad de absorber fallos, adaptarse y recuperarse automáticamente sin intervención humana. | Si la pasarela A da timeout, el sistema corta en 2s y cobra por la pasarela B solo. |
| **Fault Tolerance (Tolerancia a Fallos)** | Capacidad de un sistema de seguir operando sin interrupción aunque falle uno o más de sus componentes físicos. | Si se apaga 1 de los 3 nodos de PostgreSQL, la réplica asume como primaria en segundos. |
| **High Availability - HA (Alta Disponibilidad)** | Porcentaje de tiempo que el sistema está operativo (ej. *"Cuatro nueves" = 99.99%*, que permite solo 52 minutos de caída al año). | Correr mínimo 2 o 3 instancias del microservicio en distintas zonas de disponibilidad (*Multi-AZ*). |
| **Cascading Failure (Fallo en Cascada)** | Efecto dominó donde la lentitud o caída de un servicio arrastra y tumba a todos los servicios que lo llaman. | Base de datos lenta -> agota pool de conexiones -> agota hilos de API -> cae el API Gateway. |
| **Blast Radius (Radio de Explosión)** | Qué porcentaje del sistema o de los usuarios se ve afectado cuando falla un componente. El objetivo de la resiliencia es **achicar el radio de explosión**. | Usar **Bulkhead** para que si falla el módulo de reportes PDF, solo falle ese botón y no los cobros. |
| **Transient Fault (Fallo Transitorio)** | Error pasajero que se arregla solo unos milisegundos después (microcorte de red, bloqueo momentáneo de fila, *Garbage Collection pause*). | Un `SocketTimeoutException` puntual que al reintentar 500ms después funciona perfecto. |
| **Permanent Fault (Fallo Permanente)** | Error definitivo que **nunca se va a arreglar por más que reintentes** (`400 Bad Request`, `401 Unauthorized`, tarjeta robada, JSON mal formado). | **Regla de oro:** Solo se reintentan los fallos *transitorios* (`502`, `503`, `504`, `Timeout`), jamás los *permanentes* (`400`, `401`, `404`, `422`). |
| **Thundering Herd (Estampida de Búfalos)** | Cuando un servicio caído vuelve a levantarse y miles de clientes en espera lo golpean todos juntos en el mismo milisegundo, volviéndolo a tumbar. | Se previene agregando aleatoriedad (**Jitter**) a los tiempos de reintento y de expiración de caché. |
| **Fail-Fast (Fallar Rápido)** | Si sabes que una operación no va a poder completarse (el circuito está abierto o no hay conexiones), rechazarla en `1 ms` en lugar de hacer esperar `30 s` al usuario. | `connection-timeout: 2000` en HikariCP o un `CircuitBreaker` en estado `OPEN`. |
| **Fail-Open vs Fail-Closed** | - **Fail-Open:** Si falla un control secundario (ej. se cayó Redis del limitador de velocidad o recomendador), **dejas pasar la petición** para no frenar el negocio.<br>- **Fail-Closed:** Si falla un control de seguridad crítico (ej. validador de permisos o saldo), **bloqueas la petición** por seguridad. | Si se cae Redis de caché, haces *Fail-Open* y lees de la BD. Si falla la firma del token JWT, haces *Fail-Closed* y rechazas con `401`. |

---

## 3. Los 6 Escudos de Resiliencia (Mapa General de Patrones)

En el ecosistema Java y Spring Boot, el estándar de la industria es **Resilience4j** (integrado en `spring-cloud-starter-circuitbreaker-resilience4j`). Ofrece estos **6 escudos** que trabajan en equipo:

| Escudo / Patrón | Qué Problema Exacto Resuelve | Cómo Actúa |
|---|---|---|
| **1. Timeout (`TimeLimiter`)** | *"El servidor externo no responde y mi hilo se quedó colgado esperando eternamente"*. | Corta la espera si supera un límite máximo estricto (ej. `2 segundos`). |
| **2. Retry (Reintento con Backoff + Jitter)** | *"Hubo un parpadeo de red de medio segundo y falló una petición que era válida"*. | Vuelve a intentar automáticamente 2 o 3 veces esperando un tiempo creciente + aleatorio. |
| **3. Circuit Breaker (Disyuntor)** | *"El proveedor externo está caído hace 10 minutos y seguimos mandándole peticiones que tardan y fallan"*. | Cuando el porcentaje de fallos supera el umbral (ej. 50%), **abre el circuito** y corta las llamadas en `0 ms` durante 30 segundos. |
| **4. Bulkhead (Mamparo de Barco)** | *"La integración con el Correo anda lenta y se consumió los 200 hilos de mi servidor dejando sin hilos al resto de la app"*. | Le asigna un cupo máximo aislado (ej. máximo 15 llamadas simultáneas) a ese proveedor. |
| **5. Rate Limiter (Limitador de Tasa)** | *"Un cliente nos está mandando 5.000 peticiones por segundo (o la API externa nos multa si pasamos de 100 req/s)"*. | Limita el caudal a `N` operaciones por período de tiempo. |
| **6. Fallback (Plan B / Contingencia)** | *"Falló el proveedor principal, ¿qué le respondemos al usuario para no mostrarle una pantalla rota?"*. | Ejecuta un método alternativo (usa una pasarela secundaria, devuelve caché o encola para procesar después). |

---

## 4. Escudo 1: Timeouts (`Connect Timeout`, `Read Timeout` y `@TimeLimiter`)

### La Regla #1 de la Resiliencia: "Jamás hagas una llamada de red sin Timeout"
Muchos clientes HTTP vienen por defecto con un timeout infinito (`0`). En producción debes configurar **dos tipos de Timeout** en cada cliente HTTP:
1. **`Connect Timeout` (Tiempo de conexión TCP):** Cuánto tarda en establecerse el saludo inicial con el servidor remoto. Si el servidor está apagado o la IP no responde, debe cortar rápido (ej. **`1 a 2 segundos`**).
2. **`Read / Response Timeout` (Tiempo de lectura):** Una vez conectado, cuánto tiempo máximo esperamos a que el servidor procese y nos devuelva el JSON (ej. **`3 a 5 segundos`**).

```java
@Configuration
public class HttpClientResilienteConfig {

    @Bean
    public RestClient pasarelaRestClient(RestClient.Builder builder) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        // 1. Connect Timeout: Si en 2 segundos no abrió el socket TCP, aborta rápido
        factory.setConnectTimeout(Duration.ofSeconds(2));
        // 2. Read Timeout: Si en 4 segundos no devolvió la respuesta HTTP, aborta y libera el hilo
        factory.setReadTimeout(Duration.ofSeconds(4));

        return builder
                .baseUrl("https://api.proveedor-pagos.com")
                .requestFactory(factory)
                .build();
    }
}
```

---

## 5. Escudo 2: Retry con *Exponential Backoff* y *Jitter* (`@Retry` y `@Retryable`)

### ¿Qué errores SÍ se deben reintentar y cuáles NUNCA se deben reintentar?
- **SÍ reintentar (`retryExceptions`):** Errores de red transitorios (`SocketTimeoutException`, `ConnectException`, `HttpServerErrorException` `502 Bad Gateway`, `503 Service Unavailable`, `504 Gateway Timeout`) y choques de concurrencia optimista (`OptimisticLockingFailureException`).
- **NUNCA reintentar (`ignoreExceptions`):** Errores de negocio o de cliente (`400 Bad Request`, `401 Unauthorized`, `403 Forbidden`, `404 Not Found`, `422 Unprocessable Entity`, `SaldoInsuficienteException`). Reintentar un `400` dará `400` mil veces seguidas.

> **Peligro en Pagos:** Solo activa reintentos automáticos en operaciones que sean **100% Idempotentes** (que envíen `Idempotency-Key` o sean lecturas `GET`).

### Ejemplo en Código con Resilience4j (`@Retry` + `fallbackMethod`):
```java
@Service
public class ConsultaRiesgoCrediticioService {

    private static final Logger log = LoggerFactory.getLogger(ConsultaRiesgoCrediticioService.class);
    private final BureauCreditoHttpClient bureauClient;

    public ConsultaRiesgoCrediticioService(BureauCreditoHttpClient bureauClient) {
        this.bureauClient = bureauClient;
    }

    // Si falla por un microcorte de red, reintenta hasta 3 veces con espera exponencial + Jitter.
    // Si los 3 intentos fallan, salta automáticamente al método fallback.
    @Retry(name = "bureauCreditoApi", fallbackMethod = "evaluacionPorDefectoEnContingencia")
    public ScoreCrediticioDto consultarScore(String cuit) {
        log.info("Consultando bureau crediticio externo para CUIT {}", cuit);
        return bureauClient.obtenerScorePorCuit(cuit);
    }

    // El método fallback SIEMPRE debe tener exactamente los mismos parámetros que el original + el Throwable al final
    public ScoreCrediticioDto evaluacionPorDefectoEnContingencia(String cuit, Throwable causaFallo) {
        log.warn("Agotados los reintentos hacia Bureau para CUIT {}. Causa: {}. Aplicando política conservadora.",
                cuit, causaFallo.getMessage());
        return new ScoreCrediticioDto(cuit, 500, "EVALUACION_CONTINGENCIA_LIMITE_REDUCIDO");
    }
}
```

---

## 6. Escudo 3: Circuit Breaker / Disyuntor (`CLOSED`, `OPEN`, `HALF_OPEN`)

Funciona igual que la llave térmica de electricidad de una casa: cuando detecta un cortocircuito continuo en un proveedor externo, **salta la térmica** para que no se incendie tu propio servidor.

### La Máquina de 3 Estados del Circuit Breaker:
```text
   (Tasa de fallos >= 50%)                 (Pasan 30s de enfriamiento)
[CLOSED] ──────────────────────> [OPEN] ───────────────────────────────> [HALF_OPEN]
   ▲     (Normal: pasan todas)   (Cortado: rechaza en 0ms sin llamar)        │
   │                                ▲                                        │ (Deja pasar 5 llamadas de prueba)
   │                                └─────── (Si vuelven a fallar) ──────────┤
   └──────────────────────────────────────── (Si las 5 salen bien) <─────────┘
```

| Estado del Circuito | Qué significa | Qué hace tu servidor con cada petición |
|---|---|---|
| **`CLOSED` (Cerrado — Normal)** | El proveedor externo funciona bien. | Las llamadas pasan normalmente hacia el proveedor y una ventana deslizante (*Ring Bit Buffer*) registra si salen bien o mal. |
| **`OPEN` (Abierto — Cortado)** | La tasa de errores (`failureRateThreshold`, ej. `50%`) o de llamadas lentas (`slowCallRateThreshold`) superó el límite. | **No hace ninguna llamada de red.** Lanza `CallNotPermittedException` en `0.01 ms` y ejecuta directamente tu `fallbackMethod`, ahorrando hilos y dándole respiro al proveedor caído. |
| **`HALF_OPEN` (Semi-Abierto — Prueba)** | Ya pasó el tiempo de espera (`waitDurationInOpenState`, ej. `30 segundos`). | Deja pasar unas pocas llamadas de prueba (ej. `5 llamadas`). Si tienen éxito, vuelve a **`CLOSED`**; si fallan, vuelve a **`OPEN`** por otros 30 segundos. |

**Ejemplo de `@CircuitBreaker` en Spring Boot:**
```java
@Service
public class PasarelaPagosResilienteService {

    private final StripeAdapter stripeAdapter;
    private final MercadoPagoAdapter mercadoPagoAdapter;

    public PasarelaPagosResilienteService(StripeAdapter stripeAdapter, MercadoPagoAdapter mercadoPagoAdapter) {
        this.stripeAdapter = stripeAdapter;
        this.mercadoPagoAdapter = mercadoPagoAdapter;
    }

    @CircuitBreaker(name = "pasarelaPrimaria", fallbackMethod = "cobrarPorPasarelaSecundaria")
    public ReciboPago cobrar(OrdenCobro orden) {
        return stripeAdapter.ejecutarCobro(orden);
    }

    // Si Stripe está caído (CircuitBreaker OPEN) o da Timeout, desvía el cobro en el acto a Mercado Pago:
    public ReciboPago cobrarPorPasarelaSecundaria(OrdenCobro orden, Throwable ex) {
        return mercadoPagoAdapter.ejecutarCobro(orden);
    }
}
```

---

## 7. Escudo 4: Bulkhead / Mamparo de Aislamiento (Semáforos vs Pool de Hilos)

El nombre viene de los **mamparos estancos de los barcos** (como el Titanic): se divide el casco del barco en compartimentos separados para que, si entra agua en un compartimento, se inunde solo ese sector y el barco entero no se hunda.

En programación, si tu aplicación llama a 3 servicios externos (Pagos, Correo y Generador de PDFs), usas **`@Bulkhead`** para que el Generador de PDFs nunca pueda usar más de `10 hilos simultáneos`. Si se satura el PDF, los otros `190 hilos` del servidor siguen libres para procesar Pagos.

| Tipo de Bulkhead en Resilience4j | Cómo funciona internamente | Cuándo usarlo |
|---|---|---|
| **1. `SEMAPHORE` (Por defecto)** | Usa un `java.util.concurrent.Semaphore` en el mismo hilo actual. Limita la cantidad de ejecuciones concurrentes (`maxConcurrentCalls`). | Ideal para el 90% de los casos síncronos y con **Virtual Threads de Java 21**. |
| **2. `THREADPOOL` (Pool de Hilos Fijo)** | Ejecuta la llamada en un pool de hilos separado con una cola de espera (`maxThreadPoolSize` + `queueCapacity`). | Cuando necesitas aislar físicamente los hilos o combinarlo con `@TimeLimiter` asíncrono (`CompletableFuture`). |

```java
@Service
public class ExportadorReportesPesadosService {

    // Limita a un máximo de 10 exportaciones de PDF al mismo tiempo en todo el servidor.
    // Si llega la petición #11 simultánea, espera hasta 500ms y si no se libera lugar, va al fallback.
    @Bulkhead(name = "generadorPdfPesado", type = Bulkhead.Type.SEMAPHORE, fallbackMethod = "encolarReporteParaEnvioPorEmail")
    public RespuestaReporteDto generarPdfEnVivo(Long empresaId) {
        // Proceso pesado que consume mucha CPU y RAM...
        return new RespuestaReporteDto("LISTO", "https://...");
    }

    public RespuestaReporteDto encolarReporteParaEnvioPorEmail(Long empresaId, Throwable ex) {
        // Degradación elegante ante pico de carga: en vez de colgar el servidor, lo pasamos a cola asíncrona
        return new RespuestaReporteDto("ENCOLADO", "Alta demanda: te enviaremos el PDF por email en 2 minutos.");
    }
}
```

---

## 8. Escudo 5: Rate Limiter y *Load Shedding* (Control de Tráfico y Rechazo Temprano)

| Concepto | Para qué sirve | Ejemplo en Spring Boot |
|---|---|---|
| **Rate Limiter Saliente (Hacia terceros)** | La API de un banco o de AFIP tiene una regla: *"Máximo 20 llamadas por segundo; si mandas 21 te bloqueamos la IP por 1 hora"*. Usas `@RateLimiter` en tu cliente HTTP para no superar jamás esa velocidad. | `@RateLimiter(name = "apiBancoLimite")` |
| **Rate Limiter Entrante (Protección de tu API)** | Evitar ataques de fuerza bruta en `/login`, *scraping* de precios o clientes con un `while(true)` mal programado. | Filtro con **Bucket4j** o Redis devolviendo `HTTP 429 Too Many Requests` + header `Retry-After: 60`. |
| **Load Shedding (Desprendimiento de Carga)** | Cuando tu servidor llega al 90% de CPU en un Black Friday, descarta temporalmente las peticiones no esenciales (ej. autocompletado, historial de vistos) para garantizar el 100% de recursos al botón de **"Pagar"**. | Priorizar tráfico crítico sobre tráfico secundario. |

---

## 9. Escudo 6: Fallback y Degradación Elegante (*Graceful Degradation*)

Cuando todos los escudos anteriores detectan que un servicio no está disponible, el **`fallbackMethod`** define cómo se degrada el sistema sin mostrar un error `500` en la cara del usuario.

### Las 4 Estrategias de Fallback más usadas en Empresas:
1. **Fallback a Caché Local (*Stale Data*):** Si se cayó el servicio de precios o cotización de moneda, devolver la última cotización guardada en caché de hace 5 minutos.
2. **Fallback a Proveedor Secundario (*Failover*):** Si falló el envío de SMS por Twilio, enviarlo por AWS SNS o WhatsApp.
3. **Fallback por Encolamiento (*Store and Forward*):** Si estás registrando un pedido o factura y el sistema del ERP o AFIP está caído, guardar el pedido como `PENDIENTE_SINCRONIZACION` en base de datos local y procesarlo en segundo plano cuando vuelva el servicio.
4. **Fallback Silencioso / Valor por Defecto:** Si se cayó el microservicio de *"Productos recomendados para ti"*, devolver una lista vacía `List.of()` para que la página cargue rápido sin esa sección.

---

## 10. El Orden Exacto de Ejecución al Combinar los Patrones en `Resilience4j`

¿Qué pasa cuando en un mismo método de Spring Boot pones `@Retry`, `@CircuitBreaker`, `@RateLimiter` y `@Bulkhead` todos juntos?
Resilience4j los envuelve como capas de una cebolla en este **orden predeterminado de afuera hacia adentro**:

```text
[Tu Llamador]
   └──> 1. @Retry (El más externo: solo reintenta si todo lo de adentro falló)
          └──> 2. @CircuitBreaker (Si el circuito está ABIERTO, falla al instante antes de tocar el Bulkhead)
                 └──> 3. @RateLimiter (Verifica que no superemos el límite por segundo)
                        └──> 4. @TimeLimiter (Controla el tiempo máximo de ejecución)
                               └──> 5. @Bulkhead (El más interno: reserva el hilo/semáforo justo antes de llamar)
                                      └──> [TU MÉTODO REAL QUE LLAMA A LA API EXTERNA]
```

> **¿Por qué `@Retry` va por AFUERA de `@CircuitBreaker`?**
> Porque si en el intento #1 el Circuit Breaker detecta que se alcanzó el 50% de fallos y pasa a estado **`OPEN`**, cuando el `@Retry` intente hacer el intento #2 un segundo después, el Circuit Breaker lo frenará en seco sin golpear de nuevo al servidor caído.

---

## 11. Configuración Completa de `Resilience4j` en `application.yml` (Lista para Producción)

Copia esta plantilla en tu `application.yml` cuando integres `spring-cloud-starter-circuitbreaker-resilience4j` y `spring-boot-starter-aop`:

```yaml
resilience4j:
  # 1. Configuración del Circuit Breaker
  circuitbreaker:
    instances:
      pasarelaPrimaria:
        registerHealthIndicator: true
        slidingWindowType: COUNT_BASED
        slidingWindowSize: 20                 # Evalúa las últimas 20 llamadas
        minimumNumberOfCalls: 10              # No abre el circuito hasta tener al menos 10 llamadas de muestra
        failureRateThreshold: 50              # Si el 50% o más fallan -> Pasa a estado OPEN
        slowCallRateThreshold: 80             # Si el 80% son demasiado lentas -> También pasa a OPEN
        slowCallDurationThreshold: 3s         # Se considera "llamada lenta" si tarda más de 3 segundos
        waitDurationInOpenState: 30s          # Tiempo que permanece OPEN antes de probar pasar a HALF_OPEN
        permittedNumberOfCallsInHalfOpenState: 5 # Llamadas de prueba en HALF_OPEN
        recordExceptions:                     # Solo estas excepciones técnicas suman como fallo del proveedor:
          - java.io.IOException
          - java.net.SocketTimeoutException
          - org.springframework.web.client.HttpServerErrorException
        ignoreExceptions:                     # Errores de negocio (ej. tarjeta sin saldo) NO abren el circuito:
          - com.miempresa.pagos.exception.FondosInsuficientesException
          - com.miempresa.pagos.exception.ReglaNegocioException

  # 2. Configuración de Reintentos (Retry con Exponential Backoff + Jitter Aleatorio)
  retry:
    instances:
      bureauCreditoApi:
        maxAttempts: 3                        # 1 intento original + 2 reintentos
        waitDuration: 500ms                   # Espera base inicial
        enableExponentialBackoff: true        # Duplica la espera en cada intento (500ms -> 1s -> 2s)
        exponentialBackoffMultiplier: 2
        enableRandomizedWait: true            # Activa JITTER aleatorio para evitar estampidas (Thundering Herd)
        randomizedWaitFactor: 0.5
        retryExceptions:
          - java.net.SocketTimeoutException
          - org.springframework.web.client.ResourceAccessException

  # 3. Configuración de Aislamiento (Bulkhead)
  bulkhead:
    instances:
      generadorPdfPesado:
        maxConcurrentCalls: 10                # Máximo 10 ejecuciones simultáneas
        maxWaitDuration: 500ms                # Cuánto espera el #11 antes de saltar al fallback

  # 4. Configuración de Limitador de Velocidad (Rate Limiter)
  ratelimiter:
    instances:
      apiBancoLimite:
        limitForPeriod: 20                    # Máximo 20 llamadas...
        limitRefreshPeriod: 1s                # ...por cada 1 segundo
        timeoutDuration: 200ms                # Tiempo máximo esperando turno
```

---

## 12. Resiliencia en Bases de Datos (Reintentos de `@Version`, HikariCP y *Failover*)

La resiliencia no es solo para APIs externas; la base de datos también sufre choques temporales de concurrencia y microcortes.

### 1. Reintentar Choques de Bloqueo Optimista (`@Version`) con `Spring Retry`
Cuando dos hilos intentan actualizar el mismo producto o saldo con `@Version` al mismo tiempo, el primero gana y el segundo recibe `ObjectOptimisticLockingFailureException`.
En lugar de devolverle error al usuario, usamos **`@Retryable`** de `spring-retry` para que el segundo hilo relea el saldo actualizado y aplique su operación `50 ms` después:

```java
// Requiere @EnableRetry en una clase @Configuration y la dependencia 'spring-retry'
@Service
public class StockResilienteService {

    private final ProductoRepository productoRepository;

    public StockResilienteService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    // Si otro hilo modificó la fila justo antes que nosotros (fallo de @Version) o hubo un Deadlock temporal,
    // Spring hace rollback, espera 50ms y vuelve a ejecutar la transacción desde cero con los datos frescos:
    @Retryable(
        retryFor = {
            ObjectOptimisticLockingFailureException.class,
            CannotAcquireLockException.class
        },
        maxAttempts = 3,
        backoff = @Backoff(delay = 50, multiplier = 2, random = true)
    )
    @Transactional
    public void descontarStockConBloqueoOptimista(Long productoId, int cantidad) {
        ProductoEntity producto = productoRepository.findById(productoId).orElseThrow();
        producto.descontarStock(cantidad);
        productoRepository.save(producto); // Al hacer flush verifica automáticamente la columna @Version
    }

    // Si después de los 3 intentos sigue fallando por altísima concurrencia, ejecuta @Recover:
    @Recover
    public void recuperarFalloConcurrencia(Exception ex, Long productoId, int cantidad) {
        throw new OperacionOcupadaException("El producto está recibiendo demasiadas compras simultáneas, intente nuevamente.");
    }
}
```

### 2. Tuning Resiliente del Pool de Conexiones (`HikariCP`)
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20           # Un pool chico (10-20) rinde más y evita saturar la CPU de PostgreSQL
      connection-timeout: 3000        # Fail-Fast: si en 3 seg no hay conexión libre, falla rápido en vez de colgar 30 seg
      max-lifetime: 1800000           # Renueva conexiones cada 30 min para evitar cortes silenciosos del firewall
      keepalive-time: 60000           # Verifica cada 1 min que las conexiones abiertas sigan vivas
```

---

## 13. Resiliencia en Caché (*Cache Stampede*, *Cache Penetration* y *Fail-Open*)

Cuando usas **Redis** o una caché en memoria frente a tu base de datos, pueden ocurrir **3 fenómenos peligrosos** que debes saber prevenir:

| Fenómeno Peligroso | Qué es | Cómo Prevenirlo Algorítmicamente |
|---|---|---|
| **1. *Cache Stampede* (Avalancha / Estampida de Caché)** | Una clave muy popular (ej. la portada del diario o el precio del dólar) vence su `TTL` a las `12:00:00`. En ese milisegundo entran 5.000 usuarios, ven que no está en caché, y **los 5.000 hacen la consulta pesada a la Base de Datos al mismo tiempo**, tumbándola. | - Usar bloqueo por clave (`sync = true` en `@Cacheable`).<br>- Agregar **Jitter aleatorio al TTL** (que en vez de vencer todas a los 60 min exactos, venzan entre 55 y 65 min). |
| **2. *Cache Penetration* (Penetración por IDs Inexistentes)** | Un atacante pide `GET /productos/9999999` (un ID que no existe ni en caché ni en la BD). Como no existe, nunca se guarda en caché y golpea siempre a la BD. | Guardar en caché un marcador de "No existe" con TTL corto (ej. 1 minuto) o usar un **Filtro de Bloom (*Bloom Filter*)** previo. |
| **3. Caída del Servidor Redis (*Cache Outage*)** | Si se cae Redis y tu aplicación lanza excepción y deja de funcionar, **Redis se convirtió en un Punto Único de Fallo (SPOF)**. | Implementar **`CacheErrorHandler` en modo *Fail-Open***: si Redis da error de conexión, loguear un warning y seguir leyendo directo de la base de datos como si simplemente no estuviera en caché. |

**Ejemplo de `CacheErrorHandler` Resiliente (*Fail-Open*: Si se cae Redis, la app sigue funcionando con la BD):**
```java
@Configuration
@EnableCaching
public class CacheResilienteConfig implements CachingConfigurer {

    private static final Logger log = LoggerFactory.getLogger(CacheResilienteConfig.class);

    // 1. En el método de negocio usamos sync = true para evitar Cache Stampede (solo 1 hilo va a la BD, los demás esperan):
    // @Cacheable(value = "productos", key = "#id", sync = true)

    // 2. Si el servidor de Redis se cae, atrapamos el error y dejamos que Spring consulte a PostgreSQL sin romper la petición:
    @Override
    public CacheErrorHandler errorHandler() {
        return new SimpleCacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException ex, Cache cache, Object key) {
                log.warn("Redis no disponible al leer clave '{}' en caché '{}'. Haciendo Fail-Open hacia la BD: {}",
                        key, cache.getName(), ex.getMessage());
            }

            @Override
            public void handleCachePutError(RuntimeException ex, Cache cache, Object key, Object value) {
                log.warn("Redis no disponible al guardar clave '{}': {}", key, ex.getMessage());
            }
        };
    }
}
```

---

## 14. Resiliencia en Colas y Mensajería (`DLQ` - Dead Letter Queue, *Poison Pills* y *Backpressure*)

Cuando procesas mensajes asíncronos con **RabbitMQ, Apache Kafka o AWS SQS**, ¿qué pasa si llega un mensaje con un JSON corrupto o un dato que causa un error permanente?
- **El Problema de la "Píldora Venenosa" (*Poison Pill*):** Si el consumidor falla al procesar el mensaje, lo devuelve a la cola, lo vuelve a leer un milisegundo después, vuelve a fallar, y entra en un **bucle infinito de reintentos a máxima velocidad** que frena toda la cola y llena el disco de logs.

### La Solución Estándar: Reintentos Acotados + *Dead Letter Queue (DLQ)*
1. Si falla por un error de negocio permanente (`JsonParseException`, `IllegalArgumentException`), **no se reintenta**: se envía directo a la **Cola de Mensajes Muertos (`DLQ` / *Dead Letter Topic*)**.
2. Si falla por un error de red transitorio (la BD o API externa no respondió), se reintenta hasta **3 veces con *Backoff***.
3. Si tras el 3° intento sigue fallando, se mueve el mensaje a la **`DLQ`**, se dispara una alerta a soporte y el consumidor sigue procesando los demás mensajes sanos de la fila.

```java
// Ejemplo en Spring Boot con Kafka (@RetryableTopic):
// Crea automáticamente tópicos de reintento escalonados (1s, 5s, 25s) y si falla las 3 veces lo manda al tópico DLT (Dead Letter Topic):
@Component
public class ProcesadorEventosPagoConsumer {

    private static final Logger log = LoggerFactory.getLogger(ProcesadorEventosPagoConsumer.class);

    @RetryableTopic(
        attempts = "3",
        backoff = @Backoff(delay = 1000, multiplier = 5.0),
        exclude = { IllegalArgumentException.class, DeserializationException.class } // Las Poison Pills van directo a la DLT sin reintentar
    )
    @KafkaListener(topics = "pagos-confirmados", groupId = "facturacion-group")
    public void consumirEventoPago(String payloadJson) {
        // Procesar facturación...
    }

    // Método que recibe los mensajes que agotaron sus reintentos y cayeron en la Dead Letter Queue (DLQ / DLT):
    @DltHandler
    public void manejarMensajeEnColaMuerta(String payloadJson,
                                           @Header(KafkaHeaders.RECEIVED_TOPIC) String topico,
                                           @Header(KafkaHeaders.EXCEPTION_MESSAGE) String motivoError) {
        log.error("ALERTA DLQ: Mensaje movido a cola muerta desde {} por error: {}. Payload guardado para reproceso: {}",
                topico, motivoError, payloadJson);
        // Guardar en tabla 'mensajes_fallidos_dlq' para poder reprocesarlo con 1 clic cuando se arregle el problema
    }
}
```

---

## 15. Apagado Elegante (*Graceful Shutdown*), *Health Probes* e Ingeniería del Caos

### 1. *Graceful Shutdown* (No matar transacciones al desplegar una nueva versión)
Cuando subes una versión nueva a producción y el servidor viejo recibe la orden de apagarse (`SIGTERM`):
- Sin *Graceful Shutdown*: Se apaga en `0 ms` cortando por la mitad los cobros que estaban ejecutándose.
- Con *Graceful Shutdown*: Deja de aceptar peticiones HTTP nuevas, pero **espera hasta 30 segundos a que terminen los hilos que ya estaban procesando un pago o transacción** antes de cerrar el proceso.

### 2. Separación de *Liveness Probe* vs *Readiness Probe* en Actuator
En Kubernetes o balanceadores de carga existen dos chequeos de salud muy distintos:
- **`/actuator/health/liveness` (¿Está vivo el proceso Java?):** Solo verifica que la JVM no esté colgada (*Deadlock*). **Jamás pongas el chequeo de una API externa o base de datos dentro de `liveness`**, porque si se cae un proveedor externo por 10 segundos, Kubernetes creerá que tu aplicación se colgó y **reiniciará todos tus contenedores en bucle**, empeorando todo.
- **`/actuator/health/readiness` (¿Está listo para recibir tráfico de usuarios?):** Verifica que el pool de conexiones y las dependencias locales estén listas.

```yaml
server:
  shutdown: graceful

spring:
  lifecycle:
    timeout-per-shutdown-phase: 30s

management:
  endpoint:
    health:
      probes:
        enabled: true # Habilita /actuator/health/liveness y /actuator/health/readiness por separado
      show-details: always
  health:
    circuitbreakers:
      enabled: true
```

### 3. Ingeniería del Caos (*Chaos Engineering*)
¿Cómo sabes que tu `@CircuitBreaker`, tus *Timeouts* y tus *Fallbacks* realmente funcionan antes de que ocurra una tragedia un viernes a la noche en producción?
- **En Tests de Integración:** Usas **WireMock** (simulando respuestas con `withFixedDelay(5000)` o errores `503`) o **Toxiproxy** con Testcontainers (cortando o degradando la conexión TCP hacia PostgreSQL/Redis en pleno test) para comprobar que tu aplicación pasa al *Fallback* y se recupera sola.

---

## 16. Checklist Rápido de Resiliencia Antes de Salir a Producción

Usa esta tabla de control para auditar cualquier microservicio o integración en Java y Spring Boot:

| # | Pregunta de Auditoría de Resiliencia | Solución Estándar |
|---|---|---|
| **1** | **¿Todos los clientes HTTP (`RestClient`, `WebClient`, `Feign`) tienen `Connect Timeout` y `Read Timeout` configurados?** | Sí: `Connect Timeout` de `1-2s` y `Read Timeout` de `3-5s`. Nunca dejar el valor infinito por defecto. |
| **2** | **¿Qué pasa si un proveedor externo empieza a devolver `503` o a tardar 30 segundos?** | Se activa el **`@CircuitBreaker`**, abre el circuito al superar el 50% de fallos y ejecuta el **`fallbackMethod`** en `0 ms`. |
| **3** | **¿Los reintentos (`@Retry`) tienen *Exponential Backoff* con *Jitter* aleatorio?** | Sí, para no provocar una estampida (*Thundering Herd*) sobre el servidor que intenta recuperarse. |
| **4** | **¿Estamos seguros de NO estar reintentando errores `4xx` ni operaciones no idempotentes?** | `ignoreExceptions` excluye errores de negocio/validación, y todo cobro reintentado lleva su `Idempotency-Key`. |
| **5** | **¿Un proveedor secundario lento puede agotar todos los hilos de nuestro Tomcat?** | No, porque está aislado dentro de un **`@Bulkhead`** con límite máximo de llamadas concurrentes. |
| **6** | **¿Qué pasa si se cae Redis (nuestro servidor de caché)?** | Tenemos configurado `CacheErrorHandler` en modo ***Fail-Open*** para que la app siga funcionando leyendo de la BD. |
| **7** | **¿Qué pasa si entra un mensaje corrupto (*Poison Pill*) por Kafka o RabbitMQ?** | Tras 3 intentos o error de formato, va a la **Cola de Mensajes Muertos (`DLQ`)** sin trabar la fila principal. |
| **8** | **¿Qué pasa cuando desplegamos una versión nueva mientras hay usuarios comprando?** | `server.shutdown: graceful` espera a que terminen las transacciones en vuelo antes de apagar el contenedor. |

> **Tip de lectura:** Cuando revises un proyecto empresarial para evaluar su resiliencia, busca primero dónde se configuran los **`RestClient` / `WebClient` / `@FeignClient`** y los **`@KafkaListener` / `@RabbitListener`**. Si una llamada HTTP externa no tiene *Timeout* ni *Circuit Breaker*, o si un consumidor de cola no tiene configurada una *Dead Letter Queue (`DLQ`)*, acabas de encontrar un punto crítico que puede tumbar el sistema en producción.

