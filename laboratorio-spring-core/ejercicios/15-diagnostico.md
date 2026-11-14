# 15 — DevTools, empaquetado y diagnóstico

## Lectura

Secciones 16, 25–26, 28, 30 de la [guía](../../guias/Glosario-Spring-Core-Boot-Core.md).

## Consigna

Agregá DevTools como dependencia de desarrollo opcional y comprobá cuándo reinicia.
Compará un cambio de clases con uno de recursos y verificá el classpath efectivo.

Empaquetá el JAR y ejecutalo fuera de IntelliJ, con archivo externo y argumento de puerto.
Inspeccioná manifiesto y BOOT-INF. Reproducí fallos en tests aislados o ejecuciones separadas:
bean ausente, ambigüedad, ciclo, propiedad inválida, fallo de inicialización y puerto ocupado.
Para cada uno guardá síntoma, causa, evidencia y corrección mínima.

## Experimentos y aceptación

- mvn verify genera un JAR ejecutable y los recursos se leen dentro de él.
- El arranque externo usa el perfil y puerto pedidos.
- DevTools no se incluye en el JAR repaquetado por defecto; verificás el contenido.
- Las configuraciones rotas viven en tests sin contaminar el scan normal.
- Identificás una causa relevante en la cadena de excepciones, no solo el mensaje superior.
- No resolvés fallos habilitando globalmente ciclos, overriding o excluyendo configuraciones al azar.
- Distinguís JAR ejecutable, WAR, imagen de contenedor y compilación nativa.

## Entrega

Código y pruebas del ejercicio, evidencia y respuestas en `ejercicios/respuestas/15.md`.
Agregá solicitudes a Postman cuando haya comportamiento HTTP.

## Para explicar

¿Cómo detectarías que IntelliJ y Maven están usando JDK distintos?

Siguiente: [Integración y defensa del laboratorio](16-integracion.md).

