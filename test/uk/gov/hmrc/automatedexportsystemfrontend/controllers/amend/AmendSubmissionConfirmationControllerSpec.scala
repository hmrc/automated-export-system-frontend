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

import org.jsoup.Jsoup
import org.mockito.ArgumentMatchers.{any, eq as eqTo}
import org.mockito.Mockito.when
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.automatedexportsystemfrontend.connectors.AutomatedExportSystemConnector
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.amend.routes as amendRoute
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.problem.routes as problemRoute
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.submission.routes as submissionRoute
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.routes as mainRoute
import uk.gov.hmrc.automatedexportsystemfrontend.helpers.SpecBase
import uk.gov.hmrc.automatedexportsystemfrontend.models.{
  SingleSubmissionCustomsOfficeOfExitActual,
  SingleSubmissionExportOperation,
  SingleSubmissionResponse
}
import uk.gov.hmrc.http.{SessionKeys, UpstreamErrorResponse}

import java.time.LocalDateTime
import scala.concurrent.Future

class AmendSubmissionConfirmationControllerSpec extends SpecBase {

  private val submissionId = "submissionId"
  private val mrn = "24GB12345678901234"

  private val submission = SingleSubmissionResponse(
    submissionId = submissionId,
    status = 1,
    exportOperation = SingleSubmissionExportOperation("1", mrn, 0, 0),
    customsOfficeOfExitActual = SingleSubmissionCustomsOfficeOfExitActual("GB000051"),
    goodsShipment = None,
    updatedAt = LocalDateTime.of(2026, 10, 12, 10, 30),
    metadata = None
  )

  "AmendSubmissionConfirmationController" - {

    "must display the original MRN, confirmation content and return links without amendment answers" in {
      val connector = mock[AutomatedExportSystemConnector]

      when(connector.getSingleSubmission(eqTo(submissionId))(any()))
        .thenReturn(Future.successful(submission))

      val application = applicationBuilder(userAnswers = None)
        .overrides(
          bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector),
          bind[AutomatedExportSystemConnector].toInstance(connector)
        )
        .build()

      running(application) {
        val request = FakeRequest(GET, amendRoute.AmendSubmissionConfirmationController.onPageLoad(submissionId).url)
          .withSession(SessionKeys.sessionId -> "some-session-id")

        val result = route(application, request).value

        status(result) shouldBe OK

        val document = Jsoup.parse(contentAsString(result))

        document.select(".govuk-panel__title").text() shouldBe "Amendment submitted"
        document.select(".govuk-panel__body").text() should include("Original MRN")
        document.select(".govuk-panel__body").text() should include(mrn)
        document.text() should include("Your amendment has been sent to the Customs Declaration Service for revalidation.")
        document.select(".govuk-inset-text").text() should include("If your amendment is rejected")

        val links = document.select("a")

        def linkTo(label: String): String =
          (0 until links.size())
            .map(links.get)
            .find(_.text() == label)
            .value
            .attr("href")

        linkTo("Return to your submissions") shouldBe
          submissionRoute.ViewSubmissionsController.onPageLoad().url

        linkTo("Return to start") shouldBe
          mainRoute.IndexController.onPageLoad().url
      }
    }

    "must return NOT_FOUND when the backend cannot find the submission" in {
      val connector = mock[AutomatedExportSystemConnector]

      when(connector.getSingleSubmission(eqTo(submissionId))(any()))
        .thenReturn(Future.failed(UpstreamErrorResponse("Not found", NOT_FOUND)))

      val application = applicationBuilder(userAnswers = None)
        .overrides(
          bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector),
          bind[AutomatedExportSystemConnector].toInstance(connector)
        )
        .build()

      running(application) {
        val request = FakeRequest(GET, amendRoute.AmendSubmissionConfirmationController.onPageLoad(submissionId).url)
          .withSession(SessionKeys.sessionId -> "some-session-id")

        status(route(application, request).value) shouldBe NOT_FOUND
      }
    }

    "must redirect to Journey Recovery when retrieval fails" in {
      val connector = mock[AutomatedExportSystemConnector]

      when(connector.getSingleSubmission(eqTo(submissionId))(any()))
        .thenReturn(Future.failed(UpstreamErrorResponse("Backend failure", INTERNAL_SERVER_ERROR)))

      val application = applicationBuilder(userAnswers = None)
        .overrides(
          bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector),
          bind[AutomatedExportSystemConnector].toInstance(connector)
        )
        .build()

      running(application) {
        val request = FakeRequest(GET, amendRoute.AmendSubmissionConfirmationController.onPageLoad(submissionId).url)
          .withSession(SessionKeys.sessionId -> "some-session-id")

        val result = route(application, request).value

        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe
          problemRoute.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
