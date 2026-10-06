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
    val index = 1

    "must include a GoodsShipment for a split exit when AnyDiscrepanciesPage is not present" in {
      val userAnswers = for {
        userAnswers <- emptyUserAnswers.set(EnterMrnPage, "MRN")
        userAnswers <- userAnswers.set(IsSplitExitPage, true)
        userAnswers <- userAnswers.set(OfficeOfExitPage, OfficeOfExit.Belfast)
        userAnswers <- userAnswers.set(DiscrepancyConsignmentPage, ModeOfTransportAtBorder.Sea)
        userAnswers <- userAnswers.set(EnterDucrPage, "5GB000000000000-12345")
        userAnswers <- userAnswers.set(PartOfConsolidationPage, PartOfConsolidationAnswer(true, Some("GB/000000000000-12345")))
        userAnswers <- userAnswers.set(DiscrepancyTransportPage, ContainerDetails(Some("containerId"), numberOfSeals = Some(1)))
        userAnswers <- userAnswers.set(DiscrepancySealsPage, Some("sealId"))
        userAnswers <- userAnswers.set(LocationTypePage, LocationType.AuthorisedPlace)
        userAnswers <- userAnswers.set(LocationIdPage, LocationDetails(LocationQualifier.UnLocode, "GBBEL", "locationId", "abc123"))
        userAnswers <- userAnswers.set(DiscrepancyTransportMeansPage, TransportAcrossBorderDetails(Some("road"), Some("transportId"), Some("GB")))
        userAnswers <- userAnswers.set(DiscrepancyTransportDocPage, DocumentDetails(Some(1), Some(1234)))
        userAnswers <- userAnswers.set(DiscrepancyReferencePage, Some("1"))
        userAnswers <- userAnswers.set(DiscrepancyGoodsPage, WhatHasChangedDetails(Some(1), Some("5GB000000000000-12345"), "20", "10"))
        userAnswers <- userAnswers.set(DiscrepancyPackingPage(index), PackingDetails("PK", 1, "marks"))
      } yield userAnswers

      val result = service.buildStandardSubmission(userAnswers.get)

      result shouldBe defined
      result.value should include("<discrepanciesExist>1</discrepanciesExist>")
      result.value should include("<splitIndicator>1</splitIndicator>")
      result.value should include("<GoodsShipment>")
    }

    "must return an String of XML with no GoodsShipment when the minimal set of required answers are present " in {
      val userAnswers = for {
        userAnswers <- emptyUserAnswers.set(EnterMrnPage, "MRN")
        userAnswers <- userAnswers.set(AnyDiscrepanciesPage, false)
        userAnswers <- userAnswers.set(IsSplitExitPage, false)
        userAnswers <- userAnswers.set(OfficeOfExitPage, OfficeOfExit.Belfast)
        // userAnswers <- userAnswers.set(EnterDucrPage, "someDUCR")
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
        userAnswers <- userAnswers.set(DiscrepancyTransportPage, ContainerDetails(Some("containerId"), numberOfSeals = Some(1)))
        userAnswers <- userAnswers.set(DiscrepancySealsPage, Some("sealId"))
        userAnswers <- userAnswers.set(LocationTypePage, LocationType.AuthorisedPlace)
        userAnswers <- userAnswers.set(LocationIdPage, LocationDetails(LocationQualifier.UnLocode, "GBBEL", "locationId", "abc123"))
        userAnswers <- userAnswers.set(DiscrepancyTransportMeansPage, TransportAcrossBorderDetails(Some("road"), Some("transportId"), Some("GB")))
        userAnswers <- userAnswers.set(DiscrepancyTransportDocPage, DocumentDetails(Some(1), Some(1234)))
        userAnswers <- userAnswers.set(DiscrepancyReferencePage, Some("1"))
        userAnswers <- userAnswers.set(DiscrepancyGoodsPage, WhatHasChangedDetails(Some(1), Some("5GB000000000000-12345"), "20", "10"))
        userAnswers <- userAnswers.set(DiscrepancyPackingPage(index), PackingDetails("PK", 1, "marks"))
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
      result.value should include("<typeOfIdentification>road</typeOfIdentification>")
      result.value should include("<identificationNumber>transportId</identificationNumber>")
      result.value should include("<nationality>GB</nationality>")

      result.value should include("<TransportDocument>")
      result.value should include("<type>1</type>")
      result.value should include("<typeOfIdentification>road</typeOfIdentification>")
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

    "must omit GoodsReference when the reference is blank" in {
      val userAnswers = (for {
        answers <- emptyUserAnswers.set(EnterMrnPage, "MRN")
        answers <- answers.set(AnyDiscrepanciesPage, true)
        answers <- answers.set(IsSplitExitPage, false)
        answers <- answers.set(OfficeOfExitPage, OfficeOfExit.Belfast)
        answers <- answers.set(EnterDucrPage, "5GB000000000000-12345")
        answers <- answers.set(LocationTypePage, LocationType.AuthorisedPlace)
        answers <- answers.set(LocationIdPage, LocationDetails(LocationQualifier.UnLocode, "GBBEL", "locationId", "abc123"))
        answers <- answers.set(DiscrepancyTransportPage, ContainerDetails(Some("CONT123"), None))
        answers <- answers.set(DiscrepancyReferencePage, Option.empty[String])
      } yield answers).get

      val result = service.buildStandardSubmission(userAnswers)

      result shouldBe defined

      val equipment = scala.xml.XML.loadString(result.value) \\ "TransportEquipment"

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
        answers <- answers.set(LocationIdPage, LocationDetails(LocationQualifier.UnLocode, "GBBEL", "locationId", "abc123"))
        answers <- answers.set(DiscrepancyTransportPage, ContainerDetails(Some("CONT123"), None))
        answers <- answers.set(DiscrepancySealsPage, Option.empty[String])
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
        answers <- answers.set(LocationIdPage, LocationDetails(LocationQualifier.UnLocode, "GBBEL", "locationId", "abc123"))
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
        answers <- answers.set(LocationIdPage, LocationDetails(LocationQualifier.UnLocode, "GBBEL", "locationId", "abc123"))
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

    "must omit absent container fields and preserve entered values in XML" in {
      val baseAnswers = (for {
        answers <- emptyUserAnswers.set(EnterMrnPage, "MRN")
        answers <- answers.set(AnyDiscrepanciesPage, true)
        answers <- answers.set(IsSplitExitPage, false)
        answers <- answers.set(OfficeOfExitPage, OfficeOfExit.Belfast)
        answers <- answers.set(EnterDucrPage, "5GB000000000000-12345")
        answers <- answers.set(LocationTypePage, LocationType.AuthorisedPlace)
        answers <- answers.set(LocationIdPage, LocationDetails(LocationQualifier.UnLocode, "GBBEL", "locationId", "abc123"))
        answers <- answers.set(DiscrepancySealsPage, Some("sealId"))
      } yield answers).get

      val cases = Seq(
        ContainerDetails(None, None),
        ContainerDetails(Some("CONT123"), None),
        ContainerDetails(None, Some(0)),
        ContainerDetails(Some("CONT123"), Some(1))
      )

      cases.foreach { details =>
        val answers = baseAnswers.set(DiscrepancyTransportPage, details).get
        val result = service.buildStandardSubmission(answers)

        result shouldBe defined

        val equipment = scala.xml.XML.loadString(result.value) \\ "TransportEquipment"

        equipment.size shouldBe 1
        (equipment \ "containerIdentificationNumber").map(_.text).toList shouldBe details.containerId.toList
        (equipment \ "numberOfSeals").map(_.text).toList shouldBe details.numberOfSeals.map(_.toString).toList
        (equipment \ "Seal" \ "identifier").text shouldBe "sealId"
      }
    }

    "must return a None when all required answers not present" in {

      val userAnswers = emptyUserAnswers.set(EnterMrnPage, "MRN").get

      service.buildStandardSubmission(userAnswers) shouldBe None
    }
  }
}
