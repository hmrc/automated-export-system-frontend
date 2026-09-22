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
import uk.gov.hmrc.automatedexportsystemfrontend.models.PackingDetails
import uk.gov.hmrc.automatedexportsystemfrontend.pages.create.*

class DiscrepancyPackingSpec extends SpecBase {

  "DiscrepancyPacking" - {
    "must getAll DiscrepancyPacking data" in {
      val userAnswers = emptyUserAnswers
        .set(DiscrepancyPackingPage(1), PackingDetails("BX", 1, "MARKS1"))
        .flatMap(_.set(DiscrepancyPackingPage(2), PackingDetails("CT", 2, "MARKS2")))
        .flatMap(_.set(DiscrepancyPackingPage(3), PackingDetails("PK", 3, "MARKS3")))
        .success
        .value

      val expected = List(PackingDetails("BX", 1, "MARKS1"), PackingDetails("CT", 2, "MARKS2"), PackingDetails("PK", 3, "MARKS3"))

      DiscrepancyPacking.getAll(userAnswers) mustBe expected
    }

    "must count how many DiscrepancyPacking data items" in {
      val userAnswers = emptyUserAnswers
        .set(DiscrepancyPackingPage(1), PackingDetails("BX", 1, "MARKS1"))
        .flatMap(_.set(DiscrepancyPackingPage(2), PackingDetails("CT", 2, "MARKS2")))
        .flatMap(_.set(DiscrepancyPackingPage(3), PackingDetails("PK", 3, "MARKS3")))
        .success
        .value

      DiscrepancyPacking.count(userAnswers) mustBe 3
    }

    "must remove DiscrepancyPacking data" in {
      val userAnswers = emptyUserAnswers
        .set(DiscrepancyPackingPage(1), PackingDetails("BX", 1, "MARKS1"))
        .flatMap(_.set(DiscrepancyPackingPage(2), PackingDetails("CT", 2, "MARKS2")))
        .flatMap(_.set(DiscrepancyPackingPage(3), PackingDetails("PK", 3, "MARKS3")))
        .success
        .value

      val updatedUserAnswers = userAnswers.remove(DiscrepancyPacking).success.value

      updatedUserAnswers.get(DiscrepancyPacking) mustNot be(defined)
    }

  }
}
