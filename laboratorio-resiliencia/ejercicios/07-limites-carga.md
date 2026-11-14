# 07 — Rate limiting y rechazo temprano

## Lectura

Secciones 8 de la [guía](../../guias/Glosario-Reciliencia-Programacion.md).

## Consigna

Agregá un límite saliente para la cuota del proveedor y un límite entrante separado
para la API. Usá espera cero o pequeña y presupuestada. Definí período, capacidad,
identidad de agrupación y qué pasa en los bordes del período.

Implementá rechazo temprano de reportes cuando se agote su capacidad, sin rechazar
el catálogo. Los identificadores de cliente de esta práctica son ficticios; no
presentes un header libre como identidad autenticada.

## Experimentos y aceptación

- El límite entrante responde 429 y Retry-After útil.
- La cuota saliente rechaza con 503 según el caso y no culpa al consumidor por su cuota.
- No se reintentan automáticamente rechazos locales.
- Las llamadas rechazadas no alcanzan al simulador.
- Una prueba en el borde del período describe las ráfagas admitidas por el algoritmo.
- Dos instancias con limitadores locales no demuestran una cuota global compartida.
- Las claves del limitador tienen cardinalidad y vida acotadas.

## Entrega

Código, pruebas y evidencia en `ejercicios/respuestas/07.md`.
Agregá solicitudes Postman cuando corresponda y anotá límites de lo demostrado.

## Para explicar

¿Qué diferencia hay entre limitar operaciones por segundo y operaciones simultáneas?

Siguiente: [Fallback y orden de protecciones](08-fallback-composicion.md).

