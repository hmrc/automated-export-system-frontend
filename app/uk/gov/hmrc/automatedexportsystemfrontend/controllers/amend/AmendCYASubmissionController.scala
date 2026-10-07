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

package uk.gov.hmrc.automatedexportsystemfrontend.controllers.amend

import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.automatedexportsystemfrontend.connectors.AutomatedExportSystemConnector
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.actions.{AesAuthRequestActionBuilder, AesDataRequiredAction, AesDataRetrievalAction}
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.problem.routes as problemRoute
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.submission.routes as submissionRoute
import uk.gov.hmrc.automatedexportsystemfrontend.models.Mode
import uk.gov.hmrc.automatedexportsystemfrontend.navigation.AmendNavigator
import uk.gov.hmrc.automatedexportsystemfrontend.repositories.SessionRepository
import uk.gov.hmrc.automatedexportsystemfrontend.services.{AmendSubmissionDataService, CreateSubmissionDataService}
import uk.gov.hmrc.automatedexportsystemfrontend.utils.UserAnswerHelper
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.checkAnswers.Amend.*
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.govuk.all.SummaryListViewModel
import uk.gov.hmrc.automatedexportsystemfrontend.views.html.amend.AmendCYASubmissionView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

class AmendCYASubmissionController @Inject() (
  override val messagesApi: MessagesApi,
  val actionBuilder: AesAuthRequestActionBuilder,
  getData: AesDataRetrievalAction,
  requireData: AesDataRequiredAction,
  val controllerComponents: MessagesControllerComponents,
  view: AmendCYASubmissionView,
  amendNavigator: AmendNavigator,
  submissionDataService: AmendSubmissionDataService,
  automatedExportSystemConnector: AutomatedExportSystemConnector,
  sessionRepository: SessionRepository,
  userAnswerHelper: UserAnswerHelper
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport {

  val logger: Logger = Logger(this.getClass.getName)

  def onPageLoad(mode: Mode, submissionId: String): Action[AnyContent] =
    (actionBuilder andThen getData andThen requireData).async { implicit request =>

      val answers = request.userAnswers

      val exportOperationList =
        SummaryListViewModel(Seq(AmendEnterMrnSummary.row(answers)(submissionId), AmendIsSplitExitSummary.row(answers)(submissionId)).flatten)

      val consignmentList = SummaryListViewModel(
        Seq(
          AmendEnterDucrSummary.row(answers)(submissionId),
          AmendDiscrepancyConsignmentSummary.row(answers)(submissionId),
          AmendPartOfConsolidationSummary.row(answers)(submissionId)
        ).flatten
      )

      val customsOfficeExitList = SummaryListViewModel(Seq(AmendOfficeOfExitSummary.row(answers)(submissionId)).flatten)

      val discrepancyList = SummaryListViewModel(
        Seq(AmendAnyDiscrepanciesSummary.row(answers)(submissionId), AmendDiscrepancyConsignmentSummary.row(answers)(submissionId)).flatten
      )
      Future.successful(Ok(view(mode, submissionId, exportOperationList, consignmentList, customsOfficeExitList, discrepancyList)))
    }

  def onSubmit(mode: Mode, submissionId: String): Action[AnyContent] =
    (actionBuilder andThen getData andThen requireData).async { implicit request =>
      submissionDataService.buildAmendSubmission(request.userAnswers, submissionId) match {
        case Some(xmlSubmission) =>
          automatedExportSystemConnector
            .submitIE507a(xmlSubmission.toString)
            .flatMap { _ =>
              sessionRepository.set(userAnswerHelper.removeAmendSubmissionAnswers(submissionId, request.userAnswers)).map { _ =>
                Redirect(submissionRoute.StandardSubmissionConfirmationController.onPageLoad().url)
              }
            }
            .recoverWith { case NonFatal(ex) =>
              logger.warn("Unexpected error from amend submission", ex)
              sessionRepository.set(userAnswerHelper.removeAmendSubmissionAnswers(submissionId, request.userAnswers)).map { _ =>
                Redirect(problemRoute.JourneyRecoveryController.onPageLoad().url)
              }
            }

        case None =>
          logger.error(s"Failed to build XML due to missing user answers when submitting amend IE507a. submissionId=$submissionId")
          sessionRepository
            .set(userAnswerHelper.removeAmendSubmissionAnswers(submissionId, request.userAnswers))
            .map { _ =>
              Redirect(problemRoute.JourneyRecoveryController.onPageLoad().url)
            }
      }
    }
}
