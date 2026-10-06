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
import uk.gov.hmrc.automatedexportsystemfrontend.models.IE507a.TransportMode
import uk.gov.hmrc.automatedexportsystemfrontend.models.{CheckMode, ModeOfTransportAtBorder, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.pages.amend.AmendDiscrepancyConsignmentPage
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.govuk.all.ValueViewModel
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.govuk.summarylist.*
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.implicits.*
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow

object AmendDiscrepancyConsignmentSummary {

  def row(answers: UserAnswers)(submissionId: String)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(AmendDiscrepancyConsignmentPage(submissionId)).map { answer =>
      build(answer, submissionId, withChangeLink = true)
    }

  def row(value: ModeOfTransportAtBorder, submissionId: String, withChangeLink: Boolean)(implicit messages: Messages): Option[SummaryListRow] =
    Some(build(value, submissionId, withChangeLink))

  private def build(value: ModeOfTransportAtBorder, submissionId: String, withChangeLink: Boolean)(implicit messages: Messages): SummaryListRow =
    SummaryListRowViewModel(
      key = "consignment.checkYourAnswersLabel",
      value = ValueViewModel(HtmlFormat.escape(messages(s"discrepancyConsignment.${value.toString}")).toString),
      actions =
        if (withChangeLink)
          Seq(
            ActionItemViewModel("site.change", amendRoute.AmendDiscrepancyConsignmentController.onPageLoad(CheckMode, submissionId).url)
              .withVisuallyHiddenText(messages("AmendDiscrepancyConsignment.change.hidden"))
          )
        else Seq.empty
    )
}
