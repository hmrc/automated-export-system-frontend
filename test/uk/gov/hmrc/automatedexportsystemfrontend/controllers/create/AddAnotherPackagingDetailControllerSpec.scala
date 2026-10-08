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

import org.scalatest.matchers.must.Matchers.mustEqual
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.create.routes as createRoute
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.problem.routes as problemRoute
import uk.gov.hmrc.automatedexportsystemfrontend.helpers.SpecBase
import uk.gov.hmrc.automatedexportsystemfrontend.models.{NormalMode, PackingDetails}
import uk.gov.hmrc.automatedexportsystemfrontend.pages.create.DiscrepancyPackingPage
import uk.gov.hmrc.automatedexportsystemfrontend.queries.DiscrepancyPacking

class AddAnotherPackagingDetailControllerSpec extends SpecBase with MockitoSugar {

  val maxPackagingDetails: Int = DiscrepancyPacking.maxPackagingDetails

  "AddAnotherPackagingDetail Controller" - {

    "onPageLoad" - {
      "must return OK and the correct view for a GET when allowed to add more items" in {

        val userAnswers = emptyUserAnswers
          .set(DiscrepancyPackingPage(1), PackingDetails("BX", 1, "MARKS1"))
          .flatMap(_.set(DiscrepancyPackingPage(2), PackingDetails("CT", 2, "MARKS2")))
          .flatMap(_.set(DiscrepancyPackingPage(3), PackingDetails("PK", 3, "MARKS3")))
          .success
          .value

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(application) {
          val request = FakeRequest(GET, createRoute.AddAnotherPackagingDetailController.onPageLoad().url)

          val result = route(application, request).value

          status(result) mustEqual OK
          val body = contentAsString(result)
          body should include("You have added 3 packaging details")
          body should include("Packaging detail 1")
          body should include("Change packaging detail 1")
          body should include("Remove packaging detail 1")
          body should include("Packaging detail 2")
          body should include("Change packaging detail 2")
          body should include("Remove packaging detail 2")
          body should include("Packaging detail 3")
          body should include("Change packaging detail 3")
          body should include("Remove packaging detail 3")
          body should include("You can add up to <strong>96</strong> more packaging details.")
          body should include("<strong>Do you need to add another packaging detail?</strong>")
          body should include("Yes")
          body should include("No")
          body should include("Continue")
        }
      }

      "must return OK and the correct view for a GET when the max limit of items has been reached" in {

        val packingDetailsList = (1 to maxPackagingDetails).map { number =>
          PackingDetails(s"packagingCode$number", number, s"shippingMarks$number")
        }.toList

        val userAnswers = emptyUserAnswers
          .set(DiscrepancyPacking, packingDetailsList)
          .success
          .value

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(application) {
          val request = FakeRequest(GET, createRoute.AddAnotherPackagingDetailController.onPageLoad().url)

          val result = route(application, request).value

          status(result) mustEqual OK
          val body = contentAsString(result)
          body should include("You have added 99 packaging details")
          (1 to maxPackagingDetails).foreach { number =>
            body should include(s"Packaging detail $number")
            body should include(s"Change packaging detail $number")
            body should include(s"Remove packaging detail $number")
          }
          body should not include "You can add up to"
          body should not include "<strong>Do you need to add another packaging detail?</strong>"
          body should not include "Yes"
          body should not include "No"
          body should include("You cannot add any more packaging details. To add another, you need to remove one first.")
          body should include("Continue")
        }
      }

      "must redirect to DiscrepancyPackingPage at the first index if zero items added" in {
        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(application) {
          val request = FakeRequest(GET, createRoute.AddAnotherPackagingDetailController.onPageLoad().url)

          val result = route(application, request).value

          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe createRoute.DiscrepancyPackingController.onPageLoad(1, NormalMode).url
        }
      }

      "must redirect to Journey Recovery when there are no user answers data" in {
        val application = applicationBuilder(userAnswers = None)
          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(application) {
          val request = FakeRequest(GET, createRoute.AddAnotherPackagingDetailController.onPageLoad().url)

          val result = route(application, request).value

          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe problemRoute.JourneyRecoveryController.onPageLoad().url
        }
      }
    }

    "onSubmit" - {
      "must redirect to next DiscrepancyPackingPage at the correct index when Yes is selected" in {
        val userAnswers = emptyUserAnswers
          .set(DiscrepancyPackingPage(1), PackingDetails("BX", 1, "MARKS1"))
          .flatMap(_.set(DiscrepancyPackingPage(2), PackingDetails("CT", 2, "MARKS2")))
          .flatMap(_.set(DiscrepancyPackingPage(3), PackingDetails("PK", 3, "MARKS3")))
          .success
          .value

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(application) {
          val request = FakeRequest(POST, createRoute.AddAnotherPackagingDetailController.onSubmit().url).withFormUrlEncodedBody("value" -> "true")

          val result = route(application, request).value

          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe createRoute.DiscrepancyPackingController.onPageLoad(4, NormalMode).url
        }
      }

      "must redirect to CYASubmission when No is selected" in {
        val userAnswers = emptyUserAnswers
          .set(DiscrepancyPackingPage(1), PackingDetails("BX", 1, "MARKS1"))
          .flatMap(_.set(DiscrepancyPackingPage(2), PackingDetails("CT", 2, "MARKS2")))
          .flatMap(_.set(DiscrepancyPackingPage(3), PackingDetails("PK", 3, "MARKS3")))
          .success
          .value

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(application) {
          val request = FakeRequest(POST, createRoute.AddAnotherPackagingDetailController.onSubmit().url).withFormUrlEncodedBody("value" -> "false")

          val result = route(application, request).value

          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe createRoute.CYASubmissionController.onPageLoad().url
        }
      }

      "must return a Bad Request when no option chosen and max limit not reached" in {
        val userAnswers = emptyUserAnswers
          .set(DiscrepancyPackingPage(1), PackingDetails("BX", 1, "MARKS1"))
          .flatMap(_.set(DiscrepancyPackingPage(2), PackingDetails("CT", 2, "MARKS2")))
          .flatMap(_.set(DiscrepancyPackingPage(3), PackingDetails("PK", 3, "MARKS3")))
          .success
          .value

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(application) {
          val request = FakeRequest(POST, createRoute.AddAnotherPackagingDetailController.onSubmit().url)

          val result = route(application, request).value

          status(result) shouldBe BAD_REQUEST
          contentAsString(result) should include("Select yes if you want to add another packaging detail")
        }
      }

      "must redirect to CYASubmission when maximum items reached" in {
        val packingDetailsList = (1 to maxPackagingDetails).map { number =>
          PackingDetails(s"packagingCode$number", number, s"shippingMarks$number")
        }.toList

        val userAnswers = emptyUserAnswers
          .set(DiscrepancyPacking, packingDetailsList)
          .success
          .value

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(application) {
          val request = FakeRequest(POST, createRoute.AddAnotherPackagingDetailController.onSubmit().url)

          val result = route(application, request).value

          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe createRoute.CYASubmissionController.onPageLoad().url
        }
      }

      "must redirect to Journey Recovery when there are no user answers data" in {
        val application = applicationBuilder(userAnswers = None)
          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(application) {
          val request = FakeRequest(POST, createRoute.AddAnotherPackagingDetailController.onSubmit().url)

          val result = route(application, request).value

          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe problemRoute.JourneyRecoveryController.onPageLoad().url
        }
      }
    }
  }
}
