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
import play.api.i18n.Messages
import play.api.test.Helpers
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.create.routes as createRoute
import uk.gov.hmrc.automatedexportsystemfrontend.helpers.SpecBase
import uk.gov.hmrc.automatedexportsystemfrontend.models.{CheckMode, NormalMode, PackingDetails, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.pages.create.DiscrepancyPackingPage
import uk.gov.hmrc.automatedexportsystemfrontend.queries.DiscrepancyPacking
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text

class AddAnotherPackagingDetailViewModelSpec extends SpecBase {

  private implicit val messages: Messages = Helpers.stubMessages()

  val maxPackagingDetails: Int = DiscrepancyPacking.maxPackagingDetails

  val userAnswers: UserAnswers = emptyUserAnswers.set(DiscrepancyPackingPage(1), PackingDetails("packagingCode1", 1, "shippingMarks1")).success.value

  val packagingDetailsListMax: List[PackingDetails] = (1 to maxPackagingDetails).map { number =>
    PackingDetails(s"packagingCode$number", number, s"shippingMarks$number")
  }.toList

  "AddAnotherPackagingDetailViewModel" - {

    "rows" - {
      "must build a valid row with packaging details, hidden value, change and remove actions" in {
        val result = AddAnotherPackagingDetailViewModel(userAnswers, None, NormalMode)
        val rows = result.rows
        rows.size mustBe 1
        val row = rows.head

        val actions = row.actions.value

        row.key.content mustBe Text(messages("addAnotherPackagingDetail.key", "1"))
        actions.items.size mustBe 2

        val change = actions.items.head
        change.href mustBe createRoute.DiscrepancyPackingController.onPageLoad(1, NormalMode).url
        change.content mustBe Text(messages("site.change"))
        change.visuallyHiddenText mustBe Some(messages("addAnotherPackagingDetail.change.hidden", "1"))

        val remove = actions.items(1)
        remove.href mustBe createRoute.RemovePackagingDetailController.onPageLoad(1, 1, NormalMode).url
        remove.content mustBe Text(messages("site.remove"))
        remove.visuallyHiddenText mustBe Some(messages("addAnotherPackagingDetail.remove.hidden", "1"))

        result.numberOfPackagingDetails mustBe 1
        result.remainingPackagingDetails mustBe 98
        result.allowMore mustBe true
        result.addedSingularOrPlural mustBe "singular"
        result.remainingSingularOrPlural mustBe "plural"
        result.currentPage mustBe 1
        result.totalPages mustBe 1
        result.firstPackagingDetail mustBe 1
        result.lastPackagingDetail mustBe 1
        result.pageNumbers mustBe (1 to 1)
      }

      "must build a valid row with packaging details, hidden value, change and remove actions in CheckMode" in {
        val result = AddAnotherPackagingDetailViewModel(userAnswers, None, CheckMode)
        val rows = result.rows
        rows.size mustBe 1
        val row = rows.head

        val actions = row.actions.value

        row.key.content mustBe Text(messages("addAnotherPackagingDetail.key", "1"))
        actions.items.size mustBe 2

        val change = actions.items.head
        change.href mustBe createRoute.DiscrepancyPackingController.onPageLoad(1, CheckMode).url
        change.content mustBe Text(messages("site.change"))
        change.visuallyHiddenText mustBe Some(messages("addAnotherPackagingDetail.change.hidden", "1"))

        val remove = actions.items(1)
        remove.href mustBe createRoute.RemovePackagingDetailController.onPageLoad(1, 1, CheckMode).url
        remove.content mustBe Text(messages("site.remove"))
        remove.visuallyHiddenText mustBe Some(messages("addAnotherPackagingDetail.remove.hidden", "1"))

        result.numberOfPackagingDetails mustBe 1
        result.remainingPackagingDetails mustBe 98
        result.allowMore mustBe true
        result.addedSingularOrPlural mustBe "singular"
        result.remainingSingularOrPlural mustBe "plural"
        result.currentPage mustBe 1
        result.totalPages mustBe 1
        result.firstPackagingDetail mustBe 1
        result.lastPackagingDetail mustBe 1
        result.pageNumbers mustBe (1 to 1)
      }
    }

    "when determining if add another should be enabled" - {
      "must provide the correct model when 1 item has been added" in {
        val result = AddAnotherPackagingDetailViewModel(userAnswers, None, NormalMode)

        result.numberOfPackagingDetails mustBe 1
        result.remainingPackagingDetails mustBe 98
        result.allowMore mustBe true
        result.addedSingularOrPlural mustBe "singular"
        result.remainingSingularOrPlural mustBe "plural"
        result.currentPage mustBe 1
        result.totalPages mustBe 1
        result.firstPackagingDetail mustBe 1
        result.lastPackagingDetail mustBe 1
        result.pageNumbers mustBe (1 to 1)
      }

      "must provide the correct model when 1 item has been added in CheckMode" in {
        val result = AddAnotherPackagingDetailViewModel(userAnswers, None, CheckMode)

        result.numberOfPackagingDetails mustBe 1
        result.remainingPackagingDetails mustBe 98
        result.allowMore mustBe true
        result.addedSingularOrPlural mustBe "singular"
        result.remainingSingularOrPlural mustBe "plural"
        result.currentPage mustBe 1
        result.totalPages mustBe 1
        result.firstPackagingDetail mustBe 1
        result.lastPackagingDetail mustBe 1
        result.pageNumbers mustBe (1 to 1)
      }

      "must provide the correct model when 1 more item can be added" in {
        val packingDetailsList = (1 until maxPackagingDetails).map { number =>
          PackingDetails(s"packagingCode$number", number, s"shippingMarks$number")
        }.toList

        val userAnswers = emptyUserAnswers
          .set(DiscrepancyPacking, packingDetailsList)
          .success
          .value

        val result = AddAnotherPackagingDetailViewModel(userAnswers, None, NormalMode)

        result.numberOfPackagingDetails mustBe 98
        result.remainingPackagingDetails mustBe 1
        result.allowMore mustBe true
        result.addedSingularOrPlural mustBe "plural"
        result.remainingSingularOrPlural mustBe "singular"
        result.currentPage mustBe 1
        result.totalPages mustBe 4
        result.firstPackagingDetail mustBe 1
        result.lastPackagingDetail mustBe 25
        result.pageNumbers mustBe (1 to 2)
      }

      "must provide the correct model when the maximum number of items has been reached" in {
        val userAnswers = emptyUserAnswers
          .set(DiscrepancyPacking, packagingDetailsListMax)
          .success
          .value

        val result = AddAnotherPackagingDetailViewModel(userAnswers, None, NormalMode)

        result.numberOfPackagingDetails mustBe 99
        result.remainingPackagingDetails mustBe 0
        result.allowMore mustBe false
        result.addedSingularOrPlural mustBe "plural"
        result.remainingSingularOrPlural mustBe "plural"
        result.currentPage mustBe 1
        result.totalPages mustBe 4
        result.firstPackagingDetail mustBe 1
        result.lastPackagingDetail mustBe 25
        result.pageNumbers mustBe (1 to 2)
      }
    }

    "when calculating pagination" - {
      "must provide the correct model before pagination starts" in {
        val packingDetailsList = (1 to 25).map { number =>
          PackingDetails(s"packagingCode$number", number, s"shippingMarks$number")
        }.toList

        val userAnswers = emptyUserAnswers
          .set(DiscrepancyPacking, packingDetailsList)
          .success
          .value

        val result = AddAnotherPackagingDetailViewModel(userAnswers, None, NormalMode)

        result.numberOfPackagingDetails mustBe 25
        result.remainingPackagingDetails mustBe 74
        result.allowMore mustBe true
        result.addedSingularOrPlural mustBe "plural"
        result.remainingSingularOrPlural mustBe "plural"
        result.currentPage mustBe 1
        result.totalPages mustBe 1
        result.firstPackagingDetail mustBe 1
        result.lastPackagingDetail mustBe 25
        result.pageNumbers mustBe (1 to 1)

        result.hasPreviousPage mustBe false
        result.hasNextPage mustBe false
        result.pageParam mustBe None
      }

      "must provide the correct model before pagination starts in CheckMode" in {
        val packingDetailsList = (1 to 25).map { number =>
          PackingDetails(s"packagingCode$number", number, s"shippingMarks$number")
        }.toList

        val userAnswers = emptyUserAnswers
          .set(DiscrepancyPacking, packingDetailsList)
          .success
          .value

        val result = AddAnotherPackagingDetailViewModel(userAnswers, None, CheckMode)

        result.numberOfPackagingDetails mustBe 25
        result.remainingPackagingDetails mustBe 74
        result.allowMore mustBe true
        result.addedSingularOrPlural mustBe "plural"
        result.remainingSingularOrPlural mustBe "plural"
        result.currentPage mustBe 1
        result.totalPages mustBe 1
        result.firstPackagingDetail mustBe 1
        result.lastPackagingDetail mustBe 25
        result.pageNumbers mustBe (1 to 1)

        result.hasPreviousPage mustBe false
        result.hasNextPage mustBe false
        result.pageParam mustBe None
      }

      "must provide the correct model for when pagination starts at 26 items added" in {
        val packingDetailsList = (1 to 26).map { number =>
          PackingDetails(s"packagingCode$number", number, s"shippingMarks$number")
        }.toList

        val userAnswers = emptyUserAnswers
          .set(DiscrepancyPacking, packingDetailsList)
          .success
          .value

        val result = AddAnotherPackagingDetailViewModel(userAnswers, None, NormalMode)

        result.numberOfPackagingDetails mustBe 26
        result.remainingPackagingDetails mustBe 73
        result.allowMore mustBe true
        result.addedSingularOrPlural mustBe "plural"
        result.remainingSingularOrPlural mustBe "plural"
        result.currentPage mustBe 1
        result.totalPages mustBe 2
        result.firstPackagingDetail mustBe 1
        result.lastPackagingDetail mustBe 25
        result.pageNumbers mustBe (1 to 2)

        result.hasPreviousPage mustBe false
        result.hasNextPage mustBe true
        result.nextPage mustBe 2
        result.pageParam mustBe Some(1)
      }

      "must provide the correct model for a final but partially completed page" in {
        val packingDetailsList = (1 to 26).map { number =>
          PackingDetails(s"packagingCode$number", number, s"shippingMarks$number")
        }.toList

        val userAnswers = emptyUserAnswers
          .set(DiscrepancyPacking, packingDetailsList)
          .success
          .value

        val result = AddAnotherPackagingDetailViewModel(userAnswers, Some(2), NormalMode)

        result.numberOfPackagingDetails mustBe 26
        result.remainingPackagingDetails mustBe 73
        result.allowMore mustBe true
        result.addedSingularOrPlural mustBe "plural"
        result.remainingSingularOrPlural mustBe "plural"
        result.currentPage mustBe 2
        result.totalPages mustBe 2
        result.firstPackagingDetail mustBe 26
        result.lastPackagingDetail mustBe 26
        result.pageNumbers mustBe (1 to 2)

        result.hasPreviousPage mustBe true
        result.hasNextPage mustBe false
        result.previousPage mustBe 1
        result.pageParam mustBe Some(2)
      }

      "must provide the correct model for page 2 at max" in {
        val userAnswers = emptyUserAnswers
          .set(DiscrepancyPacking, packagingDetailsListMax)
          .success
          .value

        val result = AddAnotherPackagingDetailViewModel(userAnswers, Some(2), NormalMode)

        result.numberOfPackagingDetails mustBe 99
        result.remainingPackagingDetails mustBe 0
        result.allowMore mustBe false
        result.addedSingularOrPlural mustBe "plural"
        result.remainingSingularOrPlural mustBe "plural"
        result.currentPage mustBe 2
        result.totalPages mustBe 4
        result.firstPackagingDetail mustBe 26
        result.lastPackagingDetail mustBe 50
        result.pageNumbers mustBe (1 to 3)

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

        val result = AddAnotherPackagingDetailViewModel(userAnswers, Some(3), NormalMode)

        result.numberOfPackagingDetails mustBe 99
        result.remainingPackagingDetails mustBe 0
        result.allowMore mustBe false
        result.addedSingularOrPlural mustBe "plural"
        result.remainingSingularOrPlural mustBe "plural"
        result.currentPage mustBe 3
        result.totalPages mustBe 4
        result.firstPackagingDetail mustBe 51
        result.lastPackagingDetail mustBe 75
        result.pageNumbers mustBe (2 to 4)

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

        val result = AddAnotherPackagingDetailViewModel(userAnswers, Some(4), NormalMode)

        result.numberOfPackagingDetails mustBe 99
        result.remainingPackagingDetails mustBe 0
        result.allowMore mustBe false
        result.addedSingularOrPlural mustBe "plural"
        result.remainingSingularOrPlural mustBe "plural"
        result.currentPage mustBe 4
        result.totalPages mustBe 4
        result.firstPackagingDetail mustBe 76
        result.lastPackagingDetail mustBe 99
        result.pageNumbers mustBe (3 to 4)

        result.hasPreviousPage mustBe true
        result.hasNextPage mustBe false
        result.previousPage mustBe 3
      }
    }
  }
}
