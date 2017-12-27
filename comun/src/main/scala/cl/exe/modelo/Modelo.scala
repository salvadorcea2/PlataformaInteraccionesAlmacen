package cl.exe.modelo

import java.sql.Date

import cl.exe.modelo.Entidad.EntidadId

object Entidad {
  type EntidadId = Int
}

trait Entidad {
  def id: EntidadId
}

case class CanalTransmision(id : EntidadId, nombre : String, descripcion: String,  habilitado : Boolean)
case class Canal(id : EntidadId, nombre : String, descripcion: String,  habilitado : Boolean)
case class Formato(id : EntidadId, nombre : String, descripcion: String, habilitado : Boolean)
case class Plantilla(id : EntidadId, nombre : String, descripcion: String, plantilla: String, habilitado : Boolean)
case class Periodicidad(id : EntidadId, nombre : String, descripcion: String, habilitado : Boolean)
case class TipoTramite(id : EntidadId, nombre : String, descripcion: String, institucionId : EntidadId, codigoPMG : String, url : String, periodicidad : Int, habilitado : Boolean)
case class Recepcion(id:EntidadId, receptorId : EntidadId, fechaCreacion : Date, estado : String, archivo : String, propiedades : Map[String, String], usuarioId : EntidadId,  hmac : Array[Byte] )
case class Tramite(id:EntidadId, recepcionId : EntidadId, tipoTramiteId : EntidadId, personaId : EntidadId, fechaCreacion : Date, fechaTramite : Date,  hmac : Array[Byte])
case class Interaccion(id:EntidadId, recepcionId : EntidadId, periodicidadId : EntidadId, tipoInteraccionId : EntidadId, canalId : EntidadId, tramiteId : EntidadId, localidadId : EntidadId, fechaCreacion : Date, fechaInteraccion : Date, hmac : Array[Byte] )
case class TramiteDWH(id:EntidadId, fechaId : EntidadId, tipoTramiteId : EntidadId, ministerioId : EntidadId, subsecretariaId : EntidadId, institucionId : EntidadId, cantidad : Int, fechaCreacion : Date )
case class InteraccionDWH(id:EntidadId, fechaId : EntidadId, tipoInteraccionId : EntidadId, canalId : EntidadId, ministerioId : EntidadId, subsecretariaId : EntidadId, institucionId : EntidadId, cantidad : Int, fechaCreacion : Date )
case class Receptor(id : EntidadId, nombre : String, descripcion: String, canalTransmisionId : EntidadId, formatoId: EntidadId, plantillaId : EntidadId, periodicidadId : EntidadId, usuarioId : EntidadId,  propiedades : Map[String, String], habilitado : Boolean)