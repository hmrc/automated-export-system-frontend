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

import uk.gov.hmrc.automatedexportsystemfrontend.controllers.create.routes as happyRoute
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.discrepancies.routes as discrepanciesRoute
import uk.gov.hmrc.automatedexportsystemfrontend.helpers.SpecBase
import uk.gov.hmrc.automatedexportsystemfrontend.models.*
import uk.gov.hmrc.automatedexportsystemfrontend.pages.discrepancies.*

class UnhappyPathNavigatorSpec extends SpecBase {

  val navigator = new UnhappyPathNavigator

  "UnhappyPathNavigator" - {

    "in Normal mode" - {

      "navigate from DiscrepancyConsignmentPage" - {
        "to DiscrepancyDucrPage" in {
          val userAnswers = emptyUserAnswers.set(DiscrepancyConsignmentPage, ModeOfTransportAtBorder.values.head).success.value
          navigator.nextPage(DiscrepancyConsignmentPage, NormalMode, userAnswers) shouldBe
            discrepanciesRoute.DiscrepancyDucrController.onPageLoad(NormalMode)
        }
      }

      "navigate from DiscrepancyDucrPage" - {
        "to DiscrepancyMucrPage" in {
          val userAnswers = emptyUserAnswers.set(DiscrepancyDucrPage, "ducr").success.value
          navigator.nextPage(DiscrepancyDucrPage, NormalMode, userAnswers) shouldBe
            discrepanciesRoute.DiscrepancyMucrController.onPageLoad(NormalMode)
        }
      }

      "navigate from DiscrepancyMucrPage" - {
        "to DiscrepancyTransportPage" in {
          val userAnswers = emptyUserAnswers.set(DiscrepancyMucrPage, "mucr").success.value
          navigator.nextPage(DiscrepancyMucrPage, NormalMode, userAnswers) shouldBe
            discrepanciesRoute.DiscrepancyTransportController.onPageLoad(NormalMode)
        }
      }

      "navigate from DiscrepancyTransportPage" - {
        "to DiscrepancySealsPage" in {
          val userAnswers = emptyUserAnswers.set(DiscrepancyTransportPage, ContainerDetails("containerId", 99)).success.value
          navigator.nextPage(DiscrepancyTransportPage, NormalMode, userAnswers) shouldBe
            discrepanciesRoute.DiscrepancySealsController.onPageLoad(NormalMode)
        }
      }

      "navigate from DiscrepancySealsPage" - {
        "to DiscrepancyReferencePage" in {
          val userAnswers = emptyUserAnswers.set(DiscrepancySealsPage, "sealId").success.value
          navigator.nextPage(DiscrepancySealsPage, NormalMode, userAnswers) shouldBe
            discrepanciesRoute.DiscrepancyReferenceController.onPageLoad(NormalMode)
        }
      }

      "navigate from DiscrepancyReferencePage" - {
        "to LocationTypePage" in {
          val userAnswers = emptyUserAnswers.set(DiscrepancyReferencePage, "reference").success.value
          navigator.nextPage(DiscrepancyReferencePage, NormalMode, userAnswers) shouldBe
            discrepanciesRoute.LocationTypeController.onPageLoad(NormalMode)
        }
      }

      "navigate from LocationTypePage" - {
        "to LocationIdPage" in {
          val userAnswers = emptyUserAnswers.set(LocationTypePage, LocationType.values.head).success.value
          navigator.nextPage(LocationTypePage, NormalMode, userAnswers) shouldBe
            discrepanciesRoute.LocationIdController.onPageLoad(NormalMode)
        }
      }

      "navigate from LocationIdPage" - {
        "to DiscrepancyTransportMeansPage" in {
          val userAnswers = emptyUserAnswers
            .set(LocationIdPage, LocationDetails("locationType", "unlocode", "locationAdditionalIdentifier", "authorisationReferenceNumber"))
            .success
            .value
          navigator.nextPage(LocationIdPage, NormalMode, userAnswers) shouldBe
            discrepanciesRoute.DiscrepancyTransportMeansController.onPageLoad(NormalMode)
        }
      }

      "navigate from DiscrepancyTransportMeansPage" - {
        "to DiscrepancyTransportDocPage" in {
          val userAnswers = emptyUserAnswers
            .set(DiscrepancyTransportMeansPage, TransportAcrossBorderDetails("transportType", "transportIdNumber", "countryOfRegistration"))
            .success
            .value
          navigator.nextPage(DiscrepancyTransportMeansPage, NormalMode, userAnswers) shouldBe
            discrepanciesRoute.DiscrepancyTransportDocController.onPageLoad(NormalMode)
        }
      }

      "navigate from DiscrepancyTransportDocPage" - {
        "to DiscrepancyGoodsPage" in {
          val userAnswers = emptyUserAnswers
            .set(DiscrepancyTransportDocPage, DocumentDetails("documentType", "referenceNumber"))
            .success
            .value
          navigator.nextPage(DiscrepancyTransportDocPage, NormalMode, userAnswers) shouldBe
            discrepanciesRoute.DiscrepancyGoodsController.onPageLoad(NormalMode)
        }
      }

      "navigate from DiscrepancyGoodsPage" - {
        "to DiscrepancyPackingPage" in {
          val userAnswers = emptyUserAnswers
            .set(
              DiscrepancyGoodsPage,
              WhatHasChangedDetails("goodsItemNumber", Some("declarationUniqueConsignmentReference"), "newGrossMass", "newNetMass")
            )
            .success
            .value
          navigator.nextPage(DiscrepancyGoodsPage, NormalMode, userAnswers) shouldBe
            discrepanciesRoute.DiscrepancyPackingController.onPageLoad(NormalMode)
        }
      }

      "navigate from DiscrepancyPackingPage" - {
        "to CYASubmissionPage" in {
          val userAnswers = emptyUserAnswers
            .set(DiscrepancyPackingPage, PackingDetails("packagingCode", "numberOfPackages", "shippingMarks"))
            .success
            .value
          navigator.nextPage(DiscrepancyPackingPage, NormalMode, userAnswers) shouldBe
            happyRoute.CYASubmissionController.onPageLoad()
        }
      }
    }
  }
}
