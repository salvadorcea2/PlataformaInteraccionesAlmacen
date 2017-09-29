package controllers


import javax.inject.{Inject, Singleton}

import play.api.Configuration
import play.api.mvc.{AbstractController, ControllerComponents}
import cl.exe.bd.Conexion._
import cl.exe.json.JSonDAO._

import scala.concurrent.Future
import scala.collection.mutable.ArrayBuffer


@Singleton
class RecepcionController @Inject()(cc: ControllerComponents)(implicit config: Configuration) extends AbstractController(cc) {
  def recepcion(inicio: Int, cuantos: Int, orden: String, tipoOrden: String, fecha_inicio: Option[String], fecha_termino: Option[String], estado: Option[String], ministerio_id: Option[Int], subsecretaria_id: Option[Int], institucion_id: Option[Int]) = Action.async { implicit request =>
    /*request.session.get("usuario").map { usuario => */
    try {
      val parametros = ArrayBuffer.empty[Any]

      var sql = obtenerSQL("recepcion.select.sql")
      var sqlCuantos = obtenerSQL("recepcion.count.sql")
      fecha_inicio.foreach(f => {
        parametros += toDate(f)
        sql = sql + " and recepcion.fecha_creacion >= ?"
        sqlCuantos = sqlCuantos + " and recepcion.fecha_creacion >= ?"
      })
      fecha_termino.foreach(f => {
        parametros += toDate(f)
        sql = sql + " and recepcion.fecha_creacion < ?"
        sqlCuantos = sqlCuantos + " and recepcion.fecha_creacion < ?"
      })
      estado.foreach(e => {
        parametros += e
        sql = sql + " and recepcion.estado = ?"
        sqlCuantos = sqlCuantos + " and recepcion.estado = ?"
      })
      ministerio_id.foreach(m => {
        if (m != 0) {
          parametros += m
          sql = sql + " and usuario.ministerio_id = ?"
          sqlCuantos = sqlCuantos + " and usuario.ministerio_id = ?"
        }
      })
      subsecretaria_id.foreach(s => {
        if (s != 0) {
          parametros += s
          sql = sql + " and usuario.subsecretaria_id = ?"
          sqlCuantos = sqlCuantos + " and usuario.subsecretaria_id = ?"
        }
      })
      institucion_id.foreach(i => {
        if (i != 0) {
          parametros += i
          sql = sql + " and usuario.institucion_id = ?"
          sqlCuantos = sqlCuantos + " and usuario.institucion_id = ?"
        }
      })
      sql = sql + s" order by $orden $tipoOrden limit $cuantos offset $inicio"
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
          Status(500)(toError(e, "Error al recuperar recepciones", 100))
        }
    }
    /*
    }.getOrElse {
      Future {
        Unauthorized("Sin conexion")
      }
    } */
  }

  def receptores() = Action.async { implicit request =>
    request.session.get("usuario").map { usuario =>
      val ministerio = request.session.get("ministerio_id").map(_.toInt).getOrElse(0)
      val subsecretaria = request.session.get("subsecretaria_id").map(_.toInt).getOrElse(0)
      val institucion = request.session.get("institucion_id").map(_.toInt).getOrElse(0)

    try {
      val parametros = ArrayBuffer.empty[Any]
      parametros += ministerio
      parametros += subsecretaria
      parametros += institucion

      var sql = "select receptor.id as id,receptor.nombre as nombre from receptor, receptor_mascara where receptor.id = receptor_mascara.receptor_id "
      sql = sql + " and (receptor_mascara.ministerio_id = ? or receptor_mascara.ministerio_id = 0) "
      sql = sql + " and (receptor_mascara.subsecretaria_id = ? or receptor_mascara.subsecretaria_id = 0) "
      sql = sql + " and (receptor_mascara.institucion_id = ? or receptor_mascara.institucion_id = 0) "
      sql = sql + " order by nombre"
      for {
        f <- pool.sendPreparedStatement(sql, parametros).map(qr => {
          Ok(toRespuesta(toJson(qr.rows, qr.rows.get.columnNames)))
        })
      } yield f

    }
    catch {
      case e: Exception =>
        Future {
          Status(500)(toError(e, "Error al recuperar receptores", 100))
        }
    }

    }.getOrElse {
      Future {
        Unauthorized("Sin conexion")
      }
    }
  }

  def bitacora(id: Int, inicio: Int, cuantos: Int, orden: String, tipoOrden: String) = Action.async { implicit request =>

    try {
      val parametros = Array(id)
      var sql = obtenerSQL("bitacora.select.sql")
      var sqlCuantos = obtenerSQL("bitacora.count.sql")
      sql = sql + s" order by $orden $tipoOrden limit $cuantos offset $inicio"
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
          Status(500)(toError(e, "Error al recuperar bitácora", 100))
        }
    }
  }

  def detalle(id: Int, inicio: Int, cuantos: Int, orden: String, tipoOrden: String) = Action.async { implicit request =>

    try {
      val parametros = Array(id)
      var sql = obtenerSQL("recepciondetalle.select.sql")
      var sqlCuantos = obtenerSQL("recepciondetalle.count.sql")
      sql = sql + s" order by $orden $tipoOrden limit $cuantos offset $inicio"
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
          Status(500)(toError(e, "Error al recuperar detalle", 100))
        }
    }
  }


  val tabla_factor = "recepcion_factor"

  def getFactores(id: Int, inicio: Int, cuantos: Int, orden: String, tipoOrden: String, canal_id: Option[Int]) = Action.async { implicit request =>

    try {
      val parametros = ArrayBuffer.empty[Any]


      var sql = s"select id, recepcion_id, tipo_tramite_id, canal_id, factor from $tabla_factor where recepcion_id = ?"
      var sqlCuantos = s"select count(*) from $tabla_factor  where recepcion_id = ?"

      parametros += id

      var clausula = "and"

      canal_id.foreach(c => {
        parametros += c
        sql = s"$sql $clausula canal_id = ?"
        sqlCuantos = s"$sqlCuantos $clausula canal_id = ?"
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
          Status(500)(toError(e, s"Error al recuperar $tabla_factor", 100))
        }
    }
  }
}
