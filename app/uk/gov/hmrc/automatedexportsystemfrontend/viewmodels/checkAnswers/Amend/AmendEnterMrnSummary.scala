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

import uk.gov.hmrc.automatedexportsystemfrontend.controllers.amend.routes as amendRoute
import uk.gov.hmrc.automatedexportsystemfrontend.models.{CheckMode, UserAnswers}
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.automatedexportsystemfrontend.pages.amend.AmendEnterMrnPage
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.govuk.summarylist.*
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.implicits.*
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent

object AmendEnterMrnSummary {
  def row(answers: UserAnswers)(submissionId: String)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(AmendEnterMrnPage(submissionId)).map { answer =>
      build(answer, submissionId, withChangeLink = true)
    }

  def row(value: String, submissionId: String, withChangeLink: Boolean)(implicit messages: Messages): Option[SummaryListRow] =
    Some(build(value, submissionId, withChangeLink))

  def lockedRow(value: String, submissionId: String)(implicit messages: Messages): Option[SummaryListRow] =
    row(value, submissionId, withChangeLink = false).map { summaryRow =>
      summaryRow.copy(value =
        ValueViewModel(
          HtmlContent(HtmlFormat.raw(s"${HtmlFormat.escape(value)}<br>${HtmlFormat.escape(messages("amendCheckYourAnswers.cannotBeAmended"))}"))
        )
      )
    }

  private def build(value: String, submissionId: String, withChangeLink: Boolean)(implicit messages: Messages): SummaryListRow =
    SummaryListRowViewModel(
      key = "enterMrn.checkYourAnswersLabel",
      value = ValueViewModel(HtmlFormat.escape(value).toString),
      actions =
        if (withChangeLink)
          Seq(
            ActionItemViewModel("site.change", amendRoute.AmendEnterMrnController.onPageLoad(CheckMode, submissionId).url)
              .withVisuallyHiddenText(messages("enterMrn.change.hidden"))
          )
        else Seq.empty
    )
}
