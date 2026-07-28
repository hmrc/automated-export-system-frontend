/*
 * Copyright 2021 HM Revenue & Customs
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

package uk.gov.hmrc.automatedexportsystemfrontend.controllers

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{never, times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.auth.core.Enrolments
import uk.gov.hmrc.auth.core.retrieve.{~, Credentials}
import uk.gov.hmrc.automatedexportsystemfrontend.helpers.SpecBase
import uk.gov.hmrc.automatedexportsystemfrontend.helpers.TestFixture.{testAuthorityId, testGroupId}
import uk.gov.hmrc.automatedexportsystemfrontend.repositories.SessionRepository

import scala.concurrent.Future

class KeepAliveControllerSpec extends SpecBase with MockitoSugar {

  "keepAlive" - {

    "when the user has answered some questions" - {

      "must keep the answers alive and return OK" in {

        val mockSessionRepository = mock[SessionRepository]
        when(mockSessionRepository.keepAlive(any())) thenReturn Future.successful(true)
        val mockAuthConnector = mock[uk.gov.hmrc.auth.core.AuthConnector]
        val enrolmentIdentifier = uk.gov.hmrc.auth.core.EnrolmentIdentifier("EORINumber", "some-eori")
        val enrolments = Enrolments(Set(uk.gov.hmrc.auth.core.Enrolment("HMRC-CUS-ORG", Seq(enrolmentIdentifier), "active")))

        when(mockAuthConnector.authorise[Option[Credentials] ~ Option[String] ~ Enrolments](any(), any())(any(), any()))
          .thenReturn(Future.successful(new ~(new ~(Some(Credentials(testAuthorityId, "government-gateway")), Some(testGroupId)), enrolments)))

        val application =
          applicationBuilder(Some(emptyUserAnswers))
            .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
            .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
            .build()

        running(application) {

          val request = FakeRequest(GET, s"${routes.KeepAliveController.keepAlive().url}")

          val result = route(application, request).value

          status(result) shouldBe OK
          verify(mockSessionRepository, times(1)).keepAlive(emptyUserAnswers.id)
        }
      }
    }
    "when the user has not answered any questions" - {

      "must return OK" in {

        val mockSessionRepository = mock[SessionRepository]
        when(mockSessionRepository.keepAlive(any())) thenReturn Future.successful(true)
        val mockAuthConnector = mock[uk.gov.hmrc.auth.core.AuthConnector]
        val enrolmentIdentifier = uk.gov.hmrc.auth.core.EnrolmentIdentifier("EORINumber", "some-eori")
        val enrolments = Enrolments(Set(uk.gov.hmrc.auth.core.Enrolment("HMRC-CUS-ORG", Seq(enrolmentIdentifier), "active")))

        when(mockAuthConnector.authorise[Option[Credentials] ~ Option[String] ~ Enrolments](any(), any())(any(), any()))
          .thenReturn(Future.successful(new ~(new ~(Some(Credentials(testAuthorityId, "government-gateway")), Some(testGroupId)), enrolments)))

        val application =
          applicationBuilder(None)
            .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
            .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
            .build()

        running(application) {

          val request = FakeRequest(GET, routes.KeepAliveController.keepAlive().url)

          val result = route(application, request).value

          status(result) shouldBe OK
          verify(mockSessionRepository, never()).keepAlive(any())
        }
      }
    }
  }
}
