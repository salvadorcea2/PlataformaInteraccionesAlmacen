with tramites as (
    select distinct tipo_tramite.id as id
    from interaccion, tramite, tipo_tramite
    where interaccion.recepcion_id = ? and
          interaccion.tramite_id = tramite.id and
          tramite.tipo_tramite_id = tipo_tramite.id)
select count(*) from tramites;