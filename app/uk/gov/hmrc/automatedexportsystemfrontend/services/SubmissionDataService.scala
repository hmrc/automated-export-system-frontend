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

  private def collectTransportEquipment(userAnswers: UserAnswers): List[TransportEquipment] = {
    val discrepancyTransport = userAnswers.get(DiscrepancyTransportPage).toList
    val discrepancySeals = userAnswers.get(DiscrepancySealsPage).toList
    discrepancyTransport.zipWithIndex.map { case (transport, transportIndex) =>
      TransportEquipment(
        transportIndex + 1,
        transport.containerId,
        transport.numberOfSeals,
        discrepancySeals.zipWithIndex.map { case (seal, sealIndex) =>
          Seal(sealIndex + 1, seal)
        }
      )
    }
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

  private def collectUserAnswers(userAnswers: UserAnswers): Option[Submission] =
    for {
      mrn <- userAnswers.get(EnterMrnPage)
      discrepanciesExist <- userAnswers.get(AnyDiscrepanciesPage)
      splitIndicator <- userAnswers.get(IsSplitExitPage)
      referenceNumber <- userAnswers.get(OfficeOfExitPage)

      goodsShipment = for {
        discrepancyConsignment <- userAnswers.get(DiscrepancyConsignmentPage)
        transportMode = TransportMode.fromUserAnswers(discrepancyConsignment)
        ducr <- userAnswers.get(DiscrepancyDucrPage)
        mucr <- userAnswers.get(DiscrepancyMucrPage)
        transportEquipment = collectTransportEquipment(userAnswers)
        location <- collectGoodsLocation(userAnswers)
      } yield GoodsShipment(Consignment(transportMode, ducr, mucr, transportEquipment, location))

    } yield Submission(
      None,
      ExportOperation(Standard, mrn, discrepanciesExist, splitIndicator),
      CustomsOfficeOfExitActual(referenceNumber.toString),
      goodsShipment
    )

  private def buildXmlWithDeclaration(submission: Submission): String =
    s"""<?xml version="1.0" encoding="UTF-8" standalone="yes"?>${submission.toXml}"""

}
