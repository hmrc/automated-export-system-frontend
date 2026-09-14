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

///*
// * Copyright 2026 HM Revenue & Customs
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */

package uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.checkAnswers.Amend

import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.amend.routes as amendRoute
import uk.gov.hmrc.automatedexportsystemfrontend.models.{CheckMode, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.pages.amend.AmendLocationIdPage
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.govuk.summarylist.*
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.implicits.*
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow

object AmendLocationIdSummary {

  def qualifierRow(answerFromXml: String, submissionId: String, withAmendLink: Boolean)(implicit messages: Messages): Option[SummaryListRow] =
    Some(
      SummaryListRowViewModel(
        key = "locationId.identificationType.checkYourAnswersLabel",
        value = ValueViewModel(HtmlContent(answerFromXml)),
        actions = if (withAmendLink) {
          Seq(
            ActionItemViewModel("site.change", amendRoute.AmendLocationIdController.onPageLoad(CheckMode, submissionId).url)
              .withVisuallyHiddenText(messages("locationId.identificationType.change.hidden"))
          )
        } else {
          Seq.empty
        }
      )
    )

  def authNumberRow(answerFromXml: String, submissionId: String, withAmendLink: Boolean)(implicit messages: Messages): Option[SummaryListRow] =
    Some(
      SummaryListRowViewModel(
        key = "locationId.identificationType.checkYourAnswersLabel",
        value = ValueViewModel(HtmlContent(answerFromXml)),
        actions = if (withAmendLink) {
          Seq(
            ActionItemViewModel("site.change", amendRoute.AmendLocationIdController.onPageLoad(CheckMode, submissionId).url)
              .withVisuallyHiddenText(messages("locationId.unlocode.change.hidden"))
          )
        } else {
          Seq.empty
        }
      )
    )

  def additionalIdRow(answerFromXml: String, submissionId: String, withAmendLink: Boolean)(implicit messages: Messages): Option[SummaryListRow] =
    Some(
      SummaryListRowViewModel(
        key = "locationId.identificationType.checkYourAnswersLabel",
        value = ValueViewModel(HtmlContent(answerFromXml)),
        actions = if (withAmendLink) {
          Seq(
            ActionItemViewModel("site.change", amendRoute.AmendLocationIdController.onPageLoad(CheckMode, submissionId).url)
              .withVisuallyHiddenText(messages("locationId.unlocode.change.hidden"))
          )
        } else {
          Seq.empty
        }
      )
    )

  def unloRow(answerFromXml: String, submissionId: String, withAmendLink: Boolean)(implicit messages: Messages): Option[SummaryListRow] =
    Some(
      SummaryListRowViewModel(
        key = "locationId.identificationType.checkYourAnswersLabel",
        value = ValueViewModel(HtmlContent(answerFromXml)),
        actions = if (withAmendLink) {
          Seq(
            ActionItemViewModel("site.change", amendRoute.AmendLocationIdController.onPageLoad(CheckMode, submissionId).url)
              .withVisuallyHiddenText(messages("locationId.unlocode.change.hidden"))
          )
        } else {
          Seq.empty
        }
      )
    )

//      val locationType = HtmlFormat.escape(messages(s"locationId.${answer.locationType.toString}"))
//      val unlocode = HtmlFormat.escape(answer.unlocode)
//      val locationAdditionalIdentifier = HtmlFormat.escape(answer.locationAdditionalIdentifier)
//      val authorisationReferenceNumber = HtmlFormat.escape(answer.authorisationReferenceNumber)
//
//      Seq(
//        SummaryListRowViewModel(
//          key = "locationId.identificationType.checkYourAnswersLabel",
//          value = ValueViewModel(HtmlContent(locationType)),
//          actions = Seq(
//            ActionItemViewModel("site.change", amendRoute.AmendLocationIdController.onPageLoad(CheckMode).url)
//              .withVisuallyHiddenText(messages("locationId.identificationType.change.hidden"))
//          )
//        ),
//        SummaryListRowViewModel(
//          key = "locationId.unlocode.checkYourAnswersLabel",
//          value = ValueViewModel(HtmlContent(unlocode)),
//          actions = Seq(
//            ActionItemViewModel("site.change", amendRoute.AmendLocationIdController.onPageLoad(CheckMode).url)
//              .withVisuallyHiddenText(messages("locationId.unlocode.change.hidden"))
//          )
//        ),
//        SummaryListRowViewModel(
//          key = "locationId.locationAdditionalIdentifier.checkYourAnswersLabel",
//          value = ValueViewModel(HtmlContent(locationAdditionalIdentifier)),
//          actions = Seq(
//            ActionItemViewModel("site.change", amendRoute.AmendLocationIdController.onPageLoad(CheckMode).url)
//              .withVisuallyHiddenText(messages("locationId.locationAdditionalIdentifier.change.hidden"))
//          )
//        ),
//        SummaryListRowViewModel(
//          key = "locationId.authorisationReferenceNumber.checkYourAnswersLabel",
//          value = ValueViewModel(HtmlContent(authorisationReferenceNumber)),
//          actions = Seq(
//            ActionItemViewModel("site.change", amendRoute.AmendLocationIdController.onPageLoad(CheckMode).url)
//              .withVisuallyHiddenText(messages("locationId.authorisationReferenceNumber.change.hidden"))
//          )
//        )
//      )
//    }
}
