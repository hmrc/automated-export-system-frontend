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
import uk.gov.hmrc.automatedexportsystemfrontend.models.{CheckMode, NormalMode, PackingDetails, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.pages.create.DiscrepancyPackingPage
import uk.gov.hmrc.automatedexportsystemfrontend.queries.DiscrepancyPacking

class AddAnotherPackagingDetailControllerSpec extends SpecBase with MockitoSugar {

  val maxPackagingDetails: Int = DiscrepancyPacking.maxPackagingDetails

  val userAnswers: UserAnswers = emptyUserAnswers
    .set(DiscrepancyPackingPage(1), PackingDetails("BX", 1, "MARKS1"))
    .flatMap(_.set(DiscrepancyPackingPage(2), PackingDetails("CT", 2, "MARKS2")))
    .flatMap(_.set(DiscrepancyPackingPage(3), PackingDetails("PK", 3, "MARKS3")))
    .success
    .value

  "AddAnotherPackagingDetail Controller" - {

    "onPageLoad" - {
      "must return OK and the correct view for a GET when allowed to add more items" in {

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(application) {
          val request = FakeRequest(GET, createRoute.AddAnotherPackagingDetailController.onPageLoad(None, NormalMode).url)

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

      "must return OK and the correct view for a GET without pagination" in {

        val packingDetailsList = (1 to 25).map { number =>
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
          val request = FakeRequest(GET, createRoute.AddAnotherPackagingDetailController.onPageLoad(None, NormalMode).url)

          val result = route(application, request).value

          status(result) mustEqual OK
          val body = contentAsString(result)
          body should include("You have added 25 packaging details")
          (1 to 25).foreach { number =>
            body should include(s"Packaging detail $number")
            body should include(s"Change packaging detail $number")
            body should include(s"Remove packaging detail $number")
          }
          body should not include "Showing 1-25 of 25 items"
          body should not include "Previous"
          body should not include s"""href="${createRoute.AddAnotherPackagingDetailController.onPageLoad(Some(1), NormalMode).url}""""
          body should not include s"""href="${createRoute.AddAnotherPackagingDetailController.onPageLoad(Some(2), NormalMode).url}""""
          body should not include "Next"
          body should include("You can add up to <strong>74</strong> more packaging details.")
          body should include("<strong>Do you need to add another packaging detail?</strong>")
          body should include("Yes")
          body should include("No")
          body should include("Continue")
        }
      }

      "must return OK and the correct view for a GET with pagination" in {

        val packingDetailsList = (1 to 26).map { number =>
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
          val request = FakeRequest(GET, createRoute.AddAnotherPackagingDetailController.onPageLoad(Some(1), NormalMode).url)

          val result = route(application, request).value

          status(result) mustEqual OK
          val body = contentAsString(result)
          body should include("You have added 26 packaging details")
          (1 to 25).foreach { number =>
            body should include(s"Packaging detail $number")
            body should include(s"Change packaging detail $number")
            body should include(s"Remove packaging detail $number")
          }
          body should not include "Packaging detail 26"
          body should not include "Change packaging detail 26"
          body should not include "Remove packaging detail 26"
          body should include("Showing 1-25 of 26 items")
          body should not include "Previous"
          body should include(s"""href="${createRoute.AddAnotherPackagingDetailController.onPageLoad(Some(1), NormalMode).url}"""")
          body should include(s"""href="${createRoute.AddAnotherPackagingDetailController.onPageLoad(Some(2), NormalMode).url}"""")
          body should include("Next")
          body should include("You can add up to <strong>73</strong> more packaging details.")
          body should include("<strong>Do you need to add another packaging detail?</strong>")
          body should include("Yes")
          body should include("No")
          body should include("Continue")
        }
      }

      "must return OK and the correct view for a GET when the max limit of items has been reached" - {

        val packingDetailsList = (1 to maxPackagingDetails).map { number =>
          PackingDetails(s"packagingCode$number", number, s"shippingMarks$number")
        }.toList

        val userAnswers = emptyUserAnswers
          .set(DiscrepancyPacking, packingDetailsList)
          .success
          .value

        "on page 1" in {

          val application = applicationBuilder(userAnswers = Some(userAnswers))
            .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
            .build()

          running(application) {
            val request = FakeRequest(GET, createRoute.AddAnotherPackagingDetailController.onPageLoad(Some(1), NormalMode).url)

            val result = route(application, request).value

            status(result) mustEqual OK
            val body = contentAsString(result)
            body should include("You have added 99 packaging details")
            (1 to 25).foreach { number =>
              body should include(s"Packaging detail $number")
              body should include(s"Change packaging detail $number")
              body should include(s"Remove packaging detail $number")
            }
            body should not include "Packaging detail 26"
            body should not include "Change packaging detail 26"
            body should not include "Remove packaging detail 26"
            body should include("Showing 1-25 of 99 items")
            body should not include "Previous"
            body should include(s"""href="${createRoute.AddAnotherPackagingDetailController.onPageLoad(Some(1), NormalMode).url}"""")
            body should include(s"""href="${createRoute.AddAnotherPackagingDetailController.onPageLoad(Some(2), NormalMode).url}"""")
            body should include("Next")
            body should not include "You can add up to"
            body should not include "<strong>Do you need to add another packaging detail?</strong>"
            body should not include "Yes"
            body should not include "No"
            body should include("You cannot add any more packaging details. To add another, you need to remove one first.")
            body should include("Continue")
          }
        }

        "on page 2" in {

          val application = applicationBuilder(userAnswers = Some(userAnswers))
            .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
            .build()

          running(application) {
            val request = FakeRequest(GET, createRoute.AddAnotherPackagingDetailController.onPageLoad(Some(2), NormalMode).url)

            val result = route(application, request).value

            status(result) mustEqual OK
            val body = contentAsString(result)
            body should include("You have added 99 packaging details")
            (26 to 50).foreach { number =>
              body should include(s"Packaging detail $number")
              body should include(s"Change packaging detail $number")
              body should include(s"Remove packaging detail $number")
            }
            body should not include "Packaging detail 51"
            body should not include "Change packaging detail 51"
            body should not include "Remove packaging detail 51"
            body should include("Showing 26-50 of 99 items")
            body should include("Previous")
            body should include(s"""href="${createRoute.AddAnotherPackagingDetailController.onPageLoad(Some(1), NormalMode).url}"""")
            body should include(s"""href="${createRoute.AddAnotherPackagingDetailController.onPageLoad(Some(2), NormalMode).url}"""")
            body should include(s"""href="${createRoute.AddAnotherPackagingDetailController.onPageLoad(Some(3), NormalMode).url}"""")
            body should include("Next")
            body should not include "You can add up to"
            body should not include "<strong>Do you need to add another packaging detail?</strong>"
            body should not include "Yes"
            body should not include "No"
            body should include("You cannot add any more packaging details. To add another, you need to remove one first.")
            body should include("Continue")
          }
        }

        "on page 3" in {

          val application = applicationBuilder(userAnswers = Some(userAnswers))
            .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
            .build()

          running(application) {
            val request = FakeRequest(GET, createRoute.AddAnotherPackagingDetailController.onPageLoad(Some(3), NormalMode).url)

            val result = route(application, request).value

            status(result) mustEqual OK
            val body = contentAsString(result)
            body should include("You have added 99 packaging details")
            (51 to 75).foreach { number =>
              body should include(s"Packaging detail $number")
              body should include(s"Change packaging detail $number")
              body should include(s"Remove packaging detail $number")
            }
            body should not include "Packaging detail 76"
            body should not include "Change packaging detail 76"
            body should not include "Remove packaging detail 76"
            body should include("Showing 51-75 of 99 items")
            body should include("Previous")
            body should include(s"""href="${createRoute.AddAnotherPackagingDetailController.onPageLoad(Some(2), NormalMode).url}"""")
            body should include(s"""href="${createRoute.AddAnotherPackagingDetailController.onPageLoad(Some(3), NormalMode).url}"""")
            body should include(s"""href="${createRoute.AddAnotherPackagingDetailController.onPageLoad(Some(4), NormalMode).url}"""")
            body should include("Next")
            body should not include "You can add up to"
            body should not include "<strong>Do you need to add another packaging detail?</strong>"
            body should not include "Yes"
            body should not include "No"
            body should include("You cannot add any more packaging details. To add another, you need to remove one first.")
            body should include("Continue")
          }
        }

        "on page 4" in {

          val application = applicationBuilder(userAnswers = Some(userAnswers))
            .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
            .build()

          running(application) {
            val request = FakeRequest(GET, createRoute.AddAnotherPackagingDetailController.onPageLoad(Some(4), NormalMode).url)

            val result = route(application, request).value

            status(result) mustEqual OK
            val body = contentAsString(result)
            body should include("You have added 99 packaging details")
            (76 to 99).foreach { number =>
              body should include(s"Packaging detail $number")
              body should include(s"Change packaging detail $number")
              body should include(s"Remove packaging detail $number")
            }
            body should not include "Packaging detail 100"
            body should not include "Change packaging detail 100"
            body should not include "Remove packaging detail 100"
            body should include("Showing 76-99 of 99 items")
            body should include("Previous")
            body should include(s"""href="${createRoute.AddAnotherPackagingDetailController.onPageLoad(Some(3), NormalMode).url}"""")
            body should include(s"""href="${createRoute.AddAnotherPackagingDetailController.onPageLoad(Some(4), NormalMode).url}"""")
            body should not include "Next"
            body should not include "You can add up to"
            body should not include "<strong>Do you need to add another packaging detail?</strong>"
            body should not include "Yes"
            body should not include "No"
            body should include("You cannot add any more packaging details. To add another, you need to remove one first.")
            body should include("Continue")
          }
        }
      }

      "must redirect to DiscrepancyPackingPage at the first index if zero items added for a GET" in {

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(application) {
          val request = FakeRequest(GET, createRoute.AddAnotherPackagingDetailController.onPageLoad(None, NormalMode).url)

          val result = route(application, request).value

          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe createRoute.DiscrepancyPackingController.onPageLoad(1, NormalMode).url
        }
      }

      "must redirect to Journey Recovery if the page doesn't exist for a GET" in {

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(application) {
          val request = FakeRequest(GET, createRoute.AddAnotherPackagingDetailController.onPageLoad(Some(0), NormalMode).url)

          val result = route(application, request).value

          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe problemRoute.JourneyRecoveryController.onPageLoad().url
        }
      }

      "must redirect back to AddAnother without a page when pagination has not started but page 1 is provided for a GET" in {

        val packingDetailsList = (1 to 25).map { number =>
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
          val request = FakeRequest(GET, createRoute.AddAnotherPackagingDetailController.onPageLoad(Some(1), NormalMode).url)

          val result = route(application, request).value

          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe createRoute.AddAnotherPackagingDetailController.onPageLoad(None, NormalMode).url
        }
      }

      "must redirect back to AddAnother with page 1 when pagination has started and no page is provided for a GET" in {

        val packingDetailsList = (1 to 26).map { number =>
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
          val request = FakeRequest(GET, createRoute.AddAnotherPackagingDetailController.onPageLoad(None, NormalMode).url)

          val result = route(application, request).value

          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe createRoute.AddAnotherPackagingDetailController.onPageLoad(Some(1), NormalMode).url
        }
      }

      "must redirect to Journey Recovery when there are no user answers data" in {

        val application = applicationBuilder(userAnswers = None)
          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(application) {
          val request = FakeRequest(GET, createRoute.AddAnotherPackagingDetailController.onPageLoad(None, NormalMode).url)

          val result = route(application, request).value

          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe problemRoute.JourneyRecoveryController.onPageLoad().url
        }
      }
    }

    "onSubmit" - {
      "must redirect to next DiscrepancyPackingPage at the correct index when Yes is selected" in {

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(application) {
          val request =
            FakeRequest(POST, createRoute.AddAnotherPackagingDetailController.onSubmit(None, NormalMode).url)
              .withFormUrlEncodedBody("value" -> "true")

          val result = route(application, request).value

          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe createRoute.DiscrepancyPackingController.onPageLoad(4, NormalMode).url
        }
      }

      "must redirect to next DiscrepancyPackingPage at the correct index and preserve mode in CheckMode" in {

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(application) {
          val request =
            FakeRequest(POST, createRoute.AddAnotherPackagingDetailController.onSubmit(None, CheckMode).url).withFormUrlEncodedBody("value" -> "true")

          val result = route(application, request).value

          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe createRoute.DiscrepancyPackingController.onPageLoad(4, CheckMode).url
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
          val request =
            FakeRequest(POST, createRoute.AddAnotherPackagingDetailController.onSubmit(None, NormalMode).url)
              .withFormUrlEncodedBody("value" -> "false")

          val result = route(application, request).value

          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe createRoute.CYASubmissionController.onPageLoad().url
        }
      }

      "must return a Bad Request when no option chosen and max limit not reached while preserving pagination page number" in {

        val packingDetailsList = (1 to 51).map { number =>
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
          val request = FakeRequest(POST, createRoute.AddAnotherPackagingDetailController.onSubmit(Some(2), NormalMode).url)

          val result = route(application, request).value

          status(result) shouldBe BAD_REQUEST
          val body = contentAsString(result)
          body should include("Select yes if you want to add another packaging detail")
          body should not include "Packaging detail 25"
          body should include("Packaging detail 26")
          body should include("Packaging detail 50")
          body should not include "Packaging detail 51"
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
          val request = FakeRequest(POST, createRoute.AddAnotherPackagingDetailController.onSubmit(None, NormalMode).url)

          val result = route(application, request).value

          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe createRoute.CYASubmissionController.onPageLoad().url
        }
      }

      "must redirect to DiscrepancyPackingPage at the first index if zero items added for a POST" in {

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(application) {
          val request = FakeRequest(POST, createRoute.AddAnotherPackagingDetailController.onPageLoad(None, NormalMode).url)

          val result = route(application, request).value

          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe createRoute.DiscrepancyPackingController.onPageLoad(1, NormalMode).url
        }
      }

      "must redirect to Journey Recovery if the page doesn't exist for a POST" in {

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(application) {
          val request = FakeRequest(POST, createRoute.AddAnotherPackagingDetailController.onPageLoad(Some(2), NormalMode).url)

          val result = route(application, request).value

          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe problemRoute.JourneyRecoveryController.onPageLoad().url
        }
      }

      "must redirect to Journey Recovery when there are no user answers data" in {

        val application = applicationBuilder(userAnswers = None)
          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(application) {
          val request = FakeRequest(POST, createRoute.AddAnotherPackagingDetailController.onSubmit(None, NormalMode).url)

          val result = route(application, request).value

          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe problemRoute.JourneyRecoveryController.onPageLoad().url
        }
      }
    }
  }
}
