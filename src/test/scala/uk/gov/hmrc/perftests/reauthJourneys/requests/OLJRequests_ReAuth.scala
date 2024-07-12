/*
 * Copyright 2024 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.perftests.reauthJourneys.requests

import io.gatling.core.Predef._
import io.gatling.http.Predef.{header, _}
import io.gatling.http.request.builder.HttpRequestBuilder
import uk.gov.hmrc.perftests.reauthJourneys.common.RequestFunctions.{saveCsrfToken, saveOlfgJourneyId}

trait OLJRequests_ReAuth extends BaseRequests {


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

  def getBackendCall1: HttpRequestBuilder = http("GET Sign in to HMRC page Call1")
    .get("${authorizeUrl}")
    .check(
      status.is(303),
      header("Location").saveAs("authorizeUrl1"))


  def getBackendCall2: HttpRequestBuilder = http("GET Sign in to HMRC page Call2")
    .get("${authorizeUrl1}").check(saveCsrfToken)
    .check(
      status.is(200))
    //.check(saveCsrfToken)
      //saveFormPostUrl,
      //currentLocationRegex("(.*)/one-login-stub/authorize-simplifed?(.*)"))


  def postReAuthStubAuthnPage(success: Boolean): HttpRequestBuilder = http("POST authorize url/one login stub for ReAuth journey")
    .post("$${authorizeUrl2}").check(saveOlfgJourneyId)
    .formParam("""csrfToken""", """${csrfToken}""")
    .formParam("""signInType""", "oneLogin")
    .check(status.is(303))
    .check(header("Location").saveAs("continueUrl"))
    header("Location").saveAs("authorizeUrl3")


  def getBackendFirstCallAfterPost: HttpRequestBuilder = http("GET Sign in to HMRC page Call1")
    .get("${authorizeUrl3}")
    .formParam("""saveOlfgJourneyId""", """${saveOlfgJourneyId}""")
    .check(
      status.is(303),
      header("Location").saveAs("authorizeUrl4"))


  def getBackend2SecondCallAfterPost: HttpRequestBuilder = http("GET Sign in to HMRC page Call1")
    .get("${authorizeUrl4}")
    .formParam("""saveOlfgJourneyId""", """${saveOlfgJourneyId}""")
    .check(
      status.is(200))

  def postOneLoginPostAuthorizePage1st: HttpRequestBuilder = http("POST OneLoginAuth Page")
    .post("http://localhost:12000/one-login-stub/authorize")
    .formParam("state", "8f431a38-1e51-43e6-ad00-ffb1d3e70608")
    .formParam("nonce", "aQQpjf_gvlh0Ax9a")
    .formParam("vtr", """["Cl.Cm"]""")
    .formParam("isReauth", "false")
    .formParam("userInfo.success", "true")
    .formParam("userInfo.sub", "ALLOK404238J99CD")
    .formParam("userInfo.email", "66666666email@email.com")
    .formParam("userInfo.failureReason", "")
    .formParam("userInfo.otherFailureReason", "")
    .formParam("userInfo.failureDescription", "")
    .formParam("submit", "submit")
    .check(
      status.is(303),
      header("Location").saveAs("continueUrl5")

    )

  def postOneLoginPostAuthorizePage2nd: HttpRequestBuilder = http("POST OneLoginAuth Page")
    .post("http://localhost:12000/one-login-stub/authorize")
    .formParam("state", "8f431a38-1e51-43e6-ad00-ffb1d3e70608")
    .formParam("nonce", "aQQpjf_gvlh0Ax9a")
    .formParam("vtr", """["Cl.Cm"]""")
    .formParam("isReauth", "false")
    .formParam("userInfo.success", "true")
    .formParam("userInfo.sub", "ALLOK404238J99CD")
    .formParam("userInfo.email", "66666666email@email.com")
    .formParam("userInfo.failureReason", "")
    .formParam("userInfo.otherFailureReason", "")
    .formParam("userInfo.failureDescription", "")
    .formParam("submit", "submit")
    .check(
      status.is(303),
      header("Location").saveAs("continueUrl5")

    )

}
