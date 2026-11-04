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

package uk.gov.hmrc.automatedexportsystemfrontend.controllers.create

import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.actions.{AesAuthRequestActionBuilder, AesDataRequiredAction, AesDataRetrievalAction}
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.problem.routes as problemRoute
import uk.gov.hmrc.automatedexportsystemfrontend.forms.create.DiscrepancyTransportDocFormProvider
import uk.gov.hmrc.automatedexportsystemfrontend.models.{DocumentDetails, Mode, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.navigation.CreateNavigator
import uk.gov.hmrc.automatedexportsystemfrontend.pages.create.DiscrepancyTransportDocPage
import uk.gov.hmrc.automatedexportsystemfrontend.queries.DiscrepancyTransportDoc
import uk.gov.hmrc.automatedexportsystemfrontend.repositories.SessionRepository
import uk.gov.hmrc.automatedexportsystemfrontend.views.html.create.DiscrepancyTransportDocView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DiscrepancyTransportDocController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  createNavigator: CreateNavigator,
  val actionBuilder: AesAuthRequestActionBuilder,
  getData: AesDataRetrievalAction,
  requireData: AesDataRequiredAction,
  formProvider: DiscrepancyTransportDocFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: DiscrepancyTransportDocView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport {

  val form: Form[DocumentDetails] = formProvider()

  def onPageLoad(docIndex: Int, mode: Mode): Action[AnyContent] = (actionBuilder andThen getData andThen requireData) { implicit request =>

    val userAnswers: UserAnswers = request.userAnswers
    val nextIndex: Int = DiscrepancyTransportDoc.nextIndex(userAnswers)

    if (
      DiscrepancyTransportDoc
        .exists(userAnswers, docIndex) || docIndex == nextIndex
    ) {
      val preparedForm = userAnswers.get(DiscrepancyTransportDocPage(docIndex)) match {
        case None        => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, docIndex, mode))
    } else {
      Redirect(problemRoute.JourneyRecoveryController.onPageLoad())
    }
  }

  def onSubmit(docIndex: Int, mode: Mode): Action[AnyContent] = (actionBuilder andThen getData andThen requireData).async { implicit request =>
    val userAnswers: UserAnswers = request.userAnswers
    val nextIndex: Int = DiscrepancyTransportDoc.nextIndex(userAnswers)

    if (
      DiscrepancyTransportDoc
        .exists(userAnswers, docIndex) || docIndex == nextIndex
    ) {
      form
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, docIndex, mode))),
          value =>
            for {
              updatedAnswers <- value match {
                case document if document.documentType.isDefined || document.referenceNumber.isDefined =>
                  Future
                    .fromTry(request.userAnswers.set(DiscrepancyTransportDocPage(docIndex), value))
                // TODO, see PR note about removal when time to add removal functionality
                case _ =>
                  Future.fromTry(request.userAnswers.remove(DiscrepancyTransportDocPage(docIndex)))
              }
              _ <- sessionRepository.set(updatedAnswers)
            } yield Redirect(createNavigator.nextPage(DiscrepancyTransportDocPage(docIndex), mode, updatedAnswers))
        )
    } else {
      Future.successful(Redirect(problemRoute.JourneyRecoveryController.onPageLoad()))
    }
  }
}
