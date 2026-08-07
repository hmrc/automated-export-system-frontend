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

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.happyPath.routes as happyRoute
import uk.gov.hmrc.automatedexportsystemfrontend.forms.happyPath.OfficeOfExitFormProvider
import uk.gov.hmrc.automatedexportsystemfrontend.helpers.SpecBase
import uk.gov.hmrc.automatedexportsystemfrontend.models.{NormalMode, OfficeOfExit, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.navigation.{FakeNavigator, Navigator}
import uk.gov.hmrc.automatedexportsystemfrontend.pages.happyPath.OfficeOfExitPage
import uk.gov.hmrc.automatedexportsystemfrontend.repositories.SessionRepository
import uk.gov.hmrc.automatedexportsystemfrontend.views.html.happyPath.OfficeOfExitView
import uk.gov.hmrc.http.SessionKeys

import scala.concurrent.Future

class OfficeOfExitControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  lazy val officeOfExitRoute = happyRoute.OfficeOfExitController.onPageLoad(NormalMode).url

  val formProvider = new OfficeOfExitFormProvider()
  val form = formProvider()

  "OfficeOfExit Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
        .build()

      running(application) {
        val request = FakeRequest(GET, officeOfExitRoute)
          .withSession(SessionKeys.sessionId -> "some-session-id")

        val result = route(application, request).value

        val view = application.injector.instanceOf[OfficeOfExitView]

        status(result) shouldBe OK
        val body = contentAsString(result)
        body should include("automated-export-system-frontend")
        body should include("Where do you expect the goods to exit the UK?")
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId).set(OfficeOfExitPage, OfficeOfExit.Larne).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
        .build()

      running(application) {
        val request = FakeRequest(GET, officeOfExitRoute)
          .withSession(SessionKeys.sessionId -> "some-session-id")

        val view = application.injector.instanceOf[OfficeOfExitView]

        val result = route(application, request).value

        status(result) shouldBe OK
        val body = contentAsString(result)
        body should include("automated-export-system-frontend")
        body should include("Where do you expect the goods to exit the UK?")
        body should include("""value="GB000142"""")
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
          FakeRequest(POST, officeOfExitRoute)
            .withFormUrlEncodedBody(("value", OfficeOfExit.values.head.toString))

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
          FakeRequest(POST, officeOfExitRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))
            .withSession(SessionKeys.sessionId -> "some-session-id")

        val boundForm = form.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[OfficeOfExitView]

        val result = route(application, request).value

        status(result) shouldEqual BAD_REQUEST
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
//        val request = FakeRequest(GET, officeOfExitRoute)
//
//        val result = route(application, request).value
//
//        status(result) mustEqual SEE_OTHER
//        redirectLocation(result).value mustEqual controllers.problem.routes.JourneyRecoveryController.onPageLoad().url
//      }
//    }
//
//    "redirect to Journey Recovery for a POST if no existing data is found" in {
//
//      val application = applicationBuilder(userAnswers = None).build()
//
//      running(application) {
//        val request =
//          FakeRequest(POST, officeOfExitRoute)
//            .withFormUrlEncodedBody(("value", OfficeOfExit.values.head.toString))
//
//        val result = route(application, request).value
//
//        status(result) mustEqual SEE_OTHER
//
//        redirectLocation(result).value mustEqual controllers.problem.routes.JourneyRecoveryController.onPageLoad().url
//      }
//    }
  }
}
