INSERT INTO public.tramite(
  recepcion_id, tipo_tramite_id, fecha_tramite, persona_id, fecha_creacion)
VALUES  (?, ?, ?, ?, now()) RETURNING id