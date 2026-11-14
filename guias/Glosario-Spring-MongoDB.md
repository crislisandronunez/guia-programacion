# Glosario de Spring Data MongoDB (NoSQL Documental en Spring Boot)

Este glosario reúne todos los conceptos, anotaciones, repositorios, consultas (`@Query`, `Criteria`, `MongoTemplate`), agregaciones, índices, relaciones y configuraciones que encontrarás al leer o desarrollar aplicaciones con **Spring Data MongoDB** y **Spring Boot**.

---

## Índice
1. [Conceptos Fundamentales de MongoDB (SQL vs MongoDB)](#1-conceptos-fundamentales-de-mongodb-sql-vs-mongodb)
2. [Dependencias y Configuración (`application.yml`)](#2-dependencias-y-configuración-applicationyml)
3. [Anotaciones de Mapeo de Documentos](#3-anotaciones-de-mapeo-de-documentos)
4. [El Identificador `@Id` y `ObjectId`](#4-el-identificador-id-y-objectid)
5. [Relaciones: Documentos Embebidos vs Referencias (`@DocumentReference`)](#5-relaciones-documentos-embebidos-vs-referencias-documentreference)
6. [Índices: `@Indexed`, `@CompoundIndex`, `@TextIndexed` y TTL](#6-índices-indexed-compoundindex-textindexed-y-ttl)
7. [`MongoRepository` y Query Methods Derivados](#7-mongorepository-y-query-methods-derivados)
8. [Consultas JSON Nativas con `@Query`](#8-consultas-json-nativas-con-query)
9. [Operadores de Consulta de MongoDB (`$gt`, `$in`, `$regex`, `$elemMatch`)](#9-operadores-de-consulta-de-mongodb-gt-in-regex-elemmatch)
10. [`MongoTemplate`, `Query` y `Criteria` (Consultas Dinámicas)](#10-mongotemplate-query-y-criteria-consultas-dinámicas)
11. [Actualizaciones Parciales (`Update`, `findAndModify`, `@Update`)](#11-actualizaciones-parciales-update-findandmodify-update)
12. [Pipeline de Agregación (`@Aggregation` y `Aggregation`)](#12-pipeline-de-agregación-aggregation-y-aggregation)
13. [Proyecciones (Interfaces, DTOs y Exclusión de Campos)](#13-proyecciones-interfaces-dtos-y-exclusión-de-campos)
14. [Auditoría y Ciclo de Vida (`@EnableMongoAuditing`)](#14-auditoría-y-ciclo-de-vida-enablemongoauditing)
15. [Transacciones Multi-Documento y Bloqueo Optimista (`@Version`)](#15-transacciones-multi-documento-y-bloqueo-optimista-version)
16. [Consultas Geoespaciales (`GeoJsonPoint`, `@GeoSpatialIndexed`)](#16-consultas-geoespaciales-geojsonpoint-geospatialindexed)
17. [GridFS (Almacenamiento de Archivos Grandes)](#17-gridfs-almacenamiento-de-archivos-grandes)
18. [Conversores Personalizados (`Converter`) y `_class`](#18-conversores-personalizados-converter-y-_class)
19. [Testing con MongoDB (`@DataMongoTest` y Testcontainers)](#19-testing-con-mongodb-datamongotest-y-testcontainers)
20. [Buenas Prácticas y Errores Comunes](#20-buenas-prácticas-y-errores-comunes)

---

## 1. Conceptos Fundamentales de MongoDB (SQL vs MongoDB)

MongoDB es una base de datos **NoSQL orientada a documentos**. En lugar de guardar filas en tablas rígidas, almacena documentos flexibles en formato **BSON** (Binary JSON).

| Término en MongoDB | Equivalente en SQL / JPA | Significado | Ejemplo en Spring Data MongoDB |
|---|---|---|---|
| **Database (Base de datos)** | Database / Schema | Contenedor lógico de colecciones. | `spring.data.mongodb.database=tienda_db` |
| **Collection (Colección)** | Table (Tabla) | Agrupación de documentos BSON (sin esquema fijo obligatorio). | `@Document(collection = "productos")` |
| **Document (Documento)** | Row / Record (Fila) | Registro individual en formato JSON/BSON compuesto por pares clave-valor. | Una instancia de `Producto.java` serializada a BSON. |
| **Field (Campo)** | Column (Columna) | Propiedad dentro de un documento (puede ser primitivo, objeto o array). | `@Field("precio_unitario") private BigDecimal precio;` |
| **`_id` / `ObjectId`** | Primary Key (`PK`) | Identificador único obligatorio de cada documento en la colección (12 bytes autogenerados). | `@Id private String id;` |
| **Embedded Document (Subdocumento)** | `JOIN` / Tabla hija | Objeto anidado directamente dentro del mismo documento padre. | `private Direccion direccionEnvio;` dentro de `Pedido`. |
| **Document Reference** | Foreign Key (`FK`) | Guardar solo el `_id` de otro documento que vive en otra colección. | `@DocumentReference private Cliente cliente;` |
| **BSON (Binary JSON)** | Formato binario de fila | Representación binaria de JSON que soporta tipos adicionales como `ObjectId`, `Date` y `Decimal128`. | Manejo interno del driver de MongoDB. |
| **Replica Set** | Clúster Primaria-Réplica | Conjunto de nodos MongoDB que mantienen los mismos datos para alta disponibilidad y soporte de transacciones. | `mongodb://nodo1:27017,nodo2:27017/db?replicaSet=rs0` |
| **Sharding** | Particionamiento horizontal | Distribuir datos de una colección gigante entre múltiples servidores usando una *Shard Key*. | Escalamiento horizontal de colecciones de millones de documentos. |

**Ejemplo de cómo se ve una clase Java convertida a Documento BSON en MongoDB:**
```json
{
  "_id": ObjectId("654a1f9e8c3b2a1d4e5f6a7b"),
  "nombre": "Notebook Pro 16",
  "precio": NumberDecimal("1850.50"),
  "etiquetas": ["tecnologia", "computacion", "oferta"],
  "especificaciones": {
    "ramGb": 32,
    "almacenamiento": "1TB NVMe"
  }
}
```

---

## 2. Dependencias y Configuración (`application.yml`)

| Elemento | Significado | Ejemplo |
|---|---|---|
| **`spring-boot-starter-data-mongodb`** | Starter principal que incluye el driver síncrono de MongoDB y Spring Data MongoDB. | `<artifactId>spring-boot-starter-data-mongodb</artifactId>` |
| **`spring-boot-starter-data-mongodb-reactive`** | Starter para programación reactiva no bloqueante con Project Reactor (`Mono` / `Flux`). | `ReactiveMongoRepository<Producto, String>` |
| **`spring.data.mongodb.uri`** | Cadena de conexión completa (incluye usuario, password, host, puerto, base de datos y opciones). | `mongodb://admin:secret@localhost:27017/tienda_db?authSource=admin` |
| **`auto-index-creation`** | Por defecto en Spring Boot 3 es `false`. Si se pone en `true`, Spring crea automáticamente en Mongo los índices anotados con `@Indexed` al arrancar. | `spring.data.mongodb.auto-index-creation: true` |
| **`uuid-representation`** | Define cómo se serializan los `UUID` en BSON (`standard` es el recomendado hoy en día). | `spring.data.mongodb.uuid-representation: standard` |

**Ejemplo de configuración en `pom.xml` y `application.yml`:**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>
```

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://root:password123@localhost:27017/ecommerce_db?authSource=admin
      auto-index-creation: true
      uuid-representation: standard

logging:
  level:
    # Muestra en consola todas las queries JSON que Spring envía a MongoDB (equivale a show-sql en JPA)
    org.springframework.data.mongodb.core.MongoTemplate: DEBUG
```

---

## 3. Anotaciones de Mapeo de Documentos

En Spring Data MongoDB **no se usa `@Entity`, `@Table` ni `@Column`** (esas son de JPA). En su lugar se usan las anotaciones del paquete `org.springframework.data.mongodb.core.mapping.*`:

| Anotación | Dónde va | Significado | Ejemplo |
|---|---|---|---|
| **`@Document`** | Clase | Marca la clase como un documento raíz que se guarda en una colección de MongoDB. | `@Document(collection = "clientes")` |
| **`@Id`** | Atributo | Marca la clave primaria que mapea al campo `_id` de MongoDB. | `@Id private String id;` |
| **`@Field`** | Atributo | Personaliza el nombre del campo en el JSON de Mongo o su tipo BSON (`targetType`). | `@Field(name = "fecha_alta", targetType = FieldType.DATE_TIME)` |
| **`@Transient`** | Atributo | Ignora el atributo: no se guarda ni se lee desde MongoDB (`org.springframework.data.annotation.Transient`). | `@Transient private BigDecimal precioConDescuentoTemporal;` |
| **`@PersistenceCreator`** | Constructor | Indica a Spring qué constructor usar al reconstruir el objeto desde la base de datos (útil si tienes varios constructores). | `@PersistenceCreator public Producto(String id, String nombre)` |
| **`@Value`** | Parámetro de constructor | Permite inyectar valores de expresiones SpEL o propiedades del documento al construir el objeto. | `@Value("#root.nombre ?: 'Sin Nombre'") String nombre` |
| **`@ReadOnlyProperty`** | Atributo | El campo se lee desde MongoDB pero nunca se incluye en operaciones de escritura (`insert` / `update`). | `@ReadOnlyProperty private String calculadoPorTrigger;` |

**Ejemplo completo de un Documento mapeado:**
```java
package com.ejemplo.tienda.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Document(collection = "productos")
public class Producto {

    @Id
    private String id; // Se mapea automáticamente a "_id": ObjectId(...)

    @Field("nombre_producto")
    private String nombre;

    // IMPORTANTE: Para dinero, guardar como DECIMAL128 permite ordenar y sumar números en Mongo
    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal precio;

    private Integer stock;

    private List<String> categorias; // Se guarda como un array JSON nativo: ["audio", "gamer"]

    private EspecificacionTecnica especificacion; // Subdocumento embebido (no necesita @Document)

    @Transient
    private boolean seleccionadoEnVista; // No se guarda en MongoDB

    private Instant fechaCreacion = Instant.now();

    // Getters, Setters y Constructores...
}
```

---

## 4. El Identificador `@Id` y `ObjectId`

En MongoDB todo documento tiene obligatoriamente un campo llamado `_id`. Spring Data maneja la conversión de este campo según el tipo de dato que elijas en Java:

| Tipo en Java para `@Id` | Cómo se guarda en MongoDB | Ventajas y Cuándo usarlo | Ejemplo |
|---|---|---|---|
| **`String`** | `ObjectId("654a1f...")` | **El más usado y recomendado.** Si el `String` es `null` al guardar, Mongo genera un `ObjectId` automático y Spring lo convierte a texto hexadecimal de 24 caracteres. | `@Id private String id;` |
| **`ObjectId`** (`org.bson.types.ObjectId`) | `ObjectId("654a1f...")` | Da acceso directo al timestamp de creación embebido en el ID (`id.getDate()`), pero acopla tu clase al driver BSON. | `@Id private ObjectId id;` |
| **`UUID` / `Long` / Clave de negocio** | `"SKU-9981"` o `UUID` | Debes asignarle el valor tú mismo manualmente **antes** de llamar a `save()`. Mongo no autogenera `Long` autoincrementales. | `@Id private String codigoIso;` |
| **`@MongoId(FieldType.OBJECT_ID)`** | Fuerza el tipo exacto | Permite controlar explícitamente cómo se convierte el `@Id` entre Java y BSON. | `@MongoId(FieldType.STRING) private String id;` |

**Ejemplo de comportamiento del `@Id`:**
```java
Producto nuevo = new Producto();
nuevo.setNombre("Teclado Mecánico");
// nuevo.getId() es null antes de guardar

Producto guardado = productoRepository.save(nuevo);
System.out.println(guardado.getId()); // Imprime ej: "654a2b8c9d1e2f3a4b5c6d7e" (24 caracteres hex)
```

---

## 5. Relaciones: Documentos Embebidos vs Referencias (`@DocumentReference`)

A diferencia de SQL donde siempre normalizamos en múltiples tablas con claves foráneas, en MongoDB la regla de oro es: **"Lo que se consulta junto, se guarda junto (Embebido)"**.

| Enfoque | Cómo funciona | Cuándo usarlo | Ejemplo |
|---|---|---|---|
| **Documento Embebido (Anidado)** | El objeto hijo o lista de objetos se guarda **adentro** del mismo JSON del padre. Se recupera en 1 sola lectura sin `JOIN`. | Relaciones **1 a 1** o **1 a Pocos** (*One-to-Few*) donde los hijos no existen sin el padre (ej: `Pedido` y sus `ItemsPedido`, `Usuario` y sus `Direcciones`). | `private List<ItemPedido> items;` |
| **`@DocumentReference`** *(Recomendado desde Spring Data 3+)* | Guarda en el documento padre únicamente el `_id` (o campo personalizado) del documento hijo que vive en otra colección. | Relaciones **1 a Muchos (miles)** o **Muchos a Muchos** donde la entidad referenciada cambia sola o se comparte (ej: `Editorial` de miles de `Libros`). | `@DocumentReference private Autor autor;` |
| **`@DBRef`** *(Legado)* | Formato antiguo de referencia de MongoDB (`{ "$ref": "autores", "$id": ObjectId(...) }`). Hoy se prefiere `@DocumentReference`. | Proyectos antiguos por compatibilidad retroactiva. | `@DBRef(lazy = true) private Categoria categoria;` |
| **Referencia Manual por ID** | Guardar directamente el `String clienteId` o `List<String> productosIds` sin anotaciones mágicas. | Microservicios o alta escala donde quieres control total de cuándo hacer la segunda consulta. | `private String clienteId;` |

**Ejemplo comparativo en código (Embebido vs `@DocumentReference`):**
```java
@Document(collection = "pedidos")
public class Pedido {

    @Id
    private String id;

    // 1. EMBEBIDO: Dirección e Ítems se guardan ADENTRO del mismo documento Pedido.
    // Leer el pedido trae sus ítems y dirección en 1 sola operación de disco ultra rápida.
    private DireccionEntrega direccion;
    private List<ItemPedido> items;

    // 2. REFERENCIA (@DocumentReference): El Cliente vive en su propia colección "clientes".
    // En Mongo solo se guarda el _id del cliente, pero Spring lo resuelve al leer.
    @DocumentReference(lazy = true)
    private Cliente cliente;
}

// Clase simple (POJO): NO lleva @Document ni @Id porque vive embebida dentro de Pedido
public class ItemPedido {
    private String productoId;
    private String nombreProducto; // En MongoDB es común desnormalizar (duplicar) el nombre para no hacer JOIN
    private int cantidad;
    private BigDecimal precioUnitario;
}
```

---

## 6. Índices: `@Indexed`, `@CompoundIndex`, `@TextIndexed` y TTL

Sin índices, MongoDB debe hacer un `COLLSCAN` (escanear todos los documentos de la colección uno por uno).

| Anotación | Significado | Ejemplo |
|---|---|---|
| **`@Indexed`** | Crea un índice sobre un campo individual. Permite definir `unique = true` o dirección (`ASCENDING` / `DESCENDING`). | `@Indexed(unique = true) private String email;` |
| **`@Indexed(expireAfter = ...)`** | **Índice TTL (*Time-To-Live*)**: MongoDB borra automáticamente el documento de la base de datos cuando pasa ese tiempo desde la fecha del campo. | `@Indexed(expireAfter = "24h") private Instant fechaCreacion;` |
| **`@CompoundIndex`** | Índice compuesto sobre múltiples campos a nivel de clase. Respeta la regla **ESR** (*Equality, Sort, Range*). | `@CompoundIndex(name = "idx_cat_precio", def = "{'categoria': 1, 'precio': -1}")` |
| **`@TextIndexed`** | Incluye el campo en el índice de búsqueda de texto completo (*Full-Text Search*) del documento. | `@TextIndexed(weight = 2) private String titulo;` |
| **`@GeoSpatialIndexed`** | Crea un índice espacial (`2dsphere`) para búsquedas por coordenadas geográficas y cercanía. | `@GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)` |

**Ejemplo de uso de Índices en una entidad:**
```java
@Document(collection = "sesiones_usuario")
@CompoundIndex(name = "idx_usuario_activo", def = "{'usuarioId': 1, 'activa': 1}")
public class SesionUsuario {

    @Id
    private String id;

    @Indexed(unique = true)
    private String tokenRefresh;

    private String usuarioId;
    private boolean activa;

    // Índice TTL: MongoDB eliminará sola esta sesión exactamente 7 días después de 'creadaEn'
    @Indexed(expireAfter = "7d")
    private Instant creadaEn = Instant.now();
}
```

---

## 7. `MongoRepository` y Query Methods Derivados

Al igual que en Spring Data JPA, puedes crear interfaces que extiendan `MongoRepository<Entidad, TipoId>` y Spring generará las consultas automáticamente a partir del nombre del método:

| Palabra Clave en el Método | Consulta equivalente en MongoDB | Ejemplo de Método en el Repositorio |
|---|---|---|
| **`findBy...And...`** | `{ "campo1": ?0, "campo2": ?1 }` | `List<Producto> findByCategoriaAndActivoTrue(String cat);` |
| **`GreaterThan` / `LessThan`** | `{ "precio": { "$gt": ?0 } }` | `List<Producto> findByPrecioGreaterThan(BigDecimal min);` |
| **`Between`** | `{ "precio": { "$gt": ?0, "$lt": ?1 } }` | `List<Producto> findByPrecioBetween(BigDecimal min, BigDecimal max);` |
| **`Containing` / `Like` / `Regex`** | `{ "nombre": { "$regex": ?0 } }` | `List<Producto> findByNombreContainingIgnoreCase(String texto);` |
| **`In` / `NotIn`** | `{ "estado": { "$in": [ ... ] } }` | `List<Pedido> findByEstadoIn(Collection<String> estados);` |
| **`Exists`** | `{ "campo": { "$exists": true } }` | `List<Usuario> findByTelefonoExists(boolean existe);` |
| **Navegación de Subdocumentos (`_`)** | `{ "direccion.ciudad": ?0 }` | `List<Cliente> findByDireccionCiudad(String ciudad);` |
| **Búsqueda dentro de Arrays** | `{ "etiquetas": ?0 }` | `List<Producto> findByEtiquetasContaining(String etiqueta);` |
| **`existsBy...` / `countBy...` / `deleteBy...`** | Verifica existencia, cuenta o elimina | `boolean existsByEmail(String email);` |

**Ejemplo completo de `MongoRepository`:**
```java
public interface ProductoRepository extends MongoRepository<Producto, String> {

    // Busca por campo exacto dentro del subdocumento embebido 'especificacion.ramGb'
    List<Producto> findByEspecificacionRamGbGreaterThanEqual(int ramMinima);

    // Busca si el array 'categorias' contiene el valor pasado, con paginación
    Page<Producto> findByCategoriasContaining(String categoria, Pageable pageable);

    // Búsqueda de texto ignorando mayúsculas/minúsculas ordenada por precio descendente
    List<Producto> findByNombreContainingIgnoreCaseOrderByPrecioDesc(String palabra);

    boolean existsByNombreIgnoreCase(String nombre);
}
```

---

## 8. Consultas JSON Nativas con `@Query`

Cuando el nombre del método derivado queda muy largo, usas `@Query` escribiendo directamente el filtro JSON de MongoDB donde `?0`, `?1` (o `:?#{...}`) representan los parámetros del método:

| Atributo de `@Query` | Para qué sirve | Ejemplo |
|---|---|---|
| **`value`** | Define el filtro JSON de búsqueda (equivale al primer parámetro de `db.coleccion.find(filtro)`). | `@Query("{ 'categoria': ?0, 'stock': { $gt: 0 } }")` |
| **`fields`** | Define qué campos traer (`1`) o excluir (`0`) para optimizar red y memoria (Proyección). | `@Query(value = "{ 'activo': true }", fields = "{ 'nombre': 1, 'precio': 1 }")` |
| **`sort`** | Define el ordenamiento JSON por defecto (`1` ascendente, `-1` descendente). | `@Query(value = "{ 'categoria': ?0 }", sort = "{ 'precio': -1 }")` |
| **`count = true`** | Hace que el método devuelva únicamente la cantidad (`long`) de documentos que coinciden. | `@Query(value = "{ 'stock': 0 }", count = true) long contarAgotados();` |
| **`exists = true`** | Devuelve `boolean` indicando si existe al menos un documento que cumpla el filtro. | `@Query(value = "{ 'sku': ?0 }", exists = true) boolean existeSku(String sku);` |
| **`delete = true`** | Elimina directamente los documentos que coincidan con el filtro JSON. | `@Query(value = "{ 'activo': false }", delete = true) long borrarInactivos();` |

**Ejemplo en código:**
```java
public interface ProductoRepository extends MongoRepository<Producto, String> {

    // ?0 es el primer parámetro (categoria), ?1 es el segundo (precioMaximo)
    @Query("{ 'categorias': ?0, 'precio': { $lte: ?1 }, 'stock': { $gt: 0 } }")
    List<Producto> buscarDisponiblesPorCategoriaYPrecioMaximo(String categoria, BigDecimal precioMaximo);

    // Trae solo nombre y precio (excluye especificaciones pesadas) ordenado por nombre A-Z
    @Query(value = "{ 'activo': true }", fields = "{ 'nombre': 1, 'precio': 1 }", sort = "{ 'nombre': 1 }")
    List<Producto> listarCatalogoLiviano();
}
```

---

## 9. Operadores de Consulta de MongoDB (`$gt`, `$in`, `$regex`, `$elemMatch`)

Al leer `@Query` o construir consultas con `Criteria`, verás constantemente estos operadores nativos de MongoDB:

| Operador Mongo | Método en `Criteria` (Java) | Significado | Ejemplo en JSON `@Query` |
|---|---|---|---|
| **`$eq` / `$ne`** | `.is(val)` / `.ne(val)` | Igual a / Distinto de. | `{ 'estado': { $ne: 'CANCELADO' } }` |
| **`$gt` / `$gte`** | `.gt(val)` / `.gte(val)` | Mayor que (`>`) / Mayor o igual que (`>=`). | `{ 'edad': { $gte: 18 } }` |
| **`$lt` / `$lte`** | `.lt(val)` / `.lte(val)` | Menor que (`<`) / Menor o igual que (`<=`). | `{ 'precio': { $lte: 500 } }` |
| **`$in` / `$nin`** | `.in(...)` / `.nin(...)` | El valor está (o no está) dentro de una lista. | `{ 'rol': { $in: ['ADMIN', 'EDITOR'] } }` |
| **`$and` / `$or`** | `.andOperator(...)` / `.orOperator(...)` | Combina múltiples condiciones con Y / O lógico. | `{ $or: [ { 'stock': 0 }, { 'activo': false } ] }` |
| **`$exists`** | `.exists(boolean)` | Verifica si el campo existe o no en el documento BSON. | `{ 'fechaBaja': { $exists: false } }` |
| **`$regex`** | `.regex(patron, "i")` | Expresión regular (`"i"` ignora mayúsculas/minúsculas). | `{ 'email': { $regex: '@gmail\\.com$', $options: 'i' } }` |
| **`$all`** | `.all(lista)` | El campo array debe contener **todos** los elementos indicados. | `{ 'tags': { $all: ['java', 'spring'] } }` |
| **`$size`** | `.size(n)` | El campo array tiene exactamente `n` elementos. | `{ 'items': { $size: 3 } }` |
| **`$elemMatch`** | `.elemMatch(criteria)` | Un **mismo elemento** dentro de un array de objetos debe cumplir todas las condiciones a la vez. | `{ 'items': { $elemMatch: { 'productoId': ?0, 'cantidad': { $gte: 5 } } } }` |

**Ejemplo de por qué `$elemMatch` es crucial al buscar en listas de subdocumentos:**
```java
// Queremos pedidos que tengan un ítem del producto "P1" con cantidad >= 10 EN EL MISMO ÍTEM:
@Query("{ 'items': { $elemMatch: { 'productoId': ?0, 'cantidad': { $gte: ?1 } } } }")
List<Pedido> buscarPedidosMayoristasDeProducto(String productoId, int cantidadMinima);
```

---

## 10. `MongoTemplate`, `Query` y `Criteria` (Consultas Dinámicas)

Cuando tienes filtros opcionales (ej. un buscador con 6 filtros donde el usuario puede completar solo 2), `MongoRepository` se queda corto y se utiliza **`MongoTemplate`** junto con **`Query`** y **`Criteria`** (el equivalente en MongoDB a `CriteriaBuilder` / `Specification` de JPA):

| Clase / Método | Propósito | Ejemplo |
|---|---|---|
| **`MongoTemplate`** | Bean central de Spring para ejecutar cualquier operación contra MongoDB (`find`, `findOne`, `upsert`, `updateMulti`, `aggregate`). | `mongoTemplate.find(query, Producto.class);` |
| **`Criteria`** | Constructor fluido de condiciones de filtrado (`where("campo").is(...)`). | `Criteria.where("precio").gte(100).lte(500)` |
| **`Query`** | Envuelve los `Criteria` y permite añadir paginación (`.with(pageable)`), ordenamiento (`.with(sort)`), límites (`.limit(10)`) y proyecciones (`.fields()`). | `Query query = new Query(criteria).with( pageable );` |
| **`MongoPageableExecutionUtils`** | Utilidad para armar un objeto `Page<T>` combinando la lista de resultados y el `count` de la consulta dinámica. | `PageableExecutionUtils.getPage(lista, pageable, () -> mongoTemplate.count(...))` |

**Ejemplo de buscador dinámico con `MongoTemplate` y `Criteria`:**
```java
@Service
public class ProductoBusquedaDinamicaService {

    private final MongoTemplate mongoTemplate;

    public ProductoBusquedaDinamicaService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public Page<Producto> buscarConFiltros(String nombre,
                                           String categoria,
                                           BigDecimal precioMin,
                                           BigDecimal precioMax,
                                           Pageable pageable) {
        List<Criteria> condiciones = new ArrayList<>();

        if (nombre != null && !nombre.isBlank()) {
            // Pattern.quote evita inyección de expresiones regulares
            condiciones.add(Criteria.where("nombre").regex(Pattern.quote(nombre), "i"));
        }
        if (categoria != null && !categoria.isBlank()) {
            condiciones.add(Criteria.where("categorias").is(categoria));
        }
        if (precioMin != null && precioMax != null) {
            condiciones.add(Criteria.where("precio").gte(precioMin).lte(precioMax));
        } else if (precioMin != null) {
            condiciones.add(Criteria.where("precio").gte(precioMin));
        } else if (precioMax != null) {
            condiciones.add(Criteria.where("precio").lte(precioMax));
        }

        Query query = new Query();
        if (!condiciones.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(condiciones.toArray(new Criteria[0])));
        }

        // Clonar la query antes de aplicar paginación para poder contar el total real
        Query countQuery = Query.of(query).limit(-1).skip(-1);

        query.with(pageable);
        List<Producto> productos = mongoTemplate.find(query, Producto.class);

        return PageableExecutionUtils.getPage(
                productos,
                pageable,
                () -> mongoTemplate.count(countQuery, Producto.class)
        );
    }
}
```

---

## 11. Actualizaciones Parciales (`Update`, `findAndModify`, `@Update`)

Si usas `repository.save(entidad)`, MongoDB **reemplaza todo el documento completo**. Para modificar solo un campo, sumar un contador atómicamente o agregar un elemento a un array sin traer ni pisar todo el documento, se usan operaciones de actualización parcial:

| Operador Mongo / Método `Update` | Significado | Ejemplo con `Update` de Spring |
|---|---|---|
| **`$set` (`.set(k, v)`)** | Cambia el valor de un campo específico sin tocar el resto del documento. | ` new Update().set("estado", "ENVIADO")` |
| **`$unset` (`.unset(k)`)** | Elimina por completo el campo del documento BSON. | `new Update().unset("tokenTemporal")` |
| **`$inc` (`.inc(k, n)`)** | Incrementa o decrementa atómicamente un número en la base de datos (ideal para stock o contadores de visitas). | `new Update().inc("stock", -1)` |
| **`$push` (`.push(k, v)`)** | Agrega un elemento al final de un array dentro del documento. | `new Update().push("historialEstados", nuevoEstado)` |
| **`$addToSet` (`.addToSet(k, v)`)** | Agrega un elemento al array **solo si no existe ya** (evita duplicados). | `new Update().addToSet("categorias", "oferta")` |
| **`$pull` (`.pull(k, v)`)** | Elimina de un array todos los elementos que coincidan con el valor dado. | `new Update().pull("categorias", "temporal")` |
| **`updateFirst` / `updateMulti`** | Actualiza el primer documento que coincida o todos los que coincidan. | `mongoTemplate.updateMulti(query, update, Producto.class);` |
| **`upsert`** | Si el documento existe lo actualiza; si no existe, lo inserta. | `mongoTemplate.upsert(query, update, Estadistica.class);` |
| **`findAndModify`** | Busca y actualiza el documento de forma **100% atómica** devolviendo el documento (antes o después del cambio). | `mongoTemplate.findAndModify(query, update, options().returnNew(true), Producto.class);` |

**Ejemplo 1: Con la anotación `@Update` directamente en `MongoRepository` (Spring Data MongoDB 3.4+):**
```java
public interface ProductoRepository extends MongoRepository<Producto, String> {

    @Query("{ '_id': ?0, 'stock': { $gte: ?1 } }")
    @Update("{ '$inc': { 'stock': -?1 }, '$set': { 'ultimaVenta': ?2 } }")
    long descontarStockSiAlcanza(String productoId, int cantidad, Instant fechaVenta);
}
```

**Ejemplo 2: Descuento atómico con `findAndModify` en `MongoTemplate`:**
```java
public Optional<Producto> descontarStockAtomico(String productoId, int cantidad) {
    // Solo actualiza si el producto existe Y su stock actual es >= cantidad
    Query query = new Query(Criteria.where("_id").is(productoId).and("stock").gte(cantidad));
    Update update = new Update().inc("stock", -cantidad);

    FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true); // Devuelve el stock ya restado
    Producto actualizado = mongoTemplate.findAndModify(query, update, options, Producto.class);
    return Optional.ofNullable(actualizado);
}
```

---

## 12. Pipeline de Agregación (`@Aggregation` y `Aggregation`)

El **Aggregation Framework** de MongoDB es el equivalente a `GROUP BY`, `SUM`, `AVG`, `JOIN` y subconsultas de SQL. Funciona como una tubería (*pipeline*) donde los documentos pasan por etapas (*stages*) sucesivas:

| Etapa (*Stage*) en Mongo | Método en `Aggregation` (Java) | Equivalente en SQL | Propósito |
|---|---|---|---|
| **`$match`** | `Aggregation.match(criteria)` | `WHERE` / `HAVING` | Filtra documentos (siempre debe ir primero para aprovechar los índices). |
| **`$group`** | `Aggregation.group("campo").sum("x").as("total")` | `GROUP BY` + `SUM / COUNT / AVG` | Agrupa documentos y calcula totales, promedios, mínimos o máximos. |
| **`$project`** | `Aggregation.project("a", "b")` | `SELECT col1, col2 AS alias` | Selecciona, renombra o calcula nuevos campos para la siguiente etapa. |
| **`$sort`** | `Aggregation.sort(Sort.Direction.DESC, "total")` | `ORDER BY` | Ordena los resultados. |
| **`$skip` / `$limit`** | `Aggregation.skip(n)` / `limit(m)` | `OFFSET` / `LIMIT` | Paginación de los resultados de la agregación. |
| **`$unwind`** | `Aggregation.unwind("items")` | Desanidar array | Descompone un array produciendo un documento separado por cada elemento del array. |
| **`$lookup`** | `Aggregation.lookup("otraCol", "local", "ext", "alias")` | `LEFT OUTER JOIN` | Une datos de otra colección dentro de la misma base de datos. |

**Ejemplo 1: Usando `@Aggregation` declarativa en el repositorio:**
```java
public record ReporteCategoriaDto(String id, long cantidadProductos, BigDecimal precioPromedio) {}

public interface ProductoRepository extends MongoRepository<Producto, String> {

    @Aggregation(pipeline = {
        "{ '$match': { 'stock': { $gt: 0 } } }",
        "{ '$unwind': '$categorias' }",
        "{ '$group': { '_id': '$categorias', 'cantidadProductos': { '$sum': 1 }, 'precioPromedio': { '$avg': '$precio' } } }",
        "{ '$sort': { 'cantidadProductos': -1 } }"
    })
    List<ReporteCategoriaDto> obtenerEstadisticasPorCategoria();
}
```

**Ejemplo 2: Usando el builder tipado de `MongoTemplate`:**
```java
public List<ReporteCategoriaDto> calcularReporteConTemplate() {
    Aggregation agg = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("stock").gt(0)),
            Aggregation.unwind("categorias"),
            Aggregation.group("categorias")
                    .count().as("cantidadProductos")
                    .avg("precio").as("precioPromedio"),
            Aggregation.sort(Sort.Direction.DESC, "cantidadProductos")
    );

    return mongoTemplate.aggregate(agg, "productos", ReporteCategoriaDto.class)
            .getMappedResults();
}
```

---

## 13. Proyecciones (Interfaces, DTOs y Exclusión de Campos)

Si un documento de MongoDB tiene 50 campos y varios arrays anidados, traer todo a memoria cuando solo necesitas mostrar un listado de `id`, `nombre` y `precio` desperdicia red y RAM.

| Tipo de Proyección | Cómo se define | Ejemplo |
|---|---|---|
| **Proyección por Record / DTO (Cerrada)** | Creas un `record` cuyos parámetros coinciden con los campos del documento. Spring genera automáticamente la proyección en MongoDB para traer **solo esos campos**. | `public record ProductoResumenDto(String id, String nombre, BigDecimal precio) {}` |
| **Proyección por Interfaz** | Creas una interfaz con getters (`getNombre()`, `getPrecio()`). Permite usar `@Value` con SpEL para campos calculados. | `public interface ProductoResumenView { String getNombre(); }` |
| **Proyección Dinámica (`<T>`)** | Un único método en el repositorio que recibe `Class<T> type` y devuelve la vista que el llamador elija. | `<T> List<T> findByCategoriasContaining(String cat, Class<T> type);` |

**Ejemplo práctico con Java `record`:**
```java
// 1. Definimos el DTO liviano con record
public record ProductoCardDto(String id, String nombre, BigDecimal precio) {}

// 2. Lo usamos directamente como tipo de retorno en el repositorio
public interface ProductoRepository extends MongoRepository<Producto, String> {
    // Spring ejecuta automáticamente: db.productos.find({ "categorias": ?0 }, { "nombre": 1, "precio": 1 })
    List<ProductoCardDto> findByCategoriasContaining(String categoria);
}
```

---

## 14. Auditoría y Ciclo de Vida (`@EnableMongoAuditing`)

Spring Data MongoDB puede rellenar automáticamente quién y cuándo creó o modificó un documento, además de disparar eventos antes o después de guardar:

| Elemento | Significado | Ejemplo |
|---|---|---|
| **`@EnableMongoAuditing`** | Anotación en una clase `@Configuration` que activa la auditoría automática en Mongo. | `@Configuration @EnableMongoAuditing public class MongoConfig {}` |
| **`@CreatedDate` / `@LastModifiedDate`** | Guarda automáticamente la fecha/hora de creación (solo en insert) y de última modificación (en cada save/update). | `@CreatedDate private Instant creadoEn;` |
| **`@CreatedBy` / `@LastModifiedBy`** | Guarda el usuario actual obtenido de un bean `AuditorAware<String>` (conectado a Spring Security). | `@CreatedBy private String creadoPor;` |
| **`AbstractMongoEventListener<T>`** | Listener para interceptar eventos del ciclo de vida del documento (`onBeforeConvert`, `onBeforeSave`, `onAfterSave`, `onAfterLoad`). | Validar o calcular un hash antes de que cualquier `Producto` se guarde. |

**Ejemplo de Listener del Ciclo de Vida en MongoDB:**
```java
@Component
public class ProductoMongoListener extends AbstractMongoEventListener<Producto> {

    @Override
    public void onBeforeConvert(BeforeConvertEvent<Producto> event) {
        Producto producto = event.getSource();
        if (producto.getSlug() == null && producto.getNombre() != null) {
            producto.setSlug(producto.getNombre().toLowerCase().replace(" ", "-"));
        }
    }
}
```

---

## 15. Transacciones Multi-Documento y Bloqueo Optimista (`@Version`)

### 1. Atomicidad de Documento Único vs `@Transactional`
- En MongoDB, las operaciones sobre **un único documento** (incluyendo todos sus subdocumentos embebidos) son **siempre 100% atómicas** por naturaleza sin necesidad de configurar transacciones.
- Si necesitas modificar **varios documentos en distintas colecciones** en una operación de "todo o nada", debes registrar el bean `MongoTransactionManager` y anotar el método con `@Transactional` (requiere que MongoDB corra como **Replica Set**, no como instancia *standalone*).

### 2. Bloqueo Optimista con `@Version`
Evita que dos usuarios pisen los cambios del otro al editar el mismo documento simultáneamente:

| Elemento | Cómo funciona | Ejemplo |
|---|---|---|
| **`@Version`** | Al insertar vale `0`. En cada `save()` incrementa en `+1` y verifica que nadie lo haya cambiado en el medio. Si alguien lo modificó antes, lanza `OptimisticLockingFailureException`. | `@Version private Long version;` |
| **`MongoTransactionManager`** | Bean que habilita el soporte de `@Transactional` para operaciones multi-documento en MongoDB. | `@Bean MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory)` |

**Ejemplo de configuración de Transacciones y `@Version`:**
```java
@Configuration
public class MongoTxConfig {
    @Bean
    public MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }
}

@Document(collection = "cuentas")
public class CuentaBilletera {
    @Id
    private String id;
    private BigDecimal saldo;

    @Version
    private Long version; // Protege contra escrituras concurrentes perdidas
}
```

---

## 16. Consultas Geoespaciales (`GeoJsonPoint`, `@GeoSpatialIndexed`)

MongoDB es extremadamente potente para aplicaciones con mapas (delivery, transporte, sucursales cercanas):

| Clase / Anotación | Significado | Ejemplo |
|---|---|---|
| **`GeoJsonPoint`** | Representa una coordenada geográfica. **¡Atención al orden!**: en GeoJSON siempre va primero **Longitud (`X`)** y segundo **Latitud (`Y`)**. | `new GeoJsonPoint(-58.3816, -34.6037)` *(Buenos Aires)* |
| **`@GeoSpatialIndexed`** | Crea el índice `2dsphere` necesario para hacer búsquedas por radio o distancia. | `@GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)` |
| **`Distance` y `Metrics`** | Define el radio de búsqueda en kilómetros (`Metrics.KILOMETERS`) o millas. | `new Distance(5, Metrics.KILOMETERS)` |
| **`GeoResults<T>`** | Resultado que envuelve cada documento encontrado junto con la distancia exacta a la que se encuentra del punto buscado. | `GeoResults<Sucursal> findByUbicacionNear(Point p, Distance d);` |

**Ejemplo de búsqueda de sucursales en un radio de 5 km:**
```java
@Document(collection = "sucursales")
public class Sucursal {
    @Id
    private String id;
    private String nombre;

    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint ubicacion; // Longitud, Latitud
}

public interface SucursalRepository extends MongoRepository<Sucursal, String> {
    // Devuelve las sucursales cercanas ordenadas de la más próxima a la más lejana con su distancia en km
    GeoResults<Sucursal> findByUbicacionNear(Point ubicacionActual, Distance radioMaximo);
}
```

---

## 17. GridFS (Almacenamiento de Archivos Grandes)

Un documento BSON en MongoDB tiene un límite máximo estricto de **16 MB**. Si necesitas guardar archivos mayores (videos, PDFs pesados, backups) dentro de MongoDB, se usa **GridFS** (`GridFsTemplate`), que divide el archivo en fragmentos (*chunks*) de 255 KB en dos colecciones (`fs.files` y `fs.chunks`):

| Clase / Método | Propósito | Ejemplo |
|---|---|---|
| **`GridFsTemplate`** | Bean inyectable de Spring Boot para subir, buscar, descargar y borrar archivos en GridFS. | `gridFsTemplate.store(inputStream, "contrato.pdf", "application/pdf", metadata);` |
| **`GridFSFile`** | Metadatos del archivo encontrado (nombre, tamaño, fecha, metadatos personalizados). | `GridFSFile file = gridFsTemplate.findOne(query);` |
| **`GridFsResource`** | Recurso que contiene el `InputStream` para descargar o transmitir los bytes del archivo al cliente HTTP. | `GridFsResource resource = gridFsTemplate.getResource(file);` |

**Ejemplo de subida y descarga con `GridFsTemplate`:**
```java
@Service
public class ArchivoGridFsService {

    private final GridFsTemplate gridFsTemplate;

    public ArchivoGridFsService(GridFsTemplate gridFsTemplate) {
        this.gridFsTemplate = gridFsTemplate;
    }

    public String subirArchivo(MultipartFile archivo, String usuarioId) throws IOException {
        DBObject metadata = new BasicDBObject();
        metadata.put("subidoPor", usuarioId);

        ObjectId id = gridFsTemplate.store(
                archivo.getInputStream(),
                archivo.getOriginalFilename(),
                archivo.getContentType(),
                metadata
        );
        return id.toHexString();
    }

    public GridFsResource descargarArchivo(String fileId) {
        GridFSFile file = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(fileId)));
        if (file == null) {
            throw new IllegalArgumentException("Archivo no encontrado: " + fileId);
        }
        return gridFsTemplate.getResource(file);
    }
}
```

---

## 18. Conversores Personalizados (`Converter`) y `_class`

Por defecto, Spring Data MongoDB agrega un campo oculto llamado **`_class`** en cada documento BSON con el nombre completo del paquete y clase Java (ej. `"_class": "com.ejemplo.tienda.model.Producto"`) para saber cómo deserializar herencia y polimorfismo.

| Concepto | Para qué sirve | Ejemplo |
|---|---|---|
| **Campo `_class`** | Guarda el tipo Java en MongoDB. Si no usas herencia y quieres ahorrar espacio o compartes la base de datos con aplicaciones en Node/Python, puedes desactivarlo. | `DefaultMongoTypeMapper(null)` |
| **`@WritingConverter`** | Convierte un tipo complejo de Java (ej. `ZonedDateTime` o un Value Object `Dinero`) a un tipo nativo de Mongo al guardar. | `class ZonedDateTimeToDocumentConverter implements Converter<ZonedDateTime, String>` |
| **`@ReadingConverter`** | Convierte el dato guardado en Mongo de vuelta a tu tipo de Java al leer. | `class StringToZonedDateTimeConverter implements Converter<String, ZonedDateTime>` |
| **`MongoCustomConversions`** | Bean donde registras tu lista de conversores personalizados. | `new MongoCustomConversions(List.of(new MiWritingConverter(), new MiReadingConverter()))` |

**Ejemplo para eliminar el campo `_class` de todos los documentos guardados:**
```java
@Configuration
public class MongoMappingConfig {

    @Bean
    public MappingMongoConverter mappingMongoConverter(MongoDatabaseFactory factory,
                                                       MongoMappingContext context,
                                                       MongoCustomConversions conversions) {
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(factory);
        MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, context);
        converter.setCustomConversions(conversions);
        // Pasar null en DefaultMongoTypeMapper elimina el campo "_class" de MongoDB:
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
        return converter;
    }
}
```

---

## 19. Testing con MongoDB (`@DataMongoTest` y Testcontainers)

Para probar la capa de persistencia de MongoDB sin levantar toda la aplicación (ni controladores ni seguridad), Spring Boot ofrece `@DataMongoTest`:

| Herramienta | Qué hace | Ejemplo |
|---|---|---|
| **`@DataMongoTest`** | Slice test que configura únicamente los `@Document`, `MongoRepository` y `MongoTemplate`. | `@DataMongoTest class ProductoRepositoryTest` |
| **Testcontainers (`MongoDBContainer`)** | Levanta un contenedor Docker real de MongoDB (incluso en modo Replica Set) durante la ejecución del test. | `@Container @ServiceConnection static MongoDBContainer mongo = new MongoDBContainer("mongo:7.0");` |
| **`@ServiceConnection`** | Anotación de Spring Boot 3.1+ que conecta automáticamente el `MongoDBContainer` con la configuración del test sin escribir `@DynamicPropertySource`. | Ver ejemplo abajo. |

**Ejemplo de Test de Integración con `@DataMongoTest` + Testcontainers:**
```java
@DataMongoTest
@Testcontainers
class ProductoRepositoryIntegrationTest {

    @Container
    @ServiceConnection // Configura automáticamente spring.data.mongodb.uri apuntando al contenedor
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0");

    @Autowired
    private ProductoRepository productoRepository;

    @BeforeEach
    void limpiarBase() {
        productoRepository.deleteAll();
    }

    @Test
    void deberiaEncontrarProductosPorCategoriaYPrecio() {
        // Arrange
        Producto p1 = new Producto("Monitor 27", new BigDecimal("350.00"), List.of("monitores", "gamer"));
        Producto p2 = new Producto("Mouse Pad", new BigDecimal("15.00"), List.of("accesorios", "gamer"));
        productoRepository.saveAll(List.of(p1, p2));

        // Act
        List<Producto> resultado = productoRepository.buscarDisponiblesPorCategoriaYPrecioMaximo(
                "gamer", new BigDecimal("100.00")
        );

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Mouse Pad");
    }
}
```

---

## 20. Buenas Prácticas y Errores Comunes

| Error Común / Mala Práctica | Por qué ocurre | Solución Recomendada (Buena Práctica) |
|---|---|---|
| **`BigDecimal` se guarda como `String` y ordena mal (`"100" < "20"`)** | Por defecto sin configurar, `BigDecimal` puede mapearse a `String` en versiones o configuraciones por defecto. | Anotar siempre los campos monetarios con `@Field(targetType = FieldType.DECIMAL128)`. |
| **Los índices `@Indexed` no se crean en la base de datos** | Desde Spring Boot 3, `auto-index-creation` viene en `false` por defecto para no bloquear el arranque en producción. | Activar `spring.data.mongodb.auto-index-creation=true` en desarrollo o gestionar índices con migraciones (ej. **Mongock**). |
| **Modelar MongoDB igual que SQL (lleno de `@DocumentReference` y colecciones diminutas)** | Pensar en tablas normalizadas en vez de documentos agregados. | **Embeber** los datos que pertenecen a la entidad y se leen juntos (ej. ítems de un pedido, direcciones de un usuario). |
| **Arrays embebidos que crecen infinitamente (*Unbounded Arrays*)** | Guardar una lista de 500.000 logs o comentarios adentro de un único documento `Post` hasta superar el límite de 16 MB. | Si una relación 1 a N puede crecer a miles de elementos sin techo, sepárala en su propia colección referenciando al padre. |
| **Usar `findAll()` y filtrar en Java con `.stream().filter(...)`** | Trae toda la colección por red a la memoria de la JVM, causando `OutOfMemoryError`. | Filtrar y paginar siempre en la base de datos usando Query Methods, `@Query`, `Criteria` o `@Aggregation`. |

> **Tip de lectura:** Cuando inspecciones un proyecto que usa Spring Data MongoDB, empieza revisando las clases con **`@Document`** para distinguir qué objetos están **embebidos** (clases normales sin `@Document`) y cuáles están **referenciados** (`@DocumentReference`). Luego busca dónde se inyecta **`MongoTemplate`**: ahí es donde encontrarás las búsquedas con filtros dinámicos, las actualizaciones atómicas (`Update` / `findAndModify`) y los reportes complejos (`Aggregation`).

