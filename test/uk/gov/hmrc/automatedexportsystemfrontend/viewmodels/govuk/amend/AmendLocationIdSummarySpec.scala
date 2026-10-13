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
import uk.gov.hmrc.automatedexportsystemfrontend.models.{CheckMode, LocationDetails, LocationQualifier, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.pages.amend.AmendLocationIdPage
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.checkAnswers.Amend.AmendLocationIdSummary
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.govuk.all.*
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent

class AmendLocationIdSummarySpec extends AnyFreeSpec with Matchers with Generators {

  private implicit val messages: Messages = Helpers.stubMessages()

  "row" - {
    "when answered, return the summary row with change links" in {
      val qualifier = "locationQualifier"
      val unlocode = "unlocode"
      val locationAdditionalIdentifier = "locationAdditionalIdentifier"
      val authorisationReferenceNumber = "authorisationReferenceNumber"

      AmendLocationIdSummary.qualifierRow(qualifier, "submissionId", true) shouldBe Some(
        SummaryListRowViewModel(
          key = "locationId.identificationType.checkYourAnswersLabel",
          value = ValueViewModel(HtmlContent("locationQualifier")),
          actions = Seq(
            ActionItemViewModel(
              "site.change",
              uk.gov.hmrc.automatedexportsystemfrontend.controllers.amend.routes.AmendLocationIdController.onPageLoad(CheckMode, "submissionId").url
            )
              .withVisuallyHiddenText("locationId.identificationType.change.hidden")
          )
        )
      )
      AmendLocationIdSummary.unloRow(unlocode, "submissionId", true) shouldBe Some(
        SummaryListRowViewModel(
          key = "locationId.unlocode.checkYourAnswersLabel",
          value = ValueViewModel(HtmlContent("unlocode")),
          actions = Seq(
            ActionItemViewModel(
              "site.change",
              uk.gov.hmrc.automatedexportsystemfrontend.controllers.amend.routes.AmendLocationIdController.onPageLoad(CheckMode, "submissionId").url
            )
              .withVisuallyHiddenText("locationId.unlocode.change.hidden")
          )
        )
      )
      AmendLocationIdSummary.additionalIdRow(locationAdditionalIdentifier, "submissionId", true) shouldBe Some(
        SummaryListRowViewModel(
          key = "locationId.locationAdditionalIdentifier.checkYourAnswersLabel",
          value = ValueViewModel(HtmlContent("locationAdditionalIdentifier")),
          actions = Seq(
            ActionItemViewModel(
              "site.change",
              uk.gov.hmrc.automatedexportsystemfrontend.controllers.amend.routes.AmendLocationIdController.onPageLoad(CheckMode, "submissionId").url
            )
              .withVisuallyHiddenText("locationId.locationAdditionalIdentifier.change.hidden")
          )
        )
      )
      AmendLocationIdSummary.authNumberRow(authorisationReferenceNumber, "submissionId", true) shouldBe Some(
        SummaryListRowViewModel(
          key = "locationId.authorisationReferenceNumber.checkYourAnswersLabel",
          value = ValueViewModel(HtmlContent("authorisationReferenceNumber")),
          actions = Seq(
            ActionItemViewModel(
              "site.change",
              uk.gov.hmrc.automatedexportsystemfrontend.controllers.amend.routes.AmendLocationIdController.onPageLoad(CheckMode, "submissionId").url
            )
              .withVisuallyHiddenText("locationId.authorisationReferenceNumber.change.hidden")
          )
        )
      )
    }

    "when answered, return the summary row without change links" in {
      val qualifier = "locationQualifier"
      val unlocode = "unlocode"
      val locationAdditionalIdentifier = "locationAdditionalIdentifier"
      val authorisationReferenceNumber = "authorisationReferenceNumber"

      AmendLocationIdSummary.qualifierRow(qualifier, "submissionId", false) shouldBe Some(
        SummaryListRowViewModel(
          key = "locationId.identificationType.checkYourAnswersLabel",
          value = ValueViewModel(HtmlContent("locationQualifier")),
          actions = Seq.empty
        )
      )
      AmendLocationIdSummary.unloRow(unlocode, "submissionId", false) shouldBe Some(
        SummaryListRowViewModel(
          key = "locationId.unlocode.checkYourAnswersLabel",
          value = ValueViewModel(HtmlContent("unlocode")),
          actions = Seq.empty
        )
      )
      AmendLocationIdSummary.additionalIdRow(locationAdditionalIdentifier, "submissionId", false) shouldBe Some(
        SummaryListRowViewModel(
          key = "locationId.locationAdditionalIdentifier.checkYourAnswersLabel",
          value = ValueViewModel(HtmlContent("locationAdditionalIdentifier")),
          actions = Seq.empty
        )
      )
      AmendLocationIdSummary.authNumberRow(authorisationReferenceNumber, "submissionId", false) shouldBe Some(
        SummaryListRowViewModel(
          key = "locationId.authorisationReferenceNumber.checkYourAnswersLabel",
          value = ValueViewModel(HtmlContent("authorisationReferenceNumber")),
          actions = Seq.empty
        )
      )
    }

  }
}
