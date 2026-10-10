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

import org.mockito.ArgumentMatchers.{any, eq as eqTo}
import org.mockito.Mockito.{reset, verify, when}
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.automatedexportsystemfrontend.connectors.AutomatedExportSystemConnector
import uk.gov.hmrc.automatedexportsystemfrontend.helpers.SpecBase
import uk.gov.hmrc.automatedexportsystemfrontend.helpers.TestFixture.{singleSubmission, testMrn}
import uk.gov.hmrc.http.{SessionKeys, UpstreamErrorResponse}

import java.util.UUID
import scala.concurrent.Future

class CancellationSuccessControllerSpec extends SpecBase {
  private val submissionId = UUID.randomUUID().toString
  private val connector = mock[AutomatedExportSystemConnector]

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(connector)
  }

  private def buildApp() = applicationBuilder(userAnswers = Some(emptyUserAnswers))
    .overrides(
      bind[uk.gov.hmrc.auth.core.AuthConnector]
        .toInstance(mockAuthConnector),
      bind[AutomatedExportSystemConnector]
        .toInstance(connector)
    )
    .build()

  "CancellationSuccessController" - {

    "must return OK when the submission exists" in {
      val submission = singleSubmission(submissionId)
      when(
        connector
          .getSingleSubmission(eqTo(submissionId))(any())
      ).thenReturn(Future.successful(submission))

      val application = buildApp()

      running(application) {

        val request =
          FakeRequest(
            GET,
            routes.CancellationSuccessController
              .onPageLoad(submission.submissionId)
              .url
          ).withSession(SessionKeys.sessionId -> "some-session-id")

        val result = route(application, request).value

        status(result) shouldBe OK

        val body = contentAsString(result)

        body should include("Submission cancelled")
        body should include(testMrn)
      }
    }

    "must return NOT_FOUND when the submission does not exist" in {
      when(
        connector
          .getSingleSubmission(eqTo(submissionId))(any())
      ).thenReturn(Future.failed(UpstreamErrorResponse("not found", NOT_FOUND)))

      val application = buildApp()

      running(application) {

        val request =
          FakeRequest(
            GET,
            routes.CancellationSuccessController
              .onPageLoad(submissionId)
              .url
          ).withSession(SessionKeys.sessionId -> "some-session-id")

        val result = route(application, request).value

        status(result) shouldBe NOT_FOUND
        contentAsString(result) should not include "Submission cancelled"
      }
    }

    "must look up the submission using the ID in the URL" in {
      when(connector.getSingleSubmission(eqTo(submissionId))(any()))
        .thenReturn(Future.successful(singleSubmission(submissionId)))

      val application = buildApp()

      running(application) {
        val request =
          FakeRequest(
            GET,
            routes.CancellationSuccessController
              .onPageLoad(submissionId)
              .url
          ).withSession(SessionKeys.sessionId -> "some-session-id")

        status(route(application, request).value) shouldBe OK

        verify(connector).getSingleSubmission(eqTo(submissionId))(any())
      }
    }

    "must propagate the error when retrieving the submission fails with a non-404 error" in {
      when(connector.getSingleSubmission(eqTo(submissionId))(any()))
        .thenReturn(Future.failed(UpstreamErrorResponse("failure", INTERNAL_SERVER_ERROR)))

      val application = buildApp()

      running(application) {
        val request =
          FakeRequest(
            GET,
            routes.CancellationSuccessController
              .onPageLoad(submissionId)
              .url
          ).withSession(SessionKeys.sessionId -> "some-session-id")

        val result = route(application, request).value

        intercept[UpstreamErrorResponse] {
          await(result)
        }.statusCode shouldBe INTERNAL_SERVER_ERROR
      }
    }
  }
}
