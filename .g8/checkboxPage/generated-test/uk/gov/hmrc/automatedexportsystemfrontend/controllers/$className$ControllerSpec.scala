package uk.gov.hmrc.automatedexportsystemfrontend.controllers

import uk.gov.hmrc.automatedexportsystemfrontend.helpers.SpecBase
import uk.gov.hmrc.automatedexportsystemfrontend.forms.$className$FormProvider
import uk.gov.hmrc.automatedexportsystemfrontend.models.{NormalMode, $className$, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.navigation.{FakeNavigator, Navigator}
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.routes
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import uk.gov.hmrc.automatedexportsystemfrontend.pages.$className$Page
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.automatedexportsystemfrontend.repositories.SessionRepository
import uk.gov.hmrc.automatedexportsystemfrontend.views.html.$className$View

import scala.concurrent.Future

class $className$ControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  lazy val $className;format="decap"$Route = routes.$className$Controller.onPageLoad(NormalMode).url

  val formProvider = new $className$FormProvider()
  val form = formProvider()

  "$className$ Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
        .build()

      running(application) {
        val request = FakeRequest(GET, $className;format="decap"$Route)

        val result = route(application, request).value

        val view = application.injector.instanceOf[$className$View]

        status(result) shouldBe OK

        val body = contentAsString(result)
        body should include("automated-export-system-frontend")
        body should include("$className;format="decap"$")
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId).set($className$Page, $className$.values.toSet).success.value

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
        .build()

      running(application) {
        val request = FakeRequest(GET, $className;format="decap"$Route)

        val view = application.injector.instanceOf[$className$View]

        val result = route(application, request).value

        status(result) shouldBe OK
        body should include("automated-export-system-frontend")
        body should include("$className;format="decap"$")
        body should include("value")
      }
    }

    "must redirect to the next page when valid data is submitted" in {

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
        .build()

      running(application) {
        val request =
          FakeRequest(POST, $className;format="decap"$Route)
            .withFormUrlEncodedBody(("value[0]", $className$.values.head.toString))

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
          FakeRequest(POST, $className;format="decap"$Route)
            .withFormUrlEncodedBody(("value", "invalid value"))
          .withSession(SessionKeys.sessionId -> "some-session-id")

        val boundForm = form.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[$className$View]

        val result = route(application, request).value

        status(result) shouldBe BAD_REQUEST
        val body = contentAsString(result)
        body should include("automated-export-system-frontend")
        body should include("$className;format="decap"$")
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
        .build()
      
      running(application) {
        val request = FakeRequest(GET, $className;format="decap"$Route)

        val result = route(application, request).value

        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
        .build()
      
      running(application) {
        val request =
          FakeRequest(POST, $className;format="decap"$Route)
            .withFormUrlEncodedBody(("value[0]", $className$.values.head.toString))

        val result = route(application, request).value

        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
