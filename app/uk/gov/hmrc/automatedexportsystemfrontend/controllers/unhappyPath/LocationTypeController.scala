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

package uk.gov.hmrc.automatedexportsystemfrontend.controllers.unhappyPath

import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.actions.{AesAuthRequestActionBuilder, AesDataRequiredAction, AesDataRetrievalAction}
import uk.gov.hmrc.automatedexportsystemfrontend.forms.unhappyPath.LocationTypeFormProvider
import uk.gov.hmrc.automatedexportsystemfrontend.models.{LocationType, Mode}
import uk.gov.hmrc.automatedexportsystemfrontend.navigation.UnhappyPathNavigator
import uk.gov.hmrc.automatedexportsystemfrontend.pages.unhappyPath.LocationTypePage
import uk.gov.hmrc.automatedexportsystemfrontend.repositories.SessionRepository
import uk.gov.hmrc.automatedexportsystemfrontend.views.html.unhappyPath.LocationTypeView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class LocationTypeController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  unhappyPathNavigator: UnhappyPathNavigator,
  val actionBuilder: AesAuthRequestActionBuilder,
  getData: AesDataRetrievalAction,
  requireData: AesDataRequiredAction,
  formProvider: LocationTypeFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: LocationTypeView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport {

  val form: Form[LocationType] = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (actionBuilder andThen getData andThen requireData) { implicit request =>

    val preparedForm = request.userAnswers.get(LocationTypePage) match {
      case None        => form
      case Some(value) => form.fill(value)
    }

    Ok(view(preparedForm, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (actionBuilder andThen getData andThen requireData).async { implicit request =>
    form
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(view(formWithErrors, mode))),
        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(LocationTypePage, value))
            _ <- sessionRepository.set(updatedAnswers)
          } yield Redirect(unhappyPathNavigator.nextPage(LocationTypePage, mode, updatedAnswers))
      )
  }
}
