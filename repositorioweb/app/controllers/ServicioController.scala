package controllers

import javax.inject.{Inject, Singleton}

import cl.exe.bd.Conexion._
import cl.exe.json.JSonDAO._
import play.api.Configuration
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsError, __}
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.Future


@Singleton
class ServicioController @Inject()(cc: ControllerComponents)(implicit config: Configuration) extends AbstractController(cc) {

  val tabla = "servicio"

  def get(inicio: Int, cuantos: Int, orden: String, tipoOrden: String, habilitado: Option[Boolean], id: Option[Int], nombre: Option[String],  ministerio_id : Option[Int],subsecretaria_id : Option[Int],institucion_id : Option[Int]) = Action.async { implicit request =>

    try {
      val parametros = ArrayBuffer.empty[Any]


      var sql = s"select id, nombre, url, institucion_id, habilitado from $tabla"
      var sqlCuantos = s"select count(*) from $tabla"

      var clausula = "where"

      if (institucion_id.isEmpty && subsecretaria_id.isEmpty)
        ministerio_id.foreach(m => {
          sql = s"$sql $clausula id in (select ss.id from servicio ss, institucion i, subsecretaria s, ministerio m  where m.id = $m and s.ministerio_id=m.id and i.subsecretaria_id = s.id and ss.institucion_id = i.id)"
          sqlCuantos = s"$sqlCuantos $clausula id in (select ss.id from servicio ss, institucion i, subsecretaria s, ministerio m  where m.id = $m and s.ministerio_id=m.id and i.subsecretaria_id = s.id and ss.institucion_id = i.id)"
          clausula = "and"

        })

      if (institucion_id.isEmpty )
        subsecretaria_id.foreach(s => {
          sql = s"$sql $clausula id in (select ss.id from servicio ss, institucion i, subsecretaria s   where s.id = $s and i.subsecretaria_id = s.id and ss.institucion_id = i.id)"
          sqlCuantos = s"$sqlCuantos $clausula id in (select ss.id from servicio ss, institucion i, subsecretaria s   where s.id = $s and i.subsecretaria_id = s.id and ss.institucion_id = i.id)"
          clausula = "and"
        })



      id.foreach(i => {
        parametros += i
        sql = s"$sql $clausula id = ?"
        sqlCuantos = s"$sqlCuantos $clausula id = ?"
        clausula = "and"
      })
      nombre.foreach(n => {
        parametros += "%"+n.toLowerCase+"%"
        sql = s"$sql $clausula LOWER(nombre) like ?"
        sqlCuantos = s"$sqlCuantos $clausula LOWER(nombre) like ?"
        clausula = "and"
      })
      habilitado.foreach(h => {
        parametros += h
        sql = s"$sql $clausula habilitado = ?"
        sqlCuantos = s"$sqlCuantos $clausula habilitado = ?"
        clausula = "and"
      })
      institucion_id.foreach(i => {
        parametros += i
        sql = s"$sql $clausula institucion_id = ?"
        sqlCuantos = s"$sqlCuantos $clausula institucion_id = ?"
        clausula = "and"
      })



      if (cuantos != 0)
        sql = sql + s" order by $orden $tipoOrden limit $cuantos offset $inicio"
      else
        sql = sql + s" order by $orden $tipoOrden"
      for {
        total <- pool.sendPreparedStatement(sqlCuantos, parametros).map(qr => {
          qr.rows.get.head(0).asInstanceOf[Long]
        })
        f <- pool.sendPreparedStatement(sql, parametros).map(qr => {
          Ok(toRespuesta(toJson(qr.rows, qr.rows.get.columnNames), inicio, cuantos, orden, tipoOrden, total))
        })
      } yield f
    }
    catch {
      case e: Exception =>
        Future {
          Status(500)(toError(e, "Error al recuperar servicios", 100))
        }
    }
  }

  def delete(id: Int) = Action.async  { implicit request =>

    try {
      val parametros = ArrayBuffer.empty[Any]

      var sql = s"delete from $tabla where id=$id returning id"

      pool.sendPreparedStatement(sql, parametros).map(qr => {
        Ok(toRespuesta(toJson(qr.rows, qr.rows.get.columnNames)))
      })
    }
    catch {
      case e: Exception =>
        Future {
          Status(500)(toError(e, s"Error al borrar servicio $id", 100))
        }
    }
  }

  implicit val rdsPost = (
    (__ \ 'nombre).read[String] and
      (__ \ 'url).read[String] and
      (__ \ 'institucion_id).read[Int] and
      (__ \ 'habilitado).read[Boolean]
    ) tupled

  def post() = Action.async { implicit request =>
    request.body.asJson.map { json =>
      json.validate[(String, String, Int, Boolean)](rdsPost).map {
        case (nombre, url, institucion, habilitado) =>
          val sql = s"insert into $tabla (nombre, url, institucion_id, habilitado) values (?,?,?,?) returning id"
          val parametros = ArrayBuffer.empty[Any]
          parametros += nombre
          parametros += url
          parametros += institucion
          parametros += habilitado
          pool.sendPreparedStatement(sql, parametros).map(qr => {
            Ok(toRespuesta(toJson(qr.rows, qr.rows.get.columnNames)))
          })
      }.recoverTotal {
        e =>
          Future {
            BadRequest("Error Detectado:" + JsError.toJson(e))
          }
      }
    }.getOrElse {
      Future {
        BadRequest("Se espera JSON")
      }
    }
  }


  implicit val rdsPut = (
    (__ \ 'id).read[Int] and
      (__ \ 'nombre).read[String] and
      (__ \ 'url).read[String] and
      (__ \ 'institucion_id).read[Int] and
      (__ \ 'habilitado).read[Boolean]
    ) tupled

  def put() = Action.async { implicit request =>
    request.body.asJson.map { json =>
      json.validate[(Int, String, String, Int, Boolean)](rdsPut).map {
        case (id, nombre, url, institucion, habilitado) =>
          val sql = s"update $tabla set nombre=?, url=?, institucion_id = ?, habilitado = ? where id = ? returning id"
          val parametros = ArrayBuffer.empty[Any]
          parametros += nombre
          parametros += url
          parametros += institucion
          parametros += habilitado
          parametros += id
          pool.sendPreparedStatement(sql, parametros).map(qr => {
            Ok(toRespuesta(toJson(qr.rows, qr.rows.get.columnNames)))
          })
      }.recoverTotal {
        e =>
          Future {
            BadRequest("Error Detectado:" + JsError.toJson(e))
          }
      }
    }.getOrElse {
      Future {
        BadRequest("Se espera JSON")
      }
    }
  }

}
