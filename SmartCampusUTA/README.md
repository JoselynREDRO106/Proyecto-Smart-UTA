# SmartCampus UTA Web

Proyecto web Java por capas para gestionar atencion estudiantil, tramites, organizacion documental y rutas dentro del campus universitario.

## Arquitectura

- `dominio/models`: POJO sin anotaciones.
- `dominio/estructuras`: implementaciones propias de lista secuencial, lista simple, lista doble, lista circular, pila, cola, arbol y grafo.
- `persistencia`: conexion JDBC a Supabase, DAO y repositorio SQL.
- `aplicacion/services`: reglas de negocio y uso real de estructuras.
- `presentacion/controllers`: servlet frontal JSON.
- `presentacion/web`: interfaz HTML, CSS y JS.

## Configuracion Supabase

El proyecto usa JDBC puro. Define variables de entorno antes de ejecutar:

```powershell
$env:SUPABASE_DB_HOST="db.gjqrqfxgzewbrhovwtyi.supabase.co"
$env:SUPABASE_DB_PORT="5432"
$env:SUPABASE_DB_NAME="postgres"
$env:SUPABASE_DB_USER="postgres"
$env:SUPABASE_DB_PASSWORD="TU_PASSWORD"
```

La URL del proyecto Supabase es `https://gjqrqfxgzewbrhovwtyi.supabase.co`, pero JDBC debe conectarse al host de base de datos `db.gjqrqfxgzewbrhovwtyi.supabase.co`.

## Ejecutar

1. Ejecuta en Supabase el archivo `persistencia/repositories/Queries.sql`.
2. Ejecuta `persistencia/repositories/SeedData.sql` para crear roles base y datos minimos de prueba.
3. Inicia la app:

```powershell
mvn jetty:run
```

4. Abre `http://localhost:8080/smartcampus`.

## Uso de estructuras de datos

- Cola: `AtencionService` carga turnos pendientes y atiende en orden FIFO.
- Lista circular: `AtencionService` rota ventanillas activas con Round-Robin.
- Lista simple: `TramiteService` expone historial dinamico de solicitudes.
- Lista doble: `TramiteService` arma navegacion adelante/atras de expedientes.
- Pila: `TramiteService` registra cambios de estado para deshacer el ultimo evento.
- Arbol: `DocumentoService` construye jerarquia de categorias documentales.
- Grafo: `RutasService` calcula ruta mas corta entre puntos del campus.

## Nota importante sobre RLS

El script habilita Row Level Security en Supabase. Para que JDBC funcione con usuario `postgres`, aplica politicas RLS apropiadas o usa credenciales con permisos suficientes para el entorno academico.
