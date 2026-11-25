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
import play.api.Logger
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.actions.{AesAuthRequestActionBuilder, AesDataRequiredAction, AesDataRetrievalAction}
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.create.routes as createRoute
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.problem.routes as problemRoute
import uk.gov.hmrc.automatedexportsystemfrontend.forms.create.DiscrepancySealsFormProvider
import uk.gov.hmrc.automatedexportsystemfrontend.models.{CheckMode, Mode, NormalMode, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.navigation.CreateNavigator
import uk.gov.hmrc.automatedexportsystemfrontend.pages.create.{DiscrepancySealsPage, VisitedDiscrepancySealsPage}
import uk.gov.hmrc.automatedexportsystemfrontend.queries.DiscrepancySeals
import uk.gov.hmrc.automatedexportsystemfrontend.repositories.SessionRepository
import uk.gov.hmrc.automatedexportsystemfrontend.views.html.create.DiscrepancySealsView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DiscrepancySealsController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  createNavigator: CreateNavigator,
  val actionBuilder: AesAuthRequestActionBuilder,
  getData: AesDataRetrievalAction,
  requireData: AesDataRequiredAction,
  formProvider: DiscrepancySealsFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: DiscrepancySealsView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport {

  private val logger = Logger(classOf[DiscrepancySealsController])

  val form: Form[Option[String]] = formProvider()

  def onPageLoad(transportEquipmentIndex: Int, sealsIndex: Int, mode: Mode): Action[AnyContent] =
    (actionBuilder andThen getData andThen requireData) { implicit request =>
      val userAnswers: UserAnswers = request.userAnswers
      val nextIndex: Int = DiscrepancySeals(transportEquipmentIndex).nextIndex(userAnswers)
      if (DiscrepancySeals(transportEquipmentIndex).exists(userAnswers, sealsIndex) || sealsIndex == nextIndex) {
        val preparedForm = userAnswers.get(DiscrepancySealsPage(transportEquipmentIndex, sealsIndex)) match {
          case None        => form
          case Some(value) => form.fill(Some(value))
        }
        Ok(view(preparedForm, transportEquipmentIndex: Int, sealsIndex, mode))
      } else {
        logger.warn(s"[DiscrepancySealsController] Invalid seal index on page load. sealsIndex=$sealsIndex, nextIndex=$nextIndex")
        Redirect(problemRoute.JourneyRecoveryController.onPageLoad())
      }
    }

  def onSubmit(transportEquipmentIndex: Int, sealsIndex: Int, mode: Mode): Action[AnyContent] =
    (actionBuilder andThen getData andThen requireData).async { implicit request =>
      val userAnswers: UserAnswers = request.userAnswers
      val nextIndex: Int = DiscrepancySeals(transportEquipmentIndex).nextIndex(userAnswers)
      if (DiscrepancySeals(transportEquipmentIndex).exists(userAnswers, sealsIndex) || sealsIndex == nextIndex) {
        form
          .bindFromRequest()
          .fold(
            formWithErrors => Future.successful(BadRequest(view(formWithErrors, transportEquipmentIndex: Int, sealsIndex, mode))),
            {
              case Some(sealIdentifier) =>
                for {
                  updatedAnswers <- Future
                    .fromTry(request.userAnswers.set(DiscrepancySealsPage(transportEquipmentIndex, sealsIndex), sealIdentifier))
                  markedAnswers <- Future.fromTry(updatedAnswers.set(VisitedDiscrepancySealsPage(transportEquipmentIndex), true))
                  _ <- sessionRepository.set(markedAnswers)
                } yield Redirect(createNavigator.nextPage(DiscrepancySealsPage(transportEquipmentIndex, sealsIndex), mode, updatedAnswers))

              case None =>
                for {
                  updatedAnswers <- Future.fromTry(request.userAnswers.remove(DiscrepancySealsPage(transportEquipmentIndex, sealsIndex)))
                  markedAnswers <- Future.fromTry(updatedAnswers.set(VisitedDiscrepancySealsPage(transportEquipmentIndex), true))
                  _ <- sessionRepository.set(markedAnswers)
                } yield Redirect(nextPageAfterBlank(transportEquipmentIndex, updatedAnswers, mode))
            }
          )

      } else {
        logger.warn(s"[DiscrepancySealsController] Invalid seal index on submit. sealsIndex=$sealsIndex, nextIndex=$nextIndex")
        Future.successful(Redirect(problemRoute.JourneyRecoveryController.onPageLoad()))
      }
    }

  private def nextPageAfterBlank(transportEquipmentIndex: Int, answers: UserAnswers, mode: Mode): Call =
    if (DiscrepancySeals(transportEquipmentIndex).count(answers) > 0)
      createRoute.AddAnotherSealController.onPageLoad(transportEquipmentIndex, None, mode)
    else
      mode match {
        case NormalMode => createRoute.DiscrepancyReferenceController.onPageLoad(transportEquipmentIndex, 1, mode)
        case CheckMode  => createRoute.CYASubmissionController.onPageLoad()
      }
}
