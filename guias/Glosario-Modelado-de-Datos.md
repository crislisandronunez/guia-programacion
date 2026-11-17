# Glosario y Guía de Modelado de Datos

Esta guía enseña a **decidir qué información representa un sistema, cómo se relaciona y qué reglas debe conservar** antes de elegir tablas, documentos o una tecnología de persistencia. Los conceptos aparecen primero; los ejemplos usan **Java** para hacer explícitas las decisiones del modelo.

---

## Índice

1. [Qué significa modelar datos](#1-qué-significa-modelar-datos)
2. [Glosario fundamental](#2-glosario-fundamental)
3. [Empezar por preguntas y reglas del problema](#3-empezar-por-preguntas-y-reglas-del-problema)
4. [Identificar entidades, hechos y atributos](#4-identificar-entidades-hechos-y-atributos)
5. [Identidad: cómo reconocer el mismo elemento](#5-identidad-cómo-reconocer-el-mismo-elemento)
6. [Tipos, unidades y significado de los valores](#6-tipos-unidades-y-significado-de-los-valores)
7. [Relaciones, cardinalidad y opcionalidad](#7-relaciones-cardinalidad-y-opcionalidad)
8. [Pertenencia, ciclo de vida y límites de un conjunto](#8-pertenencia-ciclo-de-vida-y-límites-de-un-conjunto)
9. [Restricciones e invariantes de los datos](#9-restricciones-e-invariantes-de-los-datos)
10. [Duplicación, dependencia y fuente de verdad](#10-duplicación-dependencia-y-fuente-de-verdad)
11. [Datos derivados y datos que conviene conservar](#11-datos-derivados-y-datos-que-conviene-conservar)
12. [Tiempo, estados e historial](#12-tiempo-estados-e-historial)
13. [Modelo conceptual, lógico y físico](#13-modelo-conceptual-lógico-y-físico)
14. [Diseñar según las consultas y cambios reales](#14-diseñar-según-las-consultas-y-cambios-reales)
15. [Del modelo a objetos Java y representaciones externas](#15-del-modelo-a-objetos-java-y-representaciones-externas)
16. [Caso completo: préstamos de una biblioteca](#16-caso-completo-préstamos-de-una-biblioteca)
17. [Errores comunes de modelado](#17-errores-comunes-de-modelado)
18. [Ejercicios de análisis y diseño](#18-ejercicios-de-análisis-y-diseño)
19. [Lista de verificación antes de elegir una base de datos](#19-lista-de-verificación-antes-de-elegir-una-base-de-datos)

---

## 1. Qué significa modelar datos

Un **modelo de datos** es una descripción deliberada de los hechos que importan para un problema: qué representan, cómo se identifican, cómo se relacionan y cuáles son sus límites válidos.

```text
La biblioteca dice: "Tenemos libros y prestamos libros".

Preguntas que revela el modelo:
- ¿Un libro es una obra, una edición o un ejemplar físico?
- Si hay tres copias de la misma edición, ¿cuántos elementos prestables existen?
- ¿Qué dato permite saber quién tiene cada ejemplar ahora?
- ¿Necesitamos conservar préstamos ya terminados?
```

Si respondemos mal estas preguntas, una tabla o una clase perfectamente escrita puede representar **el problema equivocado**. El modelo no es un dibujo decorativo: guía qué datos se guardan, qué operaciones son posibles y qué errores deben impedirse.

**Secuencia útil:** entender el problema → nombrar hechos → definir identidad y relaciones → escribir reglas → comprobar casos → elegir representación técnica.

---

## 2. Glosario fundamental

| Término | Significado | Ejemplo |
|---|---|---|
| **Dominio** | Parte de la realidad que el sistema necesita representar. | Préstamos de una biblioteca. |
| **Entidad** | Elemento cuya identidad se mantiene aunque cambien sus atributos. | Un ejemplar con código `E-17`. |
| **Atributo** | Propiedad de un elemento o hecho. | Título, fecha de préstamo. |
| **Objeto de valor** | Dato identificado por sus valores, no por un ID propio. | Un rango de fechas. |
| **Identificador** | Valor utilizado para distinguir elementos. | Código de ejemplar. |
| **Clave natural** | Identificador que ya tiene significado en el dominio. | Un código asignado por la biblioteca. |
| **Clave sustituta** | Identificador creado por el sistema sin significado de negocio. | `prestamoId = 8731`. |
| **Relación** | Vínculo entre elementos. | Un préstamo corresponde a un socio y un ejemplar. |
| **Cardinalidad** | Cuántos elementos pueden vincularse con otros. | Un socio puede tener varios préstamos. |
| **Opcionalidad** | Si una relación o atributo puede faltar. | La fecha de devolución falta mientras el préstamo está abierto. |
| **Restricción** | Regla que limita los datos permitidos. | La devolución no puede ser anterior al préstamo. |
| **Invariante** | Regla que debe seguir siendo cierta tras cada operación válida. | Un ejemplar no tiene dos préstamos activos simultáneos. |
| **Fuente de verdad** | Lugar o dato considerado autoridad para un hecho. | El préstamo activo determina quién tiene el ejemplar. |
| **Dato derivado** | Valor calculado a partir de otros datos. | `prestamoActivo = fechaDevolucion == null`. |
| **Historial** | Registro de hechos anteriores, no solo del estado actual. | Todos los préstamos pasados de un ejemplar. |
| **Esquema** | Forma estructurada que deben cumplir datos almacenados o intercambiados. | Campos y tipos exigidos para un préstamo. |

**Cuidado con las palabras:** «entidad de dominio», «objeto Java» y «fila de una base de datos» no son sinónimos obligatorios. Pueden representar información relacionada, pero cumplen funciones distintas.

---

## 3. Empezar por preguntas y reglas del problema

Antes de abrir un editor, pedí **ejemplos concretos**. Las palabras de una consigna suelen esconder varias interpretaciones.

| Pregunta | Qué descubre |
|---|---|
| ¿Qué necesita consultar el usuario? | Información que debe poder recuperarse. |
| ¿Qué puede cambiar y quién lo cambia? | Ciclo de vida y operaciones. |
| ¿Qué nunca debería ocurrir? | Restricciones e invariantes. |
| ¿Qué datos vienen de otro sistema? | Fuente de verdad y sincronización. |
| ¿Qué debe recordarse del pasado? | Historial y fechas. |
| ¿Qué significa exactamente cada palabra? | Límites del dominio. |

```text
Enunciado: "Mostrar los libros disponibles".

¿Disponible significa que existe al menos un ejemplar sin préstamo activo?
¿O que todos los ejemplares están libres?
¿Se excluyen los ejemplares dañados o reservados?
```

La respuesta afecta al modelo. Si el sistema debe distinguir «prestado», «dañado» y «reservado», guardar un solo booleano `disponible` puede perder información necesaria. Definí primero el significado de cada estado.

---

## 4. Identificar entidades, hechos y atributos

Un sustantivo del negocio **puede** sugerir una entidad, pero no alcanza para decidirlo. Preguntá si necesita identidad, ciclo de vida y relaciones propias.

```text
Libro o edición: título y datos bibliográficos.
Ejemplar: copia física concreta que la biblioteca presta.
Socio: persona registrada que puede pedir préstamos.
Préstamo: hecho de que un socio recibió un ejemplar en una fecha.
```

| Elemento | ¿Por qué existe en el modelo? | Ejemplo de atributo |
|---|---|---|
| **Edición** | Agrupa copias con la misma descripción bibliográfica. | Título, ISBN si corresponde. |
| **Ejemplar** | Se presta individualmente y tiene su propio estado. | Código, condición física. |
| **Socio** | Mantiene identidad a través de varios préstamos. | ID, nombre. |
| **Préstamo** | Conserva quién recibió qué y cuándo. | Fecha de inicio, fecha de devolución. |

Un **hecho** puede merecer un elemento propio si tiene atributos, identidad o historial. En este ejemplo, «Socio tiene Ejemplar» no alcanza: la relación **Préstamo** necesita fechas y puede repetirse en distintos momentos.

---

## 5. Identidad: cómo reconocer el mismo elemento

Dos elementos pueden tener los mismos atributos y ser **distintos**. Dos ejemplares de la misma edición tienen igual título e ISBN, pero códigos de ejemplar diferentes.

```text
Edición: ISBN 978-...; título "Introducción a Java".
Ejemplar E-17: copia física en estante A.
Ejemplar E-42: otra copia de la misma edición en estante B.
```

**Pregunta de identidad:** si cambia el título corregido de una edición o el estado de un ejemplar, ¿sigue siendo el mismo elemento? Si la respuesta es sí, ese atributo no debería ser el único criterio de identidad.

| Tipo de clave | Ventaja | Riesgo a evaluar |
|---|---|---|
| Natural | Ya existe en el negocio y puede ser reconocible. | Puede cambiar, faltar o no ser tan única como se creía. |
| Sustituta | Estable y controlada por el sistema. | No reemplaza las reglas de unicidad del negocio. |

Un ID interno para `Prestamo` no impide por sí mismo duplicar accidentalmente el mismo préstamo activo. La identidad y las **restricciones** resuelven preguntas diferentes.

En Java, conviene distinguir también identidad de igualdad de valores. `record Coordenada(2, 3)` es adecuado como valor; dos préstamos con los mismos campos pueden seguir siendo hechos distintos.

---

## 6. Tipos, unidades y significado de los valores

Elegir un tipo no es solo decidir entre `String` e `int`. Debés registrar **qué significa el dato, en qué unidad está y cuáles son sus valores válidos**.

| Dato | Preguntas necesarias | Representación posible en Java |
|---|---|---|
| Cantidad de ejemplares | ¿Puede ser negativa? ¿Es un conteo entero? | `int` con validación `>= 0`. |
| Fecha de préstamo | ¿Importa la hora o solo el día? ¿Qué zona horaria rige? | `LocalDate` si basta el día. |
| Momento exacto de una operación | ¿Hay que ordenar eventos de distintas zonas? | `Instant`. |
| Dinero | ¿Moneda, escala y redondeo? | `BigDecimal` más una moneda explícita. |
| Código de ejemplar | ¿Preserva ceros iniciales? ¿Admite letras? | `String`, no `int`. |
| Estado | ¿Cuáles son las opciones permitidas? | `enum` si son un conjunto definido. |

```java
record Cantidad(int valor) {
    Cantidad {
        if (valor < 0) throw new IllegalArgumentException("Cantidad negativa");
    }
}
```

El pequeño tipo `Cantidad` hace explícita una regla. En problemas más complejos, `Dinero` o `RangoFechas` pueden impedir mezclas inválidas que un `String` o un número suelto no detectan.

---

## 7. Relaciones, cardinalidad y opcionalidad

Para cada relación, respondé **cuántos** elementos pueden vincularse y **si el vínculo es obligatorio**.

```text
Una Edición -> cero o muchos Ejemplares.
Un Ejemplar -> exactamente una Edición.
Un Socio -> cero o muchos Préstamos a lo largo del tiempo.
Un Préstamo -> exactamente un Socio y exactamente un Ejemplar.
Un Préstamo -> fecha de devolución opcional mientras está abierto.
```

| Relación | Pregunta que evita errores |
|---|---|
| Uno a uno | ¿De verdad nunca habrá más de uno? |
| Uno a muchos | ¿El «muchos» puede ser cero? |
| Muchos a muchos | ¿El vínculo tiene datos propios, como fecha o estado? |

La cardinalidad no sustituye las reglas temporales. Un ejemplar puede tener **muchos préstamos históricos**, pero **a lo sumo uno activo en un instante**. Dibujar solo «Ejemplar 1:N Préstamo» no expresa esa segunda regla; hay que escribirla aparte.

---

## 8. Pertenencia, ciclo de vida y límites de un conjunto

Dos elementos relacionados no siempre tienen el mismo ciclo de vida. Un ejemplar puede existir antes de su primer préstamo y seguir existiendo después del último. Un préstamo, en cambio, no tiene sentido sin socio ni ejemplar.

| Pregunta | Ejemplo |
|---|---|
| ¿Se crea como parte de otro elemento? | Un ejemplar se incorpora a una edición del catálogo. |
| ¿Puede existir por separado? | Un socio existe sin préstamos. |
| ¿Qué ocurre si se elimina o desactiva el elemento padre? | Los préstamos históricos quizá deban conservarse. |
| ¿Quién puede cambiarlo? | La devolución debe modificar el préstamo correcto. |

**Evitar borrados impulsivos:** si desaparece un socio del registro activo, borrar también todos sus préstamos podría destruir historial necesario. En muchos modelos, «desactivar» y «borrar» son operaciones distintas. La regla depende del negocio y de sus obligaciones de conservación de datos.

En código orientado a objetos, puede convenir que un objeto proteja las operaciones de sus partes. En almacenamiento, puede convenir guardar esas partes separadas. **El límite del objeto y la forma de guardarlo no tienen que ser idénticos.**

---

## 9. Restricciones e invariantes de los datos

Un modelo es más que una lista de campos. Debe decir qué combinaciones de valores **son imposibles o inválidas**.

```text
Reglas de la biblioteca:
1. Cada ejemplar tiene un código único.
2. Todo préstamo referencia un socio y un ejemplar existentes.
3. La devolución no puede ser anterior al inicio del préstamo.
4. Un ejemplar no puede tener dos préstamos activos al mismo tiempo.
```

| Regla | Alcance |
|---|---|
| Cantidad positiva de ejemplares adquiridos | Un valor. |
| Fecha de devolución >= fecha de inicio | Un préstamo. |
| Código de ejemplar único | Todos los ejemplares. |
| A lo sumo un préstamo activo por ejemplar | Varios préstamos y operaciones simultáneas. |

Una validación de constructor protege reglas **dentro de un objeto**. Una regla que compara muchos elementos necesita además un mecanismo en el lugar que coordina o almacena esos elementos. **Consultar «¿hay uno activo?» y después insertar no alcanza por sí solo si dos operaciones ocurren al mismo tiempo**. Más adelante, la guía de concurrencia y la de SQL explicarán cómo proteger esa regla durante cambios simultáneos.

---

## 10. Duplicación, dependencia y fuente de verdad

Guardar el mismo hecho en varios lugares puede facilitar lecturas, pero obliga a mantenerlos sincronizados. Primero identificá cuál es la **fuente de verdad**.

```text
Préstamo activo: Ejemplar E-17 -> Socio S-4.
Campo duplicado: Ejemplar.prestado = false.

¿Cuál de los dos datos manda? Si ambos se consideran oficiales,
el sistema puede dar dos respuestas opuestas.
```

Una forma de evitar esa contradicción es **derivar** `prestado` de la existencia de un préstamo activo. Otra es guardar un estado adicional y definir una operación única que mantenga ambos datos coherentes. La segunda opción necesita una razón concreta, por ejemplo una lectura muy frecuente que deba ser rápida.

| Señal de duplicación | Pregunta |
|---|---|
| El nombre del socio aparece en cada préstamo | ¿Debe mostrarse el nombre actual o el nombre histórico del momento? |
| El total se guarda junto a cada línea | ¿Es un cálculo o un valor acordado que debe conservarse? |
| Hay dos campos que expresan «activo» | ¿Cuál es la fuente oficial y cómo se actualizan? |

**No toda repetición es un error.** A veces una copia conserva una fotografía histórica o mejora una consulta. La decisión debe incluir qué pasa cuando cambia el dato original.

---

## 11. Datos derivados y datos que conviene conservar

Un dato **derivado** se calcula a partir de otros. Si puede calcularse siempre con claridad y a un costo aceptable, quizá no haga falta guardarlo.

```java
boolean activo(java.time.LocalDate fechaDevolucion) {
    return fechaDevolucion == null;
}
```

Pero hay datos que parecen calculables y aun así conviene conservar. Ejemplo: el **precio acordado** de un producto en un pedido. Si solo guardás el ID del producto y mañana cambia el precio del catálogo, no podrás reconstruir cuánto se acordó ayer. Ese precio no es simplemente el «precio actual derivado».

| Pregunta | Si la respuesta es sí... |
|---|---|
| ¿Debe reconstruirse exactamente el valor histórico? | Guardá una fotografía o evento suficiente. |
| ¿Cambian las reglas de cálculo con el tiempo? | Guardá versión de regla, resultado o ambos según necesidad. |
| ¿Calcularlo exige un trabajo excesivo en cada lectura? | Evaluá una copia derivada con actualización controlada. |
| ¿Puede obtenerse fácilmente de datos estables? | Preferí derivarlo para evitar contradicciones. |

---

## 12. Tiempo, estados e historial

Los datos cambian. Un modelo que solo guarda el **estado actual** puede perder la respuesta a «¿qué pasó antes?».

```text
Préstamo P-9:
Inicio: 2026-04-02
Devolución: 2026-04-11

El préstamo terminó, pero el hecho histórico sigue existiendo.
```

| Pregunta temporal | Dato necesario |
|---|---|
| ¿Cuándo ocurrió? | Fecha o instante de la operación. |
| ¿Cuándo era válido el dato? | Inicio y fin de vigencia, si importa. |
| ¿Quién lo cambió? | Actor y registro de cambio, si el problema lo exige. |
| ¿Qué valor tenía antes? | Historial o fotografía anterior. |

Una **máquina de estados** describe transiciones permitidas, por ejemplo `ABIERTO -> DEVUELTO`. Si el modelo también admite `PERDIDO`, definí desde qué estados puede llegarse y qué significa para la disponibilidad del ejemplar.

Elegí con cuidado `LocalDate`, `LocalDateTime` e `Instant`. Un día civil de devolución y el instante exacto en que se registró la operación responden preguntas diferentes.

---

## 13. Modelo conceptual, lógico y físico

Estas son **tres vistas del mismo problema**, con distintos niveles de detalle.

| Nivel | Pregunta | Ejemplo |
|---|---|---|
| **Conceptual** | ¿Qué cosas y reglas existen? | Socio toma prestado un ejemplar; no puede haber dos préstamos activos del mismo ejemplar. |
| **Lógico** | ¿Qué datos, identificadores y relaciones se necesitan? | `Prestamo(id, socioId, ejemplarCodigo, fechaInicio, fechaDevolucion)`. |
| **Físico** | ¿Cómo se guardan y consultan en una tecnología concreta? | Tablas e índices SQL, o documentos y referencias. |

```text
Conceptual: Socio -- realiza --> Préstamo -- corresponde a --> Ejemplar
                                                         |
                                                         v
                                                       Edición

Lógico: cada Préstamo guarda su ID, socioId, ejemplarCodigo y fechas.
Físico: se decidirá al elegir motor, esquema, índices y mecanismos de integridad.
```

No saltes al nivel físico porque conocés una anotación de JPA o una estructura de MongoDB. Si el modelo conceptual no distingue edición de ejemplar, ningún mapeo técnico recuperará esa diferencia por sí solo.

---

## 14. Diseñar según las consultas y cambios reales

El modelo debe responder a las **operaciones que importan**, no solo describir sustantivos.

| Operación | Qué necesita el modelo |
|---|---|
| Buscar ejemplares disponibles de una edición | Identidad de edición, ejemplares y regla de disponibilidad. |
| Ver préstamos activos de un socio | Relación préstamo-socio y estado o fecha de devolución. |
| Consultar historial de un ejemplar | Conservar préstamos terminados y sus fechas. |
| Registrar devolución | Identificar el préstamo activo exacto y aplicar la transición. |

Una consulta frecuente puede influir en la representación física, los índices o alguna copia derivada. **No cambies el significado de los datos para hacer cómoda una sola consulta.** Primero mantené un modelo que responda correctamente; después elegí cómo hacerlo eficiente.

Anotá también los **cambios**: crear, corregir, cerrar, desactivar. Un diseño que lee fácil pero no puede actualizarse sin contradicciones está incompleto.

---

## 15. Del modelo a objetos Java y representaciones externas

El mismo concepto puede tener varias formas técnicas:

```text
Concepto: préstamo activo de un ejemplar a un socio.

Objeto de dominio: protege reglas de abrir y devolver.
Representación de API: expone solo datos que necesita el cliente.
Representación persistida: guarda IDs, fechas y restricciones.
```

No tienen que tener **exactamente los mismos campos**. Por ejemplo, la API puede mostrar el nombre del socio, el objeto de dominio puede trabajar con `SocioId`, y el almacenamiento puede conservar una clave que refiere al socio.

```java
record SocioId(String valor) {
    SocioId {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("ID de socio inválido");
        }
    }
}
```

Un tipo como `SocioId` evita confundirlo por accidente con un código de ejemplar, aunque ambos se representen como texto. Esto es una decisión de modelado, no un requisito de una base de datos específica.

**Contrato entre representaciones:** al transformar datos, no pierdas identidad, unidades ni significado. Convertir un importe a `double`, una fecha sin zona a `Instant` sin definir la zona, o un estado desconocido a `PENDIENTE` puede alterar la información aunque el programa compile.

---

## 16. Caso completo: préstamos de una biblioteca

**Consigna:** la biblioteca registra ediciones, ejemplares físicos y socios. Cada préstamo identifica un socio y un ejemplar, comienza en una fecha y puede cerrarse con una devolución. Hay que consultar préstamos activos e historial.

### Paso 1: decisiones del modelo

| Elemento | Identidad | Datos relevantes | Relación |
|---|---|---|---|
| Edición | `edicionId` | Título, ISBN si lo tiene. | Tiene cero o muchos ejemplares. |
| Ejemplar | `ejemplarCodigo` | Condición física. | Pertenece a una edición. |
| Socio | `socioId` | Nombre y estado de inscripción. | Tiene préstamos a lo largo del tiempo. |
| Préstamo | `prestamoId` | Inicio, devolución opcional. | Corresponde a un socio y un ejemplar. |

**Reglas:** la devolución no precede al inicio; un préstamo cerrado no vuelve a cerrarse; no se abre otro préstamo activo del mismo ejemplar. Esta última regla abarca varios préstamos y debe protegerse en el lugar que coordina las altas, además de cualquier validación del objeto individual.

### Paso 2: ejemplo de objeto Java para un préstamo

```java
import java.time.LocalDate;
import java.util.Objects;

final class Prestamo {
    private final String id;
    private final String socioId;
    private final String ejemplarCodigo;
    private final LocalDate inicio;
    private LocalDate devolucion;

    Prestamo(String id, String socioId, String ejemplarCodigo, LocalDate inicio) {
        if (id == null || id.isBlank()
                || socioId == null || socioId.isBlank()
                || ejemplarCodigo == null || ejemplarCodigo.isBlank()) {
            throw new IllegalArgumentException("Los identificadores son obligatorios");
        }
        this.id = id;
        this.socioId = socioId;
        this.ejemplarCodigo = ejemplarCodigo;
        this.inicio = Objects.requireNonNull(inicio, "La fecha de inicio es obligatoria");
    }

    boolean activo() {
        return devolucion == null;
    }

    void devolver(LocalDate fecha) {
        Objects.requireNonNull(fecha, "La fecha de devolución es obligatoria");
        if (!activo()) {
            throw new IllegalStateException("El préstamo ya fue devuelto");
        }
        if (fecha.isBefore(inicio)) {
            throw new IllegalArgumentException("La devolución precede al inicio");
        }
        devolucion = fecha;
    }

    String id() { return id; }
    String socioId() { return socioId; }
    String ejemplarCodigo() { return ejemplarCodigo; }
    LocalDate inicio() { return inicio; }
    LocalDate devolucion() { return devolucion; }
}
```

El método `activo()` **deriva** el estado de `devolucion`; no hay un segundo booleano que pueda contradecirla. El objeto protege sus propias fechas y transición, pero **no puede saber por sí solo si existe otro préstamo activo** del mismo ejemplar en todo el sistema.

### Paso 3: comprobar escenarios

| Escenario | Resultado esperado |
|---|---|
| Un ejemplar nunca prestado | Puede prestarse si no existe otra restricción. |
| Un préstamo activo de `E-17` | No puede abrirse otro activo de `E-17`. |
| Devolución anterior al inicio | Se rechaza. |
| Segunda devolución del mismo préstamo | Se rechaza. |
| Préstamo ya devuelto | Permanece en el historial. |

**Punto de diseño pendiente para la persistencia:** la regla «a lo sumo un préstamo activo por ejemplar» debe mantenerse incluso con dos solicitudes simultáneas. El modelo identifica la regla; una implementación posterior deberá elegir un mecanismo que la garantice.

---

## 17. Errores comunes de modelado

| Diseño que merece revisión | Consecuencia posible | Pregunta para corregirlo |
|---|---|---|
| Confundir edición con ejemplar | No se sabe cuál copia se prestó. | ¿Qué elemento físico cambia de manos? |
| Usar nombre como identificador | Personas o productos distintos pueden compartir nombre. | ¿Qué dato es único y estable? |
| Guardar solo el estado actual | Se pierde el historial. | ¿Qué preguntas habrá sobre el pasado? |
| Duplicar un dato sin fuente de verdad | Dos respuestas incompatibles. | ¿Cuál manda y cómo se sincroniza la copia? |
| Usar `String` para cualquier significado | Se mezclan IDs, unidades y estados. | ¿Qué valores son válidos para este campo? |
| Suponer que `null`, vacío y cero significan lo mismo | Aparecen interpretaciones ambiguas. | ¿Qué representa cada ausencia? |
| Poner todas las reglas en un constructor | No se protegen reglas entre varios elementos. | ¿Qué operación coordina el conjunto? |
| Elegir tablas o documentos antes de definir relaciones | El diseño queda atado a una herramienta. | ¿Cómo describiría el problema sin mencionar tecnología? |

---

## 18. Ejercicios de análisis y diseño

Para cada caso, escribí **elementos, identificadores, relaciones con cardinalidad, reglas, datos derivados y consultas principales**. Después elegí tipos Java para tres atributos importantes.

| Caso | Preguntas para empezar |
|---|---|
| **Turnos médicos** | ¿Qué distingue al profesional, al paciente y al turno? ¿Puede haber turnos superpuestos? |
| **Cursos y estudiantes** | ¿La inscripción es una relación simple o tiene fecha, estado y nota? |
| **Inventario** | ¿Producto y unidad física son el mismo concepto? ¿Qué significa «stock disponible»? |
| **Suscripciones** | ¿Se necesita el plan actual o el plan que tenía el cliente al facturar? |
| **Envíos** | ¿Qué estados y eventos deben quedar en el historial? |

**Desafío:** para un curso con inscripciones, explicá por qué guardar `List<Estudiante>` dentro de `Curso` puede ser suficiente en un ejercicio pequeño, pero una inscripción con **fecha, estado y nota** necesita un hecho propio en el modelo. No dibujes aún tablas: definí primero el significado.

---

## 19. Lista de verificación antes de elegir una base de datos

- [ ] Puedo explicar qué problema representa cada elemento del modelo.
- [ ] Distingo entidades con identidad de valores definidos solo por sus datos.
- [ ] Cada identificador tiene una regla clara de unicidad y estabilidad.
- [ ] Definí tipos, unidades, límites y significado de las ausencias.
- [ ] Escribí la cardinalidad y opcionalidad de cada relación importante.
- [ ] Sé qué hechos deben conservarse históricamente.
- [ ] Identifiqué las restricciones de un valor, de un elemento y de un conjunto.
- [ ] Sé cuál es la fuente de verdad de cada dato duplicado o derivado.
- [ ] Puedo responder las consultas y cambios principales con el modelo.
- [ ] Probé ejemplos normales, límites y escenarios inválidos.
- [ ] Puedo explicar el modelo sin usar palabras como «tabla», «documento» o «anotación».

**Idea central:** modelar datos es decidir **qué hechos son verdaderos y cómo impedir que el sistema represente hechos contradictorios**. La tecnología elegida después debe implementar esas decisiones, no reemplazarlas.
