--liquibase formatted sql

--changeset juratempest:usuarios-1
CREATE TABLE usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cuenta_id BIGINT NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    frecuente BOOLEAN NOT NULL DEFAULT FALSE,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_registro DATE NOT NULL
);

--changeset juratempest:usuarios-2
INSERT INTO usuario (cuenta_id, nombre, apellido, email, frecuente, activo, fecha_registro) VALUES
(1, 'Admin', 'Sistema', 'admin@juratempest.cl', TRUE, TRUE, '2026-05-01'),
(2, 'Operador', 'Principal', 'operador@juratempest.cl', FALSE, TRUE, '2026-05-01'),
(3, 'Camila', 'Rojas', 'camila.rojas@mail.cl', TRUE, TRUE, '2026-05-01'),
(4, 'Nicolas', 'Paredes', 'nicolas.paredes@mail.cl', TRUE, TRUE, '2026-05-01'),
(5, 'Valentina', 'Munoz', 'valentina.munoz@mail.cl', FALSE, TRUE, '2026-05-01'),
(6, 'Benjamin', 'Silva', 'benjamin.silva@mail.cl', FALSE, TRUE, '2026-05-01'),
(7, 'Ignacia', 'Torres', 'ignacia.torres@mail.cl', TRUE, TRUE, '2026-05-01'),
(8, 'Martin', 'Carrasco', 'martin.carrasco@mail.cl', FALSE, TRUE, '2026-05-01'),
(9, 'Sofia', 'Herrera', 'sofia.herrera@mail.cl', TRUE, TRUE, '2026-05-01'),
(10, 'Diego', 'Fuentes', 'diego.fuentes@mail.cl', FALSE, TRUE, '2026-05-01');
