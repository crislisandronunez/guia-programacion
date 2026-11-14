# 15 — Trie, checksums e integridad

## Lectura

Secciones 15 de la [guía](../../guias/Glosario-Tipos-de-Algoritmos-Programadores.md).

## Consigna

Implementá Trie para prefijos con normalización explícita (Unicode NFC y minúsculas
con Locale.ROOT), orden y máximo de resultados. Implementá Luhn sobre identificadores
ficticios de dígitos ASCII; validá formato por separado y no uses datos reales.

Usá javax.crypto.Mac para HMAC-SHA256 con clave efímera de prueba: firmá los bytes
exactos y compará MAC de longitud válida mediante MessageDigest.isEqual.
Compará mediante bibliotecas el propósito de hash de contraseña y cifrado autenticado;
BCrypt/Argon2 y AES-GCM son exploraciones, no primitivas para escribir a mano.

## Experimentos y aceptación

- Trie distingue palabra completa de prefijo y no duplica términos normalizados.
- El costo incluye enumerar resultados, no solo recorrer el prefijo.
- Luhn prueba vacío, formato inválido y vectores ficticios válidos/alterados.
- Pasar Luhn no demuestra identidad, existencia de cuenta ni ausencia de fraude.
- Cambiar un byte o la clave invalida el HMAC; volver a serializar JSON puede cambiar los bytes.
- HMAC solo no evita replay: documentás la necesidad de contexto temporal/nonce si se usara en un protocolo.
- No extrapolás módulo 11 a todos los documentos; cada formato exige especificación propia.

## Entrega

Implementación, pruebas, análisis de tiempo/memoria y respuestas en
`ejercicios/respuestas/15.md`. Incluí precondiciones, casos límite e invariantes.

## Para explicar

¿Qué diferencias hay entre checksum, hash, MAC y cifrado reversible?

Siguiente: [Integración y elección fundamentada](16-integracion.md).

