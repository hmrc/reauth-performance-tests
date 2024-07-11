/*
 * Copyright 2024 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.perftests.reauthJourneys

import io.gatling.http.request.builder.HttpRequestBuilder
import uk.gov.hmrc.perftests.reauthJourneys.requests.{BaseRequests, OLJRequests}


object OljParts extends BaseRequests with OLJRequests {

//  def AuthnJourney(): Seq[HttpRequestBuilder] = Seq(
//
//    postInitialise("AUTHN"),
//    getStartUrl,
//    getOneLoginStubPage,
//    postOneLoginStubAuthnPage(true),
//    getContinueUrl
//
//    )

  def IvJourney(): Seq[HttpRequestBuilder]  = Seq(

    //postInitialise("IV"),
    getStartUrl,
    getOneLoginStubPage,
    getSimplifiedStubPage,
    //postOneLoginStubIvPage(true),
    //getContinueUrl
  )

}
