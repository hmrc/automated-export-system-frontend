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
import uk.gov.hmrc.automatedexportsystemfrontend.forms.create.DiscrepancySealsFormProvider
import uk.gov.hmrc.automatedexportsystemfrontend.models.Mode
import uk.gov.hmrc.automatedexportsystemfrontend.navigation.CreateNavigator
import uk.gov.hmrc.automatedexportsystemfrontend.pages.create.DiscrepancySealsPage
import uk.gov.hmrc.automatedexportsystemfrontend.repositories.SessionRepository
import uk.gov.hmrc.automatedexportsystemfrontend.views.html.create.DiscrepancySealsView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DiscrepancySealsController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  createNavigator: CreateNavigator,
  val actionBuilder: AesAuthRequestActionBuilder,
  getData: AesDataRetrievalAction,
  requireData: AesDataRequiredAction,
  formProvider: DiscrepancySealsFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: DiscrepancySealsView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport {

  val form: Form[Option[String]] = formProvider()

  def onPageLoad(sealsIndex: Int, mode: Mode): Action[AnyContent] = (actionBuilder andThen getData andThen requireData) { implicit request =>

    val preparedForm = request.userAnswers.get(DiscrepancySealsPage(sealsIndex)) match {
      case None        => form
      case Some(value) => form.fill(Some(value))
    }

    Ok(view(preparedForm, sealsIndex, mode))
  }

  def onSubmit(sealsIndex: Int, mode: Mode): Action[AnyContent] = (actionBuilder andThen getData andThen requireData).async { implicit request =>
    form
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(view(formWithErrors, sealsIndex, mode))),
        value =>
          for {
            updatedAnswers <- value match {
              case Some(sealIdentifier) =>
                Future
                  .fromTry(request.userAnswers.set(DiscrepancySealsPage(sealsIndex), sealIdentifier))
              case None =>
                Future.fromTry(
                  request.userAnswers.remove(DiscrepancySealsPage(sealsIndex))
                ) // TODO: discuss with BA if this is okay, also discuss the implications on check route
            }
            _ <- sessionRepository.set(updatedAnswers)
          } yield Redirect(createNavigator.nextPage(DiscrepancySealsPage(sealsIndex), mode, updatedAnswers))
      )
  }
}
