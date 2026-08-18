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

import uk.gov.hmrc.automatedexportsystemfrontend.controllers.actions.*
import uk.gov.hmrc.automatedexportsystemfrontend.models.{Mode, UserAnswers}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.automatedexportsystemfrontend.forms.create.EnterMrnFormProvider
import uk.gov.hmrc.automatedexportsystemfrontend.navigation.CreateNavigator
import uk.gov.hmrc.automatedexportsystemfrontend.pages.create.EnterMrnPage
import uk.gov.hmrc.automatedexportsystemfrontend.repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.automatedexportsystemfrontend.views.html.create.EnterMrnView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class EnterMrnController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  createNavigator: CreateNavigator,
  val actionBuilder: AesAuthRequestActionBuilder,
  getData: AesDataRetrievalAction,
  requireData: AesDataRequiredAction,
  formProvider: EnterMrnFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: EnterMrnView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (actionBuilder andThen getData).async { implicit request =>

    val answers = request.userAnswers.getOrElse(UserAnswers(request.sessionId)) // TO BE REPLACED WITH

    val preparedForm = answers.get(EnterMrnPage).fold(form)(form.fill)

    val preparedView: HtmlFormat.Appendable = view(preparedForm, mode)
    Future.successful(Ok(preparedView))
  }

  // TO BE REPLACED WITH
  // def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
  def onSubmit(mode: Mode): Action[AnyContent] = (actionBuilder andThen getData).async { implicit request =>

    val answers = request.userAnswers.getOrElse(UserAnswers(request.sessionId)) // TO BE REMOVED

    form
      .bindFromRequest()
      .fold(
        formWithErrors => {
          val errorPage: play.twirl.api.HtmlFormat.Appendable = view(formWithErrors, mode)
          Future.successful(BadRequest(errorPage))
        },
        value =>
          for {
            updatedAnswers <- Future.fromTry(answers.set(EnterMrnPage, value))
            // updatedAnswers <- Future.fromTry(request.userAnswers.set(EnterDucrPage, value))
            _ <- sessionRepository.set(updatedAnswers)
          } yield Redirect(createNavigator.nextPage(EnterMrnPage, mode, updatedAnswers))
      )
  }
}
