# Glosario y Guía de Asincronía, Eventos y Mensajería

Esta guía explica **cómo separar una tarea de quien la inicia sin perder de vista su resultado**. Presenta comunicación asíncrona, eventos, comandos, colas, suscripciones, confirmaciones y duplicados antes de estudiar un broker concreto. Los ejemplos usan **Java**; el objetivo es comprender el contrato y los fallos posibles para poder elegir una tecnología después.

---

## Índice

1. [Por qué separar el inicio de una tarea de su resultado](#1-por-qué-separar-el-inicio-de-una-tarea-de-su-resultado)
2. [Glosario fundamental](#2-glosario-fundamental)
3. [Sincronía, asincronía y espera bloqueante](#3-sincronía-asincronía-y-espera-bloqueante)
4. [Comando, evento y consulta](#4-comando-evento-y-consulta)
5. [Productor, intermediario y consumidor](#5-productor-intermediario-y-consumidor)
6. [Cola de trabajo y publicación con suscripciones](#6-cola-de-trabajo-y-publicación-con-suscripciones)
7. [Qué debe contener un mensaje](#7-qué-debe-contener-un-mensaje)
8. [Orden de mensajes y claves de agrupación](#8-orden-de-mensajes-y-claves-de-agrupación)
9. [Confirmaciones y garantías de entrega](#9-confirmaciones-y-garantías-de-entrega)
10. [Consumidores idempotentes y efectos de negocio](#10-consumidores-idempotentes-y-efectos-de-negocio)
11. [Reintentos, errores transitorios y mensajes inválidos](#11-reintentos-errores-transitorios-y-mensajes-inválidos)
12. [Mensajes apartados y recuperación manual](#12-mensajes-apartados-y-recuperación-manual)
13. [Límites de capacidad y backpressure](#13-límites-de-capacidad-y-backpressure)
14. [Desacoplamiento temporal y trabajo pendiente](#14-desacoplamiento-temporal-y-trabajo-pendiente)
15. [Publicar sin perder un cambio confirmado](#15-publicar-sin-perder-un-cambio-confirmado)
16. [Solicitar una respuesta mediante mensajes](#16-solicitar-una-respuesta-mediante-mensajes)
17. [Evolución del formato y compatibilidad](#17-evolución-del-formato-y-compatibilidad)
18. [Observar el flujo y medir el atraso](#18-observar-el-flujo-y-medir-el-atraso)
19. [Ejemplo en Java: una cola dentro del mismo proceso](#19-ejemplo-en-java-una-cola-dentro-del-mismo-proceso)
20. [Caso completo: pedido confirmado y tareas posteriores](#20-caso-completo-pedido-confirmado-y-tareas-posteriores)
21. [Contrato de evento y consumidor en Java](#21-contrato-de-evento-y-consumidor-en-java)
22. [Errores frecuentes de diseño](#22-errores-frecuentes-de-diseño)
23. [Ejercicios de razonamiento](#23-ejercicios-de-razonamiento)
24. [Lista de verificación antes de usar un broker](#24-lista-de-verificación-antes-de-usar-un-broker)

---

## 1. Por qué separar el inicio de una tarea de su resultado

Algunas tareas deben terminar antes de responder al usuario; otras pueden ejecutarse después. Separarlas permite que el flujo principal espere solo lo que necesita para dar una respuesta verdadera.

```text
Confirmar un pedido:
  obligatorio ahora -> decidir si el pedido quedó registrado.
  puede suceder después -> actualizar estadísticas.

Respuesta al usuario: "pedido registrado".
Estado posterior: "estadísticas pendientes" hasta completar esa tarea.
```

**Asincronía no vuelve instantáneo el trabajo.** Cambia quién espera, cuándo se conoce el resultado y cómo se recupera una tarea que falla. Si la factura es requisito para declarar la venta terminada, la respuesta y los estados del sistema deben reflejarlo; no basta con mover la facturación a otro hilo y afirmar éxito total.

---

## 2. Glosario fundamental

| Término | Significado | Ejemplo |
|---|---|---|
| **Asincronía** | Quien inicia una tarea puede continuar sin esperar su finalización en ese punto. | Registrar un trabajo y consultar luego su estado. |
| **Productor** | Parte que crea y envía un mensaje. | Pedidos publica `PedidoConfirmado`. |
| **Consumidor** | Parte que recibe y procesa un mensaje. | Estadísticas actualiza un resumen. |
| **Broker** | Intermediario que recibe y entrega mensajes según sus reglas. | Un sistema de colas o de registro de eventos. |
| **Mensaje** | Unidad de información transmitida. | ID, tipo, datos y versión de un evento. |
| **Comando** | Solicitud de que alguien realice una acción. | `EnviarFactura`. |
| **Evento** | Hecho que ya ocurrió. | `PedidoConfirmado`. |
| **Consulta** | Solicitud de información sin intención de cambiar estado. | `ConsultarEstadoPedido`. |
| **Cola de trabajo** | Mecanismo para repartir tareas entre trabajadores. | Un trabajo de generación de PDF lo procesa un trabajador. |
| **Publicación/suscripción** | Un hecho puede interesar a varios grupos independientes. | Auditoría y estadísticas reciben `PedidoConfirmado`. |
| **Confirmación (*ack*)** | Señal de que una etapa aceptó o procesó un mensaje según su contrato. | El broker acepta una publicación. |
| **Reintento** | Repetición de un intento tras fallo o incertidumbre. | Volver a entregar un evento no confirmado. |
| **Deduplicación** | Reconocimiento de un mensaje u operación ya procesados. | `eventoId` ya figura en el registro. |
| **Mensaje inválido persistente** | Mensaje que fallará de nuevo sin corregir datos o código. | No cumple el esquema obligatorio. |
| **Backpressure** | Control del ritmo cuando se produce más de lo que se consume. | Límite de cola y rechazo explícito. |

**Preguntá siempre:** ¿este mensaje expresa una **intención futura** o un **hecho pasado**? Esa diferencia cambia quién es responsable del resultado.

---

## 3. Sincronía, asincronía y espera bloqueante

Son decisiones relacionadas, pero distintas.

| Diseño | Qué hace quien inicia | Qué sabe al continuar |
|---|---|---|
| Llamada síncrona | Espera la respuesta para seguir el flujo. | Conoce el resultado informado por el receptor. |
| Trabajo asíncrono | Inicia o registra trabajo y continúa. | Sabe que fue aceptado, si hay una confirmación de aceptación. |
| Espera bloqueante | El hilo queda detenido mientras falta un resultado. | Nada nuevo hasta desbloquearse. |

```java
java.util.concurrent.Future<String> futuro = ejecutor.submit(() -> generarInforme());
// Aquí el llamador puede hacer otra cosa.
String informe = futuro.get(); // Aquí sí espera si la tarea no terminó.
```

Este fragmento muestra **asincronía dentro de un proceso**. Si el proceso se apaga, no implica que el trabajo siga existiendo. Para trabajo que debe sobrevivir reinicios, hace falta un registro duradero o un sistema de mensajería configurado con la garantía necesaria.

---

## 4. Comando, evento y consulta

Nombrar bien el mensaje expresa **quién decide y qué significa recibirlo**.

| Tipo | Frase típica | Responsable principal | Ejemplo |
|---|---|---|---|
| **Comando** | «Hacé esto». | Receptor que ejecuta o rechaza la acción. | `ReservarStock`. |
| **Evento** | «Esto ocurrió». | Productor afirma un hecho; cada interesado decide cómo reaccionar. | `StockReservado`. |
| **Consulta** | «Decime esto». | Receptor responde sin efecto de negocio previsto. | `ConsultarStock`. |

```text
ReservarStock -> no afirma que el stock ya esté reservado.
StockReservado -> afirma que la reserva ya se confirmó.
```

Publicar `PedidoConfirmado` **antes** de confirmar el pedido crea una contradicción si luego falla la escritura. Un evento debe describir un hecho que el sistema puede sostener como verdadero.

No conviertas todo en evento. Si necesitás la respuesta de una acción para decidir inmediatamente, una llamada síncrona puede ser más fácil de comprender. Si optás por mensajes, definí estados intermedios y cómo llegará el resultado.

---

## 5. Productor, intermediario y consumidor

La comunicación mediante mensajes tiene varias etapas con confirmaciones distintas.

```text
Productor -> broker -> consumidor -> efecto de negocio
      A         B          C              D
```

| Confirmación | Qué puede demostrar | Qué no demuestra por sí sola |
|---|---|---|
| A → B | El broker aceptó el mensaje según su configuración. | Que el consumidor lo procesó. |
| B → C | El mensaje fue entregado al consumidor. | Que su efecto de negocio quedó confirmado. |
| C → B | El consumidor indicó progreso o finalización según el protocolo. | Que otro sistema externo completó un efecto. |

El significado exacto de cada confirmación depende de la tecnología y de su configuración. **La regla conceptual es separar aceptación del mensaje de éxito del negocio**.

---

## 6. Cola de trabajo y publicación con suscripciones

Hay dos necesidades frecuentes:

```text
Trabajo repartido:
  GenerarFactura F-42 -> un trabajador de facturación la procesa.

Hecho para interesados:
  PedidoConfirmado P-42 -> Auditoría registra el hecho.
                         -> Estadísticas actualiza un resumen.
```

| Pregunta | Cola de trabajo | Publicación con suscripciones |
|---|---|---|
| ¿Qué se distribuye? | Una tarea entre trabajadores de un mismo rol. | Un hecho a grupos interesados. |
| ¿Qué sucede al agregar trabajadores del mismo rol? | Pueden repartir carga. | Depende del grupo o suscripción. |
| ¿Qué sucede al agregar otro interesado? | Necesita una decisión explícita de distribución. | Puede recibir su propia entrega del hecho. |

Las tecnologías reales combinan estas ideas de distintas maneras. **No supongas que «cola» significa borrar el mensaje inmediatamente ni que «publicación» significa entrega garantizada a todos**; mirá el contrato del sistema elegido.

---

## 7. Qué debe contener un mensaje

Un mensaje debe llevar lo necesario para identificarlo, interpretarlo y cumplir su propósito sin depender de supuestos ocultos.

```text
eventoId: E-91
tipo: PedidoConfirmado
version: 1
pedidoId: P-42
ocurridoEn: instante del hecho
```

| Campo | Para qué sirve |
|---|---|
| **ID de mensaje/evento** | Detectar reenvíos del mismo mensaje. |
| **ID de operación** | Relacionar intentos de una misma acción de negocio. |
| **Tipo** | Elegir cómo interpretar el contenido. |
| **Versión** | Saber qué forma del contrato se está recibiendo. |
| **Clave de entidad** | Mantener juntos mensajes que requieren cierto orden. |
| **Fecha o instante** | Describir cuándo ocurrió el hecho, sin usarlo como única prueba de orden. |
| **Datos de negocio** | Permitir la reacción prevista sin consultas innecesarias. |

No coloques contraseñas, tokens o datos personales innecesarios en un evento que verán varios consumidores. El evento es un **contrato compartido**: añadir campos tiene costo de compatibilidad y de protección de datos.

---

## 8. Orden de mensajes y claves de agrupación

Dos mensajes pueden requerir orden **para una misma entidad** sin exigir un orden total entre todas las entidades.

```text
Pedido P-42: Creado -> Confirmado -> Cancelado.
Pedido P-99: sus mensajes pueden procesarse en paralelo con P-42.
```

| Pregunta | Decisión |
|---|---|
| ¿Qué entidad necesita preservar orden? | Elegir una clave como `pedidoId`. |
| ¿Todos los mensajes de esa entidad usan la misma ruta ordenada? | Asegurar una política consistente de envío. |
| ¿Puede un reintento adelantar o atrasar mensajes? | Definir cómo maneja el consumidor versiones o estados. |
| ¿Hay varios consumidores paralelos? | Comprobar qué orden conserva realmente el broker y el procesamiento. |

Un broker puede garantizar orden **dentro de una cola o partición concreta**, pero no necesariamente entre todas las colas ni hasta completar efectos externos. Para cambios de estado, una **versión** o una transición válida puede proteger contra un mensaje viejo que llega tarde.

---

## 9. Confirmaciones y garantías de entrega

Cuando falta una confirmación, el emisor no siempre sabe si el mensaje o el efecto ya ocurrieron. La estrategia de reenvío determina riesgos diferentes.

| Estrategia conceptual | Riesgo |
|---|---|
| **Como máximo una entrega intentada** | Puede perderse trabajo si falla antes de procesarse. |
| **Reentregar hasta confirmar** | El receptor puede ver el mismo mensaje más de una vez. |
| **Efecto de negocio una sola vez** | Requiere coordinación entre ID procesado y cambio de negocio. |

```text
Consumidor aplica el efecto.
La confirmación al broker se pierde.
El broker reentrega el mensaje.
```

La **entrega del mensaje** y el **efecto del negocio** son garantías distintas. Un sistema puede ofrecer procesamiento especial dentro de su propio alcance, pero eso no vuelve automáticamente única una escritura en otra base, un correo o un cobro externo.

---

## 10. Consumidores idempotentes y efectos de negocio

Un consumidor idempotente reconoce que un mensaje ya produjo un resultado. Necesita un ID estable y una forma de **guardar la marca de procesado junto al efecto**, si ambos viven en la misma base.

```text
Transacción del consumidor:
1. Comprobar o registrar eventoId de forma que dos instancias no ganen a la vez.
2. Si es nuevo, aplicar el cambio de negocio.
3. Confirmar marca y cambio juntos.
```

Si guardás la marca **antes** del efecto y el proceso cae, un reintento podría ignorar trabajo que nunca se hizo. Si guardás la marca **después** del efecto en otra transacción, un reintento podría repetirlo. La frontera atómica es la parte decisiva.

Para un efecto externo como enviar correo, quizá no exista una transacción común con tu base. En ese caso necesitás un contrato con el proveedor, una clave de operación si la ofrece, o una política explícita para el resultado desconocido. **No prometas «exactamente una vez» sin definir el límite de esa promesa.**

---

## 11. Reintentos, errores transitorios y mensajes inválidos

Un consumidor debe distinguir **si volver a intentar podría cambiar el resultado**.

| Tipo de fallo | Ejemplo | Respuesta posible |
|---|---|---|
| Transitorio | Base temporalmente inaccesible. | Reintento acotado con espera. |
| Permanente del mensaje | Falta un campo obligatorio. | Apartar y revisar; repetir igual no lo arregla. |
| Permanente de negocio | Pedido inexistente por datos erróneos. | Rechazar o investigar según el contrato. |
| Resultado desconocido | Se perdió respuesta de un proveedor externo. | Consultar por ID o reintentar con idempotencia. |

```text
Reintentar cada milisegundo un mensaje inválido
-> vuelve a fallar, ocupa al consumidor y llena registros.
```

Los reintentos consumen capacidad y pueden alterar el orden de procesamiento. Definí un límite, pausas y qué ocurre al agotarlo. **No conviertas todo error en un reintento infinito**.

---

## 12. Mensajes apartados y recuperación manual

Cuando un mensaje no puede procesarse tras la política de reintentos, puede enviarse a un espacio de **mensajes apartados** (*dead letter queue/topic*, según la tecnología). Esto permite continuar con otros trabajos y conservar evidencia para investigar.

| Dato a conservar | Para qué sirve |
|---|---|
| Mensaje original e ID | Reproducir y reconocer el intento. |
| Motivo del fallo | Distinguir datos inválidos de dependencia caída. |
| Intentos y fechas | Saber si la política agotó sus oportunidades. |
| Versión del contrato | Interpretar mensajes viejos. |

Apartar un mensaje **no resuelve** el problema de negocio. Debe existir una manera de alertar, corregir la causa, decidir si se reprocesa y verificar que no se duplique un efecto anterior. Si el orden entre mensajes es obligatorio, avanzar después de apartar uno puede tener consecuencias que deben definirse.

---

## 13. Límites de capacidad y backpressure

Si se producen `100` mensajes por segundo y el consumidor completa `60`, quedan `40` mensajes adicionales por segundo mientras dure esa diferencia. Una cola persistente puede absorber un pico, pero **el trabajo pendiente no desaparece**.

```text
Productor -> cola creciente -> consumidor limitado
                    |
                    +-> mayor espera, almacenamiento y costo de recuperación
```

| Pregunta | Decisión necesaria |
|---|---|
| ¿Cuánto atraso es aceptable? | Objetivo de tiempo para completar el trabajo. |
| ¿Cuánto puede acumularse? | Límite de almacenamiento o cuota. |
| ¿Qué ocurre al alcanzar el límite? | Rechazar, pausar o reducir producción. |
| ¿Se pueden sumar consumidores? | Verificar particiones, recursos y orden requerido. |

**Backpressure** hace visible al productor que el receptor no puede sostener el ritmo. Agregar consumidores sin límite puede trasladar la saturación a la base de datos o a una API externa.

---

## 14. Desacoplamiento temporal y trabajo pendiente

Un mensaje duradero permite que productor y consumidor **no estén ejecutándose al mismo tiempo**, si el intermediario conserva el mensaje con la garantía necesaria. Eso es desacoplamiento temporal.

```text
Pedidos confirma ahora.
Estadísticas está detenido.
El mensaje queda pendiente.
Estadísticas vuelve y procesa después.
```

Pero hay costos: el resultado llega tarde, la cola puede crecer y un mensaje puede volverse irrelevante o incompatible con una versión nueva. Definí cuánto tiempo tiene sentido procesarlo y qué hacer con trabajo viejo.

Una llamada a un listener **en memoria** dentro del mismo proceso puede separar tareas, pero no garantiza supervivencia al reinicio. Es útil para algunas acciones no críticas; para una obligación de negocio, la durabilidad debe estar respaldada por el almacenamiento o broker elegido.

---

## 15. Publicar sin perder un cambio confirmado

Guardar un pedido y luego publicar un evento son dos escrituras independientes si ocurren en sistemas distintos.

```text
Base de Pedidos: COMMIT exitoso.
Broker: publicación falla.
Pedido existe, pero nadie recibió su evento.
```

El patrón **outbox** guarda el pedido y un registro de evento pendiente **en la misma transacción local**. Después, un publicador lee los pendientes y los envía.

```text
Transacción: pedido P-42 + evento pendiente E-91 -> COMMIT.
Publicador: E-91 -> broker -> marcar progreso.
```

Si el publicador envía y cae antes de marcar progreso, **puede enviar E-91 otra vez**. Por eso outbox reduce el riesgo de perder la intención de publicar, pero exige consumidores que toleren duplicados. También requiere supervisar pendientes antiguos y errores de publicación.

---

## 16. Solicitar una respuesta mediante mensajes

La mensajería también puede transportar una **solicitud y su resultado**. Se necesita un ID de correlación para unir ambos.

```text
Solicitud: ReservarStock, operacionId=R-817.
Respuesta posterior: StockReservado, operacionId=R-817.
```

| Pregunta | Respuesta de diseño |
|---|---|
| ¿Dónde queda el solicitante mientras espera? | Estado `PENDIENTE` u otro estado explícito. |
| ¿Cómo se une la respuesta a la solicitud? | ID de operación/correlación. |
| ¿Qué pasa si no llega? | Timeout de espera, consulta o reconciliación. |
| ¿Qué pasa si llega dos veces? | Aplicación idempotente del resultado. |
| ¿Qué pasa si llega tarde? | Regla para el estado actual y quizá compensación. |

Si el solicitante **bloquea esperando** la respuesta del mensaje en el mismo flujo, el sistema quizá no ganó la ventaja de la asincronía. Puede ser válido, pero debe ser una elección consciente.

---

## 17. Evolución del formato y compatibilidad

Los productores y consumidores no siempre se despliegan juntos. Durante un cambio puede haber mensajes antiguos en espera y consumidores con versiones distintas.

```text
Versión 1: PedidoConfirmado { eventoId, pedidoId }
Versión 2: PedidoConfirmado { eventoId, pedidoId, canalOpcional }
```

Agregar un campo **opcional** suele ser más fácil de tolerar que renombrar o eliminar uno requerido, pero la compatibilidad real depende de cómo deserializan los consumidores y de las reglas de negocio. No supongas que «JSON acepta campos extras» significa que todos los consumidores los aceptarán.

| Cambio | Pregunta de compatibilidad |
|---|---|
| Agregar campo opcional | ¿Qué valor asume un consumidor antiguo? |
| Cambiar significado de un campo | ¿Una versión vieja interpretará mal el mismo nombre? |
| Eliminar campo | ¿Quedan mensajes o consumidores que lo necesitan? |
| Cambiar tipo | ¿Se pueden leer mensajes anteriores? |

El contrato del mensaje debe documentar **identidad, semántica, tipos y reglas**, no solo la forma de serialización.

---

## 18. Observar el flujo y medir el atraso

Para saber si la mensajería cumple su propósito, medí tanto la **aceptación** como la **finalización real del trabajo**.

| Señal | Qué muestra |
|---|---|
| Mensajes producidos | Cuánto trabajo entra. |
| Mensajes completados | Cuánto trabajo termina. |
| Edad del más antiguo pendiente | Cuánto lleva esperando un trabajo. |
| Reintentos y mensajes apartados | Fallos que necesitan atención. |
| Tiempo de extremo a extremo | Desde el hecho hasta el efecto del consumidor. |

Usá `eventoId`, `operacionId` y el ID de la entidad para seguir una historia entre productor, broker y consumidor. **Un broker vacío no demuestra que el efecto del negocio haya terminado** si el consumidor confirma antes de guardar su trabajo.

No registres cargas completas con datos sensibles solo para diagnosticar. IDs, versiones, estados y causas suelen dar una base más segura y legible.

---

## 19. Ejemplo en Java: una cola dentro del mismo proceso

Este ejemplo muestra que un productor puede entregar trabajo y un consumidor puede procesarlo en otro hilo. **La cola es memoria del proceso: no es un broker duradero ni garantiza recuperación tras reinicio.**

```java
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public final class EjemploColaLocal {
    record Trabajo(String id, String pedidoId) {}

    public static void main(String[] args) throws Exception {
        BlockingQueue<Trabajo> cola = new ArrayBlockingQueue<>(2);
        ExecutorService ejecutor = Executors.newSingleThreadExecutor();

        try {
            Future<?> consumidor = ejecutor.submit(() -> {
                try {
                    Trabajo trabajo = cola.take();
                    System.out.println("Procesando " + trabajo.pedidoId());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });

            cola.put(new Trabajo("T-1", "P-42"));
            // El productor pudo continuar tras entregar el trabajo.
            consumidor.get(); // Este ejemplo espera para mostrar la finalización.
        } finally {
            ejecutor.shutdownNow();
        }
    }
}
```

`put` puede **bloquear** cuando la cola está llena; esa capacidad limitada ejerce presión sobre el productor. `take` retira el elemento antes de ejecutar el efecto. Si el proceso se cae después de `take` y antes de terminar, **el trabajo se pierde**. El código sirve para aprender la separación entre producir y consumir, no para implementar una obligación duradera de negocio.

---

## 20. Caso completo: pedido confirmado y tareas posteriores

**Problema:** después de confirmar un pedido, Auditoría debe registrar el hecho y Estadísticas debe actualizar un resumen. La confirmación del pedido es la operación principal; ambas tareas posteriores pueden completarse más tarde.

### Paso 1: definir el hecho y sus responsables

```text
Hecho: PedidoConfirmado P-42, evento E-91.

Pedidos: dueño del estado confirmado.
Auditoría: guarda el hecho en su propio almacenamiento.
Estadísticas: actualiza un resumen derivado.
```

El evento se llama en pasado porque **el pedido ya fue confirmado**. Un error en Estadísticas no debe reescribir ese hecho como si nunca hubiera ocurrido; debe dejar visible el trabajo pendiente o fallido.

### Paso 2: publicación recuperable

Pedidos guarda su cambio y el evento pendiente `E-91` en una transacción local. Un publicador envía el evento al intermediario y registra progreso. Si pierde la confirmación, puede reenviarlo. Auditoría y Estadísticas tienen **suscripciones independientes**, de modo que una puede avanzar mientras la otra está detenida.

### Paso 3: procesar sin duplicar efectos

Auditoría usa `eventoId` para que el mismo evento no cree dos registros de auditoría. Estadísticas evita sumar el pedido dos veces si recibe `E-91` de nuevo. Cada consumidor debe guardar **su marca de procesamiento y su propio efecto** con la garantía atómica que permita su almacenamiento.

| Situación | Resultado correcto |
|---|---|
| Pedido confirma, publicador aún no envía | Evento queda pendiente y observable. |
| Publicador envía dos veces | Consumidores reconocen `E-91`. |
| Auditoría funciona, Estadísticas falla | Auditoría queda completa; Estadísticas se reintenta o aparta. |
| Mensaje de versión antigua | Consumidor lo interpreta según contrato compatible o lo rechaza explícitamente. |
| Cola de Estadísticas crece | Se mide atraso y se limita la carga; el usuario no ve un resumen falsamente actualizado. |

**Alcance de la promesa:** confirmar el pedido no significa que las dos tareas posteriores ya terminaron. Si una de ellas fuera requisito legal inmediato, el flujo y el estado comunicado al usuario tendrían que diseñarse de otro modo.

---

## 21. Contrato de evento y consumidor en Java

El mensaje puede modelarse como un valor inmutable con campos suficientes para identificarlo e interpretarlo:

```java
import java.time.Instant;
import java.util.Objects;

record PedidoConfirmado(
        String eventoId,
        String pedidoId,
        Instant ocurridoEn,
        int version) {
    PedidoConfirmado {
        if (eventoId == null || eventoId.isBlank()
                || pedidoId == null || pedidoId.isBlank()
                || version <= 0) {
            throw new IllegalArgumentException("Evento inválido");
        }
        Objects.requireNonNull(ocurridoEn);
    }
}

interface AuditoriaDePedidos {
    // Registra el hecho como máximo una vez por eventoId.
    // La marca de procesado y el registro de auditoría se confirman juntos.
    void registrarUnaVez(PedidoConfirmado evento);
}
```

La interfaz **declara** la garantía; no la implementa. Un consumidor real necesita convertir bytes a este contrato, validar la versión, aplicar el efecto con persistencia adecuada y decidir cuándo confirmar el mensaje al broker.

**Pregunta de implementación:** si `registrarUnaVez` confirma en su base y el proceso cae antes de confirmar al broker, ¿qué ocurre al recibir el evento otra vez? Debe encontrar `eventoId` y no crear otro registro.

---

## 22. Errores frecuentes de diseño

| Suposición | Problema | Pregunta correcta |
|---|---|---|
| «Asíncrono significa que el trabajo terminó» | Solo se inició o aceptó. | ¿Cuál es el estado real y cómo se consulta? |
| «Publicar después del COMMIT es suficiente» | Puede caer entre ambas escrituras. | ¿Dónde queda registrada la intención de publicar? |
| «El broker entregó una vez, entonces el efecto ocurrió una vez» | El consumidor puede caer antes de confirmar o reintentarse. | ¿Dónde se guarda el efecto junto a su ID? |
| «El evento trae una fecha, así que está ordenado» | Relojes y procesamiento pueden diferir. | ¿Qué garantía de orden existe por clave? |
| «Reintentar todo arregla los errores» | Un mensaje inválido falla indefinidamente. | ¿Es transitorio o permanente? |
| «La cola aguanta cualquier pico» | El atraso y el almacenamiento crecen. | ¿Cuál es el límite y la política de backpressure? |
| «Mover correo a otro hilo lo vuelve duradero» | Un reinicio puede perder trabajo en memoria. | ¿Qué componente conserva la tarea? |
| «Apartar el mensaje lo resolvió» | El efecto de negocio sigue pendiente. | ¿Quién investiga y cómo se reprocesa? |

---

## 23. Ejercicios de razonamiento

Para cada caso, definí **tipo de mensaje, ID, responsable, confirmación, reintento, duplicados y estado visible**.

| Caso | Pregunta central |
|---|---|
| **Generar PDF** | ¿Es comando o evento? ¿Puede perderse si el proceso reinicia? |
| **Pedido confirmado** | ¿Qué consumidores independientes necesitan conocer el hecho? |
| **Reserva de stock** | ¿Cómo llega el resultado y qué estado tiene el pedido mientras tanto? |
| **Correo enviado dos veces** | ¿El proveedor externo acepta una clave idempotente o cómo se gestiona la incertidumbre? |
| **Mensaje inválido** | ¿Cuándo deja de reintentarse y quién lo revisa? |
| **Eventos de un pedido** | ¿Qué orden debe conservarse y qué clave usarías? |
| **Pico de ventas** | ¿Cuánto trabajo pendiente es aceptable y qué pasa al superar ese límite? |

**Desafío:** diseñá el flujo de una notificación de pedido donde el servicio de correo tarda una hora en recuperarse. Especificá qué dice la aplicación al usuario, cómo se observa el atraso y qué evita enviar dos veces el mismo correo si se pierde una confirmación del proveedor.

---

## 24. Lista de verificación antes de usar un broker

- [ ] Sé qué trabajo debe completarse antes de responder y cuál puede quedar pendiente.
- [ ] Distingo comandos, eventos y consultas por su significado.
- [ ] Definí quién produce, quién consume y quién es dueño del dato.
- [ ] Cada mensaje tiene IDs, tipo, versión y datos suficientes para su propósito.
- [ ] Escribí qué orden se necesita y para qué entidad.
- [ ] Sé qué confirma cada etapa: publicación, entrega y efecto de negocio.
- [ ] Los consumidores toleran reentregas sin duplicar efectos críticos.
- [ ] La intención de publicar sobrevive a fallos entre base y broker.
- [ ] Hay reintentos acotados y tratamiento de mensajes que no se recuperan solos.
- [ ] Existe un límite de trabajo pendiente y una respuesta cuando se alcanza.
- [ ] El formato puede evolucionar mientras quedan productores y consumidores antiguos.
- [ ] Puedo observar atraso, mensajes fallidos y tiempo de extremo a extremo.

**Idea central:** la mensajería separa el momento de **pedir o informar algo** del momento de **hacerlo y conocer el resultado**. Ese espacio entre ambos momentos debe tener identidad, estados, límites y reglas de recuperación.
