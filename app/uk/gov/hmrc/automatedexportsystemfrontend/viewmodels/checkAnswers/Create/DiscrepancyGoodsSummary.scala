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

package uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.checkAnswers.Create

import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.create.routes as createRoute
import uk.gov.hmrc.automatedexportsystemfrontend.models.{CheckMode, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.pages.create.DiscrepancyGoodsPage
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.govuk.summarylist.*
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.implicits.*
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow

object DiscrepancyGoodsSummary {

  def rows(answers: UserAnswers)(implicit messages: Messages): Option[Seq[SummaryListRow]] =
    answers.get(DiscrepancyGoodsPage).map { answer =>

      val maybeDeclarationGoodsItemNumber =
        answer.declarationGoodsItemNumber.map(declarationGoodsItemNum => HtmlFormat.escape(declarationGoodsItemNum.toString))
      val maybeDucr = answer.declarationUniqueConsignmentReference.map(ducr => HtmlFormat.escape(ducr))
      val newGrossMass = HtmlFormat.escape(answer.newGrossMass)
      val newNetMass = HtmlFormat.escape(answer.newNetMass)

      Seq(
        maybeDeclarationGoodsItemNumber.map { declarationGoodsItemNumber =>
          SummaryListRowViewModel(
            key = "discrepancyGoods.goodsItemNumber.checkYourAnswersLabel",
            value = ValueViewModel(HtmlContent(declarationGoodsItemNumber)),
            actions = Seq(
              ActionItemViewModel("site.change", createRoute.DiscrepancyGoodsController.onPageLoad(CheckMode).url)
                .withVisuallyHiddenText(messages("discrepancyGoods.goodsItemNumber.change.hidden"))
            )
          )
        },
        maybeDucr.map { ducr =>
          SummaryListRowViewModel(
            key = "discrepancyGoods.ducr.checkYourAnswersLabel",
            value = ValueViewModel(HtmlContent(ducr)),
            actions = Seq(
              ActionItemViewModel("site.change", createRoute.DiscrepancyGoodsController.onPageLoad(CheckMode).url)
                .withVisuallyHiddenText(messages("discrepancyGoods.ducr.change.hidden"))
            )
          )
        },
        Some(
          SummaryListRowViewModel(
            key = "discrepancyGoods.newGrossMass.checkYourAnswersLabel",
            value = ValueViewModel(HtmlContent(newGrossMass)),
            actions = Seq(
              ActionItemViewModel("site.change", createRoute.DiscrepancyGoodsController.onPageLoad(CheckMode).url)
                .withVisuallyHiddenText(messages("discrepancyGoods.newGrossMass.change.hidden"))
            )
          )
        ),
        Some(
          SummaryListRowViewModel(
            key = "discrepancyGoods.newNetMass.checkYourAnswersLabel",
            value = ValueViewModel(HtmlContent(newNetMass)),
            actions = Seq(
              ActionItemViewModel("site.change", createRoute.DiscrepancyGoodsController.onPageLoad(CheckMode).url)
                .withVisuallyHiddenText(messages("discrepancyGoods.newNetMass.change.hidden"))
            )
          )
        )
      ).flatten
    }
}
