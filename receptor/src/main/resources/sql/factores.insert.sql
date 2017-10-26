WITH r as (Select id from recepcion where id = ?)
insert into recepcion_factor(recepcion_id, tipo_tramite_id, tipo_interaccion_id, factor)
  select (select id from r), tipo_tramite_id, tipo_interaccion_id, factor from tipo_tramite_factor