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
import uk.gov.hmrc.automatedexportsystemfrontend.forms.unhappyPath.DiscrepancyTransportMeansFormProvider
import uk.gov.hmrc.automatedexportsystemfrontend.helpers.SpecBase
import uk.gov.hmrc.automatedexportsystemfrontend.models.{NormalMode, TransportAcrossBorderDetails, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.navigation.{FakeUnhappyPathNavigator, UnhappyPathNavigator}
import uk.gov.hmrc.automatedexportsystemfrontend.pages.unhappyPath.DiscrepancyTransportMeansPage
import uk.gov.hmrc.automatedexportsystemfrontend.repositories.SessionRepository
import uk.gov.hmrc.automatedexportsystemfrontend.views.html.unhappyPath.DiscrepancyTransportMeansView

import scala.concurrent.Future

class DiscrepancyTransportMeansControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new DiscrepancyTransportMeansFormProvider()
  val form: Form[TransportAcrossBorderDetails] = formProvider()

  lazy val discrepancyTransportMeansRoute: String = unhappyRoute.DiscrepancyTransportMeansController.onPageLoad(NormalMode).url

  val userAnswers = UserAnswers(
    userAnswersId,
    Json.obj(
      DiscrepancyTransportMeansPage.toString -> Json
        .obj("transportType" -> "value 1", "transportIdNumber" -> "value 2", "countryOfRegistration" -> "value 3")
    )
  )

  "DiscrepancyTransportMeans Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
        .build()

      running(application) {
        val request = FakeRequest(GET, discrepancyTransportMeansRoute)

        val view = application.injector.instanceOf[DiscrepancyTransportMeansView]

        val result = route(application, request).value

        status(result) shouldBe OK

        val body = contentAsString(result)
        body should include("Transport across the border")
        body should include("If the transport taking the goods across the border has changed, enter the new details.")
        body should include("Transport type")
        body should include("For example, 10 = IMO ship number, 40 = IATA flight number.")
        body should include("Transport ID number")
        body should include("Enter the registration or identification number that matches the transport type you selected.")
        body should include("Country of registration")
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
        .build()

      running(application) {
        val request = FakeRequest(GET, discrepancyTransportMeansRoute)

        val view = application.injector.instanceOf[DiscrepancyTransportMeansView]

        val result = route(application, request).value

        status(result) shouldBe OK

        val body = contentAsString(result)
        body should include("Transport across the border")
        body should include("If the transport taking the goods across the border has changed, enter the new details.")
        body should include("Transport type")
        body should include("For example, 10 = IMO ship number, 40 = IATA flight number.")
        body should include("""id="transportType"""")
        body should include("""name="transportType"""")
        body should include("""type="text"""")
        body should include("""value="value 1"""")
        body should include("Transport ID number")
        body should include("Enter the registration or identification number that matches the transport type you selected.")
        body should include("""id="transportIdNumber"""")
        body should include("""name="transportIdNumber"""")
        body should include("""type="text"""")
        body should include("""value="value 2"""")
        body should include("Country of registration")
        body should include("""id="countryOfRegistration"""")
        body should include("""name="countryOfRegistration"""")
        body should include("""type="text"""")
        body should include("""value="value 3"""")
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
          FakeRequest(POST, discrepancyTransportMeansRoute)
            .withFormUrlEncodedBody(("transportType", "value 1"), ("transportIdNumber", "value 2"), ("countryOfRegistration", "value 3"))

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
          FakeRequest(POST, discrepancyTransportMeansRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = form.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[DiscrepancyTransportMeansView]

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
        val request = FakeRequest(GET, discrepancyTransportMeansRoute)

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
          FakeRequest(POST, discrepancyTransportMeansRoute)
            .withFormUrlEncodedBody(("transportType", "value 1"), ("transportIdNumber", "value 2"), ("countryOfRegistration", "value 3"))

        val result = route(application, request).value

        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe problemRoute.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
