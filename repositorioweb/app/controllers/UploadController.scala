package controllers

import java.nio.file.Paths
import javax.inject.{Inject, Singleton}

import cl.exe.config.Configuracion
import play.api.Configuration
import play.api.mvc._
import play.filters.csrf.{AddCSRFToken, RequireCSRFCheck}

import scala.concurrent.Future

@Singleton
class UploadController @Inject()(cc: ControllerComponents)(implicit config: Configuration) extends AbstractController(cc) {

  /**
    * Create an Action to render an HTML page with a welcome message.
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */



  def uploadForm = Action {implicit request =>
    Ok(views.html.upload())
  }

  def upload = Action(parse.multipartFormData) { request =>
    request.session.get("usuario").map { usuario =>
      request.body.file("archivo").map { archivo =>
        val filename = archivo.filename
        val contentType = archivo.contentType

        val directorio = if (filename.endsWith("xlsx")) Configuracion.config.getString("repositorio.directorioExcel") else Configuracion.config.getString("repositorio.directorioLog")
        println(directorio)
        Paths.get(s"$directorio/$usuario").toFile.mkdirs()

        archivo.ref.moveTo(Paths.get(s"$directorio/$usuario/$filename"), replace = true)
        Redirect("index.html")
      }.getOrElse {
        Redirect(routes.UploadController.uploadForm).flashing(
          "error" -> "Missing file")
      }
    }.getOrElse {
      Unauthorized("Sin conexion")
    }

  }


}



