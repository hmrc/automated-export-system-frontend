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
}

object AddAnotherGoodsReferenceViewModel {
  def apply(userAnswers: UserAnswers, transportEquipmentIndex: Int, page: Option[Int], mode: Mode)(
    implicit messages: Messages
  ): AddAnotherGoodsReferenceViewModel = {

    val numberOfGoodsReferences: Int = DiscrepancyReference(transportEquipmentIndex).count(userAnswers)

    val remainingGoodsReferences: Int = DiscrepancyReference(transportEquipmentIndex).remaining(userAnswers)

    val currentPage: Int = page.getOrElse(1)

    val totalPages: Int = PaginationBuilder.totalPages(numberOfGoodsReferences)

    AddAnotherGoodsReferenceViewModel(
      numberOfGoodsReferences = numberOfGoodsReferences,
      remainingGoodsReferences = remainingGoodsReferences,
      allowMore = remainingGoodsReferences > 0,
      addedSingularOrPlural = if (numberOfGoodsReferences == 1) "singular" else "plural",
      remainingSingularOrPlural = if (remainingGoodsReferences == 1) "singular" else "plural",
      rows = rows(userAnswers, transportEquipmentIndex, currentPage, mode),
      currentPage = currentPage,
      totalPages = totalPages,
      firstGoodsReference = PaginationBuilder.firstItem(currentPage),
      lastGoodsReference = PaginationBuilder.lastItem(currentPage, numberOfGoodsReferences),
      pagination = PaginationBuilder.pagination(
        currentPage,
        totalPages,
        index => createRoute.AddAnotherGoodsReferenceController.onPageLoad(transportEquipmentIndex, Some(index), mode).url
      )
    )
  }

  def rows(userAnswers: UserAnswers, transportEquipmentIndex: Int, currentPage: Int, mode: Mode)(implicit messages: Messages): Seq[SummaryListRow] =
    PaginationBuilder.slice(DiscrepancyReference(transportEquipmentIndex).getAll(userAnswers).zipWithIndex, currentPage).map {
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
}
