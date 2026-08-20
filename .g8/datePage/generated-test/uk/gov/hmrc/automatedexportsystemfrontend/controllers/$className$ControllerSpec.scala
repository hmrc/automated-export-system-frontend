package uk.gov.hmrc.automatedexportsystemfrontend.controllers

import java.time.{LocalDate, ZoneOffset}

import uk.gov.hmrc.automatedexportsystemfrontend.helpers.SpecBase
import uk.gov.hmrc.automatedexportsystemfrontend.forms.$className$FormProvider
import uk.gov.hmrc.automatedexportsystemfrontend.models.{NormalMode, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.navigation.{FakeNavigator, Navigator}
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.routes
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import uk.gov.hmrc.automatedexportsystemfrontend.pages.$className$Page
import play.api.i18n.Messages
import play.api.inject.bind
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.automatedexportsystemfrontend.repositories.SessionRepository
import uk.gov.hmrc.automatedexportsystemfrontend.views.html.$className$View
import uk.gov.hmrc.http.SessionKeys

import scala.concurrent.Future

class $className$ControllerSpec extends SpecBase with MockitoSugar {

  private implicit val messages: Messages = stubMessages()

  private val formProvider = new $className$FormProvider()
  private def form = formProvider()

  def onwardRoute = Call("GET", "/foo")

  val validAnswer = LocalDate.now(ZoneOffset.UTC)

  lazy val $className;format="decap"$Route = routes.$className$Controller.onPageLoad(NormalMode).url

  override val emptyUserAnswers = UserAnswers(userAnswersId)

  def getRequest(): FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, $className;format="decap"$Route)

  def postRequest(): FakeRequest[AnyContentAsFormUrlEncoded] =
    FakeRequest(POST, $className;format="decap"$Route)
      .withFormUrlEncodedBody(
        "value.day"   -> validAnswer.getDayOfMonth.toString,
        "value.month" -> validAnswer.getMonthValue.toString,
        "value.year"  -> validAnswer.getYear.toString
      )

  "$className$ Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
        .build()

      running(application) {
        val result = route(application, getRequest()).value

        val view = application.injector.instanceOf[$className$View]

        status(result) shouldBe OK
        val body = contentAsString(result)
        body should include("automated-export-system-frontend")
        body should include("$className;format="decap"$")
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId).set($className$Page, validAnswer).success.value

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
        .build()

      running(application) {
        val view = application.injector.instanceOf[$className$View]

        val result = route(application, getRequest()).value

        status(result) shouldBe OK
        val body = contentAsString(result)
        body should include("automated-export-system-frontend")
        body should include("$className;format="decap"$")
      }
    }

    "must redirect to the next page when valid data is submitted" in {

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
            bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector),
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

      running(application) {
        val result = route(application, postRequest()).value

        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe onwardRoute.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
        .build()

      val request =
        FakeRequest(POST, $className;format="decap"$Route)
          .withFormUrlEncodedBody(("value", "invalid value"))

      running(application) {
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

      val application = applicationBuilder(userAnswers = None)
        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
        .build()

      running(application) {
        val result = route(application, getRequest()).value

        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe problem.routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None)
        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
        .build()

      running(application) {
        val result = route(application, postRequest()).value

        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe problem.routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
