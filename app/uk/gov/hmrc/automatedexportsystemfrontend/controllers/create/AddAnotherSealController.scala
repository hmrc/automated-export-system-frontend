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

import uk.gov.hmrc.automatedexportsystemfrontend.forms.create.AddAnotherSealFormProvider

import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.actions.{AesAuthRequestActionBuilder, AesDataRequiredAction, AesDataRetrievalAction}
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.create.routes as createRoute
import uk.gov.hmrc.automatedexportsystemfrontend.models.Mode
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.addAnother.create.AddAnotherSealViewModel
import uk.gov.hmrc.automatedexportsystemfrontend.views.html.create.AddAnotherSealView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import javax.inject.Inject

class AddAnotherSealController @Inject() (
  override val messagesApi: MessagesApi,
  val actionBuilder: AesAuthRequestActionBuilder,
  formProvider: AddAnotherSealFormProvider,
  getData: AesDataRetrievalAction,
  requireData: AesDataRequiredAction,
  val controllerComponents: MessagesControllerComponents,
  view: AddAnotherSealView
) extends FrontendBaseController with I18nSupport {

  private def form(allowMore: Boolean): Form[Boolean] = formProvider(allowMore)

  def onPageLoad(mode: Mode): Action[AnyContent] = (actionBuilder andThen getData andThen requireData) { implicit request =>
    val viewModel = AddAnotherSealViewModel(request.userAnswers, mode)

    if (viewModel.numberOfSeals > 0) {
      Ok(view(form(viewModel.allowMore), viewModel, mode))
    } else {
      Redirect(createRoute.DiscrepancySealsController.onPageLoad(1, mode).url)
    }
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (actionBuilder andThen getData andThen requireData) { implicit request =>
    val viewModel = AddAnotherSealViewModel(request.userAnswers, mode)
    if (viewModel.numberOfSeals > 0) {
      formProvider(viewModel.allowMore)
        .bindFromRequest()
        .fold(
          formWithErrors => BadRequest(view(formWithErrors, viewModel, mode)),
          {
            case true  => Redirect(createRoute.DiscrepancySealsController.onPageLoad(viewModel.numberOfSeals + 1, mode))
            case false => Redirect(createRoute.DiscrepancyReferenceController.onPageLoad(mode))
          }
        )
    } else {
      Redirect(createRoute.DiscrepancySealsController.onPageLoad(1, mode))
    }
  }
}
