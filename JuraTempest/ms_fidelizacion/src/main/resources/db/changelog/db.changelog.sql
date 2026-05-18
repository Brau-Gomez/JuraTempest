--liquibase formatted sql

--changeset juratempest:fidelizacion-1
CREATE TABLE fidelizacion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    puntos INT NOT NULL CHECK (puntos > 0),
    descripcion VARCHAR(200) NOT NULL,
    fecha_registro DATE NOT NULL
);

--changeset juratempest:fidelizacion-2
INSERT INTO fidelizacion (
    usuario_id,
    puntos,
    descripcion,
    fecha_registro
)
VALUES
(1, 50, 'Puntos por primera reserva completada', '2026-05-07'),
(1, 25, 'Bono por uso frecuente del servicio', '2026-05-08'),
(2, 40, 'Puntos por reserva en horario disponible', '2026-05-08'),
(3, 30, 'Puntos por mantenimiento de historial activo', '2026-05-09'),
(2, 20, 'Bono por reserva en tramo de baja demanda', '2026-05-10'),
(4, 60, 'Puntos por completar cinco reservas', '2026-05-10'),
(5, 35, 'Puntos por uso continuo del servicio', '2026-05-11'),
(3, 45, 'Bono por reserva confirmada sin cancelacion', '2026-05-11'),
(4, 15, 'Puntos por actualizacion de datos de usuario', '2026-05-12'),
(5, 70, 'Bono especial por fidelizacion mensual', '2026-05-12');
