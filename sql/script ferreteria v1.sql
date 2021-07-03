CREATE TABLE caja_estado (
    cod_barra_caja   VARCHAR(30) 
--  ERROR: VARCHAR2 size not specified 
     NOT NULL,
    estatus          NUMERIC(1)
);

ALTER TABLE caja_estado ADD CONSTRAINT caja_estado_pk PRIMARY KEY ( cod_barra_caja );

CREATE TABLE caja_estatus_reporte (
    cod_barra_caja   VARCHAR(30) 
--  ERROR: VARCHAR2 size not specified 
     NOT NULL,
    fecha            VARCHAR(10),
    hora             VARCHAR(5),
    estatus          NUMERIC(1),
    comentario       VARCHAR(1000),
    id_dispositivo   VARCHAR(30) 
--  ERROR: VARCHAR2 size not specified 
     NOT NULL
);

CREATE TABLE dispositivo (
    id_dispositivo         VARCHAR(30) 
--  ERROR: VARCHAR2 size not specified 
     NOT NULL,
    marca_modelo           VARCHAR(30) 
--  ERROR: VARCHAR2 size not specified 
   ,
    empleado_id_empleado   VARCHAR(30) 
--  ERROR: VARCHAR2 size not specified 
     NOT NULL
);

ALTER TABLE dispositivo ADD CONSTRAINT dispositivo_pk PRIMARY KEY ( id_dispositivo );

CREATE TABLE empleado (
    id_empleado   VARCHAR(30) 
--  ERROR: VARCHAR2 size not specified 
     NOT NULL,
    nombre        VARCHAR(30)
--  ERROR: VARCHAR2 size not specified 
   ,
    apellido      VARCHAR(30)
--  ERROR: VARCHAR2 size not specified 
);

ALTER TABLE empleado ADD CONSTRAINT empleado_pk PRIMARY KEY ( id_empleado );

ALTER TABLE caja_estatus_reporte
    ADD CONSTRAINT caja_estado_fk FOREIGN KEY ( cod_barra_caja )
        REFERENCES caja_estado ( cod_barra_caja );

ALTER TABLE dispositivo
    ADD CONSTRAINT dispositivo_empleado_fk FOREIGN KEY ( empleado_id_empleado )
        REFERENCES empleado ( id_empleado );

ALTER TABLE caja_estatus_reporte
    ADD CONSTRAINT id_dispositivo_fk FOREIGN KEY ( id_dispositivo )
        REFERENCES dispositivo ( id_dispositivo );