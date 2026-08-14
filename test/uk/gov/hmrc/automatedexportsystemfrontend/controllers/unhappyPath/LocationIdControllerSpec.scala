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
import uk.gov.hmrc.automatedexportsystemfrontend.forms.unhappyPath.LocationIdFormProvider
import uk.gov.hmrc.automatedexportsystemfrontend.helpers.SpecBase
import uk.gov.hmrc.automatedexportsystemfrontend.models.{LocationDetails, NormalMode, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.navigation.{FakeUnhappyPathNavigator, UnhappyPathNavigator}
import uk.gov.hmrc.automatedexportsystemfrontend.pages.unhappyPath.LocationIdPage
import uk.gov.hmrc.automatedexportsystemfrontend.repositories.SessionRepository
import uk.gov.hmrc.automatedexportsystemfrontend.views.html.unhappyPath.LocationIdView

import scala.concurrent.Future

class LocationIdControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new LocationIdFormProvider()
  val form: Form[LocationDetails] = formProvider()

  lazy val locationIdRoute: String = unhappyRoute.LocationIdController.onPageLoad(NormalMode).url

  val userAnswers = UserAnswers(
    userAnswersId,
    Json.obj(
      LocationIdPage.toString -> Json.obj(
        "locationType" -> "value 1",
        "unlocode" -> "value 2",
        "locationAdditionalIdentifier" -> "value 3",
        "authorisationReferenceNumber" -> "value 4"
      )
    )
  )

  "LocationId Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
        .build()

      running(application) {
        val request = FakeRequest(GET, locationIdRoute)

        val view = application.injector.instanceOf[LocationIdView]

        val result = route(application, request).value

        status(result) shouldBe OK

        val body = contentAsString(result)
        body should include("Identify the location")
        body should include("These are placeholder location codes for the prototype. The actual list will come from the CL327 and CL244 codelists.")
        body should include("Location type")
        body should include("UN/LOCODE")
        body should include("Location additional identifier")
        body should include("Authorisation reference number")
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
        .build()

      running(application) {
        val request = FakeRequest(GET, locationIdRoute)

        val view = application.injector.instanceOf[LocationIdView]

        val result = route(application, request).value

        status(result) shouldBe OK

        val body = contentAsString(result)
        body should include("Identify the location")
        body should include("These are placeholder location codes for the prototype. The actual list will come from the CL327 and CL244 codelists.")
        body should include("Location type")
        body should include("""id="locationType"""")
        body should include("""name="locationType"""")
        body should include("""type="text"""")
        body should include("""value="value 1"""")
        body should include("UN/LOCODE")
        body should include("""id="unlocode"""")
        body should include("""name="unlocode"""")
        body should include("""type="text"""")
        body should include("""value="value 2"""")
        body should include("Location additional identifier")
        body should include("""id="locationAdditionalIdentifier"""")
        body should include("""name="locationAdditionalIdentifier"""")
        body should include("""type="text"""")
        body should include("""value="value 3"""")
        body should include("Authorisation reference number")
        body should include("""id="authorisationReferenceNumber"""")
        body should include("""name="authorisationReferenceNumber"""")
        body should include("""type="text"""")
        body should include("""value="value 4"""")
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
          FakeRequest(POST, locationIdRoute)
            .withFormUrlEncodedBody(
              ("locationType", "value 1"),
              ("unlocode", "value 2"),
              ("locationAdditionalIdentifier", "value 3"),
              ("authorisationReferenceNumber", "value 4")
            )

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
          FakeRequest(POST, locationIdRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = form.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[LocationIdView]

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
        val request = FakeRequest(GET, locationIdRoute)

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
          FakeRequest(POST, locationIdRoute)
            .withFormUrlEncodedBody(
              ("locationType", "value 1"),
              ("unlocode", "value 2"),
              ("locationAdditionalIdentifier", "value 3"),
              ("authorisationReferenceNumber", "value 4")
            )

        val result = route(application, request).value

        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe problemRoute.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
