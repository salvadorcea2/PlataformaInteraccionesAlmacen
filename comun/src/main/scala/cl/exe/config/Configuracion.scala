package cl.exe.config

import java.io.File
import com.typesafe.config.ConfigFactory

/**
  * Created by utaladriz on 21-06-17.
  */
object Configuracion {
  val config =  ConfigFactory.parseFile(new File("aplicacion.conf")).withFallback(ConfigFactory.load()).resolve()


}
