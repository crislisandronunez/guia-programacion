# Glosario y Guía de Bases de Datos Relacionales, SQL y PostgreSQL

Esta guía parte del **modelo de datos** para explicar cómo guardarlo, consultarlo y proteger sus reglas en una base relacional. Primero presenta tablas, claves, relaciones y transacciones; después muestra **SQL** y ejemplos concretos de **PostgreSQL**, con una conexión desde **Java**. La meta es comprender qué hace la base de datos antes de delegar ese trabajo en un ORM.

---

## Índice

1. [Qué resuelve una base de datos relacional](#1-qué-resuelve-una-base-de-datos-relacional)
2. [Glosario fundamental](#2-glosario-fundamental)
3. [Del modelo de datos a tablas y relaciones](#3-del-modelo-de-datos-a-tablas-y-relaciones)
4. [Tipos de datos y significado de NULL](#4-tipos-de-datos-y-significado-de-null)
5. [Claves y restricciones que protegen reglas](#5-claves-y-restricciones-que-protegen-reglas)
6. [Crear un esquema con SQL](#6-crear-un-esquema-con-sql)
7. [Insertar, actualizar y borrar datos](#7-insertar-actualizar-y-borrar-datos)
8. [Consultar con SELECT, WHERE y ORDER BY](#8-consultar-con-select-where-y-order-by)
9. [Relacionar filas con JOIN](#9-relacionar-filas-con-join)
10. [Agrupar datos y calcular resultados](#10-agrupar-datos-y-calcular-resultados)
11. [Subconsultas y expresiones comunes](#11-subconsultas-y-expresiones-comunes)
12. [Transacciones: confirmar o deshacer una operación](#12-transacciones-confirmar-o-deshacer-una-operación)
13. [Aislamiento y anomalías concurrentes](#13-aislamiento-y-anomalías-concurrentes)
14. [Bloqueos y cambios condicionales](#14-bloqueos-y-cambios-condicionales)
15. [Índices: acelerar búsquedas con un costo](#15-índices-acelerar-búsquedas-con-un-costo)
16. [Leer un plan con EXPLAIN](#16-leer-un-plan-con-explain)
17. [Herramientas útiles de PostgreSQL](#17-herramientas-útiles-de-postgresql)
18. [Consultar desde Java con parámetros](#18-consultar-desde-java-con-parámetros)
19. [Cambios de esquema y migraciones](#19-cambios-de-esquema-y-migraciones)
20. [Caso completo: préstamos de una biblioteca](#20-caso-completo-préstamos-de-una-biblioteca)
21. [Errores comunes al trabajar con SQL](#21-errores-comunes-al-trabajar-con-sql)
22. [Ejercicios para practicar](#22-ejercicios-para-practicar)
23. [Lista de verificación antes de dar el esquema por terminado](#23-lista-de-verificación-antes-de-dar-el-esquema-por-terminado)

---

## 1. Qué resuelve una base de datos relacional

Una base de datos conserva información más allá de la ejecución de un programa y permite que distintas operaciones la consulten y modifiquen. El modelo relacional organiza esa información en **tablas de filas y columnas**, relacionadas mediante claves y protegidas por restricciones.

```text
Pregunta del negocio: ¿quién tiene el ejemplar E-17?

Modelo: un préstamo activo une un socio y un ejemplar.
Representación relacional: una fila de prestamos contiene socio_id,
ejemplar_codigo y fecha_devolucion.
Consulta: buscar el préstamo de E-17 cuya devolución aún no existe.
```

La base no «entiende» por sí sola qué significa préstamo, pero puede hacer cumplir muchas de sus reglas: identidad, existencia de referencias, fechas válidas y unicidad. **El diseño empieza por el significado de los datos**, no por la sintaxis de una anotación Java.

---

## 2. Glosario fundamental

| Término | Significado | Ejemplo |
|---|---|---|
| **Tabla** | Conjunto de filas con columnas definidas. | `ejemplares`. |
| **Fila** | Registro individual de una tabla. | El ejemplar `E-17`. |
| **Columna** | Atributo almacenado en cada fila. | `codigo`. |
| **Esquema** | Estructura de tablas, tipos, relaciones y restricciones. | Tablas de biblioteca. |
| **Clave primaria (*PK*)** | Identifica una fila de manera única y no nula. | `prestamos.id`. |
| **Clave foránea (*FK*)** | Exige que una referencia apunte a una fila existente. | `prestamos.socio_id`. |
| **UNIQUE** | Impide valores o combinaciones duplicadas según su definición. | Código único de ejemplar. |
| **NOT NULL** | Impide que falte el valor de una columna. | Fecha de inicio. |
| **CHECK** | Verifica una condición sobre una fila. | Devolución posterior o igual al inicio. |
| **Consulta (*query*)** | Petición de datos o modificación expresada en SQL. | Buscar préstamos activos. |
| **JOIN** | Combina filas relacionadas para una consulta. | Préstamos con nombre de socio. |
| **Índice** | Estructura adicional que puede acelerar ciertas búsquedas. | Búsqueda por `socio_id`. |
| **Transacción** | Conjunto de operaciones coordinadas con confirmación o deshacer. | Registrar un préstamo. |
| **COMMIT** | Confirma una transacción. | Los cambios quedan comprometidos. |
| **ROLLBACK** | Deshace cambios no confirmados de una transacción. | Se rechaza una operación incompleta. |
| **Aislamiento** | Regla sobre lo que una transacción puede observar de otras. | Lecturas mientras otro usuario modifica. |
| **Plan de ejecución** | Estrategia elegida por la base para resolver una consulta. | Usar índice o recorrer tabla. |

**SQL** es el lenguaje para definir y manipular datos relacionales. **PostgreSQL** es un motor que implementa SQL y agrega capacidades propias. Una clase JPA puede representar una fila, pero aprender SQL permite comprender qué consulta y qué cambios ejecuta realmente el ORM.

---

## 3. Del modelo de datos a tablas y relaciones

Retomemos la biblioteca de la guía de modelado de datos:

```text
Edición -> tiene varios Ejemplares.
Socio -> puede realizar varios Préstamos.
Ejemplar -> puede aparecer en varios Préstamos históricos.
Préstamo -> corresponde a exactamente un Socio y un Ejemplar.
```

Una representación lógica posible es:

| Tabla | Clave primaria | Otras columnas importantes | Claves foráneas |
|---|---|---|---|
| `ediciones` | `id` | `titulo`, `isbn` | — |
| `ejemplares` | `codigo` | `estado` | `edicion_id` → `ediciones.id` |
| `socios` | `id` | `nombre`, `activo` | — |
| `prestamos` | `id` | `inicio`, `devolucion` | `socio_id` y `ejemplar_codigo` |

**Decisión importante:** un préstamo es una tabla propia porque el vínculo entre socio y ejemplar tiene fecha, estado e historial. Guardar solo `ejemplar.socio_actual` haría difícil explicar préstamos pasados.

Una relación «uno a muchos» suele representarse colocando la clave foránea en el lado que puede tener muchas filas: muchos ejemplares incluyen `edicion_id`; muchos préstamos incluyen `socio_id`.

---

## 4. Tipos de datos y significado de NULL

El tipo de una columna define qué clase de valor puede almacenar. Además de elegir un tipo técnico, hay que definir **qué significa**.

| Dato | Tipo PostgreSQL posible | Pregunta de diseño |
|---|---|---|
| Identificador numérico | `bigint` | ¿Lo genera la base o viene del negocio? |
| Código de ejemplar | `text` | ¿Debe conservar letras y ceros iniciales? |
| Fecha de préstamo | `date` | ¿Importa solo el día civil? |
| Instante de registro | `timestamptz` | ¿Se necesita un momento absoluto? |
| Importe | `numeric(12,2)` | ¿Qué moneda y escala se aceptan? |
| Bandera de inscripción | `boolean` | ¿Puede faltar o siempre es verdadero/falso? |

`NULL` significa **ausencia de un valor conocido en esa columna**. No equivale a `0`, cadena vacía ni `false`. En el ejemplo, `prestamos.devolucion IS NULL` significa «todavía no se registró la devolución».

```sql
-- Correcto para buscar ausencias:
SELECT id FROM prestamos WHERE devolucion IS NULL;

-- No sirve para ese propósito:
SELECT id FROM prestamos WHERE devolucion = NULL;
```

Las comparaciones con `NULL` producen un valor lógico **desconocido**. Por eso SQL usa `IS NULL` y `IS NOT NULL`. Un `CHECK` tampoco rechaza automáticamente una expresión que resulta desconocida; si el dato es obligatorio, agregá `NOT NULL`.

---

## 5. Claves y restricciones que protegen reglas

Una **clave primaria** identifica la fila. Una **clave foránea** protege que la referencia exista. Las restricciones permiten que la base rechace datos inválidos incluso si dos programas distintos escriben sobre ella.

```sql
CREATE TABLE categorias (
    id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre text NOT NULL UNIQUE,
    CHECK (btrim(nombre) <> '')
);

CREATE TABLE productos (
    id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    categoria_id bigint NOT NULL REFERENCES categorias(id),
    nombre text NOT NULL,
    precio numeric(12,2) NOT NULL CHECK (precio >= 0)
);
```

| Restricción | Qué evita | Qué no decide |
|---|---|---|
| `PRIMARY KEY` | Dos filas con el mismo ID. | Si dos pedidos son conceptualmente el mismo pedido. |
| `FOREIGN KEY` | Referencia a una fila inexistente. | Si el socio está habilitado para pedir. |
| `UNIQUE` | Duplicados de la clave definida. | Si la clave elegida representa bien al negocio. |
| `CHECK` | Valores de una fila que incumplen una condición. | Reglas que necesitan inspeccionar varias filas. |

En PostgreSQL, una restricción `UNIQUE` habitual permite varios `NULL`, porque no se tratan como valores iguales para esa unicidad. Si un dato debe existir, combiná `UNIQUE` con `NOT NULL`. Si su regla es más específica, diseñá la restricción correspondiente.

---

## 6. Crear un esquema con SQL

Las sentencias que definen estructuras suelen llamarse **DDL**. Una tabla se crea con columnas, tipos y restricciones.

```sql
CREATE TABLE socios (
    id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre text NOT NULL CHECK (btrim(nombre) <> ''),
    activo boolean NOT NULL DEFAULT true
);
```

`GENERATED ALWAYS AS IDENTITY` pide que PostgreSQL genere el identificador. `DEFAULT true` establece el valor si una inserción no proporciona `activo`; no reemplaza `NOT NULL` si la regla exige que nunca falte.

Una tabla puede cambiar después con `ALTER TABLE`, pero ese cambio debe pensarse junto con **las filas ya guardadas** y las versiones de la aplicación que podrían seguir usando el esquema anterior. Crear el primer esquema es solo el comienzo de su ciclo de vida.

---

## 7. Insertar, actualizar y borrar datos

`INSERT`, `UPDATE` y `DELETE` modifican filas. En PostgreSQL, `RETURNING` permite obtener datos de las filas afectadas.

```sql
INSERT INTO socios (nombre)
VALUES ('Ana')
RETURNING id, nombre, activo;

UPDATE socios
SET activo = false
WHERE id = 42
RETURNING id, activo;

DELETE FROM socios
WHERE id = 42
RETURNING id;
```

**Antes de ejecutar un `UPDATE` o `DELETE`, comprobá su `WHERE`.** Sin filtro, pueden afectar todas las filas de la tabla. También inspeccioná cuántas filas se modificaron: `0` puede significar que el ID no existía o que el estado esperado ya había cambiado.

El borrado físico puede chocar con claves foráneas o destruir historial. Para un socio con préstamos, la política de «desactivar» puede ser más adecuada que borrar; eso depende del modelo y de las necesidades del negocio.

---

## 8. Consultar con SELECT, WHERE y ORDER BY

`SELECT` elige columnas; `FROM` indica la fuente; `WHERE` filtra filas; `ORDER BY` define un orden. Si querés una cantidad acotada de resultados, usá `LIMIT`.

```sql
SELECT id, nombre
FROM socios
WHERE activo = true
ORDER BY nombre ASC, id ASC
LIMIT 20;
```

Sin `ORDER BY`, **no hay un orden de filas garantizado**. Agregar `id` como segundo criterio hace estable el orden cuando dos socios comparten nombre.

```sql
SELECT id, nombre
FROM socios
WHERE nombre ILIKE 'an%'
ORDER BY id;
```

`ILIKE` permite comparar texto sin distinguir mayúsculas y minúsculas en PostgreSQL. No toda búsqueda de texto se beneficia del mismo índice; la forma de la condición importa.

**Recordatorio:** `SELECT *` puede ser útil al explorar, pero en código de aplicación conviene pedir las columnas que realmente necesitás. Cambios en el esquema no deberían alterar sin querer el contrato de una respuesta.

---

## 9. Relacionar filas con JOIN

Un `JOIN` combina filas mediante una condición. La pregunta no es «qué tabla va primero» sino **qué relación del modelo estás reconstruyendo**.

```sql
SELECT p.id, s.nombre, p.inicio
FROM prestamos AS p
JOIN socios AS s ON s.id = p.socio_id
WHERE p.devolucion IS NULL;
```

`JOIN` (interno) devuelve filas con coincidencia a ambos lados. `LEFT JOIN` conserva las filas de la izquierda aunque no haya coincidencia.

```sql
SELECT s.id, s.nombre, p.id AS prestamo_id
FROM socios AS s
LEFT JOIN prestamos AS p
    ON p.socio_id = s.id AND p.devolucion IS NULL;
```

Aquí aparecen socios sin préstamos activos, con `prestamo_id = NULL`. Si pusieras `p.devolucion IS NULL` en `WHERE`, podrías cambiar el significado del filtro, especialmente al combinar otras condiciones sobre `p`. **En un `LEFT JOIN`, pensá si la condición limita la coincidencia (`ON`) o elimina filas del resultado (`WHERE`).**

Una relación uno a muchos puede producir **varias filas por socio**. Ese resultado no es un duplicado accidental: representa varias coincidencias.

---

## 10. Agrupar datos y calcular resultados

`GROUP BY` reúne filas por una clave; funciones como `COUNT`, `SUM` y `AVG` calculan valores por grupo.

```sql
SELECT s.id, s.nombre, COUNT(p.id) AS prestamos_activos
FROM socios AS s
LEFT JOIN prestamos AS p
    ON p.socio_id = s.id AND p.devolucion IS NULL
GROUP BY s.id, s.nombre
ORDER BY prestamos_activos DESC, s.id;
```

`COUNT(p.id)` devuelve `0` para un socio sin préstamo coincidente. `COUNT(*)` contaría la fila conservada por el `LEFT JOIN` y daría `1` en ese caso. Esa diferencia es importante.

```sql
SELECT socio_id, COUNT(*) AS cantidad
FROM prestamos
GROUP BY socio_id
HAVING COUNT(*) >= 3;
```

`WHERE` filtra **antes** de agrupar; `HAVING` filtra **grupos** ya calculados. Para un promedio, revisá qué filas entran al cálculo y cómo se tratan los `NULL`. Una consulta sintácticamente válida puede contestar una pregunta distinta de la que pidió el negocio.

---

## 11. Subconsultas y expresiones comunes

Una **subconsulta** resuelve una pregunta dentro de otra. `EXISTS` es útil para preguntar si hay al menos una fila relacionada.

```sql
SELECT e.codigo
FROM ejemplares AS e
WHERE NOT EXISTS (
    SELECT 1
    FROM prestamos AS p
    WHERE p.ejemplar_codigo = e.codigo
      AND p.devolucion IS NULL
);
```

Una **CTE** (*Common Table Expression*) con `WITH` da nombre a un resultado intermedio y puede hacer una consulta más legible:

```sql
WITH activos AS (
    SELECT ejemplar_codigo
    FROM prestamos
    WHERE devolucion IS NULL
)
SELECT e.codigo
FROM ejemplares AS e
WHERE NOT EXISTS (
    SELECT 1 FROM activos AS a WHERE a.ejemplar_codigo = e.codigo
);
```

Estas dos consultas expresan una idea similar. **La forma más legible no garantiza por sí sola mejor rendimiento**; PostgreSQL decide un plan según estadísticas, índices y versión. Si importa el costo, medilo con `EXPLAIN`.

---

## 12. Transacciones: confirmar o deshacer una operación

Una transacción agrupa cambios que deben tratarse como una operación. En SQL se delimita con `BEGIN`, `COMMIT` y, cuando se necesita deshacer, `ROLLBACK`.

```sql
BEGIN;

UPDATE socios SET activo = false WHERE id = 42;
-- Otros cambios que pertenecen a la misma operación.

COMMIT;
```

| Propiedad ACID | Idea práctica |
|---|---|
| **Atomicidad** | La transacción confirma sus cambios o se deshace como unidad. |
| **Consistencia** | Las restricciones definidas deben cumplirse al confirmar. |
| **Aislamiento** | Las transacciones concurrentes observan datos según el nivel elegido. |
| **Durabilidad** | Los cambios confirmados sobreviven a los fallos que el motor promete tolerar. |

**Una transacción local de PostgreSQL no incluye automáticamente una llamada HTTP ni la publicación de un mensaje externo.** Si una operación mezcla sistemas, hay que diseñar qué pasa cuando uno confirma y otro falla.

Además de errores de SQL, una transacción larga mantiene recursos y puede bloquear trabajo ajeno. Definí su alcance según la regla que protege y evitá esperas externas innecesarias dentro de ella.

---

## 13. Aislamiento y anomalías concurrentes

Dos transacciones pueden leer y escribir al mismo tiempo. El **nivel de aislamiento** define qué pueden observar y qué anomalías evita el motor.

| Anomalía | Situación simplificada |
|---|---|
| **Lectura sucia** | Una transacción lee un cambio de otra que aún podría deshacerse. |
| **Lectura no repetible** | La misma fila devuelve valores distintos en dos lecturas de una transacción. |
| **Fantasma** | Una búsqueda por condición devuelve un conjunto diferente tras otra transacción. |
| **Actualización perdida** | Dos operaciones basadas en un valor previo terminan pisándose sin la coordinación adecuada. |

PostgreSQL usa **MVCC** (*Multi-Version Concurrency Control*) para ofrecer vistas de datos según la transacción y el aislamiento. En su nivel habitual `READ COMMITTED`, cada sentencia observa una fotografía de datos confirmados al comenzar esa sentencia. Dos sentencias dentro de la misma transacción pueden observar cambios confirmados entre ellas.

```text
T1: consulta préstamos activos de E-17 -> ninguno.
T2: consulta préstamos activos de E-17 -> ninguno.
T1: inserta préstamo.
T2: intenta insertar préstamo.
```

Una comprobación previa en la aplicación **no protege por sí sola** la regla de unicidad. Una restricción de la base o una operación de escritura coordinada es necesaria. Niveles más estrictos pueden rechazar transacciones que deben reintentarse; no transforman una lógica incompleta en una regla de negocio clara.

---

## 14. Bloqueos y cambios condicionales

Un bloqueo permite coordinar operaciones sobre una fila. `SELECT ... FOR UPDATE` toma un bloqueo de fila para una transacción que necesita decidir y cambiar un dato.

```sql
BEGIN;

SELECT estado
FROM ejemplares
WHERE codigo = 'E-17'
FOR UPDATE;

-- La aplicación comprueba que existe y que su estado admite préstamos.
-- Después inserta el préstamo dentro de esta misma transacción.

COMMIT;
```

Otra opción es hacer que el cambio sea **condicional** y comprobar cuántas filas modificó:

```sql
UPDATE inventario
SET disponibles = disponibles - 1
WHERE producto_id = 42 AND disponibles > 0
RETURNING disponibles;
```

Si no devuelve filas, no se descontó stock. Para este caso, la condición y el cambio ocurren en una sentencia. La tabla necesitaría además una restricción como `CHECK (disponibles >= 0)` para proteger el dato frente a otras escrituras.

**Alcance:** `FOR UPDATE` debe ejecutarse dentro de una transacción que continúe hasta el cambio que protege. Si cada sentencia se confirma por separado, el bloqueo se libera antes de la decisión posterior.

---

## 15. Índices: acelerar búsquedas con un costo

Un índice permite encontrar o recorrer ciertos datos sin inspeccionar necesariamente cada fila de la tabla. Es una **estructura adicional** que ocupa espacio y debe actualizarse cuando cambian los datos.

```sql
CREATE INDEX prestamos_socio_inicio_idx
ON prestamos (socio_id, inicio DESC);
```

Esa definición puede ayudar a buscar préstamos de un socio ordenados por fecha. **El orden de columnas importa**: un índice que empieza por `socio_id` no ofrece las mismas posibilidades que uno que empieza por `inicio`.

| Consulta | Índice candidato | Por qué |
|---|---|---|
| `WHERE socio_id = ? ORDER BY inicio DESC` | `(socio_id, inicio DESC)` | Filtra por socio y conserva orden útil. |
| `WHERE ejemplar_codigo = ? AND devolucion IS NULL` | Índice parcial sobre activos | Accede al subconjunto de préstamos activos. |
| Buscar todos los registros de una tabla pequeña | Tal vez ninguno adicional | Recorrerla puede ser más barato. |

Un índice **no garantiza** que toda consulta lo use. PostgreSQL compara costos estimados. Agregar índices a ciegas puede volver más lentas las escrituras sin mejorar lecturas importantes.

---

## 16. Leer un plan con EXPLAIN

`EXPLAIN` muestra el plan que PostgreSQL estima para una consulta. `EXPLAIN ANALYZE` **ejecuta** la consulta y añade mediciones reales; si la sentencia modifica datos, ejecutarla tiene efectos, así que usala con cuidado.

```sql
EXPLAIN
SELECT id, inicio
FROM prestamos
WHERE socio_id = 42
ORDER BY inicio DESC
LIMIT 20;
```

| Señal del plan | Pregunta |
|---|---|
| `Seq Scan` | ¿Recorrer la tabla completa es razonable para este tamaño y filtro? |
| `Index Scan` | ¿El índice ayuda a encontrar las filas deseadas? |
| `Sort` | ¿El orden ya podía obtenerse de un índice? |
| Filas estimadas frente a reales | ¿Las estadísticas representan bien los datos? |

No clasifiques `Seq Scan` como error automático: para una tabla pequeña o un filtro que devuelve gran parte de ella, puede ser la mejor elección. Buscá primero **la consulta real, sus parámetros y su volumen de datos**.

---

## 17. Herramientas útiles de PostgreSQL

PostgreSQL ofrece construcciones que permiten expresar reglas y operaciones concretas:

| Herramienta | Uso | Ejemplo |
|---|---|---|
| `GENERATED ... AS IDENTITY` | Generar identificadores. | `id bigint GENERATED ALWAYS AS IDENTITY`. |
| `RETURNING` | Obtener filas insertadas o modificadas. | `INSERT ... RETURNING id`. |
| Índice parcial | Indexar o imponer unicidad solo sobre filas que cumplen una condición. | Préstamos con `devolucion IS NULL`. |
| `ON CONFLICT` | Definir cómo actuar ante un conflicto de unicidad. | Insertar una configuración o actualizarla. |
| `timestamptz` | Guardar un instante con manejo de zona al interpretar la entrada/salida. | Momento de registro. |

```sql
CREATE UNIQUE INDEX un_prestamo_activo_por_ejemplar
ON prestamos (ejemplar_codigo)
WHERE devolucion IS NULL;
```

El índice parcial impide que existan **dos préstamos activos** para un mismo ejemplar. Cuando se devuelve uno y su `devolucion` deja de ser `NULL`, un préstamo posterior puede abrirse. Esto protege la regla incluso si dos instancias de la aplicación intentan insertar al mismo tiempo: una puede recibir un error de unicidad y debe manejarlo.

`ON CONFLICT` **no sustituye** comprender qué conflicto puede ocurrir ni qué respuesta espera el negocio. Ignorar silenciosamente un intento de préstamo rechazado podría comunicar éxito cuando no lo hubo.

---

## 18. Consultar desde Java con parámetros

Java puede enviar SQL mediante **JDBC**. Un `PreparedStatement` separa los valores de la estructura de la consulta y evita construir SQL concatenando entrada del usuario.

```java
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

static String buscarNombre(DataSource dataSource, long socioId) throws SQLException {
    String sql = "SELECT nombre FROM socios WHERE id = ?";
    try (Connection conexion = dataSource.getConnection();
         PreparedStatement consulta = conexion.prepareStatement(sql)) {
        consulta.setLong(1, socioId);
        try (ResultSet filas = consulta.executeQuery()) {
            return filas.next() ? filas.getString("nombre") : null;
        }
    }
}
```

El ejemplo supone un `DataSource` configurado con un controlador JDBC de PostgreSQL. Devuelve `null` cuando no hay socio solo para mantener el fragmento corto; en una aplicación real, **definí explícitamente** si la ausencia se representa con `Optional`, una excepción o un resultado de negocio.

```java
// Evitar: entrada del usuario mezclada con el texto SQL.
String sqlInseguro = "SELECT nombre FROM socios WHERE id = " + entradaUsuario;
```

Además de seguridad, los parámetros aclaran qué parte es **dato** y qué parte es **instrucción**. JPA y Spring Data construyen muchas de estas operaciones por vos, pero el motor sigue ejecutando SQL y aplicando restricciones.

---

## 19. Cambios de esquema y migraciones

El esquema cambia junto con el programa. Una **migración** describe un cambio ordenado y reproducible: agregar una columna, crear un índice, transformar datos existentes o retirar una estructura antigua.

```text
Necesidad: cada socio tendrá un correo opcional.
1. Agregar la columna sin romper registros existentes.
2. Desplegar código que puede leerla y escribirla.
3. Completar datos si el negocio lo requiere.
4. Solo después evaluar si debe ser obligatoria.
```

No agregues `NOT NULL` a una columna nueva de una tabla con datos sin decidir qué valor tendrán las filas anteriores. Tampoco renombres o borres una columna mientras una versión de la aplicación todavía la usa.

| Cambio | Riesgo que revisar |
|---|---|
| Crear índice en tabla grande | Tiempo y carga de construcción. |
| Cambiar tipo de columna | Conversión de valores existentes. |
| Agregar restricción | Filas antiguas que no la cumplen. |
| Borrar columna | Lectores antiguos y pérdida de información. |

Una herramienta de migraciones ayuda a ejecutar y registrar estos pasos; **el criterio para diseñarlos sigue siendo el modelo de datos y la compatibilidad entre versiones**.

---

## 20. Caso completo: préstamos de una biblioteca

La guía de modelado de datos definió **ediciones, ejemplares, socios y préstamos**. Ahora expresamos ese modelo en PostgreSQL. El ejemplo se concentra en las reglas principales; en un sistema real también se diseñan permisos, operaciones administrativas y migraciones.

### Paso 1: crear las tablas

```sql
CREATE TABLE ediciones (
    id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    titulo text NOT NULL CHECK (btrim(titulo) <> ''),
    isbn text UNIQUE
);

CREATE TABLE ejemplares (
    codigo text PRIMARY KEY CHECK (btrim(codigo) <> ''),
    edicion_id bigint NOT NULL REFERENCES ediciones(id),
    estado text NOT NULL DEFAULT 'DISPONIBLE'
        CHECK (estado IN ('DISPONIBLE', 'FUERA_DE_SERVICIO'))
);

CREATE TABLE socios (
    id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre text NOT NULL CHECK (btrim(nombre) <> ''),
    activo boolean NOT NULL DEFAULT true
);

CREATE TABLE prestamos (
    id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    ejemplar_codigo text NOT NULL REFERENCES ejemplares(codigo),
    socio_id bigint NOT NULL REFERENCES socios(id),
    inicio date NOT NULL,
    devolucion date,
    CHECK (devolucion IS NULL OR devolucion >= inicio)
);

CREATE UNIQUE INDEX un_prestamo_activo_por_ejemplar
ON prestamos (ejemplar_codigo)
WHERE devolucion IS NULL;

CREATE INDEX prestamos_socio_inicio_idx
ON prestamos (socio_id, inicio DESC);
```

El `CHECK` protege el orden de fechas. La clave foránea protege la existencia de socio y ejemplar. El índice parcial protege **a lo sumo un préstamo activo por ejemplar**. `estado = 'DISPONIBLE'` describe la condición física o administrativa del ejemplar; **no es un segundo indicador de si está prestado**.

### Paso 2: cargar un ejemplar y consultar disponibilidad

```sql
WITH nueva_edicion AS (
    INSERT INTO ediciones (titulo, isbn)
    VALUES ('Introducción a Java', '9780000000001')
    RETURNING id
)
INSERT INTO ejemplares (codigo, edicion_id)
SELECT 'E-17', id FROM nueva_edicion;

SELECT e.codigo, d.titulo
FROM ejemplares AS e
JOIN ediciones AS d ON d.id = e.edicion_id
WHERE e.estado = 'DISPONIBLE'
  AND NOT EXISTS (
      SELECT 1 FROM prestamos AS p
      WHERE p.ejemplar_codigo = e.codigo
        AND p.devolucion IS NULL
  );
```

La consulta expresa la regla de lectura. **No uses esta lectura previa como única protección al escribir**, porque dos solicitudes pueden obtener el mismo resultado antes de insertar.

### Paso 3: registrar y devolver un préstamo

El siguiente ejemplo crea un socio y le presta `E-17`. En una operación real normalmente se usaría un socio ya existente. Antes de insertar, la aplicación debe confirmar que el ejemplar existe y que su estado permite el préstamo; el bloqueo de fila mantiene esa decisión dentro de la transacción. El índice parcial sigue siendo la protección final contra dos préstamos activos.

```sql
BEGIN;

SELECT estado
FROM ejemplares
WHERE codigo = 'E-17'
FOR UPDATE;

-- Continuar solo si se obtuvo la fila y estado = 'DISPONIBLE'.
WITH nuevo_socio AS (
    INSERT INTO socios (nombre) VALUES ('Ana') RETURNING id
)
INSERT INTO prestamos (ejemplar_codigo, socio_id, inicio)
SELECT 'E-17', id, CURRENT_DATE FROM nuevo_socio
RETURNING id;

COMMIT;
```

Si la comprobación de estado falla, **no ejecutes el `INSERT` y cerrá la transacción con `ROLLBACK`**. Si aparece una violación de unicidad porque ya había un préstamo activo, la transacción debe deshacerse y la aplicación debe comunicar «ejemplar no disponible»; no debe mostrar éxito.

Para devolver, elegí el préstamo activo exacto y comprobá cuántas filas se modificaron:

```sql
UPDATE prestamos
SET devolucion = CURRENT_DATE
WHERE id = 42
  AND devolucion IS NULL
  AND inicio <= CURRENT_DATE
RETURNING id, devolucion;
```

El `42` representa un ID de ejemplo que debe reemplazarse por el préstamo real. Si no hay fila devuelta, el préstamo no existe, ya fue devuelto o la fecha no cumple la condición; la aplicación debe distinguir esos casos si el negocio necesita mensajes diferentes.

**Límite del ejemplo:** si la biblioteca permite préstamos con fechas históricas o futuras, la regla basada en `CURRENT_DATE` debe ajustarse. El SQL implementa una política; no decide por sí mismo cuál debería ser esa política.

---

## 21. Errores comunes al trabajar con SQL

| Error | Consecuencia | Pregunta para corregirlo |
|---|---|---|
| Diseñar tablas antes de identificar entidades | Se mezclan conceptos distintos. | ¿Qué representa cada fila? |
| Usar `= NULL` | No se detectan las ausencias buscadas. | ¿Corresponde `IS NULL`? |
| Suponer orden sin `ORDER BY` | Resultados cambian de orden. | ¿Qué criterio define la salida? |
| Hacer `UPDATE` sin revisar `WHERE` | Se modifican demasiadas filas. | ¿Qué filas debería afectar? |
| Filtrar mal un `LEFT JOIN` | Desaparecen filas que debían conservarse. | ¿La condición va en `ON` o en `WHERE`? |
| Confiar solo en una comprobación previa | Dos operaciones concurrentes pasan la misma validación. | ¿Qué protege la escritura? |
| Agregar índices por cada columna | Aumenta costo de escritura y mantenimiento. | ¿Qué consultas reales los necesitan? |
| Construir SQL concatenando entrada | Se mezclan datos con instrucciones y aparece riesgo de inyección. | ¿Puedo usar parámetros? |
| Tomar `EXPLAIN ANALYZE` por lectura inocua | Una sentencia de cambio se ejecuta. | ¿Es seguro ejecutar esta consulta aquí? |
| Confundir transacción local con operación entre sistemas | Una parte confirma y otra falla. | ¿Dónde está el límite real de atomicidad? |

---

## 22. Ejercicios para practicar

Para cada ejercicio, empezá por la **pregunta de negocio**. Escribí la consulta o restricción, probá casos borde y explicá por qué cumple la regla.

| Ejercicio | Consigna | Caso que debe comprobarse |
|---|---|---|
| **Socios activos** | Listar ID y nombre ordenados. | Dos socios con el mismo nombre. |
| **Historial** | Mostrar todos los préstamos de un ejemplar por fecha. | Un ejemplar con varios préstamos terminados. |
| **Disponibilidad** | Listar ejemplares sin préstamo activo. | Ejemplar prestado, devuelto y fuera de servicio. |
| **Conteo** | Contar préstamos activos por socio. | Socio sin préstamos: debe dar `0`. |
| **Integridad** | Impedir una devolución anterior al inicio. | Fechas iguales y fechas invertidas. |
| **Concurrencia** | Intentar dos préstamos activos del mismo ejemplar. | Solo uno puede quedar confirmado. |
| **Rendimiento** | Analizar la búsqueda de historial por socio. | Comparar plan antes y después de un índice apropiado. |

**Desafío:** agregá una regla «un socio inactivo no puede recibir nuevos préstamos». Indicá qué parte puede validarla antes del `INSERT` y por qué una restricción simple sobre la fila de `prestamos` no basta para inspeccionar `socios.activo`. Diseñá cómo conservarla ante intentos concurrentes sin asumir que una lectura previa es suficiente.

---

## 23. Lista de verificación antes de dar el esquema por terminado

- [ ] Cada tabla y fila representa un concepto definido del dominio.
- [ ] Elegí identificadores y claves foráneas acordes a las relaciones.
- [ ] Definí tipos, unidades, valores obligatorios y significado de `NULL`.
- [ ] Las restricciones protegen reglas que deben valer para cualquier escritor.
- [ ] Puedo explicar qué transacciones corresponden a operaciones completas.
- [ ] Revisé qué ocurre si dos transacciones intentan el mismo cambio.
- [ ] Las consultas devuelven las filas correctas con un orden explícito si importa.
- [ ] Distingo `JOIN`, `LEFT JOIN`, `WHERE` y `HAVING` en los casos que uso.
- [ ] Elegí índices a partir de consultas reales y comprobé sus planes.
- [ ] El código Java envía valores como parámetros, no concatenados al SQL.
- [ ] Pensé cómo evolucionará el esquema cuando ya existan datos.
- [ ] Sé qué reglas quedan fuera de una sola base de datos y requieren coordinación adicional.

**Idea central:** SQL permite expresar tanto **preguntas sobre los datos** como **reglas que deben seguir siendo verdaderas**. Comprender esas dos funciones hace más fácil usar PostgreSQL, diagnosticar rendimiento y entender lo que ejecuta JPA.
