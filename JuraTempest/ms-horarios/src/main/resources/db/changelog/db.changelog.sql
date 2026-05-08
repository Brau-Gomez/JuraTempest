--liquibase formatted sql

--changeset juratempest:horarios-1
CREATE TABLE bloque_horario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    fecha DATE NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    disponible BOOLEAN NOT NULL,
    estado VARCHAR(30) NOT NULL CHECK (estado in ('DISPONIBLE','CERRADO','MANTENCION','BLOQUEADO')),
    capacidad_maquina INT NOT NULL,
    cupos_disponibles INT NOT NULL CHECK (cupos_disponibles >= 0 and cupos_disponibles <= capacidad_maquina)
);



--changeset juratempest:horarios-2
INSERT INTO bloque_horario (
    fecha,
    hora_inicio,
    hora_fin,
    disponible,
    estado,
    capacidad_maquina,
    cupos_disponibles
) 
VALUES
('2026-05-07', '08:00:00', '10:00:00', TRUE, 'DISPONIBLE', 4, 1),
('2026-05-07', '10:00:00', '12:00:00', TRUE, 'DISPONIBLE', 4, 2),
('2026-05-07', '12:00:00', '14:00:00', TRUE, 'DISPONIBLE', 4, 3),
('2026-05-07', '14:00:00', '16:00:00', TRUE, 'DISPONIBLE', 4, 4),
('2026-05-07', '16:00:00', '18:00:00', TRUE, 'DISPONIBLE', 4, 3),
('2026-05-07', '18:00:00', '20:00:00', TRUE, 'DISPONIBLE', 4, 1),
('2026-05-08', '08:00:00', '10:00:00', TRUE, 'DISPONIBLE', 4, 4),
('2026-05-08', '10:00:00', '12:00:00', TRUE, 'DISPONIBLE', 4, 2),
('2026-05-08', '12:00:00', '14:00:00', TRUE, 'DISPONIBLE', 4, 1),
('2026-05-08', '14:00:00', '16:00:00', TRUE, 'DISPONIBLE', 4, 3);


