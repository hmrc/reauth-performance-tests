/*
 * Copyright 2024 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.perftests.reauthJourneys.requests

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder

trait OLJRequests extends BaseRequests {

  val olgUrl: String = baseUrlFor("one-login-gateway")
  val olgFeUrl: String = baseUrlFor("one-login-gateway-frontend")
  val oljStub: String = baseUrlFor("one-login-stub")

  def postInitialise(action: String): HttpRequestBuilder = http("Create account in IDP store")
    .post(s"$olgUrl/one-login-gateway/initialise")
    .body(StringBody(
      s"""|
          |{
          |  "action": "$action",
          |  "completion-url": "https://www.example.com"
          |}
          |""".stripMargin))
    .headers(Map("Content-Type" -> "application/json"))
    .headers(Map("User-Agent" -> "identity-provider-gateway-frontend"))
    .check(
      status.is(200),
      jsonPath("$..start-url").saveAs("startUrl"))

  def getStartUrl: HttpRequestBuilder = http("GET start url for one login journey")
    .get("${startUrl}")
    .check(
      status.is(303),
      header("Location").saveAs("authorizeUrl")
    )

  def getOneLoginStubPage: HttpRequestBuilder = http("GET authorize url/one login stub page")
    .get("${authorizeUrl}")
    .check(status.is(200),
      saveNonce,
      saveState,
      saveFormPostUrl,
      saveSimplifiedJourneyUrl,
      currentLocationRegex("(.*)/one-login-stub/authorize?(.*)"))

  def getSimplifiedStubPage: HttpRequestBuilder = http("GET Simplified stub page")
    .get(s"$oljStub/$${simplifiedStubUrl}")
    .check(status.is(200),
      saveFormPostUrl,
      currentLocationRegex("(.*)/one-login-stub/authorize-simplifed?(.*)"))

  def postOneLoginStubAuthnPage(success: Boolean): HttpRequestBuilder = http("POST authorize url/one login stub for AUTHN journey")
    .post(s"$oljStub/$${ivStubPostUrl}")
    .formParam("state", "${state}")
    .formParam("nonce", "${nonce}")
    .formParam("vtr", "[\"Cl.Cm\"]")
    .formParam("userInfo.success", s"$success")
    .formParam("userInfo.sub", "ALLOK404238J99CD")
    .formParam("userInfo.email", "email@email.com")
    .formParam("submit", "submit")
    .check(status.is(303))
    .check(header("Location").saveAs("continueUrl"))

  def postOneLoginStubIvPage(success: Boolean): HttpRequestBuilder = http("POST authorize url/one login stub for IV journey")
    .post(s"$oljStub/$${ivStubPostUrl}")
    .formParam("state", "${state}")
    .formParam("nonce", "${nonce}")
    .formParam("vtr", "[\"Cl.Cm.P2\"]")
    .formParam("userInfo.success", s"$success")
    .formParam("userInfo.sub", "ALLOK404238J99CD")
    .formParam("userInfo.email", "email@email.com")
    .formParam("userInfo.vc.cs.name[0].nameParts[0].type", "GivenName")
    .formParam("userInfo.vc.cs.name[0].nameParts[0].value", "Jim")
    .formParam("userInfo.vc.cs.name[0].nameParts[1].type", "FamilyName")
    .formParam("userInfo.vc.cs.name[0].nameParts[1].value", "Ferguson")
    .formParam("userInfo.vc.cs.birthDate[0].value", "1948-04-23")
    .formParam("userInfo.addresses[0].postalCode", "EC4 2AA")
    .formParam("submit", "submit")
    .check(status.is(303))
    .check(header("Location").saveAs("continueUrl"))
  def getContinueUrl: HttpRequestBuilder = http("GET continue url")
    .get("${continueUrl}")
    .check(status.is(303))
    .check(header("Location").is("https://www.example.com"))

}
