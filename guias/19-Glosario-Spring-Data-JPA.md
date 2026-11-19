# Glosario Spring Data JPA / Hibernate

Términos para entender entidades, repositorios, consultas, relaciones y transacciones en proyectos Spring Boot con base de datos.

## Índice
1. [Conceptos generales](#1-conceptos-generales)
2. [Dependencias y configuración](#2-dependencias-y-configuración)
3. [Entidades y mapeo básico](#3-entidades-y-mapeo-básico)
4. [Claves primarias y generación de ID](#4-claves-primarias-y-generación-de-id)
5. [Tipos especiales de columnas](#5-tipos-especiales-de-columnas)
6. [Relaciones entre entidades](#6-relaciones-entre-entidades)
7. [Herencia de entidades](#7-herencia-de-entidades)
8. [Repositorios](#8-repositorios)
9. [Query methods (consultas por nombre)](#9-query-methods-consultas-por-nombre)
10. [Consultas con @Query (JPQL y SQL nativo)](#10-consultas-con-query-jpql-y-sql-nativo)
11. [Paginación y ordenamiento](#11-paginación-y-ordenamiento)
12. [Proyecciones](#12-proyecciones)
13. [Specification y consultas dinámicas](#13-specification-y-consultas-dinámicas)
14. [Transacciones](#14-transacciones)
15. [Ciclo de vida y EntityManager](#15-ciclo-de-vida-y-entitymanager)
16. [Lazy vs Eager y el problema N+1](#16-lazy-vs-eager-y-el-problema-n1)
17. [Auditoría y callbacks](#17-auditoría-y-callbacks)
18. [Excepciones comunes](#18-excepciones-comunes)
19. [Migraciones, pool y otras herramientas](#19-migraciones-pool-y-otras-herramientas)
20. [Testing de la capa de datos](#20-testing-de-la-capa-de-datos)
21. [Ejemplo completo](#21-ejemplo-completo)

---

## 1. Conceptos generales

| Término | Significado |
|---|---|
| **JPA** | Jakarta Persistence API: estándar de Java para mapear objetos a tablas. Solo define interfaces/anotaciones. |
| **Hibernate** | Implementación más usada de JPA (el motor que realmente trabaja). |
| **Spring Data JPA** | Capa de Spring sobre JPA que genera repositorios automáticamente. |
| **ORM** | Object-Relational Mapping: traduce objetos Java ↔ filas de tablas. |
| **Entidad (Entity)** | Clase Java mapeada a una tabla. |
| **Tabla / Fila / Columna** | Estructura de la base de datos ↔ Clase / Objeto / Atributo. |
| **Repositorio** | Interfaz con operaciones de acceso a datos (CRUD y consultas). |
| **Persistencia** | Guardar datos de forma que sobrevivan al cierre del programa. |
| **Persistence Context** | "Caché de primer nivel": conjunto de entidades gestionadas durante una transacción. |
| **EntityManager** | API principal de JPA para guardar, buscar y eliminar entidades. |
| **EntityManagerFactory** | Fábrica de `EntityManager` (una por aplicación/BD). |
| **Session / SessionFactory** | Equivalentes de Hibernate a `EntityManager` / `EntityManagerFactory`. |
| **DataSource** | Objeto que provee conexiones a la BD. |
| **Dialect** | Adaptación de Hibernate a cada motor SQL (MySQL, PostgreSQL, Oracle...). |
| **JDBC** | API base de Java para BD; JPA/Hibernate trabajan encima. |
| **JPQL** | Java Persistence Query Language: SQL orientado a entidades y atributos (no a tablas). |
| **HQL** | Lenguaje de consultas de Hibernate (casi igual a JPQL). |
| **Criteria API** | Construir consultas con código Java en vez de texto. |
| **Native Query** | Consulta en SQL puro. |
| **CRUD** | Create, Read, Update, Delete. |
| **DDL** | Instrucciones que definen estructura (`CREATE TABLE`). |
| **DML** | Instrucciones que manipulan datos (`INSERT`, `UPDATE`, `DELETE`). |
| **Transacción** | Conjunto de operaciones que se ejecutan todas o ninguna. |
| **ACID** | Atomicidad, Consistencia, Aislamiento (Isolation), Durabilidad. |
| **Commit / Rollback** | Confirmar / deshacer una transacción. |
| **Dirty checking** | Hibernate detecta cambios en entidades gestionadas y hace el `UPDATE` automáticamente al terminar la transacción. |
| **Flush** | Enviar a la BD los cambios pendientes (sin hacer commit). |
| **Proxy** | Objeto sustituto que Hibernate crea para cargar datos bajo demanda. |
| **Caché de primer nivel** | Caché por transacción/sesión (siempre activa). |
| **Caché de segundo nivel** | Caché compartida entre sesiones (opcional, ej. Ehcache). |
| **Cascade** | Propagar operaciones de una entidad a sus relacionadas. |
| **Orphan removal** | Borrar hijos que dejan de estar referenciados por el padre. |
| **Join** | Unir tablas en una consulta. |
| **Índice** | Estructura que acelera búsquedas en una columna. |
| **Clave primaria (PK)** | Identificador único de la fila. |
| **Clave foránea (FK)** | Columna que referencia la PK de otra tabla. |
| **Constraint** | Restricción (`UNIQUE`, `NOT NULL`, `FOREIGN KEY`). |
| **Esquema (schema)** | Conjunto de tablas de una base de datos. |

### Capas
```
Controller → Service (@Transactional) → Repository (Spring Data) → Hibernate → JDBC → Base de datos
```

## 2. Dependencias y configuración

| Término | Significado |
|---|---|
| `spring-boot-starter-data-jpa` | Starter con Spring Data JPA + Hibernate + HikariCP. |
| **Driver JDBC** | Librería para conectar a un motor concreto (`mysql-connector-j`, `postgresql`, `ojdbc`, `h2`). |
| **H2** | BD en memoria, ideal para pruebas y desarrollo. |
| **HikariCP** | Pool de conexiones por defecto en Spring Boot. |
| `spring.datasource.url` | URL JDBC de la base de datos. |
| `spring.datasource.username / password` | Credenciales. |
| `spring.datasource.driver-class-name` | Clase del driver (suele autodetectarse). |
| `spring.jpa.hibernate.ddl-auto` | Cómo gestiona Hibernate el esquema. |
| `none` | No toca el esquema (recomendado en producción). |
| `validate` | Verifica que las entidades coincidan con las tablas. |
| `update` | Agrega lo que falta (no borra). Útil en desarrollo. |
| `create` | Crea el esquema al arrancar (borra el anterior). |
| `create-drop` | Crea al iniciar y borra al cerrar. |
| `spring.jpa.show-sql` | Muestra el SQL ejecutado. |
| `spring.jpa.properties.hibernate.format_sql` | SQL formateado. |
| `spring.jpa.properties.hibernate.dialect` | Dialecto SQL (suele autodetectarse). |
| `spring.jpa.open-in-view` | OSIV: mantiene la sesión abierta durante la petición web (conviene `false`). |
| `spring.jpa.hibernate.naming.physical-strategy` | Cómo convertir nombres Java a nombres de tabla/columna. |
| `spring.jpa.defer-datasource-initialization` | Ejecutar `data.sql` después de crear tablas con Hibernate. |
| `schema.sql` / `data.sql` | Scripts SQL ejecutados al iniciar. |
| `@EnableJpaRepositories` | Indica dónde buscar repositorios (Boot lo hace solo). |
| `@EntityScan` | Indica dónde buscar entidades. |
| `@EnableJpaAuditing` | Activa la auditoría (fechas/usuario). |
| `@EnableTransactionManagement` | Activa `@Transactional` (Boot lo hace solo). |

### Ejemplo: `application.properties`
```properties
# MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/mi_bd
spring.datasource.username=root
spring.datasource.password=${DB_PASSWORD}

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.open-in-view=false

# Pool HikariCP
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.connection-timeout=30000
```

### Ejemplo: H2 en memoria (desarrollo/tests)
```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.h2.console.enabled=true          # consola en /h2-console
```

### Ejemplo: dependencias en `pom.xml`
```xml
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
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```

## 3. Entidades y mapeo básico

| Anotación | Significado | Ejemplo |
|---|---|---|
| `@Entity` | Marca la clase como entidad (requiere `@Id` y constructor vacío). | `@Entity public class Usuario` |
| `@Table` | Nombre de tabla, esquema, índices y restricciones. | `@Table(name = "usuarios", schema = "app")` |
| `@Column` | Configura una columna. | `@Column(name = "correo", nullable = false, length = 100, unique = true)` |
| `@Id` | Clave primaria. | `@Id private Long id;` |
| `@Transient` | Campo que **no** se guarda en BD. | `@Transient private int edadCalculada;` |
| `@Basic` | Mapeo básico (opcional; permite `fetch`, `optional`). | `@Basic(optional = false)` |
| `@Lob` | Campo grande (texto largo o binario). | `@Lob private String descripcion;` |
| `@Temporal` | Tipo de fecha en `java.util.Date` (legado). | `@Temporal(TemporalType.DATE)` |
| `@Enumerated` | Cómo guardar un enum. | `@Enumerated(EnumType.STRING)` |
| `@Convert` | Convierte un tipo Java a otro en BD. | `@Convert(converter = BooleanSNConverter.class)` |
| `@Embeddable` | Clase cuyos campos se incrustan en otra entidad. | `@Embeddable public class Direccion` |
| `@Embedded` | Incrusta un `@Embeddable` en la entidad. | `@Embedded private Direccion direccion;` |
| `@AttributeOverride` | Cambia el nombre de columnas de un embebido. | `@AttributeOverride(name="calle", column=@Column(name="calle_fact"))` |
| `@Version` | Control de concurrencia optimista (número de versión). | `@Version private Long version;` |
| `@DynamicUpdate` | Hibernate actualiza solo columnas cambiadas. | |
| `@DynamicInsert` | Inserta solo columnas no nulas. | |
| `@Formula` | Campo calculado con SQL (Hibernate). | `@Formula("(precio * cantidad)")` |
| `@Immutable` | Entidad de solo lectura. | |
| `@Subselect` / `@Synchronize` | Entidad basada en un SELECT (Hibernate). | |
| `@NaturalId` | Identificador natural (ej. DNI, email). | |
| `@Access` | Acceso por campo o por getter. | `@Access(AccessType.FIELD)` |
| `@Index` | Índice en `@Table(indexes=...)`. | |
| `@UniqueConstraint` | Unicidad combinada de columnas. | |
| `@Check` | Restricción CHECK de Hibernate. | |
| `@Where` / `@SQLRestriction` | Filtro SQL permanente (ej. borrado lógico). | |
| `@SQLDelete` | Reemplaza el `DELETE` por un SQL propio (borrado lógico). | |
| `@Cache` | Activa caché de segundo nivel (Hibernate). | |

**Atributos frecuentes de `@Column`:** `name`, `nullable`, `unique`, `length`, `precision`, `scale`, `insertable`, `updatable`, `columnDefinition`.

### Ejemplo: entidad básica
```java
@Entity
@Table(name = "usuarios",
       uniqueConstraints = @UniqueConstraint(columnNames = "email"),
       indexes = @Index(name = "idx_nombre", columnList = "nombre"))
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_completo", nullable = false, length = 80)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(precision = 10, scale = 2)          // BigDecimal: 10 dígitos, 2 decimales
    private BigDecimal saldo;

    private LocalDate fechaNacimiento;          // DATE
    private LocalDateTime creadoEn;             // DATETIME

    @Enumerated(EnumType.STRING)                // guarda "ACTIVO" en vez de 0
    private Estado estado;

    @Lob
    private String biografia;                   // TEXT/CLOB

    @Transient
    private int edad;                           // no existe en la tabla

    @Version
    private Long version;                       // bloqueo optimista

    protected Usuario() {}                      // constructor vacío exigido por JPA

    // getters y setters
}

public enum Estado { ACTIVO, INACTIVO, SUSPENDIDO }
```

### Ejemplo: `@Embeddable` (objeto de valor incrustado)
```java
@Embeddable
public class Direccion {
    private String calle;
    private String ciudad;
    private String codigoPostal;
}

@Entity
public class Cliente {
    @Id @GeneratedValue private Long id;

    @Embedded
    private Direccion direccion;    // columnas: calle, ciudad, codigo_postal en tabla cliente

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "calle",  column = @Column(name = "calle_envio")),
        @AttributeOverride(name = "ciudad", column = @Column(name = "ciudad_envio")),
        @AttributeOverride(name = "codigoPostal", column = @Column(name = "cp_envio"))
    })
    private Direccion direccionEnvio;
}
```

### Ejemplo: `@Enumerated` y `@Convert`
```java
// ORDINAL guarda 0,1,2 (frágil si reordenas el enum). STRING es más seguro.
@Enumerated(EnumType.STRING)
private Estado estado;

@Converter(autoApply = false)
public class BooleanSNConverter implements AttributeConverter<Boolean, String> {
    public String convertToDatabaseColumn(Boolean valor) { return valor ? "S" : "N"; }
    public Boolean convertToEntityAttribute(String db)   { return "S".equals(db); }
}

@Convert(converter = BooleanSNConverter.class)
private Boolean activo;
```

### Ejemplo: borrado lógico (soft delete)
```java
@Entity
@SQLDelete(sql = "UPDATE productos SET eliminado = true WHERE id = ?")
@SQLRestriction("eliminado = false")        // @Where en Hibernate < 6.3
public class Producto {
    @Id @GeneratedValue private Long id;
    private String nombre;
    private boolean eliminado = false;
}
// repo.delete(producto) ahora hace UPDATE y findAll() ignora los eliminados
```

### Ejemplo: Lombok en entidades
```java
@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Producto {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private BigDecimal precio;
}
```
> **Cuidado:** evita `@Data` / `@ToString` / `@EqualsAndHashCode` en entidades con relaciones: pueden provocar bucles infinitos y cargas innecesarias de datos.

### Convención de nombres
`UsuarioPerfil` → tabla `usuario_perfil` (Spring Boot usa `snake_case` por defecto). `@Column(name=...)` y `@Table(name=...)` lo sobrescriben.

## 4. Claves primarias y generación de ID

| Término | Significado |
|---|---|
| `@GeneratedValue` | Indica que el ID lo genera el sistema. |
| `GenerationType.IDENTITY` | Autoincremental de la BD (MySQL `AUTO_INCREMENT`, PostgreSQL `SERIAL`). El más común. |
| `GenerationType.SEQUENCE` | Usa una secuencia de la BD (Oracle, PostgreSQL). |
| `GenerationType.TABLE` | Usa una tabla auxiliar (poco usado). |
| `GenerationType.AUTO` | Hibernate elige según el motor. |
| `GenerationType.UUID` | Genera un UUID (JPA 3.1+). |
| `@SequenceGenerator` | Configura la secuencia (nombre, `allocationSize`). |
| `@TableGenerator` | Configura el generador por tabla. |
| `@GenericGenerator` | Generador personalizado de Hibernate. |
| `allocationSize` | Cuántos IDs pide Hibernate de golpe a la secuencia (por defecto 50). |
| `@IdClass` | Clave compuesta con una clase separada. |
| `@EmbeddedId` | Clave compuesta con un `@Embeddable`. |
| **Clave natural** | ID con significado real (DNI, código). |
| **Clave sustituta (surrogate)** | ID artificial sin significado (autonumérico, UUID). |

### Ejemplos
```java
// Autoincremental (MySQL / PostgreSQL con identity)
@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

// Secuencia (Oracle / PostgreSQL)
@Id
@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usuario_seq")
@SequenceGenerator(name = "usuario_seq", sequenceName = "usuario_seq", allocationSize = 1)
private Long id;

// UUID
@Id @GeneratedValue(strategy = GenerationType.UUID)
private UUID id;

// Sin generación: ID asignado manualmente
@Id private String codigo;
```
```java
// Clave compuesta con @EmbeddedId
@Embeddable
public class MatriculaId implements Serializable {
    private Long alumnoId;
    private Long cursoId;
    // equals() y hashCode() OBLIGATORIOS
}

@Entity
public class Matricula {
    @EmbeddedId
    private MatriculaId id;
    private LocalDate fecha;
}
```
> Con `IDENTITY`, Hibernate no puede agrupar inserciones en lote (*batch*); con `SEQUENCE` sí.

## 5. Tipos especiales de columnas

| Tipo Java | Tipo SQL habitual | Notas |
|---|---|---|
| `String` | `VARCHAR(255)` | Cambia largo con `@Column(length=...)`. |
| `Integer` / `int` | `INT` | `Integer` permite `null`. |
| `Long` | `BIGINT` | Ideal para IDs. |
| `Double` / `Float` | `DOUBLE` / `FLOAT` | Evitar para dinero. |
| `BigDecimal` | `DECIMAL(p,s)` | **Usar para dinero**, con `precision` y `scale`. |
| `Boolean` | `BIT` / `BOOLEAN` | |
| `LocalDate` | `DATE` | |
| `LocalDateTime` | `DATETIME` / `TIMESTAMP` | |
| `LocalTime` | `TIME` | |
| `Instant` / `ZonedDateTime` / `OffsetDateTime` | `TIMESTAMP` (con zona) | |
| `Date` / `Calendar` | `DATE` / `TIMESTAMP` | Legado; usar `@Temporal`. |
| `UUID` | `BINARY(16)` / `UUID` | |
| `byte[]` | `BLOB` / `VARBINARY` | Archivos pequeños. |
| `Enum` | `VARCHAR` / `INT` | Con `@Enumerated`. |
| `List<String>` + `@ElementCollection` | Tabla auxiliar | Lista de valores simples. |
| `Map<K,V>` + `@ElementCollection` | Tabla auxiliar | |
| `String` JSON | `JSON` / `TEXT` | `@JdbcTypeCode(SqlTypes.JSON)` en Hibernate 6. |

### Ejemplo: `@ElementCollection`
```java
@Entity
public class Persona {
    @Id @GeneratedValue private Long id;

    @ElementCollection
    @CollectionTable(name = "persona_telefonos", joinColumns = @JoinColumn(name = "persona_id"))
    @Column(name = "telefono")
    private List<String> telefonos = new ArrayList<>();
}
```

### Ejemplo: columna JSON
```java
@JdbcTypeCode(SqlTypes.JSON)
@Column(columnDefinition = "json")
private Map<String, Object> atributos;
```

## 6. Relaciones entre entidades

| Anotación | Significado | Ejemplo real |
|---|---|---|
| `@OneToOne` | Uno a uno. | Usuario ↔ Perfil |
| `@OneToMany` | Uno a muchos. | Cliente → Pedidos |
| `@ManyToOne` | Muchos a uno. | Pedido → Cliente |
| `@ManyToMany` | Muchos a muchos. | Estudiantes ↔ Cursos |
| `@JoinColumn` | Columna de clave foránea. | `@JoinColumn(name = "cliente_id")` |
| `@JoinTable` | Tabla intermedia (N:N). | `@JoinTable(name="estudiante_curso", ...)` |
| `mappedBy` | Indica el lado **inverso** (no dueño) de la relación bidireccional. | `@OneToMany(mappedBy = "cliente")` |
| **Lado dueño (owner)** | El que tiene la FK (normalmente el `@ManyToOne`). | |
| **Unidireccional** | Solo una entidad conoce a la otra. | |
| **Bidireccional** | Ambas se referencian; hay que sincronizar los dos lados. | |
| `cascade` | Propaga operaciones. | `cascade = CascadeType.ALL` |
| `CascadeType.PERSIST` | Al guardar el padre, guarda hijos. | |
| `CascadeType.MERGE` | Al fusionar el padre, fusiona hijos. | |
| `CascadeType.REMOVE` | Al borrar el padre, borra hijos. | |
| `CascadeType.REFRESH` / `DETACH` | Propagan refresh / detach. | |
| `CascadeType.ALL` | Todas las anteriores. | |
| `orphanRemoval = true` | Elimina hijos huérfanos al quitarlos de la colección. | |
| `fetch` | Cuándo cargar la relación. | `FetchType.LAZY` / `EAGER` |
| `optional` | Si la relación puede ser `null`. | `optional = false` |
| `@OrderBy` | Orden de la colección. | `@OrderBy("fecha DESC")` |
| `@OrderColumn` | Guarda el orden de una lista en una columna. | |
| `@MapsId` | Comparte la PK entre dos entidades (1:1). | |
| `@ForeignKey` | Nombre/restricción de la FK. | |
| `@NotFound` | Qué hacer si la FK apunta a un registro inexistente (Hibernate). | |
| `@BatchSize` | Carga en lotes para reducir consultas (Hibernate). | |

### Ejemplo: ManyToOne / OneToMany bidireccional (el más común)
```java
@Entity
public class Cliente {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;

    @OneToMany(mappedBy = "cliente",              // campo "cliente" de Pedido (lado dueño)
               cascade = CascadeType.ALL,
               orphanRemoval = true)
    private List<Pedido> pedidos = new ArrayList<>();

    // Método auxiliar: mantiene sincronizados ambos lados
    public void agregarPedido(Pedido p) {
        pedidos.add(p);
        p.setCliente(this);
    }
    public void quitarPedido(Pedido p) {
        pedidos.remove(p);
        p.setCliente(null);
    }
}

@Entity
public class Pedido {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime fecha;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id")              // columna FK en tabla pedido
    private Cliente cliente;
}
```
```
Tabla cliente:  id | nombre
Tabla pedido:   id | fecha | cliente_id (FK → cliente.id)
```

### Ejemplo: OneToOne
```java
@Entity
public class Usuario {
    @Id @GeneratedValue private Long id;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "perfil_id", referencedColumnName = "id")
    private Perfil perfil;                        // FK perfil_id en tabla usuario
}

@Entity
public class Perfil {
    @Id @GeneratedValue private Long id;
    private String bio;

    @OneToOne(mappedBy = "perfil")                // lado inverso (opcional)
    private Usuario usuario;
}
```

### Ejemplo: ManyToMany con tabla intermedia
```java
@Entity
public class Estudiante {
    @Id @GeneratedValue private Long id;
    private String nombre;

    @ManyToMany
    @JoinTable(name = "estudiante_curso",
        joinColumns        = @JoinColumn(name = "estudiante_id"),
        inverseJoinColumns = @JoinColumn(name = "curso_id"))
    private Set<Curso> cursos = new HashSet<>();   // Set evita duplicados y problemas de rendimiento
}

@Entity
public class Curso {
    @Id @GeneratedValue private Long id;
    private String titulo;

    @ManyToMany(mappedBy = "cursos")
    private Set<Estudiante> estudiantes = new HashSet<>();
}
```

### Ejemplo: ManyToMany con datos extra (tabla intermedia como entidad)
Cuando la relación necesita atributos propios (nota, fecha), se modela con una entidad intermedia:
```java
@Entity
public class Inscripcion {
    @Id @GeneratedValue private Long id;

    @ManyToOne(fetch = FetchType.LAZY) private Estudiante estudiante;
    @ManyToOne(fetch = FetchType.LAZY) private Curso curso;

    private LocalDate fechaInscripcion;
    private Double nota;
}
// Estudiante: @OneToMany(mappedBy="estudiante") List<Inscripcion>
// Curso:      @OneToMany(mappedBy="curso")      List<Inscripcion>
```

### Ejemplo: uso en el Service
```java
@Transactional
public void crearPedido(Long clienteId) {
    Cliente cliente = clienteRepo.findById(clienteId).orElseThrow();
    Pedido pedido = new Pedido();
    pedido.setFecha(LocalDateTime.now());
    cliente.agregarPedido(pedido);     // sincroniza ambos lados
    // cascade = ALL → el pedido se guarda al guardar/commit del cliente
}
```

### Valores por defecto de `fetch`
| Relación | Por defecto |
|---|---|
| `@OneToOne`, `@ManyToOne` | `EAGER` (conviene cambiar a `LAZY`) |
| `@OneToMany`, `@ManyToMany` | `LAZY` |

## 7. Herencia de entidades

| Estrategia / Anotación | Significado |
|---|---|
| `@Inheritance` | Define la estrategia de herencia. |
| `InheritanceType.SINGLE_TABLE` | Una sola tabla para toda la jerarquía + columna discriminadora. Rápida, con muchas columnas `null`. (Por defecto) |
| `InheritanceType.JOINED` | Una tabla por clase, unidas por la PK. Normalizada, requiere `JOIN`. |
| `InheritanceType.TABLE_PER_CLASS` | Una tabla completa por clase concreta. Poco usada. |
| `@DiscriminatorColumn` | Columna que indica el tipo de fila (`SINGLE_TABLE`). |
| `@DiscriminatorValue` | Valor de esa columna para cada subclase. |
| `@MappedSuperclass` | Clase base con campos comunes que **no** es entidad (no tiene tabla propia). |

### Ejemplo: `@MappedSuperclass` (campos comunes, lo más usado)
```java
@MappedSuperclass
@Getter @Setter
public abstract class BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate  @Column(updatable = false)
    private LocalDateTime creadoEn;

    @LastModifiedDate
    private LocalDateTime actualizadoEn;
}

@Entity
public class Producto extends BaseEntity {
    private String nombre;     // hereda id, creadoEn, actualizadoEn
}
```

### Ejemplo: herencia de entidades
```java
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo", discriminatorType = DiscriminatorType.STRING)
public abstract class Vehiculo {
    @Id @GeneratedValue private Long id;
    private String marca;
}

@Entity @DiscriminatorValue("AUTO")
public class Auto extends Vehiculo { private int puertas; }

@Entity @DiscriminatorValue("MOTO")
public class Moto extends Vehiculo { private int cilindrada; }
```
```
Tabla vehiculo: id | tipo (AUTO/MOTO) | marca | puertas | cilindrada
```

## 8. Repositorios

| Interfaz / Clase | Significado |
|---|---|
| `Repository<T, ID>` | Interfaz marcadora base. |
| `CrudRepository<T, ID>` | Operaciones CRUD básicas. |
| `ListCrudRepository<T, ID>` | Igual, pero devuelve `List` en vez de `Iterable`. |
| `PagingAndSortingRepository<T, ID>` | Añade paginación y ordenamiento. |
| `JpaRepository<T, ID>` | Todo lo anterior + funciones propias de JPA (`flush`, `saveAll`, `deleteAllInBatch`). **El más usado.** |
| `JpaSpecificationExecutor<T>` | Permite consultas dinámicas con `Specification`. |
| `QueryByExampleExecutor<T>` | Consultas por ejemplo (`Example`). |
| `@Repository` | Marca el repositorio (opcional en interfaces de Spring Data). |
| `@NoRepositoryBean` | Interfaz base intermedia que Spring **no** debe instanciar. |
| `T` | Tipo de la entidad. |
| `ID` | Tipo de la clave primaria (`Long`, `String`, `UUID`...). |

### Métodos heredados de `JpaRepository`
| Método | Qué hace | SQL aproximado |
|---|---|---|
| `save(entidad)` | Inserta si es nueva, actualiza si ya existe (según el ID). | `INSERT` / `UPDATE` |
| `saveAll(lista)` | Guarda varias. | |
| `saveAndFlush(e)` | Guarda y envía a BD inmediatamente. | |
| `findById(id)` | Busca por ID → `Optional<T>`. | `SELECT ... WHERE id=?` |
| `getReferenceById(id)` | Devuelve un **proxy** sin ir a la BD (error al usarlo si no existe). | |
| `findAll()` | Todas las filas. | `SELECT *` |
| `findAll(Sort)` | Todas, ordenadas. | `ORDER BY` |
| `findAll(Pageable)` | Una página (`Page<T>`). | `LIMIT/OFFSET` + `COUNT` |
| `findAllById(ids)` | Varias por lista de IDs. | `WHERE id IN (...)` |
| `existsById(id)` | ¿Existe? → `boolean`. | |
| `count()` | Cantidad de filas. | `SELECT COUNT(*)` |
| `deleteById(id)` | Borra por ID. | `DELETE` |
| `delete(entidad)` | Borra una entidad. | |
| `deleteAll()` | Borra todo (uno por uno). | |
| `deleteAllById(ids)` | Borra por lista de IDs. | |
| `deleteAllInBatch()` | Borra todo con **una sola** sentencia. | `DELETE FROM tabla` |
| `flush()` | Fuerza el envío de cambios pendientes a la BD. | |

### Ejemplo: declarar un repositorio
```java
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Sin escribir implementación: Spring la genera en tiempo de ejecución
}
```

### Ejemplo: usar los métodos básicos
```java
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repo;

    public Usuario crear(Usuario u) {
        return repo.save(u);                       // INSERT (id es null)
    }

    public Usuario actualizar(Long id, String nuevoNombre) {
        Usuario u = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No existe " + id));
        u.setNombre(nuevoNombre);
        return repo.save(u);                       // UPDATE (id existe)
    }

    public List<Usuario> todos()            { return repo.findAll(); }
    public boolean existe(Long id)          { return repo.existsById(id); }
    public long cantidad()                  { return repo.count(); }
    public void borrar(Long id)             { repo.deleteById(id); }
}
```

### `save()` — cómo decide insertar o actualizar
- ID `null` (o `0` en primitivos) → entidad **nueva** → `persist` (INSERT).
- ID con valor → `merge` (hace un `SELECT` y luego `UPDATE`).
- Con `@Version` nula también se considera nueva.

### `findById` vs `getReferenceById`
```java
Usuario u1 = repo.findById(1L).orElseThrow();   // consulta a BD ya
Usuario ref = repo.getReferenceById(1L);        // proxy, NO consulta aún
pedido.setCliente(ref);                         // útil para asignar FK sin cargar el cliente
```

### Ejemplo: repositorio base propio
```java
@NoRepositoryBean
public interface BaseRepository<T, ID> extends JpaRepository<T, ID> {
    // métodos comunes a todos los repositorios
}
```

## 9. Query methods (consultas por nombre)

Spring Data genera la consulta **a partir del nombre del método**: `findBy` + Propiedad + Operador.

### Prefijos
| Prefijo | Devuelve / hace |
|---|---|
| `findBy...` / `getBy...` / `readBy...` / `queryBy...` / `searchBy...` | Busca y devuelve entidad(es). |
| `findFirstBy...` / `findTopBy...` | Primer resultado. |
| `findFirst3By...` / `findTop10By...` | Los primeros N. |
| `findDistinctBy...` | Sin duplicados. |
| `existsBy...` | `boolean`. |
| `countBy...` | `long` con la cantidad. |
| `deleteBy...` / `removeBy...` | Elimina (requiere `@Transactional`). |

### Palabras clave (operadores)
| Palabra | Equivale a | Ejemplo |
|---|---|---|
| `And` | `AND` | `findByNombreAndEmail(n, e)` |
| `Or` | `OR` | `findByNombreOrEmail(n, e)` |
| `Is`, `Equals` | `=` | `findByEstadoIs(e)` |
| `Not` / `IsNot` | `<>` | `findByEstadoNot(e)` |
| `Between` | `BETWEEN` | `findByEdadBetween(18, 30)` |
| `LessThan` / `LessThanEqual` | `<` / `<=` | `findByPrecioLessThan(100)` |
| `GreaterThan` / `GreaterThanEqual` | `>` / `>=` | `findByEdadGreaterThanEqual(18)` |
| `After` / `Before` | `>` / `<` en fechas | `findByFechaAfter(fecha)` |
| `IsNull` / `IsNotNull` | `IS NULL` / `IS NOT NULL` | `findByTelefonoIsNull()` |
| `Like` / `NotLike` | `LIKE` (tú pones `%`) | `findByNombreLike("%an%")` |
| `StartingWith` | `LIKE 'x%'` | `findByNombreStartingWith("An")` |
| `EndingWith` | `LIKE '%x'` | `findByEmailEndingWith("@gmail.com")` |
| `Containing` | `LIKE '%x%'` | `findByNombreContaining("an")` |
| `In` / `NotIn` | `IN (...)` | `findByEstadoIn(List<Estado>)` |
| `True` / `False` | `= true` / `= false` | `findByActivoTrue()` |
| `IgnoreCase` | Ignora mayúsculas | `findByNombreIgnoreCase("ana")` |
| `OrderBy...Asc/Desc` | `ORDER BY` | `findByEstadoOrderByNombreAsc(e)` |
| `Exists` | Condición `EXISTS` | |
| `Regex` | Expresión regular (según BD) | |

### Ejemplos
```java
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);
    // SELECT * FROM usuarios WHERE email = ?

    List<Usuario> findByNombreContainingIgnoreCase(String texto);
    // WHERE UPPER(nombre) LIKE UPPER('%texto%')

    List<Usuario> findByEstadoAndEdadGreaterThanEqual(Estado estado, int edad);
    // WHERE estado = ? AND edad >= ?

    List<Usuario> findByEdadBetween(int desde, int hasta);

    List<Usuario> findByEstadoInOrderByNombreAsc(Collection<Estado> estados);

    List<Usuario> findByActivoTrue();

    List<Usuario> findByTelefonoIsNull();

    Usuario findFirstByOrderByCreadoEnDesc();              // el más reciente
    List<Usuario> findTop5ByOrderByEdadDesc();             // los 5 mayores

    boolean existsByEmail(String email);
    long countByEstado(Estado estado);

    @Transactional
    long deleteByEstado(Estado estado);                    // devuelve cuántas borró

    // Navegar relaciones con guion bajo o camelCase:
    List<Pedido> findByClienteNombre(String nombre);       // pedido.cliente.nombre
    List<Pedido> findByCliente_Id(Long clienteId);         // el "_" aclara el recorrido

    // Con paginación y orden
    Page<Usuario> findByEstado(Estado estado, Pageable pageable);
    List<Usuario> findByEstado(Estado estado, Sort sort);
}
```

### Tipos de retorno permitidos
| Retorno | Cuándo usarlo |
|---|---|
| `Entidad` | Un resultado (error si hay más de uno; `null` si ninguno). |
| `Optional<Entidad>` | Uno o ninguno (**recomendado**). |
| `List<Entidad>` / `Set<Entidad>` | Varios. |
| `Stream<Entidad>` | Procesamiento en streaming (cerrar con try-with-resources y `@Transactional`). |
| `Page<Entidad>` | Página con total. |
| `Slice<Entidad>` | Página sin consultar el total. |
| `boolean` / `long` | Con `existsBy` / `countBy`. |
| `CompletableFuture<...>` | Con `@Async`. |
| Proyección (DTO/interfaz) | Ver sección 12. |

> Si el nombre del método es muy largo o complicado, usa `@Query`.

## 10. Consultas con @Query (JPQL y SQL nativo)

| Término | Significado |
|---|---|
| `@Query` | Define la consulta a mano sobre un método. |
| `nativeQuery = true` | La consulta es SQL puro (no JPQL). |
| `@Param("nombre")` | Vincula un parámetro con `:nombre`. |
| `?1`, `?2` | Parámetros por posición. |
| `:nombre` | Parámetro con nombre (**recomendado**). |
| `@Modifying` | Obligatoria para `UPDATE` / `DELETE` / `INSERT`. |
| `clearAutomatically = true` | Limpia el contexto de persistencia tras ejecutar. |
| `flushAutomatically = true` | Hace flush antes de ejecutar. |
| `JOIN FETCH` | Une y **carga** la relación en la misma consulta (evita N+1). |
| `LEFT JOIN FETCH` | Igual, incluyendo padres sin hijos. |
| `DISTINCT` | Evita duplicados al hacer `JOIN FETCH` de colecciones. |
| `SELECT NEW paquete.Dto(...)` | Construye un DTO directamente. |
| `LIKE` / `LOWER()` / `UPPER()` / `CONCAT()` | Funciones JPQL comunes. |
| `COUNT` / `SUM` / `AVG` / `MIN` / `MAX` | Funciones de agregación. |
| `GROUP BY` / `HAVING` | Agrupar y filtrar grupos. |
| `ORDER BY` | Ordenar. |
| `IN`, `BETWEEN`, `IS NULL`, `EXISTS`, `MEMBER OF` | Condiciones. |
| `countQuery` | Consulta de conteo personalizada para paginación. |
| `@NamedQuery` | Consulta con nombre declarada en la entidad. |
| `@NamedNativeQuery` | Igual, en SQL nativo. |
| `@EntityGraph` | Define qué relaciones cargar (alternativa a `JOIN FETCH`). |
| `@QueryHints` | Pistas para el proveedor (cache, readOnly...). |
| `@Lock` | Bloqueo pesimista/optimista en la consulta. |
| **SpEL** | `#{#entityName}` permite escribir consultas genéricas. |

> **JPQL:** se escribe con nombres de **clases y atributos** Java (`FROM Usuario u WHERE u.nombre = ...`), no con tablas y columnas.

### Ejemplos JPQL
```java
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Parámetro con nombre
    @Query("SELECT u FROM Usuario u WHERE u.email = :email")
    Optional<Usuario> buscarPorEmail(@Param("email") String email);

    // Parámetro posicional
    @Query("SELECT u FROM Usuario u WHERE u.edad > ?1 AND u.estado = ?2")
    List<Usuario> mayoresQue(int edad, Estado estado);

    // LIKE con CONCAT (case-insensitive)
    @Query("SELECT u FROM Usuario u WHERE LOWER(u.nombre) LIKE LOWER(CONCAT('%', :texto, '%'))")
    List<Usuario> buscarPorTexto(@Param("texto") String texto);

    // Agregación
    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.estado = :estado")
    long contar(@Param("estado") Estado estado);

    @Query("SELECT AVG(u.edad) FROM Usuario u")
    Double edadPromedio();

    // Agrupar → devuelve Object[] por fila
    @Query("SELECT u.estado, COUNT(u) FROM Usuario u GROUP BY u.estado")
    List<Object[]> contarPorEstado();

    // JOIN FETCH: trae cliente con sus pedidos en una sola consulta (evita N+1)
    @Query("SELECT DISTINCT c FROM Cliente c LEFT JOIN FETCH c.pedidos WHERE c.id = :id")
    Optional<Cliente> buscarConPedidos(@Param("id") Long id);

    // JOIN normal para filtrar por una relación
    @Query("SELECT p FROM Pedido p JOIN p.cliente c WHERE c.nombre = :nombre")
    List<Pedido> pedidosDeCliente(@Param("nombre") String nombre);

    // Subconsulta
    @Query("SELECT u FROM Usuario u WHERE u.edad > (SELECT AVG(x.edad) FROM Usuario x)")
    List<Usuario> mayoresAlPromedio();

    // Paginación con consulta de conteo
    @Query(value = "SELECT u FROM Usuario u WHERE u.estado = :estado",
           countQuery = "SELECT COUNT(u) FROM Usuario u WHERE u.estado = :estado")
    Page<Usuario> porEstado(@Param("estado") Estado estado, Pageable pageable);
}
```

### Ejemplo: SQL nativo
```java
@Query(value = "SELECT * FROM usuarios WHERE email = :email", nativeQuery = true)
Optional<Usuario> nativoPorEmail(@Param("email") String email);

@Query(value = "SELECT estado, COUNT(*) AS total FROM usuarios GROUP BY estado",
       nativeQuery = true)
List<Object[]> estadisticas();

// Nativo con paginación (countQuery obligatorio)
@Query(value = "SELECT * FROM usuarios WHERE estado = :estado",
       countQuery = "SELECT COUNT(*) FROM usuarios WHERE estado = :estado",
       nativeQuery = true)
Page<Usuario> nativoPaginado(@Param("estado") String estado, Pageable pageable);
```

### Ejemplo: `UPDATE` / `DELETE` con `@Modifying`
```java
@Modifying(clearAutomatically = true, flushAutomatically = true)
@Transactional
@Query("UPDATE Usuario u SET u.estado = :estado WHERE u.ultimoAcceso < :fecha")
int desactivarInactivos(@Param("estado") Estado estado, @Param("fecha") LocalDate fecha);
// devuelve la cantidad de filas afectadas

@Modifying
@Transactional
@Query("DELETE FROM Usuario u WHERE u.estado = 'INACTIVO'")
int eliminarInactivos();
```
> Las consultas `@Modifying` van directo a la BD y **no** actualizan el contexto de persistencia; por eso se usa `clearAutomatically = true`.

### Ejemplo: DTO directo con `SELECT NEW`
```java
public record UsuarioResumen(Long id, String nombre, String email) {}

@Query("SELECT new com.empresa.dto.UsuarioResumen(u.id, u.nombre, u.email) " +
       "FROM Usuario u WHERE u.estado = :estado")
List<UsuarioResumen> resumenes(@Param("estado") Estado estado);
```

### Ejemplo: `@EntityGraph` (alternativa a `JOIN FETCH`)
```java
@EntityGraph(attributePaths = {"pedidos", "perfil"})
Optional<Cliente> findWithDetallesById(Long id);

@EntityGraph(attributePaths = "cliente")
List<Pedido> findByFechaAfter(LocalDateTime fecha);   // trae el cliente junto a cada pedido
```

### Ejemplo: `@NamedQuery`
```java
@Entity
@NamedQuery(name = "Usuario.activos",
            query = "SELECT u FROM Usuario u WHERE u.estado = 'ACTIVO'")
public class Usuario { ... }

// En el repositorio, el método con el mismo nombre la usa:
List<Usuario> activos();    // Usuario.activos
```

### Ejemplo: bloqueo pesimista
```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT p FROM Producto p WHERE p.id = :id")
Optional<Producto> buscarParaActualizar(@Param("id") Long id);   // SELECT ... FOR UPDATE
```

### Resumen JPQL vs SQL nativo
| | JPQL | SQL nativo |
|---|---|---|
| Se escribe con | Entidades y atributos | Tablas y columnas |
| Portable entre motores | Sí | No |
| Funciones propias del motor | Limitado | Todas |
| Cuándo usar | Casi siempre | Consultas muy complejas o específicas |

## 11. Paginación y ordenamiento

| Término | Significado |
|---|---|
| `Pageable` | Interfaz que indica página, tamaño y orden. |
| `PageRequest.of(page, size)` | Crea un `Pageable` (**la página empieza en 0**). |
| `PageRequest.of(page, size, Sort)` | Con orden. |
| `Pageable.unpaged()` | Sin paginar. |
| `Sort` | Define el orden. |
| `Sort.by("campo")` | Orden por campo. |
| `.ascending()` / `.descending()` | Dirección. |
| `Sort.by(Direction, "campo")` | Dirección + campo. |
| `Sort.Order` | Un criterio individual de orden. |
| `Page<T>` | Resultado paginado **con** el total (hace un `COUNT` extra). |
| `Slice<T>` | Resultado paginado **sin** total (más eficiente; solo sabe si hay "siguiente"). |
| `getContent()` | Lista de elementos de la página. |
| `getTotalElements()` | Total de filas. |
| `getTotalPages()` | Total de páginas. |
| `getNumber()` | Número de la página actual. |
| `getSize()` | Tamaño de página. |
| `getNumberOfElements()` | Elementos en esta página. |
| `hasNext()` / `hasPrevious()` | Si hay página siguiente / anterior. |
| `isFirst()` / `isLast()` | Si es la primera / última. |
| `map(...)` | Transforma el contenido (Entity → DTO) conservando la paginación. |
| `PagedModel` / `Page` serializado | Estructura JSON de respuesta paginada. |
| **Offset pagination** | Paginación clásica `LIMIT/OFFSET` (lenta con páginas profundas). |
| **Keyset (seek) pagination** | Paginación por último ID visto; más eficiente. |
| `ScrollPosition` | Paginación por cursor/keyset en Spring Data 3.1+. |

### Ejemplos
```java
// Crear Pageable y Sort
Pageable p1 = PageRequest.of(0, 10);                                   // página 1, 10 filas
Pageable p2 = PageRequest.of(0, 10, Sort.by("nombre").ascending());
Pageable p3 = PageRequest.of(2, 20, Sort.by("edad").descending()
                                        .and(Sort.by("nombre")));      // varios criterios
Pageable p4 = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "creadoEn"));

// Usar en el repositorio
Page<Usuario> pagina = repo.findAll(p2);
Page<Usuario> activos = repo.findByEstado(Estado.ACTIVO, p2);

// Leer la página
List<Usuario> contenido = pagina.getContent();
long total = pagina.getTotalElements();
int paginas = pagina.getTotalPages();
int actual = pagina.getNumber();
boolean hayMas = pagina.hasNext();

// Convertir Entity → DTO conservando paginación
Page<UsuarioResponse> dtos = pagina.map(mapper::toResponse);

// Recorrer todas las páginas
Pageable pageable = PageRequest.of(0, 100);
Page<Usuario> actualPag;
do {
    actualPag = repo.findAll(pageable);
    actualPag.forEach(this::procesar);
    pageable = actualPag.nextPageable();
} while (actualPag.hasNext());
```

### Ejemplo: en el controlador
```java
@GetMapping("/api/usuarios")
public Page<UsuarioResponse> listar(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "nombre") String sortBy) {
    Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
    return service.listar(pageable);
}

// O directamente con Pageable:  GET /api/usuarios?page=0&size=10&sort=nombre,desc
@GetMapping("/api/usuarios2")
public Page<UsuarioResponse> listar2(Pageable pageable) {
    return service.listar(pageable);
}
```

### Ejemplo: respuesta JSON de un `Page`
```json
{
  "content": [ {"id":1,"nombre":"Ana"}, {"id":2,"nombre":"Luis"} ],
  "pageable": { "pageNumber": 0, "pageSize": 2 },
  "totalElements": 25,
  "totalPages": 13,
  "last": false,
  "first": true,
  "number": 0,
  "size": 2,
  "numberOfElements": 2
}
```

### `Page` vs `Slice`
| | `Page` | `Slice` |
|---|---|---|
| Consulta `COUNT` extra | Sí | No |
| Sabe el total de páginas | Sí | No |
| Sabe si hay siguiente | Sí | Sí |
| Ideal para | Tablas con "página 1 de N" | "Cargar más" / scroll infinito |

## 12. Proyecciones

Sirven para traer **solo algunas columnas** en vez de la entidad completa.

| Tipo | Significado |
|---|---|
| **Proyección de interfaz (cerrada)** | Interfaz con getters de los campos que quieres. |
| **Proyección de interfaz (abierta)** | Usa `@Value("#{...}")` (SpEL) para valores calculados. |
| **Proyección de clase / DTO** | Clase o `record` con constructor. |
| **Proyección dinámica** | El tipo se elige al llamar al método (`Class<T>`). |
| `Tuple` | Resultado genérico clave-valor de JPA. |
| `@Value` (de Spring) | Expresión SpEL en proyecciones abiertas. |

### Ejemplos
```java
// 1. Interfaz (cerrada)
public interface UsuarioNombreEmail {
    String getNombre();
    String getEmail();
}
List<UsuarioNombreEmail> findByEstado(Estado estado);

// 2. DTO/record (se mapea por nombre de constructor)
public record UsuarioDto(String nombre, String email) {}
List<UsuarioDto> findByEdadGreaterThan(int edad);

// 3. Proyección de interfaz abierta (calculada)
public interface UsuarioConEtiqueta {
    @Value("#{target.nombre + ' <' + target.email + '>'}")
    String getEtiqueta();
}

// 4. Proyección dinámica
<T> List<T> findByEstado(Estado estado, Class<T> tipo);
repo.findByEstado(ACTIVO, UsuarioDto.class);
repo.findByEstado(ACTIVO, Usuario.class);

// 5. Proyección anidada
public interface PedidoResumen {
    Long getId();
    ClienteInfo getCliente();
    interface ClienteInfo { String getNombre(); }
}

// 6. Con @Query nativa (usa alias iguales a los getters)
@Query(value = "SELECT nombre AS nombre, email AS email FROM usuarios", nativeQuery = true)
List<UsuarioNombreEmail> nativas();
```

## 13. Specification y consultas dinámicas

Para filtros opcionales o combinados (buscadores con muchos criterios).

| Término | Significado |
|---|---|
| `Specification<T>` | Una condición reutilizable que se traduce a `WHERE`. |
| `JpaSpecificationExecutor<T>` | Interfaz que el repositorio debe extender. |
| `Root<T>` | La entidad raíz de la consulta (el `FROM`). |
| `CriteriaQuery<?>` | La consulta en construcción. |
| `CriteriaBuilder` | Fábrica de condiciones (`equal`, `like`, `and`, `or`...). |
| `Predicate` | Una condición. |
| `Specification.where(...)` | Inicia una especificación. |
| `.and()` / `.or()` / `Specification.not()` | Combinadores. |
| `Example<T>` / `ExampleMatcher` | Consulta por ejemplo: rellenas un objeto y Spring busca similares. |
| **Criteria API** | API programática de JPA para crear consultas. |
| **QueryDSL** | Librería alternativa con consultas tipadas (`QUsuario`). |
| **Metamodelo (`Usuario_`)** | Clases generadas con los nombres de atributos, para evitar usar strings. |

### Ejemplo: Specifications
```java
public interface UsuarioRepository
        extends JpaRepository<Usuario, Long>, JpaSpecificationExecutor<Usuario> {}

public class UsuarioSpecs {

    public static Specification<Usuario> nombreContiene(String texto) {
        return (root, query, cb) ->
            texto == null ? null
                          : cb.like(cb.lower(root.get("nombre")), "%" + texto.toLowerCase() + "%");
    }

    public static Specification<Usuario> conEstado(Estado estado) {
        return (root, query, cb) ->
            estado == null ? null : cb.equal(root.get("estado"), estado);
    }

    public static Specification<Usuario> edadMinima(Integer edad) {
        return (root, query, cb) ->
            edad == null ? null : cb.greaterThanOrEqualTo(root.get("edad"), edad);
    }
}

// Uso: los filtros nulos se ignoran
public Page<Usuario> buscar(String nombre, Estado estado, Integer edadMin, Pageable pageable) {
    Specification<Usuario> spec = Specification
            .where(UsuarioSpecs.nombreContiene(nombre))
            .and(UsuarioSpecs.conEstado(estado))
            .and(UsuarioSpecs.edadMinima(edadMin));
    return repo.findAll(spec, pageable);
}
```

### Ejemplo: Query By Example
```java
Usuario probe = new Usuario();
probe.setEstado(Estado.ACTIVO);
probe.setNombre("an");

ExampleMatcher matcher = ExampleMatcher.matching()
        .withMatcher("nombre", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
        .withIgnoreNullValues();

List<Usuario> resultado = repo.findAll(Example.of(probe, matcher));
```
(el repositorio debe extender `QueryByExampleExecutor`, que `JpaRepository` ya incluye).

### Ejemplo: Criteria API manual con `EntityManager`
```java
CriteriaBuilder cb = em.getCriteriaBuilder();
CriteriaQuery<Usuario> cq = cb.createQuery(Usuario.class);
Root<Usuario> root = cq.from(Usuario.class);
cq.select(root)
  .where(cb.and(
        cb.equal(root.get("estado"), Estado.ACTIVO),
        cb.greaterThan(root.get("edad"), 18)))
  .orderBy(cb.asc(root.get("nombre")));
List<Usuario> lista = em.createQuery(cq).getResultList();
```

## 14. Transacciones

| Término | Significado |
|---|---|
| `@Transactional` | Ejecuta el método dentro de una transacción (commit al terminar, rollback si hay excepción). |
| **TransactionManager** | Componente que gestiona las transacciones (`JpaTransactionManager`). |
| `readOnly = true` | Optimiza consultas de solo lectura (sin dirty checking). |
| `propagation` | Qué hacer si ya hay una transacción en curso. |
| `Propagation.REQUIRED` | (Por defecto) Usa la existente o crea una. |
| `Propagation.REQUIRES_NEW` | Siempre crea una nueva, suspendiendo la actual. |
| `Propagation.SUPPORTS` | Usa una si existe; si no, sin transacción. |
| `Propagation.MANDATORY` | Exige una transacción existente (si no, error). |
| `Propagation.NOT_SUPPORTED` | Ejecuta sin transacción. |
| `Propagation.NEVER` | Falla si hay transacción. |
| `Propagation.NESTED` | Transacción anidada con savepoint. |
| `isolation` | Nivel de aislamiento entre transacciones concurrentes. |
| `Isolation.READ_UNCOMMITTED` | Permite lecturas sucias. |
| `Isolation.READ_COMMITTED` | Solo lee datos confirmados. |
| `Isolation.REPEATABLE_READ` | La misma lectura da lo mismo dentro de la transacción. |
| `Isolation.SERIALIZABLE` | Aislamiento total (más lento). |
| `rollbackFor` | Excepciones que provocan rollback. |
| `noRollbackFor` | Excepciones que **no** provocan rollback. |
| `timeout` | Tiempo máximo de la transacción. |
| **Dirty read** | Leer datos no confirmados de otra transacción. |
| **Non-repeatable read** | Releer un dato y obtener otro valor. |
| **Phantom read** | Releer una consulta y aparecen filas nuevas. |
| **Lock optimista** | Detecta conflictos con `@Version` al guardar. |
| **Lock pesimista** | Bloquea las filas al leerlas (`SELECT ... FOR UPDATE`). |
| `TransactionTemplate` | Control programático de transacciones. |
| `TransactionSynchronizationManager` | Acceso al estado de la transacción actual. |
| `@TransactionalEventListener` | Ejecuta un listener después del commit. |
| **Self-invocation** | Llamar a un método `@Transactional` desde la misma clase **no** activa la transacción (limitación del proxy). |

### Reglas importantes
- Por defecto el **rollback** ocurre solo con `RuntimeException` y `Error`; las excepciones *checked* **no** lo provocan salvo que uses `rollbackFor`.
- `@Transactional` solo funciona en métodos **públicos** y llamados **desde otra clase** (proxy de Spring).
- Se coloca normalmente en la capa **Service**, no en el Controller.
- Los repositorios de Spring Data ya son transaccionales (`save`, `delete` tienen su propia transacción; las consultas son `readOnly`).

### Ejemplos
```java
@Service
@Transactional(readOnly = true)             // por defecto en la clase: solo lectura
public class PedidoService {

    public Pedido buscar(Long id) {          // usa readOnly de la clase
        return repo.findById(id).orElseThrow();
    }

    @Transactional                           // sobrescribe: lectura/escritura
    public Pedido crear(PedidoRequest req) {
        Pedido p = repo.save(mapper.toEntity(req));
        stockService.descontar(req.items());   // si falla, se revierte todo, incluso el save
        return p;
    }

    @Transactional(rollbackFor = Exception.class)   // también ante excepciones checked
    public void procesar() throws IOException { ... }

    @Transactional(noRollbackFor = StockBajoException.class)
    public void vender() { ... }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void auditar(String accion) {
        // se confirma aunque la transacción externa haga rollback
        auditRepo.save(new Auditoria(accion));
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, timeout = 5)
    public void operacionCritica() { ... }
}
```

### Ejemplo: dirty checking (no hace falta `save`)
```java
@Transactional
public void renombrar(Long id, String nombre) {
    Usuario u = repo.findById(id).orElseThrow();
    u.setNombre(nombre);       // Hibernate detecta el cambio y hace UPDATE al commit
    // repo.save(u) no es necesario aquí
}
```

### Ejemplo: error típico de self-invocation
```java
@Service
public class MiService {
    public void metodoA() {
        metodoB();                     // ✗ NO abre transacción (llamada interna)
    }
    @Transactional
    public void metodoB() { ... }
}
// Solución: mover metodoB a otro bean, o inyectar el propio servicio, o usar TransactionTemplate.
```

### Ejemplo: `TransactionTemplate`
```java
@Autowired private TransactionTemplate tx;

public void hacerAlgo() {
    tx.executeWithoutResult(status -> {
        repo.save(new Usuario());
        if (algoFalla()) status.setRollbackOnly();
    });
}
```

### Ejemplo: bloqueo optimista con `@Version`
```java
@Entity
public class Cuenta {
    @Id @GeneratedValue private Long id;
    private BigDecimal saldo;
    @Version private Long version;
}
// Si dos usuarios modifican la misma cuenta a la vez, el segundo recibe
// ObjectOptimisticLockingFailureException (se puede reintentar).
```

### Cuándo se hace COMMIT y ROLLBACK
```
Método @Transactional inicia  →  BEGIN
   ...ejecuta operaciones...
   termina normal              →  COMMIT
   lanza RuntimeException      →  ROLLBACK
   lanza Exception (checked)   →  COMMIT (salvo rollbackFor)
```

## 15. Ciclo de vida y EntityManager

### Estados de una entidad
| Estado | Significado |
|---|---|
| **New / Transient** | Objeto creado con `new`, sin relación con la BD. |
| **Managed / Persistent** | Gestionado por el contexto de persistencia: los cambios se sincronizan solos. |
| **Detached** | Estuvo gestionado pero la sesión/transacción terminó; cambios no se guardan solos. |
| **Removed** | Marcado para eliminar al hacer flush/commit. |

```
new Usuario() ──persist/save──▶ MANAGED ──commit/close──▶ DETACHED
                                   │  ▲                       │
                                remove │ merge/save ◀──────────┘
                                   ▼
                                REMOVED
```

### Métodos del `EntityManager`
| Método | Significado |
|---|---|
| `persist(e)` | Hace la entidad nueva gestionada (INSERT al flush). |
| `merge(e)` | Copia el estado de una entidad *detached* a una gestionada (devuelve la gestionada). |
| `remove(e)` | Marca para borrar. |
| `find(Clase, id)` | Busca por ID (devuelve `null` si no existe). |
| `getReference(Clase, id)` | Devuelve un proxy sin ir a la BD. |
| `refresh(e)` | Recarga el estado desde la BD. |
| `flush()` | Envía cambios pendientes a la BD. |
| `clear()` | Vacía el contexto (todas pasan a *detached*). |
| `detach(e)` | Desvincula una entidad. |
| `contains(e)` | ¿Está gestionada? |
| `createQuery(jpql)` | Crea una consulta JPQL. |
| `createNativeQuery(sql)` | Consulta SQL nativa. |
| `createNamedQuery(name)` | Consulta con nombre. |
| `getCriteriaBuilder()` | Para Criteria API. |
| `getTransaction()` | Transacción (solo en entorno no gestionado). |
| `@PersistenceContext` | Inyecta el `EntityManager`. |

### Ejemplo: usar `EntityManager` directamente
```java
@Repository
public class UsuarioCustomRepository {

    @PersistenceContext
    private EntityManager em;

    public List<Usuario> buscarAvanzado(String texto) {
        return em.createQuery(
                "SELECT u FROM Usuario u WHERE u.nombre LIKE :t", Usuario.class)
                 .setParameter("t", "%" + texto + "%")
                 .setMaxResults(20)
                 .getResultList();
    }

    @Transactional
    public void guardar(Usuario u) {
        em.persist(u);
    }
}
```

### Ejemplo: repositorio personalizado (fragmento propio)
```java
public interface UsuarioRepositoryCustom {
    List<Usuario> buscarAvanzado(String texto);
}

public class UsuarioRepositoryCustomImpl implements UsuarioRepositoryCustom {  // sufijo "Impl"
    @PersistenceContext private EntityManager em;
    public List<Usuario> buscarAvanzado(String texto) { ... }
}

public interface UsuarioRepository
        extends JpaRepository<Usuario, Long>, UsuarioRepositoryCustom {}
```

### Ejemplo: `LazyInitializationException`
```java
Cliente c = repo.findById(1L).orElseThrow();   // fuera de transacción
c.getPedidos().size();                         // ✗ LazyInitializationException (sesión cerrada)
// Soluciones: @Transactional en el service, JOIN FETCH, @EntityGraph o usar DTOs.
```

## 16. Lazy vs Eager y el problema N+1

| Término | Significado |
|---|---|
| **`FetchType.LAZY`** | Carga diferida: la relación se consulta solo cuando se accede a ella. |
| **`FetchType.EAGER`** | Carga inmediata: se trae junto con la entidad. |
| **Proxy** | Objeto "vacío" que Hibernate pone para las relaciones lazy. |
| **Problema N+1** | 1 consulta para la lista + N consultas extra (una por cada elemento) para cargar relaciones. |
| **`LazyInitializationException`** | Acceder a una relación lazy con la sesión ya cerrada. |
| **`JOIN FETCH`** | Solución: traer relaciones en la misma consulta. |
| **`@EntityGraph`** | Solución: indicar qué cargar sin escribir JPQL. |
| **`@BatchSize(size=N)`** | Reduce las N consultas agrupando cargas en lotes. |
| **Open Session In View (OSIV)** | Mantiene la sesión abierta durante toda la petición web (oculta problemas, mejor desactivar). |
| **Producto cartesiano** | Al hacer `JOIN FETCH` de varias colecciones a la vez, se multiplican las filas. |
| `MultipleBagFetchException` | Error por hacer fetch de varias `List` a la vez (usar `Set` o consultas separadas). |
| `Hibernate.initialize(x)` | Fuerza la carga de una relación lazy. |
| **DTO** | Objeto que evita exponer entidades y relaciones lazy al exterior. |

### Ejemplo del problema N+1
```java
List<Pedido> pedidos = pedidoRepo.findAll();         // 1 consulta
for (Pedido p : pedidos) {
    System.out.println(p.getCliente().getNombre());  // +1 consulta POR CADA pedido (lazy)
}
// 100 pedidos = 101 consultas
```

### Soluciones
```java
// 1. JOIN FETCH
@Query("SELECT p FROM Pedido p JOIN FETCH p.cliente")
List<Pedido> findAllConCliente();

// 2. EntityGraph
@EntityGraph(attributePaths = "cliente")
List<Pedido> findAll();

// 3. BatchSize (en la entidad o relación)
@OneToMany(mappedBy = "cliente")
@BatchSize(size = 20)
private List<Pedido> pedidos;

// 4. Proyección/DTO: traer solo lo necesario
@Query("SELECT new com.empresa.dto.PedidoDto(p.id, c.nombre) FROM Pedido p JOIN p.cliente c")
List<PedidoDto> findDtos();
```

### Detectar N+1
```properties
spring.jpa.show-sql=true
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.orm.jdbc.bind=TRACE     # muestra los valores de parámetros
spring.jpa.properties.hibernate.generate_statistics=true
```

### Regla práctica
- Pon **todas** las relaciones en `LAZY`.
- Carga lo necesario por consulta (`JOIN FETCH`, `@EntityGraph`, DTOs).
- Evita devolver entidades directamente en la API (usa DTOs).

## 17. Auditoría y callbacks

| Término | Significado |
|---|---|
| `@EnableJpaAuditing` | Activa la auditoría automática. |
| `@EntityListeners(AuditingEntityListener.class)` | Habilita auditoría en la entidad. |
| `@CreatedDate` | Fecha de creación (se rellena sola). |
| `@LastModifiedDate` | Fecha de última modificación. |
| `@CreatedBy` | Usuario creador. |
| `@LastModifiedBy` | Usuario que modificó. |
| `AuditorAware<T>` | Interfaz que indica quién es el usuario actual. |
| `@PrePersist` | Método que corre antes de insertar. |
| `@PostPersist` | Después de insertar. |
| `@PreUpdate` | Antes de actualizar. |
| `@PostUpdate` | Después de actualizar. |
| `@PreRemove` / `@PostRemove` | Antes / después de borrar. |
| `@PostLoad` | Después de cargar la entidad. |
| `@CreationTimestamp` / `@UpdateTimestamp` | Alternativa de Hibernate para fechas automáticas. |
| **Hibernate Envers** | Librería para historial completo de cambios (`@Audited`). |

### Ejemplo: auditoría con Spring Data
```java
@SpringBootApplication
@EnableJpaAuditing
public class App { ... }

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter
public abstract class Auditable {

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime creadoEn;

    @LastModifiedDate
    private LocalDateTime modificadoEn;

    @CreatedBy
    @Column(updatable = false)
    private String creadoPor;

    @LastModifiedBy
    private String modificadoPor;
}

@Component
public class AuditorAwareImpl implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return Optional.ofNullable(auth).map(Authentication::getName);
    }
}
```

### Ejemplo: callbacks y timestamps de Hibernate
```java
@Entity
public class Articulo {

    @CreationTimestamp @Column(updatable = false)
    private LocalDateTime creado;

    @UpdateTimestamp
    private LocalDateTime actualizado;

    @PrePersist
    void antesDeGuardar() {
        if (slug == null) slug = titulo.toLowerCase().replace(' ', '-');
    }

    @PreUpdate
    void antesDeActualizar() { /* validaciones */ }
}
```

## 18. Excepciones comunes

| Excepción | Causa habitual |
|---|---|
| `DataAccessException` | Raíz de las excepciones de acceso a datos de Spring (traduce las de JPA/JDBC). |
| `DataIntegrityViolationException` | Viola una restricción: `UNIQUE` duplicado, `NOT NULL`, FK inexistente, valor demasiado largo. |
| `DuplicateKeyException` | Clave duplicada (JDBC). |
| `ConstraintViolationException` | Restricción violada (Hibernate/Bean Validation). |
| `EntityNotFoundException` | Entidad no encontrada (`getReferenceById`, `getOne`). |
| `EmptyResultDataAccessException` | `deleteById` de un ID inexistente (en versiones antiguas). |
| `NoSuchElementException` | `Optional.get()` / `orElseThrow()` sin valor. |
| `IncorrectResultSizeDataAccessException` | Se esperaba 1 resultado y hubo varios (o ninguno). |
| `NonUniqueResultException` | La consulta devolvió más de un resultado. |
| `LazyInitializationException` | Acceso a relación lazy fuera de la sesión. |
| `MultipleBagFetchException` | `JOIN FETCH` de varias `List` simultáneamente. |
| `TransientObjectException` / `TransientPropertyValueException` | Relacionar con una entidad no guardada aún. |
| `PersistentObjectException` (`detached entity passed to persist`) | `persist` sobre una entidad con ID ya existente. |
| `StaleObjectStateException` / `OptimisticLockException` / `ObjectOptimisticLockingFailureException` | Conflicto de `@Version`. |
| `PessimisticLockException` / `CannotAcquireLockException` | No se pudo obtener el bloqueo. |
| `QueryException` / `InvalidDataAccessApiUsageException` | JPQL mal escrito o uso incorrecto de la API. |
| `PropertyReferenceException` | El nombre del método de repositorio usa un atributo que no existe (`findByNombr`). |
| `SQLGrammarException` | SQL inválido (tabla/columna inexistente). |
| `CannotCreateTransactionException` | No se pudo abrir transacción/conexión a la BD. |
| `JpaSystemException` | Error genérico de JPA, revisar la causa. |
| `TransactionRequiredException` | Se necesita transacción (falta `@Transactional`, típico con `@Modifying`). |
| `UnexpectedRollbackException` | Transacción marcada para rollback pero se intentó commit. |
| `HikariPool ... Connection is not available` | Pool de conexiones agotado (conexiones sin liberar). |
| `StackOverflowError` | Bucle por `toString`/`equals`/JSON en relaciones bidireccionales. |

### Ejemplo: manejar errores de BD en un handler global
```java
@RestControllerAdvice
public class DbExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> duplicado(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiError(409, "Dato duplicado o inválido"));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> noExiste(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiError(404, ex.getMessage()));
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ApiError> conflicto(ObjectOptimisticLockingFailureException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiError(409, "El registro fue modificado por otro usuario"));
    }
}
```

## 19. Migraciones, pool y otras herramientas

| Término | Significado |
|---|---|
| **Flyway** | Herramienta de migraciones SQL versionadas (`V1__crear_tablas.sql`). |
| **Liquibase** | Migraciones en XML/YAML/SQL con control de cambios. |
| **Migración** | Script que evoluciona el esquema de la BD de forma controlada. |
| `db/migration` | Carpeta de scripts de Flyway (`src/main/resources/db/migration`). |
| `V1__nombre.sql` | Migración versionada (se ejecuta una sola vez, en orden). |
| `R__nombre.sql` | Migración repetible (se ejecuta si cambia su contenido). |
| `flyway_schema_history` | Tabla donde Flyway registra lo ejecutado. |
| `databasechangelog` | Tabla equivalente en Liquibase. |
| **HikariCP** | Pool de conexiones: reutiliza conexiones en vez de abrir una por consulta. |
| `maximum-pool-size` | Máximo de conexiones simultáneas. |
| `minimum-idle` | Conexiones mínimas inactivas. |
| `connection-timeout` | Tiempo máximo esperando una conexión. |
| `idle-timeout` / `max-lifetime` | Vida de conexiones inactivas / totales. |
| **MapStruct** | Generador de mappers Entity ↔ DTO. |
| **Querydsl** | Consultas tipadas con metamodelo `Q...`. |
| **Hibernate Envers** | Historial de versiones de entidades. |
| **Testcontainers** | Levanta una BD real (Docker) para pruebas. |
| **Spring Data REST** | Expone repositorios automáticamente como API REST. |
| **Spring Data JDBC** | Alternativa más simple a JPA (sin lazy ni caché). |
| **JdbcTemplate** | Ejecutar SQL directo con plantillas de Spring. |
| **JOOQ** | Generador de SQL tipado. |

### Ejemplo: Flyway
```properties
spring.jpa.hibernate.ddl-auto=validate      # Flyway crea las tablas, Hibernate solo valida
spring.flyway.enabled=true
```
```sql
-- src/main/resources/db/migration/V1__crear_usuarios.sql
CREATE TABLE usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(80) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    creado_en DATETIME
);

-- V2__agregar_estado.sql
ALTER TABLE usuarios ADD COLUMN estado VARCHAR(20) DEFAULT 'ACTIVO';
```

### Ejemplo: `JdbcTemplate`
```java
@Repository
@RequiredArgsConstructor
public class ReporteRepository {
    private final JdbcTemplate jdbc;

    public List<Map<String, Object>> ventasPorMes() {
        return jdbc.queryForList("SELECT MONTH(fecha) mes, SUM(total) total FROM pedidos GROUP BY 1");
    }

    public int contarActivos() {
        return jdbc.queryForObject("SELECT COUNT(*) FROM usuarios WHERE estado = ?", Integer.class, "ACTIVO");
    }
}
```

## 20. Testing de la capa de datos

| Término | Significado |
|---|---|
| `@DataJpaTest` | Test solo de la capa JPA: levanta repositorios + BD en memoria (H2) y hace rollback tras cada test. |
| `@AutoConfigureTestDatabase` | Controla si usar H2 o tu BD real (`replace = NONE`). |
| `TestEntityManager` | Versión de `EntityManager` para tests (`persist`, `flush`, `clear`, `find`). |
| `@Sql` | Ejecuta scripts SQL antes de un test. |
| `@Rollback` / `@Commit` | Control de rollback en tests. |
| `@SpringBootTest` | Contexto completo (integración). |
| `@Testcontainers` / `@Container` | BD real en contenedor Docker. |
| `@DynamicPropertySource` | Inyecta propiedades dinámicas (URL del contenedor). |
| `@ActiveProfiles("test")` | Usa `application-test.properties`. |

### Ejemplo: `@DataJpaTest`
```java
@DataJpaTest
class UsuarioRepositoryTest {

    @Autowired private UsuarioRepository repo;
    @Autowired private TestEntityManager em;

    @Test
    void findByEmail_devuelveUsuario() {
        Usuario u = new Usuario();
        u.setNombre("Ana");
        u.setEmail("ana@mail.com");
        em.persistAndFlush(u);

        Optional<Usuario> resultado = repo.findByEmail("ana@mail.com");

        assertTrue(resultado.isPresent());
        assertEquals("Ana", resultado.get().getNombre());
    }

    @Test
    void existsByEmail_falsoSiNoExiste() {
        assertFalse(repo.existsByEmail("nadie@mail.com"));
    }

    @Test
    void emailDuplicado_lanzaExcepcion() {
        repo.saveAndFlush(new Usuario("Ana", "ana@mail.com"));
        assertThrows(DataIntegrityViolationException.class,
            () -> repo.saveAndFlush(new Usuario("Otra", "ana@mail.com")));
    }

    @Test
    @Sql("/datos-prueba.sql")                     // carga datos desde src/test/resources
    void cuentaActivos() {
        assertEquals(3, repo.countByEstado(Estado.ACTIVO));
    }
}
```

### Ejemplo: Testcontainers con MySQL/PostgreSQL
```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class RepoConBdRealTest {

    @Container
    static PostgreSQLContainer<?> pg = new PostgreSQLContainer<>("postgres:16");

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        r.add("spring.datasource.url", pg::getJdbcUrl);
        r.add("spring.datasource.username", pg::getUsername);
        r.add("spring.datasource.password", pg::getPassword);
    }
    // tests...
}
```

## 21. Ejemplo completo

Estructura mínima de un CRUD con JPA: **Entity → Repository → Service → Controller**.

```java
// ---------- Entity ----------
@Entity
@Table(name = "productos")
@Getter @Setter @NoArgsConstructor
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)        // autoincremental
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(precision = 10, scale = 2)
    private BigDecimal precio;

    @Enumerated(EnumType.STRING)
    private Categoria categoria;

    @ManyToOne(fetch = FetchType.LAZY)                         // lazy siempre
    @JoinColumn(name = "proveedor_id")                         // FK
    private Proveedor proveedor;

    @CreationTimestamp @Column(updatable = false)
    private LocalDateTime creadoEn;
}

// ---------- Repository ----------
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByCategoria(Categoria categoria);                      // query method
    Page<Producto> findByNombreContainingIgnoreCase(String texto, Pageable p);

    @Query("SELECT p FROM Producto p JOIN FETCH p.proveedor WHERE p.precio < :max")
    List<Producto> baratosConProveedor(@Param("max") BigDecimal max);         // JPQL + fetch

    @Modifying
    @Transactional
    @Query("UPDATE Producto p SET p.precio = p.precio * :factor WHERE p.categoria = :cat")
    int ajustarPrecios(@Param("factor") BigDecimal factor, @Param("cat") Categoria cat);
}

// ---------- Service ----------
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductoService {

    private final ProductoRepository repo;

    public Page<Producto> listar(String texto, Pageable pageable) {
        return repo.findByNombreContainingIgnoreCase(texto, pageable);
    }

    public Producto buscar(Long id) {
        return repo.findById(id)
                   .orElseThrow(() -> new EntityNotFoundException("Producto " + id + " no existe"));
    }

    @Transactional
    public Producto crear(Producto p) {
        return repo.save(p);                        // INSERT
    }

    @Transactional
    public Producto actualizar(Long id, Producto datos) {
        Producto p = buscar(id);                    // queda gestionado (managed)
        p.setNombre(datos.getNombre());
        p.setPrecio(datos.getPrecio());
        return p;                                   // UPDATE automático al commit (dirty checking)
    }

    @Transactional
    public void eliminar(Long id) {
        repo.deleteById(id);                        // DELETE
    }
}

// ---------- Controller ----------
@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService service;

    @GetMapping
    public Page<Producto> listar(@RequestParam(defaultValue = "") String q, Pageable pageable) {
        return service.listar(q, pageable);
    }

    @GetMapping("/{id}")
    public Producto buscar(@PathVariable Long id) { return service.buscar(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Producto crear(@RequestBody Producto p) { return service.crear(p); }
}
```

---

## Tabla rápida: nombre del método → SQL

| Método | SQL generado |
|---|---|
| `findByEmail(e)` | `SELECT ... WHERE email = ?` |
| `findByNombreContaining(t)` | `WHERE nombre LIKE '%t%'` |
| `findByEdadBetween(a,b)` | `WHERE edad BETWEEN a AND b` |
| `findByEstadoIn(lista)` | `WHERE estado IN (...)` |
| `findTop3ByOrderByPrecioDesc()` | `ORDER BY precio DESC LIMIT 3` |
| `existsByEmail(e)` | `SELECT 1 ... WHERE email = ? LIMIT 1` |
| `countByEstado(e)` | `SELECT COUNT(*) WHERE estado = ?` |
| `deleteByEstado(e)` | `DELETE ... WHERE estado = ?` |

## Glosario de errores de "diseño" frecuentes
| Problema | Consecuencia | Solución |
|---|---|---|
| `EAGER` en todas las relaciones | Consultas enormes y lentas | `LAZY` + `JOIN FETCH`/`@EntityGraph` |
| Devolver entidades en el Controller | Bucles JSON, `LazyInitializationException`, exposición de datos | Usar DTOs |
| `@Data` en entidades | `toString`/`hashCode` recursivos | `@Getter`/`@Setter` + `equals/hashCode` por ID |
| `ddl-auto=create` en producción | **Pierde todos los datos** | `validate` + Flyway/Liquibase |
| `ddl-auto=update` en producción | Cambios de esquema impredecibles | Migraciones controladas |
| `@Transactional` en método privado o llamada interna | No hace nada | Método público, desde otro bean |
| Falta de índices en columnas de búsqueda | Consultas lentas | `@Index` / índice en BD |
| `List` en `@ManyToMany` | Borra y reinserta toda la tabla intermedia | Usar `Set` |
| `double` para dinero | Errores de redondeo | `BigDecimal` |
| `EnumType.ORDINAL` | Rompe datos si cambia el orden del enum | `EnumType.STRING` |
| No sincronizar ambos lados de una relación bidireccional | Datos inconsistentes | Métodos `agregarX()` / `quitarX()` |

---

> **Tip de lectura:** ante una entidad desconocida, mira primero `@Table`, el `@Id`, y las anotaciones de relación (`@ManyToOne`, `mappedBy`, `cascade`, `fetch`); te dicen cómo se conecta con las demás tablas. Ante un repositorio, lee el **nombre del método** (`findBy...`) o el `@Query` para saber qué consulta ejecuta.

