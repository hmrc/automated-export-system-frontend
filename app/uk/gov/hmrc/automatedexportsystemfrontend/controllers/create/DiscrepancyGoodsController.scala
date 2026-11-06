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
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.actions.{AesAuthRequestActionBuilder, AesDataRequiredAction, AesDataRetrievalAction}
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.create.routes as createRoute
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.problem.routes as problemRoute
import uk.gov.hmrc.automatedexportsystemfrontend.forms.create.DiscrepancyGoodsFormProvider
import uk.gov.hmrc.automatedexportsystemfrontend.models.{CheckMode, Mode, NormalMode, UserAnswers, WhatHasChangedDetails}
import uk.gov.hmrc.automatedexportsystemfrontend.navigation.CreateNavigator
import uk.gov.hmrc.automatedexportsystemfrontend.pages.create.DiscrepancyGoodsPage
import uk.gov.hmrc.automatedexportsystemfrontend.queries.DiscrepancyGoods
import uk.gov.hmrc.automatedexportsystemfrontend.repositories.SessionRepository
import uk.gov.hmrc.automatedexportsystemfrontend.views.html.create.DiscrepancyGoodsView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DiscrepancyGoodsController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  createNavigator: CreateNavigator,
  val actionBuilder: AesAuthRequestActionBuilder,
  getData: AesDataRetrievalAction,
  requireData: AesDataRequiredAction,
  formProvider: DiscrepancyGoodsFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: DiscrepancyGoodsView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport {

  val form: Form[Option[WhatHasChangedDetails]] = formProvider()

  def onPageLoad(goodsItemIndex: Int, mode: Mode): Action[AnyContent] = (actionBuilder andThen getData andThen requireData) { implicit request =>
    val userAnswers: UserAnswers = request.userAnswers
    val nextIndex: Int = DiscrepancyGoods.nextIndex(userAnswers)
    if (DiscrepancyGoods.exists(userAnswers, goodsItemIndex) || goodsItemIndex == nextIndex) {
      val preparedForm = userAnswers.get(DiscrepancyGoodsPage(goodsItemIndex)) match {
        case None        => form
        case Some(value) => form.fill(Some(value))
      }

      Ok(view(preparedForm, goodsItemIndex, mode))
    } else {
      Redirect(problemRoute.JourneyRecoveryController.onPageLoad())
    }
  }

  def onSubmit(goodsItemIndex: Int, mode: Mode): Action[AnyContent] = (actionBuilder andThen getData andThen requireData).async { implicit request =>
    val userAnswers: UserAnswers = request.userAnswers
    val nextIndex: Int = DiscrepancyGoods.nextIndex(userAnswers)

    if (DiscrepancyGoods.exists(userAnswers, goodsItemIndex) || goodsItemIndex == nextIndex) {
      form
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, goodsItemIndex, mode))),
          {
            case Some(goodsItem) =>
              for {
                updatedAnswers <- Future
                  .fromTry(request.userAnswers.set(DiscrepancyGoodsPage(goodsItemIndex), goodsItem))
                _ <- sessionRepository.set(updatedAnswers)
              } yield Redirect(createNavigator.nextPage(DiscrepancyGoodsPage(goodsItemIndex), mode, updatedAnswers))

            case None =>
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.remove(DiscrepancyGoodsPage(goodsItemIndex)))
                _ <- sessionRepository.set(updatedAnswers)
              } yield Redirect(nextPageAfterBlank(updatedAnswers, mode))
          }
        )
    } else {
      Future.successful(Redirect(problemRoute.JourneyRecoveryController.onPageLoad()))
    }
  }

  private def nextPageAfterBlank(answers: UserAnswers, mode: Mode): Call =
    if (DiscrepancyGoods.count(answers) > 0)
      createRoute.AddAnotherGoodsItemController.onPageLoad(None, mode)
    else
      mode match {
        case NormalMode => createRoute.DiscrepancyPackingController.onPageLoad(1, mode)
        case CheckMode  => createRoute.CYASubmissionController.onPageLoad()
      }
}
