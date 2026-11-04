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
import uk.gov.hmrc.automatedexportsystemfrontend.queries.DiscrepancyTransportEquipment
import uk.gov.hmrc.govukfrontend.views.Aliases.{Text, Value}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Empty
import uk.gov.hmrc.govukfrontend.views.viewmodels.pagination.{Pagination, PaginationItem, PaginationLink}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.*

case class AddAnotherTransportEquipmentViewModel(
  numberOfTransportEquipment: Int,
  remainingTransportEquipment: Int,
  allowMore: Boolean,
  addedSingularOrPlural: String,
  remainingSingularOrPlural: String,
  currentPage: Int,
  totalPages: Int,
  firstTransportEquipment: Int,
  lastTransportEquipment: Int,
  rows: Seq[SummaryListRow],
  transportEquipmentNumbers: Seq[Int],
  pageNumbers: Seq[Int]
) {

  def hasPreviousPage: Boolean = currentPage > 1

  def hasNextPage: Boolean = currentPage < totalPages

  def previousPage: Int = currentPage - 1

  def nextPage: Int = currentPage + 1

  def pageParam: Option[Int] =
    if (totalPages > 1) Some(currentPage)
    else None

  def pagination(mode: Mode)(implicit messages: Messages): Option[Pagination] =
    if (totalPages <= 1) None
    else {
      def href(page: Int): String =
        createRoute.AddAnotherSealController.onPageLoad(Some(page), mode).url

      Some(
        Pagination(
          items = Some(pageNumbers.map { number =>
            PaginationItem(href = href(number), number = Some(number.toString), current = Some(number == currentPage))
          }),
          previous = Option.when(hasPreviousPage)(PaginationLink(href = href(previousPage), text = Some(messages("site.pagination.previous")))),
          next = Option.when(hasNextPage)(PaginationLink(href = href(nextPage), text = Some(messages("site.pagination.next"))))
        )
      )
    }
}

object AddAnotherTransportEquipmentViewModel {
  private val pageSize = 25
  def apply(userAnswers: UserAnswers, page: Option[Int], mode: Mode)(implicit messages: Messages): AddAnotherTransportEquipmentViewModel = {

    val numberOfTransportEquipment: Int = DiscrepancyTransportEquipment.count(userAnswers)

    val remainingTransportEquipment: Int = DiscrepancyTransportEquipment.remaining(userAnswers)

    val requestedPage: Int = page.getOrElse(1)

    val totalPages: Int = math.max(1, ((numberOfTransportEquipment - 1) / pageSize) + 1)

    val currentPage: Int = math.min(math.max(requestedPage, 1), totalPages)

    val firstTransportEquipment: Int =
      if (numberOfTransportEquipment == 0) {
        0
      } else {
        ((currentPage - 1) * pageSize) + 1
      }

    val lastTransportEquipment: Int = math.min(currentPage * pageSize, numberOfTransportEquipment)

    val firstPageNumber: Int = math.max(1, currentPage - 1)

    val lastPageNumber: Int = math.min(totalPages, currentPage + 1)

    val transportEquipmentNumbers: Seq[Int] =
      if (numberOfTransportEquipment == 0) {
        Seq.empty
      } else {
        firstTransportEquipment to lastTransportEquipment
      }

    AddAnotherTransportEquipmentViewModel(
      numberOfTransportEquipment = numberOfTransportEquipment,
      remainingTransportEquipment = remainingTransportEquipment,
      allowMore = remainingTransportEquipment > 0,
      addedSingularOrPlural = if (numberOfTransportEquipment == 1) "singular" else "plural",
      remainingSingularOrPlural = if (remainingTransportEquipment == 1) "singular" else "plural",
      currentPage = currentPage,
      totalPages = totalPages,
      firstTransportEquipment = firstTransportEquipment,
      lastTransportEquipment = lastTransportEquipment,
      rows = rows(transportEquipmentNumbers, mode),
      transportEquipmentNumbers = transportEquipmentNumbers,
      pageNumbers = firstPageNumber to lastPageNumber
    )
  }

  private def rows(transportEquipmentNumbers: Seq[Int], mode: Mode)(implicit messages: Messages): Seq[SummaryListRow] =
    transportEquipmentNumbers.map { transportEquipmentNumber =>
      SummaryListRow(
        key = Key(
          content = Text(messages("addAnotherTransportEquipment.key", transportEquipmentNumber)),
          classes = "govuk-!-font-weight-regular hmrc-summary-list__key"
        ),
        value = Value(content = Empty, classes = "govuk-!-display-none"),
        actions = Some(
          Actions(items =
            Seq(
              ActionItem(
                href = createRoute.DiscrepancyTransportController
                  .onPageLoad(transportEquipmentNumber, mode)
                  .url,
                content = Text(messages("site.change")),
                visuallyHiddenText = Some(messages("addAnotherTransportEquipment.change.hidden", transportEquipmentNumber))
              )
            )
          )
        )
      )
    }
}
