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

import generators.Generators
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import play.api.i18n.Messages
import play.api.test.Helpers
import uk.gov.hmrc.automatedexportsystemfrontend.models.{CheckMode, DocumentDetails, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.pages.create.DiscrepancyTransportDocPage
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.checkAnswers.Create.DiscrepancyTransportDocSummary
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.govuk.all.*
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent

class DiscrepancyTransportDocSummarySpec extends AnyFreeSpec with Matchers with Generators {

  private implicit val messages: Messages = Helpers.stubMessages()

  "row" - {
    "when answered, return the summary row" in {
      val documentDetails = DocumentDetails("documentType", "referenceNumber")
      val userAnswers = UserAnswers("id")
        .set(DiscrepancyTransportDocPage, documentDetails)
        .get

      DiscrepancyTransportDocSummary.row(userAnswers) shouldBe Some(
        SummaryListRowViewModel(
          key = "discrepancyTransportDoc.checkYourAnswersLabel",
          value = ValueViewModel(HtmlContent("documentType<br/>referenceNumber")),
          actions = Seq(
            ActionItemViewModel(
              "site.change",
              uk.gov.hmrc.automatedexportsystemfrontend.controllers.create.routes.DiscrepancyTransportDocController.onPageLoad(CheckMode).url
            )
              .withVisuallyHiddenText("discrepancyTransportDoc.change.hidden")
          )
        )
      )
    }

    "when answer unavailable, return empty" in {
      val userAnswers = UserAnswers("id")
      DiscrepancyTransportDocSummary.row(userAnswers) shouldBe None
    }
  }
}
