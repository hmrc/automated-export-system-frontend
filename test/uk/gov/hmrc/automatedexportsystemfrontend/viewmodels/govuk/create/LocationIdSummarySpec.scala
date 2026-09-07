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
import uk.gov.hmrc.automatedexportsystemfrontend.models.{CheckMode, LocationDetails, LocationQualifier, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.pages.create.LocationIdPage
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.checkAnswers.Create.LocationIdSummary
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.govuk.all.*
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent

class LocationIdSummarySpec extends AnyFreeSpec with Matchers with Generators {

  private implicit val messages: Messages = Helpers.stubMessages()

  "row" - {
    "when answered, return the summary row" in {
      val locationDetails = LocationDetails(LocationQualifier.UnLocode, "unlocode", "locationAdditionalIdentifier", "authorisationReferenceNumber")
      val userAnswers = UserAnswers("id")
        .set(LocationIdPage, locationDetails)
        .get

      LocationIdSummary.row(userAnswers) shouldBe Some(
        Seq(
          SummaryListRowViewModel(
            key = "locationId.identificationType.checkYourAnswersLabel",
            value = ValueViewModel(HtmlContent("locationId.unlocode")),
            actions = Seq(
              ActionItemViewModel(
                "site.change",
                uk.gov.hmrc.automatedexportsystemfrontend.controllers.create.routes.LocationIdController.onPageLoad(CheckMode).url
              )
                .withVisuallyHiddenText("locationId.identificationType.change.hidden")
            )
          ),
          SummaryListRowViewModel(
            key = "locationId.unlocode.checkYourAnswersLabel",
            value = ValueViewModel(HtmlContent("unlocode")),
            actions = Seq(
              ActionItemViewModel(
                "site.change",
                uk.gov.hmrc.automatedexportsystemfrontend.controllers.create.routes.LocationIdController.onPageLoad(CheckMode).url
              )
                .withVisuallyHiddenText("locationId.unlocode.change.hidden")
            )
          ),
          SummaryListRowViewModel(
            key = "locationId.locationAdditionalIdentifier.checkYourAnswersLabel",
            value = ValueViewModel(HtmlContent("locationAdditionalIdentifier")),
            actions = Seq(
              ActionItemViewModel(
                "site.change",
                uk.gov.hmrc.automatedexportsystemfrontend.controllers.create.routes.LocationIdController.onPageLoad(CheckMode).url
              )
                .withVisuallyHiddenText("locationId.locationAdditionalIdentifier.change.hidden")
            )
          ),
          SummaryListRowViewModel(
            key = "locationId.authorisationReferenceNumber.checkYourAnswersLabel",
            value = ValueViewModel(HtmlContent("authorisationReferenceNumber")),
            actions = Seq(
              ActionItemViewModel(
                "site.change",
                uk.gov.hmrc.automatedexportsystemfrontend.controllers.create.routes.LocationIdController.onPageLoad(CheckMode).url
              )
                .withVisuallyHiddenText("locationId.authorisationReferenceNumber.change.hidden")
            )
          )
        )
      )
    }

    "when answer unavailable, return empty" in {
      val userAnswers = UserAnswers("id")
      LocationIdSummary.row(userAnswers) shouldBe None
    }
  }
}
