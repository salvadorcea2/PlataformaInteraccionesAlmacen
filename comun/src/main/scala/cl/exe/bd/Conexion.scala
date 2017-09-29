package cl.exe.bd

import com.typesafe.scalalogging.LazyLogging
import cl.exe.bd.Conexion.ejecutarSQL
import cl.exe.config.Configuracion.config
import com.github.mauricio.async.db.pool.{ConnectionPool, PoolConfiguration}
import com.github.mauricio.async.db._
import com.github.mauricio.async.db.postgresql.util.URLParser
import com.github.mauricio.async.db.postgresql.PostgreSQLConnection
import com.github.mauricio.async.db.postgresql.pool.PostgreSQLConnectionFactory

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._
import scala.io.Source
import scala.reflect.ClassTag

/**
  * Created by utaladriz on 21-06-17.
  */



object Conexion extends LazyLogging {

  implicit val ec = ExecutionContext.global

  private val bdConfig =  new Configuration(
      username =  config.getString("servidor.postgres.usuario"),
      password = Some(config.getString("servidor.postgres.password")),
      database = Some(config.getString("servidor.postgres.basedatos")),
      host = config.getString("servidor.postgres.host"),
      port = config.getInt("servidor.postgres.puerto")
    )

  logger.debug(s"Configuración base de datos $bdConfig")
  private val factory = new PostgreSQLConnectionFactory( bdConfig )
  val pool = new ConnectionPool(factory, PoolConfiguration(
    maxObjects = config.getInt("servidor.postgres.conexiones"),
    maxIdle = config.getInt("servidor.postgres.max-idle"),
    maxQueueSize = config.getInt("servidor.postgres.cola"),
    validationInterval = config.getInt("servidor.postgres.mili-val")
  ))
  logger.debug(s"Pool activado $pool")
  def close() {
    pool.close
  }



  def obtenerSQL(sqlId:String) = {
    val source = Source.fromResource(s"sql/$sqlId")
    val sql = try source.mkString finally source.close
    sql

  }

  def ejecutarSQL(sqlId : String, parametros : Seq[Any]): Future[QueryResult]= {

    logger.debug(s"$sqlId $parametros")
    pool.sendPreparedStatement(obtenerSQL(sqlId), parametros)
  }


}

abstract class DAO[T,I] extends LazyLogging {
  import cl.exe.bd.Conexion._

  def limpiaH(s : String): String ={
    val s1 = s.trim
    s1.substring(1, s1.length-1) //saca las "
  }

  def hstoreToMap(h : String) : Map[String, String] = {
    h.split("=>|,").grouped(2).map { case Array(k, v) => limpiaH(k) -> limpiaH(v)}.toMap
  }

  def mapToHstore(m : Map[String, String]):String = {
    m.map({case (k,v)=> "\""+k+"\""+"=>"+"\""+v+"\""}).mkString(",")
  }


  def aEntidad(r : RowData) : T

  def obtenerPorId(id : I)(implicit tag: ClassTag[T], itag: ClassTag[I]) : Future[Option[T]] = {
    ejecutarSQL(s"${tag.runtimeClass.asInstanceOf[Class[T]].getSimpleName.toLowerCase}.selectid.sql", Array(id)).map(qr => {
      if (qr.rows.isEmpty)
         None
      else
         Some(aEntidad(qr.rows.get(0)))
    })
  }

  def obtenerTodos(implicit tag: ClassTag[T]) : Future[Option[IndexedSeq[T]]] = {
    ejecutarSQL(s"${tag.runtimeClass.asInstanceOf[Class[T]].getSimpleName.toLowerCase}.selecttodos.sql",Array[Int]()).map(qr => {
      if (qr.rows.isEmpty)
        None
      else
        Some(qr.rows.get.map(aEntidad(_)))
    })
  }

}
