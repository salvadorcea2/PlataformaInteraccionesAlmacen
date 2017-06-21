package cl.exe.test

import org.scalatest.FlatSpec

import scala.io.Source
import java.io._

import com.github.mauricio.async.db.Connection


/**
  * Created by utaladriz on 21-06-17.
  */
class Test extends FlatSpec {

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

  def insertarTramite(parametros : Seq[Any])(implicit conexion : Connection): Unit ={

  }

  "probando" should "true" in {
    val e1 = "2017-01-16 12:08:30 10.30.1.172 GET /registroempresa/ParitarioFaena/GeneraComprobante Rut=Nzk3MTk3MzAtMA%3D%3D&Razon=Q09OU1RSVUNUT1JBIEFWRUxMQU5FREEgTElNSVRBREE%3D&idSolComite=57298&TipCom=DE%20FAENA&Dom=AVDA%20FERROCARRIL%20150&fechaSol=29-05-2015&Trab=176&Com=PROVIDENCIA&token=ec6107d8-7f18-4065-87ce-d9f42a058ecf 80 - 10.30.4.69 Mozilla/5.0+(Windows+NT+10.0)+AppleWebKit/537.36+(KHTML,+like+Gecko)+Chrome/55.0.2883.87+Safari/537.36 200 0 0 265"
    val e2 = "2017-01-16 12:07:58 10.30.1.172 GET /registroempresa/Paritario/Inicio Rut=Nzk3MTk3MzAtMA%3D%3D&Razon=Q09OU1RSVUNUT1JBIEFWRUxMQU5FREEgTElNSVRBREE%3D&token=ec6107d8-7f18-4065-87ce-d9f42a058ecf 80 - 10.30.4.69 Mozilla/5.0+(Windows+NT+10.0)+AppleWebKit/537.36+(KHTML,+like+Gecko)+Chrome/55.0.2883.87+Safari/537.36 200 0 0 62"
    val patron = "^(.*)\\sGET\\s.*(Cartas|Certifcados|Constancia|ParitarioFaena|Paritario|Centralizacion|Multas|IntermediarioAgricola|ContratoMenor|TrabCasaParticular).*(Inicio|Genera).*".r

    val archivos = getListOfFiles("logs")
    var lineas = 0
    var inserciones = 0
    val inicio = System.currentTimeMillis()
    for (archivo <- archivos){
      println(archivo.getName)
      for (linea <- Source.fromFile(archivo,"ISO-8859-1").getLines) {
        lineas = lineas + 1
        if (lineas % 10000 == 0)
          println(lineas)
        patron.findAllIn(linea).matchData foreach {
          m => {
            inserciones=inserciones+1
            println(s"${m.group(1)} ${m.group(2)} ${m.group(3)}")
          }
        }
      }
    }
    val fin = System.currentTimeMillis()
    println((fin - inicio) / 1000)
    println(lineas)
    println(inserciones)

  }

}


