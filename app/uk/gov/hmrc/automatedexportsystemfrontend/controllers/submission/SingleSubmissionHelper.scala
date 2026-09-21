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

package uk.gov.hmrc.automatedexportsystemfrontend.controllers.submission

import play.api.i18n.Messages
import uk.gov.hmrc.automatedexportsystemfrontend.models.SingleSubmissionGoodsReference
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.checkAnswers.Amend.{
  AmendDiscrepancyConsignmentSummary,
  AmendDiscrepancyReferenceSummary,
  AmendDiscrepancySealsSummary,
  AmendDiscrepancyTransportDocSummary,
  AmendDiscrepancyTransportMeansSummary,
  AmendDiscrepancyTransportSummary,
  AmendLocationIdSummary,
  AmendLocationTypeSummary,
  AmendPartOfConsolidationSummary
}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow

class SingleSubmissionHelper {

  def modeOfTransportAtBorderHandler(modeOfTransport: Option[Int], submissionId: String, withChangeLink: Boolean)(
    implicit messages: Messages
  ): Option[SummaryListRow] =
    modeOfTransport match {
      case Some(mode) => AmendDiscrepancyConsignmentSummary.row(mode, submissionId, withChangeLink)
      case _          => None
    }

  def parentUCRIDHandler(referenceNumber: Option[String], submissionId: String, withChangeLink: Boolean)(
    implicit messages: Messages
  ): Option[SummaryListRow] =
    referenceNumber match {
      case Some(number) => AmendPartOfConsolidationSummary.row(number, submissionId, withChangeLink)
      case _            => None
    }

  def containerIdHandler(containerId: Option[String], submissionId: String, withChangeLink: Boolean)(
    implicit messages: Messages
  ): Option[SummaryListRow] =
    containerId match {
      case Some(id) =>
        AmendDiscrepancyTransportSummary.containerIdRow(id, submissionId, withChangeLink)
      case _ => None
    }

  def numberOfSealsHandler(numberOfSeals: Option[Int], submissionId: String, withChangeLink: Boolean)(
    implicit messages: Messages
  ): Option[SummaryListRow] =
    numberOfSeals match {
      case Some(number) => AmendDiscrepancyTransportSummary.numberOfSealsRow(number, submissionId, withChangeLink)
      case _            => None
    }

  // TODO, not sure if we've got the designs to handle how we display the indexed items to the user, this can maybe
  // TODO be updated to a generic index handler and be reused, for now will leave commented out and we can revisit
//  def sequenceNumberHandler(sequenceNumber: Option[Int], submissionId: String, withChangeLink: Boolean)(
//    implicit messages: Messages
//  ): Option[SummaryListRow] =
//    sequenceNumber.flatMap { value =>
//      AmendDiscrepancyTransportSummary.numberOfSealsRow(value, submissionId, withChangeLink)
//    }

  def sealIdentifierHandler(numberOfSeals: Option[String], submissionId: String, withChangeLink: Boolean)(
    implicit messages: Messages
  ): Option[SummaryListRow] =
    numberOfSeals.flatMap { value =>
      AmendDiscrepancySealsSummary.row(value, submissionId, withChangeLink)
    }

  def declarationGoodsItemNumberHandler(declarationGoods: Option[Int], submissionId: String, withChangeLink: Boolean)(
    implicit messages: Messages
  ): Option[SummaryListRow] =
    declarationGoods.flatMap { value =>
      AmendDiscrepancyReferenceSummary.row(value, submissionId, withChangeLink)
    }

  def goodsReferenceHandler(goodsReference: Option[Seq[SingleSubmissionGoodsReference]], submissionId: String, withChangeLink: Boolean)(
    implicit messages: Messages
  ): Seq[Option[SummaryListRow]] =
    goodsReference.toSeq.flatten.flatMap { value =>
      Some(value.declarationGoodsItemNumber match {
        case Some(value) => AmendDiscrepancyTransportSummary.numberOfSealsRow(value, submissionId, withChangeLink)
        case _           => None
      })
    }

  def authorisationNumberHandler(authorisationNumber: Option[String], submissionId: String, withChangeLink: Boolean)(
    implicit messages: Messages
  ): Option[SummaryListRow] =
    authorisationNumber match {
      case Some(number) => AmendLocationIdSummary.authNumberRow(number, submissionId, withChangeLink)
      case _            => None
    }

  def additionalIdHandler(additionalId: Option[String], submissionId: String, withChangeLink: Boolean)(
    implicit messages: Messages
  ): Option[SummaryListRow] =
    additionalId match {
      case Some(number) => AmendLocationIdSummary.additionalIdRow(number, submissionId, withChangeLink)
      case _            => None
    }

  def unloHandler(unlo: Option[String], submissionId: String, withChangeLink: Boolean)(implicit messages: Messages): Option[SummaryListRow] =
    unlo match {
      case Some(code) => AmendLocationIdSummary.unloRow(code, submissionId, withChangeLink)
      case _          => None
    }

  def transportTypeHandler(borderType: Option[String], submissionId: String, withChangeLink: Boolean)(
    implicit messages: Messages
  ): Option[SummaryListRow] =
    borderType match {
      case Some(value) => AmendDiscrepancyTransportMeansSummary.transportTypeRow(value, submissionId, withChangeLink)
      case _           => None
    }

  def transportIdHandler(id: Option[String], submissionId: String, withChangeLink: Boolean)(implicit messages: Messages): Option[SummaryListRow] =
    id match {
      case Some(value) => AmendDiscrepancyTransportMeansSummary.transportIdRow(value, submissionId, withChangeLink)
      case _           => None
    }

  def countryOfRegistrationHandler(id: Option[String], submissionId: String, withChangeLink: Boolean)(
    implicit messages: Messages
  ): Option[SummaryListRow] =
    id match {
      case Some(value) => AmendDiscrepancyTransportMeansSummary.countryOfRegistrationRow(value, submissionId, withChangeLink)
      case _           => None
    }

  def docTypeHandler(docType: Option[Int], submissionId: String, withChangeLink: Boolean)(implicit messages: Messages): Option[SummaryListRow] =
    docType match {
      case Some(value) => AmendDiscrepancyTransportDocSummary.transportTypeRow(value, submissionId, withChangeLink)
      case _           => None
    }

  def docReferenceHandler(docReference: Option[String], submissionId: String, withChangeLink: Boolean)(
    implicit messages: Messages
  ): Option[SummaryListRow] =
    docReference match {
      case Some(value) => AmendDiscrepancyTransportDocSummary.docReferenceRow(value, submissionId, withChangeLink)
      case _           => None
    }
}
