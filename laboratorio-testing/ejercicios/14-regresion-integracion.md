# 14 — Diagnóstico, regresión y recorrido final

## Lectura

Secciones 18, 20–21 de la [guía](../../guias/Glosario-Testing.md).

## Consigna

Elegí tres fallos reproducibles: fixture compartido, assertion ausente y stub incorrecto.
Guardá síntoma, causa y corrección; no mantengas tests fallando o deshabilitados como
entrega final. Agregá una prueba de regresión por defecto corregido.

Completá el flujo crear → consultar → cancelar con servidor real y PostgreSQL temporal.
El proveedor permanece como stub explícito. Prepará matriz requisito → prueba → nivel
→ dependencia real/simulada, y completá Postman para exploración manual.

## Experimentos y aceptación

- La suite pasa individualmente y en conjunto, sin depender del orden.
- Ningún test se limita a no lanzar excepción cuando debe comprobar un resultado.
- Mocks no sustituyen todas las piezas del flujo que llamás integración.
- Una prueba denegada o fallida verifica que no dejó efectos parciales.
- mvn verify ejecuta las integraciones con Docker y reporta los resultados reales.
- Documentás lo no verificado: proveedor real, navegador, carga y entrega durable.
- El JAR y las instrucciones manuales funcionan con el perfil y dependencias documentados.
- Cada requisito importante tiene evidencia, sin convertir cada línea privada en un test.

## Entrega

Pruebas y código mínimo del comportamiento, más evidencia y respuestas en
`ejercicios/respuestas/14.md`. Indicá qué falla si rompés la regla que estás verificando.

## Para explicar

¿Qué cambio de implementación debería poder hacerse sin modificar tus pruebas de comportamiento?

Volvé al [índice](README.md) para revisar cobertura.

