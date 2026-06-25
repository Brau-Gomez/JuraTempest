CREATE TABLE torneo (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(120) NOT NULL,
    descripcion VARCHAR(500) NOT NULL,
    maquina_id BIGINT NOT NULL,
    horario_id BIGINT NOT NULL,
    cupos_maximos INT NOT NULL,
    cupos_disponibles INT NOT NULL,
    estado VARCHAR(30) NOT NULL,
    ganador_usuario_id BIGINT NULL,
    fecha_creacion DATE NOT NULL
);

CREATE TABLE inscripcion_torneo (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    torneo_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    estado VARCHAR(30) NOT NULL,
    fecha_inscripcion DATETIME NOT NULL,
    CONSTRAINT fk_inscripcion_torneo FOREIGN KEY (torneo_id) REFERENCES torneo(id)
);

CREATE INDEX idx_torneo_estado ON torneo(estado);
CREATE INDEX idx_inscripcion_torneo ON inscripcion_torneo(torneo_id);
CREATE INDEX idx_inscripcion_usuario ON inscripcion_torneo(usuario_id);

INSERT INTO torneo (nombre, descripcion, maquina_id, horario_id, cupos_maximos, cupos_disponibles, estado, ganador_usuario_id, fecha_creacion) VALUES
('Copa Street Fighter', 'Torneo de eliminacion directa en maquina de pelea', 1, 1, 8, 6, 'ABIERTO', NULL, CURRENT_DATE()),
('Reto Pac-Man', 'Competencia por puntaje maximo', 2, 2, 6, 6, 'PROGRAMADO', NULL, CURRENT_DATE()),
('Final Arcade Retro', 'Final de temporada para jugadores frecuentes', 3, 3, 4, 3, 'CERRADO', NULL, DATE_SUB(CURRENT_DATE(), INTERVAL 2 DAY)),
('Desafio Pinball', 'Evento casual de pinball por rondas', 4, 4, 10, 10, 'PROGRAMADO', NULL, CURRENT_DATE()),
('Campeonato Galaga', 'Campeonato historico con premio de fidelizacion', 5, 5, 4, 3, 'FINALIZADO', 1, DATE_SUB(CURRENT_DATE(), INTERVAL 7 DAY));

INSERT INTO inscripcion_torneo (torneo_id, usuario_id, estado, fecha_inscripcion) VALUES
(1, 1, 'INSCRITO', NOW()),
(1, 2, 'INSCRITO', NOW()),
(3, 3, 'INSCRITO', DATE_SUB(NOW(), INTERVAL 2 DAY)),
(5, 1, 'INSCRITO', DATE_SUB(NOW(), INTERVAL 7 DAY)),
(5, 2, 'CANCELADO', DATE_SUB(NOW(), INTERVAL 6 DAY));
