package test

import java.io._
import java.sql.Timestamp
import java.text.SimpleDateFormat

import com.github.mauricio.async.db.{Connection, QueryResult}
import org.scalatest.FlatSpec

import scala.io.{BufferedSource, Source}
import cl.exe.bd.Conexion._
import com.github.mauricio.async.db.postgresql.PostgreSQLConnection

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._


/**
  * Created by utaladriz on 21-06-17.
  */
class TestBD extends FlatSpec {




  "probando" should "true" in {
      ejecutarSQL("interaccion_dwh.insert.sql", Array(166)).map(qr => {
        println("Ok")
      })

    println("Waiting")
    System.in.read()
    while ((pool.queued.size > 0) || (pool.inUse.size > 0))
      println(s"Waiting! ${pool.queued.size} ${pool.inUse.size}")
    pool.close
  }
}
