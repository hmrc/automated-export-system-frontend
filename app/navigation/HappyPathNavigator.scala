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

package navigation

import models.{NormalMode, UserAnswers}
import pages.{OfficeOfExitPage, Page}
import pages.happyPath.{AnyDiscrepanciesPage, EnterDucrPage, EnterMrnPage, IsSplitExitPage, PartOfConsolidationPage}
import play.api.mvc.Call

class HappyPathNavigator extends Navigator {

  override val normalRoutes: Page => UserAnswers => Call = {
    case EnterMrnPage            => _ => controllers.happyPath.routes.EnterDucrController.onPageLoad(NormalMode)
    case EnterDucrPage           => _ => controllers.happyPath.routes.OfficeOfExitController.onPageLoad(NormalMode)
    case PartOfConsolidationPage => partOfConsolidationRoute
    case OfficeOfExitPage        => _ => controllers.happyPath.routes.PartOfConsolidationController.onPageLoad(NormalMode)
    case IsSplitExitPage         => isSplitExitRoute
    case AnyDiscrepanciesPage    => anyDiscrepanciesRoute
  }

  private def partOfConsolidationRoute(answers: UserAnswers): Call =
    answers.get(PartOfConsolidationPage) match {
      case Some(true)  => controllers.happyPath.routes.IsSplitExitController.onPageLoad(NormalMode)
      case Some(false) => controllers.problem.routes.JourneyRecoveryController.onPageLoad() // temporary reroute
      case None        => controllers.problem.routes.JourneyRecoveryController.onPageLoad()
    }

  private def isSplitExitRoute(answers: UserAnswers): Call =
    answers.get(IsSplitExitPage) match {
      case Some(true)  => controllers.happyPath.routes.AnyDiscrepanciesController.onPageLoad(NormalMode)
      case Some(false) => controllers.problem.routes.JourneyRecoveryController.onPageLoad() // temporary reroute
      case None        => controllers.problem.routes.JourneyRecoveryController.onPageLoad()
    }
  private def anyDiscrepanciesRoute(answers: UserAnswers): Call =
    answers.get(AnyDiscrepanciesPage) match {
      case Some(true)  => controllers.routes.CYASubmissionController.onPageLoad()
      case Some(false) => controllers.problem.routes.JourneyRecoveryController.onPageLoad() // temporary reroute
      case None        => controllers.problem.routes.JourneyRecoveryController.onPageLoad()
    }

}
