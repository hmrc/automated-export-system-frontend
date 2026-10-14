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
import uk.gov.hmrc.automatedexportsystemfrontend.pages.amend.AmendDiscrepancyTransportPage
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.govuk.summarylist.*
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.implicits.*
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow

object AmendDiscrepancyTransportSummary {

  def rows(answers: UserAnswers)(submissionId: String)(implicit messages: Messages): Seq[Option[SummaryListRow]] = {
    val maybeAnswer = answers.get(AmendDiscrepancyTransportPage(submissionId))

    Seq(
      maybeAnswer.flatMap(_.containerId.map { containerId =>
        buildContainerId(containerId, submissionId, withAmendLink = true)
      }),
      maybeAnswer.flatMap(_.numberOfSeals.map { numberOfSeals =>
        buildNumberOfSeals(numberOfSeals, submissionId, withAmendLink = true)
      })
    )
  }

  def containerIdRow(answerFromXml: String, submissionId: String, withAmendLink: Boolean)(implicit messages: Messages): Option[SummaryListRow] =
    Some(buildContainerId(answerFromXml, submissionId, withAmendLink))

  private def buildContainerId(value: String, submissionId: String, withAmendLink: Boolean)(implicit messages: Messages): SummaryListRow =
    SummaryListRowViewModel(
      key = "discrepancyTransport.containerId.checkYourAnswersLabel",
      value = ValueViewModel(HtmlContent(HtmlFormat.escape(value))),
      actions =
        if (withAmendLink)
          Seq(
            ActionItemViewModel("site.change", amendRoute.AmendDiscrepancyTransportController.onPageLoad(CheckMode, submissionId).url)
              .withVisuallyHiddenText(messages("discrepancyTransport.containerId.change.hidden"))
          )
        else Seq.empty
    )

  def numberOfSealsRow(answerFromXml: Int, submissionId: String, withAmendLink: Boolean)(implicit messages: Messages): Option[SummaryListRow] =
    Some(buildNumberOfSeals(answerFromXml, submissionId, withAmendLink))

  private def buildNumberOfSeals(value: Int, submissionId: String, withAmendLink: Boolean)(implicit messages: Messages): SummaryListRow =
    SummaryListRowViewModel(
      key = "discrepancyTransport.numberOfSeals.checkYourAnswersLabel",
      value = ValueViewModel(HtmlContent(value.toString)),
      actions =
        if (withAmendLink)
          Seq(
            ActionItemViewModel("site.change", amendRoute.AmendDiscrepancyTransportController.onPageLoad(CheckMode, submissionId).url)
              .withVisuallyHiddenText(messages("discrepancyTransport.numberOfSeals.change.hidden"))
          )
        else Seq.empty
    )
}
