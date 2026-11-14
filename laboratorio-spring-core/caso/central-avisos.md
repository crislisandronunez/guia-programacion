# Caso: central de avisos del instituto Horizonte

Un instituto ficticio quiere preparar avisos de cursos y registrar envíos simulados.
Necesita cambiar configuración y canales sin reescribir el servicio principal.
El objetivo del laboratorio es entender cómo Spring crea, conecta y ejecuta sus objetos.

## Datos y reglas para implementar

- Curso JAVA-01: Introducción a Java; CORE-01: Spring Core.
- Destinatario de ejemplo: ana@example.test. Todos los datos son ficticios.
- Un aviso tiene UUID, código de curso, destinatario, texto, canal y fecha UTC (`Instant`).
- Canal admitido: CONSOLA o MEMORIA. Ninguno envía correo ni llama a proveedores.
- Código y destinatario son obligatorios; el destinatario tiene formato de email.
- Texto no vacío, de hasta 500 caracteres. Rechazá un curso inexistente.
- Crear un aviso lo deja PENDIENTE. Enviarlo lo pasa a ENVIADO; repetir el envío es conflicto.
- Un envío simulado fallido deja el aviso PENDIENTE para un reintento explícito.
- La creación y el envío son operaciones separadas. No reintentes automáticamente.
- El repositorio en memoria debe proteger también la transición de estado ante concurrencia.

En la etapa 07 crearás POST `/api/avisos`, GET `/api/avisos/{id}` y
POST `/api/avisos/{id}/envios`. Al crear: 201 y Location. Al consultar o completar un
envío síncrono: 200. Entrada inválida: 400; aviso o curso ausente: 404; ya enviado: 409;
fallo del canal simulado: 503. No marques ENVIADO si el simulador falla.

Más adelante, el trabajo asíncrono usará una operación separada para generar un reporte
de cantidades de avisos. Así no cambia silenciosamente el contrato del envío síncrono.
No se exige almacenar el texto o destinatario en logs, eventos ni etiquetas de métricas.

## Límites deliberados

Una sola instancia, almacenamiento y eventos en memoria, sin usuarios reales, login,
broker, transacciones de base de datos ni despliegue público. Reiniciar pierde los avisos.
Los perfiles dev y prod son escenarios de configuración local; prod no certifica que
esta aplicación sea apta para producción.

Las clases de este caso no están creadas: las vas incorporando con las consignas.
