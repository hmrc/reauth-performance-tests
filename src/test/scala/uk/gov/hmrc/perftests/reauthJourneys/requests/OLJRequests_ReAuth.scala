/*
 * Copyright 2024 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.perftests.reauthJourneys.requests

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder
import uk.gov.hmrc.perftests.reauthJourneys.common.RequestFunctions.saveCsrfToken

trait OLJRequests_ReAuth extends BaseRequests {

  //val olgUrl: String = baseUrlFor("one-login-gateway")
  //val olgFeUrl: String = baseUrlFor("one-login-gateway-frontend")
  //val oljStub: String = baseUrlFor("one-login-stub")

//  def postInitialise(action: String): HttpRequestBuilder = http("Create account in IDP store")
//    .post(s"$olgUrl/one-login-gateway/initialise")
//    .body(StringBody(
//      s"""|
//          |{
//          |  "action": "$action",
//          |  "completion-url": "https://www.example.com"
//          |}
//          |""".stripMargin))
//    .headers(Map("Content-Type" -> "application/json"))
//    .headers(Map("User-Agent" -> "identity-provider-gateway-frontend"))
//    .check(
//      status.is(200),
//      jsonPath("$..start-url").saveAs("startUrl"))

  def getCentralAuthDemoPage: HttpRequestBuilder = http("GET start url for CentralAuthDemo Page")
    .get("http://localhost:15009/centralised-authorisation-demo/home")
    .check(
      status.is(200)
      //header("Location").saveAs("authorizeUrl")
    )

  def getReAuth: HttpRequestBuilder = http("GET Sign in to HMRC Page")
    .get("http://localhost:15009/centralised-authorisation-demo/RE_AUTH")
    .check(
      status.is(303),
      header("Location").saveAs("authorizeUrl")
    )

  def getBackendCall1: HttpRequestBuilder = http("GET Sign in to HMRC page")
    .get("${authorizeUrl}")
    .check(
      status.is(303),
      header("Location").saveAs("authorizeUrl1"))
      //saveNonce,
      //saveState,
      //saveFormPostUrl,
      //saveSimplifiedJourneyUrl,
      //currentLocationRegex("(.*)/one-login-stub/authorize?(.*)"))

  def getBackendCall2: HttpRequestBuilder = http("GET Sign in to HMRC page")
    .get("${authorizeUrl1}")
    .check(
      status.is(303),
      header("Location").saveAs("authorizeUrl2"))
    //.check(saveCsrfToken)
      //saveFormPostUrl,
      //currentLocationRegex("(.*)/one-login-stub/authorize-simplifed?(.*)"))


//  def postOneLoginStubAuthnPage(success: Boolean): HttpRequestBuilder = http("POST authorize url/one login stub for ReAuth journey")
//    .post("$${authorizeUrl2}")
//    .formParam("state", "${state}")
//    .formParam("nonce", "${nonce}")
//    .formParam("vtr", "[\"Cl.Cm\"]")
//    .formParam("userInfo.success", s"$success")
//    .formParam("userInfo.sub", "ALLOK404238J99CD")
//    .formParam("userInfo.email", "email@email.com")
//    .formParam("submit", "submit")
//    .check(status.is(303))
//    .check(header("Location").saveAs("continueUrl"))
//
//  def postOneLoginStubIvPage(success: Boolean): HttpRequestBuilder = http("POST authorize url/one login stub for IV journey")
//    .post(s"$oljStub/$${ivStubPostUrl}")
//    .formParam("state", "${state}")
//    .formParam("nonce", "${nonce}")
//    .formParam("vtr", "[\"Cl.Cm.P2\"]")
//    .formParam("userInfo.success", s"$success")
//    .formParam("userInfo.sub", "ALLOK404238J99CD")
//    .formParam("userInfo.email", "email@email.com")
//    .formParam("userInfo.vc.cs.name[0].nameParts[0].type", "GivenName")
//    .formParam("userInfo.vc.cs.name[0].nameParts[0].value", "Jim")
//    .formParam("userInfo.vc.cs.name[0].nameParts[1].type", "FamilyName")
//    .formParam("userInfo.vc.cs.name[0].nameParts[1].value", "Ferguson")
//    .formParam("userInfo.vc.cs.birthDate[0].value", "1948-04-23")
//    .formParam("userInfo.addresses[0].postalCode", "EC4 2AA")
//    .formParam("submit", "submit")
//    .check(status.is(303))
//    .check(header("Location").saveAs("continueUrl"))
//  def getContinueUrl: HttpRequestBuilder = http("GET continue url")
//    .get("${continueUrl}")
//    .check(status.is(303))
//    .check(header("Location").is("https://www.example.com"))

}
