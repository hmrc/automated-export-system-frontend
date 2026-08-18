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

package uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.checkAnswers

import uk.gov.hmrc.automatedexportsystemfrontend.controllers.routes
import uk.gov.hmrc.automatedexportsystemfrontend.models.{CheckMode, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.pages.TestPage
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.govuk.all.ValueViewModel
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.govuk.summarylist.*
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.implicits.*

object TestSummary {

  def row(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(TestPage).map { answers =>

      val value = ValueViewModel(HtmlContent(answers.map { answer =>
        HtmlFormat.escape(messages(s"test.$answer")).toString
      }
        .mkString(",<br>")))

      SummaryListRowViewModel(
        key = "test.checkYourAnswersLabel",
        value = value,
        actions = Seq(
          ActionItemViewModel("site.change", uk.gov.hmrc.automatedexportsystemfrontend.controllers.routes.TestController.onPageLoad(CheckMode).url)
            .withVisuallyHiddenText(messages("test.change.hidden"))
        )
      )
    }
}
