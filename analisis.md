# Análisis Técnico – Comedor Solidario

> **Propósito de este documento:** Servir de guía de referencia rápida para cualquier desarrollador nuevo (o futuro colaborador) que necesite entender cómo está construido el sistema, sin tener que leer cada archivo de código individualmente.

---

## 1. Descripción general

**Comedor Solidario** es una aplicación web Java EE (Jakarta EE 6) orientada a conectar **comedores comunitarios** con **donantes** que quieran aportar alimentos, utensilios o dinero. Gestiona comedores, necesidades, donaciones y campañas de recolección.

| Atributo | Detalle |
|---|---|
| Tipo | Aplicación web WAR (Java EE / Jakarta EE) |
| Lenguaje | Java 21 |
| Especificación Servlet | Jakarta Servlet 6.0 |
| Plantillas de vista | JSP + JSTL 3.0 |
| Base de datos | MySQL (motor InnoDB, charset utf8mb4) |
| Conector BD | MySQL Connector/J 8.4.0 |
| Front-end | Bootstrap 5 (vendor local) + CSS/JS propios |
| Construcción | Maven (empaqueta como `.war`) |
| Servidor destino | Apache Tomcat (u otro contenedor Jakarta EE 6) |

---

## 2. Estructura de directorios

```
ComedorSolidario/
├── database/               # Scripts SQL de base de datos
│   ├── comedor_solidario.sql   # Creación completa + datos de prueba
│   └── migracion_rubrica.sql   # Migración incremental (no aplicar si se usó el .sql completo)
├── src/
│   ├── java/com/comedorsolidario/
│   │   ├── controller/     # Servlets (controladores HTTP)
│   │   ├── dao/            # Acceso a datos (Data Access Objects)
│   │   ├── filter/         # Filtros Jakarta Servlet
│   │   ├── model/          # JavaBeans (entidades del dominio)
│   │   ├── service/        # Lógica de negocio pura (sin HTTP ni SQL)
│   │   └── util/           # Utilidades transversales
│   └── resources/          # Recursos del classpath (db.properties, etc.)
├── web/
│   ├── index.jsp           # Página de inicio pública
│   ├── WEB-INF/
│   │   ├── web.xml         # Descriptor de despliegue
│   │   └── tags/           # Tag files reutilizables (JSPF)
│   ├── assets/
│   │   ├── css/            # Hojas de estilo propias
│   │   ├── js/             # Scripts propios
│   │   ├── img/            # Imágenes estáticas del sitio
│   │   └── vendor/bootstrap/  # Bootstrap 5 local
│   └── views/
│       ├── admin/          # Vistas exclusivas de ADMIN
│       ├── auth/           # Login y registro
│       ├── common/         # Fragmentos reutilizables (header, footer, badge, cards)
│       ├── donante/        # Vistas exclusivas de DONADOR
│       ├── errors/         # Páginas de error HTTP (403, 404, 500)
│       └── public/         # Vistas accesibles sin sesión
├── pom.xml                 # Configuración Maven
└── analisis.md             # Este documento
```

---

## 3. Arquitectura: patrón MVC

El sistema sigue el patrón **MVC clásico de Servlet/JSP**:

```
Petición HTTP
     │
     ▼
[SeguridadFilter]   ← Filtro global: autenticación, CSRF, encoding, cabeceras seguras
     │
     ▼
[Servlet / Controller]  ← Lógica de control: valida entrada, llama DAO/Service, decide vista
     │          │
     │          └──► [DAO]   ← Consultas SQL (extiende JdbcDAO)
     │                  │
     │                  └──► MySQL (via ConexionBD)
     │
     ▼
[JSP / Vista]       ← Solo presenta datos; sin lógica de negocio
```

---

## 4. Capas del backend

### 4.1 `model/` — Entidades del dominio (JavaBeans)

Clases POJO puras: solo almacenan datos, **sin lógica SQL ni de negocio**.

| Clase | Tabla BD | Campos destacados |
|---|---|---|
| `Usuario` | `usuarios` | `id`, `nombre`, `apellido`, `email`, `passwordHash`, `telefono`, `rol` (`ADMIN`/`DONADOR`), `estado` (`ACTIVO`/`INACTIVO`), `fechaRegistro` |
| `Comedor` | `comedores` | `id`, `nombre`, `descripcion`, `direccion`, `zona`, `distrito`, `provincia`, `responsable`, `telefono`, `estado`, `fechaRegistro` |
| `Donacion` | `donaciones` | `id`, `donanteId`, `comedorId`, `tipo` (`ALIMENTOS`/`UTENSILIOS`/`DINERO`/`OTROS`), `cantidad`, `unidad`, `descripcion`, `estado` (`PENDIENTE`/`ACEPTADA`/`ENTREGADA`), `activo`, `fechaRegistro`, `fechaEntrega`, `comedorNombre` *(campo desnormalizado de JOIN)*, `donanteNombre` |
| `Necesidad` | `necesidades` | `id`, `comedorId`, `productoId` *(FK opcional)*, `nombre`, `descripcion`, `cantidadNecesaria`, `unidad`, `prioridad` (`ALTA`/`MEDIA`/`BAJA`), `estado` (`ACTIVA`/`INACTIVA`), `comedorNombre` |
| `Campania` | `campanias` | `id`, `comedorId`, `nombre`, `descripcion`, `fechaInicio`, `fechaFin`, `estado` |
| `Producto` | `productos` | `id`, `nombre`, `descripcion`, `unidad`, `estado` |
| `ResumenProducto` | *(sin tabla propia)* | Agrupación calculada en memoria: nombre, unidad, lista de `Necesidad` |

---

### 4.2 `dao/` — Acceso a datos

#### Clase base: `JdbcDAO`

Clase abstracta que encapsula el manejo de recursos JDBC. **Todos los DAO heredan de ella.**

| Método protegido | Propósito |
|---|---|
| `consultar(sql, mapper, params...)` | `SELECT`: devuelve `List<T>` usando una función lambda `Mapper<T>` que convierte cada fila |
| `ejecutar(sql, params...)` | `UPDATE`/`DELETE`: devuelve filas afectadas |
| `insertar(sql, params...)` | `INSERT`: devuelve el ID generado automáticamente por MySQL |
| `eliminarRegistro(sql, id, msg)` | `DELETE` con validación de existencia y de restricciones de integridad referencial |
| `contar(sql, params...)` | `SELECT COUNT(*)`: devuelve un `long` |

Cada conexión se abre y cierra con `try-with-resources`; no hay pool de conexiones propio.

#### DAOs concretos

| DAO | Operaciones principales |
|---|---|
| `UsuarioDAO` | `buscarEmail`, `buscar(id)`, `registrar`, `cambiarEstado`, `totalDonadores` |
| `ComedorDAO` | `listar` *(filtros: activos, zona, distrito, provincia)*, `buscar`, `guardar` *(insert/update)*, `cambiarEstado`, `eliminar`, `total`, `listarResponsables` |
| `DonacionDAO` | `listar` *(donante, estado)*, `listarAdministracion`, `buscar`, `registrar`, `actualizar`, `avanzar` *(ciclo de estado)*, `darBaja`, `cambiarVigencia`, `eliminar`, `resumen` |
| `NecesidadDAO` | `listar` *(por comedor, solo activas)*, `buscar`, `guardar`, `cambiarEstado`, `eliminar` |
| `CampaniaDAO` | `listar`, `listarPublicas`, `buscar`, `guardar`, `cambiarEstado`, `eliminar` |
| `ProductoDAO` | `listar`, `buscar`, `guardar`, `eliminar` |

> **Nota sobre `DonacionDAO.registrar`:** usa un `INSERT … SELECT` que reverifica en la misma transacción que el comedor y el donante existen y están activos, evitando race conditions.

---

### 4.3 `service/` — Lógica de negocio

#### `ResumenProductosService`

Opera **en memoria** (sin SQL). Recibe la lista de `Necesidad` ya cargada y las agrupa por nombre+unidad para construir un catálogo de productos con los comedores que los solicitan. Normaliza nombres (elimina tildes, normaliza unidades: `kg`, `litros`, `unidades`, `cajas`, `soles`) para que "Arroz", "arroz" y "ARROZ" se traten como el mismo producto.

---

### 4.4 `filter/` — Seguridad centralizada

#### `SeguridadFilter`

Registrado en `web.xml` para el patrón `/*`. Ejecuta **antes que cualquier Servlet**:

1. Fija encoding UTF-8 en request y response.
2. Añade cabeceras de seguridad: `X-Content-Type-Options`, `X-Frame-Options: DENY`, `Referrer-Policy`.
3. Permite sin restricción los recursos estáticos (`/assets/`).
4. Bloquea acceso directo a `/views/` (las JSP solo son accesibles por forward interno).
5. Genera token CSRF en sesión si no existe.
6. Redirige a `/login` si se intenta acceder a rutas `/admin/` o `/donante/` sin sesión activa. Guarda el ID del comedor en sesión si el usuario iba a hacer una donación (`retornoDonacion`).
7. Verifica en cada petición autenticada que el usuario siga existiendo y esté **ACTIVO** (invalida sesión si fue desactivado).
8. Verifica que el rol coincida con la ruta (`ADMIN` → `/admin/`, `DONADOR` → `/donante/`), devuelve 403 si no.
9. Valida el token CSRF en cada `POST`.
10. Maneja el caso especial de multipart demasiado grande en `/admin/comedores` (redirige con mensaje de error en lugar de lanzar excepción).

---

### 4.5 `controller/` — Servlets (controladores)

Todos heredan de `BaseServlet`, que:
- Enruta `doGet` → `get(...)` y `doPost` → `post(...)`.
- Captura `IllegalArgumentException` → HTTP 400 y muestra vista de error.
- Captura `SQLException` → HTTP 500 y loguea con `getServletContext().log`.
- Provee helpers: `vista(r, s, ruta)`, `ir(r, s, ruta, mensaje)`, `usuarioId(r)`.

#### Mapa de rutas y servlets

| URL(s) | Servlet | Rol requerido | Descripción |
|---|---|---|---|
| `/inicio` | `InicioServlet` | Público | Página principal: 3 comedores destacados, totales estadísticos |
| `/comedores` | `ComedorServlet` | Público | Listado público con filtros (zona, distrito, provincia) |
| `/comedores/detalle` | `ComedorServlet` | Público | Detalle de un comedor: necesidades activas + campañas públicas |
| `/login` | `LoginServlet` | Público | GET muestra formulario; POST autentica y redirige según rol |
| `/registro` | `RegistroServlet` | Público | GET muestra formulario; POST crea cuenta de DONADOR |
| `/logout` | `LogoutServlet` | Autenticado | Invalida sesión y redirige a `/inicio` |
| `/donante/dashboard` | `DonadorDashboardServlet` | DONADOR | Panel del donante |
| `/donante/donaciones` | `DonacionServlet` | DONADOR | Lista las donaciones propias |
| `/donante/donaciones/nueva` | `DonacionServlet` | DONADOR | GET muestra formulario; POST registra nueva donación |
| `/donante/perfil` | `PerfilServlet` | DONADOR | Ver y actualizar datos personales |
| `/admin/dashboard` | `AdminDashboardServlet` | ADMIN | Panel admin: totales de donaciones, comedores y donadores |
| `/admin/comedores` | `AdminComedorServlet` | ADMIN | CRUD completo de comedores + subida de foto |
| `/admin/comedores/formulario` | `AdminComedorServlet` | ADMIN | Formulario de alta/edición de comedor |
| `/admin/comedores/detalle` | `AdminComedorServlet` | ADMIN | Vista detallada de un comedor (admin) |
| `/admin/necesidades` | `NecesidadServlet` | ADMIN | CRUD de necesidades |
| `/admin/necesidades/formulario` | `NecesidadServlet` | ADMIN | Alta/edición de necesidad |
| `/admin/necesidades/detalle` | `NecesidadServlet` | ADMIN | Detalle de necesidad |
| `/admin/donaciones` | `AdminDonacionServlet` | ADMIN | Listado y gestión de donaciones |
| `/admin/campanias` | `AdminCampaniaServlet` | ADMIN | CRUD de campañas por comedor |
| `/admin/usuarios` | `AdminUsuarioServlet` | ADMIN | Gestión de usuarios (cambiar estado) |
| `/admin/productos` | `AdminProductoServlet` | ADMIN | CRUD de catálogo de productos |
| `/fotos/comedores` | `FotoComedorServlet` | Público | Sirve la imagen PNG de un comedor desde el sistema de archivos externo |

---

### 4.6 `util/` — Utilidades transversales

| Clase | Función |
|---|---|
| `ConexionBD` | Abre conexiones JDBC. Lee configuración de variables de entorno (`CS_DB_URL`, `CS_DB_USER`, `CS_DB_PASSWORD`) con fallback a `db.properties` (en classpath) y valores por defecto. Usuario default: `comedor_app` en `localhost:3306`. |
| `PasswordUtil` | Hash y verificación de contraseñas con **PBKDF2-HMAC-SHA256**, sal de 16 bytes aleatoria, 600 000 iteraciones. Formato almacenado: `iteraciones:base64(sal):base64(hash)`. |
| `ValidacionUtil` | Métodos estáticos de validación de parámetros HTTP: `texto`, `email`, `password`, `cantidad`, `id`, `fecha`, `opcion`. Lanza `IllegalArgumentException` (que `BaseServlet` convierte en HTTP 400). |
| `ImagenComedor` | Gestiona fotos de comedores **fuera del WAR** (en `~/ComedorSolidarioDatos/comedores/` o ruta de `CS_UPLOAD_DIR`). Valida formato (JPG/PNG), tamaño (≤ 5 MB) y dimensiones (≤ 6000×6000 px, ≤ 16 Mpx). Guarda como `<id>.png`. |
| `UbicacionComedor` | Catálogo estático de provincias y distritos de Lima y Callao. Normaliza nombres (ignora tildes y mayúsculas). Compartido por admin y búsqueda pública. |

---

## 5. Base de datos

### Esquema de tablas

```
usuarios
  ├── id (PK, AUTO)
  ├── nombre, apellido, email (UNIQUE), password_hash, telefono
  ├── rol: ENUM('ADMIN','DONADOR')
  ├── estado: ENUM('ACTIVO','INACTIVO')
  └── fecha_registro: DATETIME

comedores
  ├── id (PK, AUTO)
  ├── nombre, descripcion, direccion, zona, distrito, provincia
  ├── responsable, telefono
  ├── estado: ENUM('ACTIVO','INACTIVO')
  ├── fecha_registro: DATETIME
  └── INDEX idx_ubicacion (provincia, distrito, zona)

necesidades
  ├── id (PK, AUTO)
  ├── comedor_id → comedores.id  (FK)
  ├── producto_id → productos.id (FK, nullable) *añadido en migración*
  ├── nombre, descripcion
  ├── cantidad_necesaria: DECIMAL(12,2) CHECK > 0
  ├── unidad
  ├── prioridad: ENUM('ALTA','MEDIA','BAJA')
  └── estado: ENUM('ACTIVA','INACTIVA')

donaciones
  ├── id (PK, AUTO)
  ├── donante_id → usuarios.id  (FK)
  ├── comedor_id → comedores.id (FK)
  ├── tipo: ENUM('ALIMENTOS','UTENSILIOS','DINERO','OTROS')
  ├── cantidad: DECIMAL(12,2) CHECK > 0
  ├── unidad
  ├── descripcion
  ├── estado: ENUM('PENDIENTE','ACEPTADA','ENTREGADA')
  ├── activo: BOOLEAN DEFAULT TRUE  *añadido en migración*
  ├── fecha_registro: DATETIME
  └── fecha_entrega: DATETIME NULL (NOT NULL solo si estado='ENTREGADA')

productos
  ├── id (PK, AUTO)
  ├── nombre, descripcion, unidad
  └── estado: ENUM('ACTIVO','INACTIVO')

campanias
  ├── id (PK, AUTO)
  ├── comedor_id → comedores.id (FK)
  ├── nombre, descripcion
  ├── fecha_inicio, fecha_fin: DATE (CHECK fecha_fin >= fecha_inicio)
  └── estado: ENUM('ACTIVO','INACTIVO')
```

### Flujo de estados

**Donación:**
```
PENDIENTE ──► ACEPTADA ──► ENTREGADA
```
- Solo avanza hacia adelante (`DonacionDAO.avanzar`).
- Soft-delete mediante campo `activo = FALSE` (`darBaja` / `cambiarVigencia`).

**Comedor / Necesidad / Campaña / Producto / Usuario:**
```
ACTIVO ⟺ INACTIVO
```
- Cambio de estado; borrado físico solo si no hay relaciones dependientes.

---

## 6. Seguridad

| Mecanismo | Implementación |
|---|---|
| **Autenticación** | Sesión HTTP (`HttpSession`); `usuarioId` y `usuarioRol` en sesión |
| **Contraseñas** | PBKDF2-HMAC-SHA256, 600 000 iteraciones, sal aleatoria por usuario |
| **Autorización** | `SeguridadFilter` verifica rol contra prefijo de ruta (`/admin/`, `/donante/`) |
| **CSRF** | Token UUID en sesión, validado en cada POST (campo oculto `csrf`) |
| **Session Fixation** | `r.changeSessionId()` tras login exitoso |
| **Cabeceras HTTP** | `X-Content-Type-Options: nosniff`, `X-Frame-Options: DENY`, `Referrer-Policy: same-origin`, `Cache-Control: no-store` |
| **Acceso directo a JSP** | El filtro devuelve 404 para cualquier URL que empiece por `/views/` |
| **Inyección SQL** | Todo el SQL usa `PreparedStatement` parametrizado (via `JdbcDAO`) |
| **Validación de entrada** | `ValidacionUtil` valida todos los parámetros antes de usarlos |
| **Path Traversal (imágenes)** | `ImagenComedor.guardar` verifica que el destino sea exactamente el directorio configurado |
| **Multipart demasiado grande** | Manejado en el filtro con mensaje amigable en lugar de excepción al usuario |

---

## 7. Vistas JSP

Las vistas **solo muestran datos**; no llaman a DAOs ni contienen lógica de negocio.

### Estructura de vistas

```
web/views/
├── auth/
│   ├── login.jsp
│   └── registro.jsp
├── common/                         # Fragmentos incluidos con <jsp:include>
│   ├── header.jspf                 # Navbar, links de sesión, flash messages
│   ├── footer.jspf
│   ├── badge.jspf                  # Badge de estado (ACTIVO/INACTIVO/etc.)
│   └── cards-comedores.jspf        # Tarjetas de comedores (reutilizado en inicio y listado)
├── public/
│   ├── comedores.jsp               # Listado público con filtros
│   └── detalle-comedor.jsp         # Detalle: info, necesidades, campañas
├── admin/
│   ├── dashboard.jsp
│   ├── comedores/  (listar, formulario, detalle)
│   ├── necesidades/ (listar, formulario, detalle)
│   ├── donaciones/  (listar, formulario, detalle)
│   ├── campanias/   (listar, formulario, detalle)
│   ├── productos/   (listar, formulario)
│   └── usuarios/    (listar)
├── donante/
│   ├── dashboard.jsp
│   ├── mis-donaciones.jsp
│   ├── realizar-donacion.jsp
│   └── perfil.jsp
└── errors/
    ├── 403.jsp
    ├── 404.jsp
    └── 500.jsp
```

### Convenciones de vistas

- Los **mensajes flash** (éxito/error de acciones) se transmiten como atributo de sesión `exito` o `errorAccion` y se muestran en `header.jspf`.
- El atributo de request `error` se usa para errores de validación de formularios (HTTP 400).
- Los datos se pasan como atributos de request (`r.setAttribute`) desde el servlet.

---

## 8. Configuración de entorno

### Variables de entorno

| Variable | Uso | Valor por defecto |
|---|---|---|
| `CS_DB_URL` | URL de conexión JDBC | `jdbc:mysql://localhost:3306/comedor_solidario?...` |
| `CS_DB_USER` | Usuario de MySQL | `comedor_app` |
| `CS_DB_PASSWORD` | Contraseña de MySQL | `""` (vacía) |
| `CS_UPLOAD_DIR` | Directorio de imágenes de comedores | `~/ComedorSolidarioDatos/comedores/` |

### Archivo `db.properties` (en classpath)

Alternativa a variables de entorno. Se lee desde `src/resources/db.properties`. Las variables de entorno tienen **prioridad** sobre este archivo.

### Sesión HTTP

- Timeout: **30 minutos** (configurado en `web.xml`).
- Cookie HttpOnly activada; modo de tracking: solo COOKIE (sin URL rewriting).

---

## 9. Flujos principales

### Registro e inicio de sesión

```
Usuario ──► GET /registro ──► RegistroServlet ──► auth/registro.jsp
         ──► POST /registro ──► Valida datos ──► PasswordUtil.hash ──► UsuarioDAO.registrar
                                                ──► Redirige a /login con mensaje de éxito

Usuario ──► POST /login ──► LoginServlet ──► UsuarioDAO.buscarEmail
                                          ──► PasswordUtil.verificar
                                          ──► changeSessionId + atributos de sesión
                                          ──► Redirige a dashboard (por rol)
```

### Realizar una donación (flujo completo)

```
Visitante ──► /comedores/detalle?id=X ──► Botón "Donar"
          ──► SeguridadFilter: sin sesión → guarda retornoDonacion=X en sesión → /login
          ──► Login exitoso → detecta retornoDonacion → /donante/donaciones/nueva?comedorId=X
          ──► DonacionServlet.get → carga comedores activos → realizar-donacion.jsp
          ──► DonacionServlet.post → valida → DonacionDAO.registrar (INSERT SELECT) → PENDIENTE
```

### Ciclo de vida de una donación (admin)

```
PENDIENTE ──► AdminDonacionServlet: avanzar ──► ACEPTADA
ACEPTADA  ──► AdminDonacionServlet: avanzar ──► ENTREGADA (+ fecha_entrega = NOW())
```

### Gestión de comedores con foto

```
AdminComedorServlet.post (multipart/form-data)
  ├── Valida campos de texto (ValidacionUtil)
  ├── ImagenComedor.preparar: valida imagen → crea archivo temporal
  ├── ComedorDAO.guardar: INSERT/UPDATE → obtiene ID
  └── ImagenComedor.guardar: mueve temporal → <id>.png (fuera del WAR)
```

---

## 10. Decisiones de diseño notables

| Decisión | Justificación |
|---|---|
| Sin pool de conexiones | Cada `JdbcDAO` abre y cierra su propia conexión con try-with-resources. Aceptable para carga académica/moderada. |
| Imágenes fuera del WAR | Sobreviven a Clean & Build y a redeployments. Directorio configurable por variable de entorno. |
| `INSERT … SELECT` en donaciones | Garantiza atomicidad: verifica comedor y donante activos en la misma escritura, evitando condición de carrera. |
| Soft-delete en donaciones | El campo `activo` permite dar de baja sin perder el historial; el borrado físico solo lo hace el admin y está bloqueado por FK. |
| Validación en `ValidacionUtil` | Centraliza reglas reutilizables; las excepciones `IllegalArgumentException` son capturadas por `BaseServlet` y convertidas en HTTP 400. |
| `ResumenProductosService` en memoria | Evita una consulta compleja de agrupación en SQL; la normalización de unidades es más fácil de mantener en Java. |
| CSRF doble (sesión + campo hidden) | Patrón sincronizador estándar; el filtro lo valida en todos los POST. |
| `changeSessionId()` en login | Previene session fixation sin invalidar la sesión completa. |

---

## 11. Cómo añadir una nueva funcionalidad

1. **Modelo:** Crear/modificar el JavaBean en `model/` si hay nuevos datos.
2. **Tabla BD:** Añadir la tabla o columna en `database/` (crear un archivo de migración `migration_*.sql`).
3. **DAO:** Crear un DAO que extienda `JdbcDAO`; implementar un método `map(ResultSet)` privado para convertir filas.
4. **Servlet:** Crear un servlet que extienda `BaseServlet`; anotar con `@WebServlet({"/ruta"})`.
5. **Vista JSP:** Crear la JSP en el subdirectorio apropiado de `web/views/`. Incluir `common/header.jspf` y `common/footer.jspf`.
6. **Seguridad:** Si la ruta requiere autenticación, usar el prefijo `/admin/` o `/donante/`; `SeguridadFilter` la protegerá automáticamente.

---

## 12. Dependencias del proyecto (`pom.xml`)

| Artefacto | Versión | Propósito |
|---|---|---|
| `jakarta.servlet-api` | 6.0.0 | API Servlet (provided — la provee Tomcat) |
| `jakarta.servlet.jsp.jstl-api` | 3.0.2 | API JSTL |
| `jakarta.servlet.jsp.jstl` (GlassFish) | 3.0.1 | Implementación JSTL |
| `mysql-connector-j` | 8.4.0 | Conector JDBC para MySQL |
| Bootstrap | 5.x | CSS/JS front-end (vendor local en `web/assets/vendor/bootstrap/`) |

---

*Documento generado el 21/10/2026 a partir del análisis del código fuente del proyecto.*
