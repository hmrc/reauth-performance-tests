/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.perftests.reauthJourneys.requests

import io.gatling.core.Predef._
import io.gatling.core.check.CheckBuilder
import io.gatling.core.check.css.CssCheckType
import jodd.lagarto.dom.NodeSelector
import uk.gov.hmrc.performance.conf.ServicesConfiguration

import scala.util.Random

trait BaseRequests extends ServicesConfiguration {
  def saveFormPostUrl: CheckBuilder[CssCheckType, NodeSelector, String] = css("form[method='POST']", "action").optional.saveAs("ivStubPostUrl")

  def saveSimplifiedJourneyUrl: CheckBuilder[CssCheckType, NodeSelector, String] = css("a[role='button']", "href").optional.saveAs("simplifiedStubUrl")

  def saveState: CheckBuilder[CssCheckType, NodeSelector, String] = css("input[name='state']", "value").optional.saveAs("state")

  def saveNonce: CheckBuilder[CssCheckType, NodeSelector, String] = css("input[name='nonce']", "value").optional.saveAs("nonce")


}
