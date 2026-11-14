# Caso: portal de solicitudes de Horizonte

El instituto ficticio Horizonte permite a sus alumnos solicitar certificados.
Cada alumno consulta y cancela únicamente sus solicitudes. Una administradora
consulta todas y las aprueba. Todos los datos son de práctica.

## Identidades a implementar

| Identidad estable | Usuario | Autoridades |
|---|---|---|
| ana | ana | ROLE_ALUMNO, SOLICITUD_CREAR |
| bruno | bruno | ROLE_ALUMNO, SOLICITUD_CREAR |
| admin | admin | ROLE_ADMIN, SOLICITUD_APROBAR |

No hay jerarquía de roles implícita: ADMIN no hereda ALUMNO.
Usá estas identidades como sub al generar JWT de prueba; mapear identidades externas
será una decisión explícita en la etapa OIDC. Contraseñas locales elegidas al resolver
el ejercicio, almacenadas como hash. No se aceptan roles enviados por el cliente.

## Dominio a construir

Solicitud: UUID, propietario, tipo (ALUMNO_REGULAR o ANALITICO), motivo de 1 a 300
caracteres, fecha Instant y estado PENDIENTE, APROBADA o CANCELADA.
El propietario sale de la identidad autenticada. Nunca se toma de un campo del DTO.
Solo PENDIENTE puede aprobarse o cancelarse; el cambio debe ser atómico en memoria.

## Contrato a implementar desde la etapa 02

| Operación | Acceso | Resultado correcto |
|---|---|---|
| GET /public/info | Público | 200, descripción sin datos privados |
| GET /api/me | Autenticado | 200, identidad y autoridades sin credenciales |
| POST /api/solicitudes | SOLICITUD_CREAR | 201 y Location |
| GET /api/solicitudes | Autenticado | 200, propias; ADMIN ve todas |
| GET /api/solicitudes/{id} | Propietario o ADMIN | 200 |
| POST /api/solicitudes/{id}/cancelacion | Propietario | 200 |
| POST /api/solicitudes/{id}/aprobacion | SOLICITUD_APROBAR | 200 |

Datos inválidos: 400; credenciales ausentes o inválidas en API: 401; permiso ausente: 403;
solicitud ausente o ajena para quien no puede verla: 404; transición inválida: 409.
La falta de permiso global se evalúa antes de buscar un recurso.
Tener ROLE_ADMIN no habilita cancelar solicitudes ajenas.

El 404 deliberado evita distinguir inexistencia de recurso ajeno en consultas y
cancelaciones; ninguna respuesta debe incluir el motivo o propietario de otra persona.
La lista también debe filtrar por dueño: proteger solo el detalle no alcanza.

## Web y evolución

En la etapa 06 creá GET /web/me, autenticado por sesión, para observar la identidad,
más el login y logout de Spring. Puede devolver JSON; no hace falta diseñar un frontend.
Al separar cadenas, la sesión web no autentica /api/**, y un Bearer de API no inicia
sesión web. La página pública y health permanecen accesibles.

Sin correo, datos reales, registro público, base de datos ni proveedor externo obligatorio.
Un identificador UUID no sustituye el control de propiedad.

