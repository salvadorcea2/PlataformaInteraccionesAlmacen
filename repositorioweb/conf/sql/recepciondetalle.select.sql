select tipo_tramite.codigo_pmg as codigo, tipo_tramite.nombre as nombre, tipo_tramite.institucion_id, institucion.subsecretaria_id, subsecretaria.ministerio_id, min(fecha_interaccion) as desde,  max(fecha_interaccion) as hasta, count(interaccion.*) as interacciones
from interaccion, tramite, tipo_tramite, institucion, subsecretaria
where interaccion.recepcion_id = ? and
      interaccion.tramite_id = tramite.id and
      tramite.tipo_tramite_id = tipo_tramite.id and
      tipo_tramite.institucion_id = institucion.id AND
      institucion.subsecretaria_id = subsecretaria.id
group by tipo_tramite.nombre, tipo_tramite.codigo_pmg, tipo_tramite.institucion_id, institucion.subsecretaria_id, subsecretaria.ministerio_id