package cl.exe.actores

import java.io.{File, FileInputStream}
import java.sql.{Date, Timestamp}
import java.text.SimpleDateFormat

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, PoisonPill, Props, RootActorPath, Terminated}
import akka.cluster.{Cluster, Member, MemberStatus}
import akka.cluster.ClusterEvent._
import akka.cluster.singleton.{ClusterSingletonProxy, ClusterSingletonProxySettings}
import akka.pattern.{ask, pipe}
import cl.exe.modelo.{Entidad, Recepcion, Receptor, TipoTramite}
import cl.exe.modelo.Entidad.EntidadId
import cl.exe.modelo.TipoTramite
import cl.exe.bd.Conexion._
import com.typesafe.scalalogging.LazyLogging
import org.apache.camel.CamelContext
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.builder.RouteBuilder
import cl.exe.config.Configuracion.config
import cl.exe.actores._
import cl.exe.dao.Cache
import cl.exe.dao.ReceptorDAO.bitacora
import com.github.mauricio.async.db.QueryResult
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.joda.time.{DateTime, Days, Months}

import scala.collection.mutable
import scala.concurrent.{Await, Future}
import scala.io.Source
import scala.concurrent.duration._

/*
Agregar en la bitacora la info total del procesamiento
 */

object RecepcionUtil {

  def obtenerUsuario(file : File) : Int = {
    file.getParentFile().getName.toInt
  }
  def obtenerMascaraUsuario(usuario : Int) : (Int,Int,Int) = {
    var futureTramite: Future[QueryResult] = pool.sendQuery(s"select ministerio_id, subsecretaria_id, institucion_id from usuario where id=$usuario")
    Await.result(futureTramite.map(qr => {
      val row = qr.rows.get(0)
      (row(0).asInstanceOf[Int],row(1).asInstanceOf[Int], row(2).asInstanceOf[Int])
    }), 30 seconds)
  }

  def obtenerMascaraInstitucion( institucion : Int) : (Int,Int,Int) = {

    var futureTramite: Future[QueryResult] = pool.sendQuery(s"select subsecretaria.ministerio_id, subsecretaria.id, $institucion from subsecretaria, institucion where institucion.id=$institucion and subsecretaria.id = institucion.subsecretaria_id")
    Await.result(futureTramite.map(qr => {
      val row = qr.rows.get(0)
      (row(0).asInstanceOf[Int],row(1).asInstanceOf[Int], row(2).asInstanceOf[Int])
    }), 30 seconds)
  }

}

class DwhActor extends Actor with ActorLogging {
  def receive = {
    case (recepcion: Recepcion, rec: ActorRef) =>
      log.info("DWH Generado")
      /*
      log.info(recepcion.propiedades("generadores"));

      val generadores = recepcion.propiedades("generadores").split(";")
      for (generador <- generadores) {
        log.info(s"Invocando generado $generador")
        var future: Future[QueryResult] = pool.sendQuery(s"select * from ${generador.trim}(${recepcion.id})")
        Await.result(future, 30 seconds)
      }*/
      rec ! recepcion.copy(estado = "finalizada")
      context.parent ! PoisonPill

  }
}


class EjecutorActor extends Actor with ActorLogging {


  val administrador = context.actorOf(
    ClusterSingletonProxy.props(
      settings = ClusterSingletonProxySettings(context.system).withRole("administrador"),
      singletonManagerPath = "/user/administrador"
    ),
    name = "administradorProxy")

  override def preStart(): Unit =
    administrador ! RegistroEjecutor

  def receive = {
    case recepcion: Recepcion =>
      val ejecutor = recepcion.propiedades.get("ejecutor")
      log.debug(s"Creando Actor ejecutor $ejecutor para la recepcion ${recepcion.id}")
      val nombreClase = ejecutor.map(t => "cl.exe.actores.Ejecutor" + t.capitalize + "Actor")

      val a = nombreClase.map(n => {
        context.actorOf(
          Props(this.getClass.getClassLoader.loadClass(n)), name = "Ejecutor" + recepcion.id.toString
        )
      })

      a.foreach(_ forward recepcion)


  }
}

abstract class EjecutorBaseActor extends Actor with ActorLogging {
  val dwh = context.actorOf(Props[DwhActor], name = "dwh")

  def procesamientoCompleto(receptor: ActorRef, errores: Int, recepcion: Recepcion) = {
    var estado = "procesada"
    if (errores > 0)
      estado = "procesada_errores"
    ejecutarSQL("recepcion_estado.update.sql", Array(recepcion.id, estado)).map(qr => {
      receptor ! recepcion.copy(estado = estado)
      if (errores == 0)
        dwh ! (recepcion, receptor)
    })
  }
}

class EjecutorLogActor extends EjecutorBaseActor with ActorLogging {


  def receive = {
    case recepcion: Recepcion =>
      val rec = sender()
      log.info("Recepcion {} {}", recepcion.id, recepcion.archivo)
      val patron = "^(.*)\\sGET\\s.*(Cartas|Certificados|Constancia|ParitarioFaena|Paritario|Centralizacion|Multas|IntermediarioAgricola|ContratoMenor|TrabCasaParticular).*(Inicio|Genera).*".r
      val sf = new SimpleDateFormat("yyyy-MM-DD HH:mm:ss")
      var lineas = 0
      var inserciones = 0
      var errores = 0
      val source = Source.fromFile(recepcion.archivo, "ISO-8859-1")
      for (linea <- source.getLines) {
        lineas = lineas + 1
        try {
          patron.findAllIn(linea).matchData foreach {
            m => {
              inserciones = inserciones + 1

              val tipoTramite = m.group(2) match {
                case "Cartas" => 777
                case "Certificados" => 746
                case "Constancia" => 748
                case "Paritario" => 749
                case "ParitarioFaena" => 749
                case "Centralizacion" => 790
                case "Multas" => 789
                case "IntermediarioAgricola" => 750
                case "ContratoMenor" => 784
                case "TrabCasaParticular" => 799
              }
              val tipoInteraccion = m.group(3) match {
                case "Inicio" => 1
                case "Genera" => 2
              }
              val fecha = new Timestamp(sf.parse(m.group(1).substring(0, 19)).getTime)
              val future2: Future[QueryResult] = ejecutarSQL("tramite.insert.sql", Array(recepcion.id, tipoTramite, fecha, 0))
              val mapResult2: Future[Int] = future2.map(qr => qr.rows.get(0)("id").asInstanceOf[Int])
              val tramite = Await.result(mapResult2, 30 seconds)


              val future3: Future[QueryResult] = ejecutarSQL("interaccion.insert.sql", Array(recepcion.id, 1, fecha, tipoInteraccion, 2, tramite, 0))
              val mapResult3: Future[Int] = future3.map(qr => qr.rows.get(0)("id").asInstanceOf[Int])

              val interaccion = Await.result(mapResult3, 30 seconds)
            }
          }
        }
        catch {
          case e: Exception =>
            errores = errores + 1
            bitacora(recepcion.id, "ERROR", s"Error en la linea $lineas: ${e.getMessage}", "procesamiento")
        }

      }
      log.info("Lineas procesadas {} interacciones {}", lineas, inserciones)
      bitacora(recepcion.id, "INFO", s"Lineas procesadas $lineas tramites $inserciones interacciones $inserciones", "procesada")
      source.close()
      procesamientoCompleto(rec, errores, recepcion)

  }
}

class EjecutorExcelActor extends EjecutorBaseActor with ActorLogging {


  val sdf = new SimpleDateFormat("yyyyMMdd")
  val sdfExcel = new SimpleDateFormat("dd-MM-yyyy")






  def insertar(recepcion: Recepcion, tipoTramite: TipoTramite, periodicidad : Int, factor : Option[Map[Int, Double]], fechaMensual: DateTime, fechaAnual:DateTime, canal: Int, cantidad : Int) : Int = {


    var inserciones = 0
    val mes = sdf.format(fechaMensual.toDate).toInt
    val anual = sdf.format(fechaAnual.toDate).toInt

    val factores = if (factor.isEmpty)
      Map(0->1.0)
    else
        factor.get


    val funcionTramite = periodicidad match {
      case 4 /* MENSUAL */ => s"select * from dwh.insertar_tramite_mensual($mes,$canal,${tipoTramite.id},$cantidad, $anual)"
      case 5 /* ANUAL */ => s"select * from dwh.insertar_tramite_anual($anual,$canal,${tipoTramite.id},$cantidad)"

    }


    var futureTramite : Future[QueryResult] = pool.sendQuery(funcionTramite)
    Await.result(futureTramite.map(qr=>qr), 30 seconds)
    inserciones = inserciones+1

    factores.foreach{
      case (tipoInteraccion, valorFactor) =>
        val funcionInteraccion = periodicidad  match {
          case 4 /* MENSUAL */ => s"select * from dwh.insertar_interaccion_mensual($mes,$tipoInteraccion,$canal,${tipoTramite.id}, ${(cantidad*valorFactor).toInt}, $anual)"
          case 5 /* ANUAL */ => s"select * from dwh.insertar_interaccion_anual($anual,$tipoInteraccion,$canal,${tipoTramite.id}, ${(cantidad*valorFactor).toInt})"

        }
        var futureInteraccion : Future[QueryResult] = pool.sendQuery(funcionInteraccion)
        Await.result(futureInteraccion.map(qr=>qr), 30 seconds)
        inserciones = inserciones+1

    }
    inserciones

  }


  def receive = {
    case recepcion: Recepcion =>
      val rec = sender()
      val file = new File(recepcion.archivo)
      val usuario = RecepcionUtil.obtenerUsuario(file)
      val mascaraUsuario = RecepcionUtil.obtenerMascaraUsuario(usuario)

      val excelFile = new FileInputStream(file)
      val workbook = new XSSFWorkbook(excelFile)
      val datatypeSheet = workbook.getSheetAt(0)
      log.info("Recepcion {} {}", recepcion.id, recepcion.archivo)
      val iterator = datatypeSheet.iterator()
      iterator.next //Saltar titulos
       var lineas = 0
      var inserciones = 0
      var errores = 0
      val f = new scala.collection.mutable.HashMap[String, scala.collection.mutable.HashMap[Int, Double]].empty
      val f1 = Cache.obtenerTipoTramites()
      val tipoTramites = Await.result(f1, 30 seconds).getOrElse( Map[String, TipoTramite]().empty)



      val f2 = ejecutarSQL("factores.select.sql", Array[Int]()).map(qr => {
        qr.rows.get.foreach(r=> {

          if (!f.contains(r("codigo_pmg").asInstanceOf[String])){
            f += (r("codigo_pmg").asInstanceOf[String]-> new mutable.HashMap[Int,Double]())
          }
          val m = f(r("codigo_pmg").asInstanceOf[String])
          m += (r("tipo_interaccion_id").asInstanceOf[Int] -> r("factor").asInstanceOf[Double])


        })
      })


      Await.result(f2, 30 seconds)
      val factores = f.map(kv => (kv._1->kv._2.toMap)).toMap

      val f3 = pool.sendQuery(s"""WITH r as (Select id from recepcion where id = ${recepcion.id})
        insert into recepcion_factor(recepcion_id, tipo_tramite_id, tipo_interaccion_id, factor)
        select (select id from r), tipo_tramite_id, tipo_interaccion_id, factor from tipo_tramite_factor """)
      Await.result(f3, 30 seconds)

      while (iterator.hasNext()) {



          try {
            val currentRow = iterator.next()
            val cellIterator = currentRow.iterator()
            var cell = cellIterator.next()
            var error = false
            val codigoPMG = if (cell.getCellTypeEnum == CellType.STRING) cell.getStringCellValue() else cell.getNumericCellValue.toInt.toString
            log.info(s"Codigo PMG $codigoPMG")
            val nombre = cellIterator.next().getStringCellValue()
            cell = cellIterator.next()
            val periodoInicio = if (cell.getCellTypeEnum == CellType.STRING) sdfExcel.parse(cell.getStringCellValue()) else cell.getDateCellValue()
            cell = cellIterator.next()
            val periodoFin = if (cell.getCellTypeEnum == CellType.STRING) sdfExcel.parse(cell.getStringCellValue()) else cell.getDateCellValue()
            cell = cellIterator.next()
            val total = try {
              if (cell.getCellTypeEnum == CellType.STRING) cell.getStringCellValue.trim.toInt else cell.getNumericCellValue().toInt
            }
            catch {
              case e: Exception =>
                0
            }
            val presencial = try {cell = cellIterator.next()

                if (cell.getCellTypeEnum == CellType.STRING) cell.getStringCellValue.trim.toInt else cell.getNumericCellValue().toInt
              }
              catch {
                case e: Exception =>
                  0
              }
             val canalWeb = try {cell = cellIterator.next()
                if (cell.getCellTypeEnum == CellType.STRING) cell.getStringCellValue.trim.toInt else cell.getNumericCellValue().toInt
              }
              catch {
                case e : Exception =>
                  0
              }
            val callCenter = try {cell = cellIterator.next()
                if (cell.getCellTypeEnum == CellType.STRING) cell.getStringCellValue.trim.toInt else cell.getNumericCellValue().toInt
              }
              catch {
                case e: Exception =>
                  0
              }

              val canales = Array(1, 2, 7)
              val totales = Array(presencial, canalWeb, callCenter)
              val tipoTramite = tipoTramites.get(codigoPMG)
              val factor = factores.get(codigoPMG)

              if (tipoTramite.isEmpty) {
                bitacora(recepcion.id, "ERROR", s"Tipo de trámite $codigoPMG no existe en la fila ${currentRow.getRowNum +1 }", "procesamiento")
                errores = errores + 1
                error = true
              }
              else  {
                val mascaraTipoTramite = RecepcionUtil.obtenerMascaraInstitucion(tipoTramite.get.institucionId)
                if ((mascaraUsuario._3 != 0 && (mascaraTipoTramite._3 != mascaraUsuario._3)) ||
                  (mascaraUsuario._2 != 0 && (mascaraTipoTramite._2 != mascaraUsuario._2)) ||
                  (mascaraUsuario._1 != 0 && (mascaraTipoTramite._1 != mascaraUsuario._1))){
                  error = true
                  errores = errores + 1
                  bitacora(recepcion.id, "ERROR", s"El usuario no está autorizado para procesar el trámite  $codigoPMG en la fila ${currentRow.getRowNum+1}", "procesamiento")
                }
              }
              if (total != canalWeb + presencial + callCenter) {
                bitacora(recepcion.id, "ERROR", s"El total para el trámite $codigoPMG, no coincide con la sumatoria de los canales en la fila ${currentRow.getRowNum+1}", "procesamiento")
                errores = errores + 1
                error = true
              }


            if (!error) {
              val fechaMensual = new DateTime(periodoInicio).withDayOfMonth(1)
              val fechaAnual = new DateTime(periodoInicio).withMonthOfYear(1).withDayOfMonth(1)
              val fechaFin = new DateTime(periodoFin)

              val meses = Months.monthsBetween(fechaMensual, fechaFin).getMonths
              log.info("MESES "+meses)
              val periodicidad = meses match {
                case 0 => 4 /* MENSUAL */
                case _ => 5 /* ANUAL */
              }
              log.info("PERIODICIDAD "+periodicidad)




                for (i <- 0 to 2) {
                  val total = totales(i)
                  val canal = canales(i)
                  if (total >= 0)
                   periodicidad match {
                     case 4 => inserciones = inserciones + insertar(recepcion, tipoTramite.get, periodicidad,factor, fechaMensual, fechaAnual, canal,total)
                     case 5 => for (i <- 1 to 12 ){
                       val cuantos = if (i != 12) total / 12 else total/12 + total%12
                       if (cuantos > 0)
                          inserciones = inserciones + insertar(recepcion, tipoTramite.get, 4,factor, new DateTime(fechaAnual.withMonthOfYear(i)), fechaAnual, canal,cuantos)
                     }
                   }

                  }
                }

              lineas = lineas + 1

          }
          catch {
            case e: Exception =>
              log.error(e,"Error al procesar la linea")
              log.error("FIN???")
              //iterator.next()
          }

      }


      log.info("Filas procesadas {} inserciones {}", lineas, inserciones)
      bitacora(recepcion.id, "INFO", s"Filas procesadas $lineas inserciones ${inserciones}", "procesada")

      workbook.close()
      procesamientoCompleto(rec, errores, recepcion)

  }
}


class AdministradorActor extends Actor with ActorLogging {
  var ejecutores = IndexedSeq.empty[ActorRef]
  var turno = 0

  def receive = {
    case recepcion: Recepcion if (ejecutores.isEmpty && recepcion.estado == "recepcionada") =>
      sender() ! RecepcionFallida("Recepcion fallida", recepcion)

    case recepcion: Recepcion if (recepcion.estado == "recepcionada") =>
      log.info("Recepcion")
      ejecutores(turno % ejecutores.size) forward recepcion
      turno = (turno + 1) % ejecutores.size

    case RegistroEjecutor if !ejecutores.contains(sender()) =>
      log.info("Ejecutor registrado ")
      context watch sender()
      ejecutores = ejecutores :+ sender()

    case Terminated(a) =>
      ejecutores = ejecutores.filterNot(_ == a)
  }
}

abstract class ReceptorActor(receptor: Receptor) extends Actor with Entidad with ActorLogging {
  def id = receptor.id

  val administrador = context.actorOf(
    ClusterSingletonProxy.props(
      settings = ClusterSingletonProxySettings(context.system).withRole("administrador"),
      singletonManagerPath = "/user/administrador"
    ),
    name = "administradorProxy")

  override def preStart() = {
    log.info(s"Iniciando Receptor $id")
  }


  override def receive = {
    case recepcion@Recepcion(0, _, _, _, _, _, _) =>
      log.info(recepcion.archivo)
      val usuario = RecepcionUtil.obtenerUsuario(new java.io.File(recepcion.archivo))
      val mascara = RecepcionUtil.obtenerMascaraUsuario(usuario)
      ejecutarSQL("recepcion.insert.sql", Array(receptor.id, recepcion.archivo, usuario, mascara._1, mascara._2, mascara._3)).map(qr => {
        val id = qr.rows.get(0)("id").asInstanceOf[Int]
        recepcion.copy(id = id)
      }).pipeTo(self)

    case recepcion: Recepcion if (recepcion.estado == "recepcionada") =>
      log.info("Enviando al administrador")
      administrador ! recepcion
    case recepcion: Recepcion if (recepcion.estado != "recepcionada") =>
      log.info("Recepcion {} {}", recepcion.id, recepcion.estado)
  }

  def procesarArchivo(archivo: String) = {
    val recepcion = Recepcion(0, receptor.id, null, "recepcionada", archivo, receptor.propiedades, Array[Byte]())
    self ! recepcion
  }
}

object ReceptorActor extends LazyLogging {


  def apply(system: ActorSystem, receptor: Receptor) = {
    val tipo = receptor.propiedades.get("tipo")
    logger.debug(s"Creando Actor tipo $tipo para el receptor ${receptor.nombre}")
    val nombreClase = tipo.map(t => "cl.exe.actores.Receptor" + t.capitalize + "Actor")

    nombreClase.map(n => {
      system.actorOf(
        Props(this.getClass.getClassLoader.loadClass(n), receptor), name = receptor.nombre
      )
    })

  }
}

class ReceptorArchivoActor(receptor: Receptor) extends ReceptorActor(receptor) {
  val contextoCamel: CamelContext = new DefaultCamelContext
  val procesando = receptor.propiedades.get("sufijoProcesando").getOrElse(config.getString("receptor.sufijoProcesando"))


  override def preStart = {
    super.preStart()

    receptor.propiedades.get("directorioEntrada").map(directorio => {
      contextoCamel.addRoutes(new RouteBuilder {
        def configure = {
          //  from(s"file://$directorio?exclude=.*\\.$procesando&move=$${file:name}.$procesando")
          from(s"file://$directorio?recursive=true")
            .to(s"file://${receptor.propiedades.get("directorioProcesamiento").get}/")
            .process(exchange => procesarArchivo(receptor.propiedades.get("directorioProcesamiento").get + "/" + exchange.getIn().getHeader("CamelFileName").toString))
        }
      })
      contextoCamel.start
    })
  }


}
