package cl.exe.usabilla

import java.net.URLEncoder

import akka.http.scaladsl.model.headers.RawHeader
import akka.stream.Materializer
import cl.exe.config.Configuracion.config
import com.typesafe.scalalogging.LazyLogging
import org.apache.commons.codec.digest.{DigestUtils, HmacUtils}

import scala.concurrent.Future

/*
https://data.usabilla.com/live/websites/button/8d73568ac2be/feedback/?limit=1&since=1419340238645
 */

import akka.http.scaladsl.model.Uri.{Path, Query}
import akka.http.scaladsl.model.{HttpHeader, HttpRequest}
import java.time.format.DateTimeFormatter
import java.time.{ZoneOffset, ZonedDateTime}

// Documentation: http://developers.usabilla.com/#api-_
case class CanonicalRequest(method: String,
                                             uri: String,
                                             queryString: String,
                                             headerString: String,
                                             signedHeaders: String,
                                             hashedPayload: String) {
  def canonicalString: String = s"$method\n$uri\n$queryString\n$headerString\n\n$signedHeaders\n$hashedPayload"
}

object CanonicalRequest extends LazyLogging {
  def from(req: HttpRequest): CanonicalRequest = {
    val hashedBody = DigestUtils.sha256Hex("")
    CanonicalRequest(
      req.method.value,
      pathEncode(req.uri.path),
      canonicalQueryString(req.uri.query()),
      canonicalHeaderString(req.headers),
      signedHeadersString(req.headers),
      hashedBody
    )
  }

  def canonicalQueryString(query: Query): String =
    query.sortBy(_._1).map { case (a, b) => s"${uriEncode(a)}=${uriEncode(b)}" }.mkString("&")

  private def uriEncode(str: String) = URLEncoder.encode(str, "utf-8")

  /* def headersToSign(headers: Seq[HttpHeader]): String = {
    val date = ZonedDateTime.parse(headers.find(_.name() == "date").get.value(), Signer.dateFormatterRFC)
    val headers2 = headers.filter(_.name() != "date").+:(RawHeader("date", Signer.dateFormatter.format(date)))
    val grouped = headers2.groupBy(_.lowercaseName())
    val combined = grouped.mapValues(_.map(_.value.replaceAll("\\s+", " ").trim).mkString(","))
    combined.toList.sortBy(_._1).map { case (k, v) => s"$k:$v" }.mkString("\n")
  } */

  def canonicalHeaderString(headers: Seq[HttpHeader]): String = {
    val grouped = headers.groupBy(_.lowercaseName())
    val combined = grouped.mapValues(_.map(_.value.replaceAll("\\s+", " ").trim).mkString(","))
    combined.toList.sortBy(_._1).map { case (k, v) => s"$k:$v" }.mkString("\n")
  }

  def signedHeadersString(headers: Seq[HttpHeader]): String =
    headers.map(_.lowercaseName()).distinct.sorted.mkString(";")

  private def pathEncode(path: Path): String =
    if (path.isEmpty) "/"
    else
      path.toString().flatMap {
        case ch if "!$&'()*+,;:=".contains(ch) => "%" + Integer.toHexString(ch.toInt).toUpperCase
        case other => other.toString
      }

}

object Signer extends LazyLogging {
  val dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmssX")
  val dateFormatterShort = DateTimeFormatter.ofPattern("yyyyMMdd")
  val dateFormatterRFC = DateTimeFormatter.RFC_1123_DATE_TIME

  def signedRequest(request: HttpRequest, accessKey : String, secretKey : String, date: ZonedDateTime = ZonedDateTime.now(ZoneOffset.UTC)) : HttpRequest = {


      val headersToAdd = Vector(RawHeader("x-usbl-date", date.format(dateFormatter)),RawHeader("host", "data.usabilla.com"))
      val reqWithHeaders = request.withHeaders(request.headers ++ headersToAdd)
      val cr = CanonicalRequest.from(reqWithHeaders)
      val authHeader = authorizationHeader("USBL1-HMAC-SHA256", accessKey, secretKey, date, cr)
      val req = reqWithHeaders.withHeaders(reqWithHeaders.headers :+ authHeader)
      logger.info("****USABILLA REQUEST****")
      logger.info(req.toString())
      logger.info("****END USABILLA REQUEST****")
      req


  }



  def authorizationHeader(algorithm: String,
                                        accessKey: String,
                                        secretKey : String,
                                        requestDate: ZonedDateTime,
                                        canonicalRequest: CanonicalRequest): HttpHeader =
    RawHeader("Authorization", authorizationString(algorithm, accessKey, secretKey, requestDate, canonicalRequest))

  def authorizationString(algorithm: String,
                                        accessKey : String,
                                        secretKey : String,
                                        requestDate: ZonedDateTime,
                                        canonicalRequest: CanonicalRequest): String = {
    val kDate = HmacUtils.hmacSha256(("USBL1"+secretKey).getBytes, requestDate.format(dateFormatterShort).getBytes())
    val kSign = HmacUtils.hmacSha256(kDate,"usbl1_request".getBytes())
    val sign = HmacUtils.hmacSha256Hex(kSign, stringToSign(algorithm, requestDate, canonicalRequest).getBytes)
    s"$algorithm Credential=${accessKey}/${requestDate.format(dateFormatterShort)}/usbl1_request, SignedHeaders=${canonicalRequest.signedHeaders}, Signature=$sign"
  }

  def stringToSign(algorithm: String,
                   requestDate: ZonedDateTime,
                   canonicalRequest: CanonicalRequest): String = {
    logger.info("**** CANONINCAL STRING *****")
    logger.info(canonicalRequest.canonicalString)

    val hashedRequest =  DigestUtils.sha256Hex(canonicalRequest.canonicalString.getBytes())
    val date = requestDate.format(dateFormatter)
    val scope = requestDate.format(dateFormatterShort)+"/usbl1_request"
    val s = s"$algorithm\n$date\n$scope\n$hashedRequest"
    logger.info("**** STRING TO SIGN *****")
    logger.info(s)
    s
  }

}


