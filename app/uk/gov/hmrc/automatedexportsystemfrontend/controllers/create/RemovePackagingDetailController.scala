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
import uk.gov.hmrc.automatedexportsystemfrontend.forms.create.RemovePackagingDetailFormProvider
import uk.gov.hmrc.automatedexportsystemfrontend.models.{Mode, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.queries.DiscrepancyPacking
import uk.gov.hmrc.automatedexportsystemfrontend.repositories.SessionRepository
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.addAnother.create.AddAnotherPackagingDetailViewModel
import uk.gov.hmrc.automatedexportsystemfrontend.views.html.create.RemovePackagingDetailView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class RemovePackagingDetailController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  val actionBuilder: AesAuthRequestActionBuilder,
  getData: AesDataRetrievalAction,
  requireData: AesDataRequiredAction,
  formProvider: RemovePackagingDetailFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: RemovePackagingDetailView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport {

  def form(packagingDetailIndex: Int): Form[Boolean] = formProvider(packagingDetailIndex)

  def onPageLoad(packagingDetailIndex: Int, returnPage: Int, mode: Mode): Action[AnyContent] =
    (actionBuilder andThen getData andThen requireData) { implicit request =>
      if (DiscrepancyPacking.exists(request.userAnswers, packagingDetailIndex) && validReturnPage(request.userAnswers, returnPage)) {
        Ok(view(form(packagingDetailIndex), packagingDetailIndex, returnPageAfterRemoval(request.userAnswers, returnPage), mode))
      } else {
        Redirect(problemRoute.JourneyRecoveryController.onPageLoad())
      }
    }

  def onSubmit(packagingDetailIndex: Int, returnPage: Int, mode: Mode): Action[AnyContent] =
    (actionBuilder andThen getData andThen requireData).async { implicit request =>
      if (DiscrepancyPacking.exists(request.userAnswers, packagingDetailIndex)) {
        form(packagingDetailIndex)
          .bindFromRequest()
          .fold(
            formWithErrors => Future.successful(BadRequest(view(formWithErrors, packagingDetailIndex, returnPage, mode))),
            {
              case true =>
                for {
                  updatedAnswers <- Future.fromTry(DiscrepancyPacking.removeOne(request.userAnswers, packagingDetailIndex))
                  _ <- sessionRepository.set(updatedAnswers)
                } yield
                  if (DiscrepancyPacking.count(updatedAnswers) == 0) {
                    Redirect(createRoute.DiscrepancyPackingController.onPageLoad(1, mode))
                  } else {
                    Redirect(
                      createRoute.AddAnotherPackagingDetailController.onPageLoad(Some(returnPageAfterRemoval(updatedAnswers, returnPage)), mode)
                    )
                  }
              case false => Future.successful(Redirect(createRoute.AddAnotherPackagingDetailController.onPageLoad(Some(returnPage), mode)))
            }
          )
      } else {
        Future.successful(Redirect(problemRoute.JourneyRecoveryController.onPageLoad()))
      }
    }

  private def validReturnPage(userAnswers: UserAnswers, returnPage: Int): Boolean = {
    val addAnotherPackagingDetailViewModel =
      AddAnotherPackagingDetailViewModel(userAnswers, Some(returnPage))

    returnPage >= 1 && returnPage <= addAnotherPackagingDetailViewModel.totalPages
  }

  private def returnPageAfterRemoval(updatedUserAnswers: UserAnswers, returnPage: Int): Int = {
    val addAnotherPackagingDetailViewModel =
      AddAnotherPackagingDetailViewModel(updatedUserAnswers, Some(returnPage))

    math.min(returnPage, addAnotherPackagingDetailViewModel.totalPages)
  }
}
