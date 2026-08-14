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

import uk.gov.hmrc.automatedexportsystemfrontend.controllers.problem.routes as problemRoute
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.unhappyPath.routes as unhappyRoute
import uk.gov.hmrc.automatedexportsystemfrontend.helpers.SpecBase
import uk.gov.hmrc.automatedexportsystemfrontend.models.{ContainerDetails, ModeOfTransportAtBorder, NormalMode}
import uk.gov.hmrc.automatedexportsystemfrontend.pages.unhappyPath.{
  DiscrepancyConsignmentPage,
  DiscrepancyDucrPage,
  DiscrepancyMucrPage,
  DiscrepancyReferencePage,
  DiscrepancySealsPage,
  DiscrepancyTransportPage
}

class UnhappyPathNavigatorSpec extends SpecBase {

  val navigator = new UnhappyPathNavigator

  "UnhappyPathNavigator" - {

    "in Normal mode" - {

      "navigate from DiscrepancyConsignmentPage" - {
        "to DiscrepancyDucrPage" in {
          val userAnswers = emptyUserAnswers.set(DiscrepancyConsignmentPage, ModeOfTransportAtBorder.values.head).success.value
          navigator.nextPage(DiscrepancyConsignmentPage, NormalMode, userAnswers) shouldBe
            unhappyRoute.DiscrepancyDucrController.onPageLoad(NormalMode)
        }
      }

      "navigate from DiscrepancyDucrPage" - {
        "to DiscrepancyMucrPage" in {
          val userAnswers = emptyUserAnswers.set(DiscrepancyDucrPage, "ducr").success.value
          navigator.nextPage(DiscrepancyDucrPage, NormalMode, userAnswers) shouldBe
            unhappyRoute.DiscrepancyMucrController.onPageLoad(NormalMode)
        }
      }

      "navigate from DiscrepancyMucrPage" - {
        "to DiscrepancyTransportPage" in {
          val userAnswers = emptyUserAnswers.set(DiscrepancyMucrPage, "mucr").success.value
          navigator.nextPage(DiscrepancyMucrPage, NormalMode, userAnswers) shouldBe
            unhappyRoute.DiscrepancyTransportController.onPageLoad(NormalMode)
        }
      }

      "navigate from DiscrepancyTransportPage" - {
        "to DiscrepancySealsPage" in {
          val userAnswers = emptyUserAnswers.set(DiscrepancyTransportPage, ContainerDetails("containerId", 99)).success.value
          navigator.nextPage(DiscrepancyTransportPage, NormalMode, userAnswers) shouldBe
            unhappyRoute.DiscrepancySealsController.onPageLoad(NormalMode)
        }
      }

      "navigate from DiscrepancySealsPage" - {
        "to DiscrepancyReferencePage" in {
          val userAnswers = emptyUserAnswers.set(DiscrepancySealsPage, "sealId").success.value
          navigator.nextPage(DiscrepancySealsPage, NormalMode, userAnswers) shouldBe
            unhappyRoute.DiscrepancyReferenceController.onPageLoad(NormalMode)
        }
      }

      "navigate from DiscrepancyReferencePage" - {
        "to JourneyRecovery TEMPORARY" in {
          val userAnswers = emptyUserAnswers.set(DiscrepancyReferencePage, "reference").success.value
          navigator.nextPage(DiscrepancyReferencePage, NormalMode, userAnswers) shouldBe
            problemRoute.JourneyRecoveryController.onPageLoad()
        }
      }
    }
  }
}
