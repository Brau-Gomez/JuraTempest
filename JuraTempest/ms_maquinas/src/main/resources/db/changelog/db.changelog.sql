--liquibase formatted sql

--changeset juratempest:maquinas-1
CREATE TABLE maquina (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    ubicacion VARCHAR(100) NOT NULL,
    estado VARCHAR(30) NOT NULL CHECK (estado in('ACTIVA', 'MANTENCION', 'INACTIVA')),
    costo_por_bloque INT NOT NULL,
    fecha_instalacion DATE NOT NULL
);

--changeset juratempest:maquinas-2
INSERT INTO maquina (nombre, tipo, ubicacion, estado, costo_por_bloque, fecha_instalacion) VALUES
('Street Fighter II', 'LUCHA', 'Zona A', 'ACTIVA', 2500, '2026-05-01'),
('Pac-Man', 'CLASICO', 'Zona A', 'ACTIVA', 2000, '2026-05-01'),
('Metal Slug', 'ARCADE', 'Zona B', 'ACTIVA', 2500, '2026-05-01'),
('The King of Fighters 98', 'LUCHA', 'Zona B', 'ACTIVA', 2500, '2026-05-01'),
('Dance Dance Revolution', 'RITMO', 'Zona C', 'ACTIVA', 3000, '2026-05-01'),
('Time Crisis', 'DISPAROS', 'Zona C', 'MANTENCION', 3000, '2026-05-01'),
('Mortal Kombat II', 'LUCHA', 'Zona A', 'ACTIVA', 2500, '2026-05-01'),
('Out Run', 'CARRERAS', 'Zona D', 'ACTIVA', 2200, '2026-05-01'),
('House of the Dead', 'DISPAROS', 'Zona D', 'INACTIVA', 3000, '2026-05-01'),
('Galaga', 'CLASICO', 'Zona B', 'ACTIVA', 2000, '2026-05-01');
