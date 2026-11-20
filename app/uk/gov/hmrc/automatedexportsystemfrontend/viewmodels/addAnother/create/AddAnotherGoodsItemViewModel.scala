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
import uk.gov.hmrc.automatedexportsystemfrontend.queries.DiscrepancyGoods
import uk.gov.hmrc.govukfrontend.views.Aliases.{Text, Value}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Empty
import uk.gov.hmrc.govukfrontend.views.viewmodels.pagination.Pagination
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.*

case class AddAnotherGoodsItemViewModel(
  numberOfGoodsItem: Int,
  remainingGoodsItem: Int,
  allowMore: Boolean,
  addedSingularOrPlural: String,
  remainingSingularOrPlural: String,
  currentPage: Int,
  totalPages: Int,
  firstGoodsItem: Int,
  lastGoodsItem: Int,
  rows: Seq[SummaryListRow],
  pagination: Option[Pagination]
) {
  def pageParam: Option[Int] = if (totalPages > 1) Some(currentPage) else None
}

object AddAnotherGoodsItemViewModel {
  def apply(userAnswers: UserAnswers, page: Option[Int], mode: Mode)(implicit messages: Messages): AddAnotherGoodsItemViewModel = {

    val numberOfGoodsItem: Int = DiscrepancyGoods.count(userAnswers)

    val remainingGoodsItem: Int = DiscrepancyGoods.remaining(userAnswers)

    val currentPage: Int = page.getOrElse(1)

    val totalPages: Int = PaginationBuilder.totalPages(numberOfGoodsItem)

    AddAnotherGoodsItemViewModel(
      numberOfGoodsItem = numberOfGoodsItem,
      remainingGoodsItem = remainingGoodsItem,
      allowMore = remainingGoodsItem > 0,
      addedSingularOrPlural = if (numberOfGoodsItem == 1) "singular" else "plural",
      remainingSingularOrPlural = if (remainingGoodsItem == 1) "singular" else "plural",
      currentPage = currentPage,
      totalPages = totalPages,
      firstGoodsItem = PaginationBuilder.firstItem(currentPage),
      lastGoodsItem = PaginationBuilder.lastItem(currentPage, numberOfGoodsItem),
      rows = rows(userAnswers, mode, currentPage),
      pagination =
        PaginationBuilder.pagination(currentPage, totalPages, index => createRoute.AddAnotherGoodsItemController.onPageLoad(Some(index), mode).url)
    )

  }

  def rows(userAnswers: UserAnswers, mode: Mode, currentPage: Int)(implicit messages: Messages): Seq[SummaryListRow] =
    PaginationBuilder.slice(DiscrepancyGoods.getAll(userAnswers).zipWithIndex, currentPage).map { case (_, index) =>
      val goodsIndex = index + 1
      SummaryListRow(
        key = Key(content = Text(messages("addAnotherGoodsItem.key", goodsIndex)), classes = "govuk-!-font-weight-regular hmrc-summary-list__key"),
        value = Value(content = Empty, classes = "govuk-!-display-none"),
        actions = Some(
          Actions(items =
            Seq(
              ActionItem(
                href = createRoute.DiscrepancyGoodsController.onPageLoad(goodsIndex, mode).url,
                content = Text(messages("site.change")),
                visuallyHiddenText = Some(messages("addAnotherGoodsItem.change.hidden", goodsIndex))
              ),
              ActionItem(
                href = createRoute.RemoveGoodsItemController.onPageLoad(goodsIndex, currentPage, mode).url,
                content = Text(messages("site.remove")),
                visuallyHiddenText = Some(messages("addAnotherGoodsItem.remove.hidden", goodsIndex))
              )
            )
          )
        )
      )
    }
}
