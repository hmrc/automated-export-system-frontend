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

package uk.gov.hmrc.automatedexportsystemfrontend.services

import uk.gov.hmrc.automatedexportsystemfrontend.helpers.SpecBase
import uk.gov.hmrc.automatedexportsystemfrontend.models.*
import uk.gov.hmrc.automatedexportsystemfrontend.pages.create.*

import scala.language.postfixOps

class SubmissionDataServiceSpec extends SpecBase {

  "buildStandardSubmission" - {

    val service = new SubmissionDataService

    "must return an String of XML with no GoodsShipment when the minimal set of required answers are present " in {
      val userAnswers = for {
        userAnswers <- emptyUserAnswers.set(EnterMrnPage, "MRN")
        userAnswers <- userAnswers.set(AnyDiscrepanciesPage, false)
        userAnswers <- userAnswers.set(IsSplitExitPage, false)
        userAnswers <- userAnswers.set(OfficeOfExitPage, OfficeOfExit.Belfast)
      } yield userAnswers

      val result = service.buildStandardSubmission(userAnswers.get)

      result shouldBe an[Option[String]]
      result.value should include("<MRN>MRN</MRN>")
      result.value should include("<type>1</type>")
      result.value should include("<discrepanciesExist>0</discrepanciesExist>")
      result.value should include("<splitIndicator>0</splitIndicator>")
      result.value should include("<referenceNumber>GB000051</referenceNumber>")
      result.value shouldNot include("<GoodsShipment>")
    }

    "must include a GoodsShipment when all the required answers are present" in {
      val userAnswers = for {
        userAnswers <- emptyUserAnswers.set(EnterMrnPage, "MRN")
        userAnswers <- userAnswers.set(AnyDiscrepanciesPage, false)
        userAnswers <- userAnswers.set(IsSplitExitPage, false)
        userAnswers <- userAnswers.set(OfficeOfExitPage, OfficeOfExit.Belfast)
        userAnswers <- userAnswers.set(DiscrepancyConsignmentPage, ModeOfTransportAtBorder.Sea)
        userAnswers <- userAnswers.set(EnterDucrPage, "5GB000000000000-12345")
        userAnswers <- userAnswers.set(PartOfConsolidationPage, PartOfConsolidationAnswer(true, Some("GB/000000000000-12345")))
        userAnswers <- userAnswers.set(DiscrepancyTransportPage, ContainerDetails("containerId", numberOfSeals = 1))
        userAnswers <- userAnswers.set(DiscrepancySealsPage, "sealId")
        userAnswers <- userAnswers.set(LocationTypePage, LocationType.AuthorisedPlace)
        userAnswers <- userAnswers.set(LocationIdPage, LocationDetails(LocationQualifier.UnLocode, "GBBEL", "locationId", "abc123"))
        userAnswers <- userAnswers.set(DiscrepancyTransportMeansPage, TransportAcrossBorderDetails("road", "transportId", "GB"))
        userAnswers <- userAnswers.set(DiscrepancyTransportDocPage, DocumentDetails("documentType", "documentReference"))
        userAnswers <- userAnswers.set(DiscrepancyReferencePage, "1")
        userAnswers <- userAnswers.set(DiscrepancyGoodsPage, WhatHasChangedDetails("1", Some("5GB000000000000-12345"), "20", "10"))
        userAnswers <- userAnswers.set(DiscrepancyPackingPage, PackingDetails("PK", "1", "marks"))
      } yield userAnswers

      val result = service.buildStandardSubmission(userAnswers.get)

      result shouldBe an[Option[String]]
      result.value should include("<MRN>MRN</MRN>")
      result.value should include("<type>1</type>")
      result.value should include("<discrepanciesExist>0</discrepanciesExist>")
      result.value should include("<splitIndicator>0</splitIndicator>")
      result.value should include("<referenceNumber>GB000051</referenceNumber>")

      result.value should include("<GoodsShipment>")
      result.value should include("<Consignment>")

      result.value should include("<modeOfTransportAtTheBorder>1</modeOfTransportAtTheBorder>")
      result.value should include("<referenceNumberUCR>5GB000000000000-12345</referenceNumberUCR>")
      result.value should include("<parentUCRID>GB/000000000000-12345</parentUCRID>")

      result.value should include("<TransportEquipment>")
      result.value should include("<sequenceNumber>1</sequenceNumber>")
      result.value should include("<containerIdentificationNumber>containerId</containerIdentificationNumber>")
      result.value should include("<numberOfSeals>1</numberOfSeals>")

      result.value should include("<Seal>")
      result.value should include("<identifier>sealId</identifier>")

      result.value should include("<GoodsReference>")
      result.value should include("<declarationGoodsItemNumber>1</declarationGoodsItemNumber>")

      result.value should include("<LocationOfGoods>")
      result.value should include("<typeOfLocation>B</typeOfLocation>")
      result.value should include("<qualifierOfIdentification>U</qualifierOfIdentification>")
      result.value should include("<authorisationNumber>abc123</authorisationNumber>")
      result.value should include("<additionalIdentifier>locationId</additionalIdentifier>")
      result.value should include("<UNLocode>GBBEL</UNLocode>")

      result.value should include("<ActiveBorderTransportMeans>")
      result.value should include("<type>road</type>")
      result.value should include("<identificationNumber>transportId</identificationNumber>")
      result.value should include("<nationality>GB</nationality>")

      result.value should include("<TransportDocument>")
      result.value should include("<type>documentType</type>")
      result.value should include("<referenceNumber>documentReference</referenceNumber>")

      result.value should include("<GoodsItem>")
      result.value should include("<declarationGoodsItemNumber>1</declarationGoodsItemNumber>")
      result.value should include("<declarationUniqueConsignmentReference>5GB000000000000-12345</declarationUniqueConsignmentReference>")

      result.value should include("<Commodity>")
      result.value should include("<grossMass>20</grossMass>")
      result.value should include("<netMass>10</netMass>")

      result.value should include("<Packaging>")
      result.value should include("<sequenceNumber>1</sequenceNumber>")
      result.value should include("<typeOfPackages>PK</typeOfPackages>")
      result.value should include("<numberOfPackages>1</numberOfPackages>")
      result.value should include("<shippingMarks>marks</shippingMarks>")
    }

    "must return a None when all required answers not present" in {

      val userAnswers = emptyUserAnswers.set(EnterMrnPage, "MRN").get

      service.buildStandardSubmission(userAnswers) shouldBe None
    }
  }
}
