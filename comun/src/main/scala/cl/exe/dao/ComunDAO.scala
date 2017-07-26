package cl.exe.dao

import cl.exe.bd.DAO
import cl.exe.modelo.Entidad.EntidadId
import cl.exe.modelo.{Receptor, TipoTramite}
import com.github.mauricio.async.db.RowData
import cl.exe.bd.Conexion._
import com.typesafe.scalalogging.LazyLogging

import scala.util.{Failure, Success}

/**
  * Created by utaladriz on 18-07-17.
  */

object Cache extends LazyLogging {
  var tipoTramites = Map[String, TipoTramite]().empty


  def refrescar  {
   for (t <- obtenerTipoTramites) yield {
     tipoTramites = t.getOrElse( Map[String, TipoTramite]().empty)
   }


  }

  def obtenerTipoTramites() = {
    TipoTramiteDAO.obtenerTodos.map(_.map(_.map(tt => tt.codigoPMG -> tt).toMap))
  }



}
object TipoTramiteDAO extends DAO[TipoTramite, EntidadId] {

  override def aEntidad(r : RowData) : TipoTramite = {

    TipoTramite(id = r("id").asInstanceOf[Int], nombre = r("nombre").asInstanceOf[String],
      descripcion = r("nombre").asInstanceOf[String],institucionId = r("institucion_id").asInstanceOf[Int],
      codigoPMG = r("codigo_pmg").asInstanceOf[String], url = r("url").asInstanceOf[String], habilitado = r("habilitado").asInstanceOf[Boolean])

  }

}
