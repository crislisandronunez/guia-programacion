# 02 — Estereotipos e inyección

## Lectura

Secciones 3–5, 16 de la [guía](../../guias/Glosario-Spring-Core-Boot-Core.md).

## Consigna

Incorporá CanalMemoria y registrá ambos canales con estereotipos. Usá inyección por
constructor y campos final. Compará selección individual con @Primary y @Qualifier,
y la inyección de List<CanalAviso> y Map<String, CanalAviso>. Registrá el orden de
ejecución con @Order sin confundirlo con orden de construcción.

Diseñá un colaborador opcional para registrar estadísticas con ObjectProvider.
Reproducí ausencia, ambigüedad y un ciclo de dependencias en contextos de prueba aislados.
Rediseñá el ciclo extrayendo una responsabilidad común.

## Experimentos y aceptación

- Sin selección explícita, dos candidatos causan el fallo esperado; evitá que el nombre del parámetro lo resuelva accidentalmente.
- @Qualifier selecciona el canal pedido aunque otro tenga @Primary.
- La colección conserva ambos candidatos: @Primary no filtra la lista.
- El servicio funciona con y sin el colaborador opcional.
- Un bean fuera del paquete escaneado queda ausente; @Import o un scan acotado lo incorpora.
- El contexto normal arranca sin habilitar referencias circulares ni overriding.

## Entrega

Código y pruebas del ejercicio, evidencia y respuestas en `ejercicios/respuestas/02.md`.
Agregá solicitudes a Postman cuando haya comportamiento HTTP.

## Para explicar

¿Por qué inyectar una interfaz no elimina por sí solo una dependencia circular?

Siguiente: [Configuración Java y creación explícita](03-configuracion-java.md).

