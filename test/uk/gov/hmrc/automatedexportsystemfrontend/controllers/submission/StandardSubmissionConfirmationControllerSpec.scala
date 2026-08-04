package uk.gov.hmrc.automatedexportsystemfrontend.controllers.submission

import controllers.routes
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import views.html.IE507aSubmissionConfirmationView

class StandardSubmissionConfirmationControllerSpec extends SpecBase {

  "IE507aSubmissionConfirmation Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, routes.IE507aSubmissionConfirmationController.onPageLoad().url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[IE507aSubmissionConfirmationView]

        status(result) shouldBe OK
        contentAsString(result) shouldBe view()(request, messages(application)).toString
      }
    }
  }
}
