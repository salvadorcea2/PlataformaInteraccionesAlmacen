package controllers

import javax.inject.{Inject, Singleton}

import play.api.Configuration
import cl.exe.bd.Conexion._
import cl.exe.json.JSonDAO._
import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._

import scala.concurrent.Await
import scala.concurrent.duration._

@Singleton
class TestController  @Inject()(cc: ControllerComponents)(implicit config: Configuration) extends AbstractController(cc) {

  def test() = Action { implicit request =>
   val s = "{\"RolUnico\": {\"DV\": \"4\", \"numero\": 44444444, \"tipo\": \"RUN\"}, \"sub\": \"2594\", \"name\": {\"apellidos\": [\"Del rio\", \"Gonzalez\"], \"nombres\": [\"Maria\", \"Carmen\", \"De los angeles\"]}}"
    val jsonU = Json.parse(s).asInstanceOf[JsObject]
    val rut = (jsonU \ "RolUnico" \ "numero").get.as[Int]  + "-" + (jsonU \ "RolUnico" \ "DV").get.as[String]

    Ok(rut)

  }
}
