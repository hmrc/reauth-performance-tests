/*
 * Copyright 2024 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.perftests.reauthJourneys.common

import io.gatling.core.Predef.{defaultPatterns, regex}
import io.gatling.core.check.CheckBuilder
import io.gatling.core.check.regex.RegexCheckType
import uk.gov.hmrc.performance.conf.ServicesConfiguration

object AppConfig extends ServicesConfiguration {

  private val oneLoginStubBaseUrl: String = baseUrlFor("one-login-stub")
  val oneLoginStubUrl: String = s"$oneLoginStubBaseUrl/one-login-stub"

  val ctxUrl: String = baseUrlFor("identity-provider-account-context")

}
