package cl.exe.actores

import akka.actor.{Actor, ActorLogging}
import cl.exe.modelo.{Entidad, Recepcion}
import cl.exe.modelo.Entidad.EntidadId
import cl.exe.config.Configuracion
import akka.actor.{Actor, ActorLogging}


case class RecepcionFallida(mensaje : String, recepcion : Recepcion)
object  RegistroEjecutor

