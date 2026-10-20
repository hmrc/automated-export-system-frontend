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

case class ValidationConstraint(
  fieldLabel: String,
  minValue: Option[String] = None,
  maxValue: Option[String] = None,
  minLength: Option[Int] = None,
  maxLength: Option[Int] = None
)

object ValidationErrorMapper {

  private val constraints: Map[String, ValidationConstraint] = Map(
    "submissionId" ->
      ValidationConstraint(fieldLabel = "Submission ID", minLength = Some(1), maxLength = Some(36)),
    "Body.ExportOperation.type" ->
      ValidationConstraint(fieldLabel = "Export Operation Type", minValue = Some("1"), maxValue = Some("3")),
    "Body.ExportOperation.MRN" ->
      ValidationConstraint(fieldLabel = "MRN", minLength = Some(18), maxLength = Some(18)),
    "Body.ExportOperation.discrepanciesExist" ->
      ValidationConstraint(fieldLabel = "Discrepancies exist", minValue = Some("0"), maxValue = Some("1")),
    "Body.ExportOperation.splitIndicator" ->
      ValidationConstraint(fieldLabel = "Split indicator", minValue = Some("0"), maxValue = Some("1")),
    "Body.CustomsOfficeOfExitActual.referenceNumber" ->
      ValidationConstraint(fieldLabel = "Reference number", minLength = Some(8), maxLength = Some(8)),
    "Body.GoodsShipment.Consignment.modeOfTransportAtBorder" ->
      ValidationConstraint(fieldLabel = "Mode of transport at border", minValue = Some("0"), maxValue = Some("9")),
    "Body.GoodsShipment.Consignment.referenceNumberUCR" ->
      ValidationConstraint(fieldLabel = "DUCR", minLength = Some(8), maxLength = Some(35)),
    "Body.GoodsShipment.Consignment.parentUCRID" ->
      ValidationConstraint(fieldLabel = "MUCR", minLength = Some(10), maxLength = Some(35)),
    "Body.GoodsShipment.Consignment.TransportEquipment.sequenceNumber" ->
      ValidationConstraint(fieldLabel = "Transport equipment sequence number", minValue = Some("1"), maxValue = Some("99999")),
    "Body.GoodsShipment.Consignment.TransportEquipment.containerIdentificationNumber" ->
      ValidationConstraint(fieldLabel = "Container identification number", minLength = Some(1), maxLength = Some(17)),
    "Body.GoodsShipment.Consignment.TransportEquipment.numberOfSeals" ->
      ValidationConstraint(fieldLabel = "Container identification number", minValue = Some("0"), maxValue = Some("9999")),
    "Body.GoodsShipment.Consignment.TransportEquipment.Seal.sequenceNumber" ->
      ValidationConstraint(fieldLabel = "Seal sequence number", minValue = Some("1"), maxValue = Some("99999")),
    "Body.GoodsShipment.Consignment.TransportEquipment.Seal.identifier" ->
      ValidationConstraint(fieldLabel = "Seal identifier", minLength = Some(1), maxLength = Some(20)),
    "Body.GoodsShipment.Consignment.TransportEquipment.GoodsReference.sequenceNumber" ->
      ValidationConstraint(fieldLabel = "Goods sequence number", minValue = Some("1"), maxValue = Some("99999")),
    "Body.GoodsShipment.Consignment.TransportEquipment.GoodsReference.declarationGoodsItemNumber" ->
      ValidationConstraint(fieldLabel = "Declaration of goods item number", minValue = Some("1"), maxValue = Some("99999")),
    "Body.GoodsShipment.Consignment.LocationOfGoods.authorisationNumber" ->
      ValidationConstraint(fieldLabel = "Location of goods authorisation number", minLength = Some(1), maxLength = Some(35)),
    "Body.GoodsShipment.Consignment.LocationOfGoods.additionalIdentifier" ->
      ValidationConstraint(fieldLabel = "Location of goods additional identifier", minLength = Some(1), maxLength = Some(4)),
    "Body.GoodsShipment.Consignment.LocationOfGoods.UNLocode" ->
      ValidationConstraint(fieldLabel = "Location of goods UNLocode", minLength = Some(1), maxLength = Some(17)),
    "Body.GoodsShipment.Consignment.ActiveBorderTransportMeans.identificationNumber" ->
      ValidationConstraint(fieldLabel = "Active border transport identification number", minLength = Some(1), maxLength = Some(35)),
    "Body.GoodsShipment.Consignment.TransportDocument.sequenceNumber" ->
      ValidationConstraint(fieldLabel = "Transport document sequence number", minValue = Some("1"), maxValue = Some("99999")),
    "Body.GoodsShipment.Consignment.TransportDocument.type" ->
      ValidationConstraint(fieldLabel = "Transport document type", minValue = Some("1"), maxValue = Some("9999")),
    "Body.GoodsShipment.Consignment.TransportDocument.referenceNumber" ->
      ValidationConstraint(fieldLabel = "Transport document reference number", minValue = Some("1"), maxValue = Some("9999")),
    "Body.GoodsShipment.GoodsItem.declarationGoodsItemNumber" ->
      ValidationConstraint(fieldLabel = "Declaration goods item number", minValue = Some("0"), maxValue = Some("999")),
    "Body.GoodsShipment.GoodsItem.referenceNumberUCR" ->
      ValidationConstraint(fieldLabel = "Goods DUCR", minLength = Some(8), maxLength = Some(35)),
    "Body.GoodsShipment.GoodsItem.Packaging.sequenceNumber" ->
      ValidationConstraint(fieldLabel = "Goods packaging sequence number", minValue = Some("1"), maxValue = Some("99999")),
    "Body.GoodsShipment.GoodsItem.Packaging.typeOfPackages" ->
      ValidationConstraint(fieldLabel = "Type of packages", minLength = Some("1".length), maxLength = Some("1".length)),
    "Body.GoodsShipment.GoodsItem.Packaging.numberOfPackages" ->
      ValidationConstraint(fieldLabel = "Number of packages", minValue = Some("0"), maxValue = Some("99999999")),
    "Body.GoodsShipment.GoodsItem.Packaging.shippingMarks" ->
      ValidationConstraint(fieldLabel = "Shipping Marks", minLength = Some(1), maxLength = Some(512))
  )

  def get(path: String): Option[ValidationConstraint] =
    constraints.get(path)
}
