
﻿
CREATE TABLE dwh.usabilla_semanal
(
  fecha_id integer NOT NULL,
  fecha_creacion timestamp with time zone DEFAULT now(),
  button_id varchar(40),
  categoria varchar(20),
  ficha integer,
  rating DOUBLE PRECISION NOT NULL,
  CONSTRAINT pk_usabilla_semanal PRIMARY KEY (fecha_id, button_id, categoria, ficha)
);


CREATE OR REPLACE FUNCTION dwh.insertar_usabilla_semanal(
    integer,
    varchar(20),
    varchar(40),
    integer,
    DOUBLE PRECISION)
  RETURNS void
LANGUAGE 'sql'
AS $BODY$


WITH usabilla AS (SELECT
                    $1                       AS fecha_id,
                    now(),
                    $2 AS button_id,
                    $3            AS categoria,
                    $4                 AS ficha,
                    $5                      AS rating
                  FROM
                    dwh.fecha AS fecha
                  WHERE
                    fecha.id = $1)


INSERT INTO dwh.usabilla_semanal AS i (fecha_id, fecha_creacion, button_id, categoria, ficha, rating)
  SELECT *
  FROM usabilla
ON CONFLICT ON CONSTRAINT pk_usabilla_semanal
  DO UPDATE SET rating = EXCLUDED.rating;

$BODY$;

﻿
INSERT INTO receptor VALUES (5, 'ReceptorUsabilla', NULL, 4, 4, 0, 0, false, '"cron"=>"0 1 * * 1", "tipo"=>"usabilla", "ejecutor"=>"usabilla", "sufijoProcesando"=>"procesando", "directorioProcesamiento"=>"receptorUsabillaProcesamiento", "recuperarUsabilla"=>"true", "inicioUsabilla"=>"20180319;20180326", "buttons"=>"996ca51785d1;a89e99cb53ec"', true, 1);

