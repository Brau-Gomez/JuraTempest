--liquibase formatted sql

--changeset juratempest:notificaciones-1
CREATE TABLE notificacion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    titulo VARCHAR(150) NOT NULL,
    mensaje VARCHAR(500) NOT NULL,
    tipo VARCHAR(30) NOT NULL CHECK (tipo in('RESERVA', 'PAGO', 'MANTENIMIENTO', 'TORNEO', 'PROMOCION', 'SISTEMA')),
    canal VARCHAR(30) NOT NULL CHECK (canal in('SISTEMA', 'EMAIL', 'SMS_SIMULADO', 'WHATSAPP_SIMULADO')),
    leida BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_creacion DATETIME NOT NULL
);
