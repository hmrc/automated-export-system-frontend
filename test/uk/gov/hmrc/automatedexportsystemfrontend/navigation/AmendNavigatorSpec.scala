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

import uk.gov.hmrc.automatedexportsystemfrontend.controllers.amend.routes as amendRoute
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.problem.routes as problemRoute
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.unhappyPath.routes as unhappyRoute
import uk.gov.hmrc.automatedexportsystemfrontend.helpers.SpecBase
import uk.gov.hmrc.automatedexportsystemfrontend.models.{NormalMode, OfficeOfExit, PartOfConsolidationAnswer}
import uk.gov.hmrc.automatedexportsystemfrontend.navigation.AmendNavigator
import uk.gov.hmrc.automatedexportsystemfrontend.pages.amend.*

class AmendSpec extends SpecBase {

  val navigator = new AmendNavigator

  "ThirdPartyNavigator" - {

    "in Normal mode" - {

      "navigate from AmendEnterMrnPage" - {
        "to AmendEnterDucrPage" in {
          val userAnswers = emptyUserAnswers.set(AmendEnterMrnPage("submissionId"), "TEST").success.value
          navigator.nextPage(AmendEnterMrnPage("submissionId"), NormalMode, userAnswers) shouldBe
            amendRoute.AmendEnterDucrController.onPageLoad(NormalMode, "submissionId")
        }
      }

      "navigate from AmendEnterDucrPage" - {
        "to AmendPartOfConsolidationpage" in {
          val userAnswers = emptyUserAnswers.set(AmendEnterMrnPage("submissionId"), "TEST").success.value
          navigator.nextPage(AmendEnterDucrPage("submissionId"), NormalMode, userAnswers) shouldBe
            amendRoute.AmendPartOfConsolidationController.onPageLoad(NormalMode, "submissionId")
        }
      }

      "navigate from AmendOfficeOfExitPage" - {
        "to AmendIsSplitExitPage" in {
          val userAnswers = emptyUserAnswers.set(AmendOfficeOfExitPage("submissionId"), OfficeOfExit.Belfast).success.value
          navigator.nextPage(AmendOfficeOfExitPage("submissionId"), NormalMode, userAnswers) shouldBe
            amendRoute.AmendIsSplitExitController.onPageLoad(NormalMode, "submissionId")
        }
      }

      "navigate from AmendPartOfConsolidationPage" - {
        "to AmendOfficeOfExitPage when true" in {
          val userAnswers =
            emptyUserAnswers.set(AmendPartOfConsolidationPage("submissionId"), PartOfConsolidationAnswer(true, Some("mucr"))).success.value
          navigator.nextPage(AmendPartOfConsolidationPage("submissionId"), NormalMode, userAnswers) shouldBe
            amendRoute.AmendOfficeOfExitController.onPageLoad(NormalMode, "submissionId")
        }
        "to AmendOfficeOfExitPage when false" in {
          val userAnswers = emptyUserAnswers.set(AmendPartOfConsolidationPage("submissionId"), PartOfConsolidationAnswer(false, None)).success.value
          navigator.nextPage(AmendPartOfConsolidationPage("submissionId"), NormalMode, userAnswers) shouldBe
            amendRoute.AmendOfficeOfExitController.onPageLoad(NormalMode, "submissionId")
        }
        "to JourneyRecovery when None" in {
          val userAnswers = emptyUserAnswers
          navigator.nextPage(AmendPartOfConsolidationPage("submissionId"), NormalMode, userAnswers) shouldBe
            problemRoute.JourneyRecoveryController.onPageLoad()
        }
      }

      "navigate from AmendIsSplitExitPage" - {
        "to AmendAnyDiscrepanciesPage when false" in {
          val userAnswers = emptyUserAnswers.set(AmendIsSplitExitPage("submissionId"), false).success.value
          navigator.nextPage(AmendIsSplitExitPage("submissionId"), NormalMode, userAnswers) shouldBe
            amendRoute.AmendAnyDiscrepanciesController.onPageLoad(NormalMode, "submissionId")
        }
        "to AmendDiscrepancyConsignmentPage true" in {
          val userAnswers = emptyUserAnswers.set(AmendIsSplitExitPage("submissionId"), true).success.value
          navigator.nextPage(AmendIsSplitExitPage("submissionId"), NormalMode, userAnswers) shouldBe
            unhappyRoute.DiscrepancyConsignmentController.onPageLoad(NormalMode)
        }
        "to JourneyRecovery TEMPORARY when None" in {
          val userAnswers = emptyUserAnswers
          navigator.nextPage(AmendPartOfConsolidationPage("submissionId"), NormalMode, userAnswers) shouldBe
            problemRoute.JourneyRecoveryController.onPageLoad()
        }
      }

//      "navigate from AmendAnyDiscrepanciesPage" - {
//        "to AmendCYASubmissionController when false" in {
//          val userAnswers = emptyUserAnswers.set(AmendAnyDiscrepanciesPage("submissionId"), false).success.value
//          navigator.nextPage(AmendAnyDiscrepanciesPage("submissionId"), NormalMode, userAnswers) shouldBe
//            amendRoute.AmendCYASubmissionController.onPageLoad("submissionId")
//        }
      "to DiscrepancyConsignmentPage when true" in {
        val userAnswers = emptyUserAnswers.set(AmendAnyDiscrepanciesPage("submissionId"), true).success.value
        navigator.nextPage(AmendAnyDiscrepanciesPage("submissionId"), NormalMode, userAnswers) shouldBe
          unhappyRoute.DiscrepancyConsignmentController.onPageLoad(NormalMode)
      }
      "to JourneyRecovery TEMPORARY when None" in {
        val userAnswers = emptyUserAnswers
        navigator.nextPage(AmendPartOfConsolidationPage("submissionId"), NormalMode, userAnswers) shouldBe
          problemRoute.JourneyRecoveryController.onPageLoad()
      }
    }
  }
}
