package cl.exe.main

import cl.exe.bd.Conexion.{ejecutarSQL, pool}
import cl.exe.config.Configuracion.config
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.ExecutionContext

/**
  * Created by utaladriz on 14-07-17.
  */
object ComunMain extends App with LazyLogging {
  implicit val ec = ExecutionContext.global
  println(config.getString("servidor.postgres.usuario"))
  ejecutarSQL("interaccion_dwh.insert.sql", Array(166)).map(qr => {
    println("Ok")
  })

  println("Waiting")
  System.in.read()
  while ((pool.queued.size > 0) || (pool.inUse.size > 0))
    println(s"Waiting! ${pool.queued.size} ${pool.inUse.size}")
  pool.close

}
