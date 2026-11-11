-- T3: ejecutar MANUALMENTE en Workbench, con aplicación detenida y respaldo previo.
-- Compatible con MySQL 8.4.10 inspeccionado. No ejecutar de nuevo el instalador inicial.
-- DDL hace commit implícito. Cada ampliación comprueba su existencia; se puede reanudar.
USE comedor_solidario;
CREATE TABLE IF NOT EXISTS puntos_acopio (
 id INT AUTO_INCREMENT PRIMARY KEY, nombre VARCHAR(120) NOT NULL,
 direccion VARCHAR(200) NOT NULL, provincia VARCHAR(100) NOT NULL, distrito VARCHAR(100) NOT NULL,
 zona VARCHAR(80) NOT NULL, referencia VARCHAR(250) NOT NULL, horarios VARCHAR(200) NOT NULL,
 responsable VARCHAR(120) NOT NULL, telefono VARCHAR(20) NOT NULL,
 estado ENUM('ACTIVO','INACTIVO') NOT NULL DEFAULT 'ACTIVO',
 fecha_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
 INDEX idx_acopio_ubicacion(estado,provincia,distrito)
) ENGINE=InnoDB;
ALTER TABLE donaciones MODIFY estado ENUM('PENDIENTE','ACEPTADA','ENTREGADA','PROGRAMADA','RECHAZADA','CANCELADA','INCUMPLIDA') NOT NULL DEFAULT 'PENDIENTE';
SET @t3_sql=IF(EXISTS(SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='donaciones' AND COLUMN_NAME='modalidad'),'SELECT 1','ALTER TABLE donaciones ADD COLUMN modalidad ENUM(''DIRECTA'',''ACOPIO'',''RECOJO'') NOT NULL DEFAULT ''DIRECTA''');
PREPARE t3_stmt FROM @t3_sql; EXECUTE t3_stmt; DEALLOCATE PREPARE t3_stmt;
SET @t3_sql=IF(EXISTS(SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='donaciones' AND COLUMN_NAME='punto_acopio_id'),'SELECT 1','ALTER TABLE donaciones ADD COLUMN punto_acopio_id INT NULL');
PREPARE t3_stmt FROM @t3_sql; EXECUTE t3_stmt; DEALLOCATE PREPARE t3_stmt;
SET @t3_sql=IF(EXISTS(SELECT 1 FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='donaciones' AND COLUMN_NAME='motivo'),'SELECT 1','ALTER TABLE donaciones ADD COLUMN motivo VARCHAR(1000) NOT NULL DEFAULT ''''');
PREPARE t3_stmt FROM @t3_sql; EXECUTE t3_stmt; DEALLOCATE PREPARE t3_stmt;
SET @t3_sql=IF(EXISTS(SELECT 1 FROM information_schema.TABLE_CONSTRAINTS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='donaciones' AND CONSTRAINT_NAME='fk_donacion_acopio'),'SELECT 1','ALTER TABLE donaciones ADD CONSTRAINT fk_donacion_acopio FOREIGN KEY(punto_acopio_id) REFERENCES puntos_acopio(id)');
PREPARE t3_stmt FROM @t3_sql; EXECUTE t3_stmt; DEALLOCATE PREPARE t3_stmt;
SET @t3_sql=IF(EXISTS(SELECT 1 FROM information_schema.STATISTICS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='donaciones' AND INDEX_NAME='idx_donador_cupo'),'SELECT 1','CREATE INDEX idx_donador_cupo ON donaciones(donante_id,activo,estado)');
PREPARE t3_stmt FROM @t3_sql; EXECUTE t3_stmt; DEALLOCATE PREPARE t3_stmt;

CREATE TABLE IF NOT EXISTS solicitudes_recojo (
 id INT AUTO_INCREMENT PRIMARY KEY, donacion_id INT NOT NULL UNIQUE,
 direccion VARCHAR(200) NOT NULL, provincia VARCHAR(100) NOT NULL, distrito VARCHAR(100) NOT NULL,
 zona VARCHAR(80) NOT NULL, referencia VARCHAR(250) NOT NULL, fecha_preferida DATE NOT NULL,
 horario_preferido VARCHAR(200) NOT NULL, telefono VARCHAR(20) NOT NULL,
 fecha_programada DATE NULL, horario_programado VARCHAR(200) NOT NULL DEFAULT '',
 responsable VARCHAR(120) NOT NULL DEFAULT '', observaciones VARCHAR(1000) NOT NULL DEFAULT '',
 estado ENUM('SOLICITADO','PROGRAMADO','RECOGIDO','COMPLETADO','RECHAZADO','CANCELADO','INCUMPLIDO') NOT NULL DEFAULT 'SOLICITADO',
 fecha_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
 fecha_actualizacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 CONSTRAINT fk_recojo_donacion FOREIGN KEY(donacion_id) REFERENCES donaciones(id),
 INDEX idx_recojo_filtros(estado,distrito,fecha_programada)
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS rutas_recojo (
 id INT AUTO_INCREMENT PRIMARY KEY, fecha DATE NOT NULL, distrito VARCHAR(100) NOT NULL,
 horario VARCHAR(200) NOT NULL, responsable VARCHAR(120) NOT NULL, observaciones VARCHAR(1000) NOT NULL DEFAULT '',
 estado ENUM('PLANIFICADA','EN_PROCESO','COMPLETADA','CANCELADA') NOT NULL DEFAULT 'PLANIFICADA',
 fecha_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, INDEX idx_ruta_fecha(fecha,distrito,estado)
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS detalle_ruta_recojo (
 ruta_id INT NOT NULL, solicitud_id INT NOT NULL, orden INT NOT NULL,
 activa BOOLEAN NOT NULL DEFAULT TRUE,
 solicitud_activa INT GENERATED ALWAYS AS (CASE WHEN activa THEN solicitud_id ELSE NULL END) STORED,
 fecha_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
 PRIMARY KEY(ruta_id,solicitud_id), UNIQUE KEY uq_solicitud_ruta_activa(solicitud_activa),
 CONSTRAINT fk_parada_ruta FOREIGN KEY(ruta_id) REFERENCES rutas_recojo(id),
 CONSTRAINT fk_parada_solicitud FOREIGN KEY(solicitud_id) REFERENCES solicitudes_recojo(id),
 CONSTRAINT chk_parada_orden CHECK(orden>0)
) ENGINE=InnoDB;
-- Auditoría independiente de la vigencia: desactivar no oculta los incumplimientos.
CREATE TABLE IF NOT EXISTS historial_donacion (
 id INT AUTO_INCREMENT PRIMARY KEY, donacion_id INT NOT NULL, administrador_id INT NOT NULL,
 estado VARCHAR(30) NOT NULL, motivo VARCHAR(1000) NOT NULL,
 fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
 FOREIGN KEY(donacion_id) REFERENCES donaciones(id), FOREIGN KEY(administrador_id) REFERENCES usuarios(id),
 INDEX idx_historial_donacion(donacion_id,fecha)
) ENGINE=InnoDB;
CREATE TABLE IF NOT EXISTS historial_usuario (
 id INT AUTO_INCREMENT PRIMARY KEY, usuario_id INT NOT NULL, administrador_id INT NOT NULL,
 estado VARCHAR(20) NOT NULL, motivo VARCHAR(1000) NOT NULL,
 fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
 FOREIGN KEY(usuario_id) REFERENCES usuarios(id), FOREIGN KEY(administrador_id) REFERENCES usuarios(id)
) ENGINE=InnoDB;
-- No se alteran credenciales ni permisos. La aplicación solo necesita SELECT/INSERT/UPDATE/DELETE.
