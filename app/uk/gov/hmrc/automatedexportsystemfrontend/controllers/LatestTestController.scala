package uk.gov.hmrc.automatedexportsystemfrontend.controllers

import javax.inject.Inject
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.actions.{AesAuthRequestActionBuilder, AesDataRequiredAction, AesDataRetrievalAction}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.automatedexportsystemfrontend.views.html.LatestTestView

class LatestTestController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       val actionBuilder: AesAuthRequestActionBuilder,
                                       getData: AesDataRetrievalAction,
                                       requireData: AesDataRequiredAction,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: LatestTestView
                                     ) extends FrontendBaseController with I18nSupport {

  def onPageLoad: Action[AnyContent] = (actionBuilder andThen getData andThen requireData) {
    implicit request =>
      Ok(view())
  }
}
