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

import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.actions.*
import uk.gov.hmrc.automatedexportsystemfrontend.forms.create.OfficeOfExitFormProvider
import uk.gov.hmrc.automatedexportsystemfrontend.models.{Mode, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.navigation.HappyPathNavigator
import uk.gov.hmrc.automatedexportsystemfrontend.pages.create.OfficeOfExitPage
import uk.gov.hmrc.automatedexportsystemfrontend.repositories.SessionRepository
import uk.gov.hmrc.automatedexportsystemfrontend.views.html.create.OfficeOfExitView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class OfficeOfExitController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  happyPathNavigator: HappyPathNavigator,
  val actionBuilder: AesAuthRequestActionBuilder,
  getData: AesDataRetrievalAction,
  requireData: AesDataRequiredAction,
  formProvider: OfficeOfExitFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: OfficeOfExitView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport {

  val form = formProvider()

//  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData) {
  def onPageLoad(mode: Mode): Action[AnyContent] = (actionBuilder andThen getData).async { implicit request =>

    val answers = request.userAnswers.getOrElse(UserAnswers(request.sessionId)) // TO BE REMOVED

    //      val preparedForm = request.userAnswers.get(OfficeOfExitPage) match {
    val preparedForm = answers.get(OfficeOfExitPage).fold(form)(form.fill)

    val preparedView: HtmlFormat.Appendable = view(preparedForm, mode)
    Future.successful(Ok(preparedView))
  }

//  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
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
//            updatedAnswers <- Future.fromTry(request.userAnswers.set(OfficeOfExitPage, value))
            updatedAnswers <- Future.fromTry(answers.set(OfficeOfExitPage, value))
            _ <- sessionRepository.set(updatedAnswers)
          } yield Redirect(happyPathNavigator.nextPage(OfficeOfExitPage, mode, updatedAnswers))
      )
  }
}
