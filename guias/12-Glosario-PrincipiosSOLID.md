# Glosario de Principios SOLID y Diseño Limpio en Java y Spring Boot

Este glosario reúne todos los términos, conceptos, patrones, *code smells* (malos olores de código) y ejemplos prácticos sobre los **Principios SOLID** (formulados por Robert C. Martin, *"Uncle Bob"*) aplicados directamente a **Java** y **Spring Boot**, junto con principios complementarios de diseño de software.

---

## Índice
1. [¿Qué es SOLID y por qué importa?](#1-qué-es-solid-y-por-qué-importa)
2. [Conceptos Base de Diseño Orientado a Objetos](#2-conceptos-base-de-diseño-orientado-a-objetos)
3. [S — Single Responsibility Principle (SRP)](#3-s--single-responsibility-principle-srp)
4. [O — Open/Closed Principle (OCP)](#4-o--openclosed-principle-ocp)
5. [L — Liskov Substitution Principle (LSP)](#5-l--liskov-substitution-principle-lsp)
6. [I — Interface Segregation Principle (ISP)](#6-i--interface-segregation-principle-isp)
7. [D — Dependency Inversion Principle (DIP)](#7-d--dependency-inversion-principle-dip)
8. [Cómo Spring Boot implementa SOLID de forma nativa](#8-cómo-spring-boot-implementa-solid-de-forma-nativa)
9. [Code Smells: Señales de que estás violando SOLID](#9-code-smells-señales-de-que-estás-violando-solid)
10. [Patrones de Diseño que hacen cumplir SOLID](#10-patrones-de-diseño-que-hacen-cumplir-solid)
11. [Principios Complementarios (DRY, KISS, YAGNI, LoD, Tell Don't Ask)](#11-principios-complementarios-dry-kiss-yagni-lod-tell-dont-ask)
12. [Composición sobre Herencia (Composition over Inheritance)](#12-composición-sobre-herencia-composition-over-inheritance)
13. [SOLID y el Testing (Unit Testing & Mocking)](#13-solid-y-el-testing-unit-testing--mocking)
14. [Arquitectura Limpia y Hexagonal con SOLID en Spring Boot](#14-arquitectura-limpia-y-hexagonal-con-solid-en-spring-boot)
15. [Caso Práctico Completo: Refactorizando un Servicio Monolítico a SOLID](#15-caso-práctico-completo-refactorizando-un-servicio-monolítico-a-solid)
16. [Resumen Rápido / Cheat Sheet de SOLID](#16-resumen-rápido--cheat-sheet-de-solid)

---

## 1. ¿Qué es SOLID y por qué importa?

**SOLID** es un acrónimo mnemotécnico que agrupa cinco principios fundamentales del diseño orientado a objetos:

| Letra | Acrónimo | Nombre en Inglés | Nombre en Español | Idea Central en 1 Frase | Ejemplo en Spring Boot |
|---|---|---|---|---|---|
| **S** | **SRP** | *Single Responsibility Principle* | Principio de Responsabilidad Única | Una clase debe tener **una sola razón para cambiar** (un único actor/propósito). | Separar `PedidoController` (HTTP), `PedidoService` (negocio), `PedidoRepository` (BD) y `EmailService` (notificación). |
| **O** | **OCP** | *Open/Closed Principle* | Principio de Abierto/Cerrado | El código debe estar **abierto para su extensión**, pero **cerrado para su modificación**. | Usar una interfaz `MetodoPagoStrategy` e inyectar `List<MetodoPagoStrategy>` en lugar de un `switch/if-else` gigante. |
| **L** | **LSP** | *Liskov Substitution Principle* | Principio de Sustitución de Liskov | Cualquier subclase o implementación debe poder **sustituir a su clase base/interfaz** sin romper el programa. | Cualquier implementación de `Notificador` (`EmailNotificador`, `SmsNotificador`) envía el mensaje sin lanzar `UnsupportedOperationException`. |
| **I** | **ISP** | *Interface Segregation Principle* | Principio de Segregación de Interfaces | Ningún cliente debe verse forzado a depender de **métodos que no utiliza**; mejor varias interfaces pequeñas que una gigante. | Separar `LecturaRepository` y `EscrituraRepository` en lugar de obligar a un servicio de reportes de solo lectura a tener `deleteAll()`. |
| **D** | **DIP** | *Dependency Inversion Principle* | Principio de Inversión de Dependencias | Los módulos de alto nivel no deben depender de los de bajo nivel; **ambos deben depender de abstracciones (interfaces)**. | `PedidoService` depende de la interfaz `PasarelaPago` y no de `StripeClientImpl` directamente (`new StripeClientImpl()`). |

**Ejemplo de qué pasa sin SOLID vs con SOLID:**
```java
// ❌ SIN SOLID: Acoplado, difícil de probar, difícil de cambiar
public class ProcesadorOrden {
    public void procesar(Orden orden) {
        // 1. Valida (SRP violado)
        // 2. Calcula impuestos con if/else por país (OCP violado)
        // 3. Instancia cliente concreto con 'new' (DIP violado)
        MySqlDatabase db = new MySqlDatabase();
        db.guardar(orden);
    }
}

// ✅ CON SOLID: Desacoplado, extensible y fácil de testear con Mocks
@Service
public class ProcesadorOrden {
    private final ValidadorOrden validador;
    private final CalculadoraImpuestos calculadora; // Interfaz (OCP / DIP)
    private final OrdenRepository repository;       // Interfaz (DIP)

    public ProcesadorOrden(ValidadorOrden validador,
                           CalculadoraImpuestos calculadora,
                           OrdenRepository repository) {
        this.validador = validador;
        this.calculadora = calculadora;
        this.repository = repository;
    }

    public void procesar(Orden orden) {
        validador.validar(orden);
        orden.setImpuesto(calculadora.calcular(orden));
        repository.guardar(orden);
    }
}
```

---

## 2. Conceptos Base de Diseño Orientado a Objetos

Antes de leer sobre SOLID, es clave dominar estos términos que aparecen constantemente en revisiones de código y arquitectura:

| Término | Significado | Ejemplo en Código |
|---|---|---|
| **Acoplamiento (*Coupling*)** | Grado de dependencia entre dos clases. Buscamos **Bajo Acoplamiento (*Loose/Low Coupling*)** para que cambiar una clase no obligue a cambiar otras diez. | ❌ Alto: `private StripeApi stripe = new StripeApi();`<br>✅ Bajo: `private final PasarelaPago pasarela;` |
| **Cohesión (*Cohesion*)** | Grado en que los elementos de una misma clase están relacionados entre sí y sirven a un mismo propósito. Buscamos **Alta Cohesión (*High Cohesion*)**. | ❌ Baja: `UtilidadesGenerales` (formatea fechas, manda mails y calcula IVA).<br>✅ Alta: `CalculadoraIva` (solo métodos relacionados al cálculo de IVA). |
| **Abstracción (*Abstraction*)** | Contrato que define **qué** se hace sin especificar **cómo** se hace (en Java: `interface` o `abstract class`). | `public interface AlmacenamientoArchivos { String subir(byte[] datos); }` |
| **Implementación Concreta (*Concrete Class*)** | Clase que define el **cómo** real y detallado de una abstracción. | `@Component public class S3Almacenamiento implements AlmacenamientoArchivos { ... }` |
| **Módulo de Alto Nivel (*High-Level Module*)** | Clase que contiene la lógica de negocio pura y las reglas de la aplicación (el *qué* hace el negocio). | `TransferenciaBancariaService`, `CheckoutUseCase`. |
| **Módulo de Bajo Nivel (*Low-Level Module*)** | Clase que lidia con detalles técnicos o de infraestructura (bases de datos, llamadas HTTP, sistema de archivos, SMTP). | `JpaCuentaRepository`, `SmtpEmailSender`, `TwilioSmsClient`. |
| **Rigidez (*Rigidity*)** | Síntoma de mal diseño donde un cambio pequeño obliga a modificar muchas partes del sistema en cascada. | Agregar un nuevo tipo de descuento obliga a tocar 12 clases con `switch(tipo)`. |
| **Fragilidad (*Fragility*)** | Síntoma donde un cambio en un lugar rompe partes del sistema que parecen no tener ninguna relación. | Modificar el formato de un reporte PDF rompe el guardado del usuario en BD porque estaban en la misma clase. |
| **Inmovilidad (*Immobility*)** | Dificultad para reutilizar un componente en otro proyecto o módulo porque arrastra demasiadas dependencias innecesarias. | Querer reusar un validador de RUT/DNI pero no poder porque recibe un `HttpServletRequest` y consulta a `UsuarioRepository`. |
| **Invariante (*Invariant*)** | Condición o regla que siempre debe mantenerse verdadera durante la vida de un objeto. | En una clase `CuentaBancaria`, la invariante es que `saldo >= 0` en todo momento. |

**Ejemplo de Cohesión y Acoplamiento:**
```java
// ❌ BAJA COHESIÓN Y ALTO ACOPLAMIENTO
public class UsuarioManager {
    private SmtpClient smtp = new SmtpClient("smtp.servidor.com"); // Alto acoplamiento

    public void registrarUsuario(Usuario u) { /* guarda en BD */ }
    public void enviarEmailBienvenida(String email) { smtp.send(email); }
    public byte[] exportarUsuariosAExcel() { /* genera archivo .xlsx */ return new byte[0]; }
}

// ✅ ALTA COHESIÓN Y BAJO ACOPLAMIENTO
@Service
public class RegistroUsuarioService {
    private final UsuarioRepository usuarioRepository; // Bajo acoplamiento (interfaz)
    private final NotificadorUsuario notificador;      // Bajo acoplamiento (interfaz)

    public RegistroUsuarioService(UsuarioRepository usuarioRepository, NotificadorUsuario notificador) {
        this.usuarioRepository = usuarioRepository;
        this.notificador = notificador;
    }

    public void registrar(Usuario u) {
        usuarioRepository.save(u);
        notificador.notificarBienvenida(u);
    }
}
```

---

## 3. S — Single Responsibility Principle (SRP)

> **"Una clase debe tener una, y solo una, razón para cambiar."**
> *(O dicho por Robert C. Martin más tarde: "Una clase debe ser responsable ante un, y solo un, actor o rol del negocio").*

### Conceptos Clave de SRP

| Término | Significado | Ejemplo Práctico |
|---|---|---|
| **Razón para cambiar** | Cambio en los requisitos de un área específica (ej: cambia la regla fiscal, cambia el diseño del PDF, cambia el motor de base de datos). | Si cambia cómo se diseña el PDF de la factura, `FacturaService` (que calcula impuestos) **no debería modificarse**. |
| **Actor** | Grupo de usuarios o interesados (*stakeholders*) que piden cambios sobre una funcionalidad (Contabilidad, Marketing, Operaciones, DBA). | Contabilidad pide cambiar el cálculo de horas extra; RRHH pide cambiar el formato del reporte de asistencia. Deben ser clases separadas. |
| **Separación de Preocupaciones (*Separation of Concerns - SoC*)** | Dividir un programa en secciones distintas donde cada una aborda una preocupación diferente (presentación, negocio, persistencia, mapeo). | `FacturaController` (HTTP), `FacturaMapper` (DTO <-> Entidad), `FacturaService` (Negocio), `FacturaRepository` (SQL). |
| **God Class / Clase Todopoderosa** | Antipatión clásico que viola SRP: una clase de miles de líneas que valida, convierte, guarda en BD, llama a APIs externas y envía correos. | `AppService` con 45 métodos y 18 dependencias inyectadas en el constructor. |

### Ejemplo en Java / Spring Boot (Violación vs Solución)

```java
// ❌ MALO: Viola SRP (Esta clase tiene 4 razones distintas para cambiar)
@Service
public class FacturaServiceMalo {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void crearFactura(Factura exam) {
        // Razón 1: Reglas de negocio e impuestos (Finanzas)
        double total = exam.getSubtotal() * 1.21;
        exam.setTotal(total);

        // Razón 2: Persistencia SQL directa (Infraestructura / DBA)
        jdbcTemplate.update("INSERT INTO facturas (id, total) VALUES (?, ?)", exam.getId(), total);

        // Razón 3: Generación de documento PDF (Diseño / Impresión)
        String pdfContent = "=== FACTURA #" + exam.getId() + " === Total: $" + total;
         generarArchivoPdf(pdfContent);

        // Razón 4: Envío de correo electrónico (Marketing / Comunicaciones)
        enviarSmtpEmail(exam.getClienteEmail(), "Su factura está lista", pdfContent);
    }

    private void generarArchivoPdf(String content) { /* lógica de iText/PDFBox */ }
    private void enviarSmtpEmail(String to, String subject, String body) { /* lógica de JavaMail */ }
}
```

```java
// ✅ BUENO: Cumple SRP (Cada clase tiene una única responsabilidad clara)

// 1. Responsabilidad: Calcular impuestos y totales de facturación
@Component
public class CalculadoraImpuestosFactura {
    private static final double IVA = 0.21;

    public double calcularTotalConIva(double subtotal) {
        return subtotal + (subtotal * IVA);
    }
}

// 2. Responsabilidad: Persistir en Base de Datos
@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long> {
}

// 3. Responsabilidad: Generar la representación en PDF
@Component
public class GeneradorFacturaPdf {
    public byte[] generarPdf(Factura factura) {
        // Única razón para cambiar: cambia el diseño o librería del PDF
        return ("Factura #" + factura.getId() + " - Total: " + factura.getTotal()).getBytes();
    }
}

// 4. Responsabilidad: Enviar notificaciones por correo
@Service
public class EmailNotificacionService {
    public void enviarFacturaPorEmail(String emailDestino, byte[] pdfAdjunto) {
        // Única razón para cambiar: cambia el proveedor o plantilla de email
    }
}

// 5. Responsabilidad: Orquestar el caso de uso de emisión de factura
@Service
public class EmitirFacturaUseCase {
    private final CalculadoraImpuestosFactura calculadora;
    private final FacturaRepository repository;
    private final GeneradorFacturaPdf generadorPdf;
    private final EmailNotificacionService emailService;

    public EmitirFacturaUseCase(CalculadoraImpuestosFactura calculadora,
                                FacturaRepository repository,
                                GeneradorFacturaPdf generadorPdf,
                                EmailNotificacionService emailService) {
        this.calculadora = calculadora;
        this.repository = repository;
        this.generadorPdf = generadorPdf;
        this.emailService = emailService;
    }

    @Transactional
    public Factura emitir(Factura factura) {
        factura.setTotal(calculadora.calcularTotalConIva(factura.getSubtotal()));
        Factura guardada = repository.save(factura);
        byte[] pdf = generadorPdf.generarPdf(guardada);
        emailService.enviarFacturaPorEmail(guardada.getClienteEmail(), pdf);
        return guardada;
    }
}
```

---

## 4. O — Open/Closed Principle (OCP)

> **"Las entidades de software (clases, módulos, funciones) deben estar abiertas para su extensión, pero cerradas para su modificación."**

### Conceptos Clave de OCP

| Término | Significado | Ejemplo Práctico |
|---|---|---|
| **Abierto para extensión** | Puedes agregar nuevo comportamiento o soportar nuevos casos en el sistema cuando cambian los requerimientos. | Agregar el método de pago `MercadoPago` o `Crypto` creando una nueva clase `@Component`. |
| **Cerrado para modificación** | Para agregar ese nuevo comportamiento, **no tienes que abrir ni editar el código fuente de las clases existentes** que ya fueron probadas y funcionan en producción. | `ProcesadorPagosService` no sufre ni una sola línea de cambio cuando agregas `ApplePayStrategy`. |
| **Polimorfismo** | Mecanismo clave para lograr OCP: invocar un mismo método sobre una interfaz y que cada objeto ejecute su propia implementación. | `estrategiaDescuento.aplicar(precio)` en vez de `if (tipo.equals("VIP")) ... else if ...` |
| **Inyección de Colecciones en Spring** | Característica de Spring donde al inyectar `List<MiInterfaz>` o `Map<String, MiInterfaz>`, Spring reúne automáticamente todos los `@Component` que implementan esa interfaz. | `public CalculadoraEnvioService(List<CotizadorEnvio> cotizadores)` |

### Ejemplo en Java / Spring Boot (Violación vs Solución)

```java
// ❌ MALO: Viola OCP
// Cada vez que la empresa agrega un nuevo tipo de cliente o promoción,
// hay que modificar esta clase, arriesgando romper los cálculos existentes.
@Service
public class CalculadoraDescuentoMala {

    public double calcularDescuento(String tipoCliente, double monto) {
        if ("REGULAR".equals(tipoCliente)) {
            return monto * 0.05;
        } else if ("VIP".equals(tipoCliente)) {
            return monto * 0.15;
        } else if ("EMPLEADO".equals(tipoCliente)) {
            return monto * 0.30;
        } else if ("BLACK_FRIDAY".equals(tipoCliente)) {
            return monto * 0.50;
        }
        return 0;
    }
}
```

```java
// ✅ BUENO: Cumple OCP usando Polimorfismo e Inyección de Listas de Spring

// 1. Definimos el contrato (Abstracción)
public interface ReglaDescuento {
    boolean aplicaPara(String tipoCliente);
    double calcular(double monto);
}

// 2. Extendemos el sistema creando nuevas clases sin tocar las demás
@Component
public class DescuentoClienteRegular implements ReglaDescuento {
    @Override
    public boolean aplicaPara(String tipoCliente) { return "REGULAR".equalsIgnoreCase(tipoCliente); }

    @Override
    public double calcular(double monto) { return monto * 0.05; }
}

@Component
public class DescuentoClienteVip implements ReglaDescuento {
    @Override
    public boolean aplicaPara(String tipoCliente) { return "VIP".equalsIgnoreCase(tipoCliente); }

    @Override
    public double calcular(double monto) { return monto * 0.15; }
}

// Si mañana piden "JUBILADO", solo creamos DescuentoJubilado implements ReglaDescuento.
// ¡La clase CalculadoraDescuentoService de abajo NUNCA se modifica!

// 3. Clase CERRADA a modificación pero ABIERTA a extensión
@Service
public class CalculadoraDescuentoService {

    private final List<ReglaDescuento> reglas;

    // Spring inyecta automáticamente todas las clases @Component que implementen ReglaDescuento
    public CalculadoraDescuentoService(List<ReglaDescuento> reglas) {
        this.reglas = reglas;
    }

    public double calcularDescuento(String tipoCliente, double monto) {
        return reglas.stream()
                .filter(regla -> regla.aplicaPara(tipoCliente))
                .findFirst()
                .map(regla -> regla.calcular(monto))
                .orElse(0.0);
    }
}
```

---

## 5. L — Liskov Substitution Principle (LSP)

> **"Si `S` es un subtipo de `T`, entonces los objetos de tipo `T` en un programa pueden ser reemplazados por objetos de tipo `S` sin alterar ninguna de las propiedades deseables de ese programa."** *(Barbara Liskov, 1987).*

### Conceptos Clave de LSP

| Término | Significado | Ejemplo Práctico |
|---|---|---|
| **Sustituibilidad** | Quien usa una referencia a una clase padre o interfaz (`Ave`, `Cuenta`, `Repositorio`) debe poder usar cualquier clase hija sin saber cuál es y sin sufrir errores inesperados. | Si un método recibe `List<String>`, debe funcionar igual si le pasas `ArrayList` o `LinkedList`. |
| **Precondiciones (*Preconditions*)** | Condiciones que deben cumplirse *antes* de llamar a un método. **Una subclase NO puede exigir precondiciones más estrictas** que la clase padre. | Si `Pago.procesar(double monto)` acepta cualquier `monto > 0`, una subclase no puede fallar si `monto < 1000`. |
| **Postcondiciones (*Postconditions*)** | Garantías que devuelve el método *después* de ejecutarse. **Una subclase NO puede entregar postcondiciones más débiles** que la clase padre. | Si la interfaz promete devolver una lista nunca nula (`@NonNull`), la subclase no puede devolver `null`. |
| **Excepción `UnsupportedOperationException`** | La señal más clara de violación de LSP: heredar de una clase o implementar una interfaz y dejar métodos vacíos o lanzando error porque "este hijo no hace eso". | `class Pinguino extends Ave { void volar() { throw new UnsupportedOperationException(); } }` |
| **Problema Rectángulo-Cuadrado / Ave-Pingüino** | Ejemplos académicos donde una relación *"es-un"* del mundo real falla en programación porque el comportamiento del hijo rompe las suposiciones del padre. | En geometría un Cuadrado "es un" Rectángulo, pero en código `setAncho(5)` en un Cuadrado cambia también el alto, rompiendo los tests que esperan el comportamiento de un Rectángulo. |

### Ejemplo en Java / Spring Boot (Violación vs Solución)

```java
// ❌ MALO: Viola LSP
// Tenemos una interfaz o clase base para cuentas bancarias
public abstract class CuentaBancaria {
    protected BigDecimal saldo = BigDecimal.ZERO;

    public void depositar(BigDecimal monto) {
        this.saldo = this.saldo.add(monto);
    }

    public abstract void retirar(BigDecimal monto);
}

public class CuentaCorriente extends CuentaBancaria {
    @Override
    public void retirar(BigDecimal monto) {
        this.saldo = this.saldo.subtract(monto);
    }
}

// Un Plazo Fijo NO permite retiros antes del vencimiento, pero heredó de CuentaBancaria:
public class CuentaPlazoFijoMala extends CuentaBancaria {
    @Override
    public void retirar(BigDecimal monto) {
        // ¡VIOLACIÓN DE LSP! Rompe el contrato prometido por CuentaBancaria
        throw new UnsupportedOperationException("No se puede retirar de un plazo fijo");
    }
}

// Cuando un servicio usa el polimorfismo confiando en la clase base, explota en producción:
@Service
public class CajeroAutomaticoService {
    public void extraerDinero(CuentaBancaria cuenta, BigDecimal monto) {
        cuenta.retirar(monto); // ¡Lanza excepción si cuenta es CuentaPlazoFijoMala!
    }
}
```

```java
// ✅ BUENO: Cumple LSP separando las jerarquías según su comportamiento real

// 1. Toda cuenta permite consultar saldo y depositar
public interface CuentaDepositable {
    void depositar(BigDecimal monto);
    BigDecimal getSaldo();
}

// 2. Solo las cuentas que realmente permiten extracciones implementan esta interfaz
public interface CuentaRetirable extends CuentaDepositable {
    void retirar(BigDecimal monto);
}

public class CuentaCorriente implements CuentaRetirable {
    private BigDecimal saldo = BigDecimal.ZERO;

    @Override
    public void depositar(BigDecimal monto) { this.saldo = this.saldo.add(monto); }

    @Override
    public void retirar(BigDecimal monto) { this.saldo = this.saldo.subtract(monto); }

    @Override
    public BigDecimal getSaldo() { return saldo; }
}

public class CuentaPlazoFijo implements CuentaDepositable {
    private BigDecimal saldo = BigDecimal.ZERO;

    @Override
    public void depositar(BigDecimal monto) { this.saldo = this.saldo.add(monto); }

    @Override
    public BigDecimal getSaldo() { return saldo; }
    // No tiene el método retirar(), por lo que jamás promete algo que no puede cumplir.
}

// 3. Ahora el Cajero solo acepta CuentaRetirable: 100% seguro en tiempo de compilación
@Service
public class CajeroAutomaticoSeguroService {
    public void extraerDinero(CuentaRetirable cuenta, BigDecimal monto) {
        cuenta.retirar(monto); // Cualquier implementación de CuentaRetirable funcionará sin sorpresas
    }
}
```

---

## 6. I — Interface Segregation Principle (ISP)

> **"Los clientes no deben verse obligados a depender de interfaces (o métodos) que no utilizan."**

### Conceptos Clave de ISP

| Término | Significado | Ejemplo Práctico |
|---|---|---|
| **Interfaz Gorda (*Fat / Polluted Interface*)** | Interfaz con demasiados métodos que agrupa responsabilidades de distintos tipos de clientes en un solo archivo. | `UsuarioOperations` con 25 métodos: login, CRUD, cambio de clave, exportación CSV, auditoría y envío de SMS. |
| **Interfaz de Rol (*Role Interface*)** | Interfaz pequeña y cohesiva enfocada exactamente en lo que necesita un cliente o caso de uso específico. | `Autenticable`, `ExportableCsv`, `Auditable`. |
| **Relación entre ISP y LSP** | Cuando una interfaz es demasiado grande (viola ISP), las clases que la implementan suelen dejar métodos vacíos o lanzar `UnsupportedOperationException` (violando también LSP). | Obligar a un `ImpresoraSimple` a implementar `escanear()` y `enviarFax()`. |
| **Interfaces Funcionales (`@FunctionalInterface`)** | El extremo positivo de ISP en Java: interfaces de un único método abstracto (SAM), como `Runnable`, `Callable`, `Predicate<T>`, `Function<T,R>`. | `public interface Validador<T> { boolean esValido(T item); }` |

### Ejemplo en Java / Spring Boot (Violación vs Solución)

```java
// ❌ MALO: Viola ISP (Interfaz "Gorda" / Fat Interface)
public interface TrabajadorOficina {
    void programarCodigo();
    void gestionarPresupuesto();
    void limpiarInstalaciones();
    void tomarDescansoParaCafe();
}

// Un Robot de limpieza se ve obligado a implementar métodos que no tienen sentido para él:
public class RobotLimpieza implements TrabajadorOficina {
    @Override
    public void programarCodigo() { /* No hace nada */ }

    @Override
    public void gestionarPresupuesto() { /* No hace nada */ }

    @Override
    public void limpiarInstalaciones() {
        System.out.println("Limpiando piso con sensores láser...");
    }

    @Override
    public void tomarDescansoParaCafe() {
        throw new UnsupportedOperationException("Los robots no toman café");
    }
}
```

```java
// ✅ BUENO: Cumple ISP segregando en interfaces pequeñas por capacidad/rol

public interface Programador {
    void programarCodigo();
}

public interface GestorPresupuesto {
    void gestionarPresupuesto();
}

public interface PersonalLimpieza {
    void limpiarInstalaciones();
}

public interface TrabajadorHumano {
    void tomarDescansoParaCafe();
}

// Cada clase implementa ÚNICAMENTE los contratos que realmente utiliza y soporta:
@Component
public class DesarrolladorSenior implements Programador, TrabajadorHumano {
    @Override
    public void programarCodigo() { System.out.println("Escribiendo microservicio en Spring Boot..."); }

    @Override
    public void tomarDescansoParaCafe() { System.out.println("Tomando un espresso..."); }
}

@Component
public class RobotAspiradora implements PersonalLimpieza {
    @Override
    public void limpiarInstalaciones() { System.out.println("Aspirando oficina..."); }
}
```

**Ejemplo real en Spring Data (Segregación de Repositorios):**
```java
// En vez de exponer JpaRepository (que tiene deleteAll, deleteById, flush, saveAll)
// a servicios que SOLO deben consultar datos, puedes crear una interfaz segregada de solo lectura:
@NoRepositoryBean
public interface SoloLecturaRepository<T, ID> extends Repository<T, ID> {
    Optional<T> findById(ID id);
    List<T> findAll();
}

public interface ProductoConsultaRepository extends SoloLecturaRepository<Producto, Long> {
    List<Producto> findByCategoria(String categoria);
    // ¡Este repositorio NO tiene métodos save() ni delete()!
    // Ningún servicio que lo inyecte podrá borrar productos por error.
}
```

---

## 7. D — Dependency Inversion Principle (DIP)

> **1. "Los módulos de alto nivel no deben depender de los módulos de bajo nivel. Ambos deben depender de abstracciones."**
> **2. "Las abstracciones no deben depender de los detalles. Los detalles deben depender de las abstracciones."**

### Conceptos Clave de DIP

| Término | Significado | Ejemplo Práctico |
|---|---|---|
| **DIP vs DI vs IoC** | No son lo mismo, pero trabajan juntos:<br>- **DIP**: Principio de diseño (depender de interfaces).<br>- **IoC (Inversión de Control)**: El framework controla el flujo y ciclo de vida, no tú.<br>- **DI (Inyección de Dependencias)**: Técnica de pasarle a una clase sus dependencias desde afuera (por constructor). | - **DIP**: `private final Notificador notificador;` (interfaz).<br>- **DI**: Recibir `notificador` en el constructor.<br>- **IoC**: Spring crea el bean y lo inyecta al arrancar. |
| **El operador `new` es pegamento** | Hacer `new ServicioConcreto()` dentro de tu lógica de negocio acopla tu clase de forma rígida a esa implementación exacta, impidiendo usar Mocks en tests o cambiar de proveedor. | ❌ `var s3 = new AmazonS3Client();`<br>✅ `private final FileStorage storage;` |
| **Propiedad de la Interfaz** | Según DIP puro, la interfaz pertenece al **módulo de alto nivel** (el negocio define qué necesita: `RepositorioPedidos`), y el módulo de bajo nivel (JPA, Mongo, API) se adapta implementándola. | El paquete `domain` define `interface PagoPort` y el paquete `infrastructure` define `class StripeAdapter implements PagoPort`. |

### Ejemplo en Java / Spring Boot (Violación vs Solución)

```java
// ❌ MALO: Viola DIP
// El módulo de alto nivel (Servicio de Alertas) depende directamente del módulo de bajo nivel (Cliente Twilio SMS)
public class TwilioSmsClient {
    public void enviarSms(String telefono, String texto) {
        System.out.println("Enviando SMS por Twilio a " + telefono + ": " + texto);
    }
}

@Service
public class AlertaSeguridadServiceMalo {
    // Dependencia directa de una clase concreta (detalle de bajo nivel)
    private final TwilioSmsClient twilioClient = new TwilioSmsClient();

    public void dispararAlerta(String usuario, String mensaje) {
        // Si mañana cambiamos Twilio por AWS SNS, WhatsApp o Telegram,
        // estamos obligados a modificar la lógica de negocio.
        twilioClient.enviarSms("+54911123456", "ALERTA para " + usuario + ": " + mensaje);
    }
}
```

```java
// ✅ BUENO: Cumple DIP

// 1. Abstracción (definida según lo que necesita el negocio, sin mencionar Twilio ni SMS)
public interface CanalNotificacionAlerta {
    void enviarAlerta(String destinatario, String mensaje);
}

// 2. Detalle de Bajo Nivel 1: Implementación con Twilio
@Component
@Primary
public class TwilioNotificacionAdapter implements CanalNotificacionAlerta {
    @Override
    public void enviarAlerta(String destinatario, String mensaje) {
        System.out.println("Enviando SMS vía Twilio a " + destinatario + ": " + mensaje);
    }
}

// 3. Detalle de Bajo Nivel 2: Implementación con Slack / Discord
@Component("slackAlerta")
public class SlackNotificacionAdapter implements CanalNotificacionAlerta {
    @Override
    public void enviarAlerta(String destinatario, String mensaje) {
        System.out.println("Enviando mensaje a canal de Slack para " + destinatario + ": " + mensaje);
    }
}

// 4. Módulo de Alto Nivel: Depende ÚNICAMENTE de la abstracción (interfaz)
@Service
public class AlertaSeguridadService {

    private final CanalNotificacionAlerta canalNotificacion;

    // Inyección por constructor (DI): No sabe ni le importa si por detrás es Twilio, Slack o un Mock de test
    public AlertaSeguridadService(CanalNotificacionAlerta canalNotificacion) {
        this.canalNotificacion = canalNotificacion;
    }

    public void dispararAlerta(String destinatario, String mensaje) {
        canalNotificacion.enviarAlerta(destinatario, "ALERTA CRÍTICA: " + mensaje);
    }
}
```

---

## 8. Cómo Spring Boot implementa SOLID de forma nativa

Spring Framework fue diseñado desde su origen siguiendo los principios SOLID. Cuando lees código de Spring Boot, ves SOLID en acción en estas herramientas:

| Principio | Mecanismo en Spring Boot | Cómo lo usa Spring | Ejemplo en tu Código |
|---|---|---|---|
| **SRP** | Esterotipos de capas (`@RestController`, `@Service`, `@Repository`, `@Component`) y AOP (`@Transactional`, `@Async`). | Separa el manejo de transacciones, seguridad y caché en *Proxies AOP* para que tu servicio solo tenga código de negocio. | Poner `@Transactional` en el método en lugar de escribir `conn.setAutoCommit(false)` y `conn.commit()` mezclado con tu negocio. |
| **OCP** | Inyección de colecciones (`List<T>`), `BeanPostProcessor`, `HandlerInterceptor`, `SecurityFilterChain`. | Permite agregar nuevos filtros HTTP, convertidores JSON o validadores sin modificar el código interno de Spring. | Crear un `@ControllerAdvice` nuevo para capturar excepciones sin tocar los controladores existentes. |
| **LSP** | Abstracciones de infraestructura (`PlatformTransactionManager`, `Resource`, `CacheManager`, `UserDetailsService`). | Spring puede cambiar de `JpaTransactionManager` a `DataSourceTransactionManager` o de `RedisCacheManager` a `CaffeineCacheManager` sin que tu código se entere. | Cambiar `@Cacheable("productos")` de memoria local a Redis solo cambiando `application.yml`, sin tocar el servicio. |
| **ISP** | Interfaces granulares en Spring Core y Spring Data (`CrudRepository`, `PagingAndSortingRepository`, `ListCrudRepository`, `InitializingBean`). | Divide las capacidades en interfaces pequeñas para que solo implementes o extiendas lo que necesitas. | Extender `PagingAndSortingRepository` cuando solo necesitas paginación y no todas las utilidades específicas de JPA. |
| **DIP** | Contenedor IoC (`ApplicationContext`), Inyección de Dependencias por constructor, `@Primary`, `@Qualifier`, `@Profile`. | Los servicios declaran interfaces en sus constructores y el contenedor IoC de Spring resuelve e inyecta la implementación concreta en tiempo de ejecución. | Usar `@Profile("dev") class PagoSimulado implements PasarelaPago` y `@Profile("prod") class PagoReal implements PasarelaPago`. |

**Ejemplo de DIP + OCP + LSP combinados con `@Profile` en Spring Boot:**
```java
public interface ServicioAlmacenamiento {
    String guardarArchivo(String nombre, byte[] contenido);
}

@Component
@Profile("local") // Se inyecta automáticamente cuando corres en tu PC
public class AlmacenamientoDiscoLocal implements ServicioAlmacenamiento {
    @Override
    public String guardarArchivo(String nombre, byte[] contenido) {
        return "/tmp/uploads/" + nombre;
    }
}

@Component
@Profile("prod") // Se inyecta automáticamente en el servidor de producción
public class AlmacenamientoAwsS3 implements ServicioAlmacenamiento {
    @Override
    public String guardarArchivo(String nombre, byte[] contenido) {
        return "https://mi-bucket.s3.amazonaws.com/" + nombre;
    }
}
```

---

## 9. Code Smells: Señales de que estás violando SOLID

Cuando leas código existente, estos "malos olores" (*Code Smells*) te indicarán inmediatamente qué principio SOLID se está rompiendo:

| Code Smell (Señal de Alerta) | Qué Principio Viola | Cómo se ve en el código | Cómo solucionarlo (Refactoring) |
|---|---|---|---|
| **Clase de más de 500 líneas o con >7 dependencias en el constructor** | **SRP** | Un `UsuarioService` que inyecta 10 repositorios, `JavaMailSender`, `RestTemplate`, `ObjectMapper` y `PasswordEncoder`. | Dividir en casos de uso específicos: `RegistrarUsuarioUseCase`, `RecuperarPasswordUseCase`, `ActualizarPerfilUseCase`. |
| **Nombres de clase vagos (`Manager`, `Processor`, `Helper`, `Util`)** | **SRP** | `DataHelper`, `CommonUtils`, `OrderManager` donde cae cualquier método que nadie sabía dónde poner. | Nombrar las clases por su responsabilidad específica: `ValidadorFormatoCuit`, `CalculadoraCostoEnvio`. |
| **Cascadas de `if / else if` o `switch` preguntando por el tipo** | **OCP** | `if (pago.getTipo() == Tipo.TARJETA) ... else if (pago.getTipo() == Tipo.EFECTIVO)` repetido en 5 clases. | Aplicar el **Patrón Strategy**: crear una interfaz `EstrategiaPago` y una clase por cada tipo. |
| **Uso de `instanceof` y casteos manuales** | **OCP / LSP** | `if (notificador instanceof EmailNotificador) { ((EmailNotificador) notificador).setAsunto(...); }` | Mover el comportamiento común a la interfaz base para que el cliente no tenga que adivinar el subtipo concreto. |
| **Métodos que lanzan `UnsupportedOperationException` o están vacíos `// TODO: no aplica`** | **LSP / ISP** | `@Override public void exportarPdf() { throw new UnsupportedOperationException("No soportado"); }` | Segregar la interfaz (ISP) o corregir la jerarquía de herencia para que solo implementen el método quienes lo soporten. |
| **Interfaces con decenas de métodos donde los clientes solo usan 1 o 2** | **ISP** | Implementar una interfaz de 15 métodos en un test o adaptador y tener que escribir `return null;` en 13 de ellos. | Dividir la interfaz grande en 3 o 4 interfaces pequeñas enfocadas en cada rol. |
| **Uso de `new` para instanciar servicios, clientes HTTP o repositorios dentro de un método** | **DIP** | `RestTemplate rt = new RestTemplate();` o ` Validador v = new Validador();` dentro de `procesarPedido()`. | Declarar el colaborador como atributo `private final` e inyectarlo por constructor para que Spring lo administre. |
| **Llamar a métodos estáticos con efectos secundarios (`EmailUtil.send()`, `Database.save()`)** | **DIP** | `ReporteStaticHelper.enviarAlServidor(datos);` (imposible de reemplazar o mockear en un test unitario sin herramientas pesadas). | Convertir la clase estática en un `@Component` o `@Service` que implemente una interfaz e inyectarlo. |

**Ejemplo de detección rápida en código:**
```java
// ❌ 3 Violaciones en 10 líneas de código:
public void procesar(Notificacion n) {
    if (n instanceof NotificacionPush) { // <-- Violación de OCP y LSP (instanceof)
        FirebaseClient client = new FirebaseClient(); // <-- Violación de DIP (new de cliente externo)
        String token =formatearToken(n.getDestino()); // <-- Violación de SRP (formateo técnico en el orquestador)
        client.sendPush(token, n.getMensaje());
    }
}

// ✅ Código limpio refactorizado:
public void procesar(Notificacion n) {
    // Polimorfismo puro: el canal correcto se encarga sin if, sin instanceof y sin new
    canalNotificacion.enviar(n);
}
```

---

## 10. Patrones de Diseño que hacen cumplir SOLID

Los patrones de diseño (GoF - *Gang of Four*) son fórmulas probadas que aplican los principios SOLID en la práctica:

| Patrón de Diseño | Principios SOLID que aplica | Propósito | Ejemplo en Spring Boot |
|---|---|---|---|
| **Strategy (Estrategia)** | **OCP, DIP, LSP** | Definir una familia de algoritmos, encapsular cada uno en su propia clase y hacerlos intercambiables. | Diferentes formas de calcular el costo de envío (`EnvioCorreoArgentino`, `EnvioAndreani`, `EnvioRetiroEnTienda`). |
| **Adapter (Adaptador)** | **DIP, SRP, OCP** | Envolver una librería o API externa para que cumpla con la interfaz que define tu dominio de negocio. | `MercadoPagoAdapter implements PasarelaPagoPort` (traduce tu objeto `Pago` al SDK externo de MercadoPago). |
| **Decorator (Decorador)** | **OCP, SRP** | Agregar responsabilidades adicionales a un objeto dinámicamente envolviéndolo sin modificar su código original. | Agregar caché o logging a un servicio envolviendo la interfaz original: `RepositorioConCache implements ProductoRepository`. |
| **Factory Method / Simple Factory** | **OCP, DIP, SRP** | Centralizar la creación o selección de qué estrategia/objeto concreto usar según un parámetro. | `NotificadorFactory.obtenerPorCanal(TipoCanal.WHATSAPP)` usando un `Map<TipoCanal, Notificador>`. |
| **Facade (Fachada)** | **SRP, ISP** | Proveer una interfaz simple y unificada frente a un subsistema complejo de muchas clases. | `CheckoutFacade` que coordina internamente `InventarioService`, `PagoService` y `EnvioService`. |
| **Template Method** | **OCP, DRY** | Definir el esqueleto de un algoritmo en una clase base y dejar que las subclases redefinan ciertos pasos sin cambiar la estructura general. | `JdbcTemplate`, `RestTemplate` o un `GeneradorReporteBase` que fija los pasos: `1. obtenerDatos()`, `2. formatear()`, `3. exportar()`. |

**Ejemplo del Patrón Strategy + Factory con `Map` en Spring Boot (El patrón más útil para OCP):**
```java
public enum TipoMetodoPago {
    TARJETA_CREDITO, TRANSFERENCIA, MERCADO_PAGO
}

// 1. Interfaz Strategy
public interface EstrategiaPago {
    TipoMetodoPago getTipo();
    ReciboPago procesarPago(BigDecimal monto);
}

// 2. Implementaciones concretas
@Component
public class PagoTarjetaStrategy implements EstrategiaPago {
    @Override
    public TipoMetodoPago getTipo() { return TipoMetodoPago.TARJETA_CREDITO; }

    @Override
    public ReciboPago procesarPago(BigDecimal monto) {
        return new ReciboPago("Aprobado con Tarjeta por $" + monto);
    }
}

@Component
public class PagoTransferenciaStrategy implements EstrategiaPago {
    @Override
    public TipoMetodoPago getTipo() { return TipoMetodoPago.TRANSFERENCIA; }

    @Override
    public ReciboPago procesarPago(BigDecimal monto) {
        return new ReciboPago("Aprobado por Transferencia CBU por $" + monto);
    }
}

// 3. Factory / Contexto que registra todas las estrategias en un Map al iniciar Spring
@Service
public class ProcesadorPagosService {

    private final Map<TipoMetodoPago, EstrategiaPago> estrategias;

    // Spring inyecta la lista de todas las estrategias y las guardamos en un Map por su Enum
    public ProcesadorPagosService(List<EstrategiaPago> listaEstrategias) {
        this.estrategias = listaEstrategias.stream()
                .collect(Collectors.toMap(EstrategiaPago::getTipo, Function.identity()));
    }

    public ReciboPago cobrar(TipoMetodoPago tipo, BigDecimal monto) {
        EstrategiaPago estrategia = estrategias.get(tipo);
        if (estrategia == null) {
            throw new IllegalArgumentException("Método de pago no soportado: " + tipo);
        }
        return estrategia.procesarPago(monto); // Cero if/else, búsqueda O(1) en el Map
    }
}
```

---

## 11. Principios Complementarios (DRY, KISS, YAGNI, LoD, Tell Don't Ask)

En el mundo profesional, SOLID siempre va acompañado de estos principios de código limpio:

| Acrónimo / Principio | Nombre Completo | Significado | Ejemplo Práctico |
|---|---|---|---|
| **DRY** | *Don't Repeat Yourself* (No te repitas) | Cada pieza de conocimiento o regla de negocio debe tener una representación única y sin ambigüedades en el sistema. | En lugar de escribir la validación de mayoría de edad (`edad >= 18`) en 6 controladores, ponerla en el método `usuario.esMayorDeEdad()` o en una anotación `@MayorDeEdad`. |
| **KISS** | *Keep It Simple, Stupid* (Mantenlo simple) | La mayoría de los sistemas funcionan mejor si se mantienen simples en lugar de hacerlos complejos; evita sobre-ingeniería innecesaria. | Si solo tienes 1 implementación de `CalculadoraIva` y nunca va a cambiar, no crees una fábrica abstracta con 4 interfaces genéricas. |
| **YAGNI** | *You Aren't Gonna Need It* (No lo vas a necesitar) | No programes funcionalidades "por si acaso en el futuro las piden". Implementa las cosas cuando realmente se necesiten. | No programar soporte multi-moneda y multi-idioma hoy si el cliente solo vende en pesos argentinos en una tienda local. |
| **LoD** | *Law of Demeter* (Ley de Deméter / Principio de menor conocimiento) | "Habla solo con tus amigos inmediatos, no con extraños". Evita encadenar múltiples `.get().get().get()` (*Train Wrecks* / Choques de tren). | ❌ `pedido.getCliente().getDireccion().getCiudad().getCodigoPostal()`<br>✅ `pedido.obtenerCodigoPostalEntrega()` |
| **Tell, Don't Ask** | Dile qué hacer, no le preguntes su estado | En lugar de pedirle datos a un objeto para tomar decisiones afuera y volver a meterle el resultado, **pídele al objeto que ejecute la acción él mismo** (evita el *Modelo de Dominio Anémico*). | ❌ `if (cuenta.getSaldo() >= monto) { cuenta.setSaldo(cuenta.getSaldo() - monto); }`<br>✅ `cuenta.debitar(monto);` (la cuenta valida y resta adentro). |
| **Fail Fast** | Fallar Rápido | Validar los datos de entrada al principio del método o en el constructor y lanzar una excepción inmediata si algo es inválido, evitando que el error avance. | Usar `Objects.requireNonNull(pedido)` o `if (monto <= 0) throw new IllegalArgumentException(...)` en la línea 1 del método. |

**Ejemplo de *Tell, Don't Ask* + *Ley de Deméter*:**
```java
// ❌ MALO: Modelo Anémico + Violación de Tell Don't Ask + Violación de Ley de Deméter
public void agregarItemAlCarrito(Pedido pedido, Producto producto, int cantidad) {
    // Le preguntamos todo el estado interno al objeto para hacer la cuenta afuera:
    if (pedido.getEstado().equals("ABIERTO") &&
        pedido.getCliente().getBilletera().getSaldoDisponible().compareTo(producto.getPrecio()) >= 0) {
        pedido.getItems().add(new ItemPedido(producto, cantidad));
        pedido.setTotal(pedido.getTotal().add(producto.getPrecio().multiply(BigDecimal.valueOf(cantidad))));
    }
}

// ✅ BUENO: Modelo Rico (Tell, Don't Ask + Ley de Deméter)
public void agregarItemAlCarrito(Pedido pedido, Producto producto, int cantidad) {
    // Le decimos al objeto de dominio qué hacer y él protege sus propias reglas internas:
    pedido.agregarProducto(producto, cantidad);
}

// Dentro de la clase Pedido.java:
public class Pedido {
    private EstadoPedido estado;
    private Cliente cliente;
    private List<ItemPedido> items = new ArrayList<>();
    private BigDecimal total = BigDecimal.ZERO;

    public void agregarProducto(Producto producto, int cantidad) {
        if (this.estado != EstadoPedido.ABIERTO) {
            throw new IllegalStateException("No se pueden agregar productos a un pedido cerrado");
        }
        BigDecimal subtotal = producto.calcularSubtotal(cantidad);
        if (!cliente.puedePagar(subtotal)) {
            throw new SaldoInsuficienteException("El cliente no posee saldo suficiente");
        }
        this.items.add(new ItemPedido(producto, cantidad));
        this.total = this.total.add(subtotal);
    }
}
```

---

## 12. Composición sobre Herencia (Composition over Inheritance)

Uno de los pilares para cumplir **OCP**, **LSP** y **DIP** es preferir la **composición** (tener un atributo de otro objeto: relación *"tiene-un" / has-a*) antes que abusar de la **herencia de clases** (`extends`: relación *"es-un" / is-a*).

| Aspecto | Herencia (`class A extends B`) | Composición (`class A { private final B b; }`) |
|---|---|---|
| **Acoplamiento** | **Muy alto** (Caja blanca): la clase hija queda atada a la implementación interna del padre. Si el padre cambia, puede romper a todos los hijos. | **Bajo** (Caja negra): la clase solo conoce la interfaz pública de su colaborador. |
| **Flexibilidad en ejecución** | **Estática**: se decide en tiempo de compilación y no se puede cambiar el padre de un objeto mientras el programa corre. | **Dinámica**: puedes cambiar la estrategia o implementación inyectada en cualquier momento o mediante configuración. |
| **Límite de Java** | Java **no permite herencia múltiple de clases** (solo puedes hacer `extends` de 1 clase). | Puedes componer una clase inyectándole 2, 3 o más interfaces colaboradoras sin límite. |
| **Riesgo de violar LSP** | Alto: es fácil heredar métodos que no aplican a la subclase. | Nulo: solo expones e invocas los métodos que necesitas del componente inyectado. |

**Ejemplo de Herencia Rígida vs Composición Flexible:**
```java
// ❌ MALO: Abuso de herencia (¿Qué pasa si ahora queremos un ExportadorCsvConCompresionZipYEncriptacion?)
public class ExportadorDatos {
    public String exportar(String datos) { return datos; }
}
public class ExportadorConCompresionZip extends ExportadorDatos { ... }
public class ExportadorConEncriptacion extends ExportadorDatos { ... }
// ¡Explosión combinatoria de clases! No podemos hacer: extends ExportadorConCompresionZip, ExportadorConEncriptacion

// ✅ BUENO: Composición mediante interfaces colaboradoras
public interface Compresor {
    byte[] comprimir(byte[] datos);
}

public interface Encriptador {
    byte[] encriptar(byte[] datos);
}

@Service
public class ExportadorSeguroService {
    private final Compresor compresor;       // "Tiene un" compresor
    private final Encriptador encriptador;   // "Tiene un" encriptador

    public ExportadorSeguroService(Compresor compresor, Encriptador encriptador) {
        this.compresor = compresor;
        this.encriptador = encriptador;
    }

    public byte[] exportar(String datos) {
        byte[] comprimido = compresor.comprimir(datos.getBytes());
        return encriptador.encriptar(comprimido);
    }
}
```

---

## 13. SOLID y el Testing (Unit Testing & Mocking)

Cumplir SOLID no es un ejercicio teórico: su beneficio más inmediato se ve al escribir **Tests Unitarios con JUnit 5 y Mockito**. Si un código es difícil de testear, casi siempre es porque viola algún principio SOLID.

| Principio | Impacto directo en tus Tests Unitarios | Ejemplo en JUnit 5 + Mockito |
|---|---|---|
| **SRP** | Tests cortos, rápidos y con pocos casos por clase. No necesitas configurar 15 mocks para probar un solo método. | Probar `CalculadoraImpuestosTest` sin siquiera usar Mockito porque solo hace matemáticas puras. |
| **OCP** | Cuando agregas una nueva funcionalidad (ej. `DescuentoNavidad`), creas `DescuentoNavidadTest` sin tener que tocar ni arreglar los tests viejos que ya pasaban. | Cada estrategia tiene su propio archivo de test aislado. |
| **LSP** | Permite crear **Tests de Contrato** que prueban que todas las implementaciones de una interfaz respetan las mismas reglas básicas. | Una clase de test base que verifica que ningún `Notificador` lance excepción cuando el mensaje es válido. |
| **ISP** | Al crear un `@Mock` o un *Fake* manual para un test, solo tienes que simular 1 o 2 métodos relevantes en vez de decenas de métodos irrelevantes. | `when(lecturaRepo.findById(1L)).thenReturn(Optional.of(prod));` |
| **DIP** | Es el principio que **hace posible el Mocking**. Gracias a que tu servicio recibe interfaces por constructor, en el test puedes pasarle un `@Mock` en milisegundos sin levantar base de datos ni servidores reales. | `@InjectMocks private AlertaSeguridadService service;` + `@Mock private CanalNotificacionAlerta canalMock;` |

**Ejemplo de cómo DIP + SRP hacen que el test unitario sea trivial y ultrarrápido:**
```java
@ExtendWith(MockitoExtension.class)
class AlertaSeguridadServiceTest {

    @Mock
    private CanalNotificacionAlerta canalMock; // Gracias a DIP podemos simular la interfaz sin llamar a Twilio real

    @InjectMocks
    private AlertaSeguridadService service;    // Se inyecta por constructor sin necesitar levantar todo Spring

    @Test
    void deberiaEnviarAlertaConPrefijoCritico() {
        // Act (Ejecutar)
        service.dispararAlerta("admin@empresa.com", "Intento de acceso fallido");

        // Assert (Verificar)
        verify(canalMock).enviarAlerta(
                eq("admin@empresa.com"),
                eq("ALERTA CRÍTICA: Intento de acceso fallido")
        );
    }
}
```

---

## 14. Arquitectura Limpia y Hexagonal con SOLID en Spring Boot

En proyectos modernos de Spring Boot escucharás mucho hablar de **Clean Architecture** (Arquitectura Limpia) o **Arquitectura Hexagonal (Puertos y Adaptadores)**. Estas arquitecturas no son más que **aplicar SOLID (especialmente SRP, ISP y DIP) a nivel de paquetes y módulos**.

| Concepto en Arquitectura Hexagonal | Qué es | Equivalente en SOLID | Ejemplo de Clase |
|---|---|---|---|
| **Dominio (*Domain*)** | El corazón del negocio: Entidades de negocio puras, Value Objects y Reglas. No depende de ningún framework. | **SRP** (solo lógica de negocio) y **DIP** (nivel más alto). | `Cuenta.java`, `Dinero.java`, `SaldoInsuficienteException.java`. |
| **Caso de Uso (*Application / Use Case*)** | Orquesta una acción concreta que un usuario hace en el sistema. | **SRP** (una clase por cada acción del usuario). | `TransferirDineroUseCase.java`, `CrearProductoService.java`. |
| **Puerto de Entrada (*Input / Driving Port*)** | Interfaz que expone qué casos de uso ofrece el negocio hacia afuera. | **ISP** y **DIP**. | `public interface TransferirDineroInputPort { void transferir(...); }` |
| **Puerto de Salida (*Output / Driven Port*)** | Interfaz definida por el dominio/aplicación para pedir algo al exterior (guardar en BD, mandar mail, cobrar). | **DIP** (la abstracción pertenece al negocio, no a la base de datos) e **ISP**. | `public interface CuentaRepositoryPort { Optional<Cuenta> buscarPorId(Long id); }` |
| **Adaptador de Entrada (*Driving Adapter*)** | Clase técnica que recibe estímulos externos (HTTP, Kafka, CLI) y llama al Puerto de Entrada. | **SRP** (solo lidia con HTTP/JSON). | `@RestController public class TransferenciaController` |
| **Adaptador de Salida (*Driven Adapter*)** | Clase técnica que implementa el Puerto de Salida usando una tecnología concreta (Spring Data JPA, Mongo, Feign, S3). | **DIP** (el detalle técnico depende de la interfaz del dominio) y **LSP**. | `@Repository public class JpaCuentaAdapter implements CuentaRepositoryPort` |

**Diagrama del flujo de dependencias (Inversión de Dependencias en Arquitectura Hexagonal):**
```text
[Controller HTTP] ---> [Caso de Uso (Negocio)] <--- (implementa) --- [Adaptador JPA / MySQL]
                             |                                              |
                             +---> [Interfaz CuentaRepositoryPort] <--------+
                        (El negocio define el contrato; la BD se adapta a él)
```

**Ejemplo de Puerto y Adaptador en Spring Boot:**
```java
// 1. PUERTO DE SALIDA (En el paquete domain/port - No sabe qué es JPA ni SQL)
public interface ClienteOutputPort {
    Optional<Cliente> obtenerPorDocumento(String documento);
    Cliente guardar(Cliente cliente);
}

// 2. CASO DE USO (En el paquete application - Solo conoce el Puerto)
@Service
public class RegistrarClienteUseCase {
    private final ClienteOutputPort clientePort;

    public RegistrarClienteUseCase(ClienteOutputPort clientePort) {
        this.clientePort = clientePort;
    }

    public Cliente ejecutar(Cliente nuevo) {
        if (clientePort.obtenerPorDocumento(nuevo.getDocumento()).isPresent()) {
            throw new IllegalStateException("El cliente ya existe");
        }
        return clientePort.guardar(nuevo);
    }
}

// 3. ADAPTADOR DE SALIDA (En el paquete infrastructure/persistence - Implementa el Puerto usando JPA)
@Component
public class ClienteJpaAdapter implements ClienteOutputPort {

    private final SpringDataClienteRepository jpaRepository;
    private final ClientePersistenceMapper mapper;

    public ClienteJpaAdapter(SpringDataClienteRepository jpaRepository, ClientePersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Cliente> obtenerPorDocumento(String documento) {
        return jpaRepository.findByDocumento(documento).map(mapper::toDomain);
    }

    @Override
    public Cliente guardar(Cliente cliente) {
        ClienteEntity entity = mapper.toEntity(cliente);
        return mapper.toDomain(jpaRepository.save(entity));
    }
}
```

---

## 15. Caso Práctico Completo: Refactorizando un Servicio Monolítico a SOLID

Veamos cómo luce una clase típica de proyecto real que viola los 5 principios SOLID al mismo tiempo y cómo queda después de refactorizarla.

### ❌ Antes: Clase que viola S, O, L, I, D
```java
@Service
public class PedidoManagerMalo {

    // Viola DIP: instancia dependencias concretas con 'new'
    private MySqlPedidoDao dao = new MySqlPedidoDao();
    private SmtpMailer mailer = new SmtpMailer();

    // Viola ISP: implementa una interfaz gigante donde varios métodos no aplican
    public void procesarPedido(PedidoDto dto, String metodoEnvio) {
        // Viola SRP: Validación HTTP/DTO mezclada con negocio
        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new RuntimeException("Pedido vacío");
        }

        // Viola OCP: if/else por cada método de envío
        double costoEnvio = 0;
        if ("CORREO".equals(metodoEnvio)) {
            costoEnvio = 1500.0;
        } else if ("MOTO".equals(metodoEnvio)) {
            costoEnvio = 3000.0;
        } else if ("RETIRO_GRATIS".equals(metodoEnvio)) {
            // Viola LSP si tratamos Retiro Gratis como un Envío con tracking postal
            costoEnvio = 0.0;
        }

        double total = dto.getSubtotal() + costoEnvio;

        // Viola SRP y DIP: Persistencia y armado de HTML de email en el mismo método
        dao.insertarConSql("INSERT INTO pedidos VALUES (" + dto.getId() + ", " + total + ")");
        String html = "<h1>Gracias por tu compra</h1><p>Total: $" + total + "</p>";
        mailer.enviarHtml(dto.getEmail(), "Confirmación", html);
    }
}
```

### ✅ Después: Diseño limpio cumpliendo los 5 principios SOLID
```java
// 1. ISP + LSP: Contrato pequeño y específico solo para calcular costos de entrega
public interface CalculadoraEntrega {
    boolean soporta(TipoEntrega tipo);
    BigDecimal calcularCosto(Pedido pedido);
}

// 2. OCP: Cada forma de entrega es una clase independiente (Abierto a extensión, cerrado a modificación)
@Component
public class EntregaMotomensajeria implements CalculadoraEntrega {
    @Override
    public boolean soporta(TipoEntrega tipo) { return tipo == TipoEntrega.MOTO; }

    @Override
    public BigDecimal calcularCosto(Pedido pedido) { return new BigDecimal("3000.00"); }
}

@Component
public class EntregaRetiroSucursal implements CalculadoraEntrega {
    @Override
    public boolean soporta(TipoEntrega tipo) { return tipo == TipoEntrega.RETIRO_SUCURSAL; }

    @Override
    public BigDecimal calcularCosto(Pedido pedido) { return BigDecimal.ZERO; }
}

// 3. DIP + ISP: Interfaces específicas para persistencia y notificación
public interface PedidoRepositorio {
    Pedido guardar(Pedido pedido);
}

public interface NotificadorConfirmacionPedido {
    void notificarConfirmacion(Pedido pedido);
}

// 4. SRP + DIP: El servicio de aplicación solo coordina el caso de uso apoyándose en abstracciones
@Service
public class ConfirmarPedidoUseCase {

    private final List<CalculadoraEntrega> calculadorasEntrega;
    private final PedidoRepositorio pedidoRepositorio;
    private final NotificadorConfirmacionPedido notificador;

    public ConfirmarPedidoUseCase(List<CalculadoraEntrega> calculadorasEntrega,
                                  PedidoRepositorio pedidoRepositorio,
                                  NotificadorConfirmacionPedido notificador) {
        this.calculadorasEntrega = calculadorasEntrega;
        this.pedidoRepositorio = pedidoRepositorio;
        this.notificador = notificador;
    }

    @Transactional
    public Pedido confirmar(Pedido pedido) {
        BigDecimal costoEntrega = calculadorasEntrega.stream()
                .filter(calc -> calc.soporta(pedido.getTipoEntrega()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Tipo de entrega no válido"))
                .calcularCosto(pedido);

        pedido.confirmarConCostoEntrega(costoEntrega); // Tell, Don't Ask (el dominio valida y suma)
        Pedido pedidoGuardado = pedidoRepositorio.guardar(pedido);
        notificador.notificarConfirmacion(pedidoGuardado);
        return pedidoGuardado;
    }
}
```

---

## 16. Resumen Rápido / Cheat Sheet de SOLID

Usa esta tabla de bolsillo como checklist cada vez que escribas o revises una clase en Java y Spring Boot:

| Principio | Pregunta Clave de Autoevaluación | Olor de Violación (*Smell*) | Solución Rápida en Spring Boot |
|---|---|---|---|
| **S — SRP** | *"¿Cuántas razones o áreas de la empresa podrían pedirme cambiar esta clase?"* | Clase gigante que valida, calcula, hace SQL, genera PDF y manda emails. | Extraer responsabilidades en clases `@Component` o `@Service` dedicadas y dejar un caso de uso que coordine. |
| **O — OCP** | *"Si mañana agregan un nuevo tipo de pago/descuento/envío, ¿tengo que tocar un `if/else` o `switch` existente?"* | Bloques `switch` o `if / else if` preguntando por enums o strings de tipo. | Crear una interfaz común e inyectar `List<MiEstrategia>` o un `Map` de estrategias. |
| **L — LSP** | *"¿Puedo pasar cualquier implementación de esta interfaz sin que el código explote o haga cosas raras?"* | Métodos con `throw new UnsupportedOperationException()` o preguntas con `instanceof`. | No forzar herencia donde no hay comportamiento común; separar interfaces por capacidades reales. |
| **I — ISP** | *"¿Alguna clase está obligada a implementar métodos vacíos porque la interfaz le pide cosas que no usa?"* | Interfaces de 10+ métodos donde algunas clases dejan la mitad sin implementar. | Partir la interfaz grande en varias interfaces pequeñas y específicas de 1 a 3 métodos. |
| **D — DIP** | *"¿Mi clase de negocio está haciendo `new` de un cliente técnico o dependiendo de una librería concreta?"* | Atributos de clases concretas (`SmtpClient`, `StripeSdk`) o llamadas a `new` dentro de servicios. | Depender siempre de una `interface` e inyectarla por constructor para que Spring suministre el `@Component`. |

> **Tip de lectura:** Cuando abras un proyecto Spring Boot nuevo, fíjate primero en los **constructores de los `@Service`** y en las **interfaces del paquete de dominio o servicio**. Si los constructores tienen entre 2 y 4 interfaces bien nombradas y los métodos delegan responsabilidades específicas sin grandes bloques `if/else` por tipo, estás leyendo código que aplica correctamente los principios SOLID.

