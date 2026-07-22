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

package navigation

import base.SpecBase
import models.{NormalMode, OfficeOfExit}
import pages.OfficeOfExitPage
import pages.happyPath.{AnyDiscrepanciesPage, EnterDucrPage, EnterMrnPage, IsSplitExitPage, PartOfConsolidationPage}

class HappyPathNavigatorSpec extends SpecBase {

  val navigator = new HappyPathNavigator

  "ThirdPartyNavigator" - {

    "in Normal mode" - {

      "navigate from EnterMrnPage" - {
        "to EnterDucrPage" in {
          val userAnswers = emptyUserAnswers.set(EnterMrnPage, "TEST").success.value
          navigator.nextPage(EnterMrnPage, NormalMode, userAnswers) mustBe
            controllers.happyPath.routes.EnterDucrController.onPageLoad(NormalMode)
        }
      }

      "navigate from EnterDucrPage" - {
        "to OfficeOfExitPage" in {
          val userAnswers = emptyUserAnswers.set(EnterDucrPage, "TEST").success.value
          navigator.nextPage(EnterDucrPage, NormalMode, userAnswers) mustBe
            controllers.happyPath.routes.OfficeOfExitController.onPageLoad(NormalMode)
        }
      }

      "navigate from OfficeOfExitPage" - {
        "to AnyDiscrepancies" in {
          val userAnswers = emptyUserAnswers.set(OfficeOfExitPage, OfficeOfExit.Belfast).success.value
          navigator.nextPage(OfficeOfExitPage, NormalMode, userAnswers) mustBe
            controllers.happyPath.routes.PartOfConsolidationController.onPageLoad(NormalMode)
        }
      }

      "navigate from PartOfConsolidationPage" - {
        "to IsSplitExitPage when true" in {
          val userAnswers = emptyUserAnswers.set(PartOfConsolidationPage, true).success.value
          navigator.nextPage(PartOfConsolidationPage, NormalMode, userAnswers) mustBe
            controllers.happyPath.routes.IsSplitExitController.onPageLoad(NormalMode)
        }
        "to JourneyRecovery TEMPORARY when false" in {
          val userAnswers = emptyUserAnswers.set(PartOfConsolidationPage, false).success.value
          navigator.nextPage(PartOfConsolidationPage, NormalMode, userAnswers) mustBe
            controllers.problem.routes.JourneyRecoveryController.onPageLoad()
        }
      }

      "navigate from IsSplitExitPage" - {
        "to AnyDiscrepanciesPage when true" in {
          val userAnswers = emptyUserAnswers.set(IsSplitExitPage, true).success.value
          navigator.nextPage(IsSplitExitPage, NormalMode, userAnswers) mustBe
            controllers.happyPath.routes.AnyDiscrepanciesController.onPageLoad(NormalMode)
        }
        "to JourneyRecovery TEMPORARY when false" in {
          val userAnswers = emptyUserAnswers.set(PartOfConsolidationPage, false).success.value
          navigator.nextPage(PartOfConsolidationPage, NormalMode, userAnswers) mustBe
            controllers.problem.routes.JourneyRecoveryController.onPageLoad()
        }
      }

      "navigate from AnyDiscrepanciesPage" - {
        "to CYASubmissionController when true" in {
          val userAnswers = emptyUserAnswers.set(AnyDiscrepanciesPage, true).success.value
          navigator.nextPage(AnyDiscrepanciesPage, NormalMode, userAnswers) mustBe
            controllers.routes.CYASubmissionController.onPageLoad()
        }
        "to JourneyRecovery TEMPORARY when false" in {
          val userAnswers = emptyUserAnswers.set(PartOfConsolidationPage, false).success.value
          navigator.nextPage(PartOfConsolidationPage, NormalMode, userAnswers) mustBe
            controllers.problem.routes.JourneyRecoveryController.onPageLoad()
        }
      }
    }
  }
}