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
import uk.gov.hmrc.automatedexportsystemfrontend.queries.DiscrepancyReference
import uk.gov.hmrc.govukfrontend.views.Aliases.{Actions, Empty}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.pagination.{Pagination, PaginationItem, PaginationLink}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{ActionItem, Key, SummaryListRow, Value}

case class AddAnotherGoodsReferenceViewModel(
  numberOfGoodsReferences: Int,
  remainingGoodsReferences: Int,
  allowMore: Boolean,
  addedSingularOrPlural: String,
  remainingSingularOrPlural: String,
  rows: Seq[SummaryListRow],
  currentPage: Int,
  totalPages: Int,
  firstGoodsReference: Int,
  lastGoodsReference: Int,
  pagination: Option[Pagination]
) {

  def pageParam: Option[Int] = if (totalPages > 1) Some(currentPage) else None

  def hasPreviousPage: Boolean = currentPage > 1

  def hasNextPage: Boolean = currentPage < totalPages

  def previousPage: Int = currentPage - 1

  def nextPage: Int = currentPage + 1
}

object AddAnotherGoodsReferenceViewModel {
  def apply(userAnswers: UserAnswers, transportEquipmentIndex: Int, page: Option[Int], mode: Mode)(
    implicit messages: Messages
  ): AddAnotherGoodsReferenceViewModel = {

    val numberOfGoodsReferences: Int = DiscrepancyReference(transportEquipmentIndex).count(userAnswers)

    val remainingGoodsReferences: Int = DiscrepancyReference(transportEquipmentIndex).remaining(userAnswers)

    val currentPage: Int = page.getOrElse(1)

    val totalPages: Int = ((numberOfGoodsReferences - 1) / pageSize) + 1

    val firstGoodsReference: Int = ((currentPage - 1) * pageSize) + 1

    val lastGoodsReference: Int = math.min(currentPage * pageSize, numberOfGoodsReferences)

    AddAnotherGoodsReferenceViewModel(
      numberOfGoodsReferences = numberOfGoodsReferences,
      remainingGoodsReferences = remainingGoodsReferences,
      allowMore = remainingGoodsReferences > 0,
      addedSingularOrPlural = if (numberOfGoodsReferences == 1) "singular" else "plural",
      remainingSingularOrPlural = if (remainingGoodsReferences == 1) "singular" else "plural",
      rows = rows(userAnswers, transportEquipmentIndex, currentPage, mode),
      currentPage = currentPage,
      totalPages = totalPages,
      firstGoodsReference = firstGoodsReference,
      lastGoodsReference = lastGoodsReference,
      pagination = buildPagination(currentPage, transportEquipmentIndex, totalPages, mode)
    )
  }

  def rows(userAnswers: UserAnswers, transportEquipmentIndex: Int, currentPage: Int, mode: Mode)(implicit messages: Messages): Seq[SummaryListRow] =
    DiscrepancyReference(transportEquipmentIndex).getAll(userAnswers).zipWithIndex.slice((currentPage - 1) * pageSize, currentPage * pageSize).map {
      case (goodsReference, i) =>
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
                  href = createRoute.DiscrepancyReferenceController.onPageLoad(transportEquipmentIndex, index, mode).url,
                  content = Text(messages("site.change")),
                  visuallyHiddenText = Some(messages("addAnotherGoodsReference.change.hidden", goodsReference))
                ),
                ActionItem(
                  href = createRoute.RemoveGoodsReferenceController.onPageLoad(transportEquipmentIndex, index, currentPage, mode).url,
                  content = Text(messages("site.remove")),
                  visuallyHiddenText = Some(messages("addAnotherGoodsReference.remove.hidden", goodsReference))
                )
              )
            )
          )
        )
    }

  private def buildPagination(currentPage: Int, transportEquipmentIndex: Int, totalPages: Int, mode: Mode): Option[Pagination] =
    if (totalPages <= 1) {
      None
    } else {

      def href(number: Int) = createRoute.AddAnotherGoodsReferenceController.onPageLoad(transportEquipmentIndex, Some(number), mode).url

      val neighbouringPages = math.max(1, currentPage - 1) to math.min(totalPages, currentPage + 1)

      val visiblePages: Seq[Int] =
        if (totalPages <= 4) {
          1 to totalPages
        } else {
          (
            Seq(1, totalPages) ++
              neighbouringPages
          ).distinct.sorted
        }

      def pageItem(number: Int): PaginationItem =
        PaginationItem(href = href(number), number = Some(number.toString), current = if (number == currentPage) Some(true) else None)

      val items: Seq[PaginationItem] =
        visiblePages.zipWithIndex.flatMap {
          case (number, 0) => Seq(pageItem(number))
          case (number, index) =>
            val previous = visiblePages(index - 1)

            val gapItems =
              if (number - previous == 2) {
                Seq(pageItem(previous + 1))
              } else if (number - previous > 2) {
                Seq(PaginationItem(ellipsis = Some(true)))
              } else {
                Seq.empty
              }

            gapItems :+ pageItem(number)
        }

      Some(
        Pagination(
          items = Some(items),
          previous = if (currentPage > 1) {
            Some(PaginationLink(href = href(currentPage - 1)))
          } else {
            None
          },
          next = if (currentPage < totalPages) {
            Some(PaginationLink(href = href(currentPage + 1)))
          } else {
            None
          }
        )
      )
    }
}
