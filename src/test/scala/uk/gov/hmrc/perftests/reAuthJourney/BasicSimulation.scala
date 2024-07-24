/*
 * Copyright 2023 HM Revenue & Customs

 */

package uk.gov.hmrc.perftests.reAuthJourney

import uk.gov.hmrc.performance.simulation.PerformanceTestRunner

class BasicSimulation extends PerformanceTestRunner {

   setup("reauth-login", "User successfully logs into or creates an account").withRequests(ReAuthParts.AuthnJourney(): _*)

  runSimulation()
}
