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

import java.time.LocalDateTime
import scala.xml.NodeSeq
import scala.math.BigDecimal

object SingleSubmissionResponseParser {

  def parse(xml: NodeSeq): SingleSubmissionResponse = {

    val submission =
      (xml \ "Submission").headOption.getOrElse(xml)

    val exportOperationXml = (submission \ "ExportOperation").head

    val exportOperation =
      SingleSubmissionExportOperation(
        exportOperationType = (exportOperationXml \ "type").text.trim,
        mrn = (exportOperationXml \ "MRN").text.trim,
        discrepanciesExist = (exportOperationXml \ "discrepanciesExist").text.trim.toInt,
        splitIndicator = (exportOperationXml \ "splitIndicator").text.trim.toInt
      )

    val customsOfficeXml =
      (submission \ "CustomsOfficeOfExitActual").head

    val customsOfficeOfExitActual =
      SingleSubmissionCustomsOfficeOfExitActual(referenceNumber = (customsOfficeXml \ "referenceNumber").text.trim)

    val goodsShipment =
      (submission \ "GoodsShipment").headOption.map(parseGoodsShipment)

    SingleSubmissionResponse(
      submissionId = (submission \ "submissionId").text.trim,
      exportOperation = exportOperation,
      customsOfficeOfExitActual = customsOfficeOfExitActual,
      goodsShipment = goodsShipment,
      updatedAt = LocalDateTime.parse((submission \ "updatedAt").text.trim)
    )
  }

  private def parseGoodsShipment(xml: NodeSeq): SingleSubmissionGoodsShipment = {

    val consignmentXml =
      (xml \ "Consignment").head

    val consignment =
      SingleSubmissionConsignment(
        modeOfTransportAtTheBorder = (consignmentXml \ "modeOfTransportAtTheBorder").headOption
          .map(_.text.trim.toInt),
        referenceNumberUCR = (consignmentXml \ "referenceNumberUCR").text.trim,
        parentUCRID = (consignmentXml \ "parentUCRID").headOption
          .map(_.text.trim)
          .filter(_.nonEmpty),
        transportEquipment = parseOptionalList(consignmentXml \ "TransportEquipment")(parseTransportEquipment),
        locationOfGoods = parseLocationOfGoods(
          (consignmentXml \ "LocationOfGoods").headOption.getOrElse(throw new IllegalStateException("Missing LocationOfGoods element in submission"))
        ),
        activeBorderTransportMeans = (consignmentXml \ "ActiveBorderTransportMeans").headOption
          .map(parseActiveBorderTransportMeans),
        transportDocument = parseOptionalList(consignmentXml \ "TransportDocument")(parseTransportDocument)
      )

    SingleSubmissionGoodsShipment(consignment = consignment, goodsItems = parseOptionalList(xml \ "GoodsItem")(parseGoodsItem))
  }

  private def parseTransportEquipment(xml: NodeSeq): SingleSubmissionTransportEquipment =
    SingleSubmissionTransportEquipment(
      sequenceNumber = (xml \ "sequenceNumber").headOption.map(_.text.trim.toInt),
      containerIdentificationNumber = (xml \ "containerIdentificationNumber").headOption
        .map(_.text.trim)
        .filter(_.nonEmpty),
      numberOfSeals = (xml \ "numberOfSeals").headOption.map(_.text.trim.toInt),
      seal = parseOptionalList(xml \ "Seal")(parseSeal),
      goodsReference = parseOptionalList(xml \ "GoodsReference")(parseGoodsReference)
    )

  private def parseSeal(xml: NodeSeq): SingleSubmissionSeal =
    SingleSubmissionSeal(
      sequenceNumber = (xml \ "sequenceNumber").headOption.map(_.text.trim.toInt),
      identifier = (xml \ "identifier").headOption
        .map(_.text.trim)
        .filter(_.nonEmpty)
    )

  private def parseGoodsReference(xml: NodeSeq): SingleSubmissionGoodsReference =
    SingleSubmissionGoodsReference(
      sequenceNumber = (xml \ "sequenceNumber").headOption.map(_.text.trim.toInt),
      declarationGoodsItemNumber = (xml \ "declarationGoodsItemNumber").headOption
        .map(_.text.trim.toInt)
    )

  private def parseLocationOfGoods(xml: NodeSeq): SingleSubmissionLocationOfGoods =
    SingleSubmissionLocationOfGoods(
      typeOfLocation = (xml \ "typeOfLocation").text.trim,
      qualifierOfIdentification = (xml \ "qualifierOfIdentification").text.trim,
      authorisationNumber = (xml \ "authorisationNumber").headOption
        .map(_.text.trim)
        .filter(_.nonEmpty),
      additionalIdentifier = (xml \ "additionalIdentifier").headOption
        .map(_.text.trim)
        .filter(_.nonEmpty),
      UNLocode = (xml \ "UNLocode").headOption
        .map(_.text.trim)
        .filter(_.nonEmpty)
    )

  private def parseActiveBorderTransportMeans(xml: NodeSeq): SingleSubmissionActiveBorderTransportMeans =
    SingleSubmissionActiveBorderTransportMeans(
      typeOfIdentification = (xml \ "typeOfIdentification").headOption
        .map(_.text.trim)
        .filter(_.nonEmpty),
      identificationNumber = (xml \ "identificationNumber").headOption
        .map(_.text.trim)
        .filter(_.nonEmpty),
      nationality = (xml \ "nationality").headOption
        .map(_.text.trim)
        .filter(_.nonEmpty)
    )

  private def parseTransportDocument(xml: NodeSeq): SingleSubmissionTransportDocument =
    SingleSubmissionTransportDocument(
      sequenceNumber = (xml \ "sequenceNumber").headOption.map(_.text.trim.toInt),
      `type` = (xml \ "type").headOption.map(_.text.trim.toInt),
      referenceNumber = (xml \ "referenceNumber").headOption
        .map(_.text.trim)
        .filter(_.nonEmpty)
    )

  private def parseGoodsItem(xml: NodeSeq): SingleSubmissionGoodsItem =
    SingleSubmissionGoodsItem(
      declarationGoodsItemNumber = (xml \ "declarationGoodsItemNumber").headOption
        .map(_.text.trim.toInt),
      referenceNumberUCR = (xml \ "referenceNumberUCR").headOption
        .map(_.text.trim)
        .filter(_.nonEmpty),
      commodity = parseCommodity(
        (xml \ "Commodity").headOption
          .getOrElse(throw new IllegalStateException("Missing Commodity element in goods item"))
      ),
      packaging = parseOptionalList(xml \ "Packaging")(parsePackaging)
    )

  private def parseCommodity(xml: NodeSeq): SingleSubmissionCommodity = {

    val goodsMeasureXml =
      (xml \ "GoodsMeasure").headOption.getOrElse(throw new IllegalStateException("Missing GoodsMeasure element in commodity"))

    SingleSubmissionCommodity(
      grossMass = BigDecimal((goodsMeasureXml \ "grossMass").text.trim),
      netMass = BigDecimal((goodsMeasureXml \ "netMass").text.trim)
    )
  }

  private def parsePackaging(xml: NodeSeq): SingleSubmissionPackaging =
    SingleSubmissionPackaging(
      sequenceNumber = (xml \ "sequenceNumber").headOption.map(_.text.trim.toInt),
      typeOfPackages = (xml \ "typeOfPackages").headOption
        .map(_.text.trim)
        .filter(_.nonEmpty),
      numberOfPackages = (xml \ "numberOfPackages").headOption
        .map(_.text.trim)
        .filter(_.nonEmpty),
      shippingMarks = (xml \ "shippingMarks").headOption
        .map(_.text.trim)
        .filter(_.nonEmpty)
    )

  private def parseOptionalList[T](nodes: NodeSeq)(parser: NodeSeq => T): Option[Seq[T]] =
    if (nodes.nonEmpty) Some(nodes.map(parser)) else None
}
