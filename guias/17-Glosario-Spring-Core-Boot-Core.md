# Glosario Spring Core y Spring Boot Core

Términos del **núcleo de Spring** (contenedor IoC, beans, inyección de dependencias, AOP, eventos) y del **núcleo de Spring Boot** (auto-configuración, propiedades, perfiles, logging, arranque y empaquetado). Es la base para entender cualquier proyecto Spring.

## Índice

**Parte A: Spring Core**
1. [Conceptos generales](#1-conceptos-generales)
2. [Contenedor IoC y ApplicationContext](#2-contenedor-ioc-y-applicationcontext)
3. [Beans: definición y estereotipos](#3-beans-definición-y-estereotipos)
4. [Inyección de dependencias](#4-inyección-de-dependencias)
5. [Resolver ambigüedades: @Qualifier, @Primary](#5-resolver-ambigüedades-qualifier-primary)
6. [Scopes de un bean](#6-scopes-de-un-bean)
7. [Ciclo de vida de un bean](#7-ciclo-de-vida-de-un-bean)
8. [Configuración Java: @Configuration y @Bean](#8-configuración-java-configuration-y-bean)
9. [Carga condicional: @Conditional y @Profile](#9-carga-condicional-conditional-y-profile)
10. [Propiedades y Environment](#10-propiedades-y-environment)
11. [Eventos](#11-eventos)
12. [AOP: programación orientada a aspectos](#12-aop-programación-orientada-a-aspectos)
13. [SpEL y recursos](#13-spel-y-recursos)
14. [Tareas asíncronas, programadas y caché](#14-tareas-asíncronas-programadas-y-caché)
15. [Validación, conversión e i18n](#15-validación-conversión-e-i18n)
16. [Excepciones comunes del contenedor](#16-excepciones-comunes-del-contenedor)

**Parte B: Spring Boot Core**

17. [Qué es Spring Boot](#17-qué-es-spring-boot)
18. [Starters y gestión de dependencias](#18-starters-y-gestión-de-dependencias)
19. [La clase principal y el arranque](#19-la-clase-principal-y-el-arranque)
20. [Auto-configuración](#20-auto-configuración)
21. [Propiedades de configuración](#21-propiedades-de-configuración)
22. [Perfiles](#22-perfiles)
23. [Logging](#23-logging)
24. [Actuator y monitoreo](#24-actuator-y-monitoreo)
25. [DevTools y herramientas de desarrollo](#25-devtools-y-herramientas-de-desarrollo)
26. [Empaquetado, ejecución y despliegue](#26-empaquetado-ejecución-y-despliegue)
27. [Arranque: hooks y ciclo de vida](#27-arranque-hooks-y-ciclo-de-vida)
28. [Propiedades más usadas (referencia)](#28-propiedades-más-usadas-referencia)
29. [Ejemplo completo](#29-ejemplo-completo)
30. [Errores frecuentes de arranque](#30-errores-frecuentes-de-arranque)

---

# PARTE A: SPRING CORE

## 1. Conceptos generales

| Término | Significado |
|---|---|
| **Spring Framework** | Framework de Java para construir aplicaciones; su núcleo es un contenedor que crea y conecta objetos. |
| **Spring Core** | Módulo base: contenedor IoC, beans, DI, recursos, eventos, SpEL, AOP. |
| **IoC (Inversion of Control)** | Inversión de control: **no creas tú los objetos con `new`**; el framework los crea y gestiona. |
| **DI (Dependency Injection)** | Forma concreta de IoC: el contenedor **entrega** a cada clase los objetos que necesita. |
| **Dependencia** | Objeto que otra clase necesita para funcionar (un `Service` depende de un `Repository`). |
| **Acoplamiento** | Qué tan atada está una clase a otra. DI lo reduce. |
| **Bean** | Objeto creado y gestionado por el contenedor de Spring. |
| **Contenedor (container)** | Componente que crea, configura y administra los beans. |
| **ApplicationContext** | El contenedor de Spring (interfaz principal). |
| **Metadatos de configuración** | Información que le dice al contenedor qué beans crear: anotaciones, clases Java o XML. |
| **POJO** | Plain Old Java Object: clase Java simple, sin heredar de nada del framework. Spring trabaja con POJOs. |
| **Programación contra interfaces** | Depender de una interfaz, no de una clase concreta; permite cambiar la implementación. |
| **Component scanning** | Búsqueda automática de clases anotadas (`@Component`, etc.) en los paquetes. |
| **Autowiring (cableado automático)** | Spring resuelve y conecta solo las dependencias por tipo. |
| **Proxy** | Objeto intermediario que Spring crea alrededor de un bean para añadir comportamiento (transacciones, seguridad, async). |
| **AOP** | Añadir lógica transversal (logs, transacciones) sin tocar el código de negocio. |
| **Aspecto transversal (cross-cutting concern)** | Funcionalidad que atraviesa muchas clases: logging, seguridad, transacciones, auditoría. |
| **Reflection** | Capacidad de Java de inspeccionar/instanciar clases en ejecución; Spring la usa mucho. |
| **Anotación** | Metadato (`@Algo`) que Spring lee para saber qué hacer. |
| **Convención sobre configuración** | Valores por defecto razonables para escribir menos configuración. |
| **Inmutabilidad** | Objetos cuyo estado no cambia; se logra con `final` e inyección por constructor. |
| **Singleton (de Spring)** | Una única instancia por contenedor (no es el patrón estricto, es "una por ApplicationContext"). |
| **Dependency Lookup** | Pedir un bean al contenedor manualmente (`getBean`), en contraste con DI. |
| **Container-managed** | Todo objeto con ciclo de vida controlado por el contenedor. |

### Sin Spring vs con Spring
```java
// SIN Spring: tú creas y conectas todo (alto acoplamiento)
public class PedidoService {
    private PedidoRepository repo = new PedidoRepositoryJdbc();   // atado a una implementación
}

// CON Spring: declaras lo que necesitas y el contenedor lo entrega
@Service
public class PedidoService {
    private final PedidoRepository repo;                          // interfaz
    public PedidoService(PedidoRepository repo) { this.repo = repo; }   // Spring inyecta
}
```

### Idea central
```
Tu código declara  →  "necesito un PedidoRepository"
El contenedor      →  busca/crea el bean adecuado y lo inyecta
Resultado          →  clases desacopladas, fáciles de probar y de cambiar
```

## 2. Contenedor IoC y ApplicationContext

| Término | Significado |
|---|---|
| `BeanFactory` | Contenedor básico (carga perezosa). Interfaz raíz. |
| `ApplicationContext` | `BeanFactory` + eventos, i18n, recursos, AOP, integración. **El que se usa siempre.** |
| `AnnotationConfigApplicationContext` | Contexto configurado con clases Java/anotaciones. |
| `ClassPathXmlApplicationContext` | Contexto configurado con XML (legado). |
| `AnnotationConfigServletWebServerApplicationContext` | El que crea Spring Boot para apps web. |
| `ConfigurableApplicationContext` | Contexto con operaciones de ciclo de vida (`close()`, `refresh()`). |
| `getBean(Clase.class)` | Obtiene un bean por tipo. |
| `getBean("nombre")` | Obtiene un bean por nombre. |
| `getBeansOfType(Clase.class)` | Mapa nombre → bean de todos los de ese tipo. |
| `getBeanDefinitionNames()` | Nombres de todos los beans registrados. |
| `containsBean("x")` | ¿Existe ese bean? |
| `ApplicationContextAware` | Interfaz para recibir el contexto en un bean. |
| `BeanFactoryAware` / `BeanNameAware` | Reciben la factory / su propio nombre. |
| `BeanDefinition` | Descripción de un bean (clase, scope, dependencias). |
| `BeanDefinitionRegistry` | Registro donde se añaden definiciones. |
| `BeanFactoryPostProcessor` | Modifica **definiciones** de beans antes de crear instancias. |
| `BeanPostProcessor` | Modifica **instancias** de beans al crearse (así funcionan `@Autowired`, `@Transactional`, AOP). |
| `refresh()` | Arranca/recarga el contenedor. |
| `close()` | Cierra el contexto y destruye los beans. |
| **Context hierarchy** | Contextos padre-hijo (típico: raíz y servlet en Spring MVC antiguo). |
| `ObjectProvider<T>` | Obtención perezosa/opcional de un bean. |
| `Provider<T>` / `@Lookup` | Alternativas para obtener un bean cada vez (útil con scope prototype). |

### Ejemplos
```java
// Obtener el contexto desde main en Spring Boot
ConfigurableApplicationContext ctx = SpringApplication.run(MiApp.class, args);
PedidoService servicio = ctx.getBean(PedidoService.class);
String[] nombres = ctx.getBeanDefinitionNames();
Arrays.stream(nombres).sorted().forEach(System.out::println);   // ver todos los beans
ctx.close();

// Contexto manual (sin Boot)
ApplicationContext ctx2 = new AnnotationConfigApplicationContext(AppConfig.class);
Saludo s = ctx2.getBean(Saludo.class);

// Inyectar el propio contexto (poco recomendado)
@Component
public class Util implements ApplicationContextAware {
    private ApplicationContext ctx;
    @Override
    public void setApplicationContext(ApplicationContext ctx) { this.ctx = ctx; }
}
// Más moderno: private final ApplicationContext ctx;  (constructor)

// ObjectProvider: bean opcional o perezoso
@Service
public class ReporteService {
    private final ObjectProvider<Exportador> exportador;
    public ReporteService(ObjectProvider<Exportador> exportador) { this.exportador = exportador; }

    public void generar() {
        Exportador e = exportador.getIfAvailable(() -> new ExportadorPorDefecto());
        e.exportar();
    }
}
```
```java
// BeanPostProcessor: se ejecuta para CADA bean
@Component
public class LogBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String name) {
        System.out.println("Creando: " + name);
        return bean;
    }
}
```

## 3. Beans: definición y estereotipos

| Anotación | Significado |
|---|---|
| `@Component` | Marca una clase como bean genérico detectable por component scan. |
| `@Service` | Bean de la capa de **lógica de negocio**. |
| `@Repository` | Bean de **acceso a datos**; además traduce excepciones de BD a `DataAccessException`. |
| `@Controller` | Bean de **controlador web** MVC (devuelve vistas). |
| `@RestController` | `@Controller` + `@ResponseBody` (devuelve JSON). |
| `@Configuration` | Clase que **declara beans** mediante métodos `@Bean`. |
| `@Bean` | Método que **produce** un bean (para clases que no puedes anotar, como las de librerías). |
| `@ControllerAdvice` / `@RestControllerAdvice` | Manejo global de excepciones del web. |
| `@ConfigurationProperties` | Bean que agrupa propiedades de configuración. |
| `@ComponentScan` | Indica qué paquetes escanear. |
| `@Import` | Importa otras clases de configuración. |
| `@ImportResource` | Importa configuración XML. |
| `@Lazy` | El bean se crea solo cuando se usa por primera vez. |
| `@DependsOn("otro")` | Fuerza a que otro bean se cree antes. |
| `@Order(n)` / `Ordered` | Define el orden entre beans del mismo tipo (listas, filtros, aspectos). |
| `@Role` | Rol del bean (aplicación/infraestructura). |
| `@Description` | Descripción del bean. |
| **Estereotipo** | Anotación que indica el rol de una clase (`@Service`, `@Repository`...). Todas son `@Component` por debajo. |
| **Nombre del bean** | Por defecto, el nombre de la clase en camelCase (`PedidoService` → `pedidoService`). En `@Bean`, el nombre del método. |
| **Meta-anotación** | Anotación que se compone de otras (ej. `@SpringBootApplication`). |

### Diferencia entre estereotipos
| Anotación | Capa | Comportamiento extra |
|---|---|---|
| `@Component` | Cualquiera | Ninguno |
| `@Service` | Negocio | Ninguno (solo semántica) |
| `@Repository` | Datos | Traducción de excepciones |
| `@Controller` | Web | Habilita mapeos MVC |
| `@RestController` | Web/API | `@ResponseBody` automático |

### `@Component` vs `@Bean`
| | `@Component` (y estereotipos) | `@Bean` |
|---|---|---|
| Dónde va | Sobre la **clase** | Sobre un **método** en `@Configuration` |
| Detección | Automática (component scan) | Explícita |
| Cuándo usar | Tus propias clases | Clases de terceros o creación con lógica |

### Ejemplos
```java
@Component
public class GeneradorCodigo {
    public String nuevo() { return UUID.randomUUID().toString(); }
}

@Service("clienteSrv")                              // nombre personalizado
public class ClienteService { ... }

@Repository
public class ClienteDaoJdbc implements ClienteDao { ... }

// @Lazy: no se crea al arrancar
@Component
@Lazy
public class ServicioPesado { ... }

// @DependsOn
@Component
@DependsOn("inicializadorBd")
public class CargadorDatos { ... }

// Escanear paquetes distintos al de la clase principal
@SpringBootApplication(scanBasePackages = {"com.empresa.app", "com.empresa.comun"})
public class MiApp { ... }

// Meta-anotación propia
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Service
@Transactional
public @interface ServicioTransaccional {}
```

### Regla del component scan
Spring escanea el **paquete de la clase `@SpringBootApplication` y sus subpaquetes**. Una clase fuera de ese árbol **no se detecta**:
```
com.empresa.app           ← MiApp (@SpringBootApplication)
├── service/              ✓ detectado
├── repository/           ✓ detectado
com.empresa.otro          ✗ NO detectado (hay que usar scanBasePackages o @Import)
```

## 4. Inyección de dependencias

| Término | Significado |
|---|---|
| `@Autowired` | Pide a Spring que inyecte la dependencia por **tipo**. |
| `required = false` | La dependencia es opcional. |
| **Inyección por constructor** | **Recomendada.** Las dependencias llegan al crear el objeto; permite `final`. Si hay un único constructor, `@Autowired` es opcional. |
| **Inyección por setter** | Mediante un método `setX`; para dependencias opcionales/cambiables. |
| **Inyección por campo** | `@Autowired` sobre el atributo. Corta, pero **no recomendada** (dificulta tests, oculta dependencias). |
| `@Inject` (JSR-330) | Equivalente a `@Autowired` (`jakarta.inject`). |
| `@Resource` (JSR-250) | Inyección **por nombre** primero. |
| `@Value` | Inyecta valores simples/propiedades/SpEL. |
| `@RequiredArgsConstructor` (Lombok) | Genera el constructor con los campos `final` → inyección por constructor sin escribirlo. |
| `Optional<T>` | Inyección opcional. |
| `List<T>` / `Set<T>` / `Map<String,T>` | Inyecta **todos** los beans de ese tipo. |
| `@Autowired(required=false)` / `@Nullable` | Permite que sea `null`. |
| `ObjectProvider<T>` | Obtención perezosa. |
| `@Lookup` | Método que devuelve un bean nuevo en cada llamada. |
| **Dependencia circular** | A depende de B y B de A: Spring no puede crearlas (error). |
| `@Lazy` en inyección | Rompe ciclos inyectando un proxy perezoso. |
| `spring.main.allow-circular-references` | Permite ciclos (no recomendado). |

### Los tres tipos de inyección
```java
// 1. CONSTRUCTOR (recomendada)
@Service
public class PedidoService {
    private final PedidoRepository repo;
    private final EmailService email;

    public PedidoService(PedidoRepository repo, EmailService email) {  // sin @Autowired si es 1 solo constructor
        this.repo = repo;
        this.email = email;
    }
}

// Lo mismo con Lombok
@Service
@RequiredArgsConstructor
public class PedidoService {
    private final PedidoRepository repo;
    private final EmailService email;
}

// 2. SETTER (dependencias opcionales)
@Service
public class ReporteService {
    private Exportador exportador;

    @Autowired(required = false)
    public void setExportador(Exportador exportador) { this.exportador = exportador; }
}

// 3. CAMPO (evitar)
@Service
public class MalEjemplo {
    @Autowired
    private PedidoRepository repo;      // no se puede hacer final; difícil de testear sin Spring
}
```

### ¿Por qué constructor?
| Ventaja | Detalle |
|---|---|
| Campos `final` | Inmutabilidad, el objeto nunca queda a medio construir. |
| Fácil de testear | `new PedidoService(mockRepo, mockEmail)` sin Spring. |
| Dependencias visibles | El constructor muestra qué necesita la clase. |
| Detecta ciclos al arrancar | En vez de fallar en ejecución. |

### Inyectar colecciones y opcionales
```java
public interface Notificador { void enviar(String msg); }

@Component class EmailNotificador implements Notificador { ... }
@Component class SmsNotificador implements Notificador { ... }

@Service
public class AlertaService {
    private final List<Notificador> notificadores;                 // inyecta TODOS
    private final Map<String, Notificador> porNombre;              // clave = nombre del bean
    private final Optional<AuditoriaService> auditoria;            // opcional

    public AlertaService(List<Notificador> notificadores,
                         Map<String, Notificador> porNombre,
                         Optional<AuditoriaService> auditoria) {
        this.notificadores = notificadores;
        this.porNombre = porNombre;
        this.auditoria = auditoria;
    }

    public void alertar(String msg) {
        notificadores.forEach(n -> n.enviar(msg));
        auditoria.ifPresent(a -> a.registrar(msg));
    }
}
```

### Ejemplo: `@Value`
```java
@Component
public class MiConfig {
    @Value("${app.nombre}")                    private String nombre;
    @Value("${app.timeout:30}")                private int timeout;            // valor por defecto 30
    @Value("${app.activo:true}")               private boolean activo;
    @Value("${app.servidores}")                private List<String> servidores; // a,b,c
    @Value("#{2 * 10}")                        private int veinte;             // SpEL
    @Value("#{systemProperties['user.home']}") private String home;
    @Value("${JAVA_HOME}")                     private String javaHome;         // variable de entorno
}
```

### Ejemplo: dependencia circular y soluciones
```java
@Service class A { A(B b) {} }
@Service class B { B(A a) {} }          // ✗ BeanCurrentlyInCreationException

// Soluciones (en orden de preferencia):
// 1. Rediseñar: extraer lo común a una tercera clase C que usen A y B.
// 2. Inyectar de forma perezosa:
@Service class A2 { A2(@Lazy B b) {} }
// 3. Usar eventos para desacoplar.
```

## 5. Resolver ambigüedades: @Qualifier, @Primary

Si hay **varios beans del mismo tipo**, Spring no sabe cuál inyectar y lanza `NoUniqueBeanDefinitionException`.

| Término | Significado |
|---|---|
| `@Primary` | Marca el bean **preferido** por defecto cuando hay varios candidatos. |
| `@Qualifier("nombre")` | Elige un bean concreto por nombre/etiqueta. |
| **Nombre del parámetro** | Si coincide con el nombre del bean, Spring lo usa como último recurso. |
| `@Resource(name="x")` | Inyección por nombre. |
| **Qualifier personalizado** | Anotación propia con `@Qualifier` como meta-anotación. |
| `@ConditionalOnProperty` | Activa solo un bean según la configuración (Boot). |
| `@Profile` | Activa solo un bean según el perfil. |

### Ejemplos
```java
public interface Pago { void pagar(double monto); }

@Service("tarjeta")
@Primary                                  // el elegido si nadie especifica otro
public class PagoTarjeta implements Pago { ... }

@Service("paypal")
public class PagoPaypal implements Pago { ... }

@Service
public class CompraService {
    private final Pago pagoPorDefecto;     // → PagoTarjeta (por @Primary)
    private final Pago pagoPaypal;

    public CompraService(Pago pagoPorDefecto,
                         @Qualifier("paypal") Pago pagoPaypal) {   // → PagoPaypal
        this.pagoPorDefecto = pagoPorDefecto;
        this.pagoPaypal = pagoPaypal;
    }
}

// Qualifier en @Bean
@Bean
@Qualifier("rapido")
public Cache cacheRapido() { return new CacheEnMemoria(); }

// Qualifier personalizado
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Qualifier
public @interface Paypal {}
```

### Errores relacionados
| Error | Causa | Solución |
|---|---|---|
| `NoUniqueBeanDefinitionException` | Varios candidatos | `@Primary` o `@Qualifier` |
| `NoSuchBeanDefinitionException` | Ningún bean del tipo | Anotar la clase o declarar `@Bean`; revisar paquete/scan |

## 6. Scopes de un bean

| Scope | Significado |
|---|---|
| `singleton` | **(Por defecto)** Una única instancia compartida en todo el contenedor. |
| `prototype` | Una **nueva instancia** cada vez que se pide. |
| `request` | Una instancia por **petición HTTP** (web). |
| `session` | Una por **sesión HTTP**. |
| `application` | Una por `ServletContext`. |
| `websocket` | Una por sesión WebSocket. |
| `@Scope("...")` | Anotación para fijar el scope. |
| `proxyMode` | Cómo inyectar un bean de scope corto en uno singleton (`ScopedProxyMode.TARGET_CLASS`). |
| **Scope personalizado** | Se puede definir uno propio (`CustomScopeConfigurer`). |
| `@RequestScope` / `@SessionScope` / `@ApplicationScope` | Atajos con proxy incluido. |

### Importante sobre singleton
- Los beans singleton **comparten estado** entre todos los hilos → deben ser **sin estado (stateless)** o seguros para concurrencia.
- **No guardes datos de un usuario/petición en campos de un singleton.**

### Ejemplos
```java
@Component                                     // singleton por defecto
public class ContadorGlobal { ... }

@Component
@Scope("prototype")                            // nueva instancia en cada inyección/getBean
public class Ticket { ... }

@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class DatosPeticion {                    // una por petición
    private String usuario;
    // getters/setters
}

@Component
@SessionScope                                  // atajo equivalente para sesión
public class Carrito {
    private final List<Item> items = new ArrayList<>();
}

// Problema: prototype dentro de singleton se inyecta UNA sola vez
@Service
public class Fabrica {
    private final ObjectProvider<Ticket> tickets;       // solución: pedir uno nuevo cada vez
    public Fabrica(ObjectProvider<Ticket> tickets) { this.tickets = tickets; }
    public Ticket nuevoTicket() { return tickets.getObject(); }
}
```

### Singleton vs Prototype
| | singleton | prototype |
|---|---|---|
| Instancias | 1 | N (una por pedido) |
| Gestiona destrucción | Sí (`@PreDestroy`) | **No** (el contenedor lo crea y se desentiende) |
| Estado | Debe ser stateless | Puede tener estado |
| Uso típico | Services, repositories, controllers | Objetos con estado por uso |

## 7. Ciclo de vida de un bean

| Fase / Anotación | Significado |
|---|---|
| **1. Instanciación** | Se crea el objeto (constructor). |
| **2. Inyección** | Se asignan las dependencias. |
| **3. Aware callbacks** | `BeanNameAware`, `ApplicationContextAware`... |
| **4. `BeanPostProcessor` (before)** | Procesadores antes de inicializar. |
| **5. Inicialización** | `@PostConstruct` → `InitializingBean.afterPropertiesSet()` → `initMethod`. |
| **6. `BeanPostProcessor` (after)** | Aquí se crean los **proxies** (AOP, `@Transactional`). |
| **7. Bean listo** | Se usa en la aplicación. |
| **8. Destrucción** | `@PreDestroy` → `DisposableBean.destroy()` → `destroyMethod`. |
| `@PostConstruct` | Método que corre **tras** inyectar dependencias (inicializar cachés, validar). |
| `@PreDestroy` | Método que corre **antes** de destruir el bean (cerrar recursos). |
| `InitializingBean` | Interfaz con `afterPropertiesSet()`. |
| `DisposableBean` | Interfaz con `destroy()`. |
| `initMethod` / `destroyMethod` | Atributos de `@Bean` para indicar los métodos. |
| `SmartLifecycle` / `Lifecycle` | Beans con `start()`/`stop()` controlados por el contexto. |
| `SmartInitializingSingleton` | Callback cuando **todos** los singletons ya están creados. |
| `@EventListener(ContextRefreshedEvent.class)` | Reaccionar cuando el contexto terminó de arrancar. |

### Orden
```
constructor → inyección de dependencias → @PostConstruct → [bean en uso] → @PreDestroy
```

### Ejemplo
```java
@Component
public class ConexionExterna {

    private final ClienteHttp cliente;

    public ConexionExterna(ClienteHttp cliente) {
        this.cliente = cliente;                    // 1. constructor (aquí las deps ya llegan)
        System.out.println("Constructor");
    }

    @PostConstruct
    public void iniciar() {                        // 2. después de inyectar todo
        System.out.println("Inicializando conexión");
        cliente.conectar();
    }

    @PreDestroy
    public void cerrar() {                         // 3. al cerrar la aplicación
        System.out.println("Cerrando conexión");
        cliente.cerrar();
    }
}

// Con @Bean
@Configuration
public class Config {
    @Bean(initMethod = "abrir", destroyMethod = "cerrar")
    public Recurso recurso() { return new Recurso(); }
}

// Ejecutar algo cuando la app está lista
@Component
public class AlIniciar {
    @EventListener(ApplicationReadyEvent.class)
    public void listo() { System.out.println("App lista para recibir peticiones"); }
}
```
> **Cuidado con `@PostConstruct` + proxies:** dentro de `@PostConstruct` el bean aún **no** tiene sus proxies aplicados, por lo que `@Transactional`/`@Async` en esos métodos pueden no funcionar.

## 8. Configuración Java: @Configuration y @Bean

| Término | Significado |
|---|---|
| `@Configuration` | Clase cuya función es declarar beans. Spring la proxea (CGLIB) para que los métodos `@Bean` devuelvan siempre el mismo singleton. |
| `@Bean` | Método cuyo valor de retorno se registra como bean. |
| `@Bean(name = "x")` | Nombre explícito. |
| `proxyBeanMethods = false` | `@Configuration(proxyBeanMethods = false)`: modo "lite", más rápido; los `@Bean` no se llaman entre sí. |
| `@Import(Otra.class)` | Importa otra configuración. |
| `@ImportResource` | Importa XML. |
| `@PropertySource("classpath:x.properties")` | Carga un archivo de propiedades adicional. |
| `@EnableXxx` | Activa una funcionalidad (`@EnableScheduling`, `@EnableAsync`, `@EnableCaching`...). |
| `@ComponentScan` | Define qué escanear. |
| `ImportSelector` / `ImportBeanDefinitionRegistrar` | Registro programático avanzado (lo usa el auto-config). |
| **Method injection** | Los parámetros de un `@Bean` se inyectan automáticamente. |
| `static` `@Bean` | Para `BeanFactoryPostProcessor` (deben ser estáticos). |
| `FactoryBean<T>` | Fábrica de beans personalizada (el bean expuesto es el que produce). |
| **Bean de terceros** | Clase de una librería que no puedes anotar → se declara con `@Bean`. |

### Ejemplos
```java
@Configuration
public class AppConfig {

    // Bean de una librería externa
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper().registerModule(new JavaTimeModule());
    }

    // Los parámetros se inyectan solos
    @Bean
    public RestClient restClient(RestClient.Builder builder,
                                 @Value("${api.url}") String url) {
        return builder.baseUrl(url).build();
    }

    // Un bean que depende de otro
    @Bean
    public PedidoService pedidoService(PedidoRepository repo) {
        return new PedidoService(repo);
    }

    // Múltiples beans del mismo tipo
    @Bean("cacheCorta")
    public Cache cacheCorta() { return new Cache(Duration.ofMinutes(1)); }

    @Bean("cacheLarga")
    public Cache cacheLarga() { return new Cache(Duration.ofHours(1)); }
}

// Modo lite (más rápido): válido si los @Bean no se llaman entre sí
@Configuration(proxyBeanMethods = false)
public class ConfigLite { ... }

// Cargar un archivo de propiedades extra
@Configuration
@PropertySource("classpath:mensajes.properties")
public class ConfigMensajes {}

// Importar configuraciones
@Configuration
@Import({SeguridadConfig.class, CacheConfig.class})
public class RaizConfig {}
```

### Ejemplo: `FactoryBean`
```java
public class ConexionFactoryBean implements FactoryBean<Conexion> {
    public Conexion getObject() { return new Conexion("localhost"); }   // lo que se inyecta
    public Class<?> getObjectType() { return Conexion.class; }
}
```

### `@Configuration` con `@Bean` entre métodos
```java
@Configuration
public class Config {
    @Bean public A a() { return new A(b()); }       // b() devuelve SIEMPRE el mismo singleton
    @Bean public B b() { return new B(); }          // (gracias al proxy CGLIB de @Configuration)
}
```

### Configuración XML equivalente (legado)
```xml
<beans xmlns="http://www.springframework.org/schema/beans" ...>
    <bean id="pedidoRepo" class="com.empresa.PedidoRepositoryJdbc"/>
    <bean id="pedidoService" class="com.empresa.PedidoService">
        <constructor-arg ref="pedidoRepo"/>
    </bean>
    <context:component-scan base-package="com.empresa"/>
</beans>
```

## 9. Carga condicional: @Conditional y @Profile

| Anotación | Significado |
|---|---|
| `@Conditional(MiCondicion.class)` | Registra el bean solo si la condición se cumple. |
| `Condition` | Interfaz con `matches(...)` que implementa la lógica. |
| `@Profile("dev")` | Activo solo en ese perfil. |
| `@Profile("!prod")` | Activo en todos **menos** `prod`. |
| `@ConditionalOnProperty` | (Boot) Según el valor de una propiedad. |
| `@ConditionalOnClass` | (Boot) Si una clase está en el classpath. |
| `@ConditionalOnMissingClass` | (Boot) Si una clase **no** está. |
| `@ConditionalOnBean` | (Boot) Si existe otro bean. |
| `@ConditionalOnMissingBean` | (Boot) Si **no** existe un bean (el usuario puede sobrescribirlo). |
| `@ConditionalOnWebApplication` | (Boot) Si es una app web. |
| `@ConditionalOnExpression` | (Boot) Según una expresión SpEL. |
| `@ConditionalOnResource` | (Boot) Si existe un recurso. |
| `@ConditionalOnJava` | (Boot) Según versión de Java. |
| `@ConditionalOnCloudPlatform` | (Boot) Según la plataforma cloud. |

### Ejemplos
```java
// Por perfil
@Configuration
@Profile("dev")
public class DevConfig {
    @Bean public DataSource dataSource() { return new EmbeddedDatabaseBuilder().setType(H2).build(); }
}

@Configuration
@Profile("prod")
public class ProdConfig {
    @Bean public DataSource dataSource() { return new HikariDataSource(); }
}

// Por propiedad: activar/desactivar una función con una bandera
@Service
@ConditionalOnProperty(name = "app.notificaciones.enabled", havingValue = "true", matchIfMissing = false)
public class NotificacionService { ... }

// Elegir implementación según propiedad
@Bean
@ConditionalOnProperty(name = "app.storage", havingValue = "s3")
public Storage s3Storage() { return new S3Storage(); }

@Bean
@ConditionalOnProperty(name = "app.storage", havingValue = "local", matchIfMissing = true)
public Storage localStorage() { return new LocalStorage(); }

// Solo si el usuario no definió el suyo
@Bean
@ConditionalOnMissingBean
public Auditor auditorPorDefecto() { return new AuditorBasico(); }

// Condición propia
public class EsLinux implements Condition {
    public boolean matches(ConditionContext ctx, AnnotatedTypeMetadata md) {
        return System.getProperty("os.name").toLowerCase().contains("linux");
    }
}
@Bean @Conditional(EsLinux.class)
public Monitor monitorLinux() { return new MonitorLinux(); }
```

## 10. Propiedades y Environment

| Término | Significado |
|---|---|
| `Environment` | Acceso a propiedades y perfiles activos. |
| `PropertySource` | Fuente de propiedades (archivo, variable de entorno, argumentos...). |
| `@PropertySource` | Añade un archivo de propiedades. |
| `@Value("${x}")` | Inyecta una propiedad. |
| `${x:defecto}` | Valor por defecto si no existe. |
| `@ConfigurationProperties` | Agrupa propiedades en un objeto tipado (ver sección 21). |
| `environment.getProperty("x")` | Lee una propiedad por código. |
| `environment.getActiveProfiles()` | Perfiles activos. |
| `PropertySourcesPlaceholderConfigurer` | Resuelve los `${...}`. |
| `EnvironmentAware` | Recibir el `Environment` en un bean. |
| `ConfigurableEnvironment` | Permite modificar propiedades/perfiles. |

### Ejemplo
```java
@Component
public class Info {
    private final Environment env;
    public Info(Environment env) { this.env = env; }

    public void mostrar() {
        String url = env.getProperty("spring.datasource.url");
        int puerto = env.getProperty("server.port", Integer.class, 8080);   // con tipo y defecto
        boolean dev = Arrays.asList(env.getActiveProfiles()).contains("dev");
        String req = env.getRequiredProperty("app.clave");                  // lanza excepción si falta
    }
}
```

## 11. Eventos

Permiten que un componente **avise** de que algo pasó sin conocer a quién le interesa (desacoplamiento).

| Término | Significado |
|---|---|
| `ApplicationEvent` | Clase base de eventos (hoy puede ser cualquier objeto). |
| `ApplicationEventPublisher` | Interfaz para **publicar** eventos. |
| `publishEvent(evento)` | Publica el evento. |
| `@EventListener` | Método que **escucha** un tipo de evento. |
| `ApplicationListener<E>` | Interfaz alternativa para escuchar. |
| `@TransactionalEventListener` | Escucha **tras** el commit/rollback de la transacción. |
| `phase` | `AFTER_COMMIT` (defecto), `BEFORE_COMMIT`, `AFTER_ROLLBACK`, `AFTER_COMPLETION`. |
| `@Async` + `@EventListener` | Listener asíncrono. |
| `@Order` | Orden entre listeners. |
| `condition` | Condición SpEL en `@EventListener(condition = "...")`. |
| `ContextRefreshedEvent` | Contexto inicializado/refrescado. |
| `ContextStartedEvent` / `ContextStoppedEvent` / `ContextClosedEvent` | Ciclo de vida del contexto. |
| `ApplicationReadyEvent` | (Boot) App lista para recibir peticiones. |
| `ApplicationStartedEvent` | (Boot) Contexto creado, runners aún no. |
| `ApplicationFailedEvent` | (Boot) Falló el arranque. |
| `ApplicationEnvironmentPreparedEvent` | (Boot) Entorno preparado. |
| `AvailabilityChangeEvent` | (Boot) Cambia el estado de liveness/readiness. |

### Ejemplo
```java
// 1. Evento (un record sirve)
public record PedidoCreadoEvent(Long pedidoId, String email) {}

// 2. Publicar
@Service
@RequiredArgsConstructor
public class PedidoService {
    private final PedidoRepository repo;
    private final ApplicationEventPublisher publisher;

    @Transactional
    public Pedido crear(PedidoRequest req) {
        Pedido p = repo.save(new Pedido(req));
        publisher.publishEvent(new PedidoCreadoEvent(p.getId(), req.email()));
        return p;                     // PedidoService no sabe quién reaccionará
    }
}

// 3. Escuchar (puede haber varios listeners)
@Component
public class EmailListener {
    @EventListener
    public void alCrear(PedidoCreadoEvent e) {
        System.out.println("Enviando email a " + e.email());
    }
}

@Component
public class StockListener {
    @Async                                         // en otro hilo (requiere @EnableAsync)
    @EventListener(condition = "#e.pedidoId > 0")
    public void alCrear(PedidoCreadoEvent e) { ... }

    // Solo si la transacción confirmó (evita enviar email de un pedido que se revirtió)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void despuesDelCommit(PedidoCreadoEvent e) { ... }
}
```

## 12. AOP: programación orientada a aspectos

Para añadir comportamiento (logs, medición, seguridad, transacciones) **alrededor** de métodos sin modificarlos.

| Término | Significado |
|---|---|
| **Aspecto (Aspect)** | Clase que encapsula la lógica transversal. |
| **Advice** | La acción concreta (el código) que se ejecuta. |
| **Join point** | Punto de ejecución donde se puede intervenir (en Spring AOP: la **ejecución de un método**). |
| **Pointcut** | Expresión que selecciona **dónde** aplicar el advice. |
| **Weaving (tejido)** | Proceso de aplicar aspectos al código. Spring lo hace en **ejecución** mediante proxies. |
| **Target** | El objeto original sobre el que se aplica el aspecto. |
| **Proxy** | Objeto envoltorio que ejecuta el advice y luego delega al target. |
| **JDK dynamic proxy** | Proxy basado en interfaces. |
| **CGLIB proxy** | Proxy basado en subclases (Boot lo usa por defecto: `spring.aop.proxy-target-class=true`). |
| `@Aspect` | Marca la clase como aspecto (además `@Component`). |
| `@EnableAspectJAutoProxy` | Activa AOP (Boot lo hace solo con `spring-boot-starter-aop`). |
| `@Before` | Antes del método. |
| `@After` | Después, siempre (como `finally`). |
| `@AfterReturning` | Después, solo si terminó bien (accede al retorno). |
| `@AfterThrowing` | Después, solo si lanzó excepción. |
| `@Around` | **Envuelve** el método: puede ejecutarlo o no, modificar argumentos y retorno. El más potente. |
| `JoinPoint` | Información del método (nombre, argumentos). |
| `ProceedingJoinPoint` | En `@Around`: `proceed()` ejecuta el método real. |
| `execution(...)` | Designador de pointcut por firma de método. |
| `within(...)` | Por tipo/paquete. |
| `@annotation(...)` | Métodos con cierta anotación. |
| `@within(...)` | Clases con cierta anotación. |
| `bean(...)` | Por nombre de bean. |
| `args(...)` | Por tipo de argumentos. |
| `@Pointcut` | Define un pointcut reutilizable. |
| `@Order` | Orden entre aspectos. |
| **Self-invocation** | Si un método llama a otro **de la misma clase**, el proxy no interviene y el aspecto **no** se aplica. |

### Sintaxis de `execution`
```
execution( [modificador] tipoRetorno  paquete.Clase.metodo(parámetros) )

execution(* com.empresa.service.*.*(..))        cualquier método de cualquier clase en service
execution(public * com.empresa..*Service.*(..)) métodos públicos de clases *Service (subpaquetes incluidos con "..")
execution(* *.guardar*(..))                     métodos cuyo nombre empieza por guardar
execution(String com.empresa.*.buscar(Long))    firma exacta
```
- `*` = cualquier cosa (un elemento) · `..` = cualquier número de elementos/parámetros.

### Ejemplos
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
```
```java
// Aspecto de logging y medición de tiempo
@Aspect
@Component
public class LogAspect {

    private static final Logger log = LoggerFactory.getLogger(LogAspect.class);

    @Pointcut("execution(* com.empresa.app.service..*(..))")
    public void servicios() {}                       // pointcut reutilizable

    @Before("servicios()")
    public void antes(JoinPoint jp) {
        log.info("→ {} args={}", jp.getSignature().toShortString(), Arrays.toString(jp.getArgs()));
    }

    @AfterReturning(pointcut = "servicios()", returning = "resultado")
    public void despues(JoinPoint jp, Object resultado) {
        log.info("← {} devolvió {}", jp.getSignature().getName(), resultado);
    }

    @AfterThrowing(pointcut = "servicios()", throwing = "ex")
    public void error(JoinPoint jp, Exception ex) {
        log.error("✗ {} lanzó {}", jp.getSignature().getName(), ex.getMessage());
    }

    @Around("servicios()")
    public Object medir(ProceedingJoinPoint pjp) throws Throwable {
        long inicio = System.currentTimeMillis();
        try {
            return pjp.proceed();                    // ejecuta el método real
        } finally {
            log.info("{} tardó {} ms", pjp.getSignature().getName(),
                     System.currentTimeMillis() - inicio);
        }
    }
}
```
```java
// Aspecto activado por una anotación propia
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Auditar { String accion() default ""; }

@Aspect
@Component
public class AuditoriaAspect {
    @Around("@annotation(auditar)")
    public Object auditar(ProceedingJoinPoint pjp, Auditar auditar) throws Throwable {
        System.out.println("Auditando: " + auditar.accion());
        return pjp.proceed();
    }
}

@Service
public class CuentaService {
    @Auditar(accion = "TRANSFERENCIA")
    public void transferir(...) { ... }
}
```

### AOP en el día a día de Spring
`@Transactional`, `@Async`, `@Cacheable`, `@PreAuthorize`, `@Retryable`, `@Validated` funcionan **todas con proxies AOP**. Por eso comparten la limitación de self-invocation y de métodos no públicos.

## 13. SpEL y recursos

| Término | Significado |
|---|---|
| **SpEL** | Spring Expression Language: lenguaje de expresiones evaluado en ejecución. |
| `#{...}` | Sintaxis SpEL (distinta de `${...}` que es placeholder de propiedades). |
| `#{bean.propiedad}` | Accede a propiedades de otros beans. |
| `#{T(java.lang.Math).random()}` | Llama a métodos estáticos con `T(...)`. |
| `#{condicion ? a : b}` | Ternario. |
| `#{systemProperties['x']}` / `#{systemEnvironment['x']}` | Propiedades del sistema/entorno. |
| `ExpressionParser` / `SpelExpressionParser` | Evaluar SpEL por código. |
| `Resource` | Abstracción de un archivo/recurso. |
| `ResourceLoader` | Carga recursos (`classpath:`, `file:`, `http:`). |
| `ClassPathResource` | Recurso del classpath. |
| `FileSystemResource` | Archivo del disco. |
| `UrlResource` | Recurso por URL. |
| `classpath:` | Prefijo: dentro de `src/main/resources`. |
| `file:` | Prefijo: sistema de archivos. |
| `@Value("classpath:datos.json")` | Inyecta un `Resource`. |
| `ResourcePatternResolver` | Busca con comodines: `classpath*:/plantillas/*.html`. |

### Ejemplos
```java
@Component
public class Demo {
    @Value("#{2 + 3 * 4}")                                private int calculo;     // 14
    @Value("#{'hola'.toUpperCase()}")                     private String texto;    // HOLA
    @Value("#{T(java.lang.Math).PI}")                     private double pi;
    @Value("#{configApp.timeout > 10 ? 'largo' : 'corto'}") private String tipo;
    @Value("#{${app.max:5} * 2}")                         private int doble;       // mezcla ${} y #{}

    @Value("classpath:datos/paises.json")
    private Resource archivo;

    public String leer() throws IOException {
        return new String(archivo.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }
}

// ResourceLoader
@Service
public class Cargador {
    private final ResourceLoader loader;
    public Cargador(ResourceLoader loader) { this.loader = loader; }

    public Resource cargar(String ruta) {
        return loader.getResource("classpath:" + ruta);    // o "file:/ruta/archivo.txt"
    }
}

// Evaluar SpEL manualmente
ExpressionParser parser = new SpelExpressionParser();
Integer r = parser.parseExpression("10 * 2").getValue(Integer.class);   // 20
```
| Sintaxis | Qué hace |
|---|---|
| `${app.nombre}` | Lee una **propiedad** de configuración. |
| `#{app.nombre}` | Evalúa una **expresión** SpEL (accede a un bean). |
| `#{'${app.nombre}'.toUpperCase()}` | Combina ambas. |

## 14. Tareas asíncronas, programadas y caché

| Anotación / Término | Significado |
|---|---|
| `@EnableAsync` | Activa `@Async`. |
| `@Async` | Ejecuta el método en **otro hilo** (devuelve `void`, `Future` o `CompletableFuture`). |
| `TaskExecutor` / `ThreadPoolTaskExecutor` | Pool de hilos para tareas. |
| `AsyncConfigurer` | Configura el executor por defecto de `@Async`. |
| `@EnableScheduling` | Activa `@Scheduled`. |
| `@Scheduled(fixedRate = ms)` | Cada N ms desde el **inicio** de la ejecución anterior. |
| `@Scheduled(fixedDelay = ms)` | N ms desde el **fin** de la ejecución anterior. |
| `@Scheduled(initialDelay = ms)` | Espera inicial. |
| `@Scheduled(cron = "...")` | Expresión cron. |
| `TaskScheduler` | Programar tareas por código. |
| **Cron (Spring)** | 6 campos: `segundo minuto hora díaMes mes díaSemana`. |
| `@EnableCaching` | Activa la caché. |
| `@Cacheable` | Guarda el resultado; si se llama con los mismos args, devuelve el guardado. |
| `@CachePut` | Ejecuta siempre y actualiza la caché. |
| `@CacheEvict` | Elimina entradas de la caché. |
| `@Caching` | Combina varias anotaciones de caché. |
| `@CacheConfig` | Configuración común a nivel de clase. |
| `CacheManager` | Gestor de cachés (`ConcurrentMapCacheManager`, Caffeine, Redis). |
| `key` / `condition` / `unless` | Clave SpEL / cuándo cachear / cuándo **no** cachear. |
| `@Retryable` / `@Recover` / `@EnableRetry` | Reintentos (Spring Retry). |

### Cron (6 campos)
```
┌──── segundo (0-59)
│ ┌── minuto (0-59)
│ │ ┌ hora (0-23)
│ │ │ ┌ día del mes (1-31)
│ │ │ │ ┌ mes (1-12)
│ │ │ │ │ ┌ día de la semana (0-7 o MON-SUN)
* * * * * *

0 0 8 * * *        todos los días a las 08:00:00
0 */15 * * * *     cada 15 minutos
0 0 9 * * MON-FRI  lunes a viernes a las 9
0 0 0 1 * *        el día 1 de cada mes a medianoche
```

### Ejemplos
```java
@Configuration
@EnableAsync
@EnableScheduling
@EnableCaching
public class AppConfig {
    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor ex = new ThreadPoolTaskExecutor();
        ex.setCorePoolSize(4);
        ex.setMaxPoolSize(10);
        ex.setQueueCapacity(100);
        ex.setThreadNamePrefix("async-");
        ex.initialize();
        return ex;
    }
}

// Async
@Service
public class EmailService {
    @Async
    public CompletableFuture<Void> enviar(String to) {
        // se ejecuta en otro hilo
        return CompletableFuture.completedFuture(null);
    }
}

// Scheduled
@Component
public class Tareas {
    @Scheduled(fixedRate = 5000)                 // cada 5 s
    public void ping() { ... }

    @Scheduled(cron = "0 0 2 * * *")             // 02:00 diario
    public void limpieza() { ... }

    @Scheduled(cron = "${app.cron.reporte}")     // cron en propiedades
    public void reporte() { ... }
}

// Caché
@Service
@CacheConfig(cacheNames = "productos")
public class ProductoService {

    @Cacheable(key = "#id")                              // 1ª vez va a la BD; luego usa caché
    public Producto buscar(Long id) { ... }

    @Cacheable(key = "#nombre", unless = "#result == null")
    public Producto porNombre(String nombre) { ... }

    @CachePut(key = "#p.id")                             // actualiza la caché
    public Producto actualizar(Producto p) { ... }

    @CacheEvict(key = "#id")                             // borra una entrada
    public void eliminar(Long id) { ... }

    @CacheEvict(allEntries = true)                       // vacía todo
    public void limpiar() { ... }
}
```
```properties
spring.cache.type=caffeine
spring.cache.caffeine.spec=maximumSize=500,expireAfterWrite=10m
```
> `@Async`, `@Scheduled` y `@Cacheable` también usan proxies: **no funcionan** en llamadas internas de la misma clase ni en métodos privados.

## 15. Validación, conversión e i18n

| Término | Significado |
|---|---|
| `Validator` (Spring) | Interfaz propia de Spring para validar objetos. |
| **Bean Validation** | Estándar `jakarta.validation` (`@NotNull`, `@Size`...). |
| `@Validated` | Activa validación en clase/método (y grupos). |
| `Errors` / `BindingResult` | Almacén de errores de validación. |
| `Converter<S,T>` | Convierte un tipo en otro. |
| `ConversionService` | Servicio central de conversiones. |
| `Formatter<T>` | Convierte entre objeto y texto con `Locale`. |
| `PropertyEditor` | Mecanismo antiguo de conversión. |
| `@DateTimeFormat` / `@NumberFormat` | Formato de fechas/números en binding. |
| `DataBinder` | Enlaza valores a objetos. |
| `MessageSource` | Resuelve mensajes por idioma (`messages_es.properties`). |
| `ResourceBundleMessageSource` | Implementación basada en bundles. |
| `LocaleResolver` | Determina el idioma del usuario. |
| `spring.messages.basename` | Nombre de los archivos de mensajes. |

### Ejemplos
```java
// Converter personalizado: "12.5,USD" → Dinero
@Component
public class DineroConverter implements Converter<String, Dinero> {
    @Override
    public Dinero convert(String origen) {
        String[] partes = origen.split(",");
        return new Dinero(new BigDecimal(partes[0]), partes[1]);
    }
}

// Mensajes internacionalizados
// messages.properties:     saludo=Hello {0}
// messages_es.properties:  saludo=Hola {0}
@Service
public class Mensajes {
    private final MessageSource ms;
    public Mensajes(MessageSource ms) { this.ms = ms; }

    public String saludar(String nombre, Locale locale) {
        return ms.getMessage("saludo", new Object[]{nombre}, locale);   // "Hola Ana"
    }
}
```
```properties
spring.messages.basename=messages
spring.messages.encoding=UTF-8
```

## 16. Excepciones comunes del contenedor

| Excepción | Causa | Solución |
|---|---|---|
| `NoSuchBeanDefinitionException` | No existe un bean de ese tipo/nombre. | Anotar con `@Component`/`@Service`; revisar que esté dentro del paquete escaneado; declarar `@Bean`. |
| `NoUniqueBeanDefinitionException` | Hay varios beans candidatos. | `@Primary` o `@Qualifier`. |
| `BeanCurrentlyInCreationException` | Dependencia circular. | Rediseñar, `@Lazy` o eventos. |
| `UnsatisfiedDependencyException` | No se pudo satisfacer una dependencia (envuelve otra causa). | Leer la causa raíz ("Caused by"). |
| `BeanCreationException` | Falló la creación del bean (error en constructor, `@PostConstruct`, propiedad faltante...). | Ver "Caused by" al final del stacktrace. |
| `BeanInstantiationException` | No se pudo instanciar (constructor privado, clase abstracta/interfaz). | Revisar la clase. |
| `BeanDefinitionStoreException` | Definición de bean inválida. | Revisar configuración. |
| `BeanNotOfRequiredTypeException` | El bean no es del tipo esperado. | Revisar proxies JDK vs CGLIB, tipo solicitado. |
| `NoSuchBeanDefinitionException: ... required a bean of type 'X' that could not be found` | Falta el bean. | Igual al primero. |
| `ApplicationContextException` | Error al arrancar el contexto. | Ver causa. |
| `BeanCreationNotAllowedException` | Crear un bean durante la destrucción del contexto. | Revisar `@PreDestroy`. |
| `ConversionFailedException` | No se pudo convertir un tipo (propiedad, parámetro). | Revisar tipo/formato. |
| `IllegalArgumentException: Could not resolve placeholder 'x'` | Propiedad `${x}` no existe. | Definirla o usar `${x:defecto}`. |
| `BindException` / `ConfigurationPropertiesBindException` | Error al enlazar `@ConfigurationProperties`. | Revisar nombres y tipos. |
| `PlaceholderResolutionException` | Placeholder sin resolver. | Igual. |

> **Truco para leer errores de arranque:** el mensaje largo está anidado. Baja hasta el último **`Caused by:`**: ahí está el problema real.

---

# PARTE B: SPRING BOOT CORE

## 17. Qué es Spring Boot

| Término | Significado |
|---|---|
| **Spring Boot** | Capa sobre Spring que da **auto-configuración**, servidor embebido y arranque rápido sin XML. |
| **Opinionated defaults** | Valores por defecto sensatos; los cambias solo si lo necesitas. |
| **Auto-configuración** | Boot crea beans automáticamente según las librerías presentes en el classpath. |
| **Starter** | Dependencia "paquete" que trae todo lo necesario para una función. |
| **Servidor embebido** | Tomcat/Jetty/Undertow dentro del `.jar`; se ejecuta con `java -jar`. |
| **Fat JAR / Uber JAR** | `.jar` ejecutable que incluye todas las dependencias. |
| **Standalone** | La aplicación se ejecuta sola, sin servidor externo. |
| **Actuator** | Endpoints de monitoreo/gestión (`/actuator/health`). |
| **Spring Initializr** | `start.spring.io`: generador de proyectos. |
| **Spring Boot CLI** | Herramienta de línea de comandos. |
| **GraalVM Native Image** | Compilar a ejecutable nativo (arranque instantáneo). |
| **Spring Boot vs Spring** | Spring = framework base; Boot = facilita configurarlo y ejecutarlo. |
| **Versionado** | `3.3.5` (mayor.menor.parche); Boot 3 exige **Java 17+** y usa `jakarta.*`. |
| **Jakarta EE** | Reemplazo de `javax.*` (Boot 3+: `jakarta.persistence`, `jakarta.servlet`, `jakarta.validation`). |
| **BOM** | Bill of Materials: lista de versiones compatibles entre sí (gestionada por el parent de Boot). |

### Lo que Boot hace por ti
```
Sin Boot:  configurar DispatcherServlet, ViewResolver, DataSource, TransactionManager,
           ObjectMapper, servidor externo, versiones compatibles de cada librería...
Con Boot:  añades el starter → todo se configura solo → public static void main → listo.
```

## 18. Starters y gestión de dependencias

| Término | Significado |
|---|---|
| `spring-boot-starter-parent` | POM padre: versiones gestionadas, plugins, Java, encoding. |
| `spring-boot-dependencies` | BOM importable sin usar el parent. |
| `spring-boot-maven-plugin` | Plugin que crea el JAR ejecutable (`repackage`) y permite `spring-boot:run`. |
| `spring-boot-starter` | Starter base (core, logging, YAML, auto-config). |
| `spring-boot-starter-web` | Spring MVC + Tomcat + Jackson. |
| `spring-boot-starter-webflux` | Web reactiva (Netty). |
| `spring-boot-starter-data-jpa` | JPA + Hibernate + HikariCP. |
| `spring-boot-starter-data-mongodb` / `-redis` / `-jdbc` | Otros accesos a datos. |
| `spring-boot-starter-security` | Spring Security. |
| `spring-boot-starter-validation` | Bean Validation. |
| `spring-boot-starter-test` | JUnit 5, Mockito, AssertJ, Spring Test. |
| `spring-boot-starter-actuator` | Monitoreo. |
| `spring-boot-starter-thymeleaf` | Plantillas. |
| `spring-boot-starter-aop` | AOP. |
| `spring-boot-starter-cache` | Caché. |
| `spring-boot-starter-mail` | Envío de correo. |
| `spring-boot-starter-amqp` / `-kafka` | Mensajería. |
| `spring-boot-starter-oauth2-*` | OAuth2. |
| `spring-boot-starter-logging` | Logback + SLF4J (incluido por defecto). |
| `spring-boot-starter-tomcat` / `-jetty` / `-undertow` | Servidor embebido. |
| `spring-boot-starter-batch` | Spring Batch. |
| `spring-boot-starter-quartz` | Planificador Quartz. |
| `spring-boot-configuration-processor` | Genera metadatos para autocompletar `application.properties`. |
| `spring-boot-devtools` | Reinicio automático y utilidades de desarrollo. |
| `spring-boot-docker-compose` / `spring-boot-testcontainers` | Soporte Docker (Boot 3.1+). |
| `<scope>` | `compile`, `runtime`, `provided`, `test`, `optional`. |
| `<optional>true</optional>` | No se propaga a proyectos que dependan de este. |
| **Excluir dependencia** | `<exclusions>`: ej. quitar Tomcat para usar Jetty. |
| `mvn dependency:tree` | Árbol de dependencias (ver conflictos). |
| `mvn spring-boot:build-info` | Genera `build-info.properties` para Actuator. |

### Ejemplo: `pom.xml` típico
```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.3.5</version>
    <relativePath/>
</parent>

<properties>
    <java.version>21</java.version>
</properties>

<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```

### Ejemplo: cambiar Tomcat por Jetty
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-tomcat</artifactId>
        </exclusion>
    </exclusions>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jetty</artifactId>
</dependency>
```

### Ejemplo: sobrescribir una versión gestionada
```xml
<properties>
    <jackson-bom.version>2.17.2</jackson-bom.version>
</properties>
```

### Convención de nombres de starters
| Nombre | Quién lo publica |
|---|---|
| `spring-boot-starter-*` | Oficiales de Spring |
| `*-spring-boot-starter` | De terceros (ej. `mybatis-spring-boot-starter`) |

## 19. La clase principal y el arranque

| Término | Significado |
|---|---|
| `@SpringBootApplication` | Meta-anotación = `@SpringBootConfiguration` + `@EnableAutoConfiguration` + `@ComponentScan`. |
| `@SpringBootConfiguration` | Variante de `@Configuration` (la usan los tests para encontrar la config). |
| `@EnableAutoConfiguration` | Activa la auto-configuración. |
| `@ComponentScan` | Escanea desde el paquete de la clase principal. |
| `SpringApplication.run(Clase.class, args)` | Arranca la aplicación y devuelve el `ApplicationContext`. |
| `SpringApplication` | Clase que controla el arranque (banner, tipo de app, listeners...). |
| `SpringApplicationBuilder` | Versión fluida para configurar el arranque. |
| `args` | Argumentos de línea de comandos (`--server.port=9090`). |
| `ApplicationArguments` | Acceso estructurado a los argumentos. |
| `WebApplicationType` | `SERVLET`, `REACTIVE` o `NONE`. |
| `setBannerMode` / `banner.txt` | Banner ASCII de arranque. |
| `exclude` | Excluir auto-configuraciones: `@SpringBootApplication(exclude = X.class)`. |
| `spring.main.*` | Propiedades que controlan `SpringApplication`. |
| `spring.main.web-application-type=none` | Aplicación de consola sin servidor web. |
| `spring.main.lazy-initialization=true` | Inicialización perezosa global de beans. |
| `spring.main.banner-mode=off` | Oculta el banner. |

### Ejemplos
```java
@SpringBootApplication
public class MiApp {
    public static void main(String[] args) {
        SpringApplication.run(MiApp.class, args);
    }
}

// Personalizar el arranque
public static void main(String[] args) {
    SpringApplication app = new SpringApplication(MiApp.class);
    app.setBannerMode(Banner.Mode.OFF);
    app.setAdditionalProfiles("dev");
    app.setDefaultProperties(Map.of("server.port", "9090"));
    app.run(args);
}

// Forma fluida
new SpringApplicationBuilder(MiApp.class)
        .profiles("dev")
        .web(WebApplicationType.NONE)
        .run(args);

// Excluir una auto-configuración
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class SinBd { ... }
```
```bash
# Pasar argumentos al arrancar
java -jar app.jar --server.port=9090 --spring.profiles.active=prod --app.nombre=Demo
```

### Equivalencia de `@SpringBootApplication`
```java
@SpringBootConfiguration      // = @Configuration
@EnableAutoConfiguration      // ← la "magia" de Boot
@ComponentScan                // escanea el paquete actual y subpaquetes
public class MiApp { ... }
```

### Banner personalizado
Crear `src/main/resources/banner.txt`:
```
  __  __ _    _
 |  \/  (_)  / \   _ __  _ __
 | |\/| | | / _ \ | '_ \| '_ \
 | |  | | |/ ___ \| |_) | |_) |
 |_|  |_|_/_/   \_\ .__/| .__/
                  |_|   |_|     v${application.version}
```

## 20. Auto-configuración

| Término | Significado |
|---|---|
| **Auto-configuración** | Boot examina el classpath, beans y propiedades, y registra beans por ti **de forma condicional**. |
| `@AutoConfiguration` | Marca una clase como auto-configuración (Boot 2.7+/3). |
| `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` | Archivo que **lista** las clases de auto-configuración (Boot 3; antes `spring.factories`). |
| `@ConditionalOnClass` | Solo si hay cierta clase en el classpath (ej. `DataSource`). |
| `@ConditionalOnMissingBean` | Solo si **tú no** definiste ese bean → permite sobrescribir. |
| `@ConditionalOnProperty` | Según una propiedad. |
| `@ConditionalOnWebApplication` | Solo en apps web. |
| `@AutoConfigureBefore` / `@AutoConfigureAfter` / `@AutoConfigureOrder` | Orden entre auto-configuraciones. |
| `@EnableConfigurationProperties` | Registra una clase `@ConfigurationProperties`. |
| `exclude` / `spring.autoconfigure.exclude` | Desactivar auto-configuraciones. |
| **Back-off** | Boot "se retira" si defines tu propio bean. |
| `--debug` / `debug=true` | Imprime el **informe de condiciones** (qué se activó y qué no). |
| `ConditionEvaluationReport` | Ese informe: *Positive matches*, *Negative matches*, *Exclusions*. |
| `DataSourceAutoConfiguration` | Crea el `DataSource` si hay driver. |
| `HibernateJpaAutoConfiguration` | Configura JPA/Hibernate. |
| `JacksonAutoConfiguration` | Crea el `ObjectMapper`. |
| `WebMvcAutoConfiguration` | Configura Spring MVC. |
| `ServletWebServerFactoryAutoConfiguration` | Crea el servidor embebido. |
| `SecurityAutoConfiguration` | Configuración por defecto de seguridad. |
| `TransactionAutoConfiguration` | Habilita transacciones. |
| **Starter personalizado** | Módulo con auto-configuración propia reutilizable. |

### Cómo decide Boot
```
¿Está spring-boot-starter-web en el classpath?      → configura MVC + Tomcat + Jackson
¿Hay un driver de BD y spring.datasource.url?       → crea DataSource + EntityManager + TransactionManager
¿Definiste tu propio @Bean ObjectMapper?            → Boot NO crea el suyo (back-off)
¿Hay spring-boot-starter-security?                  → protege todos los endpoints
```

### Ver qué se auto-configuró
```properties
debug=true         # o ejecutar con --debug
```
Salida:
```
Positive matches:
-----------------
   DataSourceAutoConfiguration matched:
      - @ConditionalOnClass found required class 'javax.sql.DataSource'

Negative matches:
-----------------
   MongoAutoConfiguration:
      Did not match:
         - @ConditionalOnClass did not find required class 'com.mongodb.client.MongoClient'
```

### Ejemplo: sobrescribir un bean auto-configurado
```java
// Boot crea un ObjectMapper por defecto. Al declarar el tuyo, Boot se retira (@ConditionalOnMissingBean)
@Bean
public ObjectMapper objectMapper() {
    return JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .build();
}
```

### Ejemplo: excluir auto-configuraciones
```java
@SpringBootApplication(exclude = {
    DataSourceAutoConfiguration.class,
    SecurityAutoConfiguration.class
})
```
```properties
spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
```

### Ejemplo: crear una auto-configuración propia (starter)
```java
@AutoConfiguration
@ConditionalOnClass(SaludoService.class)
@EnableConfigurationProperties(SaludoProperties.class)
public class SaludoAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SaludoService saludoService(SaludoProperties props) {
        return new SaludoService(props.getMensaje());
    }
}

@ConfigurationProperties(prefix = "saludo")
public class SaludoProperties {
    private String mensaje = "Hola";
    // getters y setters
}
```
Registrar en `src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`:
```
com.empresa.saludo.SaludoAutoConfiguration
```

## 21. Propiedades de configuración

### Archivos y orden de carga
| Término | Significado |
|---|---|
| `application.properties` | Configuración en formato `clave=valor`. |
| `application.yml` / `.yaml` | Misma configuración en YAML (jerárquico). |
| `application-{perfil}.properties` | Configuración específica de un perfil (`application-dev.yml`). |
| `bootstrap.properties` | (Spring Cloud) configuración que se carga antes. |
| `src/main/resources` | Ubicación habitual. |
| `config/` junto al JAR | Archivos externos que **sobrescriben** los internos. |
| `spring.config.location` | Reemplaza las ubicaciones de búsqueda. |
| `spring.config.additional-location` | Añade ubicaciones. |
| `spring.config.import` | Importa otro archivo/fuente (`optional:file:./extra.yml`). |
| `spring.config.name` | Cambia el nombre base (`application`). |
| **Relaxed binding** | Boot acepta varias formas: `my.mi-propiedad`, `my.miPropiedad`, `MY_MIPROPIEDAD`, `my.mi_propiedad`. |

### Orden de prioridad (de **mayor** a **menor** — gana el de arriba)
```
 1. Argumentos de línea de comandos          --server.port=9090
 2. SPRING_APPLICATION_JSON
 3. Propiedades del sistema Java             -Dserver.port=9090
 4. Variables de entorno del SO              SERVER_PORT=9090
 5. application-{perfil}.properties/yml  fuera del JAR
 6. application.properties/yml           fuera del JAR
 7. application-{perfil}.properties/yml  dentro del JAR
 8. application.properties/yml           dentro del JAR
 9. @PropertySource
10. Valores por defecto (SpringApplication.setDefaultProperties)
```
> Lo más externo y específico **sobrescribe** lo interno: así cambias la configuración por entorno sin recompilar.

### `@ConfigurationProperties`
| Término | Significado |
|---|---|
| `@ConfigurationProperties(prefix="app")` | Enlaza un grupo de propiedades a una clase tipada. |
| `@EnableConfigurationProperties` | Registra la clase como bean. |
| `@ConfigurationPropertiesScan` | Escanea clases anotadas automáticamente. |
| `@ConstructorBinding` | Enlace por constructor (inmutable; automático en `record` o constructor único desde Boot 3). |
| `@DefaultValue` | Valor por defecto en enlace por constructor. |
| `@Validated` | Valida las propiedades al arrancar (falla rápido). |
| `@NestedConfigurationProperty` | Objeto anidado. |
| `Duration` / `DataSize` | Tipos soportados: `30s`, `5m`, `10MB`. |
| `@DurationUnit` / `@DataSizeUnit` | Unidad por defecto. |
| `@Value` vs `@ConfigurationProperties` | `@Value` para uno o dos valores; `@ConfigurationProperties` para grupos tipados y validables. |
| `spring-boot-configuration-processor` | Genera `META-INF/spring-configuration-metadata.json` → autocompletado en el IDE. |

### Ejemplo: `application.properties` vs `application.yml`
```properties
server.port=8081
spring.application.name=mi-api
app.nombre=Demo
app.servidores[0]=a.com
app.servidores[1]=b.com
app.limites.max-usuarios=100
```
```yaml
server:
  port: 8081
spring:
  application:
    name: mi-api
app:
  nombre: Demo
  servidores:
    - a.com
    - b.com
  limites:
    max-usuarios: 100
```

### Ejemplo: `@ConfigurationProperties` con record (inmutable, recomendado)
```yaml
app:
  nombre: Demo
  timeout: 30s
  max-subida: 10MB
  correo:
    host: smtp.mail.com
    puerto: 587
  admins:
    - ana@mail.com
    - luis@mail.com
```
```java
@ConfigurationProperties(prefix = "app")
@Validated
public record AppProperties(
        @NotBlank String nombre,
        @DefaultValue("30s") Duration timeout,
        DataSize maxSubida,
        Correo correo,
        List<String> admins) {

    public record Correo(@NotBlank String host, @Min(1) int puerto) {}
}

@SpringBootApplication
@ConfigurationPropertiesScan          // detecta AppProperties solo
public class MiApp { ... }

@Service
public class Saludo {
    private final AppProperties props;
    public Saludo(AppProperties props) { this.props = props; }
    public String hola() { return "Hola desde " + props.nombre() + " (" + props.correo().host() + ")"; }
}
```

### Ejemplo: clase clásica con setters
```java
@Component
@ConfigurationProperties(prefix = "app")
public class AppConfig {
    private String nombre;
    private List<String> servidores = new ArrayList<>();
    // getters y setters
}
```

### Ejemplo: valores desde el entorno y por defecto
```properties
spring.datasource.password=${DB_PASSWORD}             # variable de entorno obligatoria
app.url=${APP_URL:http://localhost:8080}              # con valor por defecto
app.nombre-completo=${app.nombre} v1                  # referencia a otra propiedad
```
```bash
export DB_PASSWORD=secreta
java -jar app.jar
```

### Ejemplo: múltiples documentos en un YAML
```yaml
spring:
  application:
    name: mi-api
---
spring:
  config:
    activate:
      on-profile: dev
server:
  port: 8081
---
spring:
  config:
    activate:
      on-profile: prod
server:
  port: 80
```

### Ejemplo: importar configuración externa
```yaml
spring:
  config:
    import:
      - optional:file:./secretos.properties
      - optional:configserver:http://config:8888
```

## 22. Perfiles

| Término | Significado |
|---|---|
| **Perfil (profile)** | Conjunto de configuración/beans que se activa según el entorno (`dev`, `test`, `prod`). |
| `spring.profiles.active` | Perfil(es) activo(s): `dev` o `dev,debug`. |
| `spring.profiles.default` | Perfil si no se activa ninguno. |
| `spring.profiles.include` | Perfiles que se añaden siempre. |
| `spring.profiles.group.*` | Agrupa perfiles bajo un nombre. |
| `@Profile("dev")` | Bean/configuración solo en ese perfil. |
| `@Profile("!prod")` | En todos menos `prod`. |
| `@Profile({"dev","test"})` | En cualquiera de ellos. |
| `@ActiveProfiles("test")` | Perfil activo en un test. |
| `application-{perfil}.yml` | Config propia del perfil. |
| `spring.config.activate.on-profile` | Activa un bloque de un YAML por perfil. |
| `Environment.getActiveProfiles()` | Perfiles activos por código. |
| `SPRING_PROFILES_ACTIVE` | Variable de entorno equivalente. |

### Formas de activar un perfil
```bash
java -jar app.jar --spring.profiles.active=prod            # argumento
java -Dspring.profiles.active=prod -jar app.jar            # propiedad de sistema
export SPRING_PROFILES_ACTIVE=prod                         # variable de entorno
mvn spring-boot:run -Dspring-boot.run.profiles=dev         # con Maven
```
```properties
# application.properties
spring.profiles.active=dev
```
```java
@ActiveProfiles("test")      // en una clase de test
```

### Ejemplo: archivos por perfil
```
application.yml          (común a todos)
application-dev.yml      (H2, logs DEBUG, show-sql)
application-prod.yml     (MySQL real, logs INFO)
application-test.yml     (para pruebas)
```
```yaml
# application-dev.yml
spring:
  datasource:
    url: jdbc:h2:mem:devdb
  jpa:
    show-sql: true
logging:
  level:
    com.empresa: DEBUG
```
```yaml
# application-prod.yml
spring:
  datasource:
    url: jdbc:mysql://prod-db:3306/app
    username: ${DB_USER}
    password: ${DB_PASSWORD}
  jpa:
    show-sql: false
logging:
  level:
    root: WARN
    com.empresa: INFO
```

### Ejemplo: grupos de perfiles
```yaml
spring:
  profiles:
    group:
      produccion: prod, metricas, seguridad-estricta
      desarrollo: dev, debug
# --spring.profiles.active=produccion activa los tres
```

### Ejemplo: beans por perfil
```java
@Service @Profile("dev")
public class EmailFalso implements EmailService { /* solo imprime en consola */ }

@Service @Profile("prod")
public class EmailSmtp implements EmailService { /* envía de verdad */ }

@Bean @Profile("!prod")
public DataLoader datosDePrueba() { return new DataLoader(); }
```

## 23. Logging

| Término | Significado |
|---|---|
| **SLF4J** | Fachada de logging (interfaz común). |
| **Logback** | Implementación de logging por defecto en Boot. |
| **Log4j2** | Alternativa (`spring-boot-starter-log4j2`). |
| `Logger` / `LoggerFactory` | Para obtener un logger: `LoggerFactory.getLogger(Clase.class)`. |
| `@Slf4j` (Lombok) | Genera el campo `log` automáticamente. |
| **Niveles** | `TRACE` < `DEBUG` < `INFO` < `WARN` < `ERROR` (+ `OFF`). Nivel por defecto: `INFO`. |
| `logging.level.root` | Nivel global. |
| `logging.level.<paquete>` | Nivel de un paquete/clase. |
| `logging.file.name` | Archivo de log. |
| `logging.file.path` | Carpeta de logs (`spring.log`). |
| `logging.pattern.console` / `.file` | Formato de cada línea. |
| `logging.logback.rollingpolicy.*` | Rotación de archivos (tamaño, historial). |
| `logging.group.*` | Agrupa paquetes bajo un nombre. |
| `logback-spring.xml` | Configuración avanzada de Logback (admite perfiles). |
| `MDC` | Contexto de datos añadido a cada línea (id de correlación, usuario). |
| `--debug` / `--trace` | Activa logs `DEBUG`/`TRACE` del núcleo de Spring Boot. |
| **Placeholders `{}`** | `log.info("Usuario {}", id)`: evitan concatenar y son más eficientes. |
| `logging.charset.console` | Codificación de la consola. |
| `logging.structured.format.console` | (Boot 3.4+) Logs en formato JSON (`ecs`, `logstash`). |

### Cuándo usar cada nivel
| Nivel | Uso |
|---|---|
| `ERROR` | Fallos que impiden completar una operación. |
| `WARN` | Situación anómala pero recuperable. |
| `INFO` | Hitos importantes (arranque, operaciones de negocio). |
| `DEBUG` | Detalle útil para desarrolladores. |
| `TRACE` | Máximo detalle (valores, flujo paso a paso). |

### Ejemplos
```java
// Con Lombok
@Slf4j
@Service
public class PedidoService {
    public void crear(Long id) {
        log.info("Creando pedido {}", id);                      // placeholders
        log.debug("Datos completos: {}", pedido);
        try { ... }
        catch (Exception e) {
            log.error("Error creando pedido {}", id, e);        // la excepción va al final → stacktrace
        }
    }
}

// Sin Lombok
private static final Logger log = LoggerFactory.getLogger(PedidoService.class);

// Evitar trabajo costoso si el nivel está desactivado
if (log.isDebugEnabled()) { log.debug("Resumen: {}", calcularResumenCostoso()); }

// MDC: añadir contexto a todas las líneas de la petición
MDC.put("usuario", "ana");
try { ... } finally { MDC.clear(); }
```
```yaml
logging:
  level:
    root: INFO
    com.empresa.app: DEBUG
    org.springframework.web: DEBUG
    org.hibernate.SQL: DEBUG                         # SQL generado
    org.hibernate.orm.jdbc.bind: TRACE               # valores de parámetros SQL
    org.springframework.security: DEBUG
  file:
    name: logs/app.log
  logback:
    rollingpolicy:
      max-file-size: 10MB
      max-history: 14
      total-size-cap: 1GB
  pattern:
    console: "%d{HH:mm:ss.SSS} %-5level [%thread] %logger{36} - %msg%n"
```

### Formato por defecto de una línea de log
```
2026-11-05T12:30:15.123-03:00  INFO 12345 --- [main] c.e.app.MiApp : Started MiApp in 3.2 seconds
└──────── fecha/hora ────────┘ └nivel┘ └PID┘   └hilo┘ └─ logger ──┘   └──── mensaje ────┘
```

### Ejemplo: `logback-spring.xml` con perfiles
```xml
<configuration>
    <include resource="org/springframework/boot/logging/logback/base.xml"/>

    <springProfile name="dev">
        <logger name="com.empresa" level="DEBUG"/>
    </springProfile>

    <springProfile name="prod">
        <root level="WARN"/>
        <logger name="com.empresa" level="INFO"/>
    </springProfile>
</configuration>
```

## 24. Actuator y monitoreo

| Término | Significado |
|---|---|
| **Actuator** | Módulo con endpoints para **monitorear y gestionar** la app en ejecución. |
| `/actuator/health` | Estado de salud (`UP`/`DOWN`). |
| `/actuator/info` | Información de la aplicación. |
| `/actuator/metrics` | Métricas (memoria, CPU, peticiones HTTP). |
| `/actuator/env` | Propiedades del entorno. |
| `/actuator/beans` | Todos los beans del contexto. |
| `/actuator/mappings` | Todos los endpoints web mapeados. |
| `/actuator/loggers` | Ver y **cambiar niveles de log en caliente**. |
| `/actuator/configprops` | Valores de `@ConfigurationProperties`. |
| `/actuator/conditions` | Informe de auto-configuración. |
| `/actuator/threaddump` / `/heapdump` | Volcados de hilos/memoria. |
| `/actuator/prometheus` | Métricas formato Prometheus. |
| `/actuator/shutdown` | Apaga la aplicación (desactivado por defecto). |
| `/actuator/scheduledtasks` | Tareas `@Scheduled`. |
| `/actuator/httpexchanges` | Últimas peticiones HTTP. |
| `management.endpoints.web.exposure.include` | Endpoints expuestos por HTTP (`*` = todos). |
| `management.endpoints.web.exposure.exclude` | Endpoints ocultos. |
| `management.endpoint.<id>.enabled` | Habilitar/deshabilitar un endpoint. |
| `management.endpoint.health.show-details` | `never`, `when-authorized`, `always`. |
| `management.endpoints.web.base-path` | Prefijo (por defecto `/actuator`). |
| `management.server.port` | Puerto separado para Actuator. |
| `HealthIndicator` | Componente que aporta información a `/health` (BD, disco, Redis...). |
| **Liveness / Readiness probes** | `/actuator/health/liveness` y `/readiness`: usados por Kubernetes. |
| **Micrometer** | Librería de métricas que usa Actuator (`MeterRegistry`, `Counter`, `Timer`, `Gauge`). |
| `InfoContributor` | Aporta datos a `/info`. |
| `@Endpoint` | Crear un endpoint de Actuator propio. |

> **Seguridad:** no expongas `env`, `heapdump`, `beans` ni `shutdown` en producción sin proteger.

### Ejemplos
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health, info, metrics, prometheus, loggers
  endpoint:
    health:
      show-details: when-authorized
      probes:
        enabled: true                  # liveness / readiness
  server:
    port: 9001                         # Actuator en otro puerto
info:
  app:
    nombre: ${spring.application.name}
    version: 1.0.0
```
```bash
curl localhost:8080/actuator/health
# {"status":"UP","components":{"db":{"status":"UP"},"diskSpace":{"status":"UP"}}}

# Cambiar un nivel de log SIN reiniciar
curl -X POST localhost:8080/actuator/loggers/com.empresa \
     -H "Content-Type: application/json" -d '{"configuredLevel":"DEBUG"}'
```
```java
// HealthIndicator propio
@Component
public class ApiExternaHealth implements HealthIndicator {
    @Override
    public Health health() {
        boolean ok = ping();
        return ok ? Health.up().withDetail("latencia", "20ms").build()
                  : Health.down().withDetail("error", "sin respuesta").build();
    }
}

// Métrica propia con Micrometer
@Service
public class PedidoService {
    private final Counter pedidos;
    public PedidoService(MeterRegistry registry) {
        this.pedidos = Counter.builder("pedidos.creados").description("Pedidos").register(registry);
    }
    public void crear() { pedidos.increment(); }
}
```

## 25. DevTools y herramientas de desarrollo

| Término | Significado |
|---|---|
| `spring-boot-devtools` | Mejora la productividad en desarrollo (se desactiva en el JAR de producción). |
| **Restart automático** | Reinicia la app cuando cambian las clases compiladas (más rápido que un arranque frío). |
| **LiveReload** | Recarga el navegador al cambiar recursos. |
| `spring.devtools.restart.enabled` | Activa/desactiva el reinicio. |
| `spring.devtools.restart.exclude` | Rutas que no provocan reinicio. |
| `spring.devtools.restart.additional-paths` | Rutas extra a vigilar. |
| `spring.devtools.livereload.enabled` | Activa LiveReload. |
| `spring.devtools.remote.*` | Desarrollo remoto. |
| **Valores por defecto de desarrollo** | Desactiva caché de plantillas, activa logs web `DEBUG`, etc. |
| `spring-boot-docker-compose` | Levanta automáticamente `compose.yaml` al ejecutar la app (Boot 3.1+). |
| `@ServiceConnection` | Conecta tests/contenedores sin configurar URL (Testcontainers). |
| **Lombok** | Reduce boilerplate (necesita plugin del IDE). |
| **Spring Initializr** | start.spring.io para generar proyectos. |
| `mvn spring-boot:run` | Ejecuta la app con Maven. |
| `mvn spring-boot:build-image` | Crea una imagen Docker con buildpacks. |
| `mvn spring-boot:start / stop` | Usado en tests de integración. |

### Ejemplo
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <scope>runtime</scope>
    <optional>true</optional>
</dependency>
```
```properties
spring.devtools.restart.exclude=static/**,public/**
spring.devtools.livereload.enabled=true
```

## 26. Empaquetado, ejecución y despliegue

| Término | Significado |
|---|---|
| **JAR ejecutable** | `.jar` con dependencias y servidor embebido: `java -jar app.jar`. **Forma recomendada.** |
| **WAR** | Archivo para desplegar en un Tomcat/servidor externo. |
| `<packaging>jar</packaging>` / `war` | Tipo de empaquetado en `pom.xml`. |
| `SpringBootServletInitializer` | Necesario para arrancar como WAR en servidor externo. |
| `mvn clean package` | Genera el JAR en `target/`. |
| `mvn clean package -DskipTests` | Empaqueta sin ejecutar tests. |
| `target/app-1.0.0.jar` | Artefacto resultante. |
| `target/app.jar.original` | JAR sin dependencias (antes del `repackage`). |
| **Layered JAR** | JAR por capas (dependencias, código) para imágenes Docker más eficientes. |
| **Buildpacks** | Crean imágenes de contenedor sin Dockerfile (`spring-boot:build-image`). |
| **Dockerfile** | Receta para construir la imagen. |
| **GraalVM native** | Ejecutable nativo: `mvn -Pnative native:compile`. |
| **CDS / AOT** | Optimizaciones de arranque. |
| **Graceful shutdown** | Apagado ordenado: termina las peticiones en curso (`server.shutdown=graceful`). |
| `server.shutdown` | `immediate` (defecto) o `graceful`. |
| `spring.lifecycle.timeout-per-shutdown-phase` | Tiempo máximo de espera al apagar. |
| **JVM options** | `-Xms512m -Xmx1g`, `-XX:+UseG1GC`. |
| `JAVA_TOOL_OPTIONS` | Variable de entorno con opciones JVM. |
| **systemd service** | Ejecutar el JAR como servicio de Linux. |
| **12-Factor App** | Principios para apps cloud: config por entorno, logs a stdout, procesos sin estado. |

### Ejemplos
```bash
# Compilar y empaquetar
mvn clean package

# Ejecutar
java -jar target/mi-app-1.0.0.jar
java -Xmx512m -jar target/mi-app-1.0.0.jar --spring.profiles.active=prod --server.port=9090

# Con Maven (desarrollo)
mvn spring-boot:run

# Imagen Docker sin Dockerfile
mvn spring-boot:build-image -Dspring-boot.build-image.imageName=miapp:1.0
docker run -p 8080:8080 -e SPRING_PROFILES_ACTIVE=prod miapp:1.0
```
```dockerfile
# Dockerfile sencillo
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/mi-app-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```
```java
// Para generar un WAR desplegable (además de packaging war en el pom)
@SpringBootApplication
public class MiApp extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(MiApp.class);
    }
}
```
```xml
<packaging>war</packaging>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-tomcat</artifactId>
    <scope>provided</scope>      <!-- el Tomcat externo lo provee -->
</dependency>
```
```properties
server.shutdown=graceful
spring.lifecycle.timeout-per-shutdown-phase=30s
```
```ini
# /etc/systemd/system/miapp.service
[Service]
User=app
ExecStart=/usr/bin/java -jar /opt/miapp/app.jar --spring.profiles.active=prod
SuccessExitStatus=143
Restart=always
```

### JAR vs WAR
| | JAR | WAR |
|---|---|---|
| Servidor | Embebido | Externo (Tomcat) |
| Ejecución | `java -jar` | Se despliega en el servidor |
| Recomendado | **Sí** | Solo si la empresa lo exige |

## 27. Arranque: hooks y ciclo de vida

| Término | Significado |
|---|---|
| `CommandLineRunner` | Interfaz con `run(String... args)`: código que se ejecuta **al terminar el arranque**. |
| `ApplicationRunner` | Igual, pero recibe `ApplicationArguments` (opciones `--x=y` parseadas). |
| `@Order` | Orden entre runners. |
| `ApplicationListener` | Escucha eventos de arranque. |
| `SpringApplicationRunListener` | Listener de bajo nivel (registrado en `spring.factories`). |
| `ApplicationContextInitializer` | Modifica el contexto **antes** de refrescarse. |
| `EnvironmentPostProcessor` | Modifica el `Environment` (propiedades) muy temprano. |
| `BeanFactoryPostProcessor` | Modifica definiciones de beans. |
| `SmartLifecycle` | Beans que se inician/detienen con el contexto. |
| `ApplicationAvailability` | Estado de la app: `LivenessState`, `ReadinessState`. |
| `AvailabilityChangeEvent` | Cambio de estado. |
| `ExitCodeGenerator` | Define el código de salida del proceso. |
| `SpringApplication.exit(ctx)` | Cierra la aplicación programáticamente. |
| `Runtime.addShutdownHook` | Hook de apagado de la JVM (Spring registra el suyo). |
| `@EventListener(ApplicationReadyEvent.class)` | Ejecutar algo cuando la app está **completamente lista**. |

### Orden del arranque
```
main()
 └ SpringApplication.run()
     ├ ApplicationStartingEvent
     ├ Prepara Environment (propiedades, perfiles)   → ApplicationEnvironmentPreparedEvent
     ├ Crea ApplicationContext
     ├ ApplicationContextInitializer
     ├ Carga definiciones de beans (scan + auto-config)
     ├ Crea beans singleton (constructor → inyección → @PostConstruct)
     ├ ContextRefreshedEvent / servidor web arranca
     ├ ApplicationStartedEvent
     ├ CommandLineRunner / ApplicationRunner
     └ ApplicationReadyEvent  ← ya recibe peticiones
```

### Ejemplos
```java
// Cargar datos iniciales al arrancar
@Component
@Order(1)
public class CargaInicial implements CommandLineRunner {
    private final UsuarioRepository repo;
    public CargaInicial(UsuarioRepository repo) { this.repo = repo; }

    @Override
    public void run(String... args) {
        if (repo.count() == 0) {
            repo.save(new Usuario("admin", "admin@mail.com"));
        }
    }
}

// ApplicationRunner con argumentos parseados: java -jar app.jar --modo=demo archivo.txt
@Component
public class MiRunner implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) {
        boolean demo = args.containsOption("modo");              // true
        List<String> modo = args.getOptionValues("modo");        // ["demo"]
        List<String> sueltos = args.getNonOptionArgs();          // ["archivo.txt"]
    }
}

// Runner como @Bean
@Bean
CommandLineRunner init(ProductoRepository repo) {
    return args -> repo.save(new Producto("Demo"));
}

// Tras el arranque completo
@Component
public class Notificar {
    @EventListener(ApplicationReadyEvent.class)
    public void listo() { System.out.println("¡Aplicación lista!"); }
}

// Modificar el Environment muy temprano (registrado en META-INF/spring.factories)
public class MiEnvPostProcessor implements EnvironmentPostProcessor {
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment env, SpringApplication app) {
        env.getSystemProperties().put("zona", "UTC");
    }
}

// Cerrar la app programáticamente
@Autowired ConfigurableApplicationContext ctx;
public void apagar() { SpringApplication.exit(ctx, () -> 0); }
```
```java
// Aplicación de consola (sin web) que hace algo y termina
@SpringBootApplication
public class Batch implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(Batch.class, args);
    }
    @Override
    public void run(String... args) {
        System.out.println("Procesando...");
    }                                  // al terminar run(), si no hay servidor web, la JVM finaliza
}
```
```properties
spring.main.web-application-type=none
```

## 28. Propiedades más usadas (referencia)

| Propiedad | Para qué sirve | Ejemplo |
|---|---|---|
| `spring.application.name` | Nombre de la app | `mi-api` |
| `spring.profiles.active` | Perfil activo | `dev` |
| `spring.config.import` | Importar configuración | `optional:file:./.env.properties` |
| `spring.main.banner-mode` | Banner | `off` |
| `spring.main.lazy-initialization` | Beans perezosos globales | `true` |
| `spring.main.web-application-type` | Tipo de app | `none` |
| `spring.main.allow-circular-references` | Permitir ciclos | `false` |
| `spring.main.allow-bean-definition-overriding` | Permitir sobrescribir definiciones de bean con mismo nombre | `false` |
| `spring.autoconfigure.exclude` | Excluir auto-configuraciones | `...DataSourceAutoConfiguration` |
| `server.port` | Puerto | `8080` (`0` = aleatorio) |
| `server.servlet.context-path` | Prefijo global | `/api` |
| `server.address` | Interfaz de escucha | `127.0.0.1` |
| `server.shutdown` | Apagado | `graceful` |
| `server.compression.enabled` | Comprimir respuestas | `true` |
| `server.error.include-message` | Mensaje en errores | `always` |
| `server.tomcat.threads.max` | Hilos máximos de Tomcat | `200` |
| `server.max-http-request-header-size` | Tamaño máximo de cabeceras | `16KB` |
| `spring.datasource.*` | Conexión a BD | ver glosario JPA |
| `spring.jpa.*` | JPA/Hibernate | ver glosario JPA |
| `spring.jackson.*` | JSON | `default-property-inclusion=non_null` |
| `spring.servlet.multipart.*` | Subida de archivos | `max-file-size=10MB` |
| `spring.mvc.*` | Spring MVC | |
| `spring.web.resources.*` | Recursos estáticos | |
| `spring.thymeleaf.cache` | Caché de plantillas | `false` en dev |
| `spring.cache.type` | Proveedor de caché | `caffeine` |
| `spring.task.execution.pool.*` | Pool de `@Async` | `core-size=4` |
| `spring.task.scheduling.pool.size` | Hilos para `@Scheduled` | `2` |
| `spring.security.user.*` | Usuario por defecto | `name`, `password`, `roles` |
| `spring.messages.basename` | Mensajes i18n | `messages` |
| `spring.output.ansi.enabled` | Colores en consola | `always` |
| `spring.lifecycle.timeout-per-shutdown-phase` | Espera al apagar | `30s` |
| `spring.devtools.restart.enabled` | Reinicio automático | `true` |
| `spring.docker.compose.*` | Docker Compose integrado | |
| `logging.level.*` | Niveles de log | `DEBUG` |
| `logging.file.name` | Archivo de log | `logs/app.log` |
| `management.endpoints.web.exposure.include` | Endpoints Actuator | `health,info` |
| `debug` | Informe de auto-configuración | `true` |
| `trace` | Logs TRACE del núcleo | `true` |

## 29. Ejemplo completo

Estructura de una aplicación Spring Boot mínima y bien organizada:
```
mi-app/
├── pom.xml
└── src/
    ├── main/
    │   ├── java/com/empresa/app/
    │   │   ├── MiAppApplication.java          ← @SpringBootApplication
    │   │   ├── config/
    │   │   │   ├── AppProperties.java         ← @ConfigurationProperties
    │   │   │   └── AppConfig.java             ← @Configuration + @Bean
    │   │   ├── controller/
    │   │   ├── service/
    │   │   ├── repository/
    │   │   ├── model/
    │   │   └── aspect/LogAspect.java
    │   └── resources/
    │       ├── application.yml
    │       ├── application-dev.yml
    │       ├── application-prod.yml
    │       ├── banner.txt
    │       ├── messages.properties
    │       └── static/ templates/
    └── test/java/com/empresa/app/
```

```java
// ---------- MiAppApplication ----------
@SpringBootApplication
@ConfigurationPropertiesScan
@EnableScheduling
@EnableAsync
public class MiAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(MiAppApplication.class, args);
    }
}

// ---------- AppProperties ----------
@ConfigurationProperties(prefix = "app")
@Validated
public record AppProperties(@NotBlank String nombre, @DefaultValue("30s") Duration timeout) {}

// ---------- AppConfig ----------
@Configuration
public class AppConfig {

    @Bean
    public RestClient restClient(RestClient.Builder builder, AppProperties props) {
        return builder.baseUrl("https://api.ejemplo.com").build();
    }

    @Bean
    @Profile("dev")
    public CommandLineRunner datosDemo(ProductoRepository repo) {
        return args -> repo.save(new Producto("Demo", BigDecimal.TEN));
    }
}

// ---------- Service con DI por constructor ----------
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository repo;
    private final ApplicationEventPublisher publisher;
    private final AppProperties props;

    @Transactional
    public Producto crear(Producto p) {
        Producto guardado = repo.save(p);
        log.info("Producto creado {} en {}", guardado.getId(), props.nombre());
        publisher.publishEvent(new ProductoCreadoEvent(guardado.getId()));
        return guardado;
    }
}

// ---------- Listener ----------
@Component
@Slf4j
public class ProductoListener {
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void alCrear(ProductoCreadoEvent e) { log.info("Reaccionando a producto {}", e.id()); }
}
```
```yaml
# application.yml
spring:
  application:
    name: mi-app
  profiles:
    default: dev
app:
  nombre: Mi App
  timeout: 20s
server:
  shutdown: graceful
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
```

## 30. Errores frecuentes de arranque

| Mensaje de error | Causa | Solución |
|---|---|---|
| `APPLICATION FAILED TO START ... Parameter 0 of constructor in X required a bean of type 'Y' that could not be found.` | Falta el bean `Y`. | Anotar `Y` con `@Component/@Service/@Repository`; comprobar que está dentro del paquete escaneado. |
| `required a single bean, but 2 were found` | Varios candidatos del mismo tipo. | `@Primary` o `@Qualifier`. |
| `The dependencies of some of the beans in the application context form a cycle` | Dependencia circular. | Rediseñar; `@Lazy`; eventos. |
| `Web server failed to start. Port 8080 was already in use.` | Puerto ocupado. | `server.port=8081` o cerrar el proceso que lo usa (`lsof -i :8080`). |
| `Failed to configure a DataSource: 'url' attribute is not specified` | Hay starter de datos pero no `spring.datasource.url`. | Configurar la BD, o excluir `DataSourceAutoConfiguration`. |
| `Cannot determine embedded database driver class` | Sin driver de BD en el classpath. | Añadir la dependencia del driver (`mysql-connector-j`, `h2`...). |
| `Could not resolve placeholder 'x' in value "${x}"` | Propiedad inexistente. | Definirla o usar `${x:valor}`. |
| `Binding to target ... failed` | `@ConfigurationProperties` mal enlazada (tipo o nombre). | Revisar nombres/tipos/validaciones. |
| `Unable to find a @SpringBootConfiguration` | Test sin clase principal localizable. | Poner el test en el mismo paquete (o subpaquete) que `@SpringBootApplication` o usar `classes=`. |
| `Unsupported class file major version 65` | La versión de Java no coincide con la que soporta la librería/plugin. | Alinear `java.version` y el JDK instalado. |
| `package javax.persistence does not exist` | Migración a Boot 3: ahora es `jakarta.*`. | Reemplazar `javax.*` por `jakarta.*`. |
| `ClassNotFoundException / NoClassDefFoundError` | Dependencia faltante o conflicto de versiones. | `mvn dependency:tree`; revisar scopes. |
| `NoSuchMethodError` | Dos versiones de una librería en conflicto. | Excluir la duplicada / usar versión gestionada por Boot. |
| `Invalid bean definition ... bean definition overriding` | Dos beans con el mismo nombre. | Renombrar uno; no habilitar overriding sin necesidad. |
| `Error creating bean with name 'X': Invocation of init method failed` | Excepción en `@PostConstruct`/`afterPropertiesSet`. | Ver "Caused by". |
| `Whitelabel Error Page` | Ruta sin mapear en una app web. | Revisar el `@RequestMapping` y el paquete del controlador. |
| `Main method not found` / `no main manifest attribute` | JAR no "repackageado". | Añadir `spring-boot-maven-plugin`; usar `mvn package`. |
| Los cambios de `application.properties` no se aplican | Se editó el de `src` pero se ejecuta una copia en `target`; o hay otra prioridad mayor (variable de entorno, argumento). | `mvn clean`; revisar el orden de precedencia. |
| `@Autowired` campo es `null` | La clase se creó con `new` en vez de dejar que Spring la gestione; o no es un bean. | Inyectarla; anotarla. |
| `@Transactional/@Async/@Cacheable` no hace nada | Self-invocation, método privado, o falta `@EnableX`. | Llamar desde otro bean; método público; activar la anotación. |
| `@Value` devuelve `null` en un campo `static` | `@Value` no se inyecta en `static`. | Usar inyección por setter de instancia. |
| `@Value` no funciona en una clase creada con `new` | No es un bean. | Convertirla en bean. |

### Checklist de depuración al arrancar
1. Lee el **último `Caused by:`** del stacktrace.
2. Ejecuta con `--debug` y revisa *Negative matches* / *Exclusions*.
3. Verifica que las clases están **dentro del paquete** de `@SpringBootApplication`.
4. Revisa `mvn dependency:tree` por duplicados.
5. Comprueba el perfil activo (`Active profiles` aparece en el log inicial).
6. Revisa la precedencia de propiedades (¿variable de entorno o argumento sobrescribiendo?).

---

## Tablas rápidas

### Anotaciones de Spring Core más importantes
| Anotación | Para qué |
|---|---|
| `@Component` / `@Service` / `@Repository` / `@Controller` | Declarar beans |
| `@Configuration` + `@Bean` | Declarar beans con código |
| `@Autowired` / `@Qualifier` / `@Primary` | Inyectar y desambiguar |
| `@Value` / `@ConfigurationProperties` | Leer configuración |
| `@Scope` / `@Lazy` / `@DependsOn` | Controlar creación |
| `@PostConstruct` / `@PreDestroy` | Ciclo de vida |
| `@Profile` / `@Conditional*` | Beans condicionales |
| `@EventListener` / `ApplicationEventPublisher` | Eventos |
| `@Aspect` / `@Around`... | AOP |
| `@Async` / `@Scheduled` / `@Cacheable` / `@Transactional` | Funciones basadas en proxy |

### Spring (Core) vs Spring Boot
| Tema | Spring (Core) | Spring Boot |
|---|---|---|
| Configuración | Manual (beans, XML o Java) | Automática (auto-config) |
| Dependencias | Eliges cada versión | Starters con versiones compatibles |
| Servidor | Externo (WAR) | Embebido (JAR) |
| Arranque | Código de contexto manual | `SpringApplication.run` |
| Propiedades | Tú las lees | `application.yml` + binding tipado |
| Monitoreo | No incluido | Actuator |
| Propósito | Contenedor IoC, AOP, etc. | Productividad y despliegue simple |

### Glosario de siglas
| Sigla | Significado |
|---|---|
| **IoC** | Inversion of Control |
| **DI** | Dependency Injection |
| **AOP** | Aspect-Oriented Programming |
| **SpEL** | Spring Expression Language |
| **POJO** | Plain Old Java Object |
| **BOM** | Bill of Materials |
| **JSR-330 / JSR-250** | Estándares Java de inyección (`@Inject`) / anotaciones comunes (`@PostConstruct`, `@Resource`) |
| **CGLIB** | Librería que genera proxies por subclase |
| **SLF4J** | Simple Logging Facade for Java |
| **MDC** | Mapped Diagnostic Context |
| **CLI** | Command-Line Interface |
| **AOT** | Ahead-Of-Time (compilación anticipada) |
| **CDS** | Class Data Sharing |
| **JVM / JDK / JRE** | Máquina virtual / kit de desarrollo / entorno de ejecución de Java |
| **12-Factor** | Metodología para apps en la nube |

---

> **Tip de lectura:** al abrir un proyecto Spring Boot, mira en este orden: **1)** `pom.xml` (qué starters usa) → **2)** la clase `@SpringBootApplication` (paquete raíz y anotaciones `@Enable...`) → **3)** `application.yml` y sus perfiles → **4)** las clases `@Configuration` (beans propios) → **5)** `@ConfigurationProperties` → **6)** los `CommandLineRunner`/listeners (qué ocurre al arrancar). Con eso entiendes cómo está armada la aplicación antes de leer una sola línea de negocio.

