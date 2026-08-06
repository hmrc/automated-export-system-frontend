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

import play.api.i18n.Lang.logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.actions.{AesAuthRequestActionBuilder, AesDataRequiredAction, AesDataRetrievalAction}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.automatedexportsystemfrontend.connectors.AutomatedExportSystemConnector
import uk.gov.hmrc.automatedexportsystemfrontend.services.SubmissionDataService

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class SubmissionController @Inject() (
  override val messagesApi: MessagesApi,
  val actionBuilder: AesAuthRequestActionBuilder,
  getData: AesDataRetrievalAction,
  requireData: AesDataRequiredAction,
  automatedExportSystemConnector: AutomatedExportSystemConnector,
  submissionDataService: SubmissionDataService,
  val controllerComponents: MessagesControllerComponents
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport {

  def standardSubmit: Action[AnyContent] =
    (actionBuilder andThen getData andThen requireData).async { implicit request =>
      submissionDataService.buildStandardSubmission(request.userAnswers) match {
        case Some(xmlSubmission) =>
          automatedExportSystemConnector
            .submitIE507a(xmlSubmission)
            .map { _ =>
              // TODO Delete All UserAnswers by directory on success when methods become available
              Redirect(
                uk.gov.hmrc.automatedexportsystemfrontend.controllers.submission.routes.StandardSubmissionConfirmationController.onPageLoad().url
              )
            }
            .recover { case ex =>
              logger.error("Unexpected response from standard submitIE507a", ex)
              // TODO recover somewhere more graceful when available
              Redirect(uk.gov.hmrc.automatedexportsystemfrontend.controllers.problem.routes.JourneyRecoveryController.onPageLoad().url)
            }
        case None =>
          logger.error("Failed to build XML due to missing user answers when submitting standard IE507a")
          // TODO Delete All UserAnswers by directory on success when methods become available
          Future.successful(Redirect(uk.gov.hmrc.automatedexportsystemfrontend.controllers.problem.routes.JourneyRecoveryController.onPageLoad().url))
      }
    }
}
