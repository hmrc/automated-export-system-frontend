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

import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.automatedexportsystemfrontend.connectors.AutomatedExportSystemConnector
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.actions.{AesAuthRequestActionBuilder, AesDataRequiredAction, AesDataRetrievalAction}
import uk.gov.hmrc.automatedexportsystemfrontend.models.{
  SingleSubmissionActiveBorderTransportMeans,
  SingleSubmissionConsignment,
  SingleSubmissionCustomsOfficeOfExitActual,
  SingleSubmissionExportOperation,
  SingleSubmissionGoodsItem,
  SingleSubmissionGoodsShipment,
  SingleSubmissionLocationOfGoods,
  SingleSubmissionTransportDocument,
  SingleSubmissionTransportEquipment
}
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.checkAnswers.Amend.{
  AmendAnyDiscrepanciesSummary,
  AmendDiscrepancyConsignmentSummary,
  AmendDiscrepancyGoodsSummary,
  AmendEnterDucrSummary,
  AmendEnterMrnSummary,
  AmendIsSplitExitSummary,
  AmendLocationIdSummary,
  AmendLocationTypeSummary,
  AmendOfficeOfExitSummary
}
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.checkAnswers.Create.{AnyDiscrepanciesSummary, EnterMrnSummary}
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.govuk.all.SummaryListViewModel
import uk.gov.hmrc.automatedexportsystemfrontend.views.html.submission.ViewSingleSubmissionView
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.submission.SingleSubmissionHelper
import uk.gov.hmrc.automatedexportsystemfrontend.views.submission.lookups.SubmissionLookups

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ViewSingleSubmissionController @Inject() (
  override val messagesApi: MessagesApi,
  val actionBuilder: AesAuthRequestActionBuilder,
  getData: AesDataRetrievalAction,
  requireData: AesDataRequiredAction,
  val controllerComponents: MessagesControllerComponents,
  view: ViewSingleSubmissionView,
  automatedExportSystemConnector: AutomatedExportSystemConnector,
  singleSubmissionHelper: SingleSubmissionHelper
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport {

  //  def onPageLoad: Action[AnyContent] = (actionBuilder andThen getData andThen requireData) { implicit request =>
  def onPageLoad(submissionId: String): Action[AnyContent] = (actionBuilder andThen getData).async { implicit request =>
    automatedExportSystemConnector.getSingleSubmissionTestOnly("12345").flatMap { submission =>

      val submissionId = submission.submissionId

      val submissionStatus = SubmissionLookups.mapStatus(submission.exportOperation.exportOperationType)

      val updatedAt = submission.updatedAt.toLocalDate

      val locationOfGoods = submission.goodsShipment.map(_.consignment.locationOfGoods)

      val consignment = submission.goodsShipment.map(_.consignment)

      val transportEquipment = submission.goodsShipment.flatMap(_.consignment.transportEquipment)

      val activeBorderTransportMeans = submission.goodsShipment.flatMap(_.consignment.activeBorderTransportMeans)

      val transportDocument = submission.goodsShipment.flatMap(_.consignment.transportDocument)

      val goodsItem = submission.goodsShipment.flatMap(_.goodsItems)

      Future.successful(
        Ok(
          view(
            submissionId,
            submissionStatus,
            updatedAt,
            SummaryListViewModel(exportOperationRowsGenerator(submission.exportOperation, submissionId).flatten),
            SummaryListViewModel(customsOfficeOfExitRowsGenerator(submission.customsOfficeOfExitActual, submissionId).flatten),
            Some(SummaryListViewModel(consignmentRowsGenerator(consignment, submissionId).flatten)),
            Some(SummaryListViewModel(transportEquipmentRowsGenerator(transportEquipment, submissionId).flatMap(_.flatten))),
            Some(SummaryListViewModel(locationOfGoodsRowsGenerator(locationOfGoods, submissionId).flatten)),
            Some(SummaryListViewModel(activeBorderTransportMeansRowsGenerator(activeBorderTransportMeans, submissionId).flatten)),
            Some(SummaryListViewModel(transportDocumentRowsGenerator(transportDocument, submissionId).flatten)),
            Some(SummaryListViewModel(goodsItemRowsGenerator(goodsItem, submissionId).flatMap(_.flatten)))
          )
        )
      )
    }
  }

  private def exportOperationRowsGenerator(answers: SingleSubmissionExportOperation, submissionId: String)(
    implicit messages: Messages
  ): Seq[Option[SummaryListRow]] =
    Seq(
      AmendEnterMrnSummary.row(answers.mrn, submissionId, false),
      AmendAnyDiscrepanciesSummary.row(answers.discrepanciesExist, submissionId, false),
      AmendIsSplitExitSummary.row(answers.splitIndicator, submissionId, false)
    )

  private def consignmentRowsGenerator(answers: Option[SingleSubmissionConsignment], submissionId: String)(
    implicit messages: Messages
  ): Seq[Option[SummaryListRow]] =
    Seq(
      singleSubmissionHelper.modeOfTransportAtBorderHandler(answers.flatMap(_.modeOfTransportAtTheBorder), submissionId, false),
      AmendEnterDucrSummary.row(answers.map(_.referenceNumberUCR).get, submissionId, false),
      singleSubmissionHelper.parentUCRIDHandler(answers.flatMap(_.parentUCRID), submissionId, false)
    )

  private def transportEquipmentRowsGenerator(answers: Option[Seq[SingleSubmissionTransportEquipment]], submissionId: String)(
    implicit messages: Messages
  ): Seq[Seq[Option[SummaryListRow]]] =
    answers.toSeq.flatten.flatMap { answer =>
      Seq(
        Seq(singleSubmissionHelper.containerIdHandler(answer.containerIdentificationNumber, submissionId, false)),
        Seq(singleSubmissionHelper.numberOfSealsHandler(answer.numberOfSeals, submissionId, false)),
        answer.seal.toSeq.flatten.flatMap { value =>
          Seq(
            // TODO, commented out until we understand how we are going to display indexed to user
            // singleSubmissionHelper.sequenceNumberHandler(value.sequenceNumber, submissionId, false),
            singleSubmissionHelper.sealIdentifierHandler(value.identifier, submissionId, false)
          )
        },
        answer.goodsReference.toSeq.flatten.flatMap { value =>
          Seq(
            // TODO, commented out until we understand how we are going to display indexed to user
            // singleSubmissionHelper.sequenceNumberHandler(value.sequenceNumber, submissionId, false),
            singleSubmissionHelper.declarationGoodsItemNumberHandler(value.declarationGoodsItemNumber, submissionId, false)
          )
        }
      )
    }

  private def customsOfficeOfExitRowsGenerator(answers: SingleSubmissionCustomsOfficeOfExitActual, submissionId: String)(
    implicit messages: Messages
  ): Seq[Option[SummaryListRow]] =
    Seq(AmendOfficeOfExitSummary.row(answers.referenceNumber, submissionId, false))

  private def locationOfGoodsRowsGenerator(answers: Option[SingleSubmissionLocationOfGoods], submissionId: String)(
    implicit messages: Messages
  ): Seq[Option[SummaryListRow]] =
    Seq(
      AmendLocationTypeSummary.row(answers.map(_.typeOfLocation).get, submissionId, false),
      AmendLocationIdSummary.qualifierRow(answers.map(_.qualifierOfIdentification).get, submissionId, false),
      singleSubmissionHelper.authorisationNumberHandler(answers.flatMap(_.authorisationNumber), submissionId, false),
      singleSubmissionHelper.additionalIdHandler(answers.flatMap(_.additionalIdentifier), submissionId, false),
      singleSubmissionHelper.unloHandler(answers.flatMap(_.UNLocode), submissionId, false)
    )

  private def activeBorderTransportMeansRowsGenerator(answers: Option[SingleSubmissionActiveBorderTransportMeans], submissionId: String)(
    implicit messages: Messages
  ): Seq[Option[SummaryListRow]] =
    Seq(
      singleSubmissionHelper.transportTypeHandler(answers.flatMap(_.typeOfIdentification), submissionId, false),
      singleSubmissionHelper.transportIdHandler(answers.flatMap(_.identificationNumber), submissionId, false),
      singleSubmissionHelper.countryOfRegistrationHandler(answers.flatMap(_.nationality), submissionId, false)
    )

  private def transportDocumentRowsGenerator(answers: Option[Seq[SingleSubmissionTransportDocument]], submissionId: String)(
    implicit messages: Messages
  ): Seq[Option[SummaryListRow]] =
    answers.toSeq.flatten.flatMap { answer =>
      Seq(
        // TODO, commented out until we understand how we are going to display indexed to user
        // singleSubmissionHelper.sequenceNumberHandler(answer.sequenceNumber, submissionId, false),
        singleSubmissionHelper.docTypeHandler(answer.`type`, submissionId, false),
        singleSubmissionHelper.docReferenceHandler(answer.referenceNumber, submissionId, false)
      )

    }

  private def goodsItemRowsGenerator(answers: Option[Seq[SingleSubmissionGoodsItem]], submissionId: String)(
    implicit messages: Messages
  ): Seq[Seq[Option[SummaryListRow]]] =
    answers.toSeq.flatten.flatMap { answer =>
      Seq(
        Seq(singleSubmissionHelper.goodsItemNumberHandler(answer.declarationGoodsItemNumber, submissionId, false)),
        Seq(singleSubmissionHelper.goodsItemDucrHandler(answer.referenceNumberUCR, submissionId, false)),
        Seq(AmendDiscrepancyGoodsSummary.grossMassRow(answer.commodity.grossMass, submissionId, false)),
        Seq(AmendDiscrepancyGoodsSummary.netMassRow(answer.commodity.netMass, submissionId, false)),
        answer.packaging.toSeq.flatten.flatMap { value =>
          Seq(
            // TODO, commented out until we understand how we are going to display indexed to user
            // singleSubmissionHelper.sequenceNumberHandler(answer.sequenceNumber, submissionId, false),
            singleSubmissionHelper.typeOfPackagesHandler(value.typeOfPackages, submissionId, false),
            singleSubmissionHelper.numberOfPackagesHandler(value.numberOfPackages, submissionId, false),
            singleSubmissionHelper.shippingMarksHandler(value.shippingMarks, submissionId, false)
          )

        }
      )

    }

}
