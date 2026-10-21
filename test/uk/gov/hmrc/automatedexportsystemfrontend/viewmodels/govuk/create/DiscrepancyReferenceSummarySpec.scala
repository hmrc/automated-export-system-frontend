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

package uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.govuk.create

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import play.api.i18n.Messages
import play.api.test.Helpers
import uk.gov.hmrc.automatedexportsystemfrontend.models.{CheckMode, NormalMode, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.pages.create.DiscrepancyReferencePage
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.checkAnswers.Create.DiscrepancyReferenceSummary
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.govuk.all.*

class DiscrepancyReferenceSummarySpec extends AnyFreeSpec with Matchers {

  private implicit val messages: Messages = Helpers.stubMessages()

  "row" - {
    "when answered, return the summary row in NormalMode" in {
      val userAnswers = UserAnswers("id")
        .set(DiscrepancyReferencePage(1), "declarationGoodsReference")
        .get

      DiscrepancyReferenceSummary.row(1, userAnswers, NormalMode) shouldBe Some(
        SummaryListRowViewModel(
          key = "discrepancyReference.checkYourAnswersLabel",
          value = ValueViewModel("declarationGoodsReference"),
          actions = Seq(
            ActionItemViewModel(
              "site.change",
              uk.gov.hmrc.automatedexportsystemfrontend.controllers.create.routes.DiscrepancyReferenceController.onPageLoad(1, NormalMode).url
            )
              .withVisuallyHiddenText("discrepancyReference.change.hidden")
          )
        )
      )
    }

    "when answered, return the summary row in CheckMode" in {
      val userAnswers = UserAnswers("id")
        .set(DiscrepancyReferencePage(1), "declarationGoodsReference")
        .get

      DiscrepancyReferenceSummary.row(1, userAnswers, CheckMode) shouldBe Some(
        SummaryListRowViewModel(
          key = "discrepancyReference.checkYourAnswersLabel",
          value = ValueViewModel("declarationGoodsReference"),
          actions = Seq(
            ActionItemViewModel(
              "site.change",
              uk.gov.hmrc.automatedexportsystemfrontend.controllers.create.routes.DiscrepancyReferenceController.onPageLoad(1, CheckMode).url
            )
              .withVisuallyHiddenText("discrepancyReference.change.hidden")
          )
        )
      )
    }

    "when answer unavailable, return empty" in {
      val userAnswers = UserAnswers("id")
      DiscrepancyReferenceSummary.row(1, userAnswers, NormalMode) shouldBe None
    }
  }
}
