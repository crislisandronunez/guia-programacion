# Glosario y Guía de Fundamentos de Redes para Programadores

Esta guía explica **qué sucede cuando un programa se comunica con otro**: cómo encuentra al destinatario, abre una conexión, envía datos, recibe una respuesta y enfrenta demoras o fallos. Los conceptos aparecen antes que los protocolos concretos; los ejemplos usan **Java** para observarlos en un programa.

---

## Índice

1. [Por qué un programador necesita entender redes](#1-por-qué-un-programador-necesita-entender-redes)
2. [Glosario fundamental](#2-glosario-fundamental)
3. [Capas: cada protocolo resuelve un problema distinto](#3-capas-cada-protocolo-resuelve-un-problema-distinto)
4. [Cliente, servidor, dirección y puerto](#4-cliente-servidor-dirección-y-puerto)
5. [DNS: encontrar la dirección de un nombre](#5-dns-encontrar-la-dirección-de-un-nombre)
6. [Paquetes, rutas y pérdidas](#6-paquetes-rutas-y-pérdidas)
7. [TCP y UDP: dos formas de transporte](#7-tcp-y-udp-dos-formas-de-transporte)
8. [Conexión, petición y respuesta no son lo mismo](#8-conexión-petición-y-respuesta-no-son-lo-mismo)
9. [TLS y HTTPS: proteger la comunicación](#9-tls-y-https-proteger-la-comunicación)
10. [Anatomía de un mensaje HTTP](#10-anatomía-de-un-mensaje-http)
11. [Métodos, estados e idempotencia](#11-métodos-estados-e-idempotencia)
12. [Bytes, texto, formatos y límites de mensaje](#12-bytes-texto-formatos-y-límites-de-mensaje)
13. [Latencia, ancho de banda y capacidad](#13-latencia-ancho-de-banda-y-capacidad)
14. [Timeouts, reintentos y resultado desconocido](#14-timeouts-reintentos-y-resultado-desconocido)
15. [Reutilizar conexiones y limitar recursos](#15-reutilizar-conexiones-y-limitar-recursos)
16. [Comunicación síncrona, asíncrona y bloqueo](#16-comunicación-síncrona-asíncrona-y-bloqueo)
17. [Intermediarios: proxies y balanceadores](#17-intermediarios-proxies-y-balanceadores)
18. [Observar y diagnosticar una comunicación](#18-observar-y-diagnosticar-una-comunicación)
19. [Caso completo: cliente y servidor HTTP locales en Java](#19-caso-completo-cliente-y-servidor-http-locales-en-java)
20. [Ejercicios de razonamiento](#20-ejercicios-de-razonamiento)
21. [Lista de verificación antes de integrar dos sistemas](#21-lista-de-verificación-antes-de-integrar-dos-sistemas)

---

## 1. Por qué un programador necesita entender redes

Una llamada a otro programa no es igual a llamar a un método local. Entre ambos pueden existir resolución de nombres, conexiones, intermediarios, límites de tiempo y fallos parciales.

```text
Tu programa -> red -> servicio de precios

¿El destinatario existe?
¿Se pudo establecer la conexión?
¿La petición llegó?
¿El servidor la procesó?
¿La respuesta regresó completa?
```

Si falta la respuesta, **no siempre sabés cuál de esos pasos falló**. Por eso las decisiones de timeout, reintento e idempotencia dependen de entender el recorrido de los datos.

Esta guía se concentra en el modelo mental de comunicación. Las guías de Spring Web y resiliencia desarrollan herramientas específicas sobre esta base.

---

## 2. Glosario fundamental

| Término | Significado | Ejemplo |
|---|---|---|
| **Cliente** | Programa que inicia una comunicación. | Una aplicación que consulta una API. |
| **Servidor** | Programa que escucha y atiende comunicaciones. | Servicio que responde cotizaciones. |
| **IP** | Dirección usada para enrutar datos hacia una interfaz de red. | `127.0.0.1` para la propia máquina. |
| **Puerto** | Número que ayuda a entregar la comunicación al proceso correcto. | Un servidor escucha en `8080`. |
| **Socket** | Extremo de comunicación que maneja el sistema operativo. | Conexión del cliente al servidor. |
| **DNS** | Sistema que resuelve nombres a direcciones. | `api.ejemplo.com` → una IP. |
| **Paquete** | Unidad de datos transportada por la red. | Parte de una transmisión. |
| **TCP** | Transporte orientado a conexión que ofrece un flujo de bytes ordenado. | Una conexión HTTP clásica. |
| **UDP** | Transporte de datagramas sin las mismas garantías de TCP. | Base para protocolos que gestionan por sí mismos entrega y orden. |
| **TLS** | Protocolo que protege una comunicación y permite autenticar al servidor mediante certificados. | HTTPS. |
| **HTTP** | Protocolo de petición y respuesta entre aplicaciones. | `GET /productos/42`. |
| **URL** | Identificador de un recurso y su ubicación o acceso. | `https://ejemplo.com:443/productos/42`. |
| **Header** | Metadato de un mensaje. | `Content-Type: application/json`. |
| **Body** | Contenido de una petición o respuesta. | Un documento JSON. |
| **Latencia** | Tiempo que tarda una operación o comunicación. | Respuesta en `120 ms`. |
| **Timeout** | Límite de espera que decide cuándo abandonar un intento. | Cortar una consulta tras un plazo definido. |
| **Reintento** | Nuevo intento después de uno fallido o sin respuesta. | Volver a consultar un recurso. |
| **Idempotencia** | Propiedad de una operación cuyo efecto final no cambia al repetirla con la misma intención. | Fijar un valor específico varias veces. |

**Distinción clave:** una IP, un puerto, una conexión y una URL responden preguntas diferentes. No son nombres intercambiables para «el servidor».

---

## 3. Capas: cada protocolo resuelve un problema distinto

La comunicación combina protocolos. Un modelo simplificado para una petición web es:

```text
Aplicación: HTTP      -> significado de petición, respuesta, método y estado
Seguridad: TLS        -> protección de los bytes y autenticación del extremo
Transporte: TCP       -> flujo de bytes entre extremos
Red: IP               -> encaminamiento hacia una dirección
```

El mensaje HTTP se **representa como bytes** y viaja usando mecanismos de las capas inferiores. Al recibirlo, el servidor interpreta esos bytes según el protocolo de aplicación.

Este esquema sirve para pensar un caso común de HTTPS sobre TCP. **No todos los protocolos usan exactamente la misma combinación**: hay formas de HTTP que utilizan otros transportes. La pregunta práctica es *«¿qué capa explica este fallo?»*: un nombre que no resuelve, una conexión que no se abre y una respuesta HTTP `404` son problemas distintos.

---

## 4. Cliente, servidor, dirección y puerto

Para llegar a un servicio, el cliente necesita una dirección de destino y un puerto donde el servidor escucha. La dirección identifica un destino de red; el puerto diferencia servicios o conexiones en ese destino.

```text
http://127.0.0.1:8080/saludo
       └── IP ──┘ └puerto┘ └ruta┘
```

`127.0.0.1` es la dirección de **loopback**: la comunicación permanece en la propia máquina. Sirve para estudiar cliente y servidor, pero no prueba el comportamiento de una red externa.

| Pregunta | Dato que la responde |
|---|---|
| ¿A qué máquina o interfaz intento llegar? | Dirección IP o nombre que se resolverá. |
| ¿Qué servicio debe recibir la conexión? | Puerto. |
| ¿Qué recurso le pido al servicio HTTP? | Ruta y otros componentes de la URL. |

Que un proceso esté encendido no garantiza que escuche en el puerto esperado. Que escuche en el puerto no garantiza que la ruta solicitada exista.

---

## 5. DNS: encontrar la dirección de un nombre

Las personas y programas suelen usar nombres como `api.ejemplo.com`. Antes de conectar, el cliente necesita una dirección apropiada. **DNS** proporciona esa resolución.

```text
Nombre de host -> consulta DNS -> una o más direcciones posibles
                                     |
                                     v
                               intento de conexión
```

| Situación | Qué podría observarse |
|---|---|
| Nombre mal escrito o inexistente | No se obtiene una dirección. |
| DNS tarda en responder | Demora antes de intentar conectar. |
| Cambia la dirección del servicio | Las respuestas guardadas temporalmente pueden tardar en actualizarse. |

Resolver un nombre **no demuestra que el servidor funcione**. DNS responde cómo encontrar una dirección; después todavía hay que conectar y comunicarse. Un mismo nombre puede llevar a distintas direcciones según la configuración del servicio.

---

## 6. Paquetes, rutas y pérdidas

Los datos atraviesan enlaces y equipos intermedios. Una transmisión puede dividirse en unidades más pequeñas que recorren la red. Algunas pueden demorarse o perderse; los protocolos de transporte deciden cómo tratar esas situaciones.

```text
Cliente -> red local -> equipos intermedios -> red del servidor -> servidor
```

**No confundas:** que el cliente haya entregado bytes a su sistema operativo no prueba que la aplicación remota los haya procesado. Incluso si se estableció una conexión, puede fallar antes de que llegue una respuesta.

Al investigar, localizá la fase: resolución de nombre, conexión, envío, procesamiento remoto o recepción. Esa división orienta qué evidencia necesitas.

---

## 7. TCP y UDP: dos formas de transporte

**TCP** establece una conexión y presenta a la aplicación un **flujo ordenado de bytes**. Se ocupa de retransmitir datos perdidos y controlar el flujo mientras la conexión funciona. **UDP** envía datagramas sin establecer una conexión TCP ni ofrecer por sí mismo la misma entrega ordenada; una aplicación o protocolo construido sobre UDP puede añadir sus propias garantías.

| Pregunta | TCP | UDP |
|---|---|---|
| ¿La aplicación ve un flujo de bytes ordenado? | Sí. | No; recibe datagramas. |
| ¿El transporte gestiona retransmisión? | Sí. | No por sí mismo. |
| ¿Hay un establecimiento de conexión TCP? | Sí. | No. |
| ¿Desaparecen todos los fallos? | No. | No. |

**Punto importante:** TCP **no define límites de mensajes de tu aplicación**. Dos llamadas de envío pueden recibirse en fragmentos o juntas; el protocolo de aplicación debe definir cómo reconocer un mensaje completo. Tampoco garantiza que una operación de negocio se haya completado si la conexión se corta antes de recibir la respuesta.

---

## 8. Conexión, petición y respuesta no son lo mismo

Una **conexión** es un canal de transporte. Una **petición** es un mensaje con significado de aplicación. Una **respuesta** es otro mensaje. Según el protocolo y la configuración, una conexión puede servir para más de un intercambio.

```text
1. Abrir conexión.
2. Enviar petición A.
3. Recibir respuesta A.
4. Enviar petición B por una conexión reutilizada, si corresponde.
5. Recibir respuesta B.
```

Esto explica tres fallos diferentes:

| Fallo | Qué sabemos |
|---|---|
| No se pudo conectar | La petición no empezó a viajar por esa conexión. |
| Conexión abierta, sin respuesta | El resultado remoto puede ser desconocido. |
| Respuesta HTTP de error | El servidor o intermediario **sí devolvió** un mensaje con un estado. |

Una respuesta `500` no es un «fallo de conexión»: es un resultado HTTP que informa que la operación no pudo completarse normalmente en el lado que respondió.

---

## 9. TLS y HTTPS: proteger la comunicación

**TLS** cifra la comunicación, protege su integridad y permite al cliente verificar la identidad del servidor mediante certificados. **HTTPS** es HTTP protegido con TLS.

```text
Cliente -- establece comunicación TLS y verifica el servidor --> Servidor
Cliente -- envía HTTP dentro del canal protegido -----------> Servidor
```

Si falla la verificación del certificado, no alcanza con que exista conexión TCP: el cliente no debería tratar al extremo como confiable. Desactivar la validación de certificados para «hacer que funcione» elimina una protección central.

TLS protege el tramo **entre los extremos de esa conexión TLS**. Si un intermediario termina TLS y abre otra conexión hacia un servidor interno, hay que analizar también ese segundo tramo. TLS tampoco sustituye autorización: un canal seguro no implica que cualquier cliente pueda ejecutar cualquier operación.

---

## 10. Anatomía de un mensaje HTTP

HTTP define la forma de una petición y una respuesta: método, destino, cabeceras, cuerpo opcional y estado de respuesta.

```http
GET /productos/42 HTTP/1.1
Host: ejemplo.com
Accept: application/json

```

```http
HTTP/1.1 200 OK
Content-Type: application/json

{"id":42,"nombre":"Cuaderno"}
```

| Parte | Función |
|---|---|
| Método `GET` | Expresa la operación solicitada. |
| Ruta `/productos/42` | Identifica el recurso dentro del servicio. |
| `Host` | Identifica el host solicitado en HTTP. |
| `Accept` | Indica formatos de respuesta aceptables. |
| Estado `200` | Informa el resultado de la petición a nivel HTTP. |
| `Content-Type` | Describe el formato del cuerpo enviado. |
| Cuerpo | Contiene la representación del recurso. |

Este texto muestra una representación legible; la forma concreta de los bytes varía según la versión de HTTP. **Una respuesta HTTP exitosa no demuestra por sí sola que el dato de negocio sea correcto**: también debe cumplir el contrato de la API.

---

## 11. Métodos, estados e idempotencia

Los métodos HTTP comunican la intención de la petición. Los códigos de estado comunican una clase de resultado.

| Método | Intención habitual | Pregunta al repetir |
|---|---|---|
| `GET` | Obtener una representación. | ¿La repetición cambia el estado del recurso? No debería. |
| `POST` | Enviar datos para procesar o crear. | ¿Podría crear o ejecutar dos veces? |
| `PUT` | Establecer una representación de un recurso. | ¿Repetir el mismo valor deja el mismo estado final? |
| `DELETE` | Eliminar un recurso o relación. | ¿Repetir deja el recurso ausente? |

| Familia de estados | Significado general |
|---|---|
| `2xx` | La petición se procesó con éxito según el estado concreto. |
| `3xx` | Redirección u otra acción para obtener el recurso. |
| `4xx` | La petición no puede atenderse en las condiciones indicadas por el cliente. |
| `5xx` | El servidor o intermediario encontró un problema al atenderla. |

**Idempotencia** se refiere al **efecto final** de repetir la misma intención, no a que cada respuesta tenga los mismos bytes o el mismo código. Por ejemplo, un `DELETE` repetido puede recibir respuestas diferentes y seguir dejando el recurso ausente. Una operación concreta puede incumplir las propiedades esperadas de su método si fue implementada incorrectamente; no supongas seguridad para reintentar solo por leer `PUT` o `DELETE`.

---

## 12. Bytes, texto, formatos y límites de mensaje

La red transporta **bytes**. Cliente y servidor necesitan acordar cómo interpretarlos: codificación de texto, formato, campos obligatorios y límites de tamaño.

```text
Objeto Java -> serialización -> bytes -> red -> bytes -> deserialización -> datos del receptor
```

| Problema | Ejemplo |
|---|---|
| Codificación distinta | Caracteres acentuados se interpretan mal. |
| Formato distinto | El cliente espera JSON y recibe HTML de error. |
| Contrato distinto | Falta un campo obligatorio o cambia su tipo. |
| Tamaño sin límite | Un cuerpo enorme consume memoria antes de validarse. |

`Content-Type` ayuda a declarar el formato del cuerpo HTTP. **No alcanza con que el texto sea JSON válido**: sus campos y significados deben cumplir el contrato acordado. Las transformaciones de datos también pueden perder precisión, unidades o fechas si no se definen bien.

---

## 13. Latencia, ancho de banda y capacidad

Estos términos miden cosas distintas:

| Medida | Pregunta | Ejemplo |
|---|---|---|
| **Latencia** | ¿Cuánto tarda una operación? | Una petición tarda `200 ms`. |
| **Ancho de banda** | ¿Cuántos bits por segundo puede transportar un enlace? | Capacidad nominal de la conexión. |
| **Throughput o caudal** | ¿Cuánto trabajo útil se completa por unidad de tiempo? | Peticiones completadas por segundo. |

Una respuesta pequeña puede tardar mucho si exige varios viajes o espera procesamiento remoto. Un archivo grande puede tardar por volumen de bytes aunque la primera respuesta llegue rápido.

```text
Tres llamadas secuenciales de 200 ms cada una
-> alrededor de 600 ms solo en esas esperas, si no se solapan.
```

La latencia total incluye más que la red: DNS, conexión, TLS, colas, procesamiento, transferencia y deserialización. Para diagnosticar, **medí por etapas** antes de atribuir toda demora a «internet».

---

## 14. Timeouts, reintentos y resultado desconocido

Un **timeout** define cuánto está dispuesto a esperar el cliente. No demuestra que el servidor haya cancelado el trabajo.

```text
Cliente envía "crear pedido".
Servidor crea el pedido.
La respuesta se pierde.
Cliente llega al timeout.

Desde el cliente: resultado desconocido.
Desde el servidor: pedido creado.
```

Reintentar sin más puede crear otro pedido. Antes de repetir una operación, preguntá:

1. ¿La petición pudo haber llegado y producir un efecto?
2. ¿La operación es idempotente por su diseño real?
3. ¿Existe una clave de operación o mecanismo para reconocer el mismo intento?
4. ¿Se puede consultar el estado antes de decidir?

Un reintento también consume capacidad y tiempo. Elegí límites y pausas apropiados; reintentos masivos e inmediatos pueden empeorar una caída. La guía de resiliencia desarrolla políticas de reintento. Aquí la idea fundamental es **no confundir «no recibí respuesta» con «no ocurrió»**.

---

## 15. Reutilizar conexiones y limitar recursos

Abrir una conexión nueva puede exigir trabajo adicional. Reutilizar conexiones cuando el protocolo y el cliente lo permiten evita repetir parte de ese costo.

```text
Sin reutilizar: conectar -> solicitar -> responder -> cerrar, en cada petición.
Reutilizando: conectar -> solicitar/responder varias veces -> cerrar al final.
```

Pero una conexión abierta consume recursos. También puede quedar inactiva, cerrarse por un intermediario o saturarse si se espera demasiado trabajo sobre ella. Por eso los clientes suelen tener políticas de reutilización, límites y tiempos de espera.

**Distinción:** el número de conexiones no es automáticamente el número de solicitudes concurrentes. Depende del protocolo y de cómo se use. Lo importante es definir cuánta demanda puede admitir cada dependencia y qué hará el cliente cuando se alcance ese límite.

---

## 16. Comunicación síncrona, asíncrona y bloqueo

Una comunicación **síncrona** suele esperar una respuesta para continuar el flujo. Una interacción **asíncrona** permite seguir y conocer el resultado más tarde. Esto es independiente de si el hilo bloquea durante alguna espera.

```text
Síncrono: consultar precio -> esperar -> mostrar precio.
Asíncrono: solicitar informe -> recibir ID -> consultar resultado después.
```

| Pregunta | Consecuencia de diseño |
|---|---|
| ¿El usuario necesita la respuesta ahora? | Puede convenir una petición con respuesta inmediata. |
| ¿El trabajo tarda mucho? | Puede convenir separar inicio y resultado. |
| ¿Qué pasa si el receptor está caído? | Hay que definir rechazo, espera o procesamiento posterior. |
| ¿Cómo se reconoce el resultado correcto? | Puede necesitar un identificador de operación. |

Usar una cola o un evento no elimina los fallos: cambia **cuándo** se observa el resultado y cómo se coordina. La futura guía de mensajería profundizará en ese modelo.

---

## 17. Intermediarios: proxies y balanceadores

El cliente no siempre habla directamente con el proceso que resuelve la petición. Un **proxy** recibe y reenvía comunicaciones; un **balanceador** reparte trabajo entre destinos según una política.

```text
Cliente -> intermediario -> servidor A
                        \-> servidor B
```

Un intermediario puede terminar TLS, modificar cabeceras, rechazar cuerpos grandes, aplicar un timeout o producir una respuesta propia. Por eso, cuando aparece un estado de error, preguntá **qué componente respondió realmente**.

| Síntoma | Posible lugar para investigar |
|---|---|
| Solo fallan cuerpos grandes | Límites del intermediario o del servidor. |
| Unas solicitudes funcionan y otras no | Destinos distintos detrás del balanceador. |
| El cliente ve timeout antes que el servidor | Plazos distintos en cada salto. |

No concluyas la causa a partir de esta tabla: usala para decidir qué evidencia recoger.

---

## 18. Observar y diagnosticar una comunicación

Para investigar una integración, separá las etapas y registrá datos que permitan seguir **una petición concreta**.

```text
Nombre resuelto -> conexión abierta -> petición enviada
-> respuesta recibida -> estado leído -> cuerpo interpretado
```

| Dato útil | Para qué sirve |
|---|---|
| Identificador de petición | Relacionar logs del cliente, intermediario y servidor. |
| Destino y operación | Saber qué integración falló. |
| Tiempo de conexión y tiempo total | Separar problemas de apertura y procesamiento. |
| Estado HTTP o tipo de excepción | Distinguir respuesta de fallo de transporte. |
| Tamaño de petición y respuesta | Detectar cargas inesperadas. |

No registres credenciales, tokens ni datos sensibles para diagnosticar. Una **traza distribuida** puede relacionar varios saltos de una operación, pero la primera habilidad sigue siendo formular la pregunta correcta: *«¿cuál fue la última etapa que sabemos que ocurrió?»*.

---

## 19. Caso completo: cliente y servidor HTTP locales en Java

**Objetivo:** iniciar un servidor local, enviarle una petición `GET`, leer su estado y su cuerpo, y cerrar el servidor. El ejemplo usa solo herramientas del JDK y la dirección de loopback. Sirve para observar el intercambio HTTP; **no prueba DNS externo ni TLS**.

### Paso 1: intercambio esperado

```text
Cliente: GET http://127.0.0.1:<puerto>/saludo
Servidor: 200, cuerpo "hola desde el servidor"
```

### Paso 2: implementación

```java
import com.sun.net.httpserver.HttpServer;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public final class EjemploRedLocal {
    public static void main(String[] args) throws Exception {
        HttpServer servidor = HttpServer.create(
                new InetSocketAddress("127.0.0.1", 0), 0);

        servidor.createContext("/saludo", intercambio -> {
            if (!"GET".equals(intercambio.getRequestMethod())) {
                intercambio.sendResponseHeaders(405, -1);
                intercambio.close();
                return;
            }

            byte[] cuerpo = "hola desde el servidor"
                    .getBytes(StandardCharsets.UTF_8);
            intercambio.getResponseHeaders().set(
                    "Content-Type", "text/plain; charset=utf-8");
            intercambio.sendResponseHeaders(200, cuerpo.length);
            try (OutputStream salida = intercambio.getResponseBody()) {
                salida.write(cuerpo);
            }
        });

        servidor.start();
        try {
            int puerto = servidor.getAddress().getPort();
            URI destino = URI.create(
                    "http://127.0.0.1:" + puerto + "/saludo");

            HttpClient cliente = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(2))
                    .build();
            HttpRequest peticion = HttpRequest.newBuilder(destino)
                    .timeout(Duration.ofSeconds(3))
                    .GET()
                    .build();

            HttpResponse<String> respuesta = cliente.send(
                    peticion, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            System.out.println(respuesta.statusCode()); // 200
            System.out.println(respuesta.body()); // hola desde el servidor
        } finally {
            servidor.stop(0);
        }
    }
}
```

El puerto `0` pide al sistema operativo elegir un puerto libre. `connectTimeout` limita el intento de conexión; el timeout de la petición limita su espera según el cliente HTTP. **Aunque el ejemplo tenga límites, una operación real necesita decidir qué hacer cuando vencen**, especialmente si pudo producir cambios remotos.

### Paso 3: preguntas para observarlo

1. ¿Qué cambia si la ruta es `/otra`? El servidor local no tiene ese contexto; se espera una respuesta de recurso ausente.
2. ¿Qué cambia si el servidor no se inicia? La conexión no se establece.
3. ¿Qué cambia si el servidor recibe `POST` en `/saludo`? El ejemplo devuelve `405`.
4. ¿Por qué `127.0.0.1` evita una consulta DNS para un nombre externo?

---

## 20. Ejercicios de razonamiento

Para cada situación, indicá **qué etapa conocemos, cuál es incierta y qué dato observarías**.

| Situación | Pregunta principal |
|---|---|
| El nombre del servicio no resuelve | ¿Se llegó a intentar la conexión? |
| La conexión fue rechazada | ¿Había un proceso escuchando en esa dirección y puerto? |
| Recibiste HTTP `404` | ¿Qué ruta y host recibió el servidor? |
| Un `POST` termina en timeout | ¿Podría el servidor haber completado la operación? |
| Recibiste HTML cuando esperabas JSON | ¿Respondió el servicio o un intermediario? |
| Una llamada tarda mucho con cuerpo pequeño | ¿Dónde se consumió el tiempo: conexión, cola o procesamiento? |
| La misma URL funciona desde una máquina y falla desde otra | ¿Qué cambia en DNS, ruta, permisos o red entre ambas? |

**Desafío:** dibujá el recorrido de una compra desde un navegador hasta un servicio de pedidos y una base de datos. Marcá qué pasos ocurren por red, qué respuestas pueden perderse y por qué un timeout del navegador no demuestra que la compra no se haya registrado.

---

## 21. Lista de verificación antes de integrar dos sistemas

- [ ] Sé quién inicia la comunicación y quién responde.
- [ ] Puedo separar nombre, dirección, puerto, ruta y recurso.
- [ ] Distingo un fallo de DNS, de conexión, de transporte y una respuesta HTTP de error.
- [ ] Conozco el contrato de datos: formato, tipos, unidades y errores.
- [ ] Definí límites de tiempo para conectar y esperar la operación.
- [ ] Sé qué significa un timeout para una operación que puede haber cambiado datos.
- [ ] Decidí si una operación puede reintentarse y cómo reconocer intentos repetidos.
- [ ] Conozco el límite de conexiones, tareas y tamaño de mensajes.
- [ ] Sé qué tramos están protegidos por TLS y qué identidad verifica el cliente.
- [ ] Puedo identificar intermediarios que podrían alterar el resultado.
- [ ] Tengo una forma de relacionar una petición con sus registros y tiempos.

**Idea central:** una llamada por red es una conversación entre procesos separados. Para diseñarla bien, distinguí **qué llegó a ocurrir, qué resultado se conoce y qué sigue siendo incierto**.
