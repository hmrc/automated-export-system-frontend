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
import uk.gov.hmrc.automatedexportsystemfrontend.helpers.SpecBase
import uk.gov.hmrc.automatedexportsystemfrontend.models.NormalMode
import uk.gov.hmrc.automatedexportsystemfrontend.pages.create.DiscrepancySealsPage
import uk.gov.hmrc.automatedexportsystemfrontend.queries.DiscrepancySeals
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text

class AddAnotherSealViewModelSpec extends SpecBase {
  private implicit val messages: Messages = Helpers.stubMessages()
  val userAnswers = emptyUserAnswers
    .set(DiscrepancySealsPage(1), "sealIdentifier")
    .get
  val maxSeals: Int = DiscrepancySeals.maxNumberOfSealsInList

  "AddAnotherSealViewModel" - {
    "rows" - {
      "should build a valid row with seal identifier, hidden value, change and remove actions" in {
        val result = AddAnotherSealViewModel(userAnswers, NormalMode)
        val rows = result.rows
        rows.size shouldBe 1
        val row = rows.head

        val actions = row.actions.value

        row.key.content shouldBe Text("sealIdentifier")
        actions.items.size shouldBe 2

        val change = actions.items.head
        change.href shouldBe uk.gov.hmrc.automatedexportsystemfrontend.controllers.create.routes.DiscrepancySealsController
          .onPageLoad(1, NormalMode)
          .url
        change.content shouldBe Text(messages("site.change"))
        change.visuallyHiddenText shouldBe Some(messages("addAnotherSeal.change.hidden", 1))

        val remove = actions.items(1)
        remove.href shouldBe uk.gov.hmrc.automatedexportsystemfrontend.controllers.create.routes.RemoveSealController.onPageLoad(1, NormalMode).url
        remove.content shouldBe Text(messages("site.remove"))
        remove.visuallyHiddenText shouldBe Some(messages("addAnotherSeal.remove.hidden", 1))
        result.numberOfSeals shouldBe 1
        result.remainingSeals shouldBe 98
        result.allowMore shouldBe true
        result.addedSingularOrPlural shouldBe "singular"
        result.addedSingularOrPlural shouldBe "singular"
        result.remainingSingularOrPlural shouldBe "plural"
      }

      "should be empty be empty when there are no seals" in {
        AddAnotherSealViewModel(emptyUserAnswers, NormalMode).rows shouldBe Seq.empty
      }

      "should provide the correct model when one more item can be added" in {
        val sealsList = (1 until maxSeals).map(number => s"seal$number").toList

        val userAnswers = emptyUserAnswers
          .set(DiscrepancySeals, sealsList)
          .success
          .value

        val result = AddAnotherSealViewModel(userAnswers, NormalMode)

        result.numberOfSeals shouldBe 98
        result.remainingSeals shouldBe 1
        result.allowMore shouldBe true
        result.addedSingularOrPlural shouldBe "plural"
        result.remainingSingularOrPlural shouldBe "singular"

      }

      "must provide the correct model when the maximum number of items has been reached" in {
        val sealsList = (1 to maxSeals).map(number => s"seal$number").toList

        val userAnswers = emptyUserAnswers
          .set(DiscrepancySeals, sealsList)
          .success
          .value

        val result = AddAnotherSealViewModel(userAnswers, NormalMode)

        result.numberOfSeals shouldBe 99
        result.remainingSeals shouldBe 0
        result.allowMore shouldBe false
        result.addedSingularOrPlural shouldBe "plural"
        result.remainingSingularOrPlural shouldBe "plural"
      }

    }
  }

}
