# Diseno tecnico - SmartCampus UTA Web

## Capas

Presentacion:
- `presentacion/controllers/SmartCampusController.java`: servlet frontal para endpoints JSON.
- `presentacion/web`: interfaz HTML, CSS y JS con formularios validados por HTML5.

Aplicacion:
- `AuthService`: registro y autenticacion.
- `TramiteService`: solicitudes, cambios de estado, historial, expediente y deshacer.
- `AtencionService`: turnos FIFO y asignacion Round-Robin de ventanillas.
- `DocumentoService`: arbol de categorias documentales.
- `RutasService`: grafo del campus y ruta mas corta.

Dominio:
- `models`: POJO sin anotaciones para `Usuario`, `Tramite`, `Turno`, `Documento`, `PuntoRuta`, etc.
- `estructuras`: implementaciones propias de estructuras de datos.

Persistencia:
- `DatabaseConnection`: conexion JDBC a PostgreSQL/Supabase.
- `dao`: consultas parametrizadas con `PreparedStatement`.
- `repositories/Queries.sql`: esquema base compatible con Supabase.

## Casos reales por estructura

| Estructura | Clase | Caso de uso |
| --- | --- | --- |
| Lista secuencial | `ListaSecuencial` | Catalogos compactos: puntos de ruta y categorias documentales. |
| Lista simple | `ListaSimple` | Historial dinamico de cambios de tramite. |
| Lista doble | `ListaDoble` | Expediente navegable adelante/atras. |
| Lista circular | `ListaCircular` | Rotacion de ventanillas activas. |
| Cola | `Cola` | Turnos pendientes con atencion FIFO. |
| Pila | `Pila` | Deshacer el ultimo cambio de estado. |
| Arbol | `Arbol` | Jerarquia padre-hijo de categorias documentales. |
| Grafo | `Grafo` | Mapa del campus con Dijkstra para ruta mas corta. |

## Endpoints principales

| Metodo | Ruta | Descripcion |
| --- | --- | --- |
| POST | `/api/auth/registro` | Crea usuario con hash PBKDF2. |
| POST | `/api/auth/login` | Verifica credenciales. |
| POST | `/api/tramites` | Registra solicitud de tramite. |
| POST | `/api/tramites/estado` | Cambia estado y registra historial. |
| POST | `/api/tramites/deshacer` | Revierte ultimo cambio usando pila. |
| GET | `/api/tramites/historial?tramiteId=1` | Lista historial del tramite. |
| POST | `/api/turnos` | Crea turno pendiente. |
| POST | `/api/turnos/atender` | Desencola y asigna ventanilla. |
| GET | `/api/documentos/arbol` | Devuelve arbol documental. |
| GET | `/api/rutas/calcular?origen=1&destino=2` | Calcula ruta mas corta. |

## Conexion Supabase

La URL publica del proyecto es `https://gjqrqfxgzewbrhovwtyi.supabase.co`.
Para JDBC se usa:

```text
jdbc:postgresql://db.gjqrqfxgzewbrhovwtyi.supabase.co:5432/postgres?sslmode=require
```

Las credenciales se leen desde variables de entorno para no exponer passwords en codigo.

## Buenas practicas aplicadas

- DAO con SQL parametrizado para evitar inyeccion SQL.
- Transacciones en cambios de estado de tramite.
- Hash de passwords con PBKDF2 nativo de Java.
- POJO sin anotaciones en dominio.
- Separacion de responsabilidades entre servlet, servicios y DAO.
- Errores convertidos a respuestas JSON con estado HTTP apropiado.
