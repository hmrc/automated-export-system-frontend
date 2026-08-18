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
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.amend.routes as amendRoute
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.problem.routes as problemRoute
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.unhappyPath.routes as unhappyRoute
import uk.gov.hmrc.automatedexportsystemfrontend.models.{NormalMode, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.navigation.Navigator
import uk.gov.hmrc.automatedexportsystemfrontend.pages.Page
import uk.gov.hmrc.automatedexportsystemfrontend.pages.amend.*

class AmendNavigator extends Navigator {

  override val normalRoutes: Page => UserAnswers => Call = {
    case AmendEnterMrnPage(submissionId)            => _ => amendRoute.AmendEnterDucrController.onPageLoad(NormalMode, submissionId)
    case AmendEnterDucrPage(submissionId)           => _ => amendRoute.AmendPartOfConsolidationController.onPageLoad(NormalMode, submissionId)
    case AmendPartOfConsolidationPage(submissionId) => amendPartOfConsolidationRoute(submissionId)
    case AmendOfficeOfExitPage(submissionId)        => _ => amendRoute.AmendIsSplitExitController.onPageLoad(NormalMode, submissionId)
    case AmendIsSplitExitPage(submissionId)         => amendIsSplitExitRoute(submissionId)
    case AmendAnyDiscrepanciesPage(submissionId)    => amendAnyDiscrepanciesRoute(submissionId)
  }

  private def amendPartOfConsolidationRoute(submissionId: String)(answers: UserAnswers): Call =
    answers.get(AmendPartOfConsolidationPage(submissionId)) match {
      case Some(_, _) => amendRoute.AmendOfficeOfExitController.onPageLoad(NormalMode, submissionId)
      case None       => problemRoute.JourneyRecoveryController.onPageLoad()
    }

  private def amendIsSplitExitRoute(submissionId: String)(answers: UserAnswers): Call =
    answers.get(AmendIsSplitExitPage(submissionId)) match {
      case Some(true)  => unhappyRoute.DiscrepancyConsignmentController.onPageLoad(NormalMode)
      case Some(false) => amendRoute.AmendAnyDiscrepanciesController.onPageLoad(NormalMode, submissionId)
      case None        => problemRoute.JourneyRecoveryController.onPageLoad()
    }
  private def amendAnyDiscrepanciesRoute(submissionId: String)(answers: UserAnswers): Call =
    answers.get(AmendAnyDiscrepanciesPage(submissionId)) match {
      case Some(true) => unhappyRoute.DiscrepancyConsignmentController.onPageLoad(NormalMode)
      // case Some(false) => amendRoute.AmendCYASubmissionController.onPageLoad()
      case None => problemRoute.JourneyRecoveryController.onPageLoad()
    }

}
