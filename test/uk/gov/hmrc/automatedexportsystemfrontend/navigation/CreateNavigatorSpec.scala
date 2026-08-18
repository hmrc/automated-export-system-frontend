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
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.discrepancies.routes as discrepanciesRoute
import uk.gov.hmrc.automatedexportsystemfrontend.helpers.SpecBase
import uk.gov.hmrc.automatedexportsystemfrontend.models.{NormalMode, OfficeOfExit, PartOfConsolidationAnswer}
import uk.gov.hmrc.automatedexportsystemfrontend.navigation.CreateNavigator
import uk.gov.hmrc.automatedexportsystemfrontend.pages.create.*

class CreateNavigatorSpec extends SpecBase {

  val navigator = new CreateNavigator

  "ThirdPartyNavigator" - {

    "in Normal mode" - {

      "navigate from EnterMrnPage" - {
        "to EnterDucrPage" in {
          val userAnswers = emptyUserAnswers.set(EnterMrnPage, "TEST").success.value
          navigator.nextPage(EnterMrnPage, NormalMode, userAnswers) shouldBe
            createRoute.EnterDucrController.onPageLoad(NormalMode)
        }
      }

      "navigate from EnterDucrPage" - {
        "to PartOfConsolidationpage" in {
          val userAnswers = emptyUserAnswers.set(EnterDucrPage, "TEST").success.value
          navigator.nextPage(EnterDucrPage, NormalMode, userAnswers) shouldBe
            createRoute.PartOfConsolidationController.onPageLoad(NormalMode)
        }
      }

      "navigate from OfficeOfExitPage" - {
        "to IsSplitExitPage" in {
          val userAnswers = emptyUserAnswers.set(OfficeOfExitPage, OfficeOfExit.Belfast).success.value
          navigator.nextPage(OfficeOfExitPage, NormalMode, userAnswers) shouldBe
            createRoute.IsSplitExitController.onPageLoad(NormalMode)
        }
      }

      "navigate from PartOfConsolidationPage" - {
        "to OfficeOfExitPage when true" in {
          val userAnswers = emptyUserAnswers.set(PartOfConsolidationPage, PartOfConsolidationAnswer(true, Some("mucr"))).success.value
          navigator.nextPage(PartOfConsolidationPage, NormalMode, userAnswers) shouldBe
            createRoute.OfficeOfExitController.onPageLoad(NormalMode)
        }
        "to OfficeOfExitPage when false" in {
          val userAnswers = emptyUserAnswers.set(PartOfConsolidationPage, PartOfConsolidationAnswer(false, None)).success.value
          navigator.nextPage(PartOfConsolidationPage, NormalMode, userAnswers) shouldBe
            createRoute.OfficeOfExitController.onPageLoad(NormalMode)
        }
        "to JourneyRecovery when None" in {
          val userAnswers = emptyUserAnswers
          navigator.nextPage(PartOfConsolidationPage, NormalMode, userAnswers) shouldBe
            problemRoute.JourneyRecoveryController.onPageLoad()
        }
      }

      "navigate from IsSplitExitPage" - {
        "to AnyDiscrepanciesPage when false" in {
          val userAnswers = emptyUserAnswers.set(IsSplitExitPage, false).success.value
          navigator.nextPage(IsSplitExitPage, NormalMode, userAnswers) shouldBe
            createRoute.AnyDiscrepanciesController.onPageLoad(NormalMode)
        }
        "to DiscrepancyConsignmentPage true" in {
          val userAnswers = emptyUserAnswers.set(IsSplitExitPage, true).success.value
          navigator.nextPage(IsSplitExitPage, NormalMode, userAnswers) shouldBe
            discrepanciesRoute.DiscrepancyConsignmentController.onPageLoad(NormalMode)
        }
        "to JourneyRecovery TEMPORARY when None" in {
          val userAnswers = emptyUserAnswers
          navigator.nextPage(PartOfConsolidationPage, NormalMode, userAnswers) shouldBe
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
            discrepanciesRoute.DiscrepancyConsignmentController.onPageLoad(NormalMode)
        }
        "to JourneyRecovery TEMPORARY when None" in {
          val userAnswers = emptyUserAnswers
          navigator.nextPage(PartOfConsolidationPage, NormalMode, userAnswers) shouldBe
            problemRoute.JourneyRecoveryController.onPageLoad()
        }
      }
    }
  }
}
