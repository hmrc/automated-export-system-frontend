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

import org.apache.pekko.Done
import org.mockito.ArgumentMatchers.{any, eq as eqTo}
import org.mockito.Mockito.{reset, when}
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.automatedexportsystemfrontend.connectors.AutomatedExportSystemConnector
import uk.gov.hmrc.automatedexportsystemfrontend.helpers.SpecBase
import uk.gov.hmrc.automatedexportsystemfrontend.helpers.TestFixture.{singleSubmission, testMrn}
import uk.gov.hmrc.http.{SessionKeys, UpstreamErrorResponse}

import java.util.UUID
import scala.concurrent.Future

class CancelSubmissionControllerSpec extends SpecBase {

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

  "CancelSubmissionController" - {

    "must return OK and display the submission when the submission exists" in {
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
            routes.CancelSubmissionController
              .onPageLoad(submission.submissionId)
              .url
          ).withSession(SessionKeys.sessionId -> "some-session-id")

        val result = route(application, request).value

        val body = contentAsString(result)

        status(result) shouldBe OK
        body should include(testMrn)
        body should include(routes.CancelSubmissionController.onPageLoad(submissionId).url)

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
            routes.CancelSubmissionController
              .onPageLoad(submissionId)
              .url
          ).withSession(SessionKeys.sessionId -> "some-session-id")

        val result = route(application, request).value

        status(result) shouldBe NOT_FOUND
      }
    }

    "must redirect to cancellation success page when cancellation succeeds" in {
      when(
        connector
          .cancelSubmission(eqTo(submissionId))(any())
      ).thenReturn(Future.successful(Done))

      val application = buildApp()

      running(application) {
        val request =
          FakeRequest(
            POST,
            routes.CancelSubmissionController
              .onSubmit(submissionId)
              .url
          ).withSession(SessionKeys.sessionId -> "some-session-id")

        val result = route(application, request).value

        status(result) shouldBe SEE_OTHER

        redirectLocation(result).value shouldBe
          routes.CancellationSuccessController
            .onPageLoad(submissionId)
            .url
      }
    }

    "must redirect to journey recovery when cancellation fails" in {
      when(
        connector
          .cancelSubmission(eqTo(submissionId))(any())
      ).thenReturn(Future.failed(new RuntimeException("Cancellation failed")))

      val application = buildApp()

      running(application) {
        val request =
          FakeRequest(
            POST,
            routes.CancelSubmissionController
              .onSubmit(submissionId)
              .url
          ).withSession(SessionKeys.sessionId -> "some-session-id")

        val result = route(application, request).value

        status(result) shouldBe SEE_OTHER

        redirectLocation(result).value shouldBe
          uk.gov.hmrc.automatedexportsystemfrontend.controllers.problem.routes.JourneyRecoveryController
            .onPageLoad()
            .url
      }
    }
  }
}
