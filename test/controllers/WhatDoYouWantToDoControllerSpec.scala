package controllers

import base.SpecBase
import forms.WhatDoYouWantToDoFormProvider
import uk.gov.hmrc.automatedexportsystemfrontend.models.{NormalMode, UserAnswers, WhatDoYouWantToDo}
import navigation.{FakeNavigator, Navigator}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.WhatDoYouWantToDoPage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.WhatDoYouWantToDoView

import scala.concurrent.Future

class WhatDoYouWantToDoControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  lazy val whatDoYouWantToDoRoute = routes.WhatDoYouWantToDoController.onPageLoad(NormalMode).url

  val formProvider = new WhatDoYouWantToDoFormProvider()
  val form = formProvider()

  "WhatDoYouWantToDo Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, whatDoYouWantToDoRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[WhatDoYouWantToDoView]

        status(result) shouldBe OK
        contentAsString(result) shouldBe view(form, NormalMode)(request, messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId).set(WhatDoYouWantToDoPage, WhatDoYouWantToDo.values.head).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, whatDoYouWantToDoRoute)

        val view = application.injector.instanceOf[WhatDoYouWantToDoView]

        val result = route(application, request).value

        status(result) shouldBe OK
        contentAsString(result) shouldBe view(form.fill(WhatDoYouWantToDo.values.head), NormalMode)(request, messages(application)).toString
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
          FakeRequest(POST, whatDoYouWantToDoRoute)
            .withFormUrlEncodedBody(("value", WhatDoYouWantToDo.values.head.toString))

        val result = route(application, request).value

        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe onwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request =
          FakeRequest(POST, whatDoYouWantToDoRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = form.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[WhatDoYouWantToDoView]

        val result = route(application, request).value

        status(result) shouldBe BAD_REQUEST
        contentAsString(result) shouldBe view(boundForm, NormalMode)(request, messages(application)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request = FakeRequest(GET, whatDoYouWantToDoRoute)

        val result = route(application, request).value

        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "redirect to Journey Recovery for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      running(application) {
        val request =
          FakeRequest(POST, whatDoYouWantToDoRoute)
            .withFormUrlEncodedBody(("value", WhatDoYouWantToDo.values.head.toString))

        val result = route(application, request).value

        status(result) shouldBe SEE_OTHER

        redirectLocation(result).value shouldBe routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
