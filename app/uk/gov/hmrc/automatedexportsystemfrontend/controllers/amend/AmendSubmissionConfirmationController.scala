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

import play.api.Logging
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.automatedexportsystemfrontend.connectors.AutomatedExportSystemConnector
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.actions.AesAuthRequestActionBuilder
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.problem.routes as problemRoute
import uk.gov.hmrc.automatedexportsystemfrontend.views.html.amend.AmendSubmissionConfirmationView
import uk.gov.hmrc.http.UpstreamErrorResponse
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import javax.inject.Inject
import scala.concurrent.ExecutionContext
import scala.util.control.NonFatal

class AmendSubmissionConfirmationController @Inject() (
  override val messagesApi: MessagesApi,
  val controllerComponents: MessagesControllerComponents,
  view: AmendSubmissionConfirmationView,
  automatedExportSystemConnector: AutomatedExportSystemConnector,
  actionBuilder: AesAuthRequestActionBuilder
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport with Logging {

  def onPageLoad(submissionId: String): Action[AnyContent] =
    actionBuilder.async { implicit request =>
      automatedExportSystemConnector
        .getSingleSubmission(submissionId)
        .map { submission =>
          Ok(view(submission.exportOperation.mrn))
        }
        .recover {
          case UpstreamErrorResponse(_, NOT_FOUND, _, _) =>
            logger.warn(s"No submission found for submission ID $submissionId")
            NotFound("Not Found")

          case NonFatal(ex) =>
            logger.error(s"Failed to retrieve amendment confirmation for submission ID $submissionId", ex)
            Redirect(problemRoute.JourneyRecoveryController.onPageLoad())
        }
    }
}
