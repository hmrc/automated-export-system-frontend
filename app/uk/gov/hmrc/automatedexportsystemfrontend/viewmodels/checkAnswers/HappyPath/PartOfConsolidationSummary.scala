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

package uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.checkAnswers.HappyPath

import controllers.routes
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.happyPath.{routes => happyRoute}
import play.api.i18n.Messages
import uk.gov.hmrc.automatedexportsystemfrontend.models.{CheckMode, PartOfConsolidationAnswer, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.pages.happyPath.PartOfConsolidationPage
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.govuk.summarylist.*
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.implicits.*
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow

object PartOfConsolidationSummary {

  def row(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(PartOfConsolidationPage).map { answer =>

      val value = answer match {
        case PartOfConsolidationAnswer(true, Some(mucr)) => messages("site.yes") + " - " + messages("site.mucr") + ": " + mucr
        case _                                           => "site.no"
      }

      SummaryListRowViewModel(
        key = "partOfConsolidation.checkYourAnswersLabel",
        value = ValueViewModel(value),
        actions = Seq(
          ActionItemViewModel("site.change", happyRoute.PartOfConsolidationController.onPageLoad(CheckMode).url)
            .withVisuallyHiddenText(messages("partOfConsolidation.change.hidden"))
        )
      )
    }
}
