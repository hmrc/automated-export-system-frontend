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
import uk.gov.hmrc.automatedexportsystemfrontend.models.{CheckMode, PackingDetails, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.pages.amend.AmendDiscrepancyPackingPage
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.checkAnswers.Amend.AmendDiscrepancyPackingSummary
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.govuk.all.*
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent

class AmendDiscrepancyPackingSummarySpec extends AnyFreeSpec with Matchers with Generators {

  private implicit val messages: Messages = Helpers.stubMessages()

  "rows" - {
    "when answered, return the summary rows with change lnik" in {
      val packagingCode = "BX"
      val numberOfPackages = "2"
      val shippingMarks = "MARKS123"

      AmendDiscrepancyPackingSummary.typeOfPackagesRow(packagingCode, "submissionId", true) shouldBe Some(
          SummaryListRowViewModel(
            key = "discrepancyPacking.packagingCode.checkYourAnswersLabel",
            value = ValueViewModel(HtmlContent("BX")),
            actions = Seq(
              ActionItemViewModel(
                "site.change",
                uk.gov.hmrc.automatedexportsystemfrontend.controllers.amend.routes.AmendDiscrepancyPackingController.onPageLoad(CheckMode, "submissionId").url
              )
                .withVisuallyHiddenText("discrepancyPacking.packagingCode.change.hidden")
            )
          )
      )

      AmendDiscrepancyPackingSummary.numberOfPackagesRow(numberOfPackages, "submissionId", true) shouldBe Some(
        SummaryListRowViewModel(
            key = "discrepancyPacking.numberOfPackages.checkYourAnswersLabel",
            value = ValueViewModel(HtmlContent("2")),
            actions = Seq(
              ActionItemViewModel(
                "site.change",
                uk.gov.hmrc.automatedexportsystemfrontend.controllers.amend.routes.AmendDiscrepancyPackingController.onPageLoad(CheckMode, "submissionId").url
              )
                .withVisuallyHiddenText("discrepancyPacking.numberOfPackages.change.hidden")
            )
          )
      )
      AmendDiscrepancyPackingSummary.shippingMarksRow(shippingMarks, "submissionId", true) shouldBe Some(
          SummaryListRowViewModel(
            key = "discrepancyPacking.shippingMarks.checkYourAnswersLabel",
            value = ValueViewModel(HtmlContent("MARKS123")),
            actions = Seq(
              ActionItemViewModel(
                "site.change",
                uk.gov.hmrc.automatedexportsystemfrontend.controllers.amend.routes.AmendDiscrepancyPackingController.onPageLoad(CheckMode, "submissionId").url
              )
                .withVisuallyHiddenText("discrepancyPacking.shippingMarks.change.hidden")
            )
          )
        )
    }

    "when answered, return the summary rows without change links" in {
      val packagingCode = "BX"
      val numberOfPackages = "2"
      val shippingMarks = "MARKS123"

      AmendDiscrepancyPackingSummary.typeOfPackagesRow(packagingCode, "submissionId", false) shouldBe Some(
        SummaryListRowViewModel(
          key = "discrepancyPacking.packagingCode.checkYourAnswersLabel",
          value = ValueViewModel(HtmlContent("BX")),
          actions =  Seq.empty
        )
      )

      AmendDiscrepancyPackingSummary.numberOfPackagesRow(numberOfPackages, "submissionId", false) shouldBe Some(
        SummaryListRowViewModel(
          key = "discrepancyPacking.numberOfPackages.checkYourAnswersLabel",
          value = ValueViewModel(HtmlContent("2")),
          actions = Seq.empty
        )
      )
      AmendDiscrepancyPackingSummary.shippingMarksRow(shippingMarks, "submissionId", false) shouldBe Some(
        SummaryListRowViewModel(
          key = "discrepancyPacking.shippingMarks.checkYourAnswersLabel",
          value = ValueViewModel(HtmlContent("MARKS123")),
          actions = Seq.empty
        )
      )
    }
  }
}
