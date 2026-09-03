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

package uk.gov.hmrc.automatedexportsystemfrontend.controllers.create

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.data.Form
import play.api.inject.bind
import play.api.libs.json.Json
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.create.routes as createRoute
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.problem.routes as problemRoute
import uk.gov.hmrc.automatedexportsystemfrontend.forms.create.DiscrepancyGoodsFormProvider
import uk.gov.hmrc.automatedexportsystemfrontend.helpers.SpecBase
import uk.gov.hmrc.automatedexportsystemfrontend.models.{NormalMode, UserAnswers, WhatHasChangedDetails}
import uk.gov.hmrc.automatedexportsystemfrontend.navigation.{CreateNavigator, FakeCreateNavigator}
import uk.gov.hmrc.automatedexportsystemfrontend.pages.create.DiscrepancyGoodsPage
import uk.gov.hmrc.automatedexportsystemfrontend.repositories.SessionRepository

import scala.concurrent.Future

class DiscrepancyGoodsControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new DiscrepancyGoodsFormProvider()
  val form: Form[WhatHasChangedDetails] = formProvider()

  lazy val discrepancyGoodsRoute: String = createRoute.DiscrepancyGoodsController.onPageLoad(NormalMode).url

  val userAnswers = UserAnswers(
    userAnswersId,
    Json.obj(
      DiscrepancyGoodsPage.toString -> Json.obj(
        "declarationGoodsItemNumber" -> 1,
        "declarationUniqueConsignmentReference" -> "value 2",
        "newGrossMass" -> "value 3",
        "newNetMass" -> "value 4"
      )
    )
  )

  "DiscrepancyGoods Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
        .build()

      running(application) {
        val request = FakeRequest(GET, discrepancyGoodsRoute)
        val result = route(application, request).value

        status(result) shouldBe OK

        val body = contentAsString(result)
        body should include("Tell us what’s changed")
        body should include("Enter the details of any changes to the goods.")
        body should include("Goods item number")
        body should include("Declaration Unique Consignment Reference (DUCR)")
        body should include("If you wish to amend the DUCR from the original IE501 message.")
        body should include("New gross mass in kilograms")
        body should include("The total weight of the goods, including all packaging and containers.")
        body should include("New net mass in kilograms")
        body should include("The weight of the goods only.")
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
        .build()

      running(application) {
        val request = FakeRequest(GET, discrepancyGoodsRoute)

        // val view = application.injector.instanceOf[DiscrepancyGoodsView]

        val result = route(application, request).value

        status(result) shouldBe OK

        val body = contentAsString(result)
        body should include("Tell us what’s changed")
        body should include("Enter the details of any changes to the goods.")
        body should include("Goods item number")
        body should include("""id="declarationGoodsItemNumber"""")
        body should include("""name="declarationGoodsItemNumber"""")
        body should include("""type="text"""")
        body should include("""value="1"""")
        body should include("Declaration Unique Consignment Reference (DUCR)")
        body should include("If you wish to amend the DUCR from the original IE501 message.")
        body should include("""id="declarationUniqueConsignmentReference"""")
        body should include("""name="declarationUniqueConsignmentReference"""")
        body should include("""type="text"""")
        body should include("""value="value 2"""")
        body should include("New gross mass in kilograms")
        body should include("The total weight of the goods, including all packaging and containers.")
        body should include("""id="newGrossMass"""")
        body should include("""name="newGrossMass"""")
        body should include("""type="text"""")
        body should include("""value="value 3"""")
        body should include("New net mass in kilograms")
        body should include("The weight of the goods only.")
        body should include("""id="newNetMass"""")
        body should include("""name="newNetMass"""")
        body should include("""type="text"""")
        body should include("""value="value 4"""")
      }
    }

    "must redirect to the next page when valid data is submitted" in {

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[CreateNavigator].toInstance(new FakeCreateNavigator(onwardRoute)),
            bind[SessionRepository].toInstance(mockSessionRepository),
            bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, discrepancyGoodsRoute)
            .withFormUrlEncodedBody(
              ("declarationGoodsItemNumber", "1"),
              ("declarationUniqueConsignmentReference", "5GB000000000000-12345"),
              ("newGrossMass", "1.0"),
              ("newNetMass", "1.0")
            )

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
          FakeRequest(POST, discrepancyGoodsRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val result = route(application, request).value

        status(result) shouldBe BAD_REQUEST

        val body = contentAsString(result)
        body should include("There is a problem")
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None)
        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
        .build()

      running(application) {
        val request = FakeRequest(GET, discrepancyGoodsRoute)

        val result = route(application, request).value

        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe problemRoute.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None)
        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
        .build()

      running(application) {
        val request =
          FakeRequest(POST, discrepancyGoodsRoute)
            .withFormUrlEncodedBody(
              ("declarationGoodsItemNumber", "1"),
              ("declarationUniqueConsignmentReference", "value 2"),
              ("newGrossMass", "value 3"),
              ("newNetMass", "value 4")
            )

        val result = route(application, request).value

        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe problemRoute.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
