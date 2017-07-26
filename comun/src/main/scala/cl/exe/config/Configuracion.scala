package cl.exe.config

import java.io.File
import java.net.InetAddress

import com.typesafe.config.{ConfigFactory, ConfigValueFactory}

/**
  * Created by utaladriz on 21-06-17.
  */
object Configuracion {
  val config =  ConfigFactory.parseFile(new File("aplicacion.conf")). withValue("akka.remote.netty.tcp.bind-hostname", ConfigValueFactory.fromAnyRef(java.net.InetAddress.getLocalHost().getHostAddress())).withFallback(ConfigFactory.load()).resolve()


}
