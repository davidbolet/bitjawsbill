-- Insertar Organización
INSERT INTO organizaciones (nombre, tipo_cliente, nif, email_contacto) 
VALUES ('Mi Empresa', 1, 'B12345678', 'contacto@miempresa.com');

-- Insertar Usuario
INSERT INTO usuarios (username, password, email, activo)
VALUES ('admin', '$2a$10$rDkPvvAFV8c3a1zqX1qQeO1qX1qX1qX1qX1qX1qX1qX1qX1qX1qX', 'admin@example.com', true);

-- Insertar Clientes
INSERT INTO clientes (tipo_cliente, nombre, apellido, email, telefono, direccion, ciudad, pais, nif, organizacion_id) 
VALUES 
(0, 'Juan', 'Pérez', 'juan@email.com', '123456789', 'Calle Principal 1', 'Madrid', 'España', '12345678A', 1),
(1, 'Empresa ABC', NULL, 'info@empresaabc.com', '987654321', 'Avenida Central 100', 'Barcelona', 'España', 'B87654321', 1);
(2, 'Mi Empresa', NULL, 'contacto@miempresa.com', '987654321', 'Avenida Central 100', 'Barcelona', 'España', 'B12345678', 1);

UPDATE organizaciones SET cliente_id = 2 WHERE id = 1;

-- Insertar Formas de Pago
INSERT INTO formas_pago (codigo, descripcion, organizacion_id) 
VALUES 
('TRANSFERENCIA', 'Transferencia Bancaria', 1),
('EFECTIVO', 'Pago en Efectivo', 1),
('TARJETA', 'Pago con Tarjeta', 1);

-- Comentamos la inserción de cuentas bancarias para evitar problemas en las pruebas
-- INSERT INTO cuentas_bancarias (entidad, iban, swift_bic, cuenta_predeterminada, organizacion_id, cliente_id) 
-- VALUES 
-- ('Banco Popular', 'ES9121000418450200051332', 'CAIXESBBXXX', true, 1, 1),
-- ('Santander', 'ES7921000813610123456789', 'BSCHESMMXXX', true, 1, 2); 