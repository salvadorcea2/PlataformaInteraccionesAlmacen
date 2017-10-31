package controllers

import java.net.{URI, URLEncoder}
import javax.inject.{Inject, Singleton}

import cl.exe.bd.Conexion._
import cl.exe.config.Configuracion
import cl.exe.json.JSonDAO.{toJson, toRespuesta}
import com.github.mauricio.async.db.RowData
import com.mashape.unirest.http.Unirest
import play.api.mvc._
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jose.util.Base64URL
import com.nimbusds.jwt.JWT
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.JWTParser
import com.nimbusds.jwt.SignedJWT
import com.nimbusds.langtag.LangTag
import com.nimbusds.oauth2.sdk._
import com.nimbusds.oauth2.sdk.http.HTTPRequest
import com.nimbusds.oauth2.sdk.http.HTTPResponse
import com.nimbusds.oauth2.sdk.id.ClientID
import com.nimbusds.oauth2.sdk.id.State
import com.nimbusds.openid.connect.sdk.AuthenticationErrorResponse
import com.nimbusds.openid.connect.sdk.AuthenticationRequest
import com.nimbusds.openid.connect.sdk.AuthenticationResponse
import com.nimbusds.openid.connect.sdk.AuthenticationResponseParser
import com.nimbusds.openid.connect.sdk.AuthenticationSuccessResponse
import com.nimbusds.openid.connect.sdk.Nonce
import com.nimbusds.openid.connect.sdk.OIDCScopeValue
import com.nimbusds.openid.connect.sdk.claims.ACR
import com.nimbusds.oauth2.sdk.token.AccessToken
import com.mashape.unirest.http.HttpResponse
import play.api.Configuration
import play.api.libs.json._

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}


object ClaveUnica {
  val SECRET = if (Configuracion.config.getBoolean("repositorio.produccion")) "2d24045fbd70447fb7b765b98501e350" else "15ac5b28bce945798707f3600203295f"
  val URL_CU_TOKEN = "https://accounts.claveunica.gob.cl/openid/token"
  val URL_CU_USER_INFO = "https://www.claveunica.gob.cl/openid/userinfo"
  val CU_URI_STR = "https://accounts.claveunica.gob.cl/openid/authorize"
  val RETURN_URL = Configuracion.config.getString("repositorio.urlCallback")
  val CLIENT_ID = if (Configuracion.config.getBoolean("repositorio.produccion")) "54cdc791832e4bee92a1f22e5e201129" else "71aaf452ddab4ecd8929348117dd259e"
}


@Singleton
class OpenIdController @Inject()(cc: ControllerComponents)(implicit config: Configuration) extends AbstractController(cc) {

  import ClaveUnica._


  val tiempoCrud = config.getInt("repositorioweb.tiempoCrud").get

  def   autenticar = Action {
    val uri = new URI(CU_URI_STR)

    //Socope de Query a Clave Única
    val scopeId = "1"


    //Scope puede ser rut, nombre, sandbox
    val scope = new Scope()
    scope.add("openid")
    scope.add("run")
    scope.add("name")


    val clientIdOpenId = new ClientID(CLIENT_ID)
    val state = new State()
    // State se utiliza en las siguientes fases


    // Generate nonce
    val nonce = new Nonce

    // Se prepara el request a CU
    var authenticationRequest = new AuthenticationRequest(uri, new ResponseType(ResponseType.Value.CODE), scope, clientIdOpenId, new URI(RETURN_URL), state, nonce)

    authenticationRequest = AuthenticationRequest.parse(uri, authenticationRequest.toQueryString)
    val authReqURI = authenticationRequest.toURI

    //Crea la URL de Respuesta se debe eliminar %E2%80%8B ya que la API usada lo genera y CU no lo soporta
    Redirect(authReqURI.toString.replaceAll("%E2%80%8B", ""))
  }

  def callback = Action.async { implicit request =>
    val parametros = request.queryString.map { case (k, v) => k -> v.mkString }
    val state = parametros.get("state").get
    val code = parametros.get("code").get

    val responseCU: HttpResponse[String] = Unirest.post(URL_CU_TOKEN)
      .header("content-type", "application/x-www-form-urlencoded")
      .header("cache-control", "no-cache")

      .body("code=" + code + "&"
        + "client_id=" + CLIENT_ID + "&"
        + "client_secret=" + SECRET + "&"
        + "redirect_uri=" + URLEncoder.encode(RETURN_URL, "UTF-8") + "&"
        + "grant_type=authorization_code&"
        + "state=" + state)
      .asString()

    val json = Json.parse(responseCU.getBody).asInstanceOf[JsObject]

    val accessToken = json.value("access_token").as[String]
    val responseCU2: HttpResponse[String] = Unirest.post(URL_CU_USER_INFO)
      .header("content-type", "application/x-www-form-urlencoded")
      .header("authorization", "Bearer " + accessToken)
      .header("cache-control", "no-cache")
      .asString()
    val jsonU = Json.parse(responseCU2.getBody).asInstanceOf[JsObject]
     //{ "sub": "2", "RolUnico": { "numero": 55555555, "DV": "5", "tipo": "RUN" }, "name": { "nombres": ['Maria', 'Carmen', 'De', 'Los', 'Angeles'], "apellidos": ['Del', ' Rio', 'Gonzalez'] } }
    val rut = (jsonU \ "RolUnico" \ "numero").get.as[Int] + "-" + (jsonU \ "RolUnico" \ "DV").get.as[String]
    var sql = s"select id,ministerio_id, subsecretaria_id, institucion_id from usuario where habilitado = true and rut ='" + rut + "'"
    pool.sendQuery(sql).map(qr => {
      if (qr.rows.get.size == 0)
        None
      else
        Some(qr.rows.get.head)
    }).map(row => {
      if (!row.isEmpty) {
        val fila = row.get
        Redirect("/").withSession("usuario" -> fila(0).asInstanceOf[Int].toString, "ministerio_id"->fila(1).asInstanceOf[Int].toString,"subsecretaria_id"->fila(2).asInstanceOf[Int].toString,"institucion_id"->fila(3).asInstanceOf[Int].toString,"session_token"->accessToken)
      }
      else
        Redirect("/")
    }
    )
  }

  def fakecallback(id: Int) = Action.async { implicit request =>
    var sql = s"select id, ministerio_id, subsecretaria_id, institucion_id from usuario where habilitado = true and  id =" + id
    pool.sendPreparedStatement(sql).map(qr => {
      if (qr.rows.get.size == 0)
        None
      else
        Some(qr.rows.get.head)
    }).map{row =>
      if (!row.isEmpty) {
        val fila = row.get
        Redirect("/").withSession("usuario" -> fila(0).asInstanceOf[Int].toString, "ministerio_id" -> fila(1).asInstanceOf[Int].toString, "subsecretaria_id" -> fila(2).asInstanceOf[Int].toString, "institucion_id" -> fila(3).asInstanceOf[Int].toString)

      }
      else
        Redirect("/")
    }
  }


  def logout = Action.async { implicit request =>
    Future {
      println(s"Logout ${request.session.get("usuario").isEmpty}")
      if (request.session.get("usuario").isEmpty)
        Redirect("/")
      else
        //Redirect("https://api.claveunica.gob.cl/api/v1/accounts/app/logout").withNewSession
        Redirect("/").withNewSession
    }
  }



}
