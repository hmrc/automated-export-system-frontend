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

class CreateSubmissionDataServiceSpec extends SpecBase {

  "buildStandardSubmission" - {

    val service = new CreateSubmissionDataService
    val parentIndex: Int = 1
    val index = 1

    "must include a GoodsShipment for a split exit when AnyDiscrepanciesPage is not present" in {
      val userAnswers = for {
        userAnswers <- emptyUserAnswers.set(EnterMrnPage, "MRN")
        userAnswers <- userAnswers.set(IsSplitExitPage, true)
        userAnswers <- userAnswers.set(OfficeOfExitPage, OfficeOfExit.Belfast)
        userAnswers <- userAnswers.set(DiscrepancyConsignmentPage, "1")
        userAnswers <- userAnswers.set(EnterDucrPage, "5GB000000000000-12345")
        userAnswers <- userAnswers.set(PartOfConsolidationPage, PartOfConsolidationAnswer(true, Some("GB/000000000000-12345")))
        userAnswers <- userAnswers.set(DiscrepancyTransportPage(index), ContainerDetails(Some("containerId"), numberOfSeals = Some(1)))
        userAnswers <- userAnswers.set(DiscrepancySealsPage(parentIndex, index), "sealId")
        userAnswers <- userAnswers.set(LocationTypePage, LocationType.AuthorisedPlace)
        userAnswers <- userAnswers.set(LocationIdPage, LocationDetails(LocationQualifier.UnLocode, Some("GBBEL"), Some("locationId"), Some("abc123")))
        userAnswers <- userAnswers.set(DiscrepancyTransportMeansPage, TransportAcrossBorderDetails(Some("road"), Some("transportId"), Some("GB")))
        userAnswers <- userAnswers.set(DiscrepancyTransportDocPage(index), DocumentDetails(Some("1"), Some(1234)))
        userAnswers <- userAnswers.set(DiscrepancyGoodsPage(index), WhatHasChangedDetails(Some(1), Some("5GB000000000000-12345"), "20", "10"))
        userAnswers <- userAnswers.set(DiscrepancyReferencePage(parentIndex, index), "1")
        userAnswers <- userAnswers.set(DiscrepancyPackingPage(index), PackingDetails("PK", 1, "marks"))
      } yield userAnswers

      val result = service.buildStandardSubmission(userAnswers.get)

      result shouldBe defined
      result.value should include("<discrepanciesExist>1</discrepanciesExist>")
      result.value should include("<splitIndicator>1</splitIndicator>")
      result.value should include("<GoodsShipment>")
    }

    "must include DUCR and required location properties with minimal answers" in {
      Seq(LocationQualifier.UnLocode -> "U", LocationQualifier.AuthorisationNumber -> "Y").foreach { case (qualifier, expectedCode) =>
        val answers = (for {
          answers <- emptyUserAnswers.set(EnterMrnPage, "MRN")
          answers <- answers.set(AnyDiscrepanciesPage, false)
          answers <- answers.set(IsSplitExitPage, false)
          answers <- answers.set(OfficeOfExitPage, OfficeOfExit.Belfast)
          answers <- answers.set(EnterDucrPage, "5GB000000000000-12345")
          answers <- answers.set(LocationTypePage, LocationType.AuthorisedPlace)
          answers <- answers.set(LocationIdPage, LocationDetails(qualifier, None, None, None))
        } yield answers).get

        val result = service.buildStandardSubmission(answers)

        result shouldBe defined

        val consignment = scala.xml.XML.loadString(result.value) \ "GoodsShipment" \ "Consignment"
        val location = consignment \ "LocationOfGoods"

        consignment.size shouldBe 1
        (consignment \ "referenceNumberUCR").text shouldBe "5GB000000000000-12345"
        location.size shouldBe 1
        (location \ "typeOfLocation").text shouldBe "B"
        (location \ "qualifierOfIdentification").text shouldBe expectedCode
        (location \ "authorisationNumber") shouldBe empty
        (location \ "additionalIdentifier") shouldBe empty
        (location \ "UNLocode") shouldBe empty

        Seq(EnterDucrPage, LocationTypePage, LocationIdPage).foreach { page =>
          val incompleteAnswers = answers.remove(page).get

          service.buildStandardSubmission(incompleteAnswers) shouldBe None
        }
      }
    }

    "must include a GoodsShipment when all the required answers are present" in {
      val userAnswers = for {
        userAnswers <- emptyUserAnswers.set(EnterMrnPage, "MRN")
        userAnswers <- userAnswers.set(AnyDiscrepanciesPage, false)
        userAnswers <- userAnswers.set(IsSplitExitPage, false)
        userAnswers <- userAnswers.set(OfficeOfExitPage, OfficeOfExit.Belfast)
        userAnswers <- userAnswers.set(DiscrepancyConsignmentPage, "1")
        userAnswers <- userAnswers.set(EnterDucrPage, "5GB000000000000-12345")
        userAnswers <- userAnswers.set(PartOfConsolidationPage, PartOfConsolidationAnswer(true, Some("GB/000000000000-12345")))
        userAnswers <- userAnswers.set(DiscrepancyTransportPage(index), ContainerDetails(Some("containerId"), numberOfSeals = Some(1)))
        userAnswers <- userAnswers.set(DiscrepancySealsPage(parentIndex, index), "sealId")
        userAnswers <- userAnswers.set(LocationTypePage, LocationType.AuthorisedPlace)
        userAnswers <- userAnswers.set(LocationIdPage, LocationDetails(LocationQualifier.UnLocode, Some("GBBEL"), Some("1234"), Some("abc123")))
        userAnswers <- userAnswers.set(DiscrepancyTransportMeansPage, TransportAcrossBorderDetails(Some("road"), Some("transportId"), Some("GB")))
        userAnswers <- userAnswers.set(DiscrepancyTransportDocPage(index), DocumentDetails(Some("1"), Some(1234)))
        userAnswers <- userAnswers.set(DiscrepancyReferencePage(parentIndex, index), "1")
        userAnswers <- userAnswers.set(DiscrepancyGoodsPage(index), WhatHasChangedDetails(Some(1), Some("5GB000000000000-12345"), "20", "10"))
        userAnswers <- userAnswers.set(DiscrepancyPackingPage(index), PackingDetails("PK", 1, "marks"))
      } yield userAnswers

      val result = service.buildStandardSubmission(userAnswers.get)

      result shouldBe an[Option[String]]
      result.value should include("<MRN>MRN</MRN>")
      result.value should include("<type>1</type>")
      result.value should include("<discrepanciesExist>0</discrepanciesExist>")
      result.value should include("<splitIndicator>0</splitIndicator>")
      result.value should include("<referenceNumberUCR>5GB000000000000-12345</referenceNumberUCR>")

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
      result.value should include("<additionalIdentifier>1234</additionalIdentifier>")
      result.value should include("<UNLocode>GBBEL</UNLocode>")

      result.value should include("<ActiveBorderTransportMeans>")
      result.value should include("<typeOfIdentification>road</typeOfIdentification>")
      result.value should include("<identificationNumber>transportId</identificationNumber>")
      result.value should include("<nationality>GB</nationality>")

      result.value should include("<TransportDocument>")
      result.value should include("<type>1</type>")
      result.value should include("<referenceNumber>1234</referenceNumber>")

      result.value should include("<GoodsItem>")
      result.value should include("<declarationGoodsItemNumber>1</declarationGoodsItemNumber>")
      result.value should include("<referenceNumberUCR>5GB000000000000-12345</referenceNumberUCR>")

      result.value should include("<Commodity>")
      result.value should include("<GoodsMeasure>")
      result.value should include("<grossMass>20</grossMass>")
      result.value should include("<netMass>10</netMass>")

      result.value should include("<Packaging>")
      result.value should include("<sequenceNumber>1</sequenceNumber>")
      result.value should include("<typeOfPackages>PK</typeOfPackages>")
      result.value should include("<numberOfPackages>1</numberOfPackages>")
      result.value should include("<shippingMarks>marks</shippingMarks>")
    }

    "must omit GoodsReference when the reference is absent" in {
      val userAnswers = (for {
        answers <- emptyUserAnswers.set(EnterMrnPage, "MRN")
        answers <- answers.set(AnyDiscrepanciesPage, true)
        answers <- answers.set(IsSplitExitPage, false)
        answers <- answers.set(OfficeOfExitPage, OfficeOfExit.Belfast)
        answers <- answers.set(EnterDucrPage, "5GB000000000000-12345")
        answers <- answers.set(LocationTypePage, LocationType.AuthorisedPlace)
        answers <- answers.set(LocationIdPage, LocationDetails(LocationQualifier.UnLocode, Some("GBBEL"), Some("1234"), Some("abc123")))
        answers <- answers.set(DiscrepancyTransportPage(index), ContainerDetails(Some("CONT123"), None))
      } yield answers).get

      val result = service.buildStandardSubmission(userAnswers)

      result shouldBe defined

      val equipment =
        scala.xml.XML.loadString(result.value) \\ "TransportEquipment"

      equipment.size shouldBe 1
      (equipment \ "containerIdentificationNumber").text shouldBe "CONT123"
      (equipment \ "GoodsReference") shouldBe empty
    }

    "must omit Seal when the identifier is blank" in {
      val userAnswers = (for {
        answers <- emptyUserAnswers.set(EnterMrnPage, "MRN")
        answers <- answers.set(AnyDiscrepanciesPage, true)
        answers <- answers.set(IsSplitExitPage, false)
        answers <- answers.set(OfficeOfExitPage, OfficeOfExit.Belfast)
        answers <- answers.set(EnterDucrPage, "5GB000000000000-12345")
        answers <- answers.set(LocationTypePage, LocationType.AuthorisedPlace)
        answers <- answers.set(LocationIdPage, LocationDetails(LocationQualifier.UnLocode, Some("GBBEL"), Some("1234"), Some("abc123")))
        answers <- answers.set(DiscrepancyTransportPage(index), ContainerDetails(Some("CONT123"), None))
      } yield answers).get

      val result = service.buildStandardSubmission(userAnswers)

      result shouldBe defined

      val equipment = scala.xml.XML.loadString(result.value) \\ "TransportEquipment"

      equipment.size shouldBe 1
      (equipment \ "containerIdentificationNumber").text shouldBe "CONT123"
      (equipment \ "Seal") shouldBe empty
    }

    "must omit blank transport details and include only entered XML fields" in {
      val baseAnswers = (for {
        answers <- emptyUserAnswers.set(EnterMrnPage, "MRN")
        answers <- answers.set(AnyDiscrepanciesPage, true)
        answers <- answers.set(IsSplitExitPage, false)
        answers <- answers.set(OfficeOfExitPage, OfficeOfExit.Belfast)
        answers <- answers.set(EnterDucrPage, "5GB000000000000-12345")
        answers <- answers.set(LocationTypePage, LocationType.AuthorisedPlace)
        answers <- answers.set(LocationIdPage, LocationDetails(LocationQualifier.UnLocode, Some("GBBEL"), Some("1234"), Some("abc123")))
      } yield answers).get

      val cases = Seq(
        TransportAcrossBorderDetails(None, None, None),
        TransportAcrossBorderDetails(Some("10"), None, None),
        TransportAcrossBorderDetails(None, Some("SHIP123"), None),
        TransportAcrossBorderDetails(None, None, Some("GB")),
        TransportAcrossBorderDetails(Some("10"), Some("SHIP123"), Some("GB"))
      )

      cases.foreach { details =>
        val answers = baseAnswers.set(DiscrepancyTransportMeansPage, details).get
        val result = service.buildStandardSubmission(answers)

        result shouldBe defined

        val transport = scala.xml.XML.loadString(result.value) \\ "ActiveBorderTransportMeans"
        val hasDetails =
          details.transportType.isDefined ||
            details.transportIdNumber.isDefined ||
            details.countryOfRegistration.isDefined

        transport.size shouldBe (if (hasDetails) 1 else 0)

        (transport \ "typeOfIdentification").map(_.text).toList shouldBe details.transportType.toList
        (transport \ "identificationNumber").map(_.text).toList shouldBe details.transportIdNumber.toList
        (transport \ "nationality").map(_.text).toList shouldBe details.countryOfRegistration.toList
      }
    }

    "must include the DUCR in GoodsShipment when there are no discrepancies" in {
      val userAnswers = for {
        answers <- emptyUserAnswers.set(EnterMrnPage, "MRN")
        answers <- answers.set(EnterDucrPage, "5GB000000000000-12345")
        answers <- answers.set(PartOfConsolidationPage, PartOfConsolidationAnswer(false, None))
        answers <- answers.set(LocationTypePage, LocationType.AuthorisedPlace)
        answers <- answers.set(LocationIdPage, LocationDetails(LocationQualifier.UnLocode, Some("GBBEL"), Some("1234"), Some("abc123")))
        answers <- answers.set(OfficeOfExitPage, OfficeOfExit.Belfast)
        answers <- answers.set(IsSplitExitPage, false)
        answers <- answers.set(AnyDiscrepanciesPage, false)
      } yield answers

      val result = service.buildStandardSubmission(userAnswers.get)

      result shouldBe defined
      result.value should include("<discrepanciesExist>0</discrepanciesExist>")
      result.value should include("<splitIndicator>0</splitIndicator>")
      result.value should include("<GoodsShipment>")
      result.value should include("<Consignment>")
      result.value should include("<referenceNumberUCR>5GB000000000000-12345</referenceNumberUCR>")
      result.value shouldNot include("<modeOfTransportAtTheBorder>")
      result.value shouldNot include("<GoodsItem>")
    }

    "must omit absent container fields and preserve entered values across multiple equipments in XML" in {
      val baseAnswers = (for {
        answers <- emptyUserAnswers.set(EnterMrnPage, "MRN")
        answers <- answers.set(AnyDiscrepanciesPage, true)
        answers <- answers.set(IsSplitExitPage, false)
        answers <- answers.set(OfficeOfExitPage, OfficeOfExit.Belfast)
        answers <- answers.set(EnterDucrPage, "5GB000000000000-12345")
        answers <- answers.set(LocationTypePage, LocationType.AuthorisedPlace)
        answers <- answers.set(LocationIdPage, LocationDetails(LocationQualifier.UnLocode, Some("GBBEL"), Some("1234"), Some("abc123")))
        answers <- answers.set(DiscrepancyTransportPage(parentIndex), ContainerDetails(None, None))
        answers <- answers.set(DiscrepancySealsPage(parentIndex, index), "sealId1")
        answers <- answers.set(DiscrepancyReferencePage(parentIndex, index), "12345")
        answers <- answers.set(DiscrepancyTransportPage(2), ContainerDetails(Some("CONT123"), None))
        answers <- answers.set(DiscrepancySealsPage(2, index), "sealId2")
        answers <- answers.set(DiscrepancyReferencePage(2, index), "23456")
        answers <- answers.set(DiscrepancyTransportPage(3), ContainerDetails(None, Some(0)))
        answers <- answers.set(DiscrepancySealsPage(3, index), "sealId3")
        answers <- answers.set(DiscrepancyReferencePage(3, index), "34567")
        answers <- answers.set(DiscrepancyTransportPage(4), ContainerDetails(Some("CONT234"), Some(1)))
        answers <- answers.set(DiscrepancySealsPage(4, index), "sealId4")
        answers <- answers.set(DiscrepancySealsPage(4, 2), "sealId5")
        answers <- answers.set(DiscrepancyReferencePage(4, index), "45678")
        answers <- answers.set(DiscrepancyReferencePage(4, 2), "56789")
      } yield answers).get

      val result = service.buildStandardSubmission(baseAnswers)

      result shouldBe defined

      val equipments =
        scala.xml.XML.loadString(result.value) \\ "TransportEquipment"

      equipments.size shouldBe 4

      equipments.map { equipment =>
        (equipment \ "sequenceNumber").text
      } shouldBe Seq("1", "2", "3", "4")

      (equipments \ "containerIdentificationNumber")
        .map(_.text)
        .toList shouldBe List("CONT123", "CONT234")

      (equipments \ "numberOfSeals")
        .map(_.text)
        .toList shouldBe List("0", "1")

      equipments.take(3).foreach { equipment =>
        (equipment \ "Seal").map { seal =>
          (seal \ "sequenceNumber").text
        } shouldBe Seq("1")
      }

      (equipments(3) \ "Seal").map { seal =>
        (seal \ "sequenceNumber").text
      } shouldBe Seq("1", "2")

      (equipments \ "Seal" \ "identifier").map(_.text).toList shouldBe List("sealId1", "sealId2", "sealId3", "sealId4", "sealId5")

      equipments.take(3).foreach { equipment =>
        (equipment \ "GoodsReference").map { reference =>
          (reference \ "sequenceNumber").text
        } shouldBe Seq("1")
      }

      (equipments(3) \ "GoodsReference").map { reference =>
        (reference \ "sequenceNumber").text
      } shouldBe Seq("1", "2")

      (equipments \ "GoodsReference" \ "declarationGoodsItemNumber").map(_.text).toList shouldBe List("12345", "23456", "34567", "45678", "56789")
    }

    "must return a None when all required answers not present" in {

      val userAnswers = emptyUserAnswers.set(EnterMrnPage, "MRN").get

      service.buildStandardSubmission(userAnswers) shouldBe None
    }
  }
}
