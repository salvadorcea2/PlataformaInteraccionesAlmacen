package cl.exe.actores

import java.io.{File, FileInputStream, PrintWriter}
import java.nio.file.Paths
import java.sql.{Date, Timestamp}
import java.text.SimpleDateFormat
import java.util.{Base64, Calendar, GregorianCalendar}

import akka.actor._
import akka.cluster.{Cluster, Member, MemberStatus}
import akka.cluster.ClusterEvent._
import akka.cluster.singleton.{ClusterSingletonProxy, ClusterSingletonProxySettings}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes.Success
import akka.http.scaladsl.model.headers.{Authorization, OAuth2BearerToken}
import akka.http.scaladsl.model._
import akka.pattern.{ask, pipe}
import akka.util.ByteString
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
import com.markatta.akron.{CronExpression, CronTab}
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.joda.time.{DateTime, Days, Months, Period}

import scala.collection.mutable
import scala.concurrent.{Await, Future}
import scala.io.Source
import scala.concurrent.duration._
import scala.util.{Failure, Success}
import spray.json._
import DefaultJsonProtocol._
import akka.stream.scaladsl.FileIO
import cl.exe.main.ReceptorMain.{materializer, system}
import org.apache.commons.compress.archivers.{ArchiveException, ArchiveStreamFactory}
import org.apache.commons.compress.archivers.tar.{TarArchiveEntry, TarArchiveInputStream}

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
      log.info("Generando DWH")

      log.info(recepcion.propiedades.get("generadores").getOrElse("Sin generadores"))

      val generadores = recepcion.propiedades.get("generadores").foreach{g =>
        val generadores = g.split(";")
      for (generador <- generadores) {
        log.info(s"Invocando generado $generador")
        var future: Future[QueryResult] = pool.sendQuery(s"select * from ${generador.trim}(${recepcion.id})")
        Await.result(future, 30 seconds)
      }
      }
      log.info("DWH Generado")
      rec ! recepcion.copy(estado = "finalizada")
      context.actorSelection("/user/correo").resolveOne(15 seconds).foreach(_ ! recepcion)
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
  val sdf = new SimpleDateFormat("yyyyMMdd")

  def obtenerTramites() = Await.result(Cache.obtenerTipoTramites(), 30 seconds).getOrElse(Map[String, TipoTramite]().empty)

  def obtenerFactores() = {
    val f = new scala.collection.mutable.HashMap[String, scala.collection.mutable.HashMap[Int, Double]].empty

    Await.result(ejecutarSQL("factores.select.sql", Array[Int]()).map(qr => {
      qr.rows.get.foreach(r=> {

        if (!f.contains(r("codigo_pmg").asInstanceOf[String])){
          f += (r("codigo_pmg").asInstanceOf[String]-> new mutable.HashMap[Int,Double]())
        }
        val m = f(r("codigo_pmg").asInstanceOf[String])
        m += (r("tipo_interaccion_id").asInstanceOf[Int] -> r("factor").asInstanceOf[Double])


      })
    }), 30 seconds)
    f.map(kv => (kv._1->kv._2.toMap)).toMap
  }

  def insertarFactores(recepcion: Recepcion) = Await.result(pool.sendQuery(s"""WITH r as (Select id from recepcion where id = ${recepcion.id})
        insert into recepcion_factor(recepcion_id, tipo_tramite_id, tipo_interaccion_id, factor)
        select (select id from r), tipo_tramite_id, tipo_interaccion_id, factor from tipo_tramite_factor """), 30 seconds)

  def insertar(recepcion: Recepcion, tipoTramite: TipoTramite, periodicidad : Int, factor : Option[Map[Int, Double]],fechaMensual: DateTime, fechaAnual:DateTime, canal: Int, cantidad : Int, fechaDiaria : Option[DateTime] = None) : Int = {


    var inserciones = 0
    val mes = sdf.format(fechaMensual.toDate).toInt
    val anual = sdf.format(fechaAnual.toDate).toInt

    val factores = if (factor.isEmpty)
      Map(0->1.0)
    else
      factor.get


    val funcionTramite = periodicidad match {
      case 2 /* DIARIA */ =>
        val dia = sdf.format(fechaDiaria.get.toDate).toInt
        s"select * from dwh.insertar_tramite_diaria($dia,$canal,${tipoTramite.id},$cantidad, $mes,$anual)"
      case 4 /* MENSUAL */ => s"select * from dwh.insertar_tramite_mensual($mes,$canal,${tipoTramite.id},$cantidad, $anual)"
      case 5 /* ANUAL */ => s"select * from dwh.insertar_tramite_anual($anual,$canal,${tipoTramite.id},$cantidad)"

    }


    var futureTramite : Future[QueryResult] = pool.sendQuery(funcionTramite)
    Await.result(futureTramite.map(qr=>qr), 30 seconds)
    inserciones = inserciones+1

    factores.foreach{
      case (tipoInteraccion, valorFactor) =>
        val funcionInteraccion = periodicidad  match {
          case 2 /* DIARIA */ =>
            val dia = sdf.format(fechaDiaria.get.toDate).toInt
            s"select * from dwh.insertar_interaccion_diaria($dia,$tipoInteraccion,$canal,${tipoTramite.id}, ${(cantidad*valorFactor).toInt}, $mes, $anual)"
          case 4 /* MENSUAL */ => s"select * from dwh.insertar_interaccion_mensual($mes,$tipoInteraccion,$canal,${tipoTramite.id}, ${(cantidad*valorFactor).toInt}, $anual)"
          case 5 /* ANUAL */ => s"select * from dwh.insertar_interaccion_anual($anual,$tipoInteraccion,$canal,${tipoTramite.id}, ${(cantidad*valorFactor).toInt})"

        }
        var futureInteraccion : Future[QueryResult] = pool.sendQuery(funcionInteraccion)
        Await.result(futureInteraccion.map(qr=>qr), 30 seconds)
        inserciones = inserciones+1

    }
    inserciones

  }



  def procesamientoCompleto(receptor: ActorRef, errores: Int, recepcion: Recepcion) = {
    var estado = "procesada"
    if (errores > 0)
      estado = "procesada_errores"
    ejecutarSQL("recepcion_estado.update.sql", Array(recepcion.id, estado)).map(qr => {
      receptor ! recepcion.copy(estado = estado)
      if (errores == 0)
        dwh ! (recepcion, receptor)
      else
        context.actorSelection("/user/correo").resolveOne(15 seconds).foreach(_ ! recepcion)
    })
  }
}

class EjecutorLogActor extends EjecutorBaseActor {


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

class EjecutorExcelActor extends EjecutorBaseActor {

  val sdfExcel = new SimpleDateFormat("dd-MM-yyyy")


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
      var lineas = 0
      var inserciones = 0
      var errores = 0
      val tipoTramites = obtenerTramites()
      val factores = obtenerFactores()
      insertarFactores(recepcion)
      try {
        val headers = iterator.next
        val headerIterator = headers.cellIterator()
        var h = ""
        while (headerIterator.hasNext){
           h=h+headerIterator.next().getStringCellValue+","
        }
        if (h != "id_tramite_pmg,nombre_tramite_pmg,inicio,fin,total,presencial,web,callcenter,kiosko,mobile,")
          throw new Exception(h)

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
            val presencial = try {
              cell = cellIterator.next()

              if (cell.getCellTypeEnum == CellType.STRING) cell.getStringCellValue.trim.toInt else cell.getNumericCellValue().toInt
            }
            catch {
              case e: Exception =>
                0
            }
            val canalWeb = try {
              cell = cellIterator.next()
              if (cell.getCellTypeEnum == CellType.STRING) cell.getStringCellValue.trim.toInt else cell.getNumericCellValue().toInt
            }
            catch {
              case e: Exception =>
                0
            }
            val callCenter = try {
              cell = cellIterator.next()
              if (cell.getCellTypeEnum == CellType.STRING) cell.getStringCellValue.trim.toInt else cell.getNumericCellValue().toInt
            }
            catch {
              case e: Exception =>
                0
            }

            val kioskos = try {
              cell = cellIterator.next()
              if (cell.getCellTypeEnum == CellType.STRING) cell.getStringCellValue.trim.toInt else cell.getNumericCellValue().toInt
            }
            catch {
              case e: Exception =>
                0
            }

            val mobile = try {
              cell = cellIterator.next()
              if (cell.getCellTypeEnum == CellType.STRING) cell.getStringCellValue.trim.toInt else cell.getNumericCellValue().toInt
            }
            catch {
              case e: Exception =>
                0
            }

            val canales = Array(1, 2, 7, 9, 15)
            val totales = Array(presencial, canalWeb, callCenter, kioskos, mobile)
            val tipoTramite = tipoTramites.get(codigoPMG)
            val factor = factores.get(codigoPMG)

            if (tipoTramite.isEmpty) {
              bitacora(recepcion.id, "ERROR", s"Tipo de trámite $codigoPMG no existe en la fila ${currentRow.getRowNum + 1}", "procesamiento")
              errores = errores + 1
              error = true
            }
            else {
              val mascaraTipoTramite = RecepcionUtil.obtenerMascaraInstitucion(tipoTramite.get.institucionId)
              if ((mascaraUsuario._3 != 0 && (mascaraTipoTramite._3 != mascaraUsuario._3)) ||
                (mascaraUsuario._2 != 0 && (mascaraTipoTramite._2 != mascaraUsuario._2)) ||
                (mascaraUsuario._1 != 0 && (mascaraTipoTramite._1 != mascaraUsuario._1))) {
                error = true
                errores = errores + 1
                bitacora(recepcion.id, "ERROR", s"El usuario no está autorizado para procesar el trámite  $codigoPMG en la fila ${currentRow.getRowNum + 1}", "procesamiento")
              }
            }
            if (total != canalWeb + presencial + callCenter) {
              bitacora(recepcion.id, "ERROR", s"El total para el trámite $codigoPMG, no coincide con la sumatoria de los canales en la fila ${currentRow.getRowNum + 1}", "procesamiento")
              errores = errores + 1
              error = true
            }


            if (!error) {
              val fechaMensual = new DateTime(periodoInicio).withDayOfMonth(1)
              val fechaAnual = new DateTime(periodoInicio).withMonthOfYear(1).withDayOfMonth(1)
              val fechaFin = new DateTime(periodoFin)

              val meses = Months.monthsBetween(fechaMensual, fechaFin).getMonths
              log.info("MESES " + meses)
              val periodicidad = meses match {
                case 0 => 4 /* MENSUAL */
                case _ => 5 /* ANUAL */
              }
              log.info("PERIODICIDAD " + periodicidad)


              for (i <- 0 to 2) {
                val total = totales(i)
                val canal = canales(i)
                if (total >= 0)
                  periodicidad match {
                    case 4 => inserciones = inserciones + insertar(recepcion, tipoTramite.get, periodicidad, factor, fechaMensual, fechaAnual, canal, total)
                    case 5 => for (i <- 1 to (meses +1)) {
                      val cuantos = if (i != 12) total / 12 else total / 12 + total % 12
                      if (cuantos > 0)
                        inserciones = inserciones + insertar(recepcion, tipoTramite.get, 4, factor, new DateTime(fechaAnual.withMonthOfYear(i)), fechaAnual, canal, cuantos)
                    }
                  }

              }
            }

            lineas = lineas + 1

          }
          catch {
            case e: Exception =>
              log.error(e, "Error al procesar la linea")
              log.error("FIN???")
            //iterator.next()
          }

        }
      }
      catch {
        case e: Exception =>
          bitacora(recepcion.id, "ERROR", s"Los títulos de las columnas no cumplen con el estándar ${e.getMessage}", "procesamiento")
          errores = errores + 1
        //iterator.next()
      }



      log.info("Filas procesadas {} inserciones {}", lineas, inserciones)
      bitacora(recepcion.id, "INFO", s"Filas procesadas $lineas inserciones ${inserciones}", "procesada")

      workbook.close()
      procesamientoCompleto(rec, errores, recepcion)

  }
}


class EjecutorMdsActor extends EjecutorBaseActor {


  def receive = {
    case recepcion: Recepcion =>
      val rec = sender()
      log.info(recepcion.archivo)
      val tipoTramites = obtenerTramites()
      val factores = obtenerFactores()
      insertarFactores(recepcion)
      val file = new File(recepcion.archivo)
      val usuario = RecepcionUtil.obtenerUsuario(file)
      val mascaraUsuario = RecepcionUtil.obtenerMascaraUsuario(usuario)
      val codigoPMG = config.getString("receptor.PMGTramiteMDS")
      val tipoTramite = tipoTramites.get(codigoPMG)
      val factor = factores.get(codigoPMG)
      val s = new SimpleDateFormat("yyyyMMdd")
      var elementos = 0
      var inserciones = 0
      var errores = 0
      val fecha = new DateTime(sdf.parse(file.getName.split("\\.")(0)))
      log.info("Procesando fecha "+fecha.toString)
      val canales : Map[Int, mutable.Map[String, Int]] = Map(1-> new mutable.HashMap[String,Int](), 2 -> new mutable.HashMap[String,Int]())


      try {
      val jsonVal = Source.fromFile(recepcion.archivo).mkString.parseJson
      val contenido = jsonVal.asJsObject.fields("contenido").asInstanceOf[JsArray]
      for(elemento <- contenido.elements){
        try {
          elementos = elementos + 1
          val canal_mds = elemento.asJsObject.fields("id_canal").convertTo[String].toInt
          val cuantos = elemento.asJsObject.fields("total").convertTo[String].toInt
          val id_tramite = elemento.asJsObject.fields("total").convertTo[String]
          val canal = canal_mds match {
            case 1 => //Clave Única
              2  //WEB
            case 2 => //Clave Única
              2 //WEB
            case _ =>
              1

          }
          if (canales.get(canal).get.getOrElse(id_tramite,0) < cuantos)
            canales.get(canal).get .put(id_tramite,cuantos)
        }
        catch {
          case e: Exception =>
            errores = errores + 1
            bitacora(recepcion.id, "ERROR", s"Error en el contenido $elementos del JSON: ${e.getMessage}", "procesamiento")
        }

      }

      insertar(recepcion, tipoTramite.get, 2 /*PERIODICIDAD DIARIA*/ , factor, fecha.withDayOfMonth(1), fecha.withMonthOfYear(1).withDayOfMonth(1), 2 , canales.get(2).get.values.sum, Some(fecha))
      insertar(recepcion, tipoTramite.get, 2 /*PERIODICIDAD_DIARIA*/ , factor, fecha.withDayOfMonth(1), fecha.withMonthOfYear(1).withDayOfMonth(1), 1 ,  canales.get(1).get.values.sum, Some(fecha))

      inserciones = 2
      }
      catch {
        case e: Exception =>
          errores = errores + 1
          bitacora(recepcion.id, "ERROR", s"Error en el JSON: ${e.getMessage}", "procesamiento")
      }
      log.info("Elementos procesados {} inserciones {}", elementos, inserciones)
      bitacora(recepcion.id, "INFO", s"Elementos procesados $elementos inserciones ${inserciones}", "procesada")
      procesamientoCompleto(rec, errores, recepcion)
  }
}

class EjecutorInteroperabilidadActor extends EjecutorBaseActor {


  /*
  \[(\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}-\d{2}:\d{2})\]\s(\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3})\s([[:alnum:]|\.]+)\s\"([[:alnum:]]+)\s([[:print:]]+)\"\s(\d{3})\s\"([[:alnum:]]+)\s([[:print:]]+)\"
   [2018-02-06T16:34:24-04:00] 42.252.95.184 apis.digital.gob.cl "GET /v2/instituciones?ordenar=nombre,desc" 200 "AM008 d85d6e2c-92e2-4623-b937-1288666529da"
   */

  def receive = {
    case recepcion: Recepcion =>
      val rec = sender()
      val sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
      log.info(recepcion.archivo)
      val tipoTramites = obtenerTramites()
      val factores = obtenerFactores()
      insertarFactores(recepcion)
      val file = new File(recepcion.archivo)
      val usuario = RecepcionUtil.obtenerUsuario(file)
      val mascaraUsuario = RecepcionUtil.obtenerMascaraUsuario(usuario)
      var lineas = 0
      var inserciones = 0
      var errores = 0
      var elementos = 0
      val patron ="^\\[(\\d{4}\\-\\d{2}\\-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\-\\d{2}:\\d{2})\\]\\s(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})\\s(.*)\\s\\\"(\\w+)\\s(.*)\\\"\\s(\\d{3})\\s\\\"(\\w{5})\\s(.*)\\\"".r
        try {
        val buffer = Source.fromFile(recepcion.archivo)
        for (linea <- buffer.getLines()) {
          lineas = lineas + 1
          val it = patron.findAllIn(linea).matchData
          if (it.isEmpty){
            errores = errores + 1
            log.error("Error en la línea : {}. Error {} no calza con la expresión regular", lineas, linea)
            bitacora(recepcion.id, "ERROR", s"Error en la línea : $lineas. Error $linea no calza con la expresión regular", "procesamiento")
           }
          else
           it foreach {
            m => {
              try {
                val fecha = sdf.parse(m.group(1).replace('T', ' ').substring(0, 18))
                val hostRemoto = m.group(2)
                val servicio = m.group(3)
                val metodo = m.group(4)
                val urlRelativa = m.group(5)
                val status = m.group(6)
                val origen = m.group(7)
                val transaccion = m.group(8)

                /*INSERT INTO dwh.transaccion_interoperabilidad(
 fecha_log, host_remoto, servicio, metodo_http, uri, status_http, codigo_institucion_consumidora, id_transaccion, servicio_id, institucion_id)
VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)*/

                Await.result(ejecutarSQL("interoperabilidad.insert.sql", Array(fecha, hostRemoto, servicio, metodo, servicio + "/" + urlRelativa, status, origen, transaccion, 1, 1)), 30 seconds)
                inserciones = inserciones + 1
              }
              catch {
                case e : Exception =>
                  errores = errores + 1
                  bitacora(recepcion.id, "ERROR", s"Error en la línea : $lineas. Error ${e.getMessage}", "procesamiento")
              }



            }
          }

        }
      }
      catch {
        case e: Exception =>
          errores = errores + 1
          log.error("Error en el archivo {}", e.getMessage)
          bitacora(recepcion.id, "ERROR", s"Error en el archivo : ${e.getMessage}", "procesamiento")
      }
      log.info("Lineas procesados {} inserciones {}", lineas, inserciones)
      bitacora(recepcion.id, "INFO", s"Líneas procesadas $lineas inserciones ${inserciones}", "procesada")
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
    case recepcion@Recepcion(0, _, _, _, _, _, _, _) =>
      log.info(recepcion.archivo)
      val mascara = RecepcionUtil.obtenerMascaraUsuario(recepcion.usuarioId)
      ejecutarSQL("recepcion.insert.sql", Array(receptor.id, recepcion.archivo, recepcion.usuarioId, mascara._1, mascara._2, mascara._3)).map(qr => {
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
    val recepcion = Recepcion(0, receptor.id, null, "recepcionada", archivo, receptor.propiedades,RecepcionUtil.obtenerUsuario(new File(archivo)) , Array[Byte]())
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


class ReceptorMdsActor(receptor: Receptor) extends ReceptorActor(receptor) {


  case class MDSGet(fecha : Option[DateTime])
  val sdt = new SimpleDateFormat("yyyyMMdd")

  override def preStart = {
    super.preStart()

    if (config.getBoolean("receptor.recuperarTramiteMDS")) {
      val fechaInicio = new DateTime(sdt.parse(config.getString("receptor.inicioTramiteMDS")))
      system.scheduler.scheduleOnce(
        5000 milliseconds) {
        self ! MDSGet(Some(fechaInicio))
    }
    }

     receptor.propiedades.get("cron").foreach(cron => {
      context.actorSelection("/user/crontab").resolveOne( 5 seconds).map(crontab => {
        crontab ! CronTab.Schedule(self, MDSGet(None), CronExpression(cron))

      })
     })

  }

  override def receive = {
    case m:MDSGet =>
      val date = m.fecha.getOrElse(new DateTime().minusDays(1))
      val dia = sdt.format(date.toDate)
      log.info("Recuperando información MDS del día "+dia)
      m.fecha.foreach(f => {
        if (new org.joda.time.Duration(f,new DateTime().minusDays(1)).getStandardDays() > 1){
          system.scheduler.scheduleOnce(
            5000 milliseconds) {
            self ! MDSGet(Some(f.plusDays(1)))
          }

        }
      })

      val archivo = ""
      receptor.propiedades.get("urlAutenticacion").foreach(urlAutenticacion  => {
        val s = "{\"grant_type\":\"client_credentials\",\"client_id\":\"81\",\"client_secret\":\"YWvrCPJD\"}"
        val e = HttpEntity(contentType = ContentTypes.`application/json`, string = s)
        Http().singleRequest(HttpRequest(uri = urlAutenticacion, method = HttpMethods.POST, entity = e ))
          .onComplete {
            case scala.util.Success(response) =>
            response match {
            case HttpResponse(StatusCodes.OK, headers, entity, _) =>
              entity.dataBytes.runFold(ByteString(""))(_ ++ _).foreach { body =>
                 log.info(body.utf8String)
                 val jsonRes =  body.utf8String.parseJson.asInstanceOf[JsObject]
                 val token = jsonRes.fields("access_token").convertTo[String]
                receptor.propiedades.get("url").foreach(url  => {

                  val a = Authorization(OAuth2BearerToken(token))


                  Http().singleRequest(HttpRequest(uri = url+"/"+dia, method = HttpMethods.GET, headers = List(a)))
                    .onComplete{
                      case scala.util.Success(response) =>
                        response match {
                          case HttpResponse(StatusCodes.OK, headers, entity, _) =>
                            entity.dataBytes.runFold(ByteString(""))(_ ++ _).foreach { body =>
                              val jsonRes = body.utf8String.parseJson.asInstanceOf[JsObject]
                              log.info(body.utf8String)
                              val directorio = s"${receptor.propiedades.get("directorioProcesamiento").get}/${receptor.usuarioId}"
                              Paths.get(directorio).toFile.mkdirs()
                              val archivo =  directorio+ "/" + dia + ".json"
                              val pw = new PrintWriter(new File(archivo))
                              pw.write(body.utf8String)
                              pw.close
                              procesarArchivo(archivo)
                            }

                          case resp@HttpResponse(code, _, _, _) =>
                            log.info("Request failed, response code: " + code)
                            resp.discardEntityBytes()
                        }
                      case Failure(e)   => log.error(e.getMessage,e)
                    }
                })

                }
            case resp @ HttpResponse(code, _, _, _) =>
              log.info("Request failed, response code: " + code)
              resp.discardEntityBytes()
            }
            case Failure(e)   => log.error(e.getMessage,e)
          }

      })

    case ce:CronTab.Scheduled =>
       log.info("Scheduled")

    case e =>
      super.receive(e)
  }


}



class ReceptorInteroperabilidadActor(receptor: Receptor) extends ReceptorActor(receptor) {


  case class InteropGET(fecha : Option[DateTime])
  val sdt = new SimpleDateFormat("yyyy-MM-dd")

  import org.apache.poi.util.IOUtils
  import java.io.FileInputStream
  import java.io.FileNotFoundException
  import java.io.FileOutputStream
  import java.io.IOException
  import java.io.InputStream
  import java.io.OutputStream
  import java.util.zip.GZIPInputStream

  @throws[FileNotFoundException]
  @throws[IOException]
  @throws[ArchiveException]
  private def unTar(inputFile: File, outputDir: File) = {
    log.info(String.format("Untaring %s to dir %s.", inputFile.getAbsolutePath, outputDir.getAbsolutePath))
    var untaredFiles = new mutable.ArrayBuffer[File]()
    val is = new FileInputStream(inputFile)
    val debInputStream = new ArchiveStreamFactory().createArchiveInputStream("tar", is).asInstanceOf[TarArchiveInputStream]
    var entry : TarArchiveEntry = debInputStream.getNextEntry.asInstanceOf[TarArchiveEntry]
    while (entry != null)
     {
      val outputFile = new File(outputDir, entry.getName)
      if (entry.isDirectory) {
        log.info(String.format("Attempting to write output directory %s.", outputFile.getAbsolutePath))
        if (!outputFile.exists) {
          log.info(String.format("Attempting to create output directory %s.", outputFile.getAbsolutePath))
          if (!outputFile.mkdirs) throw new IllegalStateException(String.format("Couldn't create directory %s.", outputFile.getAbsolutePath))
        }
      }
      else {
        log.info(String.format("Creating output file %s.", outputFile.getAbsolutePath))
        val outputFileStream = new FileOutputStream(outputFile)
        IOUtils.copy(debInputStream, outputFileStream)
        outputFileStream.close()
      }
      untaredFiles += outputFile
      entry = debInputStream.getNextEntry.asInstanceOf[TarArchiveEntry]
    }
    debInputStream.close
    untaredFiles
  }

  /**
    * Ungzip an input file into an output file.
    * <p>
    * The output file is created in the output folder, having the same name
    * as the input file, minus the '.gz' extension.
    *
    * @param inputFile the input .gz file
    * @param outputDir the output directory file.
    * @throws IOException
    * @throws FileNotFoundException
    * @return The { @File } with the ungzipped content.
    */
  @throws[FileNotFoundException]
  @throws[IOException]
  private def unGzip(inputFile: File, outputDir: File) = {
    log.info(String.format("Ungzipping %s to dir %s.", inputFile.getAbsolutePath, outputDir.getAbsolutePath))
    val outputFile = new File(outputDir, inputFile.getName.substring(0, inputFile.getName.length - 3))
    val in = new GZIPInputStream(new FileInputStream(inputFile))
    val out = new FileOutputStream(outputFile)
    IOUtils.copy(in, out)
    in.close()
    out.close()
    outputFile
  }

  override def preStart = {
    super.preStart()



    receptor.propiedades.get("cron").foreach(cron => {
      context.actorSelection("/user/crontab").resolveOne( 5 seconds).map(crontab => {
        crontab ! CronTab.Schedule(self, InteropGET(None), CronExpression(cron))

      })
    })

  }

  override def receive = {
    case m:InteropGET =>
      val calendar = new GregorianCalendar()
      calendar.add(Calendar.DAY_OF_MONTH,-1)
      val dia = sdt.format(calendar.getTime)
      log.info("Recuperando información de logs de interoperabilidad del día "+dia)
      receptor.propiedades.get("url").foreach(url  => {


        Http().singleRequest(HttpRequest(uri = url + "?inicio=" + dia + "&fin=" + dia, method = HttpMethods.GET))
          .onComplete {
            case scala.util.Success(response) =>
              response match {
                case HttpResponse(StatusCodes.OK, headers, entity, _) =>
                  val directorio = s"${receptor.propiedades.get("directorioProcesamiento").get}/${receptor.usuarioId}"
                  val archivo =  dia + ".tar.gz"
                  Paths.get(directorio).toFile.mkdirs()
                  entity.dataBytes.runWith(FileIO.toFile(new File(directorio, archivo))).onComplete({
                    case scala.util.Success(r) =>
                      if (r.wasSuccessful) {
                        val tarFile = unGzip(new File(directorio, archivo), new File(directorio))
                        val archivos = unTar(tarFile, new File(directorio))
                        for (a <- archivos)
                          procesarArchivo(a.getAbsolutePath)
                      }

                    case Failure(e) =>
                      log.error(e, e.getMessage)

                  })

                case resp@HttpResponse(code, _, _, _) =>
                  log.info("Request failed, response code: " + code)
                  resp.discardEntityBytes()
              }
            case Failure(e) => log.error(e.getMessage, e)
          }
      })



    case ce:CronTab.Scheduled =>
      log.info("Scheduled")

    case e =>
      super.receive(e)
  }


}

case class Correo(de : String, para : Seq[String], asunto : String, texto : String )

class CorreoActor extends Actor with ActorLogging {

  def receive = {
    case Some(correo : Correo) =>
      val client_id = config.getString("receptor.correo.client_id")
      val client_secret = config.getString("receptor.correo.client_secret")
      val urlAutenticacion = config.getString("receptor.correo.autenticacion")
      val urlEnviar = config.getString("receptor.correo.enviar")
      val token_app = config.getString("receptor.correo.token_app")
      val s = "{\"grant_type\":\"client_credentials\",\"client_id\":\""+client_id+"\",\"client_secret\":\""+client_secret+"\", \"scope\":\"sendmail\"}"
      val e = HttpEntity(contentType = ContentTypes.`application/json`, string = s)
      Http().singleRequest(HttpRequest(uri = urlAutenticacion, method = HttpMethods.POST, entity = e ))
        .onComplete {
          case scala.util.Success(response) =>
            response match {
              case HttpResponse(StatusCodes.OK, headers, entity, _) =>
                entity.dataBytes.runFold(ByteString(""))(_ ++ _).foreach { body =>
                  log.info(body.utf8String)
                  val jsonRes =  body.utf8String.parseJson.asInstanceOf[JsObject]
                  val token = jsonRes.fields("access_token").convertTo[String]


                  val a = Authorization(OAuth2BearerToken(token))
                  val para = correo.para.map("\""+_+"\"").mkString("[",",","]")
                  val s = "{\"from\":\""+correo.de+"\", \"subject\":\""+Base64.getEncoder.encodeToString(correo.asunto.getBytes)+"\",\"body\":\""+Base64.getEncoder.encodeToString(correo.texto.getBytes)+"\",\"to\":"+para+",\"token_app\":\""+token_app+"\"}"
                  log.info(s)
                  val e = HttpEntity(contentType = ContentTypes.`application/json`, string = s)

                    Http().singleRequest(HttpRequest(uri = urlEnviar, method = HttpMethods.POST, entity=e, headers = List(a)))
                      .onComplete{
                        case scala.util.Success(response) =>
                          response match {
                            case HttpResponse(StatusCodes.OK, headers, entity, _) =>
                              entity.dataBytes.runFold(ByteString(""))(_ ++ _).foreach { body =>
                                val jsonRes = body.utf8String.parseJson.asInstanceOf[JsObject]
                                log.info(body.utf8String)
                              }

                            case resp@HttpResponse(code, _, _, _) =>
                              log.info("Request failed, response code: " + code)
                              resp.discardEntityBytes()
                          }
                        case Failure(e)   => log.error(e.getMessage,e)
                      }


                }
              case resp @ HttpResponse(code, _, _, _) =>
                log.info("Request failed, response code: " + code)
                resp.discardEntityBytes()
            }
          case Failure(e)   => log.error(e.getMessage,e)
        }


    case recepcion : Recepcion=>
      val sdt = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss")
      val de = config.getString("receptor.correo.de")
      val asunto ="Recepcion de interacciones "+recepcion.id
      val texto =""

      val f = for {
        para <- pool.sendPreparedStatement("select email from usuario where id = ?", Array(recepcion.usuarioId)).map(qr=>{
        qr.rows.head.map(row=>row("email").asInstanceOf[String])
        })

        correo <- pool.sendPreparedStatement("select etapa, nivel, log  from recepcion_bitacora where recepcion_id=? order by id desc", Array(recepcion.id)).map(qr=>{
        qr.rows.map(rs => {
          rs.map(row => s"""En la etapa ${row("etapa").asInstanceOf[String]} ${row("nivel").asInstanceOf[String]} ${row("log").asInstanceOf[String]}""").mkString("\n")
        }).map(Correo(de,para,asunto, _))
      })
      } yield correo

      f.pipeTo(self)


  }
}