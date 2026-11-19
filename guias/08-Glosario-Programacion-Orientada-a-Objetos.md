# Glosario y Guía de Programación Orientada a Objetos

Esta guía explica **cómo pensar con objetos antes de elegir clases o escribir `extends`**. Presenta identidad, estado, comportamiento, encapsulación, abstracción, composición, herencia y polimorfismo como ideas de diseño. Los ejemplos usan **Java**, pero las preguntas de diseño sirven en otros lenguajes orientados a objetos.

---

## Índice

1. [Qué problema intenta resolver la orientación a objetos](#1-qué-problema-intenta-resolver-la-orientación-a-objetos)
2. [Glosario fundamental](#2-glosario-fundamental)
3. [Clase, objeto e identidad](#3-clase-objeto-e-identidad)
4. [Estado, comportamiento y responsabilidades](#4-estado-comportamiento-y-responsabilidades)
5. [Encapsulación: proteger las reglas del objeto](#5-encapsulación-proteger-las-reglas-del-objeto)
6. [Construcción de objetos válidos](#6-construcción-de-objetos-válidos)
7. [Referencias, alias y cambios visibles desde afuera](#7-referencias-alias-y-cambios-visibles-desde-afuera)
8. [Abstracción: mostrar lo importante y ocultar detalles](#8-abstracción-mostrar-lo-importante-y-ocultar-detalles)
9. [Composición: construir comportamiento con otros objetos](#9-composición-construir-comportamiento-con-otros-objetos)
10. [Herencia: cuándo una especialización puede sustituir a otra](#10-herencia-cuándo-una-especialización-puede-sustituir-a-otra)
11. [Polimorfismo: un contrato, varios comportamientos](#11-polimorfismo-un-contrato-varios-comportamientos)
12. [Igualdad, identidad y objetos de valor](#12-igualdad-identidad-y-objetos-de-valor)
13. [Mutabilidad e inmutabilidad](#13-mutabilidad-e-inmutabilidad)
14. [Caso completo: modelar un pedido](#14-caso-completo-modelar-un-pedido)
15. [Errores comunes de modelado](#15-errores-comunes-de-modelado)
16. [Ejercicios de razonamiento y diseño](#16-ejercicios-de-razonamiento-y-diseño)
17. [Lista de verificación para diseñar un objeto](#17-lista-de-verificación-para-diseñar-un-objeto)

---

## 1. Qué problema intenta resolver la orientación a objetos

En un programa pequeño puede bastar con variables y funciones. Cuando crecen los datos y las reglas, necesitás saber **quién es responsable de mantener cada regla**. La programación orientada a objetos (POO) organiza el programa en objetos que tienen datos y ofrecen operaciones sobre ellos.

```text
Problema: administrar pedidos.

Datos: líneas del pedido, estado, identificador.
Reglas: un pedido confirmado no admite nuevas líneas;
        un pedido vacío no puede confirmarse.

Pregunta de diseño: ¿qué parte del programa garantiza esas reglas?
Respuesta posible: el propio objeto Pedido, mediante agregarLinea() y confirmar().
```

La idea importante no es «convertir cada sustantivo en una clase». Es **agrupar datos con las operaciones que preservan sus reglas** y repartir responsabilidades de forma comprensible. POO tampoco elimina funciones, algoritmos o estructuras de datos: los objetos usan todo eso internamente.

---

## 2. Glosario fundamental

| Término | Significado | Ejemplo |
|---|---|---|
| **Clase** | Definición de un tipo de objetos: datos, operaciones y reglas. | `class Pedido`. |
| **Objeto / instancia** | Elemento concreto creado a partir de una clase. | Un pedido con ID `42`. |
| **Estado** | Valores que posee un objeto en un momento. | Líneas y estado actual del pedido. |
| **Comportamiento** | Operaciones que ofrece y reglas que ejecuta. | `agregarLinea()`, `confirmar()`. |
| **Identidad** | Propiedad de ser un objeto específico aunque cambie su estado. | El pedido `42` sigue siendo el mismo tras confirmarse. |
| **Referencia** | Valor con el que el programa accede a un objeto. | `Pedido pedido = ...`. |
| **Encapsulación** | Control de cómo se observa y modifica el estado. | No permitir modificar directamente las líneas. |
| **Invariante** | Regla que debe seguir siendo verdadera para todo estado válido. | Un pedido confirmado tiene al menos una línea. |
| **Abstracción** | Modelo que expone lo necesario para usar algo y deja fuera detalles irrelevantes. | `confirmar()` expresa una intención; el cliente no manipula el estado a mano. |
| **Composición** | Un objeto contiene o utiliza otros para cumplir su responsabilidad. | `Pedido` contiene líneas. |
| **Herencia** | Relación de especialización entre clases. | `VehiculoElectrico extends Vehiculo`, si cumple el contrato del padre. |
| **Polimorfismo** | Una operación invocada mediante un tipo común se comporta según el objeto concreto. | `notificador.enviar(...)` puede enviar correo o SMS. |
| **Interfaz** | Tipo que declara operaciones que sus implementaciones se comprometen a ofrecer. | `interface Notificador`. |
| **Objeto de valor** | Objeto definido por sus datos, sin identidad propia relevante. | Una coordenada `(x, y)` o un importe con moneda. |
| **Entidad** | Objeto cuya identidad importa a través del tiempo. | Un pedido identificado por su ID. |
| **Mutabilidad** | Posibilidad de cambiar el estado de un objeto después de crearlo. | `pedido.confirmar()` cambia su estado. |

En Java, **`private`, `extends` e `implements` son mecanismos**. Entender por qué y cuándo usarlos requiere pensar primero en estas ideas.

---

## 3. Clase, objeto e identidad

Una clase define qué pueden tener y hacer sus instancias. Dos objetos de la misma clase pueden tener estados distintos.

```java
final class Contador {
    private int valor;

    void incrementar() {
        valor++;
    }

    int valor() {
        return valor;
    }
}

Contador uno = new Contador();
Contador dos = new Contador();
uno.incrementar();
// uno.valor() == 1; dos.valor() == 0
```

`uno` y `dos` son **objetos distintos**. Cambiar uno no cambia el otro, aunque ambos se hayan creado con la misma clase. El nombre de una variable tampoco es el objeto: es una manera de referirse a él.

Un objeto puede conservar su identidad y cambiar de estado. Un pedido pendiente y ese mismo pedido confirmado no son dos pedidos distintos; son dos momentos del mismo pedido.

---

## 4. Estado, comportamiento y responsabilidades

Para proponer una clase, preguntá:

1. ¿Qué información necesita conservar?
2. ¿Qué operaciones tienen sentido sobre esa información?
3. ¿Qué reglas debe proteger cada operación?
4. ¿Qué decisiones pertenecen a otro objeto?

| Objeto | Estado | Comportamiento razonable | Regla que protege |
|---|---|---|---|
| `Cuenta` | Saldo | `depositar`, `retirar` | No aceptar un importe inválido. |
| `Pedido` | Líneas, estado | `agregarLinea`, `confirmar` | No confirmar vacío. |
| `RangoFechas` | Inicio, fin | `contiene(fecha)` | Inicio no posterior al fin. |

Una clase con solo datos públicos y setters para todo puede dejar las reglas dispersas entre sus usuarios. Una clase que calcula impuestos, envía correos y dibuja pantallas reúne responsabilidades sin relación. **La responsabilidad se descubre mirando las reglas y los cambios posibles**, no contando métodos.

---

## 5. Encapsulación: proteger las reglas del objeto

Encapsular no significa únicamente escribir `private` y generar un getter y un setter por cada campo. Significa que **los cambios importantes pasan por operaciones que hacen cumplir las reglas**.

```java
final class Cuenta {
    private long saldoCentavos;

    Cuenta(long saldoInicialCentavos) {
        if (saldoInicialCentavos < 0) {
            throw new IllegalArgumentException("El saldo inicial no puede ser negativo");
        }
        this.saldoCentavos = saldoInicialCentavos;
    }

    long saldoCentavos() {
        return saldoCentavos;
    }

    void retirar(long centavos) {
        if (centavos <= 0 || centavos > saldoCentavos) {
            throw new IllegalArgumentException("Retiro inválido o sin fondos");
        }
        saldoCentavos -= centavos;
    }
}
```

La operación `retirar` expresa una intención y conserva la regla `saldoCentavos >= 0`. Un setter público `setSaldoCentavos(-500)` permitiría saltarse esa regla. La representación en centavos evita usar `double` para este ejemplo; los sistemas de dinero reales requieren además definir moneda, límites y políticas de cálculo.

**Pregunta clave:** si alguien cambia este campo desde afuera, ¿puede dejar al objeto en un estado imposible? Si la respuesta es sí, no alcanza con ocultar el campo y agregar un setter sin validación.

---

## 6. Construcción de objetos válidos

Un constructor debe establecer un estado inicial coherente. Si un objeto nace inválido y «más tarde alguien lo completa», cada método debe adivinar si ya está listo para usarse.

```java
final class RangoFechas {
    private final java.time.LocalDate inicio;
    private final java.time.LocalDate fin;

    RangoFechas(java.time.LocalDate inicio, java.time.LocalDate fin) {
        if (inicio == null || fin == null || inicio.isAfter(fin)) {
            throw new IllegalArgumentException("Rango de fechas inválido");
        }
        this.inicio = inicio;
        this.fin = fin;
    }

    boolean contiene(java.time.LocalDate fecha) {
        if (fecha == null) return false;
        return !fecha.isBefore(inicio) && !fecha.isAfter(fin);
    }
}
```

La invariante es `inicio <= fin`. La validación del constructor la establece y, como los campos son `final`, ningún método puede reemplazarlos. `final` impide reasignar esas referencias; cuando un campo apunta a un objeto mutable, **no convierte automáticamente a ese objeto en inmutable**.

---

## 7. Referencias, alias y cambios visibles desde afuera

Dos variables pueden apuntar **al mismo objeto**. Eso se llama *aliasing*. Un cambio hecho a través de una referencia se observa a través de la otra.

```java
Cuenta principal = new Cuenta(1_000);
Cuenta alias = principal;
alias.retirar(200);
// principal.saldoCentavos() == 800
// principal == alias es true: ambas referencias apuntan al mismo objeto.
```

Una colección mutable también puede exponer el interior de un objeto. El siguiente diseño no protege sus líneas:

```java
// Diseño problemático:
List<String> lineas() { return lineasInternas; }
// Quien llama puede hacer pedido.lineas().clear().
```

Una opción es devolver una copia que el cliente no pueda modificar:

```java
List<String> lineas() { return List.copyOf(lineasInternas); }
```

También hay que cuidar las colecciones recibidas en el constructor: si se guarda directamente una lista ajena, quien la creó puede modificarla después. **La regla general es controlar tanto la entrada como la salida de estado mutable**.

---

## 8. Abstracción: mostrar lo importante y ocultar detalles

Abstraer es elegir **qué necesita saber el usuario de un objeto**. Si un pedido ofrece `confirmar()`, quien lo usa no debería tener que conocer qué campo representa el estado ni en qué orden actualizar varios campos.

```text
Operación del negocio: pedido.confirmar()
Detalles internos posibles: validar líneas, comprobar estado actual,
                           guardar momento de confirmación, cambiar estado.
```

Una buena abstracción habla en términos del problema. `confirmar()` expresa mejor la intención que `setEstado(CONFIRMADO)`, porque puede proteger todas las condiciones necesarias para confirmar.

En Java, una `interface` o una clase abstracta puede representar una abstracción, pero **también puede hacerlo una clase concreta con métodos claros**. No hace falta crear una interfaz para cada clase. La guía de interfaces y contratos profundiza en cuándo conviene separar contrato e implementación.

---

## 9. Composición: construir comportamiento con otros objetos

En composición, un objeto utiliza otros objetos para cumplir su trabajo. La relación suele leerse como **«tiene un»** o **«usa un»**.

```java
record LineaPedido(String producto, int cantidad) {
    LineaPedido {
        if (producto == null || producto.isBlank() || cantidad <= 0) {
            throw new IllegalArgumentException("Línea inválida");
        }
    }
}

final class Pedido {
    private final java.util.List<LineaPedido> lineas = new java.util.ArrayList<>();

    void agregar(LineaPedido linea) {
        if (linea == null) throw new IllegalArgumentException("Falta la línea");
        lineas.add(linea);
    }
}
```

Un `Pedido` **tiene** líneas. No *es una* línea. Composición también permite cambiar una capacidad sin crear una jerarquía de subclases: un servicio puede **usar** un `CalculadorDescuento` recibido en su constructor.

| Pregunta | Pista |
|---|---|
| ¿El objeto contiene una parte? | Considerá composición. |
| ¿Solo necesita colaborar con otro objeto? | Guardar una referencia puede ser suficiente. |
| ¿Necesita elegir entre varios comportamientos? | Una dependencia con un tipo común puede permitir polimorfismo. |

---

## 10. Herencia: cuándo una especialización puede sustituir a otra

Herencia expresa una relación **«es un tipo de»**. En Java se usa `extends`. La pregunta decisiva es: **¿podría usar la subclase en cualquier lugar donde se espera la clase padre sin romper sus promesas?**

```java
abstract class Figura {
    abstract double area();
}

final class Circulo extends Figura {
    private final double radio;

    Circulo(double radio) {
        if (radio <= 0) throw new IllegalArgumentException("Radio inválido");
        this.radio = radio;
    }

    @Override
    double area() {
        return Math.PI * radio * radio;
    }
}
```

Un `Circulo` puede responder a la operación prometida por `Figura`: calcular un área. En cambio, que dos clases compartan tres campos no basta para afirmar una relación de herencia. Crear una clase padre solo para reutilizar código puede forzar comportamientos que algunas subclases no pueden cumplir.

**Advertencia conceptual:** «un cuadrado es un rectángulo» es cierto en geometría, pero una clase `Cuadrado` puede no sustituir a una clase `Rectangulo` **mutable** que promete permitir cambiar ancho y alto de forma independiente. El contrato de software, y no solo la clasificación del mundo real, decide si la herencia sirve.

---

## 11. Polimorfismo: un contrato, varios comportamientos

Polimorfismo permite que el código cliente trabaje con un tipo común mientras el objeto concreto decide cómo ejecutar la operación.

```java
interface CostoEnvio {
    long calcular(long subtotalCentavos);
}

final class EnvioFijo implements CostoEnvio {
    @Override
    public long calcular(long subtotalCentavos) {
        return 1_500;
    }
}

final class EnvioGratisDesdeDiezMil implements CostoEnvio {
    @Override
    public long calcular(long subtotalCentavos) {
        return subtotalCentavos >= 10_000 ? 0 : 1_500;
    }
}

static long totalConEnvio(long subtotalCentavos, CostoEnvio costoEnvio) {
    if (subtotalCentavos < 0 || costoEnvio == null) {
        throw new IllegalArgumentException("Entrada inválida");
    }
    return Math.addExact(subtotalCentavos, costoEnvio.calcular(subtotalCentavos));
}
```

`totalConEnvio` invoca `calcular` sin preguntar qué clase concreta recibió. La implementación debe respetar la **promesa** del contrato: devolver el costo en centavos para un subtotal no negativo. Una implementación que devolviera un costo negativo o ignorara el subtotal cuando su política promete usarlo rompería esa promesa aunque el compilador acepte `implements`.

**No confundas:** sobrecarga (*overload*) significa mismo nombre con distintos parámetros; sobrescritura (*override*) significa que un subtipo implementa de otra manera una operación heredada. El polimorfismo del ejemplo usa sobrescritura.

---

## 12. Igualdad, identidad y objetos de valor

Hay dos preguntas diferentes:

1. **¿Son el mismo objeto en memoria?** En Java, para referencias, se consulta con `==`.
2. **¿Representan el mismo valor según el dominio?** Se consulta con `equals`, si el tipo lo define correctamente.

```java
record Coordenada(int x, int y) {}

Coordenada a = new Coordenada(2, 5);
Coordenada b = new Coordenada(2, 5);
// a == b       -> false: son dos objetos distintos.
// a.equals(b)  -> true: tienen los mismos componentes.
```

Una coordenada suele ser un **objeto de valor**: `(2, 5)` equivale a otro `(2, 5)`. Un pedido, en cambio, suele tener **identidad**: dos pedidos con las mismas líneas pueden seguir siendo pedidos distintos.

**Cuidado:** la igualdad de una entidad puede depender de un identificador que aún no existe antes de guardarla. Elegirla requiere definir qué significa «el mismo pedido» durante todo su ciclo de vida. No copies una implementación de `equals` sin decidir esa regla. Si redefinís `equals`, redefiní también `hashCode` de forma consistente, especialmente si usarás `HashSet` o claves de `HashMap`.

---

## 13. Mutabilidad e inmutabilidad

Un objeto **mutable** puede cambiar después de crearse; uno **inmutable** conserva su valor. Ninguna opción sirve para todo.

| Caso | Diseño habitual | Motivo |
|---|---|---|
| Coordenada o rango de fechas | Inmutable | Un valor ya definido no debería cambiar inesperadamente. |
| Pedido en curso | Mutable con operaciones controladas | Debe agregar líneas y pasar de estado. |
| Resultado de un cálculo | Inmutable | Representa una respuesta terminada. |

```java
record Punto(int x, int y) {
    Punto moverDerecha(int unidades) {
        return new Punto(x + unidades, y); // crea otro valor
    }
}
```

Un `record` simplifica objetos de valor pequeños, pero **no garantiza inmutabilidad profunda**. Si uno de sus componentes es una lista mutable, alguien podría modificar el contenido de esa lista. Las copias defensivas siguen siendo importantes.

Para un objeto mutable, definí **transiciones válidas**. Por ejemplo: `PENDIENTE -> CONFIRMADO`. Una transición `CONFIRMADO -> PENDIENTE` necesita una regla explícita; no debería ocurrir por un setter público accidental.

---

## 14. Caso completo: modelar un pedido

**Consigna:** un pedido empieza pendiente, admite líneas con cantidad positiva y puede confirmarse si tiene al menos una. Una vez confirmado, no admite más líneas ni otra confirmación. Queremos poder consultar sus líneas sin que un cliente las modifique.

### Paso 1: identificar responsabilidades

| Objeto | Responsabilidad |
|---|---|
| `LineaPedido` | Representar un producto y una cantidad válidos. |
| `Pedido` | Mantener líneas y estado; controlar las operaciones permitidas. |

**Invariantes:** todas las líneas son válidas; si el pedido está confirmado, tiene al menos una línea. Solo `Pedido` decide cuándo cambia su estado.

### Paso 2: implementación en Java

```java
import java.util.ArrayList;
import java.util.List;

record LineaPedido(String producto, int cantidad) {
    LineaPedido {
        if (producto == null || producto.isBlank() || cantidad <= 0) {
            throw new IllegalArgumentException("Producto y cantidad deben ser válidos");
        }
    }
}

final class Pedido {
    enum Estado { PENDIENTE, CONFIRMADO }

    private final List<LineaPedido> lineas = new ArrayList<>();
    private Estado estado = Estado.PENDIENTE;

    void agregarLinea(LineaPedido linea) {
        if (estado != Estado.PENDIENTE) {
            throw new IllegalStateException("El pedido ya está confirmado");
        }
        if (linea == null) {
            throw new IllegalArgumentException("La línea no puede ser null");
        }
        lineas.add(linea);
    }

    void confirmar() {
        if (estado != Estado.PENDIENTE) {
            throw new IllegalStateException("El pedido ya está confirmado");
        }
        if (lineas.isEmpty()) {
            throw new IllegalStateException("No se puede confirmar un pedido vacío");
        }
        estado = Estado.CONFIRMADO;
    }

    Estado estado() {
        return estado;
    }

    List<LineaPedido> lineas() {
        return List.copyOf(lineas);
    }
}
```

### Paso 3: comprobar las reglas

```java
Pedido pedido = new Pedido();
pedido.agregarLinea(new LineaPedido("Cuaderno", 2));
pedido.confirmar();
// pedido.estado() == Pedido.Estado.CONFIRMADO
// pedido.lineas().size() == 1
// pedido.agregarLinea(new LineaPedido("Lápiz", 1)) lanza IllegalStateException.
```

El ejemplo usa **encapsulación** para proteger el estado, **composición** porque el pedido contiene líneas y una **transición explícita** en `confirmar()`. No necesita herencia ni interfaz: agregar mecanismos sin una necesidad concreta haría más difícil ver las reglas principales.

---

## 15. Errores comunes de modelado

| Diseño que merece revisión | Qué puede salir mal | Pregunta para corregirlo |
|---|---|---|
| Un setter para cada campo | Se pueden saltar las reglas del objeto. | ¿Qué operación del problema debería producir este cambio? |
| Devolver una lista mutable interna | El cliente cambia el estado sin pasar por métodos del objeto. | ¿Necesita una vista, una copia o una operación específica? |
| Heredar solo para reutilizar campos | El subtipo puede incumplir lo que promete el padre. | ¿Realmente puede sustituirlo en todos sus usos? |
| Crear una interfaz por cada clase | Agrega nombres y archivos sin expresar una necesidad. | ¿Hay varias implementaciones o una frontera que separar? |
| Poner toda la lógica en una clase `Utilidades` | Las reglas quedan lejos de los datos que deben proteger. | ¿Qué objeto es responsable de esa regla? |
| Hacer que todo sea objeto mutable | Aparecen cambios inesperados y alias difíciles de seguir. | ¿Este dato representa un valor que puede ser inmutable? |
| Usar `==` para comparar valores de objetos | Se confunde identidad con igualdad de contenido. | ¿Quiero saber si es la misma instancia o el mismo valor? |

Un error de diseño se reconoce por sus consecuencias: reglas que pueden romperse, cambios que afectan muchas partes, o código que cuesta explicar. No por la presencia aislada de una palabra clave.

---

## 16. Ejercicios de razonamiento y diseño

Antes de programar, escribí para cada ejercicio **estado, operaciones, invariantes y casos inválidos**.

| Ejercicio | Consigna | Pregunta central |
|---|---|---|
| **Semáforo** | Modelar rojo, amarillo y verde con transiciones permitidas. | ¿Quién impide una transición inválida? |
| **Carrito** | Agregar productos con cantidades positivas y calcular cantidad total. | ¿Qué puede modificar quien recibe la lista de ítems? |
| **Rango de precios** | Crear un rango con mínimo y máximo. | ¿Puede nacer con mínimo mayor que máximo? |
| **Cliente y dirección** | Decidir si una dirección pertenece al cliente o es un valor compartible. | ¿Importa la identidad de la dirección? |
| **Canales de aviso** | Permitir dos formas de enviar el mismo mensaje. | ¿Qué promete exactamente el tipo común? |
| **Pedido confirmado** | Intentar agregar una línea después de confirmar. | ¿Cómo expresa el objeto que la operación no está permitida? |

**Desafío:** diseñá dos pedidos con las mismas líneas. Explicá por qué pueden tener contenido igual y, aun así, ser pedidos distintos. Después compará dos coordenadas `(3, 4)` creadas por separado: ¿por qué suele tener sentido tratarlas como el mismo valor?

---

## 17. Lista de verificación para diseñar un objeto

- [ ] Puedo explicar qué representa este objeto en el problema.
- [ ] Sé si importa su identidad o solo sus valores.
- [ ] Sus operaciones expresan intenciones, no cambios arbitrarios de campos.
- [ ] El constructor establece un estado válido.
- [ ] Cada operación conserva las invariantes.
- [ ] El estado mutable interno no se puede modificar por accidente desde afuera.
- [ ] Si uso herencia, el subtipo cumple las promesas del tipo padre.
- [ ] Si uso una interfaz, sé qué contrato común expresa y por qué la necesito.
- [ ] Puedo justificar qué objetos contiene o utiliza.
- [ ] Probé los estados iniciales, transiciones válidas y operaciones inválidas.

**Idea central:** una clase útil no es una bolsa de campos. Es un límite claro alrededor de una responsabilidad y de las reglas que deben permanecer verdaderas.
