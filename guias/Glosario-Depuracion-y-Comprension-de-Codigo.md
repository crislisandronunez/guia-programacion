# Glosario y Guía de Depuración y Comprensión de Código

Esta guía enseña a **entender qué hace un programa y encontrar por qué se comporta de otra manera**. Presenta un método para reproducir, observar y explicar fallos antes de cambiarlos. Los conceptos sirven en cualquier lenguaje; los ejemplos y las herramientas se muestran con **Java**.

---

## Índice

1. [Qué es depurar y qué significa comprender un programa](#1-qué-es-depurar-y-qué-significa-comprender-un-programa)
2. [Glosario fundamental](#2-glosario-fundamental)
3. [Cómo leer código desconocido sin perderse](#3-cómo-leer-código-desconocido-sin-perderse)
4. [Resultado esperado, resultado observado y contrato](#4-resultado-esperado-resultado-observado-y-contrato)
5. [Reproducir un fallo con datos concretos](#5-reproducir-un-fallo-con-datos-concretos)
6. [Reducir el problema hasta encontrar un contraejemplo pequeño](#6-reducir-el-problema-hasta-encontrar-un-contraejemplo-pequeño)
7. [Formular hipótesis y hacer experimentos](#7-formular-hipótesis-y-hacer-experimentos)
8. [Trazar el flujo y el estado paso a paso](#8-trazar-el-flujo-y-el-estado-paso-a-paso)
9. [Leer excepciones y stack traces en Java](#9-leer-excepciones-y-stack-traces-en-java)
10. [Usar puntos de interrupción y el depurador](#10-usar-puntos-de-interrupción-y-el-depurador)
11. [Usar registros de ejecución sin agregar ruido](#11-usar-registros-de-ejecución-sin-agregar-ruido)
12. [Patrones frecuentes de fallos de lógica](#12-patrones-frecuentes-de-fallos-de-lógica)
13. [Encontrar la primera frontera donde cambia el resultado](#13-encontrar-la-primera-frontera-donde-cambia-el-resultado)
14. [Corregir la causa y comprobar que no regresa](#14-corregir-la-causa-y-comprobar-que-no-regresa)
15. [Caso completo: un promedio que parece correcto](#15-caso-completo-un-promedio-que-parece-correcto)
16. [Ejercicios de diagnóstico](#16-ejercicios-de-diagnóstico)
17. [Lista de verificación para investigar un fallo](#17-lista-de-verificación-para-investigar-un-fallo)

---

## 1. Qué es depurar y qué significa comprender un programa

**Comprender código** es poder explicar qué entradas recibe, qué caminos puede seguir, qué estado cambia y qué resultado produce. **Depurar** es investigar una diferencia entre lo que debería pasar y lo que realmente pasa hasta encontrar su causa.

```text
Regla: una nota de 6 aprueba.
Observación: el programa dice que 6 desaprueba.
Pregunta de depuración: ¿en qué decisión se separó la ejecución de la regla?
```

Un programa puede **compilar, arrancar y no lanzar ninguna excepción**, pero seguir siendo incorrecto. También puede fallar por una entrada inválida, un estado inesperado o una dependencia externa. El trabajo comienza al describir el comportamiento con precisión.

**Meta del diagnóstico:** poder contar una cadena causal: *«Con esta entrada, la condición evaluó `false`; por eso no aumentó el contador y la salida fue menor que la esperada»*. Cambiar una línea sin poder explicar esa cadena deja abierta la posibilidad de haber ocultado el síntoma.

---

## 2. Glosario fundamental

| Término | Significado | Ejemplo |
|---|---|---|
| **Fallo (*failure*)** | Comportamiento observable distinto del esperado. | La respuesta dice `0` cuando debería decir `1`. |
| **Defecto (*bug*)** | Error en código, diseño o configuración que puede causar un fallo. | Usar `> 6` cuando la regla exige `>= 6`. |
| **Síntoma** | Señal visible del problema. | Una pantalla muestra un total incorrecto. |
| **Causa raíz** | Condición que origina el fallo y cuya corrección resuelve el caso. | El contador se incrementa fuera del `if`. |
| **Reproducción** | Conjunto de pasos y datos que vuelve a producir el fallo. | Ejecutar el método con `[6]`. |
| **Contraejemplo mínimo** | Entrada pequeña que demuestra el defecto. | Una sola nota igual a `6`. |
| **Hipótesis** | Explicación provisional que se puede comprobar. | «La comparación excluye el límite». |
| **Traza** | Registro ordenado de caminos y valores durante la ejecución. | Tabla de índice, valor y contador. |
| **Punto de interrupción (*breakpoint*)** | Lugar donde el depurador pausa la ejecución. | Primera línea del bucle. |
| **Stack trace** | Lista de llamadas activas cuando se produjo una excepción. | `main -> procesar -> calcular`. |
| **Regresión** | Reaparición de un defecto ya corregido. | Una modificación posterior vuelve a excluir el `6`. |
| **Caso de prueba** | Entrada y resultado esperado usados para comprobar una regla. | `[5, 6, 7]` debe contar `2` aprobadas. |
| **Observabilidad** | Capacidad de conocer el comportamiento interno a partir de señales. | Logs, métricas y trazas. |

**Diferencia central:** el síntoma indica *dónde se nota* el problema; la causa explica *por qué ocurre*. Pueden estar en partes distintas del programa.

---

## 3. Cómo leer código desconocido sin perderse

Leer un archivo de arriba abajo rara vez basta. Primero armá un **mapa del recorrido de una entrada**.

```text
Entrada -> método que la recibe -> validación -> cálculo -> cambio de estado -> salida
```

| Pregunta | Qué buscar |
|---|---|
| ¿Por dónde entra el caso? | Método público, `main`, controlador o consumidor que recibe la entrada. |
| ¿Cuál es el contrato? | Parámetros, valores permitidos, resultado y errores posibles. |
| ¿Qué decisiones toma? | `if`, `switch`, filtros y retornos tempranos. |
| ¿Qué datos cambia? | Campos, colecciones, base de datos o archivos. |
| ¿A quién llama? | Métodos propios y dependencias externas. |
| ¿Dónde se arma la respuesta? | `return`, constructor de resultado o escritura de salida. |

En código grande, seguí **una sola entrada concreta**. Anotá las llamadas relevantes como `A -> B -> C`; abrí los detalles de `C` solo si afectan esa entrada. Si un método hace demasiadas cosas, escribí su intención en una frase antes de estudiar cada línea.

Para entender un cambio de estado, la guía de ejecución y memoria ayuda a distinguir **reasignar una referencia** de **modificar el objeto compartido**.

---

## 4. Resultado esperado, resultado observado y contrato

Una investigación necesita una fuente para definir **qué debería pasar**: una regla escrita, un requisito acordado o un caso verificable. La intuición por sí sola puede estar equivocada.

```text
Entrada: [4, 6, 9]
Regla: aprobar desde 6, incluido.
Esperado: 2
Observado: 1
Diferencia: falta contar exactamente el valor límite.
```

Separá estas tres preguntas:

1. **¿Qué promete el método?** Por ejemplo, contar notas `>= 6`.
2. **¿Qué entrada recibió de verdad?** Puede ser distinta de la imaginada.
3. **¿Qué devolvió o qué efecto produjo?** Anotá el valor concreto, no solo «funciona mal».

Si el contrato no define qué hacer con `null`, vacío o un valor fuera de rango, primero hay que aclarar esa regla. No se puede decidir si el programa falla cuando no sabemos cuál era el resultado correcto.

---

## 5. Reproducir un fallo con datos concretos

Una reproducción permite comprobar hipótesis y verificar la solución. Incluí **entrada, pasos, resultado esperado y resultado observado**.

```text
Método: calcularDescuento(int porcentaje)
Entrada: porcentaje = 100
Esperado: precio final 0
Observado: precio final igual al precio original
Condiciones relevantes: precio original 500; no hay otros descuentos
```

Si el fallo ocurre solo a veces, anotá también qué puede influir: orden de llamadas, estado previo, hora, datos persistidos, configuración o cantidad de tareas simultáneas. **No supongas que una entrada aislada basta** cuando el método depende de estado externo.

Si no lográs reproducirlo todavía, conservá la evidencia disponible y buscá qué información falta. Un registro con la entrada y el resultado puede ser más útil que intentar adivinar a partir de una captura de pantalla.

---

## 6. Reducir el problema hasta encontrar un contraejemplo pequeño

Una entrada grande mezcla muchas posibilidades. Reducirla permite ver cuál regla rompe el resultado.

```text
Fallo original: 10.000 notas, el total de aprobadas difiere por 1.
Reducción: [5, 6, 7] -> devuelve 1 en vez de 2.
Reducción final: [6] -> devuelve 0 en vez de 1.
```

**Procedimiento:** quitá datos que no parecen necesarios, repetí el fallo y conservá la versión que todavía falla. Después probá valores justo por debajo, en y por encima del límite.

| Entrada | Esperado | ¿Qué revela? |
|---|---:|---|
| `[5]` | 0 | Caso inferior. |
| `[6]` | 1 | Límite exacto. |
| `[7]` | 1 | Caso superior. |

Un contraejemplo mínimo no demuestra por sí solo cuál línea está mal, pero reduce el espacio de búsqueda y deja un caso claro para verificar la corrección.

---

## 7. Formular hipótesis y hacer experimentos

Una **hipótesis útil** predice algo observable. Cambiá o inspeccioná **una cosa por vez** para saber qué información aportó cada experimento.

```text
Hipótesis: la condición es nota > 6.
Predicción: [5] da 0, [6] da 0, [7] da 1.
Experimento: ejecutar esos tres casos y observar la rama del if.
Conclusión: si los datos coinciden, revisar esa condición en el código.
```

| Hipótesis vaga | Hipótesis comprobable |
|---|---|
| «Java calcula mal» | «La división entre dos `int` descarta la parte decimal». |
| «La lista está rara» | «Dos variables apuntan a la misma lista y otra función la vacía». |
| «Falla por la base» | «La consulta devuelve `null` para este identificador y el siguiente método lo usa». |

Una hipótesis refutada también sirve: descarta una causa y orienta el siguiente paso. Evitá hacer cinco cambios juntos; después sería difícil saber cuál corrigió el problema.

---

## 8. Trazar el flujo y el estado paso a paso

Una traza registra **la primera diferencia** entre lo que debe representar una variable y lo que realmente contiene.

```java
int[] notas = {5, 6, 7};
int aprobadas = 0;
for (int nota : notas) {
    if (nota > 6) { // defecto: excluye el 6.
        aprobadas++;
    }
}
```

| Iteración | `nota` | Regla `nota >= 6` | Condición del código `nota > 6` | `aprobadas` |
|---:|---:|---|---|---:|
| Antes | — | — | — | 0 |
| 1 | 5 | No | No | 0 |
| 2 | 6 | **Sí** | **No** | 0 |
| 3 | 7 | Sí | Sí | 1 |

El fallo nace en la **segunda iteración**; la salida final solo lo hace visible. Podés trazar a mano, con el depurador o con registros temporales. Elegí los valores que explican la decisión, no todas las variables del programa.

---

## 9. Leer excepciones y stack traces en Java

Una excepción informa **qué tipo de error se detectó** y la pila indica **por qué llamadas llegó el programa a ese lugar**.

```text
Exception in thread "main" java.lang.NullPointerException: ...
    at ejemplo.Calculadora.total(Calculadora.java:24)
    at ejemplo.PedidoService.confirmar(PedidoService.java:41)
    at ejemplo.App.main(App.java:12)
```

Leé en este orden:

1. **Tipo y mensaje:** `NullPointerException` indica que se intentó usar una referencia nula.
2. **Primer marco relevante de tu código:** `Calculadora.java:24` muestra dónde ocurrió la operación.
3. **Llamadas anteriores:** `PedidoService.confirmar` y `App.main` muestran cómo se llegó allí.
4. **Origen del dato:** averiguá quién proporcionó el valor que terminó siendo `null`.

La línea donde se lanza la excepción **no siempre contiene la causa raíz**. Puede estar en una llamada anterior que aceptó un dato inválido o no inicializó un campo. Cuando aparece `Caused by:`, inspeccioná también esa causa anidada; las capas superiores pueden envolver la excepción original.

```java
// Esto oculta información útil y dificulta encontrar la causa:
try {
    procesar();
} catch (RuntimeException e) {
    System.out.println("Algo falló");
}
```

Si una capa debe transformar la excepción, conservá la causa (`new IllegalStateException("No se pudo procesar", e)`) o registrala en un lugar apropiado. **No uses `catch` para hacer desaparecer un fallo que todavía no comprendés.**

---

## 10. Usar puntos de interrupción y el depurador

Un **breakpoint** pausa el programa antes de ejecutar una línea. Durante la pausa podés mirar variables, pila de llamadas y expresiones. Empezá cerca de la **primera decisión dudosa**, no necesariamente en `main`.

| Acción del depurador | Qué hace | Cuándo conviene |
|---|---|---|
| **Step over** | Ejecuta la línea actual sin entrar a los métodos llamados. | La llamada ya es conocida. |
| **Step into** | Entra al método llamado. | La duda está dentro de esa llamada. |
| **Step out** | Termina el método actual y vuelve al llamador. | Ya entendiste ese método. |
| **Resume** | Continúa hasta el próximo breakpoint. | No necesitás observar cada línea intermedia. |
| **Conditional breakpoint** | Pausa solo cuando una condición se cumple. | El fallo aparece con una nota `6` entre miles. |

**Precaución:** evaluar una expresión en el depurador puede llamar métodos con efectos secundarios. Si ejecutás `lista.remove(0)` para «mirar» su resultado, acabás de cambiar el programa mientras lo investigás. Preferí inspeccionar valores sin mutarlos.

---

## 11. Usar registros de ejecución sin agregar ruido

Un registro (*log*) es útil cuando no podés pausar el programa o el fallo ocurre en otra ejecución. Registrá **datos que permitan verificar una hipótesis**.

```java
System.out.println("nota=" + nota + ", aprobadasAntes=" + aprobadas);
```

Para un ejercicio pequeño, `System.out.println` basta. En una aplicación, un logger permite niveles y contexto. En ambos casos preguntá: **¿qué valor necesito conocer y en qué punto?**

| Registro útil | Registro poco útil |
|---|---|
| `pedidoId=42, estadoAntes=PENDIENTE, accion=confirmar` | `Entró al método`. |
| `nota=6, cumpleCondicion=false` | Imprimir miles de objetos completos. |

No registres contraseñas, tokens ni datos sensibles para resolver un fallo. Quitá los registros temporales que ya no aportan información; mantenerlos puede ocultar las señales importantes entre demasiado ruido.

---

## 12. Patrones frecuentes de fallos de lógica

| Síntoma | Causa posible | Contraejemplo útil |
|---|---|---|
| Falla justo en un límite | `>` en lugar de `>=`, o viceversa. | Valores debajo, en y encima del límite. |
| Error al llegar al último elemento | `i <= longitud` en lugar de `i < longitud`. | Arreglo de un elemento. |
| Promedio demasiado bajo | Se cuentan elementos que no entraron en la suma. | Un válido y un inválido. |
| Se pierde la parte decimal | División de enteros antes de convertir a `double`. | `1 / 2` produce `0` como `int`. |
| Dos partes cambian «solas» | Ambas tienen una referencia al mismo objeto mutable. | Una lista y su alias. |
| Una cadena «igual» no coincide | Comparación de objetos con `==` en lugar de `equals`. | Dos `new String("A")`. |
| El primer caso funciona y el segundo falla | Estado no reiniciado o compartido. | Ejecutar dos veces con entradas distintas. |
| Un valor desaparece | Se reasignó una variable en vez de modificar el objeto esperado. | Método que recibe una lista y hace `lista = new ArrayList<>()`. |

La tabla propone **hipótesis**, no diagnósticos automáticos. Confirmá cada causa siguiendo la ejecución real.

---

## 13. Encontrar la primera frontera donde cambia el resultado

Cuando una operación cruza varios métodos, comprobá **qué entra y qué sale de cada uno**. Así podés localizar el tramo que transforma un dato correcto en uno incorrecto.

```text
Entrada del usuario: 100
  -> validar(): 100       correcto
  -> calcular(): 0        primera diferencia
  -> guardar(): 0         conserva el error
  -> mostrar(): 0         síntoma visible
```

En este ejemplo, corregir `mostrar()` solo maquillaría el resultado. El tramo para investigar es `calcular()`, junto con los datos que recibe. En sistemas con base de datos o red, la frontera también puede ser una respuesta externa: no supongas que contiene lo esperado; observá el valor concreto.

**Técnica:** empezá con una comprobación a mitad del flujo. Si el dato ya está mal, retrocedé; si sigue correcto, avanzá. Repetí hasta hallar la primera transformación incorrecta.

---

## 14. Corregir la causa y comprobar que no regresa

Una corrección completa tiene tres partes:

1. **Explicación:** identificaste qué regla se incumplía y qué línea o decisión lo causaba.
2. **Cambio mínimo coherente:** modificaste la lógica responsable, sin alterar reglas ajenas.
3. **Comprobación:** repetiste el caso fallido y casos vecinos; guardaste una prueba de regresión cuando corresponde.

```text
Defecto: nota > 6 excluye el valor límite.
Cambio: nota >= 6.
Comprobación: [5] -> 0, [6] -> 1, [7] -> 1.
```

Una prueba de regresión debe fallar con el defecto original y pasar con la corrección. Tenerla evita que un cambio futuro reintroduzca el mismo error. La guía de testing profundiza en herramientas como JUnit; acá el foco es **qué comportamiento conviene fijar**.

Si la corrección modifica un contrato, revisá a quienes llaman ese método. Por ejemplo, cambiar el significado de `null` puede resolver un caso y romper otro consumidor.

---

## 15. Caso completo: un promedio que parece correcto

**Regla:** calcular el promedio de tiempos no negativos. Los tiempos negativos indican datos inválidos y se ignoran. Si no hay ningún tiempo válido, el método rechaza la entrada.

### Código con defecto

```java
static double promedio(int[] tiempos) {
    int suma = 0;
    int cantidad = 0;
    for (int tiempo : tiempos) {
        if (tiempo >= 0) {
            suma += tiempo;
        }
        cantidad++; // defecto: cuenta incluso los valores ignorados.
    }
    return (double) suma / cantidad;
}
```

Con `{10, -1, 20}`, la respuesta esperada es `15.0`: sumamos `10 + 20` y dividimos por `2`. El método devuelve `10.0`: divide `30` por `3`.

| Valor leído | ¿Válido? | `suma` | `cantidad` del código | `cantidad` correcta |
|---:|---|---:|---:|---:|
| Antes | — | 0 | 0 | 0 |
| 10 | Sí | 10 | 1 | 1 |
| -1 | No | 10 | **2** | **1** |
| 20 | Sí | 30 | 3 | 2 |

**Causa raíz:** el contador se actualiza fuera de la rama que acepta el dato. Además, con `{}` o `{-1}`, `cantidad` puede ser `0` o no representar valores válidos. Hay que definir y manejar ese borde.

### Corrección

```java
static double promedio(int[] tiempos) {
    if (tiempos == null) {
        throw new IllegalArgumentException("Los tiempos no pueden ser null");
    }

    long suma = 0;
    int cantidad = 0;
    for (int tiempo : tiempos) {
        if (tiempo >= 0) {
            suma += tiempo;
            cantidad++;
        }
    }
    if (cantidad == 0) {
        throw new IllegalArgumentException("No hay tiempos válidos");
    }
    return (double) suma / cantidad;
}
```

**Invariante:** después de revisar los primeros `i` elementos, `suma` contiene la suma de los no negativos entre esos elementos y `cantidad` cuenta exactamente esos elementos. Ambos valores se actualizan juntos cuando la condición es verdadera.

**Casos de comprobación:** `{10, 20}` → `15.0`; `{10, -1, 20}` → `15.0`; `{0}` → `0.0`; `{}` y `{-1}` → excepción; `null` → excepción. La entrada con un inválido es el caso de regresión que detecta el defecto original.

---

## 16. Ejercicios de diagnóstico

En cada ejercicio escribí **esperado, observado, contraejemplo mínimo, hipótesis y primera línea a inspeccionar**. Después proponé la corrección.

| Ejercicio | Código o síntoma | Caso que lo revela |
|---|---|---|
| **Límite** | `if (edad > 18)` para «mayor de edad desde 18». | `edad = 18`. |
| **Último índice** | `for (int i = 0; i <= datos.length; i++)`. | Arreglo con un elemento. |
| **División** | `double mitad = 1 / 2;`. | Esperado `0.5`, observado `0.0`. |
| **Alias** | Dos variables apuntan a una lista; una llama a `clear()`. | La otra lista también queda vacía. |
| **Texto** | `new String("OK") == new String("OK")`. | Contenido igual, referencias distintas. |
| **Estado previo** | Un contador de instancia no se reinicia entre cálculos. | Dos invocaciones seguidas. |

**Desafío:** encontrá una entrada que haga fallar el método defectuoso de promedio **sin producir excepción**. Explicá por qué `{10, -1, 20}` es mejor contraejemplo para ese defecto que `{}`.

---

## 17. Lista de verificación para investigar un fallo

- [ ] Puedo describir el resultado esperado y citar la regla que lo define.
- [ ] Registré la entrada y el resultado observado concretos.
- [ ] Sé cómo reproducir el fallo o qué información falta para lograrlo.
- [ ] Reduje la entrada a un contraejemplo pequeño cuando fue posible.
- [ ] Tengo una hipótesis que predice algo verificable.
- [ ] Seguí el flujo y encontré la primera diferencia entre estado esperado y real.
- [ ] Leí el mensaje y la causa de la excepción, no solo la última línea del stack trace.
- [ ] Distingo el síntoma visible de la causa que lo origina.
- [ ] La corrección respeta el contrato y no depende de ocultar el error.
- [ ] Repetí el caso fallido y los casos vecinos.
- [ ] Guardé una prueba de regresión si el fallo puede reaparecer.

**Idea central:** depurar es hacer preguntas precisas al programa y usar sus respuestas como evidencia. Cuando podés explicar el primer paso en que la ejecución se aparta de la regla, la corrección deja de ser una adivinanza.
