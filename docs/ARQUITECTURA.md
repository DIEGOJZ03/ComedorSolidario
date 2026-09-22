# Arquitectura de Comedor Solidario

## Responsabilidades

| Capa | Implementación | Responsabilidad |
|---|---|---|
| Vista | JSP + HTML5 + Bootstrap + CSS3 | Presentación y formularios |
| Controlador | Servlets | Recibir GET/POST, validar y elegir la vista |
| Modelo | Usuario, Comedor, Necesidad, Donacion | JavaBeans sin acceso a datos |
| Datos | DAO + JDBC | Consultas parametrizadas y cierre de recursos |
| Persistencia | MySQL | PK, FK, UNIQUE, CHECK y estados permitidos |
| Seguridad transversal | SeguridadFilter | Sesión, roles, CSRF y bloqueo de acceso directo a JSP |

## Flujo de una donación

1. GET `/donante/donaciones/nueva`: el filtro exige DONADOR.
2. DonacionServlet carga comedores activos y hace forward a la vista.
3. El formulario envía POST con token CSRF, comedor, tipo, cantidad, unidad y descripción.
4. El filtro verifica CSRF. El Servlet obtiene donanteId de la sesión, nunca del formulario.
5. Se valida el contenido; DonacionDAO registra con INSERT SELECT y estado fijo PENDIENTE.
6. La operación comprueba nuevamente comedor y cuenta activos.
7. El Servlet redirige a GET `/donante/donaciones` y muestra un mensaje de éxito.
8. La consulta utiliza WHERE donante_id=?; no recibe un donante arbitrario del navegador.

## Decisiones

- Dos roles persistentes. Visitante significa ausencia de sesión.
- Desactivación, no eliminación física de comedores/necesidades; conserva FK e historial.
- Estado de donación condicionado al valor anterior para evitar carreras de actualización.
- Una operación de escritura de este alcance cabe en una sentencia SQL atómica. No hay una transacción Java multi-sentencia innecesaria.
- Dinero es registro de compromiso, no procesamiento de pago.
- Perfil modifica nombre, apellido y teléfono propios; correo y rol no se editan.
- Contraseñas PBKDF2-HMAC-SHA256, 600000 iteraciones, sal aleatoria de 16 bytes, clave de 256 bits. Formato: iteraciones:salBase64:hashBase64.
- Bootstrap se distribuye localmente con su licencia. Las ilustraciones son SVG originales.
- Las listas muestran el conjunto completo, apropiado para la demostración académica. Un despliegue con alto volumen requeriría paginación y otras medidas operativas.

## Explicación de la distribución

El navegador envía HTTP a Tomcat. Los Servlets ejecutados en Tomcat usan JDBC para comunicarse con MySQL. Pueden ejecutarse en máquinas distintas configurando red y credenciales; para esta práctica se ejecutan en localhost. No se afirma que sean microservicios.

## Correspondencia con la rúbrica

| Criterio | Evidencia |
|---|---|
| Presentación, roles y funciones | README, arquitectura, presentación |
| Casos de uso y especificación | docs/casos-de-uso.md y diagrama |
| Clases y datos | JavaBeans, diagramas, SQL y DAO |
| UX donador | Inicio, catálogo, dashboard, donación, seguimiento, perfil |
| UX administrador | Dashboard, CRUD, necesidades, donaciones, usuarios |
| Servlets, JSP y JavaBeans | src/java y web/views, flujo MVC implementado |
