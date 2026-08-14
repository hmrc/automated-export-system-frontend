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

package uk.gov.hmrc.automatedexportsystemfrontend.controllers.unhappyPath

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.data.Form
import play.api.inject.bind
import play.api.libs.json.Json
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.problem.routes as problemRoute
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.unhappyPath.routes as unhappyRoute
import uk.gov.hmrc.automatedexportsystemfrontend.forms.unhappyPath.DiscrepancyTransportFormProvider
import uk.gov.hmrc.automatedexportsystemfrontend.helpers.SpecBase
import uk.gov.hmrc.automatedexportsystemfrontend.models.{ContainerDetails, NormalMode, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.navigation.{FakeUnhappyPathNavigator, UnhappyPathNavigator}
import uk.gov.hmrc.automatedexportsystemfrontend.pages.unhappyPath.DiscrepancyTransportPage
import uk.gov.hmrc.automatedexportsystemfrontend.repositories.SessionRepository
import uk.gov.hmrc.automatedexportsystemfrontend.views.html.unhappyPath.DiscrepancyTransportView

import scala.concurrent.Future

class DiscrepancyTransportControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new DiscrepancyTransportFormProvider()
  val form: Form[ContainerDetails] = formProvider()

  lazy val discrepancyTransportRoute: String = unhappyRoute.DiscrepancyTransportController.onPageLoad(NormalMode).url

  val userAnswers =
    UserAnswers(userAnswersId, Json.obj(DiscrepancyTransportPage.toString -> Json.obj("containerId" -> "value 1", "numberOfSeals" -> 99)))

  "DiscrepancyTransport Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
        .build()

      running(application) {
        val request = FakeRequest(GET, discrepancyTransportRoute)

        val view = application.injector.instanceOf[DiscrepancyTransportView]

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

        val view = application.injector.instanceOf[DiscrepancyTransportView]

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
            bind[UnhappyPathNavigator].toInstance(new FakeUnhappyPathNavigator(onwardRoute)),
            bind[SessionRepository].toInstance(mockSessionRepository),
            bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, discrepancyTransportRoute)
            .withFormUrlEncodedBody(("containerId", "value 1"), ("numberOfSeals", "99"))

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
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm: Form[ContainerDetails] = form.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[DiscrepancyTransportView]

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
  }
}
