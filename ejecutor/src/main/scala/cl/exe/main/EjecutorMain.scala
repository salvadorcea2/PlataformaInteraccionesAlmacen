package cl.exe.main

import java.net.InetAddress

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.event.LoggingReceive
import cl.exe.cluster.ClusterListener
import cl.exe.config.Configuracion.config
import cl.exe.mensaje.Mensaje

import scala.util.Random

object EjecutorMain extends App {
  println("Ejecutor ****")
  println(s"CLUSTER_IP ${System.getenv("CLUSTER_IP")}")
  println(InetAddress.getLocalHost.getHostName)
  println(config.getString("akka.remote.netty.tcp.bind-hostname"))
  println(config.getString("akka.remote.netty.tcp.bind-port"))
  println(config.getString("akka.remote.netty.tcp.hostname"))
  println(config.getString("akka.remote.netty.tcp.port"))
  println(config.getStringList("akka.cluster.seed-nodes"))
  try {
  val clusterName = config.getString("clustering.cluster.name")
  implicit val system = ActorSystem(clusterName, config)

  val clusterListener = system.actorOf(Props[ClusterListener], name = "clusterListener")

}
catch {
  case t:Throwable => println(t.getMessage)
  t.printStackTrace()

}

}
