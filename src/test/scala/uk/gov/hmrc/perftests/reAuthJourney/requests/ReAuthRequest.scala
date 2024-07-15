/*
 * Copyright 2024 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.perftests.reAuthJourney.requests

import io.gatling.http.request.builder.HttpRequestBuilder
import io.gatling.core.Predef._
import io.gatling.http.Predef.{header, _}
import uk.gov.hmrc.perftests.reAuthJourney.common.RequestFunctions._

trait ReAuthRequest extends BaseRequests {

  // val cadUrl = centralised-authorisation-demo Url
  val cadUrl: String  = baseUrlFor("centralised-authorisation-demo")
  val oljStub: String = baseUrlFor("one-login-stub")

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

  def getStartUrl: HttpRequestBuilder = http("Req:1 GET HOME")
    .get(s"$cadUrl/centralised-authorisation-demo/home")
    .check(status.is(200), currentLocationRegex("(.*)/centralised-authorisation-demo/home(.*)"))

  def getReAuthUrl: HttpRequestBuilder = http("Req:2 GET RE_AUTH")
    .get(s"$cadUrl/centralised-authorisation-demo/RE_AUTH")
    .check(
      status.is(303),
      header("Location").saveAs("reAuthUrl"),
      currentLocationRegex("(.*)/centralised-authorisation-demo/RE_AUTH(.*)")
    )

  def getNavigateReAuthUrl: HttpRequestBuilder = http("Req:3 GET NAVIGATE RE_AUTH URL")
    .get("${reAuthUrl}")
    .check(
      status.is(303),
      header("Location").saveAs("reAuthContinue"),
      currentLocationRegex("(.*)/centralised-authorisation-server/interact/(.*)")
    )

  def getContinueReAuthUrl: HttpRequestBuilder = http("Req:4 GET CONTINUE RE_AUTH URL")
    .get("${reAuthContinue}")
    .check(saveCsrfToken)
    .check(
      status.is(200),
      currentLocationRegex("(.*)/identity-provider-gateway/access-hmrc-services/sign-in/selector/(.*)")
    )

  def postContinueReAuthUrl: HttpRequestBuilder = http("Req:5 POST CONTINUE RE_AUTH URL")
    .post("${reAuthContinue}")
    .formParam("""csrfToken""", """${csrfToken}""")
    .formParam("""signInType""", "oneLogin")
    .check(saveOlfgJourneyId)
    .check(
      status.is(303),
      header("Location").saveAs("reAuthContinueWithOljfId"),
      currentLocationRegex("(.*)/identity-provider-gateway/access-hmrc-services/sign-in/selector/(.*)")
    )

  def getOlfjJourneyId: HttpRequestBuilder = http("Req:6 START OLFJ Journey ID")
    .get("${reAuthContinueWithOljfId}")
    .formParam("""olfgJourneyId""", """${olfgJourneyId}""")
    .check(saveOlfgSignedJWT)
    .check(saveOlfgNonce)
    .check(
      status.is(303),
      header("Location").saveAs("reAuthContinueWithAuthorizeResponse"),
      currentLocationRegex("(.*)/one-login-gateway/start?(.*)")
    )

  def getAuthorizeResponseOlfjJourneyId: HttpRequestBuilder = http("Req:7 AUTHORIZE RESPONSE")
    .get("${reAuthContinueWithAuthorizeResponse}")
    .check(status.is(200), currentLocationRegex("(.*)/one-login-stub/authorize?(.*)"))

  def postSubmitJourney: HttpRequestBuilder = http("Req:8 SUBMIT JOURNEY")
    .post(s"$oljStub/one-login-stub/authorize")
    .formParam("state", """${olfgJourneyId}""")
    .formParam("nonce", """${olfgNonce}""")
    .formParam("vtr", """["Cl.Cm"]""")
    .formParam("isReauth", "false")
    .formParam("userInfo.success", "true")
    .formParam("userInfo.sub", "ALLOK404238J99CD")
    .formParam("userInfo.email", "66666666email@email.com")
    .formParam("userInfo.failureReason", "")
    .formParam("userInfo.otherFailureReason", "")
    .formParam("userInfo.failureDescription", "")
    .formParam("submit", "submit")
    .check(status.is(303), currentLocationRegex("(.*)/one-login-stub/authorize?(.*)"))
    .check(header("Location").saveAs("continueUrl"))

  def getContinueUrl: HttpRequestBuilder = http("GET continue url")
    .get("${continueUrl}")
    .check(status.is(303))
    .check(header("Location").is("https://www.example.com"))

}
