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
import uk.gov.hmrc.automatedexportsystemfrontend.forms.Constants.pageSize
import uk.gov.hmrc.automatedexportsystemfrontend.models.{Mode, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.queries.DiscrepancyPacking
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{Empty, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.pagination.{Pagination, PaginationItem, PaginationLink}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.*

case class AddAnotherPackagingDetailViewModel(
  numberOfPackagingDetails: Int,
  remainingPackagingDetails: Int,
  allowMore: Boolean,
  addedSingularOrPlural: String,
  remainingSingularOrPlural: String,
  currentPage: Int,
  totalPages: Int,
  firstPackagingDetail: Int,
  lastPackagingDetail: Int,
  rows: Seq[SummaryListRow],
  pagination: Option[Pagination]
) {
  def pageParam: Option[Int] = if (totalPages > 1) Some(currentPage) else None
}

object AddAnotherPackagingDetailViewModel {

  def apply(userAnswers: UserAnswers, page: Option[Int], goodsItemIndex: Int, mode: Mode)(
    implicit messages: Messages
  ): AddAnotherPackagingDetailViewModel = {

    val numberOfPackagingDetails: Int = DiscrepancyPacking(goodsItemIndex).count(userAnswers)

    val remainingPackagingDetails: Int = DiscrepancyPacking(goodsItemIndex).remaining(userAnswers)

    val currentPage: Int = page.getOrElse(1)

    val totalPages: Int = PaginationBuilder.totalPages(numberOfPackagingDetails)

    AddAnotherPackagingDetailViewModel(
      numberOfPackagingDetails = numberOfPackagingDetails,
      remainingPackagingDetails = remainingPackagingDetails,
      allowMore = remainingPackagingDetails > 0,
      addedSingularOrPlural = if (numberOfPackagingDetails == 1) "singular" else "plural",
      remainingSingularOrPlural = if (remainingPackagingDetails == 1) "singular" else "plural",
      currentPage = currentPage,
      totalPages = totalPages,
      firstPackagingDetail = PaginationBuilder.firstItem(currentPage),
      lastPackagingDetail = PaginationBuilder.lastItem(currentPage, numberOfPackagingDetails),
      rows = rows(userAnswers, currentPage, goodsItemIndex, mode),
      pagination = PaginationBuilder.pagination(
        currentPage,
        totalPages,
        index => createRoute.AddAnotherPackagingDetailController.onPageLoad(goodsItemIndex, Some(index), mode).url
      )
    )
  }

  def rows(userAnswers: UserAnswers, currentPage: Int, goodsItemIndex: Int, mode: Mode)(implicit messages: Messages): Seq[SummaryListRow] =
    PaginationBuilder.slice(DiscrepancyPacking(goodsItemIndex).getAll(userAnswers).zipWithIndex, currentPage).map { case (_, i) =>
      val packagingDetailNumber = i + 1
      SummaryListRow(
        key = Key(
          content = Text(messages("addAnotherPackagingDetail.key", packagingDetailNumber)),
          classes = "govuk-!-font-weight-regular hmrc-summary-list__key"
        ),
        value = Value(content = Empty, classes = "govuk-!-display-none"),
        actions = Some(
          Actions(items =
            Seq(
              ActionItem(
                href = createRoute.DiscrepancyPackingController.onPageLoad(goodsItemIndex, packagingDetailNumber, mode).url,
                content = Text(messages("site.change")),
                visuallyHiddenText = Some(messages("addAnotherPackagingDetail.change.hidden", packagingDetailNumber))
              ),
              ActionItem(
                href = createRoute.RemovePackagingDetailController.onPageLoad(goodsItemIndex, packagingDetailNumber, currentPage, mode).url,
                content = Text(messages("site.remove")),
                visuallyHiddenText = Some(messages("addAnotherPackagingDetail.remove.hidden", packagingDetailNumber))
              )
            )
          )
        )
      )
    }
}
