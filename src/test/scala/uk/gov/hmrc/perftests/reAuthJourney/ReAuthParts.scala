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

package uk.gov.hmrc.perftests.reAuthJourney

import io.gatling.http.request.builder.HttpRequestBuilder
import uk.gov.hmrc.perftests.reAuthJourney.requests.{BaseRequests, ReAuthRequest}

object ReAuthParts extends BaseRequests with ReAuthRequest{
  def AuthnJourney(): Seq[HttpRequestBuilder] = Seq(

    getJourneyStartUrl,
    getSignInSelectorUrl,
    postSignInSelectorUrl,
    getOlfgStartUrl,
    getAuthorizeResponseOlfg,
    postSubmitJourney,
    getSignInContinueUrl,
    getSignInCompleteUrl,
    getSignInLocationRedirect,
    getSignInHashRedirect,
    getReAuthUrl,
    getReAuthRedirect,
    getOlfgStartUrl,
    getAuthorizeResponseOlfg,
    postReSubmitJourney,
    getReauthContinueUrl


  )
}
