/*
 * Copyright 2024 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.perftests.reauthJourneys

import io.gatling.core.action.builder.ActionBuilder
import uk.gov.hmrc.perftests.reauthJourneys.requests.{ApiLogoutRequests, BaseRequests}

object APIRemoteLogoutParts extends BaseRequests with ApiLogoutRequests {

  def postRemoteLogout(): Seq[ActionBuilder] = {
    postLogout()
  }

  def postFrontChannelLogout(): Seq[ActionBuilder] = {
    postLogoutInitialise :+
    getFCStartLogout :+
    getFCLogoutStub :+
    getFCPostLogout
  }

}
