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

import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.actions.{AesAuthRequestActionBuilder, AesDataRequiredAction, AesDataRetrievalAction}
import uk.gov.hmrc.automatedexportsystemfrontend.services.IE507a.SubmissionDataService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.automatedexportsystemfrontend.connectors.AutomatedExportSystemConnector

import javax.inject.Inject

class SubmissionController @Inject() (
  override val messagesApi: MessagesApi,
  val actionBuilder: AesAuthRequestActionBuilder,
  getData: AesDataRetrievalAction,
  requireData: AesDataRequiredAction,
  automatedExportSystemConnector: AutomatedExportSystemConnector,
  submissionDataService: SubmissionDataService,
  val controllerComponents: MessagesControllerComponents,
  logger: Logger
) extends FrontendBaseController with I18nSupport {

  def standardSubmit: Action[AnyContent] = (actionBuilder andThen getData andThen requireData) { implicit request =>
    submissionDataService.buildStandardSubmission(request.userAnswers) match {
      case Some(xmlSubmission) =>
        automatedExportSystemConnector.submitIE507a(xmlSubmission)
        Redirect(uk.gov.hmrc.automatedexportsystemfrontend.controllers.submission.routes.StandardSubmissionConfirmationController.onPageLoad().url)
      case None =>
        logger.error("Failed to convert Submission to XML")
        // clean all user answers
        Redirect(uk.gov.hmrc.automatedexportsystemfrontend.controllers.problem.routes.JourneyRecoveryController.onPageLoad().url)
    }

  }
}
