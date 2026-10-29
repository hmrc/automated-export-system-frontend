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

class AddAnotherGoodsReferenceViewModelSpec extends SpecBase {

  private implicit val messages: Messages = Helpers.stubMessages()

  val userAnswers: UserAnswers = emptyUserAnswers.set(DiscrepancyReferencePage(1), "12345").get

  val maxGoodsReferences: Int = DiscrepancyReference.maxDiscrepancyReferences

  "AddAnotherGoodsReferenceViewModel" - {

    "rows" - {
      "must build a valid row with goods reference, hidden value, change and remove actions" in {
        val result = AddAnotherGoodsReferenceViewModel(userAnswers, NormalMode)
        val rows = result.rows
        rows.size mustBe 1
        val row = rows.head

        val actions = row.actions.value

        row.key.content mustBe Text(messages("addAnotherGoodsReference.key", "12345"))
        actions.items.size mustBe 2

        val change = actions.items.head
        change.href mustBe createRoute.DiscrepancyReferenceController.onPageLoad(1, NormalMode).url
        change.content mustBe Text(messages("site.change"))
        change.visuallyHiddenText mustBe Some(messages("addAnotherGoodsReference.change.hidden", "12345"))

        val remove = actions.items(1)
        remove.href mustBe createRoute.RemoveGoodsReferenceController.onPageLoad(1, NormalMode).url
        remove.content mustBe Text(messages("site.remove"))
        remove.visuallyHiddenText mustBe Some(messages("addAnotherGoodsReference.remove.hidden", "12345"))

        result.numberOfGoodsReferences mustBe 1
        result.remainingGoodsReferences mustBe 998
        result.allowMore mustBe true
        result.addedSingularOrPlural mustBe "singular"
        result.remainingSingularOrPlural mustBe "plural"
      }

      "must build a valid row with goods reference, hidden value, change and remove actions in CheckMode" in {
        val result = AddAnotherGoodsReferenceViewModel(userAnswers, CheckMode)
        val rows = result.rows
        rows.size mustBe 1
        val row = rows.head

        val actions = row.actions.value

        row.key.content mustBe Text(messages("addAnotherGoodsReference.key", "12345"))
        actions.items.size mustBe 2

        val change = actions.items.head
        change.href mustBe createRoute.DiscrepancyReferenceController.onPageLoad(1, CheckMode).url
        change.content mustBe Text(messages("site.change"))
        change.visuallyHiddenText mustBe Some(messages("addAnotherGoodsReference.change.hidden", "12345"))

        val remove = actions.items(1)
        remove.href mustBe createRoute.RemoveGoodsReferenceController.onPageLoad(1, CheckMode).url
        remove.content mustBe Text(messages("site.remove"))
        remove.visuallyHiddenText mustBe Some(messages("addAnotherGoodsReference.remove.hidden", "12345"))

        result.numberOfGoodsReferences mustBe 1
        result.remainingGoodsReferences mustBe 998
        result.allowMore mustBe true
        result.addedSingularOrPlural mustBe "singular"
        result.remainingSingularOrPlural mustBe "plural"
      }

      "must be empty be empty when there are no goods references" in {
        AddAnotherGoodsReferenceViewModel(emptyUserAnswers, NormalMode).rows mustBe Seq.empty
      }
    }

    "must provide the correct model when 1 item has been added" in {
      val result = AddAnotherGoodsReferenceViewModel(userAnswers, NormalMode)

      result.numberOfGoodsReferences mustBe 1
      result.remainingGoodsReferences mustBe 998
      result.allowMore mustBe true
      result.addedSingularOrPlural mustBe "singular"
      result.remainingSingularOrPlural mustBe "plural"
    }

    "must provide the correct model when 1 item has been added in CheckMode" in {
      val result = AddAnotherGoodsReferenceViewModel(userAnswers, CheckMode)

      result.numberOfGoodsReferences mustBe 1
      result.remainingGoodsReferences mustBe 998
      result.allowMore mustBe true
      result.addedSingularOrPlural mustBe "singular"
      result.remainingSingularOrPlural mustBe "plural"
    }

    "must provide the correct model when 1 more item can be added" in {
      val goodsReferencesList = (1 until maxGoodsReferences).map { number =>
        number.toString
      }.toList

      val userAnswers = emptyUserAnswers
        .set(DiscrepancyReference, goodsReferencesList)
        .success
        .value

      val result = AddAnotherGoodsReferenceViewModel(userAnswers, NormalMode)

      result.numberOfGoodsReferences mustBe 998
      result.remainingGoodsReferences mustBe 1
      result.allowMore mustBe true
      result.addedSingularOrPlural mustBe "plural"
      result.remainingSingularOrPlural mustBe "singular"
    }

    "must provide the correct model when the maximum number of items has been reached" in {
      val goodsReferencesListMax = (1 to maxGoodsReferences).map { number =>
        number.toString
      }.toList

      val userAnswers = emptyUserAnswers
        .set(DiscrepancyReference, goodsReferencesListMax)
        .success
        .value

      val result = AddAnotherGoodsReferenceViewModel(userAnswers, NormalMode)

      result.numberOfGoodsReferences mustBe 999
      result.remainingGoodsReferences mustBe 0
      result.allowMore mustBe false
      result.addedSingularOrPlural mustBe "plural"
      result.remainingSingularOrPlural mustBe "plural"
    }
  }
}
