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
  pagination: Option[Pagination]
) {

  def pageParam: Option[Int] =
    if (totalPages > 1) Some(currentPage)
    else None
}

object AddAnotherTransportEquipmentViewModel {
  def apply(userAnswers: UserAnswers, page: Option[Int], mode: Mode)(implicit messages: Messages): AddAnotherTransportEquipmentViewModel = {

    val numberOfTransportEquipment: Int = DiscrepancyTransportEquipment.count(userAnswers)

    val remainingTransportEquipment: Int = DiscrepancyTransportEquipment.remaining(userAnswers)

    val currentPage: Int = page.getOrElse(1)

    val totalPages: Int = PaginationBuilder.totalPages(numberOfTransportEquipment)

    AddAnotherTransportEquipmentViewModel(
      numberOfTransportEquipment = numberOfTransportEquipment,
      remainingTransportEquipment = remainingTransportEquipment,
      allowMore = remainingTransportEquipment > 0,
      addedSingularOrPlural = if (numberOfTransportEquipment == 1) "singular" else "plural",
      remainingSingularOrPlural = if (remainingTransportEquipment == 1) "singular" else "plural",
      currentPage = currentPage,
      totalPages = totalPages,
      firstTransportEquipment = PaginationBuilder.firstItem(currentPage),
      lastTransportEquipment = PaginationBuilder.lastItem(currentPage, numberOfTransportEquipment),
      rows = rows(userAnswers, currentPage, mode),
      pagination =
        PaginationBuilder.pagination(currentPage, totalPages, index => createRoute.AddAnotherTransportController.onPageLoad(Some(index), mode).url)
    )
  }

  private def rows(userAnswers: UserAnswers, currentPage: Int, mode: Mode)(implicit messages: Messages): Seq[SummaryListRow] =
    PaginationBuilder.slice(DiscrepancyTransportEquipment.getAll(userAnswers).zipWithIndex, currentPage).map { case (transport, i) =>
      val index = i + 1

      val containerIdOrIndex = transport.containerDetails.flatMap(_.containerId).getOrElse(f"$index%,d")

      SummaryListRow(
        key = Key(
          content = Text(messages("addAnotherTransportEquipment.key", containerIdOrIndex)),
          classes = "govuk-!-font-weight-regular hmrc-summary-list__key"
        ),
        value = Value(content = Empty, classes = "govuk-!-display-none"),
        actions = Some(
          Actions(items =
            Seq(
              ActionItem(
                href = createRoute.DiscrepancyTransportController
                  .onPageLoad(index, mode)
                  .url,
                content = Text(messages("site.change")),
                visuallyHiddenText = Some(messages("addAnotherTransportEquipment.change.hidden", containerIdOrIndex))
              ),
              ActionItem(
                href = createRoute.RemoveTransportEquipmentController
                  .onPageLoad(index, currentPage, mode)
                  .url,
                content = Text(messages("site.remove")),
                visuallyHiddenText = Some(messages("addAnotherTransportEquipment.remove.hidden", containerIdOrIndex))
              )
            )
          )
        )
      )
    }
}
