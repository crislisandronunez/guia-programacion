# Ejercicio 03: evitar operaciones duplicadas

## Objetivo y lectura

Completá la etapa 02 y leé la sección 7 de
[Mundo de pagos](../../guias/Mundo-Pagos.md). El cliente debe poder repetir una solicitud
sin provocar otro efecto económico.

## Consigna

1. Exigí `Idempotency-Key` en todos los POST de pagos. Aplicá la misma convención a los
   futuros POST que muevan dinero. Una clave ausente o vacía devuelve 400.
2. Persistí una operación con ámbito, clave, huella del request normalizado, estado,
   respuesta y fechas. Usá una restricción UNIQUE sobre ámbito y clave.
   Inicialmente el ámbito es el comercio ficticio; en la etapa 08 se deriva de la identidad.
3. La huella incluye método, ruta, recurso y datos de negocio. Normalizá montos y moneda:
   cambiar espacios o el orden de campos JSON no debe cambiar el significado.
4. Misma clave y mismos datos: reproducí el código HTTP y cuerpo guardados, sin repetir
   el efecto. Misma clave con datos distintos: 409.
5. Una operación todavía en curso devuelve 202 con su identificador y URL de consulta.
   Agregá `GET /api/operaciones/{id}`. No mantengas al segundo cliente esperando un bloqueo.
6. Propagá una referencia estable de operación al simulador. Hacé que el simulador también
   deduplique por esa referencia y rechace reutilizarla con otro contenido.

El registro se conserva durante todo el laboratorio. No agregues todavía una limpieza
automática que permita reutilizar claves antiguas accidentalmente.

## Experimentos

En Postman, enviá dos veces una creación con clave `crear-pedido-001`. Cambiá luego el
monto manteniendo la clave. Usá una clave nueva para autorizar y otra para capturar.

En una prueba de integración, lanzá 20 requests iguales al mismo tiempo usando una barrera.
Inspeccioná operaciones, pagos y el contador de efectos del simulador.

## Criterios de aceptación

| Situación | Resultado |
|---|---|
| Repetir una operación terminada | Mismo HTTP y body, un efecto |
| Misma clave, distinto monto o recurso | 409, ningún efecto adicional |
| Veinte requests concurrentes iguales | Una operación; respuestas finales o 202 |
| Repetición después de reiniciar la app | Respuesta persistida, sin nuevo cobro |
| Claves distintas intentando capturar el mismo pago | A lo sumo una captura |

La restricción UNIQUE debe resolver la carrera; una consulta previa de existencia no basta.
Ante una colisión, recuperá el registro ganador fuera de la transacción fallida.
Registrá la intención en una transacción corta antes de invocar la pasarela. No sostengas
un bloqueo de base durante la llamada externa. En la etapa 04 resolveremos caídas entre
la llamada remota y la confirmación local; no borres una operación incierta para reintentarlo.

## Para explicar

¿Por qué una clave distinta no autoriza una segunda captura del mismo pago?
¿Por qué una transacción PostgreSQL no revierte un cobro ya aceptado por otra aplicación?

Siguiente: [04 — Webhooks y timeouts](04-webhooks-y-timeouts.md).
