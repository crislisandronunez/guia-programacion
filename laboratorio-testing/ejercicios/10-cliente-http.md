# 10 — Proveedor simulado y límites del mock

## Lectura

Secciones 10, 16 de la [guía](../../guias/Glosario-Testing.md).

## Consigna

Implementá cotizador HTTP con timeout finito y traducción de tarifa_centavos.
Usá @RestClientTest y MockRestServiceServer para request/response cuando corresponda
al cliente elegido. Agregá también un stub HTTP real con WireMock o MockWebServer
para socket, demora y conexión; fijá una versión compatible.

Los fixtures son contrato local de práctica. Una respuesta programada en un stub
no demuestra que un proveedor externo real cumple ese contrato.

## Experimentos y aceptación

- Se verifica destino, peso, método y ruta, además de convertir centavos sin pérdida.
- 503 y demora producen indisponibilidad; JSON roto o tarifa negativa produce error de contrato.
- Un error no se convierte en envío gratis.
- Los tests de timeout atraviesan una conexión real local.
- Stub usa puerto dinámico, reinicia escenarios y cierra recursos.
- No hay llamadas a Internet para que los tests pasen.
- Una integración completa usa el adaptador real contra el stub en lugar de mockearlo.

## Entrega

Pruebas y código mínimo del comportamiento, más evidencia y respuestas en
`ejercicios/respuestas/10.md`. Indicá qué falla si rompés la regla que estás verificando.

## Para explicar

¿Cuándo es suficiente simular RestClient y cuándo necesitás simular un servidor?

Siguiente: [Pruebas de autenticación y permisos](11-seguridad.md).

