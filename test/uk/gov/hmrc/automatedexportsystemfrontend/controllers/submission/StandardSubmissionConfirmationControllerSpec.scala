package uk.gov.hmrc.automatedexportsystemfrontend.controllers.submission

import uk.gov.hmrc.automatedexportsystemfrontend.controllers.submission.routes
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.automatedexportsystemfrontend.helpers.SpecBase
import uk.gov.hmrc.automatedexportsystemfrontend.views.html.submission.StandardSubmissionConfirmationView

class StandardSubmissionConfirmationControllerSpec extends SpecBase {

  "StandardSubmissionConfirmation Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, routes.StandardSubmissionConfirmationController.onPageLoad().url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[StandardSubmissionConfirmationView]

        status(result) shouldBe OK
        contentAsString(result) shouldBe view()(request, messages(application)).toString
      }
    }
  }
}
