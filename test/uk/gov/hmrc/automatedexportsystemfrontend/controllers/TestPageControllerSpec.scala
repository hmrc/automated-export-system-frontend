package uk.gov.hmrc.automatedexportsystemfrontend.controllers

import base.SpecBase
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.TestPageView

class TestPageControllerSpec extends SpecBase {

  "TestPage Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, routes.TestPageController.onPageLoad().url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[TestPageView]

        status(result) shouldBe OK
        contentAsString(result) shouldBe view()(request, messages(application)).toString
      }
    }
  }
}
