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

import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{verify, when}
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
import uk.gov.hmrc.automatedexportsystemfrontend.models.{CheckMode, NormalMode, UserAnswers, WhatHasChangedDetails}
import uk.gov.hmrc.automatedexportsystemfrontend.navigation.{CreateNavigator, FakeCreateNavigator}
import uk.gov.hmrc.automatedexportsystemfrontend.pages.create.DiscrepancyGoodsPage
import uk.gov.hmrc.automatedexportsystemfrontend.queries.DiscrepancyGoods
import uk.gov.hmrc.automatedexportsystemfrontend.repositories.SessionRepository

import scala.concurrent.Future

class DiscrepancyGoodsControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new DiscrepancyGoodsFormProvider()
  val form: Form[Option[WhatHasChangedDetails]] = formProvider()

  lazy val discrepancyGoodsRoute: String = createRoute.DiscrepancyGoodsController.onPageLoad(1, NormalMode).url

  val userAnswers = UserAnswers(
    userAnswersId,
    Json.obj(
      "standard" -> Json.obj(
        DiscrepancyGoodsPage(1).toString -> Json.arr(
          Json.obj(
            "declarationGoodsItemNumber" -> 123,
            "declarationUniqueConsignmentReference" -> "value 2",
            "newGrossMass" -> "value 3",
            "newNetMass" -> "value 4"
          )
        ),
        DiscrepancyGoodsPage(2).toString -> Json.arr(
          Json.obj(
            "declarationGoodsItemNumber" -> 1232,
            "declarationUniqueConsignmentReference" -> "value 2",
            "newGrossMass" -> "value 3",
            "newNetMass" -> "value 4"
          )
        )
      )
    )
  )

  "DiscrepancyGoods Controller" - {

    "must return OK and the correct view for a GET at the next index" in {

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

    "must populate the view correctly on a GET when the question has previously been answered at an existing index" in {

      val application = applicationBuilder(userAnswers = Some(userAnswers))
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
        body should include("""id="goodsItem_declarationGoodsItemNumber"""")
        body should include("""name="goodsItem.declarationGoodsItemNumber"""")
        body should include("""type="text"""")
        body should include("""value="123""")
        body should include("Declaration Unique Consignment Reference (DUCR)")
        body should include("If you wish to amend the DUCR from the original IE501 message.")
        body should include("""id="goodsItem_declarationUniqueConsignmentReference"""")
        body should include("""name="goodsItem.declarationUniqueConsignmentReference"""")
        body should include("""type="text"""")
        body should include("""value="value 2"""")
        body should include("New gross mass in kilograms")
        body should include("The total weight of the goods, including all packaging and containers.")
        body should include("""id="goodsItem_newGrossMass"""")
        body should include("""name="goodsItem.newGrossMass"""")
        body should include("""type="text"""")
        body should include("""value="value 3"""")
        body should include("New net mass in kilograms")
        body should include("The weight of the goods only.")
        body should include("""id="goodsItem_newNetMass"""")
        body should include("""name="goodsItem.newNetMass"""")
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
              ("goodsItem.declarationGoodsItemNumber", "123"),
              ("goodsItem.declarationUniqueConsignmentReference", "5GB000000000000-12345"),
              ("goodsItem.newGrossMass", "1.0"),
              ("goodsItem.newNetMass", "1.0")
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
            .withFormUrlEncodedBody(("goodsItem.declarationUniqueConsignmentReference", "invalid value"))

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
              ("goodsItem.declarationGoodsItemNumber", "1"),
              ("goodsItem.declarationUniqueConsignmentReference", "value 2"),
              ("goodsItem.newGrossMass", "value 3"),
              ("goodsItem.newNetMass", "value 4")
            )

        val result = route(application, request).value

        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe problemRoute.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a GET where the index doesn't exist and is not the next index" in {

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
        .build()

      running(application) {
        val request = FakeRequest(GET, createRoute.DiscrepancyGoodsController.onPageLoad(5, NormalMode).url)

        val result = route(application, request).value

        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe problemRoute.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST where the index doesn't exist and is not the next index" in {

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
        .build()

      running(application) {
        val request = FakeRequest(POST, createRoute.DiscrepancyGoodsController.onPageLoad(5, NormalMode).url)

        val result = route(application, request).value

        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe problemRoute.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must remove the goods item and redirect to the goods items list when no data is entered and other goods items remain" in {
      val userAnswers = UserAnswers(userAnswersId)
        .set(
          DiscrepancyGoods,
          List(WhatHasChangedDetails(Some(1), Some("GB00000000-1"), "1", "2"), WhatHasChangedDetails(Some(2), Some("GB00000000-2"), "1", "2"))
        )
        .success
        .value

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[CreateNavigator].toInstance(new FakeCreateNavigator(onwardRoute)),
            bind[SessionRepository].toInstance(mockSessionRepository),
            bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, createRoute.DiscrepancyGoodsController.onSubmit(1, NormalMode).url)
            .withFormUrlEncodedBody(("goodsItem", ""))

        val result = route(application, request).value

        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe createRoute.AddAnotherGoodsItemController.onPageLoad(None, NormalMode).url

        val captor = ArgumentCaptor.forClass(classOf[UserAnswers])
        verify(mockSessionRepository).set(captor.capture())
        DiscrepancyGoods.getAll(captor.getValue) shouldBe List(WhatHasChangedDetails(Some(2), Some("GB00000000-2"), "1", "2"))
      }
    }

    "must redirect to the DiscrepancyPacking when no data is entered and no goods item remain in NormalMode" in {
      val userAnswers =
        UserAnswers(userAnswersId).set(DiscrepancyGoods, List(WhatHasChangedDetails(Some(1), Some("GB00000000-1"), "1", "2"))).success.value

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[CreateNavigator].toInstance(new FakeCreateNavigator(onwardRoute)),
            bind[SessionRepository].toInstance(mockSessionRepository),
            bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, createRoute.DiscrepancyGoodsController.onSubmit(1, NormalMode).url)
            .withFormUrlEncodedBody(("goodsItem", ""))

        val result = route(application, request).value

        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe createRoute.DiscrepancyPackingController.onPageLoad(1, NormalMode).url

        val captor = ArgumentCaptor.forClass(classOf[UserAnswers])
        verify(mockSessionRepository).set(captor.capture())
        DiscrepancyGoods.getAll(captor.getValue) shouldBe List.empty
      }
    }

    "must redirect to the CYASubmissionController when no data is entered and no goods item remain in CheckMode" in {
      val userAnswers =
        UserAnswers(userAnswersId).set(DiscrepancyGoods, List(WhatHasChangedDetails(Some(1), Some("GB00000000-1"), "1", "2"))).success.value

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[CreateNavigator].toInstance(new FakeCreateNavigator(onwardRoute)),
            bind[SessionRepository].toInstance(mockSessionRepository),
            bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, createRoute.DiscrepancyGoodsController.onSubmit(1, CheckMode).url)
            .withFormUrlEncodedBody(("value", ""))

        val result = route(application, request).value

        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe createRoute.CYASubmissionController.onPageLoad().url

        val captor = ArgumentCaptor.forClass(classOf[UserAnswers])
        verify(mockSessionRepository).set(captor.capture())
        DiscrepancyGoods.getAll(captor.getValue) shouldBe List.empty
      }
    }
  }
}
