package controllers

import javax.inject.{Inject, Singleton}

import cl.exe.bd.Conexion._
import cl.exe.json.JSonDAO._
import play.api.Configuration
import play.api.libs.functional.syntax._
import play.api.libs.functional._
import play.api.libs.json.{JsError, __}
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.Future


@Singleton
class SubsecretariaController @Inject()(cc: ControllerComponents)(implicit config: Configuration) extends AbstractController(cc) {

  val tabla = "subsecretaria"

  def get(inicio: Int, cuantos: Int, orden: String, tipoOrden: String, habilitado: Option[Boolean], id: Option[Int], nombre: Option[String], ministerio_id: Option[Int]) = Action.async { implicit request =>

    try {
      val parametros = ArrayBuffer.empty[Any]


      var sql = s"select id, nombre, ministerio_id, habilitado from $tabla"
      var sqlCuantos = s"select count(*) from $tabla"

      var clausula = "where"
      id.foreach(i => {
        parametros += i
        sql = s"$sql $clausula id = ?"
        sqlCuantos = s"$sqlCuantos $clausula id = ?"
        clausula = "and"
      })
      nombre.foreach(n => {
        parametros += n
        sql = s"$sql $clausula nombre like ?"
        sqlCuantos = s"$sqlCuantos $clausula nombre like ?"
        clausula = "and"
      })
      habilitado.foreach(h => {
        parametros += h
        sql = s"$sql $clausula habilitado = ?"
        sqlCuantos = s"$sqlCuantos $clausula habilitado = ?"
        clausula = "and"
      })
      ministerio_id.foreach(s => {
        parametros += s
        sql = s"$sql $clausula ministerio_id = ?"
        sqlCuantos = s"$sqlCuantos $clausula ministerio_id = ?"
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
          Status(500)(toError(e, "Error al recuperar subsecretarías", 100))
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
          Status(500)(toError(e, s"Error al borrar subsecretaría $id", 100))
        }
    }
  }

  implicit val rdsPost = (
    (__ \ 'nombre).read[String] and
      (__ \ 'ministerio_id).read[Int] and
      (__ \ 'habilitado).read[Boolean]
    ) tupled

  def post() = Action.async { implicit request =>
    request.body.asJson.map { json =>
      json.validate[(String, Int, Boolean)](rdsPost).map {
        case (nombre, ministerio, habilitado) =>
          val sql = s"insert into $tabla (nombre, ministerio_id, habilitado) values (?,?,?) returning id"
          val parametros = ArrayBuffer.empty[Any]
          parametros += nombre
          parametros += ministerio
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
      (__ \ 'ministerio_id).read[Int] and
      (__ \ 'habilitado).read[Boolean]
    ) tupled

  def put() = Action.async { implicit request =>
    request.body.asJson.map { json =>
      json.validate[(Int, String, Int, Boolean)](rdsPut).map {
        case (id, nombre, ministerio, habilitado) =>
          val sql = s"update $tabla set nombre=?, ministerio_id = ?, habilitado = ? where id = ? returning id"
          val parametros = ArrayBuffer.empty[Any]
          parametros += nombre
          parametros += ministerio
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
