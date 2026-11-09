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
        result.addedSingularOrPlural shouldBe "singular"
        result.remainingSingularOrPlural shouldBe "plural"
      }

      "should be empty when there are no goodsItem" in {
        AddAnotherGoodsItemViewModel(emptyUserAnswers, None, NormalMode).rows shouldBe Seq.empty
      }

    }

  }

}
