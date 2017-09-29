package cl.exe.dao

import cl.exe.bd.DAO
import cl.exe.modelo.Entidad.EntidadId
import cl.exe.modelo.{Receptor, TipoTramite}
import com.github.mauricio.async.db.RowData
import cl.exe.bd.Conexion._
import com.typesafe.scalalogging.LazyLogging
import cl.exe.bd.Conexion._

import scala.collection.mutable
import scala.util.{Failure, Success}

/**
  * Created by utaladriz on 18-07-17.
  */

object Cache extends LazyLogging {
  var tipoTramites = Map[String, TipoTramite]().empty
  var factores = Map[String, Map[Int, Double]]().empty


  def refrescar  {
   val f = new scala.collection.mutable.HashMap[String, scala.collection.mutable.HashMap[Int, Double]].empty
   for (
         t <- obtenerTipoTramites
        ) yield {
     tipoTramites = t.getOrElse( Map[String, TipoTramite]().empty)
   }


   ejecutarSQL("factores.select.sql", Array[Int]()).map(qr => {
     qr.rows.get.foreach(r=> {

         if (!f.contains(r("codigo_pmg").asInstanceOf[String])){
           f += (r("codigo_pmg").asInstanceOf[String]-> new mutable.HashMap[Int,Double]())
         }
         val m = f(r("codigo_pmg").asInstanceOf[String])
         m += (r("canal_id").asInstanceOf[Int] -> r("factor").asInstanceOf[Double])


       })
   }).onComplete({
     case Success(s) =>  factores = f.map(kv => (kv._1->kv._2.toMap)).toMap
       logger.info(s"Factores $factores")
     case Failure(e) =>
       logger.error("Error al recuperar factores",e)
   })


  }

  def obtenerFactores() = {

  }

  def obtenerTipoTramites() = {
    TipoTramiteDAO.obtenerTodos.map(_.map(_.map(tt => tt.codigoPMG -> tt).toMap))
  }



}
object TipoTramiteDAO extends DAO[TipoTramite, EntidadId] {

  override def aEntidad(r : RowData) : TipoTramite = {

    TipoTramite(id = r("id").asInstanceOf[Int], nombre = r("nombre").asInstanceOf[String],
      descripcion = r("nombre").asInstanceOf[String],institucionId = r("institucion_id").asInstanceOf[Int],
      codigoPMG = r("codigo_pmg").asInstanceOf[String], url = r("url").asInstanceOf[String], periodicidad = r("periodicidad_id").asInstanceOf[Integer], habilitado = r("habilitado").asInstanceOf[Boolean])

  }

}
