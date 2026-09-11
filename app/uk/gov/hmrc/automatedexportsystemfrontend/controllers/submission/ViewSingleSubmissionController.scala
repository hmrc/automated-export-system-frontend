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
import uk.gov.hmrc.automatedexportsystemfrontend.models.{SingleSubmissionCustomsOfficeOfExitActual, SingleSubmissionExportOperation}
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.checkAnswers.Amend.{
  AmendAnyDiscrepanciesSummary,
  AmendEnterMrnSummary,
  AmendIsSplitExitSummary,
  AmendOfficeOfExitSummary
}
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.checkAnswers.Create.{AnyDiscrepanciesSummary, EnterMrnSummary}
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.govuk.all.SummaryListViewModel
import uk.gov.hmrc.automatedexportsystemfrontend.views.html.submission.ViewSingleSubmissionView
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ViewSingleSubmissionController @Inject() (
  override val messagesApi: MessagesApi,
  val actionBuilder: AesAuthRequestActionBuilder,
  getData: AesDataRetrievalAction,
  requireData: AesDataRequiredAction,
  val controllerComponents: MessagesControllerComponents,
  view: ViewSingleSubmissionView,
  automatedExportSystemConnector: AutomatedExportSystemConnector
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport {

//  def onPageLoad: Action[AnyContent] = (actionBuilder andThen getData andThen requireData) { implicit request =>
  def onPageLoad: Action[AnyContent] = (actionBuilder andThen getData).async { implicit request =>
    automatedExportSystemConnector.getSingleSubmissionTestOnly("12345").flatMap { submission =>
      Future.successful(
        Ok(
          view(
            SummaryListViewModel(exportOperationRowsGenerator(submission.exportOperation, submission.submissionId).flatten),
            SummaryListViewModel(customsOfficeOfExitRowsGenerator(submission.customsOfficeOfExitActual, submission.submissionId).flatten)
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
      // TODO Check -> There's apparently a goods being stored but we only have a message file for it I don't know if this even exists
      AmendAnyDiscrepanciesSummary.row(answers.discrepanciesExist, submissionId, false),
      AmendIsSplitExitSummary.row(answers.splitIndicator, submissionId, false)
    )

  private def exportOperationRRowsGenerator(answers: SingleSubmissionExportOperation, submissionId: String)(
    implicit messages: Messages
  ): Seq[Option[SummaryListRow]] =
    Seq.empty

  private def customsOfficeOfExitRowsGenerator(answers: SingleSubmissionCustomsOfficeOfExitActual, submissionId: String)(
    implicit messages: Messages
  ): Seq[Option[SummaryListRow]] =
    Seq(AmendOfficeOfExitSummary.row(answers.referenceNumber, submissionId, false))
}
