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
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.actions.{AesAuthRequestActionBuilder, AesDataRequiredAction, AesDataRetrievalAction}
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.create.routes as createRoute
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.problem.routes as problemRoute
import uk.gov.hmrc.automatedexportsystemfrontend.forms.create.RemoveSealFormProvider
import uk.gov.hmrc.automatedexportsystemfrontend.models.{Mode, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.queries.DiscrepancySeals
import uk.gov.hmrc.automatedexportsystemfrontend.repositories.SessionRepository
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.addAnother.create.AddAnotherSealViewModel
import uk.gov.hmrc.automatedexportsystemfrontend.views.html.create.RemoveSealView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class RemoveSealController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  val actionBuilder: AesAuthRequestActionBuilder,
  getData: AesDataRetrievalAction,
  requireData: AesDataRequiredAction,
  formProvider: RemoveSealFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: RemoveSealView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport {

  val form: Form[Boolean] = formProvider()

  def onPageLoad(sealIndex: Int, returnPage: Int, mode: Mode): Action[AnyContent] = (actionBuilder andThen getData andThen requireData) {
    implicit request =>
      if (DiscrepancySeals.exists(request.userAnswers, sealIndex) && validReturnPage(request.userAnswers, returnPage, mode)) {
        Ok(view(form, sealIndex, returnPage, mode))
      } else {
        Redirect(problemRoute.JourneyRecoveryController.onPageLoad())
      }
  }

  def onSubmit(sealIndex: Int, returnPage: Int, mode: Mode): Action[AnyContent] = (actionBuilder andThen getData andThen requireData).async {
    implicit request =>
      if (DiscrepancySeals.exists(request.userAnswers, sealIndex) && validReturnPage(request.userAnswers, returnPage, mode)) {
        form
          .bindFromRequest()
          .fold(
            formWithErrors => Future.successful(BadRequest(view(formWithErrors, sealIndex, returnPage, mode))),
            {
              case true =>
                for {
                  updatedAnswers <- Future.fromTry(DiscrepancySeals.removeOne(request.userAnswers, sealIndex))
                  _ <- sessionRepository.set(updatedAnswers)
                } yield
                  if (DiscrepancySeals.count(updatedAnswers) == 0) {
                    Redirect(createRoute.DiscrepancySealsController.onPageLoad(1, mode))
                  } else {
                    Redirect(createRoute.AddAnotherSealController.onPageLoad(Some(returnPageAfterRemoval(updatedAnswers, returnPage, mode)), mode))
                  }
              case false => Future.successful(Redirect(createRoute.AddAnotherSealController.onPageLoad(Some(returnPage), mode)))
            }
          )
      } else {
        Future.successful(Redirect(problemRoute.JourneyRecoveryController.onPageLoad()))
      }
  }

  private def validReturnPage(userAnswers: UserAnswers, returnPage: Int, mode: Mode)(implicit messages: Messages): Boolean = {
    val addAnotherSealViewModel = AddAnotherSealViewModel(userAnswers, Some(returnPage), mode)

    returnPage >= 1 && returnPage <= addAnotherSealViewModel.totalPages
  }

  private def returnPageAfterRemoval(updatedUserAnswers: UserAnswers, returnPage: Int, mode: Mode)(implicit messages: Messages): Int = {
    val addAnotherSealViewModel =
      AddAnotherSealViewModel(updatedUserAnswers, Some(returnPage), mode)

    math.min(returnPage, addAnotherSealViewModel.totalPages)
  }

}
