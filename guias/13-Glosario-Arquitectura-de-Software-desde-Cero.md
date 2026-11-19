# Glosario y Guía de Arquitectura de Software desde Cero

Esta guía explica **cómo organizar un sistema para que sus reglas, entradas, salidas y dependencias sean comprensibles**. Comienza por responsabilidades y límites; después muestra cómo llevar esas decisiones a **Java**. El objetivo es aprender a razonar sobre la estructura del software antes de elegir un framework, una base de datos o una forma de despliegue.

---

## Índice

1. [Qué es arquitectura de software](#1-qué-es-arquitectura-de-software)
2. [Glosario fundamental](#2-glosario-fundamental)
3. [Empezar por objetivos y cambios esperados](#3-empezar-por-objetivos-y-cambios-esperados)
4. [Responsabilidades, cohesión y acoplamiento](#4-responsabilidades-cohesión-y-acoplamiento)
5. [Límites: decidir qué pertenece a cada parte](#5-límites-decidir-qué-pertenece-a-cada-parte)
6. [Reglas del dominio, casos de uso y detalles externos](#6-reglas-del-dominio-casos-de-uso-y-detalles-externos)
7. [Flujo de ejecución y dirección de dependencias](#7-flujo-de-ejecución-y-dirección-de-dependencias)
8. [Contratos entre partes](#8-contratos-entre-partes)
9. [Entradas, salidas y adaptadores](#9-entradas-salidas-y-adaptadores)
10. [Construcción del sistema e inyección de dependencias](#10-construcción-del-sistema-e-inyección-de-dependencias)
11. [Validaciones y errores en el límite adecuado](#11-validaciones-y-errores-en-el-límite-adecuado)
12. [Estado, transacciones y operaciones completas](#12-estado-transacciones-y-operaciones-completas)
13. [Módulos y propiedad de los datos](#13-módulos-y-propiedad-de-los-datos)
14. [Una aplicación o varias: elegir según el problema](#14-una-aplicación-o-varias-elegir-según-el-problema)
15. [Probar una estructura y permitir su evolución](#15-probar-una-estructura-y-permitir-su-evolución)
16. [Registrar decisiones y evaluar compromisos](#16-registrar-decisiones-y-evaluar-compromisos)
17. [Caso completo: cotizar el envío de un paquete](#17-caso-completo-cotizar-el-envío-de-un-paquete)
18. [Errores comunes de arquitectura](#18-errores-comunes-de-arquitectura)
19. [Ejercicios de diseño](#19-ejercicios-de-diseño)
20. [Lista de verificación para explicar una arquitectura](#20-lista-de-verificación-para-explicar-una-arquitectura)

---

## 1. Qué es arquitectura de software

La **arquitectura** es el conjunto de decisiones importantes sobre las partes de un sistema, las responsabilidades de cada una y la manera en que colaboran. Una arquitectura sirve si ayuda a cumplir requisitos reales y permite cambiar el sistema sin perder el control.

```text
Una persona pide una cotización de envío.

Hay que decidir:
- ¿Quién recibe y valida la solicitud?
- ¿Dónde está la regla para calcular el precio?
- ¿De dónde viene la tarifa de cada zona?
- ¿Qué ocurre si la zona no existe?
- ¿Qué partes cambian si mañana las tarifas vienen de otro sistema?
```

El diagrama de carpetas es una **consecuencia** de estas decisiones. Poner clases en directorios llamados `controller`, `service` y `repository` no garantiza que las responsabilidades estén bien repartidas.

**Pregunta guía:** si cambia una regla del negocio o un detalle técnico, ¿qué partes deberían cambiar y cuáles no tienen por qué conocer ese cambio?

---

## 2. Glosario fundamental

| Término | Significado | Ejemplo |
|---|---|---|
| **Responsabilidad** | Razón concreta por la que una parte existe. | Calcular el costo de envío. |
| **Módulo** | Conjunto de código con un propósito y una interfaz reconocible. | Módulo de cotizaciones. |
| **Límite (*boundary*)** | Separación que define qué información cruza entre partes. | La aplicación pide una tarifa sin conocer dónde se guarda. |
| **Cohesión** | Grado en que el contenido de una parte responde al mismo propósito. | Un calculador que solo aplica reglas de precio. |
| **Acoplamiento** | Conocimiento o dependencia de una parte respecto de otra. | Un cálculo que importa directamente un cliente HTTP. |
| **Dependencia** | Relación en la que una parte necesita otra para compilar o funcionar. | `CotizarEnvio` necesita un `Tarifario`. |
| **Dominio** | Conceptos y reglas propios del problema. | Cómo se calcula un envío según peso y tarifa. |
| **Caso de uso** | Acción completa que ofrece el sistema a un actor. | Cotizar un paquete para una zona. |
| **Infraestructura** | Mecanismos para comunicarse, guardar, leer o ejecutar. | Archivo de tarifas, HTTP, base de datos. |
| **Contrato** | Promesa sobre entradas, salidas, reglas y errores entre partes. | `Tarifario` devuelve el precio por kilo de una zona. |
| **Adaptador** | Código que conecta una tecnología o forma de entrada con un contrato interno. | Adaptador que lee tarifas de un archivo. |
| **Puerto** | Contrato que expresa cómo una parte usa o ofrece una capacidad. | Interfaz para consultar tarifas. |
| **Composición** | Construcción de una parte con sus colaboradores. | Crear un caso de uso con un tarifario concreto. |
| **Transacción** | Límite de una operación cuyos cambios deben coordinarse. | Confirmar un pedido y descontar stock de forma coherente. |
| **Compromiso (*trade-off*)** | Ventaja obtenida a cambio de un costo o limitación. | Separar servicios permite despliegues propios, pero añade comunicación por red. |

Los nombres cambian entre estilos arquitectónicos. **La pregunta estable es quién sabe qué y quién protege cada regla**.

---

## 3. Empezar por objetivos y cambios esperados

Antes de dibujar capas, distinguí **lo que el sistema debe hacer** de **cómo debe comportarse**.

| Tipo de necesidad | Ejemplo | Efecto en el diseño |
|---|---|---|
| Funcional | Cotizar un envío por peso y zona. | Necesita una regla y una fuente de tarifas. |
| Calidad | Responder en un tiempo aceptable. | Puede influir en acceso a tarifas y medición. |
| Integridad | Nunca devolver un precio negativo. | Requiere validación del dato y del cálculo. |
| Cambio esperado | Poder cargar nuevas tarifas. | Conviene separar tarifa de fórmula. |

No diseñes para toda posibilidad imaginable. Pedí ejemplos: *«Si cambia la tarifa, ¿cambia también la fórmula? ¿La tarifa depende de la zona, del cliente o de ambos?»* Esas respuestas muestran qué cosas conviene separar.

**Regla práctica:** una separación se justifica cuando aclara responsabilidades, protege una regla o permite un cambio probable. No por sumar más clases.

---

## 4. Responsabilidades, cohesión y acoplamiento

Una parte tiene **alta cohesión** cuando sus decisiones están relacionadas. El **acoplamiento** aumenta cuando necesita conocer los detalles internos de otras partes.

```text
Clase CotizadorGigante:
  parsea JSON, valida peso, consulta una web, calcula precio,
  guarda cotización y formatea la respuesta.

Cambios que la afectan: formato JSON, regla de precio,
proveedor externo, almacenamiento y respuesta al usuario.
```

Separar responsabilidades permite identificar **por qué cambiaría** cada parte:

| Parte | Motivo principal de cambio |
|---|---|
| Entrada | Cambia la forma en que llega una solicitud. |
| Regla de cálculo | Cambia la política de precio. |
| Acceso a tarifas | Cambia dónde o cómo se obtiene una tarifa. |
| Salida | Cambia la presentación de la cotización. |

Separar no significa que cada línea tenga su propia clase. Una función pequeña y clara puede ser suficiente. Tampoco significa perseguir «cero acoplamiento»: las partes **tienen que colaborar**. El objetivo es que dependan de lo que necesitan y conozcan lo menos posible de detalles ajenos.

---

## 5. Límites: decidir qué pertenece a cada parte

Un límite organiza decisiones que cambian por motivos distintos. Puede ser entre métodos, clases, paquetes, módulos o procesos. Cuanto más costoso sea cruzarlo, más claro debe ser el contrato.

```text
Entrada del usuario | Caso de uso | Regla de cálculo | Fuente de tarifas
        datos        |  coordina  |     calcula     |    proporciona
```

**Preguntas para ubicar una responsabilidad:**

1. ¿Depende de la manera en que llegó la solicitud?
2. ¿Es una regla que seguiría existiendo si cambiara la interfaz de usuario?
3. ¿Depende de una base de datos, archivo, red o biblioteca externa?
4. ¿Quién debe garantizar que la operación completa tenga sentido?

Si una regla de precio se ejecutaría igual desde una web, una consola o un proceso por lotes, no debería vivir escondida dentro del controlador web. Si una clase solo sabe leer un archivo, no debería decidir el descuento del cliente.

---

## 6. Reglas del dominio, casos de uso y detalles externos

Tres responsabilidades comunes ayudan a ordenar el pensamiento:

| Parte | Pregunta | Ejemplo |
|---|---|---|
| **Dominio** | ¿Qué reglas hacen válido el resultado? | Un peso positivo y un precio por kilo no negativo producen un costo. |
| **Aplicación / caso de uso** | ¿Qué pasos cumplen la solicitud completa? | Recibir solicitud, obtener tarifa, calcular y devolver cotización. |
| **Infraestructura** | ¿Cómo se obtiene o entrega información fuera del núcleo? | Leer tarifas de memoria, archivo o servicio remoto. |

```text
Caso de uso Cotizar:
1. Validar que la solicitud contiene datos utilizables.
2. Obtener la tarifa de la zona.
3. Aplicar la regla de cálculo.
4. Devolver una cotización.
```

Este reparto es una **herramienta de diseño**, no una obligación de crear tres proyectos. En una aplicación pequeña, las tres partes pueden vivir en un solo programa y seguir teniendo responsabilidades claras.

---

## 7. Flujo de ejecución y dirección de dependencias

El **flujo de ejecución** muestra qué se llama primero. La **dirección de dependencia de código** muestra qué nombres o tipos conoce cada parte. No siempre son la misma flecha.

```text
Flujo cuando llega una solicitud:
Entrada -> Caso de uso -> Tarifario concreto -> Fuente de datos
                     \-> Regla de cálculo

Dependencias del código:
Entrada -> Caso de uso -> Contrato Tarifario
Infraestructura -------> Contrato Tarifario
Caso de uso -----------> Regla de cálculo
```

El caso de uso puede **ejecutar** un adaptador de infraestructura sin importar su clase concreta: conoce el contrato `Tarifario`, y el objeto real se conecta al construir el sistema. Así, cambiar de un archivo a una base de datos no exige reescribir la fórmula.

Esto no convierte cada dependencia en una interfaz. Una regla pura y estable puede usarse directamente. La separación mediante contrato aporta valor cuando existe una **frontera que conviene aislar** o varios comportamientos legítimos.

---

## 8. Contratos entre partes

Un contrato incluye más que el nombre de un método. Debe aclarar qué valores acepta, qué garantiza y cómo expresa que algo falló.

```java
interface Tarifario {
    // Para una zona conocida devuelve centavos por kilo, nunca negativos.
    // Si la zona no existe, lanza IllegalArgumentException.
    long precioPorKiloCentavos(String zona);
}
```

| Elemento | Pregunta |
|---|---|
| Entrada | ¿Se admite `null`, vacío o peso cero? |
| Salida | ¿En qué unidad se devuelve el precio? |
| Error | ¿Qué ocurre si la zona no existe? |
| Efecto | ¿La consulta modifica algo o solo lee? |

Un contrato claro permite cambiar la implementación sin cambiar el significado que espera el cliente. Si un adaptador devuelve `-1` para «zona desconocida» y otro lanza una excepción, **no respetan el mismo contrato** salvo que esa diferencia esté prevista.

La guía de interfaces y contratos profundiza en su ubicación por capas. Aquí importa la idea básica: **las partes deben acordar significado, no solo tipos que compilan**.

---

## 9. Entradas, salidas y adaptadores

Una entrada puede ser un método llamado desde consola, HTTP o una tarea programada. Un adaptador de entrada convierte esa forma externa en una solicitud que entiende el caso de uso. Un adaptador de salida cumple una necesidad del caso de uso mediante una tecnología concreta.

```text
Consola -------┐
Web -----------+--> CotizarEnvio --> Tarifario (contrato)
Proceso lote --┘                         |
                                         +--> TarifarioArchivo
                                         +--> TarifarioServicioRemoto
```

No hace falta implementar todas estas opciones desde el primer día. El dibujo muestra que **la regla de cálculo puede mantenerse estable** mientras cambian las formas de entrada o la fuente de tarifas.

Evitá que un objeto del dominio reciba tipos específicos de HTTP o de una biblioteca de persistencia cuando su regla solo necesita `zona`, `peso` y `tarifa`. Traducí esos datos en el borde correspondiente.

---

## 10. Construcción del sistema e inyección de dependencias

Alguien debe elegir **qué implementación concreta** usar y construir los objetos. Ese lugar se suele llamar *raíz de composición*.

```java
Tarifario tarifario = new TarifarioMemoria();
CotizarEnvio cotizar = new CotizarEnvio(tarifario);
```

El caso de uso recibe su colaborador por constructor. No necesita ejecutar `new TarifarioMemoria()` dentro de su lógica. Así, una aplicación puede conectar otra fuente de tarifas sin cambiar el caso de uso.

La **inyección de dependencias** es esta idea de proporcionar colaboradores desde afuera. Spring puede automatizar el armado, pero el concepto funciona en Java puro. Un contenedor no corrige responsabilidades mal distribuidas; solo construye la estructura que le indicamos.

---

## 11. Validaciones y errores en el límite adecuado

No todas las validaciones contestan la misma pregunta.

| Lugar | Qué verifica | Ejemplo |
|---|---|---|
| Adaptador de entrada | Que se pueda interpretar la solicitud externa. | El texto `"2500"` se convierte a un número. |
| Caso de uso | Que la solicitud de la acción esté completa. | Hay zona y peso. |
| Dominio | Que sus reglas nunca produzcan un estado inválido. | Peso positivo, tarifa no negativa. |
| Adaptador de salida | Que los datos obtenidos cumplan el contrato interno. | La fuente no devuelve una tarifa negativa. |

Puede haber comprobaciones en más de un lugar con **propósitos distintos**. La entrada puede rechazar texto ilegible y el dominio seguir protegiendo su invariante cuando se lo llama desde otra entrada.

Definí errores comprensibles. «Zona inexistente» es diferente de «archivo de tarifas no disponible». Si se convierten ambos en `0`, el sistema podría entregar una cotización aparentemente válida cuando en realidad faltan datos.

---

## 12. Estado, transacciones y operaciones completas

Un caso de uso que **solo lee y calcula** puede devolver un resultado sin cambiar datos. Uno que escribe necesita pensar qué conjunto de cambios constituye una operación completa.

```text
Confirmar pedido:
1. Verificar que el pedido admite confirmación.
2. Reservar existencias.
3. Registrar el nuevo estado.

Pregunta: si falla el paso 3, ¿debe mantenerse la reserva del paso 2?
```

El límite de **transacción** se elige para proteger las reglas de esa operación. Una anotación o una llamada técnica no decide por sí sola qué cambios deben ir juntos; eso surge del significado del caso de uso.

Si los cambios ocurren en **sistemas distintos**, una transacción local puede no abarcar ambos. Esa situación necesita un diseño específico de consistencia y fallos, tratado con más detalle en las guías de sistemas distribuidos y mensajería.

---

## 13. Módulos y propiedad de los datos

Un módulo debería exponer operaciones comprensibles y conservar autoridad sobre sus reglas. Otro módulo no debería modificar sus datos internos por atajos que eluden esas operaciones.

```text
Módulo Catálogo: describe productos y sus datos comerciales.
Módulo Pedidos: conserva qué se compró y a qué precio se acordó.

El precio actual del catálogo puede cambiar.
El precio histórico de un pedido no debería reescribirse por ese cambio.
```

La **propiedad de los datos** responde quién puede decidir su significado y sus cambios. Compartir una tabla, objeto mutable o archivo entre partes sin definir autoridad suele producir reglas contradictorias.

Los límites de módulos pueden seguir una capacidad del negocio, como pedidos o inventario, en vez de crear un módulo global para «todos los controladores» y otro para «todos los repositorios». Dentro de cada capacidad pueden existir sus propias entradas, reglas y salidas.

---

## 14. Una aplicación o varias: elegir según el problema

La separación **lógica** de módulos no exige procesos independientes. Un sistema puede empezar como una sola aplicación con límites internos claros.

| Opción | Ventaja | Costo |
|---|---|---|
| Una aplicación con módulos | Llamadas locales, despliegue y depuración más simples. | Los módulos comparten ciclo de despliegue. |
| Varias aplicaciones comunicadas por red | Equipos y despliegues pueden tener mayor independencia. | Fallos de red, coordinación de datos y operación más complejos. |

La decisión depende de necesidades concretas: equipos, ritmo de cambio, aislamiento, escala y tolerancia a fallos. **No hay una cantidad universal de servicios correcta**. Dividir un código confuso en procesos separados suele conservar la confusión y añadir problemas de comunicación.

Primero identificá límites coherentes; después decidí dónde se ejecuta cada parte.

---

## 15. Probar una estructura y permitir su evolución

Una arquitectura es más fácil de verificar cuando las reglas pueden ejecutarse sin iniciar todas las tecnologías externas.

```text
Regla de cálculo: probar con peso y tarifa concretos.
Caso de uso: conectar un tarifario controlado y comprobar el resultado.
Adaptador: comprobar que lee y traduce tarifas correctamente.
Sistema completo: comprobar que todas las piezas colaboran.
```

Estas pruebas responden preguntas distintas. Un test rápido de la fórmula no demuestra que se está leyendo bien un archivo. Una prueba del sistema completo puede detectar el fallo, pero quizá cueste localizarlo. La guía de testing desarrolla herramientas y estrategias; aquí importa que **los límites hagan visible qué se está comprobando**.

Una estructura también debe permitir cambios graduales. Si una regla se mueve de lugar, preservá su contrato y verificá a sus usuarios. Cambiar todas las partes simultáneamente dificulta saber qué rompió un comportamiento.

---

## 16. Registrar decisiones y evaluar compromisos

Cada decisión importante tiene **contexto, alternativa, beneficio y costo**. Escribirlos brevemente ayuda a entenderla meses después.

```text
Decisión: mantener el tarifario detrás de un contrato.
Contexto: las tarifas hoy son locales, pero su fuente podría cambiar.
Beneficio: el cálculo no conoce el formato ni la ubicación de los datos.
Costo: un contrato y un adaptador adicionales.
Condición para revisar: si nunca aparece otra fuente y la separación complica el uso.
```

No hace falta documentar cada `if`. Sí conviene registrar decisiones que afectan varios módulos o que serían costosas de revertir. **Elegí la complejidad que resuelve una necesidad identificada**; cada capa, cola o servicio adicional también debe operarse y comprenderse.

---

## 17. Caso completo: cotizar el envío de un paquete

**Regla:** para una zona conocida, se obtiene un precio en centavos por cada kilo iniciado. El peso debe ser positivo. Un paquete de `1.001` gramos ocupa dos unidades de cobro. Una tarifa negativa es inválida.

### Paso 1: mapa de responsabilidades

| Parte | Responsabilidad |
|---|---|
| `Solicitud` | Llevar zona y peso con datos mínimos válidos. |
| `CalculoEnvio` | Aplicar la regla de kilos iniciados y tarifa. |
| `CotizarEnvio` | Coordinar consulta de tarifa y cálculo. |
| `Tarifario` | Expresar la necesidad de obtener tarifa por zona. |
| `TarifarioMemoria` | Proporcionar tarifas concretas para este ejemplo. |
| `main` | Elegir implementaciones y mostrar el resultado. |

### Paso 2: implementación en Java puro

El ejemplo se presenta en **un solo archivo** para poder leerlo de principio a fin. En un proyecto mayor, estas partes pueden separarse por módulos o paquetes según sus responsabilidades.

```java
import java.util.Map;
import java.util.Objects;

public final class EjemploCotizacion {
    record Solicitud(String zona, int pesoGramos) {
        Solicitud {
            if (zona == null || zona.isBlank() || pesoGramos <= 0) {
                throw new IllegalArgumentException("Zona y peso deben ser válidos");
            }
        }
    }

    record Cotizacion(String zona, long precioCentavos) {}

    interface Tarifario {
        // Devuelve centavos por kilo para una zona conocida; nunca negativos.
        // Lanza IllegalArgumentException si la zona es desconocida.
        long precioPorKiloCentavos(String zona);
    }

    static final class CalculoEnvio {
        static long calcular(int pesoGramos, long tarifaCentavos) {
            if (pesoGramos <= 0 || tarifaCentavos < 0) {
                throw new IllegalArgumentException("Peso o tarifa inválidos");
            }
            long kilosIniciados = (pesoGramos + 999L) / 1_000L;
            return Math.multiplyExact(kilosIniciados, tarifaCentavos);
        }
    }

    static final class CotizarEnvio {
        private final Tarifario tarifario;

        CotizarEnvio(Tarifario tarifario) {
            this.tarifario = Objects.requireNonNull(tarifario);
        }

        Cotizacion ejecutar(Solicitud solicitud) {
            Objects.requireNonNull(solicitud);
            long tarifa = tarifario.precioPorKiloCentavos(solicitud.zona());
            long precio = CalculoEnvio.calcular(solicitud.pesoGramos(), tarifa);
            return new Cotizacion(solicitud.zona(), precio);
        }
    }

    static final class TarifarioMemoria implements Tarifario {
        private final Map<String, Long> tarifas = Map.of(
                "CENTRO", 1_500L,
                "INTERIOR", 2_300L
        );

        @Override
        public long precioPorKiloCentavos(String zona) {
            Long precio = tarifas.get(zona);
            if (precio == null) {
                throw new IllegalArgumentException("Zona desconocida: " + zona);
            }
            return precio;
        }
    }

    public static void main(String[] args) {
        Tarifario tarifario = new TarifarioMemoria();
        CotizarEnvio cotizar = new CotizarEnvio(tarifario);
        Cotizacion resultado = cotizar.ejecutar(new Solicitud("CENTRO", 1_001));
        System.out.println(resultado.precioCentavos()); // 3000
    }
}
```

### Paso 3: comprobar el comportamiento y el diseño

| Entrada | Resultado esperado | Regla observada |
|---|---:|---|
| `CENTRO`, `1.000 g` | `1.500` centavos | Un kilo iniciado. |
| `CENTRO`, `1.001 g` | `3.000` centavos | Dos kilos iniciados. |
| `INTERIOR`, `2.000 g` | `4.600` centavos | Tarifa distinta, misma fórmula. |
| Zona inexistente | Error explícito | No se inventa una tarifa. |
| Peso `0` | Error explícito | El dominio no acepta un paquete sin peso. |

Si las tarifas pasaran a un archivo, reemplazarías `TarifarioMemoria` por otro adaptador que cumpla el mismo contrato. **La fórmula y el caso de uso seguirían expresando la misma regla**. Si cambiara la política de kilos iniciados, el cambio correspondería a `CalculoEnvio`.

---

## 18. Errores comunes de arquitectura

| Decisión que merece revisión | Consecuencia | Pregunta para mejorarla |
|---|---|---|
| Poner reglas del negocio en la entrada HTTP | Otra entrada debe duplicarlas. | ¿La regla existe aunque cambie el canal? |
| Poner todas las decisiones en un `Service` gigante | Cada cambio toca la misma clase. | ¿Qué responsabilidades cambian por motivos distintos? |
| Crear una interfaz para cada clase | Más archivos sin un límite claro. | ¿Qué contrato o variación real representa? |
| Devolver tipos técnicos desde el dominio | La regla queda atada a una herramienta. | ¿Qué dato del problema necesita realmente? |
| Ocultar todos los errores como `null` o `0` | Un fallo parece un resultado válido. | ¿Cómo distinguir ausencia, invalidez y fallo externo? |
| Compartir datos internos entre módulos | Se saltan reglas del dueño de los datos. | ¿Quién autoriza el cambio? |
| Separar procesos antes de entender módulos | Se agregan fallos de red y coordinación. | ¿Cuál es el límite de negocio y por qué necesita otro despliegue? |
| Crear capas que solo reenvían llamadas | Aumenta recorrido sin aclarar responsabilidades. | ¿Qué regla, traducción o límite protege esta capa? |

---

## 19. Ejercicios de diseño

Para cada caso, dibujá **entrada → caso de uso → reglas → salidas**, anotá quién conoce a quién y definí dos errores posibles. Recién después proponé clases Java.

| Caso | Pregunta de arquitectura |
|---|---|
| **Reservar un turno** | ¿Quién impide dos reservas del mismo horario? |
| **Calcular descuento** | ¿Dónde vive la regla y de dónde vienen los datos del cliente? |
| **Importar un archivo** | ¿Quién interpreta el formato y quién decide si cada registro es válido? |
| **Enviar avisos** | ¿Qué parte decide que debe enviarse y cuál conoce el canal concreto? |
| **Consultar stock** | ¿Quién es dueño del dato y qué ocurre si la fuente no responde? |

**Desafío:** en el caso de cotización, imaginá que una entrada HTTP y una consola usan el mismo cálculo. Dibujá qué partes serían compartidas y cuáles específicas de cada entrada. Después imaginá que la tarifa pasa a otro sistema: indicá qué contrato permanece y qué adaptador cambia.

---

## 20. Lista de verificación para explicar una arquitectura

- [ ] Puedo describir qué problema y qué requisitos busca resolver.
- [ ] Cada parte tiene una responsabilidad que puedo explicar en una frase.
- [ ] Sé qué reglas pertenecen al dominio y cuáles son detalles de entrada o salida.
- [ ] Puedo seguir una solicitud completa desde su entrada hasta la respuesta.
- [ ] Puedo dibujar las dependencias del código y justificar cada frontera.
- [ ] Los contratos aclaran entradas, salidas, unidades, errores y efectos.
- [ ] Sé quién construye y conecta las implementaciones concretas.
- [ ] Identifiqué quién protege los cambios de estado y sus transacciones.
- [ ] Los módulos tienen una autoridad clara sobre sus datos.
- [ ] Elegí la cantidad de partes y procesos por necesidades reales.
- [ ] Puedo decir qué cambiaría ante una nueva regla o una nueva tecnología.
- [ ] Registré los costos de las decisiones importantes.

**Idea central:** una arquitectura ayuda cuando permite responder con claridad **dónde vive una regla, quién puede cambiar un dato y qué parte se verá afectada por un cambio**. Las capas y herramientas son medios para sostener esas respuestas.
