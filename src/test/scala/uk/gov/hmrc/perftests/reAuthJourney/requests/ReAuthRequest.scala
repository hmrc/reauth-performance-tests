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

import io.gatling.core.Predef._
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.http.Predef.{header, _}
import io.gatling.http.request.builder.HttpRequestBuilder
import uk.gov.hmrc.perftests.reAuthJourney.common.AppConfig._
import uk.gov.hmrc.perftests.reAuthJourney.common.RequestFunctions._

trait ReAuthRequest extends BaseRequests {

  val oljStub: String = baseUrlFor("one-login-stub")
  val flushAllCookies: ActionBuilder =
    exec(flushCookieJar).actionBuilders.head

  def getJourneyStartUrl: HttpRequestBuilder = http("Get start of journey url")
    .get(s"$caCanaryFeServiceUrl/centralised-authorisation-canary/RE_AUTH")
    .check(
      status.is(303),
      header("Location").saveAs("confirmYourEmailAddressPage"),
      currentLocationRegex("(.*)/centralised-authorisation-canary/RE_AUTH(.*)")
    )

  // Private Beta Page Start

  val redirectToConfirmYourGovUkOneLoginEmailAddressPage: HttpRequestBuilder =
    if (runLocal) {
      http("Get Redirect to Confirm your GOV.UK OneLogin email address page")
        .get("${confirmYourEmailAddressPage}")
        .check(saveCsrfToken)
        .check(
          status.is(200)
        )
    } else {
      http("Get Redirect to Confirm your GOV.UK OneLogin email address page")
        .get(s"$identityProviderGatewayFrontendUrl$${confirmYourEmailAddressPage}")
        .check(saveCsrfToken)
        .check(
          status.is(200)
        )
    }


  val postToConfirmYourGovUkOneLoginEmailAddressPage: HttpRequestBuilder =
    if (runLocal) {
      http("Post to Confirm your GOV.UK OneLogin email address page")
        .post("${confirmYourEmailAddressPage}")
        .formParam("""csrfToken""", """${csrfToken}""")
        .formParam("""signInType""", "oneLogin")
        .check(status.is(303), header("Location").saveAs("confirmYourEmailAddressPageLocation"))
    } else {
      http("Post to Confirm your GOV.UK OneLogin email address page")
        .post(s"$identityProviderGatewayFrontendUrl$${confirmYourEmailAddressPage}")
        .formParam("""csrfToken""", """${csrfToken}""")
        .formParam("""signInType""", "oneLogin")
        .check(status.is(303), header("Location").saveAs("confirmYourEmailAddressPageLocation"))
    }

  val getToConfirmYourGovUkOneLoginEmailAddressPage: HttpRequestBuilder =
    http("Get to Confirm your GOV.UK OneLogin email address page")
      .get(s"$identityProviderGatewayFrontendUrl$${confirmYourEmailAddressPageLocation}")
      .check(saveCsrfToken)
      .check(
        status.is(200)
      )

  val postToConfirmYourGovUkOneLoginEmailAddressPageLocation: HttpRequestBuilder =
    http("Post to Confirm your GOV.UK OneLogin email address Location page")
      .post(s"$identityProviderGatewayFrontendUrl$${confirmYourEmailAddressPageLocation}")
      .formParam("""csrfToken""", """${csrfToken}""")
      .formParam("""profile""", "")
      .formParam("""email-address""", StringBody("66666666email@email.com"))
      .check(saveOlfgJourneyId, status.is(303), header("Location").saveAs("startOlfgJourney"))

  val getStartOlfgJourney: HttpRequestBuilder =
    http("Get to start olfg JourneyID")
      .get(s"$${startOlfgJourney}")
      .check(
        saveOlfgNonce,
        status.is(303),
        header("Location").saveAs("signInPage")
      )

  // Private Beta Pages End

  def getSignInSelectorUrl: HttpRequestBuilder = if (runLocal) {
    http("GET Sign in selector page")
      .get("${signInPage}")
      .check(
        status.is(200)
      )
  } else
    http("GET Sign in selector page")
      .get("${signInPage}")
      .check(
        status.is(200)
      )

  def postSignInSelectorUrl: HttpRequestBuilder =
    if (runLocal) {
      http("POST Sign in selector page")
        .post(s"$identityProviderGatewayFrontendUrl$${signInPage}")
        .formParam("""csrfToken""", """${csrfToken}""")
        .formParam("""signInType""", "oneLogin")
        .check(
          saveOlfgJourneyId,
          status.is(303),
          header("Location").saveAs("reAuthStartUrl")
        )
    } else
      http("POST Sign in selector page")
        .post(s"$identityProviderGatewayFrontendUrl$${signInPage}")
        .formParam("""csrfToken""", """${csrfToken}""")
        .formParam("""signInType""", "oneLogin")
        .check(
          saveOlfgJourneyId,
          status.is(303),
          header("Location").saveAs("reAuthStartUrl")
        )

  def getOlfgStartUrl: HttpRequestBuilder = http("GET reauth start url")
    .get("${reAuthStartUrl}")
    .formParam("""olfgJourneyId""", """${olfgJourneyId}""")
    .check(
      saveOlfgSignedJWT,
      saveOlfgNonce,
      status.is(303),
      header("Location").saveAs("reAuthAuthorizeUrl"),
      currentLocationRegex("(.*)/sign-in-to-hmrc-online-services/one-login/start?(.*)")
    )

  def getAuthorizeResponseOlfg: HttpRequestBuilder = http("GET authorize url for sign in")
    .get("${reAuthAuthorizeUrl}")
    .check(
      status.is(200),
      currentLocationRegex("(.*)/one-login-stub/authorize?(.*)"))

  def postSubmitJourney: HttpRequestBuilder = http("POST authorize url for sign in")
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
    .check(
      status.is(303),
      currentLocationRegex("(.*)/one-login-stub/authorize?(.*)"),
      header("Location").saveAs("signInContinueUrl"))


  def getSignInContinueUrl: HttpRequestBuilder = http("GET sign in continue url")
    .get("${signInContinueUrl}")
    .check(
      status.is(303),
      header("Location").saveAs("reauthSignInComplete"),
      currentLocationRegex("(.*)/sign-in-to-hmrc-online-services/one-login/continue?(.*)")
    )

  def getSignInCompleteUrl: HttpRequestBuilder =
    if (runLocal) {
      http("GET sign in complete url")
        .get("${reauthSignInComplete}")
        .check(
          status.is(303),
          header("Location").saveAs("reauthLocationRedirect"),
          currentLocationRegex("(.*)/sign-in-to-hmrc-online-services/identity/authorize/complete?(.*)"))
    } else {
      http("GET sign in complete url")
        .get(s"$identityProviderGatewayFrontendUrl$${reauthSignInComplete}")
        .check(
          status.is(303),
          header("Location").saveAs("reauthLocationRedirect"),
          currentLocationRegex("(.*)/sign-in-to-hmrc-online-services/identity/authorize/complete?(.*)"))
    }

  def getSignInLocationRedirect: HttpRequestBuilder =
    if (runLocal) {
      http("GET interact reference call from CA")
        .get("${reauthLocationRedirect}")
        .check(
          status.is(303),
          header("Location").saveAs("reauthHash"),
          currentLocationRegex("(.*)/centralised-authorisation-server/interact(.*)"))
    } else {
      http("GET interact reference call from CA")
        .get(s"$authorisationServerFeUrl$${reauthLocationRedirect}")
        .check(
          status.is(303),
          header("Location").saveAs("reauthHash"),
          currentLocationRegex("(.*)/centralised-authorisation-server/interact(.*)"))
    }

  def getSignInHashRedirect: HttpRequestBuilder =
    if (runLocal) {
      http("GET Reauth hash redirect from CA")
        .get("${reauthHash}")
        .check(
          status.is(303))
    } else {
      http("GET Reauth hash redirect from CA")
        .get(s"$caCanaryFeServiceUrl$${reauthHash}")
        .check(
          status.is(303))
    }

  def getReAuthUrl: HttpRequestBuilder = http("GET Reauth journey url")
    .get(s"$caCanaryFeServiceUrl/centralised-authorisation-canary/RE_AUTH")
    .check(
      status.is(303),
      header("Location").saveAs("reauthRedirect"),
      currentLocationRegex("(.*)/centralised-authorisation-canary/RE_AUTH")
    )

  def getReAuthRedirect: HttpRequestBuilder =
    if (runLocal) {
      http("GET Reauth redirect url")
        .get("${reauthRedirect}")
        .check(
          status.is(303),
          header("Location").saveAs("reAuthStartUrl"),
          saveOlfgJourneyId,
          currentLocationRegex("(.*)/sign-in-to-hmrc-online-services/identity/reauth(.*)"))
    } else {
      http("GET Reauth redirect url")
        .get(s"$authorisationServerFeUrl$${reauthRedirect}")
        .check(
          status.is(303),
          header("Location").saveAs("reAuthStartUrl"),
          saveOlfgJourneyId,
          currentLocationRegex("(.*)/sign-in-to-hmrc-online-services/identity/reauth(.*)"))
    }


  def postReSubmitJourney: HttpRequestBuilder = http("POST reauth sign in")
    .post(s"$oljStub/one-login-stub/authorize")
    .formParam("state", """${olfgJourneyId}""")
    .formParam("nonce", """${olfgNonce}""")
    .formParam("vtr", """["Cl.Cm"]""")
    .formParam("isReauth", "true")
    .formParam("userInfo.success", "true")
    .formParam("userInfo.sub", "ALLOK404238J99CD")
    .formParam("userInfo.email", "66666666email@email.com")
    .formParam("userInfo.failureReason", "")
    .formParam("userInfo.otherFailureReason", "")
    .formParam("userInfo.failureDescription", "")
    .formParam("submit", "submit")
    .check(
      status.is(303),
      currentLocationRegex("(.*)/one-login-stub/authorize?(.*)"),
      header("Location").saveAs("reauthContinueUrl"))

  def getReauthContinueUrl: HttpRequestBuilder = http("GET reauth journey complete continue url")
    .get("${reauthContinueUrl}")
    .check(
      status.is(303),
      currentLocationRegex("(.*)/sign-in-to-hmrc-online-services/one-login/continue?(.*)"),
      headerRegex("Location", "(.*)/sign-in-to-hmrc-online-services/identity/reauth/complete(.*)"))
}
