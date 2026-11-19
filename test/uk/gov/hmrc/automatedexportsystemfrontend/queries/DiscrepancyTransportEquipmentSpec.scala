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
import uk.gov.hmrc.automatedexportsystemfrontend.models.{ContainerDetails, TransportEquipmentData, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.pages.create.*
import uk.gov.hmrc.automatedexportsystemfrontend.queries.DiscrepancyTransportEquipment

class DiscrepancyTransportEquipmentSpec extends SpecBase {

  val userAnswers: UserAnswers = emptyUserAnswers
    .set(DiscrepancyTransportPage(1), ContainerDetails(Some("CONT1"), Some(1)))
    .flatMap(_.set(DiscrepancySealsPage(1, 1), "sealId1"))
    .flatMap(_.set(DiscrepancyTransportPage(2), ContainerDetails(Some("CONT2"), Some(2))))
    .flatMap(_.set(DiscrepancyReferencePage(2, 1), "12345"))
    .flatMap(_.set(DiscrepancyTransportPage(3), ContainerDetails(Some("CONT3"), Some(3))))
    .flatMap(_.set(DiscrepancySealsPage(3, 1), "sealId2"))
    .flatMap(_.set(DiscrepancyReferencePage(3, 1), "23456"))
    .flatMap(_.set(DiscrepancyTransportPage(4), ContainerDetails(Some("CONT4"), Some(4))))
    .flatMap(_.set(DiscrepancyTransportPage(5), ContainerDetails(None, None)))
    .flatMap(_.set(DiscrepancySealsPage(5, 1), "sealId3"))
    .flatMap(_.set(DiscrepancySealsPage(5, 2), "sealId4"))
    .flatMap(_.set(DiscrepancyReferencePage(5, 1), "34567"))
    .flatMap(_.set(DiscrepancyReferencePage(5, 2), "45678"))
    .success
    .value

  "DiscrepancyReferenceEquipment" - {
    ".getAll" - {
      "must get all of the DiscrepancyTransportEquipment data" in {
        val expected = List(
          TransportEquipmentData(Some(ContainerDetails(Some("CONT1"), Some(1))), List("sealId1"), List.empty),
          TransportEquipmentData(Some(ContainerDetails(Some("CONT2"), Some(2))), List.empty, List("12345")),
          TransportEquipmentData(Some(ContainerDetails(Some("CONT3"), Some(3))), List("sealId2"), List("23456")),
          TransportEquipmentData(Some(ContainerDetails(Some("CONT4"), Some(4))), List.empty, List.empty),
          TransportEquipmentData(Some(ContainerDetails(None, None)), List("sealId3", "sealId4"), List("34567", "45678"))
        )

        DiscrepancyTransportEquipment.getAll(userAnswers) mustBe expected
      }
    }

    ".count" - {
      "must return how many DiscrepancyTransportEquipment data items have been added" in {
        DiscrepancyTransportEquipment.count(userAnswers) mustBe 5
      }
    }

    ".remaining" - {
      "must return how many remaining DiscrepancyTransportEquipment data items can be added" in {
        DiscrepancyTransportEquipment.remaining(userAnswers) mustBe 9994
      }
    }

    ".maxDiscrepancyTransportEquipment" - {
      "must have a maximum of 9999" in {
        DiscrepancyTransportEquipment.maxTransportEquipment mustBe 9999
      }
    }

    ".exists" - {
      "must return true if the index is defined" in {
        DiscrepancyTransportEquipment.exists(userAnswers, 5) mustBe true
      }

      "must return false if the index is not defined" in {
        DiscrepancyTransportEquipment.exists(userAnswers, 6) mustBe false
      }
    }

    ".nextIndex" - {
      "must return the next empty index positon" in {
        DiscrepancyTransportEquipment.nextIndex(userAnswers) mustBe 6
      }
    }

    ".removeOne" - {
      "must remove one DiscrepancyTransportEquipment item at the correct index" in {
        val updatedUserAnswers = DiscrepancyTransportEquipment.removeOne(userAnswers, 2).success.value

        val expected = List(
          TransportEquipmentData(Some(ContainerDetails(Some("CONT1"), Some(1))), List("sealId1"), List.empty),
          TransportEquipmentData(Some(ContainerDetails(Some("CONT3"), Some(3))), List("sealId2"), List("23456")),
          TransportEquipmentData(Some(ContainerDetails(Some("CONT4"), Some(4))), List.empty, List.empty),
          TransportEquipmentData(Some(ContainerDetails(None, None)), List("sealId3", "sealId4"), List("34567", "45678"))
        )

        DiscrepancyTransportEquipment.getAll(updatedUserAnswers) mustBe expected
      }
    }

    "must be able to remove all DiscrepancyTransportEquipment data from user answers" in {
      val updatedUserAnswers = userAnswers.remove(DiscrepancyTransportEquipment).success.value

      updatedUserAnswers.get(DiscrepancyTransportEquipment) mustNot be(defined)
    }

  }
}
