select codigo_pmg, tipo_interaccion_id, factor from tipo_tramite_factor, tipo_tramite
where
  tipo_tramite.id = tipo_tramite_factor.tipo_tramite_id;