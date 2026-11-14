# 09 — Seguridad, errores y pruebas

## Objetivo
Leé las secciones 16–19 de la [guía](../../guias/Glosario-Spring-Redis.md).

## Consigna
Agregá un perfil Docker con ACL, un usuario de aplicación y otro de administración.
Guardá credenciales fuera de Git. Restringí claves a los namespaces necesarios y canales
a los del laboratorio; habilitá los comandos requeridos por templates, sesiones,
repositorios y scripts. No otorgues CONFIG o FLUSHALL a la aplicación.

Prepará escenarios controlados: WRONGTYPE, WRONGPASS, NOPERM, JSON incompatible y Redis
apagado. Asociá cada error con diagnóstico y respuesta HTTP; no expongas contraseñas,
cookies, códigos de un solo uso ni trazas internas.

Exponé métricas solo en configuración local o protegida. Medí tiempo de acceso a Redis,
errores y hits/misses de caché; no supongas que todas las métricas aparecen automáticamente
sin habilitar estadísticas o instrumentación.

## Experimentos y aceptación
- El usuario de aplicación accede a claves y canales autorizados, y falla fuera de ellos.
- Las pruebas cubren TTL real, serialización, concurrencia del limitador, liberación
  por token y recuperación de eventos. Los mocks solos no verifican estas garantías.
- Testcontainers usa una instancia y claves aisladas; ninguna prueba borra datos del Compose.
- Health identifica Redis caído; las lecturas de catálogo degradan según su contrato,
  mientras que sesiones y límites responden con fallos controlados.
- Definí timeouts finitos y observá recuperación al reiniciar Redis.
- Documentá requisitos de TLS y autenticación HTTP antes de exposición externa.
- RedisInsight, si lo usás, queda local; no es necesario para aprobar el ejercicio.

## Para explicar
¿Por qué MONITOR puede revelar datos sensibles y alterar mediciones? ¿Qué falta para
recuperarse de una pérdida total del volumen? Registrá limitaciones en tu respuesta.

Siguiente: [10](10-distribucion.md).
