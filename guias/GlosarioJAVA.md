# Glosario de términos JAVA

Guía rápida para entender palabras, clases y métodos que aparecen al leer un proyecto Java.

## Índice
1. [Conceptos básicos](#1-conceptos-básicos)
2. [Palabras reservadas](#2-palabras-reservadas)
3. [Tipos de datos](#3-tipos-de-datos)
4. [Clases comunes del JDK](#4-clases-comunes-del-jdk)
5. [Colecciones](#5-colecciones)
6. [Métodos frecuentes](#6-métodos-frecuentes)
7. [Excepciones](#7-excepciones)
8. [Anotaciones](#8-anotaciones)
9. [Streams y programación funcional](#9-streams-y-programación-funcional)
10. [Maven y estructura de proyecto](#10-maven-y-estructura-de-proyecto)
11. [Spring y frameworks comunes](#11-spring-y-frameworks-comunes)
12. [Convenciones de nombres y sufijos](#12-convenciones-de-nombres-y-sufijos)
13. [Patrones de diseño](#13-patrones-de-diseño)
14. [Siglas](#14-siglas)

---

## 1. Conceptos básicos

| Término | Significado |
|---|---|
| **Clase (class)** | Plantilla que define atributos y comportamientos de un objeto. |
| **Objeto** | Instancia de una clase, creada con `new`. |
| **Atributo / Campo (field)** | Variable que pertenece a una clase u objeto. |
| **Método** | Función definida dentro de una clase. |
| **Constructor** | Método especial que inicializa un objeto; se llama igual que la clase y no tiene tipo de retorno. |
| **Instancia** | Objeto concreto creado a partir de una clase. |
| **Paquete (package)** | Carpeta lógica que agrupa clases (ej. `com.empresa.app`). |
| **Import** | Permite usar clases de otros paquetes sin escribir el nombre completo. |
| **Herencia** | Una clase hereda atributos y métodos de otra (`extends`). |
| **Polimorfismo** | Un mismo método se comporta distinto según el objeto que lo ejecuta. |
| **Encapsulamiento** | Ocultar datos internos (`private`) y exponerlos mediante getters/setters. |
| **Abstracción** | Mostrar solo lo esencial, ocultando detalles (clases abstractas e interfaces). |
| **Interfaz (interface)** | Contrato: define métodos que otras clases deben implementar. |
| **Clase abstracta** | Clase que no se puede instanciar; puede tener métodos sin implementar. |
| **Sobrecarga (overload)** | Varios métodos con el mismo nombre pero distintos parámetros. |
| **Sobreescritura (override)** | Una subclase redefine un método heredado. |
| **Clase interna (inner class)** | Clase declarada dentro de otra. |
| **Clase anónima** | Clase sin nombre, definida e instanciada en el mismo lugar. |
| **Lambda** | Función corta anónima: `(a, b) -> a + b`. |
| **Generics** | Tipos parametrizados: `List<String>`; el `<T>` es un tipo genérico. |
| **POJO** | Plain Old Java Object: clase simple con atributos, getters y setters. |
| **JavaBean** | POJO con constructor vacío, atributos privados y getters/setters. |
| **Record** | Clase inmutable compacta (Java 16+): `record Persona(String nombre, int edad) {}`. |
| **Enum** | Conjunto fijo de constantes: `enum Estado { ACTIVO, INACTIVO }`. |
| **Singleton** | Clase que solo permite una instancia. |
| **Inmutable** | Objeto cuyo estado no cambia tras crearse (ej. `String`). |
| **Null** | Ausencia de valor/referencia. Usarlo mal causa `NullPointerException`. |
| **Heap** | Memoria donde viven los objetos. |
| **Stack** | Memoria de llamadas a métodos y variables locales. |
| **Garbage Collector (GC)** | Proceso que libera automáticamente la memoria de objetos sin uso. |
| **JVM** | Java Virtual Machine: ejecuta el bytecode. |
| **JRE** | Java Runtime Environment: JVM + librerías para ejecutar. |
| **JDK** | Java Development Kit: JRE + herramientas para desarrollar (`javac`, etc.). |
| **Bytecode** | Código intermedio (`.class`) generado por el compilador. |
| **Thread (hilo)** | Flujo de ejecución independiente. |
| **Sincronización** | Control de acceso concurrente a recursos compartidos (`synchronized`). |
| **Serialización** | Convertir un objeto en bytes/texto para guardarlo o enviarlo. |
| **Reflection** | Inspeccionar o modificar clases en tiempo de ejecución. |
| **Classpath** | Ruta donde la JVM busca clases y librerías. |

## 2. Palabras reservadas

| Palabra | Significado |
|---|---|
| `public` | Accesible desde cualquier lugar. |
| `private` | Accesible solo dentro de la misma clase. |
| `protected` | Accesible en el mismo paquete y subclases. |
| (sin modificador) | *Package-private*: solo mismo paquete. |
| `static` | Pertenece a la clase, no a una instancia. |
| `final` | Constante (variable), no sobreescribible (método) o no heredable (clase). |
| `abstract` | Clase/método sin implementación completa. |
| `void` | El método no devuelve nada. |
| `new` | Crea un objeto. |
| `this` | Referencia al objeto actual. |
| `super` | Referencia a la clase padre. |
| `extends` | Hereda de una clase. |
| `implements` | Implementa una interfaz. |
| `interface` | Declara una interfaz. |
| `class` | Declara una clase. |
| `enum` | Declara una enumeración. |
| `return` | Devuelve un valor y termina el método. |
| `throw` | Lanza una excepción. |
| `throws` | Declara que un método puede lanzar una excepción. |
| `try / catch / finally` | Manejo de errores; `finally` siempre se ejecuta. |
| `instanceof` | Comprueba si un objeto es de cierto tipo. |
| `synchronized` | Bloquea el acceso concurrente. |
| `volatile` | La variable se lee siempre de memoria principal (hilos). |
| `transient` | Excluye el campo de la serialización. |
| `native` | Método implementado en otro lenguaje (C/C++). |
| `default` | Método con implementación en una interfaz; o caso por defecto en `switch`. |
| `var` | Inferencia de tipo local (Java 10+): `var x = 10;`. |
| `sealed / permits` | Restringe qué clases pueden heredar (Java 17+). |
| `package` | Declara el paquete de la clase. |
| `import` | Importa clases. |
| `assert` | Verifica una condición (solo si se activa con `-ea`). |

## 3. Tipos de datos

### Primitivos
| Tipo | Descripción | Rango / ejemplo |
|---|---|---|
| `byte` | Entero 8 bits | -128 a 127 |
| `short` | Entero 16 bits | -32 768 a 32 767 |
| `int` | Entero 32 bits | ≈ ±2 mil millones |
| `long` | Entero 64 bits | `10000000000L` |
| `float` | Decimal 32 bits | `3.14f` |
| `double` | Decimal 64 bits | `3.14` |
| `boolean` | Verdadero/falso | `true`, `false` |
| `char` | Un carácter | `'A'` |

### Wrappers (envoltorios)
`Integer`, `Long`, `Double`, `Float`, `Boolean`, `Character`, `Byte`, `Short`: versiones objeto de los primitivos. Permiten `null` y se usan en colecciones.

- **Autoboxing**: conversión automática primitivo → wrapper (`int` → `Integer`).
- **Unboxing**: wrapper → primitivo.

## 4. Clases comunes del JDK

| Clase | Para qué sirve |
|---|---|
| `Object` | Clase raíz: todas las clases heredan de ella. |
| `String` | Texto inmutable. |
| `StringBuilder` | Texto mutable y rápido (no thread-safe). |
| `StringBuffer` | Texto mutable thread-safe (más lento). |
| `Math` | Funciones matemáticas (`Math.max`, `Math.round`). |
| `System` | Entrada/salida estándar, propiedades, hora (`System.out.println`). |
| `Scanner` | Lee entrada del usuario o archivos. |
| `Random` | Números aleatorios. |
| `Optional<T>` | Contenedor que puede o no tener un valor (evita `null`). |
| `Objects` | Utilidades: `Objects.equals`, `Objects.requireNonNull`. |
| `Arrays` | Utilidades para arreglos (`sort`, `asList`, `toString`). |
| `Collections` | Utilidades para colecciones (`sort`, `unmodifiableList`). |
| `Thread` | Representa un hilo. |
| `Runnable` | Tarea ejecutable sin retorno (`run()`). |
| `Callable<T>` | Tarea que retorna valor (`call()`). |
| `ExecutorService` | Administra un pool de hilos. |
| `CompletableFuture` | Programación asíncrona encadenable. |
| `File` / `Path` / `Files` | Manejo de archivos y rutas. |
| `InputStream / OutputStream` | Flujos de bytes. |
| `Reader / Writer` | Flujos de caracteres. |
| `BufferedReader` | Lectura eficiente de texto línea a línea. |
| `LocalDate / LocalDateTime / LocalTime` | Fechas y horas modernas (`java.time`). |
| `Instant` | Momento exacto en el tiempo (UTC). |
| `Duration / Period` | Intervalos de tiempo. |
| `DateTimeFormatter` | Formatear/parsear fechas. |
| `Date / Calendar` | Fechas antiguas (legado, evitar). |
| `BigDecimal` | Decimales de precisión exacta (dinero). |
| `BigInteger` | Enteros de tamaño ilimitado. |
| `UUID` | Identificador único universal. |
| `Pattern / Matcher` | Expresiones regulares. |
| `Class<T>` | Representa una clase en tiempo de ejecución. |
| `Iterator<T>` | Recorre una colección (`hasNext()`, `next()`). |
| `Comparable<T>` | Define orden natural (`compareTo`). |
| `Comparator<T>` | Define un orden personalizado (`compare`). |
| `Logger` | Registro de mensajes (logs). |
| `Properties` | Pares clave-valor de configuración. |
| `Connection / Statement / PreparedStatement / ResultSet` | JDBC: conexión y consultas a base de datos. |

## 5. Colecciones

| Tipo | Descripción |
|---|---|
| `Collection` | Interfaz raíz de las colecciones. |
| `List` | Lista ordenada, permite duplicados. |
| `ArrayList` | Lista con arreglo dinámico; acceso rápido por índice. |
| `LinkedList` | Lista doblemente enlazada; rápida para insertar/eliminar en extremos. |
| `Set` | Sin duplicados. |
| `HashSet` | Set sin orden garantizado, muy rápido. |
| `LinkedHashSet` | Set que mantiene orden de inserción. |
| `TreeSet` | Set ordenado. |
| `Map<K,V>` | Pares clave → valor. |
| `HashMap` | Map sin orden, rápido. |
| `LinkedHashMap` | Map con orden de inserción. |
| `TreeMap` | Map ordenado por clave. |
| `ConcurrentHashMap` | Map seguro para múltiples hilos. |
| `Queue` | Cola (FIFO). |
| `Deque` | Cola de doble extremo. |
| `ArrayDeque` | Implementación eficiente de Deque/pila. |
| `PriorityQueue` | Cola con prioridad. |
| `Stack` | Pila (LIFO, legado). |
| `Vector` / `Hashtable` | Versiones antiguas sincronizadas (legado). |
| `Map.Entry` | Un par clave-valor dentro de un Map. |
| `List.of(...)`, `Set.of(...)`, `Map.of(...)` | Crean colecciones inmutables. |

## 6. Métodos frecuentes

### De `Object` (heredados por todos)
| Método | Significado |
|---|---|
| `toString()` | Representación en texto del objeto. |
| `equals(Object o)` | Compara si dos objetos son "iguales". |
| `hashCode()` | Número entero usado por colecciones hash; debe ser coherente con `equals`. |
| `getClass()` | Devuelve la clase del objeto. |
| `clone()` | Copia el objeto. |
| `finalize()` | Obsoleto; antes del GC. |
| `wait() / notify() / notifyAll()` | Coordinación entre hilos. |

### Generales
| Método | Significado |
|---|---|
| `main(String[] args)` | Punto de entrada del programa. |
| `getXxx()` | *Getter*: devuelve el valor de un atributo. |
| `setXxx(valor)` | *Setter*: asigna valor a un atributo. |
| `isXxx()` | Getter de booleanos. |
| `hasXxx()` | Pregunta si "tiene" algo (devuelve boolean). |
| `add / remove / get / set` | Agregar, eliminar, obtener, reemplazar en colecciones. |
| `size() / length()` | Cantidad de elementos (`length()` en String, `length` en arreglos). |
| `isEmpty()` | ¿Está vacío? |
| `contains(x)` | ¿Contiene el elemento? |
| `put / get / containsKey / getOrDefault` | Operaciones de Map. |
| `keySet() / values() / entrySet()` | Claves, valores o pares de un Map. |
| `forEach(...)` | Recorre aplicando una acción. |
| `sort(...)` | Ordena. |
| `clear()` | Vacía la colección. |
| `iterator()` | Obtiene un Iterator. |
| `valueOf(x)` | Convierte a un tipo (`Integer.valueOf("5")`). |
| `parseXxx(s)` | Convierte texto a número (`Integer.parseInt("5")`). |
| `compareTo(o)` | Compara (negativo, 0, positivo). |
| `substring / split / trim / replace` | Operaciones comunes de String. |
| `equalsIgnoreCase` | Compara Strings ignorando mayúsculas. |
| `format(...)` | Texto con formato (`String.format("%d", 5)`). |
| `close()` | Libera recursos (`AutoCloseable`). |
| `run()` | Cuerpo de un hilo / Runnable. |
| `start()` | Inicia un hilo (no usar `run()` directo). |
| `join()` | Espera a que termine un hilo. |
| `sleep(ms)` | Pausa el hilo. |
| `of(...)` | Método de fábrica estático (`List.of`, `LocalDate.of`). |
| `from(...)` / `copyOf(...)` | Crea a partir de otro objeto. |
| `build()` | Construye el objeto final (patrón Builder). |
| `builder()` | Obtiene un Builder. |
| `orElse / orElseGet / orElseThrow` | Valor alterno o excepción en `Optional`. |
| `ifPresent(...)` | Ejecuta algo si el Optional tiene valor. |
| `isPresent()` | ¿El Optional tiene valor? |

## 7. Excepciones

| Excepción | Significado |
|---|---|
| `Throwable` | Raíz de errores y excepciones. |
| `Exception` | Excepciones recuperables. |
| `RuntimeException` | Excepción **no verificada** (unchecked); no obliga a `try/catch`. |
| `Error` | Problemas graves de la JVM (no se suelen capturar). |
| **Checked** | Debe capturarse o declararse con `throws` (ej. `IOException`). |
| `NullPointerException` | Uso de una referencia `null`. |
| `ArrayIndexOutOfBoundsException` | Índice fuera del arreglo. |
| `IndexOutOfBoundsException` | Índice fuera de la lista. |
| `IllegalArgumentException` | Argumento inválido. |
| `IllegalStateException` | Objeto en estado inadecuado. |
| `ClassCastException` | Conversión de tipo inválida. |
| `NumberFormatException` | Texto no convertible a número. |
| `ArithmeticException` | Error aritmético (ej. división por cero). |
| `UnsupportedOperationException` | Operación no soportada (ej. modificar lista inmutable). |
| `ConcurrentModificationException` | Se modificó una colección mientras se recorría. |
| `IOException` | Error de entrada/salida. |
| `FileNotFoundException` | Archivo no encontrado. |
| `SQLException` | Error de base de datos. |
| `ClassNotFoundException` | Clase no encontrada en el classpath. |
| `InterruptedException` | Hilo interrumpido. |
| `StackOverflowError` | Recursión demasiado profunda. |
| `OutOfMemoryError` | Sin memoria disponible. |
| `try-with-resources` | `try (Recurso r = ...) {}` cierra el recurso automáticamente. |
| **Stack trace** | Lista de llamadas que llevó al error. |

## 8. Anotaciones

Las anotaciones (`@Algo`) añaden metadatos al código.

### Del lenguaje
| Anotación | Significado |
|---|---|
| `@Override` | Indica que el método sobreescribe uno del padre. |
| `@Deprecated` | Marcado como obsoleto. |
| `@SuppressWarnings` | Oculta advertencias del compilador. |
| `@FunctionalInterface` | Interfaz con un único método abstracto (usable con lambdas). |
| `@SafeVarargs` | Suprime avisos de varargs genéricos. |

### Lombok (reduce código repetitivo)
| Anotación | Genera |
|---|---|
| `@Getter / @Setter` | Getters y setters. |
| `@ToString` | `toString()`. |
| `@EqualsAndHashCode` | `equals()` y `hashCode()`. |
| `@NoArgsConstructor` | Constructor vacío. |
| `@AllArgsConstructor` | Constructor con todos los campos. |
| `@RequiredArgsConstructor` | Constructor con campos `final`/`@NonNull`. |
| `@Data` | Getter, setter, toString, equals, hashCode y constructor requerido. |
| `@Builder` | Patrón Builder. |
| `@Slf4j` | Crea un `log` listo para usar. |
| `@Value` | Versión inmutable de `@Data`. |

### JPA / Hibernate (base de datos)
| Anotación | Significado |
|---|---|
| `@Entity` | Clase mapeada a una tabla. |
| `@Table(name="...")` | Nombre de la tabla. |
| `@Id` | Clave primaria. |
| `@GeneratedValue` | Valor autogenerado (ID). |
| `@Column` | Configura una columna. |
| `@OneToMany / @ManyToOne / @OneToOne / @ManyToMany` | Relaciones entre entidades. |
| `@JoinColumn` | Columna de la clave foránea. |
| `@Transactional` | Ejecuta el método dentro de una transacción. |
| `@Query` | Consulta personalizada. |
| `@Enumerated` | Cómo guardar un enum. |

### Validación
`@NotNull`, `@NotBlank`, `@NotEmpty`, `@Size`, `@Min`, `@Max`, `@Email`, `@Pattern`, `@Valid`.

### Testing
| Anotación | Significado |
|---|---|
| `@Test` | Método de prueba. |
| `@BeforeEach / @AfterEach` | Se ejecuta antes/después de cada test. |
| `@BeforeAll / @AfterAll` | Una vez antes/después de todos los tests. |
| `@Mock` | Objeto simulado (Mockito). |
| `@InjectMocks` | Inyecta los mocks en el objeto a probar. |
| `@SpringBootTest` | Levanta el contexto de Spring para pruebas. |
| `@Disabled` | Desactiva un test. |
| `@ParameterizedTest` | Test con varios conjuntos de datos. |

## 9. Streams y programación funcional

| Término | Significado |
|---|---|
| `Stream<T>` | Secuencia de elementos procesable en cadena. |
| `stream()` | Crea un stream desde una colección. |
| `filter(...)` | Filtra elementos según una condición. |
| `map(...)` | Transforma cada elemento. |
| `flatMap(...)` | Transforma y "aplana" listas anidadas. |
| `collect(...)` | Reúne el resultado (ej. `Collectors.toList()`). |
| `Collectors` | `toList`, `toSet`, `toMap`, `groupingBy`, `joining`, `counting`. |
| `reduce(...)` | Combina todos los elementos en uno. |
| `sorted()` | Ordena. |
| `distinct()` | Quita duplicados. |
| `limit(n) / skip(n)` | Toma / omite los primeros n. |
| `count()` | Cuenta elementos. |
| `anyMatch / allMatch / noneMatch` | Verifica condiciones. |
| `findFirst / findAny` | Obtiene un elemento (en `Optional`). |
| `peek(...)` | Observa elementos (útil para debug). |
| `toList()` | Lista inmutable (Java 16+). |
| `parallelStream()` | Procesamiento en paralelo. |
| **Operación intermedia** | Devuelve otro stream (`filter`, `map`); es perezosa. |
| **Operación terminal** | Produce resultado y cierra el stream (`collect`, `count`). |
| `Function<T,R>` | Recibe T, devuelve R (`apply`). |
| `Predicate<T>` | Recibe T, devuelve boolean (`test`). |
| `Consumer<T>` | Recibe T, no devuelve nada (`accept`). |
| `Supplier<T>` | No recibe nada, devuelve T (`get`). |
| `BiFunction` / `BiConsumer` / `BiPredicate` | Versiones con dos parámetros. |
| `UnaryOperator<T>` / `BinaryOperator<T>` | Function donde entrada y salida son del mismo tipo. |
| `Clase::metodo` | **Referencia a método**: `String::toUpperCase`. |

## 10. Maven y estructura de proyecto

| Término | Significado |
|---|---|
| **Maven** | Herramienta de construcción y gestión de dependencias. |
| `pom.xml` | Archivo de configuración del proyecto Maven. |
| `groupId` | Identificador de la organización (ej. `com.empresa`). |
| `artifactId` | Nombre del proyecto/módulo. |
| `version` | Versión (`1.0-SNAPSHOT` = en desarrollo). |
| `packaging` | Tipo de salida: `jar`, `war`, `pom`. |
| `dependency` | Librería externa requerida. |
| `scope` | Alcance: `compile`, `test`, `provided`, `runtime`. |
| `plugin` | Extensión de Maven (compilar, empaquetar, etc.). |
| `properties` | Variables del pom (ej. versión de Java). |
| `parent` | pom del que se heredan configuraciones. |
| `mvn clean` | Borra la carpeta `target`. |
| `mvn compile` | Compila el código. |
| `mvn test` | Ejecuta pruebas. |
| `mvn package` | Genera el `.jar`/`.war`. |
| `mvn install` | Instala en el repositorio local. |
| Gradle (`build.gradle`) | Alternativa a Maven. |
| JAR | Archivo comprimido de clases Java. |
| WAR | Archivo para aplicaciones web. |

### Estructura típica
```
proyecto/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/         ← código fuente
│   │   └── resources/    ← configuración (application.properties, etc.)
│   └── test/
│       └── java/         ← pruebas
└── target/               ← archivos compilados (generado, no se edita)
```

## 11. Spring y frameworks comunes

| Término | Significado |
|---|---|
| **Spring Framework** | Framework para aplicaciones empresariales. |
| **Spring Boot** | Spring con configuración automática y servidor embebido. |
| **IoC** | Inversión de Control: el framework crea y gestiona los objetos. |
| **DI** | Inyección de Dependencias: el framework entrega las dependencias a la clase. |
| **Bean** | Objeto administrado por Spring. |
| **ApplicationContext** | Contenedor que contiene los beans. |
| `@SpringBootApplication` | Clase principal de arranque. |
| `@Component` | Clase genérica administrada por Spring. |
| `@Service` | Capa de lógica de negocio. |
| `@Repository` | Capa de acceso a datos. |
| `@Controller` | Controlador web (devuelve vistas). |
| `@RestController` | Controlador que devuelve JSON/datos. |
| `@Configuration` | Clase de configuración. |
| `@Bean` | Método que produce un bean. |
| `@Autowired` | Inyecta una dependencia. |
| `@Value("${...}")` | Inyecta un valor de configuración. |
| `@Qualifier` | Elige entre varios beans del mismo tipo. |
| `@Primary` | Bean preferido por defecto. |
| `@Scope` | Ciclo de vida del bean (`singleton`, `prototype`). |
| `@Profile` | Activa beans según el entorno. |
| `@RequestMapping` | Mapea una URL a un método/clase. |
| `@GetMapping / @PostMapping / @PutMapping / @DeleteMapping / @PatchMapping` | Mapean cada verbo HTTP. |
| `@PathVariable` | Valor tomado de la URL (`/users/{id}`). |
| `@RequestParam` | Parámetro de consulta (`?nombre=x`). |
| `@RequestBody` | Cuerpo de la petición convertido a objeto. |
| `@ResponseBody` | El retorno se escribe directo en la respuesta. |
| `@ResponseStatus` | Define el código HTTP. |
| `@ControllerAdvice` / `@ExceptionHandler` | Manejo global de excepciones. |
| `@EnableAutoConfiguration` | Activa la configuración automática. |
| `@Scheduled` | Tarea programada. |
| `@Async` | Ejecuta el método de forma asíncrona. |
| `@Cacheable` | Cachea el resultado del método. |
| `ResponseEntity<T>` | Respuesta HTTP completa (cuerpo, estado, cabeceras). |
| `JpaRepository` | Interfaz con operaciones CRUD listas. |
| `CrudRepository` | CRUD básico. |
| `findById / findAll / save / delete / existsById / count` | Métodos CRUD típicos. |
| `Hibernate` | ORM: mapea objetos a tablas. |
| `JPA` | Estándar de persistencia en Java. |
| `ORM` | Object-Relational Mapping. |
| `JDBC` | API estándar para bases de datos. |
| `application.properties / application.yml` | Archivo de configuración de Spring Boot. |
| `Mockito` | Librería para crear mocks en pruebas. |
| `JUnit` | Framework de pruebas unitarias. |
| `Jackson` | Convierte objetos ↔ JSON (`ObjectMapper`). |
| `SLF4J / Logback / Log4j` | Librerías de logging. |
| `Servlet` | Clase Java que atiende peticiones HTTP. |
| `Tomcat` | Servidor web/contenedor de servlets. |
| `JSP` | Páginas web con Java (antiguo). |
| `Swing / JavaFX` | Librerías para interfaces gráficas de escritorio. |

## 12. Convenciones de nombres y sufijos

### Estilo
| Elemento | Convención | Ejemplo |
|---|---|---|
| Clases / Interfaces | `PascalCase` | `UsuarioService` |
| Métodos / variables | `camelCase` | `buscarPorId` |
| Constantes | `MAYUSCULAS_CON_GUION` | `MAX_INTENTOS` |
| Paquetes | minúsculas | `com.empresa.app` |

### Sufijos habituales en nombres de clase
| Sufijo | Significado |
|---|---|
| `...Service` | Lógica de negocio. |
| `...ServiceImpl` | Implementación de una interfaz `Service`. |
| `...Controller` | Recibe peticiones (web/API). |
| `...Repository` / `...DAO` | Acceso a base de datos. |
| `...Entity` / `...Model` | Clase que representa una tabla o dato. |
| `...DTO` | Data Transfer Object: transporta datos entre capas. |
| `...VO` | Value Object: objeto de valor inmutable. |
| `...Request` / `...Response` | Datos de entrada / salida de una API. |
| `...Mapper` / `...Converter` | Convierte entre tipos (Entity ↔ DTO). |
| `...Util` / `...Utils` / `...Helper` | Métodos estáticos de apoyo. |
| `...Config` / `...Configuration` | Configuración. |
| `...Exception` | Excepción personalizada. |
| `...Handler` | Maneja eventos o errores. |
| `...Factory` | Crea objetos (patrón Factory). |
| `...Builder` | Construye objetos paso a paso. |
| `...Listener` / `...Observer` | Reacciona a eventos. |
| `...Filter` / `...Interceptor` | Intercepta peticiones. |
| `...Adapter` | Adapta una interfaz a otra. |
| `...Strategy` | Algoritmo intercambiable. |
| `...Provider` | Suministra recursos/datos. |
| `...Manager` | Coordina varias operaciones. |
| `...Validator` | Valida datos. |
| `...Client` | Consume un servicio externo. |
| `...Test` / `...Tests` | Clase de pruebas. |
| `...Application` / `...Main` | Clase de arranque. |
| `I...` (ej. `IUsuario`) | Prefijo de interfaz (estilo antiguo). |
| `Abstract...` / `Base...` | Clase abstracta base. |

### Capas típicas de una aplicación
```
Controller  →  Service  →  Repository  →  Base de datos
   (HTTP)     (negocio)    (datos)
        usan DTO / Entity para mover los datos
```

## 13. Patrones de diseño

| Patrón | Idea |
|---|---|
| **Singleton** | Una sola instancia. |
| **Factory** | Un método/clase decide qué objeto crear. |
| **Builder** | Construir objetos complejos paso a paso. |
| **Prototype** | Clonar objetos existentes. |
| **Adapter** | Hace compatibles dos interfaces. |
| **Decorator** | Añade funcionalidad envolviendo un objeto. |
| **Facade** | Interfaz simple para un sistema complejo. |
| **Proxy** | Intermediario que controla el acceso a un objeto. |
| **Observer** | Los suscriptores reciben aviso de cambios. |
| **Strategy** | Cambiar el algoritmo en ejecución. |
| **Template Method** | Esqueleto de algoritmo con pasos redefinibles. |
| **Command** | Encapsula una acción como objeto. |
| **Iterator** | Recorrer elementos sin exponer su estructura. |
| **MVC** | Modelo-Vista-Controlador: separa datos, interfaz y lógica. |
| **DAO** | Data Access Object: aísla el acceso a datos. |
| **Dependency Injection** | Las dependencias se entregan desde fuera. |

## 14. Siglas

| Sigla | Significado |
|---|---|
| **API** | Application Programming Interface. |
| **REST** | Estilo de API web basado en HTTP. |
| **HTTP** | Protocolo web (GET, POST, PUT, DELETE...). |
| **JSON** | Formato de datos ligero (`{"clave": "valor"}`). |
| **XML** | Formato de datos con etiquetas. |
| **CRUD** | Create, Read, Update, Delete. |
| **SQL** | Lenguaje de consultas de bases de datos. |
| **URL / URI** | Dirección de un recurso. |
| **IDE** | Entorno de desarrollo (IntelliJ, Eclipse, NetBeans, VS Code). |
| **SDK** | Software Development Kit. |
| **LTS** | Long Term Support (Java 8, 11, 17, 21...). |
| **TDD** | Test-Driven Development. |
| **CI/CD** | Integración y despliegue continuos. |
| **SOLID** | 5 principios de diseño orientado a objetos. |
| **DRY** | Don't Repeat Yourself. |
| **KISS** | Keep It Simple, Stupid. |
| **OOP / POO** | Programación Orientada a Objetos. |
| **TODO / FIXME** | Comentarios que marcan trabajo pendiente / código a corregir. |

---

> **Tip de lectura:** al ver una clase desconocida, mira primero su **paquete** (`import`), su **sufijo** y sus **anotaciones**; suelen decirte su rol (por ejemplo `@Service` + `UsuarioServiceImpl` = lógica de negocio de usuarios).

