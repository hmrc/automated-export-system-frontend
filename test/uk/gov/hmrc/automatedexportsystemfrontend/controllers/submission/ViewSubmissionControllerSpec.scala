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
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.auth.core.Enrolments
import uk.gov.hmrc.auth.core.retrieve.{~, Credentials}
import uk.gov.hmrc.automatedexportsystemfrontend.connectors.AutomatedExportSystemConnector
import uk.gov.hmrc.automatedexportsystemfrontend.helpers.SpecBase
import uk.gov.hmrc.automatedexportsystemfrontend.helpers.TestFixture.{testAuthorityId, testGroupId}
import uk.gov.hmrc.automatedexportsystemfrontend.models.{SingleSubmissionResponseParser, SubmissionSummaryResponse, SubmissionSummaryResponseList}
import uk.gov.hmrc.automatedexportsystemfrontend.repositories.SessionRepository
import uk.gov.hmrc.http.{SessionKeys, UpstreamErrorResponse}

import java.time.LocalDateTime
import java.util.UUID
import scala.concurrent.Future

class ViewSubmissionControllerSpec extends SpecBase {

  private val xml =
    """<Submission>
      |  <submissionId>12345</submissionId>
      |  <ExportOperation>
      |    <type>1</type>
      |    <MRN>mrn12345</MRN>
      |    <discrepanciesExist>1</discrepanciesExist>
      |    <splitIndicator>1</splitIndicator>
      |  </ExportOperation>
      |  <CustomsOfficeOfExitActual>
      |    <referenceNumber>GB000051</referenceNumber>
      |  </CustomsOfficeOfExitActual>
      |  <GoodsShipment>
      |    <Consignment>
      |      <referenceNumberUCR>referenceNumberUcr</referenceNumberUCR>
      |      <LocationOfGoods>
      |        <qualifierOfIdentification>q</qualifierOfIdentification>
      |      </LocationOfGoods>
      |    </Consignment>
      |  </GoodsShipment>
      |  <updatedAt>2026-08-11T00:00:00</updatedAt>
      |</Submission>""".stripMargin

  "ViewSubmissionController" - {

    "must return OK and display the submission when the submission exists" in {

      val mockAuthConnector = mock[uk.gov.hmrc.auth.core.AuthConnector]
      val mockAutomatedExportSystemConnector = mock[AutomatedExportSystemConnector]

      val mockSessionRepository = mock[SessionRepository]
      val enrolmentIdentifier =
        uk.gov.hmrc.auth.core.EnrolmentIdentifier("EORINumber", "some-eori")

      val enrolments =
        Enrolments(Set(uk.gov.hmrc.auth.core.Enrolment("HMRC-CUS-ORG", Seq(enrolmentIdentifier), "active")))

      when(mockAuthConnector.authorise[Option[Credentials] ~ Option[String] ~ Enrolments](any(), any())(any(), any()))
        .thenReturn(Future.successful(new ~(new ~(Some(Credentials(testAuthorityId, "government-gateway")), Some(testGroupId)), enrolments)))

      when(mockAutomatedExportSystemConnector.getSingleSubmission(any[String])(any()))
        .thenReturn(Future.successful(SingleSubmissionResponseParser.parse(scala.xml.XML.loadString(xml))))
      when(mockSessionRepository.set(any())).thenReturn(Future.successful(true))

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(
          bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector),
          bind[AutomatedExportSystemConnector].toInstance(mockAutomatedExportSystemConnector),
          bind[SessionRepository].toInstance(mockSessionRepository)
        )
        .build()

      running(application) {
        val request = FakeRequest(GET, routes.ViewSubmissionController.onPageLoad("12345").url)
          .withSession(SessionKeys.sessionId -> "some-session-id")

        val result = route(application, request).value

        status(result) shouldBe OK
        val body = contentAsString(result)
        body should include("mrn12345")
        body should include("referenceNumberUcr")
      }
    }

    "must return NOT_FOUND when the submission does not exist" in {

      val mockAuthConnector = mock[uk.gov.hmrc.auth.core.AuthConnector]
      val mockAutomatedExportSystemConnector = mock[AutomatedExportSystemConnector]

      val enrolmentIdentifier =
        uk.gov.hmrc.auth.core.EnrolmentIdentifier("EORINumber", "some-eori")

      val enrolments =
        Enrolments(Set(uk.gov.hmrc.auth.core.Enrolment("HMRC-CUS-ORG", Seq(enrolmentIdentifier), "active")))

      when(mockAuthConnector.authorise[Option[Credentials] ~ Option[String] ~ Enrolments](any(), any())(any(), any()))
        .thenReturn(Future.successful(new ~(new ~(Some(Credentials(testAuthorityId, "government-gateway")), Some(testGroupId)), enrolments)))

      when(mockAutomatedExportSystemConnector.getSingleSubmission(any[String])(any()))
        .thenReturn(Future.failed(UpstreamErrorResponse("not found", NOT_FOUND)))

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(
          bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector),
          bind[AutomatedExportSystemConnector].toInstance(mockAutomatedExportSystemConnector)
        )
        .build()

      running(application) {

        val request =
          FakeRequest(
            GET,
            routes.ViewSubmissionController
              .onPageLoad(UUID.randomUUID().toString)
              .url
          ).withSession(SessionKeys.sessionId -> "some-session-id")

        val result = route(application, request).value

        status(result) shouldBe NOT_FOUND
      }
    }
  }
}
