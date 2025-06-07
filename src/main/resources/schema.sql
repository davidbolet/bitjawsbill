-- Tabla de organizaciones
CREATE TABLE IF NOT EXISTS organizaciones (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    tipo_cliente VARCHAR(50) NOT NULL,
    nif VARCHAR(20),
    email_contacto VARCHAR(255)
);

-- Tabla de roles
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE
);

-- Tabla de clientes
CREATE TABLE IF NOT EXISTS clientes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    organizacion_id BIGINT NOT NULL,
    tipo_cliente INT NOT NULL,
    nombre VARCHAR(255) NOT NULL,
    apellido VARCHAR(255),
    email VARCHAR(255),
    telefono VARCHAR(50),
    direccion VARCHAR(255),
    numero VARCHAR(20),
    piso VARCHAR(20),
    puerta VARCHAR(20),
    codigo_postal VARCHAR(20),
    ciudad VARCHAR(100),
    provincia VARCHAR(100),
    pais VARCHAR(100),
    nif VARCHAR(50),
    cif VARCHAR(50),
    razon_social VARCHAR(255),
    nombre_comercial VARCHAR(255),
    persona_contacto VARCHAR(255),
    cargo_contacto VARCHAR(255),
    telefono_contacto VARCHAR(50),
    email_contacto VARCHAR(255),
    observaciones TEXT,
    activo BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (organizacion_id) REFERENCES organizaciones(id)
);

-- Tabla de usuarios
CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    nombre VARCHAR(255),
    apellidos VARCHAR(255),
    activo BOOLEAN DEFAULT TRUE,
    organizacion_id BIGINT,
    FOREIGN KEY (organizacion_id) REFERENCES organizaciones(id)
);

-- Tabla de relación usuario-roles
CREATE TABLE IF NOT EXISTS usuario_roles (
    usuario_id BIGINT,
    rol_id BIGINT,
    PRIMARY KEY (usuario_id, rol_id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    FOREIGN KEY (rol_id) REFERENCES roles(id)
);

-- Tabla de facturas
CREATE TABLE IF NOT EXISTS facturas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    organizacion_id BIGINT,
    serie VARCHAR(255),
    numero_factura VARCHAR(255),
    fecha_emision DATE,
    descripcion VARCHAR(255),
    tipo_factura VARCHAR(50),
    estado VARCHAR(50),
    es_recibida BOOLEAN,
    moneda VARCHAR(3),
    total_base DECIMAL(19,2),
    total_impuestos DECIMAL(19,2),
    total_factura DECIMAL(19,2),
    cliente_id BIGINT,
    emisor_id BIGINT,
    factura_rectificada_id BIGINT,
    fecha_creacion TIMESTAMP,
    FOREIGN KEY (organizacion_id) REFERENCES organizaciones(id),
    FOREIGN KEY (cliente_id) REFERENCES clientes(id),
    FOREIGN KEY (emisor_id) REFERENCES clientes(id),
    FOREIGN KEY (factura_rectificada_id) REFERENCES facturas(id),
    UNIQUE KEY uk_factura_organizacion_serie_numero (organizacion_id, serie, numero_factura)
);

-- Insertar roles por defecto
INSERT INTO roles (nombre) VALUES 
    ('ROLE_ADMIN'),
    ('ROLE_MANAGER'),
    ('ROLE_USER')
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre); 