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
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.create.{routes as createRoute, RemoveGoodsItemController}
import uk.gov.hmrc.automatedexportsystemfrontend.forms.Constants.pageSize
import uk.gov.hmrc.automatedexportsystemfrontend.models.{Mode, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.queries.DiscrepancyGoods
import uk.gov.hmrc.govukfrontend.views.Aliases.{Text, Value}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Empty
import uk.gov.hmrc.govukfrontend.views.viewmodels.pagination.{Pagination, PaginationItem, PaginationLink}
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
  pageNumbers: Seq[Int],
  rows: Seq[SummaryListRow]
) {
  def hasPreviousPage: Boolean = currentPage > 1

  def hasNextPage: Boolean = currentPage < totalPages

  def previousPage: Int = currentPage - 1

  def nextPage: Int = currentPage + 1

  def pageParam: Option[Int] = if (totalPages > 1) Some(currentPage) else None

}

object AddAnotherGoodsItemViewModel {
  def apply(userAnswers: UserAnswers, page: Option[Int], mode: Mode)(implicit messages: Messages): AddAnotherGoodsItemViewModel = {

    val numberOfGoodsItem: Int = DiscrepancyGoods.count(userAnswers)

    val remainingGoodsItem: Int = DiscrepancyGoods.remaining(userAnswers)

    val currentPage: Int = page.getOrElse(1)

    val totalPages: Int = ((numberOfGoodsItem - 1) / pageSize) + 1

    val firstGoodsItem: Int = ((currentPage - 1) * pageSize) + 1

    val lastGoodsItem: Int = math.min(currentPage * pageSize, numberOfGoodsItem)

    val firstPageNumber: Int = math.max(1, currentPage - 1)

    val lastPageNumber: Int = math.min(totalPages, currentPage + 1)

    val pageNumbers = firstPageNumber to lastPageNumber

    AddAnotherGoodsItemViewModel(
      numberOfGoodsItem = numberOfGoodsItem,
      remainingGoodsItem = remainingGoodsItem,
      allowMore = remainingGoodsItem > 0,
      addedSingularOrPlural = if (numberOfGoodsItem == 1) "singular" else "plural",
      remainingSingularOrPlural = if (remainingGoodsItem == 1) "singular" else "plural",
      currentPage = currentPage,
      totalPages = totalPages,
      firstGoodsItem = firstGoodsItem,
      lastGoodsItem = lastGoodsItem,
      pageNumbers = pageNumbers,
      rows = rows(firstGoodsItem to lastGoodsItem, userAnswers, mode, currentPage)
    )

  }

  def rows(goodsNumbers: Seq[Int], userAnswers: UserAnswers, mode: Mode, currentPage: Int)(implicit messages: Messages): Seq[SummaryListRow] =
    goodsNumbers.slice((currentPage - 1) * pageSize, currentPage * pageSize).map { goodsIndex =>
      SummaryListRow(
        key = Key(content = Text(messages("addAnotherGoodsItem.key", goodsIndex)), classes = "govuk-!-font-weight-regular hmrc-summary-list__key"),
        value = Value(content = Empty, classes = "govuk-!-display-none"),
        actions = Some(
          Actions(items =
            Seq(
              ActionItem(
                href = createRoute.DiscrepancyGoodsController.onPageLoad(goodsIndex, mode).url,
                content = Text(messages("site.change")),
                visuallyHiddenText = Some(messages("addAnotherGoodsItem.hidden", goodsIndex))
              ),
              ActionItem(
                href = createRoute.RemoveGoodsItemController.onPageLoad(goodsIndex, currentPage, mode).url,
                content = Text(messages("site.remove")),
                visuallyHiddenText = Some(messages("addAnotherGoodsItem.hidden", goodsIndex))
              )
            )
          )
        )
      )
    }
}
