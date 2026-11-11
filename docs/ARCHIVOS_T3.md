# Inventario de la entrega T3

Rutas relativas a ComedorSolidario-yoel. Se conserva la estructura previa; no se eliminó ningún archivo existente. No incluye target, credenciales locales ni los datos temporales usados en las pruebas.

Inventario final revisado el 11 de noviembre de 2026: 31 archivos nuevos y 32 modificados respecto de la base de esta entrega, incluyendo el SVG completo solicitado posteriormente.

## Archivos nuevos

- [database/migracion_t3_logistica.sql](../database/migracion_t3_logistica.sql)
- [docs/ARCHIVOS_T3.md](../docs/ARCHIVOS_T3.md)
- [docs/base-datos-t3.puml](../docs/base-datos-t3.puml)
- [docs/base-datos-t3.svg](../docs/base-datos-t3.svg)
- [docs/ClasesCompletas.svg](../docs/ClasesCompletas.svg)
- [docs/EJECUCION_Y_PRUEBAS_MANUALES_T3.md](../docs/EJECUCION_Y_PRUEBAS_MANUALES_T3.md)
- [docs/evidencia-t3-acopio.jpg](../docs/evidencia-t3-acopio.jpg)
- [docs/T3_LOGISTICA.md](../docs/T3_LOGISTICA.md)
- [docs/VERIFICACION_T3.md](../docs/VERIFICACION_T3.md)
- [src/java/com/comedorsolidario/config/LogisticaConfig.java](../src/java/com/comedorsolidario/config/LogisticaConfig.java)
- [src/java/com/comedorsolidario/controller/logistica/LogisticaController.java](../src/java/com/comedorsolidario/controller/logistica/LogisticaController.java)
- [src/java/com/comedorsolidario/dao/DetalleRutaRecojoDAO.java](../src/java/com/comedorsolidario/dao/DetalleRutaRecojoDAO.java)
- [src/java/com/comedorsolidario/dao/LogisticaDAO.java](../src/java/com/comedorsolidario/dao/LogisticaDAO.java)
- [src/java/com/comedorsolidario/dao/PuntoAcopioDAO.java](../src/java/com/comedorsolidario/dao/PuntoAcopioDAO.java)
- [src/java/com/comedorsolidario/dao/RutaRecojoDAO.java](../src/java/com/comedorsolidario/dao/RutaRecojoDAO.java)
- [src/java/com/comedorsolidario/dao/SolicitudRecojoDAO.java](../src/java/com/comedorsolidario/dao/SolicitudRecojoDAO.java)
- [src/java/com/comedorsolidario/model/DetalleRutaRecojo.java](../src/java/com/comedorsolidario/model/DetalleRutaRecojo.java)
- [src/java/com/comedorsolidario/model/PuntoAcopio.java](../src/java/com/comedorsolidario/model/PuntoAcopio.java)
- [src/java/com/comedorsolidario/model/RutaRecojo.java](../src/java/com/comedorsolidario/model/RutaRecojo.java)
- [src/java/com/comedorsolidario/model/SolicitudRecojo.java](../src/java/com/comedorsolidario/model/SolicitudRecojo.java)
- [src/java/com/comedorsolidario/service/logistica/LogisticaService.java](../src/java/com/comedorsolidario/service/logistica/LogisticaService.java)
- [verificacion/InspeccionarEsquema.java](../verificacion/InspeccionarEsquema.java)
- [verificacion/verificar_http_t3.py](../verificacion/verificar_http_t3.py)
- [verificacion/VerificarLogistica.java](../verificacion/VerificarLogistica.java)
- [web/assets/js/logistica.js](../web/assets/js/logistica.js)
- [web/views/logistica/alertas.jsp](../web/views/logistica/alertas.jsp)
- [web/views/logistica/error.jsp](../web/views/logistica/error.jsp)
- [web/views/logistica/historial.jsp](../web/views/logistica/historial.jsp)
- [web/views/logistica/puntos.jsp](../web/views/logistica/puntos.jsp)
- [web/views/logistica/recojos.jsp](../web/views/logistica/recojos.jsp)
- [web/views/logistica/rutas.jsp](../web/views/logistica/rutas.jsp)

## Archivos modificados

- [README.md](../README.md)
- [docs/clases-completas.puml](../docs/clases-completas.puml)
- [docs/modelo-clases.puml](../docs/modelo-clases.puml)
- [pom.xml](../pom.xml)
- [src/java/com/comedorsolidario/controller/AdminDashboardServlet.java](../src/java/com/comedorsolidario/controller/AdminDashboardServlet.java)
- [src/java/com/comedorsolidario/controller/AdminDonacionServlet.java](../src/java/com/comedorsolidario/controller/AdminDonacionServlet.java)
- [src/java/com/comedorsolidario/controller/AdminUsuarioServlet.java](../src/java/com/comedorsolidario/controller/AdminUsuarioServlet.java)
- [src/java/com/comedorsolidario/controller/DonacionServlet.java](../src/java/com/comedorsolidario/controller/DonacionServlet.java)
- [src/java/com/comedorsolidario/controller/PerfilServlet.java](../src/java/com/comedorsolidario/controller/PerfilServlet.java)
- [src/java/com/comedorsolidario/controller/RegistroServlet.java](../src/java/com/comedorsolidario/controller/RegistroServlet.java)
- [src/java/com/comedorsolidario/dao/DonacionDAO.java](../src/java/com/comedorsolidario/dao/DonacionDAO.java)
- [src/java/com/comedorsolidario/dao/JdbcDAO.java](../src/java/com/comedorsolidario/dao/JdbcDAO.java)
- [src/java/com/comedorsolidario/dao/UsuarioDAO.java](../src/java/com/comedorsolidario/dao/UsuarioDAO.java)
- [src/java/com/comedorsolidario/model/Donacion.java](../src/java/com/comedorsolidario/model/Donacion.java)
- [src/java/com/comedorsolidario/util/ValidacionUtil.java](../src/java/com/comedorsolidario/util/ValidacionUtil.java)
- [verificacion/VerificarEliminacion.java](../verificacion/VerificarEliminacion.java)
- [verificacion/VerificarRubrica.java](../verificacion/VerificarRubrica.java)
- [web/WEB-INF/web.xml](../web/WEB-INF/web.xml)
- [web/assets/js/main.js](../web/assets/js/main.js)
- [web/views/admin/dashboard.jsp](../web/views/admin/dashboard.jsp)
- [web/views/admin/donaciones/detalle.jsp](../web/views/admin/donaciones/detalle.jsp)
- [web/views/admin/donaciones/listar.jsp](../web/views/admin/donaciones/listar.jsp)
- [web/views/admin/usuarios/detalle.jsp](../web/views/admin/usuarios/detalle.jsp)
- [web/views/admin/usuarios/formulario.jsp](../web/views/admin/usuarios/formulario.jsp)
- [web/views/admin/usuarios/listar.jsp](../web/views/admin/usuarios/listar.jsp)
- [web/views/auth/registro.jsp](../web/views/auth/registro.jsp)
- [web/views/common/badge.jspf](../web/views/common/badge.jspf)
- [web/views/common/footer.jspf](../web/views/common/footer.jspf)
- [web/views/common/header.jspf](../web/views/common/header.jspf)
- [web/views/donante/mis-donaciones.jsp](../web/views/donante/mis-donaciones.jsp)
- [web/views/donante/perfil.jsp](../web/views/donante/perfil.jsp)
- [web/views/donante/realizar-donacion.jsp](../web/views/donante/realizar-donacion.jsp)

## Responsabilidad de los cambios

- Modelos: cuatro entidades logísticas nuevas y ampliación de Donacion, conservando los modelos anteriores.
- DAO: consultas parametrizadas, transacciones, cupo concurrente, historial, relaciones y estados de logística sobre ConexionBD.
- Controladores: integración Spring MVC limitada a los nuevos módulos; Servlets previos adaptados para contacto, modalidad y decisiones justificadas.
- Vistas: formularios de acopio, solicitudes y rutas, historial y alertas; navegación y formularios anteriores ampliados.
- JavaScript: campos condicionales por modalidad y corrección del botón elegido en el diálogo de confirmación.
- Base: migración T3 manual e independiente, sin ejecución automática en el arranque.
- Documentación: guía de módulos y ejecución, resultados de pruebas, inventario y dos diagramas PlantUML actualizados.
- Verificación: pruebas reales JDBC/HTTP separadas de la base habitual; correspondencia del UML ampliada a diez modelos.

Dependencia directa nueva de aplicación: org.springframework:spring-webmvc:6.2.19. PlantUML se descargó únicamente como herramienta de verificación; no se agregó al pom.xml.

Los cambios permanecen locales en la rama diego/nuevas-mejoras. No se realizó commit, push ni merge.
