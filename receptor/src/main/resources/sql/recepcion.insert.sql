WITH insertado as (INSERT INTO public.recepcion(receptor_id, fecha_creacion, estado, archivo, hmac256, propiedades, usuario_id, ministerio_id, subsecretaria_id, institucion_id)
VALUES (?, now(), 'recepcionada', ?, NULL , NULL, ?, ?, ?, ? ) RETURNING id)
insert into recepcion_estados (recepcion_id, estado, fecha_creacion) values ((select id from insertado), 'recepcionada', now())
returning recepcion_id as id

