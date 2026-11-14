# 14 — Integración y revisión final

## Lectura

Secciones 1–25, especialmente 24–25 de la [guía](../../guias/Glorasio-Spring-Security.md).

## Consigna

Completá el portal y una colección Postman por mecanismo: web con sesión/CSRF y API
con Bearer. El emisor de tokens para la demostración será una herramienta de prueba
local que vos prepares o el IdP de la extensión; nunca un endpoint público que firme
claims arbitrarias. Dejá variables vacías para claves y tokens al exportar.

Prepará un diagrama de cadenas y una tabla requisito → regla URL/método → prueba.
Ejecutá mvn verify, empaquetá y comprobá el JAR con el puerto documentado.
Incluí instrucciones para generar datos y claves ficticias y restablecer el escenario.

## Experimentos y aceptación

- Health y público funcionan; el resto respeta la matriz de acceso y el cierre por defecto.
- Credenciales, propiedad, permisos, estados y CSRF se comprueban por separado.
- Sesión y Bearer mantienen contratos distintos sin rutas fuera de toda cadena.
- No se serializan hashes ni se registran tokens.
- Tests, JAR y Postman son reproducibles; las pruebas no dependen de cuentas externas.
- Documentás pérdida de estado al reiniciar y límites de bloqueo/revocación en memoria.
- Identificás extensiones pendientes: persistencia de usuarios, MFA, ACL/PermissionEvaluator,
  token opaco/introspección, authorization server propio e integración IdP real si no la realizaste.

## Entrega

Código y pruebas cuando corresponda, solicitudes Postman y evidencia en
`ejercicios/respuestas/14.md`. En las tareas de diseño, entregá decisiones y diagramas;
no presentes una simulación como integración real.

## Para explicar

¿Qué pruebas y cambios faltarían antes de usar este diseño con datos reales y varias instancias?

Volvé al [índice](README.md) para revisar el recorrido.

