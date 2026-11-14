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
import uk.gov.hmrc.automatedexportsystemfrontend.forms.create.RemoveTransportEquipmentFormProvider
import uk.gov.hmrc.automatedexportsystemfrontend.models.{Mode, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.pages.create.DiscrepancyTransportPage
import uk.gov.hmrc.automatedexportsystemfrontend.queries.DiscrepancyTransportEquipment
import uk.gov.hmrc.automatedexportsystemfrontend.repositories.SessionRepository
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.addAnother.create.AddAnotherTransportEquipmentViewModel
import uk.gov.hmrc.automatedexportsystemfrontend.views.html.create.RemoveTransportEquipmentView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class RemoveTransportEquipmentController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  val actionBuilder: AesAuthRequestActionBuilder,
  getData: AesDataRetrievalAction,
  requireData: AesDataRequiredAction,
  formProvider: RemoveTransportEquipmentFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: RemoveTransportEquipmentView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport {

  def form(transportEquipmentIndex: String): Form[Boolean] = formProvider(transportEquipmentIndex)

  def onPageLoad(transportEquipmentIndex: Int, returnPage: Int, mode: Mode): Action[AnyContent] =
    (actionBuilder andThen getData andThen requireData) { implicit request =>
      val addAnotherTransportEquipmentViewModel: AddAnotherTransportEquipmentViewModel =
        AddAnotherTransportEquipmentViewModel(request.userAnswers, Some(returnPage), mode)

      if (
        DiscrepancyTransportEquipment
          .exists(request.userAnswers, transportEquipmentIndex) && validReturnPage(addAnotherTransportEquipmentViewModel, returnPage)
      ) {
        val containerId = request.userAnswers
          .get(DiscrepancyTransportPage(transportEquipmentIndex))
          .flatMap(_.containerId)
          .getOrElse(f"$transportEquipmentIndex%,d")

        Ok(view(form(containerId), containerId, transportEquipmentIndex, returnPage, mode))
      } else {
        Redirect(problemRoute.JourneyRecoveryController.onPageLoad())
      }
    }

  def onSubmit(transportEquipmentIndex: Int, returnPage: Int, mode: Mode): Action[AnyContent] =
    (actionBuilder andThen getData andThen requireData).async { implicit request =>
      def addAnotherTransportEquipmentViewModel(userAnswers: UserAnswers, returnPage: Int, mode: Mode): AddAnotherTransportEquipmentViewModel =
        AddAnotherTransportEquipmentViewModel(userAnswers, Some(returnPage), mode)

      if (
        DiscrepancyTransportEquipment.exists(request.userAnswers, transportEquipmentIndex) && validReturnPage(
          addAnotherTransportEquipmentViewModel(request.userAnswers, returnPage, mode),
          returnPage
        )
      ) {
        val containerId = request.userAnswers
          .get(DiscrepancyTransportPage(transportEquipmentIndex))
          .flatMap(_.containerId)
          .getOrElse(transportEquipmentIndex.toString)

        form(containerId)
          .bindFromRequest()
          .fold(
            formWithErrors => Future.successful(BadRequest(view(formWithErrors, containerId, transportEquipmentIndex, returnPage, mode))),
            {
              case true =>
                for {
                  updatedAnswers <- Future.fromTry(DiscrepancyTransportEquipment.removeOne(request.userAnswers, transportEquipmentIndex))
                  _ <- sessionRepository.set(updatedAnswers)
                } yield
                  if (DiscrepancyTransportEquipment.count(updatedAnswers) == 0) {
                    Redirect(createRoute.DiscrepancyTransportController.onPageLoad(1, mode))
                  } else {
                    Redirect(
                      createRoute.AddAnotherTransportController.onPageLoad(
                        Some(returnPageAfterRemoval(addAnotherTransportEquipmentViewModel(updatedAnswers, returnPage, mode), returnPage)),
                        mode
                      )
                    )
                  }
              case false => Future.successful(Redirect(createRoute.AddAnotherTransportController.onPageLoad(Some(returnPage), mode)))
            }
          )
      } else {
        Future.successful(Redirect(problemRoute.JourneyRecoveryController.onPageLoad()))
      }
    }

  private def validReturnPage(viewModel: AddAnotherTransportEquipmentViewModel, returnPage: Int): Boolean =
    returnPage >= 1 && returnPage <= viewModel.totalPages

  private def returnPageAfterRemoval(viewModel: AddAnotherTransportEquipmentViewModel, returnPage: Int): Int =
    math.min(returnPage, viewModel.totalPages)
}
