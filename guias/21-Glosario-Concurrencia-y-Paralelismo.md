# Glosario y Guía de Concurrencia y Paralelismo

Esta guía explica **qué cambia cuando varias tareas pueden avanzar al mismo tiempo**: cómo se intercalan, qué ocurre si comparten datos y cómo conservar resultados correctos. Primero presenta las ideas; después muestra ejemplos en **Java**. El objetivo es poder razonar sobre el problema antes de elegir hilos, bloqueos o bibliotecas.

---

## Índice

1. [Por qué existe la concurrencia](#1-por-qué-existe-la-concurrencia)
2. [Glosario fundamental](#2-glosario-fundamental)
3. [Concurrencia, paralelismo y asincronía](#3-concurrencia-paralelismo-y-asincronía)
4. [Procesos, hilos, tareas y estado compartido](#4-procesos-hilos-tareas-y-estado-compartido)
5. [Intercalaciones: una misma lógica, varios órdenes](#5-intercalaciones-una-misma-lógica-varios-órdenes)
6. [Condiciones de carrera y actualizaciones perdidas](#6-condiciones-de-carrera-y-actualizaciones-perdidas)
7. [Atomicidad, visibilidad y orden](#7-atomicidad-visibilidad-y-orden)
8. [Secciones críticas y exclusión mutua](#8-secciones-críticas-y-exclusión-mutua)
9. [Qué garantiza synchronized en Java](#9-qué-garantiza-synchronized-en-java)
10. [Volatile: visibilidad sin convertir una operación compuesta en atómica](#10-volatile-visibilidad-sin-convertir-una-operación-compuesta-en-atómica)
11. [Contadores atómicos y operaciones condicionales](#11-contadores-atómicos-y-operaciones-condicionales)
12. [Evitar compartir estado mutable](#12-evitar-compartir-estado-mutable)
13. [Coordinar tareas y esperar resultados](#13-coordinar-tareas-y-esperar-resultados)
14. [Límites de carga y colas de trabajo](#14-límites-de-carga-y-colas-de-trabajo)
15. [Deadlock, starvation y progreso](#15-deadlock-starvation-y-progreso)
16. [Cancelación, interrupción y limpieza](#16-cancelación-interrupción-y-limpieza)
17. [Cuándo el paralelismo ayuda y cuándo cuesta más](#17-cuándo-el-paralelismo-ayuda-y-cuándo-cuesta-más)
18. [El límite de una JVM: datos externos y varias instancias](#18-el-límite-de-una-jvm-datos-externos-y-varias-instancias)
19. [Caso completo: reservar el último cupo](#19-caso-completo-reservar-el-último-cupo)
20. [Diagnóstico y ejercicios de razonamiento](#20-diagnóstico-y-ejercicios-de-razonamiento)
21. [Lista de verificación para diseñar código concurrente](#21-lista-de-verificación-para-diseñar-código-concurrente)

---

## 1. Por qué existe la concurrencia

Una aplicación suele tener varias cosas pendientes: atender personas, esperar una respuesta de red, leer archivos o calcular resultados. **Concurrencia** permite que varias tareas estén en progreso durante un mismo período. Esto puede mejorar la capacidad de respuesta, pero también permite que una tarea observe datos mientras otra los modifica.

```text
Solo queda 1 cupo.
Persona A consulta: hay 1.
Persona B consulta: hay 1.
Ambas intentan reservarlo.

Pregunta: ¿qué operación garantiza que solo una reserva tenga éxito?
```

El problema no se resuelve diciendo «el procesador es muy rápido». El orden entre tareas puede variar, y el diseño debe seguir siendo correcto en todos los órdenes permitidos.

---

## 2. Glosario fundamental

| Término | Significado | Ejemplo |
|---|---|---|
| **Tarea** | Unidad de trabajo que debe completarse. | Calcular una cotización. |
| **Hilo (*thread*)** | Flujo de ejecución que puede ejecutar instrucciones. | Un hilo atiende una tarea. |
| **Concurrencia** | Estructura donde varias tareas progresan durante el mismo período. | Dos solicitudes activas. |
| **Paralelismo** | Ejecución física simultánea de trabajo en distintos recursos. | Dos cálculos usan dos núcleos. |
| **Asincronía** | El solicitante puede continuar sin esperar inmediatamente el resultado. | Se inicia una tarea y se recoge su resultado después. |
| **Estado compartido** | Dato al que varias tareas pueden acceder. | Un contador en un objeto común. |
| **Intercalación (*interleaving*)** | Orden en que se mezclan pasos de distintas tareas. | A lee, B lee, A escribe, B escribe. |
| **Condición de carrera** | Resultado que depende de un orden no controlado entre tareas. | Dos reservas aceptan el último cupo. |
| **Operación atómica** | Operación que, para las demás tareas, ocurre como una unidad indivisible. | Descontar un cupo solo si queda alguno. |
| **Visibilidad** | Posibilidad de que una tarea observe los cambios de otra según las reglas de sincronización. | Un hilo detecta una señal de parada. |
| **Sección crítica** | Pasos sobre estado compartido que deben coordinarse. | Comprobar y descontar cupos. |
| **Exclusión mutua** | Impedir que dos tareas entren a la vez en una sección crítica. | Un bloqueo alrededor de la reserva. |
| **Deadlock** | Espera circular que impide avanzar a las tareas involucradas. | A espera el bloqueo de B y B el de A. |
| **Starvation** | Una tarea espera indefinidamente mientras otras siguen avanzando. | Nunca obtiene un recurso muy disputado. |
| **Backpressure** | Mecanismo para limitar o frenar trabajo que llega más rápido de lo que se procesa. | Cola con capacidad y política de rechazo. |

**Recordatorio:** que un método sea correcto con una sola tarea no demuestra que lo sea con varias.

---

## 3. Concurrencia, paralelismo y asincronía

Son ideas relacionadas, pero responden preguntas diferentes.

| Idea | Pregunta | Ejemplo |
|---|---|---|
| **Concurrencia** | ¿Pueden varias tareas estar en progreso y alternarse? | Una tarea espera red mientras otra atiende a un usuario. |
| **Paralelismo** | ¿Se ejecutan instrucciones de varias tareas al mismo tiempo? | Dos núcleos procesan cálculos distintos. |
| **Asincronía** | ¿Puede quien inicia la tarea seguir sin esperar el resultado en ese punto? | Recibe un `Future` y consulta el resultado más tarde. |
| **Bloqueo al esperar** | ¿El hilo queda detenido hasta que algo suceda? | `future.get()` espera si el resultado aún no está listo. |

```text
Concurrencia en un núcleo:   A-A | B-B | A-A | B-B
Paralelismo en dos núcleos:  A-A-A-A
                             B-B-B-B  (al mismo tiempo)
```

Una llamada asíncrona **puede terminar ejecutándose secuencialmente** si solo hay un trabajador. Una operación concurrente **puede bloquear** cuando espera. No elijas una herramienta por la etiqueta; identificá primero qué comportamiento necesitás.

---

## 4. Procesos, hilos, tareas y estado compartido

Un **proceso** es una instancia de programa con recursos propios. Dentro de una JVM puede haber varios **hilos**. Una **tarea** es el trabajo lógico; puede ser ejecutada por un hilo del conjunto de trabajadores.

```text
Proceso JVM
  ├── Hilo A -> tarea de reserva
  ├── Hilo B -> otra tarea de reserva
  └── Objeto Cupos compartido por A y B
```

Cada llamada a un método tiene sus variables locales, pero dos hilos pueden acceder al **mismo objeto** si ambos reciben una referencia a él. Los campos de ese objeto son estado compartido. Un campo `static` mutable también puede compartir estado entre hilos de la misma JVM.

**Pregunta esencial:** ¿estas tareas leen datos independientes, leen un valor inmutable compartido o modifican el mismo dato? El tercer caso exige coordinación. El segundo suele ser mucho más sencillo de razonar.

---

## 5. Intercalaciones: una misma lógica, varios órdenes

Un programa concurrente no tiene un único orden global de instrucciones decidido por el código fuente. El planificador puede detener una tarea entre dos pasos y continuar otra.

```text
Estado inicial: contador = 0

Tarea A: leer contador -> sumar 1 -> escribir contador
Tarea B: leer contador -> sumar 1 -> escribir contador
```

| Paso | Tarea A | Tarea B | Valor compartido |
|---:|---|---|---:|
| 1 | Lee `0` | — | 0 |
| 2 | — | Lee `0` | 0 |
| 3 | Calcula `1` | — | 0 |
| 4 | — | Calcula `1` | 0 |
| 5 | Escribe `1` | — | 1 |
| 6 | — | Escribe `1` | 1 |

Se intentaron dos incrementos, pero el resultado es `1`. Una ejecución diferente podría dar `2`. **El error es depender de un orden que no se garantizó.**

---

## 6. Condiciones de carrera y actualizaciones perdidas

Una **condición de carrera** ocurre cuando la corrección depende del orden de pasos concurrentes sin coordinación suficiente. El incremento anterior es una **actualización perdida**.

```java
final class ContadorInseguro {
    private int valor;

    void incrementar() {
        valor++; // lectura, cálculo y escritura; no una operación atómica.
    }

    int valor() { return valor; }
}
```

Otro patrón frecuente es **comprobar y después actuar**:

```java
// Incorrecto si dos hilos comparten esta instancia sin coordinación:
if (cuposDisponibles > 0) {
    cuposDisponibles--;
    aceptarReserva();
}
```

Dos tareas pueden aprobar la condición antes de que una descuente el cupo. Si la regla es «solo aceptar cuando se pudo descontar», **comprobar y descontar deben formar una sola operación coordinada**.

---

## 7. Atomicidad, visibilidad y orden

Son tres preguntas distintas sobre el estado compartido:

| Propiedad | Pregunta | Fallo posible |
|---|---|---|
| **Atomicidad** | ¿Puede otra tarea observar o interferir entre pasos que deben ir juntos? | Dos hilos realizan `contador++` y se pierde un incremento. |
| **Visibilidad** | ¿Cuándo puede un hilo observar un cambio de otro? | Un hilo sigue leyendo un valor anterior de una señal. |
| **Orden** | ¿Qué operaciones debe observar otra tarea antes que otras? | Ve «listo» sin observar datos publicados previamente. |

En Java, estas garantías se describen mediante relaciones de sincronización del **modelo de memoria**. Un ejemplo: al salir de un bloque `synchronized`, los cambios hechos antes pueden ser observados por otro hilo que después entra usando **el mismo monitor**. Sin una relación de este tipo, razonar únicamente con «seguro ya se escribió en RAM» es insuficiente.

**Importante:** visibilidad no vuelve atómica una secuencia de varios pasos; atomicidad de una variable no protege automáticamente una regla que abarca varias variables.

---

## 8. Secciones críticas y exclusión mutua

La **sección crítica** debe cubrir toda la regla que exige una vista coherente del dato. En el caso de cupos, proteger solo la resta deja la comprobación expuesta.

```text
Sección crítica correcta:
  comprobar que hay cupos
  descontar uno
  decidir si la reserva fue aceptada
```

**Exclusión mutua** hace que solo una tarea ejecute esa sección sobre el mismo estado a la vez. El bloqueo debe estar asociado al dato que protege. Dos objetos con bloqueos distintos no coordinan una misma variable externa por arte de magia.

Mantener la sección crítica corta reduce esperas. Evitá realizar llamadas de red o trabajo lento mientras sostenés un bloqueo cuando pueda separarse sin romper la regla. La solución exacta depende del estado y del contrato de la operación.

---

## 9. Qué garantiza synchronized en Java

`synchronized` permite que un solo hilo a la vez posea el **monitor de un objeto**. También establece una relación de visibilidad cuando un hilo libera el monitor y otro lo adquiere después.

```java
final class Cupos {
    private int disponibles;

    Cupos(int iniciales) {
        if (iniciales < 0) throw new IllegalArgumentException("Cupos negativos");
        disponibles = iniciales;
    }

    synchronized boolean reservar() {
        if (disponibles == 0) return false;
        disponibles--;
        return true;
    }

    synchronized int disponibles() {
        return disponibles;
    }
}
```

Ambos métodos usan el monitor de **la misma instancia** de `Cupos`. Una segunda instancia tiene su propio monitor y su propio contador. El método sincronizado mantiene unida la decisión «hay cupo» con la resta. Si el dato estuviera en una base de datos compartida por varias instancias del programa, este bloqueo local no alcanzaría.

**No confundas:** `synchronized` protege este tramo dentro de una JVM; no convierte una llamada remota ni varias escrituras externas en una transacción.

---

## 10. Volatile: visibilidad sin convertir una operación compuesta en atómica

Un campo `volatile` se usa cuando varios hilos deben observar una variable con las garantías de visibilidad y orden previstas por el modelo de memoria de Java.

```java
final class Trabajador {
    private volatile boolean detener;

    void solicitarDetencion() {
        detener = true;
    }

    void ejecutar() {
        while (!detener) {
            realizarPaso();
        }
    }

    private void realizarPaso() { /* trabajo acotado */ }
}
```

El ejemplo supone que `realizarPaso()` **termina periódicamente** para poder volver a comprobar la señal. Si el hilo queda bloqueado en otra operación, quizá se necesite interrupción o un mecanismo de cancelación adicional.

```java
private volatile int contador;
void incrementar() { contador++; } // sigue sin ser atómico.
```

`volatile` no hace indivisible el «leer, sumar y escribir». Para contadores o decisiones con varios pasos, elegí una operación atómica apropiada o una sección crítica.

---

## 11. Contadores atómicos y operaciones condicionales

Las clases atómicas de Java ofrecen operaciones indivisibles sobre **una variable**. Son útiles para un contador compartido sin reglas que abarquen otros campos.

```java
import java.util.concurrent.atomic.AtomicInteger;

AtomicInteger completadas = new AtomicInteger();
completadas.incrementAndGet();
int resultado = completadas.get();
```

Para reservar un cupo con un solo contador atómico, la decisión y el descuento pueden expresarse juntos:

```java
static boolean reservar(AtomicInteger disponibles) {
    while (true) {
        int actual = disponibles.get();
        if (actual == 0) return false;
        if (disponibles.compareAndSet(actual, actual - 1)) return true;
        // Otro hilo cambió el valor; leer de nuevo y reintentar.
    }
}
```

Esto protege **ese contador**. Si también hay que crear una reserva, registrar un usuario y actualizar otra estructura, la operación completa necesita otra estrategia. Un `AtomicInteger` no vuelve atómica una secuencia repartida entre varios objetos o sistemas.

---

## 12. Evitar compartir estado mutable

La opción más sencilla suele ser **reducir el estado que varias tareas pueden modificar**.

| Estrategia | Idea | Ejemplo |
|---|---|---|
| Datos inmutables | Compartir valores que nadie cambia. | `record Solicitud(String zona, int peso)`. |
| Confinamiento | Cada tarea modifica solo sus datos locales. | Cada tarea calcula un subtotal propio. |
| Transferencia de mensajes | Una tarea entrega datos a otra sin que ambas editen el mismo objeto. | Una cola entrega trabajos a un único procesador. |
| Coordinación explícita | Proteger el pequeño estado que sí debe compartirse. | Reservar un cupo. |

```java
static long sumar(int[] valores) {
    long total = 0; // local a esta llamada
    for (int valor : valores) total += valor;
    return total;
}
```

El `total` local no se comparte entre llamadas. Sin embargo, el arreglo recibido **sí podría ser modificado por otro hilo**; para razonar con seguridad, el contrato debe exigir que no cambie durante el cálculo o entregar una copia estable.

---

## 13. Coordinar tareas y esperar resultados

Iniciar tareas no significa que hayan terminado. Si necesitás sus respuestas, debés coordinar el punto en que las recogés.

```java
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

ExecutorService ejecutor = Executors.newFixedThreadPool(2);
try {
    Future<Integer> primera = ejecutor.submit(() -> 2 + 3);
    Future<Integer> segunda = ejecutor.submit(() -> 4 + 5);

    int total = primera.get() + segunda.get(); // espera resultados si aún faltan.
    System.out.println(total); // 14
} finally {
    ejecutor.shutdown();
}
```

`Future.get()` puede bloquear hasta que el trabajo termine y puede propagar un fallo de la tarea mediante `ExecutionException`. El ejemplo muestra el concepto de **iniciar, continuar y reunir resultados**; en una aplicación real también hay que definir límites de espera y manejo de errores según el caso.

No uses `Thread.sleep(100)` como prueba de que otra tarea ya terminó. El tiempo que tarda depende de la carga y de la planificación. Esperá una señal o un resultado concreto.

---

## 14. Límites de carga y colas de trabajo

Crear trabajo más rápido de lo que puede completarse hace crecer la cantidad de tareas pendientes. Aunque cada tarea sea correcta, el sistema puede agotar memoria, hilos o conexiones.

```text
Llegan 100 tareas por segundo.
Se completan 60 por segundo.
La diferencia de 40 por segundo se acumula mientras continúe esa situación.
```

Un límite de concurrencia decide cuántas tareas trabajan a la vez. Una cola acotada decide cuántas pueden esperar. Si se alcanza el límite, debe existir una política: **rechazar, esperar, reducir ritmo o descartar** según el significado de las tareas.

| Pregunta | Consecuencia de ignorarla |
|---|---|
| ¿Cuántas tareas pueden estar activas? | Saturación de CPU o recursos externos. |
| ¿Cuántas pueden esperar? | Crecimiento de memoria y latencia. |
| ¿Qué hace el productor cuando se llena la capacidad? | Pérdida silenciosa o caída del sistema. |

Esto es **backpressure**: hacer visible que el consumidor no puede seguir el ritmo. No se resuelve únicamente agregando más hilos.

---

## 15. Deadlock, starvation y progreso

La seguridad pregunta **«¿se conserva la regla?»**. El progreso pregunta **«¿las tareas pueden terminar?»**. Un sistema puede proteger sus datos y, aun así, quedar detenido.

```text
Tarea A tiene bloqueo 1 y espera bloqueo 2.
Tarea B tiene bloqueo 2 y espera bloqueo 1.
Ninguna puede continuar: deadlock.
```

| Problema | Qué ocurre | Medida de diseño |
|---|---|---|
| **Deadlock** | Espera circular. | Adquirir recursos en un orden consistente; reducir bloqueos simultáneos. |
| **Starvation** | Una tarea nunca obtiene oportunidad de avanzar. | Revisar políticas de prioridad y tiempos de espera. |
| **Livelock** | Las tareas siguen actuando pero se impiden mutuamente progresar. | Introducir reglas de desempate o retroceso controlado. |

Los tiempos de espera ayudan a detectar que no hay progreso, pero no sustituyen una regla correcta de coordinación. Un bloqueo adquirido debe liberarse incluso si ocurre un error; las construcciones de Java como `synchronized` o `try/finally` alrededor de `Lock` ayudan a garantizarlo.

---

## 16. Cancelación, interrupción y limpieza

Una tarea puede dejar de ser necesaria: el usuario canceló, venció el plazo o se está apagando la aplicación. **Cancelar** no significa que el trabajo desaparezca instantáneamente; debe haber puntos donde la tarea pueda detectar la señal y terminar de manera coherente.

```java
static void procesarHastaCancelar() {
    while (!Thread.currentThread().isInterrupted()) {
        procesarUnidad(); // debería terminar en un tiempo razonable.
    }
}
```

Si una operación bloqueante lanza `InterruptedException` y el método no puede propagarla, una respuesta habitual es **restablecer la señal** con `Thread.currentThread().interrupt()` y finalizar el trabajo. Ignorarla sin una razón puede impedir la cancelación.

```java
try {
    Thread.sleep(1_000);
} catch (InterruptedException e) {
    Thread.currentThread().interrupt();
    return;
}
```

El ejemplo ilustra el manejo de la interrupción, **no** recomienda `sleep` para sincronizar tareas. La limpieza de archivos, conexiones y bloqueos debe quedar definida para una salida normal, un error o una cancelación.

---

## 17. Cuándo el paralelismo ayuda y cuándo cuesta más

El paralelismo puede reducir el tiempo de un trabajo si hay partes **independientes** que consumen CPU y suficientes recursos para ejecutarlas. También introduce costos: dividir trabajo, coordinarlo, reunir resultados y competir por datos compartidos.

| Trabajo | Pregunta antes de paralelizar |
|---|---|
| Cálculos independientes sobre grandes bloques | ¿El trabajo por bloque supera el costo de coordinar tareas? |
| Llamadas a servicios externos | ¿Cuál es el límite del servicio y cuántas conexiones permite? |
| Actualización del mismo contador | ¿Toda tarea terminará esperando la misma sección crítica? |

**Ley de Amdahl, como intuición:** si el `20%` del tiempo debe seguir siendo secuencial, aun con recursos paralelos ilimitados la aceleración máxima teórica sería `5` veces (`1 / 0,20`). La coordinación real reduce ese máximo.

Medí antes y después con una carga representativa. Más hilos no implican automáticamente menos latencia ni más trabajo completado.

---

## 18. El límite de una JVM: datos externos y varias instancias

Un bloqueo de Java coordina hilos **dentro de la misma instancia de JVM y sobre el mismo objeto o mecanismo compartido**. Si dos procesos de la aplicación usan la misma base de datos, cada uno puede tener su propio `synchronized` sin coordinar al otro.

```text
Instancia A -> su objeto Cupos -> synchronized local
Instancia B -> su objeto Cupos -> synchronized local
                   \          /
                    base de datos compartida
```

Si la verdad de los cupos vive en la base, la regla «solo una reserva del último cupo» debe protegerse **en la operación sobre esa fuente compartida**. Puede requerir una actualización condicional, una restricción, un bloqueo o una estrategia transaccional adecuada. El modelo de datos identifica la regla; la guía de SQL y PostgreSQL desarrollará los mecanismos de persistencia.

**También importa la escala:** `AtomicInteger`, `ConcurrentHashMap` y `synchronized` no sustituyen una garantía de consistencia entre procesos o servicios.

---

## 19. Caso completo: reservar el último cupo

**Consigna:** dos solicitudes intentan reservar el único cupo de una instancia en memoria. Debe aceptarse exactamente una y quedar `0` disponibles.

### Paso 1: regla e intercalación peligrosa

```text
Regla: aceptar si y solo si el contador pudo pasar de 1 a 0.

Sin coordinación:
A comprueba 1 -> B comprueba 1 -> A descuenta -> B descuenta.
Ambas podrían anunciar éxito; la regla se rompe.
```

### Paso 2: implementación en Java

```java
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public final class EjemploUltimoCupo {
    static final class Cupos {
        private int disponibles;

        Cupos(int iniciales) {
            if (iniciales < 0) throw new IllegalArgumentException("Cupos negativos");
            disponibles = iniciales;
        }

        synchronized boolean reservar() {
            if (disponibles == 0) return false;
            disponibles--;
            return true;
        }

        synchronized int disponibles() {
            return disponibles;
        }
    }

    public static void main(String[] args) throws Exception {
        Cupos cupos = new Cupos(1);
        CountDownLatch listas = new CountDownLatch(2);
        CountDownLatch inicio = new CountDownLatch(1);
        ExecutorService ejecutor = Executors.newFixedThreadPool(2);

        try {
            java.util.concurrent.Callable<Boolean> intento = () -> {
                listas.countDown();
                inicio.await();
                return cupos.reservar();
            };

            Future<Boolean> a = ejecutor.submit(intento);
            Future<Boolean> b = ejecutor.submit(intento);
            listas.await(); // ambas tareas llegaron al punto de espera.
            inicio.countDown(); // pueden intentar reservar.

            int aceptadas = (a.get() ? 1 : 0) + (b.get() ? 1 : 0);
            System.out.println("Aceptadas: " + aceptadas); // 1
            System.out.println("Disponibles: " + cupos.disponibles()); // 0
        } finally {
            inicio.countDown(); // libera a las tareas si el hilo principal sale antes.
            ejecutor.shutdown();
        }
    }
}
```

`CountDownLatch` permite preparar dos tareas antes de liberar su intento. **No garantiza que ambas ejecuten exactamente la misma instrucción al mismo tiempo**; sirve para ilustrar la competencia. La garantía de una sola reserva exitosa viene del método `reservar()` sincronizado sobre la misma instancia.

### Paso 3: alcance de la garantía

El ejemplo protege un contador **en memoria dentro de un proceso**. No guarda reservas, no coordina otras JVM y no resuelve por sí solo una base de datos compartida. Si el cupo real está en otro sistema, la comprobación y el descuento deben realizarse allí con la garantía adecuada.

---

## 20. Diagnóstico y ejercicios de razonamiento

Los fallos concurrentes pueden aparecer una vez entre miles de ejecuciones. Un resultado correcto repetido **no demuestra** que no exista una carrera. Para investigar, registrá entradas, identificadores de tarea y cambios de estado; trazá una intercalación que explique el fallo y verificá qué relación de sincronización falta.

| Ejercicio | Situación | Pregunta |
|---|---|---|
| **Contador** | Dos hilos ejecutan `valor++` sobre el mismo campo. | Dibujá un orden que pierda un incremento. |
| **Señal** | Un hilo actualiza una bandera y otro espera observarla. | ¿Qué garantiza la visibilidad? |
| **Último producto** | Dos compras leen stock `1`. | ¿Qué pasos deben formar una unidad? |
| **Lista compartida** | Varias tareas agregan elementos a un `ArrayList`. | ¿Quién es dueño de la lista y cómo se coordina? |
| **Dos bloqueos** | A toma primero X y luego Y; B toma primero Y y luego X. | ¿Cómo puede aparecer deadlock? |
| **Dos procesos** | Cada proceso usa su propio `synchronized`. | ¿Por qué no basta para proteger una fila compartida? |
| **Paralelismo** | Cuatro tareas calculan sobre arreglos independientes. | ¿Qué se comparte y cuál es el costo de reunir resultados? |

**Desafío:** transformá el ejemplo de cupos para permitir `3` reservas entre `10` tareas. Definí antes la invariante: `aceptadas + disponibles == 3` y `disponibles >= 0`. Pensá qué cambiaría si además hubiera que guardar la identidad de cada persona aceptada.

---

## 21. Lista de verificación para diseñar código concurrente

- [ ] Distingo concurrencia, paralelismo, asincronía y espera bloqueante.
- [ ] Identifiqué exactamente qué estado puede ser compartido.
- [ ] Puedo dibujar al menos una intercalación peligrosa.
- [ ] Sé qué pasos deben ser una operación atómica para proteger la regla.
- [ ] Definí cómo se garantizan visibilidad y orden cuando importan.
- [ ] Evité compartir estado mutable cuando una alternativa simple sirve.
- [ ] Si uso un bloqueo, sé qué dato protege y qué tareas usan el mismo bloqueo.
- [ ] No confundo `volatile` con atomicidad de `contador++`.
- [ ] Sé cuándo una tarea termina y cómo obtengo o cancelo su resultado.
- [ ] Establecí límites para tareas activas y trabajo pendiente.
- [ ] Revisé espera circular, falta de progreso y liberación de recursos.
- [ ] Distingo las garantías dentro de una JVM de las necesarias entre procesos o bases de datos.

**Idea central:** la concurrencia exige definir **qué resultado es correcto para todos los órdenes posibles**. Una herramienta de Java ayuda solo cuando protege la operación y el estado donde vive esa regla.
