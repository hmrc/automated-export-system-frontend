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

package controllers.happyPath

import controllers.actions.*
import forms.EnterMrnFormProvider
import models.{Mode, UserAnswers}
import navigation.HappyPathNavigator
import pages.happyPath.EnterMrnPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.EnterMrnView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class EnterMrnController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        sessionRepository: SessionRepository,
                                        happyPathNavigator: HappyPathNavigator,
                                        identify: IdentifierAction,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction,
                                        formProvider: EnterMrnFormProvider,
                                        val controllerComponents: MessagesControllerComponents,
                                        view: EnterMrnView
                                    )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData) {
    implicit request =>

      val answers = request.userAnswers.getOrElse(UserAnswers(request.userId)) //TO BE REPLACED WITH

      val preparedForm =  answers.get(EnterMrnPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode))
  }

  //TO BE REPLACED WITH
  //def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData).async {
    implicit request =>

      val answers = request.userAnswers.getOrElse(UserAnswers(request.userId)) //TO BE REMOVED

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, mode))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(answers.set(EnterMrnPage, value))
            //updatedAnswers <- Future.fromTry(request.userAnswers.set(EnterDucrPage, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(happyPathNavigator.nextPage(EnterMrnPage, mode, updatedAnswers))
      )
  }
}
