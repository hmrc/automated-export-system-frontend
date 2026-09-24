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

package uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.govuk.amend

import generators.Generators
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import play.api.i18n.Messages
import play.api.test.Helpers
import uk.gov.hmrc.automatedexportsystemfrontend.models.{CheckMode, ContainerDetails, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.pages.amend.AmendDiscrepancyTransportPage
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.checkAnswers.Amend.AmendDiscrepancyTransportSummary
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.govuk.all.*
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent

class AmendDiscrepancyTransportSummarySpec extends AnyFreeSpec with Matchers with Generators {

  private implicit val messages: Messages = Helpers.stubMessages()

  "rows" - {
    "when answered, return the summary rows with change links" in {
      val containerId = "containerId"
      val numberOfSeals = 99

      AmendDiscrepancyTransportSummary.containerIdRow(containerId, "submissionId", true) shouldBe Some(
          SummaryListRowViewModel(
            key = "discrepancyTransport.containerId.checkYourAnswersLabel",
            value = ValueViewModel(HtmlContent("containerId")),
            actions = Seq(
              ActionItemViewModel(
                "site.change",
                uk.gov.hmrc.automatedexportsystemfrontend.controllers.amend.routes.AmendDiscrepancyTransportController.onPageLoad(CheckMode, "submissionId").url
              )
                .withVisuallyHiddenText("discrepancyTransport.containerId.change.hidden")
            )
          )
      )

      AmendDiscrepancyTransportSummary.numberOfSealsRow(numberOfSeals, "submissionId", true) shouldBe Some(
        SummaryListRowViewModel(
            key = "discrepancyTransport.numberOfSeals.checkYourAnswersLabel",
            value = ValueViewModel(HtmlContent("99")),
            actions = Seq(
              ActionItemViewModel(
                "site.change",
                uk.gov.hmrc.automatedexportsystemfrontend.controllers.amend.routes.AmendDiscrepancyTransportController.onPageLoad(CheckMode, "submissionId").url
              )
                .withVisuallyHiddenText("discrepancyTransport.numberOfSeals.change.hidden")
            )
          )
        )
    }

    "when answered, return the summary rows without change links" in {
      val containerId = "containerId"
      val numberOfSeals = 99

      AmendDiscrepancyTransportSummary.containerIdRow(containerId, "submissionId", false) shouldBe Some(
        SummaryListRowViewModel(
          key = "discrepancyTransport.containerId.checkYourAnswersLabel",
          value = ValueViewModel(HtmlContent("containerId")),
          actions = Seq.empty
        )
      )

      AmendDiscrepancyTransportSummary.numberOfSealsRow(numberOfSeals, "submissionId", false) shouldBe Some(
        SummaryListRowViewModel(
          key = "discrepancyTransport.numberOfSeals.checkYourAnswersLabel",
          value = ValueViewModel(HtmlContent("99")),
          actions = Seq.empty
        )
      )
    }

  }
}
