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
import uk.gov.hmrc.automatedexportsystemfrontend.forms.create.RemoveGoodsItemFormProvider
import uk.gov.hmrc.automatedexportsystemfrontend.models.{Mode, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.queries.DiscrepancyGoods
import uk.gov.hmrc.automatedexportsystemfrontend.repositories.SessionRepository
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.addAnother.create.AddAnotherGoodsItemViewModel
import uk.gov.hmrc.automatedexportsystemfrontend.views.html.create.RemoveGoodsItemView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class RemoveGoodsItemController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  val actionBuilder: AesAuthRequestActionBuilder,
  getData: AesDataRetrievalAction,
  requireData: AesDataRequiredAction,
  formProvider: RemoveGoodsItemFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: RemoveGoodsItemView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport {

  def form(goodsItemIndex: Int): Form[Boolean] = formProvider(goodsItemIndex)

  def onPageLoad(goodsItemIndex: Int, returnPage: Int, mode: Mode): Action[AnyContent] =
    (actionBuilder andThen getData andThen requireData) { implicit request =>
      val addAnotherGoodsItemViewModel: AddAnotherGoodsItemViewModel =
        AddAnotherGoodsItemViewModel(request.userAnswers, Some(returnPage), mode)

      if (DiscrepancyGoods.exists(request.userAnswers, goodsItemIndex) && validReturnPage(addAnotherGoodsItemViewModel, returnPage)) {
        Ok(view(form(goodsItemIndex), goodsItemIndex, returnPage, mode))
      } else {
        Redirect(problemRoute.JourneyRecoveryController.onPageLoad())
      }
    }

  def onSubmit(goodsItemIndex: Int, returnPage: Int, mode: Mode): Action[AnyContent] =
    (actionBuilder andThen getData andThen requireData).async { implicit request =>
      def addAnotherGoodsItemViewModel(userAnswers: UserAnswers, returnPage: Int, mode: Mode): AddAnotherGoodsItemViewModel =
        AddAnotherGoodsItemViewModel(userAnswers, Some(returnPage), mode)

      if (
        DiscrepancyGoods.exists(request.userAnswers, goodsItemIndex) && validReturnPage(
          addAnotherGoodsItemViewModel(request.userAnswers, returnPage, mode),
          returnPage
        )
      ) {
        form(goodsItemIndex)
          .bindFromRequest()
          .fold(
            formWithErrors => Future.successful(BadRequest(view(formWithErrors, goodsItemIndex, returnPage, mode))),
            {
              case true =>
                for {
                  updatedAnswers <- Future.fromTry(DiscrepancyGoods.removeOne(request.userAnswers, goodsItemIndex))
                  _ <- sessionRepository.set(updatedAnswers)
                } yield
                  if (DiscrepancyGoods.count(updatedAnswers) == 0) {
                    Redirect(createRoute.DiscrepancyGoodsController.onPageLoad(1, mode))
                  } else {
                    Redirect(
                      createRoute.AddAnotherGoodsItemController
                        .onPageLoad(Some(returnPageAfterRemoval(addAnotherGoodsItemViewModel(updatedAnswers, returnPage, mode), returnPage)), mode)
                    )
                  }
              case false => Future.successful(Redirect(createRoute.AddAnotherGoodsItemController.onPageLoad(Some(returnPage), mode)))
            }
          )
      } else {
        Future.successful(Redirect(problemRoute.JourneyRecoveryController.onPageLoad()))
      }
    }

  private def validReturnPage(viewModel: AddAnotherGoodsItemViewModel, returnPage: Int): Boolean =
    returnPage >= 1 && returnPage <= viewModel.totalPages

  private def returnPageAfterRemoval(viewModel: AddAnotherGoodsItemViewModel, returnPage: Int): Int =
    math.min(returnPage, viewModel.totalPages)
}
