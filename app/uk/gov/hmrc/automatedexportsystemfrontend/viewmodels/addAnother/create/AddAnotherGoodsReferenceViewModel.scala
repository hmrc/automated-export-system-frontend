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

package uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.addAnother.create

import play.api.i18n.Messages
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.create.routes as createRoute
import uk.gov.hmrc.automatedexportsystemfrontend.models.{Mode, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.queries.DiscrepancyReference
import uk.gov.hmrc.govukfrontend.views.Aliases.{Actions, Empty}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{ActionItem, Key, SummaryListRow, Value}

case class AddAnotherGoodsReferenceViewModel(
  numberOfGoodsReferences: Int,
  remainingGoodsReferences: Int,
  allowMore: Boolean,
  addedSingularOrPlural: String,
  remainingSingularOrPlural: String,
  rows: Seq[SummaryListRow]
)

object AddAnotherGoodsReferenceViewModel {
  def apply(userAnswers: UserAnswers, mode: Mode)(implicit messages: Messages): AddAnotherGoodsReferenceViewModel = {

    val numberOfGoodsReferences: Int = DiscrepancyReference.count(userAnswers)

    val remainingGoodsReferences: Int = DiscrepancyReference.remaining(userAnswers)

    AddAnotherGoodsReferenceViewModel(
      numberOfGoodsReferences = numberOfGoodsReferences,
      remainingGoodsReferences = remainingGoodsReferences,
      allowMore = remainingGoodsReferences > 0,
      addedSingularOrPlural = if (numberOfGoodsReferences == 1) "singular" else "plural",
      remainingSingularOrPlural = if (remainingGoodsReferences == 1) "singular" else "plural",
      rows = rows(userAnswers, mode)
    )
  }

  def rows(userAnswers: UserAnswers, mode: Mode)(implicit messages: Messages): Seq[SummaryListRow] =
    DiscrepancyReference.getAll(userAnswers).zipWithIndex.map { case (goodsReference, i) =>
      val index = i + 1

      SummaryListRow(
        key = Key(
          content = Text(messages("addAnotherGoodsReference.key", goodsReference)),
          classes = "govuk-!-font-weight-regular hmrc-summary-list__key"
        ),
        value = Value(content = Empty, classes = "govuk-!-display-none"),
        actions = Some(
          Actions(items =
            Seq(
              ActionItem(
                href = createRoute.DiscrepancyReferenceController.onPageLoad(index, mode).url,
                content = Text(messages("site.change")),
                visuallyHiddenText = Some(messages("addAnotherGoodsReference.change.hidden", goodsReference))
              ),
              ActionItem(
                href = createRoute.RemoveGoodsReferenceController.onPageLoad(index, mode).url,
                content = Text(messages("site.remove")),
                visuallyHiddenText = Some(messages("addAnotherGoodsReference.remove.hidden", goodsReference))
              )
            )
          )
        )
      )
    }
}
