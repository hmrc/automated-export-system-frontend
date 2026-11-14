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
import uk.gov.hmrc.automatedexportsystemfrontend.models.{ContainerDetails, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.pages.create.*
import uk.gov.hmrc.automatedexportsystemfrontend.queries.DiscrepancyTransportEquipment

class DiscrepancyTransportEquipmentSpec extends SpecBase {

  val userAnswers: UserAnswers = emptyUserAnswers
    .set(DiscrepancyTransportPage(1), ContainerDetails(Some("containerId1"), Some(1)))
    .flatMap(_.set(DiscrepancyTransportPage(2), ContainerDetails(Some("containerId2"), Some(2))))
    .flatMap(_.set(DiscrepancyTransportPage(3), ContainerDetails(Some("containerId3"), Some(3))))
    .success
    .value

  "DiscrepancyReferenceEquipment" - {
    ".getAll" - {
      "must get all of the DiscrepancyTransportEquipment data" in {
        val expected = List(
          ContainerDetails(Some("containerId1"), Some(1)),
          ContainerDetails(Some("containerId2"), Some(2)),
          ContainerDetails(Some("containerId3"), Some(3))
        )

        DiscrepancyTransportEquipment.getAll(userAnswers) mustBe expected
      }
    }

    ".count" - {
      "must return how many DiscrepancyTransportEquipment data items have been added" in {
        DiscrepancyTransportEquipment.count(userAnswers) mustBe 3
      }
    }

    ".remaining" - {
      "must return how many remaining DiscrepancyTransportEquipment data items can be added" in {
        DiscrepancyTransportEquipment.remaining(userAnswers) mustBe 9996
      }
    }

    ".maxDiscrepancyTransportEquipment" - {
      "must have a maximum of 9999" in {
        DiscrepancyTransportEquipment.maxTransportEquipment mustBe 9999
      }
    }

    ".exists" - {
      "must return true if the index is defined" in {
        DiscrepancyTransportEquipment.exists(userAnswers, 3) mustBe true
      }

      "must return false if the index is not defined" in {
        DiscrepancyTransportEquipment.exists(userAnswers, 4) mustBe false
      }
    }

    ".nextIndex" - {
      "must return the next empty index positon" in {
        DiscrepancyTransportEquipment.nextIndex(userAnswers) mustBe 4
      }
    }

    ".removeOne" - {
      "must remove one DiscrepancyTransportEquipment item at the correct index" in {
        val updatedUserAnswers = DiscrepancyTransportEquipment.removeOne(userAnswers, 2).success.value

        val expected = List(ContainerDetails(Some("containerId1"), Some(1)), ContainerDetails(Some("containerId3"), Some(3)))

        DiscrepancyTransportEquipment.getAll(updatedUserAnswers) mustBe expected
      }
    }

    "must be able to remove all DiscrepancyTransportEquipment data from user answers" in {
      val updatedUserAnswers = userAnswers.remove(DiscrepancyTransportEquipment).success.value

      updatedUserAnswers.get(DiscrepancyTransportEquipment) mustNot be(defined)
    }

  }
}
