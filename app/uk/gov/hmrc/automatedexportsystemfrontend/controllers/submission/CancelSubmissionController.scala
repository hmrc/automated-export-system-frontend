package uk.gov.hmrc.automatedexportsystem.controllers

import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}

@Singleton
class CancelSubmissionController @Inject() (
                                             cc: ControllerComponents
                                           ) extends FrontendController(cc):

  def confirmCancellation(mrn: String): Action[AnyContent] =
    Action { implicit request =>
      Ok
    }

  def cancellationCompleted(mrn: String): Action[AnyContent] =
    Action { implicit request =>
      Ok
    }