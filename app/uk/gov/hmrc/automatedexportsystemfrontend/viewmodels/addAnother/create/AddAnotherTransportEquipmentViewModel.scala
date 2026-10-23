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

import uk.gov.hmrc.automatedexportsystemfrontend.models.UserAnswers
import uk.gov.hmrc.automatedexportsystemfrontend.queries.DiscrepancyTransportEquipment

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
}

object AddAnotherTransportEquipmentViewModel {

  private val pageSize = 25

  def apply(userAnswers: UserAnswers, page: Option[Int]): AddAnotherTransportEquipmentViewModel = {

    val numberOfTransportEquipment: Int =
      DiscrepancyTransportEquipment.count(userAnswers)

    val remainingTransportEquipment: Int =
      DiscrepancyTransportEquipment.remaining(userAnswers)

    val currentPage: Int =
      page.getOrElse(1)

    val totalPages: Int =
      ((numberOfTransportEquipment - 1) / pageSize) + 1

    val firstTransportEquipment: Int =
      ((currentPage - 1) * pageSize) + 1

    val lastTransportEquipment: Int =
      math.min(currentPage * pageSize, numberOfTransportEquipment)

    val firstPageNumber: Int =
      math.max(1, currentPage - 1)

    val lastPageNumber: Int =
      math.min(totalPages, currentPage + 1)

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
      transportEquipmentNumbers = firstTransportEquipment to lastTransportEquipment,
      pageNumbers = firstPageNumber to lastPageNumber
    )
  }
}
