/*
 * Copyright 2025 HM Revenue & Customs
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

package uk.gov.hmrc.automatedexportsystemfrontend.navigation

import play.api.mvc.Call
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.happyPath.routes as happyRoute
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.unhappyPath.routes as unhappyRoute
import uk.gov.hmrc.automatedexportsystemfrontend.models.{NormalMode, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.navigation.Navigator
import uk.gov.hmrc.automatedexportsystemfrontend.pages.Page
import uk.gov.hmrc.automatedexportsystemfrontend.pages.unhappyPath.*

class UnhappyPathNavigator extends Navigator {

  override val normalRoutes: Page => UserAnswers => Call = {
    case DiscrepancyConsignmentPage    => _ => unhappyRoute.DiscrepancyDucrController.onPageLoad(NormalMode)
    case DiscrepancyDucrPage           => _ => unhappyRoute.DiscrepancyMucrController.onPageLoad(NormalMode)
    case DiscrepancyMucrPage           => _ => unhappyRoute.DiscrepancyTransportController.onPageLoad(NormalMode)
    case DiscrepancyTransportPage      => _ => unhappyRoute.DiscrepancySealsController.onPageLoad(NormalMode)
    case DiscrepancySealsPage          => _ => unhappyRoute.DiscrepancyReferenceController.onPageLoad(NormalMode)
    case DiscrepancyReferencePage      => _ => unhappyRoute.LocationTypeController.onPageLoad(NormalMode)
    case LocationTypePage              => _ => unhappyRoute.LocationIdController.onPageLoad(NormalMode)
    case LocationIdPage                => _ => unhappyRoute.DiscrepancyTransportMeansController.onPageLoad(NormalMode)
    case DiscrepancyTransportMeansPage => _ => unhappyRoute.DiscrepancyTransportDocController.onPageLoad(NormalMode)
    case DiscrepancyTransportDocPage   => _ => unhappyRoute.DiscrepancyGoodsController.onPageLoad(NormalMode)
    case DiscrepancyGoodsPage          => _ => unhappyRoute.DiscrepancyPackingController.onPageLoad(NormalMode)
    case DiscrepancyPackingPage        => _ => happyRoute.CYASubmissionController.onPageLoad()
  }

}
