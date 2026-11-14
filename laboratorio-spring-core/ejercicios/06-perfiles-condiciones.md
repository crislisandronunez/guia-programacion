# 06 — Perfiles y condiciones

## Lectura

Secciones 9, 20–22 de la [guía](../../guias/Glosario-Spring-Core-Boot-Core.md).

## Consigna

Creá application-dev.yml y application-prod.yml con configuración local distinta.
En dev registrá un cargador de catálogo ficticio; en prod exigí el catálogo mediante
configuración explícita. Permití activar una auditoría simulada con
lab.avisos.auditoria-habilitada y @ConditionalOnProperty.

Armá una matriz de perfil, propiedad ausente, true y false. Compará una condición
propia con Condition y condiciones Boot. Usá ApplicationContextRunner para escenarios
pequeños y pruebas de aplicación para los perfiles completos.

## Experimentos y aceptación

- El perfil se activa desde fuera del archivo específico del propio perfil.
- La matriz deja claro el comportamiento de matchIfMissing.
- Desactivar auditoría no deja dependencias obligatorias sin resolver.
- Perfiles combinados no registran accidentalmente dos beans incompatibles.
- Un grupo de perfiles activa sus integrantes y lo comprobás.
- Los datos demo se cargan únicamente cuando corresponde; prod local no implica infraestructura real.

## Entrega

Código y pruebas del ejercicio, evidencia y respuestas en `ejercicios/respuestas/06.md`.
Agregá solicitudes a Postman cuando haya comportamiento HTTP.

## Para explicar

¿Cuándo necesitás un perfil completo y cuándo alcanza una propiedad de funcionalidad?

Siguiente: [API mínima, recursos, conversión e idiomas](07-validacion-recursos.md).

