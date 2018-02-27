package cl.exe.test

/**
  * Created by utaladriz on 17-07-17.
  */

import org.apache.poi.ss.usermodel._
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.{File, FileInputStream, FileNotFoundException, IOException}

import org.scalatest.FlatSpec

import scala.io.Source

class PoiTest extends FlatSpec {

  //Validaciones
  //Validar encabezados de la primera fila.
  //Primera columna de datos validar el id de trámite
  //Id de trámite debe estar asociado a su institución
  //Validar columna
  //Distribuir equitativamente entre fecha de inicio y fecha de termino
  //Validar que total sea equivalente a las sumas

  "probando" should "true" in {
    val linea = "[2018-02-12T02:45:36-04:00] 219.161.87.191 apis.digital.gob.cl \"CONNECT /v1/usuarios/50/\" 200 \"AM002 de8cb999-f1e5-4e66-9f67-d49e19940d9e"
    val patron = "\\[(\\d{4}\\-\\d{2}\\-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\-\\d{2}:\\d{2})\\]\\s(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})\\s([[:alnum:]|\\.]+)\\s\\\"([[:alnum:]]+)\\s([[:print:]]+)\\\"\\s(\\d{3})\\s\\\"([[:alnum:]]+)\\s([[:print:]]+)\\\"".r
    patron.findAllIn(linea).matchData foreach {
      m => {
        val fecha = sdf.parse(m.group(1).replace('T', ' ').substring(0, 18))
        val hostRemoto = m.group(2)
        val urlBase = m.group(3)
        val metodo = m.group(4)
        val urlRelativa = m.group(5)
        val status = m.group(6)
        val origen = m.group(7)
        val transaccion = m.group(8)
      }
    }

  }
}
