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

  def typeOfPackagesRow(answers: UserAnswers)(submissionId: String)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(AmendDiscrepancyPackingPage(submissionId)).map { answer =>
      buildPackagingCode(answer.packagingCode, submissionId, withAmendLink = true)
    }

  def typeOfPackagesRow(answerFromXml: String, submissionId: String, withAmendLink: Boolean)(implicit messages: Messages): Option[SummaryListRow] =
    Some(buildPackagingCode(answerFromXml, submissionId, withAmendLink))

  def numberOfPackagesRow(answers: UserAnswers)(submissionId: String)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(AmendDiscrepancyPackingPage(submissionId)).map { answer =>
      buildNumberOfPackages(answer.numberOfPackages, submissionId, withAmendLink = true)
    }

  def numberOfPackagesRow(answerFromXml: Int, submissionId: String, withAmendLink: Boolean)(implicit messages: Messages): Option[SummaryListRow] =
    Some(buildNumberOfPackages(answerFromXml, submissionId, withAmendLink))

  def shippingMarksRow(answers: UserAnswers)(submissionId: String)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(AmendDiscrepancyPackingPage(submissionId)).map { answer =>
      buildShippingMarks(answer.shippingMarks, submissionId, withAmendLink = true)
    }

  def shippingMarksRow(answerFromXml: String, submissionId: String, withAmendLink: Boolean)(implicit messages: Messages): Option[SummaryListRow] =
    Some(buildShippingMarks(answerFromXml, submissionId, withAmendLink))

  private def buildPackagingCode(value: String, submissionId: String, withAmendLink: Boolean)(implicit messages: Messages): SummaryListRow =
    SummaryListRowViewModel(
      key = "discrepancyPacking.packagingCode.checkYourAnswersLabel",
      value = ValueViewModel(HtmlContent(HtmlFormat.escape(value))),
      actions =
        if (withAmendLink)
          Seq(
            ActionItemViewModel("site.change", amendRoute.AmendDiscrepancyPackingController.onPageLoad(CheckMode, submissionId).url)
              .withVisuallyHiddenText(messages("discrepancyPacking.packagingCode.change.hidden"))
          )
        else Seq.empty
    )

  private def buildNumberOfPackages(value: Int, submissionId: String, withAmendLink: Boolean)(implicit messages: Messages): SummaryListRow =
    SummaryListRowViewModel(
      key = "discrepancyPacking.numberOfPackages.checkYourAnswersLabel",
      value = ValueViewModel(HtmlContent(HtmlFormat.escape(value.toString))),
      actions =
        if (withAmendLink)
          Seq(
            ActionItemViewModel("site.change", amendRoute.AmendDiscrepancyPackingController.onPageLoad(CheckMode, submissionId).url)
              .withVisuallyHiddenText(messages("discrepancyPacking.numberOfPackages.change.hidden"))
          )
        else Seq.empty
    )

  private def buildShippingMarks(value: String, submissionId: String, withAmendLink: Boolean)(implicit messages: Messages): SummaryListRow =
    SummaryListRowViewModel(
      key = "discrepancyPacking.shippingMarks.checkYourAnswersLabel",
      value = ValueViewModel(HtmlContent(HtmlFormat.escape(value))),
      actions =
        if (withAmendLink)
          Seq(
            ActionItemViewModel("site.change", amendRoute.AmendDiscrepancyPackingController.onPageLoad(CheckMode, submissionId).url)
              .withVisuallyHiddenText(messages("discrepancyPacking.shippingMarks.change.hidden"))
          )
        else Seq.empty
    )
}
