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
import org.mockito.Mockito.when
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.auth.core.Enrolments
import uk.gov.hmrc.auth.core.retrieve.{~, Credentials}
import uk.gov.hmrc.automatedexportsystemfrontend.connectors.AutomatedExportSystemConnector
import uk.gov.hmrc.automatedexportsystemfrontend.helpers.SpecBase
import uk.gov.hmrc.automatedexportsystemfrontend.helpers.TestFixture.{testAuthorityId, testGroupId}
import uk.gov.hmrc.automatedexportsystemfrontend.models.{SubmissionSummaryResponse, SubmissionSummaryResponseList}
import uk.gov.hmrc.http.SessionKeys

import java.time.LocalDateTime
import java.util.UUID
import scala.concurrent.Future

class CancelSubmissionControllerSpec extends SpecBase {

  "CancelSubmissionController" - {

    "must return OK and display the submission when the submission exists" in {

      val mockAuthConnector =
        mock[uk.gov.hmrc.auth.core.AuthConnector]

      val mockAutomatedExportSystemConnector =
        mock[AutomatedExportSystemConnector]

      val enrolmentIdentifier =
        uk.gov.hmrc.auth.core.EnrolmentIdentifier("EORINumber", "some-eori")

      val enrolments =
        Enrolments(Set(uk.gov.hmrc.auth.core.Enrolment("HMRC-CUS-ORG", Seq(enrolmentIdentifier), "active")))

      when(
        mockAuthConnector
          .authorise[Option[Credentials] ~ Option[String] ~ Enrolments](any(), any())(any(), any())
      ).thenReturn(Future.successful(new ~(new ~(Some(Credentials(testAuthorityId, "government-gateway")), Some(testGroupId)), enrolments)))

      val submission = SubmissionSummaryResponse(
        submissionId = UUID.randomUUID(),
        mrn = "24GB12345678901234",
        ducr = Some("8GB1234567890123456"),
        officeOfExitCode = "GB000051",
        updatedAt = LocalDateTime.of(2026, 8, 17, 10, 30),
        status = 1
      )

      when(
        mockAutomatedExportSystemConnector
          .getSubmissions()(any())
      ).thenReturn(Future.successful(SubmissionSummaryResponseList(Seq(submission))))

      val application =
        applicationBuilder(userAnswers = None)
          .overrides(
            bind[uk.gov.hmrc.auth.core.AuthConnector]
              .toInstance(mockAuthConnector),
            bind[AutomatedExportSystemConnector]
              .toInstance(mockAutomatedExportSystemConnector)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(
            GET,
            routes.CancelSubmissionController
              .onPageLoad(submission.submissionId.toString)
              .url
          ).withSession(SessionKeys.sessionId -> "some-session-id")

        val result = route(application, request).value

        status(result) shouldBe OK

        val body = contentAsString(result)

        status(result) shouldBe OK
      }
    }

    "must return NOT_FOUND when the submission does not exist" in {

      val mockAuthConnector =
        mock[uk.gov.hmrc.auth.core.AuthConnector]

      val mockAutomatedExportSystemConnector =
        mock[AutomatedExportSystemConnector]

      val enrolmentIdentifier =
        uk.gov.hmrc.auth.core.EnrolmentIdentifier("EORINumber", "some-eori")

      val enrolments =
        Enrolments(Set(uk.gov.hmrc.auth.core.Enrolment("HMRC-CUS-ORG", Seq(enrolmentIdentifier), "active")))

      when(
        mockAuthConnector
          .authorise[Option[Credentials] ~ Option[String] ~ Enrolments](any(), any())(any(), any())
      ).thenReturn(Future.successful(new ~(new ~(Some(Credentials(testAuthorityId, "government-gateway")), Some(testGroupId)), enrolments)))

      when(
        mockAutomatedExportSystemConnector
          .getSubmissions()(any())
      ).thenReturn(Future.successful(SubmissionSummaryResponseList(Seq.empty)))

      val application =
        applicationBuilder(userAnswers = None)
          .overrides(
            bind[uk.gov.hmrc.auth.core.AuthConnector]
              .toInstance(mockAuthConnector),
            bind[AutomatedExportSystemConnector]
              .toInstance(mockAutomatedExportSystemConnector)
          )
          .build()

      running(application) {
        val submissionId = UUID.randomUUID().toString

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

      val mockAuthConnector =
        mock[uk.gov.hmrc.auth.core.AuthConnector]

      val mockAutomatedExportSystemConnector =
        mock[AutomatedExportSystemConnector]

      val enrolmentIdentifier =
        uk.gov.hmrc.auth.core.EnrolmentIdentifier("EORINumber", "some-eori")

      val enrolments =
        Enrolments(Set(uk.gov.hmrc.auth.core.Enrolment("HMRC-CUS-ORG", Seq(enrolmentIdentifier), "active")))

      when(
        mockAuthConnector
          .authorise[Option[Credentials] ~ Option[String] ~ Enrolments](any(), any())(any(), any())
      ).thenReturn(Future.successful(new ~(new ~(Some(Credentials(testAuthorityId, "government-gateway")), Some(testGroupId)), enrolments)))

      val submissionId = UUID.randomUUID().toString

      when(
        mockAutomatedExportSystemConnector
          .cancelSubmission(eqTo(submissionId))(any())
      ).thenReturn(Future.successful(Done))

      val application =
        applicationBuilder(userAnswers = None)
          .overrides(
            bind[uk.gov.hmrc.auth.core.AuthConnector]
              .toInstance(mockAuthConnector),
            bind[AutomatedExportSystemConnector]
              .toInstance(mockAutomatedExportSystemConnector)
          )
          .build()

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

      val mockAuthConnector =
        mock[uk.gov.hmrc.auth.core.AuthConnector]

      val mockAutomatedExportSystemConnector =
        mock[AutomatedExportSystemConnector]

      val enrolmentIdentifier =
        uk.gov.hmrc.auth.core.EnrolmentIdentifier("EORINumber", "some-eori")

      val enrolments =
        Enrolments(Set(uk.gov.hmrc.auth.core.Enrolment("HMRC-CUS-ORG", Seq(enrolmentIdentifier), "active")))

      when(
        mockAuthConnector
          .authorise[Option[Credentials] ~ Option[String] ~ Enrolments](any(), any())(any(), any())
      ).thenReturn(Future.successful(new ~(new ~(Some(Credentials(testAuthorityId, "government-gateway")), Some(testGroupId)), enrolments)))

      val submissionId = UUID.randomUUID().toString

      when(
        mockAutomatedExportSystemConnector
          .cancelSubmission(eqTo(submissionId))(any())
      ).thenReturn(Future.failed(new RuntimeException("Cancellation failed")))

      val application =
        applicationBuilder(userAnswers = None)
          .overrides(
            bind[uk.gov.hmrc.auth.core.AuthConnector]
              .toInstance(mockAuthConnector),
            bind[AutomatedExportSystemConnector]
              .toInstance(mockAutomatedExportSystemConnector)
          )
          .build()

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
