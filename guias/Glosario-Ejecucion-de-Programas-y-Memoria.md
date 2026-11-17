# Glosario y Guía de Ejecución de Programas y Memoria

Esta guía explica **qué ocurre cuando un programa se ejecuta y cómo pensar sus datos en memoria**. Comienza con variables, llamadas y objetos; después aborda referencias, copias, mutabilidad, recolección de basura y errores frecuentes. Los ejemplos usan **Java**, pero el objetivo principal es construir un modelo mental que ayude a leer y depurar programas.

---

## Índice

1. [Del código fuente a un programa en ejecución](#1-del-código-fuente-a-un-programa-en-ejecución)
2. [Glosario fundamental](#2-glosario-fundamental)
3. [Variables, alcance y tiempo de vida](#3-variables-alcance-y-tiempo-de-vida)
4. [Llamadas a métodos y pila de ejecución](#4-llamadas-a-métodos-y-pila-de-ejecución)
5. [Valores primitivos, referencias y objetos](#5-valores-primitivos-referencias-y-objetos)
6. [Asignación y alias: dos nombres para un mismo objeto](#6-asignación-y-alias-dos-nombres-para-un-mismo-objeto)
7. [Paso de parámetros: Java pasa valores](#7-paso-de-parámetros-java-pasa-valores)
8. [Objetos alcanzables y recolección de basura](#8-objetos-alcanzables-y-recolección-de-basura)
9. [Arreglos, listas y copias superficiales](#9-arreglos-listas-y-copias-superficiales)
10. [Identidad, igualdad y cadenas de texto](#10-identidad-igualdad-y-cadenas-de-texto)
11. [Estado de instancia, estado estático y efectos compartidos](#11-estado-de-instancia-estado-estático-y-efectos-compartidos)
12. [Null, ausencia y errores de referencia](#12-null-ausencia-y-errores-de-referencia)
13. [Inmutabilidad y copias defensivas](#13-inmutabilidad-y-copias-defensivas)
14. [Recursión y crecimiento de la pila](#14-recursión-y-crecimiento-de-la-pila)
15. [Memoria retenida, recursos externos y errores comunes](#15-memoria-retenida-recursos-externos-y-errores-comunes)
16. [Caso completo: un catálogo que entrega datos seguros](#16-caso-completo-un-catálogo-que-entrega-datos-seguros)
17. [Ejercicios de trazado y predicción](#17-ejercicios-de-trazado-y-predicción)
18. [Lista de verificación para razonar sobre memoria](#18-lista-de-verificación-para-razonar-sobre-memoria)

---

## 1. Del código fuente a un programa en ejecución

El archivo `.java` contiene **código fuente**. El compilador `javac` lo transforma en archivos `.class` con **bytecode**. La **JVM** carga las clases y ejecuta ese bytecode; durante la ejecución puede interpretar instrucciones y compilar partes usadas frecuentemente a código máquina mediante **JIT** (*Just-In-Time*).

```text
Archivo .java -> compilador javac -> archivo .class (bytecode)
                                      |
                                      v
                           JVM carga y ejecuta clases
                                      |
                                      v
                           resultados y efectos del programa
```

| Elemento | Función |
|---|---|
| **JDK** | Herramientas para desarrollar, incluido `javac`, más lo necesario para ejecutar. |
| **JVM** | Entorno que ejecuta bytecode y administra aspectos como carga de clases y memoria. |
| **Proceso** | Programa en ejecución con recursos propios del sistema operativo. |
| **Hilo** | Flujo de instrucciones dentro de un proceso. Un proceso puede tener varios hilos. |

**Distinción importante:** compilar verifica muchas reglas de tipos y sintaxis, pero no demuestra que la lógica sea correcta. Un programa puede compilar y devolver un resultado equivocado. También puede fallar durante la ejecución, por ejemplo al acceder a `null`.

---

## 2. Glosario fundamental

| Término | Significado | Ejemplo |
|---|---|---|
| **Variable** | Nombre asociado a un valor en un punto del programa. | `int cantidad = 3`. |
| **Valor primitivo** | Valor de un tipo primitivo de Java. | `int`, `boolean`, `double`. |
| **Referencia** | Valor que permite acceder a un objeto. | La variable `cliente` que refiere a un `Cliente`. |
| **Objeto** | Instancia con identidad y estado, creada normalmente con `new`. | `new ArrayList<>()`. |
| **Campo** | Variable que pertenece a una clase o instancia. | `pedido.estado`. |
| **Variable local** | Variable declarada dentro de un método o bloque. | `int total` dentro de `calcular()`. |
| **Alcance (*scope*)** | Parte del código en la que un nombre puede usarse. | Una variable de un `if` no existe fuera de ese bloque. |
| **Marco de llamada (*stack frame*)** | Información necesaria para ejecutar una invocación de método. | Parámetros, variables locales y punto de retorno. |
| **Pila de ejecución (*call stack*)** | Secuencia de llamadas activas de un hilo. | `main -> procesar -> calcular`. |
| **Heap** | Área de memoria gestionada por la JVM donde se alojan normalmente los objetos. | Una instancia de `ArrayList`. |
| **Alias** | Dos referencias que apuntan al mismo objeto. | `Lista b = a`. |
| **Mutación** | Cambio del estado de un objeto existente. | `lista.add("A")`. |
| **Alcanzabilidad** | Posibilidad de llegar a un objeto desde referencias activas. | Una lista guardada en un campo estático sigue alcanzable. |
| **Garbage collector (GC)** | Mecanismo que recupera memoria de objetos que ya no son alcanzables. | Una lista temporal sin referencias puede ser recolectada. |
| **Efecto secundario** | Cambio observable fuera del valor devuelto por un método. | Modificar una lista recibida como parámetro. |

**Modelo mental útil:** dibujá las variables como cajas que contienen valores. Si el valor es una referencia, dibujá una flecha hacia el objeto. Es un modelo para razonar, no una descripción exacta de cada optimización que puede hacer la JVM.

---

## 3. Variables, alcance y tiempo de vida

El **alcance** indica dónde se puede usar un nombre en el código. El **tiempo de vida** indica durante cuánto tiempo su valor participa en la ejecución. No son lo mismo.

```java
static int duplicarSiEsPositivo(int numero) {
    if (numero > 0) {
        int doble = numero * 2;
        return doble;
    }
    // Aquí "doble" ya no está en alcance.
    return numero;
}
```

`numero` es un parámetro de la llamada. `doble` es local al bloque del `if`. En cambio, un **campo de instancia** forma parte del estado de un objeto y puede seguir disponible entre llamadas mientras ese objeto sea alcanzable.

```java
final class Contador {
    private int valor; // estado de cada instancia

    int incrementar() {
        int nuevo = valor + 1; // variable local de esta llamada
        valor = nuevo;
        return valor;
    }
}
```

Cada invocación tiene sus propias variables locales. El campo `valor` pertenece al objeto y conserva el cambio después de que `incrementar()` termina.

---

## 4. Llamadas a métodos y pila de ejecución

Cuando un método llama a otro, el programa necesita recordar a dónde volver y qué valores locales usa cada llamada. Pensamos cada invocación como un **marco** en la pila del hilo.

```java
static int sumarUno(int x) {
    int resultado = x + 1;
    return resultado;
}

static int calcular() {
    int base = 4;
    return sumarUno(base) * 2;
}
// calcular() devuelve 10.
```

```text
Mientras sumarUno(4) está ejecutándose:

Pila de llamadas del hilo
┌──────────────────────────────┐
│ sumarUno: x=4, resultado=5   │  <- llamada actual
├──────────────────────────────┤
│ calcular: base=4             │  <- espera la respuesta
└──────────────────────────────┘
```

Al retornar `sumarUno`, su marco deja de estar activo y `calcular` continúa con el valor devuelto. **No concluyas que todo objeto referido por una variable local se destruye al salir del método**: el objeto puede seguir alcanzable por otra referencia o por el valor de retorno.

Las especificaciones y las JVM permiten optimizaciones internas. La distinción pila de llamadas / objetos en heap es un buen modelo didáctico, pero no predice la ubicación física de cada dato en todas las ejecuciones.

---

## 5. Valores primitivos, referencias y objetos

Una variable primitiva contiene su **valor**. Una variable de tipo objeto contiene una **referencia**, que puede apuntar a un objeto o ser `null`.

```java
int edad = 20;
int otraEdad = edad;
otraEdad = 21;
// edad sigue siendo 20.

StringBuilder nombre = new StringBuilder("Ana");
StringBuilder otroNombre = nombre;
otroNombre.append(" María");
// nombre.toString() ahora es "Ana María".
```

En la primera parte se copió el número `20`. En la segunda se copió **la referencia**, de modo que `nombre` y `otroNombre` acceden al mismo `StringBuilder`.

| Operación | ¿Qué cambia? |
|---|---|
| `otraEdad = 21` | El valor de esa variable, sin afectar `edad`. |
| `otroNombre = new StringBuilder("Luis")` | La referencia guardada en `otroNombre`; `nombre` sigue apuntando al objeto anterior. |
| `otroNombre.append("!")` | El estado del objeto al que `otroNombre` apunta en ese momento. |

---

## 6. Asignación y alias: dos nombres para un mismo objeto

La asignación de una referencia **no duplica el objeto**.

```java
java.util.List<String> original = new java.util.ArrayList<>();
original.add("A");

java.util.List<String> alias = original;
alias.add("B");
// original y alias muestran ["A", "B"].
// original == alias es true.
```

```text
original ----┐
             ├----> objeto ArrayList ["A", "B"]
alias -------┘
```

Si después ejecutás `alias = new ArrayList<>()`, solo cambia la flecha de `alias`. La lista original no se vacía. Esta diferencia entre **reasignar una variable** y **mutar un objeto** explica muchos resultados sorprendentes.

---

## 7. Paso de parámetros: Java pasa valores

Java pasa los parámetros **por valor**. Para un primitivo, copia su número o booleano. Para un objeto, copia el **valor de la referencia**. El método puede modificar el objeto compartido, pero no puede reemplazar la variable del llamador.

```java
static void aumentarNumero(int numero) {
    numero++;
}

static void agregarYReasignar(java.util.List<String> recibida) {
    recibida.add("B");
    recibida = new java.util.ArrayList<>();
    recibida.add("C");
}

int n = 5;
aumentarNumero(n);
// n == 5.

java.util.List<String> letras = new java.util.ArrayList<>();
letras.add("A");
agregarYReasignar(letras);
// letras contiene ["A", "B"], no "C".
```

**Traza del segundo método:**

| Momento | `letras` del llamador | `recibida` del método |
|---|---|---|
| Al entrar | Lista 1: `[A]` | Referencia copiada a Lista 1 |
| Tras `add("B")` | Lista 1: `[A, B]` | Sigue en Lista 1 |
| Tras `new ArrayList<>()` | Sigue en Lista 1 | Ahora apunta a Lista 2 |
| Tras `add("C")` | Lista 1: `[A, B]` | Lista 2: `[C]` |

Decir «los objetos se pasan por referencia» confunde este comportamiento: **lo que se pasa es una copia de la referencia**.

---

## 8. Objetos alcanzables y recolección de basura

La recolección de basura depende de si un objeto sigue **alcanzable**, no de si la variable local que lo creó ya salió de alcance.

```java
static java.util.List<String> crearLista() {
    java.util.List<String> temporal = new java.util.ArrayList<>();
    temporal.add("dato");
    return temporal;
}

java.util.List<String> resultado = crearLista();
// La llamada terminó, pero resultado aún permite llegar a la lista.
```

En cambio, si ninguna referencia activa permite llegar a un objeto, este **puede** ser recolectado. No sabemos exactamente cuándo ocurrirá: la JVM decide el momento. Llamar a `System.gc()` tampoco garantiza que una recolección específica suceda inmediatamente.

```text
Referencias activas -> objetos -> otros objetos referidos por sus campos

Si existe un camino desde una referencia activa hasta un objeto,
ese objeto sigue alcanzable.
```

El GC administra **memoria de objetos**, no equivale a cerrar archivos, conexiones o sockets. Esos recursos tienen sus propias reglas de liberación.

---

## 9. Arreglos, listas y copias superficiales

Copiar un arreglo o una lista puede crear un contenedor nuevo sin duplicar los objetos que contiene. Eso es una **copia superficial**.

```java
final class Etiqueta {
    String texto;
    Etiqueta(String texto) { this.texto = texto; }
}

java.util.List<Etiqueta> original = new java.util.ArrayList<>();
original.add(new Etiqueta("nuevo"));

java.util.List<Etiqueta> copia = new java.util.ArrayList<>(original);
copia.add(new Etiqueta("extra"));
// original tiene 1 elemento; copia tiene 2.

copia.get(0).texto = "usado";
// original.get(0).texto también es "usado": comparten esa Etiqueta.
```

```text
original ----> Lista A ----┐
                          ├----> Etiqueta("usado")
copia ------> Lista B -----┘
```

`Arrays.copyOf`, `new ArrayList<>(otraLista)` y `List.copyOf` crean contenedores independientes, pero **no hacen copias profundas de cada elemento**. `List.copyOf` impide modificar el contenedor resultante; si un elemento es mutable, su estado todavía puede cambiar.

Para una copia profunda, debés crear también objetos nuevos para los elementos mutables que querés aislar. No existe una receta universal: depende del modelo y de qué partes pueden cambiar.

---

## 10. Identidad, igualdad y cadenas de texto

Para referencias, `==` pregunta si ambas apuntan al **mismo objeto**. `equals` pregunta por la **igualdad definida por ese tipo**.

```java
String a = new String("hola");
String b = new String("hola");
// a == b es false: son objetos distintos.
// a.equals(b) es true: tienen el mismo texto.
```

Los literales de `String` pueden compartir instancias mediante el *string pool*. Por eso algunas comparaciones de cadenas con `==` parecen funcionar, pero **no es una forma correcta de comparar su contenido**. Usá `equals` cuando la pregunta sea «¿tienen el mismo texto?».

```java
String texto = "hola";
texto.concat(" mundo");
// texto sigue siendo "hola": String es inmutable.
texto = texto.concat(" mundo");
// Ahora texto refiere a la cadena "hola mundo".
```

El `String` anterior no se modificó; cambió la referencia guardada en `texto`. Esta diferencia es la misma que vimos con asignación y mutación.

---

## 11. Estado de instancia, estado estático y efectos compartidos

Un **campo de instancia** pertenece a cada objeto. Un campo **`static`** pertenece a la clase y es compartido por sus instancias dentro de esa JVM.

```java
final class Sesion {
    private static int sesionesCreadas;
    private final int numero;

    Sesion() {
        sesionesCreadas++;
        numero = sesionesCreadas;
    }

    int numero() { return numero; }
    static int totalCreadas() { return sesionesCreadas; }
}
```

Dos `Sesion` tienen distinto `numero`, pero ven el mismo `totalCreadas()`. **Este contador sirve para ilustrar el alcance de `static`; no es seguro si varios hilos construyen sesiones al mismo tiempo.** Ese problema corresponde a concurrencia.

Un método puede producir un **efecto secundario** aunque devuelva un valor correcto: modificar un campo, una lista recibida o un archivo. Preguntá siempre: *«Además del valor devuelto, ¿qué cambió?»*.

---

## 12. Null, ausencia y errores de referencia

`null` significa que una variable de tipo referencia **no apunta a un objeto**. Intentar invocar un método por esa referencia causa `NullPointerException`.

```java
String nombre = null;
// nombre.length() -> NullPointerException.

if (nombre != null) {
    System.out.println(nombre.length());
}
```

No se trata de «memoria vacía» dentro de un objeto: **no hay un objeto al cual acceder mediante esa referencia**. `null` tampoco es lo mismo que una cadena vacía `""`, una lista vacía o el número `0`.

| Valor | Significado posible |
|---|---|
| `null` | Ausencia de referencia; puede indicar falta de dato o un error de diseño. |
| `""` | Hay una cadena cuyo contenido tiene longitud cero. |
| `List.of()` | Hay una lista sin elementos. |
| `0` | Hay un valor numérico igual a cero. |

Elegí una política clara: validar y rechazar `null` cuando el dato es obligatorio, o representar explícitamente una ausencia permitida. Agregar `if (x != null)` en todas partes sin definir el contrato solo esconde la pregunta importante: **¿debería faltar este dato?**

---

## 13. Inmutabilidad y copias defensivas

Un objeto es **inmutable** si su estado observable no cambia después de crearse. Esto facilita razonar sobre referencias compartidas: todos pueden leer el mismo valor sin sorprenderse por una mutación posterior.

```java
record Punto(int x, int y) {
    Punto moverDerecha(int unidades) {
        return new Punto(x + unidades, y);
    }
}

Punto inicial = new Punto(2, 3);
Punto siguiente = inicial.moverDerecha(1);
// inicial sigue siendo (2, 3); siguiente es (3, 3).
```

Un `record` con componentes mutables **no es profundamente inmutable**. Una forma de proteger una lista de `String` es copiarla al construir el valor:

```java
record Etiquetas(java.util.List<String> valores) {
    Etiquetas {
        valores = java.util.List.copyOf(valores);
    }
}
```

En este caso `String` es inmutable, así que la copia del contenedor alcanza para este objetivo. Si los elementos fueran objetos mutables, habría que decidir cómo copiarlos o evitar exponerlos.

---

## 14. Recursión y crecimiento de la pila

Una función recursiva se llama a sí misma. Cada llamada agrega trabajo pendiente a la pila hasta que se alcanza un **caso base**.

```java
static int sumarHasta(int n) {
    if (n < 0) throw new IllegalArgumentException("n debe ser no negativo");
    if (n == 0) return 0; // caso base
    return n + sumarHasta(n - 1);
}
// sumarHasta(3) = 3 + sumarHasta(2) = 3 + 2 + 1 + 0 = 6.
```

```text
sumarHasta(3) espera a sumarHasta(2)
sumarHasta(2) espera a sumarHasta(1)
sumarHasta(1) espera a sumarHasta(0)
sumarHasta(0) devuelve 0; luego retornan las llamadas anteriores.
```

Si nunca se llega al caso base, o la profundidad es demasiado grande, puede aparecer `StackOverflowError`. En Java **no conviene asumir que la JVM eliminará llamadas recursivas de cola**. Para recorridos potencialmente enormes, un bucle o una pila explícita puede ser más seguro.

---

## 15. Memoria retenida, recursos externos y errores comunes

El GC recupera memoria de objetos inalcanzables, pero una aplicación puede retener objetos que ya no necesita. No es necesario que exista una «fuga» al estilo de memoria sin liberar manualmente: basta con que queden **referencias activas por accidente**.

```java
static final java.util.List<byte[]> HISTORIAL = new java.util.ArrayList<>();

static void guardarParaSiempre(byte[] datos) {
    HISTORIAL.add(datos); // cada elemento sigue alcanzable desde el campo static.
}
```

Si el historial crece sin límite, aumenta el uso de memoria y puede llegar a un `OutOfMemoryError`. El GC no puede eliminar esos arreglos mientras el programa los siga referenciando.

| Síntoma | Pregunta útil | Causa posible |
|---|---|---|
| `StackOverflowError` | ¿La recursión termina y tiene profundidad acotada? | Demasiadas llamadas anidadas. |
| `OutOfMemoryError` | ¿Qué objetos siguen alcanzables y por qué? | Colección sin límite, carga masiva u otros consumos de memoria. |
| `NullPointerException` | ¿Qué referencia era `null`? | Contrato de entrada o inicialización incorrecta. |
| Un cambio aparece en dos lugares | ¿Son dos referencias al mismo objeto? | Alias o copia superficial. |

**Recursos externos:** un archivo abierto, conexión de base de datos o socket requiere un cierre adecuado. En Java, `try-with-resources` llama a `close()` aunque ocurra una excepción:

```java
try (java.io.BufferedReader lector = java.nio.file.Files.newBufferedReader(ruta)) {
    String primeraLinea = lector.readLine();
    System.out.println(primeraLinea);
}
```

`ruta` representa un `Path` disponible en el contexto donde se use este fragmento. El ejemplo muestra el cierre del recurso; **no esperes al GC para cerrar archivos**.

---

## 16. Caso completo: un catálogo que entrega datos seguros

**Problema:** un catálogo recibe nombres de productos y permite consultarlos. Quien construyó la lista original no debe poder cambiar el catálogo después. Quien consulta el catálogo tampoco debe poder modificarlo.

### Diseño problemático

```java
final class CatalogoInseguro {
    private final java.util.List<String> nombres;

    CatalogoInseguro(java.util.List<String> nombres) {
        this.nombres = nombres; // conserva la lista ajena.
    }

    java.util.List<String> nombres() {
        return nombres; // entrega su lista interna.
    }
}
```

```java
java.util.List<String> origen = new java.util.ArrayList<>();
origen.add("Cuaderno");
CatalogoInseguro catalogo = new CatalogoInseguro(origen);
origen.add("Lápiz");           // cambia el catálogo desde afuera.
catalogo.nombres().clear();    // también lo cambia desde la consulta.
```

### Diseño con copia defensiva

```java
import java.util.List;

final class Catalogo {
    private final List<String> nombres;

    Catalogo(List<String> nombres) {
        if (nombres == null) {
            throw new IllegalArgumentException("Los nombres no pueden ser null");
        }
        this.nombres = List.copyOf(nombres);
    }

    List<String> nombres() {
        return nombres;
    }
}
```

`List.copyOf` crea una lista no modificable y rechaza elementos `null`. Como los elementos son `String` inmutables, compartir esas cadenas no permite cambiar el catálogo. Si fueran objetos mutables, **copiar solo la lista no bastaría**.

**Traza conceptual:**

| Momento | Lista del llamador | Lista del catálogo |
|---|---|---|
| Antes de construir | `[Cuaderno]` | No existe. |
| Después de construir | `[Cuaderno]` | Copia no modificable `[Cuaderno]`. |
| Tras `origen.add("Lápiz")` | `[Cuaderno, Lápiz]` | Sigue `[Cuaderno]`. |

**Regla protegida:** el catálogo representa una fotografía de los nombres recibidos al crearlo. El constructor y el método de consulta mantienen esa promesa.

---

## 17. Ejercicios de trazado y predicción

Antes de ejecutar cada fragmento, dibujá variables, flechas y objetos. Después explicá el resultado.

| Ejercicio | Situación | Pregunta |
|---|---|---|
| **Primitivos** | `int a=3; int b=a; b=8;` | ¿Cuánto vale `a`? **3**. |
| **Alias** | `List<String> b=a; b.add("X");` | ¿Aparece `"X"` en `a`? **Sí**, si `a` es una lista mutable. |
| **Reasignación** | Un método recibe una lista y asigna `parametro = new ArrayList<>()`. | ¿Cambia la variable del llamador? **No**. |
| **Copia superficial** | Copiás una lista de objetos mutables y modificás un objeto de la copia. | ¿Puede cambiar lo observado desde la original? **Sí**. |
| **Cadenas** | `String a="sol"; a.concat("es");` | ¿Qué contiene `a`? **`"sol"`**. |
| **Alcanzabilidad** | Un método devuelve una lista creada localmente. | ¿Puede usarse después de que el método terminó? **Sí**, mediante el valor retornado. |

**Desafío:** modificá mentalmente el caso del catálogo para que use `List<Etiqueta>` donde `Etiqueta` tiene un campo `texto` mutable. Explicá por qué `List.copyOf` no protegería el texto y proponé una manera de representar cada etiqueta como valor inmutable.

---

## 18. Lista de verificación para razonar sobre memoria

- [ ] Distingo código fuente, bytecode, JVM y programa en ejecución.
- [ ] Sé si una variable contiene un primitivo o una referencia.
- [ ] Puedo distinguir reasignar una variable de mutar un objeto.
- [ ] Puedo dibujar cuándo dos variables apuntan al mismo objeto.
- [ ] Recuerdo que Java copia el valor de cada parámetro, incluidas las referencias.
- [ ] Sé que una copia de lista no necesariamente copia sus elementos.
- [ ] Elijo `==` para identidad y `equals` para igualdad de contenido cuando corresponda.
- [ ] No confundo `null`, vacío y cero.
- [ ] Sé si un objeto sigue alcanzable antes de esperar que el GC libere memoria.
- [ ] Cierro recursos externos explícitamente, sin depender del GC.
- [ ] Si uso recursión, identifico el caso base y cómo se acerca cada llamada a él.

**Idea central:** para entender un resultado inesperado, seguí los **valores**, las **referencias** y las **mutaciones** paso a paso. Ese razonamiento es más útil que memorizar frases como «los objetos viven en el heap» sin poder explicar qué objeto está mirando cada variable.
