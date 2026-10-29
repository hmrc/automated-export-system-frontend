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

import uk.gov.hmrc.automatedexportsystemfrontend.forms.Constants.pageSize
import uk.gov.hmrc.automatedexportsystemfrontend.models.UserAnswers
import uk.gov.hmrc.automatedexportsystemfrontend.queries.DiscrepancyPacking

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
  packagingDetailNumbers: Seq[Int],
  pageNumbers: Seq[Int]
) {

  def hasPreviousPage: Boolean = currentPage > 1

  def hasNextPage: Boolean = currentPage < totalPages

  def previousPage: Int = currentPage - 1

  def nextPage: Int = currentPage + 1

  def pageParam: Option[Int] = if (totalPages > 1) Some(currentPage) else None
}

object AddAnotherPackagingDetailViewModel {

  def apply(userAnswers: UserAnswers, page: Option[Int]): AddAnotherPackagingDetailViewModel = {

    val numberOfPackagingDetails: Int = DiscrepancyPacking.count(userAnswers)

    val remainingPackagingDetails: Int = DiscrepancyPacking.remaining(userAnswers)

    val currentPage: Int = page.getOrElse(1)

    val totalPages: Int = ((numberOfPackagingDetails - 1) / pageSize) + 1

    val firstPackagingDetail: Int = ((currentPage - 1) * pageSize) + 1

    val lastPackagingDetail: Int = math.min(currentPage * pageSize, numberOfPackagingDetails)

    val firstPageNumber: Int = math.max(1, currentPage - 1)

    val lastPageNumber: Int = math.min(totalPages, currentPage + 1)

    AddAnotherPackagingDetailViewModel(
      numberOfPackagingDetails = numberOfPackagingDetails,
      remainingPackagingDetails = remainingPackagingDetails,
      allowMore = remainingPackagingDetails > 0,
      addedSingularOrPlural = if (numberOfPackagingDetails == 1) "singular" else "plural",
      remainingSingularOrPlural = if (remainingPackagingDetails == 1) "singular" else "plural",
      currentPage = currentPage,
      totalPages = totalPages,
      firstPackagingDetail = firstPackagingDetail,
      lastPackagingDetail = lastPackagingDetail,
      packagingDetailNumbers = firstPackagingDetail to lastPackagingDetail,
      pageNumbers = firstPageNumber to lastPageNumber
    )
  }
}
