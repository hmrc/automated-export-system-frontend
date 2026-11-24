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
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.actions.{AesAuthRequestActionBuilder, AesDataRequiredAction, AesDataRetrievalAction}
import uk.gov.hmrc.automatedexportsystemfrontend.forms.create.DiscrepancyConsignmentFormProvider
import uk.gov.hmrc.automatedexportsystemfrontend.models.Mode
import uk.gov.hmrc.automatedexportsystemfrontend.navigation.CreateNavigator
import uk.gov.hmrc.automatedexportsystemfrontend.pages.create.DiscrepancyConsignmentPage
import uk.gov.hmrc.automatedexportsystemfrontend.repositories.SessionRepository
import uk.gov.hmrc.automatedexportsystemfrontend.services.CodeListService
import uk.gov.hmrc.automatedexportsystemfrontend.views.html.create.DiscrepancyConsignmentView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DiscrepancyConsignmentController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  createNavigator: CreateNavigator,
  val actionBuilder: AesAuthRequestActionBuilder,
  getData: AesDataRetrievalAction,
  requireData: AesDataRequiredAction,
  formProvider: DiscrepancyConsignmentFormProvider,
  codeListService: CodeListService,
  val controllerComponents: MessagesControllerComponents,
  view: DiscrepancyConsignmentView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport {

  def onPageLoad(mode: Mode): Action[AnyContent] =
    (actionBuilder andThen getData andThen requireData).async { implicit request =>
      codeListService.getTransportModes().map { entries =>
        val form = formProvider.forCodeList(entries)
        val preparedForm =
          request.userAnswers.get(DiscrepancyConsignmentPage).fold(form)(form.fill)

        val options = CodeListService.selectItems(entries, messagesApi.preferred(request).apply("discrepancyConsignment.placeholder"))

        Ok(view(preparedForm, mode, options))
      }
    }

  def onSubmit(mode: Mode): Action[AnyContent] =
    (actionBuilder andThen getData andThen requireData).async { implicit request =>
      codeListService.getTransportModes().flatMap { entries =>
        val options = CodeListService.selectItems(entries, messagesApi.preferred(request).apply("discrepancyConsignment.placeholder"))

        formProvider
          .forCodeList(entries)
          .bindFromRequest()
          .fold(
            formWithErrors => Future.successful(BadRequest(view(formWithErrors, mode, options))),
            code =>
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.set(DiscrepancyConsignmentPage, code))
                _ <- sessionRepository.set(updatedAnswers)
              } yield Redirect(createNavigator.nextPage(DiscrepancyConsignmentPage, mode, updatedAnswers))
          )
      }
    }
}
