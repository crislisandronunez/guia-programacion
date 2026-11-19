# Glosario y Guía de Apache Kafka

Esta guía explica **qué problema resuelve Kafka y cómo razonar sobre sus registros, particiones y consumidores** antes de configurar clientes. Parte de los conceptos de asincronía y mensajería; después recorre un laboratorio local, clientes **Java**, integración con **Spring Kafka**, errores, pruebas y operación. Las garantías de Kafka tienen un alcance concreto: publicar un registro, confirmar un offset y completar un efecto de negocio no son la misma operación.

---

## Índice

1. [Qué es Kafka y cuándo tiene sentido usarlo](#1-qué-es-kafka-y-cuándo-tiene-sentido-usarlo)
2. [Glosario fundamental](#2-glosario-fundamental)
3. [El modelo mental: un registro que se conserva](#3-el-modelo-mental-un-registro-que-se-conserva)
4. [Tópicos, particiones, registros y offsets](#4-tópicos-particiones-registros-y-offsets)
5. [Claves y orden por entidad](#5-claves-y-orden-por-entidad)
6. [Brokers, réplicas y confirmación de escritura](#6-brokers-réplicas-y-confirmación-de-escritura)
7. [El productor: enviar, esperar y manejar incertidumbre](#7-el-productor-enviar-esperar-y-manejar-incertidumbre)
8. [Retención, borrado y compactación](#8-retención-borrado-y-compactación)
9. [Grupos de consumidores y reparto de particiones](#9-grupos-de-consumidores-y-reparto-de-particiones)
10. [Posición leída y offset confirmado](#10-posición-leída-y-offset-confirmado)
11. [Inicio de lectura y reprocesamiento](#11-inicio-de-lectura-y-reprocesamiento)
12. [Rebalanceos y trabajo que puede repetirse](#12-rebalanceos-y-trabajo-que-puede-repetirse)
13. [Garantías de entrega y productores idempotentes](#13-garantías-de-entrega-y-productores-idempotentes)
14. [Transacciones de Kafka y límites con otras bases de datos](#14-transacciones-de-kafka-y-límites-con-otras-bases-de-datos)
15. [Errores, reintentos y mensajes apartados](#15-errores-reintentos-y-mensajes-apartados)
16. [Atraso del consumidor y límites de carga](#16-atraso-del-consumidor-y-límites-de-carga)
17. [Formato de mensajes y evolución del contrato](#17-formato-de-mensajes-y-evolución-del-contrato)
18. [Seguridad y permisos](#18-seguridad-y-permisos)
19. [Administración básica del clúster y los tópicos](#19-administración-básica-del-clúster-y-los-tópicos)
20. [Ejemplo de productor con el cliente Java](#20-ejemplo-de-productor-con-el-cliente-java)
21. [Ejemplo de consumidor con el cliente Java](#21-ejemplo-de-consumidor-con-el-cliente-java)
22. [Cómo se ve en Spring Kafka](#22-cómo-se-ve-en-spring-kafka)
23. [Caso completo: pedido confirmado, auditoría y estadísticas](#23-caso-completo-pedido-confirmado-auditoría-y-estadísticas)
24. [Errores frecuentes al diseñar con Kafka](#24-errores-frecuentes-al-diseñar-con-kafka)
25. [Ejercicios de razonamiento](#25-ejercicios-de-razonamiento)
26. [Lista de verificación antes de publicar un evento](#26-lista-de-verificación-antes-de-publicar-un-evento)
27. [Laboratorio local: iniciar Kafka y recorrer un tópico](#27-laboratorio-local-iniciar-kafka-y-recorrer-un-tópico)
28. [Herramientas de consola para investigar](#28-herramientas-de-consola-para-investigar)
29. [Diseñar tópicos para un entorno real](#29-diseñar-tópicos-para-un-entorno-real)
30. [Dependencias y configuración de Spring Boot](#30-dependencias-y-configuración-de-spring-boot)
31. [Contrato del evento y serialización JSON](#31-contrato-del-evento-y-serialización-json)
32. [Productor Spring completo y resultado del envío](#32-productor-spring-completo-y-resultado-del-envío)
33. [Consumidor Spring, confirmación y concurrencia](#33-consumidor-spring-confirmación-y-concurrencia)
34. [Reintentos, DLT y deserialización fallida](#34-reintentos-dlt-y-deserialización-fallida)
35. [Outbox, deduplicación y transacciones](#35-outbox-deduplicación-y-transacciones)
36. [Pruebas de integración y fallos](#36-pruebas-de-integración-y-fallos)
37. [Métricas, lag y diagnóstico](#37-métricas-lag-y-diagnóstico)
38. [Rendimiento, particionado y capacidad](#38-rendimiento-particionado-y-capacidad)
39. [Seguridad y configuración por entorno](#39-seguridad-y-configuración-por-entorno)
40. [Kafka Connect, Streams y límites del ecosistema](#40-kafka-connect-streams-y-límites-del-ecosistema)
41. [Recorrido completo de pedidos con Spring](#41-recorrido-completo-de-pedidos-con-spring)
42. [Referencia rápida y plan de práctica](#42-referencia-rápida-y-plan-de-práctica)
43. [Documentación oficial para profundizar](#43-documentación-oficial-para-profundizar)

---

## 1. Qué es Kafka y cuándo tiene sentido usarlo

**Apache Kafka** es una plataforma distribuida para **publicar, conservar y leer flujos de registros**. Un productor escribe registros en un tópico; uno o varios grupos de consumidores los leen según su propia posición. Kafka puede sostener alto volumen y permite volver a leer registros mientras continúen disponibles según la política de retención.

```text
Servicio Pedidos -> tópico pedidos-confirmados -> grupo Auditoría
                                            \-> grupo Estadísticas
```

Kafka puede servir cuando varias partes necesitan reaccionar a hechos, cuando el trabajo debe desacoplarse temporalmente o cuando interesa conservar y reprocesar un flujo. **No es obligatorio para toda tarea asíncrona**: si un proceso pequeño necesita ejecutar un trabajo local y su pérdida es aceptable, una cola en memoria puede bastar. Si la respuesta debe conocerse antes de seguir, una llamada directa también puede ser más simple.

Antes de elegirlo, definí **el hecho, sus consumidores, el orden necesario, el tiempo de conservación y qué ocurre ante duplicados**. Kafka implementará parte del transporte y almacenamiento, pero no decide por vos el significado del evento.

---

## 2. Glosario fundamental

| Término | Significado | Ejemplo |
|---|---|---|
| **Broker** | Servidor de Kafka que almacena y atiende particiones. | Nodo del clúster. |
| **Clúster** | Conjunto de servidores Kafka que colaboran. | Varios brokers. |
| **Tópico (*topic*)** | Nombre lógico de un flujo de registros. | `pedidos-confirmados`. |
| **Partición** | Registro ordenado de eventos dentro de un tópico. | Partición `2` del tópico. |
| **Registro (*record*)** | Unidad escrita en Kafka: clave, valor, timestamp y cabeceras opcionales. | Evento de un pedido. |
| **Clave (*key*)** | Valor usado normalmente para decidir partición y agrupar eventos relacionados. | `pedidoId`. |
| **Valor (*value*)** | Contenido principal del registro. | Datos de `PedidoConfirmado`. |
| **Offset** | Posición de un registro dentro de **una partición**. | Partición `2`, offset `157`. |
| **Productor** | Cliente que escribe registros. | Servicio Pedidos. |
| **Consumidor** | Cliente que lee registros. | Servicio Auditoría. |
| **Grupo de consumidores** | Consumidores que comparten el trabajo de leer un tópico. | Grupo `auditoria`. |
| **Offset confirmado** | Posición guardada para reanudar un grupo tras reinicio o reasignación. | Próximo registro a procesar. |
| **Rebalanceo** | Redistribución de particiones entre miembros de un grupo. | Entra o sale un consumidor. |
| **Réplica** | Copia de una partición en otro broker. | Respaldo de la partición `2`. |
| **ISR** | Réplicas consideradas sincronizadas para una partición. | Conjunto usado para confirmar escrituras según configuración. |
| **Retención** | Política que limita cuánto tiempo o volumen se conserva. | Borrar segmentos antiguos. |
| **Compactación** | Política que conserva el estado más reciente por clave con reglas específicas. | Último valor conocido de una configuración. |
| **Lag** | Distancia entre lo escrito y el avance de un consumidor. | Grupo atrasado respecto del final del registro. |

**La combinación `tópico + partición + offset` identifica la posición de un registro.** Un offset `157` aislado no identifica globalmente un evento.

---

## 3. El modelo mental: un registro que se conserva

Pensá cada partición como un **registro de entradas añadidas al final**. Los consumidores avanzan por ese registro. Leer no elimina el registro inmediatamente.

```text
Partición 0: [offset 0] [offset 1] [offset 2] [offset 3] ...
                            ^ grupo Auditoría
                                         ^ grupo Estadísticas
```

Los grupos pueden tener posiciones diferentes. Auditoría puede ir por `offset 1` mientras Estadísticas ya llegó a `offset 3`. Si la retención aún conserva los registros, un grupo puede volver a leerlos desde una posición anterior.

**Diferencia con una cola sencilla:** no modeles Kafka como «un consumidor toma el mensaje y lo borra». La disponibilidad depende de la política de retención o compactación, **no de que un grupo lo haya leído**.

---

## 4. Tópicos, particiones, registros y offsets

Un tópico se divide en una o más **particiones**. Cada partición tiene su propio orden y sus propios offsets. Un registro se agrega a una partición concreta.

```text
Tópico pedidos-confirmados
  Partición 0: P-10@0 -> P-12@1 -> P-13@2
  Partición 1: P-11@0 -> P-14@1
```

`P-10@0` y `P-11@0` no son el mismo registro: el offset `0` aparece en particiones diferentes. El orden de la partición `0` no crea por sí mismo un orden total con la partición `1`.

| Decisión | Consecuencia |
|---|---|
| Número de particiones | Afecta paralelismo posible y distribución de carga. |
| Clave de los registros | Afecta dónde quedan eventos relacionados. |
| Retención | Afecta cuánto tiempo puede reprocesarse. |
| Réplicas | Afectan tolerancia a fallos y costo de almacenamiento. |

Más particiones no garantizan mayor rendimiento: también aumentan coordinación, archivos y decisiones de distribución. Elegí según volumen, paralelismo y orden requerido.

---

## 5. Claves y orden por entidad

Si todos los eventos de un pedido deben conservar un orden relativo, usá **la misma clave estable**, por ejemplo `pedidoId`, para enviarlos a la misma partición bajo una política de particionado consistente.

```text
Clave P-42 -> PedidoCreado -> PedidoConfirmado -> PedidoCancelado
                    todos en la misma partición, en orden de escritura
```

Kafka preserva el orden **del registro de cada partición**. Eso no garantiza que dos productores escriban en el mismo orden en que ocurrieron hechos de negocio en lugares distintos. Tampoco garantiza que un consumidor que lanza trabajos paralelos termine sus efectos en ese mismo orden.

**Cambio importante:** aumentar el número de particiones o cambiar el particionador puede modificar a qué partición van futuros registros de una clave. Si la regla depende de que **todo** el historial de esa clave permanezca en una sola partición, planificá ese cambio.

No uses una única clave constante para todo el tópico solo por querer orden: concentrarías toda la carga en una partición.

---

## 6. Brokers, réplicas y confirmación de escritura

Las particiones pueden tener **réplicas** en varios brokers. Para cada partición existe una réplica que actúa como líder y otras que siguen sus datos. La replicación permite continuar bajo ciertos fallos, según el estado del clúster y la configuración.

```text
Partición 2:
  Broker A -> líder
  Broker B -> réplica
  Broker C -> réplica
```

| Configuración o concepto | Pregunta que responde |
|---|---|
| Factor de replicación | ¿Cuántas copias de la partición se mantienen? |
| `acks` del productor | ¿Qué confirmación exige antes de considerar el envío exitoso? |
| `min.insync.replicas` | ¿Cuántas réplicas sincronizadas mínimas exige una escritura con `acks=all`? |
| ISR | ¿Qué réplicas están al día según Kafka? |

Con `acks=all`, el líder espera la confirmación de las réplicas del ISR según las reglas del clúster. Si hay menos réplicas sincronizadas que el mínimo exigido, una escritura puede fallar **para preservar la garantía elegida**. No prometas durabilidad solo porque el productor llamó a `send()`; verificá su resultado y la configuración del tópico.

---

## 7. El productor: enviar, esperar y manejar incertidumbre

Un productor crea registros, elige tópico y clave, serializa los datos y los envía. El envío suele tener un resultado **asíncrono**; llamar a `send()` no significa que el broker ya confirmó la escritura.

```text
Aplicación -> send(registro) -> futuro pendiente
                           -> confirmación con tópico, partición y offset
                           -> o fallo / timeout
```

| Resultado | Qué significa |
|---|---|
| Confirmación exitosa | Kafka aceptó el registro según las garantías configuradas. |
| Error definitivo | La publicación no se completó según el cliente. |
| Timeout o resultado incierto | Puede hacer falta investigar si el registro llegó. |

Un **ID de evento estable** ayuda a reconocer una publicación lógica repetida desde la aplicación. El productor idempotente de Kafka evita ciertos duplicados causados por sus reintentos internos, pero **no identifica automáticamente que dos llamadas nuevas a `send()` representan el mismo hecho**.

Si el hecho nace de una transacción en otra base de datos, el patrón **outbox** sigue siendo necesario para no perder la intención de publicar entre ambas escrituras.

---

## 8. Retención, borrado y compactación

Kafka conserva registros según la política del tópico. **Leer un registro no lo elimina.**

| Política | Idea | Uso típico |
|---|---|---|
| Retención por tiempo o tamaño | Se eliminan segmentos antiguos cuando corresponde. | Historial reciente de eventos. |
| Compactación por clave | Eventualmente se conservan las versiones necesarias para representar el último valor por clave. | Estado más reciente de una entidad. |
| Combinación de políticas | Se aplican reglas de borrado y compactación según configuración. | Necesidades mixtas. |

La compactación **no es una limpieza instantánea de cada registro anterior**. Durante un tiempo pueden coexistir varias versiones de una misma clave. Un registro con valor nulo (*tombstone*) puede indicar eliminación de la clave en un tópico compactado, con reglas de conservación propias.

**Consecuencia práctica:** reprocesar «desde el inicio» solo puede leer lo que **todavía está retenido**. El offset de un grupo puede seguir guardado cuando los datos a los que apuntaba ya expiraron; entonces importa la política de reinicio del consumidor.

---

## 9. Grupos de consumidores y reparto de particiones

En el modelo habitual de **grupos de consumidores**, miembros del mismo grupo se reparten las particiones asignadas. Grupos distintos mantienen posiciones independientes.

```text
Tópico con 3 particiones

Grupo Auditoría:    consumidor A -> particiones 0 y 1
                    consumidor B -> partición 2

Grupo Estadísticas: consumidor C -> particiones 0, 1 y 2
```

Auditoría y Estadísticas reciben el flujo para **propósitos distintos**. Dentro de Auditoría, A y B se reparten el trabajo. Si el grupo tiene más consumidores activos que particiones asignables en este modelo, algunos pueden quedar sin partición; agregar procesos no crea más paralelismo de lectura por sí solo.

**Elegir `group.id` es una decisión de negocio y operación:** dos aplicaciones con el mismo ID comparten progreso y trabajo; con IDs distintos consumen de manera independiente. Cambiar el ID puede hacer que una aplicación empiece con una posición diferente según su configuración.

---

## 10. Posición leída y offset confirmado

Un consumidor tiene una **posición de lectura** que avanza al obtener registros y un **offset confirmado** usado para reanudar tras un reinicio o reasignación. No son necesariamente iguales.

```text
La llamada poll entrega offsets 10, 11 y 12.
El consumidor empieza a procesarlos.

Posición de lectura: próxima posición después del lote.
Offset confirmado: puede seguir antes del lote hasta que se confirme.
```

| Momento de confirmación | Riesgo si el proceso falla |
|---|---|
| Antes de completar el efecto | Puede saltarse trabajo no terminado. |
| Después de completar el efecto | Puede volver a recibir trabajo ya terminado. |

Para un efecto crítico, se suele **confirmar después de completar** y hacer el efecto idempotente. Si el efecto fue guardado en otra base y el proceso cae antes de confirmar el offset, Kafka puede entregar de nuevo el registro. La base debe reconocer `eventoId` para no repetir el cambio.

**Detalle de la API:** el offset confirmado representa la **próxima posición** desde la que se retomará, no el último registro procesado. Las librerías de alto nivel pueden administrar esta confirmación con sus propias reglas; hay que conocerlas antes de asumir cuándo se hace el commit.

---

## 11. Inicio de lectura y reprocesamiento

Un grupo nuevo puede no tener un offset confirmado. En ese caso, `auto.offset.reset` decide desde dónde empezar **si no hay posición válida**, por ejemplo desde lo más antiguo disponible (`earliest`) o desde el final (`latest`).

```text
Grupo nuevo + earliest -> lee registros aún retenidos desde el comienzo disponible.
Grupo nuevo + latest   -> empieza con los registros nuevos desde el final.
```

Esta opción **no obliga a un grupo con offset válido a releer desde cero cada vez que arranca**. Para reprocesar hay que planificar la posición del grupo o usar otro grupo, y verificar que la retención todavía conserve los datos.

Antes de reprocesar, preguntá si los consumidores pueden **repetir sus efectos** sin duplicarlos. Reconstruir una vista derivada puede ser legítimo; volver a cobrar o enviar correos históricos no lo es automáticamente.

---

## 12. Rebalanceos y trabajo que puede repetirse

Cuando cambia la membresía de un grupo o se reasignan particiones, ocurre un **rebalanceo**. Una partición puede pasar de un consumidor a otro, que retomará desde el offset confirmado disponible.

```text
Consumidor A procesa offset 20.
A falla antes de confirmar progreso.
La partición pasa a B.
B vuelve a recibir offset 20.
```

El consumidor debe manejar esta posibilidad. También debe seguir llamando a `poll` dentro de los límites esperados por el grupo; un procesamiento demasiado lento puede causar pérdida de asignación y repetición de trabajo.

No interpretes cada rebalanceo como pérdida de datos. Sí puede causar pausas y reentregas. Medí su frecuencia y revisá el tiempo de procesamiento por lote antes de aumentar el tamaño del lote o la cantidad de consumidores.

---

## 13. Garantías de entrega y productores idempotentes

La pregunta «¿Kafka entrega exactamente una vez?» necesita un **alcance**. Hay diferencias entre escribir un registro, leerlo, confirmar offset y modificar una base externa.

| Alcance | Qué puede proteger Kafka | Qué sigue siendo responsabilidad de la aplicación |
|---|---|---|
| Envío del productor | Idempotencia del productor frente a ciertos reintentos de envío. | No crear dos eventos lógicos con dos llamadas distintas. |
| Lectura del grupo | Reanudación desde offsets confirmados. | Evitar efectos duplicados al reprocesar. |
| Leer y escribir en Kafka | Transacciones para ciertos flujos Kafka → Kafka con offsets coordinados. | Consistencia con una base o API externa. |

`enable.idempotence=true` en el productor, junto con configuraciones compatibles como `acks=all`, evita duplicados asociados a reintentos del **mismo productor y envío** dentro de su alcance. No sustituye un `eventoId` de negocio ni una deduplicación del consumidor.

La estrategia frecuente para una base externa es aceptar que un registro puede ser **reentregado** y hacer que el consumidor aplique su efecto **una sola vez por `eventoId`**, con una restricción y una transacción donde se guarda ese efecto.

---

## 14. Transacciones de Kafka y límites con otras bases de datos

Kafka permite transacciones que agrupan escrituras en Kafka y, en ciertos flujos, el avance del offset consumido. Un consumidor configurado para leer solo datos confirmados puede evitar registros de transacciones abortadas.

```text
Consumir de tópico A -> producir en tópico B
  Kafka puede coordinar salida y offset dentro de su propio sistema.

Consumir de Kafka -> escribir en PostgreSQL
  La escritura SQL no entra automáticamente en esa transacción de Kafka.
```

**Regla de diseño:** identificá dónde termina la transacción. Si el consumidor escribe en PostgreSQL y después confirma el offset, puede caer entre ambos pasos y volver a procesar. Si confirma antes, puede perder el efecto. La solución suele incluir **idempotencia en la base** y una política clara de confirmación.

De manera similar, guardar un pedido en PostgreSQL y publicar a Kafka exige tratar la doble escritura; una outbox local registra la intención de publicación junto al pedido. **La transacción de Kafka no vuelve atómico ese `COMMIT` de PostgreSQL.**

---

## 15. Errores, reintentos y mensajes apartados

El consumidor puede fallar por una dependencia temporalmente caída, por un mensaje inválido o por una regla de negocio. Cada caso necesita una respuesta distinta.

| Fallo | Qué conviene preguntar | Tratamiento posible |
|---|---|---|
| Base temporalmente inaccesible | ¿Podría funcionar tras una espera? | Reintento acotado. |
| Valor imposible de deserializar | ¿Repetir los mismos bytes cambiaría algo? | Apartar e investigar. |
| Estado de negocio rechazado | ¿Es un resultado esperado o un error del contrato? | Registrar resultado o apartar según el flujo. |

Una **DLT** (*dead letter topic*) es un tópico al que una aplicación o biblioteca puede enviar registros que no pudo procesar. Kafka **no mueve automáticamente todos los errores allí por el simple hecho de existir el tópico**: hay que diseñar y configurar esa política.

Si un registro bloquea una partición, moverlo a DLT permite continuar, pero **puede alterar el efecto del orden esperado**. Conservá el registro original, su clave, posición, causa y versión para investigar o reprocesar. DLT es un lugar de trabajo pendiente, no una solución terminada.

---

## 16. Atraso del consumidor y límites de carga

El **lag** indica cuánto se encuentra detrás un grupo respecto de los registros disponibles para leer. Si crece sostenidamente, el grupo recibe trabajo más rápido de lo que lo completa o de lo que confirma.

```text
Se producen 100 registros/segundo.
El grupo completa 60 registros/segundo.
El atraso aumenta mientras esa diferencia continúa.
```

| Causa posible | Evidencia a buscar |
|---|---|
| Consumidor espera una API lenta | Tiempo de procesamiento por registro. |
| Pocas particiones para ese grupo | Asignación y consumidores sin trabajo. |
| Una clave concentra carga | Distribución desigual entre particiones. |
| Base de datos saturada | Tiempo de escritura y límites de conexiones. |
| Reintentos continuos | Tasa de errores y mensajes repetidos. |

Agregar particiones o consumidores puede ayudar **si el cuello está en el procesamiento paralelo y la regla de orden lo permite**. También puede aumentar la presión sobre dependencias externas. Además del número de registros atrasados, medí la **edad del trabajo pendiente**, que muestra lo que espera una persona o proceso.

---

## 17. Formato de mensajes y evolución del contrato

Kafka almacena bytes; productores y consumidores deben acordar cómo interpretarlos. Pueden usar texto, JSON, Avro, Protobuf u otro formato, pero **ninguno sustituye definir el significado de los campos**.

```text
Evento PedidoConfirmado v1:
  eventoId, pedidoId, ocurridoEn

Evento PedidoConfirmado v2:
  eventoId, pedidoId, ocurridoEn, canalOpcional
```

| Pregunta | Por qué importa |
|---|---|
| ¿Qué campos son obligatorios? | Un mensaje antiguo puede no tener uno nuevo. |
| ¿Qué significa cada ID? | `pedidoId` no es lo mismo que `eventoId`. |
| ¿Qué hacen consumidores antiguos con campos nuevos? | Puede fallar la deserialización o cambiar el comportamiento. |
| ¿Cuánto viven mensajes viejos? | La retención y el reprocesamiento prolongan la compatibilidad necesaria. |

La **clave** también forma parte del contrato operativo: cambiarla puede cambiar partición y orden. Evitá poner secretos o datos personales innecesarios en registros duraderos y replicados.

---

## 18. Seguridad y permisos

Una instalación de Kafka puede proteger conexiones y controlar quién produce, consume o administra tópicos. La seguridad no se reduce a «estar en una red interna».

| Necesidad | Pregunta |
|---|---|
| Cifrado de transporte | ¿Los datos viajan protegidos entre cliente y broker? |
| Autenticación | ¿Cómo demuestra cada cliente quién es? |
| Autorización | ¿Qué tópicos puede leer o escribir? |
| Protección del contenido | ¿El evento contiene datos que ese consumidor necesita realmente? |

Los permisos deben seguir el rol del cliente. Auditoría puede necesitar leer `pedidos-confirmados`; eso no implica permiso para escribir o administrar el tópico. Un tópico retenido contiene datos a lo largo del tiempo: revisá quién puede acceder a su historial, no solo quién recibe los nuevos registros.

---

## 19. Administración básica del clúster y los tópicos

Para operar Kafka hay que distinguir **el clúster** de los **tópicos** y sus configuraciones. El clúster administra metadatos, brokers y asignaciones; cada tópico define particiones, réplica y política de conservación.

```text
Clúster Kafka
  ├── brokers y metadatos
  ├── tópico pedidos-confirmados: particiones, réplicas, retención
  └── tópico pedidos-confirmados-dlt: política de mensajes apartados
```

Las versiones actuales de Kafka usan **KRaft** para la gestión de metadatos del clúster; guías antiguas pueden describir ZooKeeper. Los conceptos de tópico, partición, productor y consumidor siguen siendo el punto de entrada para programar, aunque la administración interna evolucione.

Antes de crear un tópico preguntá: ¿cuántos grupos lo leerán?, ¿cuánto tiempo deben poder reprocesar?, ¿qué orden por clave se necesita?, ¿qué fallos debe tolerar y qué volumen se espera? **No elijas un número de particiones solo porque otro proyecto usa ese número.**

---

## 20. Ejemplo de productor con el cliente Java

Este ejemplo usa la dependencia **`org.apache.kafka:kafka-clients`** y supone un clúster accesible en `localhost:9092` con el tópico creado. La clave `P-42` agrupa registros del mismo pedido según el particionador configurado. El código **espera la confirmación del envío**; no guarda un pedido en otra base.

```java
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

public final class PublicarPedido {
    public static void main(String[] args) throws Exception {
        Properties opciones = new Properties();
        opciones.put("bootstrap.servers", "localhost:9092");
        opciones.put("key.serializer", StringSerializer.class.getName());
        opciones.put("value.serializer", StringSerializer.class.getName());
        opciones.put("acks", "all");
        opciones.put("enable.idempotence", "true");

        try (KafkaProducer<String, String> productor =
                     new KafkaProducer<>(opciones)) {
            ProducerRecord<String, String> registro = new ProducerRecord<>(
                    "pedidos-confirmados", "P-42",
                    "{\"eventoId\":\"E-91\",\"pedidoId\":\"P-42\"}");

            RecordMetadata resultado = productor.send(registro)
                    .get(10, TimeUnit.SECONDS);
            System.out.println("Partición=" + resultado.partition()
                    + ", offset=" + resultado.offset());
        }
    }
}
```

**Si `get` vence el plazo**, no concluyas automáticamente que `E-91` está ausente. El envío podría confirmarse más tarde o haber quedado en un estado incierto para esta llamada. Un sistema real necesita política de recuperación, ID estable del evento y, si el hecho nació en otra base, una outbox.

---

## 21. Ejemplo de consumidor con el cliente Java

Este ejemplo usa el mismo módulo `kafka-clients`. Lee registros del tópico y **confirma el lote después de procesarlo**. `procesar` solo imprime para mantener visible el orden de acciones; en una base externa necesitarías guardar el efecto de forma idempotente.

```java
import java.time.Duration;
import java.util.List;
import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

public final class ConsumirPedidos {
    public static void main(String[] args) {
        Properties opciones = new Properties();
        opciones.put("bootstrap.servers", "localhost:9092");
        opciones.put("group.id", "auditoria");
        opciones.put("key.deserializer", StringDeserializer.class.getName());
        opciones.put("value.deserializer", StringDeserializer.class.getName());
        opciones.put("enable.auto.commit", "false");
        opciones.put("auto.offset.reset", "earliest");

        try (KafkaConsumer<String, String> consumidor =
                     new KafkaConsumer<>(opciones)) {
            consumidor.subscribe(List.of("pedidos-confirmados"));

            while (true) {
                ConsumerRecords<String, String> lote =
                        consumidor.poll(Duration.ofSeconds(1));
                for (ConsumerRecord<String, String> registro : lote) {
                    procesar(registro);
                }
                if (!lote.isEmpty()) {
                    consumidor.commitSync();
                }
            }
        }
    }

    private static void procesar(ConsumerRecord<String, String> registro) {
        System.out.println(registro.topic() + "/" + registro.partition()
                + "@" + registro.offset() + " -> " + registro.value());
    }
}
```

Si `procesar` falla, no se ejecuta el `commitSync` de ese lote. Al reiniciar o reasignar la partición, **puede repetirse trabajo**. El ejemplo no implementa apagado elegante, límites de lote ni manejo de mensajes inválidos; esas decisiones pertenecen al consumidor real. `earliest` solo determina la posición inicial cuando el grupo no tiene un offset válido, no reinicia cada ejecución.

---

## 22. Cómo se ve en Spring Kafka

**Spring Kafka** ofrece `KafkaTemplate` para publicar y `@KafkaListener` para recibir. Estos mecanismos reducen código de infraestructura, pero siguen aplicando las mismas preguntas sobre confirmación, offsets e idempotencia.

```java
// Fragmento ilustrativo dentro de una clase con KafkaTemplate inyectado:
CompletableFuture<SendResult<String, String>> envio =
        kafkaTemplate.send("pedidos-confirmados", pedidoId, payloadJson);

envio.whenComplete((resultado, error) -> {
    if (error != null) {
        registrarFalloDePublicacion(pedidoId, error);
    } else {
        registrarConfirmacion(resultado.getRecordMetadata().offset());
    }
});
```

```java
// Fragmento ilustrativo dentro de un bean configurado como consumidor:
@KafkaListener(topics = "pedidos-confirmados", groupId = "auditoria")
public void recibir(String payloadJson) {
    auditoria.registrarUnaVez(payloadJson);
}
```

Los fragmentos suponen configuración e imports propios de Spring Kafka. **`@KafkaListener` no vuelve idempotente a `auditoria.registrarUnaVez` por sí solo**. También hay que conocer el modo de confirmación de offsets y el manejador de errores del contenedor. No se debe devolver éxito de negocio solo porque `KafkaTemplate.send()` devolvió un `CompletableFuture`.

---

## 23. Caso completo: pedido confirmado, auditoría y estadísticas

**Problema:** Pedidos confirma `P-42`. Auditoría y Estadísticas necesitan reaccionar de forma independiente. El pedido se guarda en PostgreSQL; Kafka distribuye el evento `E-91`.

### Paso 1: definir el contrato

```text
Tópico: pedidos-confirmados
Clave: pedidoId = P-42
Valor: eventoId=E-91, pedidoId=P-42, ocurridoEn, version=1

Grupo auditoria: registra el hecho una vez por eventoId.
Grupo estadisticas: suma el pedido una vez por eventoId.
```

La clave permite que los eventos del mismo pedido sigan una partición bajo una política estable. Los dos grupos mantienen **sus propios offsets**: un atraso en Estadísticas no obliga a Auditoría a retroceder.

### Paso 2: guardar y publicar

Pedidos guarda el cambio a `CONFIRMADO` y una fila outbox para `E-91` en **una transacción SQL**. Un publicador envía `E-91` a Kafka con `pedidoId` como clave y observa la confirmación. Si cae después del envío y antes de registrar progreso en outbox, puede publicar `E-91` otra vez.

### Paso 3: consumir y confirmar

Cada grupo deserializa y valida la versión. Auditoría guarda su marca de `eventoId` junto al registro de auditoría en una transacción local. Estadísticas hace lo mismo con su actualización. Después de confirmar el efecto, cada consumidor puede avanzar su offset. Si cae entre ambos pasos, Kafka puede reentregar `E-91` y el consumidor reconocerá el ID.

| Fallo | Resultado esperado |
|---|---|
| Pedidos confirma, Kafka no disponible | `E-91` permanece pendiente en outbox. |
| Publicador envía dos veces | Los grupos pueden recibir duplicados y deben reconocerlos. |
| Auditoría falla, Estadísticas funciona | Cada grupo conserva su propio avance. |
| Un consumidor reinicia antes del commit de offset | Puede volver a recibir un registro ya aplicado. |
| La retención vence antes de que un grupo se recupere | Ese grupo ya no puede reconstruir todo solo desde ese tópico. |

**Promesa al usuario:** «pedido confirmado» significa que Pedidos confirmó su propio estado. No significa que ambos consumidores ya terminaron. Si Auditoría fuera requisito indispensable **antes** de responder, el contrato y el flujo deberían reflejarlo de otro modo.

---

## 24. Errores frecuentes al diseñar con Kafka

| Suposición | Problema | Pregunta correcta |
|---|---|---|
| «Kafka es una cola que borra al leer» | Se confunde progreso con retención. | ¿Cuánto tiempo se conserva el registro? |
| «El offset identifica el evento» | El mismo número existe en otras particiones. | ¿Cuál es tópico, partición y offset? |
| «Más consumidores siempre aceleran» | El grupo puede tener menos particiones que consumidores. | ¿Qué particiones y cuellos de botella hay? |
| «La clave ordena todo el tópico» | El orden está definido por partición. | ¿Qué entidad necesita orden y dónde se escribe? |
| «`send()` significa publicado» | El envío puede seguir pendiente. | ¿Se recibió confirmación? |
| «Confirmar offset confirma la base» | Son sistemas distintos. | ¿Qué efecto quedó persistido y cuándo? |
| «Idempotencia del productor resuelve duplicados de negocio» | Dos publicaciones lógicas pueden seguir existiendo. | ¿Cuál es el `eventoId` y cómo deduplica el consumidor? |
| «DLT es automática» | El error puede bloquear o reintentarse indefinidamente. | ¿Qué política de errores está configurada? |
| «`earliest` relee todo siempre» | Un offset válido del grupo prevalece. | ¿Existe posición confirmada? |
| «Todo se puede reprocesar» | La retención puede haber borrado datos. | ¿Siguen disponibles los registros necesarios? |

---

## 25. Ejercicios de razonamiento

Para cada ejercicio, dibujá **tópico, particiones, claves, grupos, posiciones y efecto esperado**. Después indicá qué ocurre ante un duplicado y una caída.

| Ejercicio | Pregunta central |
|---|---|
| **Dos grupos** | ¿Por qué Auditoría y Estadísticas pueden leer el mismo evento sin compartir offset? |
| **Un grupo, más consumidores** | Con tres particiones y cinco consumidores, ¿cuántos pueden tener particiones asignadas? |
| **Clave de pedido** | ¿Qué pasa con el orden si los eventos de `P-42` se publican con claves distintas? |
| **Commit temprano** | ¿Qué se pierde si confirmás el offset antes de guardar en la base? |
| **Commit tardío** | ¿Qué puede repetirse si guardás en la base y caés antes del offset? |
| **Retención** | ¿Puede un grupo nuevo reconstruir datos que ya expiraron? |
| **DLT** | ¿Qué debe conservarse para investigar un evento inválido? |
| **Cambio de particiones** | ¿Qué supuesto sobre la ubicación histórica de una clave deberías revisar? |

**Desafío:** diseñá un consumidor de `PagoConfirmado` que actualiza una tabla SQL. Definí `eventoId`, una restricción de unicidad para procesados, el límite de transacción SQL y el momento de confirmar el offset. Explicá qué ocurre si el proceso cae **después del COMMIT SQL y antes del commit del offset**.

---

## 26. Lista de verificación antes de publicar un evento

- [ ] El evento expresa un hecho que ya ocurrió y tiene un dueño claro.
- [ ] Elegí tópico, clave y número de particiones según orden y carga reales.
- [ ] Sé cuánto tiempo deben conservarse los registros para recuperaciones y reprocesos.
- [ ] Definí factor de replicación y confirmación de escritura acordes a la pérdida tolerable.
- [ ] El productor observa éxito o fallo del envío; `send()` solo no basta.
- [ ] Si el hecho se guardó en otra base, su publicación tiene una estrategia contra doble escritura.
- [ ] Cada grupo tiene un propósito y un `group.id` propios.
- [ ] Sé cuándo el consumidor confirma offsets y qué trabajo puede repetirse.
- [ ] Los efectos críticos se deduplican por un ID de negocio estable.
- [ ] Hay política para errores transitorios, mensajes inválidos y DLT.
- [ ] Vigilo lag, edad del trabajo, rebalances y fallos de publicación.
- [ ] El contrato de bytes, versión, campos y clave puede evolucionar sin romper consumidores.
- [ ] Los permisos de producción, lectura y administración corresponden a cada rol.

**Idea central:** Kafka conserva un flujo **ordenado por partición** y permite a grupos distintos avanzar a su ritmo. La corrección de un sistema depende de cómo relacionás ese flujo con los **hechos, estados y efectos** de tu negocio.

---

## 27. Laboratorio local: iniciar Kafka y recorrer un tópico

El objetivo del laboratorio es **ver registros, claves, particiones y grupos** antes de integrar Spring. Los comandos siguientes están pensados para **PowerShell** y la imagen oficial `apache/kafka:4.4.0`. Un solo broker con factor de replicación `1` sirve para aprender; no representa alta disponibilidad.

```powershell
docker run -d --name kafka-local -p 9092:9092 apache/kafka:4.4.0
docker logs kafka-local
```

Esperá a que el broker termine de iniciar. Los scripts de administración están dentro del contenedor en `/opt/kafka/bin`. Creá un tópico de tres particiones:

```powershell
docker exec kafka-local /opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --topic pedidos-lab --partitions 3 --replication-factor 1
docker exec kafka-local /opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --describe --topic pedidos-lab
```

Abrí un productor de consola y escribí dos líneas; cada línea es un registro. Luego abrí un consumidor en **otra terminal**:

```powershell
docker exec -it kafka-local /opt/kafka/bin/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic pedidos-lab
```

```powershell
docker exec -it kafka-local /opt/kafka/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic pedidos-lab --from-beginning --group auditoria-lab
```

Detené ambos procesos con `Ctrl+C`. Volvé a ejecutar el consumidor **con el mismo grupo**: retoma desde la posición confirmada, si existe. Ejecutalo con `--group estadisticas-lab`: es otro grupo y puede leer el historial disponible. Si querés practicar claves con la consola, usá un productor con `--property parse.key=true --property key.separator=:` e ingresá líneas como `P-42:{"eventoId":"E-91"}`. El separador `:` delimita clave y valor para esa herramienta; **no forma parte del valor publicado**.

```powershell
docker exec -it kafka-local /opt/kafka/bin/kafka-console-producer.sh --bootstrap-server localhost:9092 --topic pedidos-lab --property parse.key=true --property key.separator=:
```

Para estudiar el efecto del orden, repetí la clave `P-42`, agregá otra clave y compará particiones y offsets con un consumidor que imprima metadatos. El orden que observás es **por partición**. Una única instancia local no prueba qué pasa cuando falla un broker.

> Si el puerto `9092` ya está ocupado, liberalo o adaptá tanto la publicación del contenedor como `bootstrap-servers`. En Docker o Kubernetes, `advertised.listeners` debe anunciar direcciones alcanzables desde los clientes; mapear un puerto por sí solo no resuelve una dirección anunciada incorrecta.

---

## 28. Herramientas de consola para investigar

Las herramientas de Kafka ayudan a contrastar lo que la aplicación cree con el estado del clúster. Los siguientes ejemplos usan el contenedor del laboratorio.

| Necesito saber... | Herramienta |
|---|---|
| Si existe el tópico y cómo están sus particiones | `kafka-topics.sh --describe` |
| Qué grupos existen | `kafka-consumer-groups.sh --list` |
| Hasta dónde avanzó un grupo | `kafka-consumer-groups.sh --describe --group ...` |
| Si un registro llega al tópico | `kafka-console-consumer.sh` con un grupo de diagnóstico |
| Qué política concreta tiene un tópico | `kafka-configs.sh --describe` |

```powershell
docker exec kafka-local /opt/kafka/bin/kafka-consumer-groups.sh --bootstrap-server localhost:9092 --list
docker exec kafka-local /opt/kafka/bin/kafka-consumer-groups.sh --bootstrap-server localhost:9092 --describe --group auditoria-lab
docker exec kafka-local /opt/kafka/bin/kafka-configs.sh --bootstrap-server localhost:9092 --entity-type topics --entity-name pedidos-lab --describe
```

En la salida de un grupo, `CURRENT-OFFSET` es la posición confirmada, `LOG-END-OFFSET` indica el final conocido y `LAG` es la diferencia. Un grupo puede tener lag cero y aun así fallar al aplicar un efecto si confirma demasiado pronto. También puede tener lag alto durante una carga legítima; mirá la **edad del trabajo** y el tiempo esperado de respuesta.

### Releer sin confundir el offset inicial

`auto.offset.reset=earliest` **no reinicia un grupo que ya tiene offsets válidos**. Para probar desde el principio creá un grupo nuevo. Para mover un grupo existente, detení sus consumidores y primero examiná el cambio con `--dry-run`:

```powershell
docker exec kafka-local /opt/kafka/bin/kafka-consumer-groups.sh --bootstrap-server localhost:9092 --reset-offsets --group auditoria-lab --topic pedidos-lab --to-earliest --dry-run
```

Solo después de revisar el impacto en los efectos externos se usaría `--execute`. Releer puede **repetir correos, cargos o escrituras** si esos efectos no son idempotentes. Los offsets más antiguos quizá ya no existan por retención. En un entorno real, documentá quién pidió el reprocesamiento, qué registros abarca y cómo se comprobará el resultado.

---

## 29. Diseñar tópicos para un entorno real

Un tópico no es solo un nombre. Su configuración condiciona **orden, paralelismo, tolerancia a fallos, almacenamiento y capacidad de recuperación**.

| Decisión | Qué preguntar | Ejemplo |
|---|---|---|
| Nombre y significado | ¿Un evento expresa un hecho terminado o una orden? | `pedidos-confirmados` expresa un hecho. |
| Clave | ¿Qué entidad necesita orden? | `pedidoId`. |
| Particiones | ¿Cuántos consumidores útiles y qué volumen habrá? | Tres particiones para un laboratorio. |
| Réplicas | ¿Cuántos brokers pueden fallar sin perder disponibilidad? | Una réplica en local; más en un clúster real. |
| Retención | ¿Cuánto tiempo necesita un grupo para recuperarse o reprocesar? | Días o semanas según negocio. |
| Política de limpieza | ¿Se conserva historial o último valor por clave? | `delete` para hechos; `compact` para estados por clave. |
| Tamaño máximo | ¿Qué ocurre con eventos grandes? | Guardar archivos fuera y publicar su referencia. |

Una partición es una unidad de orden y asignación dentro de un grupo. **Agregar particiones puede cambiar a qué partición van claves futuras** con un particionador dependiente de la cantidad de particiones. Por eso, si el orden histórico por clave es requisito estricto, planificá ese cambio y no asumás continuidad automática. Un tópico con cuatro particiones no acelera indefinidamente por agregar diez miembros al mismo grupo.

`acks=all` solo significa que el productor espera el criterio de confirmación configurado; la protección real depende también de **réplicas disponibles**, `min.insync.replicas` y operación del clúster. Si no se alcanza el mínimo, el productor puede recibir error y debe manejarlo. No copies números de réplicas o retención sin conocer el clúster y el objetivo de recuperación.

### Crear tópicos desde Spring en desarrollo

Spring Boot detecta un bean `NewTopic` y lo usa para crear el tópico si falta. Para un ejemplo con un broker:

```java
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
class TopicosKafka {
    @Bean
    NewTopic pedidosConfirmados() {
        return TopicBuilder.name("pedidos-confirmados")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    NewTopic pedidosConfirmadosDlt() {
        return TopicBuilder.name("pedidos-confirmados-dlt")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
```

En producción, la creación y cambios de tópicos suelen formar parte de una operación controlada. El ejemplo de DLT conserva **al menos tantas particiones como el origen** para poder enviar el registro fallido a la misma partición. Ajustá `replicas(1)` cuando haya un clúster de varios brokers. No habilites creación automática de tópicos para ocultar errores de nombre.

Para practicar una política de conservación en el laboratorio, podés crear **otro tópico** con retención por tiempo. `retention.ms=86400000` expresa 24 horas como objetivo de retención; la eliminación ocurre por segmentos y no exactamente al cumplirse ese milisegundo:

```powershell
docker exec kafka-local /opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --create --topic eventos-24h --partitions 3 --replication-factor 1 --config retention.ms=86400000
docker exec kafka-local /opt/kafka/bin/kafka-configs.sh --bootstrap-server localhost:9092 --entity-type topics --entity-name eventos-24h --describe
```

---

## 30. Dependencias y configuración de Spring Boot

El recorrido de código usa **Java 17 o superior y Spring Boot 3.5.x**, con las versiones de Spring Kafka y Jackson que Boot gestiona; el laboratorio usa la imagen del broker Kafka `4.4.0`. Elegí la versión de Boot con su gestor de dependencias: evitá forzar una versión distinta de `spring-kafka` sin verificar compatibilidad. **Spring Boot 4 y Spring Kafka 4 cambiaron aspectos de Jackson y sus nombres de clases**; si trabajás con esas líneas, adaptá los imports y serializadores según su documentación.

### Maven

En un proyecto Spring Boot con `spring-boot-starter-parent` o BOM de Boot:

```xml
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-json</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka-test</artifactId>
    <scope>test</scope>
</dependency>
```

Si el proyecto expone un endpoint HTTP de prueba, añadí `spring-boot-starter-web`; **Kafka no exige HTTP**. Para pruebas con contenedor real añadí las dependencias de Testcontainers compatibles con tu versión de Boot y Testcontainers, como veremos en la sección 36.

### `application.yml` de desarrollo

Este recorrido utiliza **clave y valor `String`**. El valor será JSON explícito y la aplicación decidirá cómo interpretarlo. Evita depender de nombres de clases Java transportados como metadatos del mensaje.

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer
      acks: all
      properties:
        "[enable.idempotence]": true
    consumer:
      group-id: auditoria
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      auto-offset-reset: earliest
      enable-auto-commit: false
    listener:
      ack-mode: record
```

`ack-mode: record` hace que el contenedor confirme después de que un listener de registros termina correctamente. No confirma una transacción en PostgreSQL por sí solo. `earliest` se aplica **solo sin offset válido**. El ejemplo con `group-id: auditoria` representa el servicio Auditoría; Estadísticas debe ejecutar **otro grupo**. Para aplicaciones distintas, configurá grupos distintos, no cambies el grupo en cada despliegue.

### Parámetros que conviene reconocer

| Propiedad | Afecta | Pregunta |
|---|---|---|
| `bootstrap-servers` | Conexión inicial | ¿El cliente alcanza los brokers anunciados? |
| `acks` | Confirmación del productor | ¿Qué respuesta espera el productor? |
| `enable.idempotence` | Reintentos del productor | ¿Evito duplicación causada por reintentar una solicitud de envío? |
| `delivery.timeout.ms` | Límite total de entrega | ¿Cuándo dejo de esperar éxito de publicación? |
| `group-id` | Avance compartido | ¿Quién comparte el trabajo? |
| `auto-offset-reset` | Inicio sin posición válida | ¿Qué hago con un grupo nuevo o offset vencido? |
| `max.poll.records` | Tamaño de lote recibido | ¿Cuánto trabajo puede tardar este ciclo? |
| `max.poll.interval.ms` | Tiempo entre polls | ¿Un procesamiento largo provoca rebalanceo? |
| `spring.kafka.listener.concurrency` | Consumidores del listener | ¿Hay particiones suficientes para aprovecharlos? |

Los valores por defecto cambian entre versiones. Configurá explícitamente solo lo que responde a un requisito y verificá el valor efectivo al arrancar.

---

## 31. Contrato del evento y serialización JSON

Para que un consumidor no dependa de un objeto Java interno del productor, definí un **contrato externo**: nombre del evento, versión, IDs, fecha, clave y reglas de compatibilidad. Por ejemplo:

```json
{
  "tipo": "PedidoConfirmado",
  "version": 1,
  "eventoId": "E-91",
  "pedidoId": "P-42",
  "ocurridoEn": "2026-11-17T12:00:00Z"
}
```

La clave Kafka es `P-42`; `eventoId` identifica la **ocurrencia lógica** y sirve para deduplicar. El payload incluye el `pedidoId` para que siga teniendo sentido fuera de Kafka. `ocurridoEn` indica cuándo ocurrió el hecho, que puede diferir del tiempo en que Kafka lo recibió. Evitá incluir entidades JPA enteras, clases concretas, contraseñas o datos que nadie necesita.

```java
import java.time.Instant;

public record PedidoConfirmado(
        String tipo,
        int version,
        String eventoId,
        String pedidoId,
        Instant ocurridoEn) { }
```

Con `StringSerializer`, la aplicación convierte el record a JSON usando `ObjectMapper`. Al leerlo, primero valida `tipo` y `version`; después deserializa. Un consumidor que no conoce la versión debe tener una política explícita: rechazar y apartar, admitir campos nuevos o usar un lector compatible. **Cambiar un nombre o significado de campo puede romper consumidores aunque el JSON siga siendo válido.**

Spring Kafka también ofrece serializadores JSON; son útiles cuando los productores y consumidores comparten el contrato y la configuración de tipos. Si los servicios se despliegan de forma independiente, decidí con cuidado el uso de cabeceras de tipo, paquetes confiables y versiones del contrato. Para muchos esquemas y equipos, evaluá Avro, Protobuf o JSON Schema junto con un registro de esquemas; el registro de esquemas es **un componente adicional**, no una característica que aparezca al crear un tópico.

---

## 32. Productor Spring completo y resultado del envío

El siguiente componente usa la configuración `String` anterior. El método devuelve el futuro para que la capa que lo llama decida **qué hacer con el éxito o el error**. `ObjectMapper` lo aporta Spring Boot si está configurado Jackson.

```java
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.CompletableFuture;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

@Service
public class PublicadorPedidos {
    private final KafkaTemplate<String, String> kafka;
    private final ObjectMapper json;

    public PublicadorPedidos(KafkaTemplate<String, String> kafka,
                             ObjectMapper json) {
        this.kafka = kafka;
        this.json = json;
    }

    public CompletableFuture<SendResult<String, String>> publicar(
            PedidoConfirmado evento) {
        try {
            String valor = json.writeValueAsString(evento);
            return kafka.send("pedidos-confirmados", evento.pedidoId(), valor);
        } catch (JsonProcessingException e) {
            return CompletableFuture.failedFuture(e);
        }
    }
}
```

Un uso que **observa el resultado**:

```java
publicador.publicar(evento).whenComplete((resultado, error) -> {
    if (error != null) {
        registrarPublicacionPendiente(evento.eventoId(), error);
        return;
    }
    var metadata = resultado.getRecordMetadata();
    registrarEnvioConfirmado(evento.eventoId(),
            metadata.topic(), metadata.partition(), metadata.offset());
});
```

Las funciones `registrar...` representan acciones del publicador de outbox, no métodos de `KafkaTemplate`. Una confirmación de envío significa que Kafka aceptó el registro bajo la configuración vigente; **no prueba que Auditoría o Estadísticas terminaron**. Un error de plazo tampoco demuestra que el broker jamás lo recibió. Por eso el `eventoId` es estable y los consumidores deduplican.

Si la operación HTTP necesita esperar un resultado de Kafka, se puede esperar el futuro con un límite y mapear el error. No hagas `join()` indiscriminado en cada petición ni prometas atomicidad entre la base SQL y Kafka. Si el evento nace de una transacción SQL, usá el patrón outbox de la sección 35.

---

## 33. Consumidor Spring, confirmación y concurrencia

Un listener de registros recibe **un registro a la vez**. Con la configuración `ack-mode: record` de la sección 30, el contenedor avanza el offset **después** de que el método retorna sin excepción. Si el método falla, interviene la política de errores. En Spring Kafka, el modo por defecto del contenedor puede ser `BATCH`; por eso conviene escribir y comprobar el modo elegido.

```java
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AuditoriaPedidosListener {
    private final ObjectMapper json;
    private final AuditoriaPedidos auditoria;

    public AuditoriaPedidosListener(ObjectMapper json,
                                   AuditoriaPedidos auditoria) {
        this.json = json;
        this.auditoria = auditoria;
    }

    @KafkaListener(topics = "pedidos-confirmados", groupId = "auditoria")
    public void recibir(ConsumerRecord<String, String> registro) {
        try {
            PedidoConfirmado evento = json.readValue(
                    registro.value(), PedidoConfirmado.class);
            if (!"PedidoConfirmado".equals(evento.tipo()) || evento.version() != 1) {
                throw new IllegalArgumentException("Contrato no soportado");
            }
            if (!evento.pedidoId().equals(registro.key())) {
                throw new IllegalArgumentException("La clave no coincide");
            }
            auditoria.registrarUnaVez(evento);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("JSON inválido", e);
        }
    }
}
```

`AuditoriaPedidos.registrarUnaVez` representa un servicio que persiste el efecto de forma idempotente. Debe lanzar una excepción si falla de verdad. **Capturar el error, registrarlo y retornar normalmente** haría que el contenedor considere el registro procesado. Para un fallo permanente de contrato, la sección siguiente define cómo apartarlo.

### Concurrencia del listener

Podés configurar `spring.kafka.listener.concurrency: 3` o `concurrency = "3"` en `@KafkaListener`. Eso crea consumidores dentro del mismo grupo para repartir particiones. Con tres particiones, como máximo tres tendrán una partición de ese tópico asignada a la vez; la cantidad concreta depende de la asignación y de otros tópicos suscritos. No asumas que un mismo objeto de servicio se vuelve seguro para acceso concurrente por usar `@KafkaListener`: evitá estado mutable compartido o sincronizalo correctamente.

Si el trabajo tarda mucho, revisá `max.poll.interval.ms`, `max.poll.records` y el tiempo de procesamiento. Un consumidor que deja de llamar a `poll` por demasiado tiempo puede perder su asignación y repetir trabajo tras un rebalanceo. **Aumentar el intervalo no sustituye reducir el tiempo de trabajo o separar tareas largas.**

### Confirmación manual: cuándo usarla

Cuando necesitás controlar explícitamente el momento de confirmación, configurá `spring.kafka.listener.ack-mode: manual_immediate` y recibí `Acknowledgment`:

```java
@KafkaListener(topics = "pedidos-confirmados", groupId = "auditoria")
public void recibir(ConsumerRecord<String, String> registro,
                    Acknowledgment ack) {
    auditoria.procesarYConfirmarEnSuBase(registro.value());
    ack.acknowledge();
}
```

Este fragmento requiere el import `org.springframework.kafka.support.Acknowledgment` y supone que `procesarYConfirmarEnSuBase` termina tras confirmar su transacción. Si el proceso cae **después** de la base y **antes** de `acknowledge`, habrá repetición; la deduplicación sigue siendo necesaria. El modo manual tampoco crea una transacción atómica entre Kafka y PostgreSQL.

---

## 34. Reintentos, DLT y deserialización fallida

No todos los errores requieren la misma reacción. Una base temporalmente caída puede recuperarse; un JSON mal formado seguirá fallando en cada intento. Clasificá los errores antes de elegir la política.

| Tipo de fallo | Ejemplo | Acción habitual |
|---|---|---|
| Transitorio | Base de datos no disponible | Reintentar con límite y espera. |
| Permanente | Versión de evento desconocida | Apartar e investigar. |
| Indeterminado | Plazo vencido al publicar | Consultar estado o repetir con ID estable y deduplicación. |
| Efecto parcial | Base guardó pero offset no avanzó | Reprocesar sin repetir el efecto. |

Spring Kafka permite configurar un `DefaultErrorHandler` con un `DeadLetterPublishingRecoverer`. Con Spring Boot, un bean `CommonErrorHandler` se asocia al contenedor autoconfigurado. Este ejemplo corresponde al recorrido **`String`/`String`** y requiere que el tópico `pedidos-confirmados-dlt` exista con al menos tres particiones:

```java
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
class ErroresKafka {
    @Bean
    CommonErrorHandler commonErrorHandler(KafkaTemplate<String, String> kafka) {
        var recuperador = new DeadLetterPublishingRecoverer(kafka,
                (registro, error) -> new TopicPartition(
                        registro.topic() + "-dlt", registro.partition()));
        var manejador = new DefaultErrorHandler(
                recuperador, new FixedBackOff(1_000L, 2L));
        manejador.addNotRetryableExceptions(IllegalArgumentException.class);
        return manejador;
    }
}
```

`FixedBackOff(1_000L, 2L)` permite **dos reintentos tras el intento inicial** para errores reintentables. Un `IllegalArgumentException` del ejemplo se aparta directamente. En un sistema real es mejor crear excepciones propias para distinguir **contrato inválido** de otros errores de programación; clasificar toda `IllegalArgumentException` como permanente puede esconder un bug. Si la publicación en DLT falla, no se debe asumir que el registro original quedó resuelto. Monitoreá ambos fallos.

En el laboratorio podés inspeccionar los registros apartados con un **grupo de diagnóstico**. Hacerlo no los elimina de la DLT:

```powershell
docker exec -it kafka-local /opt/kafka/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic pedidos-confirmados-dlt --from-beginning --group inspeccion-dlt
```

### Deserialización antes de entrar al listener

El recorrido principal usa `StringDeserializer`, por lo que el JSON se valida dentro del listener y el error llega al `DefaultErrorHandler`. Si cambiás a un deserializador tipado, un error puede ocurrir durante `poll()`, **antes** de invocar el listener. Para que el contenedor pueda manejarlo, Spring Kafka ofrece `ErrorHandlingDeserializer`, que envuelve al deserializador real. Ejemplo para JSON tipado, **alternativo** al recorrido principal:

```yaml
spring:
  kafka:
    consumer:
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.ErrorHandlingDeserializer
      properties:
        "[spring.deserializer.value.delegate.class]": org.springframework.kafka.support.serializer.JsonDeserializer
        "[spring.json.value.default.type]": com.ejemplo.kafka.PedidoConfirmado
        "[spring.json.trusted.packages]": com.ejemplo.kafka
```

El paquete y el tipo deben ser los reales del proyecto. Comprobá los nombres de clases para la **versión de Spring Kafka** que estés usando. Si el error llega a DLT, conservá bytes originales, clave, tópico, partición, offset y causa; diseñá también quién puede inspeccionar y reprocesar esa DLT. Una DLT sin propietario y sin alertas se convierte en pérdida silenciosa de trabajo.

---

## 35. Outbox, deduplicación y transacciones

La secuencia «guardar pedido en PostgreSQL y publicar en Kafka» tiene dos sistemas con compromisos separados. Una transacción Kafka puede agrupar operaciones **dentro de Kafka**; no convierte por sí sola la escritura SQL y la publicación en un único commit indivisible. Incluso cuando Spring sincroniza transacciones de base y Kafka, **un commit puede completarse y el otro fallar**.

### Outbox para publicar hechos nacidos en SQL

Dentro de **una transacción de PostgreSQL**, guardá el estado del pedido y una fila de outbox con ID estable:

```sql
BEGIN;

UPDATE pedido SET estado = 'CONFIRMADO' WHERE id = 'P-42';
INSERT INTO outbox_evento (evento_id, tipo, clave, payload, estado)
VALUES ('E-91', 'PedidoConfirmado', 'P-42', '{...}', 'PENDIENTE');

COMMIT;
```

El SQL es un **esquema ilustrativo**: en tu aplicación la transacción la controla el servicio y `evento_id` debe ser único. Un publicador lee filas pendientes, envía a Kafka y registra la confirmación. Si publica y cae antes de marcar la fila como enviada, `E-91` puede salir de nuevo. Por eso el consumidor usa `eventoId` para reconocer repeticiones. Un CDC que publica desde la outbox puede reemplazar el publicador por polling, pero sigue requiriendo diseño de contrato, seguimiento y deduplicación.

### Deduplicar el efecto, no solo el registro

En el servicio Auditoría, insertá una marca de `eventoId` **y el efecto** dentro de una misma transacción local. Una restricción `UNIQUE` en `(consumidor, evento_id)` ayuda a resolver carreras entre reinicios o instancias. Si ya existe la marca, el consumidor termina normalmente y permite confirmar el offset.

```text
BEGIN transacción SQL
  insertar marca (auditoria, E-91) si no existe
  si fue insertada: escribir auditoría
COMMIT SQL
retornar del listener -> confirmar offset
```

La marca y el efecto deben compartir transacción. Guardar la marca antes en una transacción distinta puede hacer que un fallo deje el efecto ausente, pero parezca procesado.

Si el efecto de Auditoría es **guardar una fila por evento**, la misma fila puede ser marca y efecto. Ejemplo con PostgreSQL y `JdbcTemplate`, que requiere las dependencias JDBC y el controlador PostgreSQL del proyecto:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jdbc</artifactId>
</dependency>
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

```sql
CREATE TABLE auditoria_evento (
    evento_id varchar(100) PRIMARY KEY,
    pedido_id varchar(100) NOT NULL,
    ocurrido_en timestamptz NOT NULL
);
```

```java
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditoriaPedidos {
    private final JdbcTemplate jdbc;

    public AuditoriaPedidos(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Transactional
    public void registrarUnaVez(PedidoConfirmado evento) {
        jdbc.update("""
            INSERT INTO auditoria_evento (evento_id, pedido_id, ocurrido_en)
            VALUES (?, ?, ?)
            ON CONFLICT (evento_id) DO NOTHING
            """, evento.eventoId(), evento.pedidoId(),
                java.sql.Timestamp.from(evento.ocurridoEn()));
    }
}
```

Si `eventoId` se repite con **contenido diferente**, no conviene ignorarlo silenciosamente: compará el contrato original o registrá una anomalía. Para varios efectos, guardá la marca y todos los cambios en la misma transacción SQL. Si usás `@Transactional`, comprobá que el método se invoque a través del bean administrado por Spring, y no mediante una llamada interna que evite el interceptor transaccional.

### Cuándo sirven transacciones Kafka

Si una aplicación **consume de un tópico y produce a otro**, las transacciones Kafka pueden coordinar registros de salida y offsets del grupo dentro del ámbito Kafka. Los consumidores aguas abajo suelen necesitar `isolation.level=read_committed` para no ver escrituras abortadas. La configuración de Spring requiere un `transaction-id-prefix` único por instancia y una política coherente del listener. No actives transacciones solo por ver la frase *exactly once*: primero escribí qué efecto concreto querés que ocurra una vez y en qué sistema.

---

## 36. Pruebas de integración y fallos

Las pruebas útiles verifican **una garantía de negocio**: publicación observable, distribución entre grupos, tratamiento de errores o deduplicación. Un mock de `KafkaTemplate` prueba que se llamó un método, pero no muestra si el evento llega al broker ni cómo avanzan offsets. Testcontainers levanta un broker real para la prueba; `@EmbeddedKafka` es una alternativa ofrecida por `spring-kafka-test`.

### Dependencias de prueba

Además de `spring-boot-starter-test` y `spring-kafka-test`, incorporá el módulo Kafka y la integración JUnit de **Testcontainers**, con versiones gestionadas por el BOM compatible de tu proyecto. En versiones actuales, la clase para la imagen oficial está en `org.testcontainers.kafka.KafkaContainer`; la clase antigua `org.testcontainers.containers.KafkaContainer` está obsoleta.

```xml
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>kafka</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>
```

### Comprobar una publicación real

Este test integra el `PublicadorPedidos` de la sección 32 con un broker real de prueba. `@DynamicPropertySource` conecta Spring al broker efímero; un consumidor con **grupo propio** verifica la clave y el contrato publicados. La configuración anidada permite probar solo las piezas necesarias, sin levantar la base de datos de Auditoría.

```java
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;

@Testcontainers
@SpringBootTest(classes = {
        PublicacionPedidosIT.Configuracion.class, PublicadorPedidos.class
})
class PublicacionPedidosIT {
    @Container
    static KafkaContainer kafka = new KafkaContainer("apache/kafka:4.4.0");

    @DynamicPropertySource
    static void configurarKafka(DynamicPropertyRegistry propiedades) {
        propiedades.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @Autowired private PublicadorPedidos publicador;
    @Autowired private ObjectMapper json;

    @Test
    void publicaConLaClaveDelPedido() throws Exception {
        Map<String, Object> opciones = new HashMap<>();
        opciones.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                kafka.getBootstrapServers());
        opciones.put(ConsumerConfig.GROUP_ID_CONFIG,
                "inspector-" + UUID.randomUUID());
        opciones.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        opciones.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class);
        opciones.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class);

        try (KafkaConsumer<String, String> inspector =
                     new KafkaConsumer<>(opciones)) {
            inspector.subscribe(List.of("pedidos-confirmados"));

            var evento = new PedidoConfirmado("PedidoConfirmado", 1,
                    "E-91", "P-42", Instant.parse("2026-11-17T12:00:00Z"));
            publicador.publicar(evento).get(10, TimeUnit.SECONDS);

            var recibido = KafkaTestUtils.getSingleRecord(inspector,
                    "pedidos-confirmados", Duration.ofSeconds(10));
            assertEquals("P-42", recibido.key());
            var cuerpo = json.readTree(recibido.value());
            assertEquals("E-91", cuerpo.get("eventoId").asText());
            assertEquals(1, cuerpo.get("version").asInt());
        }
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    static class Configuracion {
        @Bean
        NewTopic topicoDePrueba() {
            return TopicBuilder.name("pedidos-confirmados")
                    .partitions(3).replicas(1).build();
        }
    }
}
```

Este test supone que `PedidoConfirmado` y `PublicadorPedidos` están en el paquete del proyecto o importados. Para probar el **caso completo**, agregá una base de datos de prueba y observá el efecto persistido, no solo el mensaje. Evitá `Thread.sleep` como sincronización; esperá una condición con plazo explícito. `@EmbeddedKafka` es otra opción si querés probar la integración sin Docker.

### Matriz de fallos que sí vale la pena probar

| Situación provocada | Invariante observable |
|---|---|
| Publicar `E-91` dos veces | Auditoría queda una sola vez. |
| JSON inválido | Se aparta y conserva información para investigar. |
| PostgreSQL cae durante el listener | Offset no avanza como si el efecto hubiera terminado. |
| Aplicar el efecto y caer antes del offset | El reinicio no duplica el efecto. |
| Estadísticas queda atrasado | Auditoría continúa por su grupo. |
| Outbox publica y no marca enviada | La repetición conserva `eventoId`. |

Probá al menos un camino de **fallo y recuperación** antes de afirmar que la integración es confiable. Las pruebas de un solo broker no prueban tolerancia a caída de un broker en un clúster replicado.

---

## 37. Métricas, lag y diagnóstico

Una aplicación puede estar viva y **no estar avanzando**. Combiná señales del broker, de los clientes y del negocio. El comando `kafka-consumer-groups.sh --describe` permite una primera inspección de offsets y lag, pero no reemplaza monitoreo continuo.

| Señal | Qué puede indicar | Qué verificar después |
|---|---|---|
| Lag en aumento | El grupo procesa más lento de lo que llegan registros. | Edad del evento, errores, tiempo de base, particiones. |
| Grupo sin miembros | Consumidores detenidos o incapaces de unirse. | Arranque, conectividad, autenticación, logs. |
| Rebalanceos frecuentes | Miembros inestables o procesamiento demasiado largo. | `max.poll.interval.ms`, reinicios, escalado. |
| Errores de producción | Registros no confirmados o clúster no disponible. | Excepción, `acks`, ISR, conexión, permisos. |
| DLT creciendo | Trabajo apartado que nadie resolvió. | Tipos de error, fecha, propietario, plan de replay. |
| Latencia de negocio alta con lag bajo | El problema quizá esté fuera de Kafka. | Base de datos, llamadas externas, confirmación prematura. |

**Lag en registros no equivale a atraso en tiempo.** Si un grupo tiene 100 registros pendientes de hace dos segundos, puede estar sano. Si tiene uno pendiente de dos horas, puede afectar el servicio. Medí además tiempo entre `ocurridoEn` y efecto aplicado, tasa de errores, tasa de reintentos y registros en DLT.

Spring Kafka puede exponer observaciones de `KafkaTemplate` y de listeners mediante Micrometer. Si el proyecto usa Actuator, añadí el starter correspondiente y configurá la exportación de métricas del entorno. No prometas que un endpoint de salud «UP» demuestra que todos los grupos están al día: una conexión disponible y un proceso al día son cosas distintas.

### Diagnóstico de un evento ausente

1. Confirmá el `eventoId` y el estado del pedido en la base de origen.
2. Buscá la fila outbox y su estado, si el flujo usa outbox.
3. Confirmá si el productor recibió éxito o una excepción y registró tópico, partición y offset.
4. Inspeccioná el tópico y el grupo correcto; otro grupo tiene otra posición.
5. Revisá DLT, logs de deserialización y métricas del listener.
6. Confirmá el efecto en la base destino por `eventoId`, no solo el log «recibido».

No registres el payload completo en logs generales si contiene datos sensibles. Para correlación suele bastar `eventoId`, clave, tópico, partición y offset.

---

## 38. Rendimiento, particionado y capacidad

Kafka puede mover alto volumen, pero el rendimiento de la aplicación también depende de **serialización, red, disco, número de particiones y tiempo del consumidor**. Optimizar sin medir puede aumentar complejidad o romper orden.

### Productor

| Palanca | Efecto | Costo o cuidado |
|---|---|---|
| `linger.ms` | Espera breve para agrupar envíos. | Puede aumentar latencia individual. |
| `batch.size` | Límite de lote por partición del productor. | Más memoria; no garantiza lotes llenos. |
| Compresión | Reduce bytes enviados y almacenados. | Usa CPU; elegir códec compatible. |
| `delivery.timeout.ms` | Acota el tiempo total de entrega. | Un timeout deja incertidumbre sobre recepción. |
| Clave bien elegida | Conserva orden por entidad. | Una clave muy frecuente puede sobrecargar una partición. |

El productor acumula envíos asíncronos; si se supera capacidad, el envío puede esperar o fallar según la configuración. No crees un `KafkaProducer` por cada evento. En Spring, `KafkaTemplate` comparte la infraestructura de productor gestionada por el framework.

### Consumidor

| Palanca | Efecto | Costo o cuidado |
|---|---|---|
| Más instancias en el grupo | Reparten particiones. | No superan el paralelismo útil de las particiones. |
| `max.poll.records` | Limita registros por `poll`. | Lotes grandes pueden exceder el tiempo de procesamiento. |
| Concurrencia del listener | Crea más consumidores del mismo grupo. | Estado compartido y presión en bases externas. |
| Procesar en lote | Reduce viajes a una base o API. | Aumenta complejidad de error y confirmación parcial. |
| Mejorar el efecto externo | Reduce tiempo por registro. | Puede exigir cambios de índice, transacción o API. |

**Ejemplo de clave caliente:** si el 80 % de los eventos usa `clienteId=1`, esa clave concentra el trabajo en una partición. Más particiones no distribuyen **esa misma clave** sin sacrificar el orden por cliente. Primero preguntá si el orden necesita ser por cliente, por pedido o por otra entidad. La clave es una decisión de negocio y capacidad a la vez.

Una guía de capacidad comienza con una medición: registros por segundo, tamaño medio y máximo, retención, número de grupos, tiempo de procesamiento y pico esperado. Estimá almacenamiento como una aproximación inicial (`tasa × tamaño × retención × réplicas`) y validá con compresión, índices, segmentos y margen operativo. No uses esa fórmula como dimensionamiento final.

---

## 39. Seguridad y configuración por entorno

El laboratorio abre `localhost:9092` sin autenticación. En un entorno compartido definí **quién se conecta, qué puede leer y escribir, y cómo se protege el transporte**. Los permisos se aplican a recursos como tópicos y grupos; una cuenta de consumidor no necesita administrar el clúster.

| Capa | Decisión |
|---|---|
| Red | Brokers accesibles solo desde clientes autorizados; direcciones anunciadas válidas. |
| Transporte | TLS si los datos atraviesan redes donde se necesita cifrado. |
| Identidad | Mecanismo de autenticación que soporte el clúster, por ejemplo SASL. |
| Permisos | Acceso mínimo a tópicos y grupos concretos. |
| Secretos | Credenciales fuera del repositorio, rotación y permisos de lectura limitados. |
| Datos | No publicar más datos personales o secretos de los necesarios. |

Ejemplo **solo de estructura**, para un clúster que ya haya habilitado SASL sobre TLS:

```yaml
spring:
  kafka:
    bootstrap-servers: ${KAFKA_BOOTSTRAP_SERVERS}
    properties:
      "[security.protocol]": SASL_SSL
      "[sasl.mechanism]": PLAIN
      "[sasl.jaas.config]": ${KAFKA_SASL_JAAS_CONFIG}
```

El mecanismo real, el almacén de certificados y la política de permisos dependen del proveedor. Esta configuración **no habilita seguridad en el broker** ni debe copiarse a producción sin conocer la plataforma. No pongas la cadena JAAS literal en `application.yml` versionado. Separá valores de desarrollo y producción; evitá usar `localhost` dentro de un contenedor cuando el broker está en otro servicio.

---

## 40. Kafka Connect, Streams y límites del ecosistema

Kafka puede integrarse con otras fuentes y procesar flujos, pero conviene saber **qué pieza resuelve qué problema** antes de añadirla.

| Pieza | Uso típico | No reemplaza |
|---|---|---|
| Productor/consumidor Java | Lógica de una aplicación que publica o reacciona. | Diseño de contrato y efectos. |
| Kafka Connect | Integración configurable con sistemas externos mediante conectores. | Todas las reglas de negocio. |
| Kafka Streams | Transformaciones, joins y agregaciones de flujos en una aplicación Java. | Una base transaccional de negocio. |
| Registro de esquemas | Gestión de contratos y compatibilidad de formatos. | Decidir el significado de los campos. |

Connect suele usarse para mover cambios desde una base, un archivo o un servicio hacia Kafka, o desde Kafka hacia un destino. En un patrón outbox con CDC, el conector observa cambios de la tabla outbox y publica eventos. **La tabla, su transacción y el `eventoId` siguen siendo responsabilidad del diseño de la aplicación.**

Kafka Streams puede servir si la operación es una transformación continua, por ejemplo contar pedidos confirmados por ventana temporal o unir eventos por clave. Requiere pensar en ventanas, tiempo del evento, estado local, retención y datos que llegan tarde. Para una simple reacción de negocio que escribe en PostgreSQL, un consumidor Spring suele ser una entrada más clara.

---

## 41. Recorrido completo de pedidos con Spring

Este recorrido reúne los componentes anteriores y separa **hecho confirmado, publicación, entrega y efecto**. Es la referencia para armar un ejercicio real en Java. El fragmento SQL de la sección 35 y los componentes Spring de las secciones 29 a 34 son piezas del mismo sistema, pero Pedidos, Auditoría y Estadísticas pueden ser aplicaciones distintas.

```text
POST /pedidos/P-42/confirmar
  -> transacción PostgreSQL de Pedidos:
       pedido P-42 = CONFIRMADO
       outbox E-91 = PENDIENTE
  -> respuesta: pedido confirmado

Publicador de outbox
  -> KafkaTemplate.send(topic, key=P-42, payload E-91)
  -> observa confirmación o error
  -> marca outbox como enviado si hay confirmación

Tópico pedidos-confirmados (3 particiones)
  -> grupo auditoria: efecto SQL deduplicado por E-91
  -> grupo estadisticas: efecto SQL deduplicado por E-91
  -> cada grupo confirma sus offsets después del efecto
```

### Escenario normal

1. Pedidos confirma `P-42` y persiste `E-91` en la misma transacción.
2. El publicador envía el JSON versionado con clave `P-42`.
3. Kafka confirma tópico, partición y offset.
4. Cada grupo lee por su cuenta; Auditoría y Estadísticas pueden avanzar a distintas velocidades.
5. Cada consumidor guarda marca de `E-91` y efecto en una transacción local.
6. Al terminar, avanza el offset de su grupo.

### Escenarios que cambian la implementación

| Si ocurre... | Entonces... | Medida de diseño |
|---|---|---|
| La publicación queda incierta | La outbox puede reintentar `E-91`. | ID estable y consumidores idempotentes. |
| El efecto SQL termina pero falla el commit de offset | Kafka puede reentregar `E-91`. | Marca y efecto atómicos en SQL. |
| Un evento no se puede interpretar | El listener lanza excepción. | Error handler, DLT y responsable de revisión. |
| Estadísticas se atrasa | Su lag crece; Auditoría sigue. | Alerta por edad, capacidad y estado de grupo. |
| Retención elimina registros pendientes | Ya no basta Kafka para reconstruir ese historial. | Retención acorde a recuperación y respaldo del origen. |
| Se cambia el contrato a versión 2 | Consumidores antiguos pueden fallar. | Política de compatibilidad y despliegue. |

**Pregunta final de diseño:** ¿qué significa éxito para el usuario? En este caso, «pedido confirmado» significa que el estado y la outbox quedaron comprometidos en Pedidos. Si el producto exige que Auditoría termine antes de responder, necesitás otro contrato de respuesta y coordinación; no alcanza con publicar y confiar en que ocurrió inmediatamente.

---

## 42. Referencia rápida y plan de práctica

### Qué usar según la necesidad

| Necesidad | Primera opción a evaluar |
|---|---|
| Publicar un hecho desde Spring | `KafkaTemplate` y confirmación del futuro. |
| Consumir un hecho | `@KafkaListener` con grupo explícito. |
| Crear un tópico en desarrollo | `NewTopic` o consola. |
| Publicar tras una transacción SQL | Outbox. |
| Aplicar una vez un efecto SQL | ID del evento, restricción única y transacción local. |
| Reintentar fallos de listener | `DefaultErrorHandler` con límite. |
| Apartar fallos permanentes | `DeadLetterPublishingRecoverer` y tópico DLT. |
| Ver atraso | Grupo, offsets, lag y edad del evento. |
| Reprocesar | Grupo nuevo o cambio controlado de offsets. |
| Probar integración | Broker de prueba con Testcontainers o `@EmbeddedKafka`. |

### Ruta de práctica

1. Levantá el broker local y creá `pedidos-lab` con tres particiones.
2. Publicá registros con claves `P-42`, `P-42` y `P-50`; observá su partición y offset.
3. Creá dos grupos y confirmá que ambos leen el mismo historial por separado.
4. Ejecutá el productor y el listener Spring del recorrido; verificá el valor y la clave en el tópico.
5. Enviá un JSON inválido y observá la DLT; explicá por qué no conviene reintentarlo indefinidamente.
6. Provocá un fallo de la base destino; comprobá que el efecto no queda confirmado falsamente.
7. Enviá `E-91` dos veces y verificá un único efecto de auditoría.
8. Detené y reiniciá el consumidor; explicá por qué `earliest` no obliga a releer todo.

### Siglas que aparecen al operar Kafka

| Sigla | Significado en esta guía |
|---|---|
| **ISR** | Réplicas sincronizadas de una partición. |
| **DLT** | Tópico de registros apartados tras fallos de procesamiento. |
| **CDC** | Captura de cambios de una base para publicarlos en otro sistema. |
| **ACL** | Regla de autorización sobre recursos del clúster. |
| **KRaft** | Modo actual de gestión de metadatos del clúster Kafka. |

**Si podés explicar qué queda guardado en Kafka, qué guarda cada grupo y qué quedó persistido fuera de Kafka después de cada caída, ya estás razonando sobre la integración y no solo repitiendo APIs.**

---

## 43. Documentación oficial para profundizar

- [Introducción y conceptos de Kafka](https://kafka.apache.org/44/getting-started/introduction/): tópicos, particiones, registros y productores/consumidores.
- [Diseño de Kafka](https://kafka.apache.org/44/design/design/): replicación, offsets y garantías de procesamiento.
- [Configuración del productor](https://kafka.apache.org/44/configuration/producer-configs/): `acks`, reintentos e idempotencia.
- [Configuración del consumidor](https://kafka.apache.org/44/configuration/consumer-configs/): grupo, offsets y posición inicial.
- [Configuración de tópicos](https://kafka.apache.org/44/configuration/topic-configs/): retención, compactación y réplicas mínimas.
- [Guía rápida de Kafka 4.4](https://kafka.apache.org/44/getting-started/quickstart/) y [operaciones de consola](https://kafka.apache.org/44/operations/basic-kafka-operations/): laboratorio, tópicos y grupos.
- [Spring Boot 3.5 con Kafka](https://docs.spring.io/spring-boot/3.5/reference/messaging/kafka.html): propiedades y autoconfiguración de la línea usada en el recorrido.
- [Envío con Spring Kafka](https://docs.spring.io/spring-kafka/reference/3.3/kafka/sending-messages.html) y [recepción con `@KafkaListener`](https://docs.spring.io/spring-kafka/reference/3.3/kafka/receiving-messages/listener-annotation.html).
- [Errores y DLT](https://docs.spring.io/spring-kafka/reference/3.3/kafka/annotation-error-handling.html), [transacciones](https://docs.spring.io/spring-kafka/reference/3.3/kafka/transactions.html) y [pruebas Spring Kafka](https://docs.spring.io/spring-kafka/reference/3.3/testing.html).
- [Módulo Kafka de Testcontainers](https://java.testcontainers.org/modules/kafka/): broker efímero para pruebas.

Las opciones y valores por defecto pueden cambiar entre versiones. Usá estas páginas para verificar la versión concreta del cliente y del clúster que tengas delante.
