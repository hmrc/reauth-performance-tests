/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.perftests.reauthJourneys
import org.slf4j.{Logger, LoggerFactory}
import sttp.client3._
import sttp.model.Uri
import uk.gov.hmrc.performance.simulation.PerformanceTestRunner
import uk.gov.hmrc.perftests.reauthJourneys.common.AppConfig._

import java.io._

class BasicSimulation extends PerformanceTestRunner {

  private val logger: Logger = LoggerFactory.getLogger(classOf[BasicSimulation])

  private val backend: SttpBackend[Identity, Any] = HttpClientSyncBackend()

  before {

    Uri.parse(s"$ctxUrl/identity-provider-account-context/test-only/get-provider-id") match {
      case Left(error) => logger.error(s"Bad URL: $error")
      case Right(uri) =>
        val response = basicRequest.get(uri).send(backend)
        if (response.isSuccess) {
          val ids: List[String] = response.body.toOption.getOrElse("").split(",").toList
          val logoutTokens: List[String] = ids.map(JwtHelper.createAValidLogoutToken)
          val pw = new PrintWriter(new File("src/test/resources/data/logoutToken.csv"))
          try {
            pw.write("logoutToken" + "\n")
            logoutTokens.foreach(str => pw.write(str + "\n"))
          }
          finally pw.close()
          logger.warn("successfully updated logoutToken file")
        } else {
          logger.error("unable to created logoutToken file" + response.code)
        }
    }

  }

  setup("one-login-authn", "User successfully logs into or creates an account").withRequests(OljParts.AuthnJourney(): _*)
  setup("one-login-iv", "User successfully verifies their identity").withRequests(OljParts.IvJourney(): _*)
  setup("api-logout", "User successfully logout").withActions(APIRemoteLogoutParts.postRemoteLogout(): _*)
  setup("front-channel-logout", "User successfully logs out of GDS").withActions(APIRemoteLogoutParts.postFrontChannelLogout(): _*)


  runSimulation()

}
