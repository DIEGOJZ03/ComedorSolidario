# Especificación de casos de uso

El diagrama PlantUML y su SVG están en `diagramas/`. Los dos actores son Donador y Administrador. Visitante describe ausencia de sesión; no es un tercer rol persistente. El caso Consultar mis donaciones incluye mostrar su estado; iniciar sesión se usa como precondición de las operaciones protegidas, no como include repetido.

## CU-01: Registrarse

| Campo | Especificación |
|---|---|
| Nombre | Registrarse |
| Actor | Donador |
| Objetivo | Crear una cuenta que permita donar. |
| Precondición | No necesita sesión. Correo todavía no registrado. |
| Flujo principal | 1. Abre Registro. 2. Escribe datos y confirma contraseña. 3. Envía el formulario. 4. El sistema valida y guarda el hash con rol DONADOR. 5. Lo dirige al login. |
| Flujo alternativo | Correo duplicado, campos inválidos o contraseñas distintas: muestra error y no crea la cuenta. |
| Postcondición | Cuenta activa creada. |

## CU-02: Iniciar sesión

| Campo | Especificación |
|---|---|
| Nombre | Iniciar sesión |
| Actor | Donador / Administrador |
| Objetivo | Acceder a las funciones del rol. |
| Precondición | Cuenta activa registrada. |
| Flujo principal | 1. Abre Login. 2. Escribe correo y contraseña. 3. Se verifica el hash. 4. Se renueva la sesión. 5. Abre el dashboard correspondiente. |
| Flujo alternativo | Credenciales incorrectas o cuenta inactiva: mensaje sin acceso. |
| Postcondición | Sesión con usuarioId, usuarioNombre y usuarioRol. |

## CU-03: Buscar y consultar comedor

| Campo | Especificación |
|---|---|
| Nombre | Buscar y consultar comedor |
| Actor | Donador (también disponible sin sesión) |
| Objetivo | Encontrar comedores por ubicación y conocer su información. |
| Precondición | Existencia de comedores activos para obtener resultados. |
| Flujo principal | 1. Abre Comedores. 2. Combina filtros. 3. Envía GET. 4. El sistema muestra coincidencias. 5. Abre un detalle y sus necesidades activas. |
| Flujo alternativo | Sin coincidencias: mensaje y opción de limpiar filtros. Comedor inactivo o inexistente: 404. |
| Postcondición | Información consultada sin modificar datos. |

## CU-04: Consultar necesidades

| Campo | Especificación |
|---|---|
| Nombre | Consultar necesidades |
| Actor | Donador |
| Objetivo | Identificar productos y prioridades de un comedor. |
| Precondición | Comedor activo. No requiere autenticación. |
| Flujo principal | 1. Abre detalle. 2. El sistema consulta necesidades activas. 3. Muestra nombre, cantidad, unidad, descripción y prioridad. |
| Flujo alternativo | Si no hay necesidades, informa que no hay publicaciones actuales. |
| Postcondición | Donador conoce las necesidades vigentes. |

## CU-05: Registrar donación

| Campo | Especificación |
|---|---|
| Nombre | Registrar donación |
| Actor | Donador |
| Objetivo | Registrar un compromiso de ayuda. |
| Precondición | Sesión DONADOR y comedor activo. |
| Flujo principal | 1. Selecciona comedor. 2. Indica tipo, cantidad, unidad y descripción. 3. Envía POST. 4. Se valida CSRF y contenido. 5. Se toma el usuario desde sesión. 6. Se registra PENDIENTE. 7. Abre Mis donaciones. |
| Flujo alternativo | Cantidad inválida, comedor inactivo o unidad incompatible: no registra y muestra error. Sin sesión: login. |
| Postcondición | Donación vinculada al donador y comedor con estado PENDIENTE. |

## CU-06: Consultar mis donaciones y estado

| Campo | Especificación |
|---|---|
| Nombre | Consultar mis donaciones y estado |
| Actor | Donador |
| Objetivo | Revisar los aportes propios y su progreso. |
| Precondición | Sesión DONADOR. |
| Flujo principal | 1. Abre Mis donaciones. 2. Se consulta por usuario de sesión. 3. Muestra comedor, aporte, fecha, estado y entrega. |
| Flujo alternativo | Sin aportes: mensaje de lista vacía. Un id de otro usuario no cambia el propietario consultado. |
| Postcondición | El usuario ve sus estados actuales; no modifica datos. |

## CU-07: Gestionar perfil

| Campo | Especificación |
|---|---|
| Nombre | Gestionar perfil |
| Actor | Donador |
| Objetivo | Mantener los datos de contacto. |
| Precondición | Sesión DONADOR. |
| Flujo principal | 1. Abre Mi perfil. 2. Edita nombre, apellido y teléfono. 3. Guarda. 4. Se valida y actualiza solo el usuario de sesión. |
| Flujo alternativo | Campos inválidos: conserva formulario con mensaje. Correo y rol no se modifican. |
| Postcondición | Datos propios actualizados. |

## CU-08: Gestionar comedores

| Campo | Especificación |
|---|---|
| Nombre | Gestionar comedores |
| Actor | Administrador |
| Objetivo | Crear, consultar, editar y desactivar/reactivar comedores. |
| Precondición | Sesión ADMIN. |
| Flujo principal | 1. Abre Comedores. 2. Selecciona registrar, ver, editar o desactivar. 3. Completa datos o confirma cambio. 4. Se valida y ejecuta la operación. 5. Regresa al listado con mensaje. |
| Flujo alternativo | Campos inválidos: muestra error. Registro inexistente: error/404. Desactivación: conserva historial y bloquea nuevos aportes. |
| Postcondición | CRUD aplicado; integridad referencial preservada. |

## CU-09: Gestionar necesidades

| Campo | Especificación |
|---|---|
| Nombre | Gestionar necesidades |
| Actor | Administrador |
| Objetivo | Publicar y mantener necesidades por comedor. |
| Precondición | Sesión ADMIN y comedor existente. |
| Flujo principal | 1. Abre Necesidades. 2. Registra o edita producto, cantidad, unidad y prioridad. 3. Guarda. 4. Puede desactivar/reactivar. |
| Flujo alternativo | Cantidad no positiva, prioridad inválida o comedor inexistente: rechaza. |
| Postcondición | Necesidad guardada y visible públicamente solo si ella y el comedor están activos. |

## CU-10: Consultar donaciones

| Campo | Especificación |
|---|---|
| Nombre | Consultar donaciones |
| Actor | Administrador |
| Objetivo | Supervisar todos los aportes. |
| Precondición | Sesión ADMIN. |
| Flujo principal | 1. Abre Donaciones. 2. Selecciona estado opcional. 3. Consulta resultados. 4. Abre un detalle. |
| Flujo alternativo | Filtro sin resultados: lista vacía. Identificador inexistente: 404. |
| Postcondición | Información supervisada sin cambios. |

## CU-11: Actualizar estado de donación

| Campo | Especificación |
|---|---|
| Nombre | Actualizar estado de donación |
| Actor | Administrador |
| Objetivo | Confirmar aceptación o entrega. |
| Precondición | Sesión ADMIN y donación PENDIENTE o ACEPTADA. |
| Flujo principal | 1. Abre detalle. 2. Pulsa la siguiente acción. 3. Confirma. 4. El sistema actualiza solo si conserva el estado previo. 5. Al entregar, guarda fecha. |
| Flujo alternativo | Actualización concurrente: informa que el estado ya cambió. ENTREGADA no admite avance; no se permiten saltos. |
| Postcondición | Donación ACEPTADA o ENTREGADA; cambio visible para su donador. |

## CU-12: Consultar usuarios

| Campo | Especificación |
|---|---|
| Nombre | Consultar usuarios |
| Actor | Administrador |
| Objetivo | Revisar las cuentas registradas. |
| Precondición | Sesión ADMIN. |
| Flujo principal | 1. Abre Usuarios. 2. El sistema muestra datos básicos, rol, estado y registro. |
| Flujo alternativo | Sin autorización: 403. |
| Postcondición | Información consultada; nunca se muestran contraseñas ni hashes. |

## CU-13: Cerrar sesión

| Campo | Especificación |
|---|---|
| Nombre | Cerrar sesión |
| Actor | Donador / Administrador |
| Objetivo | Terminar el acceso autenticado. |
| Precondición | Sesión activa. |
| Flujo principal | 1. Pulsa Cerrar sesión. 2. Se envía POST con CSRF. 3. Se invalida HttpSession. 4. Vuelve al inicio. |
| Flujo alternativo | Token caducado: recargar formulario; no procesa una solicitud falsificada. |
| Postcondición | Rutas protegidas vuelven a requerir login. |
