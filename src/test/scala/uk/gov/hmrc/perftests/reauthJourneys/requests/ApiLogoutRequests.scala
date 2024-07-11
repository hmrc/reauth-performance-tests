/*
 * Copyright 2024 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.perftests.reauthJourneys.requests

import io.gatling.core.Predef._
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.feeder.Feeder
import io.gatling.http.Predef._

trait ApiLogoutRequests extends BaseRequests {

  val olgUrl: String       = baseUrlFor("one-login-gateway")

  // Use the thunk to avoid evaluating this straight away
  // The before {} block needs to run to populate the file
  // and without this, the setup code tries to grab this CSV before it's been updated/created
  // NOTE: you need *at least one* central-auth-user record in your Mongo locally for this to work
  //
  private def tokenFeeder: () => Feeder[Any] = {
    () => csv("src/test/resources/data/logoutToken.csv").batch.circular()
  }

  def postLogout(): Seq[ActionBuilder] = exec(
    http("postRemoteLogout")
    .post(s"$olgUrl/one-login-gateway/remote-logout")
    .headers(Map("Content-Type" -> "application/x-www-form-urlencoded"))
    .formParam("logout_token", s"$${logoutToken}")
    .check(status.is(200))
  ).feed(tokenFeeder).actionBuilders

  def postLogoutInitialise: List[ActionBuilder] = exec(
    http("Create account in IDP store")
    .post(s"$olgUrl/one-login-gateway/initialise")
    .body(StringBody(
      s"""|
          |{
          |  "action": "LOGOUT",
          |  "completion-url": "https://www.example.com",
          |  "initialise-parameters": {
          |    "idToken": "$${logoutToken}"
          |}
          |}
          |""".stripMargin))
    .headers(Map("Content-Type" -> "application/json"))
    .headers(Map("User-Agent" -> "identity-provider-gateway-frontend"))
    .check(
      status.is(200),
      jsonPath("$..start-url").saveAs("startLogout")
    )).feed(tokenFeeder).actionBuilders

  def getFCStartLogout: ActionBuilder = http("GET start-logout? url")
    .get(s"$${startLogout}")
    .check(
      status.is(303),
      header("Location").saveAs("logoutStubRedirect"))

  def getFCLogoutStub: ActionBuilder = http("GET logout? stub url")
    .get(s"$${logoutStubRedirect}")
    .check(
      status.is(302),
      header("Location").saveAs("postLogoutUrl"))

  def getFCPostLogout: ActionBuilder = http("GET post-logout? url")
    .get(s"$${postLogoutUrl}")
    .check(
      status.is(303),
      header("Location").is("https://www.example.com")
    )

}
