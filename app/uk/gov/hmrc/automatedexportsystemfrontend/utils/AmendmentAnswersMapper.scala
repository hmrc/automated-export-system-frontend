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

import uk.gov.hmrc.automatedexportsystemfrontend.models.{
  LocationDetails,
  LocationQualifier,
  LocationType,
  ModeOfTransportAtBorder,
  OfficeOfExit,
  PartOfConsolidationAnswer,
  SingleSubmissionLocationOfGoods,
  SingleSubmissionResponse,
  UserAnswers
}
import uk.gov.hmrc.automatedexportsystemfrontend.pages.amend.{
  AmendAnyDiscrepanciesPage,
  AmendDiscrepancyConsignmentPage,
  AmendEnterDucrPage,
  AmendEnterMrnPage,
  AmendIsSplitExitPage,
  AmendLocationIdPage,
  AmendLocationTypePage,
  AmendOfficeOfExitPage,
  AmendPartOfConsolidationPage
}

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
      case "unlocode" =>
        LocationQualifier.UnLocode

      case "authorisationnumber" =>
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
          unlocode = loc.UNLocode.getOrElse(""),
          locationAdditionalIdentifier = loc.additionalIdentifier.getOrElse(""),
          authorisationReferenceNumber = loc.authorisationNumber.getOrElse("")
        )
      )
    } yield a2

  def toUserAnswers(sessionId: String, submission: SingleSubmissionResponse): Try[UserAnswers] = {
    val submissionId = submission.submissionId
    val consignmentOpt = submission.goodsShipment.map(_.consignment)

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
          b4 <- consignment.modeOfTransportAtTheBorder match {
            case Some(modeCode) =>
              ModeOfTransportAtBorder.fromCode(modeCode) match {
                case Some(mode) => b3.set(AmendDiscrepancyConsignmentPage(submissionId), mode)
                case None       => Try(b3)
              }
            case None =>
              Try(b3)
          }
        } yield b4
      }
    } yield a5
  }

  private def b1IfDucrPresentOrSame(answers: UserAnswers, submissionId: String, ducr: String): Try[UserAnswers] =
    Option(ducr).filter(_.nonEmpty) match {
      case Some(value) => answers.set(AmendEnterDucrPage(submissionId), value)
      case None        => Try(answers)
    }
}
