package cl.exe.test

import cl.exe.bd.Conexion._
import cl.exe.dao.Cache
import org.scalatest.FlatSpec


/**
  * Created by utaladriz on 21-06-17.
  */
class TestBD extends FlatSpec {

  "probando" should "true" in {
    //pool.sendPreparedStatement("select * from dwh.generar_interacciones($1)",Array(166)).map(qr =>{
    //  println("Ok")
    //}
    //)
    pool.sendQuery("select 1").map(qr => {
      println(qr.rows.get(0)(0))
      pool.sendQuery("select 2").map(qr => {
        println(qr.rows.get(0)(0))
      })
    })
    println("Waiting")
    System.in.read()
    while ((pool.queued.size > 0) || (pool.inUse.size > 0))
      println(s"Waiting! ${pool.queued.size} ${pool.inUse.size}")
    pool.close
  }
}
