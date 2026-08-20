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

package uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.govuk.discrepancies

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import play.api.i18n.Messages
import play.api.test.Helpers
import uk.gov.hmrc.automatedexportsystemfrontend.models.{CheckMode, ModeOfTransportAtBorder, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.pages.discrepancies.DiscrepancyConsignmentPage
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.checkAnswers.discrepancies.DiscrepancyConsignmentSummary
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.govuk.all.*
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent

class DiscrepancyConsignmentSummarySpec extends AnyFreeSpec with Matchers {

  private implicit val messages: Messages = Helpers.stubMessages()

  "row" - {
    "when answered, return the summary row" in {
      val userAnswers = UserAnswers("id")
        .set(DiscrepancyConsignmentPage, ModeOfTransportAtBorder.Sea)
        .get

      DiscrepancyConsignmentSummary.row(userAnswers) shouldBe Some(
        SummaryListRowViewModel(
          key = "discrepancyConsignment.checkYourAnswersLabel",
          value = ValueViewModel(HtmlContent("discrepancyConsignment.sea")),
          actions = Seq(
            ActionItemViewModel(
              "site.change",
              uk.gov.hmrc.automatedexportsystemfrontend.controllers.discrepancies.routes.DiscrepancyConsignmentController.onPageLoad(CheckMode).url
            )
              .withVisuallyHiddenText("discrepancyConsignment.change.hidden")
          )
        )
      )
    }

    "when answer unavailable, return empty" in {
      val userAnswers = UserAnswers("id")
      DiscrepancyConsignmentSummary.row(userAnswers) shouldBe None
    }
  }
}
