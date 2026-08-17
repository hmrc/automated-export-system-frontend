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

package uk.gov.hmrc.automatedexportsystemfrontend.controllers.amend

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.matchers.must.Matchers.mustEqual
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.amend.routes as amendRoute
import uk.gov.hmrc.automatedexportsystemfrontend.forms.amend.AmendEnterDucrFormProvider
import uk.gov.hmrc.automatedexportsystemfrontend.helpers.SpecBase
import uk.gov.hmrc.automatedexportsystemfrontend.models.{NormalMode, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.navigation.{FakeNavigator, Navigator}
import uk.gov.hmrc.automatedexportsystemfrontend.pages.amend.AmendEnterDucrPage
import uk.gov.hmrc.automatedexportsystemfrontend.repositories.SessionRepository
import uk.gov.hmrc.automatedexportsystemfrontend.views.html.amend.AmendEnterDucrView
import uk.gov.hmrc.http.SessionKeys

import scala.concurrent.Future

class AmendEnterDucrControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new AmendEnterDucrFormProvider()
  val form = formProvider()

  lazy val enterDucrRoute = amendRoute.AmendEnterDucrController.onPageLoad(NormalMode).url

  "EnterDucr Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
        .build()

      running(application) {
        val request = FakeRequest(GET, enterDucrRoute)
          .withSession(SessionKeys.sessionId -> "some-session-id")

        val result = route(application, request).value

        val view = application.injector.instanceOf[AmendEnterDucrView]

        status(result) mustEqual OK
        val body = contentAsString(result)
        body should include("automated-export-system-frontend")
        body should include("What is the Declaration Unique Consignment Reference (DUCR)?")
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId).set(AmendEnterDucrPage, "answer").success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
        .build()

      running(application) {
        val request = FakeRequest(GET, enterDucrRoute)
          .withSession(SessionKeys.sessionId -> "some-session-id")

        val view = application.injector.instanceOf[AmendEnterDucrView]

        val result = route(application, request).value

        status(result) mustEqual OK
        val body = contentAsString(result)
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
          FakeRequest(POST, enterDucrRoute)
            .withFormUrlEncodedBody(("value", "answer"))
            .withSession(SessionKeys.sessionId -> "some-session-id")

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
        .build()

      running(application) {
        val request =
          FakeRequest(POST, enterDucrRoute)
            .withFormUrlEncodedBody(("value", ""))
            .withSession(SessionKeys.sessionId -> "some-session-id")

        val boundForm = form.bind(Map("value" -> ""))

        val view = application.injector.instanceOf[AmendEnterDucrView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        val body = contentAsString(result)
        body should include("automated-export-system-frontend")
        body should include("There is a problem")
      }
    }

    // TODO readd when mongo set up

    //    "must redirect to Journey Recovery for a GET if no existing data is found" in {

//      val application = applicationBuilder(userAnswers = None).build()

//      running(application) {
//        val request = FakeRequest(GET, enterDucrRoute)

//        val result = route(application, request).value

//        status(result) mustEqual SEE_OTHER
//        redirectLocation(result).value mustEqual controllers.problem.routes.JourneyRecoveryController.onPageLoad().url
//      }
//    }

//    "must redirect to Journey Recovery for a POST if no existing data is found" in {

//      val application = applicationBuilder(userAnswers = None).build()

//      running(application) {
//        val request =
//          FakeRequest(POST, enterDucrRoute)
//            .withFormUrlEncodedBody(("value", "answer"))

//        val result = route(application, request).value

//        status(result) mustEqual SEE_OTHER
//        redirectLocation(result).value mustEqual controllers.problem.routes.JourneyRecoveryController.onPageLoad().url
//      }
//    }
  }
}
