-- =====================================================================
--  Peru Consulting SAC - Sistema de Administracion de Certificados
--  Esquema de base de datos (MySQL 8)
--  Proyecto Final - Tecnicas de Programacion Orientada a Objetos
-- =====================================================================

DROP DATABASE IF EXISTS pcsac_certificados;
CREATE DATABASE pcsac_certificados
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;
USE pcsac_certificados;

-- ------------------------------------------------------------ empresa
CREATE TABLE empresa (
    id_empresa   INT AUTO_INCREMENT PRIMARY KEY,
    ruc          VARCHAR(11)  NOT NULL UNIQUE,
    razon_social VARCHAR(120) NOT NULL,
    contacto     VARCHAR(100),
    telefono     VARCHAR(20),
    CONSTRAINT ck_empresa_ruc CHECK (CHAR_LENGTH(ruc) = 11)
) ENGINE = InnoDB;

-- ----------------------------------------------------------- instructor
CREATE TABLE instructor (
    id_instructor INT AUTO_INCREMENT PRIMARY KEY,
    dni           VARCHAR(8)  NOT NULL UNIQUE,
    nombres       VARCHAR(80) NOT NULL,
    apellidos     VARCHAR(80) NOT NULL,
    especialidad  VARCHAR(100) NOT NULL,
    CONSTRAINT ck_instructor_dni CHECK (CHAR_LENGTH(dni) = 8)
) ENGINE = InnoDB;

-- --------------------------------------------------------------- curso
CREATE TABLE curso (
    id_curso        INT AUTO_INCREMENT PRIMARY KEY,
    codigo          VARCHAR(15)  NOT NULL UNIQUE,
    nombre          VARCHAR(120) NOT NULL,
    duracion_horas  INT          NOT NULL,
    nota_minima     DECIMAL(4,2) NOT NULL DEFAULT 13.00,
    CONSTRAINT ck_curso_horas CHECK (duracion_horas > 0),
    CONSTRAINT ck_curso_nota  CHECK (nota_minima BETWEEN 0 AND 20)
) ENGINE = InnoDB;

-- -------------------------------------------------------------- alumno
CREATE TABLE alumno (
    id_alumno  INT AUTO_INCREMENT PRIMARY KEY,
    dni        VARCHAR(8)  NOT NULL UNIQUE,
    nombres    VARCHAR(80) NOT NULL,
    apellidos  VARCHAR(80) NOT NULL,
    correo     VARCHAR(120),
    telefono   VARCHAR(20),
    area       VARCHAR(80),
    id_empresa INT NOT NULL,
    CONSTRAINT fk_alumno_empresa FOREIGN KEY (id_empresa)
        REFERENCES empresa (id_empresa),
    CONSTRAINT ck_alumno_dni CHECK (CHAR_LENGTH(dni) = 8)
) ENGINE = InnoDB;

-- -------------------------------------------------------- capacitacion
CREATE TABLE capacitacion (
    id_capacitacion INT AUTO_INCREMENT PRIMARY KEY,
    codigo          VARCHAR(20) NOT NULL UNIQUE,
    id_curso        INT  NOT NULL,
    id_empresa      INT  NOT NULL,
    id_instructor   INT  NOT NULL,
    fecha           DATE NOT NULL,
    nota_minima     DECIMAL(4,2) NOT NULL DEFAULT 13.00,
    CONSTRAINT fk_cap_curso      FOREIGN KEY (id_curso)      REFERENCES curso (id_curso),
    CONSTRAINT fk_cap_empresa    FOREIGN KEY (id_empresa)    REFERENCES empresa (id_empresa),
    CONSTRAINT fk_cap_instructor FOREIGN KEY (id_instructor) REFERENCES instructor (id_instructor),
    CONSTRAINT ck_cap_nota CHECK (nota_minima BETWEEN 0 AND 20)
) ENGINE = InnoDB;

-- --------------------------------------------------------- inscripcion
CREATE TABLE inscripcion (
    id_inscripcion  INT AUTO_INCREMENT PRIMARY KEY,
    id_alumno       INT  NOT NULL,
    id_capacitacion INT  NOT NULL,
    fecha_inscripcion DATE NOT NULL,
    voucher         VARCHAR(40),
    nota_final      DECIMAL(4,2) NULL,
    estado          ENUM('PENDIENTE','APROBADO','DESAPROBADO') NOT NULL DEFAULT 'PENDIENTE',
    CONSTRAINT fk_ins_alumno       FOREIGN KEY (id_alumno)       REFERENCES alumno (id_alumno),
    CONSTRAINT fk_ins_capacitacion FOREIGN KEY (id_capacitacion) REFERENCES capacitacion (id_capacitacion),
    CONSTRAINT uq_ins_alumno_cap UNIQUE (id_alumno, id_capacitacion),   -- RF-04: sin duplicados
    CONSTRAINT ck_ins_nota CHECK (nota_final IS NULL OR nota_final BETWEEN 0 AND 20)
) ENGINE = InnoDB;

-- --------------------------------------------------------- certificado
CREATE TABLE certificado (
    id_certificado INT AUTO_INCREMENT PRIMARY KEY,
    codigo         VARCHAR(30) NOT NULL UNIQUE,
    id_inscripcion INT NOT NULL UNIQUE,
    fecha_emision  DATE NOT NULL,
    ruta_archivo   VARCHAR(255),
    CONSTRAINT fk_cert_inscripcion FOREIGN KEY (id_inscripcion)
        REFERENCES inscripcion (id_inscripcion)
) ENGINE = InnoDB;

-- ------------------------------------------------------------- indices
CREATE INDEX ix_alumno_empresa      ON alumno (id_empresa);
CREATE INDEX ix_cap_empresa         ON capacitacion (id_empresa);
CREATE INDEX ix_ins_capacitacion    ON inscripcion (id_capacitacion);
CREATE INDEX ix_ins_estado          ON inscripcion (estado);
