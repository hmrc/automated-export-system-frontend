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

import uk.gov.hmrc.automatedexportsystemfrontend.controllers.happyPath.{routes => happyRoute}
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.problem.{routes => problemRoute}
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.{routes => appRoute}
import uk.gov.hmrc.automatedexportsystemfrontend.models.{NormalMode, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.pages.Page
import play.api.mvc.Call
import uk.gov.hmrc.automatedexportsystemfrontend.pages.OfficeOfExitPage
import uk.gov.hmrc.automatedexportsystemfrontend.pages.happyPath.{
  AnyDiscrepanciesPage,
  EnterDucrPage,
  EnterMrnPage,
  IsSplitExitPage,
  PartOfConsolidationPage
}

class HappyPathNavigator extends Navigator {

  override val normalRoutes: Page => UserAnswers => Call = {
    case EnterMrnPage            => _ => happyRoute.EnterDucrController.onPageLoad(NormalMode)
    case EnterDucrPage           => _ => happyRoute.OfficeOfExitController.onPageLoad(NormalMode)
    case PartOfConsolidationPage => partOfConsolidationRoute
    case OfficeOfExitPage        => _ => happyRoute.PartOfConsolidationController.onPageLoad(NormalMode)
    case IsSplitExitPage         => isSplitExitRoute
    case AnyDiscrepanciesPage    => anyDiscrepanciesRoute
  }

  private def partOfConsolidationRoute(answers: UserAnswers): Call =
    answers.get(PartOfConsolidationPage) match {
      case Some(true)  => happyRoute.IsSplitExitController.onPageLoad(NormalMode)
      case Some(false) => problemRoute.JourneyRecoveryController.onPageLoad() // temporary reroute
      case None        => problemRoute.JourneyRecoveryController.onPageLoad()
    }

  private def isSplitExitRoute(answers: UserAnswers): Call =
    answers.get(IsSplitExitPage) match {
      case Some(true)  => happyRoute.AnyDiscrepanciesController.onPageLoad(NormalMode)
      case Some(false) => problemRoute.JourneyRecoveryController.onPageLoad() // temporary reroute
      case None        => problemRoute.JourneyRecoveryController.onPageLoad()
    }
  private def anyDiscrepanciesRoute(answers: UserAnswers): Call =
    answers.get(AnyDiscrepanciesPage) match {
      case Some(true)  => appRoute.CYASubmissionController.onPageLoad()
      case Some(false) => problemRoute.JourneyRecoveryController.onPageLoad() // temporary reroute
      case None        => problemRoute.JourneyRecoveryController.onPageLoad()
    }

}
