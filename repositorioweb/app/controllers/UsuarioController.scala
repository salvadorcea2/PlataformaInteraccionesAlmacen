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
class UsuarioController @Inject()(cc: ControllerComponents)(implicit config: Configuration) extends AbstractController(cc) {

  val tabla = "usuario"

  def get(inicio: Int, cuantos: Int, orden: String, tipoOrden: String, habilitado: Option[Boolean], id: Option[Int], usuario: Option[String], nombres: Option[String], apellidos: Option[String], rut: Option[String], ministerio_id: Option[Int], subsecretaria_id: Option[Int], institucion_id: Option[Int], tipo : Option[String], rol : Option[String]) = Action.async { implicit request =>

    try {
      val parametros = ArrayBuffer.empty[Any]
      var sql = s"select id, usuario, email, telefono, nombres, apellidos, rut, ministerio_id, subsecretaria_id, institucion_id, cargo, habilitado, rol, tipo from $tabla"
      var sqlCuantos = s"select count(*) from $tabla"

      var clausula = "where"
      id.foreach(i => {
        parametros += i
        sql = s"$sql $clausula id = ?"
        sqlCuantos = s"$sqlCuantos $clausula id = ?"
        clausula = "and"
      })
      usuario.foreach(u => {
        parametros += u
        sql = s"$sql $clausula usuario like ?"
        sqlCuantos = s"$sqlCuantos $clausula usuario like ?"
        clausula = "and"
      })
      nombres.foreach(n => {
        parametros += n
        sql = s"$sql $clausula nombres like ?"
        sqlCuantos = s"$sqlCuantos $clausula nombres like ?"
        clausula = "and"
      })
      apellidos.foreach(a => {
        parametros += a
        sql = s"$sql $clausula apellidos like ?"
        sqlCuantos = s"$sqlCuantos $clausula apellidos like ?"
        clausula = "and"
      })
      rut.foreach(r => {
        parametros += r
        sql = s"$sql $clausula rut = ?"
        sqlCuantos = s"$sqlCuantos $clausula rut = ?"
        clausula = "and"
      })

      habilitado.foreach(h => {
        parametros += h
        sql = s"$sql $clausula habilitado = ?"
        sqlCuantos = s"$sqlCuantos $clausula habilitado = ?"
        clausula = "and"
      })
      ministerio_id.foreach(m => {
        parametros += m
        sql = s"$sql $clausula ministerio_id = ?"
        sqlCuantos = s"$sqlCuantos $clausula ministerio_id = ?"
        clausula = "and"
      })
      subsecretaria_id.foreach(s => {
        parametros += s
        sql = s"$sql $clausula subsecretaria_id = ?"
        sqlCuantos = s"$sqlCuantos $clausula subsecretaria_id = ?"
        clausula = "and"
      })
      institucion_id.foreach(i => {
        parametros += i
        sql = s"$sql $clausula institucion_id = ?"
        sqlCuantos = s"$sqlCuantos $clausula institucion_id = ?"
        clausula = "and"
      })
      tipo.foreach(t => {
        parametros += t
        sql = s"$sql $clausula tipo = ?"
        sqlCuantos = s"$sqlCuantos $clausula tipo = ?"
        clausula = "and"
      })
      rol.foreach(r => {
        parametros += r
        sql = s"$sql $clausula rol = ?"
        sqlCuantos = s"$sqlCuantos $clausula rol = ?"
        clausula = "and"
      })

      if (cuantos != 0)
        sql = sql + s" order by $orden $tipoOrden limit $cuantos offset $inicio"
      else
        sql = sql + s" order by $orden $tipoOrden"
      for {total <- pool.sendPreparedStatement(sqlCuantos, parametros).map(qr => {
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
          Status(500)(toError(e, "Error al recuperar usuarios", 100))
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
    (__ \ 'usuario).read[String] and
      (__ \ 'email).read[String] and
      (__ \ 'telefono).read[String] and
      (__ \ 'nombres).read[String] and
      (__ \ 'apellidos).read[String] and
      (__ \ 'rut).read[String] and
      (__ \ 'ministerio_id).read[Int] and
      (__ \ 'subsecretaria_id).read[Int] and
      (__ \ 'institucion_id).read[Int] and
      (__ \ 'cargo).read[String] and
      (__ \ 'tipo).read[String] and
      (__ \ 'rol).read[String] and
      (__ \ 'habilitado).read[Boolean]
    ) tupled

  def post() = Action.async { implicit request =>
    request.body.asJson.map { json =>
      json.validate[(String, String, String, String, String, String, Int, Int, Int,  String, String, String, Boolean)](rdsPost).map {
        case (usuario, email, telefono, nombres, apellidos, rut, ministerio, subsecretaria, institucion, cargo, tipo, rol, habilitado) =>
          val sql = s"insert into $tabla (usuario, email, telefono, nombres, apellidos, rut, ministerio_id, subsecretaria_id, institucion_id, cargo, tipo, rol, habilitado) values (?,?,?,?,?,?,?,?, ?,?,?,?,?) returning id"
          val parametros = ArrayBuffer.empty[Any]
          parametros += usuario
          parametros += email
          parametros += telefono
          parametros += nombres
          parametros += apellidos
          parametros += rut
          parametros += ministerio
          parametros += subsecretaria
          parametros += institucion
          parametros += cargo
          parametros += tipo
          parametros += rol
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
      (__ \ 'usuario).read[String] and
      (__ \ 'email).read[String] and
      (__ \ 'telefono).read[String] and
      (__ \ 'nombres).read[String] and
      (__ \ 'apellidos).read[String] and
      (__ \ 'rut).read[String] and
      (__ \ 'ministerio_id).read[Int] and
      (__ \ 'subsecretaria_id).read[Int] and
      (__ \ 'institucion_id).read[Int] and
      (__ \ 'cargo).read[String] and
      (__ \ 'tipo).read[String] and
      (__ \ 'rol).read[String] and
      (__ \ 'habilitado).read[Boolean]
    ) tupled


  def put() = Action.async { implicit request =>
    request.body.asJson.map { json =>
      json.validate[(Int, String, String, String, String, String, String, Int, Int, Int, String, String, String, Boolean)](rdsPut).map {
        case (id, usuario, email, telefono, nombres, apellidos, rut, ministerio, subsecretaria, institucion, cargo, tipo,rol, habilitado) =>
          val sql = s"update $tabla set usuario=?, email = ?, telefono = ?, nombres = ?, apellidos = ?, rut = ?, ministerio_id=?, subsecretaria_id=?, institucion_id=?, cargo=?, tipo=?, rol=?, habilitado=? where id = ? returning id"
          val parametros = ArrayBuffer.empty[Any]
          parametros += usuario
          parametros += email
          parametros += telefono
          parametros += nombres
          parametros += apellidos
          parametros += rut
          parametros += ministerio
          parametros += subsecretaria
          parametros += institucion
          parametros += cargo
          parametros += tipo
          parametros += rol
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

  def sesion() = Action.async { implicit request =>
    request.session.get("usuario").map { id =>
      try {
        val parametros = Array(id.toInt)
        var sql = s"select id, usuario, email, telefono, nombres, apellidos, rut, ministerio_id, subsecretaria_id, institucion_id, cargo, habilitado, tipo, rol from $tabla where id=?"
        pool.sendPreparedStatement(sql, parametros).map(qr => {
          Ok(toRespuesta(toJson(qr.rows, qr.rows.get.columnNames)))
        })
      }
      catch {
        case e: Exception =>
          Future {
            Status(500)(toError(e, "Error al recuperar usuarios", 100))
          }
      }
    }.getOrElse {
      Future {
        Unauthorized("Sin conexion")
      }
    }


  }

}
