INSERT INTO public.interaccion(
  recepcion_id, periodicidad_id, fecha_interaccion,
  fecha_creacion, tipo_interaccion_id, canal_id, tramite_id, localidad_id,
  hmac256)
VALUES (?, ?, ?,
           now(), ?, ?, ?, ?,
           NULL) RETURNING id