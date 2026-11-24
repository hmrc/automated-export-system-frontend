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

package uk.gov.hmrc.automatedexportsystemfrontend.queries

import org.scalatest.matchers.must.Matchers.{mustBe, mustNot}
import uk.gov.hmrc.automatedexportsystemfrontend.helpers.SpecBase
import uk.gov.hmrc.automatedexportsystemfrontend.models.{PackingDetails, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.pages.create.*

class DiscrepancyPackingSpec extends SpecBase {

  val goodsItemIndex = 1

  val userAnswers: UserAnswers = emptyUserAnswers
    .set(DiscrepancyPackingPage(goodsItemIndex, 1), PackingDetails("BX", 1, "MARKS1"))
    .flatMap(_.set(DiscrepancyPackingPage(goodsItemIndex, 2), PackingDetails("CT", 2, "MARKS2")))
    .flatMap(_.set(DiscrepancyPackingPage(goodsItemIndex, 3), PackingDetails("PK", 3, "MARKS3")))
    .success
    .value

  "DiscrepancyPacking" - {
    ".getAll" - {
      "must get all of the DiscrepancyPacking data" in {
        val expected = List(PackingDetails("BX", 1, "MARKS1"), PackingDetails("CT", 2, "MARKS2"), PackingDetails("PK", 3, "MARKS3"))

        DiscrepancyPacking(goodsItemIndex).getAll(userAnswers) mustBe expected
      }
    }

    ".count" - {
      "must return how many DiscrepancyPacking data items have been added" in {
        DiscrepancyPacking(goodsItemIndex).count(userAnswers) mustBe 3
      }
    }

    ".remaining" - {
      "must return how many remaining DiscrepancyPacking data items can be added" in {
        DiscrepancyPacking(goodsItemIndex).remaining(userAnswers) mustBe 96
      }
    }

    ".maxPackagingDetails" - {
      "must have a maximum of 99" in {
        DiscrepancyPacking(goodsItemIndex).maxPackagingDetails mustBe 99
      }
    }

    ".exists" - {
      "must return true if the index is defined" in {
        DiscrepancyPacking(goodsItemIndex).exists(userAnswers, 3) mustBe true
      }

      "must return false if the index is not defined" in {
        DiscrepancyPacking(goodsItemIndex).exists(userAnswers, 4) mustBe false
      }
    }

    ".nextIndex" - {
      "must return the next empty index positon" in {
        DiscrepancyPacking(goodsItemIndex).nextIndex(userAnswers) mustBe 4
      }
    }

    ".removeOne" - {
      "must remove one DiscrepancyPacking(goodsItemIndex) item at the correct index" in {
        val updatedUserAnswers = DiscrepancyPacking(goodsItemIndex).removeOne(userAnswers, 2).success.value

        val expected = List(PackingDetails("BX", 1, "MARKS1"), PackingDetails("PK", 3, "MARKS3"))

        DiscrepancyPacking(goodsItemIndex).getAll(updatedUserAnswers) mustBe expected
      }
    }

    "must be able to remove all DiscrepancyPacking data from user answers" in {
      val updatedUserAnswers = userAnswers.remove(DiscrepancyPacking(goodsItemIndex)).success.value

      updatedUserAnswers.get(DiscrepancyPacking(goodsItemIndex)) mustNot be(defined)
    }

  }
}
