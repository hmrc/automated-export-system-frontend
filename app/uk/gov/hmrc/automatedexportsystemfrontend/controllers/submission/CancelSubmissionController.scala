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

import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.automatedexportsystemfrontend.connectors.AutomatedExportSystemConnector
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.actions.{AesAuthRequestActionBuilder, AesDataRetrievalAction}
import uk.gov.hmrc.automatedexportsystemfrontend.views.html.submission.CancelSubmissionView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import javax.inject.Inject
import scala.concurrent.ExecutionContext

import play.api.Logging

class CancelSubmissionController @Inject() (
  override val messagesApi: MessagesApi,
  val actionBuilder: AesAuthRequestActionBuilder,
  getData: AesDataRetrievalAction,
  val controllerComponents: MessagesControllerComponents,
  view: CancelSubmissionView,
  automatedExportSystemConnector: AutomatedExportSystemConnector
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport with Logging {

  def onPageLoad(submissionID: String): Action[AnyContent] =
    (actionBuilder andThen getData).async { implicit request =>
      automatedExportSystemConnector.getSubmissions().map { response =>
        response.submissions.find(_.submissionId.toString == submissionID) match {

          case Some(submission) =>
            logger.info(s"Found submission ${submission.submissionId}")

            Ok(view(submissionID, submission.mrn))

          case None =>
            logger.warn(s"No submission found for $submissionID")

            NotFound("Submission not found")
        }
      }
    }
}
