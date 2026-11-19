# Glosario de Inglés Técnico para Programadores (Java, Spring Boot y Trabajo en Equipos IT)

Este glosario reúne todo el vocabulario de **inglés técnico real** que necesitas para **leer código fuente sin trabarte, nombrar variables y métodos como un profesional, entender mensajes de error y *stack traces*, leer documentación oficial, participar en *Code Reviews* (Git/PRs) y desenvolverte en el día a día de un equipo de desarrollo**.

---

## Índice
1. [Verbos Más Usados en Nombres de Métodos (Qué hace cada uno)](#1-verbos-más-usados-en-nombres-de-métodos-qué-hace-cada-uno)
2. [Prefijos para Métodos Booleanos (`is`, `has`, `can`, `should`)](#2-prefijos-para-métodos-booleanos-is-has-can-should)
3. [Sustantivos y Sufijos Clásicos en Clases Java y Spring Boot](#3-sustantivos-y-sufijos-clásicos-en-clases-java-y-spring-boot)
4. [Parejas de Antónimos en Programación (Inicio/Fin, Entrada/Salida)](#4-parejas-de-antónimos-en-programación-iniciofin-entradasalida)
5. [Vocabulario de Datos, Colecciones y Estados](#5-vocabulario-de-datos-colecciones-y-estados)
6. [Cómo Leer Errores, Excepciones y *Stack Traces* en Inglés](#6-cómo-leer-errores-excepciones-y-stack-traces-en-inglés)
7. [Palabras Clave de Documentación Técnica y Javadoc](#7-palabras-clave-de-documentación-técnica-y-javadoc)
8. [Inglés de Arquitectura, Backend, Cloud y Rendimiento](#8-inglés-de-arquitectura-backend-cloud-y-rendimiento)
9. [Inglés para Git, Pull Requests (PRs) y Code Reviews](#9-inglés-para-git-pull-requests-prs-y-code-reviews)
10. [Inglés de Metodologías Ágiles (Scrum, Jira, Daily Standup)](#10-inglés-de-metodologías-ágiles-scrum-jira-daily-standup)
11. [*Phrasal Verbs* Esenciales en Ingeniería de Software](#11-phrasal-verbs-esenciales-en-ingeniería-de-software)
12. [Falsos Amigos (*False Friends*) en Programación](#12-falsos-amigos-false-friends-en-programación)
13. [Abreviaturas y Acrónimos de Chat Técnico, Comentarios y Código](#13-abreviaturas-y-acrónimos-de-chat-técnico-comentarios-y-código)
14. [Cómo se Leen en Inglés los Símbolos del Código](#14-cómo-se-leen-en-inglés-los-símbolos-del-código)
15. [Plantillas Prácticas Listas para Usar (Commits, PRs, Daily y Bugs)](#15-plantillas-prácticas-listas-para-usar-commits-prs-daily-y-bugs)

---

## 1. Verbos Más Usados en Nombres de Métodos (Qué hace cada uno)

Cuando lees código en inglés en librerías o proyectos de Spring Boot, el verbo con el que empieza el método te dice exactamente qué esperar:

| Verbo en Inglés | Significado en Español | Cuándo se usa exactamente | Ejemplo en Código Java / Spring |
|---|---|---|---|
| **`get` / `set`** | Obtener / Establecer | Acceder o asignar el valor de una propiedad en memoria de forma inmediata. | `user.getEmail();` / `user.setActive(true);` |
| **`fetch` / `retrieve`** | Traer / Recuperar (desde afuera) | Obtener datos que cuestan trabajo traer (desde una Base de Datos, API externa o disco). | `fetchUserProfileFromApi(userId);` |
| **`find` / `search` / `lookup`** | Buscar / Consultar | Buscar algo que **puede existir o no** (suele devolver `Optional<T>` o una lista). | `userRepository.findByEmail(email);` |
| **`compute` / `calculate`** | Calcular / Computar | Realizar un cálculo matemático o procesamiento a partir de datos. | `calculateTotalTax(subtotal);` |
| **`parse` / `format`** | Analizar texto / Formatear a texto | `parse`: convierte un `String` a objeto (`LocalDate`, `Integer`).<br>`format`: convierte un objeto a `String` legible. | `LocalDate.parse("2026-11-05");`<br>`formatter.format(date);` |
| **`map` / `convert` / `transform`** | Mapear / Convertir / Transformar | Cambiar un objeto de un tipo a otro (ej. de `Entity` a `DTO`). | `stream.map(userMapper::toDto);` |
| **`validate` / `verify` / `check`** | Validar / Verificar / Comprobar | Comprobar que se cumplan reglas; si no se cumplen, lanza excepción o devuelve `boolean`. | `validatePaymentRequest(request);` |
| **`ensure` / `assert`** | Asegurar / Afirmar | Garantizar que una condición sea verdadera antes de seguir. | `ensureSufficientBalance(account, amount);` |
| **`handle` / `process`** | Manejar / Procesar | Recibir un evento, excepción o petición y encargarse de ejecutar su lógica. | `handlePaymentWebhook(payload);` |
| **`dispatch` / `publish` / `emit`** | Despachar / Publicar / Emitir | Enviar un evento o mensaje hacia otros componentes o colas. | `eventPublisher.publishEvent(event);` |
| **`bind` / `unbind`** | Vincular / Desvincular | Conectar datos de una petición o configuración a un objeto Java. | `DataBinder` / `@ConfigurationProperties` |
| **`populate` / `hydrate`** | Poblar / Rellenar datos | Llenar un objeto o base de datos con valores o relaciones. | `populateDefaultRoles(user);` |
| **`evict` / `invalidate` / `purge`** | Desalojar / Invalidar / Purgar | Borrar entradas de una caché o limpiar datos vencidos. | `@CacheEvict("products")` / `purgeExpiredTokens();` |
| **`revoke` / `grant`** | Revocar / Otorgar | Quitar o dar permisos, accesos o tokens de seguridad. | `revokeRefreshToken(tokenId);` |
| **`acquire` / `release`** | Adquirir / Liberar | Tomar un recurso compartido (un bloqueo *lock*, una conexión) y soltarlo al terminar. | `lock.acquire(); ... lock.release();` |
| **`append` / `prepend`** | Agregar al final / Agregar al principio | Añadir elementos al final o al inicio de una cadena o lista. | `stringBuilder.append("px");` |
| **`split` / `join` / `merge`** | Dividir / Unir / Fusionar | Separar un elemento en partes o combinar varios en uno solo. | `String.join(",", tags);` |
| **`trim` / `strip` / `sanitize`** | Recortar espacios / Limpiar | Quitar espacios en blanco de los bordes o limpiar caracteres peligrosos de un input. | `email.trim().toLowerCase();` |
| **`skip` / `omit` / `bypass`** | Saltar / Omitir / Evitar | Pasar por alto cierta cantidad de elementos o un paso de validación. | `stream.skip(10).limit(5);` |
| **`poll` / `peek`** | Extraer de la cola / Espiar sin sacar | En una cola (`Queue`): `poll` saca y devuelve el primero; `peek` lo mira sin sacarlo. | `queue.poll();` / `queue.peek();` |
| **`flush`** | Vaciar / Volcar a disco o BD | Forzar a que los cambios pendientes en memoria se escriban inmediatamente en la BD o stream. | `entityManager.flush();` |
| **`wrap` / `unwrap`** | Envolver / Desenvolver | Meter un objeto dentro de otro (ej. un Decorador o `Optional`) o sacar el objeto interno. | `Optional.of(value)` / `proxy.unwrap( target )` |

**Ejemplo en código combinando estos verbos correctamente:**
```java
@Service
public class OrderProcessingService {

    public OrderDto processIncomingOrder(String rawOrderJson) {
        // 1. parse: de String crudo a objeto
        OrderRequest request = parseOrderJson(rawOrderJson);
        // 2. sanitize & validate: limpiar y verificar reglas
        sanitizeCustomerInput(request);
        validateOrderItems(request);
        // 3. fetch: traer datos desde la base de datos
        Customer customer = fetchCustomerById(request.customerId());
        // 4. calculate: hacer cuentas matemáticas
        BigDecimal total = calculateOrderTotal(request.items());
        // 5. publish: emitir evento a otros módulos
        publishOrderConfirmedEvent(customer, total);
        // 6. map: convertir a DTO de salida
        return mapToOrderDto(customer, total);
    }
}
```

---

## 2. Prefijos para Métodos Booleanos (`is`, `has`, `can`, `should`)

En Java y Clean Code, todo método o variable que devuelva `boolean` (`true` o `false`) se escribe como una **pregunta en inglés** usando uno de estos 5 prefijos:

| Prefijo | Qué pregunta | Significado | Ejemplo en Java |
|---|---|---|---|
| **`is...`** | *¿Es / Está...?* | Estado o característica actual del objeto. | `user.isActive()`, `order.isPaid()`, `list.isEmpty()`, `token.isExpired()` |
| **`has...`** | *¿Tiene / Posee...?* | Si contiene un elemento, permiso o relación. | `user.hasRole("ADMIN")`, `page.hasNext()`, `customer.hasPendingDebt()` |
| **`can...`** | *¿Puede...?* | Si está habilitado o cumple las condiciones para realizar una acción. | `account.canWithdraw(amount)`, `order.canBeCancelled()` |
| **`should...`** | *¿Debería...?* | Si corresponde ejecutar una regla o comportamiento según una configuración/condición. | `shouldRetryPayment(exception)`, `shouldSendNotification(user)` |
| **`matches` / `supports` / `exists`** | *¿Coincide / Soporta / Existe?* | Verbos en tercera persona singular que responden Sí o No. | `passwordEncoder.matches(raw, hash)`, `strategy.supports(type)`, `repo.existsById(id)` |

**Ejemplo de lectura fluida gracias a los prefijos booleanos:**
```java
public void cancelOrder(Order order, User currentUser) {
    if (order.isAlreadyShipped()) {
        throw new IllegalStateException("Cannot cancel an order that is already shipped");
    }
    if (!currentUser.hasPermission("CANCEL_ORDER") && !order.belongsTo(currentUser)) {
        throw new AccessDeniedException("User cannot cancel this order");
    }
    if (order.shouldRefundAutomatically()) {
        refundService.issueRefund(order);
    }
}
```

---

## 3. Sustantivos y Sufijos Clásicos en Clases Java y Spring Boot

Cuando abres las carpetas de un proyecto empresarial en Java/Spring Boot, casi todas las clases terminan con uno de estos sufijos en inglés:

| Sufijo de Clase | Traducción y Propósito Técnico | Ejemplo de Clase Real |
|---|---|---|
| **`...Controller`** | Punto de entrada HTTP (recibe `Request` y devuelve `Response`). | `ProductRestController` |
| **`...Service` / `...UseCase`** | Lógica de negocio u orquestación de un caso de uso de la aplicación. | `CheckoutService`, `ApproveLoanUseCase` |
| **`...Repository` / `...Dao`** | Acceso a base de datos (*Data Access Object*). | `CustomerRepository` |
| **`...Entity` / `...Document`** | Clase que representa una tabla SQL o colección NoSQL. | `InvoiceEntity`, `AuditLogDocument` |
| **`...Dto` / `...Request` / `...Response`** | Objeto de transferencia de datos para entradas y salidas de la API. | `CreateUserRequest`, `UserSummaryDto` |
| **`...Mapper` / `...Converter`** | Traduce entre dos tipos de objetos (ej. de `Entity` a `Dto`). | `OrderMapper`, `CurrencyConverter` |
| **`...Provider` / `...Supplier`** | Suministra o provee una instancia, configuración o credenciales. | `JwtTokenProvider`, `TaxRateProvider` |
| **`...Factory` / `...Builder`** | Encapsula la creación paso a paso de objetos complejos. | `PaymentStrategyFactory`, `UriComponentsBuilder` |
| **`...Handler` / `...Listener` / `...Consumer`** | Reacciona cuando ocurre un evento, mensaje de cola o excepción. | `GlobalExceptionHandler`, `OrderPaidEventListener` |
| **`...Publisher` / `...Producer` / `...Sender`** | Envía eventos, mensajes a Kafka/RabbitMQ o correos. | `NotificationPublisher`, `SmtpEmailSender` |
| **`...Filter` / `...Interceptor`** | Intercepta peticiones antes o después de que lleguen al controlador. | `JwtAuthenticationFilter`, `LoggingInterceptor` |
| **`...Adapter` / `...Client` / `...Gateway`** | Se conecta con un sistema externo o adapta una interfaz ajena a la nuestra. | `StripePaymentAdapter`, `ShippingApiClient` |
| **`...Context` / `...Holder`** | Almacena estado de una ejecución o del hilo actual (`ThreadLocal`). | `SecurityContextHolder`, `LocaleContextHolder` |
| **`...Properties` / `...Config`** | Clases de configuración o mapeo de variables de `application.yml`. | `SecurityConfig`, `AwsS3Properties` |
| **`...Spec` / `...Criteria` / `...Predicate`** | Define condiciones de filtrado para búsquedas dinámicas. | `ProductSearchCriteria`, `OrderSpecification` |
| **`...Wrapper` / `...Decorator`** | Envuelve otro objeto para agregarle funcionalidad sin modificarlo. | `BufferingClientHttpResponseWrapper` |
| **`...Resolver` / `...Extractor`** | Deduce o extrae un valor a partir de un contexto complejo (ej. de un header HTTP). | `CurrentUserArgumentResolver`, `ClaimExtractor` |
| **`...Registry` / `...Pool`** | Catálogo donde se registran componentes o conjunto de conexiones reutilizables. | `MeterRegistry`, `HikariConnectionPool` |

**Ejemplo de cómo interactúan estos sufijos en un flujo típico:**
```java
@RestController
public class PaymentController { // 1. Controller recibe el Request
    private final PaymentUseCase paymentUseCase; // 2. Llama al UseCase / Service
    private final PaymentMapper paymentMapper;   // 3. Usa el Mapper para devolver un Response DTO

    public PaymentController(PaymentUseCase paymentUseCase, PaymentMapper paymentMapper) {
        this.paymentUseCase = paymentUseCase;
        this.paymentMapper = paymentMapper;
    }

    @PostMapping("/payments")
    public PaymentResponse create(@RequestBody CreatePaymentRequest request) {
        Payment payment = paymentUseCase.execute(request);
        return paymentMapper.toResponse(payment);
    }
}
```

---

## 4. Parejas de Antónimos en Programación (Inicio/Fin, Entrada/Salida)

Usar la pareja correcta en inglés hace que tu código sea simétrico y fácil de adivinar:

| Palabra A | Palabra Opuesta B | Significado | Ejemplo de Uso Simétrico |
|---|---|---|---|
| **`source`** | **`target` / `destination`** | Origen ↔ Destino | `mapper.map(sourceObject, targetClass);` |
| **`request`** | **`response`** | Petición ↔ Respuesta | `HttpServletRequest` / `HttpServletResponse` |
| **`input`** | **`output`** | Entrada ↔ Salida | `InputStream` / `OutputStream` |
| **`producer`** | **`consumer`** | Productor ↔ Consumidor | `KafkaProducer` / `KafkaConsumer` |
| **`publisher`** | **`subscriber` / `listener`** | Publicador ↔ Suscriptor | `EventPublisher` / `EventListener` |
| **`parent`** | **`child`** | Padre ↔ Hijo | `parentCategory.addChild(subCategory);` |
| **`inbound`** | **`outbound`** | Entrante (hacia nuestro sistema) ↔ Saliente (hacia afuera) | `inboundWebhook` / `outboundHttpCall` |
| **`upstream`** | **`downstream`** | Río arriba (de quien dependemos / origen) ↔ Río abajo (quien depende de nosotros / destino) | *"The upstream payment service is down"*. |
| **`lock` / `acquire`** | **`unlock` / `release`** | Bloquear ↔ Desbloquear / Liberar | `lock.acquire()` ↔ `lock.release()` |
| **`enable`** | **`disable`** | Habilitar ↔ Deshabilitar | `user.enableTwoFactorAuth()` ↔ `user.disableTwoFactorAuth()` |
| **`encode` / `encrypt`** | **`decode` / `decrypt`** | Codificar / Encriptar ↔ Decodificar / Desencriptar | `Base64.getEncoder().encode(...)` ↔ `decode(...)` |
| **`serialize`** | **`deserialize`** | Convertir Objeto a JSON/Bytes ↔ Convertir JSON/Bytes a Objeto | `JsonSerializer` ↔ `JsonDeserializer` |
| **`enqueue` / `push`** | **`dequeue` / `pop` / `poll`** | Meter en la cola/pila ↔ Sacar de la cola/pila | `stack.push(item)` ↔ `stack.pop()` |
| **`grant`** | **`revoke`** | Conceder permiso ↔ Revocar permiso | `grantAccess(user)` ↔ `revokeAccess(user)` |
| **`bind`** | **`unbind`** | Enlazar ↔ Desenlazar | `bindResource()` ↔ `unbindResource()` |
| **`min` (`minimum`)** | **`max` (`maximum`)** | Mínimo ↔ Máximo | `minPrice` ↔ `maxPrice` |
| **`first` / `head`** | **`last` / `tail`** | Primero / Cabeza ↔ Último / Cola | `list.getFirst()` ↔ `list.getLast()` |
| **`legacy`** | **`modern` / `greenfield`** | Sistema viejo heredado ↔ Sistema nuevo desde cero | *"Migrating from the legacy ERP to a greenfield microservice"*. |

**Ejemplo de simetría de nombres en una clase:**
```java
public interface FileTransferChannel {
    void openConnection();
    void transfer(Path sourcePath, Path targetPath);
    void closeConnection(); // Simétrico con openConnection (no mezclar open() con stop() o finish())
}
```

---

## 5. Vocabulario de Datos, Colecciones y Estados

Palabras que aparecen todos los días en variables, parámetros, JSONs y bases de datos:

| Término en Inglés | Significado Exacto | Ejemplo en Código |
|---|---|---|
| **`payload`** | Carga útil: los datos reales que viajan en el cuerpo (`body`) de un request HTTP o mensaje de Kafka (sin contar los headers). | `@RequestBody OrderPayload payload` |
| **`metadata`** | Metadatos: datos descriptivos sobre otros datos (quién lo creó, tamaño del archivo, versión, traceId). | `Map<String, String> metadata` |
| **`threshold`** | Umbral o límite a partir del cual se dispara una acción o alerta. | `if (failedAttempts >= MAX_RETRY_THRESHOLD)` |
| **`fallback`** | Valor o comportamiento de respaldo por si falla la opción principal. | `String city = Optional.ofNullable(input).orElse(DEFAULT_FALLBACK_CITY);` |
| **`placeholder`** | Valor o marca de posición temporal que luego será reemplazado (ej. `?` en SQL o `{id}` en una URL). | `"/api/users/{id}"` (`{id}` es un placeholder). |
| **`raw`** | Crudo / Sin procesar (antes de validar, desencriptar o formatear). | `String rawPassword` vs `String encodedPassword`. |
| **`stale`** | Desactualizado / Viejo / Rancio (dato en caché que ya no refleja el valor real en la BD). | *"Stale data in cache"* / `StaleObjectStateException`. |
| **`orphan`** | Huérfano (registro hijo cuyo padre fue eliminado). | `@OneToMany(orphanRemoval = true)` |
| **`transient`** | Transitorio / Temporal (no se guarda en base de datos ni se serializa; o fallo pasajero). | `@Transient private int tempCalculation;` |
| **`immutable`** | Inmutable (una vez creado no se puede modificar su estado). | `List.of("A", "B")` crea una lista *immutable*. |
| **`nested`** | Anidado (un objeto, bucle o clase dentro de otro). | *Nested JSON object* / *Nested loop*. |
| **`chunk` / `batch`** | Fragmento / Lote (bloque de registros que se procesan juntos, ej. de a 500 filas). | `List<List<Item>> batches = partitionIntoChunks(items, 500);` |
| **`offset` & `limit`** | Desplazamiento (cuántos registros saltar) y Límite (cuántos traer) en una paginación. | `SELECT * FROM productos LIMIT 20 OFFSET 40` |
| **`cursor`** | Puntero que marca la posición actual al recorrer resultados página por página. | `String nextCursor` |
| **`flag`** | Bandera booleana que activa o desactiva un comportamiento. | `boolean isFeatureFlagEnabled = true;` |
| **`mock` / `stub` / `dummy` / `fake`** | Objetos simulados que se usan en los tests para reemplazar dependencias reales. | `@Mock private PaymentGateway paymentGatewayMock;` |
| **`boilerplate`** | Código repetitivo y rutinario que hay que escribir por obligación del lenguaje (ej. getters, setters, constructores que Lombok ahorra). | *"Lombok reduces boilerplate code"*. |
| **`hardcoded`** | Valor "quemado" o escrito fijo directamente en el código fuente en lugar de venir de configuración o BD. | `String url = "http://localhost:8080"; // Hardcoded URL` |
| **`deprecated`** | Obsoleto / Desaconsejado (sigue funcionando por compatibilidad, pero no debe usarse en código nuevo porque se eliminará en el futuro). | `@Deprecated(since = "2.0", forRemoval = true)` |

**Ejemplo de uso de este vocabulario en un método real:**
```java
public boolean isAboveFraudThreshold(BigDecimal rawAmount, BigDecimal customThreshold) {
    // Si no nos pasan un umbral (threshold) personalizado, usamos el valor de respaldo (fallback)
    BigDecimal effectiveThreshold = (customThreshold != null) ? customThreshold : DEFAULT_FALLBACK_THRESHOLD;
    return rawAmount.compareTo(effectiveThreshold) > 0;
}
```

---

## 6. Cómo Leer Errores, Excepciones y *Stack Traces* en Inglés

El 80% del tiempo de debugging consiste en leer mensajes de error en inglés. Esta tabla traduce las frases exactas que escupe la consola de Java y Spring Boot:

| Frase en el Error / Stack Trace | Traducción Exacta | Qué significa y Cómo arreglarlo |
|---|---|---|
| **`Caused by: ...`** | *Causado por: ...* | **¡La línea más importante del log!** Siempre baja hasta el **último** `Caused by:` del stack trace; ahí está la causa raíz real del fallo. |
| **`... cannot be null` / `must not be null`** | *... no puede ser nulo* | Le pasaste `null` a un método o columna que exige un valor obligatorio. |
| **`Expected 1 bean, but found 2`** | *Se esperaba 1 bean, pero se encontraron 2* | Hay dos clases implementando la misma interfaz en Spring; falta `@Primary` o `@Qualifier`. |
| **`No qualifying bean of type ... available`** | *No hay ningún bean disponible del tipo ...* | Olvidaste poner `@Service`, `@Repository` o `@Component` a la clase, o está fuera del paquete escaneado. |
| **`Cannot invoke "..." because "..." is null`** | *No se puede invocar el método X porque la variable Y es null* | El clásico `NullPointerException` de Java 17+: te dice exactamente cuál variable estaba en `null`. |
| **`Failed to bind properties under '...'`** | *Falló al vincular las propiedades bajo '...'* | Hay un error de tipo o sintaxis en tu `application.yml` (ej. pusiste letras donde iba un número). |
| **`Connection refused`** | *Conexión rechazada* | El servidor destino (la base de datos PostgreSQL, Redis o el otro microservicio) **está apagado** o el puerto es incorrecto. |
| **`Read timed out` / `Connect timed out`** | *Se agotó el tiempo de espera de lectura / conexión* | El servidor externo tardó demasiado en responder o un firewall bloquea el tráfico. |
| **`Duplicate key value violates unique constraint`** | *Valor de clave duplicada viola la restricción de unicidad* | Estás intentando insertar un registro con un email, DNI o código que ya existe en una columna `UNIQUE`. |
| **`Row was updated or deleted by another transaction`** | *La fila fue actualizada o borrada por otra transacción* | Choque de concurrencia detectado por `@Version` (Bloqueo Optimista). |
| **`Cannot deserialize value of type ... from String`** | *No se puede deserializar el valor de tipo X a partir de un String* | El JSON que enviaron en el body tiene un formato inválido (ej. una fecha mal escrita o un texto donde iba un número o Enum). |
| **`Required request body is missing`** | *Falta el cuerpo obligatorio de la petición* | Llamaron a tu `@PostMapping` sin enviar ningún JSON en el body. |
| **`Request method 'POST' is not supported`** | *El método HTTP 'POST' no está soportado* | Llamaron con `POST` a una URL que en tu Controller está anotada con `@GetMapping` o `@PutMapping`. |
| **`Access is denied` / `Full authentication is required`** | *Acceso denegado / Se requiere autenticación completa* | Error `403 Forbidden` (no tienes el rol necesario) o `401 Unauthorized` (no enviaste el token JWT). |
| **`Unreachable statement`** | *Instrucción inalcanzable* | Error de compilación: escribiste código debajo de un `return` o `throw` que jamás se podrá ejecutar. |
| **`Non-static method cannot be referenced from a static context`** | *Un método no estático no puede llamarse desde un contexto estático* | Intentaste llamar un método normal haciendo `NombreClase.metodo()` en vez de inyectar la instancia `objeto.metodo()`. |

**Ejemplo de cómo leer un Stack Trace en 5 segundos:**
```text
org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'orderController': ...
   at org.springframework.beans... (30 líneas internas del framework que puedes ignorar)
Caused by: java.lang.IllegalArgumentException: Could not resolve placeholder 'stripe.api.key' in value "${stripe.api.key}"
   at com.empresa.pagos.StripeClient.<init>(StripeClient.java:24)  <-- ¡AQUÍ ESTÁ TU CLASE Y LÍNEA EXACTA!
```
- **Traducción instantánea**: *"No se pudo resolver el marcador de posición `stripe.api.key`"*. Significa que olvidaste definir `stripe.api.key` en tu `application.yml` o variables de entorno.

---

## 7. Palabras Clave de Documentación Técnica y Javadoc

Cuando lees la documentación oficial de Java (Javadoc), de Spring Boot o un RFC, estas palabras aparecen todo el tiempo y tienen un significado preciso:

| Palabra en la Documentación | Significado en Español | Por qué es importante prestarle atención |
|---|---|---|
| **`under the hood`** | *Bajo el capó / Internamente* | Explica cómo hace las cosas el framework por detrás sin que tú lo veas. |
| **`out of the box`** | *Listo para usar de fábrica / Sin configuración extra* | Algo que ya viene incluido y funcionando apenas agregas la dependencia. |
| **`caveat` / `gotcha`** | *Advertencia / Trampa oculta /Detalle a tener en cuenta* | **¡Peligro!** Lee siempre los párrafos que dicen *Caveat*: explican casos donde la herramienta puede fallar si no tienes cuidado. |
| **`rule of thumb`** | *Regla general práctica* | Un consejo basado en la experiencia que funciona bien en el 90% de los casos. |
| **`mutually exclusive`** | *Mutuamente excluyentes* | Si usas la opción A, **no puedes** usar la opción B al mismo tiempo. |
| **`thread-safe`** | *Seguro para múltiples hilos* | Puedes compartir una única instancia de esa clase entre miles de peticiones simultáneas sin que se corrompan sus datos. |
| **`idempotent`** | *Idempotente* | Llamarlo 1 vez o N veces seguidas produce el mismo estado. |
| **`no-op` (*No Operation*)** | *Operación vacía / Que no hace nada* | Una implementación por defecto que no ejecuta ninguna acción (ej. `NoOpPasswordEncoder` o un caché desactivado). |
| **`lenient` vs `strict`** | *Permisivo / Tolerante* vs *Estricto* | Un parser `lenient` intenta adivinar fechas mal escritas; uno `strict` lanza excepción ante cualquier desvío. |
| **`eager` vs `lazy`** | *Ansioso (inmediato)* vs *Perezoso (bajo demanda)* | `EAGER` carga todo al principio; `LAZY` espera hasta el momento exacto en que alguien pide el dato. |
| **`standalone`** | *Autónomo / Independiente* | Que puede correr por sí solo sin necesitar un servidor externo ni un clúster. |
| **`seamless` / `seamlessly`** | *Sin costuras / De forma transparente y fluida* | Que se integra con otra herramienta sin fricción ni código extra. |
| **`leverage`** | *Aprovechar / Apalancarse en* | *"Spring Boot leverages Hibernate..."* = Spring Boot aprovecha/utiliza Hibernate. |
| **`fine-grained` vs `coarse-grained`** | *De grano fino (muy detallado)* vs *De grano grueso (general)* | Permisos *fine-grained* controlan cada botón; *coarse-grained* solo distinguen Admin vs Usuario. |

**Ejemplo leyendo un Javadoc real de Spring:**
```java
/**
 * Retrieves an entity by its id.
 * <p>Note: This implementation is thread-safe and works out of the box.
 * Caveat: returns Optional.empty() if the entity is marked as soft-deleted.
 *
 * @param id must not be {@literal null}.
 * @return the entity with the given id or {@literal Optional#empty()} if none found.
 * @throws IllegalArgumentException if {@literal id} is {@literal null}.
 */
Optional<T> findById(ID id);
```

---

## 8. Inglés de Arquitectura, Backend, Cloud y Rendimiento

Términos indispensables para diseñar sistemas, leer métricas de servidores y hablar con arquitectos o DevOps:

| Término en Inglés | Traducción y Significado Técnico | Ejemplo en Contexto |
|---|---|---|
| **`Throughput`** | Caudal de procesamiento: cuántas operaciones o peticiones por segundo (`RPS` / `TPS`) es capaz de procesar el sistema. | *"Our payment service handles a throughput of 2,000 requests per second"*. |
| **`Latency`** | Latencia: el tiempo que tarda una petición en ir y volver (medido en milisegundos `ms`, usualmente en percentiles `p95` o `p99`). | *"The p99 latency increased from 50ms to 800ms"*. |
| **`Bottleneck`** | Cuello de botella: el componente más lento que frena el rendimiento de todo el sistema. | *"The database connection pool is our main bottleneck"*. |
| **`Single Point of Failure (SPOF)`** | Punto único de fallo: si esa pieza se cae, se cae toda la empresa porque no tiene redundancia. | *"Having only one database instance without replicas is a SPOF"*. |
| **`Scalability` (*Scale up* vs *Scale out*)** | - **Scale up (Vertical):** Ponerle más RAM y CPU a la misma máquina.<br>- **Scale out (Horizontal):** Agregar más máquinas/contenedores en paralelo. | *"We scaled out from 2 to 6 Kubernetes pods during Black Friday"*. |
| **`High Availability (HA)`** | Alta Disponibilidad: diseño con múltiples nodos para que el sistema siga funcionando aunque se apague un servidor. | *"Redis is deployed in High Availability mode"*. |
| **`Failover`** | Conmutación por error: cambiar automáticamente al servidor o proveedor de respaldo cuando falla el principal. | *"Automatic database failover took 10 seconds"*. |
| **`Graceful Shutdown`** | Apagado elegante: cuando detienes el servidor, deja de recibir peticiones nuevas pero espera unos segundos a que terminen las que ya estaban en vuelo antes de apagarse. | `server.shutdown: graceful` en `application.yml`. |
| **`Graceful Degradation`** | Degradación elegante: si se cae un módulo secundario (ej. recomendaciones), la web sigue funcionando y vendiendo sin mostrar esa cajita en vez de tirar error 500 en toda la pantalla. | Devolver una lista vacía o tarifa estándar en el `fallback` del Circuit Breaker. |
| **`Backpressure`** | Contrapresión: cuando un productor envía datos más rápido de lo que el consumidor puede procesar, y el consumidor le pide que frene el ritmo. | Concepto clave en colas, streams y WebFlux. |
| **`Rate Limiting` / `Throttling`** | Limitación de tasa: poner un tope de cuántas peticiones puede hacer un usuario por minuto (devuelve `429 Too Many Requests`). | *"We applied rate limiting of 60 requests per minute per IP"*. |
| **`Stateless` vs `Stateful`** | **Sin estado** (cada petición trae su JWT y cualquier servidor puede atenderla) vs **Con estado** (el servidor guarda datos de sesión del usuario en su propia memoria RAM). | `SessionCreationPolicy.STATELESS` |
| **`Overhead`** | Sobrecarga / Costo extra de procesamiento, memoria o red que introduce una herramienta. | *"Encrypting every log line adds too much CPU overhead"*. |

**Ejemplo en configuración de Spring Boot (`application.yml`) usando estos términos:**
```yaml
server:
  shutdown: graceful # Graceful Shutdown: no corta peticiones a la mitad al desplegar
spring:
  lifecycle:
    timeout-per-shutdown-phase: 30s
  datasource:
    hikari:
      maximum-pool-size: 20 # Evita un bottleneck de conexiones
      connection-timeout: 3000 # Evita alta latency si la BD se satura
```

---

## 9. Inglés para Git, Pull Requests (PRs) y Code Reviews

En casi todas las empresas de software los commits, las ramas de Git y los comentarios en los *Pull Requests / Merge Requests* se escriben en inglés:

### Convención de Commits (*Conventional Commits*)

| Prefijo del Commit | Significado | Ejemplo de Mensaje de Commit en Inglés |
|---|---|---|
| **`feat:`** (*Feature*) | Nueva funcionalidad para el usuario o negocio. | `feat(payments): add support for partial refunds` |
| **`fix:`** (*Bug fix*) | Corrección de un error en el código. | `fix(auth): prevent NullPointerException when token is expired` |
| **`refactor:`** | Mejora de la estructura del código sin cambiar cómo funciona por fuera. | `refactor(orders): extract tax calculation into a separate strategy` |
| **`perf:`** (*Performance*) | Cambio que mejora la velocidad o el uso de memoria. | `perf(catalog): add composite index on category and price` |
| **`test:`** | Agregar o corregir tests unitarios / de integración. | `test(wallet): add unit tests for concurrent transfers` |
| **`docs:`** | Cambios únicamente en documentación o archivos `.md`. | `docs(readme): update local Docker setup instructions` |
| **`chore:`** | Tareas de mantenimiento (actualizar dependencias en `pom.xml`, configurar CI/CD). | `chore(deps): bump spring-boot-starter-parent to 3.3.5` |

### Vocabulario de Pull Requests y Code Reviews

| Término / Frase | Significado | Ejemplo de Uso en GitHub / GitLab |
|---|---|---|
| **`WIP` (*Work In Progress*)** | Trabajo en progreso (el PR todavía no está terminado, no fusionar aún). | `WIP: feat(invoices): PDF generation` |
| **`LGTM` (*Looks Good To Me*)** | *"Me parece bien / Se ve bien para mí"* (se usa al aprobar el PR de un compañero). | *"LGTM! Great job handling the edge cases. 🚀"* |
| **`Nit` / `Nitpick`** | Detalle menor o sugerencia estética que no bloquea la aprobación del PR (ej. mejorar el nombre de una variable). | *"Nit: maybe rename `list` to `activeCustomers` for clarity?"* |
| **`Breaking Change`** | Cambio incompatible que rompe a los clientes o servicios que consumen nuestra API o librería. | *"BREAKING CHANGE: renamed field `user_id` to `customerId` in JSON response"*. |
| **`Resolve conflict`** | Resolver conflicto de fusión cuando dos personas tocaron las mismas líneas. | *"Please rebase onto `main` and resolve merge conflicts"*. |
| **`Cherry-pick`** | Traer un único commit específico de una rama a otra sin fusionar toda la rama. | *"Let's cherry-pick the hotfix commit into the release branch"*. |
| **`Revert` / `Rollback`** | Deshacer un cambio o volver a la versión anterior en producción porque algo falló. | *"We had to rollback the deployment due to a database migration error"*. |
| **`Workaround`** | Solución temporal o rodeo provisorio para zafar de un bug hasta que se haga el arreglo definitivo. | *"Added a temporary workaround until the third-party API fixes their bug"*. |

---

## 10. Inglés de Metodologías Ágiles (Scrum, Jira, Daily Standup)

Palabras que escucharás todos los días en las reuniones de equipo y tableros de Jira/Trello:

| Término Ágil | Significado | Ejemplo en una Reunión |
|---|---|---|
| **`Backlog`** | Lista priorizada de tareas e historias pendientes por hacer. | *"That feature is in the product backlog for next month"*. |
| **`User Story`** | Historia de usuario: requerimiento contado desde el punto de vista de quien lo usa (*"As a user, I want to... so that..."*). | *"As a seller, I want to export my sales to CSV so that I can send them to my accountant"*. |
| **`Acceptance Criteria (AC)`** | Criterios de aceptación: la lista de condiciones exactas que debe cumplir tu código para que la tarea se dé por terminada. | *"Did you check all the acceptance criteria in the Jira ticket?"* |
| **`Definition of Done (DoD)`** | Definición de Hecho: los requisitos de calidad del equipo (ej. código compilando, tests pasando, PR aprobado y desplegado en QA). | *"Unit tests with 80% coverage are part of our Definition of Done"*. |
| **`Blocker` / `Impediment`** | Bloqueante / Impedimento: algo que te impide avanzar con tu tarea y necesitas ayuda para destrabarlo. | *"I have a blocker: I don't have credentials for the sandbox API"*. |
| **`Scope Creep`** | Cuando una tarea pequeña empieza a crecer descontroladamente porque le siguen agregando pedidos nuevos sobre la marcha. | *"Let's avoid scope creep and move those extra requests to a new ticket"*. |
| **`Technical Debt` (*Tech Debt*)** | Deuda técnica: código que se hizo rápido para salir del paso y que en el futuro hay que limpiar o refactorizar para que no frene al equipo. | *"We need to pay down some tech debt in the legacy authentication module"*. |
| **`Spike`** | Tarea de investigación técnica con tiempo fijo (ej. 1 o 2 días) para probar una librería o diseñar una solución antes de estimar el desarrollo real. | *"Let's create a 1-day spike to test how Spring Batch handles 5 million rows"*. |
| **`Hotfix`** | Parche urgente que se hace directamente para arreglar un error crítico en producción. | *"We deployed a hotfix to fix the checkout timeout"*. |
| **`ETA` (*Estimated Time of Arrival*)** | Fecha u hora estimada en la que estará lista una tarea o resuelto un problema. | *"What is the ETA for the payment gateway fix?"* |

**Ejemplo de cómo escribir un Ticket / Historia de Usuario clara en inglés:**
```markdown
**Title:** Prevent duplicate payment charges on double-click

**User Story:**
As a customer, I want my payment to be processed only once even if I accidentally click the "Pay" button twice, so that I am not overcharged.

**Acceptance Criteria (AC):**
1. Given a valid `Idempotency-Key` header, when the same request is sent twice, the second request returns the cached result without charging again.
2. If the `Idempotency-Key` header is missing, the API returns `400 Bad Request`.
```

---

## 11. *Phrasal Verbs* Esenciales en Ingeniería de Software

Los *phrasal verbs* (verbo + preposición) cambian el significado del verbo y se usan constantemente en charlas técnicas y documentación:

| Phrasal Verb | Significado en Sistemas | Ejemplo Real en el Trabajo |
|---|---|---|
| **`spin up`** | Levantar / Iniciar rápidamente un servidor, contenedor Docker o base de datos temporal. | *"Testcontainers spins up a fresh PostgreSQL container for the tests"*. |
| **`tear down`** | Destruir / Limpiar los recursos creados al terminar una prueba o proceso. | *"Tear down the test environment after all tests finish"*. |
| **`roll out`** | Desplegar / Lanzar gradualmente una nueva funcionalidad a los usuarios en producción. | *"We will roll out the new checkout flow to 10% of users first"*. |
| **`roll back`** | Revertir / Volver atrás un despliegue o una transacción de base de datos a su estado previo. | *"Spring automatically rolls back the transaction if a RuntimeException is thrown"*. |
| **`narrow down`** | Acotar / Reducir las opciones hasta encontrar dónde está exactamente el bug. | *"I narrowed down the bug to the date formatting method"*. |
| **`dig into` / `dive into`** | Investigar a fondo / Meterse de lleno a analizar logs o código complejo. | *"Let me dig into the production logs to see why it failed"*. |
| **`walk through`** | Explicar o recorrer paso a paso un código o diseño con otra persona. | *"Can you walk me through how the authentication filter works?"* |
| **`opt in` / `opt out`** | Elegir participar (activar explícitamente) / Elegir darse de baja (desactivar). | *"Users must opt in to receive promotional emails"*. |
| **`fall back (on/to)`** | Recurrir a un plan B o valor por defecto cuando falla la opción principal. | *"If Redis is down, the service falls back to querying the database"*. |
| **`Scale up` / `Scale out` / `Scale down`** | Aumentar tamaño del servidor / Aumentar cantidad de servidores / Reducir servidores cuando baja el tráfico. | *"Auto-scaling scales down the pods at night to save costs"*. |
| **`phase out` / `sunset`** | Retirar de a poco / Dar de baja gradualmente una versión vieja de una API o sistema. | *"We are phasing out API v1 by the end of the quarter"*. |
| **`wire up`** | Conectar / Inyectar los componentes entre sí (de ahí viene `@Autowired` en Spring). | *"Spring wires up all the dependencies automatically at startup"*. |
| **`mock out` / `stub out`** | Reemplazar una llamada externa real por un objeto simulado en un test. | *"We mocked out the Stripe client in the unit tests"*. |

---

## 12. Falsos Amigos (*False Friends*) en Programación

Palabras en inglés técnico que suenan muy parecidas a una palabra en español, pero **significan otra cosa totalmente distinta**:

| Palabra en Inglés | ❌ Lo que parece que significa (Error común) | ✅ Lo que REALMENTE significa en Español | Ejemplo Correcto |
|---|---|---|---|
| **`actual` / `actually`** | ❌ Actual (del presente) | ✅ **Real / Efectivo** (*"En realidad"*).<br>*(Para decir "actual del momento presente" en inglés se dice **`current`**).* | En JUnit: `assertEquals(expected, actual)` = Compara el valor *esperado* contra el valor **real obtenido**. Para el usuario actual usa `currentUser` (nunca `actualUser`). |
| **`argument`** | ❌ Discusión / Pelea | ✅ **Argumento / Parámetro** que se le pasa a una función. | `IllegalArgumentException` = Excepción de parámetro inválido. |
| **`library`** | ❌ Librería donde se compran libros (*Bookstore*) | ✅ **Biblioteca de código** reutilizable (un `.jar` en Maven). | *"Jackson is a JSON parsing library"*. |
| **`consistent` / `consistency`** | ❌ Consistente de espesor | ✅ **Coherente / Sin contradicciones** en los datos. | *"Eventual consistency"* = Coherencia eventual de datos. |
| **`support`** | ❌ Soportar / Aguantar a alguien | ✅ **Dar soporte / Ser compatible con / Admitir**. | *"This method supports pagination"* = Este método admite paginación. |
| **`assume`** | ❌ Asumir un cargo | ✅ **Suponer / Dar por sentado** sin verificar. | *"Never assume the input list is sorted"*. |
| **`comprehensive`** | ❌ Comprensivo (empático) | ✅ **Exhaustivo / Completo / Que abarca todo**. | *"A comprehensive test suite"* = Una batería de pruebas súper completa. |
| **`deprecated`** | ❌ Depreciado (que bajó de precio contable) | ✅ **Obsoleto / Desaconsejado** en versiones nuevas. | `@Deprecated` = Método obsoleto que no debe usarse. |
| **`exit` / `success`** | ❌ `exit` = Éxito | ✅ `exit` = **Salir / Terminar proceso**.<br>**Éxito** se dice **`success`**. | `System.exit(0)` sale del programa; `isSuccess()` indica éxito. |
| **`large`** | ❌ Largo de longitud (*Long*) | ✅ **Grande** en tamaño o volumen. | `BLOB (Binary Large Object)` = Objeto binario grande. |
| **`fabric`** | ❌ Fábrica (*Factory*) | ✅ **Tejido / Red interconectada de infraestructura**. *(Fábrica de objetos se dice **`Factory`**).* | *"Service fabric"* vs `ConnectionFactory`. |

**Ejemplo clásico en JUnit (`expected` vs `actual` vs `current`):**
```java
@Test
void shouldUpdateCurrentBalance() {
    Account account = new Account(new BigDecimal("100.00"));
    account.deposit(new BigDecimal("50.00"));

    BigDecimal expectedBalance = new BigDecimal("150.00"); // Lo que esperamos que dé
    BigDecimal actualBalance = account.getCurrentBalance(); // El resultado REAL que devolvió el código hoy

    assertEquals(expectedBalance, actualBalance);
}
```

---

## 13. Abreviaturas y Acrónimos de Chat Técnico, Comentarios y Código

Siglas que verás constantemente en comentarios de código (`// TODO`), chats de Slack/Teams y documentación:

| Sigla | Qué significa en Inglés | Traducción y Uso Práctico | Ejemplo |
|---|---|---|---|
| **`TODO`** | *To Do* | **Por hacer:** Comentario en el código que marca algo pendiente de completar antes de terminar. | `// TODO: replace hardcoded timeout with config property` |
| **`FIXME`** | *Fix Me* | **Arréglame:** Marca en el código algo que se sabe que tiene un bug o falla en ciertos casos y requiere arreglo urgente. | `// FIXME: fails when the list contains null elements` |
| **`TBD` / `TBC`** | *To Be Determined / To Be Confirmed* | **Por definir / Por confirmar:** Se pone en un documento de diseño cuando un detalle aún no fue decidido por el negocio. | `Tax rate for international orders: TBD` |
| **`TL;DR`** | *Too Long; Didn't Read* | **Resumen ejecutivo en 1 o 2 líneas** al principio de un mensaje o documento largo. | `TL;DR: Database crashed because of an unindexed query; fixed in PR #412.` |
| **`FYI`** | *For Your Information* | **Para tu información:** Te comparten un dato útil sin que tengas que hacer ninguna acción. | `FYI: Staging environment will be under maintenance at 6 PM.` |
| **`AFAIK` / `IMO`** | *As Far As I Know / In My Opinion* | **Hasta donde yo sé / En mi opinión.** | `AFAIK, Stripe already validates the card expiration date.` |
| **`EOD` / `EOW`** | *End Of Day / End Of Week* | **Al final del día laboral / Al final de la semana.** | *"I will push the PR by EOD"*. |
| **`OOM`** | *Out Of Memory* | El servidor se quedó sin memoria RAM (`OutOfMemoryError`). | *"The pod crashed with an OOM error"*. |
| **`NPE`** | *NullPointerException* | Abreviatura universal en Java para `NullPointerException`. | *"Check for null to avoid an NPE here"*. |
| **`POJO`** | *Plain Old Java Object* | Clase Java común y simple (con atributos, getters y setters) sin ataduras a frameworks especiales. | Una clase de dominio simple. |
| **`EOF`** | *End Of File* | Fin del archivo (error común cuando un JSON queda cortado a la mitad: `Unexpected EOF`). | `JsonEOFException: Unexpected end-of-input` |
| **`AKA`** | *Also Known As* | **También conocido como.** | *"Spring IoC container, AKA ApplicationContext"*. |
| **`e.g.` vs `i.e.`** | *Exempli gratia* vs *Id est* | - **`e.g.` = Por ejemplo** (*for example*).<br>- **`i.e.` = Es decir / O sea** (*that is*). | *"Use a relational DB (e.g., PostgreSQL) for ACID transactions (i.e., atomic operations)"*. |

---

## 14. Cómo se Leen en Inglés los Símbolos del Código

Cuando estás en una llamada en vivo haciendo *Pair Programming* o viendo un video técnico en inglés, así es como se nombra cada símbolo del teclado:

| Símbolo | Nombre en Inglés | Significado en Español | Ejemplo al Hablar de Código |
|---|---|---|---|
| **`()`** | **Parentheses** *(abreviado: **Parens**)* | Paréntesis | `Open/close parens: method()` |
| **`{}`** | **Curly braces** / **Curly brackets** | Llaves | `Put the block inside curly braces { ... }` |
| **`[]`** | **Square brackets** | Corchetes | `Array square brackets: String[]` |
| **`<>`** | **Angle brackets** / **Diamond operator** | Paréntesis angulares / Operador diamante | `List<String> uses angle brackets` |
| **`:` / `;`** | **Colon** (`:`) / **Semicolon** (`;`) | Dos puntos (`:`) / Punto y coma (`;`) | *"Missing semicolon at the end of the line"* |
| **`'` / `"` / `` ` ``** | **Single quote** (`'`) / **Double quotes** (`"`) / **Backtick** (`` ` ``) | Comilla simple / Comillas dobles / Comilla invertida | *"Wrap the JSON key in double quotes"* |
| **`/` vs `\`** | **Forward slash** (`/`) vs **Backslash** (`\`) | Barra normal (`/`) vs Barra invertida (`\`) | *"Use forward slashes in URLs and file paths"* |
| **`_`** | **Underscore** | Guion bajo | `SNAKE_CASE_WITH_UNDERSCORES` |
| **`-`** | **Hyphen** / **Dash** / **Minus** | Guion medio / Menos | `kebab-case-with-hyphens` |
| **`@`** | **At sign** | Arroba | *"Add the 'at' Service annotation (@Service)"* |
| **`#`** | **Hash** / **Pound** | Numeral / Almohadilla | `hashCode()` |
| **`&` / `\|`** | **Ampersand** (`&`) / **Pipe** (`\|`) | Ampersand (`Y`) / Tubería (`O`) | `Double ampersand (&&) for logical AND` |
| **`!` / `~` / `^`** | **Exclamation mark (Bang)** (`!`) / **Tilde** (`~`) / **Caret** (`^`) | Signo de exclamación / Virgulilla / Acento circunflejo | *"Bang operator (!isValid) negates the boolean"* |
| **`->` / `::`** | **Arrow** (`->`) / **Double colon** (`::`) | Flecha lambda / Doble dos puntos (Method Reference) | `user -> user.getName()` o `User::getName` |
| **`camelCase` / `PascalCase` / `snake_case` / `kebab-case`** | Convenciones de escritura de identificadores | - `camelCase`: variables y métodos en Java.<br>- `PascalCase`: nombres de Clases.<br>- `snake_case`: columnas en SQL.<br>- `kebab-case`: propiedades en `application.yml` y URLs. | `miVariable` / `MiClase` / `mi_columna` / `mi-url-endpoint` |

---

## 15. Plantillas Prácticas Listas para Usar (Commits, PRs, Daily y Bugs)

Aquí tienes plantillas en inglés natural y profesional listas para copiar y adaptar en tu trabajo diario:

### 1. Plantilla para el Daily Standup (Qué hice ayer, qué haré hoy y bloqueos)
```text
- Yesterday: I finished implementing the payment webhook endpoint and added unit tests for the HMAC signature verification.
- Today: I am going to work on the database migration for the ledger table and open the Pull Request for review.
- Blockers: No blockers on my side. / I have a quick blocker: I need the sandbox API credentials from the DevOps team.
```

### 2. Plantilla para Describir un Pull Request (PR)
```markdown
## What does this PR do?
Implements idempotency for the `POST /api/v1/payments` endpoint using the `Idempotency-Key` header to prevent duplicate charges.

## Key Changes
- Added `idempotency_key` unique column to `payments` table.
- Created `IdempotencyService` to validate and return existing payment results when a duplicate request arrives.
- Added integration tests covering concurrent requests and missing headers.

## How to Test
1. Send a `POST /api/v1/payments` request with header `Idempotency-Key: test-123`.
2. Repeat the exact same request and verify it returns the original payment without calling the external gateway again.
```

### 3. Plantilla para Hacer Comentarios Amables en un Code Review
```markdown
- **Sugerir una mejora sin sonar agresivo:**
  "What do you think about extracting this validation logic into a separate private method to make `processOrder()` easier to read?"

- **Advertir sobre un posible bug o caso borde:**
  "Heads up: if `customer.getAddress()` is `null` here, this line will throw a `NullPointerException`. Could we add a null check or use `Optional`?"

- **Aprobar el PR:**
  "LGTM! Clean implementation and great test coverage. Approved!"
```

> **Tip de lectura:** Cuando leas código en inglés, divide cada identificador largo en sus partes gramaticales: **Verbo + Adjetivo + Sustantivo + Preposición** (ej. `find` + `Active` + `Subscriptions` + `By` + `CustomerId`). Con los verbos de la Sección 1, los prefijos booleanos de la Sección 2 y los sufijos de la Sección 3, podrás descifrar el propósito de cualquier clase o método de Java y Spring Boot en segundos aunque sea la primera vez que ves el proyecto.

