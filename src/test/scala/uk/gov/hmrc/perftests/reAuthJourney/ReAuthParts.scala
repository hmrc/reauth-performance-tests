/*
 * Copyright 2024 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.perftests.reAuthJourney

import io.gatling.http.request.builder.HttpRequestBuilder
import uk.gov.hmrc.perftests.reAuthJourney.requests.{BaseRequests, ReAuthRequest}

object ReAuthParts extends BaseRequests with ReAuthRequest{
  def AuthnJourney(): Seq[HttpRequestBuilder] = Seq(


    getStartUrl,
    getReAuthUrl,
    getNavigateReAuthUrl,
    getContinueReAuthUrl,
    postContinueReAuthUrl,
    getOlfjJourneyId,
    getAuthorizeResponseOlfjJourneyId,
    postSubmitJourney,
    getAuthorizeResponseOlfjJourneyId2,
    postReSubmitJourney,
    getContinueUrl


  )
}
