package controllers

import javax.inject.{Inject, Singleton}

import play.api.Configuration
import play.api.mvc.{AbstractController, ControllerComponents}
import cl.exe.bd.Conexion._
import cl.exe.json.JSonDAO._
import play.api.libs.json.{JsError, __}
import play.api.libs.functional.syntax._

import scala.concurrent.Future
import scala.collection.mutable.ArrayBuffer


@Singleton
class InstitucionController @Inject()(cc: ControllerComponents)(implicit config: Configuration) extends AbstractController(cc) {

  val tabla = "institucion"

  def get(inicio: Int, cuantos: Int, orden: String, tipoOrden: String, habilitado: Option[Boolean], id: Option[Int], nombre: Option[String], ministerio_id : Option[Int], subsecretaria_id: Option[Int]) = Action.async { implicit request =>

    try {
      val parametros = ArrayBuffer.empty[Any]


      var sql = s"select id, nombre, subsecretaria_id, habilitado from $tabla"
      var sqlCuantos = s"select count(*) from $tabla"

      var clausula = "where"
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

      if (subsecretaria_id.isEmpty)
        ministerio_id.foreach(m => {
          sql = s"$sql $clausula id in (select i.id from institucion i, subsecretaria s, ministerio m  where m.id = $m and s.ministerio_id=m.id and i.subsecretaria_id = s.id)"
          sqlCuantos = s"$sqlCuantos $clausula id in (select i.id from institucion i, subsecretaria s, ministerio m  where m.id = $m and s.ministerio_id=m.id and i.subsecretaria_id = s.id)"
          clausula = "and"

        })
      subsecretaria_id.foreach(s => {
        parametros += s
        sql = s"$sql $clausula subsecretaria_id = ?"
        sqlCuantos = s"$sqlCuantos $clausula subsecretaria_id = ?"
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
          Status(500)(toError(e, "Error al recuperar instituciones", 100))
        }
    }
  }

  def delete(id: Int) = Action.async { implicit request =>

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
          Status(500)(toError(e, s"Error al borrar institución $id", 100))
        }
    }
  }

  implicit val rdsPost = (
    (__ \ 'nombre).read[String] and
      (__ \ 'subsecretaria_id).read[Int] and
      (__ \ 'habilitado).read[Boolean]
    ) tupled

  def post() = Action.async { implicit request =>
    request.body.asJson.map { json =>
      json.validate[(String, Int, Boolean)](rdsPost).map {
        case (nombre, subsecretaria, habilitado) =>
          val sql = s"insert into $tabla (nombre, subsecretaria_id, habilitado) values (?,?,?) returning id"
          val parametros = ArrayBuffer.empty[Any]
          parametros += nombre
          parametros += subsecretaria
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
      (__ \ 'subsecretaria_id).read[Int] and
      (__ \ 'habilitado).read[Boolean]
    ) tupled

  def put() = Action.async { implicit request =>
    request.body.asJson.map { json =>
      json.validate[(Int, String, Int, Boolean)](rdsPut).map {
        case (id, nombre, subsecretaria, habilitado) =>
          val sql = s"update $tabla set nombre=?, subsecretaria_id = ?, habilitado = ? where id = ? returning id"
          val parametros = ArrayBuffer.empty[Any]
          parametros += nombre
          parametros += subsecretaria
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
