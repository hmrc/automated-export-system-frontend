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

case class SingleSubmissionResponse(
  submissionId: String,
  exportOperation: SingleSubmissionExportOperation,
  customsOfficeOfExitActual: SingleSubmissionCustomsOfficeOfExitActual,
  goodsShipment: Option[SingleSubmissionGoodsShipment],
  updatedAt: LocalDateTime,
  metadata: Option[SingleSubmissionMetadata]
)

case class SingleSubmissionExportOperation(exportOperationType: String, mrn: String, discrepanciesExist: Int, splitIndicator: Int)

case class SingleSubmissionCustomsOfficeOfExitActual(referenceNumber: String)

case class SingleSubmissionGoodsShipment(consignment: SingleSubmissionConsignment, goodsItems: Option[Seq[SingleSubmissionGoodsItem]])

case class SingleSubmissionConsignment(
  modeOfTransportAtTheBorder: Option[Int],
  referenceNumberUCR: String,
  parentUCRID: Option[String],
  transportEquipment: Option[Seq[SingleSubmissionTransportEquipment]],
  locationOfGoods: SingleSubmissionLocationOfGoods,
  activeBorderTransportMeans: Option[SingleSubmissionActiveBorderTransportMeans],
  transportDocument: Option[Seq[SingleSubmissionTransportDocument]]
)

case class SingleSubmissionTransportEquipment(
  sequenceNumber: Option[Int],
  containerIdentificationNumber: Option[String],
  numberOfSeals: Option[Int],
  seal: Option[Seq[SingleSubmissionSeal]],
  goodsReference: Option[Seq[SingleSubmissionGoodsReference]]
)

case class SingleSubmissionSeal(sequenceNumber: Option[Int], identifier: Option[String])

case class SingleSubmissionGoodsReference(sequenceNumber: Option[Int], declarationGoodsItemNumber: Option[Int])

case class SingleSubmissionLocationOfGoods(
  typeOfLocation: String,
  qualifierOfIdentification: String,
  authorisationNumber: Option[String],
  additionalIdentifier: Option[String],
  UNLocode: Option[String]
)

case class SingleSubmissionActiveBorderTransportMeans(
  typeOfIdentification: Option[String],
  identificationNumber: Option[String],
  nationality: Option[String]
)

case class SingleSubmissionTransportDocument(sequenceNumber: Option[Int], `type`: Option[Int], referenceNumber: Option[String])

case class SingleSubmissionGoodsItem(
  declarationGoodsItemNumber: Option[Int],
  referenceNumberUCR: Option[String],
  commodity: SingleSubmissionCommodity,
  packaging: Option[Seq[SingleSubmissionPackaging]]
)

case class SingleSubmissionCommodity(grossMass: BigDecimal, netMass: BigDecimal)

case class SingleSubmissionPackaging(
  sequenceNumber: Option[Int],
  typeOfPackages: Option[String],
  numberOfPackages: Option[String],
  shippingMarks: Option[String]
)

case class SingleSubmissionMetadata(errors: Seq[SingleSubmissionError])

case class SingleSubmissionError(code: String, description: Option[String], path: Option[String], originalValue: Option[String])
