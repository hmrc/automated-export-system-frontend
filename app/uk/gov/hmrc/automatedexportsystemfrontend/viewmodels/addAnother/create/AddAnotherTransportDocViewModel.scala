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
import uk.gov.hmrc.automatedexportsystemfrontend.queries.DiscrepancyTransportDoc
import uk.gov.hmrc.govukfrontend.views.Aliases.{Text, Value}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Empty
import uk.gov.hmrc.govukfrontend.views.viewmodels.pagination.{Pagination, PaginationItem, PaginationLink}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.*

case class AddAnotherTransportDocViewModel(
  numberOfTransportDocs: Int,
  remainingTransportDocs: Int,
  allowMore: Boolean,
  addedSingularOrPlural: String,
  remainingSingularOrPlural: String,
  rows: Seq[SummaryListRow],
  currentPage: Int,
  totalPages: Int,
  firstTransportDoc: Int,
  lastTransportDoc: Int,
  pageNumbers: Seq[Int]
) {
  def hasPreviousPage: Boolean = currentPage > 1

  def hasNextPage: Boolean = currentPage < totalPages

  def previousPage: Int = currentPage - 1

  def nextPage: Int = currentPage + 1

  def pageParam: Option[Int] = if (totalPages > 1) Some(currentPage) else None

  def pagination(mode: Mode)(implicit messages: Messages): Option[Pagination] =
    if (totalPages <= 1) None
    else {
      def href(page: Int): String =
        createRoute.AddAnotherTransportDocController.onPageLoad(Some(page), mode).url

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

object AddAnotherTransportDocViewModel {
  def apply(userAnswers: UserAnswers, page: Option[Int], mode: Mode)(implicit messages: Messages): AddAnotherTransportDocViewModel = {

    val numberOfTransportDocs: Int = DiscrepancyTransportDoc.count(userAnswers)

    val remainingTransportDocs: Int = DiscrepancyTransportDoc.remaining(userAnswers)

    val currentPage: Int = page.getOrElse(1)

    val totalPages: Int = ((numberOfTransportDocs - 1) / pageSize) + 1

    val firstTransportDoc: Int = ((currentPage - 1) * pageSize) + 1

    val lastTransportDoc: Int = math.min(currentPage * pageSize, numberOfTransportDocs)

    val firstPageNumber: Int = math.max(1, currentPage - 1)

    val lastPageNumber: Int = math.min(totalPages, currentPage + 1)

    val pageNumbers = firstPageNumber to lastPageNumber

    AddAnotherTransportDocViewModel(
      numberOfTransportDocs = numberOfTransportDocs,
      remainingTransportDocs = remainingTransportDocs,
      allowMore = remainingTransportDocs > 0,
      addedSingularOrPlural = if (numberOfTransportDocs == 1) "singular" else "plural",
      remainingSingularOrPlural = if (remainingTransportDocs == 1) "singular" else "plural",
      rows = rows(userAnswers, mode, currentPage),
      currentPage = currentPage,
      totalPages = totalPages,
      firstTransportDoc = firstTransportDoc,
      lastTransportDoc = lastTransportDoc,
      pageNumbers = pageNumbers
    )

  }

  def rows(userAnswers: UserAnswers, mode: Mode, currentPage: Int)(implicit messages: Messages): Seq[SummaryListRow] =
    DiscrepancyTransportDoc.getAll(userAnswers).zipWithIndex.slice((currentPage - 1) * pageSize, currentPage * pageSize).map {
      case (documentDetails, i) =>
        val index = i + 1
        SummaryListRow(
          key = Key(content = Text(messages("transportDocCYA.caption", index)), classes = "govuk-!-font-weight-regular hmrc-summary-list__key"),
          value = Value(content = Empty, classes = "govuk-!-display-none"),
          actions = Some(
            Actions(items =
              Seq(
                ActionItem(
                  href = createRoute.DiscrepancyTransportDocController.onPageLoad(index, mode).url,
                  content = Text(messages("site.change")),
                  visuallyHiddenText = Some(messages("addAnotherTransportDoc.change.hidden", index))
                ),
                ActionItem(
                  href = createRoute.RemoveTransportDocController.onPageLoad(index, currentPage, mode).url,
                  content = Text(messages("site.remove")),
                  visuallyHiddenText = Some(messages("addAnotherTransportDoc.remove.hidden", index))
                )
              )
            )
          )
        )
    }
}
