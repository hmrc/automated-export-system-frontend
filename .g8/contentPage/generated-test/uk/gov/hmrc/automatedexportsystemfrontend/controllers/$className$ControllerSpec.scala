package uk.gov.hmrc.automatedexportsystemfrontend.controllers

import uk.gov.hmrc.automatedexportsystemfrontend.helpers.SpecBase
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.automatedexportsystemfrontend.views.html.$className$View
import org.scalatest.matchers.must.Matchers.mustEqual

class $className$ControllerSpec extends SpecBase with MockitoSugar{

  "$className$ Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
        .build()

      running(application) {
        val request = FakeRequest(GET, routes.$className$Controller.onPageLoad().url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[$className$View]

        status(result) mustEqual OK
        val body = contentAsString(result)
        body should include("automated-export-system-frontend")
        body should include("$className;format="decap"$")
      }
    }
  }
}
