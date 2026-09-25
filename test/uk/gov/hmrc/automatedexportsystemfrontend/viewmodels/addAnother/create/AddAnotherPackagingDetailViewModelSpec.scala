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

import org.scalatest.matchers.must.Matchers.mustBe
import uk.gov.hmrc.automatedexportsystemfrontend.helpers.SpecBase
import uk.gov.hmrc.automatedexportsystemfrontend.models.PackingDetails
import uk.gov.hmrc.automatedexportsystemfrontend.pages.create.DiscrepancyPackingPage
import uk.gov.hmrc.automatedexportsystemfrontend.queries.DiscrepancyPacking

class AddAnotherPackagingDetailViewModelSpec extends SpecBase {

  "AddAnotherPackagingDetailViewModel" - {

    val maxPackagingDetails: Int = DiscrepancyPacking.maxPackagingDetails

    "must provide the correct model when one item has been added" in {
      val userAnswers = emptyUserAnswers.set(DiscrepancyPackingPage(1), PackingDetails("packagingCode1", 1, "shippingMarks1")).success.value

      val result = AddAnotherPackagingDetailViewModel(userAnswers)

      result.numberOfPackagingDetails mustBe 1
      result.remainingPackagingDetails mustBe 98
      result.allowMore mustBe true
      result.addedSingularOrPlural mustBe "singular"
      result.remainingSingularOrPlural mustBe "plural"
    }

    "must provide the correct model when one more item can be added" in {
      val packingDetailsList = (1 until maxPackagingDetails).map { number =>
        PackingDetails(s"packagingCode$number", number, s"shippingMarks$number")
      }.toList

      val userAnswers = emptyUserAnswers
        .set(DiscrepancyPacking, packingDetailsList)
        .success
        .value

      val result = AddAnotherPackagingDetailViewModel(userAnswers)

      result.numberOfPackagingDetails mustBe 98
      result.remainingPackagingDetails mustBe 1
      result.allowMore mustBe true
      result.addedSingularOrPlural mustBe "plural"
      result.remainingSingularOrPlural mustBe "singular"
    }

    "must provide the correct model when the maximum number of items has been reached" in {
      val packingDetailsList = (1 to maxPackagingDetails).map { number =>
        PackingDetails(s"packagingCode$number", number, s"shippingMarks$number")
      }.toList

      val userAnswers = emptyUserAnswers
        .set(DiscrepancyPacking, packingDetailsList)
        .success
        .value

      val result = AddAnotherPackagingDetailViewModel(userAnswers)

      result.numberOfPackagingDetails mustBe 99
      result.remainingPackagingDetails mustBe 0
      result.allowMore mustBe false
      result.addedSingularOrPlural mustBe "plural"
      result.remainingSingularOrPlural mustBe "plural"
    }
  }
}
