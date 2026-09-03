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
import uk.gov.hmrc.automatedexportsystemfrontend.models.{ModeOfTransportAtBorder, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.pages.create.*
import uk.gov.hmrc.automatedexportsystemfrontend.xml.XmlOps

class SubmissionDataService @Inject() extends Logging {

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

  private def collectTransportEquipment(
    userAnswers: UserAnswers,
    seals: List[Seal],
    goodsReferences: List[GoodsReference]
  ): List[TransportEquipment] = {
    val discrepancyTransport = userAnswers.get(DiscrepancyTransportPage).toList
    discrepancyTransport.zipWithIndex.map { case (transport, transportIndex) =>
      TransportEquipment(transportIndex + 1, transport.containerId, transport.numberOfSeals, seals, goodsReferences)
    }
  }

  private def collectSeals(userAnswers: UserAnswers): List[Seal] =
    userAnswers.get(DiscrepancySealsPage).toList.zipWithIndex.map { case (seal, index) =>
      Seal(index + 1, seal)
    }

  private def collectGoodsReference(userAnswers: UserAnswers): List[GoodsReference] =
    userAnswers.get(DiscrepancyReferencePage).toList.zipWithIndex.map { case (reference, index) =>
      GoodsReference(index + 1, reference.toInt)
    }

  private def collectGoodsLocation(userAnswers: UserAnswers): Option[LocationOfGoods] =
    for {
      locationType <- userAnswers.get(LocationTypePage)
      typeOfLocation = TypeOfLocation.fromUserAnswers(locationType)
      locationDetails <- userAnswers.get(LocationIdPage)
    } yield LocationOfGoods(
      typeOfLocation,
      QualifierOfTheIdentification.UnLocode,
      locationDetails.authorisationReferenceNumber,
      locationDetails.locationAdditionalIdentifier,
      locationDetails.unlocode
    )

  private def collectActiveBorderTransportMeans(userAnswers: UserAnswers): Option[ActiveBorderTransportMeans] =
    userAnswers.get(DiscrepancyTransportMeansPage).map { transport =>
      ActiveBorderTransportMeans(transport.transportType, transport.transportIdNumber, transport.countryOfRegistration)
    }

  private def collectTransportDocument(userAnswers: UserAnswers): List[TransportDocument] =
    userAnswers.get(DiscrepancyTransportDocPage).toList.zipWithIndex.map { case (document, index) =>
      TransportDocument(index + 1, document.documentType, document.referenceNumber)
    }

  private def collectCommodity(userAnswers: UserAnswers): Option[Commodity] =
    userAnswers.get(DiscrepancyGoodsPage).map { goods =>
      Commodity(goods.newGrossMass, goods.newNetMass)
    }

  private def collectPackaging(userAnswers: UserAnswers): List[Packaging] =
    userAnswers.get(DiscrepancyPackingPage).toList.map { packing =>
      Packaging(1, packing.packagingCode, packing.numberOfPackages.toString, packing.shippingMarks)
    }

  private def collectUserAnswers(userAnswers: UserAnswers): Option[Submission] =
    for {
      mrn <- userAnswers.get(EnterMrnPage)
      discrepanciesExist <- collectDiscrepanciesExist(userAnswers)
      splitIndicator <- userAnswers.get(IsSplitExitPage)
      referenceNumber <- userAnswers.get(OfficeOfExitPage)

      goodsShipment = for {
        discrepancyConsignment <- userAnswers.get(DiscrepancyConsignmentPage)
        transportMode = TransportMode.fromUserAnswers(discrepancyConsignment)
        ducr <- userAnswers.get(EnterDucrPage)
        part = userAnswers.get(PartOfConsolidationPage)
        mucr = part.flatMap(_.mucr)
        seals = collectSeals(userAnswers)
        goodsReference = collectGoodsReference(userAnswers)
        transportEquipment = collectTransportEquipment(userAnswers, seals, goodsReference)
        location <- collectGoodsLocation(userAnswers)
        transport = collectActiveBorderTransportMeans(userAnswers)
        transportDocument = collectTransportDocument(userAnswers)
        goods <- userAnswers.get(DiscrepancyGoodsPage)
        declarationGoodsItemNumber = goods.declarationGoodsItemNumber
        referenceNumberUCR = goods.declarationUniqueConsignmentReference
        commodity <- collectCommodity(userAnswers)
        packaging = collectPackaging(userAnswers)
      } yield GoodsShipment(
        Consignment(transportMode, ducr, mucr, transportEquipment, location, transport, transportDocument),
        GoodsItem(declarationGoodsItemNumber, referenceNumberUCR, commodity, packaging)
      )
    } yield Submission(
      None,
      ExportOperation(Standard, mrn, discrepanciesExist, splitIndicator),
      CustomsOfficeOfExitActual(referenceNumber.toString),
      goodsShipment
    )

  private def buildXmlWithDeclaration(submission: Submission): String =
    s"""<?xml version="1.0" encoding="UTF-8" standalone="yes"?>${submission.toXml}"""

}
