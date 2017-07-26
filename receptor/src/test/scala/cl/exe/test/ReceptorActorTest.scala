package cl.exe.test

/**
  * Created by utaladriz on 12-07-17.
  */
import akka.actor.{ActorSystem, Props}
import akka.cluster.Cluster
import akka.testkit.{ImplicitSender, TestActors, TestKit}
import cl.exe.actores.{ReceptorActor, ReceptorArchivoActor}
import cl.exe.config.Configuracion.config
import cl.exe.dao.ReceptorDAO
import com.typesafe.scalalogging.LazyLogging
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._

class ReceptorActorTest() extends TestKit(ActorSystem(config.getString("clustering.cluster.name"))) with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll with LazyLogging {

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "Un actor ReceptorArchivo " must {

    "procesar archivos " in {
      implicit val ec = ExecutionContext.global
      within(60000 millis) {
        val receptor = Await.result(ReceptorDAO.obtenerPorId(1), 2000 millis)
        logger.info(s"${receptor.get}")
        ReceptorDAO.obtenerPorId(1).map(_.map(receptor => {

          Cluster(system) registerOnMemberUp {
            logger.info("Cluster UP *************")
            val receptorActor = system.actorOf(
              Props(classOf[ReceptorArchivoActor], receptor),
              name = receptor.nombre)
            receptorActor ! "hello world"
          }

        }))
        expectMsg("hello world")
      }

    }

  }
}