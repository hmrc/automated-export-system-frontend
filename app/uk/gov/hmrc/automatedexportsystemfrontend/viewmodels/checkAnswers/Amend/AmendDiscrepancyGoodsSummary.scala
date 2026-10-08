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
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.govuk.summarylist.*
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.implicits.*
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow

object AmendDiscrepancyGoodsSummary {

  def goodsItemNumberRow(answerFromXml: Int, submissionId: String, withAmendLink: Boolean)(implicit messages: Messages): Option[SummaryListRow] = {

    val declarationGoodsItemNumber = HtmlFormat.escape(answerFromXml.toString)

    Some(
      SummaryListRowViewModel(
        key = "discrepancyGoods.goodsItemNumber.checkYourAnswersLabel",
        value = ValueViewModel(HtmlContent(declarationGoodsItemNumber)),
        actions = if (withAmendLink) {
          Seq(
            ActionItemViewModel("site.change", amendRoute.AmendDiscrepancyGoodsController.onPageLoad(CheckMode, submissionId).url)
              .withVisuallyHiddenText(messages("discrepancyGoods.goodsItemNumber.change.hidden"))
          )
        } else {
          Seq.empty
        }
      )
    )

  }

  def goodsItemDucrRow(answerFromXml: String, submissionId: String, withAmendLink: Boolean)(implicit messages: Messages): Option[SummaryListRow] = {

    val ducr = HtmlFormat.escape(answerFromXml)

    Some(
      SummaryListRowViewModel(
        key = "discrepancyGoods.ducr.checkYourAnswersLabel",
        value = ValueViewModel(HtmlContent(ducr)),
        actions = if (withAmendLink) {
          Seq(
            ActionItemViewModel("site.change", amendRoute.AmendDiscrepancyGoodsController.onPageLoad(CheckMode, submissionId).url)
              .withVisuallyHiddenText(messages("discrepancyGoods.ducr.change.hidden"))
          )
        } else {
          Seq.empty
        }
      )
    )

  }

  def grossMassRow(answerFromXml: BigDecimal, submissionId: String, withAmendLink: Boolean)(implicit messages: Messages): Option[SummaryListRow] = {

    val newGrossMass = HtmlFormat.escape(answerFromXml.toString)

    Some(
      SummaryListRowViewModel(
        key = "discrepancyGoods.newGrossMass.checkYourAnswersLabel",
        value = ValueViewModel(HtmlContent(newGrossMass)),
        actions = if (withAmendLink) {
          Seq(
            ActionItemViewModel("site.change", amendRoute.AmendDiscrepancyGoodsController.onPageLoad(CheckMode, submissionId).url)
              .withVisuallyHiddenText(messages("discrepancyGoods.newGrossMass.change.hidden"))
          )
        } else {
          Seq.empty
        }
      )
    )

  }

  def netMassRow(answerFromXml: BigDecimal, submissionId: String, withAmendLink: Boolean)(implicit messages: Messages): Option[SummaryListRow] = {

    val newNetMass = HtmlFormat.escape(answerFromXml.toString)

    Some(
      SummaryListRowViewModel(
        key = "discrepancyGoods.newNetMass.checkYourAnswersLabel",
        value = ValueViewModel(HtmlContent(newNetMass)),
        actions = if (withAmendLink) {
          Seq(
            ActionItemViewModel("site.change", amendRoute.AmendDiscrepancyGoodsController.onPageLoad(CheckMode, submissionId).url)
              .withVisuallyHiddenText(messages("discrepancyGoods.newNetMass.change.hidden"))
          )
        } else {
          Seq.empty
        }
      )
    )

  }
}
