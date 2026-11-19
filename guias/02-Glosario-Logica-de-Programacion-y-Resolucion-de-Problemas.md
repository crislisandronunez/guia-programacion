# Guía de lógica de programación y resolución de problemas

La lógica de programación estudia cómo transformar un problema en una secuencia de decisiones y pasos que produzca un resultado correcto. No depende de un lenguaje: primero se entiende el problema y se diseña la solución; después se expresa en Java u otro lenguaje.

Esta guía explica ese razonamiento. Los ejemplos resueltos, las tablas y el pseudocódigo muestran cómo funcionan las ideas. La sintaxis de Java se estudia en la [guía 03](03-ManualGlosario_programacion-sintaxisJava.md); los ejercicios prácticos corresponden al laboratorio.

## Índice

1. [Problema, algoritmo y programa](#1-problema-algoritmo-y-programa)
2. [Glosario fundamental](#2-glosario-fundamental)
3. [Del enunciado a una especificación](#3-del-enunciado-a-una-especificación)
4. [Descomponer y abstraer](#4-descomponer-y-abstraer)
5. [Representar información y estado](#5-representar-información-y-estado)
6. [Expresiones y lógica booleana](#6-expresiones-y-lógica-booleana)
7. [Decisiones y cobertura de casos](#7-decisiones-y-cobertura-de-casos)
8. [Repetición y terminación](#8-repetición-y-terminación)
9. [Patrones de recorrido](#9-patrones-de-recorrido)
10. [Componer patrones y descomponer soluciones](#10-componer-patrones-y-descomponer-soluciones)
11. [Pseudocódigo y trazas](#11-pseudocódigo-y-trazas)
12. [Corrección e invariantes](#12-corrección-e-invariantes)
13. [Límites, entradas inválidas y ausencia](#13-límites-entradas-inválidas-y-ausencia)
14. [Errores de lógica y diagnóstico](#14-errores-de-lógica-y-diagnóstico)
15. [Costo de una solución](#15-costo-de-una-solución)
16. [Caso resuelto: contar notas aprobadas](#16-caso-resuelto-contar-notas-aprobadas)
17. [Caso resuelto: primera coincidencia](#17-caso-resuelto-primera-coincidencia)
18. [Caso resuelto: resumir ventas](#18-caso-resuelto-resumir-ventas)
19. [De la lógica a Java](#19-de-la-lógica-a-java)
20. [Mapa conceptual y errores frecuentes](#20-mapa-conceptual-y-errores-frecuentes)

---

## 1. Problema, algoritmo y programa

Un **problema** describe un resultado deseado y las condiciones que debe cumplir. Un **algoritmo** es un procedimiento finito y preciso para obtenerlo. Un **programa** expresa ese procedimiento en un lenguaje que una computadora puede ejecutar. La sintaxis permite escribir el programa; no decide por sí sola si el algoritmo resuelve el problema.

Ejemplo: se recibe una secuencia de edades y se necesita saber cuántas corresponden a personas de 18 años o más.

```text
Datos:     [17, 18, 25, 16]
Regla:     una edad cuenta si es mayor o igual a 18
Resultado: 2

Procedimiento:
  comenzar con una cantidad igual a 0
  observar cada edad una vez
  aumentar la cantidad solo si la edad cumple la regla
  devolver la cantidad
```

El resultado `2` para esa entrada es un **ejemplo**. La regla y el procedimiento describen lo que sucede con todas las entradas permitidas. Un algoritmo correcto debe funcionar también para una secuencia vacía, para edades iguales al límite y para cualquier otro caso definido por el problema.

El proceso de resolución puede verse como una cadena:

```text
Necesidad → reglas y datos → resultado esperado → algoritmo → implementación → comprobación
```

Un error en las reglas se propaga al resto. Si se interpreta «mayor de edad» como `edad > 18`, ningún cambio de sintaxis arreglará la respuesta incorrecta para `18`.

---

## 2. Glosario fundamental

| Término | Significado | Ejemplo |
|---|---|---|
| **Entrada** | Información recibida por la solución. | Una secuencia de edades. |
| **Salida** | Información que la solución entrega. | La cantidad de mayores de edad. |
| **Regla** | Condición que determina cómo tratar los datos. | Una edad cuenta desde `18`, inclusive. |
| **Algoritmo** | Pasos finitos y definidos que transforman entradas en salidas. | Recorrer, comparar y contar. |
| **Especificación** | Descripción del comportamiento esperado, independiente del código. | Entrada válida, regla y resultado de `contarMayores`. |
| **Contrato** | Parte de la especificación que fija entradas admitidas, resultado y tratamiento de errores. | Una entrada ausente se rechaza; una secuencia vacía devuelve `0`. |
| **Precondición** | Propiedad exigida a la entrada o al estado antes de ejecutar una operación. | Las edades deben estar entre `0` y `120`. |
| **Postcondición** | Propiedad que debe cumplir el resultado al terminar correctamente. | La salida coincide con la cantidad de edades `>= 18`. |
| **Estado** | Valores actuales que el algoritmo conserva durante su ejecución. | `posición = 2`, `cantidad = 1`. |
| **Rama** | Camino seleccionado por una decisión. | Contar una edad o continuar sin contarla. |
| **Iteración** | Una ejecución del cuerpo de un bucle. | Procesar la tercera edad. |
| **Traza** | Registro de estados sucesivos de una ejecución concreta. | Tabla de edad observada y contador. |
| **Invariante** | Propiedad que se conserva en puntos definidos de la ejecución. | Tras procesar `i` edades, el contador refleja exactamente esas `i` edades. |
| **Caso borde** | Entrada válida situada en un límite relevante. | Edad `18` o secuencia vacía. |
| **Contraejemplo** | Entrada concreta que muestra que una solución falla. | `[18]` demuestra que `> 18` no implementa la regla `>= 18`. |
| **Pseudocódigo** | Descripción estructurada de pasos sin las reglas de un lenguaje concreto. | `para cada edad: si edad >= 18, aumentar cantidad`. |

Estos términos describen niveles distintos. El contrato dice **qué** se espera; el algoritmo explica **cómo** obtenerlo; la traza muestra **qué ocurrió** en una ejecución; la invariante ayuda a justificar **por qué** el algoritmo funciona en general.

---

## 3. Del enunciado a una especificación

Un enunciado cotidiano suele omitir decisiones. «Contar las notas aprobadas» no determina, por sí solo, la escala de notas, el mínimo para aprobar, el significado de una entrada ausente ni la política ante valores fuera de rango. Esas decisiones deben fijarse antes de elegir un bucle.

Para un ejemplo concreto se define este contrato:

| Aspecto | Decisión del ejemplo |
|---|---|
| Entrada | Secuencia existente de notas enteras entre `0` y `10`. |
| Salida | Cantidad entera entre `0` y la longitud de la secuencia. |
| Regla | Se aprueba con `6` o más. |
| Secuencia vacía | Resultado `0`. |
| Entrada ausente | Error; no equivale a secuencia vacía. |
| Nota fuera de rango | Error de la entrada completa; no se entrega un conteo parcial. |

La postcondición se puede expresar sin mencionar Java:

```text
resultado = cantidad de posiciones cuyo valor está entre 6 y 10
```

Una especificación distingue el **dominio** de las entradas válidas y el comportamiento ante entradas inválidas. No existe una política universal para todos los problemas. En otra aplicación podría ignorarse una nota inválida y contar el rechazo por separado. Sería otro contrato y, por lo tanto, otro algoritmo.

También importa distinguir resultados parecidos. «Hay una nota aprobada» produce un sí/no; «cuántas notas aprobadas hay» produce una cantidad; «dónde está la primera nota aprobada» produce una posición o una señal de ausencia. La misma entrada puede exigir tres recorridos con estados y condiciones de salida diferentes.

Un buen ejemplo de entrada y salida hace visible la interpretación, pero no sustituye la regla general. `[5, 6, 9] → 2` muestra un caso; la expresión «contar todas las notas entre 6 y 10» describe el comportamiento para cualquier secuencia válida.

---

## 4. Descomponer y abstraer

**Descomponer** consiste en separar un problema en decisiones y pasos más simples. **Abstraer** consiste en conservar lo relevante para resolverlo y dejar fuera detalles que no cambian la respuesta.

Considérese «resumir una secuencia de pedidos». Una descripción útil para el algoritmo podría separar:

```text
1. Determinar qué pedidos son válidos.
2. Decidir qué ocurre con los inválidos.
3. Sumar los importes aceptados.
4. Contar los pedidos rechazados.
5. Devolver ambos resultados.
```

«Validar pedido» aún puede esconder varias reglas: el pedido debe existir, tener importe y cumplir que el importe sea positivo. Descomponer ese paso evita que una decisión importante quede implícita.

Para sumar importes y contar rechazos no hacen falta, en principio, el color de la interfaz ni el nombre de la tabla de base de datos. Esos detalles se abstraen porque no participan en la regla. En cambio, sí importa si `0` es un importe válido: esa decisión cambia el resultado.

Una descomposición útil tiene un orden lógico. Primero se define la validez; luego se decide cómo tratar cada elemento; finalmente se acumulan los resultados. Si se suma un importe antes de validarlo, el estado puede incluir datos que el contrato rechaza.

La separación también permite reconocer tareas distintas dentro de una solución: **validar**, **seleccionar**, **transformar**, **agregar** y **presentar**. Una tarea puede requerir varias de ellas. La lógica se entiende mejor cuando se conoce el propósito de cada paso, aunque al implementarla algunos pasos queden en el mismo bucle.

---

## 5. Representar información y estado

Un algoritmo necesita representar sus datos de entrada y, a veces, recordar resultados parciales. Ese conjunto de valores que cambia durante la ejecución es su **estado**.

Para contar mayores de edad bastan dos piezas de información: la edad que se observa y la cantidad acumulada hasta ese momento. Si la secuencia es `[17, 18, 25]`, el estado evoluciona así:

| Momento | Edad observada | Cantidad después de procesarla |
|---|---:|---:|
| Antes del recorrido | — | 0 |
| Primera iteración | 17 | 0 |
| Segunda iteración | 18 | 1 |
| Tercera iteración | 25 | 2 |

El valor inicial `0` tiene una razón: antes de observar elementos se han encontrado cero coincidencias. Cada actualización debe conservar el significado de la variable. Si se aumenta la cantidad para una edad que no cumple la regla, el estado deja de representar lo que su nombre promete.

Una **secuencia** conserva elementos en un orden. Cuando la posición importa, se puede usar un **índice**. En una secuencia con tres elementos, las posiciones suelen ser `0`, `1` y `2`; la longitud es `3`. La longitud cuenta elementos, mientras que el último índice identifica una posición. Confundirlos produce errores de límite.

No toda salida tiene la misma forma. Un conteo necesita un número; una búsqueda de existencia necesita un valor verdadero o falso; un máximo necesita un valor candidato y una forma de representar que aún no existe ninguno. Elegir una representación adecuada simplifica el algoritmo. Usar `0` para indicar «no hay máximo» es incorrecto cuando `0` puede ser un valor real.

El estado mínimo suele ser más fácil de razonar. Si una variable guarda información que ya puede deducirse de otra, ambas pueden desincronizarse. Por ejemplo, al recorrer `i` elementos, una variable adicional «cantidadProcesada» debe actualizarse exactamente junto a `i` o puede divergir.

---

## 6. Expresiones y lógica booleana

Una **expresión** combina valores y operaciones para producir otro valor. Una **condición** es una expresión cuyo resultado es verdadero o falso. Las decisiones y los bucles dependen de condiciones formuladas con precisión.

| Expresión conceptual | Significado |
|---|---|
| `nota >= 6` | La nota es al menos 6. |
| `nota >= 0 Y nota <= 10` | La nota pertenece al rango válido, incluidos ambos extremos. |
| `nota < 0 O nota > 10` | La nota está fuera del rango válido. |
| `NO encontrado` | Todavía no se encontró una coincidencia. |

**Y** exige que ambas condiciones sean verdaderas. **O** exige que al menos una lo sea. **NO** invierte el valor de una condición.

| A | B | A Y B | A O B |
|---|---|---|---|
| falso | falso | falso | falso |
| falso | verdadero | falso | verdadero |
| verdadero | falso | falso | verdadero |
| verdadero | verdadero | verdadero | verdadero |

Los límites cambian el significado. `nota > 6` excluye al `6`; `nota >= 6` lo incluye. Para un intervalo cerrado de `6` a `8`, la condición es `nota >= 6 Y nota <= 8`. Escribir `nota >= 6 O nota <= 8` hace que prácticamente cualquier número cumpla: un `2` satisface la segunda parte y un `10` satisface la primera.

Algunas expresiones son equivalentes por las leyes de De Morgan:

```text
NO (A Y B)  equivale a  (NO A) O (NO B)
NO (A O B)  equivale a  (NO A) Y (NO B)
```

Así, «no está entre 0 y 10» equivale a `nota < 0 O nota > 10`. Estas equivalencias ayudan a expresar validaciones, pero una condición más corta no siempre es más clara. La claridad depende de que se pueda reconocer directamente la regla del problema.

Las condiciones también pueden depender de **evaluación de corto circuito**: si la primera parte de un `Y` es falsa, no hace falta evaluar la segunda; si la primera parte de un `O` es verdadera, tampoco. En Java esto ocurre con `&&` y `||`. La idea importa cuando la segunda parte solo tiene sentido si se cumple la primera, como verificar que un dato existe antes de leer una propiedad suya.

---

## 7. Decisiones y cobertura de casos

Una decisión divide las entradas en caminos. Cada camino debe corresponder a una regla clara y el conjunto de caminos debe cubrir todos los casos válidos.

Ejemplo: clasificar notas entre `0` y `10` como desaprobadas (`0` a `5`), aprobadas (`6` a `7`) o destacadas (`8` a `10`). Primero se rechazan valores fuera de rango; después se clasifican los válidos:

```text
si nota < 0 O nota > 10: rechazar
si nota >= 8: devolver «destacada»
si nota >= 6: devolver «aprobada»
devolver «desaprobada»
```

La tabla muestra que no hay huecos ni superposiciones en el resultado:

| Valor | Camino | Resultado |
|---:|---|---|
| -1 | Rechazo | Error |
| 0 | Último camino | Desaprobada |
| 5 | Último camino | Desaprobada |
| 6 | Segundo camino | Aprobada |
| 7 | Segundo camino | Aprobada |
| 8 | Primer camino | Destacada |
| 10 | Primer camino | Destacada |
| 11 | Rechazo | Error |

El **orden** de las condiciones importa. Si se evalúa `nota >= 6` antes de `nota >= 8` y se termina al encontrar una coincidencia, una nota `9` queda mal clasificada como aprobada. Una forma de razonar sobre varias ramas es partir el dominio en intervalos disjuntos y comprobar que cada intervalo tiene una sola salida.

Las decisiones no necesitan siempre un camino alternativo explícito. En un conteo, si un elemento no cumple la regla, el contador simplemente permanece igual. Ese «no hacer nada» es una conducta definida, no un caso olvidado.

---

## 8. Repetición y terminación

Una repetición aplica pasos similares a varios elementos o continúa hasta alcanzar una condición. Para entender un bucle hacen falta cuatro piezas: **estado inicial**, **condición de continuación**, **trabajo de una iteración** y **progreso hacia el final**.

En un recorrido por posiciones de una secuencia de longitud `n`:

```text
posición = 0
mientras posición < n:
    procesar el elemento en posición
    posición = posición + 1
```

La condición `posición < n` permite visitar desde `0` hasta `n - 1`. Si `n = 0`, el cuerpo no se ejecuta. Si se usara `posición <= n`, se intentaría visitar una posición inexistente. Este es un error de «uno de más», frecuente en los límites de los bucles.

La **terminación** se justifica porque la posición aumenta en cada iteración y el límite `n` permanece fijo. La distancia `n - posición` disminuye hasta llegar a cero. En otros bucles, la medida de progreso puede ser la cantidad de intentos restantes o la longitud de una parte aún sin procesar.

Un bucle que espera una condición externa, como «repetir hasta recibir una respuesta», necesita una política de finalización: máximo de intentos, tiempo límite o una señal que pueda alcanzarse. Que exista una condición escrita no garantiza que alguna vez se vuelva falsa.

Hay dos formas habituales de recorrer una secuencia:

| Forma conceptual | Utilidad |
|---|---|
| «Para cada elemento» | La regla depende del valor, no de su posición. |
| «Para cada posición de 0 a n - 1» | La posición forma parte del resultado o se necesita comparar vecinos. |

Un recorrido puede terminar **antes** de visitar todos los elementos cuando el resultado ya está determinado. Para saber si existe un negativo, encontrar el primero permite devolver verdadero. Para afirmar que todos son no negativos, encontrar un solo negativo permite devolver falso. En cambio, para contar todos los negativos hay que revisar toda la secuencia válida.

---

## 9. Patrones de recorrido

Muchos algoritmos iniciales combinan unos pocos patrones. Cada patrón se distingue por el significado de su estado y por la operación de actualización.

| Patrón | Estado inicial | Actualización | Resultado |
|---|---|---|---|
| **Contador** | `0` | Sumar `1` cuando un elemento cumple una regla. | Cuántos elementos cumplen. |
| **Acumulador** | Valor neutro, por ejemplo `0` para una suma. | Incorporar cada valor válido. | Suma o total. |
| **Existencia** | `falso` | Pasar a `verdadero` al encontrar un caso. | Si existe al menos uno. |
| **Universalidad** | `verdadero` | Pasar a `falso` al encontrar un contraejemplo. | Si todos cumplen. |
| **Mejor candidato** | Ausente o primer elemento válido. | Reemplazar cuando aparece uno mejor. | Máximo, mínimo o candidato elegido. |
| **Primera posición** | Ausente. | Guardar la posición de la primera coincidencia y terminar. | Ubicación o ausencia. |

### Contar y acumular

Para `[2, -1, 4]`, contar positivos produce `2`; sumar positivos produce `6`. Las dos soluciones inspeccionan los mismos elementos, pero actualizan estados diferentes. El contador aumenta de a uno; el acumulador incorpora el valor observado.

El valor inicial debe ser el **elemento neutro** de la operación. Para una suma es `0`; para un producto es `1`. Empezar un producto en `0` haría que cualquier resultado final fuera `0`.

### Existencia y universalidad

«Existe al menos uno» y «todos cumplen» tienen comportamientos distintos con una secuencia vacía. Ningún elemento de `[]` satisface una existencia, por lo que el resultado es falso. Ningún elemento de `[]` contradice una afirmación universal, por lo que «todos cumplen» es verdadero. Es el significado lógico de ambas expresiones; una aplicación puede definir otra política si lo necesita, pero debe decirlo.

### Mejor candidato

Para encontrar el mayor en `[-8, -3]`, empezar con `0` inventa un candidato que no está en la entrada y produce un resultado incorrecto. Si la secuencia es no vacía, se puede empezar con el primer elemento (`-8`) y comparar los restantes. Si se admite una secuencia vacía, el resultado necesita representar explícitamente que no existe un máximo.

### Primera coincidencia

En `[4, 7, 4]`, al buscar `4` la primera posición es `0`. Si el recorrido continúa y reemplaza el resultado en cada coincidencia, terminará devolviendo `2`: habrá resuelto «última coincidencia», no «primera coincidencia». El momento de terminar es parte del algoritmo.

---

## 10. Componer patrones y descomponer soluciones

Un problema puede necesitar varios resultados. Para una lista de importes no negativos, «cantidad, total y mayor» combina un contador, un acumulador y un mejor candidato. Los tres estados pueden actualizarse durante un mismo recorrido porque observan los mismos elementos.

```text
cantidad = 0
total = 0
mayor = ausente
para cada importe válido:
    cantidad = cantidad + 1
    total = total + importe
    si mayor está ausente O importe > mayor:
        mayor = importe
```

Esta composición ahorra recorridos, pero no es un objetivo en sí mismo. Si una solución de un solo bucle mezcla reglas que cuesta distinguir, separar pasos puede facilitar la comprensión. Primero se establece la corrección; después se decide si la combinación de recorridos aporta una ventaja real.

Una operación grande también se puede describir mediante operaciones pequeñas con responsabilidades claras: «validar entrada», «clasificar elemento», «actualizar resumen». La descomposición no obliga a crear una función para cada línea. Su valor es que cada parte tenga una regla reconocible y que la salida de una parte sea una entrada bien definida para la siguiente.

Cuando se componen pasos, el orden puede cambiar el resultado. Si se actualiza el total y luego se descubre que el importe era inválido, hay un estado parcial que no debería haberse incorporado. Validar antes de acumular mantiene consistente el significado del total.

---

## 11. Pseudocódigo y trazas

El **pseudocódigo** expresa el orden de las operaciones sin exigir la sintaxis exacta de un lenguaje. Es útil para distinguir una dificultad de razonamiento de una dificultad de escritura. No tiene una gramática universal: lo importante es que sus pasos sean inequívocos y que las decisiones y repeticiones estén visibles.

Ejemplo de búsqueda de la primera posición de un valor:

```text
buscarPrimeraPosición(valores, buscado):
    si valores está ausente: rechazar entrada
    posición = 0
    mientras posición < longitud(valores):
        si valores[posición] = buscado:
            devolver posición
        posición = posición + 1
    devolver AUSENTE
```

El símbolo `=` aquí se lee como comparación en una condición; en Java se utilizaría `==` para comparar números. El pseudocódigo describe la idea, no una línea lista para compilar. `AUSENTE` también es conceptual: la implementación puede representarlo de distintas maneras, siempre que el contrato lo indique.

Una **traza** sigue una ejecución concreta. Para buscar `7` en `[4, 7, 4]`:

| Antes de la iteración | Posición | Valor observado | Comparación con 7 | Acción |
|---|---:|---:|---|---|
| Primera | 0 | 4 | Diferente | Avanzar a 1 |
| Segunda | 1 | 7 | Igual | Devolver 1 |

La tercera posición no se observa: ya se conoce la primera coincidencia. Para buscar `9`, se observan las tres posiciones y luego se devuelve ausencia.

La traza prueba qué hace el algoritmo con **esa entrada**. No demuestra, por sí sola, que funcione con todas. Para razonar en general se necesita identificar qué representa el estado después de cada paso y por qué el recorrido termina.

---

## 12. Corrección e invariantes

Una solución es **correcta respecto de un contrato** si, para cada entrada válida, termina y entrega un resultado que cumple la postcondición. La corrección siempre depende de lo que se haya especificado: si cambia la regla de aprobación, cambia el resultado correcto.

Una **invariante de bucle** describe una propiedad que permanece verdadera en un punto elegido de cada iteración. Para contar notas aprobadas:

> Antes de procesar la nota situada en la posición `i`, `cantidad` es el número de notas aprobadas entre las primeras `i` posiciones.

La justificación tiene tres partes:

1. **Inicio.** Con `i = 0` no se procesó ninguna nota. `cantidad = 0` coincide con la cantidad de aprobadas entre cero elementos.
2. **Conservación.** Si la siguiente nota aprueba, se aumenta `cantidad`; si no, se deja igual. Después de avanzar `i`, la propiedad sigue describiendo exactamente los elementos ya procesados.
3. **Final.** Al llegar a `i = longitud`, los elementos procesados son todos. La invariante coincide entonces con la postcondición.

Además se justifica la terminación: `i` aumenta en cada paso y no supera la longitud. **Conservar la invariante** y **terminar** son cuestiones distintas. Un bucle infinito podría conservar una propiedad y, aun así, no producir nunca un resultado.

Para una búsqueda de primera coincidencia, la invariante es diferente: antes de mirar la posición `i`, el valor buscado **no apareció en las primeras `i` posiciones**. Si aparece en `i`, devolver `i` es correcto porque todas las posiciones anteriores ya se descartaron. Si el bucle termina sin encontrarlo, se descartó toda la secuencia.

Los ejemplos y las pruebas detectan fallos concretos; un razonamiento con invariantes explica por qué el procedimiento debe funcionar para toda entrada que cumple el contrato. En problemas pequeños no hace falta una demostración matemática extensa, pero sí conocer el significado de las variables y justificar sus actualizaciones.

---

## 13. Límites, entradas inválidas y ausencia

Los **casos borde** son entradas válidas en las que una regla cambia de comportamiento o una estructura tiene su tamaño mínimo. Las **entradas inválidas** quedan fuera del contrato de operación normal. No son lo mismo.

Para contar notas aprobadas de `0` a `10`, con mínimo `6`:

| Entrada | Categoría | Comportamiento definido |
|---|---|---|
| `[]` | Válida, vacía | Devolver `0`. |
| `[5]` | Válida, justo bajo el límite | Devolver `0`. |
| `[6]` | Válida, en el límite | Devolver `1`. |
| `[7]` | Válida, sobre el límite | Devolver `1`. |
| `[0, 10]` | Válida, extremos del rango | Devolver `1`. |
| Entrada ausente | Inválida según este contrato | Rechazar. |
| `[6, 11]` | Inválida según este contrato | Rechazar toda la entrada. |

La **ausencia** también puede ser un resultado normal. Buscar un valor que no aparece no significa que la entrada sea inválida. Encontrar el máximo de una secuencia vacía, en cambio, requiere una decisión: rechazar la entrada o devolver un resultado que represente «sin máximo». Ambas alternativas son posibles; lo incorrecto es devolver un número ordinario y hacerlo pasar por un máximo inexistente.

En operaciones con varios datos, se debe definir si una entrada inválida descarta el conjunto entero o si se procesa parcialmente. Si el contrato exige rechazar todo, un algoritmo no debe devolver silenciosamente un total de los primeros elementos válidos.

Los límites también incluyen la **capacidad de representación**. Un acumulador puede tener un rango finito en un programa real. Aunque el razonamiento matemático permita sumar indefinidamente, una implementación necesita un tipo de dato capaz de representar los resultados admitidos o una política ante desbordamiento. Ese detalle pertenece al contrato de una implementación concreta.

---

## 14. Errores de lógica y diagnóstico

Un error de lógica aparece cuando el programa ejecuta pasos válidos para el lenguaje pero produce una respuesta que no cumple el contrato. Se distingue de un error de sintaxis, que impide interpretar o compilar el programa.

| Error de lógica | Manifestación | Causa habitual |
|---|---|---|
| Excluir un límite incluido | `[6]` se cuenta como `0`. | Usar `> 6` en lugar de `>= 6`. |
| Visitar una posición inexistente | Fallo al llegar al final de una secuencia. | Continuar mientras `posición <= longitud`. |
| Inicializar mal el estado | El máximo de `[-8, -3]` sale `0`. | Inventar un candidato inicial. |
| Actualizar en la rama incorrecta | Se cuentan también los desaprobados. | Aumentar el contador fuera de la condición. |
| Terminar demasiado pronto | Se cuenta solo la primera coincidencia. | Devolver al encontrar un caso cuando se pedía contar todos. |
| Terminar demasiado tarde | Se devuelve la última coincidencia. | Reemplazar la primera posición encontrada. |
| No progresar | El bucle nunca termina. | La variable que gobierna la salida no cambia. |

Un **contraejemplo mínimo** facilita localizar la primera decisión equivocada. Si una solución devuelve `0` al contar aprobadas en `[6]`, el caso contiene una sola iteración y aísla el límite. La traza esperada es: contador inicial `0`, nota `6`, condición verdadera, contador final `1`. La primera diferencia entre esa secuencia y la ejecución real revela la causa.

El diagnóstico sigue el flujo del algoritmo: entrada concreta, resultado esperado, resultado obtenido, estado inicial, decisiones tomadas y primera divergencia. Observar solo el valor final no permite saber si falló una condición, una actualización o la terminación.

Los casos pequeños son especialmente útiles porque sus resultados pueden calcularse a mano. Una secuencia grande puede contener el mismo error, pero esconderlo entre muchos cambios de estado.

---

## 15. Costo de una solución

La corrección es la primera condición de utilidad. Entre soluciones correctas, el **costo** describe los recursos que consumen: tiempo de ejecución, memoria adicional y, en un sistema completo, también lecturas, escrituras o llamadas externas.

Si se recorren `n` elementos y se hace una cantidad acotada de trabajo por elemento, el tiempo crece de forma **lineal**, representado como `O(n)`. Un contador que solo guarda un número usa memoria adicional constante, `O(1)`. Estas expresiones describen cómo crece el trabajo; no prometen un número exacto de milisegundos o bytes.

| Estrategia | Trabajo aproximado | Ejemplo |
|---|---|---|
| Examinar solo el primer elemento | Constante | Leer la primera nota de una secuencia no vacía. |
| Recorrer una secuencia una vez | Proporcional a `n` | Contar todas las notas aprobadas. |
| Comparar cada elemento con todos los demás | Proporcional a `n × n` | Buscar pares mediante dos recorridos anidados. |

El peor caso y el mejor caso pueden diferir. Buscar la primera coincidencia termina enseguida si aparece en la primera posición; si no aparece, revisa toda la secuencia. Contar todas las coincidencias requiere revisar todos los elementos incluso si el primero coincide.

Combinar tres resultados en un solo recorrido puede reducir trabajo, pero también puede complicar la lectura. Para problemas pequeños, una solución clara y correcta suele ser mejor punto de partida que una optimización prematura. La [guía 07 de algoritmos](07-Glosario-Tipos-de-Algoritmos-Programadores.md) desarrolla el análisis de complejidad con más detalle.

---

## 16. Caso resuelto: contar notas aprobadas

### Contrato

Se recibe una secuencia existente de notas enteras entre `0` y `10`. La nota aprueba desde `6`, inclusive. Se devuelve la cantidad de aprobadas. La secuencia vacía produce `0`; una entrada ausente o una nota fuera de rango se rechaza.

### Razonamiento

La salida es una cantidad, así que el estado principal es un contador inicializado en `0`. Es necesario mirar cada nota porque cualquiera puede modificar el resultado. Antes de contarla se comprueba que pertenezca al rango permitido. Una nota válida aumenta el contador solo si cumple `nota >= 6`.

```text
contarAprobadas(notas):
    si notas está ausente: rechazar
    cantidad = 0
    para cada nota en notas:
        si nota < 0 O nota > 10: rechazar
        si nota >= 6: cantidad = cantidad + 1
    devolver cantidad
```

### Traza para `[5, 6, 9]`

| Nota procesada | Dentro de 0..10 | Aprobada | Cantidad después |
|---:|---|---|---:|
| Antes de empezar | — | — | 0 |
| 5 | Sí | No | 0 |
| 6 | Sí | Sí | 1 |
| 9 | Sí | Sí | 2 |

La invariante es que, después de cada iteración, `cantidad` representa las aprobadas entre las notas ya procesadas. Al terminar, se han procesado todas y el resultado es `2`. Una entrada `[5, 11]` se rechaza en la segunda iteración; el valor parcial del contador no se devuelve.

Este caso combina especificación, validación, recorrido, decisión, contador, traza e invariante. Ninguno de esos conceptos depende de la sintaxis de Java.

---

## 17. Caso resuelto: primera coincidencia

### Contrato

Se recibe una secuencia existente de enteros y un valor buscado. Se devuelve la posición de su **primera** aparición. Si no aparece, se devuelve una señal de ausencia. La secuencia vacía es válida y produce ausencia.

### Razonamiento

La salida es una posición, así que se necesita conocer el índice del elemento observado. El recorrido empieza en `0` y avanza hasta antes de la longitud. Al hallar una coincidencia, el algoritmo termina: todas las posiciones anteriores ya fueron descartadas.

```text
primeraPosición(valores, buscado):
    si valores está ausente: rechazar
    posición = 0
    mientras posición < longitud(valores):
        si valores[posición] = buscado: devolver posición
        posición = posición + 1
    devolver AUSENTE
```

Para `[4, 7, 4]` y valor `4`, se devuelve `0`; la segunda aparición no cambia cuál fue la primera. Para valor `9`, el recorrido termina después de revisar las tres posiciones y devuelve ausencia. Para `[]`, la condición del bucle es falsa desde el inicio y también devuelve ausencia.

La invariante es que, antes de revisar una posición, el valor buscado no aparece en ninguna posición anterior. Esta propiedad justifica tanto el retorno temprano como la respuesta de ausencia al final.

---

## 18. Caso resuelto: resumir ventas

### Contrato

Se recibe una secuencia existente de importes enteros no negativos. Un importe `0` es válido. El resultado contiene **cantidad de ventas**, **total** y **mayor importe**. Si la secuencia está vacía, cantidad y total son `0`, y el mayor está ausente. Cualquier importe negativo rechaza la entrada completa.

Los enteros representan unidades didácticas, no un modelo de dinero real. Una aplicación de pagos necesita definir moneda, escala y reglas de precisión.

### Razonamiento

La cantidad usa un contador; el total, un acumulador; el mayor, un candidato que comienza ausente. Los tres se actualizan después de validar cada importe:

```text
resumirVentas(importes):
    si importes está ausente: rechazar
    cantidad = 0
    total = 0
    mayor = AUSENTE
    para cada importe en importes:
        si importe < 0: rechazar
        cantidad = cantidad + 1
        total = total + importe
        si mayor está ausente O importe > mayor: mayor = importe
    devolver (cantidad, total, mayor)
```

### Traza para `[120, 0, 80]`

| Importe procesado | Cantidad | Total | Mayor |
|---:|---:|---:|---:|
| Antes de empezar | 0 | 0 | Ausente |
| 120 | 1 | 120 | 120 |
| 0 | 2 | 120 | 120 |
| 80 | 3 | 200 | 120 |

El resultado es `(3, 200, 120)`. Para `[0]`, el resultado es `(1, 0, 0)`: una venta de importe cero existe y su máximo es cero. Para `[]`, el resultado es `(0, 0, AUSENTE)`: no se inventa una venta. Para `[120, -1]`, se rechaza la entrada y no se entrega el resumen parcial calculado antes del negativo.

La invariante conjunta afirma que, después de procesar un prefijo válido, `cantidad` es su longitud, `total` es la suma de sus importes y `mayor` es el máximo del prefijo o está ausente si el prefijo está vacío. Este caso muestra cómo varios patrones conservan significados diferentes dentro de un mismo recorrido.

---

## 19. De la lógica a Java

La implementación traduce el algoritmo a tipos, operaciones y estructuras del lenguaje. La lógica debe permanecer reconocible: entrada, validación, estado inicial, recorrido, decisión y resultado. Java no aporta por sí mismo la regla «se aprueba desde 6»; esa regla viene del contrato.

Una traducción breve del caso de notas es:

```java
static int contarAprobadas(int[] notas) {
    if (notas == null) {
        throw new IllegalArgumentException("Faltan las notas");
    }

    int cantidad = 0;
    for (int nota : notas) {
        if (nota < 0 || nota > 10) {
            throw new IllegalArgumentException("Nota fuera de rango");
        }
        if (nota >= 6) {
            cantidad++;
        }
    }
    return cantidad;
}
```

| Idea lógica | Expresión en este ejemplo Java |
|---|---|
| Entrada | Parámetro `int[] notas`. |
| Entrada ausente | `notas == null`. |
| Rechazo | `throw new IllegalArgumentException(...)`. |
| Contador inicial | `int cantidad = 0`. |
| Procesar cada valor | `for (int nota : notas)`. |
| Fuera del rango válido | `nota < 0 || nota > 10`. |
| Regla de aprobación | `nota >= 6`. |
| Actualización | `cantidad++`. |
| Salida | `return cantidad`. |

El código aparece aquí para mostrar la correspondencia entre razonamiento e implementación. La explicación de arreglos, métodos, operadores y excepciones como elementos de Java pertenece a la [guía de sintaxis](03-ManualGlosario_programacion-sintaxisJava.md). La misma lógica podría implementarse en otro lenguaje sin cambiar su contrato ni su invariante.

---

## 20. Mapa conceptual y errores frecuentes

La relación entre las ideas principales puede resumirse así:

```text
PROBLEMA
  ↓ precisar datos, reglas y resultados
ESPECIFICACIÓN / CONTRATO
  ↓ descomponer y elegir estados y pasos
ALGORITMO
  ↓ expresar en un lenguaje
PROGRAMA

TRAZAS Y CONTRAEJEMPLOS  → muestran comportamientos concretos
INVARIANTES Y TERMINACIÓN → justifican el recorrido completo
PRUEBAS                  → comprueban casos relevantes de la implementación
```

| Confusión frecuente | Distinción correcta |
|---|---|
| Un ejemplo que funciona prueba que el algoritmo es correcto. | Un ejemplo muestra un comportamiento; no cubre todas las entradas. |
| Vacío y ausente significan lo mismo. | Una secuencia vacía existe y contiene cero elementos; una entrada ausente requiere una política distinta. |
| `>` y `>=` son intercambiables. | Cambian el resultado en el valor límite. |
| El último índice es igual a la longitud. | En una secuencia no vacía con índices desde cero, el último índice es `longitud - 1`. |
| Un máximo puede empezar siempre en cero. | Cero puede no estar presente y puede ser mayor que todos los números negativos. |
| Encontrar un elemento permite terminar cualquier recorrido. | Solo permite terminar cuando el resultado pedido ya quedó determinado. |
| Un bucle con condición escrita necesariamente termina. | Tiene que existir progreso hacia una condición de salida alcanzable. |
| `O(n)` indica el tiempo exacto. | Describe crecimiento del trabajo respecto del tamaño de la entrada. |
| El código define el problema. | El contrato define el comportamiento; el código intenta implementarlo. |

La lógica de programación consiste en poder explicar y justificar el camino desde una entrada hasta un resultado. Las estructuras de un lenguaje son herramientas para ejecutar ese camino; la comprensión del problema decide cuáles usar y cómo combinarlas.

