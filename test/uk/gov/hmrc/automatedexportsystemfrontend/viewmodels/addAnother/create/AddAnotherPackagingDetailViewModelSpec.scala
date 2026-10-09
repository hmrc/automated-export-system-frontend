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

import org.scalatest.matchers.must.Matchers.mustBe
import uk.gov.hmrc.automatedexportsystemfrontend.helpers.SpecBase
import uk.gov.hmrc.automatedexportsystemfrontend.models.PackingDetails
import uk.gov.hmrc.automatedexportsystemfrontend.pages.create.DiscrepancyPackingPage
import uk.gov.hmrc.automatedexportsystemfrontend.queries.DiscrepancyPacking

class AddAnotherPackagingDetailViewModelSpec extends SpecBase {

  val maxPackagingDetails: Int = DiscrepancyPacking.maxPackagingDetails

  val packagingDetailsListMax: List[PackingDetails] = (1 to maxPackagingDetails).map { number =>
    PackingDetails(s"packagingCode$number", number, s"shippingMarks$number")
  }.toList

  "AddAnotherPackagingDetailViewModel" - {

    "when determining if add another should be enabled" - {
      "must provide the correct model when 1 item has been added" in {
        val userAnswers = emptyUserAnswers.set(DiscrepancyPackingPage(1), PackingDetails("packagingCode1", 1, "shippingMarks1")).success.value

        val result = AddAnotherPackagingDetailViewModel(userAnswers, None)

        result mustBe AddAnotherPackagingDetailViewModel(
          numberOfPackagingDetails = 1,
          remainingPackagingDetails = 98,
          allowMore = true,
          addedSingularOrPlural = "singular",
          remainingSingularOrPlural = "plural",
          currentPage = 1,
          totalPages = 1,
          firstPackagingDetail = 1,
          lastPackagingDetail = 1,
          packagingDetailNumbers = 1 to 1,
          pageNumbers = 1 to 1
        )
      }

      "must provide the correct model when 1 more item can be added" in {
        val packingDetailsList = (1 until maxPackagingDetails).map { number =>
          PackingDetails(s"packagingCode$number", number, s"shippingMarks$number")
        }.toList

        val userAnswers = emptyUserAnswers
          .set(DiscrepancyPacking, packingDetailsList)
          .success
          .value

        val result = AddAnotherPackagingDetailViewModel(userAnswers, None)

        result mustBe AddAnotherPackagingDetailViewModel(
          numberOfPackagingDetails = 98,
          remainingPackagingDetails = 1,
          allowMore = true,
          addedSingularOrPlural = "plural",
          remainingSingularOrPlural = "singular",
          currentPage = 1,
          totalPages = 4,
          firstPackagingDetail = 1,
          lastPackagingDetail = 25,
          packagingDetailNumbers = 1 to 25,
          pageNumbers = 1 to 2
        )
      }

      "must provide the correct model when the maximum number of items has been reached" in {
        val userAnswers = emptyUserAnswers
          .set(DiscrepancyPacking, packagingDetailsListMax)
          .success
          .value

        val result = AddAnotherPackagingDetailViewModel(userAnswers, None)

        result mustBe AddAnotherPackagingDetailViewModel(
          numberOfPackagingDetails = 99,
          remainingPackagingDetails = 0,
          allowMore = false,
          addedSingularOrPlural = "plural",
          remainingSingularOrPlural = "plural",
          currentPage = 1,
          totalPages = 4,
          firstPackagingDetail = 1,
          lastPackagingDetail = 25,
          packagingDetailNumbers = 1 to 25,
          pageNumbers = 1 to 2
        )
      }
    }

    "when calculating pagination" - {
      "must provide the correct model for when pagination starts at 26 items added" in {
        val packingDetailsList = (1 to 26).map { number =>
          PackingDetails(s"packagingCode$number", number, s"shippingMarks$number")
        }.toList

        val userAnswers = emptyUserAnswers
          .set(DiscrepancyPacking, packingDetailsList)
          .success
          .value

        val result = AddAnotherPackagingDetailViewModel(userAnswers, None)

        result mustBe AddAnotherPackagingDetailViewModel(
          numberOfPackagingDetails = 26,
          remainingPackagingDetails = 73,
          allowMore = true,
          addedSingularOrPlural = "plural",
          remainingSingularOrPlural = "plural",
          currentPage = 1,
          totalPages = 2,
          firstPackagingDetail = 1,
          lastPackagingDetail = 25,
          packagingDetailNumbers = 1 to 25,
          pageNumbers = 1 to 2
        )

        result.hasPreviousPage mustBe false
        result.hasNextPage mustBe true
        result.nextPage mustBe 2
      }

      "must provide the correct model for a final but partially completed page" in {
        val packingDetailsList = (1 to 26).map { number =>
          PackingDetails(s"packagingCode$number", number, s"shippingMarks$number")
        }.toList

        val userAnswers = emptyUserAnswers
          .set(DiscrepancyPacking, packingDetailsList)
          .success
          .value

        val result = AddAnotherPackagingDetailViewModel(userAnswers, Some(2))

        result mustBe AddAnotherPackagingDetailViewModel(
          numberOfPackagingDetails = 26,
          remainingPackagingDetails = 73,
          allowMore = true,
          addedSingularOrPlural = "plural",
          remainingSingularOrPlural = "plural",
          currentPage = 2,
          totalPages = 2,
          firstPackagingDetail = 26,
          lastPackagingDetail = 26,
          packagingDetailNumbers = 26 to 26,
          pageNumbers = 1 to 2
        )

        result.hasPreviousPage mustBe true
        result.hasNextPage mustBe false
        result.previousPage mustBe 1
      }

      "must provide the correct model for page 2 at max" in {
        val userAnswers = emptyUserAnswers
          .set(DiscrepancyPacking, packagingDetailsListMax)
          .success
          .value

        val result = AddAnotherPackagingDetailViewModel(userAnswers, Some(2))

        result mustBe AddAnotherPackagingDetailViewModel(
          numberOfPackagingDetails = 99,
          remainingPackagingDetails = 0,
          allowMore = false,
          addedSingularOrPlural = "plural",
          remainingSingularOrPlural = "plural",
          currentPage = 2,
          totalPages = 4,
          firstPackagingDetail = 26,
          lastPackagingDetail = 50,
          packagingDetailNumbers = 26 to 50,
          pageNumbers = 1 to 3
        )

        result.hasPreviousPage mustBe true
        result.hasNextPage mustBe true
        result.previousPage mustBe 1
        result.nextPage mustBe 3
      }

      "must provide the correct model for page 3 at max" in {
        val userAnswers = emptyUserAnswers
          .set(DiscrepancyPacking, packagingDetailsListMax)
          .success
          .value

        val result = AddAnotherPackagingDetailViewModel(userAnswers, Some(3))

        result mustBe AddAnotherPackagingDetailViewModel(
          numberOfPackagingDetails = 99,
          remainingPackagingDetails = 0,
          allowMore = false,
          addedSingularOrPlural = "plural",
          remainingSingularOrPlural = "plural",
          currentPage = 3,
          totalPages = 4,
          firstPackagingDetail = 51,
          lastPackagingDetail = 75,
          packagingDetailNumbers = 51 to 75,
          pageNumbers = 2 to 4
        )

        result.hasPreviousPage mustBe true
        result.hasNextPage mustBe true
        result.previousPage mustBe 2
        result.nextPage mustBe 4
      }

      "must provide the correct model for page 4 at max" in {
        val userAnswers = emptyUserAnswers
          .set(DiscrepancyPacking, packagingDetailsListMax)
          .success
          .value

        val result = AddAnotherPackagingDetailViewModel(userAnswers, Some(4))

        result mustBe AddAnotherPackagingDetailViewModel(
          numberOfPackagingDetails = 99,
          remainingPackagingDetails = 0,
          allowMore = false,
          addedSingularOrPlural = "plural",
          remainingSingularOrPlural = "plural",
          currentPage = 4,
          totalPages = 4,
          firstPackagingDetail = 76,
          lastPackagingDetail = 99,
          packagingDetailNumbers = 76 to 99,
          pageNumbers = 3 to 4
        )

        result.hasPreviousPage mustBe true
        result.hasNextPage mustBe false
        result.previousPage mustBe 3
      }
    }
  }
}
