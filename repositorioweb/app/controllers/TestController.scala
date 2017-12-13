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
import play.api.libs.json._

@Singleton
class TestController  @Inject()(cc: ControllerComponents)(implicit config: Configuration) extends AbstractController(cc) {

  def autenticacion() = Action { implicit request =>
    val s = Json.parse("{\"access_token\":\"NXt5tfmAPAjuMzUKjLzJoOqHVyOuj4RI2hZsfnLx\",\"token_type\":\"Bearer\",\"expires_in\":3600}")
    Ok(s)

  }

  def consulta(fecha : Int) = Action { implicit request =>
   val s = Json.parse("{\"estado\":\"200\",\"mensaje\":\"Existen datos para la fecha ingresada.\",\"contenido\":[{\"id_canal\":\"1\",\"canal\":\"Run Ciudadano\",\"id_clasificacion\":\"1\",\"clasificacion\":\"Solicitud\",\"id_tramite\":\"3\",\"tramite\":\"Actualizaci\\u00f3n Formulario\",\"id_interaccion\":\"2\",\"interaccion\":\"Cambio de domicilio\",\"fecha\":\"20171102\",\"total\":\"1\"},{\"id_canal\":\"1\",\"canal\":\"Run Ciudadano\",\"id_clasificacion\":\"1\",\"clasificacion\":\"Solicitud\",\"id_tramite\":\"3\",\"tramite\":\"Actualizaci\\u00f3n Formulario\",\"id_interaccion\":\"14\",\"interaccion\":\"Solicitud de actualizaci\\u00f3n de situaci\\u00f3n socioecon\\u00f3mica\",\"fecha\":\"20171102\",\"total\":\"1\"},{\"id_canal\":\"1\",\"canal\":\"Run Ciudadano\",\"id_clasificacion\":\"1\",\"clasificacion\":\"Solicitud\",\"id_tramite\":\"3\",\"tramite\":\"Actualizaci\\u00f3n Formulario\",\"id_interaccion\":\"7\",\"interaccion\":\"Solicitud de actualizaci\\u00f3n m\\u00f3dulo vivienda\",\"fecha\":\"20171102\",\"total\":\"1\"},{\"id_canal\":\"3\",\"canal\":\"Municipal\",\"id_clasificacion\":\"1\",\"clasificacion\":\"Solicitud\",\"id_tramite\":\"3\",\"tramite\":\"Actualizaci\\u00f3n Formulario\",\"id_interaccion\":\"5\",\"interaccion\":\"Solicitud de actualizaci\\u00f3n m\\u00f3dulo de salud\",\"fecha\":\"20171102\",\"total\":\"1\"},{\"id_canal\":\"1\",\"canal\":\"Run Ciudadano\",\"id_clasificacion\":\"1\",\"clasificacion\":\"Solicitud\",\"id_tramite\":\"3\",\"tramite\":\"Actualizaci\\u00f3n Formulario\",\"id_interaccion\":\"4\",\"interaccion\":\"Desvincular Integrante\",\"fecha\":\"20171102\",\"total\":\"1\"},{\"id_canal\":\"2\",\"canal\":\"Clave Unica\",\"id_clasificacion\":\"1\",\"clasificacion\":\"Solicitud\",\"id_tramite\":\"5\",\"tramite\":\"Ingreso al Registro\",\"id_interaccion\":\"1\",\"interaccion\":\"Solicitud de Ingreso al Registro\",\"fecha\":\"20171102\",\"total\":\"5\"},{\"id_canal\":\"3\",\"canal\":\"Municipal\",\"id_clasificacion\":\"1\",\"clasificacion\":\"Solicitud\",\"id_tramite\":\"3\",\"tramite\":\"Actualizaci\\u00f3n Formulario\",\"id_interaccion\":\"14\",\"interaccion\":\"Solicitud de actualizaci\\u00f3n de situaci\\u00f3n socioecon\\u00f3mica\",\"fecha\":\"20171102\",\"total\":\"6\"},{\"id_canal\":\"3\",\"canal\":\"Municipal\",\"id_clasificacion\":\"1\",\"clasificacion\":\"Solicitud\",\"id_tramite\":\"3\",\"tramite\":\"Actualizaci\\u00f3n Formulario\",\"id_interaccion\":\"4\",\"interaccion\":\"Desvincular Integrante\",\"fecha\":\"20171102\",\"total\":\"2\"},{\"id_canal\":\"3\",\"canal\":\"Municipal\",\"id_clasificacion\":\"1\",\"clasificacion\":\"Solicitud\",\"id_tramite\":\"2\",\"tramite\":\"Complemento\",\"id_interaccion\":\"50\",\"interaccion\":\"Complemento de ingreso\",\"fecha\":\"20171102\",\"total\":\"2\"},{\"id_canal\":\"3\",\"canal\":\"Municipal\",\"id_clasificacion\":\"1\",\"clasificacion\":\"Solicitud\",\"id_tramite\":\"5\",\"tramite\":\"Ingreso al Registro\",\"id_interaccion\":\"1\",\"interaccion\":\"Solicitud de Ingreso al Registro\",\"fecha\":\"20171102\",\"total\":\"51\"},{\"id_canal\":\"3\",\"canal\":\"Municipal\",\"id_clasificacion\":\"1\",\"clasificacion\":\"Solicitud\",\"id_tramite\":\"3\",\"tramite\":\"Actualizaci\\u00f3n Formulario\",\"id_interaccion\":\"2\",\"interaccion\":\"Cambio de domicilio\",\"fecha\":\"20171102\",\"total\":\"15\"},{\"id_canal\":\"3\",\"canal\":\"Municipal\",\"id_clasificacion\":\"1\",\"clasificacion\":\"Solicitud\",\"id_tramite\":\"4\",\"tramite\":\"Actualizaci\\u00f3n \\/ Rectificaci\\u00f3n\",\"id_interaccion\":\"60\",\"interaccion\":\"Actualizacion de ingreso trabajador dependiente\",\"fecha\":\"20171102\",\"total\":\"1\"},{\"id_canal\":\"3\",\"canal\":\"Municipal\",\"id_clasificacion\":\"1\",\"clasificacion\":\"Solicitud\",\"id_tramite\":\"4\",\"tramite\":\"Actualizaci\\u00f3n \\/ Rectificaci\\u00f3n\",\"id_interaccion\":\"101\",\"interaccion\":\"Desvincular Integrante\",\"fecha\":\"20171102\",\"total\":\"3\"},{\"id_canal\":\"3\",\"canal\":\"Municipal\",\"id_clasificacion\":\"1\",\"clasificacion\":\"Solicitud\",\"id_tramite\":\"3\",\"tramite\":\"Actualizaci\\u00f3n Formulario\",\"id_interaccion\":\"3\",\"interaccion\":\"Nuevo Integrante \",\"fecha\":\"20171102\",\"total\":\"6\"},{\"id_canal\":\"3\",\"canal\":\"Municipal\",\"id_clasificacion\":\"1\",\"clasificacion\":\"Solicitud\",\"id_tramite\":\"3\",\"tramite\":\"Actualizaci\\u00f3n Formulario\",\"id_interaccion\":\"19\",\"interaccion\":\"Cambio en relaci\\u00f3n de parentesco\",\"fecha\":\"20171102\",\"total\":\"7\"},{\"id_canal\":\"1\",\"canal\":\"Run Ciudadano\",\"id_clasificacion\":\"1\",\"clasificacion\":\"Solicitud\",\"id_tramite\":\"5\",\"tramite\":\"Ingreso al Registro\",\"id_interaccion\":\"1\",\"interaccion\":\"Solicitud de Ingreso al Registro\",\"fecha\":\"20171102\",\"total\":\"4\"},{\"id_canal\":\"3\",\"canal\":\"Municipal\",\"id_clasificacion\":\"1\",\"clasificacion\":\"Solicitud\",\"id_tramite\":\"3\",\"tramite\":\"Actualizaci\\u00f3n Formulario\",\"id_interaccion\":\"7\",\"interaccion\":\"Solicitud de actualizaci\\u00f3n m\\u00f3dulo vivienda\",\"fecha\":\"20171102\",\"total\":\"17\"},{\"id_canal\":\"1\",\"canal\":\"Run Ciudadano\",\"id_clasificacion\":\"1\",\"clasificacion\":\"Solicitud\",\"id_tramite\":\"3\",\"tramite\":\"Actualizaci\\u00f3n Formulario\",\"id_interaccion\":\"3\",\"interaccion\":\"Nuevo Integrante \",\"fecha\":\"20171102\",\"total\":\"1\"}]}")
    Ok(s)

  }
}
