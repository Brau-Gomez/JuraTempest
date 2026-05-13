--liquibase formatted sql

--changeset juratempest:reservas-1
CREATE TABLE reservas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    maquina_id BIGINT NOT NULL,
    horario_id BIGINT NOT NULL,
    fecha_reserva DATE NOT NULL,
    estado VARCHAR(30) NOT NULL check (estado in('ACTIVA', 'CANCELADA', 'FINALIZADA'))
);

--changeset juratempest:reservas-2
INSERT INTO reserva (usuario_id, maquina_id, horario_id, fecha_reserva, estado) VALUES
(3, 1, 1, '2026-05-07', 'ACTIVA'),
(4, 2, 2, '2026-05-07', 'ACTIVA'),
(5, 3, 3, '2026-05-07', 'ACTIVA'),
(6, 4, 4, '2026-05-07', 'CANCELADA'),
(7, 5, 5, '2026-05-07', 'ACTIVA'),
(8, 7, 6, '2026-05-07', 'FINALIZADA'),
(9, 8, 7, '2026-05-08', 'ACTIVA'),
(10, 1, 8, '2026-05-08', 'CANCELADA'),
(3, 2, 9, '2026-05-08', 'FINALIZADA'),
(4, 3, 10, '2026-05-08', 'ACTIVA');