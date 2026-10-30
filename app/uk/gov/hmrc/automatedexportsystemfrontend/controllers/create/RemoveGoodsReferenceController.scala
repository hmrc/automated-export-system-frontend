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
import uk.gov.hmrc.automatedexportsystemfrontend.forms.create.RemoveGoodsReferenceFormProvider
import uk.gov.hmrc.automatedexportsystemfrontend.models.{Mode, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.pages.create.DiscrepancyReferencePage
import uk.gov.hmrc.automatedexportsystemfrontend.queries.DiscrepancyReference
import uk.gov.hmrc.automatedexportsystemfrontend.repositories.SessionRepository
import uk.gov.hmrc.automatedexportsystemfrontend.viewmodels.addAnother.create.AddAnotherGoodsReferenceViewModel
import uk.gov.hmrc.automatedexportsystemfrontend.views.html.create.RemoveGoodsReferenceView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class RemoveGoodsReferenceController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  val actionBuilder: AesAuthRequestActionBuilder,
  getData: AesDataRetrievalAction,
  requireData: AesDataRequiredAction,
  formProvider: RemoveGoodsReferenceFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: RemoveGoodsReferenceView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport {

  def form(goodsReference: String): Form[Boolean] = formProvider(goodsReference)

  def onPageLoad(discrepancyReferenceIndex: Int, mode: Mode): Action[AnyContent] =
    (actionBuilder andThen getData andThen requireData) { implicit request =>
      request.userAnswers.get(DiscrepancyReferencePage(discrepancyReferenceIndex)) match {
        case Some(reference) => Ok(view(form(reference), reference, discrepancyReferenceIndex, mode))
        case None            => Redirect(problemRoute.JourneyRecoveryController.onPageLoad())
      }
    }

  def onSubmit(discrepancyReferenceIndex: Int, mode: Mode): Action[AnyContent] =
    (actionBuilder andThen getData andThen requireData).async { implicit request =>
      request.userAnswers.get(DiscrepancyReferencePage(discrepancyReferenceIndex)) match {
        case Some(reference) =>
          form(reference)
            .bindFromRequest()
            .fold(
              formWithErrors => Future.successful(BadRequest(view(formWithErrors, reference, discrepancyReferenceIndex, mode))),
              {
                case true =>
                  for {
                    updatedAnswers <- Future.fromTry(DiscrepancyReference.removeOne(request.userAnswers, discrepancyReferenceIndex))
                    _ <- sessionRepository.set(updatedAnswers)
                  } yield
                    if (DiscrepancyReference.count(updatedAnswers) == 0) {
                      Redirect(createRoute.DiscrepancyReferenceController.onPageLoad(1, mode))
                    } else {
                      Redirect(createRoute.AddAnotherGoodsReferenceController.onPageLoad(mode))
                    }
                case false => Future.successful(Redirect(createRoute.AddAnotherGoodsReferenceController.onPageLoad(mode)))
              }
            )
        case None => Future.successful(Redirect(problemRoute.JourneyRecoveryController.onPageLoad()))
      }
    }
}
