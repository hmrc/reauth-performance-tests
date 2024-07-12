/*
 * Copyright 2024 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.perftests.reauthJourneys

import io.gatling.http.request.builder.HttpRequestBuilder
import uk.gov.hmrc.perftests.reauthJourneys.requests.{BaseRequests, OLJRequests_ReAuth}



object ReAuthParts extends BaseRequests with OLJRequests_ReAuth {

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
    //getStartUrl,
    getCentralAuthDemoPage,
    getReAuth,
    getBackendCall1,
    getBackendCall2,
    postReAuthStubAuthnPage(true),
    getBackendFirstCallAfterPost,
    postOneLoginPostAuthorizePage,
    //getSimplifiedStubPage,
    //getContinueUrl
  )

}
