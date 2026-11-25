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
import uk.gov.hmrc.automatedexportsystemfrontend.models.{UserAnswers, WhatHasChangedDetails}
import uk.gov.hmrc.automatedexportsystemfrontend.pages.create.*

class DiscrepancyGoodsSpec extends SpecBase {

  val userAnswers: UserAnswers = emptyUserAnswers
    .set(DiscrepancyGoodsPage(1), WhatHasChangedDetails(Some(1), Some("5GB000000000000-123451"), "20", "10"))
    .flatMap(_.set(DiscrepancyGoodsPage(2), WhatHasChangedDetails(Some(2), Some("5GB000000000000-123452"), "20", "10")))
    .flatMap(_.set(DiscrepancyGoodsPage(3), WhatHasChangedDetails(Some(3), Some("5GB000000000000-123453"), "20", "10")))
    .success
    .value

  "DiscrepancyGoods" - {
    ".getAll" - {
      "must get all of the DiscrepancyGoods data" in {
        val expected = List(
          WhatHasChangedDetails(Some(1), Some("5GB000000000000-123451"), "20", "10"),
          WhatHasChangedDetails(Some(2), Some("5GB000000000000-123452"), "20", "10"),
          WhatHasChangedDetails(Some(3), Some("5GB000000000000-123453"), "20", "10")
        )

        DiscrepancyGoods.getAll(userAnswers) mustBe expected
      }
    }

    ".count" - {
      "must return how many DiscrepancyGoods data items have been added" in {
        DiscrepancyGoods.count(userAnswers) mustBe 3
      }
    }

    ".remaining" - {
      "must return how many remaining DiscrepancyGoods data items can be added" in {
        DiscrepancyGoods.remaining(userAnswers) mustBe 996
      }
    }

    ".maxPackagingDetails" - {
      "must have a maximum of 99" in {
        DiscrepancyGoods.maxDiscrepancyGoods mustBe 999
      }
    }

    ".exists" - {
      "must return true if the index is defined" in {
        DiscrepancyGoods.exists(userAnswers, 3) mustBe true
      }

      "must return false if the index is not defined" in {
        DiscrepancyGoods.exists(userAnswers, 4) mustBe false
      }
    }

    ".nextIndex" - {
      "must return the next empty index positon" in {
        DiscrepancyGoods.nextIndex(userAnswers) mustBe 4
      }
    }

    ".removeOne" - {
      "must remove one DiscrepancyGoods item at the correct index" in {
        val updatedUserAnswers = DiscrepancyGoods.removeOne(userAnswers, 2).success.value

        val expected = List(
          WhatHasChangedDetails(Some(1), Some("5GB000000000000-123451"), "20", "10"),
          WhatHasChangedDetails(Some(3), Some("5GB000000000000-123453"), "20", "10")
        )

        DiscrepancyGoods.getAll(updatedUserAnswers) mustBe expected
      }
    }

    "must be able to remove all DiscrepancyGoods data from user answers" in {
      val updatedUserAnswers = userAnswers.remove(DiscrepancyGoods).success.value

      updatedUserAnswers.get(DiscrepancyGoods) mustNot be(defined)
    }

  }
}
