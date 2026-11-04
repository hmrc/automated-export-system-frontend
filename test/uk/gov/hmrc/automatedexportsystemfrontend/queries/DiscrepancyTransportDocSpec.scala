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
import uk.gov.hmrc.automatedexportsystemfrontend.models.DocumentDetails
import uk.gov.hmrc.automatedexportsystemfrontend.pages.create.*

class DiscrepancyTransportDocSpec extends SpecBase {

  "DiscrepancyTransportDoc" - {
    ".getAll" - {
      "must get all of the DiscrepancyTransportDoc data" in {
        val userAnswers = emptyUserAnswers
          .set(DiscrepancyTransportDocPage(1), DocumentDetails(Some(1), Some(1)))
          .flatMap(_.set(DiscrepancyTransportDocPage(2), DocumentDetails(Some(2), Some(2))))
          .flatMap(_.set(DiscrepancyTransportDocPage(3), DocumentDetails(Some(3), Some(3))))
          .success
          .value

        val expected = List(DocumentDetails(Some(1), Some(1)), DocumentDetails(Some(2), Some(2)), DocumentDetails(Some(3), Some(3)))

        DiscrepancyTransportDoc.getAll(userAnswers) mustBe expected
      }
    }

    ".count" - {
      "must return how many DiscrepancyTransportDoc data items have been added" in {
        val userAnswers = emptyUserAnswers
          .set(DiscrepancyTransportDocPage(1), DocumentDetails(Some(1), Some(1)))
          .flatMap(_.set(DiscrepancyTransportDocPage(2), DocumentDetails(Some(2), Some(2))))
          .flatMap(_.set(DiscrepancyTransportDocPage(3), DocumentDetails(Some(3), Some(3))))
          .success
          .value

        DiscrepancyTransportDoc.count(userAnswers) mustBe 3
      }
    }

    ".remaining" - {
      "must return how many remaining DiscrepancyTransportDoc data items can be added" in {
        val userAnswers = emptyUserAnswers
          .set(DiscrepancyTransportDocPage(1), DocumentDetails(Some(1), Some(1)))
          .flatMap(_.set(DiscrepancyTransportDocPage(2), DocumentDetails(Some(2), Some(2))))
          .flatMap(_.set(DiscrepancyTransportDocPage(3), DocumentDetails(Some(3), Some(3))))
          .success
          .value

        DiscrepancyTransportDoc.remaining(userAnswers) mustBe 96
      }
    }

    ".maxPackagingDetails" - {
      "must have a maximum of 99" in {
        DiscrepancyTransportDoc.maxTransportDocuments mustBe 99
      }
    }

    ".exists" - {
      "must return true if the index is defined" in {
        val userAnswers = emptyUserAnswers
          .set(DiscrepancyTransportDocPage(1), DocumentDetails(Some(1), Some(1)))
          .flatMap(_.set(DiscrepancyTransportDocPage(2), DocumentDetails(Some(2), Some(2))))
          .flatMap(_.set(DiscrepancyTransportDocPage(3), DocumentDetails(Some(3), Some(3))))
          .success
          .value

        DiscrepancyTransportDoc.exists(userAnswers, 3) mustBe true
      }

      "must return false if the index is not defined" in {
        val userAnswers = emptyUserAnswers
          .set(DiscrepancyTransportDocPage(1), DocumentDetails(Some(1), Some(1)))
          .flatMap(_.set(DiscrepancyTransportDocPage(2), DocumentDetails(Some(2), Some(2))))
          .flatMap(_.set(DiscrepancyTransportDocPage(3), DocumentDetails(Some(3), Some(3))))
          .success
          .value

        DiscrepancyTransportDoc.exists(userAnswers, 4) mustBe false
      }
    }

    ".nextIndex" - {
      "must return the next empty index positon" in {
        val userAnswers = emptyUserAnswers
          .set(DiscrepancyTransportDocPage(1), DocumentDetails(Some(1), Some(1)))
          .flatMap(_.set(DiscrepancyTransportDocPage(2), DocumentDetails(Some(2), Some(2))))
          .flatMap(_.set(DiscrepancyTransportDocPage(3), DocumentDetails(Some(3), Some(3))))
          .success
          .value

        DiscrepancyTransportDoc.nextIndex(userAnswers) mustBe 4
      }
    }

    ".removeOne" - {
      "must remove one DiscrepancyTransportDoc item at the correct index" in {
        val userAnswers = emptyUserAnswers
          .set(DiscrepancyTransportDocPage(1), DocumentDetails(Some(1), Some(1)))
          .flatMap(_.set(DiscrepancyTransportDocPage(2), DocumentDetails(Some(2), Some(2))))
          .flatMap(_.set(DiscrepancyTransportDocPage(3), DocumentDetails(Some(3), Some(3))))
          .success
          .value

        val updatedUserAnswers = DiscrepancyTransportDoc.removeOne(userAnswers, 2).success.value

        val expected = List(DocumentDetails(Some(1), Some(1)), DocumentDetails(Some(3), Some(3)))

        DiscrepancyTransportDoc.getAll(updatedUserAnswers) mustBe expected
      }
    }

    "must be able to remove all DiscrepancyTransportDoc data from user answers" in {
      val userAnswers = emptyUserAnswers
        .set(DiscrepancyTransportDocPage(1), DocumentDetails(Some(1), Some(1)))
        .flatMap(_.set(DiscrepancyTransportDocPage(2), DocumentDetails(Some(2), Some(2))))
        .flatMap(_.set(DiscrepancyTransportDocPage(3), DocumentDetails(Some(3), Some(3))))
        .success
        .value

      val updatedUserAnswers = userAnswers.remove(DiscrepancyTransportDoc).success.value

      updatedUserAnswers.get(DiscrepancyTransportDoc) mustNot be(defined)
    }

  }
}
