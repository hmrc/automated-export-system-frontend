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

import com.google.inject.Inject
import play.api.Logging
import uk.gov.hmrc.automatedexportsystemfrontend.models.IE507a.*
import uk.gov.hmrc.automatedexportsystemfrontend.models.IE507a.ExportOperationType.Standard
import uk.gov.hmrc.automatedexportsystemfrontend.models.{LocationQualifier, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.pages.amend.*
import uk.gov.hmrc.automatedexportsystemfrontend.pages.create.*
import uk.gov.hmrc.automatedexportsystemfrontend.queries.*
import uk.gov.hmrc.automatedexportsystemfrontend.xml.XmlOps

class CreateSubmissionDataService @Inject() extends Logging {

  def buildStandardSubmission(userAnswers: UserAnswers): Option[String] =
    collectUserAnswers(userAnswers) match {
      case Some(submission) =>
        Some(buildXmlWithDeclaration(submission))
      case None =>
        logger.error("Could not gather required user answers to create standard IE507a submission")
        None
    }

  private def collectDiscrepanciesExist(userAnswers: UserAnswers): Option[Boolean] =
    userAnswers
      .get(AnyDiscrepanciesPage)
      .orElse(userAnswers.get(IsSplitExitPage))

  private def collectTransportEquipment(userAnswers: UserAnswers): List[TransportEquipment] = {
    val discrepancyTransport =
      DiscrepancyTransportEquipment.getAll(userAnswers)

    discrepancyTransport.zipWithIndex.map { case (transport, index) =>
      val transportIndex = index + 1
      val seals = collectSeals(transport.discrepancySeals)
      val goodsReferences = collectGoodsReference(transport.discrepancyReference)
      TransportEquipment(
        transportIndex,
        transport.containerDetails.flatMap(_.containerId),
        transport.containerDetails.flatMap(_.numberOfSeals),
        seals,
        goodsReferences
      )
    }
  }

  private def collectAmendTransportEquipment(
    userAnswers: UserAnswers,
    seals: List[Seal],
    goodsReferences: List[GoodsReference],
    submissionId: String
  ): List[TransportEquipment] = {
    val discrepancyTransport = userAnswers.get(AmendDiscrepancyTransportPage(submissionId)).toList
    discrepancyTransport.zipWithIndex.map { case (transport, transportIndex) =>
      TransportEquipment(transportIndex + 1, transport.containerId, transport.numberOfSeals, seals, goodsReferences)
    }
  }

  private def collectSeals(discrepancySeals: List[String]): List[Seal] =
    discrepancySeals.zipWithIndex.map { (sealIdentifier, index) =>
      Seal(index + 1, sealIdentifier)
    }

  private def collectGoodsReference(discrepancyReference: List[String]): List[GoodsReference] =
    discrepancyReference.zipWithIndex.map { case (reference, index) =>
      GoodsReference(index + 1, reference.toInt)
    }

  private def collectGoodsLocation(userAnswers: UserAnswers): Option[LocationOfGoods] =
    for {
      locationType <- userAnswers.get(LocationTypePage)
      typeOfLocation = TypeOfLocation.fromUserAnswers(locationType)
      locationDetails <- userAnswers.get(LocationIdPage)

      qualifier = locationDetails.locationType match {
        case LocationQualifier.UnLocode =>
          QualifierOfTheIdentification.UnLocode
        case LocationQualifier.AuthorisationNumber =>
          QualifierOfTheIdentification.AuthorisationNumber
      }
    } yield LocationOfGoods(
      typeOfLocation,
      qualifier,
      locationDetails.authorisationReferenceNumber,
      locationDetails.locationAdditionalIdentifier,
      locationDetails.unlocode
    )

  private def collectActiveBorderTransportMeans(userAnswers: UserAnswers): Option[ActiveBorderTransportMeans] =
    userAnswers
      .get(DiscrepancyTransportMeansPage)
      .filter(transport =>
        transport.transportType.isDefined ||
          transport.transportIdNumber.isDefined ||
          transport.countryOfRegistration.isDefined
      )
      .map { transport =>
        ActiveBorderTransportMeans(transport.transportType, transport.transportIdNumber, transport.countryOfRegistration)
      }

  private def collectTransportDocument(userAnswers: UserAnswers): List[TransportDocument] =
    DiscrepancyTransportDoc.getAll(userAnswers).zipWithIndex.map { case (document, index) =>
      TransportDocument(index + 1, document.documentType, document.referenceNumber)
    }

  private def collectPackaging(userAnswers: UserAnswers, goodsItemIndex: Int): List[Packaging] =
    DiscrepancyPacking(goodsItemIndex).getAll(userAnswers).zipWithIndex.map { (packing, index) =>
      Packaging(index + 1, packing.packagingCode, packing.numberOfPackages.toString, packing.shippingMarks)
    }

  private def collectGoodsItem(userAnswers: UserAnswers): List[GoodsItem] =
    DiscrepancyGoods.getAll(userAnswers).zipWithIndex.map { (goods, index) =>
      GoodsItem(
        goods.declarationGoodsItemNumber,
        goods.declarationUniqueConsignmentReference,
        Commodity(goods.newGrossMass, goods.newNetMass),
        collectPackaging(userAnswers, index + 1)
      )
    }

  private def collectGoodsShipment(userAnswers: UserAnswers): Option[GoodsShipment] =
    for {
      ducr <- userAnswers.get(EnterDucrPage)
      location <- collectGoodsLocation(userAnswers)
    } yield {
      val transportMode =
        userAnswers.get(DiscrepancyConsignmentPage).map(TransportMode.apply)

      val mucr =
        userAnswers.get(PartOfConsolidationPage).flatMap(_.mucr)
      val transportEquipment =
        collectTransportEquipment(userAnswers)

      val transport = collectActiveBorderTransportMeans(userAnswers)
      val transportDocument = collectTransportDocument(userAnswers)
      val goodsItem = collectGoodsItem(userAnswers)

      GoodsShipment(Consignment(transportMode, ducr, mucr, transportEquipment, location, transport, transportDocument), goodsItem)
    }

  private def collectUserAnswers(userAnswers: UserAnswers): Option[Submission] =
    for {
      mrn <- userAnswers.get(EnterMrnPage)
      discrepanciesExist <- collectDiscrepanciesExist(userAnswers)
      splitIndicator <- userAnswers.get(IsSplitExitPage)
      referenceNumber <- userAnswers.get(OfficeOfExitPage)
      goodsShipment <- collectGoodsShipment(userAnswers)
    } yield Submission(
      None,
      ExportOperation(Standard, mrn, discrepanciesExist, splitIndicator),
      CustomsOfficeOfExitActual(referenceNumber.toString),
      Some(goodsShipment)
    )

  private def buildXmlWithDeclaration(submission: Submission): String =
    s"""<?xml version="1.0" encoding="UTF-8" standalone="yes"?>${submission.toXml}"""

}
