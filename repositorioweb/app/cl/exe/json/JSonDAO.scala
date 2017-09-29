package cl.exe.json

import com.github.mauricio.async.db.{ResultSet, RowData}
import play.api.libs.json._
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

object JSonDAO {


 def toError(e : Exception, mensajeAlUsuario : String, codigo : Int) : JsValue = {
   Json.obj("mensaje"->mensajeAlUsuario,
            "codigo"->codigo,
            "excepcion"->e.getMessage
   )
 }

  def toRespuesta (json : JsValue):JsValue = {
    Json.obj("data"->json)
  }

  def toRespuesta (json : JsValue, inicio : Int, cuantos : Int, orden : String, tipoOrden : String, total : Long):JsValue = {
    Json.obj("data"->json,
             "inicio"->inicio,
             "cuantos"->cuantos,
             "orden"->orden,
             "tipoOrden"->tipoOrden,
             "total" -> total)
  }

  val fmt: DateTimeFormatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss")

  def toDate(s : String) = fmt.parseDateTime(s)

  def toString(f : DateTime) = fmt.print(f)

  def toJson(filas : Option[ResultSet], campos : IndexedSeq[String]):JsValue = {
    var json = JsArray.empty
    filas.foreach( _.foreach(f=>json = json.append(toJson(f,campos))))
    json

  }


  def toJson(fila : RowData, campos: IndexedSeq [(String)]) : JsValue = {
    var json = JsObject.empty

    for(campo <- campos){
       val valor = fila(campo)
       valor match {
         case v: String =>
           json = json + (campo->JsString(valor.asInstanceOf[String]))
         case v: Int =>
           json = json + (campo->JsNumber(valor.asInstanceOf[Int]))
         case v: Long =>
           json = json + (campo->JsNumber(valor.asInstanceOf[Long]))
         case v: Double =>
           json = json + (campo->JsNumber(valor.asInstanceOf[Double]))
         case v : DateTime =>
           json = json + (campo->JsString(toString(valor.asInstanceOf[DateTime])))
         case v : Boolean =>
           json = json + (campo->JsBoolean(valor.asInstanceOf[Boolean]))
         case _ =>
       }
    }
    json
  }

}
