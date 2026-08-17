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

package uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.checkAnswers.discrepancies

import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.discrepancies.routes as discrepanciesRoute
import uk.gov.hmrc.automatedexportsystemfrontend.models.{CheckMode, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.pages.discrepancies.DiscrepancyGoodsPage
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.govuk.summarylist.*
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.implicits.*
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow

object DiscrepancyGoodsSummary {

  def row(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(DiscrepancyGoodsPage).map { answer =>

      val value = Seq(
        Some(HtmlFormat.escape(answer.goodsItemNumber).toString),
        answer.declarationUniqueConsignmentReference.map(ducr => HtmlFormat.escape(ducr).toString),
        Some(HtmlFormat.escape(answer.newGrossMass).toString),
        Some(HtmlFormat.escape(answer.newNetMass).toString)
      ).flatten.mkString("<br/>")

      SummaryListRowViewModel(
        key = "discrepancyGoods.checkYourAnswersLabel",
        value = ValueViewModel(HtmlContent(value)),
        actions = Seq(
          ActionItemViewModel("site.change", discrepanciesRoute.DiscrepancyGoodsController.onPageLoad(CheckMode).url)
            .withVisuallyHiddenText(messages("discrepancyGoods.change.hidden"))
        )
      )
    }
}
