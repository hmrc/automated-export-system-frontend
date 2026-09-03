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

import play.api.Logging
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import uk.gov.hmrc.automatedexportsystemfrontend.connectors.AutomatedExportSystemConnector
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.actions.AesAuthRequestActionBuilder
import uk.gov.hmrc.automatedexportsystemfrontend.models.{SubmissionResponseList, SubmissionViewModelMapper}
import uk.gov.hmrc.automatedexportsystemfrontend.views.html.submission.CancelSubmissionView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class CancelSubmissionController @Inject() (
  override val messagesApi: MessagesApi,
  val actionBuilder: AesAuthRequestActionBuilder,
  override val controllerComponents: MessagesControllerComponents,
  view: CancelSubmissionView,
  automatedExportSystemConnector: AutomatedExportSystemConnector
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport with Logging {

  def onPageLoad(submissionId: String): Action[AnyContent] =
    actionBuilder.async { implicit request =>
      automatedExportSystemConnector
        .getSubmissions()
        .map[Result] { response =>
          response.submissions
            .find(_.submissionId.toString == submissionId)
            .map { submission =>
              val summary =
                SubmissionViewModelMapper
                  .toViewModel(SubmissionResponseList(Seq(submission)))
                  .summaries
                  .head

              Ok(view(summary))
            }
            .getOrElse {
              logger.warn(s"No submission found for submission ID $submissionId")
              NotFound("Not Found")
            }
        }
    }

  def onSubmit(submissionID: String): Action[AnyContent] =
    actionBuilder.async { implicit request =>
      automatedExportSystemConnector
        .cancelSubmission(submissionID)
        .map { _ =>
          Redirect(routes.CancellationSuccessController.onPageLoad(submissionID))
        }
        .recover { case exception =>
          logger.error(s"Failed to cancel submission $submissionID", exception)

          Redirect(
            uk.gov.hmrc.automatedexportsystemfrontend.controllers.problem.routes.JourneyRecoveryController
              .onPageLoad()
          )
        }
    }
}
