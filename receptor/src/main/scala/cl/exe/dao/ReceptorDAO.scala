package cl.exe.dao

import cl.exe.bd.Conexion.ejecutarSQL
import cl.exe.bd.DAO
import cl.exe.modelo.Entidad.EntidadId
import cl.exe.modelo.Receptor
import com.github.mauricio.async.db.{ResultSet, RowData}

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

/**
  * Created by utaladriz on 11-07-17.
  */
object ReceptorDAO extends DAO[Receptor, EntidadId] {

  override def aEntidad(r : RowData) : Receptor = {

    Receptor(id = r("id").asInstanceOf[Int], nombre = r("nombre").asInstanceOf[String],
      descripcion = r("nombre").asInstanceOf[String], canalTransmisionId = r("canal_transmision_id").asInstanceOf[Int],
      formatoId = r("formato_id").asInstanceOf[Int], plantillaId = r("plantilla_recepcion_id").asInstanceOf[Int], periodicidadId = r("periodicidad_id").asInstanceOf[Int],
      propiedades = hstoreToMap(r("propiedades").asInstanceOf[String]),habilitado = r("habilitado").asInstanceOf[Boolean])

  }

  def bitacora(recepcionId : EntidadId, nivel : String, log : String, etapa:String)(implicit ec : ExecutionContext) = {
    ejecutarSQL("bitacora.insert.sql", Array(recepcionId, nivel, log , etapa)).onComplete({
      case Success(q) =>
        logger.debug("Bitacora actualizada recepcion {} {} {} {}", recepcionId.toString, nivel, log, etapa)
      case Failure(e)=>
        logger.error("Error al generar la bitácora",e)
    })
  }

}
