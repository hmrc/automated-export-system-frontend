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
import uk.gov.hmrc.automatedexportsystemfrontend.pages.create.*

class DiscrepancySealsSpec extends SpecBase {

  private val userAnswers = emptyUserAnswers
    .set(DiscrepancySealsPage(1), "Seal1")
    .flatMap(_.set(DiscrepancySealsPage(2), "Seal2"))
    .flatMap(_.set(DiscrepancySealsPage(3), "Seal3"))
    .success
    .value

  "DiscrepancySeals" - {
    ".getAll" - {
      "must get all of the DiscrepancySeals data" in {
        val expected = List("Seal1", "Seal2", "Seal3")

        DiscrepancySeals.getAll(userAnswers) mustBe expected
      }
    }

    ".count" - {
      "must return how many DiscrepancySeals data items have been added" in {
        DiscrepancySeals.count(userAnswers) mustBe 3
      }
    }

    ".remaining" - {
      "must return how many remaining DiscrepancySeals data items can be added" in {
        DiscrepancySeals.remaining(userAnswers) mustBe 96
      }
    }

    "must be able to remove all DiscrepancySeals data from user answers" in {
      val updatedUserAnswers = userAnswers.remove(DiscrepancySeals).success.value

      updatedUserAnswers.get(DiscrepancySeals) mustNot be(defined)
    }

  }
}
