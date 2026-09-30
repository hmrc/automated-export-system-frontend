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

package uk.gov.hmrc.automatedexportsystemfrontend.models

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import java.time.LocalDateTime
import scala.xml.XML

class SingleSubmissionResponseParserSpec extends AnyWordSpec with Matchers {

  "SingleSubmissionResponseParser" should {

    "parse a submission without GoodsShipment" in {

      val xml =
        XML.loadString("""
            |<Submission>
            |  <submissionId>6fb33641-6dc7-4a4f-adef-06238c13a317</submissionId>
            |  <ExportOperation>
            |    <type>1</type>
            |    <MRN>26GB0000X6524786A9</MRN>
            |    <discrepanciesExist>0</discrepanciesExist>
            |    <splitIndicator>0</splitIndicator>
            |  </ExportOperation>
            |  <CustomsOfficeOfExitActual>
            |    <referenceNumber>GB000051</referenceNumber>
            |  </CustomsOfficeOfExitActual>
            |  <updatedAt>2026-08-03T00:00:00</updatedAt>
            |</Submission>
            |""".stripMargin)

      val result = SingleSubmissionResponseParser.parse(xml)

      result.submissionId shouldBe "6fb33641-6dc7-4a4f-adef-06238c13a317"
      result.exportOperation.exportOperationType shouldBe "1"
      result.exportOperation.mrn shouldBe "26GB0000X6524786A9"
      result.exportOperation.discrepanciesExist shouldBe 0
      result.exportOperation.splitIndicator shouldBe 0
      result.customsOfficeOfExitActual.referenceNumber shouldBe "GB000051"
      result.goodsShipment shouldBe None
      result.updatedAt shouldBe LocalDateTime.parse("2026-08-03T00:00:00")
    }

    "parse a submission with GoodsShipment" in {

      val xml =
        XML.loadString("""
            |<Submission>
            |  <submissionId>6fb33641-6dc7-4a4f-adef-06238c13a317</submissionId>
            |  <ExportOperation>
            |    <type>1</type>
            |    <MRN>26GB0000X6524786A9</MRN>
            |    <discrepanciesExist>1</discrepanciesExist>
            |    <splitIndicator>1</splitIndicator>
            |  </ExportOperation>
            |  <CustomsOfficeOfExitActual>
            |    <referenceNumber>GB000051</referenceNumber>
            |  </CustomsOfficeOfExitActual>
            |  <GoodsShipment>
            |    <Consignment>
            |      <modeOfTransportAtTheBorder>1</modeOfTransportAtTheBorder>
            |      <referenceNumberUCR>UCR123</referenceNumberUCR>
            |      <parentUCRID>PARENT123</parentUCRID>
            |      <TransportEquipment>
            |        <sequenceNumber>1</sequenceNumber>
            |        <containerIdentificationNumber>CONT123</containerIdentificationNumber>
            |        <numberOfSeals>1</numberOfSeals>
            |        <Seal>
            |          <sequenceNumber>1</sequenceNumber>
            |          <identifier>SEAL123</identifier>
            |        </Seal>
            |        <GoodsReference>
            |          <sequenceNumber>1</sequenceNumber>
            |          <declarationGoodsItemNumber>1</declarationGoodsItemNumber>
            |        </GoodsReference>
            |      </TransportEquipment>
            |      <LocationOfGoods>
            |        <typeOfLocation>A</typeOfLocation>
            |        <qualifierOfIdentification>B</qualifierOfIdentification>
            |        <authorisationNumber>AUTH123</authorisationNumber>
            |        <additionalIdentifier>ADD123</additionalIdentifier>
            |        <UNLocode>GBNCL</UNLocode>
            |      </LocationOfGoods>
            |      <ActiveBorderTransportMeans>
            |        <typeOfIdentification>10</typeOfIdentification>
            |        <identificationNumber>ABC123</identificationNumber>
            |        <nationality>GB</nationality>
            |      </ActiveBorderTransportMeans>
            |      <TransportDocument>
            |        <sequenceNumber>1</sequenceNumber>
            |        <type>1</type>
            |        <referenceNumber>DOC123</referenceNumber>
            |      </TransportDocument>
            |    </Consignment>
            |    <GoodsItem>
            |      <declarationGoodsItemNumber>1</declarationGoodsItemNumber>
            |      <referenceNumberUCR>UCR123</referenceNumberUCR>
            |      <Commodity>
            |        <GoodsMeasure>
            |          <grossMass>100.50</grossMass>
            |          <netMass>90.25</netMass>
            |        </GoodsMeasure>
            |      </Commodity>
            |      <Packaging>
            |        <sequenceNumber>1</sequenceNumber>
            |        <typeOfPackages>BX</typeOfPackages>
            |        <numberOfPackages>10</numberOfPackages>
            |        <shippingMarks>MARK123</shippingMarks>
            |      </Packaging>
            |    </GoodsItem>
            |  </GoodsShipment>
            |  <updatedAt>2026-08-03T00:00:00</updatedAt>
            |</Submission>
            |""".stripMargin)

      val result = SingleSubmissionResponseParser.parse(xml)

      result.goodsShipment shouldBe defined

      val goodsShipment = result.goodsShipment.get

      goodsShipment.consignment.modeOfTransportAtTheBorder shouldBe Some(1)
      goodsShipment.consignment.referenceNumberUCR shouldBe "UCR123"
      goodsShipment.consignment.parentUCRID shouldBe Some("PARENT123")

      val equipment =
        goodsShipment.consignment.transportEquipment.get.head

      equipment.sequenceNumber shouldBe Some(1)
      equipment.containerIdentificationNumber shouldBe Some("CONT123")
      equipment.numberOfSeals shouldBe Some(1)

      val seal = equipment.seal.get.head
      seal.sequenceNumber shouldBe Some(1)
      seal.identifier shouldBe Some("SEAL123")

      val goodsReference = equipment.goodsReference.get.head
      goodsReference.sequenceNumber shouldBe Some(1)
      goodsReference.declarationGoodsItemNumber shouldBe Some(1)

      val location = goodsShipment.consignment.locationOfGoods

      location.typeOfLocation shouldBe "A"
      location.qualifierOfIdentification shouldBe "B"
      location.authorisationNumber shouldBe Some("AUTH123")
      location.additionalIdentifier shouldBe Some("ADD123")
      location.UNLocode shouldBe Some("GBNCL")

      val transportMeans =
        goodsShipment.consignment.activeBorderTransportMeans.get

      transportMeans.typeOfIdentification shouldBe Some("10")
      transportMeans.identificationNumber shouldBe Some("ABC123")
      transportMeans.nationality shouldBe Some("GB")

      val document =
        goodsShipment.consignment.transportDocument.get.head

      document.sequenceNumber shouldBe Some(1)
      document.`type` shouldBe Some(1)
      document.referenceNumber shouldBe Some("DOC123")

      val goodsItem = goodsShipment.goodsItems.get.head

      goodsItem.declarationGoodsItemNumber shouldBe Some(1)
      goodsItem.referenceNumberUCR shouldBe Some("UCR123")
      goodsItem.commodity.grossMass shouldBe BigDecimal("100.50")
      goodsItem.commodity.netMass shouldBe BigDecimal("90.25")

      val packaging = goodsItem.packaging.get.head

      packaging.sequenceNumber shouldBe Some(1)
      packaging.typeOfPackages shouldBe Some("BX")
      packaging.numberOfPackages shouldBe Some("10")
      packaging.shippingMarks shouldBe Some("MARK123")
    }

    "parse submission metadata errors" in {

      val xml =
        XML.loadString("""
                         |<Submission>
                         |  <submissionId>123</submissionId>
                         |  <ExportOperation>
                         |    <type>1</type>
                         |    <MRN>26GB0000X6524786A9</MRN>
                         |    <discrepanciesExist>0</discrepanciesExist>
                         |    <splitIndicator>0</splitIndicator>
                         |  </ExportOperation>
                         |  <CustomsOfficeOfExitActual>
                         |    <referenceNumber>GB000051</referenceNumber>
                         |  </CustomsOfficeOfExitActual>
                         |  <updatedAt>2026-08-03T00:00:00</updatedAt>
                         |  <metadata>
                         |    <error>
                         |      <code>INVALID_MRN</code>
                         |      <description>Invalid MRN</description>
                         |      <path>mrn</path>
                         |      <originalValue>ABC123</originalValue>
                         |    </error>
                         |  </metadata>
                         |</Submission>
                         |""".stripMargin)

      val result = SingleSubmissionResponseParser.parse(xml)

      result.metadata shouldBe defined

      val error = result.metadata.get.errors.head

      error.code shouldBe "INVALID_MRN"
      error.description shouldBe Some("Invalid MRN")
      error.path shouldBe Some("mrn")
      error.originalValue shouldBe Some("ABC123")
    }
  }
}
