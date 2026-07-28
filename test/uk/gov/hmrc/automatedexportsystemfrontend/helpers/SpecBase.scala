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

package uk.gov.hmrc.automatedexportsystemfrontend.helpers

//import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.stream.Materializer
import org.mockito.ArgumentMatchers.any
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.actions.*
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterEach, Inside, OptionValues, TryValues}
import play.api.Application
import play.api.http.{HeaderNames, Status}
import play.api.i18n.{Messages, MessagesApi}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.{DefaultAwaitTimeout, FakeRequest, FutureAwaits, ResultExtractors}
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.actions.{AesDataRequiredAction, AesDataRequiredActionImpl, AesDataRetrievalAction}
import uk.gov.hmrc.automatedexportsystemfrontend.models.UserAnswers
import uk.gov.hmrc.play.audit.http.HttpAuditing
import org.scalatest.matchers.should.Matchers as ShouldMatch
import uk.gov.hmrc.http.HeaderCarrier
import org.scalatest.freespec.AnyFreeSpecLike
import uk.gov.hmrc.auth.core.Enrolments
import uk.gov.hmrc.auth.core.retrieve.{~, Credentials}
import org.mockito.Mockito.when
import uk.gov.hmrc.automatedexportsystemfrontend.helpers.TestFixture.{testAuthorityId, testGroupId}

import scala.concurrent.{ExecutionContext, Future}

trait SpecBase
    extends AnyFreeSpecLike with Inside with ShouldMatch with TryValues with OptionValues with ScalaFutures with IntegrationPatience with AllMocks
    with FutureAwaits with Configs with ResultExtractors with BeforeAndAfterEach with DefaultAwaitTimeout with HeaderNames with Status:

  implicit lazy val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
  implicit lazy val hc: HeaderCarrier = HeaderCarrier()
  implicit lazy val system: ActorSystem = ActorSystem()
  implicit lazy val materializer: Materializer = Materializer(system)

  val userAnswersId: String = "some-id"
  def emptyUserAnswers: UserAnswers = UserAnswers(userAnswersId)

  def messages(app: Application): Messages =
    app.injector.instanceOf[MessagesApi].preferred(FakeRequest())

  override val mockAuthConnector = mock[uk.gov.hmrc.auth.core.AuthConnector]
  val enrolmentIdentifier = uk.gov.hmrc.auth.core.EnrolmentIdentifier("EORINumber", "some-eori")
  val enrolments = Enrolments(Set(uk.gov.hmrc.auth.core.Enrolment("HMRC-CUS-ORG", Seq(enrolmentIdentifier), "active")))

  when(mockAuthConnector.authorise[Option[Credentials] ~ Option[String] ~ Enrolments](any(), any())(any(), any()))
    .thenReturn(Future.successful(new ~(new ~(Some(Credentials(testAuthorityId, "government-gateway")), Some(testGroupId)), enrolments)))

  protected def applicationBuilder(userAnswers: Option[UserAnswers] = None): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[AesDataRequiredAction].to[AesDataRequiredActionImpl],
        bind[AesDataRetrievalAction].toInstance(new FakeDataRetrievalAction(userAnswers)),
        bind[HttpAuditing].toInstance(mockHttpAuditing)
      )
