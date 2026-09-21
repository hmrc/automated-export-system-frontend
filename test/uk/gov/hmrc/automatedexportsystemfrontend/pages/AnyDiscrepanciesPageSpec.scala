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

package uk.gov.hmrc.automatedexportsystemfrontend.pages

import org.scalatest.matchers.must.Matchers.{mustBe, mustNot}
import uk.gov.hmrc.automatedexportsystemfrontend.helpers.SpecBase
import uk.gov.hmrc.automatedexportsystemfrontend.models.*
import uk.gov.hmrc.automatedexportsystemfrontend.pages.create.*

class AnyDiscrepanciesPageSpec extends SpecBase {

  val index = 0

  "AnyDiscrepanciesPage" - {
    "cleanup" - {
      "when no is selected" - {
        "must remove the discrepancy answers" in {
          val userAnswers = emptyUserAnswers
            .set(DiscrepancyConsignmentPage, ModeOfTransportAtBorder.Sea)
            .success
            .value
            .set(DiscrepancyTransportPage, ContainerDetails("containerId", 99))
            .success
            .value
            .set(DiscrepancySealsPage, "sealsIdentifier")
            .success
            .value
            .set(DiscrepancyReferencePage, "goodsRef")
            .success
            .value
            .set(DiscrepancyTransportMeansPage, TransportAcrossBorderDetails("transportType", "transportId", "countryOfReg"))
            .success
            .value
            .set(DiscrepancyTransportDocPage, DocumentDetails(Some(1), Some(1234)))
            .success
            .value
            .set(DiscrepancyGoodsPage, WhatHasChangedDetails(Some(1), Some("ducr"), "newGrossMass", "newNetMass"))
            .success
            .value
            .set(DiscrepancyPackingPage(index), PackingDetails("packagingCode", 2, "shippingMarks"))
            .success
            .value

          val result = AnyDiscrepanciesPage.cleanup(Some(false), userAnswers).success.value

          result.get(DiscrepancyConsignmentPage) mustNot be(defined)
          result.get(DiscrepancyTransportPage) mustNot be(defined)
          result.get(DiscrepancySealsPage) mustNot be(defined)
          result.get(DiscrepancyReferencePage) mustNot be(defined)
          result.get(DiscrepancyTransportMeansPage) mustNot be(defined)
          result.get(DiscrepancyTransportDocPage) mustNot be(defined)
          result.get(DiscrepancyGoodsPage) mustNot be(defined)
          result.get(DiscrepancyPackingPage(index)) mustNot be(defined)
        }
      }

      "when yes is selected" - {
        "must not remove the discrepancy answers" in {
          val userAnswers = emptyUserAnswers
            .set(DiscrepancyConsignmentPage, ModeOfTransportAtBorder.Sea)
            .success
            .value
            .set(DiscrepancyTransportPage, ContainerDetails("containerId", 99))
            .success
            .value
            .set(DiscrepancySealsPage, "sealsIdentifier")
            .success
            .value
            .set(DiscrepancyReferencePage, "goodsRef")
            .success
            .value
            .set(DiscrepancyTransportMeansPage, TransportAcrossBorderDetails("transportType", "transportId", "countryOfReg"))
            .success
            .value
            .set(DiscrepancyTransportDocPage, DocumentDetails(Some(1), Some(1234)))
            .success
            .value
            .set(DiscrepancyGoodsPage, WhatHasChangedDetails(Some(1), Some("ducr"), "newGrossMass", "newNetMass"))
            .success
            .value
            .set(DiscrepancyPackingPage(index), PackingDetails("packagingCode", 2, "shippingMarks"))
            .success
            .value

          val result = AnyDiscrepanciesPage.cleanup(Some(true), userAnswers).success.value

          result.get(DiscrepancyConsignmentPage) mustBe defined
          result.get(DiscrepancyTransportPage) mustBe defined
          result.get(DiscrepancySealsPage) mustBe defined
          result.get(DiscrepancyReferencePage) mustBe defined
          result.get(DiscrepancyTransportMeansPage) mustBe defined
          result.get(DiscrepancyTransportDocPage) mustBe defined
          result.get(DiscrepancyGoodsPage) mustBe defined
          result.get(DiscrepancyPackingPage(index)) mustBe defined
        }
      }

    }

  }
}
