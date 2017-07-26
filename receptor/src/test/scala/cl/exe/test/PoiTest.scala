package cl.exe.test

/**
  * Created by utaladriz on 17-07-17.
  */

import org.apache.poi.ss.usermodel._
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.{File, FileInputStream, FileNotFoundException, IOException}

import org.scalatest.FlatSpec

class PoiTest extends FlatSpec {

  //Validaciones
  //Validar encabezados de la primera fila.
  //Primera columna de datos validar el id de trámite
  //Id de trámite debe estar asociado a su institución
  //Validar columna
  //Distribuir equitativamente entre fecha de inicio y fecha de termino
  //Validar que total sea equivalente a las sumas

  "probando" should "true" in {
    try {
      val excelFile = new FileInputStream(new File("sin_informacion2.xlsx"))
      val workbook = new XSSFWorkbook(excelFile)
      val datatypeSheet = workbook.getSheetAt(0)
      val iterator = datatypeSheet.iterator()
      iterator.next //Saltar titulos
      while (iterator.hasNext()) {

        val currentRow = iterator.next()
        val cellIterator = currentRow.iterator()

        while (cellIterator.hasNext()) {
          try {
            var cell = cellIterator.next()
          val id_tramite = if (cell.getCellTypeEnum == CellType.STRING) cell.getStringCellValue() else cell.getNumericCellValue.toInt.toString
          val nombre = cellIterator.next().getStringCellValue()
          cell = cellIterator.next()
          val periodoInicio = cellIterator.next.getDateCellValue()
          val periodoFin = cellIterator.next.getDateCellValue()
          val total = cellIterator.next.getNumericCellValue()
          val canalWeb = cellIterator.next.getNumericCellValue()
          val presencial = cellIterator.next.getNumericCellValue()
          val callCenter = cellIterator.next.getNumericCellValue()
          val otros = cellIterator.next.getNumericCellValue()

          println(s"id=$id_tramite nombre=$nombre periodo=$periodoInicio $periodoFin total=$total cw=$canalWeb p=$presencial cc=$callCenter o=$otros")
        }
          catch {
            case e : Exception =>
              println("FIN???")
          }

        }
      }
    } catch {
      case e: Exception => e.printStackTrace()
    }
  }

}
