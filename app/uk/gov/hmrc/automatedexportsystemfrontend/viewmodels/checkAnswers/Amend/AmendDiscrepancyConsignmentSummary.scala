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
import uk.gov.hmrc.automatedexportsystemfrontend.pages.amend.AmendDiscrepancyConsignmentPage
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.govuk.all.ValueViewModel
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.govuk.summarylist.*
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.implicits.*
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow

object AmendDiscrepancyConsignmentSummary {

  def row(answers: UserAnswers)(submissionId: String, description: Option[String])(implicit messages: Messages): Option[SummaryListRow] =
    row(answers.get(AmendDiscrepancyConsignmentPage(submissionId)), submissionId, withChangeLink = true, description = description)

  def row(code: Option[String], submissionId: String, withChangeLink: Boolean, description: Option[String])(
    implicit messages: Messages
  ): Option[SummaryListRow] =
    code.map { _ =>
      build(description.getOrElse(messages("discrepancyConsignment.descriptionUnavailable")), submissionId, withChangeLink)
    }

  private def build(description: String, submissionId: String, withChangeLink: Boolean)(implicit messages: Messages): SummaryListRow =
    SummaryListRowViewModel(
      key = "discrepancyConsignment.checkYourAnswersLabel",
      value = ValueViewModel(HtmlContent(HtmlFormat.escape(description))),
      actions =
        if (withChangeLink)
          Seq(
            ActionItemViewModel("site.change", amendRoute.AmendDiscrepancyConsignmentController.onPageLoad(CheckMode, submissionId).url)
              .withVisuallyHiddenText(messages("AmendDiscrepancyConsignment.change.hidden"))
          )
        else Seq.empty
    )
}
