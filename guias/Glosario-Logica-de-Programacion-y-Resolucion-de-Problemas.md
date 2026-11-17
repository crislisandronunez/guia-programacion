# Glosario y Guía de Lógica de Programación y Resolución de Problemas

Esta guía enseña a **pensar una solución antes de escribir código**. Parte de problemas pequeños, muestra cómo definir entradas, salidas y reglas, y usa **Java** para convertir ese razonamiento en programas. Los conceptos sirven en cualquier lenguaje: Java aparece como herramienta para hacerlos visibles.

---

## Índice

1. [Qué significa resolver un problema con un programa](#1-qué-significa-resolver-un-problema-con-un-programa)
2. [Glosario fundamental](#2-glosario-fundamental)
3. [Traducir un enunciado a entradas, salidas y reglas](#3-traducir-un-enunciado-a-entradas-salidas-y-reglas)
4. [Descomponer un problema grande en pasos pequeños](#4-descomponer-un-problema-grande-en-pasos-pequeños)
5. [Estado, variables y seguimiento de la ejecución](#5-estado-variables-y-seguimiento-de-la-ejecución)
6. [Condiciones: tomar decisiones sin perder casos](#6-condiciones-tomar-decisiones-sin-perder-casos)
7. [Bucles: repetir con una condición de finalización](#7-bucles-repetir-con-una-condición-de-finalización)
8. [Patrones básicos: contador, acumulador, bandera y mejor candidato](#8-patrones-básicos-contador-acumulador-bandera-y-mejor-candidato)
9. [Invariantes: saber qué debe seguir siendo verdadero](#9-invariantes-saber-qué-debe-seguir-siendo-verdadero)
10. [Casos normales, bordes y entradas inválidas](#10-casos-normales-bordes-y-entradas-inválidas)
11. [Del pseudocódigo a Java](#11-del-pseudocódigo-a-java)
12. [Corrección, claridad y costo de una solución](#12-corrección-claridad-y-costo-de-una-solución)
13. [Cómo investigar un resultado incorrecto](#13-cómo-investigar-un-resultado-incorrecto)
14. [Caso completo: resumir ventas diarias](#14-caso-completo-resumir-ventas-diarias)
15. [Ejercicios para practicar el razonamiento](#15-ejercicios-para-practicar-el-razonamiento)
16. [Lista de verificación antes de dar un problema por resuelto](#16-lista-de-verificación-antes-de-dar-un-problema-por-resuelto)

---

## 1. Qué significa resolver un problema con un programa

Un programa recibe información, ejecuta pasos definidos y produce un resultado. **Resolver el problema** consiste en decidir cuáles son esos pasos y demostrar que sirven para todos los casos permitidos, no solo para el primer ejemplo que se nos ocurrió.

```text
Problema: de una lista de edades, contar cuántas personas son mayores de edad.

Entrada: [17, 18, 25, 16]
Regla: una persona es mayor de edad si edad >= 18.
Salida: 2

Pasos:
1. Empezar el contador en 0.
2. Revisar cada edad una vez.
3. Si cumple la regla, aumentar el contador.
4. Devolver el contador.
```

**Algoritmo** es la secuencia finita y precisa de pasos. **Código** es una expresión de ese algoritmo en un lenguaje. Una idea equivocada no se arregla cambiando la sintaxis de Java.

Antes de programar, explicá la solución en una o dos frases. Si todavía no podés hacerlo, probablemente falta entender alguna regla del problema.

---

## 2. Glosario fundamental

| Término | Significado | Ejemplo |
|---|---|---|
| **Entrada (*input*)** | Información que recibe la solución. | Un arreglo de edades. |
| **Salida (*output*)** | Resultado que entrega. | Cantidad de mayores de edad. |
| **Algoritmo** | Pasos precisos y finitos que transforman entradas en salidas. | Recorrer, comparar y contar. |
| **Precondición** | Condición que debe cumplir una entrada para que el algoritmo tenga sentido. | El arreglo no puede ser `null`. |
| **Postcondición** | Lo que debe ser verdadero cuando el algoritmo termina. | El resultado coincide con la cantidad de edades `>= 18`. |
| **Estado** | Valores actuales de las variables en un momento de la ejecución. | `indice = 2`, `contador = 1`. |
| **Rama** | Camino elegido según una condición. | Entrar al `if` cuando la edad es `>= 18`. |
| **Iteración** | Una repetición del cuerpo de un bucle. | Revisar la tercera edad. |
| **Caso borde (*edge case*)** | Entrada válida cerca de un límite importante. | Edad exactamente `18`; arreglo vacío. |
| **Contraejemplo** | Entrada que demuestra que una solución supuestamente correcta falla. | Usar `> 18` falla para edad `18`. |
| **Invariante** | Propiedad que se mantiene verdadera durante la ejecución. | Tras revisar `i` edades, el contador refleja las válidas entre esas `i`. |
| **Pseudocódigo** | Descripción estructurada del algoritmo sin depender de una sintaxis concreta. | `para cada edad: si edad >= 18, sumar 1`. |
| **Traza** | Registro manual de cómo cambia el estado paso a paso. | Tabla de índice, edad y contador. |

**Pregunta útil:** ¿estoy discutiendo el problema, el algoritmo o la sintaxis? Separar esas tres cosas evita quedar trabado en una línea de Java cuando todavía no está clara la regla.

---

## 3. Traducir un enunciado a entradas, salidas y reglas

Tomemos: *«Dada una lista de notas, informar cuántas están aprobadas»*. Antes de escribir un `for`, faltan decisiones.

| Pregunta | Posible respuesta para este ejemplo |
|---|---|
| ¿Qué entra? | Un arreglo de enteros entre `0` y `10`. |
| ¿Qué sale? | Un entero entre `0` y la longitud del arreglo. |
| ¿Desde qué nota se aprueba? | `6`, incluido. |
| ¿Qué pasa si está vacío? | El resultado es `0`. |
| ¿Qué pasa con `null`? | Se rechaza la entrada. |
| ¿Qué pasa con una nota `11`? | Se rechaza la entrada completa. |

Estas decisiones forman un **contrato**. No hay una única política universal para `null` o datos inválidos; hay que elegirla y comunicarla. Una especificación breve podría ser:

```text
Precondición: notas != null y cada nota está entre 0 y 10.
Postcondición: resultado = cantidad de elementos de notas cuyo valor es >= 6.
```

Si el enunciado dice *«buscar una nota aprobada»*, preguntá si necesita la **primera posición**, la **cantidad** o simplemente un **sí/no**. Las tres salidas requieren decisiones distintas.

---

## 4. Descomponer un problema grande en pasos pequeños

Cuando una consigna parece difícil, separá **reglas**, **recorrido** y **resultado**. Ejemplo: *«De una lista de pedidos, sumar los importes válidos y contar los rechazados»*.

```text
1. Definir qué significa "importe válido".
2. Definir qué hacer con un pedido inválido: ¿ignorar, contar, rechazar todo?
3. Preparar total = 0 y rechazados = 0.
4. Revisar cada pedido.
5. Actualizar uno de los resultados según la regla.
6. Entregar total y rechazados.
```

Si un paso sigue siendo confuso, descomponelo otra vez. Por ejemplo, *«validar pedido»* puede significar comprobar que existe, que tiene importe y que ese importe es positivo. Esa descomposición identifica decisiones que, si quedan implícitas, se convierten en errores.

**Método práctico:** escribí un ejemplo pequeño y calculá la respuesta a mano. Después anotá qué operaciones hiciste para llegar a ella. Recién entonces elegí las estructuras de Java.

---

## 5. Estado, variables y seguimiento de la ejecución

Una variable tiene un valor **en un momento determinado**. En un bucle, ese valor puede cambiar varias veces. Para entender el algoritmo, seguí esos cambios.

```java
int[] edades = {17, 18, 25, 16};
int mayores = 0;

for (int edad : edades) {
    if (edad >= 18) {
        mayores++;
    }
}
// mayores == 2
```

| Edad que se acaba de revisar | ¿Cumple `>= 18`? | `mayores` después de revisarla |
|---:|---|---:|
| Antes de empezar | — | 0 |
| 17 | No | 0 |
| 18 | Sí | 1 |
| 25 | Sí | 2 |
| 16 | No | 2 |

Esta tabla es una **traza**. Es especialmente útil si el resultado es incorrecto: buscá la primera fila donde el estado se aparta de lo esperado. El problema suele estar en la condición, el valor inicial o el momento de la actualización.

---

## 6. Condiciones: tomar decisiones sin perder casos

Una condición debe separar los casos según la **regla del problema**, incluidos sus límites. Si se aprueba con `6`:

```java
boolean aprobada = nota >= 6; // 6 pertenece al grupo de aprobadas
```

Usar `nota > 6` cambia la regla: el `6` queda afuera. Probar `5`, `6` y `7` permite descubrirlo enseguida.

| Situación | Estructura útil | Pregunta de control |
|---|---|---|
| Dos caminos excluyentes | `if / else` | ¿Todos los casos caen en uno de los dos? |
| Varios rangos excluyentes | `if / else if / else` | ¿Los rangos se solapan o dejan huecos? |
| Rechazo de entrada inválida | validación al inicio | ¿Se puede continuar con esta entrada? |

```java
static String clasificarNota(int nota) {
    if (nota < 0 || nota > 10) {
        throw new IllegalArgumentException("La nota debe estar entre 0 y 10");
    }
    if (nota >= 8) {
        return "destacada";
    } else if (nota >= 6) {
        return "aprobada";
    } else {
        return "desaprobada";
    }
}
```

El orden importa: si primero comprobás `nota >= 6`, una nota `9` nunca llega a la rama de *destacada*. Para comprobar una decisión, armá una tabla con un valor **debajo**, **en** y **encima** de cada límite.

---

## 7. Bucles: repetir con una condición de finalización

Un bucle necesita tres ideas: **dónde empieza**, **qué cambia en cada repetición** y **cuándo termina**.

```java
int[] valores = {4, 7, 2};
int suma = 0;

for (int i = 0; i < valores.length; i++) {
    suma += valores[i];
}
// suma == 13
```

El índice comienza en `0`, aumenta de a uno y se detiene **antes** de `valores.length`. Para un arreglo de longitud `3`, las posiciones válidas son `0`, `1` y `2`.

| Forma | Cuándo conviene |
|---|---|
| `for (int i = 0; i < n; i++)` | Necesitás la posición o un rango de posiciones. |
| `for (int valor : valores)` | Solo necesitás cada valor. |
| `while (condicion)` | No sabés de antemano cuántas repeticiones habrá. |

En un `while`, verificá que algo acerque el estado a la salida:

```java
int restantes = 3;
while (restantes > 0) {
    restantes--; // progresa hacia 0
}
```

Si `restantes` no cambiara, la condición seguiría siendo verdadera y el bucle no terminaría.

---

## 8. Patrones básicos: contador, acumulador, bandera y mejor candidato

Muchos problemas iniciales son variaciones de cuatro patrones. La clave es reconocer **qué representa cada variable**.

| Patrón | Valor inicial típico | Actualización | Pregunta que responde |
|---|---:|---|---|
| **Contador** | `0` | `cantidad++` | ¿Cuántos cumplen la regla? |
| **Acumulador** | `0` | `total += valor` | ¿Cuánto suman? |
| **Bandera** | `false` | `encontrado = true` | ¿Existe al menos uno? |
| **Mejor candidato** | Primer valor válido | Reemplazar si aparece uno mejor | ¿Cuál es el mayor, menor o más adecuado? |

```java
static int contarPares(int[] numeros) {
    int cantidad = 0;
    for (int numero : numeros) {
        if (numero % 2 == 0) {
            cantidad++;
        }
    }
    return cantidad;
}

static boolean contieneNegativo(int[] numeros) {
    for (int numero : numeros) {
        if (numero < 0) {
            return true; // ya conocemos la respuesta
        }
    }
    return false;
}
```

**Error frecuente con máximos:** empezar en `0` cuando todos los valores permitidos pueden ser negativos. Para `{-8, -3}`, el mayor es `-3`, no `0`. Una opción es usar el primer elemento, después de decidir qué hacer con el arreglo vacío.

```java
static int mayor(int[] numeros) {
    if (numeros == null || numeros.length == 0) {
        throw new IllegalArgumentException("Se necesita al menos un número");
    }
    int mejor = numeros[0];
    for (int i = 1; i < numeros.length; i++) {
        if (numeros[i] > mejor) {
            mejor = numeros[i];
        }
    }
    return mejor;
}
```

---

## 9. Invariantes: saber qué debe seguir siendo verdadero

Una **invariante de bucle** describe el significado de una variable después de cada repetición. Permite explicar por qué el algoritmo funciona, además de observar que funcionó con algunos ejemplos.

Para `contarPares`, la invariante es:

> Después de revisar los primeros `i` números, `cantidad` es exactamente la cantidad de pares entre esos `i` números.

La comprobación tiene tres partes:

1. **Inicio:** antes de revisar números, `i = 0` y `cantidad = 0`; la afirmación es verdadera.
2. **Mantenimiento:** al revisar el siguiente número, se suma uno si es par; así la afirmación sigue siendo verdadera.
3. **Final:** al terminar, se revisaron todos los números; por lo tanto, `cantidad` es la respuesta pedida.

Una invariante también detecta errores. Si aumentás el contador para **todos** los números, ya no representa la cantidad de pares después de cada paso.

No hace falta escribir una demostración formal para cada método. Sí conviene poder completar la frase: *«Después de procesar los primeros elementos, esta variable representa...»*.

---

## 10. Casos normales, bordes y entradas inválidas

Probar un solo ejemplo grande suele ocultar errores. Elegí casos por **tipo de comportamiento**.

| Tipo | Ejemplo para `contarPares` | Resultado esperado |
|---|---|---:|
| Entrada vacía | `{}` | `0` |
| Un elemento que cumple | `{2}` | `1` |
| Un elemento que no cumple | `{3}` | `0` |
| Mezcla | `{2, 3, 4}` | `2` |
| Cero | `{0}` | `1`, porque `0 % 2 == 0` |
| Negativos | `{-2, -3}` | `1` |

La entrada `null` no es un arreglo vacío. En Java, recorrer `null` provoca una excepción. Decidí si el método **rechaza** esa entrada o si el contrato permite tratarla de otra manera. No mezcles políticas según el caso.

Para rangos, probá los valores alrededor del límite. Para búsquedas, probá elemento al principio, al final y ausente. Para agregaciones, probá vacío, un elemento y varios elementos.

---

## 11. Del pseudocódigo a Java

El pseudocódigo deja ver la lógica sin distraerse con signos, tipos o nombres de métodos.

```text
función contarAprobadas(notas):
    si notas no existe: informar entrada inválida
    cantidad = 0
    para cada nota:
        si nota está fuera de 0..10: informar entrada inválida
        si nota >= 6: cantidad = cantidad + 1
    devolver cantidad
```

La traducción conserva **el mismo orden de decisiones**:

```java
static int contarAprobadas(int[] notas) {
    if (notas == null) {
        throw new IllegalArgumentException("Las notas no pueden ser null");
    }

    int cantidad = 0;
    for (int nota : notas) {
        if (nota < 0 || nota > 10) {
            throw new IllegalArgumentException("Cada nota debe estar entre 0 y 10");
        }
        if (nota >= 6) {
            cantidad++;
        }
    }
    return cantidad;
}
```

Una vez que el método funciona, podés mejorar nombres o extraer funciones. Si cambiás la lógica mientras todavía estás traduciendo, se vuelve más difícil saber dónde nació un error.

---

## 12. Corrección, claridad y costo de una solución

Una solución útil debe cumplir el contrato, poder entenderse y tener un costo adecuado. El orden para aprender es: **primero corrección**, después claridad, y luego medir si hace falta optimizar.

```java
// Recorre una vez: la cantidad de pasos crece con la cantidad de notas.
static int contarAprobadas(int[] notas) {
    int cantidad = 0;
    for (int nota : notas) {
        if (nota >= 6) cantidad++;
    }
    return cantidad;
}
```

Si hay `n` notas, el recorrido hace aproximadamente `n` comprobaciones: costo **lineal, `O(n)`**. El análisis completo de estructuras de datos y complejidad corresponde a la guía de algoritmos; acá lo importante es reconocer si se revisa cada elemento una vez, varias veces o nunca.

El método abreviado del ejemplo supone que las notas ya fueron validadas. Si recibe datos externos, usá el contrato y la validación de la sección anterior. Una solución rápida pero que cuenta notas imposibles puede ser incorrecta para el problema real.

---

## 13. Cómo investigar un resultado incorrecto

Cuando algo falla, evitá cambiar código al azar. Convertí el fallo en un **contraejemplo pequeño**.

```text
Regla: se aprueba desde 6 inclusive.
Resultado incorrecto: contarAprobadas([6]) devuelve 0.
Resultado esperado: 1.
Hipótesis: la condición usa > 6 en vez de >= 6.
Comprobación: seguir la única iteración y observar la condición.
```

**Secuencia de diagnóstico:**

1. Anotá la entrada concreta, la salida obtenida y la esperada.
2. Reducí la entrada hasta conservar el error con el menor ejemplo posible.
3. Trazá las variables que intervienen en la respuesta.
4. Identificá la primera decisión que difiere de la regla.
5. Corregí esa causa y repetí el caso fallido junto con casos vecinos.

Si la traza manual es confusa, usá un depurador y avanzá una línea por vez. Lo importante es observar **qué condición se evaluó y qué valor tenía cada variable**, no solo leer la línea donde se produjo la salida final.

---

## 14. Caso completo: resumir ventas diarias

**Problema:** recibir los importes enteros de ventas diarias. Un importe negativo representa un dato inválido y debe rechazarse. Se pide el total, la cantidad de ventas y la venta mayor. Un importe `0` es válido. Si no hay ventas, el total y la cantidad son `0`, y no existe una venta mayor.

### Paso 1: contrato y ejemplos

| Entrada | Salida esperada |
|---|---|
| `{120, 0, 80}` | Total `200`, cantidad `3`, mayor `120` |
| `{}` | Total `0`, cantidad `0`, mayor ausente |
| `{0}` | Total `0`, cantidad `1`, mayor `0` |
| `{120, -1}` | Entrada rechazada |
| `null` | Entrada rechazada |

### Paso 2: pseudocódigo

```text
si el arreglo no existe: rechazar
total = 0
cantidad = 0
mayor = ausente
para cada importe:
    si importe < 0: rechazar
    sumar importe al total
    aumentar cantidad
    si mayor está ausente o importe > mayor: actualizar mayor
devolver los tres resultados
```

### Paso 3: Java

```java
import java.util.OptionalInt;

public class ResumenDeVentas {
    public record Resumen(long total, int cantidad, OptionalInt mayor) {}

    public static Resumen resumir(int[] importes) {
        if (importes == null) {
            throw new IllegalArgumentException("Los importes no pueden ser null");
        }

        long total = 0;
        int cantidad = 0;
        OptionalInt mayor = OptionalInt.empty();

        for (int importe : importes) {
            if (importe < 0) {
                throw new IllegalArgumentException("Los importes no pueden ser negativos");
            }
            total += importe;
            cantidad++;
            if (mayor.isEmpty() || importe > mayor.getAsInt()) {
                mayor = OptionalInt.of(importe);
            }
        }
        return new Resumen(total, cantidad, mayor);
    }
}
```

`OptionalInt.empty()` representa que **no hay una venta mayor** cuando el arreglo está vacío; no inventamos un valor como `0` que podría confundirse con una venta real. `long` para el total permite sumar varios importes `int` sin desbordar tan pronto como lo haría otro `int`. Para dinero real se necesita definir moneda y precisión; este ejemplo usa enteros para concentrarse en la lógica.

**Invariante:** después de procesar los primeros `i` importes válidos, `cantidad == i`, `total` es su suma y `mayor` contiene el máximo de esos importes, o está ausente si `i == 0`.

**Decisión sobre errores:** si aparece un negativo, el método lanza una excepción y no devuelve un resumen parcial. Esta política está expresada en el contrato. Otra consigna podría pedir ignorar los negativos; sería un algoritmo distinto.

---

## 15. Ejercicios para practicar el razonamiento

Para cada ejercicio, escribí **entrada, salida, reglas, dos casos borde y pseudocódigo** antes de Java. Después hacé una traza de al menos un caso.

| Ejercicio | Consigna | Casos para comprobar |
|---|---|---|
| **Contar positivos** | Contar los números estrictamente mayores que cero. | `{}` → `0`; `{0, -1, 2}` → `1`. |
| **Primera coincidencia** | Devolver la primera posición de un valor, o `-1` si no aparece. | `{4, 7, 4}`, buscar `4` → `0`; buscar `9` → `-1`. |
| **Suma hasta un límite** | Sumar desde `1` hasta `n`, inclusive; rechazar `n < 0`. | `n = 0` → `0`; `n = 4` → `10`. |
| **Mayor sin inventar valor** | Encontrar el mayor de un arreglo no vacío; rechazar uno vacío. | `{-8, -3}` → `-3`; `{5}` → `5`. |
| **Rango de notas** | Contar notas entre `6` y `8`, ambos límites incluidos. | `{5, 6, 8, 9}` → `2`. |
| **Todos cumplen** | Informar si todos los importes son no negativos. | `{0, 3}` → `true`; `{2, -1}` → `false`; `{}` → `true` por definición de «todos». |

**Desafío de razonamiento:** para *primera coincidencia*, explicá por qué el algoritmo puede terminar apenas encuentra el valor. Para *todos cumplen*, explicá por qué un solo contraejemplo basta para devolver `false`.

---

## 16. Lista de verificación antes de dar un problema por resuelto

- [ ] Puedo explicar la regla y la salida sin mencionar Java.
- [ ] Sé qué entradas son válidas y qué ocurre con las inválidas.
- [ ] Tengo al menos un ejemplo normal y casos en cada límite importante.
- [ ] Cada variable tiene un significado claro y un valor inicial justificado.
- [ ] Cada bucle progresa y termina.
- [ ] Puedo trazar un ejemplo pequeño paso a paso.
- [ ] Puedo decir qué representan las variables después de cada iteración.
- [ ] La respuesta cumple el contrato para vacío, un elemento y varios elementos.
- [ ] Si apareció un fallo, guardé un contraejemplo pequeño que lo reproduce.

**Idea central:** programar no empieza al escribir `public static`. Empieza cuando entendés qué debe pasar con cada entrada y podés explicar por qué tus pasos producen la respuesta correcta.
