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
import uk.gov.hmrc.automatedexportsystemfrontend.forms.create.DiscrepancyTransportFormProvider
import uk.gov.hmrc.automatedexportsystemfrontend.models.{CheckMode, ContainerDetails, Mode, NormalMode, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.navigation.CreateNavigator
import uk.gov.hmrc.automatedexportsystemfrontend.pages.create.DiscrepancyTransportPage
import uk.gov.hmrc.automatedexportsystemfrontend.queries.DiscrepancyTransportEquipment
import uk.gov.hmrc.automatedexportsystemfrontend.repositories.SessionRepository
import uk.gov.hmrc.automatedexportsystemfrontend.views.html.create.DiscrepancyTransportView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DiscrepancyTransportController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  createNavigator: CreateNavigator,
  val actionBuilder: AesAuthRequestActionBuilder,
  getData: AesDataRetrievalAction,
  requireData: AesDataRequiredAction,
  formProvider: DiscrepancyTransportFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: DiscrepancyTransportView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport {

  val form: Form[ContainerDetails] = formProvider()

  def onPageLoad(transportEquipmentIndex: Int, mode: Mode): Action[AnyContent] =
    (actionBuilder andThen getData andThen requireData) { implicit request =>

      val userAnswers: UserAnswers =
        request.userAnswers

      val nextIndex: Int =
        DiscrepancyTransportEquipment.nextIndex(userAnswers)

      if (
        DiscrepancyTransportEquipment.exists(userAnswers, transportEquipmentIndex) ||
        transportEquipmentIndex == nextIndex
      ) {
        val preparedForm =
          userAnswers
            .get(DiscrepancyTransportPage(transportEquipmentIndex)) match {
            case None        => form
            case Some(value) => form.fill(value)
          }

        Ok(view(preparedForm, transportEquipmentIndex, mode))
      } else {
        Redirect(problemRoute.JourneyRecoveryController.onPageLoad())
      }
    }

  def onSubmit(transportEquipmentIndex: Int, mode: Mode): Action[AnyContent] =
    (actionBuilder andThen getData andThen requireData).async { implicit request =>
      val userAnswers: UserAnswers =
        request.userAnswers

      val nextIndex: Int =
        DiscrepancyTransportEquipment.nextIndex(userAnswers)

      if (
        DiscrepancyTransportEquipment.exists(userAnswers, transportEquipmentIndex) ||
        transportEquipmentIndex == nextIndex
      ) {
        form
          .bindFromRequest()
          .fold(
            formWithErrors => Future.successful(BadRequest(view(formWithErrors, transportEquipmentIndex, mode))),
            value =>
              for {
                updatedAnswers <- value match {
                  case equipment if equipment.containerId.isDefined || equipment.numberOfSeals.isDefined =>
                    Future.fromTry(userAnswers.set(DiscrepancyTransportPage(transportEquipmentIndex), value))
                  case _ => Future.fromTry(userAnswers.remove(DiscrepancyTransportPage(transportEquipmentIndex)))
                }
                _ <- sessionRepository.set(updatedAnswers)
              } yield Redirect(value match {
                case equipment if equipment.containerId.isDefined || equipment.numberOfSeals.isDefined =>
                  createNavigator.nextPage(DiscrepancyTransportPage(transportEquipmentIndex), mode, updatedAnswers)
                case _ => nextPageAfterBlank(updatedAnswers, mode)
              })
          )
      } else {
        Future.successful(Redirect(problemRoute.JourneyRecoveryController.onPageLoad()))
      }
    }

  private def nextPageAfterBlank(answers: UserAnswers, mode: Mode): Call =
    if (DiscrepancyTransportEquipment.count(answers) > 0)
      createRoute.AddAnotherTransportController.onPageLoad(None, mode)
    else
      mode match {
        case NormalMode => createRoute.DiscrepancySealsController.onPageLoad(1, mode)
        case CheckMode  => createRoute.CYASubmissionController.onPageLoad()
      }

}
