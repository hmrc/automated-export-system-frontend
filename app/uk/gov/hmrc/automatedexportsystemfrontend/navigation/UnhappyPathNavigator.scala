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
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.create.routes as happyRoute
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.discrepancies.routes as discrepanciesRoute
import uk.gov.hmrc.automatedexportsystemfrontend.models.{NormalMode, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.navigation.Navigator
import uk.gov.hmrc.automatedexportsystemfrontend.pages.Page
import uk.gov.hmrc.automatedexportsystemfrontend.pages.discrepancies.*

class UnhappyPathNavigator extends Navigator {

  override val normalRoutes: Page => UserAnswers => Call = {
    case DiscrepancyConsignmentPage    => _ => discrepanciesRoute.DiscrepancyDucrController.onPageLoad(NormalMode)
    case DiscrepancyDucrPage           => _ => discrepanciesRoute.DiscrepancyMucrController.onPageLoad(NormalMode)
    case DiscrepancyMucrPage           => _ => discrepanciesRoute.DiscrepancyTransportController.onPageLoad(NormalMode)
    case DiscrepancyTransportPage      => _ => discrepanciesRoute.DiscrepancySealsController.onPageLoad(NormalMode)
    case DiscrepancySealsPage          => _ => discrepanciesRoute.DiscrepancyReferenceController.onPageLoad(NormalMode)
    case DiscrepancyReferencePage      => _ => discrepanciesRoute.LocationTypeController.onPageLoad(NormalMode)
    case LocationTypePage              => _ => discrepanciesRoute.LocationIdController.onPageLoad(NormalMode)
    case LocationIdPage                => _ => discrepanciesRoute.DiscrepancyTransportMeansController.onPageLoad(NormalMode)
    case DiscrepancyTransportMeansPage => _ => discrepanciesRoute.DiscrepancyTransportDocController.onPageLoad(NormalMode)
    case DiscrepancyTransportDocPage   => _ => discrepanciesRoute.DiscrepancyGoodsController.onPageLoad(NormalMode)
    case DiscrepancyGoodsPage          => _ => discrepanciesRoute.DiscrepancyPackingController.onPageLoad(NormalMode)
    case DiscrepancyPackingPage        => _ => happyRoute.CYASubmissionController.onPageLoad()
  }

}
