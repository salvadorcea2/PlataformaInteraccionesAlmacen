INSERT INTO public.interaccion(
  recepcion_id, intermedio_id, periodicidad_id, fecha_interaccion,
  fecha_creacion, tipo_interaccion_id, canal_id, tramite_id, localidad_id,
  hmac256)
VALUES (?, NULL, 1, ?,
           now(), ?, 1, ?, NULL,
           NULL) RETURNING id
