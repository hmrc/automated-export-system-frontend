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

package uk.gov.hmrc.automatedexportsystemfrontend.controllers.actions

import javax.inject.Inject
import play.api.mvc.ActionTransformer
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.actions.requests.AesAuthRequest
import uk.gov.hmrc.automatedexportsystemfrontend.models.requests.OptionalDataRequest
import uk.gov.hmrc.automatedexportsystemfrontend.repositories.SessionRepository

import scala.concurrent.{ExecutionContext, Future}

class AesDataRetrievalActionImpl @Inject() (val sessionRepository: SessionRepository)(implicit val executionContext: ExecutionContext)
    extends AesDataRetrievalAction {

  override protected def transform[A](request: AesAuthRequest[A]): Future[OptionalDataRequest[A]] =
    sessionRepository
      .get(request.sessionId)
      .map(answers => OptionalDataRequest(request, request.sessionId, answers))
}

trait AesDataRetrievalAction extends ActionTransformer[AesAuthRequest, OptionalDataRequest]
