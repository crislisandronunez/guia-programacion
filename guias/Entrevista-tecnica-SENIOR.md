# Guía Maestra: Entrevista Técnica Senior (Java, Spring Boot, Arquitectura y System Design)

En una entrevista para un puesto **Mid/Junior**, el entrevistador evalúa *si sabes cómo funciona una herramienta* ("¿Qué hace `@Transactional`?").
En una entrevista **Senior / Staff**, el entrevistador evalúa **cómo piensas bajo presión, qué pasa cuando las herramientas fallan a escala, qué trade-offs (sacrificios) eliges y cómo explicas conceptos complejos con claridad**.

Este documento es tu manual de entrenamiento completo: incluye **el orden cronológico real de una entrevista**, **las herramientas mentales (frameworks) para estructurar tus respuestas**, **las analogías exactas que hacen brillar tu explicación** y **las preguntas más frecuentes y tramposas respondidas al nivel Senior**.

---

## Índice

1. [El Orden Real de una Entrevista Técnica Senior (Hoja de Ruta)](#1-el-orden-real-de-una-entrevista-técnica-senior-hoja-de-ruta)
2. [Herramientas para Responder como un Senior (Frameworks Mentales)](#2-herramientas-para-responder-como-un-senior-frameworks-mentales)
3. [Fase 1: El Pitch Inicial ("Háblame de ti y de tu proyecto más complejo")](#3-fase-1-el-pitch-inicial-háblame-de-ti-y-de-tu-proyecto-más-complejo)
4. [Fase 2: Java Profundo, Memoria JVM y Concurrencia](#4-fase-2-java-profundo-memoria-jvm-y-concurrencia)
5. [Fase 3: Spring Boot, Proxies, Transacciones y JPA/Hibernate](#5-fase-3-spring-boot-proxies-transacciones-y-jpahibernate)
6. [Fase 4: Bases de Datos SQL, Índices, Locks y Rendimiento](#6-fase-4-bases-de-datos-sql-índices-locks-y-rendimiento)
7. [Fase 5: Arquitectura Limpia/Hexagonal, SOLID y Patrones de Diseño](#7-fase-5-arquitectura-limpiahexagonal-solid-y-patrones-de-diseño)
8. [Fase 6: Microservicios, Kafka, Redis, Idempotencia y Resiliencia](#8-fase-6-microservicios-kafka-redis-idempotencia-y-resiliencia)
9. [Fase 7: Cómo Abordar el System Design en Vivo (Guion de 45 Minutos)](#9-fase-7-cómo-abordar-el-system-design-en-vivo-guion-de-45-minutos)
10. [Fase 8: Diagnóstico en Producción ("La API está lenta a las 3 AM")](#10-fase-8-diagnóstico-en-producción-la-api-está-lenta-a-las-3-am)
11. [Diccionario Rápido: Las 20 Analogías de Oro para Entrevistas](#11-diccionario-rápido-las-20-analogías-de-oro-para-entrevistas)
12. [Preguntas Inteligentes para Hacerle al Entrevistador al Final](#12-preguntas-inteligentes-para-hacerle-al-entrevistador-al-final)

---

## 1. El Orden Real de una Entrevista Técnica Senior (Hoja de Ruta)

Las entrevistas técnicas Senior no son aleatorias; siguen una **escalera de complejidad creciente** para encontrar tu "techo técnico". Saber en qué escalón estás te quita la ansiedad y te permite anticipar la siguiente pregunta:

```text
====================================================================================================
                  CRONOGRAMA REAL DE UNA ENTREVISTA TÉCNICA SENIOR (60 - 90 MIN)
====================================================================================================

 [Min 00 - 08] ──► ETAPA 1: Calibración de Nivel y Pitch de Impacto
                   • "Cuéntame sobre tu experiencia y el desafío técnico más complejo que resolviste."
                   • Objetivo del entrevistador: Detectar si fuiste dueño de decisiones o solo ejecutaste tickets.

 [Min 08 - 20] ──► ETAPA 2: Fundamentos Profundos (Java / JVM / Concurrencia / Algoritmos)
                   • HashMap interno, Virtual Threads, Garbage Collector, CompletableFuture, Big-O.
                   • Objetivo: Verificar que entiendes qué ocurre debajo del capó del lenguaje.

 [Min 20 - 35] ──► ETAPA 3: Dominio del Framework y Persistencia (Spring Boot + JPA + SQL)
                   • Proxies de @Transactional, N+1, HikariCP, Índices Compuestos, Locks Pesimistas/Optimistas.
                   • Objetivo: Confirmar que tu código no tirará abajo la base de datos en producción.

 [Min 35 - 50] ──► ETAPA 4: Arquitectura, Sistemas Distribuidos y Resiliencia
                   • Hexagonal, Idempotencia, Transactional Outbox, Sagas, Kafka, Redis, Circuit Breaker.
                   • Objetivo: Evaluar cómo diseñas sistemas desacoplados y tolerantes a fallos.

 [Min 50 - 75] ──► ETAPA 5: System Design o Escenario de Producción en Vivo ("What-If")
                   • "Diseña una billetera digital / procesador de pagos" o "La API subió su latencia a 10s".
                   • Objetivo: Ver cómo haces preguntas de clarificación, dibujas planos y defiendes trade-offs.

 [Min 75 - 85] ──► ETAPA 6: Liderazgo Técnico, Conflictos y Cultura (Behavioral)
                   • "¿Cómo manejas deuda técnica?", "Cuéntame de un incidente grave en producción".

 [Min 85 - 90] ──► ETAPA 7: Tus Preguntas hacia Ellos
                   • El cierre que define si te perciben como un par Senior.
====================================================================================================
```

---

## 2. Herramientas para Responder como un Senior (Frameworks Mentales)

Un candidato Junior responde con una definición de Wikipedia de 10 segundos y se queda callado, o divaga durante 5 minutos sin ir al grano.
Un candidato Senior usa **estructuras mentales repetibles**. Aquí tienes las **3 herramientas** que debes aplicar en toda la entrevista:

### Herramienta #1: La Fórmula `C-A-T-E` (Para cualquier pregunta teórica o técnica)

Cada vez que te pregunten *"¿Qué es X?"* o *"¿Cómo funciona Y?"*, responde en **4 pasos de 15 a 20 segundos cada uno** (Total: ~1 minuto):

1. **`C` — Concepto Directo (1 oración)**: Define qué es y qué problema resuelve sin rodeos.
2. **`A` — Analogía o Mecánica Interna**: Explica cómo funciona por dentro con una analogía clara y precisa.
3. **`T` — Trade-off (El toque Senior)**: Di **cuándo SÍ usarlo, cuándo NO usarlo y qué costo oculto tiene**. *(Hablar de trade-offs sin que te lo pidan suma el doble de puntos).*
4. **`E` — Ejemplo Real en Producción**: Cierra con una frase: *"Por ejemplo, en sistemas de pagos lo aplicamos así..."*.

> **Ejemplo aplicando `C-A-T-E` a: "¿Qué es el bloqueo optimista (`@Version`)?"**
> - **(C) Concepto**: Es un mecanismo de control de concurrencia que evita que dos transacciones pisen los cambios de la otra sin bloquear físicamente la fila en la base de datos.
> - **(A) Analogía / Mecánica**: Funciona como editar un documento compartido con número de revisión: si leo la fila con `version = 5`, al hacer `UPDATE` Hibernate agrega `WHERE id = 1 AND version = 5` e incrementa la versión a `6`. Si otro hilo se adelantó y ya la subió a `6`, el `UPDATE` afecta `0` filas y lanza `ObjectOptimisticLockingFailureException`.
> - **(T) Trade-off**: Es excelente cuando las colisiones son raras (baja contención) porque no retiene locks en la base de datos. Pero es **pésimo en alta contención** (como descontar el saldo de una cuenta recaudadora que recibe 500 pagos por segundo), porque el 99% de los hilos fallarán y gastarán CPU reintentando; ahí conviene bloqueo pesimista (`SELECT FOR UPDATE`).
> - **(E) Ejemplo**: Lo uso en actualizaciones de perfil o configuraciones de comercio combinándolo con `@Retryable` de Spring para reintentar 2 o 3 veces de forma transparente.

---

### Herramienta #2: El Método `R-E-D-S` (Para preguntas abiertas de Diseño de Sistemas)

Nunca empieces a dibujar cajas ni a hablar de Kafka en el segundo 1. Sigue siempre estos 4 pasos:
1. **`R` — Requirements (Requisitos Funcionales y No Funcionales)**: Haz 3 o 4 preguntas antes de diseñar (*"¿Priorizamos consistencia fuerte como en un banco o disponibilidad como en un catálogo? ¿Cuál es la relación de lecturas vs escrituras?"*).
2. **`E` — Estimations (Cálculos rápidos)**: Calcula QPS/TPS aproximados y dónde estará el cuello de botella (Red, CPU o Disco/DB).
3. **`D` — Design de Alto Nivel (El Camino Feliz)**: Dibuja de izquierda a derecha: Cliente -> API Gateway -> Servicio -> Caché/DB -> Broker Asíncrono.
4. **`S` — Scale & Failure Modes (Escala y Casos de Fallo)**: Explica qué pasa si se cae la base de datos, si el usuario hace doble clic (idempotencia) o si un servicio externo da timeout.

---

### Herramienta #3: El Protocolo de Honestidad Senior (Cómo responder cuando NO sabes algo)

En una entrevista Senior **es normal que te lleven al límite hasta encontrar algo que no sabes**. Nunca inventes ni mientas. Usa esta plantilla en 3 pasos:
1. **Reconoce con seguridad**: *"No he utilizado esa característica específica en producción..."*
2. **Conecta con el principio fundamental que sí conoces**: *"...pero por el problema que me describes (consistencia entre dos recursos), sé que bajo el capó debe resolver el bloqueo distribuido o la coordinación de transacciones..."*
3. **Explica cómo lo resolverías hoy**: *"...en mis proyectos actuales, ese mismo escenario lo resolvemos mediante [alternativa probada, ej. Transactional Outbox / Redis Lock] por esta razón."*

---

## 3. Fase 1: El Pitch Inicial ("Háblame de ti y de tu proyecto más complejo")

Los primeros 5 minutos condicionan toda la entrevista. Si das un resumen aburrido de tu CV cronológico, el entrevistador hará preguntas genéricas. Si das un **Pitch de Arquitectura**, tú mismo guías al entrevistador hacia los temas que mejor dominas.

### Plantilla para tu Presentación de 90 Segundos
> *"Soy desarrollador Backend Java / Spring Boot enfocado en diseñar sistemas de alta concurrencia, consistencia transaccional y arquitecturas limpias/hexagonales.*
> *En mi experiencia reciente he trabajado mucho con los desafíos típicos de sistemas críticos (como flujos financieros/billeteras y procesamiento de alto volumen): garantizar **idempotencia** para evitar cobros duplicados, prevenir **deadlocks y race conditions** en bases de datos relacionales usando bloqueos determinísticos y optimistas, desacoplar procesos lentos con **Kafka y el patrón Outbox**, y asegurar resiliencia con **Resilience4j y Redis**.*
> *Disfruto mucho no solo escribir código limpio y testeable en el dominio, sino entender qué pasa en el pool de conexiones, en los índices de PostgreSQL y en la JVM cuando el tráfico escala."*

*(Nota: Al mencionar palabras clave como **Idempotencia, Deadlocks, Arquitectura Hexagonal, Outbox, Kafka y Pool de Conexiones**, el 90% de los entrevistadores te preguntarán inmediatamente sobre esos mismos temas que tú acabas de poner sobre la mesa).*

---

## 4. Fase 2: Java Profundo, Memoria JVM y Concurrencia

Aquí tienes las preguntas más tomadas sobre Java y la JVM, con su analogía y su respuesta Senior lista.

---

### Pregunta 4.1: ¿Cómo funciona un `HashMap` por dentro, qué pasa si dos claves tienen el mismo `hashCode` y qué cambió desde Java 8?

- **🎭 La Analogía**:
  Imagina un **archivo de casilleros numerados del 0 al 15 en la recepción de un hotel**.
  1. Cuando llega un huésped (`Key`), pasas su apellido por una fórmula matemática (`hashCode()`) que te dice en qué casillero guardarlo (ej. Casillero #4). Ir al casillero #4 es instantáneo: `O(1)`.
  2. **Colisión**: Llega otro huésped distinto cuyo apellido da casualmente el mismo número de casillero (#4). En vez de pisar al anterior, cuelgas una **lista enlazada** dentro del casillero #4 y comparas con `equals()` nombre por nombre.
  3. **Treeification (Java 8+)**: Si por mala suerte (o un ataque DoS) caen más de **8 elementos** en el mismo casillero y el mapa tiene al menos 64 casilleros en total, buscar uno por uno en la lista sería lento (`O(n)`). Entonces Java transforma automáticamente esa lista en un **Árbol Rojo-Negro balanceado (`TreeNode`)**, bajando la búsqueda en el peor caso a **`O(log n)`**.

- **🧠 Respuesta Técnica Senior y Trampa Común**:
  - **Contrato `equals()` y `hashCode()`**: Si dos objetos son iguales según `equals()`, **deben** devolver el mismo `hashCode()`.
  - **Pregunta de repregunta (Follow-up)**: *"¿Por qué las claves de un `HashMap` deben ser inmutables (como `String`, `UUID` o un `record`)?"*
  - **Tu respuesta**: Porque si usas un objeto mutable como clave, lo insertas en el casillero #4, y después le cambias un atributo que participa en el `hashCode()`, cuando hagas `map.get(key)` Java irá a buscarlo al casillero #9. ¡El objeto quedará "perdido" dentro del casillero #4 para siempre causando un leak lógico!

---

### Pregunta 4.2: ¿Qué son los Virtual Threads de Java 21 (Project Loom) y en qué se diferencian de los Hilos Tradicionales y de WebFlux?

- **🎭 La Analogía**:
  - **Hilos de Plataforma (Tradicionales)**: Son como **mozos exclusivos de un restaurante** que, cuando toman tu pedido y van a la cocina, **se quedan parados de brazos cruzados frente al horno durante 15 minutos** esperando a que salga el plato. Si tienes 200 mozos (`max-threads: 200` en Tomcat) y llegan 201 clientes a la vez, el cliente 201 se queda afuera esperando aunque los 200 mozos no estén haciendo fuerza física (CPU), sino solo esperando (I/O bloqueante).
  - **Virtual Threads (Java 21)**: Los 8 hilos reales del procesador (*Carrier Threads*) son los mozos, y los **Virtual Threads** son **comandas ultralivianas (pesan ~1 KB en el Heap en vez de 1 MB en RAM nativa)**. Apenas un Virtual Thread hace una llamada bloqueante a PostgreSQL o a una API HTTP, la JVM **desmonta** esa comanda del hilo físico en microsegundos y el hilo físico atiende a otro cliente. Cuando la base de datos responde, la JVM vuelve a montar el Virtual Thread y sigue en la línea siguiente.

- **🧠 Trade-offs y Cuidados Senior (Lo que te destaca)**:
  1. **¿Para qué sirven?**: Multiplican el *throughput* en aplicaciones **I/O-Bound** (que esperan red, DB, Redis, APIs REST) manteniendo el código imperativo simple y fácil de depurar (a diferencia de la complejidad de `Mono`/`Flux` en WebFlux).
  2. **¿Para qué NO sirven?**: **No hacen que una operación CPU-Bound sea más rápida** (ej. encriptar un archivo o calcular hashes en memoria).
  3. **El peligro del *Thread Pinning* y del Pool de Base de Datos**:
     - En Java 21, si haces una llamada de red bloqueante dentro de un bloque `synchronized`, el Virtual Thread queda **"clavado" (pinned)** al hilo del sistema operativo, perdiendo su ventaja. Por eso en código moderno con Virtual Threads preferimos `ReentrantLock` en lugar de `synchronized`.
     - Además, tener 50.000 Virtual Threads no significa que puedas abrir 50.000 conexiones a PostgreSQL: debes proteger los recursos finitos con **Semáforos / Bulkheads** y un pool **HikariCP** acotado (ej. 20 a 50 conexiones).

---

### Pregunta 4.3: ¿Cómo se organiza la memoria de la JVM y cómo diagnosticas un Memory Leak en producción?

- **🎭 La Analogía**:
  La memoria de la JVM es como el taller de un chef:
  - **Stack (Pila por cada hilo)**: Es la **tabla de picar personal** de cada cocinero. Ahí están las variables locales primitivas y los punteros de los métodos que se están ejecutando ahora mismo. Cuando el método termina, la tabla queda limpia instantáneamente (LIFO).
  - **Heap (Montón compartido por todos los hilos)**: Es la **cámara frigorífica central** donde viven todos los objetos creados con `new`. Se divide en:
    - **Young Generation (Eden + Survivor S0/S1)**: La mesada de uso rápido. El 95% de los objetos (DTOs de un request HTTP, Strings temporales) nacen en **Eden** y mueren milisegundos después cuando termina el request (limpiados por un *Minor GC* ultrarrápido).
    - **Old / Tenured Generation**: El freezer profundo. Los objetos que sobreviven a varios ciclos (ej. Beans `@Singleton` de Spring, cachés en memoria) pasan aquí.
  - **Metaspace (Memoria Nativa fuera del Heap)**: El **libro de recetas** donde se guardan las definiciones de las clases cargadas (`.class` metadata).

- **🧠 Cómo diagnosticar un `OutOfMemoryError` o Memory Leak (Paso a Paso Senior)**:
  1. **Síntoma en métricas (Grafana / Micrometer)**: El gráfico del Heap (`jvm_memory_used_bytes` en Old Gen) tiene forma de **"dientes de sierra ascendentes"**: después de cada Garbage Collection, el piso mínimo de memoria nunca vuelve a bajar hasta llegar al 100% y disparar pausas largas de CPU (*Stop-The-World Full GC*).
  2. **Captura de evidencia**: Configuro el flag `-XX:+HeapDumpOnOutOfMemoryError` en el contenedor o tomo un Heap Dump con `jcmd <PID> GC.heap_dump` antes de que el pod muera.
  3. **Análisis**: Abro el archivo `.hprof` en **Eclipse MAT (Memory Analyzer Tool)** o **VisualVM** y busco el reporte de **Dominator Tree / Leak Suspects**.
  4. **Causas reales más frecuentes en Java/Spring**:
     - Un `Map` o `List` `static` o dentro de un `@Service` singleton donde se agregan elementos sin política de expiración ni tamaño máximo (en vez de usar **Caffeine Cache** con `maximumSize` y `expireAfterWrite`).
     - Un `ThreadLocal` que no se limpió con `.remove()` en un bloque `finally`.
     - Un `findAll()` en JPA que trae 2 millones de registros a memoria de golpe sin paginar ni usar cursores (`Stream`).

---

## 5. Fase 3: Spring Boot, Proxies, Transacciones y JPA/Hibernate

Este es el bloque donde cae el 70% de los candidatos que usan Spring todos los días pero no saben cómo funciona su magia interna.

---

### Pregunta 5.1: ¿Por qué si un método A llama a un método B dentro de la misma clase (`this.metodoB()`), la anotación `@Transactional`, `@Cacheable` o `@Async` del método B es completamente ignorada?

- **🎭 La Analogía**:
  Imagina que un `@Service` de Spring es un **actor famoso (`OrderService`)** y Spring le pone un **representante/guardaespaldas en la puerta de su oficina (`Spring Proxy`)**.
  - Cuando un extraño desde afuera (`OrderController`) quiere hablar con el actor, está obligado a pasar por el **Representante (Proxy)**. El representante abre la transacción de base de datos (`BEGIN`), deja pasar la llamada al actor, y al salir hace `COMMIT` o `ROLLBACK`.
  - Pero si el actor ya está adentro de su oficina ejecutando `metodoA()` y se habla a sí mismo (`this.metodoB()`), **la llamada ocurre puertas adentro sin salir a la recepción**. El Representante (Proxy) nunca se entera de que `metodoB()` fue invocado, por lo que no puede abrir una nueva transacción ni leer `@Async`.

```text
  ✅ LLAMADA EXTERNA (Pasa por el Proxy):
  [Controller] ──► [ 🛡️ Proxy de Spring (Abre TX) ] ──► [ OrderService.metodoA() ]

  ❌ AUTO-INVOCACIÓN / SELF-INVOCATION (Salta el Proxy):
  [Controller] ──► [ 🛡️ Proxy ] ──► [ OrderService.metodoA() ──(this.metodoB())──► metodoB() ]
                                      (¡El Proxy nunca interceptó la llamada a metodoB!)
```

- **🧠 Cómo solucionarlo en código real (3 formas limpias)**:
  1. **La mejor (Diseño SRP)**: Extraer `metodoB()` a otro componente con responsabilidad propia (ej. `AuditLogService` o `TransactionHelper`) e inyectarlo por constructor.
  2. **Usar `TransactionTemplate` programático**: Inyectar `TransactionTemplate` cuando necesitas controlar con precisión quirúrgica qué líneas exactas van dentro de una transacción sin depender de proxies AOP.
  3. **Auto-inyección con `@Lazy`**: Inyectar el propio bean (`@Lazy private final MiServicio self;`) y llamar a `self.metodoB()`.

---

### Pregunta 5.2: ¿Qué pasa exactamente cuando usas `@Transactional` y ocurre una excepción? ¿Qué diferencia hay entre `REQUIRED` y `REQUIRES_NEW`?

- **🧠 Las 3 Reglas de Oro de `@Transactional` que siempre preguntan**:
  1. **Trampa de las Excepciones (`Checked` vs `Unchecked`)**:
     Por defecto, Spring **SOLO hace rollback automático ante `RuntimeException` (Unchecked) y `Error`**. Si tu método lanza una excepción *Checked* (como `IOException` o una excepción de negocio que hereda de `Exception`), **¡Spring hace `COMMIT` de los datos a mitad de camino!**
     - *Solución Senior*: Usar excepciones de dominio que hereden de `RuntimeException` o declarar siempre `@Transactional(rollbackFor = Exception.class)`.
  2. **`Propagation.REQUIRED` (Por defecto) vs `Propagation.REQUIRES_NEW`**:
     - `REQUIRED`: Si ya existe una transacción abierta, se une a la misma conexión física de base de datos. Si el método hijo falla y marca rollback, **toda la transacción padre también muere** (`UnexpectedRollbackException`).
     - `REQUIRES_NEW`: Suspende la transacción actual, pide una **segunda conexión física distinta** al pool HikariCP, ejecuta su propio `BEGIN ... COMMIT` independiente, y luego reanuda la primera.
  3. **El peligro oculto de `REQUIRES_NEW` (Pool Exhaustion Deadlock)**:
     Si tienes un pool HikariCP de 10 conexiones, llegan 10 peticiones simultáneas que abren una transacción padre (ocupando las 10 conexiones) y las 10 intentan entrar a un método con `REQUIRES_NEW`, las 10 se quedarán esperando una segunda conexión que jamás se liberará. **El sistema entra en Deadlock de Pool**. Úsalo con extrema cautela (ej. solo para guardar auditoría de fallos críticos) o usa eventos asíncronos.

---

### Pregunta 5.3: ¿Qué es el problema `N+1` en JPA/Hibernate, por qué ocurre incluso con `FetchType.LAZY` y cómo lo resuelves cuando hay paginación?

- **🎭 La Analogía**:
  Imagina que mandas a un empleado al depósito a buscar **una caja con 50 carpetas de pedidos (`1 consulta SQL`)**. Cuando vuelve a tu escritorio y tú empiezas a abrir carpeta por carpeta en un `for`, descubres que los ítems de cada pedido no están adentro, así que **mandas al empleado a hacer 50 viajes individuales adicionales al depósito (`+50 consultas SQL`)**, uno por cada carpeta. En vez de 1 viaje, hizo 51 viajes por red.

- **🧠 Respuesta Senior Completa (incluyendo la trampa de paginación `HHH90003004`)**:
  - **Causa**: Haces `SELECT o FROM Order o` (devuelve `N` órdenes) y luego al mapear al DTO recorres `order.getItems()`. Como `items` es `LAZY`, Hibernate dispara un `SELECT * FROM order_items WHERE order_id = ?` por cada iteración del bucle.
  - **Soluciones según el caso**:
    1. **Si solo es para lectura (Query Side)**: Usar **Proyecciones directas a DTO (`record`)** o `JdbcClient` trayendo exactamente las columnas necesarias.
    2. **Si es una relación `@ManyToOne` / `@OneToOne` o sin paginación**: Usar `@EntityGraph(attributePaths = {"customer", "items"})` o `JOIN FETCH`.
    3. **🔥 La Trampa Senior (Colección `@OneToMany` + `Pageable`)**:
       Si usas `@EntityGraph` o `JOIN FETCH` sobre una lista (`@OneToMany items`) junto con `Pageable`, el `LEFT JOIN` multiplica las filas en SQL. Hibernate detecta que no puede aplicar `LIMIT 20` en SQL sin cortar los ítems de una orden, lanza el warning **`HHH90003004: firstResult/maxResults specified with collection fetch; applying in memory`** y **se descarga TODA la tabla a la memoria RAM de Java para paginar ahí**, causando `OutOfMemoryError`.
       - **¿Cómo se resuelve profesionalmente?**:
         - **Opción A (Consulta en 2 pasos / Two-Step Pagination)**: Query 1 trae solo la página de IDs (`Page<UUID>` con `LIMIT` en SQL); Query 2 hace `findWithItemsByIdIn(ids)` usando `@EntityGraph` sobre esos 20 IDs exactos.
         - **Opción B (`@BatchSize(size = 50)`)**: Hibernate pagina las 20 órdenes en SQL y luego carga todos los ítems de esas 20 órdenes en **1 sola consulta adicional** usando `WHERE order_id IN (?, ?, ...)`. Pasas de `1 + 20` queries a solo `1 + 1 = 2` queries sin explotar la RAM.

---

## 6. Fase 4: Bases de Datos SQL, Índices, Locks y Rendimiento

---

### Pregunta 6.1: ¿Cómo disenas un Índice Compuesto en SQL y por qué a veces creas un índice pero la base de datos no lo usa (hace `Seq Scan`)?

- **🎭 La Analogía**:
  Un índice B-Tree compuesto `(apellido, nombre, edad)` es exactamente igual a una **guía telefónica impresa ordenada primero por Apellido, luego por Nombre y luego por Edad**:
  - Si buscas `WHERE apellido = 'Gomez' AND nombre = 'Juan'`, vas directo a la página exacta en milisegundos.
  - Pero si buscas solo `WHERE nombre = 'Juan'` (sin dar el apellido), **la guía telefónica no te sirve de nada** porque los "Juan" están desparramados en todos los apellidos de la A a la Z. Tienes que leer el libro entero hoja por hoja (`Sequential Scan`).

- **🧠 La Regla `E-S-R` (Equality, Sort, Range) para diseñar índices perfectos**:
  Cuando tengas una consulta como:
  ```sql
  SELECT * FROM payments
  WHERE merchant_id = ? AND status = 'CAPTURED'
    AND created_at >= '2026-01-01'
  ORDER BY created_at DESC;
  ```
  El orden de las columnas en el `CREATE INDEX` debe ser:
  1. **`E` (Equality)**: Primero las columnas con `=` (`merchant_id, status`).
  2. **`S` (Sort)**: Luego las columnas del `ORDER BY` (`created_at DESC`).
  3. **`R` (Range)**: Por último las columnas con rangos `>`, `<`, `BETWEEN` (`created_at`).
  - **Resultado**: `CREATE INDEX idx_payments_esr ON payments (merchant_id, status, created_at DESC);`

- **¿Por qué PostgreSQL puede ignorar tu índice?**
  1. **No respetaste el prefijo izquierdo** (filtraste por la segunda columna sin filtrar por la primera).
  2. **Aplicaste una función sobre la columna**: `WHERE LOWER(email) = 'a@b.com'` o `WHERE YEAR(created_at) = 2026` invalida el índice normal (necesitas un índice funcional `CREATE INDEX ON users (LOWER(email))`).
  3. **Baja selectividad (Cardinalidad)**: Si la tabla tiene 1 millón de filas y el 90% tiene `status = 'ACTIVE'`, el optimizador de PostgreSQL sabe que es más barato leer las páginas secuenciales del disco (`Seq Scan`) que saltar un millón de veces del árbol B-Tree al Heap.

---

### Pregunta 6.2: En un sistema de transferencias entre billeteras (Cuenta A transfiere a Cuenta B), ¿cómo evitas que el saldo quede inconsistente (Lost Update) y cómo evitas que ocurra un Deadlock?

*(Esta es una de las preguntas favoritas en entrevistas de bancos, fintechs y empresas de producto).*

- **🎭 La Analogía del Deadlock**:
  Dos carpinteros necesitan **el martillo (Cuenta A)** y **el clavo (Cuenta B)** para trabajar.
  - Al mismo milisegundo, el Carpintero 1 agarra el **Martillo (A)** y espera a que se libere el **Clavo (B)**.
  - El Carpintero 2 agarra el **Clavo (B)** y espera a que se libere el **Martillo (A)**.
  - Ninguno suelta lo que tiene: se quedan congelados para siempre hasta que el capataz (PostgreSQL Deadlock Detector) mata a uno de los dos.
  - **La Solución (Orden Determinístico)**: Ponemos una regla en el taller: *"Todo carpintero está obligado a agarrar los objetos por orden alfabético: primero el Clavo y después el Martillo"*. Así, si el Carpintero 1 toma el primero, el Carpintero 2 espera en la puerta desde el paso 1 sin retener nada. **El Deadlock se vuelve matemáticamente imposible**.

- **🧠 Implementación Real en Java + Spring Boot**:
  ```java
  @Transactional
  public void transfer(UUID fromWalletId, UUID toWalletId, BigDecimal amount) {
      if (fromWalletId.equals(toWalletId)) {
          throw new IllegalArgumentException("No puedes transferir a la misma cuenta");
      }

      // 1. ORDENAMIENTO DETERMINÍSTICO DE LOCKS (Previene el Deadlock A->B vs B->A)
      UUID firstLockId = fromWalletId.compareTo(toWalletId) < 0 ? fromWalletId : toWalletId;
      UUID secondLockId = fromWalletId.compareTo(toWalletId) < 0 ? toWalletId : fromWalletId;

      // 2. BLOQUEO PESIMISTA EN ORDEN (SELECT ... FOR UPDATE)
      Wallet first = walletRepository.findByIdForUpdate(firstLockId).orElseThrow();
      Wallet second = walletRepository.findByIdForUpdate(secondLockId).orElseThrow();

      // 3. Identificar origen y destino ya bloqueados en memoria
      Wallet source = fromWalletId.equals(firstLockId) ? first : second;
      Wallet target = toWalletId.equals(firstLockId) ? first : second;

      // 4. Regla de dominio rica + Doble Partida
      source.debit(amount);  // Valida saldo >= amount y resta con BigDecimal
      target.credit(amount); // Suma con BigDecimal
      ledgerRepository.recordDoubleEntry(source.getId(), target.getId(), amount);
  }
  ```

---

## 7. Fase 5: Arquitectura Limpia/Hexagonal, SOLID y Patrones de Diseño

---

### Pregunta 7.1: Explícame la Arquitectura Hexagonal (Ports & Adapters). ¿Qué va en cada capa y dónde pones las interfaces?

- **🎭 La Analogía**:
  Imagina el **motor de un auto de Fórmula 1 (El Dominio y Casos de Uso)**. El motor sabe cómo generar potencia y tiene reglas físicas innegociables, pero **no sabe ni le importa** si el volante lo maneja un piloto humano en una pista o un simulador por computadora en la fábrica, ni si la telemetría se guarda en un disco duro o se transmite por radio.
  Los **Puertos (`Interfaces`)** son los enchufes universales que el motor define, y los **Adaptadores** son los cables intercambiables (REST Controller, Kafka Consumer, PostgreSQL JPA Adapter, Stripe HTTP Adapter) que se enchufan a esos puertos.

- **🧠 Cómo explicar las 3 capas y dónde van los contratos (`interface`)**:
  1. **Capa de Dominio (`domain`)**:
     - Contiene las entidades de negocio ricas (`Order`, `Wallet`), Value Objects (`Money`) y **Políticas de Dominio (`DiscountPolicy`)**.
     - **Regla de Oro**: Es 100% Java puro. No tiene `@Entity`, ni `@Service`, ni `JpaRepository`, ni sabe qué es HTTP o Kafka.
  2. **Capa de Aplicación (`application`)**:
     - Orquesta los **Casos de Uso** (`TransferMoneyService`).
     - Define dos tipos de interfaces (Puertos):
       - **Input Ports (`application.port.in`)**: Qué ofrece el sistema hacia afuera (ej. `TransferMoneyUseCase`). Lo llama el `Controller`.
       - **Output Ports (`application.port.out`)**: Qué necesita el caso de uso del mundo exterior para funcionar (ej. `WalletRepositoryPort`, `FraudCheckPort`). Aquí se aplica **Inversión de Dependencias (DIP de SOLID)**: la aplicación es dueña de la interfaz y la infraestructura obedece.
  3. **Capa de Infraestructura (`infrastructure`)**:
     - Contiene los **Adaptadores de Entrada** (`@RestController`, `@KafkaListener`) y los **Adaptadores de Salida** (`WalletPersistenceAdapter` que usa `SpringDataJpaRepository` y mapea entre `WalletJpaEntity` y el `Wallet` de dominio).

- **⚖️ Trade-off Senior (Cuándo NO usar Hexagonal pura)**:
  *"Si estoy construyendo un microservicio CRUD simple de catálogo o backoffice sin reglas de negocio complejas, hacer mapeo completo entre DTO -> Command -> Domain Model -> JPA Entity es sobreingeniería. En esos casos uso una arquitectura en capas pragmática o Vertical Slice, y reservo Hexagonal estricta para los dominios Core (Pagos, Ledger, Órdenes, Suscripciones) donde las reglas de negocio son críticas y deben testearse en milisegundos sin levantar Spring."*

---

### Pregunta 7.2: ¿Cómo aplicas los principios SOLID en la vida real (sin recitar la teoría de memoria)?

En lugar de recitar qué significa cada letra como un robot, da el **ejemplo real de refactorización con `O` (Open/Closed) + `D` (Dependency Inversion) en Spring Boot**:

- **El Problema Real**: Tienes un servicio que procesa pagos con un `if / else if` gigante para `TARJETA`, `TRANSFERENCIA`, `QR` y `CRIPTO`. Cada vez que agregas un medio de pago nuevo, modificas esa clase de 800 líneas y corres el riesgo de romper los medios existentes (viola **SRP** y **OCP**).
- **La Solución SOLID en Spring Boot (Strategy + Factory Inyectada)**:
  1. Creas la interfaz `PaymentProcessorStrategy` con `PaymentMethod getSupportedMethod()` y `void process(Payment p)`.
  2. Creas una clase `@Component` separada por cada medio (`CardProcessor`, `QrProcessor`, `CryptoProcessor`) — cumple **Single Responsibility (S)**.
  3. En el orquestador, inyectas `List<PaymentProcessorStrategy>` en el constructor y las guardas en un `Map<PaymentMethod, PaymentProcessorStrategy>` (`EnumMap`) para buscarlas en **`O(1)`**.
  4. Mañana, para agregar "Apple Pay", solo creas la clase `ApplePayProcessor implements PaymentProcessorStrategy`. **Cero líneas modificadas en el orquestador** — cumple **Open/Closed (O)**, **Liskov (L)** e **Inversión de Dependencias (D)**.

---

## 8. Fase 6: Microservicios, Kafka, Redis, Idempotencia y Resiliencia

---

### Pregunta 8.1: ¿Qué pasa si guardas un registro en PostgreSQL y justo en la línea siguiente intentas publicar un evento en Kafka (`kafkaTemplate.send()`), pero el broker de Kafka da timeout o tu pod se reinicia? (El Problema del *Dual Write*)

- **🎭 La Analogía**:
  Imagina que firmas la escritura de una casa ante el escribano (**COMMIT en PostgreSQL**) y luego sales caminando al correo para avisarle al banco (**Enviar a Kafka**), pero en la esquina te tropiezas y pierdes la carta. La casa ya cambió de dueño legalmente, pero el resto del sistema jamás se enteró. Hacer un `try/catch` tampoco sirve al revés: si envías primero la carta por correo y al volver el escribano rechaza la escritura, ya notificaste un evento falso que nunca ocurrió.

- **🧠 La Solución Senior: Patrón Transactional Outbox**:
  1. **Nunca escribas en dos infraestructuras distintas (DB + Kafka) por separado en el mismo flujo**.
  2. Dentro de la **misma transacción local ACID (`@Transactional`) de PostgreSQL** donde haces `INSERT` en la tabla `payments`, haces también un `INSERT` en una tabla local llamada `outbox_events` con el JSON del evento y `published = false`.
  3. Como ambas tablas están en la misma base de datos PostgreSQL, o se guardan las dos juntas (`COMMIT`), o no se guarda ninguna (`ROLLBACK`).
  4. Un proceso separado en segundo plano (un **Outbox Relay Worker** con `SELECT ... FOR UPDATE SKIP LOCKED` o **Debezium CDC** leyendo el log WAL de Postgres) toma los registros de `outbox_events`, los publica en Kafka esperando el `ack=all`, y los marca como `published = true`.
  5. **Trade-off que debes mencionar**: El patrón Outbox garantiza entrega **At-Least-Once (Al menos una vez)**. Si el worker publica en Kafka pero se cae antes de poner `published = true`, lo volverá a publicar. Por eso, **todo consumidor de Kafka está obligado a ser Idempotente**.

---

### Pregunta 8.2: ¿Cómo garantizas que un endpoint de cobro o un consumidor de Kafka sea 100% Idempotente?

- **🎭 La Analogía**:
  La idempotencia es como el **botón para llamar al ascensor**: la primera vez que lo presionas, la luz se enciende y el ascensor viene en camino. Si un pasajero ansioso presiona el botón 40 veces más seguidas porque su celular tiene mala señal, **no vienen 40 ascensores ni se rompe el edificio**: el sistema reconoce que la orden ya está en curso y devuelve el mismo estado.

- **🧠 Los 3 Escudos de la Idempotencia en Producción**:
  1. **Identificador Único de Intención (`Idempotency-Key` / `eventId`)**: Generado por el cliente (UUID v4/v7) antes del primer intento, o un hash determinístico `SHA-256(userId + amount + merchantId + timeWindow)` si el cliente no envía clave.
  2. **Escudo Rápido en Redis (Concurrencia en vuelo)**:
     Ejecutamos atómicamente `SET idemp:<key> "IN_PROGRESS" NX EX 120`.
     - Si devuelve `true`, es la primera petición y pasamos a procesar.
     - Si devuelve `false`, hubo un doble clic en el mismo milisegundo: devolvemos `409 Conflict (Request in progress)` o la respuesta JSON final si ya terminó (`COMPLETED`).
  3. **Escudo Definitivo en Base de Datos (Por si Redis se cae o expira)**:
     La tabla `payments` (o `processed_events` en un consumidor Kafka) tiene una restricción física `UNIQUE (idempotency_key)`. Si dos peticiones superan el caché, el motor SQL garantiza que la segunda reciba `DataIntegrityViolationException`, la cual capturamos para devolver el resultado original sin duplicar el cobro.

---

### Pregunta 8.3: En Apache Kafka, ¿cómo garantizas que los eventos de un mismo usuario o pedido se procesen en estricto orden cronológico, y qué haces si un mensaje falla al procesarse?

- **🎭 La Analogía**:
  Un Topic de Kafka con 12 Particiones es como una **autopista de peaje con 12 cabinas (carriles)**.
  - Si un mismo cliente manda 3 autos (`Pedido Creado`, `Pedido Pagado`, `Pedido Enviado`) y cada auto se mete en una cabina distinta (Cabina 1, Cabina 5 y Cabina 9), si la Cabina 1 está lenta, el auto de `Pedido Pagado` cruzará el peaje **antes** que el de `Pedido Creado`.
  - **Solución**: Obligar a que todos los autos del mismo cliente vayan siempre por **la misma cabina de peaje**, usando el **`orderId` (o `walletId`) como Partition Key** al publicar (`kafkaTemplate.send("orders-topic", orderId.toString(), event)`). Kafka aplica `murmur2(key) % numPartitions`, garantizando que todos los eventos de esa orden caigan en la misma partición física y sean leídos secuencialmente por un único hilo consumidor de ese grupo.

- **🧠 Manejo de Errores en Kafka (Poison Pill y Non-Blocking Retries)**:
  - **¿Qué pasa si un mensaje está corrupto (JSON inválido / NullPointerException)?**: Si el consumidor lanza excepción y reintenta infinitamente en el mismo lugar, bloquea toda la partición (generando un *Consumer Lag* gigante). Eso se llama **Poison Pill (Píldora Venenosa)**.
  - **Solución en Spring Kafka**:
    1. Clasificar excepciones: Errores de validación/deserialización van **directo al DLQ (Dead Letter Queue)** sin reintentar.
    2. Errores transitorios (ej. timeout de DB) usan `@RetryableTopic` con **Exponential Backoff** (topics de reintento `-retry-1000`, `-retry-2000`) y, tras agotar 3 o 4 intentos, caen en el método `@DltHandler` (topic `.DLT`), donde guardamos el fallo en base de datos y enviamos una alerta a Slack/PagerDuty para reprocesamiento controlado.

---

### Pregunta 8.4: ¿Cómo proteges tu microservicio cuando una API externa (ej. AFIP, Renaper o un Banco) empieza a tardar 30 segundos en responder?

- **🎭 La Analogía del Circuit Breaker y Bulkhead**:
  - **Sin protección**: Si una tostadora entra en cortocircuito en tu casa y no tienes térmica, se recalientan todos los cables de la pared y se incendia la casa entera (todos los hilos de tu microservicio quedan colgados esperando esos 30 segundos hasta que tu servicio también muere para todos los demás endpoints).
  - **Circuit Breaker (Llave Térmica)**: Cuando detecta que el 50% de las últimas 20 llamadas a esa API fallaron o tardaron más de 2 segundos (**Slow Call Rate**), la llave térmica **salta (`OPEN`)**. Durante los siguientes 30 segundos, ni siquiera intenta llamar por red a la API caída: falla en `0.01 ms` o ejecuta un **Fallback** (ej. encolar para procesar luego o devolver dato cacheado). Pasados 30 segundos, pasa a `HALF_OPEN` y deja pasar 3 llamadas de prueba; si funcionan, vuelve a `CLOSED`.
  - **Bulkhead (Compartimentos Estancos del Titanic)**: Limita cuántos hilos concurrentes pueden llamar a esa API externa (ej. máximo 10 llamadas simultáneas). Si esa API se cuelga, como máximo ocupa 10 permisos, dejando el resto del servidor libre para que el login y las consultas de saldo sigan funcionando al 100%.

---

## 9. Fase 7: Cómo Abordar el System Design en Vivo (Guion de 45 Minutos)

Cuando el entrevistador te comparta pantalla en Excalidraw/Miro y te diga:
> *"Diseña un sistema de pagos / billetera virtual (o un sistema de reservas de entradas / e-commerce) que soporte alta concurrencia"*

Sigue este **guion cronometrado paso a paso** y liderarás la sesión de principio a fin:

### ⏱️ Minuto 00 a 05: Hacer Preguntas de Alcance (¡Nunca dibujes sin preguntar!)
Dile al entrevistador: *"Antes de pasar al diagrama, me gustaría alinear los requisitos funcionales y no funcionales"*:
1. **Funcionales**: *"¿Nos enfocamos en transferencias entre cuentas internas (P2P), cobros con procesador externo (tarjetas), o ambos?"*
2. **Consistencia vs Disponibilidad (CAP)**: *"Al tratarse de dinero/inventario, asumimos que el núcleo contable es estrictamente **CP (Consistencia Fuerte)** donde jamás puede haber saldo negativo ni doble gasto, ¿correcto?"*
3. **Escala**: *"¿Qué volumen manejamos? ¿Estamos hablando de unos 1.000 TPS en promedio con picos de 10.000 TPS en días de quincena o promociones?"*
4. **Latencia**: *"¿Buscamos una respuesta síncrona en `< 300 ms` para transferencias internas y manejo asíncrono con Webhooks para integraciones externas?"*

---

### ⏱️ Minuto 05 a 15: Dibujar el Plano de Alto Nivel (Nivel C4 Contenedores)
Dibuja de izquierda a derecha mientras narras el flujo:
1. **Entrada (`API Gateway`)**: Terminación TLS, Autenticación OAuth2/JWT y **Rate Limiting** en Redis.
2. **Servicio Core (`Payment / Transfer Service`)**: Stateless en Spring Boot 3 con Virtual Threads.
3. **Capa de Idempotencia (`Redis Cluster`)**: Almacena el `Idempotency-Key` con bloqueo atómico (`SET NX EX`) para evitar doble clic.
4. **Base de Datos Contable (`PostgreSQL Primary + Standby Síncrono`)**:
   - Explica que usarás un modelo de **Libro Mayor de Doble Partida (Double-Entry Ledger)** donde el dinero nunca se sobreescribe sin rastro, sino que cada movimiento genera un `DEBIT` y un `CREDIT` inmutables cuya suma da `0`.
   - Explica que las lecturas de historial van a **Read Replicas** o **Caché Redis** (patrón CQRS liviano).
5. **Desacoplamiento Asíncrono (`Tabla Outbox -> Kafka`)**:
   - Para notificaciones Push, Emails, Webhooks al comercio y Analítica/Antifraude asíncrono.

---

### ⏱️ Minuto 15 a 35: Deep Dive en los 4 Problemas Difíciles (Adelántate al entrevistador)
Antes de que el entrevistador te busque los puntos débiles, dilos tú mismo:
1. **Problema 1 — Doble Cobro por Reintento de Red**: Explica el flujo de `Idempotency-Key` (Redis Lock + `UNIQUE` constraint en PostgreSQL).
2. **Problema 2 — Race Condition y Deadlock cuando dos usuarios se transfieren al mismo tiempo**: Explica el bloqueo pesimista con ordenamiento determinístico de UUIDs (`firstId < secondId` con `SELECT ... FOR UPDATE`).
3. **Problema 3 — Llamadas a Bancos Externos Lentos**: Explica cómo separas la transacción en 2 transacciones cortas de DB (`PENDING` -> llamada HTTP fuera de `@Transactional` con Timeout + Circuit Breaker -> `CAPTURED` + `Outbox`), para nunca agotar el pool HikariCP.
4. **Problema 4 — "¿Y si la llamada al banco externo da Timeout en el segundo 3 y no sabemos si cobró o no?"**:
   - ¡Esta respuesta enamora a cualquier entrevistador Senior!: *"Nunca asumimos que un Timeout es un rechazo. Dejamos el pago en estado `UNKNOWN / PENDING_RECONCILIATION` y un worker programado consulta el estado en el banco por `external_reference` (Sonda de Polling) o concilia contra el archivo de liquidación `T+1` para confirmar o revertir con seguridad."*

---

### ⏱️ Minuto 35 a 45: Observabilidad, Seguridad y Cierre
Menciona brevemente cómo operas el sistema en producción:
- **Trazabilidad Distribuida**: `traceId` propagado por headers HTTP y headers de Kafka (OpenTelemetry / Micrometer) para rastrear un pago a través de todos los servicios.
- **Seguridad**: Datos sensibles encriptados (`AES-256-GCM`), montos siempre en `BigDecimal` con `RoundingMode.HALF_EVEN` (jamás `double`/`float`) y firmas `HMAC-SHA256` en Webhooks.

---

## 10. Fase 8: Diagnóstico en Producción ("La API está lenta a las 3 AM")

### La Pregunta Clásica de Incidente:
> *"Son las 3:00 PM de un día pico. El endpoint `POST /orders` que normalmente tarda 80 ms acaba de subir su latencia p99 a 12 segundos y están empezando a dar errores 503. No hubo ningún deploy en las últimas 4 horas. ¿Cómo lo diagnosticas paso a paso?"*

### Tu Protocolo de Respuesta Senior (Los 5 Pasos en Orden):
1. **Paso 1: Aislar si el cuello de botella está en nuestra App, en la Base de Datos o en una API Externa (Métricas RED y Logs con TraceId)**:
   - Miro en Grafana/Datadog el desglose de tiempo del span (OpenTelemetry/APM): ¿los 12 segundos se van esperando un cliente HTTP externo (`RestClient`), esperando obtener una conexión del pool (`hikaricp_connections_pending`), ejecutando un query SQL, o en pausas de CPU/GC de la JVM?
2. **Paso 2: Si hay hilos esperando conexión en HikariCP (`Connection Timeout`)**:
   - Busco quién está reteniendo las conexiones abiertas:
     - ¿Hay un método `@Transactional` que está haciendo una llamada HTTP externa a un tercero que se puso lento?
     - ¿Hay un **Lock Wait / Deadlock** en PostgreSQL? Ejecuto `SELECT * FROM pg_stat_activity WHERE wait_event_type = 'Lock'` para ver si una transacción colgada (`idle in transaction`) o una migración DDL bloqueó una tabla caliente (*Hot Row*).
3. **Paso 3: Si la Base de Datos tiene la CPU al 100%**:
   - Reviso `pg_stat_statements` ordenado por `total_exec_time`. A menudo, aunque no haya habido deploy hoy, una tabla superó cierto umbral de filas o las estadísticas del planificador quedaron desactualizadas (`ANALYZE`), haciendo que PostgreSQL cambie un `Index Scan` por un `Seq Scan` masivo.
4. **Paso 4: Si la Base de Datos y las APIs externas están rápidas, pero nuestra JVM está lenta**:
   - Reviso métricas de **Garbage Collection** (si el Heap está al 99% haciendo *Full GC Stop-The-World*) y tomo un **Thread Dump** (`jstack <PID>`) para ver en qué línea exacta de código están bloqueados los hilos (`BLOCKED` esperando un `synchronized` o agotamiento de hilos).
5. **Paso 5: Mitigación Inmediata antes de Causa Raíz**:
   - En un incidente real, primero **detenemos el sangrado** (activamos un Feature Flag, abrimos el Circuit Breaker de la integración lenta, matamos el PID bloqueante en Postgres con `pg_terminate_backend` o escalamos réplicas si es pico de tráfico legítimo) y luego escribimos el fix definitivo con su test y Post-Mortem sin culpas (*Blameless Post-Mortem*).

---

## 11. Diccionario Rápido: Las 20 Analogías de Oro para Entrevistas

Repasa esta tabla 10 minutos antes de entrar a la videollamada. Tener la analogía lista te da fluidez inmediata:

| # | Concepto Técnico | Analogía Corta para la Entrevista | Qué demuestra que entiendes |
| :--- | :--- | :--- | :--- |
| **1** | **Virtual Threads (Java 21)** | Mozos que entregan la comanda a la cocina y siguen atendiendo otras mesas en vez de quedarse parados frente al horno. | I/O no bloqueante a nivel de hilo OS sin perder código imperativo simple. |
| **2** | **`HashMap` + Colisiones** | Casilleros numerados de un hotel; si caen muchos en el mismo casillero (>8), pasa de lista a árbol ordenado para buscar rápido. | Complejidad `O(1)` promedio vs `O(log n)` peor caso e importancia de inmutabilidad. |
| **3** | **Proxy de Spring (`@Transactional`)** | El guardaespaldas en la puerta de la oficina del actor: si hablas contigo mismo adentro (`this.metodo()`), el guardaespaldas no se entera. | Funcionamiento de AOP, Dynamic Proxies y por qué falla el *Self-Invocation*. |
| **4** | **Problema `N+1` en JPA** | Ir al depósito a buscar una caja con 50 carpetas (1 viaje) y luego hacer 50 viajes individuales para buscar las hojas de cada carpeta. | Impacto del *Round-Trip Time (RTT)* de red en la base de datos. |
| **5** | **Índice Compuesto B-Tree** | Guía telefónica ordenada por `(Apellido, Nombre)`: si solo sabes el nombre de pila, tienes que leer todo el libro hoja por hoja. | Regla del prefijo izquierdo y regla `ESR` (Equality, Sort, Range). |
| **6** | **Optimistic Locking (`@Version`)** | Google Docs con número de revisión: no bloquea a nadie al leer, pero al guardar verifica que nadie haya cambiado la versión antes que tú. | Ideal para baja contención; malo para alta contención (*Hot Rows*). |
| **7** | **Pessimistic Locking (`FOR UPDATE`)** | Entrar a un probador de ropa y pasar el cerrojo por dentro: el siguiente cliente hace fila afuera hasta que tú salgas. | Garantiza exclusión en alta contención; requiere transacciones ultracortas. |
| **8** | **Deadlock y Orden Determinístico** | Dos carpinteros que necesitan el martillo y el clavo; si obligas a todos a tomarlos en orden alfabético, nadie se bloquea cruzado. | Prevención matemática de ciclos de espera en transferencias P2P. |
| **9** | **Idempotencia (`Idempotency-Key`)** | El botón del ascensor: aunque lo aprietes 30 veces seguidas por ansiedad, solo se registra una orden y viene un solo ascensor. | Seguridad ante reintentos de red con `SET NX` en Redis + `UNIQUE` en DB. |
| **10** | **Transactional Outbox** | Firmar el remito de salida en el mismo libro contable antes de que el camión de correo pase a retirar los paquetes. | Resuelve el problema de *Dual Write* entre Base de Datos y Kafka. |
| **11** | **Kafka Partition Key** | Cabinas de peaje en una autopista: si todos los autos de un mismo cliente van por el mismo carril, llegan en el orden exacto en que salieron. | Orden garantizado por partición y paralelismo por `Consumer Group`. |
| **12** | **Kafka Consumer Group** | Una brigada de cajeros repartiéndose las filas de un supermercado: nunca dos cajeros del mismo equipo atienden la misma fila a la vez. | Escalabilidad horizontal hasta el número máximo de particiones del topic. |
| **13** | **Patrón Saga (Compensación)** | Reservar vuelo, hotel y auto para un viaje: si el auto no tiene cupo, llamas al hotel y a la aerolínea para cancelar las reservas previas. | Transacciones distribuidas sin `2PC` bloqueante mediante eventos compensatorios. |
| **14** | **CQRS** | En un restaurante, la ventanilla donde entregas el pedido a cocina (Command) es distinta de la pantalla gigante donde miras qué pedidos están listos (Query). | Separación de modelos de escritura (ACID) y lectura (Índices/Caché/Proyecciones). |
| **15** | **Circuit Breaker** | La llave térmica de tu casa: si un electrodoméstico entra en corto, corta el paso de inmediato para que no se incendie toda la instalación. | Fallo rápido (*Fail-Fast*) y autocuración (`CLOSED` -> `OPEN` -> `HALF_OPEN`). |
| **16** | **Bulkhead** | Los compartimentos estancos de un barco: si entra agua en la sala de máquinas, se sella esa puerta para que el barco siga flotando. | Aislamiento de pools de hilos o semáforos por integración externa. |
| **17** | **Rate Limiting (Token Bucket)** | Un balde al que le caen 10 fichas por segundo: cada petición gasta 1 ficha; si vienen 50 de golpe y vacían el balde, las demás reciben `429`. | Protección contra abusos, DDoS y vecinos ruidosos (*Noisy Neighbors*). |
| **18** | **Cache Stampede (Estampida)** | Se apaga el cartel de la estación de tren por 1 segundo y 10.000 pasajeros corren al mismo tiempo a preguntarle a la única empleada de ventanilla. | Por qué usar *Distributed Lock* o *Early Probabilistic Refresh* al expirar un caché caliente. |
| **19** | **Arquitectura Hexagonal** | Un motor con enchufes universales (Puertos): puedes conectarle un volante real o un simulador de pruebas (Adaptadores) sin tocar el motor. | Desacoplamiento total entre reglas de negocio y tecnología externa (DIP). |
| **20** | **Libro Mayor de Doble Partida (Ledger)** | Un cuaderno contable escrito con tinta indeleble donde nunca usas goma de borrar: si te equivocas o devuelves plata, escribes un nuevo asiento inverso. | Inmutabilidad financiera, auditoría perfecta y prevención de saldos corruptos. |

---

## 12. Preguntas Inteligentes para Hacerle al Entrevistador al Final

Cuando el entrevistador te diga *"¿Tienes alguna pregunta para nosotros?"*, **nunca digas "No, quedó todo claro"**. Elige **2 o 3 de estas preguntas**: demuestran mentalidad de dueño (*ownership*), madurez técnica y te ayudan a saber cómo es realmente trabajar ahí:

1. **Sobre Desafíos Técnicos Reales**:
   > *"¿Cuál es el cuello de botella técnico o el desafío de arquitectura más interesante que el equipo tiene que resolver en los próximos 6 meses?"*
2. **Sobre Operación en Producción y Calidad**:
   > *"¿Cómo manejan hoy la observabilidad, los despliegues a producción y el equilibrio entre entregar nuevas funcionalidades de negocio y pagar deuda técnica?"*
3. **Sobre Decisiones de Arquitectura**:
   > *"Cuando hay que diseñar una funcionalidad grande o cambiar algo crítico en el sistema, ¿cómo es el proceso de toma de decisiones dentro del equipo? ¿Usan RFCs / ADRs (Architecture Decision Records)?"*
4. **Sobre Expectativas del Rol Senior**:
   > *"Para alguien que entra en este rol Senior hoy, ¿cómo se ve el éxito a los 90 días? ¿Qué te haría decir 'fue una excelente contratación'?"*

