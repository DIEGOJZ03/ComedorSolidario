-- Aplicar UNA VEZ sobre la base existente; no elimina registros.
USE comedor_solidario;
CREATE TABLE productos (
 id INT AUTO_INCREMENT PRIMARY KEY,
 nombre VARCHAR(100) NOT NULL,
 descripcion VARCHAR(1000) NOT NULL,
 unidad VARCHAR(30) NOT NULL,
 estado ENUM('ACTIVO','INACTIVO') NOT NULL DEFAULT 'ACTIVO'
) ENGINE=InnoDB;
CREATE TABLE campanias (
 id INT AUTO_INCREMENT PRIMARY KEY,
 comedor_id INT NOT NULL,
 nombre VARCHAR(100) NOT NULL,
 descripcion VARCHAR(1000) NOT NULL,
 fecha_inicio DATE NOT NULL,
 fecha_fin DATE NOT NULL,
 estado ENUM('ACTIVO','INACTIVO') NOT NULL DEFAULT 'ACTIVO',
 CONSTRAINT fk_campania_comedor FOREIGN KEY(comedor_id) REFERENCES comedores(id),
 CONSTRAINT chk_campania_fechas CHECK(fecha_fin >= fecha_inicio)
) ENGINE=InnoDB;
ALTER TABLE necesidades ADD COLUMN producto_id INT NULL,
 ADD CONSTRAINT fk_necesidad_producto FOREIGN KEY(producto_id) REFERENCES productos(id);
ALTER TABLE donaciones ADD COLUMN activo BOOLEAN NOT NULL DEFAULT TRUE;
