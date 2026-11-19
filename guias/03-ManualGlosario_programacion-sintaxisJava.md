# Manual y Recetario de Sintaxis Java y Spring Boot ("¿Cómo se programa esto?")

Este manual está diseñado como tu **guía de consulta rápida y recetario de plantillas (*Copy & Adapt*)**. Cada vez que sepas **qué** quieres programar pero no recuerdes **cómo se escribe exactamente la sintaxis en Java o Spring Boot**, busca la receta en el índice, mira qué significa cada pieza y adapta la plantilla.

---

## Índice de Recetas de Código
1. [Cómo crear una Clase Estándar (Atributos, Constructores, Getters, Setters, `equals`/`hashCode`)](#1-cómo-crear-una-clase-estándar-atributos-constructores-getters-setters-equalshashcode)
2. [Cómo crear y sobrecargar Constructores (Vacío, Completo, Constructor Copia y `super`)](#2-cómo-crear-y-sobrecargar-constructores-vacío-completo-constructor-copia-y-super)
3. [Cómo crear Métodos (Con retorno, `void`, `static`, Parámetros Variables `varargs` y Genéricos)](#3-cómo-crear-métodos-con-retorno-void-static-parámetros-variables-varargs-y-genéricos)
4. [Cómo crear un `record` (DTOs y Value Objects Inmutables en Java 17/21)](#4-cómo-crear-un-record-dtos-y-value-objects-inmutables-en-java-1721)
5. [Cómo crear una `interface` (Normal, con métodos `default` y `@FunctionalInterface`)](#5-cómo-crear-una-interface-normal-con-métodos-default-y-functionalinterface)
6. [Cómo crear una Clase Abstracta (`abstract class`) y Herencia (`extends`)](#6-cómo-crear-una-clase-abstracta-abstract-class-y-herencia-extends)
7. [Cómo crear un `enum` (Simple y Avanzado con Atributos y Métodos)](#7-cómo-crear-un-enum-simple-y-avanzado-con-atributos-y-métodos)
8. [Cómo usar Estructuras de Control (`if/else`, `switch` moderno, `for`, `while`, `try-catch`)](#8-cómo-usar-estructuras-de-control-ifelse-switch-moderno-for-while-try-catch)
9. [Cómo crear y usar Colecciones (`List`, `Set`, `Map`, `Queue`)](#9-cómo-crear-y-usar-colecciones-list-set-map-queue)
10. [Cómo usar `Stream` y Expresiones Lambda (Filtrar, Transformar, Sumar y Agrupar)](#10-cómo-usar-stream-y-expresiones-lambda-filtrar-transformar-sumar-y-agrupar)
11. [Cómo usar `Optional<T>` para evitar `NullPointerException`](#11-cómo-usar-optionalt-para-evitar-nullpointerexception)
12. [Cómo crear una Excepción Personalizada (`RuntimeException`)](#12-cómo-crear-una-excepción-personalizada-runtimeexception)
13. [Cómo manejar Fechas, Horas y Dinero (`LocalDate`, `Instant`, `BigDecimal`)](#13-cómo-manejar-fechas-horas-y-dinero-localdate-instant-bigdecimal)
14. [Cómo crear una clase `@Configuration` y declarar un `@Bean` en Spring Boot](#14-cómo-crear-una-clase-configuration-y-declarar-un-bean-en-spring-boot)
15. [Cómo leer Propiedades del `application.yml` (`@Value` vs `@ConfigurationProperties`)](#15-cómo-leer-propiedades-del-applicationyml-value-vs-configurationproperties)
16. [Cómo crear un `@RestController` completo (GET, POST, PUT, PATCH, DELETE)](#16-cómo-crear-un-restcontroller-completo-get-post-put-patch-delete)
17. [Cómo crear DTOs de Entrada/Salida con Validaciones (`@Valid`, `@NotBlank`, `@Positive`)](#17-cómo-crear-dtos-de-entradasalida-con-validaciones-valid-notblank-positive)
18. [Cómo crear un `@Service` con Inyección por Constructor y `@Transactional`](#18-cómo-crear-un-service-con-inyección-por-constructor-y-transactional)
19. [Cómo crear una Entidad JPA (`@Entity`) con Relaciones (`@ManyToOne`, `@OneToMany`)](#19-cómo-crear-una-entidad-jpa-entity-con-relaciones-manytoone-onetomany)
20. [Cómo crear un `@Repository` en Spring Data JPA (Query Methods, `@Query` y `@Lock`)](#20-cómo-crear-un-repository-en-spring-data-jpa-query-methods-query-y-lock)
21. [Cómo crear un Mapper (Convertir entre `Entity` y `DTO`)](#21-cómo-crear-un-mapper-convertir-entre-entity-y-dto)
22. [Cómo crear un Manejador Global de Excepciones (`@RestControllerAdvice`)](#22-cómo-crear-un-manejador-global-de-excepciones-restcontrolleradvice)
23. [Cómo crear un Cliente HTTP (`RestClient`) para llamar a otra API](#23-cómo-crear-un-cliente-http-restclient-para-llamar-a-otra-api)
24. [Cómo crear un Filtro HTTP (`OncePerRequestFilter`), un Evento y un `@Scheduled`](#24-cómo-crear-un-filtro-http-onceperrequestfilter-un-evento-y-un-scheduled)
25. [Cómo crear un Test Unitario con JUnit 5 y Mockito (`@Mock`, `@InjectMocks`)](#25-cómo-crear-un-test-unitario-con-junit-5-y-mockito-mock-injectmocks)

---

## 1. Cómo crear una Clase Estándar (Atributos, Constructores, Getters, Setters, `equals`/`hashCode`)

### ¿Qué necesitas saber?
Toda clase Java estándar tiene **5 partes en este orden**:
1. **Constantes estáticas** (`public static final`).
2. **Atributos privados** (`private` — encapsulamiento).
3. **Constructores** (se llaman exactamente igual que la clase y no tienen tipo de retorno).
4. **Métodos de negocio y Getters/Setters**.
5. **`equals`, `hashCode` y `toString`**.

### Plantilla Lista para Usar (Java Puro vs Con Lombok)

```java
package com.miempresa.proyecto.domain;

import java.math.BigDecimal;
import java.util.Objects;

// 1. Declaración de la clase (PascalCase: Primera letra de cada palabra en mayúscula)
public class Producto {

    // 2. Constantes estáticas (UPPER_SNAKE_CASE)
    public static final BigDecimal IVA_POR_DEFECTO = new BigDecimal("0.21");

    // 3. Atributos privados (camelCase)
    private Long id;
    private String nombre;
    private BigDecimal precio;
    private boolean activo;

    // 4. Constructor vacío (requerido por frameworks como Jackson o Hibernate)
    public Producto() {
    }

    // 5. Constructor con parámetros para inicializar el objeto válido desde que nace
    public Producto(Long id, String nombre, BigDecimal precio) {
        this.id = id;
        this.nombre = Objects.requireNonNull(nombre, "El nombre es obligatorio");
        this.precio = Objects.requireNonNull(precio, "El precio es obligatorio");
        this.activo = true; // Valor inicial por defecto
    }

    // 6. Métodos propios de negocio
    public BigDecimal calcularPrecioConIva() {
        return this.precio.add(this.precio.multiply(IVA_POR_DEFECTO));
    }

    // 7. Getters (para leer) y Setters (para modificar)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    // Nota: Para booleanos el getter empieza con "is" en lugar de "get"
    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    // 8. equals y hashCode (para comparar dos objetos por su ID y usarlos en HashSet / HashMap)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Producto producto = (Producto) o;
        return Objects.equals(id, producto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // 9. toString (para imprimir el objeto en logs de forma legible)
    @Override
    public String toString() {
        return "Producto{id=" + id + ", nombre='" + nombre + "', precio=" + precio + "}";
    }
}
```

---

## 2. Cómo crear y sobrecargar Constructores (Vacío, Completo, Constructor Copia y `super`)

### Reglas de Sintaxis de un Constructor:
- **Mismo nombre exacto** que la clase (respetando mayúsculas).
- **NO lleva tipo de retorno** (ni siquiera `void`).
- Si quieres llamar a otro constructor de la misma clase usa **`this(...)`** en la primera línea.
- Si heredas de una clase padre y quieres llamar al constructor del padre, usa **`super(...)`** en la primera línea.

```java
public class CuentaBancaria {

    private final String numeroCuenta;
    private BigDecimal saldo;
    private String moneda;

    // 1. Constructor Principal (el que hace todas las validaciones y asignaciones)
    public CuentaBancaria(String numeroCuenta, BigDecimal saldoInicial, String moneda) {
        if (saldoInicial.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El saldo inicial no puede ser negativo");
        }
        this.numeroCuenta = numeroCuenta;
        this.saldo = saldoInicial;
        this.moneda = moneda;
    }

    // 2. Constructor Sobrecargado (Encadenado con this(...) para dar valores por defecto)
    // Si solo nos pasan el número de cuenta, arranca con saldo 0.00 y en pesos "ARS":
    public CuentaBancaria(String numeroCuenta) {
        this(numeroCuenta, BigDecimal.ZERO, "ARS"); // Llama al constructor #1 de arriba
    }

    // 3. Constructor Copia (Crea un objeto nuevo clonando los datos de otro)
    public CuentaBancaria(CuentaBancaria otra) {
        this(otra.numeroCuenta, otra.saldo, otra.moneda);
    }
}

// 4. Cómo se instancia (crea) el objeto desde otra clase usando la palabra clave 'new':
// CuentaBancaria cuenta1 = new CuentaBancaria("CBU-123");
// CuentaBancaria cuenta2 = new CuentaBancaria("CBU-456", new BigDecimal("5000.00"), "USD");
```

---

## 3. Cómo crear Métodos (Con retorno, `void`, `static`, Parámetros Variables `varargs` y Genéricos)

### Anatomía de un Método en Java:
```text
[visibilidad] [static/final] <T> [tipoRetorno] [nombreMetodoEnCamelCase]([Tipo param1, Tipo param2]) {
    // cuerpo del método
    return resultado;
}
```

| Parte | Opciones | Cuándo usar cada una |
|---|---|---|
| **Visibilidad** | `public` \| `private` \| `protected` \| *(sin nada = package-private)* | `public`: lo que otras clases deben llamar.<br>`private`: método auxiliar interno que solo se usa dentro de esta misma clase. |
| **Tipo de Retorno** | `void` \| `String`, `BigDecimal`, `List<T>`, etc. | `void`: realiza una acción pero no devuelve ningún dato. Cualquier otro tipo exige la palabra `return`. |
| **Modificador `static`** | `static` o sin `static` | `static`: pertenece a la clase y no usa atributos de instancia (se llama como `Calculadora.sumar(2, 3)`). |

### Plantilla de Todos los Tipos de Métodos

```java
public class EjemplosDeMetodos {

    // 1. Método normal que recibe parámetros y devuelve un resultado
    public BigDecimal calcularDescuento(BigDecimal precio, int porcentaje) {
        BigDecimal factor = BigDecimal.valueOf(porcentaje).divide(BigDecimal.valueOf(100));
        return precio.multiply(factor);
    }

    // 2. Método 'void' (no devuelve nada, solo ejecuta una acción)
    public void imprimirRecibo(String cliente, BigDecimal total) {
        if (cliente == null) {
            return; // En un método void, 'return;' vacío sirve para salir temprano del método
        }
        System.out.println("Cliente: " + cliente + " - Total: $" + total);
    }

    // 3. Método 'private' auxiliar (para dividir un método largo en pasos legibles)
    private boolean esMontoValido(BigDecimal monto) {
        return monto != null && monto.compareTo(BigDecimal.ZERO) > 0;
    }

    // 4. Método 'static' de utilidad (se puede llamar sin hacer 'new EjemplosDeMetodos()')
    public static String normalizarTexto(String texto) {
        return texto == null ? "" : texto.trim().toUpperCase();
    }

    // 5. Método con cantidad variable de argumentos (Varargs con '...')
    // Se puede llamar pasando 1, 2, 5 o ningún String: concatenarConGuion("A", "B", "C")
    public String concatenarConGuion(String... palabras) {
        return String.join("-", palabras);
    }

    // 6. Método Genérico (<T>): funciona para cualquier tipo de lista que le pases
    public <T> Optional<T> obtenerPrimerElemento(List<T> lista) {
        if (lista == null || lista.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(lista.get(0));
    }
}
```

---

## 4. Cómo crear un `record` (DTOs y Value Objects Inmutables en Java 17/21)

### ¿Cuándo usar `record` en lugar de `class`?
Siempre que necesites una clase cuyo único propósito sea **transportar datos de forma inmutable** (DTOs de Request/Response, Eventos, resultados de consultas o Value Objects como `Dinero`).
Java genera automáticamente por detrás: atributos `private final`, constructor completo, getters (`objeto.campo()`), `equals()`, `hashCode()` y `toString()`.

```java
package com.miempresa.proyecto.dto;

import java.math.BigDecimal;
import java.util.Objects;

// 1. Record simple en 1 sola línea (equivale a una clase de 60 líneas)
public record ClienteResumenDto(Long id, String nombre, String email) {
}

// 2. Record con Validación en su "Constructor Compacto" (NO lleva paréntesis en el constructor)
public record RangoPrecios(BigDecimal minimo, BigDecimal maximo) {

    // Constructor compacto: valida antes de asignar automáticamente this.minimo y this.maximo
    public RangoPrecios {
        Objects.requireNonNull(minimo, "El precio mínimo es obligatorio");
        Objects.requireNonNull(maximo, "El precio máximo es obligatorio");
        if (minimo.compareTo(maximo) > 0) {
            throw new IllegalArgumentException("El mínimo no puede ser mayor que el máximo");
        }
    }

    // También puedes agregar métodos propios a un record:
    public boolean contiene(BigDecimal precio) {
        return precio.compareTo(minimo) >= 0 && precio.compareTo(maximo) <= 0;
    }
}

// Cómo se usa un record (Nota: los getters NO llevan el prefijo "get", se llaman igual que el campo):
// ClienteResumenDto dto = new ClienteResumenDto(1L, "Ana", "ana@mail.com");
// String email = dto.email();
```

---

## 5. Cómo crear una `interface` (Normal, con métodos `default` y `@FunctionalInterface`)

### ¿Cuándo crear una `interface`?
Para definir un **contrato** (qué métodos debe tener un servicio, adaptador o estrategia) aplicando los principios **SOLID (OCP y DIP)**.

```java
package com.miempresa.proyecto.port;

import java.math.BigDecimal;

// 1. Interfaz Estándar de Negocio
public interface PasarelaPago {

    // Todos los métodos de una interfaz son implícitamente 'public abstract' (no llevan llaves {})
    boolean cobrar(String tokenTarjeta, BigDecimal monto);

    void reembolsar(String idTransaccionExterna);

    // Método 'default' (Opcional desde Java 8): tiene código por defecto que las clases hijas heredan
    default String obtenerMonedaPorDefecto() {
        return "ARS";
    }
}

// 2. Cómo una clase implementa la interfaz usando la palabra clave 'implements' y '@Override':
public class StripePasarelaPago implements PasarelaPago {

    @Override
    public boolean cobrar(String tokenTarjeta, BigDecimal monto) {
        System.out.println("Cobrando con Stripe: $" + monto);
        return true;
    }

    @Override
    public void reembolsar(String idTransaccionExterna) {
        System.out.println("Reembolsando en Stripe: " + idTransaccionExterna);
    }
}

// 3. Interfaz Funcional (@FunctionalInterface): tiene 1 solo método abstracto y permite usar Lambdas (->)
@FunctionalInterface
public interface ReglaValidacion<T> {
    boolean esValido(T objeto);
}
// Uso con Lambda: ReglaValidacion<String> noVacio = texto -> texto != null && !texto.isBlank();
```

---

## 6. Cómo crear una Clase Abstracta (`abstract class`) y Herencia (`extends`)

### ¿Cuándo usar `abstract class` en lugar de `interface`?
Cuando varias clases hijas comparten **atributos en común y código base reutilizable**, pero quieres obligar a que cada clase hija programe su propio paso específico. *(No se puede hacer `new` de una clase abstracta).*

```java
// 1. Clase Padre Abstracta (plantilla común)
public abstract class GeneradorReporteBase {

    protected final String nombreEmpresa;

    protected GeneradorReporteBase(String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
    }

    // Método concreto compartido por todos los hijos (Patrón Template Method)
    public final byte[] generarReporteCompleto() {
        String encabezado = "=== Reporte de " + nombreEmpresa + " ===\n";
        String cuerpo = construirCuerpoEspecifico(); // Llama al método que cada hijo programará a su manera
        return exportarAFormato(encabezado + cuerpo);
    }

    // Métodos abstractos: obligan a las clases hijas a escribir su implementación con @Override
    protected abstract String construirCuerpoEspecifico();
    protected abstract byte[] exportarAFormato(String contenido);
}

// 2. Clase Hija Concreta que hereda con 'extends'
public class ReporteVentasCsv extends GeneradorReporteBase {

    public ReporteVentasCsv(String nombreEmpresa) {
        super(nombreEmpresa); // Llama obligatoriamente al constructor de la clase padre (GeneradorReporteBase)
    }

    @Override
    protected String construirCuerpoEspecifico() {
        return "id,monto\n1,5000.00";
    }

    @Override
    protected byte[] exportarAFormato(String contenido) {
        return contenido.getBytes();
    }
}
```

---

## 7. Cómo crear un `enum` (Simple y Avanzado con Atributos y Métodos)

### ¿Para qué sirve un `enum`?
Para representar un conjunto cerrado de constantes válidas (estados de un pedido, tipos de documento, monedas, roles) evitando usar Strings sueltos propensos a errores de tipeo.

```java
// 1. Enum Simple
public enum RolUsuario {
    ADMIN, VENDEDOR, CLIENTE, AUDITOR
}

// 2. Enum Avanzado Profesional (Con atributos, constructor privado, métodos y búsqueda segura)
public enum MedioDePago {

    TARJETA_CREDITO("TC", new BigDecimal("0.035"), true),
    TRANSFERENCIA_BANCARIA("TB", BigDecimal.ZERO, false),
    EFECTIVO("EF", BigDecimal.ZERO, false),
    QR_INTEROPERABLE("QR", new BigDecimal("0.008"), false);

    // Atributos de cada constante del enum (siempre 'private final')
    private final String codigoCorto;
    private final BigDecimal porcentajeComision;
    private final boolean permiteCuotas;

    // El constructor de un Enum es SIEMPRE privado
    MedioDePago(String codigoCorto, BigDecimal porcentajeComision, boolean permiteCuotas) {
        this.codigoCorto = codigoCorto;
        this.porcentajeComision = porcentajeComision;
        this.permiteCuotas = permiteCuotas;
    }

    // Métodos de negocio dentro del Enum
    public BigDecimal calcularComision(BigDecimal monto) {
        return monto.multiply(this.porcentajeComision);
    }

    public String getCodigoCorto() { return codigoCorto; }
    public boolean isPermiteCuotas() { return permiteCuotas; }

    // Método estático para buscar un Enum a partir de un código String enviado desde el Frontend o BD
    public static MedioDePago desdeCodigo(String codigo) {
        for (MedioDePago medio : values()) {
            if (medio.codigoCorto.equalsIgnoreCase(codigo)) {
                return medio;
            }
        }
        throw new IllegalArgumentException("Código de medio de pago inválido: " + codigo);
    }
}
```

---

## 8. Cómo usar Estructuras de Control (`if/else`, `switch` moderno, `for`, `while`, `try-catch`)

```java
public class GuiaEstructurasDeControl {

    public void ejemplosSintaxis(EstadoPedido estado, List<String> items) {

        // 1. IF / ELSE IF / ELSE y Operador Ternario (? :)
        String etiqueta;
        if (items == null || items.isEmpty()) {
            etiqueta = "VACIO";
        } else if (items.size() > 10) {
            etiqueta = "MAYORISTA";
        } else {
            etiqueta = "MINORISTA";
        }
        // Operador ternario para asignaciones simples en 1 línea:
        int costoEnvio = (items.size() > 5) ? 0 : 1500;

        // 2. SWITCH EXPRESIÓN MODERNO (Java 14+ / 21) con flechas '->' (No necesita 'break' y devuelve valor)
        String mensajeEstado = switch (estado) {
            case PENDIENTE -> "Esperando confirmación de pago";
            case PAGADO, EN_PREPARACION -> "Estamos preparando tu paquete";
            case ENVIADO -> {
                // Si una rama tiene varias líneas, usa llaves {} y la palabra 'yield' para devolver el valor
                String tracking = generarCodigoTracking();
                yield "En camino con código: " + tracking;
            }
            case CANCELADO -> "El pedido fue anulado";
        };

        // 3. BUCLE FOR-EACH (El más usado para recorrer colecciones)
        for (String item : items) {
            System.out.println("Procesando: " + item);
        }

        // 4. BUCLE FOR CLÁSICO CON ÍNDICE (Cuando necesitas saber la posición 'i')
        for (int i = 0; i < items.size(); i++) {
            System.out.println("Posición " + i + ": " + items.get(i));
        }

        // 5. BUCLE WHILE (Cuando no sabes cuántas iteraciones habrá de antemano)
        int intentos = 0;
        while (intentos < 3) {
            intentos++;
        }

        // 6. TRY - CATCH - FINALLY y TRY-WITH-RESOURCES (Cierra archivos/conexiones automáticamente)
        try (var reader = new java.io.BufferedReader(new java.io.StringReader("datos"))) {
            String linea = reader.readLine();
        } catch (java.io.IOException | IllegalArgumentException ex) {
            // Puedes capturar múltiples excepciones en un mismo catch usando '|'
            System.err.println("Ocurrió un error: " + ex.getMessage());
        } finally {
            // El bloque finally se ejecuta SIEMPRE (haya habido error o no)
        }
    }

    private String generarCodigoTracking() { return "TRK-999"; }
}
```

---

## 9. Cómo crear y usar Colecciones (`List`, `Set`, `Map`, `Queue`)

```java
import java.util.*;

public class RecetarioColecciones {

    public void comoUsarColecciones() {

        // =========================================================================
        // 1. LISTAS (List): Ordenadas y admiten duplicados
        // =========================================================================
        // A) Lista Inmutable rápida (NO permite .add() ni .remove() después de crearse):
        List<String> monedasFijas = List.of("ARS", "USD", "BRL");

        // B) Lista Mutable dinámica (SÍ permite agregar, borrar y ordenar elementos):
        List<String> carrito = new ArrayList<>();
        carrito.add("Monitor");                  // Agrega al final
        carrito.add("Teclado");
        String primero = carrito.get(0);         // Obtiene por posición (empieza en 0)
        carrito.remove("Monitor");               // Elimina elemento
        boolean tieneTeclado = carrito.contains("Teclado");
        int cantidad = carrito.size();

        // =========================================================================
        // 2. CONJUNTOS (Set): NO permiten elementos duplicados y buscan en O(1)
        // =========================================================================
        Set<Long> idsProcesados = new HashSet<>();
        boolean seAgrego = idsProcesados.add(10L); // Devuelve true
        boolean duplicado = idsProcesados.add(10L); // Devuelve false porque el 10L ya estaba adentro
        boolean yaFueProcesado = idsProcesados.contains(10L); // Instantáneo O(1)

        // =========================================================================
        // 3. MAPAS / DICCIONARIOS (Map<Clave, Valor>): Guardan pares Clave -> Valor
        // =========================================================================
        Map<String, Integer> stockPorSku = new HashMap<>();
        stockPorSku.put("SKU-01", 50);                         // Guarda o pisa el valor de esa clave
        stockPorSku.putIfAbsent("SKU-01", 99);                 // Solo lo guarda si la clave NO existía
        int stockActual = stockPorSku.getOrDefault("SKU-99", 0); // Si no existe, devuelve 0 en vez de null

        // Cómo recorrer todas las claves y valores de un Map:
        for (Map.Entry<String, Integer> entrada : stockPorSku.entrySet()) {
            System.out.println("SKU: " + entrada.getKey() + " -> Stock: " + entrada.getValue());
        }

        // =========================================================================
        // 4. COLAS (Queue - FIFO) y PILAS (Deque - LIFO) con ArrayDeque
        // =========================================================================
        Queue<String> colaTurnos = new ArrayDeque<>();
        colaTurnos.offer("Cliente 1"); // Entra a la fila
        colaTurnos.offer("Cliente 2");
        String siguienteAtender = colaTurnos.poll(); // Saca y devuelve a "Cliente 1" (null si está vacía)
    }
}
```

---

## 10. Cómo usar `Stream` y Expresiones Lambda (Filtrar, Transformar, Sumar y Agrupar)

Los **Streams** son la forma estándar en Java moderno de procesar colecciones declarativamente sin escribir bucles `for` largos:

```java
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RecetarioStreams {

    public record Factura(Long id, String categoria, BigDecimal monto, boolean pagada) {}

    public void operacionesMasUsadasConStreams(List<Factura> facturas) {

        // 1. FILTRAR (.filter) y TRANSFORMAR A OTRA LISTA (.map + .toList)
        // "Dame los IDs de todas las facturas pagadas mayores a $1000"
        List<Long> idsFacturasGrandesPagadas = facturas.stream()
                .filter(Factura::pagada)                                       // Equivale a: f -> f.pagada()
                .filter(f -> f.monto().compareTo(new BigDecimal("1000")) > 0)  // Condición con lambda
                .map(Factura::id)                                              // Extrae solo el Long id
                .toList();                                                     // Cierra el stream en una List

        // 2. SUMAR BIGDECIMALS (.map + .reduce)
        // "¿Cuál es el monto total sumado de las facturas pendientes?"
        BigDecimal totalPendiente = facturas.stream()
                .filter(f -> !f.pagada())
                .map(Factura::monto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 3. BUSCAR EL PRIMERO QUE CUMPLA UNA CONDICIÓN (.findFirst -> devuelve Optional)
        Optional<Factura> primeraImpaga = facturas.stream()
                .filter(f -> !f.pagada())
                .findFirst();

        // 4. VERIFICAR CONDICIONES BOOLEANAS (.anyMatch, .allMatch, .noneMatch)
        boolean hayAlgunaImpaga = facturas.stream().anyMatch(f -> !f.pagada());
        boolean todasEstanPagadas = facturas.stream().allMatch(Factura::pagada);

        // 5. CONVERTIR UNA LISTA EN UN MAPA INDEXADO POR ID (Collectors.toMap)
        Map<Long, Factura> mapaPorId = facturas.stream()
                .collect(Collectors.toMap(
                        Factura::id,
                        Function.identity(),
                        (existente, reemplazo) -> existente // Evita error si vienen IDs repetidos
                ));

        // 6. AGRUPAR ELEMENTOS POR UNA PROPIEDAD (Collectors.groupingBy)
        // Agrupa las facturas en un Map donde la clave es la categoría y el valor es la lista de facturas de esa categoría
        Map<String, List<Factura>> facturasPorCategoria = facturas.stream()
                .collect(Collectors.groupingBy(Factura::categoria));
    }
}
```

---

## 11. Cómo usar `Optional<T>` para evitar `NullPointerException`

Cuando un método (como un repositorio) puede encontrar o no un resultado, devuelve `Optional<T>` en vez de `null`. **Nunca uses `optional.get()` a ciegas**; usa estas 4 recetas:

```java
public class RecetarioOptional {

    public void comoManejarUnOptional(Optional<Usuario> usuarioOpt) {

        // RECETA 1: Si no existe, lanzar nuestra excepción de negocio (.orElseThrow) -> ¡La más usada en Services!
        Usuario usuario = usuarioOpt.orElseThrow(
                () -> new EntidadNoEncontradaException("El usuario solicitado no existe")
        );

        // RECETA 2: Si no existe, devolver un valor por defecto (.orElse / .orElseGet)
        String nombre = usuarioOpt
                .map(Usuario::getNombre)
                .orElse("Invitado Anónimo");

        // RECETA 3: Ejecutar una acción solo si el valor está presente (.ifPresent)
        usuarioOpt.ifPresent(u -> enviarEmail(u.getEmail()));

        // RECETA 4: Cómo crear un Optional cuando tienes una variable que podría ser null
        String posibleNulo = obtenerHeaderOpcional();
        Optional<String> seguro = Optional.ofNullable(posibleNulo); // Si es null, crea Optional.empty() sin explotar
    }
}
```

---

## 12. Cómo crear una Excepción Personalizada (`RuntimeException`)

En Spring Boot, las excepciones de negocio heredan de **`RuntimeException`** (*Unchecked Exceptions*) porque hacen que `@Transactional` ejecute el `ROLLBACK` de la base de datos automáticamente y no ensucian las firmas de los métodos con `throws`:

```java
package com.miempresa.proyecto.exception;

// Heredamos siempre de RuntimeException
public class SaldoInsuficienteException extends RuntimeException {

    private final String codigoError;

    // 1. Constructor simple con mensaje
    public SaldoInsuficienteException(String mensaje) {
        super(mensaje);
        this.codigoError = "ERR_SALDO_INSUFICIENTE";
    }

    // 2. Constructor con mensaje y causa original (cuando envuelves otro error)
    public SaldoInsuficienteException(String mensaje, Throwable causa) {
        super(mensaje, causa);
        this.codigoError = "ERR_SALDO_INSUFICIENTE";
    }

    public String getCodigoError() {
        return codigoError;
    }
}

// Cómo se lanza en cualquier parte de tu código:
// if (saldo.compareTo(monto) < 0) {
//     throw new SaldoInsuficienteException("No tienes saldo suficiente para transferir $" + monto);
// }
```

---

## 13. Cómo manejar Fechas, Horas y Dinero (`LocalDate`, `Instant`, `BigDecimal`)

```java
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class RecetarioFechasYDinero {

    public void operacionesConFechas() {
        // 1. Obtener fecha/hora actual
        LocalDate hoy = LocalDate.now();                         // Solo fecha: 2026-11-06
        LocalDateTime ahoraLocal = LocalDateTime.now();          // Fecha y hora local
        Instant ahoraUtc = Instant.now();                        // Instante exacto universal (UTC) para BD

        // 2. Crear una fecha específica y sumar/restar días o meses (son inmutables)
        LocalDate vencimiento = LocalDate.of(2026, 12, 15);
        LocalDate proximoMes = hoy.plusMonths(1).minusDays(3);

        // 3. Comparar dos fechas y calcular días de diferencia
        boolean estaVencida = hoy.isAfter(vencimiento);
        long diasRestantes = ChronoUnit.DAYS.between(hoy, vencimiento);

        // 4. Convertir de String a LocalDate y viceversa
        DateTimeFormatter formatoArgentino = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate fechaParseada = LocalDate.parse("25/12/2026", formatoArgentino);
        String fechaTexto = hoy.format(formatoArgentino);
    }

    public void operacionesConDineroBigDecimal() {
        // 1. Instanciar SIEMPRE con String o valueOf (NUNCA con double)
        BigDecimal precio = new BigDecimal("1500.50");
        BigDecimal cantidad = BigDecimal.valueOf(3);

        // 2. Sumar, Restar, Multiplicar y Dividir (siempre reasignando el resultado porque es inmutable)
        BigDecimal subtotal = precio.multiply(cantidad);
        BigDecimal descuento = new BigDecimal("200.00");
        BigDecimal total = subtotal.subtract(descuento);

        // ¡IMPORTANTE!: Al dividir SIEMPRE pasar cantidad de decimales (escala) y modo de redondeo
        BigDecimal valorCuota = total.divide(BigDecimal.valueOf(3), 2, RoundingMode.HALF_EVEN);

        // 3. Comparar dos BigDecimal (NUNCA usar .equals(), usar siempre .compareTo())
        if (total.compareTo(BigDecimal.ZERO) > 0) {       // total > 0
            // Es positivo
        }
        if (total.compareTo(precio) == 0) {               // total == precio
            // Son exactamente iguales en valor matemático
        }
    }
}
```

---

## 14. Cómo crear una clase `@Configuration` y declarar un `@Bean` en Spring Boot

### ¿Cuándo necesitas `@Configuration` + `@Bean`?
Cuando quieres registrar en el contenedor de Spring un objeto de una librería externa (a la que no puedes ponerle `@Component` o `@Service` arriba) o cuando necesitas configurar cómo se construye un componente (ej. `PasswordEncoder`, `RestClient`, `ObjectMapper`, `SecurityFilterChain`).

```java
package com.miempresa.proyecto.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.Clock;

// 1. Anotamos la clase con @Configuration
@Configuration
public class AppBeansConfig {

    // 2. Cada método con @Bean devuelve la interfaz/clase que luego podrás inyectar en cualquier constructor
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public Clock systemClock() {
        return Clock.systemUTC(); // Ideal para poder mockear la hora actual en los tests
    }
}
```

---

## 15. Cómo leer Propiedades del `application.yml` (`@Value` vs `@ConfigurationProperties`)

Imagina que en tu `application.yml` tienes:
```yaml
integraciones:
  pasarela-pagos:
    base-url: https://api.pasarela.com
    timeout-segundos: 5
    api-key: ${PASARELA_API_KEY:clave_dev_123}
```

### Opción A: Para 1 sola propiedad suelta (`@Value`)
```java
@Component
public class ClienteSimple {
    private final String apiKey;

    public ClienteSimple(@Value("${integraciones.pasarela-pagos.api-key}") String apiKey) {
        this.apiKey = apiKey;
    }
}
```

### Opción B (Recomendada): Agrupar todo el bloque en un `record` tipado (`@ConfigurationProperties`)
```java
package com.miempresa.proyecto.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;

// 1. Mapea automáticamente todas las propiedades que cuelgan de "integraciones.pasarela-pagos"
@Validated
@ConfigurationProperties(prefix = "integraciones.pasarela-pagos")
public record PasarelaPagosProperties(
        @NotBlank String baseUrl,
        @Positive int timeoutSegundos,
        @NotBlank String apiKey
) {}

// Nota: Para activar @ConfigurationProperties en tu clase principal @SpringBootApplication agrega:
// @ConfigurationPropertiesScan
```

---

## 16. Cómo crear un `@RestController` completo (GET, POST, PUT, PATCH, DELETE)

Esta es la plantilla completa de un controlador REST profesional con paginación, códigos HTTP exactos (`200`, `201`, `204`) y validación:

```java
package com.miempresa.proyecto.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/productos") // Ruta base en plural y versionada
public class ProductoController {

    private final ProductoService productoService;

    // Inyección por constructor (sin @Autowired innecesario)
    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    // 1. GET CON FILTROS OPCIONALES Y PAGINACIÓN: GET /api/v1/productos?categoria=gamer&page=0&size=20
    @GetMapping
    public ResponseEntity<Page<ProductoResponseDto>> listar(
            @RequestParam(required = false) String categoria,
            @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        return ResponseEntity.ok(productoService.buscarTodos(categoria, pageable));
    }

    // 2. GET POR ID EN LA RUTA: GET /api/v1/productos/42
    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.obtenerPorId(id));
    }

    // 3. POST (CREAR RECURSO): Devuelve 201 Created + Header "Location" con la URL del nuevo recurso
    @PostMapping
    public ResponseEntity<ProductoResponseDto> crear(
            @Valid @RequestBody CrearProductoRequestDto request) {
        ProductoResponseDto creado = productoService.crear(request);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(creado.id())
                .toUri();

        return ResponseEntity.created(location).body(creado);
    }

    // 4. PUT (ACTUALIZACIÓN COMPLETA): PUT /api/v1/productos/42
    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponseDto> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarProductoRequestDto request) {
        return ResponseEntity.ok(productoService.actualizar(id, request));
    }

    // 5. PATCH (ACTUALIZACIÓN PARCIAL DE UN CAMPO): PATCH /api/v1/productos/42/precio
    @PatchMapping("/{id}/precio")
    public ResponseEntity<ProductoResponseDto> actualizarPrecio(
            @PathVariable Long id,
            @Valid @RequestBody CambiarPrecioRequestDto request) {
        return ResponseEntity.ok(productoService.cambiarPrecio(id, request.nuevoPrecio()));
    }

    // 6. DELETE (ELIMINAR): Devuelve 204 No Content (sin cuerpo)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
```

---

## 17. Cómo crear DTOs de Entrada/Salida con Validaciones (`@Valid`, `@NotBlank`, `@Positive`)

*(Requiere la dependencia `spring-boot-starter-validation` en tu `pom.xml`).*

```java
package com.miempresa.proyecto.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

// 1. DTO de Entrada (Request) con todas las anotaciones de Bean Validation más usadas:
public record CrearProductoRequestDto(

        @NotBlank(message = "El código SKU es obligatorio y no puede estar en blanco")
        @Size(min = 3, max = 50, message = "El SKU debe tener entre 3 y 50 caracteres")
        String sku,

        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotNull(message = "El precio es obligatorio")
        @Positive(message = "El precio debe ser estrictamente mayor a 0")
        @Digits(integer = 15, fraction = 2, message = "El precio admite hasta 2 decimales")
        BigDecimal precio,

        @NotNull(message = "El stock inicial es obligatorio")
        @PositiveOrZero(message = "El stock no puede ser negativo")
        Integer stockInicial,

        @Email(message = "El email del proveedor no tiene un formato válido")
        String emailProveedor,

        @NotEmpty(message = "Debe incluir al menos una etiqueta")
        List<@NotBlank String> etiquetas
) {}

// 2. DTO de Salida (Response): lo que devolvemos limpio al cliente HTTP
public record ProductoResponseDto(
        Long id,
        String sku,
        String nombre,
        BigDecimal precio,
        Integer stock
) {}
```

---

## 18. Cómo crear un `@Service` con Inyección por Constructor y `@Transactional`

```java
package com.miempresa.proyecto.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true) // Por defecto todos los métodos de lectura optimizan la conexión sin bloquear
public class ProductoService {

    // 1. Dependencias siempre 'private final'
    private final ProductoRepository productoRepository;
    private final ProductoMapper productoMapper;

    // 2. Inyección por constructor
    public ProductoService(ProductoRepository productoRepository, ProductoMapper productoMapper) {
        this.productoRepository = productoRepository;
        this.productoMapper = productoMapper;
    }

    // 3. Método de solo lectura (hereda el readOnly = true de la clase)
    public ProductoResponseDto obtenerPorId(Long id) {
        return productoRepository.findById(id)
                .map(productoMapper::toResponseDto)
                .orElseThrow(() -> new EntidadNoEncontradaException("Producto no encontrado con ID: " + id));
    }

    // 4. Método de escritura: lleva @Transactional sin readOnly para abrir transacción ACID (commit/rollback)
    @Transactional
    public ProductoResponseDto crear(CrearProductoRequestDto request) {
        if (productoRepository.existsBySkuIgnoreCase(request.sku())) {
            throw new ReglaNegocioException("Ya existe un producto con el SKU: " + request.sku());
        }
        ProductoEntity entidad = productoMapper.toEntity(request);
        ProductoEntity guardado = productoRepository.save(entidad);
        return productoMapper.toResponseDto(guardado);
    }
}
```

---

## 19. Cómo crear una Entidad JPA (`@Entity`) con Relaciones (`@ManyToOne`, `@OneToMany`)

```java
package com.miempresa.proyecto.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "pedidos",
    indexes = {
        @Index(name = "idx_pedidos_estado_fecha", columnList = "estado, creado_en")
    }
)
public class PedidoEntity {

    // 1. Clave Primaria autoincremental (BIGSERIAL / IDENTITY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 2. Columnas normales y Enums (¡Siempre EnumType.STRING, jamás ORDINAL!)
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 30)
    private EstadoPedido estado = EstadoPedido.PENDIENTE;

    @Column(name = "total", nullable = false, precision = 19, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    // 3. Bloqueo Optimista automático de Hibernate para evitar que dos usuarios pisen el mismo registro
    @Version
    private Long version;

    // 4. Relación Muchos a Uno (@ManyToOne): SIEMPRE usar FetchType.LAZY para no traer media base de datos
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private ClienteEntity cliente;

    // 5. Relación Uno a Muchos (@OneToMany) bidireccional con sus ítems hijos
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PedidoItemEntity> items = new ArrayList<>();

    @Column(name = "creado_en", nullable = false, updatable = false)
    private Instant creadoEn = Instant.now();

    // Constructor vacío protegido obligatorio para JPA
    protected PedidoEntity() {
    }

    // Método sincronizador para agregar hijos manteniendo ambos lados de la relación consistentes
    public void agregarItem(PedidoItemEntity item) {
        this.items.add(item);
        item.setPedido(this);
        this.total = this.total.add(item.calcularSubtotal());
    }

    // Getters y Setters...
}
```

---

## 20. Cómo crear un `@Repository` en Spring Data JPA (Query Methods, `@Query` y `@Lock`)

```java
package com.miempresa.proyecto.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<ProductoEntity, Long> {

    // 1. Query Method derivado por nombre (Spring arma el SQL solo)
    Optional<ProductoEntity> findBySkuIgnoreCase(String sku);

    boolean existsBySkuIgnoreCase(String sku);

    Page<ProductoEntity> findByActivoTrueAndPrecioLessThanEqual(BigDecimal precioMaximo, Pageable pageable);

    // 2. Consulta JPQL personalizada con @Query y proyección directa a un Record DTO
    @Query("""
           SELECT new com.miempresa.proyecto.dto.ProductoResponseDto(p.id, p.sku, p.nombre, p.precio, p.stock)
           FROM ProductoEntity p
           WHERE p.activo = true AND p.stock > :stockMinimo
           ORDER BY p.nombre ASC
           """)
    List<ProductoResponseDto> buscarDisponiblesLivianos(@Param("stockMinimo") int stockMinimo);

    // 3. Bloqueo Pesimista (SELECT ... FOR UPDATE) para actualizar stock o saldo sin condiciones de carrera
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM ProductoEntity p WHERE p.id = :id")
    Optional<ProductoEntity> findByIdForUpdate(@Param("id") Long id);

    // 4. Actualización directa en base de datos (@Modifying)
    @Modifying
    @Query("UPDATE ProductoEntity p SET p.stock = p.stock - :cantidad WHERE p.id = :id AND p.stock >= :cantidad")
    int descontarStockAtomico(@Param("id") Long id, @Param("cantidad") int cantidad);
}
```

---

## 21. Cómo crear un Mapper (Convertir entre `Entity` y `DTO`)

Nunca devuelvas una `@Entity` de JPA directamente en el `@RestController` (expone tu base de datos y causa errores `LazyInitializationException`). Usa siempre un `@Component` Mapper (o MapStruct):

```java
package com.miempresa.proyecto.mapper;

import org.springframework.stereotype.Component;

@Component
public class ProductoMapper {

    // 1. De DTO de entrada a Entidad JPA nueva
    public ProductoEntity toEntity(CrearProductoRequestDto dto) {
        if (dto == null) return null;
        ProductoEntity entity = new ProductoEntity();
        entity.setSku(dto.sku().trim().toUpperCase());
        entity.setNombre(dto.nombre().trim());
        entity.setPrecio(dto.precio());
        entity.setStock(dto.stockInicial());
        return entity;
    }

    // 2. De Entidad JPA a DTO de salida (Response)
    public ProductoResponseDto toResponseDto(ProductoEntity entity) {
        if (entity == null) return null;
        return new ProductoResponseDto(
                entity.getId(),
                entity.getSku(),
                entity.getNombre(),
                entity.getPrecio(),
                entity.getStock()
        );
    }
}
```

---

## 22. Cómo crear un Manejador Global de Excepciones (`@RestControllerAdvice`)

Con esta clase capturas todas las excepciones de tus servicios y validaciones (`@Valid`) en un solo lugar y las conviertes en respuestas JSON estándar (**RFC 7807 `ProblemDetail`**):

```java
package com.miempresa.proyecto.exception;

import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Cuando no se encuentra un recurso -> Devuelve 404 Not Found
    @ExceptionHandler(EntidadNoEncontradaException.class)
    public ProblemDetail handleNotFound(EntidadNoEncontradaException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Recurso no encontrado");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    // 2. Cuando falla una regla de negocio -> Devuelve 422 Unprocessable Entity
    @ExceptionHandler(ReglaNegocioException.class)
    public ProblemDetail handleReglaNegocio(ReglaNegocioException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        problem.setTitle("Violación de regla de negocio");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    // 3. Cuando fallan las anotaciones @Valid (@NotBlank, @Positive) del DTO -> Devuelve 400 Bad Request con el mapa de campos
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> erroresPorCampo = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fe -> fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "Valor inválido",
                        (msg1, msg2) -> msg1
                ));

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "Uno o más campos de la petición tienen errores de validación"
        );
        problem.setTitle("Error de validación de entrada");
        problem.setProperty("camposInvalidos", erroresPorCampo);
        return problem;
    }
}
```

---

## 23. Cómo crear un Cliente HTTP (`RestClient`) para llamar a otra API

En Spring Boot 3.2+, **`RestClient`** es la forma moderna, fluida y síncrona de llamar a APIs externas (reemplaza al antiguo `RestTemplate`):

```java
package com.miempresa.proyecto.client;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import java.time.Duration;

@Component
public class CotizacionDolarHttpClient {

    private final RestClient restClient;

    public CotizacionDolarHttpClient(RestClient.Builder builder, PasarelaPagosProperties props) {
        // Configurar siempre Timeouts de conexión y lectura para nunca colgar nuestros hilos
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(2));
        requestFactory.setReadTimeout(Duration.ofSeconds(props.timeoutSegundos()));

        this.restClient = builder
                .baseUrl(props.baseUrl())
                .requestFactory(requestFactory)
                .defaultHeader("Authorization", "Bearer " + props.apiKey())
                .build();
    }

    // 1. Llamada GET externa:
    public CotizacionDto obtenerCotizacionActual(String moneda) {
        return restClient.get()
                .uri("/v1/cotizaciones/{moneda}", moneda)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    throw new IntegracionExternaException("Error al consultar API externa: HTTP " + res.getStatusCode());
                })
                .body(CotizacionDto.class);
    }

    // 2. Llamada POST externa enviando un body JSON y un Header de Idempotencia:
    public RespuestaPagoExternaDto enviarCobro(String idempotencyKey, SolicitudCobroDto body) {
        return restClient.post()
                .uri("/v1/cobros")
                .header("Idempotency-Key", idempotencyKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(RespuestaPagoExternaDto.class);
    }
}
```

---

## 24. Cómo crear un Filtro HTTP (`OncePerRequestFilter`), un Evento y un `@Scheduled`

### A) Cómo crear un Filtro HTTP que intercepta todas las peticiones (`OncePerRequestFilter`)
```java
@Component
public class TiempoRespuestaFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(TiempoRespuestaFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        long inicio = System.currentTimeMillis();
        try {
            filterChain.doFilter(request, response); // Continúa hacia el Controller
        } finally {
            long duracionMs = System.currentTimeMillis() - inicio;
            log.info("{} {} respondió HTTP {} en {} ms",
                    request.getMethod(), request.getRequestURI(), response.getStatus(), duracionMs);
        }
    }
}
```

### B) Cómo publicar y escuchar un Evento Interno de Dominio en Spring
```java
// 1. El Evento (un simple record):
public record UsuarioRegistradoEvent(Long usuarioId, String email) {}

// 2. Cómo publicarlo desde tu @Service:
// applicationEventPublisher.publishEvent(new UsuarioRegistradoEvent(u.getId(), u.getEmail()));

// 3. Cómo escucharlo en otra clase después de que la transacción hizo COMMIT en la base de datos:
@Component
public class NotificacionBienvenidaListener {

    @Async // Requiere @EnableAsync en una clase @Configuration
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void alRegistrarseUnUsuario(UsuarioRegistradoEvent event) {
        System.out.println("Enviando email de bienvenida a: " + event.email());
    }
}
```

### C) Cómo crear una Tarea Programada Automática (`@Scheduled`)
```java
// Nota: Requiere agregar @EnableScheduling en una clase @Configuration
@Component
public class LimpiezaTokensVencidosJob {

    // Corre todos los días a las 03:00 AM hora de Argentina
    @Scheduled(cron = "0 0 3 * * *", zone = "America/Argentina/Buenos_Aires")
    public void borrarTokensExpirados() {
        System.out.println("Ejecutando limpieza nocturna de tokens...");
    }
}
```

---

## 25. Cómo crear un Test Unitario con JUnit 5 y Mockito (`@Mock`, `@InjectMocks`)

Cada vez que termines de programar un `@Service`, usa esta plantilla con el patrón **AAA (*Arrange, Act, Assert*)** para probar tanto el camino feliz como que lance las excepciones correctas:

```java
package com.miempresa.proyecto.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// 1. Activa Mockito sin levantar todo Spring (el test corre en 20 milisegundos)
@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    // 2. @Mock: Simula las dependencias (interfaces/repositorios) que usa nuestro servicio
    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private ProductoMapper productoMapper;

    // 3. @InjectMocks: Crea la instancia real del Servicio e inyecta los @Mock de arriba en su constructor
    @InjectMocks
    private ProductoService productoService;

    @Test
    @DisplayName("Debería devolver el DTO del producto cuando el ID existe")
    void deberiaObtenerProductoCuandoExiste() {
        // ARRANGE (Preparar el escenario y enseñarle al Mock qué responder)
        Long idBuscado = 10L;
        ProductoEntity entidadSimulada = new ProductoEntity();
        ProductoResponseDto dtoEsperado = new ProductoResponseDto(10L, "SKU-1", "Mouse", new BigDecimal("5000"), 15);

        when(productoRepository.findById(idBuscado)).thenReturn(Optional.of(entidadSimulada));
        when(productoMapper.toResponseDto(entidadSimulada)).thenReturn(dtoEsperado);

        // ACT (Ejecutar el método real que estamos probando)
        ProductoResponseDto resultado = productoService.obtenerPorId(idBuscado);

        // ASSERT (Verificar que devolvió lo esperado y que llamó al repositorio)
        assertThat(resultado).isNotNull();
        assertThat(resultado.id()).isEqualTo(10L);
        assertThat(resultado.nombre()).isEqualTo("Mouse");
        verify(productoRepository, times(1)).findById(idBuscado);
    }

    @Test
    @DisplayName("Debería lanzar EntidadNoEncontradaException cuando el ID no existe en la BD")
    void deberiaLanzarExcepcionCuandoProductoNoExiste() {
        // ARRANGE
        Long idInexistente = 999L;
        when(productoRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // ACT & ASSERT (Verificar que al ejecutar se lanza la excepción exacta)
        assertThatThrownBy(() -> productoService.obtenerPorId(idInexistente))
                .isInstanceOf(EntidadNoEncontradaException.class)
                .hasMessageContaining("999");

        verify(productoMapper, never()).toResponseDto(any());
    }
}
```

> **Tip de uso:** Cuando tengas que crear un módulo nuevo desde cero en un proyecto Spring Boot, sigue este orden de recetas de este archivo:
> 1. Crea la **`@Entity`** ([Receta 19](#19-cómo-crear-una-entidad-jpa-entity-con-relaciones-manytoone-onetomany)) y su **`@Repository`** ([Receta 20](#20-cómo-crear-un-repository-en-spring-data-jpa-query-methods-query-y-lock)).
> 2. Crea los **`record` DTOs** con validaciones ([Receta 4](#4-cómo-crear-un-record-dtos-y-value-objects-inmutables-en-java-1721) y [Receta 17](#17-cómo-crear-dtos-de-entradasalida-con-validaciones-valid-notblank-positive)) y el **Mapper** ([Receta 21](#21-cómo-crear-un-mapper-convertir-entre-entity-y-dto)).
> 3. Crea el **`@Service`** ([Receta 18](#18-cómo-crear-un-service-con-inyección-por-constructor-y-transactional)) con sus **Excepciones de negocio** ([Receta 12](#12-cómo-crear-una-excepción-personalizada-runtimeexception)).
> 4. Crea el **`@RestController`** ([Receta 16](#16-cómo-crear-un-restcontroller-completo-get-post-put-patch-delete)) y verifica tu **Test Unitario** ([Receta 25](#25-cómo-crear-un-test-unitario-con-junit-5-y-mockito-mock-injectmocks)).

