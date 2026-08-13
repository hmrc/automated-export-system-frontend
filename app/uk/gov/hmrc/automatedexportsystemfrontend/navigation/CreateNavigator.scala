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
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.create.routes as createRoute
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.problem.routes as problemRoute
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.unhappyPath.routes as unhappyRoute
import uk.gov.hmrc.automatedexportsystemfrontend.models.{NormalMode, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.navigation.Navigator
import uk.gov.hmrc.automatedexportsystemfrontend.pages.Page
import uk.gov.hmrc.automatedexportsystemfrontend.pages.create.*

class CreateNavigator extends Navigator {

  override val normalRoutes: Page => UserAnswers => Call = {
    case EnterMrnPage            => _ => createRoute.EnterDucrController.onPageLoad(NormalMode)
    case EnterDucrPage           => _ => createRoute.PartOfConsolidationController.onPageLoad(NormalMode)
    case PartOfConsolidationPage => partOfConsolidationRoute
    case OfficeOfExitPage        => _ => createRoute.IsSplitExitController.onPageLoad(NormalMode)
    case IsSplitExitPage         => isSplitExitRoute
    case AnyDiscrepanciesPage    => anyDiscrepanciesRoute
  }

  private def partOfConsolidationRoute(answers: UserAnswers): Call =
    answers.get(PartOfConsolidationPage) match {
      case Some(_, _) => createRoute.OfficeOfExitController.onPageLoad(NormalMode)
      case None       => problemRoute.JourneyRecoveryController.onPageLoad()
    }

  private def isSplitExitRoute(answers: UserAnswers): Call =
    answers.get(IsSplitExitPage) match {
      case Some(true)  => unhappyRoute.DiscrepancyConsignmentController.onPageLoad(NormalMode)
      case Some(false) => createRoute.AnyDiscrepanciesController.onPageLoad(NormalMode)
      case None        => problemRoute.JourneyRecoveryController.onPageLoad()
    }

  private def anyDiscrepanciesRoute(answers: UserAnswers): Call =
    answers.get(AnyDiscrepanciesPage) match {
      case Some(true)  => unhappyRoute.DiscrepancyConsignmentController.onPageLoad(NormalMode)
      case Some(false) => createRoute.CYASubmissionController.onPageLoad()
      case None        => problemRoute.JourneyRecoveryController.onPageLoad()
    }

}
