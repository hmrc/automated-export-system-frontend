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
import uk.gov.hmrc.automatedexportsystemfrontend.models.{CheckMode, ContainerDetails, NormalMode, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.pages.create.DiscrepancyTransportPage
import uk.gov.hmrc.automatedexportsystemfrontend.queries.DiscrepancyTransportEquipment
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.pagination.PaginationItem

class AddAnotherTransportEquipmentViewModelSpec extends SpecBase {

  private implicit val messages: Messages = Helpers.stubMessages()

  val userAnswers: UserAnswers = emptyUserAnswers.set(DiscrepancyTransportPage(1), ContainerDetails(Some("containerId1"), Some(1))).get

  val maxTransportEquipment: Int = DiscrepancyTransportEquipment.maxTransportEquipment

  val transportEquipmentListMax: List[ContainerDetails] = (1 to maxTransportEquipment).map { number =>
    ContainerDetails(Some(s"containerId${number.toString}"), Some(number))
  }.toList

  "AddAnotherTransportEquipmentViewModel" - {

    ".rows" - {
      "must build a valid row with transport equipment, hidden value, change and remove actions" in {
        val result = AddAnotherTransportEquipmentViewModel(userAnswers, None, NormalMode)
        val rows = result.rows
        rows.size mustBe 1
        val row = rows.head

        val actions = row.actions.value

        row.key.content mustBe Text(messages("addAnotherTransportEquipment.key", "1"))
        actions.items.size mustBe 2

        val change = actions.items.head
        change.href mustBe createRoute.DiscrepancyTransportController.onPageLoad(1, NormalMode).url
        change.content mustBe Text(messages("site.change"))
        change.visuallyHiddenText mustBe Some(messages("addAnotherTransportEquipment.change.hidden", "1"))

        val remove = actions.items(1)
        remove.href mustBe createRoute.RemoveTransportEquipmentController.onPageLoad(1, 1, NormalMode).url
        remove.content mustBe Text(messages("site.remove"))
        remove.visuallyHiddenText mustBe Some(messages("addAnotherTransportEquipment.remove.hidden", "1"))

        result.numberOfTransportEquipment mustBe 1
        result.remainingTransportEquipment mustBe 9998
        result.allowMore mustBe true
        result.addedSingularOrPlural mustBe "singular"
        result.remainingSingularOrPlural mustBe "plural"
        result.currentPage mustBe 1
        result.totalPages mustBe 1
        result.firstTransportEquipment mustBe 1
        result.lastTransportEquipment mustBe 1
        result.pagination mustBe None
      }

      "must build a valid row with transport equipment, hidden value, change and remove actions in CheckMode" in {
        val result = AddAnotherTransportEquipmentViewModel(userAnswers, None, CheckMode)
        val rows = result.rows
        rows.size mustBe 1
        val row = rows.head

        val actions = row.actions.value

        row.key.content mustBe Text(messages("addAnotherTransportEquipment.key", "1"))
        actions.items.size mustBe 2

        val change = actions.items.head
        change.href mustBe createRoute.DiscrepancyTransportController.onPageLoad(1, CheckMode).url
        change.content mustBe Text(messages("site.change"))
        change.visuallyHiddenText mustBe Some(messages("addAnotherTransportEquipment.change.hidden", "1"))

        val remove = actions.items(1)
        remove.href mustBe createRoute.RemoveTransportEquipmentController.onPageLoad(1, 1, CheckMode).url
        remove.content mustBe Text(messages("site.remove"))
        remove.visuallyHiddenText mustBe Some(messages("addAnotherTransportEquipment.remove.hidden", "1"))

        result.numberOfTransportEquipment mustBe 1
        result.remainingTransportEquipment mustBe 9998
        result.allowMore mustBe true
        result.addedSingularOrPlural mustBe "singular"
        result.remainingSingularOrPlural mustBe "plural"
        result.currentPage mustBe 1
        result.totalPages mustBe 1
        result.firstTransportEquipment mustBe 1
        result.lastTransportEquipment mustBe 1
        result.pagination mustBe None
      }

      "must be empty there are no transport equipment entries" in {
        AddAnotherTransportEquipmentViewModel(emptyUserAnswers, None, NormalMode).rows mustBe Seq.empty
      }

      "must be empty when there are no transport equipment entries in CheckMode" in {
        AddAnotherTransportEquipmentViewModel(emptyUserAnswers, None, CheckMode).rows mustBe Seq.empty
      }
    }

    ".pagination" - {
      "must return None when there is 25 items or less" in {
        val transportEquipmentList: List[ContainerDetails] = (1 to 25).map { number =>
          ContainerDetails(Some(s"containerId${number.toString}"), Some(number))
        }.toList

        val userAnswers = emptyUserAnswers
          .set(DiscrepancyTransportEquipment, transportEquipmentList)
          .success
          .value

        val pagination = AddAnotherTransportEquipmentViewModel(userAnswers, None, NormalMode).pagination

        pagination mustBe None
      }

      "must return None when there is 25 items or less in CheckMode" in {
        val transportEquipmentList: List[ContainerDetails] = (1 to 25).map { number =>
          ContainerDetails(Some(s"containerId${number.toString}"), Some(number))
        }.toList

        val userAnswers = emptyUserAnswers
          .set(DiscrepancyTransportEquipment, transportEquipmentList)
          .success
          .value

        val pagination = AddAnotherTransportEquipmentViewModel(userAnswers, None, CheckMode).pagination

        pagination mustBe None
      }

      "must return Pagination for page 1 when there is 26 items" in {
        val transportEquipmentList: List[ContainerDetails] = (1 to 26).map { number =>
          ContainerDetails(Some(s"containerId${number.toString}"), Some(number))
        }.toList

        val userAnswers = emptyUserAnswers
          .set(DiscrepancyTransportEquipment, transportEquipmentList)
          .success
          .value

        val pagination = AddAnotherTransportEquipmentViewModel(userAnswers, Some(1), NormalMode).pagination.value

        pagination.items mustBe Some(
          Seq(
            PaginationItem(
              href = createRoute.AddAnotherTransportController.onPageLoad(Some(1), NormalMode).url,
              number = Some("1"),
              current = Some(true)
            ),
            PaginationItem(href = createRoute.AddAnotherTransportController.onPageLoad(Some(2), NormalMode).url, number = Some("2"))
          )
        )
        pagination.previous mustBe None
        pagination.next.value.href mustBe createRoute.AddAnotherTransportController.onPageLoad(Some(2), NormalMode).url
      }

      "must return Pagination for page 1 when there is 26 items in CheckMode" in {
        val transportEquipmentList: List[ContainerDetails] = (1 to 26).map { number =>
          ContainerDetails(Some(s"containerId${number.toString}"), Some(number))
        }.toList

        val userAnswers = emptyUserAnswers
          .set(DiscrepancyTransportEquipment, transportEquipmentList)
          .success
          .value

        val pagination = AddAnotherTransportEquipmentViewModel(userAnswers, Some(1), CheckMode).pagination.value

        pagination.items mustBe Some(
          Seq(
            PaginationItem(
              href = createRoute.AddAnotherTransportController.onPageLoad(Some(1), CheckMode).url,
              number = Some("1"),
              current = Some(true)
            ),
            PaginationItem(href = createRoute.AddAnotherTransportController.onPageLoad(Some(2), CheckMode).url, number = Some("2"))
          )
        )
        pagination.previous mustBe None
        pagination.next.value.href mustBe createRoute.AddAnotherTransportController.onPageLoad(Some(2), CheckMode).url
      }

      "must return Pagination for page 2 when there is 26 items" in {
        val transportEquipmentList: List[ContainerDetails] = (1 to 26).map { number =>
          ContainerDetails(Some(s"containerId${number.toString}"), Some(number))
        }.toList

        val userAnswers = emptyUserAnswers
          .set(DiscrepancyTransportEquipment, transportEquipmentList)
          .success
          .value

        val pagination = AddAnotherTransportEquipmentViewModel(userAnswers, Some(2), NormalMode).pagination.value

        pagination.items mustBe Some(
          Seq(
            PaginationItem(href = createRoute.AddAnotherTransportController.onPageLoad(Some(1), NormalMode).url, number = Some("1")),
            PaginationItem(
              href = createRoute.AddAnotherTransportController.onPageLoad(Some(2), NormalMode).url,
              number = Some("2"),
              current = Some(true)
            )
          )
        )
        pagination.previous.value.href mustBe createRoute.AddAnotherTransportController.onPageLoad(Some(1), NormalMode).url
        pagination.next mustBe None
      }

      "must return Pagination when there is 9999 items" - {
        val userAnswers = emptyUserAnswers
          .set(DiscrepancyTransportEquipment, transportEquipmentListMax)
          .success
          .value

        "for page 1" in {
          val pagination = AddAnotherTransportEquipmentViewModel(userAnswers, Some(1), NormalMode).pagination.value

          pagination.items mustBe Some(
            Seq(
              PaginationItem(
                href = createRoute.AddAnotherTransportController.onPageLoad(Some(1), NormalMode).url,
                number = Some("1"),
                current = Some(true)
              ),
              PaginationItem(href = createRoute.AddAnotherTransportController.onPageLoad(Some(2), NormalMode).url, number = Some("2")),
              PaginationItem(ellipsis = Some(true)),
              PaginationItem(href = createRoute.AddAnotherTransportController.onPageLoad(Some(400), NormalMode).url, number = Some("400"))
            )
          )
          pagination.previous mustBe None
          pagination.next.value.href mustBe createRoute.AddAnotherTransportController.onPageLoad(Some(2), NormalMode).url
        }

        "for page 4" in {
          val pagination = AddAnotherTransportEquipmentViewModel(userAnswers, Some(4), NormalMode).pagination.value

          pagination.items mustBe Some(
            Seq(
              PaginationItem(href = createRoute.AddAnotherTransportController.onPageLoad(Some(1), NormalMode).url, number = Some("1")),
              PaginationItem(href = createRoute.AddAnotherTransportController.onPageLoad(Some(2), NormalMode).url, number = Some("2")),
              PaginationItem(href = createRoute.AddAnotherTransportController.onPageLoad(Some(3), NormalMode).url, number = Some("3")),
              PaginationItem(
                href = createRoute.AddAnotherTransportController.onPageLoad(Some(4), NormalMode).url,
                number = Some("4"),
                current = Some(true)
              ),
              PaginationItem(href = createRoute.AddAnotherTransportController.onPageLoad(Some(5), NormalMode).url, number = Some("5")),
              PaginationItem(ellipsis = Some(true)),
              PaginationItem(href = createRoute.AddAnotherTransportController.onPageLoad(Some(400), NormalMode).url, number = Some("400"))
            )
          )
          pagination.previous.value.href mustBe createRoute.AddAnotherTransportController.onPageLoad(Some(3), NormalMode).url
          pagination.next.value.href mustBe createRoute.AddAnotherTransportController.onPageLoad(Some(5), NormalMode).url
        }

        "for page 5" in {
          val pagination = AddAnotherTransportEquipmentViewModel(userAnswers, Some(5), NormalMode).pagination.value

          pagination.items mustBe Some(
            Seq(
              PaginationItem(href = createRoute.AddAnotherTransportController.onPageLoad(Some(1), NormalMode).url, number = Some("1")),
              PaginationItem(ellipsis = Some(true)),
              PaginationItem(href = createRoute.AddAnotherTransportController.onPageLoad(Some(4), NormalMode).url, number = Some("4")),
              PaginationItem(
                href = createRoute.AddAnotherTransportController.onPageLoad(Some(5), NormalMode).url,
                number = Some("5"),
                current = Some(true)
              ),
              PaginationItem(href = createRoute.AddAnotherTransportController.onPageLoad(Some(6), NormalMode).url, number = Some("6")),
              PaginationItem(ellipsis = Some(true)),
              PaginationItem(href = createRoute.AddAnotherTransportController.onPageLoad(Some(400), NormalMode).url, number = Some("400"))
            )
          )
          pagination.previous.value.href mustBe createRoute.AddAnotherTransportController.onPageLoad(Some(4), NormalMode).url
          pagination.next.value.href mustBe createRoute.AddAnotherTransportController.onPageLoad(Some(6), NormalMode).url
        }

        "for page 200" in {
          val pagination = AddAnotherTransportEquipmentViewModel(userAnswers, Some(200), NormalMode).pagination.value

          pagination.items mustBe Some(
            Seq(
              PaginationItem(href = createRoute.AddAnotherTransportController.onPageLoad(Some(1), NormalMode).url, number = Some("1")),
              PaginationItem(ellipsis = Some(true)),
              PaginationItem(href = createRoute.AddAnotherTransportController.onPageLoad(Some(199), NormalMode).url, number = Some("199")),
              PaginationItem(
                href = createRoute.AddAnotherTransportController.onPageLoad(Some(200), NormalMode).url,
                number = Some("200"),
                current = Some(true)
              ),
              PaginationItem(href = createRoute.AddAnotherTransportController.onPageLoad(Some(201), NormalMode).url, number = Some("201")),
              PaginationItem(ellipsis = Some(true)),
              PaginationItem(href = createRoute.AddAnotherTransportController.onPageLoad(Some(400), NormalMode).url, number = Some("400"))
            )
          )
          pagination.previous.value.href mustBe createRoute.AddAnotherTransportController.onPageLoad(Some(199), NormalMode).url
          pagination.next.value.href mustBe createRoute.AddAnotherTransportController.onPageLoad(Some(201), NormalMode).url
        }

        "for page 396" in {
          val pagination = AddAnotherTransportEquipmentViewModel(userAnswers, Some(396), NormalMode).pagination.value

          pagination.items mustBe Some(
            Seq(
              PaginationItem(href = createRoute.AddAnotherTransportController.onPageLoad(Some(1), NormalMode).url, number = Some("1")),
              PaginationItem(ellipsis = Some(true)),
              PaginationItem(href = createRoute.AddAnotherTransportController.onPageLoad(Some(395), NormalMode).url, number = Some("395")),
              PaginationItem(
                href = createRoute.AddAnotherTransportController.onPageLoad(Some(396), NormalMode).url,
                number = Some("396"),
                current = Some(true)
              ),
              PaginationItem(href = createRoute.AddAnotherTransportController.onPageLoad(Some(397), NormalMode).url, number = Some("397")),
              PaginationItem(ellipsis = Some(true)),
              PaginationItem(href = createRoute.AddAnotherTransportController.onPageLoad(Some(400), NormalMode).url, number = Some("400"))
            )
          )
          pagination.previous.value.href mustBe createRoute.AddAnotherTransportController.onPageLoad(Some(395), NormalMode).url
          pagination.next.value.href mustBe createRoute.AddAnotherTransportController.onPageLoad(Some(397), NormalMode).url
        }

        "for page 397" in {
          val pagination = AddAnotherTransportEquipmentViewModel(userAnswers, Some(397), NormalMode).pagination.value

          pagination.items mustBe Some(
            Seq(
              PaginationItem(href = createRoute.AddAnotherTransportController.onPageLoad(Some(1), NormalMode).url, number = Some("1")),
              PaginationItem(ellipsis = Some(true)),
              PaginationItem(href = createRoute.AddAnotherTransportController.onPageLoad(Some(396), NormalMode).url, number = Some("396")),
              PaginationItem(
                href = createRoute.AddAnotherTransportController.onPageLoad(Some(397), NormalMode).url,
                number = Some("397"),
                current = Some(true)
              ),
              PaginationItem(href = createRoute.AddAnotherTransportController.onPageLoad(Some(398), NormalMode).url, number = Some("398")),
              PaginationItem(href = createRoute.AddAnotherTransportController.onPageLoad(Some(399), NormalMode).url, number = Some("399")),
              PaginationItem(href = createRoute.AddAnotherTransportController.onPageLoad(Some(400), NormalMode).url, number = Some("400"))
            )
          )
          pagination.previous.value.href mustBe createRoute.AddAnotherTransportController.onPageLoad(Some(396), NormalMode).url
          pagination.next.value.href mustBe createRoute.AddAnotherTransportController.onPageLoad(Some(398), NormalMode).url
        }

        "for page 400" in {
          val pagination = AddAnotherTransportEquipmentViewModel(userAnswers, Some(400), NormalMode).pagination.value

          pagination.items mustBe Some(
            Seq(
              PaginationItem(href = createRoute.AddAnotherTransportController.onPageLoad(Some(1), NormalMode).url, number = Some("1")),
              PaginationItem(ellipsis = Some(true)),
              PaginationItem(href = createRoute.AddAnotherTransportController.onPageLoad(Some(399), NormalMode).url, number = Some("399")),
              PaginationItem(
                href = createRoute.AddAnotherTransportController.onPageLoad(Some(400), NormalMode).url,
                number = Some("400"),
                current = Some(true)
              )
            )
          )
          pagination.previous.value.href mustBe createRoute.AddAnotherTransportController.onPageLoad(Some(399), NormalMode).url
          pagination.next mustBe None
        }
      }
    }

    "when determining if add another should be enabled" - {
      "must provide the correct model when 1 item has been added" in {
        val result = AddAnotherTransportEquipmentViewModel(userAnswers, None, NormalMode)

        result.numberOfTransportEquipment mustBe 1
        result.remainingTransportEquipment mustBe 9998
        result.allowMore mustBe true
        result.addedSingularOrPlural mustBe "singular"
        result.remainingSingularOrPlural mustBe "plural"
        result.currentPage mustBe 1
        result.totalPages mustBe 1
        result.firstTransportEquipment mustBe 1
        result.lastTransportEquipment mustBe 1
        result.rows.size mustBe 1
        result.pagination mustBe None
      }

      "must provide the correct model when 1 item has been added in CheckMode" in {
        val result = AddAnotherTransportEquipmentViewModel(userAnswers, None, CheckMode)

        result.numberOfTransportEquipment mustBe 1
        result.remainingTransportEquipment mustBe 9998
        result.allowMore mustBe true
        result.addedSingularOrPlural mustBe "singular"
        result.remainingSingularOrPlural mustBe "plural"
        result.currentPage mustBe 1
        result.totalPages mustBe 1
        result.firstTransportEquipment mustBe 1
        result.lastTransportEquipment mustBe 1
        result.rows.size mustBe 1
        result.pagination mustBe None
      }

      "must provide the correct model when 1 more item can be added" in {
        val transportEquipmentList: List[ContainerDetails] = (1 until maxTransportEquipment).map { number =>
          ContainerDetails(Some(s"containerId${number.toString}"), Some(number))
        }.toList

        val userAnswers = emptyUserAnswers
          .set(DiscrepancyTransportEquipment, transportEquipmentList)
          .success
          .value

        val result = AddAnotherTransportEquipmentViewModel(userAnswers, None, NormalMode)

        result.numberOfTransportEquipment mustBe 9998
        result.remainingTransportEquipment mustBe 1
        result.allowMore mustBe true
        result.addedSingularOrPlural mustBe "plural"
        result.remainingSingularOrPlural mustBe "singular"
        result.currentPage mustBe 1
        result.totalPages mustBe 400
        result.firstTransportEquipment mustBe 1
        result.lastTransportEquipment mustBe 25
        result.rows.size mustBe 25
        result.pagination.isDefined mustBe true
      }

      "must provide the correct model when the maximum number of items has been reached" in {
        val userAnswers = emptyUserAnswers
          .set(DiscrepancyTransportEquipment, transportEquipmentListMax)
          .success
          .value

        val result = AddAnotherTransportEquipmentViewModel(userAnswers, None, NormalMode)

        result.numberOfTransportEquipment mustBe 9999
        result.remainingTransportEquipment mustBe 0
        result.allowMore mustBe false
        result.addedSingularOrPlural mustBe "plural"
        result.remainingSingularOrPlural mustBe "plural"
        result.currentPage mustBe 1
        result.totalPages mustBe 400
        result.firstTransportEquipment mustBe 1
        result.lastTransportEquipment mustBe 25
        result.rows.size mustBe 25
        result.pagination.isDefined mustBe true
      }
    }

    "must provide the correct model before pagination starts" in {
      val transportEquipmentList: List[ContainerDetails] = (1 to 25).map { number =>
        ContainerDetails(Some(s"containerId${number.toString}"), Some(number))
      }.toList

      val userAnswers = emptyUserAnswers
        .set(DiscrepancyTransportEquipment, transportEquipmentList)
        .success
        .value

      val result = AddAnotherTransportEquipmentViewModel(userAnswers, None, NormalMode)

      result.numberOfTransportEquipment mustBe 25
      result.remainingTransportEquipment mustBe 9974
      result.allowMore mustBe true
      result.addedSingularOrPlural mustBe "plural"
      result.remainingSingularOrPlural mustBe "plural"
      result.currentPage mustBe 1
      result.totalPages mustBe 1
      result.firstTransportEquipment mustBe 1
      result.lastTransportEquipment mustBe 25
      result.rows.size mustBe 25
      result.pagination mustBe None

      result.hasPreviousPage mustBe false
      result.hasNextPage mustBe false
      result.pageParam mustBe None
    }

    "must provide the correct model before pagination starts in CheckMode" in {
      val transportEquipmentList: List[ContainerDetails] = (1 to 25).map { number =>
        ContainerDetails(Some(s"containerId${number.toString}"), Some(number))
      }.toList

      val userAnswers = emptyUserAnswers
        .set(DiscrepancyTransportEquipment, transportEquipmentList)
        .success
        .value

      val result = AddAnotherTransportEquipmentViewModel(userAnswers, None, CheckMode)

      result.numberOfTransportEquipment mustBe 25
      result.remainingTransportEquipment mustBe 9974
      result.allowMore mustBe true
      result.addedSingularOrPlural mustBe "plural"
      result.remainingSingularOrPlural mustBe "plural"
      result.currentPage mustBe 1
      result.totalPages mustBe 1
      result.firstTransportEquipment mustBe 1
      result.lastTransportEquipment mustBe 25
      result.rows.size mustBe 25
      result.pagination mustBe None

      result.hasPreviousPage mustBe false
      result.hasNextPage mustBe false
      result.pageParam mustBe None
    }

    "must provide the correct model for when pagination starts at 26 items added" in {
      val transportEquipmentList: List[ContainerDetails] = (1 to 26).map { number =>
        ContainerDetails(Some(s"containerId${number.toString}"), Some(number))
      }.toList

      val userAnswers = emptyUserAnswers
        .set(DiscrepancyTransportEquipment, transportEquipmentList)
        .success
        .value

      val result = AddAnotherTransportEquipmentViewModel(userAnswers, None, NormalMode)

      result.numberOfTransportEquipment mustBe 26
      result.remainingTransportEquipment mustBe 9973
      result.allowMore mustBe true
      result.addedSingularOrPlural mustBe "plural"
      result.remainingSingularOrPlural mustBe "plural"
      result.currentPage mustBe 1
      result.totalPages mustBe 2
      result.firstTransportEquipment mustBe 1
      result.lastTransportEquipment mustBe 25
      result.rows.size mustBe 25
      result.pagination.isDefined mustBe true

      result.hasPreviousPage mustBe false
      result.hasNextPage mustBe true
      result.nextPage mustBe 2
      result.pageParam mustBe Some(1)
    }

    "must provide the correct model for a final but partially completed page" in {
      val transportEquipmentList: List[ContainerDetails] = (1 to 26).map { number =>
        ContainerDetails(Some(s"containerId${number.toString}"), Some(number))
      }.toList

      val userAnswers = emptyUserAnswers
        .set(DiscrepancyTransportEquipment, transportEquipmentList)
        .success
        .value

      val result = AddAnotherTransportEquipmentViewModel(userAnswers, Some(2), NormalMode)

      result.numberOfTransportEquipment mustBe 26
      result.remainingTransportEquipment mustBe 9973
      result.allowMore mustBe true
      result.addedSingularOrPlural mustBe "plural"
      result.remainingSingularOrPlural mustBe "plural"
      result.currentPage mustBe 2
      result.totalPages mustBe 2
      result.firstTransportEquipment mustBe 26
      result.lastTransportEquipment mustBe 26
      result.rows.size mustBe 1
      result.pagination.isDefined mustBe true

      result.hasPreviousPage mustBe true
      result.hasNextPage mustBe false
      result.previousPage mustBe 1
      result.pageParam mustBe Some(2)
    }
  }
}
