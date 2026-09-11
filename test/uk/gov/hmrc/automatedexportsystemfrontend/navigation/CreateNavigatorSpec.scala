/*
 * Copyright 2026 HM Revenue & Customs
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

import uk.gov.hmrc.automatedexportsystemfrontend.controllers.create.routes as createRoute
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.problem.routes as problemRoute
import uk.gov.hmrc.automatedexportsystemfrontend.helpers.SpecBase
import uk.gov.hmrc.automatedexportsystemfrontend.models.*
import uk.gov.hmrc.automatedexportsystemfrontend.navigation.CreateNavigator
import uk.gov.hmrc.automatedexportsystemfrontend.pages.Page
import uk.gov.hmrc.automatedexportsystemfrontend.pages.create.*

class CreateNavigatorSpec extends SpecBase {

  val navigator = new CreateNavigator

  "CreateNavigator" - {

    "in Normal mode" - {
      "navigate from IsSplitExitPage" - {
        "to AnyDiscrepanciesPage when false" in {
          val userAnswers = emptyUserAnswers.set(IsSplitExitPage, false).success.value
          navigator.nextPage(IsSplitExitPage, NormalMode, userAnswers) shouldBe
            createRoute.AnyDiscrepanciesController.onPageLoad(NormalMode)
        }
        "to DiscrepancyConsignmentPage true" in {
          val userAnswers = emptyUserAnswers.set(IsSplitExitPage, true).success.value
          navigator.nextPage(IsSplitExitPage, NormalMode, userAnswers) shouldBe
            createRoute.DiscrepancyConsignmentController.onPageLoad(NormalMode)
        }
        "to JourneyRecovery TEMPORARY when None" in {
          val userAnswers = emptyUserAnswers
          navigator.nextPage(IsSplitExitPage, NormalMode, userAnswers) shouldBe
            problemRoute.JourneyRecoveryController.onPageLoad()
        }
      }

      "navigate from AnyDiscrepanciesPage" - {
        "to CYASubmissionController when false" in {
          val userAnswers = emptyUserAnswers.set(AnyDiscrepanciesPage, false).success.value
          navigator.nextPage(AnyDiscrepanciesPage, NormalMode, userAnswers) shouldBe
            createRoute.CYASubmissionController.onPageLoad()
        }
        "to DiscrepancyConsignmentPage when true" in {
          val userAnswers = emptyUserAnswers.set(AnyDiscrepanciesPage, true).success.value
          navigator.nextPage(AnyDiscrepanciesPage, NormalMode, userAnswers) shouldBe
            createRoute.DiscrepancyConsignmentController.onPageLoad(NormalMode)
        }
        "to JourneyRecovery TEMPORARY when None" in {
          val userAnswers = emptyUserAnswers
          navigator.nextPage(AnyDiscrepanciesPage, NormalMode, userAnswers) shouldBe
            problemRoute.JourneyRecoveryController.onPageLoad()
        }
      }

      "navigate from DiscrepancyConsignmentPage" - {
        "to DiscrepancyTransportPage" in {
          val userAnswers = emptyUserAnswers.set(DiscrepancyConsignmentPage, ModeOfTransportAtBorder.values.head).success.value
          navigator.nextPage(DiscrepancyConsignmentPage, NormalMode, userAnswers) shouldBe
            createRoute.DiscrepancyTransportController.onPageLoad(NormalMode)
        }
      }

      "navigate from DiscrepancyTransportPage" - {
        "to DiscrepancySealsPage" in {
          val userAnswers = emptyUserAnswers.set(DiscrepancyTransportPage, ContainerDetails("containerId", 99)).success.value
          navigator.nextPage(DiscrepancyTransportPage, NormalMode, userAnswers) shouldBe
            createRoute.DiscrepancySealsController.onPageLoad(NormalMode)
        }
      }

      "navigate from DiscrepancySealsPage" - {
        "to DiscrepancyReferencePage" in {
          val userAnswers = emptyUserAnswers.set(DiscrepancySealsPage, "sealId").success.value
          navigator.nextPage(DiscrepancySealsPage, NormalMode, userAnswers) shouldBe
            createRoute.DiscrepancyReferenceController.onPageLoad(NormalMode)
        }
      }

      "navigate from DiscrepancyReferencePage" - {
        "to DiscrepancyTransportMeansPage" in {
          val userAnswers = emptyUserAnswers.set(DiscrepancyReferencePage, "reference").success.value
          navigator.nextPage(DiscrepancyReferencePage, NormalMode, userAnswers) shouldBe
            createRoute.DiscrepancyTransportMeansController.onPageLoad(NormalMode)
        }
      }

      "navigate from DiscrepancyTransportMeansPage" - {
        "to DiscrepancyTransportDocPage" in {
          val userAnswers = emptyUserAnswers
            .set(DiscrepancyTransportMeansPage, TransportAcrossBorderDetails("transportType", "transportIdNumber", "countryOfRegistration"))
            .success
            .value
          navigator.nextPage(DiscrepancyTransportMeansPage, NormalMode, userAnswers) shouldBe
            createRoute.DiscrepancyTransportDocController.onPageLoad(NormalMode)
        }
      }

      "navigate from DiscrepancyTransportDocPage" - {
        "to DiscrepancyGoodsPage" in {
          val userAnswers = emptyUserAnswers
            .set(DiscrepancyTransportDocPage, DocumentDetails(Some(1), Some(1234)))
            .success
            .value
          navigator.nextPage(DiscrepancyTransportDocPage, NormalMode, userAnswers) shouldBe
            createRoute.DiscrepancyGoodsController.onPageLoad(NormalMode)
        }
      }

      "navigate from DiscrepancyGoodsPage" - {
        "to DiscrepancyPackingPage" in {
          val userAnswers = emptyUserAnswers
            .set(DiscrepancyGoodsPage, WhatHasChangedDetails(Some(1), Some("declarationUniqueConsignmentReference"), "newGrossMass", "newNetMass"))
            .success
            .value
          navigator.nextPage(DiscrepancyGoodsPage, NormalMode, userAnswers) shouldBe
            createRoute.DiscrepancyPackingController.onPageLoad(NormalMode)
        }
      }

      "navigate from DiscrepancyPackingPage" - {
        "to CYASubmissionPage" in {
          val userAnswers = emptyUserAnswers
            .set(DiscrepancyPackingPage, PackingDetails("packagingCode", 1, "shippingMarks"))
            .success
            .value
          navigator.nextPage(DiscrepancyPackingPage, NormalMode, userAnswers) shouldBe
            createRoute.CYASubmissionController.onPageLoad()
        }
      }
    }

    "in Check mode" - {
      "navigate from EnterMrnPage" - {
        "to CYASubmissionPage" in {
          navigator.nextPage(EnterMrnPage, CheckMode, emptyUserAnswers) shouldBe
            createRoute.CYASubmissionController.onPageLoad()
        }
      }

      "navigate from EnterDucrPage" - {
        "to CYASubmissionPage" in {
          navigator.nextPage(EnterDucrPage, CheckMode, emptyUserAnswers) shouldBe
            createRoute.CYASubmissionController.onPageLoad()
        }
      }

      "navigate from PartOfConsolidationPage" - {
        "to CYASubmissionPage" in {
          navigator.nextPage(PartOfConsolidationPage, CheckMode, emptyUserAnswers) shouldBe
            createRoute.CYASubmissionController.onPageLoad()
        }
      }

      "navigate from LocationTypePage" - {
        "to CYASubmissionPage" in {
          navigator.nextPage(LocationTypePage, CheckMode, emptyUserAnswers) shouldBe
            createRoute.CYASubmissionController.onPageLoad()
        }
      }

      "navigate from LocationIdPage" - {
        "to CYASubmissionPage" in {
          navigator.nextPage(LocationIdPage, CheckMode, emptyUserAnswers) shouldBe
            createRoute.CYASubmissionController.onPageLoad()
        }
      }

      "navigate from OfficeOfExitPage" - {
        "to CYASubmissionPage" in {
          navigator.nextPage(OfficeOfExitPage, CheckMode, emptyUserAnswers) shouldBe
            createRoute.CYASubmissionController.onPageLoad()
        }
      }

      "navigate from IsSplitExitPage" - {
        "to DiscrepancyConsignmentPage when IsSplitExitPage is true and DiscrepancyConsignmentPage is unanswered" in {
          val userAnswers = emptyUserAnswers.set(IsSplitExitPage, true).success.value
          navigator.nextPage(IsSplitExitPage, CheckMode, userAnswers) shouldBe
            createRoute.DiscrepancyConsignmentController.onPageLoad(CheckMode)
        }

        "to CYASubmissionPage when IsSplitExitPage is true and DiscrepancyConsignmentPage is answered" in {
          val userAnswers = emptyUserAnswers
            .set(IsSplitExitPage, true)
            .success
            .value
            .set(DiscrepancyConsignmentPage, ModeOfTransportAtBorder.Sea)
            .success
            .value
          navigator.nextPage(IsSplitExitPage, CheckMode, userAnswers) shouldBe
            createRoute.CYASubmissionController.onPageLoad()
        }

        "to AnyDiscrepanciesPage when IsSplitExitPage is false and AnyDiscrepanciesPage is unanswered" in {
          val userAnswers = emptyUserAnswers.set(IsSplitExitPage, false).success.value
          navigator.nextPage(IsSplitExitPage, CheckMode, userAnswers) shouldBe
            createRoute.AnyDiscrepanciesController.onPageLoad(CheckMode)
        }

        "to CYASubmissionPage when IsSplitExitPage is false and AnyDiscrepanciesPage is answered" in {
          val userAnswers = emptyUserAnswers
            .set(IsSplitExitPage, false)
            .success
            .value
            .set(AnyDiscrepanciesPage, false)
            .success
            .value
          navigator.nextPage(IsSplitExitPage, CheckMode, userAnswers) shouldBe
            createRoute.CYASubmissionController.onPageLoad()
        }

        "to JourneyRecovery if IsSplitExitPage is unanswered" in {
          navigator.nextPage(IsSplitExitPage, CheckMode, emptyUserAnswers) shouldBe
            problemRoute.JourneyRecoveryController.onPageLoad()
        }
      }

      "navigate from AnyDiscrepanciesPage" - {
        "to DiscrepancyConsignmentPage when AnyDiscrepanciesPage is true and DiscrepancyConsignmentPage is unanswered" in {
          val userAnswers = emptyUserAnswers.set(AnyDiscrepanciesPage, true).success.value
          navigator.nextPage(AnyDiscrepanciesPage, CheckMode, userAnswers) shouldBe
            createRoute.DiscrepancyConsignmentController.onPageLoad(CheckMode)
        }

        "to CYASubmissionPage when AnyDiscrepanciesPage is true and DiscrepancyConsignmentPage is answered" in {
          val userAnswers = emptyUserAnswers
            .set(AnyDiscrepanciesPage, true)
            .success
            .value
            .set(DiscrepancyConsignmentPage, ModeOfTransportAtBorder.Sea)
            .success
            .value
          navigator.nextPage(AnyDiscrepanciesPage, CheckMode, userAnswers) shouldBe
            createRoute.CYASubmissionController.onPageLoad()
        }

        "to CYASubmissionPage when AnyDiscrepanciesPage is false" in {
          val userAnswers = emptyUserAnswers.set(AnyDiscrepanciesPage, false).success.value
          navigator.nextPage(AnyDiscrepanciesPage, CheckMode, userAnswers) shouldBe
            createRoute.CYASubmissionController.onPageLoad()
        }

        "to JourneyRecovery if AnyDiscrepanciesPage is unanswered" in {
          navigator.nextPage(AnyDiscrepanciesPage, CheckMode, emptyUserAnswers) shouldBe
            problemRoute.JourneyRecoveryController.onPageLoad()
        }
      }

      "navigate from DiscrepancyConsignmentPage" - {
        "to DiscrepancyTransportPage when DiscrepancyTransportPage is unanswered" in {
          navigator.nextPage(DiscrepancyConsignmentPage, CheckMode, emptyUserAnswers) shouldBe
            createRoute.DiscrepancyTransportController.onPageLoad(CheckMode)
        }

        "to CYASubmissionPage when DiscrepancyTransportPage is answered" in {
          val userAnswers = emptyUserAnswers.set(DiscrepancyTransportPage, ContainerDetails("containerId", 99)).success.value
          navigator.nextPage(DiscrepancyConsignmentPage, CheckMode, userAnswers) shouldBe
            createRoute.CYASubmissionController.onPageLoad()
        }
      }

      "navigate from DiscrepancyTransportPage" - {
        "to DiscrepancySealsPage when DiscrepancySealsPage is unanswered" in {
          navigator.nextPage(DiscrepancyTransportPage, CheckMode, emptyUserAnswers) shouldBe
            createRoute.DiscrepancySealsController.onPageLoad(CheckMode)
        }

        "to CYASubmissionPage when DiscrepancySealsPage is answered" in {
          val userAnswers = emptyUserAnswers.set(DiscrepancySealsPage, "sealIdentifier").success.value
          navigator.nextPage(DiscrepancyTransportPage, CheckMode, userAnswers) shouldBe
            createRoute.CYASubmissionController.onPageLoad()
        }
      }

      "navigate from DiscrepancySealsPage" - {
        "to DiscrepancyReferencePage when DiscrepancyReferencePage is unanswered" in {
          navigator.nextPage(DiscrepancySealsPage, CheckMode, emptyUserAnswers) shouldBe
            createRoute.DiscrepancyReferenceController.onPageLoad(CheckMode)
        }

        "to CYASubmissionPage when DiscrepancyReferencePage is answered" in {
          val userAnswers = emptyUserAnswers.set(DiscrepancyReferencePage, "reference").success.value
          navigator.nextPage(DiscrepancySealsPage, CheckMode, userAnswers) shouldBe
            createRoute.CYASubmissionController.onPageLoad()
        }
      }

      "navigate from DiscrepancyReferencePage" - {
        "to DiscrepancyTransportMeansPage when DiscrepancyTransportMeansPage is unanswered" in {
          navigator.nextPage(DiscrepancyReferencePage, CheckMode, emptyUserAnswers) shouldBe
            createRoute.DiscrepancyTransportMeansController.onPageLoad(CheckMode)
        }

        "to CYASubmissionPage when DiscrepancyTransportMeans is answered" in {
          val userAnswers = emptyUserAnswers
            .set(DiscrepancyTransportMeansPage, TransportAcrossBorderDetails("transportType", "transportIdNum", "countryOfRegistration"))
            .success
            .value
          navigator.nextPage(DiscrepancyReferencePage, CheckMode, userAnswers) shouldBe
            createRoute.CYASubmissionController.onPageLoad()
        }
      }

      "navigate from DiscrepancyTransportMeansPage" - {
        "to DiscrepancyTransportDocPage when DiscrepancyTransportDocPage is unanswered" in {
          navigator.nextPage(DiscrepancyTransportMeansPage, CheckMode, emptyUserAnswers) shouldBe
            createRoute.DiscrepancyTransportDocController.onPageLoad(CheckMode)
        }

        "to CYASubmissionPage when DiscrepancyTransportDocPage is answered" in {
          val userAnswers = emptyUserAnswers
            .set(DiscrepancyTransportDocPage, DocumentDetails(None, None))
            .success
            .value
          navigator.nextPage(DiscrepancyTransportMeansPage, CheckMode, userAnswers) shouldBe
            createRoute.CYASubmissionController.onPageLoad()
        }
      }

      "navigate from DiscrepancyTransportDocPage" - {
        "to DiscrepancyGoodsPage when DiscrepancyGoodsPage is unanswered" in {
          navigator.nextPage(DiscrepancyTransportDocPage, CheckMode, emptyUserAnswers) shouldBe
            createRoute.DiscrepancyGoodsController.onPageLoad(CheckMode)
        }

        "to CYASubmissionPage when DiscrepancyGoodsPage is answered" in {
          val userAnswers = emptyUserAnswers
            .set(DiscrepancyGoodsPage, WhatHasChangedDetails(None, None, "20", "10"))
            .success
            .value
          navigator.nextPage(DiscrepancyTransportDocPage, CheckMode, userAnswers) shouldBe
            createRoute.CYASubmissionController.onPageLoad()
        }
      }

      "navigate from DiscrepancyGoodsPage" - {
        "to DiscrepancyPackingPage when DiscrepancyPackingPage is unanswered" in {
          navigator.nextPage(DiscrepancyGoodsPage, CheckMode, emptyUserAnswers) shouldBe
            createRoute.DiscrepancyPackingController.onPageLoad(CheckMode)
        }

        "to CYASubmissionPage when DiscrepancyPackingPage is answered" in {
          val userAnswers = emptyUserAnswers
            .set(DiscrepancyPackingPage, PackingDetails("packagingCode", 2, "shippingMarks"))
            .success
            .value
          navigator.nextPage(DiscrepancyGoodsPage, CheckMode, userAnswers) shouldBe
            createRoute.CYASubmissionController.onPageLoad()
        }
      }

      Seq(EnterMrnPage, EnterDucrPage, PartOfConsolidationPage, LocationTypePage, LocationIdPage, OfficeOfExitPage, DiscrepancyPackingPage).foreach {
        page =>
          s"navigate from $page" - {
            "to CYASubmissionPage" in {
              navigator.nextPage(page, CheckMode, emptyUserAnswers) shouldBe
                createRoute.CYASubmissionController.onPageLoad()
            }
          }
      }

      "navigate from an UnknownPage" - {
        "to CYASubmissionPage" in {
          case object UnknownPage extends Page

          navigator.nextPage(UnknownPage, CheckMode, emptyUserAnswers) shouldBe
            createRoute.CYASubmissionController.onPageLoad()
        }
      }
    }
  }
}
