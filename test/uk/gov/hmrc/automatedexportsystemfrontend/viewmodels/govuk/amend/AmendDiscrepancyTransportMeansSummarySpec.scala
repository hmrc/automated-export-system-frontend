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
import uk.gov.hmrc.automatedexportsystemfrontend.models.{CheckMode, TransportAcrossBorderDetails, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.pages.amend.AmendDiscrepancyTransportMeansPage
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.checkAnswers.Amend.AmendDiscrepancyTransportMeansSummary
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.govuk.all.*
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent

class AmendDiscrepancyTransportMeansSummarySpec extends AnyFreeSpec with Matchers with Generators {

  private implicit val messages: Messages = Helpers.stubMessages()

  "rows" - {
    "when answered, return the summary rows with change links" in {
      val transportType = "transportType"
      val transportIdNumber = "transportIdNumber"
      val countryOfRegistration = "countryOfRegistration"

      AmendDiscrepancyTransportMeansSummary.transportTypeRow(transportType, "submissionId", true) shouldBe Some(
          SummaryListRowViewModel(
            key = "discrepancyTransportMeans.transportType.checkYourAnswersLabel",
            value = ValueViewModel(HtmlContent("transportType")),
            actions = Seq(
              ActionItemViewModel(
                "site.change",
                uk.gov.hmrc.automatedexportsystemfrontend.controllers.amend.routes.AmendDiscrepancyTransportMeansController.onPageLoad(CheckMode, "submissionId").url
              )
                .withVisuallyHiddenText("discrepancyTransportMeans.transportType.change.hidden")
            )
          )
      )
      AmendDiscrepancyTransportMeansSummary.transportIdRow(transportIdNumber, "submissionId", true) shouldBe Some(
          SummaryListRowViewModel(
            key = "discrepancyTransportMeans.transportIdNumber.checkYourAnswersLabel",
            value = ValueViewModel(HtmlContent("transportIdNumber")),
            actions = Seq(
              ActionItemViewModel(
                "site.change",
                uk.gov.hmrc.automatedexportsystemfrontend.controllers.amend.routes.AmendDiscrepancyTransportMeansController.onPageLoad(CheckMode, "submissionId").url
              )
                .withVisuallyHiddenText("discrepancyTransportMeans.transportIdNumber.change.hidden")
            )
          )
      )

      AmendDiscrepancyTransportMeansSummary.countryOfRegistrationRow(countryOfRegistration, "submissionId", true) shouldBe Some(
        SummaryListRowViewModel(
            key = "discrepancyTransportMeans.countryOfRegistration.checkYourAnswersLabel",
            value = ValueViewModel(HtmlContent("countryOfRegistration")),
            actions = Seq(
              ActionItemViewModel(
                "site.change",
                uk.gov.hmrc.automatedexportsystemfrontend.controllers.amend.routes.AmendDiscrepancyTransportMeansController.onPageLoad(CheckMode, "submissionId").url
              )
                .withVisuallyHiddenText("discrepancyTransportMeans.countryOfRegistration.change.hidden")
            )
          )
        )
    }

    "when answered, return the summary rows without change links" in {
      val transportType = "transportType"
      val transportIdNumber = "transportIdNumber"
      val countryOfRegistration = "countryOfRegistration"

      AmendDiscrepancyTransportMeansSummary.transportTypeRow(transportType, "submissionId", false) shouldBe Some(
        SummaryListRowViewModel(
          key = "discrepancyTransportMeans.transportType.checkYourAnswersLabel",
          value = ValueViewModel(HtmlContent("transportType")),
          actions = Seq.empty
        )
      )
      AmendDiscrepancyTransportMeansSummary.transportIdRow(transportIdNumber, "submissionId", false) shouldBe Some(
        SummaryListRowViewModel(
          key = "discrepancyTransportMeans.transportIdNumber.checkYourAnswersLabel",
          value = ValueViewModel(HtmlContent("transportIdNumber")),
          actions = Seq.empty
        )
      )

      AmendDiscrepancyTransportMeansSummary.countryOfRegistrationRow(countryOfRegistration, "submissionId", false) shouldBe Some(
        SummaryListRowViewModel(
          key = "discrepancyTransportMeans.countryOfRegistration.checkYourAnswersLabel",
          value = ValueViewModel(HtmlContent("countryOfRegistration")),
          actions = Seq.empty
        )
      )
    }

  }
}
