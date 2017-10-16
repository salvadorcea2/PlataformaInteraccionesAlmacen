select count(*) as cuantos
from recepcion, usuario
where
  recepcion.usuario_id = usuario.id