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

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import play.api.http.Status
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Result, Results}
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.auth.core.*
import uk.gov.hmrc.auth.core.retrieve.{~, Credentials}
import uk.gov.hmrc.automatedexportsystemfrontend.helpers.SpecBase
import uk.gov.hmrc.automatedexportsystemfrontend.helpers.TestFixture.*
import uk.gov.hmrc.automatedexportsystemfrontend.config.FrontendAppConfig
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.actions.AesAuthRequestActionBuilder
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.actions.requests.AesAuthRequest

import java.net.URLDecoder
import scala.concurrent.Future
class AesAuthRequestActionBuilderSpec extends SpecBase with Status with Results with ScalaFutures {

  trait Setup {
    implicit val mockAppConfig: FrontendAppConfig = mockFrontEndAppconfig
    val authenticatedAction =
      new AesAuthRequestActionBuilder(config = mockAppConfig, env = environment, authConnector = mockAuthConnector, cc = stubControllerComponents())
  }

  "authorising the request" - {
    "execute the supplied body and return the request contents successfully" in new Setup {
      val eori = "some-eori"
      val enrolmentIdentifier = EnrolmentIdentifier("EORINumber", eori)
      val enrolments = Enrolments(Set(Enrolment("HMRC-CUS-ORG", Seq(enrolmentIdentifier), "active")))

      when(mockAuthConnector.authorise[Option[Credentials] ~ Option[String] ~ Enrolments](any(), any())(any(), any()))
        .thenReturn(Future.successful(new ~(new ~(Some(Credentials(testAuthorityId, "government-gateway")), Some(testGroupId)), enrolments)))

      val action: AesAuthRequest[?] => Future[Result] =
        request => Future.successful(Ok(Json.obj("authorityId" -> request.authorityId, "groupId" -> testGroupId, "EORINumber" -> eori)))

      val result: Future[Result] = authenticatedAction.invokeBlock(FakeRequest().withSession("sessionId" -> "some-session-id"), action)

      status(result) shouldBe OK
      val json: JsValue = contentAsJson(result)
      (json \ "authorityId").as[String] shouldBe testAuthorityId
      (json \ "groupId").as[String] shouldBe testGroupId
      (json \ "EORINumber").as[String] shouldBe eori
    }

    "throw a 500 error when authorityId or groupId is missing" in new Setup {
      when(mockAuthConnector.authorise[Option[Credentials] ~ Option[String] ~ Enrolments](any(), any())(any(), any()))
        .thenReturn(Future(new ~(new ~(None, None), mock[Enrolments])))

      val action: AesAuthRequest[?] => Future[Result] = { request =>
        Future(Ok(Json.obj("authorityId" -> request.authorityId)))
      }
      intercept[InternalError](await(authenticatedAction.invokeBlock(FakeRequest(), action)))
    }

    "redirect to GG if not signed in" in new Setup {
      List(BearerTokenExpired(), MissingBearerToken(), InvalidBearerToken(), SessionRecordNotFound()).foreach { exception =>
        when(mockAuthConnector.authorise[Option[Credentials] ~ Option[String]](any(), any())(any(), any()))
          .thenReturn(Future.failed(exception))
        when(mockAppConfig.ggSignInUrl).thenReturn("http://example.com/sign-in")
        when(mockAppConfig.authContinueBaseUrl).thenReturn("http://example.com/continue")
        when(mockAppConfig.appName).thenReturn("Test App")

        val fakeRequest = FakeRequest("GET", "/automated-export-system")
        val block: AesAuthRequest[?] => Future[Result] = { request =>
          Future.successful(Ok(Json.obj("authorityId" -> request.authorityId)))
        }

        val result = authenticatedAction.invokeBlock(fakeRequest, block)

        status(result) shouldBe SEE_OTHER
        val url = redirectLocation(result).map(URLDecoder.decode(_, "UTF-8"))
        url shouldBe Some(
          s"${mockFrontEndAppconfig.ggSignInUrl}?continue=${mockFrontEndAppconfig.authContinueBaseUrl}/automated-export-system&origin=${mockFrontEndAppconfig.appName}"
        )
      }
    }

    "redirect to EEC if not enrolled" in new Setup {
      when(mockAuthConnector.authorise[Option[Credentials] ~ Option[String]](any(), any())(any(), any()))
        .thenReturn(Future.failed(InsufficientEnrolments()))
      when(mockAppConfig.eccSubscribeUrl).thenReturn("http://example.com/subscribe")

      val action: AesAuthRequest[?] => Future[Result] = { request =>
        Future(Ok(Json.obj("authorityId" -> request.authorityId)))
      }

      val result: Future[Result] = authenticatedAction.invokeBlock(FakeRequest(), action)

      status(result) shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some("http://example.com/subscribe")
    }

    "redirect to GG if missing sessionId" in new Setup {
      val eori = "some-eori"
      val enrolmentIdentifier = EnrolmentIdentifier("EORINumber", eori)
      val enrolments = Enrolments(Set(Enrolment("HMRC-CUS-ORG", Seq(enrolmentIdentifier), "active")))

      when(mockAuthConnector.authorise[Option[Credentials] ~ Option[String] ~ Enrolments](any(), any())(any(), any()))
        .thenReturn(Future.successful(new ~(new ~(Some(Credentials(testAuthorityId, "government-gateway")), Some(testGroupId)), enrolments)))

      when(mockAppConfig.ggSignInUrl).thenReturn("http://example.com/login")
      when(mockAppConfig.authContinueBaseUrl).thenReturn("http://localhost:5002")
      when(mockAppConfig.appName).thenReturn("automated-export-system-frontend")

      val action: AesAuthRequest[?] => Future[Result] =
        request => Future.successful(Ok(Json.obj("authorityId" -> request.authorityId, "groupId" -> request.groupId, "EORINumber" -> request.eori)))

      val fakeRequest = FakeRequest(GET, "/some-path-without-session-id")
      val result: Future[Result] = authenticatedAction.invokeBlock(fakeRequest, action)

      status(result) shouldBe SEE_OTHER
      val location = redirectLocation(result).value
      location should startWith("http://example.com/login")
      location should include("continue=")
      location should include("origin=")
    }

  }
}
