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
import uk.gov.hmrc.automatedexportsystemfrontend.models.{CheckMode, Mode, NormalMode, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.navigation.Navigator
import uk.gov.hmrc.automatedexportsystemfrontend.pages.Page
import uk.gov.hmrc.automatedexportsystemfrontend.pages.create.*
import uk.gov.hmrc.automatedexportsystemfrontend.queries.*

class CreateNavigator extends Navigator {

  override val normalRoutes: Page => UserAnswers => Call = {
    case EnterMrnPage               => _ => createRoute.EnterDucrController.onPageLoad(NormalMode)
    case EnterDucrPage              => _ => createRoute.PartOfConsolidationController.onPageLoad(NormalMode)
    case PartOfConsolidationPage    => partOfConsolidationRoute
    case LocationTypePage           => _ => createRoute.LocationIdController.onPageLoad(NormalMode)
    case LocationIdPage             => _ => createRoute.OfficeOfExitController.onPageLoad(NormalMode)
    case OfficeOfExitPage           => _ => createRoute.IsSplitExitController.onPageLoad(NormalMode)
    case IsSplitExitPage            => isSplitExitRoute
    case AnyDiscrepanciesPage       => anyDiscrepanciesRoute
    case DiscrepancyConsignmentPage => _ => createRoute.DiscrepancyTransportController.onPageLoad(1, NormalMode)
    case DiscrepancyTransportPage(transportEquipmentIndex) =>
      userAnswers => discrepancyTransportEquipmentRoute(userAnswers, transportEquipmentIndex, NormalMode)
    case DiscrepancySealsPage(transportEquipmentIndex, sealsIndex) =>
      userAnswers => discrepancySealsRoute(userAnswers, transportEquipmentIndex, sealsIndex, NormalMode)
    case DiscrepancyReferencePage(transportEquipmentIndex, discrepancyReferenceIndex) =>
      userAnswers => discrepancyReferenceRoute(userAnswers, transportEquipmentIndex, discrepancyReferenceIndex, NormalMode)
    case DiscrepancyTransportMeansPage         => _ => createRoute.DiscrepancyTransportDocController.onPageLoad(1, NormalMode)
    case DiscrepancyTransportDocPage(docIndex) => userAnswers => discrepancyTransportDocRoute(userAnswers, docIndex, NormalMode)
    case DiscrepancyPackingPage(goodsItemIndex, packagingDetailIndex) =>
      userAnswers => discrepancyPackingRoute(userAnswers, goodsItemIndex, packagingDetailIndex, NormalMode)
    case DiscrepancyGoodsPage(goodsItemIndex) => userAnswers => discrepancyGoodsRoute(userAnswers, goodsItemIndex, NormalMode)
  }

  override val checkRoutes: Page => UserAnswers => Call = {
    case IsSplitExitPage            => isSplitExitCheckRoute
    case AnyDiscrepanciesPage       => anyDiscrepanciesCheckRoute
    case DiscrepancyConsignmentPage => discrepancyConsignmentCheckRoute
    case DiscrepancyTransportPage(transportEquipmentIndex) =>
      userAnswers => discrepancyTransportEquipmentRoute(userAnswers, transportEquipmentIndex, CheckMode)
    case DiscrepancySealsPage(transportEquipmentIndex, sealsIndex) =>
      userAnswers => discrepancySealsRoute(userAnswers, transportEquipmentIndex, sealsIndex, CheckMode)
    case DiscrepancyReferencePage(transportEquipmentIndex, discrepancyReferenceIndex) =>
      userAnswers => discrepancyReferenceRoute(userAnswers, transportEquipmentIndex, discrepancyReferenceIndex, CheckMode)
    case DiscrepancyTransportMeansPage         => discrepancyTransportMeansCheckRoute
    case DiscrepancyTransportDocPage(docIndex) => userAnswers => discrepancyTransportDocRoute(userAnswers, docIndex, CheckMode)
    case DiscrepancyGoodsPage(goodsItemIndex)  => userAnswers => discrepancyGoodsRoute(userAnswers, goodsItemIndex, CheckMode)
    case DiscrepancyPackingPage(goodsItemIndex, packagingDetailIndex) =>
      userAnswers => discrepancyPackingRoute(userAnswers, goodsItemIndex, packagingDetailIndex, CheckMode)
    case _ => _ => createRoute.CYASubmissionController.onPageLoad()
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

  private def discrepancyReferenceRoute(answers: UserAnswers, transportEquipmentIndex: Int, discrepancyReferenceIndex: Int, mode: Mode): Call =
    (answers.get(DiscrepancyReferencePage(transportEquipmentIndex, discrepancyReferenceIndex)), mode) match {
      case (Some(_), _) => createRoute.GoodsReferenceCYAController.onPageLoad(transportEquipmentIndex, discrepancyReferenceIndex, mode)
      case (None, _) if DiscrepancyReference(transportEquipmentIndex).count(answers) > 0 =>
        createRoute.AddAnotherGoodsReferenceController.onPageLoad(transportEquipmentIndex, None, mode)
      case (None, NormalMode) => createRoute.DiscrepancyTransportMeansController.onPageLoad(NormalMode)
      case (None, CheckMode)  => createRoute.CYASubmissionController.onPageLoad()
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
    if (!answers.get(VisitedDiscrepancySealsPage(1)).contains(true)) {
      createRoute.DiscrepancyTransportController.onPageLoad(1, CheckMode)
    } else {
      createRoute.CYASubmissionController.onPageLoad()
    }

  private def discrepancyTransportMeansCheckRoute(answers: UserAnswers): Call =
    answers.get(DiscrepancyTransportDocPage(1)) match {
      case None => createRoute.DiscrepancyTransportDocController.onPageLoad(1, CheckMode)
      case _    => createRoute.CYASubmissionController.onPageLoad()
    }

  private def discrepancyTransportDocRoute(answers: UserAnswers, docIndex: Int, mode: Mode): Call =
    (answers.get(DiscrepancyTransportDocPage(docIndex)), mode) match {
      case (Some(_), _) => createRoute.TransportDocCYAController.onPageLoad(docIndex, mode)
      case (None, _) if hasExistingTransportDocs(answers) =>
        createRoute.AddAnotherTransportDocController.onPageLoad(None, mode)
      case (None, NormalMode) => createRoute.DiscrepancyGoodsController.onPageLoad(1, mode)
      case (None, CheckMode)  => createRoute.CYASubmissionController.onPageLoad()
    }

  private def hasExistingTransportDocs(userAnswers: UserAnswers): Boolean =
    DiscrepancyTransportDoc.count(userAnswers) > 0

  private def discrepancyTransportEquipmentRoute(userAnswers: UserAnswers, transportEquipmentIndex: Int, mode: Mode): Call =
    if (!userAnswers.get(VisitedDiscrepancySealsPage(transportEquipmentIndex)).contains(true)) {
      createRoute.DiscrepancySealsController.onPageLoad(transportEquipmentIndex, 1, mode)
    } else if (!userAnswers.get(VisitedDiscrepancyReferencePage(transportEquipmentIndex)).contains(true)) {
      createRoute.DiscrepancyReferenceController.onPageLoad(transportEquipmentIndex, 1, mode)
    } else {
      mode match {
        case NormalMode => createRoute.AddAnotherTransportController.onPageLoad(None, NormalMode)
        case CheckMode  => createRoute.CYASubmissionController.onPageLoad()
      }
    }

  private def discrepancySealsRoute(userAnswers: UserAnswers, transportEquipmentIndex: Int, sealsIndex: Int, mode: Mode): Call =
    (userAnswers.get(DiscrepancySealsPage(transportEquipmentIndex, sealsIndex)), mode) match {
      case (Some(_), _) =>
        createRoute.SealsCYAController.onPageLoad(transportEquipmentIndex, sealsIndex, mode)
      case (None, _) if hasExistingSeals(userAnswers, transportEquipmentIndex) =>
        createRoute.AddAnotherSealController.onPageLoad(transportEquipmentIndex, None, mode)
      case (None, NormalMode) => createRoute.DiscrepancyReferenceController.onPageLoad(transportEquipmentIndex, 1, NormalMode)
      case (None, CheckMode)  => createRoute.CYASubmissionController.onPageLoad()
    }

  private def hasExistingSeals(userAnswers: UserAnswers, transportEquipmentIndex: Int): Boolean =
    DiscrepancySeals(transportEquipmentIndex).count(userAnswers) > 0

  private def discrepancyGoodsRoute(userAnswers: UserAnswers, goodsItemIndex: Int, mode: Mode): Call =
    (userAnswers.get(DiscrepancyGoodsPage(goodsItemIndex))) match {
      case Some(_)                                   => createRoute.DiscrepancyPackingController.onPageLoad(goodsItemIndex, 1, mode)
      case None if hasExistingGoodsItem(userAnswers) => createRoute.AddAnotherGoodsItemController.onPageLoad(None, mode)
      case None                                      => createRoute.CYASubmissionController.onPageLoad()
    }

  private def hasExistingGoodsItem(userAnswers: UserAnswers): Boolean =
    DiscrepancyGoods.count(userAnswers) > 0

  private def discrepancyPackingRoute(userAnswers: UserAnswers, goodsItemIndex: Int, packagingDetailIndex: Int, mode: Mode): Call =
    (userAnswers.get(DiscrepancyPackingPage(goodsItemIndex, packagingDetailIndex)), mode) match {
      case (Some(_), _) => createRoute.PackagingDetailsCYAController.onPageLoad(goodsItemIndex, packagingDetailIndex, mode)
      case (None, _) if hasExistingPacking(userAnswers, goodsItemIndex) =>
        createRoute.AddAnotherPackagingDetailController.onPageLoad(goodsItemIndex, None, mode)
      case (None, NormalMode) => createRoute.AddAnotherGoodsItemController.onPageLoad(None, mode)
      case (None, CheckMode)  => createRoute.CYASubmissionController.onPageLoad()
    }

  private def hasExistingPacking(userAnswers: UserAnswers, goodsItemIndex: Int): Boolean =
    DiscrepancyPacking(goodsItemIndex).count(userAnswers) > 0

}
