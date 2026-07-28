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

package uk.gov.hmrc.automatedexportsystemfrontend.controllers.happyPath

import uk.gov.hmrc.automatedexportsystemfrontend.navigation.Navigator
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.automatedexportsystemfrontend.forms.happyPath.AnyDiscrepanciesFormProvider
import uk.gov.hmrc.automatedexportsystemfrontend.navigation.FakeNavigator
import uk.gov.hmrc.automatedexportsystemfrontend.pages.happyPath.AnyDiscrepanciesPage
import uk.gov.hmrc.automatedexportsystemfrontend.repositories.SessionRepository
import uk.gov.hmrc.automatedexportsystemfrontend.views.html.happyPath.AnyDiscrepanciesView
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.happyPath.routes as happyRoute
import uk.gov.hmrc.automatedexportsystemfrontend.helpers.SpecBase
import uk.gov.hmrc.automatedexportsystemfrontend.models.{NormalMode, UserAnswers}
import uk.gov.hmrc.http.SessionKeys

import scala.concurrent.Future

class AnyDiscrepanciesControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new AnyDiscrepanciesFormProvider()
  val form = formProvider()

  lazy val anyDiscrepanciesRoute = happyRoute.AnyDiscrepanciesController.onPageLoad(NormalMode).url

  "AnyDiscrepancies Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
        .build()

      running(application) {
        val request = FakeRequest(GET, anyDiscrepanciesRoute)
          .withSession(SessionKeys.sessionId -> "some-session-id")

        val result = route(application, request).value

        val view = application.injector.instanceOf[AnyDiscrepanciesView]

        status(result) shouldEqual OK
        val body = contentAsString(result)
        body should include("automated-export-system-frontend")
        body should include("anyDiscrepancies")
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId).set(AnyDiscrepanciesPage, true).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
        .build()

      running(application) {
        val request = FakeRequest(GET, anyDiscrepanciesRoute)
          .withSession(SessionKeys.sessionId -> "some-session-id")

        val result = route(application, request).value

        val view = application.injector.instanceOf[AnyDiscrepanciesView]

        status(result) shouldEqual OK
        val body = contentAsString(result)
        body should include("automated-export-system-frontend")
        body should include("""id="value"""")
        body should include("""name="value"""")
        body should include("""type="radio"""")
        body should include("""value="true"""")
        body should include("checked")
      }
    }

    "must redirect to the next page when valid data is submitted" in {

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(bind[Navigator].toInstance(new FakeNavigator(onwardRoute)), bind[SessionRepository].toInstance(mockSessionRepository))
          .build()

      running(application) {
        val request =
          FakeRequest(POST, anyDiscrepanciesRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) shouldBe SEE_OTHER
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
        .build()

      running(application) {
        val request =
          FakeRequest(POST, anyDiscrepanciesRoute)
            .withFormUrlEncodedBody(("value", ""))
            .withSession(SessionKeys.sessionId -> "some-session-id")

        val boundForm = form.bind(Map("value" -> ""))

        val view = application.injector.instanceOf[AnyDiscrepanciesView]

        val result = route(application, request).value

        status(result) shouldBe BAD_REQUEST
        val body = contentAsString(result)
        body should include("automated-export-system-frontend")
        body should include("There is a problem")
      }
    }

    // TODO readd when mongo set up
    //    "must redirect to Journey Recovery for a GET if no existing data is found" in {
    //
    //      val application = applicationBuilder(userAnswers = None).build()
    //
    //      running(application) {
    //        val request = FakeRequest(GET, anyDiscrepanciesRoute)
    //
    //        val result = route(application, request).value
    //
    //        status(result) mustEqual SEE_OTHER
    //        redirectLocation(result).value mustEqual controllers.problem.routes.JourneyRecoveryController.onPageLoad().url
    //      }
    //    }
    //
    //    "must redirect to Journey Recovery for a POST if no existing data is found" in {
    //
    //      val application = applicationBuilder(userAnswers = None).build()
    //
    //      running(application) {
    //        val request =
    //          FakeRequest(POST, anyDiscrepanciesRoute)
    //            .withFormUrlEncodedBody(("value", "true"))
    //
    //        val result = route(application, request).value
    //
    //        status(result) mustEqual SEE_OTHER
    //        redirectLocation(result).value mustEqual controllers.problem.routes.JourneyRecoveryController.onPageLoad().url
    //      }
    //    }
  }
}
