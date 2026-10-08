DROP DATABASE IF EXISTS crm_servitec;
CREATE DATABASE crm_servitec CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE crm_servitec;

CREATE TABLE rol (
    id_rol INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(30) NOT NULL UNIQUE,
    descripcion VARCHAR(100)
);

CREATE TABLE usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(30) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    nombre_completo VARCHAR(100) NOT NULL,
    correo VARCHAR(100),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    id_rol INT NOT NULL,
    CONSTRAINT fk_usuario_rol FOREIGN KEY (id_rol) REFERENCES rol(id_rol)
);

CREATE TABLE cliente (
    id_cliente INT AUTO_INCREMENT PRIMARY KEY,
    tipo_documento VARCHAR(10) NOT NULL,
    numero_documento VARCHAR(15) NOT NULL UNIQUE,
    razon_social VARCHAR(120) NOT NULL,
    contacto VARCHAR(100),
    telefono VARCHAR(20),
    correo VARCHAR(100),
    direccion VARCHAR(150),
    estado VARCHAR(10) NOT NULL DEFAULT 'ACTIVO',
    fecha_registro DATE NOT NULL
);

CREATE TABLE producto (
    id_producto INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(15) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    tipo VARCHAR(10) NOT NULL,
    marca VARCHAR(50),
    precio_mensual DECIMAL(10,2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    estado VARCHAR(10) NOT NULL DEFAULT 'ACTIVO'
);

CREATE TABLE contrato (
    id_contrato INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT NOT NULL,
    id_usuario INT NOT NULL,
    fecha_registro DATE NOT NULL,
    fecha_inicio DATE NOT NULL,
    meses INT NOT NULL,
    fecha_fin DATE NOT NULL,
    monto_mensual DECIMAL(10,2) NOT NULL,
    estado VARCHAR(12) NOT NULL DEFAULT 'VIGENTE',
    observacion VARCHAR(200),
    CONSTRAINT fk_contrato_cliente FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente),
    CONSTRAINT fk_contrato_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
);

CREATE TABLE contrato_detalle (
    id_detalle INT AUTO_INCREMENT PRIMARY KEY,
    id_contrato INT NOT NULL,
    id_producto INT NOT NULL,
    cantidad INT NOT NULL,
    precio_mensual DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_detalle_contrato FOREIGN KEY (id_contrato) REFERENCES contrato(id_contrato),
    CONSTRAINT fk_detalle_producto FOREIGN KEY (id_producto) REFERENCES producto(id_producto)
);

CREATE TABLE cuota (
    id_cuota INT AUTO_INCREMENT PRIMARY KEY,
    id_contrato INT NOT NULL,
    numero INT NOT NULL,
    fecha_vencimiento DATE NOT NULL,
    monto DECIMAL(10,2) NOT NULL,
    estado VARCHAR(10) NOT NULL DEFAULT 'PENDIENTE',
    fecha_pago DATE,
    CONSTRAINT fk_cuota_contrato FOREIGN KEY (id_contrato) REFERENCES contrato(id_contrato)
);

INSERT INTO rol (nombre, descripcion) VALUES
('ADMINISTRADOR', 'Acceso total al sistema'),
('ASESOR', 'Gestiona clientes, contratos y cobranzas');

INSERT INTO usuario (username, password, nombre_completo, correo, activo, id_rol) VALUES
('admin', '$2a$10$DsydiRivFrmaMrdrprcIO.syMkAbpzfbPIeHfbqnJcbGijbGMnHtG', 'Jaime Adán Muñoz Cuzco', 'admin@servitec.pe', TRUE, 1),
('asesor', '$2a$10$LmsZp8CcT/RBo74RHJD3eOavG1S3s7zDx219h5Lg4SxBHSi.48i8W', 'Lucía Fernández Ríos', 'asesor@servitec.pe', TRUE, 2);

INSERT INTO cliente (tipo_documento, numero_documento, razon_social, contacto, telefono, correo, direccion, estado, fecha_registro) VALUES
('RUC', '20512345671', 'Estudio Contable Ramírez S.A.C.', 'Carlos Ramírez', '987654321', 'contacto@ramirezcontable.pe', 'Av. Arequipa 1450, Lince', 'ACTIVO', '2026-01-10'),
('RUC', '20601234567', 'Clínica Dental Sonrisas E.I.R.L.', 'Patricia Vega', '956123478', 'admin@sonrisas.pe', 'Jr. Las Begonias 230, San Isidro', 'ACTIVO', '2026-02-03'),
('RUC', '20498765432', 'Inversiones Torres y Hnos. S.A.', 'Miguel Torres', '944556677', 'mtorres@torreshnos.pe', 'Av. Javier Prado Este 3200, San Borja', 'ACTIVO', '2026-03-15'),
('DNI', '45678912', 'Rosa Quispe Mamani', 'Rosa Quispe', '912345678', 'rquispe@gmail.com', 'Calle Los Pinos 120, Surco', 'ACTIVO', '2026-04-20'),
('RUC', '20587412369', 'Colegio Nuevo Horizonte S.A.C.', 'Ana Salazar', '933221144', 'direccion@nuevohorizonte.edu.pe', 'Av. Universitaria 5600, Los Olivos', 'ACTIVO', '2026-05-08');

INSERT INTO producto (codigo, nombre, tipo, marca, precio_mensual, stock, estado) VALUES
('EQ-001', 'PC de escritorio Core i5 16GB', 'EQUIPO', 'Lenovo', 120.00, 7, 'ACTIVO'),
('EQ-002', 'Laptop Core i7 16GB', 'EQUIPO', 'HP', 180.00, 8, 'ACTIVO'),
('EQ-003', 'Impresora multifuncional láser', 'EQUIPO', 'Brother', 90.00, 5, 'ACTIVO'),
('EQ-004', 'Monitor 24 pulgadas', 'EQUIPO', 'LG', 35.00, 15, 'ACTIVO'),
('SV-001', 'Soporte técnico mensual', 'SERVICIO', 'ServiTec', 250.00, 0, 'ACTIVO'),
('SV-002', 'Licencia Microsoft 365 Business', 'SERVICIO', 'Microsoft', 45.00, 0, 'ACTIVO'),
('SV-003', 'Backup en la nube 500GB', 'SERVICIO', 'ServiTec', 60.00, 0, 'ACTIVO');

INSERT INTO contrato (id_cliente, id_usuario, fecha_registro, fecha_inicio, meses, fecha_fin, monto_mensual, estado, observacion) VALUES
(1, 2, '2026-04-28', '2026-05-01', 6, '2026-10-31', 610.00, 'VIGENTE', 'Equipos para el área contable'),
(2, 2, '2026-06-27', '2026-07-01', 6, '2026-12-31', 540.00, 'VIGENTE', 'Equipos para recepción y consultorios'),
(3, 1, '2026-08-28', '2026-09-01', 6, '2027-02-28', 835.00, 'VIGENTE', 'Implementación de nueva oficina');

INSERT INTO contrato_detalle (id_contrato, id_producto, cantidad, precio_mensual, subtotal) VALUES
(1, 1, 3, 120.00, 360.00),
(1, 5, 1, 250.00, 250.00),
(2, 2, 2, 180.00, 360.00),
(2, 3, 1, 90.00, 90.00),
(2, 6, 2, 45.00, 90.00),
(3, 1, 5, 120.00, 600.00),
(3, 4, 5, 35.00, 175.00),
(3, 7, 1, 60.00, 60.00);

INSERT INTO cuota (id_contrato, numero, fecha_vencimiento, monto, estado, fecha_pago) VALUES
(1, 1, '2026-06-01', 610.00, 'PAGADA', '2026-06-01'),
(1, 2, '2026-07-01', 610.00, 'PAGADA', '2026-07-02'),
(1, 3, '2026-08-01', 610.00, 'PAGADA', '2026-08-01'),
(1, 4, '2026-09-01', 610.00, 'PAGADA', '2026-09-03'),
(1, 5, '2026-10-01', 610.00, 'PENDIENTE', NULL),
(1, 6, '2026-11-01', 610.00, 'PENDIENTE', NULL),
(2, 1, '2026-08-01', 540.00, 'PAGADA', '2026-08-05'),
(2, 2, '2026-09-01', 540.00, 'PENDIENTE', NULL),
(2, 3, '2026-10-01', 540.00, 'PENDIENTE', NULL),
(2, 4, '2026-11-01', 540.00, 'PENDIENTE', NULL),
(2, 5, '2026-12-01', 540.00, 'PENDIENTE', NULL),
(2, 6, '2027-01-01', 540.00, 'PENDIENTE', NULL),
(3, 1, '2026-10-01', 835.00, 'PAGADA', '2026-10-01'),
(3, 2, '2026-11-01', 835.00, 'PENDIENTE', NULL),
(3, 3, '2026-12-01', 835.00, 'PENDIENTE', NULL),
(3, 4, '2027-01-01', 835.00, 'PENDIENTE', NULL),
(3, 5, '2027-02-01', 835.00, 'PENDIENTE', NULL),
(3, 6, '2027-03-01', 835.00, 'PENDIENTE', NULL);
