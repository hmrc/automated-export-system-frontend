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

package uk.gov.hmrc.automatedexportsystemfrontend.utils

import uk.gov.hmrc.automatedexportsystemfrontend.models.*
import uk.gov.hmrc.automatedexportsystemfrontend.pages.amend.*

import scala.util.Try

class AmendmentAnswersMapper {

  private def mapLocationType(value: String): LocationType =
    value match {
      case "designatedLocation" => LocationType.DesignatedLocation
      case "authorisedPlace"    => LocationType.AuthorisedPlace
      case "approvedPlace"      => LocationType.ApprovedPlace
      case _                    => LocationType.Other
    }

  private def mapLocationQualifier(value: String): LocationQualifier =
    value.trim.toLowerCase match {
      case "u" | "unlocode" =>
        LocationQualifier.UnLocode

      case "y" | "authorisationnumber" | "authnumber" =>
        LocationQualifier.AuthorisationNumber

      case _ =>
        LocationQualifier.UnLocode
    }

  private def setAmendLocation(answers: UserAnswers, submissionId: String, loc: SingleSubmissionLocationOfGoods): Try[UserAnswers] =
    for {
      a1 <- answers.set(AmendLocationTypePage(submissionId), mapLocationType(loc.typeOfLocation))
      a2 <- a1.set(
        AmendLocationIdPage(submissionId),
        LocationDetails(
          locationType = mapLocationQualifier(loc.qualifierOfIdentification),
          unlocode = loc.UNLocode,
          locationAdditionalIdentifier = loc.additionalIdentifier,
          authorisationReferenceNumber = loc.authorisationNumber
        )
      )
    } yield a2

  private def setTransportEquipment(
    answers: UserAnswers,
    submissionId: String,
    equipment: Seq[SingleSubmissionTransportEquipment]
  ): Try[UserAnswers] =
    equipment.foldLeft(Try(answers)) { (acc, item) =>
      acc.flatMap { current =>
        val currentTransport = item.containerIdentificationNumber.orElse(item.numberOfSeals.map(_.toString)).map(Some(_)).getOrElse(None)
        val currentSeals = item.seal.toSeq.flatten.flatMap(_.identifier)
        val currentRefs = item.goodsReference.toSeq.flatten.flatMap(_.declarationGoodsItemNumber).map(_.toString)

        for {
          a1 <- currentTransport.fold(Try(current))(value =>
            current.set(AmendDiscrepancyTransportPage(submissionId), ContainerDetails(Some(value), item.numberOfSeals))
          )
          a2 <- currentSeals.headOption.fold(Try(a1))(seal => a1.set(AmendDiscrepancySealsPage(submissionId), seal))
          a3 <- currentRefs.headOption.fold(Try(a2))(ref => a2.set(AmendDiscrepancyReferencePage(submissionId), ref))
        } yield a3
      }
    }

  private def setTransportMeans(answers: UserAnswers, submissionId: String, transport: SingleSubmissionActiveBorderTransportMeans): Try[UserAnswers] =
    transport.typeOfIdentification match {
      case Some(tpe) =>
        answers.set(
          AmendDiscrepancyTransportMeansPage(submissionId),
          TransportAcrossBorderDetails(
            transportType = Some(tpe),
            transportIdNumber = transport.identificationNumber,
            countryOfRegistration = transport.nationality
          )
        )
      case None =>
        Try(answers)
    }

  private def setTransportDocuments(answers: UserAnswers, submissionId: String, documents: Seq[SingleSubmissionTransportDocument]): Try[UserAnswers] =
    documents.foldLeft(Try(answers)) { (acc, doc) =>
      acc.flatMap { current =>
        for {
          a1 <- doc.`type`.fold(Try(current))(value =>
            current.set(AmendDiscrepancyTransportDocPage(submissionId), DocumentDetails(Some(value), doc.referenceNumber.flatMap(_.toIntOption)))
          )
        } yield a1
      }
    }

  private def setGoodsItems(answers: UserAnswers, submissionId: String, items: Seq[SingleSubmissionGoodsItem]): Try[UserAnswers] =
    items.foldLeft(Try(answers)) { (acc, item) =>
      acc.flatMap { current =>
        val goods = WhatHasChangedDetails(
          declarationGoodsItemNumber = item.declarationGoodsItemNumber,
          declarationUniqueConsignmentReference = item.referenceNumberUCR,
          newGrossMass = item.commodity.grossMass.toString,
          newNetMass = item.commodity.netMass.toString
        )

        val withGoods: Try[UserAnswers] =
          current.set(AmendDiscrepancyGoodsPage(submissionId), goods)

        item.packaging match {
          case Some(packs) =>
            packs.foldLeft(withGoods) { (innerAcc, pack) =>
              innerAcc.flatMap { ua =>
                pack.typeOfPackages match {
                  case Some(code) =>
                    ua.set(
                      AmendDiscrepancyPackingPage(submissionId),
                      PackingDetails(code, pack.numberOfPackages.flatMap(_.toIntOption).getOrElse(0), pack.shippingMarks.getOrElse(""))
                    )
                  case None =>
                    Try(ua)
                }
              }
            }

          case None =>
            withGoods
        }
      }
    }

  def toUserAnswers(sessionId: String, submission: SingleSubmissionResponse): Try[UserAnswers] = {
    val submissionId = submission.submissionId
    val consignmentOpt = submission.goodsShipment.map(_.consignment)

    val goodsItemsOpt = submission.goodsShipment.flatMap(_.goodsItems)
    for {
      base <- Try(UserAnswers(sessionId))
      a1 <- base.set(AmendEnterMrnPage(submissionId), submission.exportOperation.mrn)
      a2 <- a1.set(AmendIsSplitExitPage(submissionId), submission.exportOperation.splitIndicator == 1)
      a3 <- a2.set(AmendAnyDiscrepanciesPage(submissionId), submission.exportOperation.discrepanciesExist == 1)
      a4 <- a3.set(AmendOfficeOfExitPage(submissionId), OfficeOfExit.fromCode(submission.customsOfficeOfExitActual.referenceNumber))
      a5 <- consignmentOpt.fold(Try(a4)) { consignment =>
        for {
          b1 <- b1IfDucrPresentOrSame(a4, submissionId, consignment.referenceNumberUCR)
          b2 <- setAmendLocation(b1, submissionId, consignment.locationOfGoods)
          b3 <- b2.set(
            AmendPartOfConsolidationPage(submissionId),
            PartOfConsolidationAnswer(boolean = consignment.parentUCRID.isDefined, mucr = consignment.parentUCRID)
          )
          b4 <- consignment.transportEquipment.fold(Try(b3))(setTransportEquipment(b3, submissionId, _))
          b5 <- consignment.activeBorderTransportMeans.fold(Try(b4))(setTransportMeans(b4, submissionId, _))
          b6 <- consignment.transportDocument.fold(Try(b5))(setTransportDocuments(b5, submissionId, _))
          b7 <- goodsItemsOpt.fold(Try(b6))(setGoodsItems(b6, submissionId, _))
        } yield b7
      }
    } yield a5
  }

  private def b1IfDucrPresentOrSame(answers: UserAnswers, submissionId: String, ducr: String): Try[UserAnswers] =
    Option(ducr).filter(_.nonEmpty) match {
      case Some(value) => answers.set(AmendEnterDucrPage(submissionId), value)
      case None        => Try(answers)
    }
}
