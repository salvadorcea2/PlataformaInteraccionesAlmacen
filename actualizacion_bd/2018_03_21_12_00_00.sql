INSERT INTO receptor VALUES (5, 'ReceptorUsabilla', NULL, 4, 4, 0, 0, false, '"cron"=>"0 12 * * *", "tipo"=>"usabilla", "buttons"=>"996ca51785d1;a89e99cb53ec", "ejecutor"=>"usabilla", "sufijoProcesando"=>"procesando", "recuperarUsabilla"=>"true", "directorioProcesamiento"=>"receptorUsabillaProcesamiento", "inicioUsabilla"=>"20180310"', true, 1);


CREATE TABLE dwh.usabilla_diaria
(
  fecha_id integer NOT NULL,
  fecha_creacion timestamp with time zone DEFAULT now(),
  button_id character varying(40) COLLATE pg_catalog."default" NOT NULL,
  categoria character varying(20) COLLATE pg_catalog."default" NOT NULL,
  ficha integer NOT NULL,
  rating double precision NOT NULL,
  cuantos integer NOT NULL,
  CONSTRAINT pk_usabilla_diaria PRIMARY KEY (fecha_id, button_id, categoria, ficha)
);


CREATE OR REPLACE FUNCTION dwh.insertar_usabilla_diaria(
    integer,
    character varying,
    character varying,
    integer,
    double precision,
    integer)
  RETURNS void
LANGUAGE 'sql'
AS $BODY$

WITH usabilla AS (SELECT
                    $1                       AS fecha_id,
                    now(),
                    $2 AS button_id,
                    $3            AS categoria,
                    $4                 AS ficha,
                    $5                      AS rating,
                    $6                      AS cuantos
                  FROM
                    dwh.fecha AS fecha
                  WHERE
                    fecha.id = $1)

INSERT INTO dwh.usabilla_diaria AS i (fecha_id, fecha_creacion, button_id, categoria, ficha, rating, cuantos)
  SELECT *
  FROM usabilla
ON CONFLICT ON CONSTRAINT pk_usabilla_diaria
  DO UPDATE SET rating = (EXCLUDED.rating*EXCLUDED.cuantos+i.rating*i.cuantos)/(EXCLUDED.cuantos+i.cuantos);

$BODY$;

|