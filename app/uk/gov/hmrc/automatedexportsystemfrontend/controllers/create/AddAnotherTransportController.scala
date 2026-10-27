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
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.problem.routes as problemRoute
import uk.gov.hmrc.automatedexportsystemfrontend.forms.create.AddAnotherTransportEquipmentFormProvider
import uk.gov.hmrc.automatedexportsystemfrontend.models.Mode
import uk.gov.hmrc.automatedexportsystemfrontend.queries.DiscrepancyTransportEquipment
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.addAnother.create.AddAnotherTransportEquipmentViewModel
import uk.gov.hmrc.automatedexportsystemfrontend.views.html.create.AddAnotherTransportEquipmentView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import javax.inject.Inject

class AddAnotherTransportController @Inject() (
  override val messagesApi: MessagesApi,
  val actionBuilder: AesAuthRequestActionBuilder,
  formProvider: AddAnotherTransportEquipmentFormProvider,
  getData: AesDataRetrievalAction,
  requireData: AesDataRequiredAction,
  val controllerComponents: MessagesControllerComponents,
  view: AddAnotherTransportEquipmentView
) extends FrontendBaseController with I18nSupport {

  def form(allowMore: Boolean): Form[Boolean] =
    formProvider(allowMore)

  def onPageLoad(page: Option[Int], mode: Mode): Action[AnyContent] =
    (actionBuilder andThen getData andThen requireData) { implicit request =>

      val viewModel =
        AddAnotherTransportEquipmentViewModel(request.userAnswers, page)

      if (viewModel.numberOfTransportEquipment == 0) {
        Redirect(
          createRoute.DiscrepancyTransportController
            .onPageLoad(DiscrepancyTransportEquipment.nextIndex(request.userAnswers), mode)
        )
      } else if (!isValidPage(viewModel)) {
        Redirect(problemRoute.JourneyRecoveryController.onPageLoad())
      } else if (page != viewModel.pageParam) {
        Redirect(
          createRoute.AddAnotherTransportController
            .onPageLoad(viewModel.pageParam, mode)
        )
      } else {
        Ok(view(form(viewModel.allowMore), viewModel, mode))
      }
    }

  def onSubmit(page: Option[Int], mode: Mode): Action[AnyContent] =
    (actionBuilder andThen getData andThen requireData) { implicit request =>

      val viewModel =
        AddAnotherTransportEquipmentViewModel(request.userAnswers, page)

      if (viewModel.numberOfTransportEquipment == 0) {
        Redirect(
          createRoute.DiscrepancyTransportController
            .onPageLoad(DiscrepancyTransportEquipment.nextIndex(request.userAnswers), mode)
        )
      } else if (!isValidPage(viewModel)) {
        Redirect(problemRoute.JourneyRecoveryController.onPageLoad())
      } else {
        formProvider(viewModel.allowMore)
          .bindFromRequest()
          .fold(
            formWithErrors => BadRequest(view(formWithErrors, viewModel, mode)),
            {
              case true =>
                Redirect(
                  createRoute.DiscrepancyTransportController
                    .onPageLoad(viewModel.numberOfTransportEquipment + 1, mode)
                )

              case false =>
                Redirect(
                  createRoute.DiscrepancySealsController
                    .onPageLoad(1, mode)
                )
            }
          )
      }
    }

  private def isValidPage(viewModel: AddAnotherTransportEquipmentViewModel): Boolean =
    viewModel.currentPage >= 1 &&
      viewModel.currentPage <= viewModel.totalPages
}
