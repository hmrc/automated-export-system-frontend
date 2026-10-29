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
import org.mockito.Mockito.{never, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import play.api.data.Form
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.create.routes as createRoute
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.problem.routes as problemRoute
import uk.gov.hmrc.automatedexportsystemfrontend.helpers.SpecBase
import uk.gov.hmrc.automatedexportsystemfrontend.models.{CheckMode, NormalMode, PackingDetails, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.navigation.{FakeNavigator, Navigator}
import uk.gov.hmrc.automatedexportsystemfrontend.pages.create.DiscrepancyPackingPage
import uk.gov.hmrc.automatedexportsystemfrontend.queries.DiscrepancyPacking
import uk.gov.hmrc.automatedexportsystemfrontend.repositories.SessionRepository

import scala.concurrent.Future

class RemovePackagingDetailControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  val packagingDetailIndex = 30

  val packingDetailsList50: List[PackingDetails] = (1 to 50).map { number =>
    PackingDetails(s"packagingCode$number", number, s"shippingMarks$number")
  }.toList

  "RemovePackagingDetail Controller" - {

    "must return OK and the correct view for a GET" in {

      val userAnswers: UserAnswers = emptyUserAnswers
        .set(DiscrepancyPacking, packingDetailsList50)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
        .build()

      running(application) {
        val request = FakeRequest(GET, createRoute.RemovePackagingDetailController.onPageLoad(packagingDetailIndex, 2, NormalMode).url)

        val result = route(application, request).value

        status(result) shouldBe OK
        val body = contentAsString(result)
        body should include("Are you sure you want to remove packaging detail 30?")
        body should include("Yes")
        body should include("No")
      }
    }

    "must redirect to AddAnotherPackagingDetail while preserving the return page and update user answers when true" in {

      val userAnswers: UserAnswers = emptyUserAnswers
        .set(DiscrepancyPacking, packingDetailsList50)
        .success
        .value

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
            bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector),
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, createRoute.RemovePackagingDetailController.onPageLoad(packagingDetailIndex, 2, NormalMode).url)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe createRoute.AddAnotherPackagingDetailController.onPageLoad(Some(2), NormalMode).url
        verify(mockSessionRepository).set(any())
      }
    }

    "must redirect to AddAnotherPackagingDetail while preserving return page and CheckMode and update user answers when true" in {

      val userAnswers: UserAnswers = emptyUserAnswers
        .set(DiscrepancyPacking, packingDetailsList50)
        .success
        .value

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
            bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector),
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, createRoute.RemovePackagingDetailController.onPageLoad(packagingDetailIndex, 2, CheckMode).url)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe createRoute.AddAnotherPackagingDetailController.onPageLoad(Some(2), CheckMode).url
        verify(mockSessionRepository).set(any())
      }
    }

    "must redirect to AddAnotherPackagingDetail while preserving the return page and not update user answers when false" in {

      val userAnswers: UserAnswers = emptyUserAnswers
        .set(DiscrepancyPacking, packingDetailsList50)
        .success
        .value

      val mockSessionRepository = mock[SessionRepository]

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
            bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector),
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, createRoute.RemovePackagingDetailController.onPageLoad(packagingDetailIndex, 2, NormalMode).url)
            .withFormUrlEncodedBody(("value", "false"))

        val result = route(application, request).value

        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe createRoute.AddAnotherPackagingDetailController.onPageLoad(Some(2), NormalMode).url
        verify(mockSessionRepository, never()).set(any())
      }
    }

    "must redirect to AddAnotherPackagingDetail at next valid page when true and last item is removed from the expected return page" in {

      val packingDetailsList: List[PackingDetails] = (1 to 51).map { number =>
        PackingDetails(s"packagingCode$number", number, s"shippingMarks$number")
      }.toList

      val userAnswers: UserAnswers = emptyUserAnswers
        .set(DiscrepancyPacking, packingDetailsList)
        .success
        .value

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
            bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector),
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, createRoute.RemovePackagingDetailController.onPageLoad(51, 3, NormalMode).url)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe createRoute.AddAnotherPackagingDetailController.onPageLoad(Some(2), NormalMode).url
        verify(mockSessionRepository).set(any())
      }
    }

    "must redirect to DiscrepancyPacking when true and last item removed" in {

      val userAnswers: UserAnswers = emptyUserAnswers
        .set(DiscrepancyPackingPage(1), PackingDetails("BX", 1, "MARKS1"))
        .success
        .value

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(
            bind[Navigator].toInstance(new FakeNavigator(onwardRoute)),
            bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector),
            bind[SessionRepository].toInstance(mockSessionRepository)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, createRoute.RemovePackagingDetailController.onPageLoad(1, 1, NormalMode).url)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe createRoute.DiscrepancyPackingController.onPageLoad(1, NormalMode).url
        verify(mockSessionRepository).set(any())
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers: UserAnswers = emptyUserAnswers
        .set(DiscrepancyPacking, packingDetailsList50)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
        .build()

      running(application) {
        val request =
          FakeRequest(POST, createRoute.RemovePackagingDetailController.onPageLoad(packagingDetailIndex, 2, NormalMode).url)
            .withFormUrlEncodedBody(("value", ""))

        val result = route(application, request).value

        status(result) shouldBe BAD_REQUEST
        val body = contentAsString(result)
        body should include("Select yes if you want to remove packaging detail 30")
      }
    }

    "must redirect to Journey Recovery for a GET if index isn't defined" in {

      val userAnswers: UserAnswers = emptyUserAnswers
        .set(DiscrepancyPacking, packingDetailsList50)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
        .build()

      running(application) {
        val request = FakeRequest(GET, createRoute.RemovePackagingDetailController.onPageLoad(51, 2, NormalMode).url)

        val result = route(application, request).value

        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe problemRoute.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a GET if return page isn't valid" in {

      val userAnswers: UserAnswers = emptyUserAnswers
        .set(DiscrepancyPacking, packingDetailsList50)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
        .build()

      running(application) {
        val request = FakeRequest(GET, createRoute.RemovePackagingDetailController.onPageLoad(50, 3, NormalMode).url)

        val result = route(application, request).value

        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe problemRoute.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if index isn't defined" in {

      val userAnswers: UserAnswers = emptyUserAnswers
        .set(DiscrepancyPacking, packingDetailsList50)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
        .build()

      running(application) {
        val request = FakeRequest(POST, createRoute.RemovePackagingDetailController.onPageLoad(51, 2, NormalMode).url)

        val result = route(application, request).value

        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe problemRoute.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None)
        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
        .build()

      running(application) {
        val request = FakeRequest(GET, createRoute.RemovePackagingDetailController.onPageLoad(packagingDetailIndex, 1, NormalMode).url)

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
          FakeRequest(POST, createRoute.RemovePackagingDetailController.onPageLoad(packagingDetailIndex, 1, NormalMode).url)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe problemRoute.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
