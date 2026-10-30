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
import uk.gov.hmrc.automatedexportsystemfrontend.queries.DiscrepancySeals
import uk.gov.hmrc.govukfrontend.views.Aliases.{Text, Value}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Empty
import uk.gov.hmrc.govukfrontend.views.viewmodels.pagination.{Pagination, PaginationItem, PaginationLink}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.*

case class AddAnotherSealViewModel(
  numberOfSeals: Int,
  remainingSeals: Int,
  allowMore: Boolean,
  addedSingularOrPlural: String,
  remainingSingularOrPlural: String,
  rows: Seq[SummaryListRow],
  currentPage: Int,
  totalPages: Int,
  firstSeal: Int,
  lastSeal: Int,
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

object AddAnotherSealViewModel {
  def apply(userAnswers: UserAnswers, page: Option[Int], mode: Mode)(implicit messages: Messages): AddAnotherSealViewModel = {

    val numberOfSeals: Int = DiscrepancySeals.count(userAnswers)

    val remainingSeals: Int = DiscrepancySeals.remaining(userAnswers)

    val currentPage: Int = page.getOrElse(1)

    val totalPages: Int = ((numberOfSeals - 1) / pageSize) + 1

    val firstSeal: Int = ((currentPage - 1) * pageSize) + 1

    val lastSeal: Int = math.min(currentPage * pageSize, numberOfSeals)

    val firstPageNumber: Int = math.max(1, currentPage - 1)

    val lastPageNumber: Int = math.min(totalPages, currentPage + 1)

    val pageNumbers = firstPageNumber to lastPageNumber

    AddAnotherSealViewModel(
      numberOfSeals = numberOfSeals,
      remainingSeals = remainingSeals,
      allowMore = remainingSeals > 0,
      addedSingularOrPlural = if (numberOfSeals == 1) "singular" else "plural",
      remainingSingularOrPlural = if (remainingSeals == 1) "singular" else "plural",
      rows = rows(userAnswers, mode, currentPage),
      currentPage = currentPage,
      totalPages = totalPages,
      firstSeal = firstSeal,
      lastSeal = lastSeal,
      pageNumbers = pageNumbers
    )

  }

  def rows(userAnswers: UserAnswers, mode: Mode, currentPage: Int)(implicit messages: Messages): Seq[SummaryListRow] =
    DiscrepancySeals.getAll(userAnswers).zipWithIndex.slice((currentPage - 1) * pageSize, currentPage * pageSize).map { case (sealIdentifier, i) =>
      val index = i + 1
      SummaryListRow(
        key = Key(content = Text(sealIdentifier), classes = "govuk-!-font-weight-regular hmrc-summary-list__key"),
        value = Value(content = Empty, classes = "govuk-!-display-none"),
        actions = Some(
          Actions(items =
            Seq(
              ActionItem(
                href = createRoute.DiscrepancySealsController.onPageLoad(index, mode).url,
                content = Text(messages("site.change")),
                visuallyHiddenText = Some(messages("addAnotherSeal.change.hidden", index))
              ),
              ActionItem(
                href = createRoute.RemoveSealController.onPageLoad(index, currentPage, mode).url,
                content = Text(messages("site.remove")),
                visuallyHiddenText = Some(messages("addAnotherSeal.remove.hidden", index))
              )
            )
          )
        )
      )
    }
}
