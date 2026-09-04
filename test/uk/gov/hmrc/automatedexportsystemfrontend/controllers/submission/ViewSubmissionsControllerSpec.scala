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
import uk.gov.hmrc.http.SessionKeys
import uk.gov.hmrc.automatedexportsystemfrontend.connectors.AutomatedExportSystemConnector
import uk.gov.hmrc.automatedexportsystemfrontend.models.{SubmissionSummaryResponse, SubmissionSummaryResponseList}

import java.time.LocalDateTime
import java.util.UUID

import scala.concurrent.Future

class ViewSubmissionsControllerSpec extends SpecBase {

  "viewSubmissions Controller" - {

    "must return OK and the correct view for a GET when submissions made" in {

      val mockAuthConnector = mock[uk.gov.hmrc.auth.core.AuthConnector]
      val mockAutomatedExportSystemConnector = mock[AutomatedExportSystemConnector]

      val enrolmentIdentifier =
        uk.gov.hmrc.auth.core.EnrolmentIdentifier("EORINumber", "some-eori")

      val enrolments =
        Enrolments(Set(uk.gov.hmrc.auth.core.Enrolment("HMRC-CUS-ORG", Seq(enrolmentIdentifier), "active")))

      when(mockAuthConnector.authorise[Option[Credentials] ~ Option[String] ~ Enrolments](any(), any())(any(), any()))
        .thenReturn(Future.successful(new ~(new ~(Some(Credentials(testAuthorityId, "government-gateway")), Some(testGroupId)), enrolments)))

      val submissionResponse = SubmissionSummaryResponse(
        submissionId = UUID.randomUUID(),
        mrn = "24GB12345678901234",
        ducr = Some("8GB1234567890123456"),
        officeOfExitCode = "GB000051",
        updatedAt = LocalDateTime.of(2026, 8, 17, 10, 30),
        status = 1
      )

      val submissionResponseList =
        SubmissionSummaryResponseList(Seq(submissionResponse))

      when(mockAutomatedExportSystemConnector.getSubmissions()(any()))
        .thenReturn(Future.successful(submissionResponseList))

      val application = applicationBuilder(userAnswers = None)
        .overrides(
          bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector),
          bind[AutomatedExportSystemConnector].toInstance(mockAutomatedExportSystemConnector)
        )
        .build()

      running(application) {
        val request =
          FakeRequest(GET, routes.ViewSubmissionsController.onPageLoad().url)
            .withSession(SessionKeys.sessionId -> "some-session-id")

        val result = route(application, request).value

        status(result) shouldBe OK

        val body = contentAsString(result)

        body should include("24GB12345678901234")
        body should include("8GB1234567890123456")
      }
    }

    "must display all submissions when multiple submissions are returned" in {

      val mockAuthConnector = mock[uk.gov.hmrc.auth.core.AuthConnector]
      val mockAutomatedExportSystemConnector = mock[AutomatedExportSystemConnector]

      val enrolmentIdentifier =
        uk.gov.hmrc.auth.core.EnrolmentIdentifier("EORINumber", "some-eori")

      val enrolments =
        Enrolments(Set(uk.gov.hmrc.auth.core.Enrolment("HMRC-CUS-ORG", Seq(enrolmentIdentifier), "active")))

      when(mockAuthConnector.authorise[Option[Credentials] ~ Option[String] ~ Enrolments](any(), any())(any(), any()))
        .thenReturn(Future.successful(new ~(new ~(Some(Credentials(testAuthorityId, "government-gateway")), Some(testGroupId)), enrolments)))

      val submission1 = SubmissionSummaryResponse(
        submissionId = UUID.randomUUID(),
        mrn = "MRN1234567890ABCDEF",
        ducr = Some("DUCR1234567890XYZ"),
        officeOfExitCode = "GB000051",
        updatedAt = LocalDateTime.of(2026, 8, 17, 10, 30),
        status = 1
      )

      val submission2 = SubmissionSummaryResponse(
        submissionId = UUID.randomUUID(),
        mrn = "A1B2C3D4E5F6G7H8I9J0",
        ducr = Some("DUCR-SECOND-0001"),
        officeOfExitCode = "GB000142",
        updatedAt = LocalDateTime.of(2026, 8, 16, 14, 45),
        status = 2
      )

      when(mockAutomatedExportSystemConnector.getSubmissions()(any()))
        .thenReturn(Future.successful(SubmissionSummaryResponseList(Seq(submission1, submission2))))

      val application = applicationBuilder(userAnswers = None)
        .overrides(
          bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector),
          bind[AutomatedExportSystemConnector].toInstance(mockAutomatedExportSystemConnector)
        )
        .build()

      running(application) {
        val request =
          FakeRequest(GET, routes.ViewSubmissionsController.onPageLoad().url)
            .withSession(SessionKeys.sessionId -> "some-session-id")

        val result = route(application, request).value

        status(result) shouldBe OK

        val body = contentAsString(result)

        body should include("MRN1234567890ABCDEF")
        body should include("DUCR1234567890XYZ")
        body should include("A1B2C3D4E5F6G7H8I9J0")
        body should include("DUCR-SECOND-0001")
      }
    }

    "must display submission when DUCR is not present" in {

      val mockAuthConnector = mock[uk.gov.hmrc.auth.core.AuthConnector]
      val mockAutomatedExportSystemConnector = mock[AutomatedExportSystemConnector]

      val enrolmentIdentifier =
        uk.gov.hmrc.auth.core.EnrolmentIdentifier("EORINumber", "some-eori")

      val enrolments =
        Enrolments(Set(uk.gov.hmrc.auth.core.Enrolment("HMRC-CUS-ORG", Seq(enrolmentIdentifier), "active")))

      when(mockAuthConnector.authorise[Option[Credentials] ~ Option[String] ~ Enrolments](any(), any())(any(), any()))
        .thenReturn(Future.successful(new ~(new ~(Some(Credentials(testAuthorityId, "government-gateway")), Some(testGroupId)), enrolments)))

      val submission = SubmissionSummaryResponse(
        submissionId = UUID.randomUUID(),
        mrn = "MRN1234567890ABCPDF",
        ducr = None,
        officeOfExitCode = "GB000051",
        updatedAt = LocalDateTime.of(2026, 8, 17, 10, 30),
        status = 1
      )

      when(mockAutomatedExportSystemConnector.getSubmissions()(any()))
        .thenReturn(Future.successful(SubmissionSummaryResponseList(Seq(submission))))

      val application = applicationBuilder(userAnswers = None)
        .overrides(
          bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector),
          bind[AutomatedExportSystemConnector].toInstance(mockAutomatedExportSystemConnector)
        )
        .build()

      running(application) {
        val request =
          FakeRequest(GET, routes.ViewSubmissionsController.onPageLoad().url)
            .withSession(SessionKeys.sessionId -> "some-session-id")

        val result = route(application, request).value

        status(result) shouldBe OK

        val body = contentAsString(result)

        body should include("MRN1234567890ABCPDF")
      }
    }

    "must display no submissions message when no submissions are returned" in {

      val mockAuthConnector = mock[uk.gov.hmrc.auth.core.AuthConnector]
      val mockAutomatedExportSystemConnector = mock[AutomatedExportSystemConnector]

      val enrolmentIdentifier =
        uk.gov.hmrc.auth.core.EnrolmentIdentifier("EORINumber", "some-eori")

      val enrolments =
        Enrolments(Set(uk.gov.hmrc.auth.core.Enrolment("HMRC-CUS-ORG", Seq(enrolmentIdentifier), "active")))

      when(
        mockAuthConnector
          .authorise[Option[Credentials] ~ Option[String] ~ Enrolments](any(), any())(any(), any())
      ).thenReturn(Future.successful(new ~(new ~(Some(Credentials(testAuthorityId, "government-gateway")), Some(testGroupId)), enrolments)))

      when(mockAutomatedExportSystemConnector.getSubmissions()(any()))
        .thenReturn(Future.successful(SubmissionSummaryResponseList(Seq.empty)))

      val application = applicationBuilder(userAnswers = None)
        .overrides(
          bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector),
          bind[AutomatedExportSystemConnector].toInstance(mockAutomatedExportSystemConnector)
        )
        .build()

      running(application) {
        val request =
          FakeRequest(GET, routes.ViewSubmissionsController.onPageLoad().url)
            .withSession(SessionKeys.sessionId -> "some-session-id")

        val result = route(application, request).value

        status(result) shouldBe OK

        val body = contentAsString(result)

        body should include("You have no IE507(a) submissions")
      }
    }

  }
}
