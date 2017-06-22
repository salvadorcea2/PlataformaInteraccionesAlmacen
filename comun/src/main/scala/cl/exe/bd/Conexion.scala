package cl.exe.bd

import cl.exe.config.Configuracion.config
import com.github.mauricio.async.db.QueryResult
import com.github.mauricio.async.db.postgresql.util.URLParser
import com.github.mauricio.async.db.postgresql.PostgreSQLConnection
import com.github.mauricio.async.db.Connection

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._
import scala.io.Source
/**
  * Created by utaladriz on 21-06-17.
  */
object Conexion {

  implicit val ec = ExecutionContext.global
  def conexion() : Connection = {
    val hostPostgres = config.getString("servidor.postgres.host")
    val usrPostgres = config.getString("servidor.postgres.usuario")
    val passPostgres = config.getString("servidor.postgres.password")
    val dbPostgres = config.getString("servidor.postgres.basedatos")
    println(hostPostgres)
    val configuration = URLParser.parse(s"jdbc:postgresql://$hostPostgres/$dbPostgres?user=$usrPostgres&password=$passPostgres")
    new PostgreSQLConnection(configuration)
  }

  def conectar()(implicit conexion : Connection)  : Connection = {
     Await.result(conexion.connect, 30 seconds)
     conexion
  }


  def ejecutarSQL(sqlId : String, parametros : Seq[Any])(implicit conexion : Connection): Future[QueryResult]= {
    val source = Source.fromFile(s"sql/$sqlId")
    val sql = try source.mkString finally source.close

    conexion.sendPreparedStatement(sql, parametros)
  }


}
