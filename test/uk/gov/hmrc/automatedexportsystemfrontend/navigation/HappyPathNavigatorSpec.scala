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

import uk.gov.hmrc.automatedexportsystemfrontend.helpers.SpecBase
import uk.gov.hmrc.automatedexportsystemfrontend.models.{NormalMode, OfficeOfExit, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.navigation.HappyPathNavigator
import uk.gov.hmrc.automatedexportsystemfrontend.pages.OfficeOfExitPage
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.happyPath.routes as happyRoute
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.routes as appRoute
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.problem.routes as problemRoute
import uk.gov.hmrc.automatedexportsystemfrontend.pages.happyPath.{
  AnyDiscrepanciesPage,
  EnterDucrPage,
  EnterMrnPage,
  IsSplitExitPage,
  PartOfConsolidationPage
}

class HappyPathNavigatorSpec extends SpecBase {

  val navigator = new HappyPathNavigator

  "ThirdPartyNavigator" - {

    "in Normal mode" - {

      "navigate from EnterMrnPage" - {
        "to EnterDucrPage" in {
          val userAnswers = emptyUserAnswers.set(EnterMrnPage, "TEST").success.value
          navigator.nextPage(EnterMrnPage, NormalMode, userAnswers) shouldBe
            happyRoute.EnterDucrController.onPageLoad(NormalMode)
        }
      }

      "navigate from EnterDucrPage" - {
        "to OfficeOfExitPage" in {
          val userAnswers = emptyUserAnswers.set(EnterDucrPage, "TEST").success.value
          navigator.nextPage(EnterDucrPage, NormalMode, userAnswers) shouldBe
            happyRoute.OfficeOfExitController.onPageLoad(NormalMode)
        }
      }

      "navigate from OfficeOfExitPage" - {
        "to AnyDiscrepancies" in {
          val userAnswers = emptyUserAnswers.set(OfficeOfExitPage, OfficeOfExit.Belfast).success.value
          navigator.nextPage(OfficeOfExitPage, NormalMode, userAnswers) shouldBe
            happyRoute.PartOfConsolidationController.onPageLoad(NormalMode)
        }
      }

      "navigate from PartOfConsolidationPage" - {
        "to IsSplitExitPage when true" in {
          val userAnswers = emptyUserAnswers.set(PartOfConsolidationPage, true).success.value
          navigator.nextPage(PartOfConsolidationPage, NormalMode, userAnswers) shouldBe
            happyRoute.IsSplitExitController.onPageLoad(NormalMode)
        }
        "to JourneyRecovery TEMPORARY when false" in {
          val userAnswers = emptyUserAnswers.set(PartOfConsolidationPage, false).success.value
          navigator.nextPage(PartOfConsolidationPage, NormalMode, userAnswers) shouldBe
            problemRoute.JourneyRecoveryController.onPageLoad()
        }
      }

      "navigate from IsSplitExitPage" - {
        "to AnyDiscrepanciesPage when true" in {
          val userAnswers = emptyUserAnswers.set(IsSplitExitPage, true).success.value
          navigator.nextPage(IsSplitExitPage, NormalMode, userAnswers) shouldBe
            happyRoute.AnyDiscrepanciesController.onPageLoad(NormalMode)
        }
        "to JourneyRecovery TEMPORARY when false" in {
          val userAnswers = emptyUserAnswers.set(PartOfConsolidationPage, false).success.value
          navigator.nextPage(PartOfConsolidationPage, NormalMode, userAnswers) shouldBe
            problemRoute.JourneyRecoveryController.onPageLoad()
        }
      }

      "navigate from AnyDiscrepanciesPage" - {
        "to CYASubmissionController when true" in {
          val userAnswers = emptyUserAnswers.set(AnyDiscrepanciesPage, true).success.value
          navigator.nextPage(AnyDiscrepanciesPage, NormalMode, userAnswers) shouldBe
            appRoute.CYASubmissionController.onPageLoad()
        }
        "to JourneyRecovery TEMPORARY when false" in {
          val userAnswers = emptyUserAnswers.set(PartOfConsolidationPage, false).success.value
          navigator.nextPage(PartOfConsolidationPage, NormalMode, userAnswers) shouldBe
            problemRoute.JourneyRecoveryController.onPageLoad()
        }
      }
    }
  }
}
