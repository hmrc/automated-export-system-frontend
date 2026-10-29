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
import org.mockito.Mockito.{never, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import play.api.data.Form
import play.api.inject.bind
import play.api.libs.json.Json
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.create.routes as createRoute
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.problem.routes as problemRoute
import uk.gov.hmrc.automatedexportsystemfrontend.forms.create.DiscrepancyTransportFormProvider
import uk.gov.hmrc.automatedexportsystemfrontend.helpers.SpecBase
import uk.gov.hmrc.automatedexportsystemfrontend.models.{ContainerDetails, NormalMode, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.navigation.{CreateNavigator, FakeCreateNavigator}
import uk.gov.hmrc.automatedexportsystemfrontend.pages.create.DiscrepancyTransportPage
import uk.gov.hmrc.automatedexportsystemfrontend.repositories.SessionRepository

import scala.concurrent.Future

class DiscrepancyTransportControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new DiscrepancyTransportFormProvider()
  val form: Form[ContainerDetails] = formProvider()

  lazy val discrepancyTransportRoute: String = createRoute.DiscrepancyTransportController.onPageLoad(NormalMode).url

  val userAnswers =
    UserAnswers(
      userAnswersId,
      Json.obj(
        "standard" ->
          Json.obj(DiscrepancyTransportPage.toString -> Json.obj("containerId" -> "value 1", "numberOfSeals" -> 99))
      )
    )

  "DiscrepancyTransport Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
        .build()

      running(application) {
        val request = FakeRequest(GET, discrepancyTransportRoute)

        val result = route(application, request).value

        status(result) shouldBe OK

        val body = contentAsString(result)
        body should include("Container details")
        body should include("If the goods are being transported in a container, enter the details.")
        body should include("Container identification number")
        body should include("Number of seals")
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
        .build()

      running(application) {
        val request = FakeRequest(GET, discrepancyTransportRoute)
        val result = route(application, request).value

        status(result) shouldBe OK

        val body = contentAsString(result)
        body should include("Container details")
        body should include("If the goods are being transported in a container, enter the details.")
        body should include("Container identification number")
        body should include("""id="containerId"""")
        body should include("""name="containerId"""")
        body should include("""type="text"""")
        body should include("""value="value 1"""")
        body should include("Number of seals")
        body should include("""id="numberOfSeals"""")
        body should include("""name="numberOfSeals"""")
        body should include("""type="text"""")
        body should include("""value="99"""")
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
          FakeRequest(POST, discrepancyTransportRoute)
            .withFormUrlEncodedBody(("containerId", "CONT123"), ("numberOfSeals", "99"))

        val result = route(application, request).value

        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe onwardRoute.url
      }
    }

    "must redirect to the next page when both fields are blank" in {
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
          FakeRequest(POST, discrepancyTransportRoute)
            .withFormUrlEncodedBody(("containerId", ""), ("numberOfSeals", ""))

        val result = route(application, request).value

        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe onwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
        .build()

      running(application) {
        val request =
          FakeRequest(POST, discrepancyTransportRoute)
            .withFormUrlEncodedBody(("containerId", "abc123 "), ("numberOfSeals", "99"))

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
        val request = FakeRequest(GET, discrepancyTransportRoute)

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
          FakeRequest(POST, discrepancyTransportRoute)
            .withFormUrlEncodedBody(("containerId", "value 1"), ("numberOfSeals", "99"))

        val result = route(application, request).value

        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe problemRoute.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must add transport equipment at the current index without overwriting a previous entry" in {

      val firstTransportEquipment =
        ContainerDetails(Some("CONT001"), Some(1))

      val secondTransportEquipment =
        ContainerDetails(Some("CONT002"), Some(2))

      val answersWithFirstTransportEquipment =
        emptyUserAnswers
          .set(DiscrepancyTransportPage(1), firstTransportEquipment)
          .success
          .value

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(answersWithFirstTransportEquipment))
          .overrides(
            bind[CreateNavigator].toInstance(new FakeCreateNavigator(onwardRoute)),
            bind[SessionRepository].toInstance(mockSessionRepository),
            bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector)
          )
          .build()

      running(application) {

        val pageRoute =
          createRoute.DiscrepancyTransportController
            .onPageLoad(2, NormalMode)
            .url

        val request =
          FakeRequest(POST, pageRoute)
            .withFormUrlEncodedBody("containerId" -> "CONT002", "numberOfSeals" -> "2")

        val result = route(application, request).value

        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe onwardRoute.url

        val userAnswersCaptor =
          ArgumentCaptor.forClass(classOf[uk.gov.hmrc.automatedexportsystemfrontend.models.UserAnswers])

        verify(mockSessionRepository).set(userAnswersCaptor.capture())

        val savedAnswers = userAnswersCaptor.getValue

        savedAnswers
          .get(DiscrepancyTransportPage(1)) shouldBe Some(firstTransportEquipment)

        savedAnswers
          .get(DiscrepancyTransportPage(2)) shouldBe Some(secondTransportEquipment)
      }
    }

    "must populate the correct transport equipment when loading a later index" in {

      val answers =
        emptyUserAnswers
          .set(DiscrepancyTransportPage(1), ContainerDetails(Some("CONT001"), Some(1)))
          .success
          .value
          .set(DiscrepancyTransportPage(2), ContainerDetails(Some("CONT002"), Some(2)))
          .success
          .value

      val application =
        applicationBuilder(userAnswers = Some(answers))
          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
          .build()

      running(application) {

        val pageRoute =
          createRoute.DiscrepancyTransportController
            .onPageLoad(2, NormalMode)
            .url

        val request = FakeRequest(GET, pageRoute)

        val result = route(application, request).value

        status(result) shouldBe OK

        val body = contentAsString(result)

        body should include("""value="CONT002"""")
        body should include("""value="2"""")

        body should not include """value="CONT001""""
      }
    }

    "must update the transport equipment at the supplied index without changing other entries" in {

      val firstTransportEquipment =
        ContainerDetails(Some("CONT001"), Some(1))

      val secondTransportEquipment =
        ContainerDetails(Some("CONT002"), Some(2))

      val updatedTransportEquipment =
        ContainerDetails(Some("UPDATED"), Some(3))

      val answers =
        emptyUserAnswers
          .set(DiscrepancyTransportPage(1), firstTransportEquipment)
          .success
          .value
          .set(DiscrepancyTransportPage(2), secondTransportEquipment)
          .success
          .value

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(answers))
          .overrides(
            bind[CreateNavigator].toInstance(new FakeCreateNavigator(onwardRoute)),
            bind[SessionRepository].toInstance(mockSessionRepository),
            bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector)
          )
          .build()

      running(application) {

        val pageRoute =
          createRoute.DiscrepancyTransportController
            .onPageLoad(2, NormalMode)
            .url

        val request =
          FakeRequest(POST, pageRoute)
            .withFormUrlEncodedBody("containerId" -> "UPDATED", "numberOfSeals" -> "3")

        val result = route(application, request).value

        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe onwardRoute.url

        val userAnswersCaptor =
          ArgumentCaptor.forClass(classOf[uk.gov.hmrc.automatedexportsystemfrontend.models.UserAnswers])

        verify(mockSessionRepository).set(userAnswersCaptor.capture())

        val savedAnswers = userAnswersCaptor.getValue

        savedAnswers
          .get(DiscrepancyTransportPage(1)) shouldBe Some(firstTransportEquipment)

        savedAnswers
          .get(DiscrepancyTransportPage(2)) shouldBe Some(updatedTransportEquipment)
      }
    }

    "must redirect to the existing Transport Equipment list when blank details are submitted and Transport Equipment already exists" in {

      val existingTransportEquipment =
        ContainerDetails(Some("CONT001"), Some(1))

      val answersWithExistingTransportEquipment =
        emptyUserAnswers
          .set(DiscrepancyTransportPage(1), existingTransportEquipment)
          .success
          .value

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(answersWithExistingTransportEquipment))
          .overrides(
            bind[SessionRepository].toInstance(mockSessionRepository),
            bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector)
          )
          .build()

      running(application) {

        val pageRoute =
          createRoute.DiscrepancyTransportController
            .onPageLoad(2, NormalMode)
            .url

        val request =
          FakeRequest(POST, pageRoute)
            .withFormUrlEncodedBody("containerId" -> "", "numberOfSeals" -> "")

        val result = route(application, request).value

        status(result) shouldBe SEE_OTHER

        redirectLocation(result).value shouldBe
          createRoute.AddAnotherTransportController
            .onPageLoad(None, NormalMode)
            .url
      }
    }

    "must redirect to the Declarations Transport Equipment page when blank details are submitted and no Transport Equipment exists" in {

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[SessionRepository].toInstance(mockSessionRepository),
            bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector)
          )
          .build()

      running(application) {

        val pageRoute =
          createRoute.DiscrepancyTransportController
            .onPageLoad(1, NormalMode)
            .url

        val request =
          FakeRequest(POST, pageRoute)
            .withFormUrlEncodedBody("containerId" -> "", "numberOfSeals" -> "")

        val result = route(application, request).value

        status(result) shouldBe SEE_OTHER

        redirectLocation(result).value shouldBe
          createRoute.DiscrepancySealsController
            .onPageLoad(1, NormalMode)
            .url
      }
    }
  }
}
