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
import uk.gov.hmrc.automatedexportsystemfrontend.models.{CheckMode, NormalMode, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.pages.create.DiscrepancyReferencePage
import uk.gov.hmrc.automatedexportsystemfrontend.queries.DiscrepancyReference
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.pagination.PaginationItem

class AddAnotherGoodsReferenceViewModelSpec extends SpecBase {

  private implicit val messages: Messages = Helpers.stubMessages()

  val transportEquipmentIndex: Int = 1

  val userAnswers: UserAnswers = emptyUserAnswers.set(DiscrepancyReferencePage(transportEquipmentIndex, 1), "12345").get

  val maxGoodsReferences: Int = DiscrepancyReference(transportEquipmentIndex).maxDiscrepancyReferences

  val goodsReferencesListMax: List[String] = (1 to maxGoodsReferences).map { number =>
    number.toString
  }.toList

  "AddAnotherGoodsReferenceViewModel" - {

    ".rows" - {
      "must build a valid row with goods reference, hidden value, change and remove actions" in {
        val result = AddAnotherGoodsReferenceViewModel(userAnswers, transportEquipmentIndex, None, NormalMode)
        val rows = result.rows
        rows.size mustBe 1
        val row = rows.head

        val actions = row.actions.value

        row.key.content mustBe Text(messages("addAnotherGoodsReference.key", "12345"))
        actions.items.size mustBe 2

        val change = actions.items.head
        change.href mustBe createRoute.DiscrepancyReferenceController.onPageLoad(transportEquipmentIndex, 1, NormalMode).url
        change.content mustBe Text(messages("site.change"))
        change.visuallyHiddenText mustBe Some(messages("addAnotherGoodsReference.change.hidden", "12345"))

        val remove = actions.items(1)
        remove.href mustBe createRoute.RemoveGoodsReferenceController.onPageLoad(transportEquipmentIndex, 1, 1, NormalMode).url
        remove.content mustBe Text(messages("site.remove"))
        remove.visuallyHiddenText mustBe Some(messages("addAnotherGoodsReference.remove.hidden", "12345"))

        result.numberOfGoodsReferences mustBe 1
        result.remainingGoodsReferences mustBe 998
        result.allowMore mustBe true
        result.addedSingularOrPlural mustBe "singular"
        result.remainingSingularOrPlural mustBe "plural"
        result.currentPage mustBe 1
        result.totalPages mustBe 1
        result.firstGoodsReference mustBe 1
        result.lastGoodsReference mustBe 1
        result.pagination mustBe None
      }

      "must build a valid row with goods reference, hidden value, change and remove actions in CheckMode" in {
        val result = AddAnotherGoodsReferenceViewModel(userAnswers, transportEquipmentIndex, None, CheckMode)
        val rows = result.rows
        rows.size mustBe 1
        val row = rows.head

        val actions = row.actions.value

        row.key.content mustBe Text(messages("addAnotherGoodsReference.key", "12345"))
        actions.items.size mustBe 2

        val change = actions.items.head
        change.href mustBe createRoute.DiscrepancyReferenceController.onPageLoad(transportEquipmentIndex, 1, CheckMode).url
        change.content mustBe Text(messages("site.change"))
        change.visuallyHiddenText mustBe Some(messages("addAnotherGoodsReference.change.hidden", "12345"))

        val remove = actions.items(1)
        remove.href mustBe createRoute.RemoveGoodsReferenceController.onPageLoad(transportEquipmentIndex, 1, 1, CheckMode).url
        remove.content mustBe Text(messages("site.remove"))
        remove.visuallyHiddenText mustBe Some(messages("addAnotherGoodsReference.remove.hidden", "12345"))

        result.numberOfGoodsReferences mustBe 1
        result.remainingGoodsReferences mustBe 998
        result.allowMore mustBe true
        result.addedSingularOrPlural mustBe "singular"
        result.remainingSingularOrPlural mustBe "plural"
        result.currentPage mustBe 1
        result.totalPages mustBe 1
        result.firstGoodsReference mustBe 1
        result.lastGoodsReference mustBe 1
        result.pagination mustBe None
      }

      "must be empty there are no goods references" in {
        AddAnotherGoodsReferenceViewModel(emptyUserAnswers, transportEquipmentIndex, None, NormalMode).rows mustBe Seq.empty
      }

      "must be empty when there are no goods references in CheckMode" in {
        AddAnotherGoodsReferenceViewModel(emptyUserAnswers, transportEquipmentIndex, None, CheckMode).rows mustBe Seq.empty
      }
    }

    ".pagination" - {
      "must return None when there is 25 items or less" in {
        val goodsReferencesList: List[String] = (1 to 25).map { number =>
          number.toString
        }.toList

        val userAnswers = emptyUserAnswers
          .set(DiscrepancyReference(transportEquipmentIndex), goodsReferencesList)
          .success
          .value

        val pagination = AddAnotherGoodsReferenceViewModel(userAnswers, transportEquipmentIndex, None, NormalMode).pagination

        pagination mustBe None
      }

      "must return None when there is 25 items or less in CheckMode" in {
        val goodsReferencesList: List[String] = (1 to 25).map { number =>
          number.toString
        }.toList

        val userAnswers = emptyUserAnswers
          .set(DiscrepancyReference(transportEquipmentIndex), goodsReferencesList)
          .success
          .value

        val pagination = AddAnotherGoodsReferenceViewModel(userAnswers, transportEquipmentIndex, None, CheckMode).pagination

        pagination mustBe None
      }

      "must return Pagination for page 1 when there is 26 items" in {
        val goodsReferencesList = (1 to 26).map { number =>
          number.toString
        }.toList

        val userAnswers = emptyUserAnswers
          .set(DiscrepancyReference(transportEquipmentIndex), goodsReferencesList)
          .success
          .value

        val pagination = AddAnotherGoodsReferenceViewModel(userAnswers, transportEquipmentIndex, Some(1), NormalMode).pagination.value

        pagination.items mustBe Some(
          Seq(
            PaginationItem(
              href = createRoute.AddAnotherGoodsReferenceController.onPageLoad(transportEquipmentIndex, Some(1), NormalMode).url,
              number = Some("1"),
              visuallyHiddenText = Some(messages("site.pagination.page", 1)),
              current = Some(true)
            ),
            PaginationItem(
              href = createRoute.AddAnotherGoodsReferenceController.onPageLoad(transportEquipmentIndex, Some(2), NormalMode).url,
              visuallyHiddenText = Some(messages("site.pagination.page", 2)),
              number = Some("2")
            )
          )
        )
        pagination.previous mustBe None
        pagination.next.value.href mustBe createRoute.AddAnotherGoodsReferenceController.onPageLoad(transportEquipmentIndex, Some(2), NormalMode).url
      }

      "must return Pagination for page 1 when there is 26 items in CheckMode" in {
        val goodsReferencesList = (1 to 26).map { number =>
          number.toString
        }.toList

        val userAnswers = emptyUserAnswers
          .set(DiscrepancyReference(transportEquipmentIndex), goodsReferencesList)
          .success
          .value

        val pagination = AddAnotherGoodsReferenceViewModel(userAnswers, transportEquipmentIndex, Some(1), CheckMode).pagination.value

        pagination.items mustBe Some(
          Seq(
            PaginationItem(
              href = createRoute.AddAnotherGoodsReferenceController.onPageLoad(transportEquipmentIndex, Some(1), CheckMode).url,
              number = Some("1"),
              visuallyHiddenText = Some(messages("site.pagination.page", 1)),
              current = Some(true)
            ),
            PaginationItem(
              href = createRoute.AddAnotherGoodsReferenceController.onPageLoad(transportEquipmentIndex, Some(2), CheckMode).url,
              visuallyHiddenText = Some(messages("site.pagination.page", 2)),
              number = Some("2")
            )
          )
        )
        pagination.previous mustBe None
        pagination.next.value.href mustBe createRoute.AddAnotherGoodsReferenceController.onPageLoad(transportEquipmentIndex, Some(2), CheckMode).url
      }

      "must return Pagination for page 2 when there is 26 items" in {
        val goodsReferencesList = (1 to 26).map { number =>
          number.toString
        }.toList

        val userAnswers = emptyUserAnswers
          .set(DiscrepancyReference(transportEquipmentIndex), goodsReferencesList)
          .success
          .value

        val pagination = AddAnotherGoodsReferenceViewModel(userAnswers, transportEquipmentIndex, Some(2), NormalMode).pagination.value

        pagination.items mustBe Some(
          Seq(
            PaginationItem(
              href = createRoute.AddAnotherGoodsReferenceController.onPageLoad(transportEquipmentIndex, Some(1), NormalMode).url,
              visuallyHiddenText = Some(messages("site.pagination.page", 1)),
              number = Some("1")
            ),
            PaginationItem(
              href = createRoute.AddAnotherGoodsReferenceController.onPageLoad(transportEquipmentIndex, Some(2), NormalMode).url,
              number = Some("2"),
              visuallyHiddenText = Some(messages("site.pagination.page", 2)),
              current = Some(true)
            )
          )
        )
        pagination.previous.value.href mustBe createRoute.AddAnotherGoodsReferenceController
          .onPageLoad(transportEquipmentIndex, Some(1), NormalMode)
          .url
        pagination.next mustBe None
      }

      "must return Pagination when there is 999 items" - {
        val userAnswers = emptyUserAnswers
          .set(DiscrepancyReference(transportEquipmentIndex), goodsReferencesListMax)
          .success
          .value

        "for page 1" in {
          val pagination = AddAnotherGoodsReferenceViewModel(userAnswers, transportEquipmentIndex, Some(1), NormalMode).pagination.value

          pagination.items mustBe Some(
            Seq(
              PaginationItem(
                href = createRoute.AddAnotherGoodsReferenceController.onPageLoad(transportEquipmentIndex, Some(1), NormalMode).url,
                number = Some("1"),
                visuallyHiddenText = Some(messages("site.pagination.page", 1)),
                current = Some(true)
              ),
              PaginationItem(
                href = createRoute.AddAnotherGoodsReferenceController.onPageLoad(transportEquipmentIndex, Some(2), NormalMode).url,
                visuallyHiddenText = Some(messages("site.pagination.page", 2)),
                number = Some("2")
              ),
              PaginationItem(ellipsis = Some(true)),
              PaginationItem(
                href = createRoute.AddAnotherGoodsReferenceController.onPageLoad(transportEquipmentIndex, Some(40), NormalMode).url,
                visuallyHiddenText = Some(messages("site.pagination.page", 40)),
                number = Some("40")
              )
            )
          )
          pagination.previous mustBe None
          pagination.next.value.href mustBe createRoute.AddAnotherGoodsReferenceController
            .onPageLoad(transportEquipmentIndex, Some(2), NormalMode)
            .url
        }

        "for page 4" in {
          val pagination = AddAnotherGoodsReferenceViewModel(userAnswers, transportEquipmentIndex, Some(4), NormalMode).pagination.value

          pagination.items mustBe Some(
            Seq(
              PaginationItem(
                href = createRoute.AddAnotherGoodsReferenceController.onPageLoad(transportEquipmentIndex, Some(1), NormalMode).url,
                visuallyHiddenText = Some(messages("site.pagination.page", 1)),
                number = Some("1")
              ),
              PaginationItem(
                href = createRoute.AddAnotherGoodsReferenceController.onPageLoad(transportEquipmentIndex, Some(2), NormalMode).url,
                visuallyHiddenText = Some(messages("site.pagination.page", 2)),
                number = Some("2")
              ),
              PaginationItem(
                href = createRoute.AddAnotherGoodsReferenceController.onPageLoad(transportEquipmentIndex, Some(3), NormalMode).url,
                visuallyHiddenText = Some(messages("site.pagination.page", 3)),
                number = Some("3")
              ),
              PaginationItem(
                href = createRoute.AddAnotherGoodsReferenceController.onPageLoad(transportEquipmentIndex, Some(4), NormalMode).url,
                number = Some("4"),
                visuallyHiddenText = Some(messages("site.pagination.page", 4)),
                current = Some(true)
              ),
              PaginationItem(
                href = createRoute.AddAnotherGoodsReferenceController.onPageLoad(transportEquipmentIndex, Some(5), NormalMode).url,
                visuallyHiddenText = Some(messages("site.pagination.page", 5)),
                number = Some("5")
              ),
              PaginationItem(ellipsis = Some(true)),
              PaginationItem(
                href = createRoute.AddAnotherGoodsReferenceController.onPageLoad(transportEquipmentIndex, Some(40), NormalMode).url,
                visuallyHiddenText = Some(messages("site.pagination.page", 40)),
                number = Some("40")
              )
            )
          )
          pagination.previous.value.href mustBe createRoute.AddAnotherGoodsReferenceController
            .onPageLoad(transportEquipmentIndex, Some(3), NormalMode)
            .url
          pagination.next.value.href mustBe createRoute.AddAnotherGoodsReferenceController
            .onPageLoad(transportEquipmentIndex, Some(5), NormalMode)
            .url
        }

        "for page 5" in {
          val pagination = AddAnotherGoodsReferenceViewModel(userAnswers, transportEquipmentIndex, Some(5), NormalMode).pagination.value

          pagination.items mustBe Some(
            Seq(
              PaginationItem(
                href = createRoute.AddAnotherGoodsReferenceController.onPageLoad(transportEquipmentIndex, Some(1), NormalMode).url,
                visuallyHiddenText = Some(messages("site.pagination.page", 1)),
                number = Some("1")
              ),
              PaginationItem(ellipsis = Some(true)),
              PaginationItem(
                href = createRoute.AddAnotherGoodsReferenceController.onPageLoad(transportEquipmentIndex, Some(4), NormalMode).url,
                visuallyHiddenText = Some(messages("site.pagination.page", 4)),
                number = Some("4")
              ),
              PaginationItem(
                href = createRoute.AddAnotherGoodsReferenceController.onPageLoad(transportEquipmentIndex, Some(5), NormalMode).url,
                number = Some("5"),
                visuallyHiddenText = Some(messages("site.pagination.page", 5)),
                current = Some(true)
              ),
              PaginationItem(
                href = createRoute.AddAnotherGoodsReferenceController.onPageLoad(transportEquipmentIndex, Some(6), NormalMode).url,
                visuallyHiddenText = Some(messages("site.pagination.page", 6)),
                number = Some("6")
              ),
              PaginationItem(ellipsis = Some(true)),
              PaginationItem(
                href = createRoute.AddAnotherGoodsReferenceController.onPageLoad(transportEquipmentIndex, Some(40), NormalMode).url,
                visuallyHiddenText = Some(messages("site.pagination.page", 40)),
                number = Some("40")
              )
            )
          )
          pagination.previous.value.href mustBe createRoute.AddAnotherGoodsReferenceController
            .onPageLoad(transportEquipmentIndex, Some(4), NormalMode)
            .url
          pagination.next.value.href mustBe createRoute.AddAnotherGoodsReferenceController
            .onPageLoad(transportEquipmentIndex, Some(6), NormalMode)
            .url
        }

        "for page 20" in {
          val pagination = AddAnotherGoodsReferenceViewModel(userAnswers, transportEquipmentIndex, Some(20), NormalMode).pagination.value

          pagination.items mustBe Some(
            Seq(
              PaginationItem(
                href = createRoute.AddAnotherGoodsReferenceController.onPageLoad(transportEquipmentIndex, Some(1), NormalMode).url,
                visuallyHiddenText = Some(messages("site.pagination.page", 1)),
                number = Some("1")
              ),
              PaginationItem(ellipsis = Some(true)),
              PaginationItem(
                href = createRoute.AddAnotherGoodsReferenceController.onPageLoad(transportEquipmentIndex, Some(19), NormalMode).url,
                visuallyHiddenText = Some(messages("site.pagination.page", 19)),
                number = Some("19")
              ),
              PaginationItem(
                href = createRoute.AddAnotherGoodsReferenceController.onPageLoad(transportEquipmentIndex, Some(19), NormalMode).url,
                number = Some("19")
              ),
              PaginationItem(
                href = createRoute.AddAnotherGoodsReferenceController.onPageLoad(transportEquipmentIndex, Some(20), NormalMode).url,
                number = Some("20"),
                visuallyHiddenText = Some(messages("site.pagination.page", 20)),
                current = Some(true)
              ),
              PaginationItem(
                href = createRoute.AddAnotherGoodsReferenceController.onPageLoad(transportEquipmentIndex, Some(21), NormalMode).url,
                visuallyHiddenText = Some(messages("site.pagination.page", 21)),
                number = Some("21")
              ),
              PaginationItem(ellipsis = Some(true)),
              PaginationItem(
                href = createRoute.AddAnotherGoodsReferenceController.onPageLoad(transportEquipmentIndex, Some(40), NormalMode).url,
                visuallyHiddenText = Some(messages("site.pagination.page", 40)),
                number = Some("40")
              )
            )
          )
          pagination.previous.value.href mustBe createRoute.AddAnotherGoodsReferenceController
            .onPageLoad(transportEquipmentIndex, Some(19), NormalMode)
            .url
          pagination.next.value.href mustBe createRoute.AddAnotherGoodsReferenceController
            .onPageLoad(transportEquipmentIndex, Some(21), NormalMode)
            .url
        }

        "for page 36" in {
          val pagination = AddAnotherGoodsReferenceViewModel(userAnswers, transportEquipmentIndex, Some(36), NormalMode).pagination.value

          pagination.items mustBe Some(
            Seq(
              PaginationItem(
                href = createRoute.AddAnotherGoodsReferenceController.onPageLoad(transportEquipmentIndex, Some(1), NormalMode).url,
                visuallyHiddenText = Some(messages("site.pagination.page", 1)),
                number = Some("1")
              ),
              PaginationItem(ellipsis = Some(true)),
              PaginationItem(
                href = createRoute.AddAnotherGoodsReferenceController.onPageLoad(transportEquipmentIndex, Some(35), NormalMode).url,
                visuallyHiddenText = Some(messages("site.pagination.page", 35)),
                number = Some("35")
              ),
              PaginationItem(
                href = createRoute.AddAnotherGoodsReferenceController.onPageLoad(transportEquipmentIndex, Some(35), NormalMode).url,
                number = Some("35")
              ),
              PaginationItem(
                href = createRoute.AddAnotherGoodsReferenceController.onPageLoad(transportEquipmentIndex, Some(36), NormalMode).url,
                number = Some("36"),
                visuallyHiddenText = Some(messages("site.pagination.page", 36)),
                current = Some(true)
              ),
              PaginationItem(
                href = createRoute.AddAnotherGoodsReferenceController.onPageLoad(transportEquipmentIndex, Some(37), NormalMode).url,
                visuallyHiddenText = Some(messages("site.pagination.page", 37)),
                number = Some("37")
              ),
              PaginationItem(ellipsis = Some(true)),
              PaginationItem(
                href = createRoute.AddAnotherGoodsReferenceController.onPageLoad(transportEquipmentIndex, Some(40), NormalMode).url,
                visuallyHiddenText = Some(messages("site.pagination.page", 40)),
                number = Some("40")
              )
            )
          )
          pagination.previous.value.href mustBe createRoute.AddAnotherGoodsReferenceController
            .onPageLoad(transportEquipmentIndex, Some(35), NormalMode)
            .url
          pagination.next.value.href mustBe createRoute.AddAnotherGoodsReferenceController
            .onPageLoad(transportEquipmentIndex, Some(37), NormalMode)
            .url
        }

        "for page 37" in {
          val pagination = AddAnotherGoodsReferenceViewModel(userAnswers, transportEquipmentIndex, Some(37), NormalMode).pagination.value

          pagination.items mustBe Some(
            Seq(
              PaginationItem(
                href = createRoute.AddAnotherGoodsReferenceController.onPageLoad(transportEquipmentIndex, Some(1), NormalMode).url,
                visuallyHiddenText = Some(messages("site.pagination.page", 1)),
                number = Some("1")
              ),
              PaginationItem(ellipsis = Some(true)),
              PaginationItem(
                href = createRoute.AddAnotherGoodsReferenceController.onPageLoad(transportEquipmentIndex, Some(36), NormalMode).url,
                visuallyHiddenText = Some(messages("site.pagination.page", 36)),
                number = Some("36")
              ),
              PaginationItem(
                href = createRoute.AddAnotherGoodsReferenceController.onPageLoad(transportEquipmentIndex, Some(37), NormalMode).url,
                number = Some("37"),
                visuallyHiddenText = Some(messages("site.pagination.page", 37)),
                current = Some(true)
              ),
              PaginationItem(
                href = createRoute.AddAnotherGoodsReferenceController.onPageLoad(transportEquipmentIndex, Some(38), NormalMode).url,
                visuallyHiddenText = Some(messages("site.pagination.page", 38)),
                number = Some("38")
              ),
              PaginationItem(
                href = createRoute.AddAnotherGoodsReferenceController.onPageLoad(transportEquipmentIndex, Some(39), NormalMode).url,
                visuallyHiddenText = Some(messages("site.pagination.page", 39)),
                number = Some("39")
              ),
              PaginationItem(
                href = createRoute.AddAnotherGoodsReferenceController.onPageLoad(transportEquipmentIndex, Some(40), NormalMode).url,
                visuallyHiddenText = Some(messages("site.pagination.page", 40)),
                number = Some("40")
              )
            )
          )
          pagination.previous.value.href mustBe createRoute.AddAnotherGoodsReferenceController
            .onPageLoad(transportEquipmentIndex, Some(36), NormalMode)
            .url
          pagination.next.value.href mustBe createRoute.AddAnotherGoodsReferenceController
            .onPageLoad(transportEquipmentIndex, Some(38), NormalMode)
            .url
        }

        "for page 40" in {
          val pagination = AddAnotherGoodsReferenceViewModel(userAnswers, transportEquipmentIndex, Some(40), NormalMode).pagination.value

          pagination.items mustBe Some(
            Seq(
              PaginationItem(
                href = createRoute.AddAnotherGoodsReferenceController.onPageLoad(transportEquipmentIndex, Some(1), NormalMode).url,
                visuallyHiddenText = Some(messages("site.pagination.page", 1)),
                number = Some("1")
              ),
              PaginationItem(ellipsis = Some(true)),
              PaginationItem(
                href = createRoute.AddAnotherGoodsReferenceController.onPageLoad(transportEquipmentIndex, Some(39), NormalMode).url,
                visuallyHiddenText = Some(messages("site.pagination.page", 39)),
                number = Some("39")
              ),
              PaginationItem(
                href = createRoute.AddAnotherGoodsReferenceController.onPageLoad(transportEquipmentIndex, Some(40), NormalMode).url,
                visuallyHiddenText = Some(messages("site.pagination.page", 40)),
                number = Some("40"),
                current = Some(true)
              )
            )
          )
          pagination.previous.value.href mustBe createRoute.AddAnotherGoodsReferenceController
            .onPageLoad(transportEquipmentIndex, Some(39), NormalMode)
            .url
          pagination.next mustBe None
        }
      }
    }

    "when determining if add another should be enabled" - {
      "must provide the correct model when 1 item has been added" in {
        val result = AddAnotherGoodsReferenceViewModel(userAnswers, transportEquipmentIndex, None, NormalMode)

        result.numberOfGoodsReferences mustBe 1
        result.remainingGoodsReferences mustBe 998
        result.allowMore mustBe true
        result.addedSingularOrPlural mustBe "singular"
        result.remainingSingularOrPlural mustBe "plural"
        result.currentPage mustBe 1
        result.totalPages mustBe 1
        result.firstGoodsReference mustBe 1
        result.lastGoodsReference mustBe 1
        result.rows.size mustBe 1
        result.pagination mustBe None
      }

      "must provide the correct model when 1 item has been added in CheckMode" in {
        val result = AddAnotherGoodsReferenceViewModel(userAnswers, transportEquipmentIndex, None, CheckMode)

        result.numberOfGoodsReferences mustBe 1
        result.remainingGoodsReferences mustBe 998
        result.allowMore mustBe true
        result.addedSingularOrPlural mustBe "singular"
        result.remainingSingularOrPlural mustBe "plural"
        result.currentPage mustBe 1
        result.totalPages mustBe 1
        result.firstGoodsReference mustBe 1
        result.lastGoodsReference mustBe 1
        result.rows.size mustBe 1
        result.pagination mustBe None
      }

      "must provide the correct model when 1 more item can be added" in {
        val goodsReferencesList = (1 until maxGoodsReferences).map { number =>
          number.toString
        }.toList

        val userAnswers = emptyUserAnswers
          .set(DiscrepancyReference(transportEquipmentIndex), goodsReferencesList)
          .success
          .value

        val result = AddAnotherGoodsReferenceViewModel(userAnswers, transportEquipmentIndex, None, NormalMode)

        result.numberOfGoodsReferences mustBe 998
        result.remainingGoodsReferences mustBe 1
        result.allowMore mustBe true
        result.addedSingularOrPlural mustBe "plural"
        result.remainingSingularOrPlural mustBe "singular"
        result.currentPage mustBe 1
        result.totalPages mustBe 40
        result.firstGoodsReference mustBe 1
        result.lastGoodsReference mustBe 25
        result.rows.size mustBe 25
        result.pagination.isDefined mustBe true
      }

      "must provide the correct model when the maximum number of items has been reached" in {
        val userAnswers = emptyUserAnswers
          .set(DiscrepancyReference(transportEquipmentIndex), goodsReferencesListMax)
          .success
          .value

        val result = AddAnotherGoodsReferenceViewModel(userAnswers, transportEquipmentIndex, None, NormalMode)

        result.numberOfGoodsReferences mustBe 999
        result.remainingGoodsReferences mustBe 0
        result.allowMore mustBe false
        result.addedSingularOrPlural mustBe "plural"
        result.remainingSingularOrPlural mustBe "plural"
        result.currentPage mustBe 1
        result.totalPages mustBe 40
        result.firstGoodsReference mustBe 1
        result.lastGoodsReference mustBe 25
        result.rows.size mustBe 25
        result.pagination.isDefined mustBe true
      }
    }

    "must provide the correct model before pagination starts" in {
      val goodsReferencesList = (1 to 25).map { number =>
        number.toString
      }.toList

      val userAnswers = emptyUserAnswers
        .set(DiscrepancyReference(transportEquipmentIndex), goodsReferencesList)
        .success
        .value

      val result = AddAnotherGoodsReferenceViewModel(userAnswers, transportEquipmentIndex, None, NormalMode)

      result.numberOfGoodsReferences mustBe 25
      result.remainingGoodsReferences mustBe 974
      result.allowMore mustBe true
      result.addedSingularOrPlural mustBe "plural"
      result.remainingSingularOrPlural mustBe "plural"
      result.currentPage mustBe 1
      result.totalPages mustBe 1
      result.firstGoodsReference mustBe 1
      result.lastGoodsReference mustBe 25
      result.rows.size mustBe 25
      result.pagination mustBe None
      result.pageParam mustBe None
    }

    "must provide the correct model before pagination starts in CheckMode" in {
      val goodsReferencesList = (1 to 25).map { number =>
        number.toString
      }.toList

      val userAnswers = emptyUserAnswers
        .set(DiscrepancyReference(transportEquipmentIndex), goodsReferencesList)
        .success
        .value

      val result = AddAnotherGoodsReferenceViewModel(userAnswers, transportEquipmentIndex, None, CheckMode)

      result.numberOfGoodsReferences mustBe 25
      result.remainingGoodsReferences mustBe 974
      result.allowMore mustBe true
      result.addedSingularOrPlural mustBe "plural"
      result.remainingSingularOrPlural mustBe "plural"
      result.currentPage mustBe 1
      result.totalPages mustBe 1
      result.firstGoodsReference mustBe 1
      result.lastGoodsReference mustBe 25
      result.rows.size mustBe 25
      result.pagination mustBe None
      result.pageParam mustBe None
    }

    "must provide the correct model for when pagination starts at 26 items added" in {
      val goodsReferencesList = (1 to 26).map { number =>
        number.toString
      }.toList

      val userAnswers = emptyUserAnswers
        .set(DiscrepancyReference(transportEquipmentIndex), goodsReferencesList)
        .success
        .value

      val result = AddAnotherGoodsReferenceViewModel(userAnswers, transportEquipmentIndex, None, NormalMode)

      result.numberOfGoodsReferences mustBe 26
      result.remainingGoodsReferences mustBe 973
      result.allowMore mustBe true
      result.addedSingularOrPlural mustBe "plural"
      result.remainingSingularOrPlural mustBe "plural"
      result.currentPage mustBe 1
      result.totalPages mustBe 2
      result.firstGoodsReference mustBe 1
      result.lastGoodsReference mustBe 25
      result.rows.size mustBe 25
      result.pagination.isDefined mustBe true
      result.pageParam mustBe Some(1)
    }

    "must provide the correct model for a final but partially completed page" in {
      val goodsReferencesList = (1 to 26).map { number =>
        number.toString
      }.toList

      val userAnswers = emptyUserAnswers
        .set(DiscrepancyReference(transportEquipmentIndex), goodsReferencesList)
        .success
        .value

      val result = AddAnotherGoodsReferenceViewModel(userAnswers, transportEquipmentIndex, Some(2), NormalMode)

      result.numberOfGoodsReferences mustBe 26
      result.remainingGoodsReferences mustBe 973
      result.allowMore mustBe true
      result.addedSingularOrPlural mustBe "plural"
      result.remainingSingularOrPlural mustBe "plural"
      result.currentPage mustBe 2
      result.totalPages mustBe 2
      result.firstGoodsReference mustBe 26
      result.lastGoodsReference mustBe 26
      result.rows.size mustBe 1
      result.pagination.isDefined mustBe true
      result.pageParam mustBe Some(2)
    }
  }
}
