package controllers

import javax.inject.{Inject, Singleton}

import cl.exe.bd.Conexion._
import cl.exe.json.JSonDAO.{toError, toJson, toRespuesta}
import play.api.Configuration
import play.api.libs.json.{JsError, __}
import play.api.mvc.{AbstractController, ControllerComponents}
import play.api.libs.functional.syntax._
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.Future

@Singleton
class ReceptorController @Inject()(cc: ControllerComponents)(implicit config: Configuration) extends AbstractController(cc) {

  val tabla = "receptor"

  def get(inicio: Int, cuantos: Int, orden: String, tipoOrden: String, habilitado: Option[Boolean], id: Option[Int], nombre: Option[String], canal_transmision_id : Option[Int], usuario_id : Option[Int]) = Action.async { implicit request =>

    try {
      val parametros = ArrayBuffer.empty[Any]


      var sql = s"select id, nombre, descripcion, canal_transmision_id, formato_id, plantilla_recepcion_id, periodicidad_id, notificacion_diaria,  propiedades, habilitado from $tabla"
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
      canal_transmision_id.foreach(c => {
        parametros += c
        sql = s"$sql $clausula canal_transmision_id = ?"
        sqlCuantos = s"$sqlCuantos $clausula canal_transmision_id = ?"
        clausula = "and"
      })
      usuario_id.foreach(u => {
        parametros += u
        sql = s"$sql $clausula usuario_id = ?"
        sqlCuantos = s"$sqlCuantos $clausula usuario_id = ?"
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
          Status(500)(toError(e, s"Error al borrar $tabla $id", 100))
        }
    }
  }



  implicit val rdsPost = (
      (__ \ 'nombre).read[String] and
      (__ \ 'descripcion).read[String] and
      (__ \ 'canal_transmision_id).read[Int] and
      (__ \ 'formato_id).read[Int] and
      (__ \ 'plantilla_recepcion_id).read[Int] and
      (__ \ 'periodicidad_id).read[Int] and
      (__ \ 'notificacion_diaria).read[Boolean] and
      (__ \ 'propiedades).read[String] and
      (__ \ 'habilitado).read[Boolean]
    ) tupled

  def post = Action.async { implicit request =>
    request.body.asJson.map { json =>
      json.validate[(String, String, Int, Int, Int, Int, Boolean, String, Boolean)](rdsPost).map {
        case (nombre, descripcion, canal_transmision_id, formato_id, plantilla_recepcion_id, periodicidad_id, notificacion_diaria, propiedades, habilitado) =>
          val sql = s"insert into $tabla (nombre, descripcion, canal_transmision_id, formato_id, plantilla_recepcion_id, periodicidad_id, notificacion_diaria, propiedades, habilitado) values (?,?,?,?,?,?,?,?,?) returning id"
          val parametros = ArrayBuffer.empty[Any]
          parametros += nombre
          parametros += descripcion
          parametros += canal_transmision_id
          parametros += formato_id
          parametros += plantilla_recepcion_id
          parametros += periodicidad_id
          parametros += notificacion_diaria
          parametros += propiedades
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
        (__ \ 'canal_transmision_id).read[Int] and
        (__ \ 'formato_id).read[Int] and
        (__ \ 'plantilla_recepcion_id).read[Int] and
        (__ \ 'periodicidad_id).read[Int] and
        (__ \ 'notificacion_diaria).read[Boolean] and
        (__ \ 'propiedades).read[String] and
        (__ \ 'habilitado).read[Boolean]
    ) tupled

  def put = Action.async { implicit request =>
    request.body.asJson.map { json =>
      json.validate[(Int, String, String, Int, Int, Int, Int, Boolean, String, Boolean)](rdsPut).map {
        case (id, nombre, descripcion, canal_transmision_id, formato_id, plantilla_recepcion_id, periodicidad_id, notificacion_diaria, propiedades, habilitado) =>
          val sql = s"update $tabla set nombre=?, descripcion=?, canal_transmision_id=?, formato_id=?, plantilla_recepcion_id=?, periodicidad_id=?, notificacion_diaria=?, propiedades=?, habilitado = ? where id = ? returning id"
          val parametros = ArrayBuffer.empty[Any]
          parametros += nombre
          parametros += descripcion
          parametros += canal_transmision_id
          parametros += formato_id
          parametros += plantilla_recepcion_id
          parametros += periodicidad_id
          parametros += notificacion_diaria
          parametros += propiedades
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

  val tabla_mascara = "receptor_mascara"

  def getMascara(idReceptor: Int, inicio: Int, cuantos: Int, orden: String, tipoOrden: String, id: Option[Int], ministerio_id: Option[Int], subsecretaria_id : Option[Int], institucion_id : Option[Int]) = Action.async { implicit request =>

    try {
      val parametros = ArrayBuffer.empty[Any]


      var sql = s"select id, receptor_id, ministerio_id, subsecretaria_id, institucion_id from $tabla_mascara where receptor_id = ?"
      var sqlCuantos = s"select count(*) from $tabla_mascara  where receptor_id = ?"

      parametros += idReceptor

      var clausula = "and"
      id.foreach(i => {
        parametros += i
        sql = s"$sql $clausula id = ?"
        sqlCuantos = s"$sqlCuantos $clausula id = ?"
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

  def deleteMascara(id: Int) = Action.async { implicit request =>

    try {
      val parametros = ArrayBuffer.empty[Any]

      var sql = s"delete from $tabla_mascara where id=$id returning id"

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



  implicit val rdsPostMascara = (
    (__ \ 'receptor_id).read[Int] and
      (__ \ 'ministerio_id).read[Int] and
      (__ \ 'subsecretaria_id).read[Int] and
      (__ \ 'institucion_id).read[Int]
    ) tupled

  def postMascara = Action.async { implicit request =>
    request.body.asJson.map { json =>
      json.validate[(Int, Int, Int, Int)](rdsPostMascara).map {
        case (receptor_id, ministerio_id, subsecretaria_id, institucion_id) =>
          val sql = s"insert into $tabla_mascara (receptor_id, ministerio_id, subsecretaria_id, institucion_id) values (?,?,?,?) returning id"
          val parametros = ArrayBuffer.empty[Any]
          parametros += receptor_id
          parametros += ministerio_id
          parametros += subsecretaria_id
          parametros += institucion_id
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

  implicit val rdsPutMascara = (
    (__ \ 'id).read[Int] and
      (__ \ 'receptor_id).read[Int] and
      (__ \ 'ministerio_id).read[Int] and
      (__ \ 'subsecretaria_id).read[Int] and
      (__ \ 'institucion_id).read[Int]
    ) tupled

  def putMascara = Action.async { implicit request =>
    request.body.asJson.map { json =>
      json.validate[(Int, Int, Int, Int, Int)](rdsPutMascara).map {
        case (id, receptor_id, ministerio_id, subsecretaria_id, institucion_id) =>
          val sql = s"update $tabla_mascara set receptor_id=?, ministerio_id=?, subsecretaria_id=?, institucion_id=? where id = ? returning id"
          val parametros = ArrayBuffer.empty[Any]
          parametros += receptor_id
          parametros += ministerio_id
          parametros += subsecretaria_id
          parametros += institucion_id
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


  val tabla_plantilla = "plantilla_recepcion"
  def getPlantilla(inicio: Int, cuantos: Int, orden: String, tipoOrden: String, habilitado: Option[Boolean], id: Option[Int], nombre: Option[String]) = Action.async { implicit request =>

    try {
      val parametros = ArrayBuffer.empty[Any]


      var sql = s"select id, nombre, descripcion, plantilla, habilitado from $tabla_plantilla"
      var sqlCuantos = s"select count(*) from $tabla_plantilla"

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
          Status(500)(toError(e, s"Error al recuperar $tabla_plantilla", 100))
        }
    }
  }

  def deletePlantilla( id: Int) = Action.async { implicit request =>

    try {
      val parametros = ArrayBuffer.empty[Any]

      var sql = s"delete from $tabla_plantilla where id=$id returning id"

      pool.sendPreparedStatement(sql, parametros).map(qr => {
        Ok(toRespuesta(toJson(qr.rows, qr.rows.get.columnNames)))
      })
    }
    catch {
      case e: Exception =>
        Future {
          Status(500)(toError(e, s"Error al borrar $tabla_plantilla $id", 100))
        }
    }
  }

  implicit val rdsPostPlantilla = (
    (__ \ 'nombre).read[String] and
      (__ \ 'descripcion).read[String] and
      (__ \ 'plantilla).read[String] and
      (__ \ 'habilitado).read[Boolean]
    ) tupled

  def postPlantilla() = Action.async { implicit request =>
    request.body.asJson.map { json =>
      json.validate[(String, String, String, Boolean)](rdsPostPlantilla).map {
        case (nombre, descripcion, plantilla,  habilitado) =>
          val sql = s"insert into $tabla_plantilla (nombre, descripcion, plantilla, habilitado) values (?,?,?, ?) returning id"
          val parametros = ArrayBuffer.empty[Any]
          parametros += nombre
          parametros += descripcion
          parametros += plantilla
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

  implicit val rdsPutPlantilla = (
    (__ \ 'id).read[Int] and
      (__ \ 'nombre).read[String] and
      (__ \ 'descripcion).read[String] and
      (__ \ 'plantilla).read[String] and
      (__ \ 'habilitado).read[Boolean]
    ) tupled

  def putPlantilla() = Action.async { implicit request =>
    request.body.asJson.map { json =>
      json.validate[(Int, String, String, String, Boolean)](rdsPutPlantilla).map {
        case (id, nombre, descripcion, plantilla, habilitado) =>
          val sql = s"update $tabla_plantilla set nombre=?, descripcion=?, plantilla = ?, habilitado = ? where id = ? returning id"
          val parametros = ArrayBuffer.empty[Any]
          parametros += nombre
          parametros += descripcion
          parametros += plantilla
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
