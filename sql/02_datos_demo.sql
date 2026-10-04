-- =====================================================================
--  Datos de prueba para demostrar los 10 requerimientos funcionales
-- =====================================================================
USE pcsac_certificados;

INSERT INTO empresa (ruc, razon_social, contacto, telefono) VALUES
 ('20100047218', 'Compania Minera Antamina S.A.', 'Gerencia SSOMA',      '01-2176300'),
 ('20536557858', 'Minera Chinalco Peru S.A.',     'Jefatura de Seguridad','01-7006000'),
 ('20419387658', 'Southern Peru Copper Corp.',    'Area de Capacitacion', '054-583000');

INSERT INTO instructor (dni, nombres, apellidos, especialidad) VALUES
 ('41235678', 'Ricardo',  'Peralta Yupanqui', 'Ingeniero de Seguridad Industrial'),
 ('43987612', 'Lucia',    'Mendoza Arce',     'Especialista en Higiene Ocupacional'),
 ('45671239', 'Fernando', 'Quiroz Tello',     'Ingeniero de Minas - Prevencion de Riesgos');

INSERT INTO curso (codigo, nombre, duracion_horas, nota_minima) VALUES
 ('SSO-101', 'Induccion general de seguridad minera',        8,  13.00),
 ('SSO-201', 'Trabajos en altura y uso de arnes',           16,  13.00),
 ('SSO-301', 'Espacios confinados',                         12,  14.00),
 ('SSO-401', 'Manejo de sustancias peligrosas (MATPEL)',    20,  14.00);

INSERT INTO alumno (dni, nombres, apellidos, correo, telefono, area, id_empresa) VALUES
 ('70112233', 'Elena',    'Ccahuana Ribera',  'eccahuana@antamina.pe', '987111222', 'Operaciones mina',  1),
 ('70223344', 'Victor',   'Palomino Rojas',   'vpalomino@antamina.pe', '987222333', 'Mantenimiento',     1),
 ('70334455', 'Rosa',     'Huanca Ticona',    'rhuanca@antamina.pe',   '987333444', 'Planta concentradora', 1),
 ('70445566', 'Julio',    'Ramos Cardenas',   'jramos@chinalco.pe',    '987444555', 'Geologia',          2),
 ('70556677', 'Patricia', 'Noriega Salas',    'pnoriega@chinalco.pe',  '987555666', 'Medio ambiente',    2),
 ('70667788', 'Andres',   'Vilca Mamani',     'avilca@southern.pe',    '987666777', 'Operaciones mina',  3),
 ('70778899', 'Carmen',   'Zegarra Pinto',    'czegarra@southern.pe',  '987777888', 'Logistica',         3);

INSERT INTO capacitacion (codigo, id_curso, id_empresa, id_instructor, fecha, nota_minima) VALUES
 ('CAP-2026-001', 2, 1, 1, '2026-09-14', 13.00),
 ('CAP-2026-002', 3, 2, 2, '2026-09-21', 14.00),
 ('CAP-2026-003', 1, 3, 3, '2026-09-28', 13.00);

-- Inscripciones con su voucher de pago (RF-05)
INSERT INTO inscripcion (id_alumno, id_capacitacion, fecha_inscripcion, voucher, nota_final, estado) VALUES
 (1, 1, '2026-09-07', 'BV-004512', 17.50, 'APROBADO'),
 (2, 1, '2026-09-07', 'BV-004513', 15.00, 'APROBADO'),
 (3, 1, '2026-09-08', 'BV-004514', 11.00, 'DESAPROBADO'),
 (4, 2, '2026-09-14', 'BV-004520', 16.00, 'APROBADO'),
 (5, 2, '2026-09-14', 'BV-004521', NULL,  'PENDIENTE'),
 (6, 3, '2026-09-21', 'BV-004530', 18.00, 'APROBADO'),
 (7, 3, '2026-09-21', 'BV-004531', 13.50, 'APROBADO');
