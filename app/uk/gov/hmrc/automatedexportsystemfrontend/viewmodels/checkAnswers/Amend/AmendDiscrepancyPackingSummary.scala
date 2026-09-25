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

package uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.checkAnswers.Amend

import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.amend.routes as amendRoute
import uk.gov.hmrc.automatedexportsystemfrontend.models.{CheckMode, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.pages.amend.AmendDiscrepancyPackingPage
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.govuk.summarylist.*
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.implicits.*
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow

object AmendDiscrepancyPackingSummary {

  def typeOfPackagesRow(answerFromXml: String, submissionId: String, withAmendLink: Boolean)(implicit messages: Messages): Option[SummaryListRow] = {

    val packagingCode = HtmlFormat.escape(answerFromXml)

    Some(
      SummaryListRowViewModel(
        key = "discrepancyPacking.packagingCode.checkYourAnswersLabel",
        value = ValueViewModel(HtmlContent(packagingCode)),
        actions = if (withAmendLink) {
          Seq(
            ActionItemViewModel("site.change", amendRoute.AmendDiscrepancyPackingController.onPageLoad(CheckMode, submissionId).url)
              .withVisuallyHiddenText(messages("discrepancyPacking.packagingCode.change.hidden"))
          )
        } else {
          Seq.empty
        }
      )
    )
  }

  def numberOfPackagesRow(answerFromXml: String, submissionId: String, withAmendLink: Boolean)(
    implicit messages: Messages
  ): Option[SummaryListRow] = {

    val numberOfPackages = HtmlFormat.escape(answerFromXml.toString)

    Some(
      SummaryListRowViewModel(
        key = "discrepancyPacking.numberOfPackages.checkYourAnswersLabel",
        value = ValueViewModel(HtmlContent(numberOfPackages)),
        actions = if (withAmendLink) {
          Seq(
            ActionItemViewModel("site.change", amendRoute.AmendDiscrepancyPackingController.onPageLoad(CheckMode, submissionId).url)
              .withVisuallyHiddenText(messages("discrepancyPacking.numberOfPackages.change.hidden"))
          )
        } else {
          Seq.empty
        }
      )
    )
  }

  def shippingMarksRow(answerFromXml: String, submissionId: String, withAmendLink: Boolean)(implicit messages: Messages): Option[SummaryListRow] = {

    val shippingMarks = HtmlFormat.escape(answerFromXml)

    Some(
      SummaryListRowViewModel(
        key = "discrepancyPacking.shippingMarks.checkYourAnswersLabel",
        value = ValueViewModel(HtmlContent(shippingMarks)),
        actions = if (withAmendLink) {
          Seq(
            ActionItemViewModel("site.change", amendRoute.AmendDiscrepancyPackingController.onPageLoad(CheckMode, submissionId).url)
              .withVisuallyHiddenText(messages("discrepancyPacking.shippingMarks.change.hidden"))
          )
        } else {
          Seq.empty
        }
      )
    )
  }

}
