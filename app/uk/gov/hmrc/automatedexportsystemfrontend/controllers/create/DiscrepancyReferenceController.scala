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
import uk.gov.hmrc.automatedexportsystemfrontend.forms.create.DiscrepancyReferenceFormProvider
import uk.gov.hmrc.automatedexportsystemfrontend.models.{Mode, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.navigation.CreateNavigator
import uk.gov.hmrc.automatedexportsystemfrontend.pages.create.DiscrepancyReferencePage
import uk.gov.hmrc.automatedexportsystemfrontend.queries.DiscrepancyReference
import uk.gov.hmrc.automatedexportsystemfrontend.repositories.SessionRepository
import uk.gov.hmrc.automatedexportsystemfrontend.views.html.create.DiscrepancyReferenceView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DiscrepancyReferenceController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  createNavigator: CreateNavigator,
  val actionBuilder: AesAuthRequestActionBuilder,
  getData: AesDataRetrievalAction,
  requireData: AesDataRequiredAction,
  formProvider: DiscrepancyReferenceFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: DiscrepancyReferenceView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport {

  val form: Form[Option[String]] = formProvider()

  def onPageLoad(discrepancyReferenceIndex: Int, mode: Mode): Action[AnyContent] = (actionBuilder andThen getData andThen requireData) {
    implicit request =>
      val userAnswers: UserAnswers = request.userAnswers
      val nextIndex: Int = DiscrepancyReference.nextIndex(userAnswers)

      if (
        DiscrepancyReference
          .exists(userAnswers, discrepancyReferenceIndex) || discrepancyReferenceIndex == nextIndex
      ) {
        val preparedForm = request.userAnswers.get(DiscrepancyReferencePage(discrepancyReferenceIndex)) match {
          case None        => form
          case Some(value) => form.fill(Some(value))
        }

        Ok(view(preparedForm, discrepancyReferenceIndex, mode))
      } else {
        Redirect(problemRoute.JourneyRecoveryController.onPageLoad())
      }
  }

  def onSubmit(discrepancyReferenceIndex: Int, mode: Mode): Action[AnyContent] = (actionBuilder andThen getData andThen requireData).async {
    implicit request =>
      val userAnswers: UserAnswers = request.userAnswers
      val nextIndex: Int = DiscrepancyReference.nextIndex(userAnswers)

      if (
        DiscrepancyReference
          .exists(userAnswers, discrepancyReferenceIndex) || discrepancyReferenceIndex == nextIndex
      ) {
        form
          .bindFromRequest()
          .fold(
            formWithErrors => Future.successful(BadRequest(view(formWithErrors, discrepancyReferenceIndex, mode))),
            value =>
              for {
                updatedAnswers <- value match {
                  case Some(discrepancyReference) =>
                    Future.fromTry(request.userAnswers.set(DiscrepancyReferencePage(discrepancyReferenceIndex), discrepancyReference))
                  case None =>
                    Future.fromTry(request.userAnswers.remove(DiscrepancyReferencePage(discrepancyReferenceIndex)))
                }
                _ <- sessionRepository.set(updatedAnswers)
              } yield Redirect(createNavigator.nextPage(DiscrepancyReferencePage(discrepancyReferenceIndex), mode, updatedAnswers))
          )
      } else {
        Future.successful(Redirect(problemRoute.JourneyRecoveryController.onPageLoad()))
      }
  }
}
