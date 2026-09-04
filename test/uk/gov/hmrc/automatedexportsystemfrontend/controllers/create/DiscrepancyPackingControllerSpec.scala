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
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.data.Form
import play.api.inject.bind
import play.api.libs.json.Json
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.create.routes as createRoute
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.problem.routes as problemRoute
import uk.gov.hmrc.automatedexportsystemfrontend.forms.create.DiscrepancyPackingFormProvider
import uk.gov.hmrc.automatedexportsystemfrontend.helpers.SpecBase
import uk.gov.hmrc.automatedexportsystemfrontend.models.{NormalMode, PackingDetails, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.navigation.{CreateNavigator, FakeCreateNavigator}
import uk.gov.hmrc.automatedexportsystemfrontend.pages.create.DiscrepancyPackingPage
import uk.gov.hmrc.automatedexportsystemfrontend.repositories.SessionRepository

import scala.concurrent.Future

class DiscrepancyPackingControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new DiscrepancyPackingFormProvider()
  val form: Form[PackingDetails] = formProvider()

  lazy val discrepancyPackingRoute: String = createRoute.DiscrepancyPackingController.onPageLoad(NormalMode).url

  val userAnswers =
    UserAnswers(
      userAnswersId,
      Json.obj(
        "standard" -> Json
          .obj(DiscrepancyPackingPage.toString -> Json.obj("packagingCode" -> "value 1", "numberOfPackages" -> 123, "shippingMarks" -> "value 3"))
      )
    )

  "DiscrepancyPacking Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
        .build()

      running(application) {
        val request = FakeRequest(GET, discrepancyPackingRoute)
        val result = route(application, request).value

        status(result) shouldBe OK

        val body = contentAsString(result)
        body should include("Packing details")
        body should include("If the packaging of the goods has changed, enter the new details here.")
        body should include("Packaging code")
        body should include("For example, BX for Box, CT for Carton or PK for Package.")
        body should include("Number of packages")
        body should include("Shipping marks")
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
        .build()

      running(application) {
        val request = FakeRequest(GET, discrepancyPackingRoute)
        val result = route(application, request).value

        status(result) shouldBe OK

        val body = contentAsString(result)
        body should include("Packing details")
        body should include("If the packaging of the goods has changed, enter the new details here.")
        body should include("Packaging code")
        body should include("For example, BX for Box, CT for Carton or PK for Package.")
        body should include("""id="packagingCode"""")
        body should include("""name="packagingCode"""")
        body should include("""type="text"""")
        body should include("""value="value 1"""")
        body should include("Number of packages")
        body should include("""id="numberOfPackages"""")
        body should include("""name="numberOfPackages"""")
        body should include("""type="text"""")
        body should include("""value="123"""")
        body should include("Shipping marks")
        body should include("""id="shippingMarks"""")
        body should include("""name="shippingMarks"""")
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
            bind[CreateNavigator].toInstance(new FakeCreateNavigator(onwardRoute)),
            bind[SessionRepository].toInstance(mockSessionRepository),
            bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, discrepancyPackingRoute)
            .withFormUrlEncodedBody(("packagingCode", "value 1"), ("numberOfPackages", "123"), ("shippingMarks", "value 3"))

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
          FakeRequest(POST, discrepancyPackingRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

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
        val request = FakeRequest(GET, discrepancyPackingRoute)

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
          FakeRequest(POST, discrepancyPackingRoute)
            .withFormUrlEncodedBody(("packagingCode", "value 1"), ("numberOfPackages", "value 2"), ("shippingMarks", "value 3"))

        val result = route(application, request).value

        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe problemRoute.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
