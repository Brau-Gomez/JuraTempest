--liquibase formatted sql

--changeset juratempest:usuarios-1
CREATE TABLE rol (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(30) NOT NULL UNIQUE check (nombre in('ADMIN', 'OPERADOR', 'CLIENTE'))
);

--changeset juratempest:usuarios-2
CREATE TABLE usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    frecuente BOOLEAN NOT NULL DEFAULT FALSE,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_registro DATE NOT NULL
);

--changeset juratempest:usuarios-3
CREATE TABLE usuario_roles (
    usuario_id BIGINT NOT NULL,
    rol_id BIGINT NOT NULL,
    PRIMARY KEY (usuario_id, rol_id),
    CONSTRAINT fk_usuario_roles_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id),
    CONSTRAINT fk_usuario_roles_rol FOREIGN KEY (rol_id) REFERENCES rol(id)
);

--changeset juratempest:usuarios-4
INSERT INTO rol (nombre) VALUES
('ADMIN'),
('OPERADOR'),
('CLIENTE');

--changeset juratempest:usuarios-5
INSERT INTO usuario (nombre, apellido, email, password, frecuente, activo, fecha_registro) VALUES
('Admin', 'Sistema', 'admin@juratempest.cl', '$2a$10$FlOyPiHgGGb4Y1laa6dEa.6vah2pqCtBETCFJW0LXEjMU8uBqEzii', TRUE, TRUE, '2026-05-01'),
('Operador', 'Principal', 'operador@juratempest.cl', '$2a$10$an17LIdbUM73iSJTRer1WOos6ck3bXoVBtvUZ05qu2V1BCViPvWwi', FALSE, TRUE, '2026-05-01'),
('Camila', 'Rojas', 'camila.rojas@mail.cl', '$2a$10$sEvjAWSY2eLfcseQFG0M8.b7Cn8oz1CyCCXDrqj4jaQnaJJhX/zyS', TRUE, TRUE, '2026-05-01'),
('Nicolas', 'Paredes', 'nicolas.paredes@mail.cl', '$2a$10$JavXRgB8YNuTJ0/kiNd/qebUNGWIpPYxtB25M6v//PkvF27P8NdPq', TRUE, TRUE, '2026-05-01'),
('Valentina', 'Munoz', 'valentina.munoz@mail.cl', '$2a$10$4GvPnQ3DCognTUpHxmscXekNJjyZHd4bW0.NRzF4t8kOKjy5uNp4q', FALSE, TRUE, '2026-05-01'),
('Benjamin', 'Silva', 'benjamin.silva@mail.cl', '$2a$10$Sn9spTSJ1o7DoqI.nEkMzeQd0vDHyd2VwFbJgQ/X3bRgvnIAqecMO', FALSE, TRUE, '2026-05-01'),
('Ignacia', 'Torres', 'ignacia.torres@mail.cl', '$2a$10$WEF58iUe4P2LxLjXE7mJ2up4ObDK1jNI.CPJJRgkS1VTk0SSIsD3q', TRUE, TRUE, '2026-05-01'),
('Martin', 'Carrasco', 'martin.carrasco@mail.cl', '$2a$10$83NVlGZ7OHJZ0KVWjiyJ0O6H/wD22819cAHSaFCiL9negiZNbkK6O', FALSE, TRUE, '2026-05-01'),
('Sofia', 'Herrera', 'sofia.herrera@mail.cl', '$2a$10$vaM0pjRYExVsv0NeHAeoiOfEBMvaKp94tFdjie/qAzdpRTCddvPE6', TRUE, TRUE, '2026-05-01'),
('Diego', 'Fuentes', 'diego.fuentes@mail.cl', '$2a$10$cjsVKFjscUfsTS1KWdRmou3io2YOv6Sdc/8xHtGJt/87hPqi0mnpK', FALSE, TRUE, '2026-05-01');

--changeset juratempest:usuarios-6
INSERT INTO usuario_roles (usuario_id, rol_id) VALUES
(1, 1),
(2, 2),
(3, 3),
(4, 3),
(5, 3),
(6, 3),
(7, 3),
(8, 3),
(9, 3),
(10, 3);
