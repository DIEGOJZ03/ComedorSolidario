-- Comedor Solidario. Datos FICTICIOS para demostración académica.
-- Ejecutar una vez sobre una instalación nueva. No elimina bases existentes.
CREATE DATABASE IF NOT EXISTS comedor_solidario CHARACTER
SET
    utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'comedor_app'@'localhost' IDENTIFIED BY '';
GRANT ALL PRIVILEGES ON *.* TO 'comedor_app'@'localhost' WITH GRANT OPTION;
FLUSH PRIVILEGES;

USE comedor_solidario;

CREATE TABLE
    usuarios (
        id INT AUTO_INCREMENT PRIMARY KEY,
        nombre VARCHAR(80) NOT NULL,
        apellido VARCHAR(80) NOT NULL,
        email VARCHAR(150) NOT NULL UNIQUE,
        password_hash VARCHAR(255) NOT NULL,
        telefono VARCHAR(20) NOT NULL DEFAULT '',
        rol ENUM ('ADMIN', 'DONADOR') NOT NULL DEFAULT 'DONADOR',
        estado ENUM ('ACTIVO', 'INACTIVO') NOT NULL DEFAULT 'ACTIVO',
        fecha_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
    ) ENGINE = InnoDB;

CREATE TABLE
    comedores (
        id INT AUTO_INCREMENT PRIMARY KEY,
        nombre VARCHAR(120) NOT NULL,
        descripcion VARCHAR(1500) NOT NULL,
        direccion VARCHAR(200) NOT NULL,
        zona VARCHAR(80) NOT NULL,
        distrito VARCHAR(100) NOT NULL,
        provincia VARCHAR(100) NOT NULL,
        responsable VARCHAR(120) NOT NULL,
        telefono VARCHAR(20) NOT NULL,
        estado ENUM ('ACTIVO', 'INACTIVO') NOT NULL DEFAULT 'ACTIVO',
        fecha_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
        INDEX idx_ubicacion (provincia, distrito, zona)
    ) ENGINE = InnoDB;

CREATE TABLE
    necesidades (
        id INT AUTO_INCREMENT PRIMARY KEY,
        comedor_id INT NOT NULL,
        nombre VARCHAR(100) NOT NULL,
        descripcion VARCHAR(1000) NOT NULL,
        cantidad_necesaria DECIMAL(12, 2) NOT NULL,
        unidad VARCHAR(30) NOT NULL,
        prioridad ENUM ('ALTA', 'MEDIA', 'BAJA') NOT NULL,
        estado ENUM ('ACTIVA', 'INACTIVA') NOT NULL DEFAULT 'ACTIVA',
        CONSTRAINT fk_necesidad_comedor FOREIGN KEY (comedor_id) REFERENCES comedores (id),
        CONSTRAINT chk_necesidad_cantidad CHECK (cantidad_necesaria > 0)
    ) ENGINE = InnoDB;

CREATE TABLE
    donaciones (
        id INT AUTO_INCREMENT PRIMARY KEY,
        donante_id INT NOT NULL,
        comedor_id INT NOT NULL,
        tipo ENUM ('ALIMENTOS', 'UTENSILIOS', 'DINERO', 'OTROS') NOT NULL,
        cantidad DECIMAL(12, 2) NOT NULL,
        unidad VARCHAR(30) NOT NULL,
        descripcion VARCHAR(1000) NOT NULL,
        estado ENUM ('PENDIENTE', 'ACEPTADA', 'ENTREGADA') NOT NULL DEFAULT 'PENDIENTE',
        fecha_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
        fecha_entrega DATETIME NULL,
        CONSTRAINT fk_donacion_usuario FOREIGN KEY (donante_id) REFERENCES usuarios (id),
        CONSTRAINT fk_donacion_comedor FOREIGN KEY (comedor_id) REFERENCES comedores (id),
        CONSTRAINT chk_donacion_cantidad CHECK (cantidad > 0),
        CONSTRAINT chk_entrega CHECK (
            (
                estado = 'ENTREGADA'
                AND fecha_entrega IS NOT NULL
            )
            OR (
                estado <> 'ENTREGADA'
                AND fecha_entrega IS NULL
            )
        ),
        INDEX idx_donante_fecha (donante_id, fecha_registro),
        INDEX idx_estado (estado)
    ) ENGINE = InnoDB;

INSERT INTO
    usuarios (
        nombre,
        apellido,
        email,
        password_hash,
        telefono,
        rol
    )
VALUES
    (
        'Ana',
        'Administradora',
        'admin@comedorsolidario.pe',
        '600000:HG22SUYvZXeDO/0HLX7U6w==:pk/Nv4tJDPIYb6un2y3whxVTX5aBLfkQ9QWxgXrehbY=',
        '900000000',
        'ADMIN'
    );

INSERT INTO
    usuarios (
        nombre,
        apellido,
        email,
        password_hash,
        telefono,
        rol
    )
VALUES
    (
        'Diego',
        'Donador',
        'donador@comedorsolidario.pe',
        '600000:kCWZ9dwa6dCubZxi+otiTg==:vzjoqYSPh5fu83hXQx+o0pzKafEBEDNfAvuub1cb/dc=',
        '900000001',
        'DONADOR'
    );

INSERT INTO
    usuarios (
        nombre,
        apellido,
        email,
        password_hash,
        telefono,
        rol
    )
VALUES
    (
        'Lucía',
        'Pérez',
        'lucia@example.com',
        '600000:c2yg875TREEQBy9l40CYTA==:rzNi3+Yjxaa6TIM0GUDN2DXgqkllMczSm1xFUf6cEPs=',
        '900000002',
        'DONADOR'
    );

INSERT INTO
    usuarios (
        nombre,
        apellido,
        email,
        password_hash,
        telefono,
        rol
    )
VALUES
    (
        'Carlos',
        'Ramos',
        'carlos@example.com',
        '600000:Mz1ttw14Enw0yOG6jYFa+Q==:Wm2RpbHVVfqSY/CIfDmCBqnTHsJJR+sTzBHFd/ErlK8=',
        '900000003',
        'DONADOR'
    );

INSERT INTO
    comedores (
        nombre,
        descripcion,
        direccion,
        zona,
        distrito,
        provincia,
        responsable,
        telefono
    )
VALUES
    (
        'Olla Común Santa Rosa',
        'Espacio comunitario de demostración que organiza alimentos para familias de la zona. Tu aporte ayuda a mantener las comidas diarias.',
        'Av. Solidaridad 100 (dirección ficticia)',
        'Este',
        'San Juan de Lurigancho',
        'Lima',
        'Rosa Salazar',
        '900111001'
    );

INSERT INTO
    necesidades (
        comedor_id,
        nombre,
        descripcion,
        cantidad_necesaria,
        unidad,
        prioridad
    )
VALUES
    (
        1,
        'Arroz',
        'Productos cerrados y dentro de su fecha de consumo.',
        50,
        'kg',
        'ALTA'
    );

INSERT INTO
    necesidades (
        comedor_id,
        nombre,
        descripcion,
        cantidad_necesaria,
        unidad,
        prioridad
    )
VALUES
    (
        1,
        'Aceite',
        'Productos cerrados y dentro de su fecha de consumo.',
        20,
        'litros',
        'MEDIA'
    );

INSERT INTO
    comedores (
        nombre,
        descripcion,
        direccion,
        zona,
        distrito,
        provincia,
        responsable,
        telefono
    )
VALUES
    (
        'Comedor Nuevo Amanecer',
        'Espacio comunitario de demostración que organiza alimentos para familias de la zona. Tu aporte ayuda a mantener las comidas diarias.',
        'Av. Solidaridad 200 (dirección ficticia)',
        'Norte',
        'Comas',
        'Lima',
        'Elena Torres',
        '900111002'
    );

INSERT INTO
    necesidades (
        comedor_id,
        nombre,
        descripcion,
        cantidad_necesaria,
        unidad,
        prioridad
    )
VALUES
    (
        2,
        'Arroz',
        'Productos cerrados y dentro de su fecha de consumo.',
        50,
        'kg',
        'ALTA'
    );

INSERT INTO
    necesidades (
        comedor_id,
        nombre,
        descripcion,
        cantidad_necesaria,
        unidad,
        prioridad
    )
VALUES
    (
        2,
        'Aceite',
        'Productos cerrados y dentro de su fecha de consumo.',
        20,
        'litros',
        'MEDIA'
    );

INSERT INTO
    comedores (
        nombre,
        descripcion,
        direccion,
        zona,
        distrito,
        provincia,
        responsable,
        telefono
    )
VALUES
    (
        'Manos que Alimentan',
        'Espacio comunitario de demostración que organiza alimentos para familias de la zona. Tu aporte ayuda a mantener las comidas diarias.',
        'Av. Solidaridad 300 (dirección ficticia)',
        'Sur',
        'Villa El Salvador',
        'Lima',
        'María Rojas',
        '900111003'
    );

INSERT INTO
    necesidades (
        comedor_id,
        nombre,
        descripcion,
        cantidad_necesaria,
        unidad,
        prioridad
    )
VALUES
    (
        3,
        'Arroz',
        'Productos cerrados y dentro de su fecha de consumo.',
        50,
        'kg',
        'ALTA'
    );

INSERT INTO
    necesidades (
        comedor_id,
        nombre,
        descripcion,
        cantidad_necesaria,
        unidad,
        prioridad
    )
VALUES
    (
        3,
        'Aceite',
        'Productos cerrados y dentro de su fecha de consumo.',
        20,
        'litros',
        'MEDIA'
    );

INSERT INTO
    comedores (
        nombre,
        descripcion,
        direccion,
        zona,
        distrito,
        provincia,
        responsable,
        telefono
    )
VALUES
    (
        'Comedor Esperanza',
        'Espacio comunitario de demostración que organiza alimentos para familias de la zona. Tu aporte ayuda a mantener las comidas diarias.',
        'Av. Solidaridad 400 (dirección ficticia)',
        'Centro',
        'Cercado de Lima',
        'Lima',
        'José Medina',
        '900111004'
    );

INSERT INTO
    necesidades (
        comedor_id,
        nombre,
        descripcion,
        cantidad_necesaria,
        unidad,
        prioridad
    )
VALUES
    (
        4,
        'Arroz',
        'Productos cerrados y dentro de su fecha de consumo.',
        50,
        'kg',
        'ALTA'
    );

INSERT INTO
    necesidades (
        comedor_id,
        nombre,
        descripcion,
        cantidad_necesaria,
        unidad,
        prioridad
    )
VALUES
    (
        4,
        'Aceite',
        'Productos cerrados y dentro de su fecha de consumo.',
        20,
        'litros',
        'MEDIA'
    );

INSERT INTO
    comedores (
        nombre,
        descripcion,
        direccion,
        zona,
        distrito,
        provincia,
        responsable,
        telefono
    )
VALUES
    (
        'Unidos por el Callao',
        'Espacio comunitario de demostración que organiza alimentos para familias de la zona. Tu aporte ayuda a mantener las comidas diarias.',
        'Av. Solidaridad 500 (dirección ficticia)',
        'Oeste',
        'Callao',
        'Callao',
        'Carmen Ruiz',
        '900111005'
    );

INSERT INTO
    necesidades (
        comedor_id,
        nombre,
        descripcion,
        cantidad_necesaria,
        unidad,
        prioridad
    )
VALUES
    (
        5,
        'Arroz',
        'Productos cerrados y dentro de su fecha de consumo.',
        50,
        'kg',
        'ALTA'
    );

INSERT INTO
    necesidades (
        comedor_id,
        nombre,
        descripcion,
        cantidad_necesaria,
        unidad,
        prioridad
    )
VALUES
    (
        5,
        'Aceite',
        'Productos cerrados y dentro de su fecha de consumo.',
        20,
        'litros',
        'MEDIA'
    );

INSERT INTO
    donaciones (
        donante_id,
        comedor_id,
        tipo,
        cantidad,
        unidad,
        descripcion,
        estado,
        fecha_entrega
    )
VALUES
    (
        2,
        1,
        'ALIMENTOS',
        10,
        'kg',
        'Arroz en bolsas selladas.',
        'PENDIENTE',
        NULL
    ),
    (
        2,
        2,
        'ALIMENTOS',
        5,
        'litros',
        'Aceite vegetal.',
        'ACEPTADA',
        NULL
    ),
    (
        2,
        3,
        'UTENSILIOS',
        12,
        'unidades',
        'Platos nuevos.',
        'ENTREGADA',
        CURRENT_TIMESTAMP
    ),
    (
        3,
        4,
        'ALIMENTOS',
        15,
        'kg',
        'Menestras.',
        'PENDIENTE',
        NULL
    ),
    (
        4,
        5,
        'ALIMENTOS',
        8,
        'unidades',
        'Conservas selladas.',
        'ACEPTADA',
        NULL
    );