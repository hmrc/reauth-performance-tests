/*
 * Copyright 2024 HM Revenue & Customs
 *

 */

package uk.gov.hmrc.perftests.reAuthJourney.common

import com.nimbusds.jwt.{JWTClaimsSet, SignedJWT}
import io.gatling.core.Predef._
import io.gatling.core.check.CheckBuilder
import io.gatling.core.check.regex.RegexCheckType
import io.gatling.http.Predef.{Response, headerRegex}
import io.gatling.http.check.header.HttpHeaderRegexCheckType

object RequestFunctions {

  val CsrfPattern                     = """<input type="hidden" name="csrfToken" value="([^"]+)""""
  val olfgJourneyIdPattern: String    = """Id=([^"]+)"""
  val olfgSignedJWTPattern: String    = """request=([^"]+)"""
  val olfgContinueCodePattern: String = """code=([^"]+)&state="""

  def saveCsrfToken: CheckBuilder[RegexCheckType, String, String] = regex(_ => CsrfPattern).saveAs("csrfToken")

  def saveOlfgJourneyId: CheckBuilder[HttpHeaderRegexCheckType, Response, String] = headerRegex(
    "Location",
    olfgJourneyIdPattern
  ).saveAs("olfgJourneyId")

  def saveOlfgSignedJWT: CheckBuilder[HttpHeaderRegexCheckType, Response, String] = headerRegex(
    "Location",
    olfgSignedJWTPattern
  ).saveAs("olfgSignedJWT")

  def saveOlfgNonce: CheckBuilder[HttpHeaderRegexCheckType, Response, String] =
    headerRegex("Location", olfgSignedJWTPattern)
      .transform(string =>
        jwtClaims(string).getClaim("nonce") match {
          case s: String => s
          case x         => throw new RuntimeException(s"got unexpected class: ${x.getClass}")
        }
      )
      .saveAs("olfgNonce")

  def jwtClaims(jwt: String): JWTClaimsSet = SignedJWT.parse(jwt).getJWTClaimsSet

}
