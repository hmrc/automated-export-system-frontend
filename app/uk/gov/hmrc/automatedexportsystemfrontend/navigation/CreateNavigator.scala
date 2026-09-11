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
import uk.gov.hmrc.automatedexportsystemfrontend.models.{CheckMode, NormalMode, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.navigation.Navigator
import uk.gov.hmrc.automatedexportsystemfrontend.pages.Page
import uk.gov.hmrc.automatedexportsystemfrontend.pages.create.*

class CreateNavigator extends Navigator {

  override val normalRoutes: Page => UserAnswers => Call = {
    case EnterMrnPage                  => _ => createRoute.EnterDucrController.onPageLoad(NormalMode)
    case EnterDucrPage                 => _ => createRoute.PartOfConsolidationController.onPageLoad(NormalMode)
    case PartOfConsolidationPage       => partOfConsolidationRoute
    case LocationTypePage              => _ => createRoute.LocationIdController.onPageLoad(NormalMode)
    case LocationIdPage                => _ => createRoute.OfficeOfExitController.onPageLoad(NormalMode)
    case OfficeOfExitPage              => _ => createRoute.IsSplitExitController.onPageLoad(NormalMode)
    case IsSplitExitPage               => isSplitExitRoute
    case AnyDiscrepanciesPage          => anyDiscrepanciesRoute
    case DiscrepancyConsignmentPage    => _ => createRoute.DiscrepancyTransportController.onPageLoad(NormalMode)
    case DiscrepancyTransportPage      => _ => createRoute.DiscrepancySealsController.onPageLoad(NormalMode)
    case DiscrepancySealsPage          => _ => createRoute.DiscrepancyReferenceController.onPageLoad(NormalMode)
    case DiscrepancyReferencePage      => _ => createRoute.DiscrepancyTransportMeansController.onPageLoad(NormalMode)
    case DiscrepancyTransportMeansPage => _ => createRoute.DiscrepancyTransportDocController.onPageLoad(NormalMode)
    case DiscrepancyTransportDocPage   => _ => createRoute.DiscrepancyGoodsController.onPageLoad(NormalMode)
    case DiscrepancyGoodsPage          => _ => createRoute.DiscrepancyPackingController.onPageLoad(NormalMode)
    case DiscrepancyPackingPage        => _ => createRoute.CYASubmissionController.onPageLoad()
  }

  private def partOfConsolidationRoute(answers: UserAnswers): Call =
    answers.get(PartOfConsolidationPage) match {
      case Some(_, _) => createRoute.LocationTypeController.onPageLoad(NormalMode)
      case None       => problemRoute.JourneyRecoveryController.onPageLoad()
    }

  private def isSplitExitRoute(answers: UserAnswers): Call =
    answers.get(IsSplitExitPage) match {
      case Some(true)  => createRoute.DiscrepancyConsignmentController.onPageLoad(NormalMode)
      case Some(false) => createRoute.AnyDiscrepanciesController.onPageLoad(NormalMode)
      case None        => problemRoute.JourneyRecoveryController.onPageLoad()
    }

  private def anyDiscrepanciesRoute(answers: UserAnswers): Call =
    answers.get(AnyDiscrepanciesPage) match {
      case Some(true)  => createRoute.DiscrepancyConsignmentController.onPageLoad(NormalMode)
      case Some(false) => createRoute.CYASubmissionController.onPageLoad()
      case None        => problemRoute.JourneyRecoveryController.onPageLoad()
    }

  override val checkRoutes: Page => UserAnswers => Call = {
    case IsSplitExitPage               => isSplitExitCheckRoute
    case AnyDiscrepanciesPage          => anyDiscrepanciesCheckRoute
    case DiscrepancyConsignmentPage    => discrepancyConsignmentCheckRoute
    case DiscrepancyTransportPage      => discrepancyTransportCheckRoute
    case DiscrepancySealsPage          => discrepancySealsCheckRoute
    case DiscrepancyReferencePage      => discrepancyReferenceCheckRoute
    case DiscrepancyTransportMeansPage => discrepancyTransportMeansCheckRoute
    case DiscrepancyTransportDocPage   => discrepancyTransportDocCheckRoute
    case DiscrepancyGoodsPage          => discrepancyGoodsCheckRoute
    case _                             => _ => createRoute.CYASubmissionController.onPageLoad()
  }

  private def isSplitExitCheckRoute(answers: UserAnswers): Call =
    answers.get(IsSplitExitPage) match {
      case Some(true) =>
        answers.get(DiscrepancyConsignmentPage) match {
          case None => createRoute.DiscrepancyConsignmentController.onPageLoad(CheckMode)
          case _    => createRoute.CYASubmissionController.onPageLoad()
        }
      case Some(false) =>
        answers.get(AnyDiscrepanciesPage) match {
          case None => createRoute.AnyDiscrepanciesController.onPageLoad(CheckMode)
          case _    => createRoute.CYASubmissionController.onPageLoad()
        }
      case _ => problemRoute.JourneyRecoveryController.onPageLoad()
    }

  private def anyDiscrepanciesCheckRoute(answers: UserAnswers): Call =
    answers.get(AnyDiscrepanciesPage) match {
      case Some(true) =>
        answers.get(DiscrepancyConsignmentPage) match {
          case None => createRoute.DiscrepancyConsignmentController.onPageLoad(CheckMode)
          case _    => createRoute.CYASubmissionController.onPageLoad()
        }
      case Some(false) => createRoute.CYASubmissionController.onPageLoad()
      case _           => problemRoute.JourneyRecoveryController.onPageLoad()
    }

  private def discrepancyConsignmentCheckRoute(answers: UserAnswers): Call =
    answers.get(DiscrepancyTransportPage) match {
      case None => createRoute.DiscrepancyTransportController.onPageLoad(CheckMode)
      case _    => createRoute.CYASubmissionController.onPageLoad()
    }

  private def discrepancyTransportCheckRoute(answers: UserAnswers): Call =
    answers.get(DiscrepancySealsPage) match {
      case None => createRoute.DiscrepancySealsController.onPageLoad(CheckMode)
      case _    => createRoute.CYASubmissionController.onPageLoad()
    }

  private def discrepancySealsCheckRoute(answers: UserAnswers): Call =
    answers.get(DiscrepancyReferencePage) match {
      case None => createRoute.DiscrepancyReferenceController.onPageLoad(CheckMode)
      case _    => createRoute.CYASubmissionController.onPageLoad()
    }

  private def discrepancyReferenceCheckRoute(answers: UserAnswers): Call =
    answers.get(DiscrepancyTransportMeansPage) match {
      case None => createRoute.DiscrepancyTransportMeansController.onPageLoad(CheckMode)
      case _    => createRoute.CYASubmissionController.onPageLoad()
    }

  private def discrepancyTransportMeansCheckRoute(answers: UserAnswers): Call =
    answers.get(DiscrepancyTransportDocPage) match {
      case None => createRoute.DiscrepancyTransportDocController.onPageLoad(CheckMode)
      case _    => createRoute.CYASubmissionController.onPageLoad()
    }

  private def discrepancyTransportDocCheckRoute(answers: UserAnswers): Call =
    answers.get(DiscrepancyGoodsPage) match {
      case None => createRoute.DiscrepancyGoodsController.onPageLoad(CheckMode)
      case _    => createRoute.CYASubmissionController.onPageLoad()
    }

  private def discrepancyGoodsCheckRoute(answers: UserAnswers): Call =
    answers.get(DiscrepancyPackingPage) match {
      case None => createRoute.DiscrepancyPackingController.onPageLoad(CheckMode)
      case _    => createRoute.CYASubmissionController.onPageLoad()
    }
}
