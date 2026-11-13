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
import uk.gov.hmrc.automatedexportsystemfrontend.forms.Constants.pageSize
import uk.gov.hmrc.govukfrontend.views.viewmodels.pagination.{Pagination, PaginationItem, PaginationLink}

object PaginationBuilder {

  def totalPages(itemCount: Int): Int =
    ((itemCount - 1) / pageSize) + 1

  def firstItem(currentPage: Int): Int = ((currentPage - 1) * pageSize) + 1

  def lastItem(currentPage: Int, itemCount: Int): Int = math.min(currentPage * pageSize, itemCount)

  def slice[A](items: Seq[A], currentPage: Int): Seq[A] =
    if (currentPage < 1) {
      Seq.empty
    } else {
      val from = (currentPage - 1).toLong * pageSize
      if (from >= items.size) Seq.empty else items.slice(from.toInt, (from + pageSize).toInt)
    }

  def pagination(currentPage: Int, totalPages: Int, href: Int => String)(implicit messages: Messages): Option[Pagination] =
    if (totalPages <= 1) {
      None
    } else {
      val neighbouringPages = math.max(1, currentPage - 1) to math.min(totalPages, currentPage + 1)

      val visiblePages: Seq[Int] =
        if (totalPages <= 4) {
          neighbouringPages
        } else {
          (
            Seq(1, totalPages) ++
              neighbouringPages
          ).distinct.sorted
        }

      def pageItem(number: Int): PaginationItem =
        PaginationItem(
          href = href(number),
          number = Some(number.toString),
          visuallyHiddenText = Some(messages("site.pagination.page", number)),
          current = Option.when(number == currentPage)(true)
        )

      val items: Seq[PaginationItem] =
        visiblePages.zipWithIndex.flatMap {
          case (number, 0) => Seq(pageItem(number))
          case (number, index) =>
            val previous = visiblePages(index - 1)

            val gapItems = (number - previous) match {
              case 2                            => Seq(pageItem(previous + 1))
              case difference if difference > 2 => Seq(PaginationItem(ellipsis = Some(true)))
              case _                            => Seq.empty
            }
            gapItems :+ pageItem(number)
        }

      Some(
        Pagination(
          items = Some(items),
          previous = Option.when(currentPage > 1)(PaginationLink(href = href(currentPage - 1), text = Some(messages("site.pagination.previous")))),
          next = Option.when(currentPage < totalPages)(PaginationLink(href = href(currentPage + 1), text = Some(messages("site.pagination.next"))))
        )
      )
    }

}
