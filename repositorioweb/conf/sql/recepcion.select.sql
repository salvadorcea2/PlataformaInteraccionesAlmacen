select recepcion.id, recepcion.fecha_creacion, recepcion.estado, recepcion.archivo, usuario.nombres, usuario.apellidos
from recepcion, usuario
where
  recepcion.usuario_id = usuario.id
