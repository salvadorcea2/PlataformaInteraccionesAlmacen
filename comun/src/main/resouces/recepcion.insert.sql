INSERT INTO public.recepcion(
  receptor_id, fecha_creacion, estado, archivo, hmac256, propiedades)
VALUES (?, now(), ?, ?, NULL , NULL ) RETURNING id
