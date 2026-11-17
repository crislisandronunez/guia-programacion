# Glosario y Guía de Sistemas Distribuidos y Consistencia

Esta guía explica **qué cambia cuando un sistema depende de varios procesos conectados por red**. Presenta fallos parciales, réplicas, orden de operaciones, consistencia y coordinación antes de elegir mensajes o tecnologías concretas. Los ejemplos usan **Java** para expresar contratos y estados; las garantías reales deben sostenerse también en los componentes que almacenan y comunican los datos.

---

## Índice

1. [De un programa local a un sistema distribuido](#1-de-un-programa-local-a-un-sistema-distribuido)
2. [Glosario fundamental](#2-glosario-fundamental)
3. [Fallos parciales y resultados desconocidos](#3-fallos-parciales-y-resultados-desconocidos)
4. [Identificar operaciones para poder repetirlas](#4-identificar-operaciones-para-poder-repetirlas)
5. [Relojes, orden y causalidad](#5-relojes-orden-y-causalidad)
6. [Réplicas: varias copias de un dato](#6-réplicas-varias-copias-de-un-dato)
7. [Modelos de consistencia para lecturas y escrituras](#7-modelos-de-consistencia-para-lecturas-y-escrituras)
8. [Disponibilidad, particiones de red y CAP](#8-disponibilidad-particiones-de-red-y-cap)
9. [Líderes, quórums y decisiones compartidas](#9-líderes-quórums-y-decisiones-compartidas)
10. [Particionar datos y ubicar su dueño](#10-particionar-datos-y-ubicar-su-dueño)
11. [Detectar fallos sin confundir demora con caída](#11-detectar-fallos-sin-confundir-demora-con-caída)
12. [Mensajes, confirmaciones y duplicados](#12-mensajes-confirmaciones-y-duplicados)
13. [El problema de escribir en dos sistemas](#13-el-problema-de-escribir-en-dos-sistemas)
14. [Outbox y consumidores idempotentes](#14-outbox-y-consumidores-idempotentes)
15. [Operaciones largas y compensaciones](#15-operaciones-largas-y-compensaciones)
16. [Cachés, datos atrasados y experiencia del usuario](#16-cachés-datos-atrasados-y-experiencia-del-usuario)
17. [Límites de dominio y propiedad de los datos](#17-límites-de-dominio-y-propiedad-de-los-datos)
18. [Diseñar para demoras, saturación y recuperación](#18-diseñar-para-demoras-saturación-y-recuperación)
19. [Observar una operación entre varios procesos](#19-observar-una-operación-entre-varios-procesos)
20. [Caso completo: pedido y reserva de stock](#20-caso-completo-pedido-y-reserva-de-stock)
21. [Contratos y estados del caso en Java](#21-contratos-y-estados-del-caso-en-java)
22. [Errores frecuentes de razonamiento](#22-errores-frecuentes-de-razonamiento)
23. [Ejercicios de diseño](#23-ejercicios-de-diseño)
24. [Lista de verificación para un flujo distribuido](#24-lista-de-verificación-para-un-flujo-distribuido)

---

## 1. De un programa local a un sistema distribuido

Un **sistema distribuido** tiene partes que se ejecutan en procesos o máquinas distintos y colaboran mediante comunicación. Cada parte conserva su propio estado y puede avanzar, detenerse o fallar de forma independiente.

```text
Programa local:
  pedido.confirmar() -> resultado dentro del mismo proceso

Sistema distribuido:
  Servicio Pedidos -> red -> Servicio Inventario -> su base de datos
```

La llamada remota puede parecer un método, pero tiene propiedades diferentes: tarda más, puede perder la respuesta y no comparte una sola transacción por defecto. **El objetivo de diseño es definir qué hacer cuando las partes no observan lo mismo al mismo tiempo**.

Varios módulos dentro de una sola aplicación pueden tener límites claros sin estar distribuidos. Separar procesos agrega posibilidades de despliegue y fallos de red; no convierte automáticamente el diseño en mejor.

---

## 2. Glosario fundamental

| Término | Significado | Ejemplo |
|---|---|---|
| **Nodo** | Proceso o máquina que participa en el sistema. | Una instancia del servicio de pedidos. |
| **Fallo parcial** | Una parte falla mientras otras siguen funcionando. | Inventario no responde, pero Pedidos atiende. |
| **Partición de red** | Dos grupos de nodos no pueden comunicarse adecuadamente entre sí. | Dos sedes dejan de intercambiar datos. |
| **Réplica** | Copia de datos mantenida en otro nodo. | Réplica de lectura de una base. |
| **Líder** | Nodo autorizado a coordinar ciertas decisiones o escrituras. | Primario de un conjunto replicado. |
| **Quórum** | Conjunto suficiente de participantes para aceptar una decisión según un protocolo. | Mayoría en un diseño concreto. |
| **Consistencia** | Garantías sobre lo que las operaciones pueden observar y en qué orden. | Una lectura posterior ve una escritura confirmada. |
| **Consistencia eventual** | Si cesan los cambios y la comunicación se recupera, las réplicas convergen según el sistema. | Una réplica tarda en mostrar un nuevo precio. |
| **Linearizabilidad** | Operaciones parecen ocurrir en un único orden compatible con el tiempo real. | Tras confirmar una reserva, una lectura posterior observa el cupo ocupado. |
| **Idempotencia** | Repetir la misma operación identificada no duplica su efecto previsto. | El mismo pedido no reserva stock dos veces. |
| **Deduplicación** | Reconocer una operación o mensaje ya procesado. | Guardar `operacionId` junto al resultado. |
| **Compensación** | Nueva acción que contrarresta un efecto de negocio anterior. | Liberar una reserva tras cancelar un pedido. |
| **Outbox** | Registro local de mensajes pendientes junto al cambio de negocio. | Pedido y evento guardados en una transacción. |
| **Observabilidad** | Evidencia para reconstruir comportamiento entre partes. | ID de operación en logs y eventos. |

**Advertencia de vocabulario:** «consistente» no significa siempre «todas las copias idénticas en todo instante». Hay que decir **qué garantía concreta** necesita cada lectura o escritura.

---

## 3. Fallos parciales y resultados desconocidos

Cuando el cliente no recibe respuesta, hay varias historias posibles:

```text
A. La petición nunca llegó al servidor.
B. Llegó, pero el servidor falló antes de cambiar datos.
C. El servidor cambió datos y falló antes de responder.
D. El servidor respondió, pero la respuesta se perdió.
```

El mismo **timeout observado por el cliente** es compatible con las cuatro. No se puede deducir «no ocurrió» a partir de «no recibí confirmación».

| Observación | Qué se sabe | Qué falta saber |
|---|---|---|
| Error antes de abrir conexión | No se envió por esa conexión. | Si existió otro intento previo. |
| Timeout después de enviar | No hubo respuesta a tiempo. | Si el servidor completó la operación. |
| Respuesta de éxito | El servidor informó éxito. | Si se cumplieron todas las garantías prometidas por su contrato. |

Para una lectura, repetir suele ser más simple. Para un cobro o una reserva, repetir sin identificar la **misma intención** puede duplicar el efecto. Este problema de conocimiento es fundamental en sistemas distribuidos.

---

## 4. Identificar operaciones para poder repetirlas

Una **clave de operación** permite que el receptor distinga un reintento de una solicitud nueva. La clave debe acompañar **la misma intención** en todos sus intentos.

```text
Primer intento: operacionId = R-817, reservar 1 unidad.
Se pierde la respuesta.
Reintento: operacionId = R-817, reservar 1 unidad.

El receptor devuelve el resultado registrado para R-817
sin reservar una segunda unidad.
```

| Regla | Por qué importa |
|---|---|
| Misma clave + mismos datos | Representa el mismo intento lógico. |
| Misma clave + datos distintos | Debe rechazarse o tratarse como conflicto. |
| Clave nueva | Representa una nueva operación que puede tener otro efecto. |
| Resultado persistido | Permite responder después de reinicios y reintentos. |

Un `Set` de claves **solo en memoria** puede ilustrar la idea, pero pierde información al reiniciar y no coordina varias instancias. La deduplicación que protege una regla persistente debe vivir donde se decide el efecto persistente, con una garantía atómica apropiada.

---

## 5. Relojes, orden y causalidad

Cada máquina consulta su propio reloj. Los relojes pueden diferir y ajustarse; **la marca de tiempo por sí sola no demuestra el orden causal entre dos operaciones de nodos distintos**.

```text
Servicio A registra: 10:00:00.120 -> pedido creado.
Servicio B registra: 10:00:00.090 -> reserva procesada.

¿B ocurrió antes que A? No necesariamente: los relojes pueden diferir.
```

| Idea | Pregunta |
|---|---|
| **Orden local** | ¿Qué ocurrió antes en un mismo proceso o registro ordenado? |
| **Causalidad** | ¿Una operación dependía del resultado de otra? |
| **Orden total** | ¿Todos los participantes necesitan acordar el mismo orden? |

Si B procesa un mensaje que A creó, **la creación del mensaje precede causalmente a su procesamiento**, aunque las horas registradas parezcan invertidas. IDs de operación, versiones y posiciones en registros de eventos ayudan a reconstruir relaciones; sus garantías dependen del sistema que los produce.

No uses el reloj de dos servidores como único árbitro para decidir quién ganó una reserva.

---

## 6. Réplicas: varias copias de un dato

Las réplicas pueden mejorar disponibilidad o distribuir lecturas, pero necesitan una forma de incorporar cambios. Durante ese proceso puede existir **retraso de replicación**.

```text
Nodo principal: stock = 4 después de una compra.
Réplica:        stock = 5 durante un intervalo.
```

| Pregunta de diseño | Consecuencia |
|---|---|
| ¿Dónde se acepta una escritura? | Define quién decide el valor nuevo. |
| ¿Cuándo se considera confirmada? | Define qué significa «éxito» para el cliente. |
| ¿De dónde se sirve una lectura? | Define si podría ver un valor anterior. |
| ¿Qué ocurre si una réplica queda aislada? | Define disponibilidad y recuperación. |

Replicar no significa simplemente copiar archivos. Un sistema debe preservar un orden o una regla de resolución de cambios y manejar interrupciones. **La elección correcta depende de qué lecturas pueden tolerar atraso y cuáles no.**

---

## 7. Modelos de consistencia para lecturas y escrituras

No todas las operaciones necesitan la misma garantía. Nombrar la garantía evita frases vagas como «la base debe ser consistente».

| Garantía | Significado práctico | Ejemplo |
|---|---|---|
| **Linearizabilidad** | Una operación completada se refleja en las siguientes operaciones según un orden único compatible con el tiempo real. | Dos usuarios no reciben el último cupo. |
| **Lectura después de escritura propia** | Un usuario puede ver el cambio que acaba de confirmar. | Cambia su dirección y la ve al recargar. |
| **Lecturas monotónicas** | Tras ver una versión nueva, ese usuario no vuelve a una versión anterior. | No reaparece un estado viejo en una siguiente pantalla. |
| **Consistencia eventual** | Sin nuevos cambios, las copias acaban convergiendo bajo las condiciones del sistema. | Un contador informativo se actualiza más tarde. |

**Ejemplo de decisión:** el conteo aproximado de vistas puede tolerar atraso; la operación que asigna el **último ejemplar disponible** no debería aceptar dos ganadores. Un mismo producto puede usar garantías distintas para datos distintos.

**No confundas:** el nivel de aislamiento de una transacción en una sola base de datos y el modelo de consistencia entre réplicas o servicios responden problemas relacionados, pero no idénticos.

---

## 8. Disponibilidad, particiones de red y CAP

Una **partición** impide que ciertos nodos se comuniquen. Si ambos lados siguen aceptando cambios sobre el mismo dato sin poder coordinarse, pueden producir respuestas incompatibles.

```text
Grupo A: cree que queda 1 cupo.
Grupo B: cree que queda 1 cupo.
La red entre ambos está cortada.

Si ambos prometen reservarlo con una respuesta definitiva,
la regla «un solo ganador» puede romperse.
```

La idea de **CAP** señala un límite: durante una partición no se pueden garantizar simultáneamente ciertas formas de **consistencia fuerte** y **disponibilidad de respuesta** para todas las solicitudes en todos los lados. Una opción puede rechazar o posponer operaciones que no puede coordinar; otra puede responder con datos que luego necesiten reconciliación.

No etiquetes un producto entero como «CP» o «AP» sin explicar **qué operación, qué dato y qué garantía** se está evaluando. Una pantalla de catálogo puede mostrar datos atrasados mientras una reserva exige decisión única.

---

## 9. Líderes, quórums y decisiones compartidas

Algunos sistemas eligen un **líder** que coordina escrituras o decisiones. Otros requieren respuestas de un **quórum** de participantes antes de considerar una operación confirmada. Los detalles dependen del protocolo.

```text
Nodos A, B y C.
El protocolo exige acuerdo de una mayoría para una decisión.
Si A queda aislado de B y C, B y C pueden formar esa mayoría;
A no debería aceptar por sí solo una decisión que exige mayoría.
```

La mayoría es un ejemplo común de quórum, **no una regla universal para todo sistema**. Importa que los conjuntos de participantes de decisiones incompatibles no puedan actuar como si ambos tuvieran autoridad.

Un líder también puede fallar. Elegir otro requiere detectar el problema y resolver quién tiene autoridad; esa transición puede tomar tiempo. **No presupongas que «hay tres nodos» equivale a «siempre disponible»**: depende de qué falló y de la garantía requerida.

---

## 10. Particionar datos y ubicar su dueño

**Particionar** reparte datos entre grupos o nodos según una clave. Puede distribuir carga, pero obliga a saber dónde consultar y dónde coordinar cambios.

```text
Clave: productoId.
Todas las operaciones del producto P-42 se dirigen al grupo dueño de P-42.
```

| Pregunta | Efecto |
|---|---|
| ¿Qué clave agrupa los cambios que deben coordinarse? | Puede mantener una regla local a una partición. |
| ¿Una clave recibe casi toda la carga? | Aparece una partición caliente. |
| ¿Qué operaciones cruzan particiones? | Pueden requerir coordinación adicional. |
| ¿Qué ocurre al mover datos? | Hay que preservar autoridad durante el cambio. |

Un hash puede distribuir claves, pero no decide por sí mismo cómo conservar una regla que abarca varios productos o cuentas. Primero definí el **límite de consistencia**; después elegí una clave de partición compatible.

---

## 11. Detectar fallos sin confundir demora con caída

Desde otro proceso, un nodo lento y uno caído pueden verse igual durante un tiempo: **no llega respuesta**. Un timeout permite dejar de esperar, pero no prueba cuál fue la causa.

```text
No responde porque:
- está apagado;
- la red está cortada;
- está saturado;
- está procesando lentamente;
- la respuesta se perdió.
```

Por eso los detectores de fallos trabajan con señales y plazos, no con conocimiento perfecto. El diseño debe definir qué puede hacer un cliente mientras el estado del otro es **desconocido**.

Para una operación irreversible, quizá convenga consultar por `operacionId` antes de volver a intentarla. Para una lectura, quizá se pueda consultar una réplica con aviso de posible atraso. La respuesta depende del contrato del negocio.

---

## 12. Mensajes, confirmaciones y duplicados

Un emisor puede enviar un mensaje y esperar una **confirmación**. Si la confirmación se pierde, puede reenviarlo. El receptor entonces podría observar el mensaje dos veces.

```text
Emisor -> mensaje M -> Receptor procesa M.
Receptor -> confirmación -> se pierde.
Emisor -> reenvía M -> Receptor vuelve a verlo.
```

| Estrategia | Riesgo principal |
|---|---|
| No reenviar cuando falta confirmación | Se puede perder trabajo que nunca llegó. |
| Reenviar hasta confirmar | El receptor puede procesar duplicados. |

Una garantía de transporte o de un broker **no implica automáticamente que el efecto de negocio ocurra una sola vez**. Si el efecto es descontar stock, el consumidor debe reconocer que `M` ya produjo un resultado y devolver ese resultado sin descontar de nuevo.

La futura guía de eventos y mensajería desarrollará colas, suscripciones, orden y confirmaciones. Aquí importa la relación entre **incertidumbre, reintento y deduplicación**.

---

## 13. El problema de escribir en dos sistemas

Supongamos que Pedidos guarda un pedido en su base y luego publica un evento para Inventario.

```text
1. Guardar pedido en base A: éxito.
2. Publicar evento en sistema B: falla.

Resultado: el pedido existe, pero Inventario no recibió el aviso.
```

Si invertís el orden, puede publicarse el evento y luego fallar el guardado. **Dos escrituras independientes no se convierten en una sola transacción por colocarlas en el mismo método Java.**

Este problema se llama a menudo **doble escritura (*dual write*)**. Para resolverlo hay que elegir una garantía explícita. Una transacción local protege lo que sucede en su propia base; no confirma automáticamente otra base, un broker o una llamada HTTP.

---

## 14. Outbox y consumidores idempotentes

El patrón **outbox** guarda el cambio de negocio y un registro de mensaje pendiente **en la misma transacción de la misma base**. Un proceso posterior publica el mensaje.

```text
Transacción en Pedidos:
  guardar pedido P-42
  guardar evento pendiente E-91
  COMMIT

Publicador:
  leer E-91 -> publicar -> registrar progreso
```

Si el publicador se cae **después de publicar y antes de registrar el progreso**, volverá a publicar `E-91`. Por eso outbox protege contra la pérdida de intención de publicar, pero **no elimina duplicados por sí solo**.

El consumidor puede conservar `eventoId` junto con el efecto producido, de forma atómica en su propia base. Si vuelve a recibir `E-91`, reconoce el ID y evita repetir el efecto. La implementación concreta debe definir orden de publicación, reintentos, fallos permanentes y retención de IDs procesados.

---

## 15. Operaciones largas y compensaciones

Una operación de negocio puede atravesar varios servicios sin una transacción única. Si el paso posterior falla, puede necesitarse una **compensación**.

```text
1. Crear pedido pendiente.
2. Reservar stock.
3. Cobrar.
4. Si el cobro falla, solicitar liberar la reserva.
```

Liberar stock **no es un rollback mágico del tiempo**: es una nueva acción que también puede fallar o llegar tarde. Debe tener identificador, regla de idempotencia y forma de saber si terminó.

| Decisión | Pregunta |
|---|---|
| Estado intermedio | ¿Qué ve el usuario mientras faltan pasos? |
| Compensación | ¿Se puede deshacer realmente el efecto? |
| Reintento | ¿Puede repetirse sin duplicar el efecto? |
| Supervisión | ¿Qué operación quedó atascada y requiere atención? |

Una **saga** organiza estos pasos y compensaciones, mediante coordinación central o reacción a eventos. Elegir su forma depende del flujo; no hay un número de pasos que obligue universalmente a una variante.

---

## 16. Cachés, datos atrasados y experiencia del usuario

Un caché y una réplica de lectura pueden devolver un dato anterior aunque la escritura ya se haya confirmado en otro lugar.

```text
Usuario cambia su dirección a "Calle 2".
La escritura confirma en la fuente principal.
La pantalla lee un caché que aún muestra "Calle 1".
```

Si el usuario debe ver inmediatamente su propio cambio, el sistema puede leer de la fuente que confirmó la escritura, actualizar o invalidar el caché, o usar otra estrategia que garantice **lectura después de escritura propia**. Cada alternativa tiene costo y posibles ventanas de error; la promesa al usuario debe ser concreta.

Para un contador informativo, mostrar un dato levemente atrasado quizá sea aceptable. Para confirmar que queda el último cupo, una lectura atrasada no puede decidir sola la reserva: **la escritura definitiva debe proteger la regla**.

---

## 17. Límites de dominio y propiedad de los datos

Cada dato importante necesita una **fuente de verdad** y una parte responsable de sus cambios. Copias para lectura pueden existir, pero deben identificarse como copias con una política de actualización.

| Dato | Dueño posible | Copias posibles |
|---|---|---|
| Estado de un pedido | Pedidos | Vista de atención al cliente. |
| Stock reservable | Inventario | Indicador aproximado en catálogo. |
| Precio acordado de un pedido | Pedidos | Resumen de facturación. |

El catálogo puede mostrar «quedan pocas unidades» desde una copia atrasada. El sistema que **acepta** la reserva debe consultar o modificar la fuente con la garantía necesaria. Si dos servicios creen ser dueños del mismo stock y escriben sin coordinarse, la arquitectura ya contiene una contradicción.

Distribuir responsabilidades debería seguir límites del problema, no simplemente separar controladores, servicios y repositorios en procesos distintos.

---

## 18. Diseñar para demoras, saturación y recuperación

Cada dependencia tiene tiempo y capacidad finitos. Si se vuelve lenta, las solicitudes pendientes pueden acumularse y afectar partes que sí funcionan.

| Mecanismo | Pregunta que responde |
|---|---|
| **Timeout** | ¿Cuánto esperamos este intento? |
| **Límite de concurrencia** | ¿Cuántas llamadas simultáneas admitimos? |
| **Reintento acotado** | ¿Qué fallos merecen otro intento y con qué demora? |
| **Cola o trabajo pendiente** | ¿Cuánto puede esperar sin saturar memoria? |
| **Degradación** | ¿Qué respuesta útil podemos dar si una función secundaria falla? |

Un reintento aumenta carga sobre la dependencia. Si todos reintentan de inmediato, una recuperación puede volverse más difícil. **La resiliencia no consiste en esconder todos los errores**: para una reserva crítica, puede ser más honesto informar «pendiente» o «no pudimos confirmarla» que inventar un éxito.

También diseñá la recuperación: ¿cómo se encuentran pedidos pendientes, mensajes no publicados o reservas sin respuesta después de reiniciar?

---

## 19. Observar una operación entre varios procesos

Un fallo distribuido se investiga siguiendo **una operación de extremo a extremo**, no leyendo un log aislado.

```text
pedidoId=P-42
operacionReservaId=R-817
eventoId=E-91

Pedidos: creó P-42 y E-91.
Publicador: envió E-91 dos veces por pérdida de confirmación.
Inventario: procesó R-817 una vez y devolvió el mismo resultado en el reintento.
Pedidos: aplicó el resultado y confirmó P-42.
```

| Dato de observación | Uso |
|---|---|
| Identificador de operación | Une intentos del mismo trabajo. |
| Identificador de mensaje | Detecta reenvíos y duplicados. |
| Estado antes y después | Explica transiciones. |
| Tiempo y componente | Localiza demoras, sin asumir orden causal solo por el reloj. |
| Edad de trabajo pendiente | Detecta operaciones atascadas. |

No registres secretos para poder reconstruir una operación. IDs, estados y causas de error suelen ser suficientes para empezar; después se agregan mediciones más específicas según el problema.

---

## 20. Caso completo: pedido y reserva de stock

**Problema:** Pedidos registra una compra y necesita que Inventario reserve una unidad del producto. Las aplicaciones y sus bases son independientes. El usuario debe saber si el pedido quedó confirmado, rechazado o todavía pendiente.

### Paso 1: definir reglas y estados

```text
Pedido: PENDIENTE_RESERVA -> CONFIRMADO
                          -> RECHAZADO

Reglas:
- Confirmado significa que Inventario confirmó la reserva.
- Rechazado significa que Inventario negó la reserva.
- Pendiente no se comunica como éxito definitivo.
- Una misma operación de reserva no descuenta stock dos veces.
```

### Paso 2: guardar la intención de pedir reserva

Pedidos guarda `pedidoId`, `estado=PENDIENTE_RESERVA` y un evento de solicitud con `operacionReservaId` en **una transacción local**. Si esa transacción falla, no queda pedido ni intención de publicar. Si confirma, el publicador puede enviar la solicitud posteriormente.

### Paso 3: decidir en Inventario

Inventario recibe la solicitud y, en su propio almacenamiento, debe realizar **como una unidad**:

1. Comprobar si `operacionReservaId` ya tiene resultado; si lo tiene, devolverlo.
2. Si es nueva, intentar descontar stock sin permitir valor negativo.
3. Guardar el resultado vinculado al ID de operación.

Si dos solicitudes distintas compiten por la última unidad, solo una debe obtener una reserva confirmada. El mecanismo concreto puede ser una escritura condicional y restricciones de la base; **no basta un `synchronized` en uno de los procesos**.

### Paso 4: cerrar el pedido o recuperar el estado

Inventario comunica `CONFIRMADA` o `RECHAZADA` con el mismo ID. Pedidos aplica el resultado al pedido si la transición sigue siendo válida. Los mensajes repetidos no deben cambiar de nuevo el estado. Si el resultado no llega, el pedido permanece **pendiente** y puede consultarse o reconciliarse mediante `operacionReservaId`.

| Fallo | Estado que permanece | Recuperación necesaria |
|---|---|---|
| Pedidos falla antes de confirmar su transacción | No hay pedido confirmado. | El usuario puede iniciar una nueva operación según el contrato. |
| Publicador envía y pierde confirmación | Pedido pendiente; solicitud quizá procesada. | Reenviar el mismo evento/ID. |
| Inventario reserva y se pierde la respuesta | Pedido pendiente; stock reservado. | Consultar o repetir con el mismo ID, sin segundo descuento. |
| Pedidos recibe resultado duplicado | Pedido ya confirmado o rechazado. | Reconocer el resultado aplicado y no repetir la transición. |

---

## 21. Contratos y estados del caso en Java

Java permite expresar el significado de la operación, pero **una interfaz no implementa por sí sola atomicidad, durabilidad ni comunicación**. Esas garantías forman parte del contrato que debe cumplir la implementación real.

```java
import java.util.Objects;

record SolicitudReserva(
        String operacionId,
        String pedidoId,
        String productoId,
        int cantidad) {
    SolicitudReserva {
        if (operacionId == null || operacionId.isBlank()
                || pedidoId == null || pedidoId.isBlank()
                || productoId == null || productoId.isBlank()
                || cantidad <= 0) {
            throw new IllegalArgumentException("Solicitud de reserva inválida");
        }
    }
}

enum EstadoReserva { CONFIRMADA, RECHAZADA }

record ResultadoReserva(String operacionId, EstadoReserva estado) {
    ResultadoReserva {
        if (operacionId == null || operacionId.isBlank()) {
            throw new IllegalArgumentException("Falta el ID de operación");
        }
        Objects.requireNonNull(estado);
    }
}

interface Reservas {
    // Misma operación y mismos datos -> mismo resultado persistido.
    // Misma operación con datos diferentes -> conflicto explícito.
    // Si confirma, descuenta stock exactamente una vez y nunca deja stock negativo.
    // Resultado y cambio de stock deben confirmarse de forma atómica.
    ResultadoReserva reservarUnaVez(SolicitudReserva solicitud);
}
```

El contrato exige que el resultado se conserve para reintentos. Una implementación que use solo un `HashMap` local **no cumple** esa promesa después de reiniciar ni cuando hay varias instancias. El trabajo del diseño distribuido es llevarla a un almacenamiento y un protocolo que realmente la sostengan.

Para el pedido, las transiciones también deben ser explícitas:

```java
enum EstadoPedido { PENDIENTE_RESERVA, CONFIRMADO, RECHAZADO }

static EstadoPedido aplicarResultado(
        EstadoPedido actual, EstadoReserva resultado) {
    if (actual != EstadoPedido.PENDIENTE_RESERVA) {
        return actual; // un resultado repetido no reabre el pedido.
    }
    return resultado == EstadoReserva.CONFIRMADA
            ? EstadoPedido.CONFIRMADO
            : EstadoPedido.RECHAZADO;
}
```

Este fragmento muestra **la regla de transición**. En una implementación real, aplicar el resultado debe verificar además que corresponde al pedido y a su `operacionId`, y proteger la escritura concurrente del estado. No se debe aceptar un resultado de otra operación solo porque diga `CONFIRMADA`.

---

## 22. Errores frecuentes de razonamiento

| Suposición incorrecta | Qué puede pasar | Pregunta adecuada |
|---|---|---|
| «No hubo respuesta, entonces no ocurrió» | El servidor pudo confirmar y perderse la respuesta. | ¿Cómo consulto el resultado por ID? |
| «Reintentar siempre es seguro» | Se duplica un cobro o reserva. | ¿La misma intención tiene un ID y efecto idempotente? |
| «Todas las réplicas ven el cambio ya» | Una lectura muestra un estado anterior. | ¿Qué garantía necesita esta lectura? |
| «El reloj decide el orden» | Relojes distintos producen una secuencia engañosa. | ¿Qué relación causal o versión tengo? |
| «Dos escrituras en un método son atómicas» | Una confirma y la otra falla. | ¿Dónde está el límite real de transacción? |
| «Outbox elimina duplicados» | El publicador reenvía tras perder una confirmación. | ¿Cómo deduplica el consumidor? |
| «Hay varios nodos, por eso siempre responde» | Un quórum puede no formarse durante fallos. | ¿Qué garantía exige esta operación? |
| «La compensación deshace mágicamente todo» | Es otra operación que puede fallar. | ¿Cómo se identifica, repite y supervisa? |

---

## 23. Ejercicios de diseño

Para cada caso, escribí **fuente de verdad, estado intermedio, ID de operación, resultado posible y recuperación tras un fallo**. Después elegí qué lectura puede tolerar atraso.

| Caso | Pregunta principal |
|---|---|
| **Último asiento** | ¿Qué componente decide el ganador si dos usuarios reservan a la vez? |
| **Perfil de usuario** | ¿Qué ocurre si una réplica muestra el nombre anterior tras guardarlo? |
| **Cobro con timeout** | ¿Cómo distinguís un reintento de un nuevo cobro? |
| **Evento duplicado** | ¿Dónde se guarda que ya produjo su efecto? |
| **Pedido y stock** | ¿Qué estado ve el usuario mientras Inventario no responde? |
| **Tres nodos y una partición** | ¿Qué lado puede confirmar una decisión que exige mayoría? |
| **Publicación fallida** | ¿Cómo queda registrada la intención de publicar? |

**Desafío:** en el caso del pedido, Inventario confirma la reserva, pero el evento de resultado tarda una hora. Diseñá una consulta de reconciliación por `operacionReservaId`. Explicá qué hace Pedidos si recibe el resultado **después** de que un operador canceló el pedido y qué compensación podría necesitarse.

---

## 24. Lista de verificación para un flujo distribuido

- [ ] Identifiqué procesos, redes y almacenamientos que participan.
- [ ] Sé qué parte es dueña de cada dato y qué copias pueden atrasarse.
- [ ] Distingo «falló», «fue rechazado» y «resultado desconocido».
- [ ] Cada operación repetible tiene un identificador estable y un contrato idempotente.
- [ ] Definí qué ocurre con mensajes o respuestas duplicados.
- [ ] Escribí la garantía de consistencia requerida para cada lectura y escritura crítica.
- [ ] Sé qué decisión debe coordinarse durante una partición de red.
- [ ] No uso relojes de máquinas distintas como única prueba de orden causal.
- [ ] La intención de comunicar un cambio no puede perderse por una doble escritura ingenua.
- [ ] Los estados intermedios y las compensaciones son visibles y recuperables.
- [ ] Hay límites de espera, carga y reintentos acordes al efecto de negocio.
- [ ] Puedo reconstruir una operación con IDs, transiciones y evidencia de cada componente.

**Idea central:** en un sistema distribuido, la pregunta no es solo **qué pasó**, sino **qué puede saber cada parte y qué garantiza al responder**. Las reglas de consistencia deben expresarse en las operaciones que las necesitan y sostenerse en el almacenamiento y la comunicación reales.
