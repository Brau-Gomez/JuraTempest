CREATE TABLE mantenimiento (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    maquina_id BIGINT NOT NULL,
    usuario_operador_id BIGINT NULL,
    tipo VARCHAR(30) NOT NULL,
    descripcion VARCHAR(500) NOT NULL,
    tecnico VARCHAR(120) NOT NULL,
    estado VARCHAR(30) NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NULL,
    costo INT NULL
);

INSERT INTO mantenimiento (maquina_id, usuario_operador_id, tipo, descripcion, tecnico, estado, fecha_inicio, fecha_fin, costo) VALUES
(1, 1, 'PREVENTIVO', 'Limpieza interna y revision de controles', 'Equipo Tecnico Norte', 'PENDIENTE', CURRENT_DATE(), NULL, 25000),
(2, 2, 'CORRECTIVO', 'Cambio de botones principales', 'Equipo Tecnico Centro', 'EN_PROCESO', DATE_SUB(CURRENT_DATE(), INTERVAL 1 DAY), NULL, 42000),
(3, 3, 'FALLA_REPORTADA', 'Pantalla con parpadeo intermitente', 'Equipo Tecnico Sur', 'FINALIZADO', DATE_SUB(CURRENT_DATE(), INTERVAL 5 DAY), DATE_SUB(CURRENT_DATE(), INTERVAL 4 DAY), 65000),
(4, 1, 'PREVENTIVO', 'Actualizacion de firmware y prueba de monedas', 'Equipo Tecnico Norte', 'PENDIENTE', CURRENT_DATE(), NULL, 18000),
(5, 2, 'CORRECTIVO', 'Revision de fuente de poder', 'Equipo Tecnico Centro', 'CANCELADO', DATE_SUB(CURRENT_DATE(), INTERVAL 3 DAY), DATE_SUB(CURRENT_DATE(), INTERVAL 2 DAY), 0);
