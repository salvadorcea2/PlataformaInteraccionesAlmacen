

CREATE TABLE dwh.interaccion_diaria
(
    fecha_id integer NOT NULL,
    fecha_creacion timestamp with time zone DEFAULT now(),
    tipo_interaccion_id integer NOT NULL,
    canal_id integer NOT NULL,
    ministerio_id integer NOT NULL,
    subsecretaria_id integer NOT NULL,
    institucion_id integer NOT NULL,
    cantidad integer NOT NULL,
    tipo_tramite_id integer NOT NULL,
    CONSTRAINT pk_interaccion_diaria PRIMARY KEY (fecha_id, tipo_tramite_id, tipo_interaccion_id, canal_id),
    CONSTRAINT fk_interaccion_diaria_canal FOREIGN KEY (canal_id)
        REFERENCES public.canal (id),
    CONSTRAINT fk_interaccion_diaria_institucion FOREIGN KEY (institucion_id)
        REFERENCES public.institucion (id),
    CONSTRAINT fk_interaccion_diaria_ministerio FOREIGN KEY (ministerio_id)
        REFERENCES public.ministerio (id),
    CONSTRAINT fk_interaccion_diaria_subsecretaria FOREIGN KEY (subsecretaria_id)
        REFERENCES public.subsecretaria (id) ,
    CONSTRAINT fk_interaccion_diaria_tipo_interaccion FOREIGN KEY (tipo_interaccion_id)
        REFERENCES public.tipo_interaccion (id),
    CONSTRAINT fk_interaccion_diaria_tipo_tramite FOREIGN KEY (tipo_tramite_id)
        REFERENCES public.tipo_tramite (id)
);



CREATE TABLE dwh.tramite_diaria
(
    fecha_id integer NOT NULL,
    fecha_creacion timestamp with time zone DEFAULT now(),
    canal_id integer NOT NULL,
    ministerio_id integer NOT NULL,
    subsecretaria_id integer NOT NULL,
    institucion_id integer NOT NULL,
    cantidad integer NOT NULL,
    tipo_tramite_id integer NOT NULL,
    CONSTRAINT pk_tramite_diaria PRIMARY KEY (fecha_id, tipo_tramite_id, canal_id),
    CONSTRAINT fk_tramite_diaria_canal FOREIGN KEY (canal_id)
        REFERENCES public.canal (id),
    CONSTRAINT fk_tramite_diaria_institucion FOREIGN KEY (institucion_id)
        REFERENCES public.institucion (id),
    CONSTRAINT fk_tramite_diaria_ministerio FOREIGN KEY (ministerio_id)
        REFERENCES public.ministerio (id),
    CONSTRAINT fk_tramite_diaria_subsecretaria FOREIGN KEY (subsecretaria_id)
        REFERENCES public.subsecretaria (id),
    CONSTRAINT fk_tramite_diaria_tipo_tramite FOREIGN KEY (tipo_tramite_id)
        REFERENCES public.tipo_tramite (id) 
);


-- FUNCTION: dwh.actualizar_interaccion_anual(integer, integer, integer, integer, integer)

-- DROP FUNCTION dwh.actualizar_interaccion_anual(integer, integer, integer, integer, integer);

CREATE OR REPLACE FUNCTION dwh.actualizar_interaccion_mensual(
    integer,
    integer,
    integer,
    integer,
    integer)
    RETURNS void
    LANGUAGE 'sql'
AS $BODY$

 
    WITH interacciones AS (SELECT
                         $1                       AS fecha_id,
                         now(),
                         $2 AS tipo_interaccion_id,
                         $3            AS canal_id,
                         ministerio.id                   AS ministerio_id,
                         subsecretaria.id                AS subsecretaria_id,
                         institucion.id                  AS institucion_id,
                         $4                 AS tipo_tramite_id, 
                         $5                      AS cantidad
                       FROM
                         ministerio, subsecretaria, institucion, tipo_tramite, dwh.fecha AS fecha
                       WHERE
                         fecha.id = $1 AND
                         tipo_tramite.id = $4 AND
                         tipo_tramite.institucion_id = institucion.id AND
                         institucion.subsecretaria_id = subsecretaria.id AND
                         subsecretaria.ministerio_id = ministerio.id)

    
INSERT INTO dwh.interaccion_mensual AS i (fecha_id, fecha_creacion, tipo_interaccion_id, canal_id, ministerio_id, subsecretaria_id, institucion_id, tipo_tramite_id, cantidad)
  SELECT *
  FROM interacciones
ON CONFLICT ON CONSTRAINT pk_interaccion_mensual
  DO UPDATE SET cantidad = EXCLUDED.cantidad + i.cantidad;

$BODY$;

CREATE OR REPLACE FUNCTION dwh.insertar_interaccion_diaria(
    integer,
    integer,
    integer,
    integer,
    integer,
    integer,
    integer)
    RETURNS void
    LANGUAGE 'sql'
AS $BODY$

    Update dwh.interaccion_mensual as u set cantidad = u.cantidad - diaria.cantidad from 
      ( Select cantidad from dwh.interaccion_diaria
      where
      fecha_id = $1 and tipo_interaccion_id=$2 and canal_id=$3 and tipo_tramite_id = $4)
     as diaria  where fecha_id = $6 and tipo_interaccion_id=$2 and canal_id=$3 and tipo_tramite_id=$4;

    Update dwh.interaccion_anual as u set cantidad = u.cantidad - diaria.cantidad from 
      ( Select cantidad from dwh.interaccion_diaria
      where
      fecha_id = $1 and tipo_interaccion_id=$2 and canal_id=$3 and tipo_tramite_id = $4)
     as diaria  where fecha_id = $7 and tipo_interaccion_id=$2 and canal_id=$3 and tipo_tramite_id=$4;  

    WITH interacciones AS (SELECT
                         $1                       AS fecha_id,
                         now() as fecha_creacion,
                         $2 AS tipo_interaccion_id,
                         $3            AS canal_id,
                         ministerio.id                   AS ministerio_id,
                         subsecretaria.id                AS subsecretaria_id,
                         institucion.id                  AS institucion_id,
                         $4                 AS tipo_tramite_id, 
                         $5                      AS cantidad
                       FROM
                         ministerio, subsecretaria, institucion, tipo_tramite, dwh.fecha AS fecha
                       WHERE
                         fecha.id = $1 AND
                         tipo_tramite.id = $4 AND
                         tipo_tramite.institucion_id = institucion.id AND
                         institucion.subsecretaria_id = subsecretaria.id AND
                         subsecretaria.ministerio_id = ministerio.id)

INSERT INTO dwh.interaccion_diaria AS i (fecha_id, fecha_creacion, tipo_interaccion_id, canal_id, ministerio_id, subsecretaria_id, institucion_id, tipo_tramite_id, cantidad)
  SELECT *
  FROM interacciones
ON CONFLICT ON CONSTRAINT pk_interaccion_diaria
  DO UPDATE SET cantidad = EXCLUDED.cantidad;

select * from dwh.actualizar_interaccion_mensual($6,$2,$3,$4,$5); 
select * from dwh.actualizar_interaccion_anual($7,$2,$3,$4,$5);

$BODY$;

CREATE OR REPLACE FUNCTION dwh.actualizar_tramite_mensual(
    integer,
    integer,
    integer,
    integer)
    RETURNS void
    LANGUAGE 'sql'
AS $BODY$

 
    WITH tramites AS (SELECT
                         $1                       AS fecha_id,
                         now(),
                         $2            AS canal_id,
                         ministerio.id                   AS ministerio_id,
                         subsecretaria.id                AS subsecretaria_id,
                         institucion.id                  AS institucion_id,
                         $3                AS tipo_tramite_id, 
                         $4                      AS cantidad
                       FROM
                         ministerio, subsecretaria, institucion, tipo_tramite, dwh.fecha AS fecha
                       WHERE
                         fecha.id = $1 AND
                         tipo_tramite.id = $3 AND
                         tipo_tramite.institucion_id = institucion.id AND
                         institucion.subsecretaria_id = subsecretaria.id AND
                         subsecretaria.ministerio_id = ministerio.id)

INSERT INTO dwh.tramite_mensual AS i (fecha_id, fecha_creacion, canal_id, ministerio_id, subsecretaria_id, institucion_id, tipo_tramite_id, cantidad)
  SELECT *
  FROM tramites
ON CONFLICT ON CONSTRAINT pk_tramite_mensual
  DO UPDATE SET cantidad = EXCLUDED.cantidad + i.cantidad;

$BODY$;

CREATE OR REPLACE FUNCTION dwh.insertar_tramite_diaria(
    integer,
    integer,
    integer,
    integer,
    integer,
    integer)
    RETURNS void
    LANGUAGE 'sql'
AS $BODY$

 Update dwh.tramite_mensual as u set cantidad = u.cantidad - mensual.cantidad from (
      Select cantidad from dwh.tramite_mensual 
      where
      fecha_id = $1 and canal_id=$2 and tipo_tramite_id = $3
    ) as mensual where fecha_id = $5 and canal_id=$2 and tipo_tramite_id=$3;

    Update dwh.tramite_anual as u set cantidad = u.cantidad - mensual.cantidad from (
      Select cantidad from dwh.tramite_mensual 
      where
      fecha_id = $1 and canal_id=$2 and tipo_tramite_id = $3
    ) as mensual where fecha_id = $6 and canal_id=$2 and tipo_tramite_id=$3;

    WITH tramites AS (SELECT
                         $1                       AS fecha_id,
                         now() AS fecha_creacion,
                         $2            AS canal_id,
                         ministerio.id                   AS ministerio_id,
                         subsecretaria.id                AS subsecretaria_id,
                         institucion.id                  AS institucion_id,
                         $3                 AS tipo_tramite_id, 
                         $4                      AS cantidad
                       FROM
                         ministerio, subsecretaria, institucion, tipo_tramite, dwh.fecha AS fecha
                       WHERE
                         fecha.id = $1 AND
                         tipo_tramite.id = $3 AND
                         tipo_tramite.institucion_id = institucion.id AND
                         institucion.subsecretaria_id = subsecretaria.id AND
                         subsecretaria.ministerio_id = ministerio.id)

INSERT INTO dwh.tramite_diaria AS i (fecha_id, fecha_creacion, canal_id, ministerio_id, subsecretaria_id, institucion_id, tipo_tramite_id, cantidad)
  SELECT *
  FROM tramites
ON CONFLICT ON CONSTRAINT pk_tramite_diaria
  DO UPDATE SET cantidad = EXCLUDED.cantidad;
select * from dwh.actualizar_tramite_mensual($5,$2,$3,$4); 
select * from dwh.actualizar_tramite_anual($6,$2,$3,$4); 

$BODY$;

create table servicio (
     id serial not null constraint pk_servicio primary key,
     nombre varchar(100),
     url varchar(100),
     institucion_id integer not null,
     habilitado boolean, 
     constraint fk_servicio_institucion foreign key (institucion_id) references institucion(id)

);

create table dwh.transaccion_interoperabilidad  (
    id serial not null constraint pk_log primary key,
    fecha_creacion timestamp with time zone DEFAULT now(),
    fecha_log timestamp with time zone,
    host_remoto varchar(12),
    servicio varchar(100),
    metodo_http varchar(6),
    uri varchar(200),
    status_http integer,
    codigo_institucion_consumidora varchar(5),
    id_transaccion varchar(36),
    servicio_id integer not null,
    institucion_id integer not null,
    constraint fk_transaccion_interoperabilidad_servicio foreign key (servicio_id) references servicio(id),
    constraint fk_transaccion_interoperabilidad_institucion foreign key (institucion_id) references institucion(id)
);

insert into canal (id, nombre, descripcion, habilitado) values (15, 'MOBILE','MOBILE', true);
INSERT INTO receptor VALUES (3, 'ReceptorMDS', NULL, 4, 4, 0, 0, false, '"url"=>"http://wsmds.ministeriodesarrollosocial.gob.cl/api/ws/getresumeninteracciones/2/pass", "cron"=>"0 2 */1 * *", "tipo"=>"mds", "ejecutor"=>"mds", "sufijoProcesando"=>"procesando", "urlAutenticacion"=>"http://wsmds.ministeriodesarrollosocial.gob.cl/api/auth/authorize-client", "directorioProcesamiento"=>"receptorMDSProcesamiento"', true, 1);
update receptor set propiedades = '﻿"tipo"=>"archivo", "ejecutor"=>"log", "generadores"=>"dwh.generar_interacciones;dwh.generar_tramites", "sufijoProcesando"=>"procesando", "directorioEntrada"=>"receptorDTEntrada", "directorioProcesamiento"=>"receptorDTProcesamiento"' where id = 1;
update receptor set propiedades = '﻿"tipo"=>"archivo", "ejecutor"=>"excel", "sufijoProcesando"=>"procesando", "directorioEntrada"=>"receptorExcelEntrada", "directorioProcesamiento"=>"receptorExcelProcesamiento"' where id = 2;

