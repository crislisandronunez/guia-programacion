# 05 — Captura, interacciones y pruebas de capas

## Lectura

Secciones 7–8 de la [guía](../../guias/Glosario-Testing.md).

## Consigna

Usá ArgumentCaptor para examinar el pedido enviado a guardar: dueño, líneas,
importes, fecha y estado. Verificá ausencia de escritura cuando falla la validación.
Probá mappers con objetos reales y campos completos.

Elegí interacciones relevantes para el contrato, sin verificar cada getter.
Si el fake en memoria se usa en varios tests, agregá pruebas de su comportamiento
mínimo para no confiar en una simulación que no respeta el puerto.

## Experimentos y aceptación

- La captura comprueba datos de negocio y no solo que save fue llamado.
- Entrada inválida no consulta innecesariamente al proveedor ni guarda.
- No devolvés un resultado exitoso si guardar falla.
- El mapper conserva valores y no expone campos internos.
- verifyNoMoreInteractions e InOrder se usan solo si el orden/cantidad es requisito.
- Refactorizar un helper interno no rompe pruebas cuyo comportamiento sigue igual.

## Entrega

Pruebas y código mínimo del comportamiento, más evidencia y respuestas en
`ejercicios/respuestas/05.md`. Indicá qué falla si rompés la regla que estás verificando.

## Para explicar

¿Qué error real podría detectar una verificación de interacción que una aserción del resultado no detecta?

Siguiente: [Contexto, JSON y configuración](06-contexto-slices.md).

