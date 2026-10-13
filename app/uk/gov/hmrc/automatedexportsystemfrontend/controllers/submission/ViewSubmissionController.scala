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
import uk.gov.hmrc.automatedexportsystemfrontend.models.{SubmissionSummaryResponseList, SubmissionViewModelMapper, ViewSubmissionViewModelMapper}
import uk.gov.hmrc.automatedexportsystemfrontend.repositories.SessionRepository
import uk.gov.hmrc.automatedexportsystemfrontend.utils.AmendmentAnswersMapper
import uk.gov.hmrc.automatedexportsystemfrontend.views.html.submission.ViewSubmissionView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.http.UpstreamErrorResponse

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ViewSubmissionController @Inject() (
  override val messagesApi: MessagesApi,
  override val controllerComponents: MessagesControllerComponents,
  view: ViewSubmissionView,
  automatedExportSystemConnector: AutomatedExportSystemConnector,
  answerMapper: AmendmentAnswersMapper,
  sessionRepository: SessionRepository,
  actionBuilder: AesAuthRequestActionBuilder
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport with Logging {

  def onPageLoad(submissionId: String): Action[AnyContent] =
    actionBuilder.async { implicit request =>
      (for {
        response <- automatedExportSystemConnector.getSingleSubmission(submissionId)
        answers <- Future.fromTry(answerMapper.toUserAnswers(submissionId, response))
        viewModel = ViewSubmissionViewModelMapper.toViewModel(response)
        _ <- sessionRepository.set(answers)
      } yield Ok(view(viewModel))).recover { case UpstreamErrorResponse(_, NOT_FOUND, _, _) =>
        logger.warn(s"No submission found for submission Id $submissionId")
        NotFound("Not Found")
      }
    }
}
