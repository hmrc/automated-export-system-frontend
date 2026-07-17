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

package uk.gov.hmrc.automatedexportsystem.controllers.actions

import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.*
import play.api.{Configuration, Environment, Logger}
import uk.gov.hmrc.auth.core.*
import uk.gov.hmrc.auth.core.retrieve.v2.*
import uk.gov.hmrc.auth.core.retrieve.{~, Credentials}
import uk.gov.hmrc.automatedexportsystem.config.FrontendAppConfig
import uk.gov.hmrc.automatedexportsystem.controllers.actions.requests.AesAuthRequest
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendHeaderCarrierProvider

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RequestActionBuilder @Inject() (
  val config: FrontendAppConfig,
  val env: Environment,
  val authConnector: AuthConnector,
  cc: ControllerComponents
)(implicit val executionContext: ExecutionContext, appConfig: FrontendAppConfig)
    extends ActionBuilder[AesAuthRequest, AnyContent] with FrontendHeaderCarrierProvider with Results with AuthorisedFunctions with I18nSupport {
  val logger: Logger = Logger(this.getClass.getName)

  val messagesApi: MessagesApi = cc.messagesApi

  val parser: BodyParser[AnyContent] = cc.parsers.anyContent

  override def invokeBlock[A](request: Request[A], block: AesAuthRequest[A] => Future[Result]): Future[Result] =
    authorised(Enrolment("HMRC-CUS-ORG"))
      .retrieve[Option[Credentials] ~ Option[String] ~ Enrolments](
        Retrievals.credentials and Retrievals.groupIdentifier and Retrievals.allEnrolments
      ) {
        case Some(information) ~ Some(groupId) ~ enrolments =>
          block(AesAuthRequest(information.providerId, groupId, request))
        case _ ~ _ => Future.failed(throw InternalError())
      }(hc(request), executionContext)
      .recover(handleFailure(request))

  private def handleFailure(implicit request: Request[?]): PartialFunction[Throwable, Result] = {
    case _: NoActiveSession =>
      Redirect(appConfig.ggSignInUrl, Map("continue" -> Seq(s"${appConfig.authContinueBaseUrl}${request.uri}"), "origin" -> Seq(appConfig.appName)))
    case _: InsufficientEnrolments =>
      Redirect(appConfig.eccSubscribeUrl)
  }

}
