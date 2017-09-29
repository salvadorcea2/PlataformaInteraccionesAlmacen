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
class MantenedorController @Inject()(cc: ControllerComponents)(implicit config: Configuration) extends AbstractController(cc) {


  def mantenedores(orden: String, tipoOrden: String) = Action.async { implicit request =>

    try {


      var sql = s"select tabla, singular, plural from mantenedor order by $orden $tipoOrden"
      pool.sendQuery(sql).map(qr => {
        Ok(toRespuesta(toJson(qr.rows, qr.rows.get.columnNames)))
      })
    }
    catch {
      case e: Exception =>
        Future {
          Status(500)(toError(e, "Error al recuperar Mantenedores", 100))
        }
    }
  }

  def get(tabla: String, inicio: Int, cuantos: Int, orden: String, tipoOrden: String, habilitado: Option[Boolean], id: Option[Int], nombre: Option[String]) = Action.async { implicit request =>

    try {
      val parametros = ArrayBuffer.empty[Any]


      var sql = s"select id, nombre, descripcion, habilitado from $tabla"
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
          Status(500)(toError(e, s"Error al recuperar $tabla", 100))
        }
    }
  }

  def delete(tabla: String, id: Int) = Action.async { implicit request =>

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
          Status(500)(toError(e, s"Error al borrar $tabla $id", 100))
        }
    }
  }

  implicit val rdsPost = (
    (__ \ 'nombre).read[String] and
      (__ \ 'descripcion).read[String] and
      (__ \ 'habilitado).read[Boolean]
    ) tupled

  def post(tabla: String) = Action.async { implicit request =>
    request.body.asJson.map { json =>
      json.validate[(String, String, Boolean)](rdsPost).map {
        case (nombre, descripcion, habilitado) =>
          val sql = s"insert into $tabla (nombre, descripcion, habilitado) values (?,?,?) returning id"
          val parametros = ArrayBuffer.empty[Any]
          parametros += nombre
          parametros += descripcion
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
      (__ \ 'descripcion).read[String] and
      (__ \ 'habilitado).read[Boolean]
    ) tupled

  def put(tabla: String) = Action.async { implicit request =>
    request.body.asJson.map { json =>
      json.validate[(Int, String, String, Boolean)](rdsPut).map {
        case (id, nombre, descripcion, habilitado) =>
          val sql = s"update $tabla set nombre=?, descripcion=?, habilitado = ? where id = ? returning id"
          val parametros = ArrayBuffer.empty[Any]
          parametros += nombre
          parametros += descripcion
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
