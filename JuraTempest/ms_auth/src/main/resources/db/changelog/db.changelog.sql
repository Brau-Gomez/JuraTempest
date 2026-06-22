--liquibase formatted sql

--changeset juratempest:auth-1
CREATE TABLE rol (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(30) NOT NULL UNIQUE check (nombre in('ADMIN', 'OPERADOR', 'CLIENTE'))
);

--changeset juratempest:auth-2
CREATE TABLE cuenta (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT UNIQUE,
    email VARCHAR(120) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_registro DATE NOT NULL
);

--changeset juratempest:auth-3
CREATE TABLE cuenta_roles (
    cuenta_id BIGINT NOT NULL,
    rol_id BIGINT NOT NULL,
    PRIMARY KEY (cuenta_id, rol_id),
    CONSTRAINT fk_cuenta_roles_cuenta FOREIGN KEY (cuenta_id) REFERENCES cuenta(id),
    CONSTRAINT fk_cuenta_roles_rol FOREIGN KEY (rol_id) REFERENCES rol(id)
);

--changeset juratempest:auth-4
INSERT INTO rol (nombre) VALUES
('ADMIN'),
('OPERADOR'),
('CLIENTE');

--changeset juratempest:auth-5
INSERT INTO cuenta (usuario_id, email, password_hash, activo, fecha_registro) VALUES
(1, 'admin@juratempest.cl', '$2a$10$FlOyPiHgGGb4Y1laa6dEa.6vah2pqCtBETCFJW0LXEjMU8uBqEzii', TRUE, '2026-05-01'),
(2, 'operador@juratempest.cl', '$2a$10$an17LIdbUM73iSJTRer1WOos6ck3bXoVBtvUZ05qu2V1BCViPvWwi', TRUE, '2026-05-01'),
(3, 'camila.rojas@mail.cl', '$2a$10$sEvjAWSY2eLfcseQFG0M8.b7Cn8oz1CyCCXDrqj4jaQnaJJhX/zyS', TRUE, '2026-05-01'),
(4, 'nicolas.paredes@mail.cl', '$2a$10$JavXRgB8YNuTJ0/kiNd/qebUNGWIpPYxtB25M6v//PkvF27P8NdPq', TRUE, '2026-05-01'),
(5, 'valentina.munoz@mail.cl', '$2a$10$4GvPnQ3DCognTUpHxmscXekNJjyZHd4bW0.NRzF4t8kOKjy5uNp4q', TRUE, '2026-05-01'),
(6, 'benjamin.silva@mail.cl', '$2a$10$Sn9spTSJ1o7DoqI.nEkMzeQd0vDHyd2VwFbJgQ/X3bRgvnIAqecMO', TRUE, '2026-05-01'),
(7, 'ignacia.torres@mail.cl', '$2a$10$WEF58iUe4P2LxLjXE7mJ2up4ObDK1jNI.CPJJRgkS1VTk0SSIsD3q', TRUE, '2026-05-01'),
(8, 'martin.carrasco@mail.cl', '$2a$10$83NVlGZ7OHJZ0KVWjiyJ0O6H/wD22819cAHSaFCiL9negiZNbkK6O', TRUE, '2026-05-01'),
(9, 'sofia.herrera@mail.cl', '$2a$10$vaM0pjRYExVsv0NeHAeoiOfEBMvaKp94tFdjie/qAzdpRTCddvPE6', TRUE, '2026-05-01'),
(10, 'diego.fuentes@mail.cl', '$2a$10$cjsVKFjscUfsTS1KWdRmou3io2YOv6Sdc/8xHtGJt/87hPqi0mnpK', TRUE, '2026-05-01');

--changeset juratempest:auth-6
INSERT INTO cuenta_roles (cuenta_id, rol_id) VALUES
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
