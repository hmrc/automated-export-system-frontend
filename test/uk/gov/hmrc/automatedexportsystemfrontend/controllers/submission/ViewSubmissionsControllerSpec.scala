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

package uk.gov.hmrc.automatedexportsystemfrontend.controllers.submission

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import play.api.inject.bind
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.submission.routes
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.auth.core.Enrolments
import uk.gov.hmrc.auth.core.retrieve.{~, Credentials}
import uk.gov.hmrc.automatedexportsystemfrontend.helpers.SpecBase
import uk.gov.hmrc.automatedexportsystemfrontend.helpers.TestFixture.{testAuthorityId, testGroupId}
import uk.gov.hmrc.automatedexportsystemfrontend.views.html.submission.ViewSubmissionsView
import uk.gov.hmrc.http.SessionKeys

import scala.concurrent.Future

class ViewSubmissionsControllerSpec extends SpecBase {

  "viewSubmissions Controller" - {

    "must return OK and the correct view for a GET when submissions made" in {

      val mockAuthConnector = mock[uk.gov.hmrc.auth.core.AuthConnector]
      val enrolmentIdentifier = uk.gov.hmrc.auth.core.EnrolmentIdentifier("EORINumber", "some-eori")
      val enrolments = Enrolments(Set(uk.gov.hmrc.auth.core.Enrolment("HMRC-CUS-ORG", Seq(enrolmentIdentifier), "active")))

      when(mockAuthConnector.authorise[Option[Credentials] ~ Option[String] ~ Enrolments](any(), any())(any(), any()))
        .thenReturn(Future.successful(new ~(new ~(Some(Credentials(testAuthorityId, "government-gateway")), Some(testGroupId)), enrolments)))

      val application = applicationBuilder(userAnswers = None)
        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
        .build()

      running(application) {
        val request = FakeRequest(GET, routes.ViewSubmissionsController.onPageLoad().url)
          .withSession(SessionKeys.sessionId -> "some-session-id")

        val result = route(application, request).value

        val view = application.injector.instanceOf[ViewSubmissionsView]

        status(result) shouldBe OK
        val body = contentAsString(result)
        // TODO Expand unit tests when GET endpoint available.
        body should include("IE507A-2026-04817")
      }
    }

    // TODO Expand unit tests when GET endpoint available.

    //    "must return OK and the correct view for a GET when no submissions made" in {
//
//      val mockAuthConnector = mock[uk.gov.hmrc.auth.core.AuthConnector]
//      val enrolmentIdentifier = uk.gov.hmrc.auth.core.EnrolmentIdentifier("EORINumber", "some-eori")
//      val enrolments = Enrolments(Set(uk.gov.hmrc.auth.core.Enrolment("HMRC-CUS-ORG", Seq(enrolmentIdentifier), "active")))
//
//      when(mockAuthConnector.authorise[Option[Credentials] ~ Option[String] ~ Enrolments](any(), any())(any(), any()))
//        .thenReturn(Future.successful(new~(new~(Some(Credentials(testAuthorityId, "government-gateway")), Some(testGroupId)), enrolments)))
//
//      val application = applicationBuilder(userAnswers = None)
//        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
//        .build()
//
//      running(application) {
//        val request = FakeRequest(GET, routes.ViewSubmissionsController.onPageLoad().url)
//          .withSession(SessionKeys.sessionId -> "some-session-id")
//
//        val result = route(application, request).value
//
//        val view = application.injector.instanceOf[ViewSubmissionsView]
//
//        status(result) shouldBe OK
//      }
//    }

  }
}
