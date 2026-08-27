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

import org.mockito.Mockito.reset
import org.apache.pekko.Done
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{never, verify, when}
import play.api.inject.bind
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.ArgumentCaptor
import play.api.test.FakeRequest
import play.api.test.Helpers.GET
import uk.gov.hmrc.automatedexportsystemfrontend.connectors.AutomatedExportSystemConnector
import uk.gov.hmrc.automatedexportsystemfrontend.helpers.SpecBase
import uk.gov.hmrc.automatedexportsystemfrontend.services.SubmissionDataService
import play.api.test.Helpers.*
import uk.gov.hmrc.automatedexportsystemfrontend.models.UserAnswers
import uk.gov.hmrc.automatedexportsystemfrontend.pages.create.IsSplitExitPage
import uk.gov.hmrc.automatedexportsystemfrontend.repositories.SessionRepository
import uk.gov.hmrc.http.UpstreamErrorResponse

import scala.concurrent.Future

class SubmissionControllerSpec extends SpecBase with MockitoSugar {

  private val mockAutomatedExportSystemConnector = mock[AutomatedExportSystemConnector]
  private val mockSubmissionDataService = mock[SubmissionDataService]
  private val mockSessionRepository = mock[SessionRepository]
  private val userAnswersCaptor = ArgumentCaptor.forClass(classOf[UserAnswers])

  val answers = emptyUserAnswers.set(IsSplitExitPage, false).get

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockAutomatedExportSystemConnector, mockSubmissionDataService, mockSessionRepository)
  }

  "standardSubmit" - {

    "redirect to the confirmation page when submission succeeds and cleanup user answers" in {

      when(mockSubmissionDataService.buildStandardSubmission(any()))
        .thenReturn(Some("<xml/>"))

      when(mockAutomatedExportSystemConnector.submitIE507a(any())(any()))
        .thenReturn(Future.successful(Done))

      when(mockSessionRepository.set(userAnswersCaptor.capture())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(answers))
          .overrides(
            bind[SessionRepository].toInstance(mockSessionRepository),
            bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector),
            bind[SubmissionDataService].toInstance(mockSubmissionDataService),
            bind[AutomatedExportSystemConnector].toInstance(mockAutomatedExportSystemConnector)
          )
          .build()

      running(application) {

        val request =
          FakeRequest(GET, routes.SubmissionController.standardSubmit.url)

        val result = route(application, request).value

        status(result) shouldBe SEE_OTHER
        val capturedAnswers = userAnswersCaptor.getValue
        capturedAnswers.get(IsSplitExitPage) shouldBe None
        redirectLocation(result).value shouldBe
          routes.StandardSubmissionConfirmationController.onPageLoad().url
      }
    }

    "redirect to journey recovery when xml generation fails and clean up user answers" in {

      when(mockSubmissionDataService.buildStandardSubmission(any()))
        .thenReturn(None)

      when(mockSessionRepository.set(userAnswersCaptor.capture())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(answers))
          .overrides(
            bind[SessionRepository].toInstance(mockSessionRepository),
            bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector),
            bind[SubmissionDataService].toInstance(mockSubmissionDataService)
          )
          .build()

      running(application) {

        val request =
          FakeRequest(GET, routes.SubmissionController.standardSubmit.url)

        val result = route(application, request).value

        status(result) shouldBe SEE_OTHER
        val capturedAnswers = userAnswersCaptor.getValue
        capturedAnswers.get(IsSplitExitPage) shouldBe None
        redirectLocation(result).value shouldBe
          uk.gov.hmrc.automatedexportsystemfrontend.controllers.problem.routes.JourneyRecoveryController.onPageLoad().url

        verify(mockAutomatedExportSystemConnector, never())
          .submitIE507a(any())(any())
      }
    }

    "redirect to journey recovery when connector submission fails and should clean up user answers" in {

      when(mockSubmissionDataService.buildStandardSubmission(any()))
        .thenReturn(Some("<xml/>"))

      when(mockAutomatedExportSystemConnector.submitIE507a(any())(any()))
        .thenReturn(Future.failed(UpstreamErrorResponse("boom", 400)))

      when(mockSessionRepository.set(any()))
        .thenReturn(Future.successful(true))

      val application =
        applicationBuilder(userAnswers = Some(answers))
          .overrides(
            bind[SessionRepository].toInstance(mockSessionRepository),
            bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector),
            bind[SubmissionDataService].toInstance(mockSubmissionDataService),
            bind[AutomatedExportSystemConnector].toInstance(mockAutomatedExportSystemConnector)
          )
          .build()

      running(application) {
        val request = FakeRequest(GET, routes.SubmissionController.standardSubmit.url)
        val result = route(application, request).value

        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe
          uk.gov.hmrc.automatedexportsystemfrontend.controllers.problem.routes.JourneyRecoveryController.onPageLoad().url

        verify(mockSessionRepository).set(any())
      }
    }
  }
}
