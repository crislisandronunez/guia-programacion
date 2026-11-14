# 12 — Starters y auto-configuración

## Lectura

Secciones 18, 20 de la [guía](../../guias/Glosario-Spring-Core-Boot-Core.md).

## Consigna

Revisá mvn dependency:tree y el informe de condiciones con --debug. Elegí dos
auto-configuraciones de la base y explicá qué condiciones cumplen y qué beans aportan.

Creá una auto-configuración didáctica para un formateador de avisos predeterminado.
Ubicala en com.estudio.avisos.autoconfigure, fuera del scan de com.estudio.core.
Registrala en META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports.
Usá @AutoConfiguration y @ConditionalOnMissingBean para ceder ante un bean del usuario.
Probala con ApplicationContextRunner y AutoConfigurations.of.

## Experimentos y aceptación

- Sin bean propio se aporta el formateador; con bean propio existe solo el elegido por el usuario.
- La aplicación completa descubre la auto-configuración mediante imports, sin ampliar el scan.
- Probás una condición por propiedad y una por clase con FilteredClassLoader cuando corresponda.
- Distinguís versión gestionada, dependencia transitiva y scope.
- No usás bean overriding para simular el comportamiento de back-off.
- Explicás la diferencia entre configuración de la aplicación y auto-configuración de una biblioteca.

## Entrega

Código y pruebas del ejercicio, evidencia y respuestas en `ejercicios/respuestas/12.md`.
Agregá solicitudes a Postman cuando haya comportamiento HTTP.

## Para explicar

¿Qué faltaría separar y publicar para convertir el ejemplo en un starter reutilizable?

Siguiente: [Logging, Actuator y disponibilidad](13-observabilidad.md).

