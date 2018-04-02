package cl.exe.test

import java.time.ZonedDateTime

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, HttpResponse, StatusCodes}
import akka.stream.ActorMaterializer
import akka.util.ByteString
import cl.exe.config.Configuracion.config
import cl.exe.main.ReceptorMain.clusterName
import org.scalatest.FlatSpec
import cl.exe.usabilla._
import com.typesafe.scalalogging.LazyLogging
import spray.json._

import scala.concurrent.ExecutionContext
import scala.util.Failure

class UsabillaTest extends FlatSpec with LazyLogging {

  "probando" should "true" in {
    implicit val ec = ExecutionContext.global
    implicit val system = ActorSystem("test")
    implicit val materializer = ActorMaterializer()
    val date = ZonedDateTime.parse("20180314T000000Z", Signer.dateFormatter)
    //val url = "https://data.usabilla.com/live/websites/button/996ca51785d1/feedback/"
    val url = "https://data.usabilla.com/live/website/button/996ca51785d1/feedback?limit=100&since="+date.toInstant.toEpochMilli

    //logger.info(date.toString)
    var request = HttpRequest(uri = url, method = HttpMethods.GET)
    request = Signer.signedRequest(request, "4b1e8b6a6987f101", "38db296fe827c2e1124a")//,date)
    request.headers.map( h => logger.info(h.name()+"="+h.value()))

    Http().singleRequest(request)
      .onComplete {
        case scala.util.Success(response) =>
          response match {
            case HttpResponse(StatusCodes.OK, headers, entity, _) =>
              entity.dataBytes.runFold(ByteString(""))(_ ++ _).foreach { body =>
                val jsonRes = body.utf8String.parseJson.asInstanceOf[JsObject]
                logger.info(body.utf8String)
              }

            case resp@HttpResponse(code, headers, entity, _) =>
              logger.info("Request failed, response code: " + code)
              entity.dataBytes.runFold(ByteString(""))(_ ++ _).foreach { body =>
                val jsonRes = body.utf8String.parseJson.asInstanceOf[JsObject]
                logger.info(body.utf8String)
              }

          }
        case Failure(e) => logger.error(e.getMessage, e)
      }
    Thread.sleep(10000)
    system.terminate()
    Thread.sleep(2000)

  }

}
