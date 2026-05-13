# SmartCampus UTA Web

Aplicacion web Java para gestionar atencion estudiantil en FISEI: autenticacion por rol, tramites, turnos, documentos institucionales y rutas del campus.

## Estructura del proyecto

- `dominio/models`: POJO del negocio, sin anotaciones de framework.
- `dominio/estructuras`: listas, pila, cola, arbol y grafo implementados para los casos academicos del sistema.
- `persistencia`: conexion JDBC a PostgreSQL/Supabase, DAO y scripts SQL.
- `aplicacion/services`: reglas de negocio y uso de estructuras de datos.
- `presentacion/controllers`: servlet frontal JSON bajo `/api/*`.
- `presentacion/web`: frontend HTML, CSS y JS por roles.

## Roles

El sistema usa estos identificadores en base de datos, backend y frontend:

| ID | Rol | Panel |
| --- | --- | --- |
| 1 | ADMIN | `roles/administrador/dashboard.html` |
| 2 | EMPLEADO | `roles/empleado/dashboard.html` |
| 3 | ESTUDIANTE | `roles/estudiante/dashboard.html` |

## Configuracion Supabase

Define variables de entorno antes de ejecutar:

```powershell
$env:SUPABASE_DB_HOST="db.gjqrqfxgzewbrhovwtyi.supabase.co"
$env:SUPABASE_DB_PORT="5432"
$env:SUPABASE_DB_NAME="postgres"
$env:SUPABASE_DB_USER="postgres"
$env:SUPABASE_DB_PASSWORD="TU_PASSWORD"
```

## Ejecutar

1. Ejecuta `persistencia/repositories/Queries.sql` en Supabase.
2. Ejecuta `persistencia/repositories/SeedData.sql` para crear roles, categorias, ventanillas, documentos y rutas iniciales.
3. Inicia Jetty:

```powershell
mvn jetty:run
```

4. Abre `http://localhost:8080/smartcampus`.

El frontend consume `http://localhost:8080/smartcampus/api` desde `presentacion/web/assets/js/config.js`.

## Rutas principales del frontend

- Publicas: `index.html`, `login.html`, `registro.html`, `turnos/cola_atencion.html`.
- Administrador: usuarios, tramites, turnos, documentos, rutas, reportes y auditoria.
- Empleado: atencion de turnos, tramites asignados, documentos pendientes, horario y mapa.
- Estudiante: solicitud de tramite, turnos, documentos, mapa, perfil e historial.

## Endpoints activos

- `POST /api/auth/registro`
- `POST /api/auth/login`
- `POST /api/auth/logout`
- `GET /api/auth/usuarios`
- `POST /api/auth/empleados`
- `GET /api/tramites`
- `POST /api/tramites`
- `GET /api/tramites/estado?tramiteId=1`
- `GET /api/tramites/historial?tramiteId=1`
- `POST /api/tramites/estado`
- `POST /api/tramites/deshacer`
- `GET /api/turnos/pendientes`
- `POST /api/turnos`
- `POST /api/turnos/atender`
- `GET /api/documentos/arbol`
- `GET /api/rutas/puntos`
- `GET /api/rutas/calcular?origen=1&destino=2`
- `GET /api/reportes/turnos-atendidos`
- `GET /api/reportes/tramites-periodo`
- `GET /api/reportes/usuarios-por-rol`

## Estructuras de datos aplicadas

- Cola: `AtencionService` carga turnos pendientes y atiende en orden FIFO.
- Lista circular: `AtencionService` rota ventanillas activas.
- Lista simple: `TramiteService` expone historial de solicitudes.
- Lista doble: `TramiteService` arma expediente navegable.
- Pila: `TramiteService` permite deshacer el ultimo cambio de estado.
- Arbol: `DocumentoService` construye categorias documentales.
- Grafo: `RutasService` calcula rutas internas con Dijkstra.
