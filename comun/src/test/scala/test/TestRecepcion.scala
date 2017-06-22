package test

import java.io._
import java.sql.Timestamp
import java.text.SimpleDateFormat

import com.github.mauricio.async.db.{Connection, QueryResult}
import org.scalatest.FlatSpec

import scala.io.Source
import cl.exe.bd.Conexion._
import com.github.mauricio.async.db.postgresql.PostgreSQLConnection

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._


/**
  * Created by utaladriz on 21-06-17.
  */
class TestRecepcion extends FlatSpec {

  /*
  •    /Cartas - Carta de aviso de término de contrato
  •    /Certificados - Certificados de antecedentes y cumplimiento de obligaciones
  •    /Constancia - Constancia laboral respecto a un trabajador
  •    /Paritario - Constitución de comité paritario de higiene y seguridad
  •    /ParitarioFaena - igual al anterior, pero para faenas en régimen de subcontratación
  •    /Centralizacion - Solicitud de centralización de documentación
  •    /Multas - información de multas cursadas
  •    /IntermediarioAgricola - Declaración jurada para registrar trabajadores de intermediario agricola
  •    /ContratoMenor - Registro de datos de contrato de menores de edad
  •    /TrabCasaParticular - Registro de datos de contrato de trabajador de casa particular
  */


  def getListOfFiles(dir: String): List[File] = {
    val d = new File(dir)
    if (d.exists && d.isDirectory) {
      d.listFiles.filter(_.isFile).toList
    } else {
      List[File]()
    }
  }

  def insertarTramite(parametros: Seq[Any])(implicit conexion: Connection): Unit = {

  }

  "probando" should "true" in {

    implicit val con = conexion()
    conectar()

    val patron = "^(.*)\\sGET\\s.*(Cartas|Certificados|Constancia|ParitarioFaena|Paritario|Centralizacion|Multas|IntermediarioAgricola|ContratoMenor|TrabCasaParticular).*(Inicio|Genera).*".r

    val archivos = getListOfFiles("logs")
    var lineas = 0
    var inserciones = 0
    val inicio = System.currentTimeMillis()
    val receptor: Long = 1
    val sf = new SimpleDateFormat("yyyy-MM-DD HH:mm:ss")
    for (archivo <- archivos) {

      val future: Future[QueryResult] = ejecutarSQL("recepcion.insert.sql", Array(receptor, "RECEPCIONADO", archivo.getAbsolutePath))
      val mapResult:Future[Int] = future.map(qr => qr.rows.get(0)("id").asInstanceOf[Int])

        val recepcion = Await.result(mapResult, 30 seconds)
        println(s" ${archivo.getName} ${recepcion}")
        val source = Source.fromFile(archivo, "ISO-8859-1")
        for (linea <- source.getLines) {
          lineas = lineas + 1

          patron.findAllIn(linea).matchData foreach {
            m => {
              inserciones = inserciones + 1
              val tipoTramite = m.group(2) match {
                case "Cartas" => 1L
                case "Certificados" => 2L
                case "Constancia" => 3L
                case "Paritario" => 4L
                case "ParitarioFaena" => 5L
                case "Centralizacion" => 6L
                case "Multas" => 7L
                case "IntermediarioAgricola" => 8L
                case "ContratoMenor" => 9L
                case "TrabCasaParticular" => 10L
              }
              val tipoInteraccion = m.group(3) match {
                case "Inicio" => 1L
                case "Genera" => 2L
              }
              val fecha = new Timestamp(sf.parse(m.group(1).substring(0, 19)).getTime)

              val future2: Future[QueryResult] = ejecutarSQL("tramite.insert.sql", Array(recepcion, tipoTramite, fecha))
              val mapResult2: Future[Int] = future2.map(qr => qr.rows.get(0)("id").asInstanceOf[Int])
              val tramite = Await.result(mapResult2, 30 seconds)
              println(s" Tramite ${tramite}")

              val future3: Future[QueryResult] = ejecutarSQL("interaccion.insert.sql", Array(recepcion, fecha, tipoInteraccion, tramite))
              val mapResult3: Future[Int] = future3.map(qr => qr.rows.get(0)("id").asInstanceOf[Int])

              val interaccion = Await.result(mapResult3, 30 seconds)
              println(s" Interaccion ${interaccion}")

            }
          }
        }
        source.close()
      }
      val fin = System.currentTimeMillis()
      println((fin-inicio)/1000)
      println(lineas)
      println(inserciones)


    }
}


