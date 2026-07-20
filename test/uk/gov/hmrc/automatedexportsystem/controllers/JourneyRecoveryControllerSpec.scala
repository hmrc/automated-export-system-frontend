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

package uk.gov.hmrc.automatedexportsystem.controllers

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.auth.core.Enrolments
import uk.gov.hmrc.auth.core.retrieve.{~, Credentials}
import uk.gov.hmrc.automatedexportsystem.helpers.SpecBase
import uk.gov.hmrc.automatedexportsystem.helpers.TestFixture.{testAuthorityId, testGroupId}
import uk.gov.hmrc.play.bootstrap.binders.RedirectUrl
import uk.gov.hmrc.automatedexportsystem.views.html.{JourneyRecoveryContinueView, JourneyRecoveryStartAgainView}

import scala.concurrent.Future

class JourneyRecoveryControllerSpec extends SpecBase {

  "JourneyRecovery Controller" should {

    "when a relative continue Url is supplied" should {

      "must return OK and the continue view" in {
        val mockAuthConnector = mock[uk.gov.hmrc.auth.core.AuthConnector]
        val enrolmentIdentifier = uk.gov.hmrc.auth.core.EnrolmentIdentifier("EORINumber", "some-eori")
        val enrolments = Enrolments(Set(uk.gov.hmrc.auth.core.Enrolment("HMRC-CUS-ORG", Seq(enrolmentIdentifier), "active")))

        when(mockAuthConnector.authorise[Option[Credentials] ~ Option[String] ~ Enrolments](any(), any())(any(), any()))
          .thenReturn(Future.successful(new ~(new ~(Some(Credentials(testAuthorityId, "government-gateway")), Some(testGroupId)), enrolments)))

        val application = applicationBuilder(userAnswers = None)
          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(application) {
          val continueUrl = RedirectUrl("/foo")
          val request = FakeRequest(GET, routes.JourneyRecoveryController.onPageLoad(Some(continueUrl)).url)

          val result = route(application, request).value

          val continueView = application.injector.instanceOf[JourneyRecoveryContinueView]

          status(result) shouldBe OK
          contentAsString(result) shouldBe continueView(continueUrl.unsafeValue)(request, messages(application)).toString
        }
      }
    }

    "when an absolute continue Url is supplied" should {

      "must return OK and the start again view" in {

        val mockAuthConnector = mock[uk.gov.hmrc.auth.core.AuthConnector]
        val enrolmentIdentifier = uk.gov.hmrc.auth.core.EnrolmentIdentifier("EORINumber", "some-eori")
        val enrolments = Enrolments(Set(uk.gov.hmrc.auth.core.Enrolment("HMRC-CUS-ORG", Seq(enrolmentIdentifier), "active")))

        when(mockAuthConnector.authorise[Option[Credentials] ~ Option[String] ~ Enrolments](any(), any())(any(), any()))
          .thenReturn(Future.successful(new ~(new ~(Some(Credentials(testAuthorityId, "government-gateway")), Some(testGroupId)), enrolments)))

        val application = applicationBuilder(userAnswers = None)
          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(application) {
          val continueUrl = RedirectUrl("https://foo.com")
          val request = FakeRequest(GET, routes.JourneyRecoveryController.onPageLoad(Some(continueUrl)).url)

          val result = route(application, request).value

          val startAgainView = application.injector.instanceOf[JourneyRecoveryStartAgainView]

          status(result) shouldBe OK
          contentAsString(result) shouldBe startAgainView()(request, messages(application)).toString
        }
      }
    }

    "when no continue Url is supplied" should {

      "must return OK and the start again view" in {
        val mockAuthConnector = mock[uk.gov.hmrc.auth.core.AuthConnector]
        val enrolmentIdentifier = uk.gov.hmrc.auth.core.EnrolmentIdentifier("EORINumber", "some-eori")
        val enrolments = Enrolments(Set(uk.gov.hmrc.auth.core.Enrolment("HMRC-CUS-ORG", Seq(enrolmentIdentifier), "active")))

        when(mockAuthConnector.authorise[Option[Credentials] ~ Option[String] ~ Enrolments](any(), any())(any(), any()))
          .thenReturn(Future.successful(new ~(new ~(Some(Credentials(testAuthorityId, "government-gateway")), Some(testGroupId)), enrolments)))

        val application = applicationBuilder(userAnswers = None)
          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(application) {
          val request = FakeRequest(GET, routes.JourneyRecoveryController.onPageLoad().url)

          val result = route(application, request).value

          val startAgainView = application.injector.instanceOf[JourneyRecoveryStartAgainView]

          status(result) shouldBe OK
          contentAsString(result) shouldBe startAgainView()(request, messages(application)).toString
        }
      }
    }
  }
}
