package cl.exe.main

import java.net.InetAddress

import akka.actor.{Actor, ActorLogging, ActorSystem, PoisonPill, Props}
import akka.cluster.Cluster
import akka.cluster.singleton.{ClusterSingletonManager, ClusterSingletonManagerSettings}
import akka.event.LoggingReceive
import cl.exe.cluster.ClusterListener
import cl.exe.config.Configuracion.config
import cl.exe.dao.{Cache, ReceptorDAO}
import cl.exe.mensaje.Mensaje
import com.typesafe.scalalogging.LazyLogging
import cl.exe.actores.{AdministradorActor, EjecutorActor, ReceptorActor}

import scala.concurrent.ExecutionContext
import scala.util.Random

object ReceptorMain extends App with LazyLogging {
  logger.info("Inicializando Receptor")
  Cache.refrescar
  val clusterName = config.getString("clustering.cluster.name")
  implicit val ec = ExecutionContext.global
  implicit val system = ActorSystem(clusterName, config)
  val clusterListener = system.actorOf(Props[ClusterListener], name = "clusterListener")
  Cluster(system) registerOnMemberUp {
    logger.info("Cluster iniciado")
    system.actorOf(
    ClusterSingletonManager.props(
      singletonProps = Props(classOf[AdministradorActor]),
      terminationMessage = PoisonPill,
      settings = ClusterSingletonManagerSettings(system).withRole("administrador")),
    name = "administrador")
    system.actorOf(Props[EjecutorActor], name="ejecutor")
    inicializarReceptores
  }


  def crearTodos(): Unit = {
    ReceptorDAO.obtenerTodos.map(_.map(_.foreach(receptor => {
      ReceptorActor(system, receptor)
    })))
  }

  def crearReceptor(idReceptor: Integer): Unit = {
    ReceptorDAO.obtenerPorId(idReceptor).map(_.map(receptor => {
      ReceptorActor(system, receptor)
    }))
  }

  def inicializarReceptores = {
    val receptores = config.getIntList("receptor.receptores")
    if (!receptores.isEmpty)
      receptores.forEach(crearReceptor(_))
    else
      crearTodos()
  }

}
