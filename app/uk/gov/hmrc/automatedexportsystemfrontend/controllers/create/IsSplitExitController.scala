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
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.actions.*
import uk.gov.hmrc.automatedexportsystemfrontend.models.{Mode, UserAnswers}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.automatedexportsystemfrontend.forms.create.IsSplitExitFormProvider
import uk.gov.hmrc.automatedexportsystemfrontend.navigation.CreateNavigator
import uk.gov.hmrc.automatedexportsystemfrontend.pages.create.IsSplitExitPage
import uk.gov.hmrc.automatedexportsystemfrontend.repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.automatedexportsystemfrontend.views.html.create.IsSplitExitView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class IsSplitExitController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  createNavigator: CreateNavigator,
  val actionBuilder: AesAuthRequestActionBuilder,
  getData: AesDataRetrievalAction,
  requireData: AesDataRequiredAction,
  formProvider: IsSplitExitFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: IsSplitExitView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport {

  val form: Form[Boolean] = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (actionBuilder andThen getData).async { implicit request =>
    val answers = request.userAnswers.getOrElse(UserAnswers(request.sessionId))
    val preparedForm = answers.get(IsSplitExitPage).fold(form)(form.fill)
    Future.successful(Ok(view(preparedForm, mode)))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (actionBuilder andThen getData andThen requireData).async { implicit request =>
    form
      .bindFromRequest()
      .fold(
        formWithErrors => {
          val errorPage: play.twirl.api.HtmlFormat.Appendable = view(formWithErrors, mode)
          Future.successful(BadRequest(errorPage))
        },
        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(IsSplitExitPage, value))
            _ <- sessionRepository.set(updatedAnswers)
          } yield Redirect(createNavigator.nextPage(IsSplitExitPage, mode, updatedAnswers))
      )
  }
}
