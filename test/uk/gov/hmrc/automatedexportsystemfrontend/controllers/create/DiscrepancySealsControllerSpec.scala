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

import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{verify, when}
import org.scalatestplus.mockito.MockitoSugar
import play.api.data.Form
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.create.routes as createRoute
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.problem.routes as problemRoute
import uk.gov.hmrc.automatedexportsystemfrontend.forms.create.DiscrepancySealsFormProvider
import uk.gov.hmrc.automatedexportsystemfrontend.helpers.SpecBase
import uk.gov.hmrc.automatedexportsystemfrontend.models.{CheckMode, NormalMode, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.navigation.{CreateNavigator, FakeCreateNavigator}
import uk.gov.hmrc.automatedexportsystemfrontend.pages.create.DiscrepancySealsPage
import uk.gov.hmrc.automatedexportsystemfrontend.queries.DiscrepancySeals
import uk.gov.hmrc.automatedexportsystemfrontend.repositories.SessionRepository

import scala.concurrent.Future

class DiscrepancySealsControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new DiscrepancySealsFormProvider()
  val form: Form[Option[String]] = formProvider()

  lazy val discrepancySealsRoute: String = createRoute.DiscrepancySealsController.onPageLoad(1, NormalMode).url

  val sealIdentifier = "testSealIdentifier"

  val userAnswers = UserAnswers(userAnswersId).set(DiscrepancySealsPage(1), sealIdentifier).success.value

  "DiscrepancySeals Controller" - {

    "must return OK and the correct view for a GET at the next index" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
        .build()

      running(application) {
        val request = FakeRequest(GET, discrepancySealsRoute)

        val result = route(application, request).value

        status(result) shouldBe OK

        val body = contentAsString(result)
        body should include("What is the seal identifier?")
        body should include("For example, GB12345678.")
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered at an existing index" in {

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
        .build()

      running(application) {
        val request = FakeRequest(GET, discrepancySealsRoute)

        val result = route(application, request).value

        status(result) shouldBe OK

        val body = contentAsString(result)
        body should include("What is the seal identifier?")
        body should include("For example, GB12345678.")
        body should include("""id="value"""")
        body should include("""name="value"""")
        body should include("""type="text"""")
        body should include("""value="testSealIdentifier"""")
      }
    }

    "must redirect to the next page when valid data is submitted" in {

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[CreateNavigator].toInstance(new FakeCreateNavigator(onwardRoute)),
            bind[SessionRepository].toInstance(mockSessionRepository),
            bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, discrepancySealsRoute)
            .withFormUrlEncodedBody(("value", "answer"))

        val result = route(application, request).value

        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe onwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {
      val invalidSealId = " invalidSealId"
      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
        .build()

      running(application) {
        val request =
          FakeRequest(POST, discrepancySealsRoute)
            .withFormUrlEncodedBody(("value", invalidSealId))

        val boundForm: Form[Option[String]] = form.bind(Map("value" -> invalidSealId))

        val result = route(application, request).value

        status(result) shouldBe BAD_REQUEST

        val body = contentAsString(result)
        body should include("There is a problem")
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None)
        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
        .build()

      running(application) {
        val request = FakeRequest(GET, discrepancySealsRoute)

        val result = route(application, request).value

        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe problemRoute.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a GET where the index doesn't exist and is not the next index" in {

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
        .build()

      running(application) {
        val request = FakeRequest(GET, createRoute.DiscrepancySealsController.onPageLoad(5, NormalMode).url)

        val result = route(application, request).value

        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe problemRoute.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST where the index doesn't exist and is not the next index" in {

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
        .build()

      running(application) {
        val request = FakeRequest(POST, createRoute.DiscrepancySealsController.onPageLoad(5, NormalMode).url)

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
          FakeRequest(POST, discrepancySealsRoute)
            .withFormUrlEncodedBody(("value", "answer"))

        val result = route(application, request).value

        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe problemRoute.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must remove the seal and redirect to the seals list when no data is entered and other seals remain" in {
      val userAnswers = UserAnswers(userAnswersId).set(DiscrepancySeals, List("seal1", "seal2")).success.value

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[CreateNavigator].toInstance(new FakeCreateNavigator(onwardRoute)),
            bind[SessionRepository].toInstance(mockSessionRepository),
            bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, createRoute.DiscrepancySealsController.onSubmit(1, NormalMode).url)
            .withFormUrlEncodedBody(("value", ""))

        val result = route(application, request).value

        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe createRoute.AddAnotherSealController.onPageLoad(None, NormalMode).url

        val captor = ArgumentCaptor.forClass(classOf[UserAnswers])
        verify(mockSessionRepository).set(captor.capture())
        DiscrepancySeals.getAll(captor.getValue) shouldBe List("seal2")
      }
    }

    "must redirect to the DiscrepancyReference when no data is entered and no seals remain in NormalMode" in {
      val userAnswers = UserAnswers(userAnswersId).set(DiscrepancySeals, List("seal1")).success.value

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[CreateNavigator].toInstance(new FakeCreateNavigator(onwardRoute)),
            bind[SessionRepository].toInstance(mockSessionRepository),
            bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, createRoute.DiscrepancySealsController.onSubmit(1, NormalMode).url)
            .withFormUrlEncodedBody(("value", ""))

        val result = route(application, request).value

        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe createRoute.DiscrepancyReferenceController.onPageLoad(1, NormalMode).url

        val captor = ArgumentCaptor.forClass(classOf[UserAnswers])
        verify(mockSessionRepository).set(captor.capture())
        DiscrepancySeals.getAll(captor.getValue) shouldBe List.empty
      }
    }

    "must redirect to the CYASubmissionController when no data is entered and no seals remain in CheckMode" in {
      val userAnswers = UserAnswers(userAnswersId).set(DiscrepancySeals, List("seal1")).success.value

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[CreateNavigator].toInstance(new FakeCreateNavigator(onwardRoute)),
            bind[SessionRepository].toInstance(mockSessionRepository),
            bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, createRoute.DiscrepancySealsController.onSubmit(1, CheckMode).url)
            .withFormUrlEncodedBody(("value", ""))

        val result = route(application, request).value

        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe createRoute.CYASubmissionController.onPageLoad().url

        val captor = ArgumentCaptor.forClass(classOf[UserAnswers])
        verify(mockSessionRepository).set(captor.capture())
        DiscrepancySeals.getAll(captor.getValue) shouldBe List.empty
      }
    }
  }
}
