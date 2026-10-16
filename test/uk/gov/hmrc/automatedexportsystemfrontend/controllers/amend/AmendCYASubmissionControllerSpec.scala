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

package uk.gov.hmrc.automatedexportsystemfrontend.controllers.amend

import org.mockito.Mockito.when
import org.mockito.ArgumentMatchers.{any, eq as eqTo}
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.auth.core.{EnrolmentIdentifier, Enrolments}
import uk.gov.hmrc.auth.core.retrieve.{~, Credentials}
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.amend.routes as amendRoute
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.problem.routes as problemRoute
import uk.gov.hmrc.automatedexportsystemfrontend.helpers.SpecBase
import uk.gov.hmrc.automatedexportsystemfrontend.helpers.TestFixture.{testAuthorityId, testGroupId}
import uk.gov.hmrc.automatedexportsystemfrontend.models.{
  CheckMode,
  LocationDetails,
  LocationQualifier,
  LocationType,
  NormalMode,
  OfficeOfExit,
  PartOfConsolidationAnswer
}
import uk.gov.hmrc.automatedexportsystemfrontend.pages.amend.*
import uk.gov.hmrc.http.SessionKeys
import org.apache.pekko.Done
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.{never, verify}
import uk.gov.hmrc.automatedexportsystemfrontend.connectors.AutomatedExportSystemConnector
import uk.gov.hmrc.automatedexportsystemfrontend.models.UserAnswers
import uk.gov.hmrc.automatedexportsystemfrontend.repositories.SessionRepository
import uk.gov.hmrc.automatedexportsystemfrontend.services.AmendSubmissionDataService
import uk.gov.hmrc.automatedexportsystemfrontend.utils.UserAnswerHelper

import scala.concurrent.Future

class AmendCYASubmissionControllerSpec extends SpecBase {

  "AmendCYASubmissionController" - {

    "must return OK and render CYA page for a GET" in {
      val mockAuthConnector = mock[uk.gov.hmrc.auth.core.AuthConnector]
      val enrolmentIdentifier = EnrolmentIdentifier("EORINumber", "some-eori")
      val enrolments = Enrolments(Set(uk.gov.hmrc.auth.core.Enrolment("HMRC-CUS-ORG", Seq(enrolmentIdentifier), "active")))

      when(mockAuthConnector.authorise[Option[Credentials] ~ Option[String] ~ Enrolments](any(), any())(any(), any()))
        .thenReturn(Future.successful(new ~(new ~(Some(Credentials(testAuthorityId, "government-gateway")), Some(testGroupId)), enrolments)))

      val submissionId = "submissionId"

      val userAnswers = emptyUserAnswers
        .set(AmendEnterMrnPage(submissionId), "MRN")
        .get
        .set(AmendIsSplitExitPage(submissionId), false)
        .get
        .set(AmendEnterDucrPage(submissionId), "DUCR")
        .get
        .set(AmendPartOfConsolidationPage(submissionId), PartOfConsolidationAnswer(boolean = true, mucr = Some("123")))
        .get
        .set(AmendOfficeOfExitPage(submissionId), OfficeOfExit.Belfast)
        .get
        .set(AmendAnyDiscrepanciesPage(submissionId), false)
        .get
        .set(AmendLocationTypePage(submissionId), LocationType.AuthorisedPlace)
        .get
        .set(
          AmendLocationIdPage(submissionId),
          LocationDetails(
            locationType = LocationQualifier.AuthorisationNumber,
            unlocode = None,
            locationAdditionalIdentifier = Some("ABCD"),
            authorisationReferenceNumber = Some("GB/SSE/2024/00471")
          )
        )
        .get

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
        .build()

      running(application) {
        val request = FakeRequest(GET, amendRoute.AmendCYASubmissionController.onPageLoad(NormalMode, submissionId).url)
          .withSession(SessionKeys.sessionId -> "some-session-id")

        val result = route(application, request).value

        status(result) shouldBe OK
        val body = contentAsString(result)
        val document = Jsoup.parse(body)

        def summaryRow(label: String): Element = {
          val rows = document.select(".govuk-summary-list__row")
          val matchingRow = (0 until rows.size()).map(rows.get).find { row =>
            row.select(".govuk-summary-list__key").text() == label
          }

          withClue(s"Missing summary row: $label") {
            matchingRow shouldBe defined
          }

          matchingRow.get
        }

        def assertChangeLink(label: String, expectedUrl: String): Unit = {
          val links = summaryRow(label).select(".govuk-summary-list__actions a")
          links.size() shouldBe 1
          links.first().attr("href") shouldBe expectedUrl
        }

        document.select("h1").text() shouldBe "Review and amend your submission"
        document.select(".govuk-caption-l").text() shouldBe s"Amending $submissionId"

        summaryRow("MRN").select(".govuk-summary-list__value").text() shouldBe
          "MRN Cannot be amended"
        summaryRow("MRN").select("a").isEmpty shouldBe true

        summaryRow("DUCR").select(".govuk-summary-list__value").text() shouldBe
          "DUCR Cannot be amended"
        summaryRow("DUCR").select("a").isEmpty shouldBe true

        summaryRow("Discrepancies").select(".govuk-summary-list__value").text() shouldBe "No"
        summaryRow("Split exit").select(".govuk-summary-list__value").text() shouldBe "No"
        summaryRow("MUCR").select(".govuk-summary-list__value").text() shouldBe "123"

        summaryRow("Office of exit").select(".govuk-summary-list__value").text() should not be empty
        summaryRow("Type of location").select(".govuk-summary-list__value").text() shouldBe
          "Authorised place"

        val identifiedBy = summaryRow("Identified by")
          .select(".govuk-summary-list__value")
          .text()

        identifiedBy should include("GB/SSE/2024/00471")

        summaryRow("Reference number").select(".govuk-summary-list__value").text() shouldBe
          "GB/SSE/2024/00471"

        summaryRow("Authorisation type").select(".govuk-summary-list__value").text() shouldBe ""
        summaryRow("Authorisation type").select("a").isEmpty shouldBe true

        assertChangeLink("Discrepancies", amendRoute.AmendAnyDiscrepanciesController.onPageLoad(CheckMode, submissionId).url)
        assertChangeLink("Split exit", amendRoute.AmendIsSplitExitController.onPageLoad(CheckMode, submissionId).url)
        assertChangeLink("MUCR", amendRoute.AmendPartOfConsolidationController.onPageLoad(CheckMode, submissionId).url)
        assertChangeLink("Office of exit", amendRoute.AmendOfficeOfExitController.onPageLoad(CheckMode, submissionId).url)
        assertChangeLink("Type of location", amendRoute.AmendLocationTypeController.onPageLoad(CheckMode, submissionId).url)
        assertChangeLink("Identified by", amendRoute.AmendLocationIdController.onPageLoad(CheckMode, submissionId).url)
        assertChangeLink("Reference number", amendRoute.AmendLocationIdController.onPageLoad(CheckMode, submissionId).url)

        val discrepanciesLabel = messages(application)("anyDiscrepancies.checkYourAnswersLabel")
        summaryRow(discrepanciesLabel).select(".govuk-summary-list__value").text() shouldBe "No"

        document.select("form").attr("action") shouldBe
          amendRoute.AmendCYASubmissionController.onSubmit(NormalMode, submissionId).url
      }
    }
    "must redirect to Journey Recovery for a GET if no existing data is found" in {
      val mockAuthConnector = mock[uk.gov.hmrc.auth.core.AuthConnector]
      val enrolmentIdentifier = EnrolmentIdentifier("EORINumber", "some-eori")
      val enrolments = Enrolments(Set(uk.gov.hmrc.auth.core.Enrolment("HMRC-CUS-ORG", Seq(enrolmentIdentifier), "active")))

      when(mockAuthConnector.authorise[Option[Credentials] ~ Option[String] ~ Enrolments](any(), any())(any(), any()))
        .thenReturn(Future.successful(new ~(new ~(Some(Credentials(testAuthorityId, "government-gateway")), Some(testGroupId)), enrolments)))

      val application = applicationBuilder(userAnswers = None)
        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
        .build()

      running(application) {
        val request = FakeRequest(GET, amendRoute.AmendCYASubmissionController.onPageLoad(NormalMode, "submissionId").url)
          .withSession(SessionKeys.sessionId -> "some-session-id")

        val result = route(application, request).value
        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe
          problemRoute.JourneyRecoveryController.onPageLoad().url
      }
    }

    "onSubmit" - {

      val submissionId = "submissionId"
      val xml = "<Submission>amendment</Submission>"

      Seq("success", "backend failure", "missing answers").foreach { scenario =>
        s"must handle $scenario when submitting an amendment" in {
          val connector = mock[AutomatedExportSystemConnector]
          val service = mock[AmendSubmissionDataService]
          val repository = mock[SessionRepository]

          val answers = emptyUserAnswers
            .set(AmendEnterMrnPage(submissionId), "24GB12345678901234")
            .get

          when(service.buildAmendSubmission(any[UserAnswers](), any[String]()))
            .thenReturn(if (scenario == "missing answers") None else Some(xml))

          val backendResult: Future[Done] =
            if (scenario == "backend failure")
              Future.failed(new RuntimeException("Backend unavailable"))
            else
              Future.successful(Done)

          when(connector.submitIE507a(any[String]())(any()))
            .thenReturn(backendResult)

          when(repository.set(any[UserAnswers]()))
            .thenReturn(Future.successful(true))

          val application = applicationBuilder(userAnswers = Some(answers))
            .overrides(
              bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector),
              bind[AutomatedExportSystemConnector].toInstance(connector),
              bind[AmendSubmissionDataService].toInstance(service),
              bind[SessionRepository].toInstance(repository)
            )
            .build()

          running(application) {
            val request = FakeRequest(POST, amendRoute.AmendCYASubmissionController.onSubmit(NormalMode, submissionId).url)
              .withSession(SessionKeys.sessionId -> "some-session-id")

            val result = route(application, request).value

            status(result) shouldBe SEE_OTHER

            val expectedUrl =
              if (scenario == "success")
                amendRoute.AmendSubmissionConfirmationController.onPageLoad(submissionId).url
              else
                problemRoute.JourneyRecoveryController.onPageLoad().url

            redirectLocation(result).value shouldBe expectedUrl

            if (scenario == "missing answers")
              verify(connector, never()).submitIE507a(any[String]())(any())
            else
              verify(connector).submitIE507a(eqTo(xml))(any())

            val captor = ArgumentCaptor.forClass(classOf[UserAnswers])
            verify(repository).set(captor.capture())

            val expectedAnswers =
              new UserAnswerHelper().removeAmendSubmissionAnswers(submissionId, answers)

            captor.getValue.id shouldBe expectedAnswers.id
            captor.getValue.data shouldBe expectedAnswers.data
          }
        }
      }
    }
  }
}
