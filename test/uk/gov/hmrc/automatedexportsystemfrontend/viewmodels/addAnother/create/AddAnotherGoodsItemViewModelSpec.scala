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
import play.api.test.Helpers
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.create.routes as createRoute
import uk.gov.hmrc.automatedexportsystemfrontend.helpers.SpecBase
import uk.gov.hmrc.automatedexportsystemfrontend.models.{CheckMode, NormalMode, WhatHasChangedDetails}
import uk.gov.hmrc.automatedexportsystemfrontend.pages.create.DiscrepancyGoodsPage
import uk.gov.hmrc.automatedexportsystemfrontend.queries.DiscrepancyGoods
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text

class AddAnotherGoodsItemViewModelSpec extends SpecBase {

  private implicit val messages: Messages = Helpers.stubMessages()

  val userAnswers = emptyUserAnswers
    .set(DiscrepancyGoodsPage(1), WhatHasChangedDetails(Some(1), Some("5GB000000000000-123451"), "20", "10"))
    .get

  val maxGoods: Int = DiscrepancyGoods.maxDiscrepancyGoods
  private def goodsItemsList(n: Int): List[WhatHasChangedDetails] = (1 to n).map { number =>
    WhatHasChangedDetails(Some(number), Some(s"5GB0000000-$number"), number.toString, number.toString)
  }.toList

  "AddAnotherGoodsItemViewModel" - {
    "rows" - {
      "should build valid rows with goods item, hidden value, change and remove actions" in {
        val result = AddAnotherGoodsItemViewModel(userAnswers, None, NormalMode)
        val rows = result.rows
        rows.size shouldBe 1
        val row = rows.head

        val actions = row.actions.value

        row.key.content shouldBe Text(messages("addAnotherGoodsItem.key", "1"))
        actions.items.size shouldBe 2

        val change = actions.items.head
        change.href shouldBe uk.gov.hmrc.automatedexportsystemfrontend.controllers.create.routes.DiscrepancyGoodsController
          .onPageLoad(1, NormalMode)
          .url
        change.content shouldBe Text(messages("site.change"))
        change.visuallyHiddenText shouldBe Some(messages("addAnotherGoodsItem.change.hidden", 1))

        val remove = actions.items(1)
        remove.href shouldBe uk.gov.hmrc.automatedexportsystemfrontend.controllers.create.routes.RemoveGoodsItemController
          .onPageLoad(1, 1, NormalMode)
          .url
        remove.content shouldBe Text(messages("site.remove"))
        remove.visuallyHiddenText shouldBe Some(messages("addAnotherGoodsItem.remove.hidden", 1))
        result.numberOfGoodsItem shouldBe 1
        result.remainingGoodsItem shouldBe 998
        result.allowMore shouldBe true
        result.addedSingularOrPlural shouldBe "singular"
        result.remainingSingularOrPlural shouldBe "plural"
        result.currentPage shouldBe 1
        result.totalPages shouldBe 1
        result.firstGoodsItem shouldBe 1
        result.lastGoodsItem shouldBe 1
        result.pagination shouldBe None
      }

      "should build a valid row with goods reference, hidden value, change and remove actions in CheckMode" in {
        val result = AddAnotherGoodsItemViewModel(userAnswers, None, CheckMode)
        val rows = result.rows
        rows.size shouldBe 1
        val row = rows.head

        val actions = row.actions.value

        row.key.content shouldBe Text(messages("addAnotherGoodsItem.key", "1"))
        actions.items.size shouldBe 2

        val change = actions.items.head
        change.href shouldBe createRoute.DiscrepancyGoodsController.onPageLoad(1, CheckMode).url
        change.content shouldBe Text(messages("site.change"))
        change.visuallyHiddenText shouldBe Some(messages("addAnotherGoodsItem.change.hidden", "12345"))

        val remove = actions.items(1)
        remove.href shouldBe createRoute.RemoveGoodsItemController.onPageLoad(1, 1, CheckMode).url
        remove.content shouldBe Text(messages("site.remove"))
        remove.visuallyHiddenText shouldBe Some(messages("addAnotherGoodsItem.remove.hidden", "12345"))

        result.numberOfGoodsItem shouldBe 1
        result.remainingGoodsItem shouldBe 998
        result.allowMore shouldBe true
        result.addedSingularOrPlural shouldBe "singular"
        result.remainingSingularOrPlural shouldBe "plural"
        result.currentPage shouldBe 1
        result.totalPages shouldBe 1
        result.firstGoodsItem shouldBe 1
        result.lastGoodsItem shouldBe 1
        result.pagination shouldBe None
      }

      "should be empty when there are no goodsItem" in {
        AddAnotherGoodsItemViewModel(emptyUserAnswers, None, NormalMode).rows shouldBe Seq.empty
      }

      "must be empty when there are no goods references in CheckMode" in {
        AddAnotherGoodsItemViewModel(emptyUserAnswers, None, CheckMode).rows shouldBe Seq.empty
      }

    }

    "pagination" - {
      "should provide the correct model when one more item can be added" in {

        val userAnswers = emptyUserAnswers
          .set(DiscrepancyGoods, goodsItemsList(998))
          .success
          .value

        val result = AddAnotherGoodsItemViewModel(userAnswers, None, NormalMode)

        result.numberOfGoodsItem shouldBe 998
        result.remainingGoodsItem shouldBe 1
        result.allowMore shouldBe true
        result.addedSingularOrPlural shouldBe "plural"
        result.remainingSingularOrPlural shouldBe "singular"
        result.currentPage shouldBe 1
        result.totalPages shouldBe 40
        result.firstGoodsItem shouldBe 1
        result.lastGoodsItem shouldBe 25
      }

      "should provide the correct model when the maximum number of items has been reached" in {
        val userAnswers = emptyUserAnswers
          .set(DiscrepancyGoods, goodsItemsList(maxGoods))
          .success
          .value

        val result = AddAnotherGoodsItemViewModel(userAnswers, None, NormalMode)

        result.numberOfGoodsItem shouldBe 999
        result.remainingGoodsItem shouldBe 0
        result.allowMore shouldBe false
        result.addedSingularOrPlural shouldBe "plural"
        result.remainingSingularOrPlural shouldBe "plural"
        result.currentPage shouldBe 1
        result.totalPages shouldBe 40
        result.firstGoodsItem shouldBe 1
        result.lastGoodsItem shouldBe 25
      }

      "should put 25 goodsItem on a single page with no pagination" in {
        val userAnswers = emptyUserAnswers
          .set(DiscrepancyGoods, goodsItemsList(25))
          .success
          .value

        val result = AddAnotherGoodsItemViewModel(userAnswers, None, NormalMode)

        result.currentPage shouldBe 1
        result.totalPages shouldBe 1
        result.rows.size shouldBe 25
        result.firstGoodsItem shouldBe 1
        result.lastGoodsItem shouldBe 25
        result.pageParam shouldBe None
        result.pagination shouldBe None
      }

      "should start pagination at 26 goodsItem, showing 25 rows on page 1" in {
        val userAnswers = emptyUserAnswers
          .set(DiscrepancyGoods, goodsItemsList(26))
          .success
          .value

        val result = AddAnotherGoodsItemViewModel(userAnswers, None, NormalMode)

        result.currentPage shouldBe 1
        result.totalPages shouldBe 2
        result.rows.size shouldBe 25
        result.firstGoodsItem shouldBe 1
        result.lastGoodsItem shouldBe 25
        result.pageParam shouldBe Some(1)
      }

      "should omit Previous on the first page" in {
        val userAnswers = emptyUserAnswers
          .set(DiscrepancyGoods, goodsItemsList(26))
          .success
          .value

        val pagination = AddAnotherGoodsItemViewModel(userAnswers, Some(1), NormalMode).pagination.value

        pagination.previous shouldBe None
        pagination.next.value.href shouldBe createRoute.AddAnotherGoodsItemController.onPageLoad(Some(2), NormalMode).url
      }

      "should include both Previous and Next on a middle page" in {
        val userAnswers = emptyUserAnswers
          .set(DiscrepancyGoods, goodsItemsList(75))
          .success
          .value

        val pagination = AddAnotherGoodsItemViewModel(userAnswers, Some(2), NormalMode).pagination.value

        pagination.previous.value.href shouldBe createRoute.AddAnotherGoodsItemController.onPageLoad(Some(1), NormalMode).url
        pagination.previous.value.text shouldBe Some(messages("site.pagination.previous"))
        pagination.next.value.href shouldBe createRoute.AddAnotherGoodsItemController.onPageLoad(Some(3), NormalMode).url
        pagination.next.value.text shouldBe Some(messages("site.pagination.next"))
      }

      "should omit Next on the last page" in {
        val userAnswers = emptyUserAnswers
          .set(DiscrepancyGoods, goodsItemsList(26))
          .success
          .value

        val pagination = AddAnotherGoodsItemViewModel(userAnswers, Some(2), NormalMode).pagination.value

        pagination.next shouldBe None
        pagination.previous.value.href shouldBe createRoute.AddAnotherGoodsItemController.onPageLoad(Some(1), NormalMode).url
      }

      "should show only the remaining goods item on a partial final page" in {
        val userAnswers = emptyUserAnswers
          .set(DiscrepancyGoods, goodsItemsList(26))
          .success
          .value

        val result = AddAnotherGoodsItemViewModel(userAnswers, Some(2), NormalMode)

        result.currentPage shouldBe 2
        result.rows.size shouldBe 1
        result.rows.head.key.content shouldBe Text(messages("addAnotherGoodsItem.key", "26"))
        result.firstGoodsItem shouldBe 26
        result.lastGoodsItem shouldBe 26
        result.pageParam shouldBe Some(2)
      }
    }

  }

}
