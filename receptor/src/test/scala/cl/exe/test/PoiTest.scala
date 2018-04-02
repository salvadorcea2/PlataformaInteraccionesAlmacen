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


  }
}
