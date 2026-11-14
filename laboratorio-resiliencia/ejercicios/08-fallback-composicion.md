# 08 — Fallback y orden de protecciones

## Lectura

Secciones 9–11 de la [guía](../../guias/Glosario-Reciliencia-Programacion.md).

## Consigna

Implementá fallback de cotización con último valor exitoso de hasta 60 segundos,
origen y marca degradado. Inyectá Clock para medir antigüedad. Sin valor válido,
propagá el error contractual; nunca uses el fallback de precio para confirmar stock.

Compará Retry(CircuitBreaker(llamada)) y CircuitBreaker(Retry(llamada)) mediante
decoradores explícitos y contadores. Después contrastá con el orden efectivo de los
aspectos de la versión fijada. Ubicá deliberadamente el fallback y medí qué errores
ven las capas externas.

## Experimentos y aceptación

- Precio fresco devuelve origen PROVEEDOR y degradado=false.
- Fallback válido indica CACHE, fecha original y degradado=true.
- Caché vacía o vencida no devuelve cero ni un éxito engañoso.
- Error permanente no se transforma en precio anterior por un catch(Throwable) general.
- El número de intentos y muestras del circuito coincide con el orden documentado.
- Demostrás cómo un fallback interno puede ocultar un fallo al retry o al circuito.
- CallNotPermittedException y rechazos de bulkhead/rate limit tienen políticas explícitas.

## Entrega

Código, pruebas y evidencia en `ejercicios/respuestas/08.md`.
Agregá solicitudes Postman cuando corresponda y anotá límites de lo demostrado.

## Para explicar

¿Por qué mover el fallback puede cambiar las métricas aunque el cliente reciba el mismo resultado?

Siguiente: [Configuración y observabilidad](09-metricas.md).

