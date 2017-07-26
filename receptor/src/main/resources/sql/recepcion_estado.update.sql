WITH insertado AS (insert into recepcion_estados (recepcion_id, estado, fecha_creacion) values (?, ?, now())
returning recepcion_id, estado)
update recepcion set estado = (select estado from insertado) where id = (select recepcion_id from insertado)