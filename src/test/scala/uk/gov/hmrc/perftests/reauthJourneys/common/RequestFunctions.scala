/*
 * Copyright 2024 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.perftests.reauthJourneys.common

import com.nimbusds.jwt.{JWTClaimsSet, SignedJWT}
import io.gatling.core.Predef._
import io.gatling.core.check.CheckBuilder
import io.gatling.core.check.regex.RegexCheckType
import io.gatling.http.Predef.{Response, headerRegex}
import io.gatling.http.check.header.HttpHeaderRegexCheckType


object RequestFunctions {

  val csrfToken = """<input type="hidden" name="csrfToken" value="([^"]+)""""
  val interactRefPattern: String = """/interact/([^"]+)"""
  val initialiseKeyPattern: String = """/access-hmrc-services/sign-in/selector/([^"]+)"""
  val hashInteractRefPattern: String = """hash=([^"]+)"""
  val olfgJourneyIdPattern: String = """olfgJourneyId=([^"]+)"""
  val olfgSignedJWTPattern: String = """request=([^"]+)"""
  val olfgContinueCodePattern: String = """code=([^"]+)&state="""
  val bearerPattern: String = """^GNAP.*?(Bearer .*?)$|^(Bearer .*?)GNAP.*?$|^(Bearer .*?)$"""

  def saveCsrfToken: CheckBuilder[RegexCheckType, String, String] = regex(
    _ => csrfToken
  ).saveAs("csrfToken")

  def saveInteractRef: CheckBuilder[HttpHeaderRegexCheckType, Response, String] = headerRegex(
    "Location", interactRefPattern
  ).saveAs("interactRef")

  def saveInitialiseKey: CheckBuilder[HttpHeaderRegexCheckType, Response, String] = headerRegex(
    "Location", initialiseKeyPattern
  ).saveAs("initialiseKey")

  def saveHashInteractRef: CheckBuilder[HttpHeaderRegexCheckType, Response, String] = headerRegex(
    "Location", hashInteractRefPattern
  ).saveAs("hashInteractRef")

  def saveOlfgJourneyId: CheckBuilder[HttpHeaderRegexCheckType, Response, String] = headerRegex(
    "Location", olfgJourneyIdPattern
  ).saveAs("olfgJourneyId")

  def saveOlfgSignedJWT: CheckBuilder[HttpHeaderRegexCheckType, Response, String] = headerRegex(
    "Location", olfgSignedJWTPattern
  ).saveAs("olfgSignedJWT")

  def saveOlfgNonce: CheckBuilder[HttpHeaderRegexCheckType, Response, String] =
    headerRegex("Location", olfgSignedJWTPattern)
      .transform(string => jwtClaims(string).getClaim("nonce") match {
        case s: String => s
        case x => throw new RuntimeException(s"got unexpected class: ${x.getClass}")
      })
      .saveAs("olfgNonce")

  def jwtClaims(jwt: String): JWTClaimsSet = SignedJWT.parse(jwt).getJWTClaimsSet

  def saveOlfgContinueCode: CheckBuilder[HttpHeaderRegexCheckType, Response, String] = headerRegex(
    "Location", olfgContinueCodePattern
  ).saveAs("olfgContinueCode")

  def saveBearerToken: CheckBuilder[HttpHeaderRegexCheckType, Response, String] = headerRegex(
    "Authorization", bearerPattern
  ).saveAs("bearerToken")

}
