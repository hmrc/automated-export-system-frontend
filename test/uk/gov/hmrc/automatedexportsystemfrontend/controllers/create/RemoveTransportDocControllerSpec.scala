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

package uk.gov.hmrc.automatedexportsystemfrontend.controllers.create

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{never, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import play.api.data.Form
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.create.routes as createRoute
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.problem.routes as problemRoute
import uk.gov.hmrc.automatedexportsystemfrontend.forms.create.RemoveTransportDocFormProvider
import uk.gov.hmrc.automatedexportsystemfrontend.helpers.SpecBase
import uk.gov.hmrc.automatedexportsystemfrontend.models.{CheckMode, DocumentDetails, NormalMode}
import uk.gov.hmrc.automatedexportsystemfrontend.navigation.{FakeNavigator, Navigator}
import uk.gov.hmrc.automatedexportsystemfrontend.pages.create.DiscrepancyTransportDocPage
import uk.gov.hmrc.automatedexportsystemfrontend.queries.DiscrepancyTransportDoc
import uk.gov.hmrc.automatedexportsystemfrontend.repositories.SessionRepository

import scala.concurrent.Future

class RemoveTransportDocControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  val docIndex = 30
  val formProvider = new RemoveTransportDocFormProvider()
  val form: Form[Boolean] = formProvider()

  val transportDocList = (1 to 50).map { number =>
    DocumentDetails(Some(number), Some(number))
  }.toList

  val userAnswers = emptyUserAnswers
    .set(DiscrepancyTransportDoc, transportDocList)
    .success
    .value

  "RemoveTransportDocController" - {

    "must return OK and the correct view for a GET" in {
      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
        .build()

      running(application) {
        val request = FakeRequest(GET, createRoute.RemoveTransportDocController.onPageLoad(docIndex, 2, NormalMode).url)

        val result = route(application, request).value

        status(result) shouldBe OK
        val body = contentAsString(result)
        body should include(s"Are you sure you want to remove transport document $docIndex")
        body should include("Yes")
        body should include("No")
      }
    }

    "must redirect to AddTransportDoc while preserving the return page and update user answers when true" in {

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
            bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector),
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, createRoute.RemoveTransportDocController.onPageLoad(docIndex, 2, NormalMode).url)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe createRoute.AddAnotherTransportDocController.onPageLoad(Some(2), NormalMode).url
        verify(mockSessionRepository).set(any())
      }
    }

    "must redirect to AddAnotherTransportDoc and update user answers when true while preserving mode in CheckMode" in {

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
            bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector),
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, createRoute.RemoveTransportDocController.onPageLoad(docIndex, 2, CheckMode).url)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe createRoute.AddAnotherTransportDocController.onPageLoad(Some(2), CheckMode).url
        verify(mockSessionRepository).set(any())
      }
    }

    "must redirect to DiscrepancyTransportDoc and update user answers when the only document type is deleted" in {
      val userAnswers = emptyUserAnswers
        .set(DiscrepancyTransportDocPage(1), DocumentDetails(Some(1), None))
        .success
        .value

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
            bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector),
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, createRoute.RemoveTransportDocController.onPageLoad(1, 1, NormalMode).url)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe createRoute.DiscrepancyTransportDocController.onPageLoad(1, NormalMode).url
        verify(mockSessionRepository).set(any())
      }
    }

    "must redirect to AddAnotherTransportDoc while preserving the return page and not update user answers when false" in {

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
            bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector),
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, createRoute.RemoveTransportDocController.onPageLoad(docIndex, 2, NormalMode).url)
            .withFormUrlEncodedBody(("value", "false"))

        val result = route(application, request).value

        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe createRoute.AddAnotherTransportDocController.onPageLoad(Some(2), NormalMode).url
        verify(mockSessionRepository, never()).set(any())
      }
    }

    "must redirect to AddAnotherTransportDoc at next valid page when true and last item is removed from the expected return page" in {

      val transportDocList = (1 to 51).map { number =>
        DocumentDetails(Some(number), Some(number))
      }.toList

      val userAnswers = emptyUserAnswers
        .set(DiscrepancyTransportDoc, transportDocList)
        .success
        .value

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
            bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector),
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, createRoute.RemoveTransportDocController.onPageLoad(51, 3, NormalMode).url)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe createRoute.AddAnotherTransportDocController.onPageLoad(Some(2), NormalMode).url
        verify(mockSessionRepository).set(any())
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
        .build()

      running(application) {
        val request =
          FakeRequest(POST, createRoute.RemoveTransportDocController.onPageLoad(docIndex, 2, NormalMode).url)
            .withFormUrlEncodedBody(("value", ""))

        val result = route(application, request).value

        status(result) shouldBe BAD_REQUEST
        val body = contentAsString(result)
        body should include("Select yes if you want to remove transport document")
      }
    }

    "must redirect to Journey Recovery for a GET if index isn't defined" in {

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
        .build()

      running(application) {
        val request = FakeRequest(GET, createRoute.RemoveTransportDocController.onPageLoad(51, 2, NormalMode).url)

        val result = route(application, request).value

        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe problemRoute.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if index isn't defined" in {

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
        .build()

      running(application) {
        val request = FakeRequest(POST, createRoute.RemoveTransportDocController.onPageLoad(51, 2, NormalMode).url)

        val result = route(application, request).value

        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe problemRoute.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None)
        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
        .build()

      running(application) {
        val request = FakeRequest(GET, createRoute.RemoveTransportDocController.onPageLoad(docIndex, 1, NormalMode).url)

        val result = route(application, request).value

        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe problemRoute.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a GET if return page isn't valid" in {

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
        .build()

      running(application) {
        val request = FakeRequest(GET, createRoute.RemoveTransportDocController.onPageLoad(50, 3, NormalMode).url)

        val result = route(application, request).value

        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe problemRoute.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None)
        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
        .build()

      running(application) {
        val request =
          FakeRequest(POST, createRoute.RemoveTransportDocController.onPageLoad(docIndex, 1, NormalMode).url)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe problemRoute.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
