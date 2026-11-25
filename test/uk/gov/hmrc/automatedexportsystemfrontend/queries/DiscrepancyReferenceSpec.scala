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
import uk.gov.hmrc.automatedexportsystemfrontend.models.UserAnswers
import uk.gov.hmrc.automatedexportsystemfrontend.pages.create.*

class DiscrepancyReferenceSpec extends SpecBase {

  val transportEquipmentIndex: Int = 1

  val userAnswers: UserAnswers = emptyUserAnswers
    .set(DiscrepancyReferencePage(transportEquipmentIndex, 1), "12345")
    .flatMap(_.set(DiscrepancyReferencePage(transportEquipmentIndex, 2), "23456"))
    .flatMap(_.set(DiscrepancyReferencePage(transportEquipmentIndex, 3), "34567"))
    .success
    .value

  "DiscrepancyReference" - {
    ".getAll" - {
      "must get all of the DiscrepancyReference data" in {
        val expected = List("12345", "23456", "34567")

        DiscrepancyReference(transportEquipmentIndex).getAll(userAnswers) mustBe expected
      }
    }

    ".count" - {
      "must return how many DiscrepancyReference data items have been added" in {
        DiscrepancyReference(transportEquipmentIndex).count(userAnswers) mustBe 3
      }
    }

    ".remaining" - {
      "must return how many remaining DiscrepancyReference data items can be added" in {
        DiscrepancyReference(transportEquipmentIndex).remaining(userAnswers) mustBe 996
      }
    }

    ".maxDiscrepancyReferences" - {
      "must have a maximum of 999" in {
        DiscrepancyReference(transportEquipmentIndex).maxDiscrepancyReferences mustBe 999
      }
    }

    ".exists" - {
      "must return true if the index is defined" in {
        DiscrepancyReference(transportEquipmentIndex).exists(userAnswers, 3) mustBe true
      }

      "must return false if the index is not defined" in {
        DiscrepancyReference(transportEquipmentIndex).exists(userAnswers, 4) mustBe false
      }
    }

    ".nextIndex" - {
      "must return the next empty index positon" in {
        DiscrepancyReference(transportEquipmentIndex).nextIndex(userAnswers) mustBe 4
      }
    }

    ".removeOne" - {
      "must remove one DiscrepancyReference item at the correct index" in {
        val updatedUserAnswers = DiscrepancyReference(transportEquipmentIndex).removeOne(userAnswers, 2).success.value

        val expected = List("12345", "34567")

        DiscrepancyReference(transportEquipmentIndex).getAll(updatedUserAnswers) mustBe expected
      }
    }

  }
}
