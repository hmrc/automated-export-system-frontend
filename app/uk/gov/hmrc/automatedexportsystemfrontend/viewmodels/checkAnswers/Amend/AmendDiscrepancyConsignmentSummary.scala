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
import uk.gov.hmrc.automatedexportsystemfrontend.models.{CheckMode, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.pages.amend.AmendDiscrepancyConsignmentPage
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.govuk.all.ValueViewModel
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.govuk.summarylist.*
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.implicits.*
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow

object AmendDiscrepancyConsignmentSummary {

  def row(answerFromXml: Int, submissionId: String, withAmendLink: Boolean)(implicit messages: Messages): Option[SummaryListRow] = {

    // TODO this is a little jank might need to see a better way of handling this
    val mode = TransportMode.fromXmlPayload(answerFromXml).toString.toLowerCase()

    val value = ValueViewModel(HtmlContent(HtmlFormat.escape(messages(s"discrepancyConsignment.$mode"))))

    Some(
      SummaryListRowViewModel(
        key = "discrepancyConsignment.checkYourAnswersLabel",
        value = value,
        actions = if (withAmendLink) {
          Seq(
            ActionItemViewModel("site.change", amendRoute.AmendDiscrepancyConsignmentController.onPageLoad(CheckMode, submissionId).url)
              .withVisuallyHiddenText(messages("discrepancyConsignment.change.hidden"))
          )
        } else {
          Seq.empty
        }
      )
    )
  }
}
