-- Roles mínimos requeridos para el funcionamiento de la aplicación
INSERT INTO rol (rol_id, nombre_rol) VALUES (1, 'ADMIN') ON CONFLICT (rol_id) DO NOTHING;
INSERT INTO rol (rol_id, nombre_rol) VALUES (2, 'USER') ON CONFLICT (rol_id) DO NOTHING;
