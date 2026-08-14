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

package uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.checkAnswers.unhappyPath

import uk.gov.hmrc.automatedexportsystemfrontend.controllers.unhappyPath.routes as unhappyRoute
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.automatedexportsystemfrontend.models.{CheckMode, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.pages.unhappyPath.DiscrepancyTransportPage
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.govuk.summarylist.*
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.implicits.*
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow

object DiscrepancyTransportSummary {

  def row(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(DiscrepancyTransportPage).map { answer =>

      val value = HtmlFormat.escape(answer.containerId).toString + "<br/>" + answer.numberOfSeals

      SummaryListRowViewModel(
        key = "discrepancyTransport.checkYourAnswersLabel",
        value = ValueViewModel(HtmlContent(value)),
        actions = Seq(
          ActionItemViewModel("site.change", unhappyRoute.DiscrepancyTransportController.onPageLoad(CheckMode).url)
            .withVisuallyHiddenText(messages("discrepancyTransport.change.hidden"))
        )
      )
    }
}
