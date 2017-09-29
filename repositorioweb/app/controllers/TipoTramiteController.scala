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
class TipoTramiteController @Inject()(cc: ControllerComponents)(implicit config: Configuration) extends AbstractController(cc) {

  val tabla = "tipo_tramite"

  def get(inicio: Int, cuantos: Int, orden: String, tipoOrden: String, habilitado: Option[Boolean], id: Option[Int], nombre: Option[String], institucion_id: Option[Int], codigo_pmg : Option[String]) = Action.async { implicit request =>

    try {
      val parametros = ArrayBuffer.empty[Any]


      var sql = s"select id, nombre, descripcion, institucion_id, codigo_pmg, url,  habilitado from $tabla"
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
      institucion_id.foreach(i => {
        parametros += i
        sql = s"$sql $clausula institucion_id = ?"
        sqlCuantos = s"$sqlCuantos $clausula institucion_id = ?"
        clausula = "and"
      })
      codigo_pmg.foreach(c => {
        parametros += c
        sql = s"$sql $clausula codigo_pmg = ?"
        sqlCuantos = s"$sqlCuantos $clausula codigo_pmg = ?"
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
      (__ \ 'descripcion).read[String] and
      (__ \ 'institucion_id).read[Int] and
      (__ \ 'codigo_pmg).read[String] and
      (__ \ 'url).read[String] and
      (__ \ 'habilitado).read[Boolean]
    ) tupled

  def post() = Action.async { implicit request =>
    request.body.asJson.map { json =>
      json.validate[(String, String, Int, String, String, Boolean)](rdsPost).map {
        case (nombre, descripcion, institucion, codigo, url, habilitado) =>
          val sql = s"insert into $tabla (nombre, descripcion, institucion_id, codigo_pmg, url, habilitado) values (?,?,?,?,?,?) returning id"
          val parametros = ArrayBuffer.empty[Any]
          parametros += nombre
          parametros += descripcion
          parametros += institucion
          parametros += codigo
          parametros += url
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
      (__ \ 'institucion_id).read[Int] and
      (__ \ 'codigo_pmg).read[String] and
      (__ \ 'url).read[String] and
      (__ \ 'habilitado).read[Boolean]
    ) tupled

  def put() = Action.async { implicit request =>
    request.body.asJson.map { json =>
      json.validate[(Int,String, String, Int, String, String , Boolean)](rdsPut).map {
        case (id, nombre, descripcion, institucion, codigo, url, habilitado) =>
          val sql = s"update $tabla set nombre=?, descripcion=?, institucion_id = ?, codigo_pmg=?, url=?, habilitado = ? where id = ? returning id"
          val parametros = ArrayBuffer.empty[Any]
          parametros += nombre
          parametros += descripcion
          parametros += institucion
          parametros += codigo
          parametros += url
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

  val tabla_factor_historico = "tipo_tramite_factor_historico"

  def getHistorico(idTipoTramite: Int, inicio: Int, cuantos: Int, orden: String, tipoOrden: String, id: Option[Int], canal_id: Option[Int], fecha_inicio : Option[String], fecha_termino : Option[String]) = Action.async { implicit request =>

    try {
      val parametros = ArrayBuffer.empty[Any]


      var sql = s"select id, tipo_tramite_id, canal_id, factor, fecha_creacion from $tabla_factor_historico where tipo_tramite_id = ?"
      var sqlCuantos = s"select count(*) from $tabla_factor_historico  where tipo_tramite_id = ?"

      parametros += idTipoTramite

      var clausula = "and"
      id.foreach(i => {
        parametros += i
        sql = s"$sql $clausula id = ?"
        sqlCuantos = s"$sqlCuantos $clausula id = ?"
        clausula = "and"
      })
      canal_id.foreach(c => {
        parametros += c
        sql = s"$sql $clausula canal_id = ?"
        sqlCuantos = s"$sqlCuantos $clausula canal_id = ?"
        clausula = "and"
      })
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
          Status(500)(toError(e, s"Error al recuperar $tabla_factor_historico", 100))
        }
    }
  }

  val tabla_factor = "tipo_tramite_factor"

  def getFactor(idTipoTramite: Int, inicio: Int, cuantos: Int, orden: String, tipoOrden: String, id: Option[Int], canal_id: Option[Int]) = Action.async { implicit request =>

    try {
      val parametros = ArrayBuffer.empty[Any]


      var sql = s"select id, tipo_tramite_id, canal_id, factor from $tabla_factor where tipo_tramite_id = ?"
      var sqlCuantos = s"select count(*) from $tabla_factor  where tipo_tramite_id = ?"


      parametros += idTipoTramite

      var clausula = "and"
      id.foreach(i => {
        parametros += i
        sql = s"$sql $clausula id = ?"
        sqlCuantos = s"$sqlCuantos $clausula id = ?"
        clausula = "and"
      })
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

  def deleteFactor(id: Int) = Action.async { implicit request =>

    try {
      val parametros = ArrayBuffer.empty[Any]

      var sql = s"delete from $tabla_factor where id=$id returning id"

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



  implicit val rdsPostFactor = (
    (__ \ 'tipo_tramite_id).read[Int] and
      (__ \ 'canal_id).read[Int] and
      (__ \ 'factor).read[Double]
    ) tupled

  def postFactor = Action.async { implicit request =>
    request.body.asJson.map { json =>
      json.validate[(Int, Int, Double)](rdsPostFactor).map {
        case (tipo_tramite_id, canal_id, factor) =>
          val sql = s"insert into $tabla_factor (tipo_tramite_id, canal_id, factor) values (?,?,?) returning id"
          var sqlHistorico = s"insert into $tabla_factor_historico (tipo_tramite_id, canal_id, factor) values (?,?,?)"
          val parametros = ArrayBuffer.empty[Any]
          parametros += tipo_tramite_id
          parametros += canal_id
          parametros += factor
          for {
          i <- pool.sendPreparedStatement(sqlHistorico, parametros)
          f <-  pool.sendPreparedStatement(sql, parametros).map(qr => {
          Ok (toRespuesta (toJson (qr.rows, qr.rows.get.columnNames)))
          } )
          } yield f
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

  implicit val rdsPutFactor = (
    (__ \ 'id).read[Int] and
      (__ \ 'tipo_tramite_id).read[Int] and
      (__ \ 'canal_id).read[Int] and
      (__ \ 'factor).read[Double]
    ) tupled

  def putFactor = Action.async { implicit request =>
    request.body.asJson.map { json =>
      json.validate[(Int, Int, Int, Double)](rdsPutFactor).map {
        case (id, tipo_tramite_id, canal_id, factor) =>
          val sqlHistorico = s"insert into $tabla_factor_historico (tipo_tramite_id, canal_id, factor) values (?,?,?) returning id"
          val sql = s"update $tabla_factor set tipo_tramite_id=?, canal_id=?, factor=? where id = ? returning id"
          val parametros = ArrayBuffer.empty[Any]
          parametros += tipo_tramite_id
          parametros += canal_id
          parametros += factor
          parametros += id
          val parametrosHistorico = ArrayBuffer.empty[Any]
          parametrosHistorico += tipo_tramite_id
          parametrosHistorico += canal_id
          parametrosHistorico += factor
          for {
          i <- pool.sendPreparedStatement(sqlHistorico, parametrosHistorico)
          f <- pool.sendPreparedStatement(sql, parametros).map(qr => {
          Ok (toRespuesta (toJson (qr.rows, qr.rows.get.columnNames)))
          } )
          } yield f
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
