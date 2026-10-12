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
import uk.gov.hmrc.automatedexportsystemfrontend.pages.amend.*
import uk.gov.hmrc.automatedexportsystemfrontend.pages.create.*
import uk.gov.hmrc.automatedexportsystemfrontend.queries.DiscrepancyPacking
import uk.gov.hmrc.automatedexportsystemfrontend.xml.XmlOps

class AmendSubmissionDataService @Inject() extends Logging {

  def buildAmendSubmission(userAnswers: UserAnswers, submissionId: String): Option[String] =
    val maybeSubmission = collectAmendUserAnswers(userAnswers, submissionId)
    maybeSubmission match {
      case Some(submission) =>
        Some(buildXmlWithDeclaration(submission))
      case None =>
        logger.warn(s"Could not gather required user answers to create amend IE507a submission for $submissionId")
        None
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

  private def collectAmendSeals(userAnswers: UserAnswers, submissionId: String): List[Seal] =
    userAnswers.get(AmendDiscrepancySealsPage(submissionId)).toList.zipWithIndex.map { case (seal, index) =>
      Seal(index + 1, seal)
    }

  private def collectAmendGoodsReference(userAnswers: UserAnswers, submissionId: String): List[GoodsReference] =
    userAnswers.get(AmendDiscrepancyReferencePage(submissionId)).toList.zipWithIndex.map { case (reference, index) =>
      GoodsReference(index + 1, reference.toInt)
    }

  private def collectAmendGoodsLocation(userAnswers: UserAnswers, submissionId: String): Option[LocationOfGoods] =
    for {
      locationType <- userAnswers.get(AmendLocationTypePage(submissionId))
      typeOfLocation = TypeOfLocation.fromUserAnswers(locationType)
      locationDetails <- userAnswers.get(AmendLocationIdPage(submissionId))
    } yield LocationOfGoods(
      typeOfLocation,
      QualifierOfTheIdentification.UnLocode,
      locationDetails.authorisationReferenceNumber,
      locationDetails.locationAdditionalIdentifier,
      locationDetails.unlocode
    )

  private def collectAmendActiveBorderTransportMeans(userAnswers: UserAnswers, submissionId: String): Option[ActiveBorderTransportMeans] =
    userAnswers.get(AmendDiscrepancyTransportMeansPage(submissionId)).map { transport =>
      ActiveBorderTransportMeans(transport.transportType, transport.transportIdNumber, transport.countryOfRegistration)
    }

  private def collectAmendTransportDocument(userAnswers: UserAnswers, submissionId: String): List[TransportDocument] =
    userAnswers.get(AmendDiscrepancyTransportDocPage(submissionId)).toList.zipWithIndex.map { case (document, index) =>
      TransportDocument(index + 1, document.documentType, document.referenceNumber)
    }

  private def collectCommodity(userAnswers: UserAnswers): Option[Commodity] =
    userAnswers.get(DiscrepancyGoodsPage).map { goods =>
      Commodity(goods.newGrossMass, goods.newNetMass)
    }

  private def collectPackaging(userAnswers: UserAnswers): List[Packaging] =
    DiscrepancyPacking.getAll(userAnswers).zipWithIndex.map { (packing, index) =>
      Packaging(index + 1, packing.packagingCode, packing.numberOfPackages.toString, packing.shippingMarks)
    }

  private def collectGoodsItem(userAnswers: UserAnswers): Option[GoodsItem] =
    userAnswers.get(DiscrepancyGoodsPage).map { goods =>
      GoodsItem(
        goods.declarationGoodsItemNumber,
        goods.declarationUniqueConsignmentReference,
        Commodity(goods.newGrossMass, goods.newNetMass),
        collectPackaging(userAnswers)
      )
    }

  private def collectAmendGoodsItem(userAnswers: UserAnswers, submissionId: String): Option[GoodsItem] =
    userAnswers.get(AmendDiscrepancyGoodsPage(submissionId)).map { goods =>
      GoodsItem(
        goods.declarationGoodsItemNumber,
        goods.declarationUniqueConsignmentReference,
        Commodity(goods.newGrossMass, goods.newNetMass),
        collectPackaging(userAnswers)
      )
    }

  private def collectAmendGoodsShipment(userAnswers: UserAnswers, submissionId: String): Option[GoodsShipment] =
    for {
      ducr <- userAnswers.get(AmendEnterDucrPage(submissionId))
      location <- collectAmendGoodsLocation(userAnswers, submissionId)
    } yield {
      val transportMode =
        userAnswers.get(AmendDiscrepancyConsignmentPage(submissionId)).map(TransportMode.fromUserAnswers)

      val mucr =
        userAnswers.get(AmendPartOfConsolidationPage(submissionId)).flatMap(_.mucr)

      val seals = collectAmendSeals(userAnswers, submissionId)
      val goodsReference = collectAmendGoodsReference(userAnswers, submissionId)
      val transportEquipment =
        collectAmendTransportEquipment(userAnswers, seals, goodsReference, submissionId)

      val transport = collectAmendActiveBorderTransportMeans(userAnswers, submissionId)
      val transportDocument = collectAmendTransportDocument(userAnswers, submissionId)
      val goodsItem = collectAmendGoodsItem(userAnswers, submissionId)

      GoodsShipment(Consignment(transportMode, ducr, mucr, transportEquipment, location, transport, transportDocument), goodsItem)
    }

  private def collectAmendUserAnswers(userAnswers: UserAnswers, submissionId: String): Option[Submission] =
    for {
      mrn <- userAnswers.get(AmendEnterMrnPage(submissionId))
      discrepanciesExist <- userAnswers.get(AmendAnyDiscrepanciesPage(submissionId))
      splitIndicator <- userAnswers.get(AmendIsSplitExitPage(submissionId))
      referenceNumber <- userAnswers.get(AmendOfficeOfExitPage(submissionId))
    } yield Submission(
      Some(submissionId),
      ExportOperation(Standard, mrn, discrepanciesExist, splitIndicator),
      CustomsOfficeOfExitActual(referenceNumber.toString),
      collectAmendGoodsShipment(userAnswers, submissionId)
    )

  private def buildXmlWithDeclaration(submission: Submission): String =
    s"""<?xml version="1.0" encoding="UTF-8" standalone="yes"?>${submission.toXml}"""
}
