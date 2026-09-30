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
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.create.routes as createRoute
import uk.gov.hmrc.automatedexportsystemfrontend.forms.create.AddAnotherPackagingDetailFormProvider
import uk.gov.hmrc.automatedexportsystemfrontend.models.NormalMode
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.addAnother.create.AddAnotherPackagingDetailViewModel
import uk.gov.hmrc.automatedexportsystemfrontend.views.html.create.AddAnotherPackagingDetailView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import javax.inject.Inject

class AddAnotherPackagingDetailController @Inject() (
  override val messagesApi: MessagesApi,
  val actionBuilder: AesAuthRequestActionBuilder,
  formProvider: AddAnotherPackagingDetailFormProvider,
  getData: AesDataRetrievalAction,
  requireData: AesDataRequiredAction,
  val controllerComponents: MessagesControllerComponents,
  view: AddAnotherPackagingDetailView
) extends FrontendBaseController with I18nSupport {

  def form(allowMore: Boolean): Form[Boolean] = formProvider(allowMore)

  def onPageLoad(): Action[AnyContent] = (actionBuilder andThen getData andThen requireData) { implicit request =>
    val viewModel = AddAnotherPackagingDetailViewModel(request.userAnswers)

    if (viewModel.numberOfPackagingDetails > 0) {
      Ok(view(form(viewModel.allowMore), viewModel))
    } else {
      Redirect(createRoute.DiscrepancyPackingController.onPageLoad(1, NormalMode).url)
    }
  }

  def onSubmit(): Action[AnyContent] = (actionBuilder andThen getData andThen requireData) { implicit request =>
    val viewModel = AddAnotherPackagingDetailViewModel(request.userAnswers)

    formProvider(viewModel.allowMore)
      .bindFromRequest()
      .fold(
        formWithErrors => BadRequest(view(formWithErrors, viewModel)),
        {
          case true  => Redirect(createRoute.DiscrepancyPackingController.onPageLoad(viewModel.numberOfPackagingDetails + 1, NormalMode))
          case false => Redirect(createRoute.CYASubmissionController.onPageLoad())
        }
      )
  }
}
