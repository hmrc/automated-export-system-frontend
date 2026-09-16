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
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.create.routes as createRoute
import uk.gov.hmrc.automatedexportsystemfrontend.models.{CheckMode, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.pages.create.DiscrepancyTransportPage
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.govuk.summarylist.*
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.implicits.*
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow

object AmendDiscrepancyTransportSummary {

  def containerIdRow(answerFromXml: String, submissionId: String, withAmendLink: Boolean)(implicit messages: Messages): Option[SummaryListRow] = {

    val containerId = HtmlFormat.escape(answerFromXml)

    Some(
      SummaryListRowViewModel(
        key = "discrepancyTransport.containerId.checkYourAnswersLabel",
        value = ValueViewModel(HtmlContent(containerId)),
        actions = Seq(
          ActionItemViewModel("site.change", createRoute.DiscrepancyTransportController.onPageLoad(CheckMode).url)
            .withVisuallyHiddenText(messages("discrepancyTransport.containerId.change.hidden"))
        )
      )
    )

  }

  def numberOfSealsRow(answerFromXml: Int, submissionId: String, withAmendLink: Boolean)(implicit messages: Messages): Option[SummaryListRow] = {

    val numberOfSeals = answerFromXml.toString

    Some(
      SummaryListRowViewModel(
        key = "discrepancyTransport.numberOfSeals.checkYourAnswersLabel",
        value = ValueViewModel(HtmlContent(numberOfSeals)),
        actions = Seq(
          ActionItemViewModel("site.change", createRoute.DiscrepancyTransportController.onPageLoad(CheckMode).url)
            .withVisuallyHiddenText(messages("discrepancyTransport.numberOfSeals.change.hidden"))
        )
      )
    )
  }
}
