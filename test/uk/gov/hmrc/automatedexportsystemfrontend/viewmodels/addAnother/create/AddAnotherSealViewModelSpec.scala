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
import uk.gov.hmrc.automatedexportsystemfrontend.models.{CheckMode, NormalMode}
import uk.gov.hmrc.automatedexportsystemfrontend.pages.create.DiscrepancySealsPage
import uk.gov.hmrc.automatedexportsystemfrontend.queries.DiscrepancySeals
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.create.routes as createRoute

class AddAnotherSealViewModelSpec extends SpecBase {
  private implicit val messages: Messages = Helpers.stubMessages()
  val userAnswers = emptyUserAnswers
    .set(DiscrepancySealsPage(1), "sealIdentifier")
    .get
  val maxSeals: Int = DiscrepancySeals.maxNumberOfSealsInList

  private def sealsList(n: Int): List[String] = (1 to n).map(i => s"seal$i").toList

  "AddAnotherSealViewModel" - {
    "rows" - {
      "should build a valid row with seal identifier, hidden value, change and remove actions" in {
        val result = AddAnotherSealViewModel(userAnswers, None, NormalMode)
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
        remove.href shouldBe uk.gov.hmrc.automatedexportsystemfrontend.controllers.create.routes.RemoveSealController.onPageLoad(1, 1, NormalMode).url
        remove.content shouldBe Text(messages("site.remove"))
        remove.visuallyHiddenText shouldBe Some(messages("addAnotherSeal.remove.hidden", 1))
        result.numberOfSeals shouldBe 1
        result.remainingSeals shouldBe 98
        result.allowMore shouldBe true
        result.addedSingularOrPlural shouldBe "singular"
        result.addedSingularOrPlural shouldBe "singular"
        result.remainingSingularOrPlural shouldBe "plural"
      }

      "should be empty when there are no seals" in {
        AddAnotherSealViewModel(emptyUserAnswers, None, NormalMode).rows shouldBe Seq.empty
      }

    }

    "pagination" - {
      "should provide the correct model when one more item can be added" in {

        val userAnswers = emptyUserAnswers
          .set(DiscrepancySeals, sealsList(98))
          .success
          .value

        val result = AddAnotherSealViewModel(userAnswers, None, NormalMode)

        result.numberOfSeals shouldBe 98
        result.remainingSeals shouldBe 1
        result.allowMore shouldBe true
        result.addedSingularOrPlural shouldBe "plural"
        result.remainingSingularOrPlural shouldBe "singular"
        result.currentPage shouldBe 1
        result.totalPages shouldBe 4
        result.firstSeal shouldBe 1
        result.lastSeal shouldBe 25
        result.pageNumbers shouldBe (1 to 2)

      }

      "should provide the correct model when the maximum number of items has been reached" in {
        val userAnswers = emptyUserAnswers
          .set(DiscrepancySeals, sealsList(maxSeals))
          .success
          .value

        val result = AddAnotherSealViewModel(userAnswers, None, NormalMode)

        result.numberOfSeals shouldBe 99
        result.remainingSeals shouldBe 0
        result.allowMore shouldBe false
        result.addedSingularOrPlural shouldBe "plural"
        result.remainingSingularOrPlural shouldBe "plural"
        result.currentPage shouldBe 1
        result.totalPages shouldBe 4
        result.firstSeal shouldBe 1
        result.lastSeal shouldBe 25
        result.pageNumbers shouldBe (1 to 2)
      }

      "should put 25 seals on a single page with no pagination" in {
        val userAnswers = emptyUserAnswers
          .set(DiscrepancySeals, sealsList(25))
          .success
          .value

        val result = AddAnotherSealViewModel(userAnswers, None, NormalMode)

        result.currentPage shouldBe 1
        result.totalPages shouldBe 1
        result.rows.size shouldBe 25
        result.firstSeal shouldBe 1
        result.lastSeal shouldBe 25
        result.hasPreviousPage shouldBe false
        result.hasNextPage shouldBe false
        result.pageParam shouldBe None
        result.pagination(NormalMode) shouldBe None
      }

      "should start pagination at 26 seals, showing 25 rows on page 1" in {
        val userAnswers = emptyUserAnswers
          .set(DiscrepancySeals, sealsList(26))
          .success
          .value

        val result = AddAnotherSealViewModel(userAnswers, None, NormalMode)

        result.currentPage shouldBe 1
        result.totalPages shouldBe 2
        result.rows.size shouldBe 25
        result.firstSeal shouldBe 1
        result.lastSeal shouldBe 25
        result.pageNumbers shouldBe (1 to 2)
        result.hasPreviousPage shouldBe false
        result.hasNextPage shouldBe true
        result.nextPage shouldBe 2
        result.pageParam shouldBe Some(1)
      }

      "should omit Previous on the first page" in {
        val userAnswers = emptyUserAnswers
          .set(DiscrepancySeals, sealsList(26))
          .success
          .value

        val pagination = AddAnotherSealViewModel(userAnswers, Some(1), NormalMode).pagination(NormalMode).value

        pagination.previous shouldBe None
        pagination.next.value.href shouldBe createRoute.AddAnotherSealController.onPageLoad(Some(2), NormalMode).url
      }

      "should include both Previous and Next on a middle page" in {
        val userAnswers = emptyUserAnswers
          .set(DiscrepancySeals, sealsList(75))
          .success
          .value

        val pagination = AddAnotherSealViewModel(userAnswers, Some(2), NormalMode).pagination(NormalMode).value

        pagination.previous.value.href shouldBe createRoute.AddAnotherSealController.onPageLoad(Some(1), NormalMode).url
        pagination.previous.value.text shouldBe Some(messages("site.pagination.previous"))
        pagination.next.value.href shouldBe createRoute.AddAnotherSealController.onPageLoad(Some(3), NormalMode).url
        pagination.next.value.text shouldBe Some(messages("site.pagination.next"))
      }

      "should omit Next on the last page" in {
        val userAnswers = emptyUserAnswers
          .set(DiscrepancySeals, sealsList(26))
          .success
          .value

        val pagination = AddAnotherSealViewModel(userAnswers, Some(2), NormalMode).pagination(NormalMode).value

        pagination.next shouldBe None
        pagination.previous.value.href shouldBe createRoute.AddAnotherSealController.onPageLoad(Some(1), NormalMode).url
      }

      "should show only the remaining seal on a partial final page" in {
        val userAnswers = emptyUserAnswers
          .set(DiscrepancySeals, sealsList(26))
          .success
          .value

        val result = AddAnotherSealViewModel(userAnswers, Some(2), NormalMode)

        result.currentPage shouldBe 2
        result.rows.size shouldBe 1
        result.rows.head.key.content shouldBe Text("seal26")
        result.firstSeal shouldBe 26
        result.lastSeal shouldBe 26
        result.hasPreviousPage shouldBe true
        result.previousPage shouldBe 1
        result.hasNextPage shouldBe false
        result.pageParam shouldBe Some(2)
      }
    }
  }

}
