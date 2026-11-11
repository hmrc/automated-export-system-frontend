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
import uk.gov.hmrc.automatedexportsystemfrontend.models.{CheckMode, ContainerDetails, NormalMode, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.pages.create.{DiscrepancyReferencePage, DiscrepancyTransportPage}
import uk.gov.hmrc.automatedexportsystemfrontend.queries.{DiscrepancyReference, DiscrepancyTransportEquipment}

class AddAnotherTransportControllerSpec extends SpecBase with MockitoSugar {

  val maxTransportEquipment: Int = DiscrepancyTransportEquipment.maxTransportEquipment

  val userAnswers: UserAnswers = emptyUserAnswers
    .set(DiscrepancyTransportPage(1), ContainerDetails(Some("containerId1"), Some(1)))
    .flatMap(_.set(DiscrepancyTransportPage(2), ContainerDetails(Some("containerId2"), Some(2))))
    .flatMap(_.set(DiscrepancyTransportPage(3), ContainerDetails(Some("containerId3"), Some(3))))
    .success
    .value

  val transportEquipmentList: List[ContainerDetails] = (1 to maxTransportEquipment).map { number =>
    ContainerDetails(Some(s"containerId${number.toString}"), Some(number))
  }.toList

  "AddAnotherTransport Controller" - {

    "onPageLoad" - {
      "must return OK and the correct view for a GET when allowed to add more items" in {

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(application) {
          val request = FakeRequest(GET, createRoute.AddAnotherTransportController.onPageLoad(None, NormalMode).url)

          val result = route(application, request).value

          status(result) mustEqual OK
          val body = contentAsString(result)
          body should include("You have added 3 transport equipment entries")
          body should include("Transport equipment 1")
          body should include("""Change<span class="govuk-visually-hidden"> transport equipment 1</span>""")
          body should include("""Remove<span class="govuk-visually-hidden"> transport equipment 1</span>""")
          body should include("Transport equipment 2")
          body should include("""Change<span class="govuk-visually-hidden"> transport equipment 2</span>""")
          body should include("""Remove<span class="govuk-visually-hidden"> transport equipment 2</span>""")
          body should include("Transport equipment 3")
          body should include("""Change<span class="govuk-visually-hidden"> transport equipment 3</span>""")
          body should include("""Remove<span class="govuk-visually-hidden"> transport equipment 3</span>""")
          body should include("You can add up to <strong>9,996</strong> more transport equipment entries")
          body should include("<strong>Do you want to add another transport equipment entry?</strong>")
          body should include("Yes")
          body should include("No")
          body should include("Continue")
        }
      }

      "must return OK and the correct view for a GET without pagination" in {

        val transportEquipmentList: List[ContainerDetails] = (1 to 25).map { number =>
          ContainerDetails(Some(s"containerId${number.toString}"), Some(number))
        }.toList

        val userAnswers = emptyUserAnswers
          .set(DiscrepancyTransportEquipment, transportEquipmentList)
          .success
          .value

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(application) {
          val request = FakeRequest(GET, createRoute.AddAnotherTransportController.onPageLoad(None, NormalMode).url)

          val result = route(application, request).value

          status(result) mustEqual OK
          val body = contentAsString(result)
          body should include("You have added 25 transport equipment entries")
          (1 to 25).foreach { number =>
            body should include(s"Transport equipment $number")
            body should include(s"""Change<span class="govuk-visually-hidden"> transport equipment $number</span>""")
            body should include(s"""Remove<span class="govuk-visually-hidden"> transport equipment $number</span>""")
          }
          body should not include "Showing 1-25 of 25 items"
          body should not include "Previous"
          body should not include s"""href="${createRoute.AddAnotherTransportController.onPageLoad(Some(1), NormalMode).url}""""
          body should not include s"""href="${createRoute.AddAnotherTransportController.onPageLoad(Some(2), NormalMode).url}""""
          body should not include "Next"
          body should include("You can add up to <strong>9,974</strong> more transport equipment entries.")
          body should include("<strong>Do you want to add another transport equipment entry?</strong>")
          body should include("Yes")
          body should include("No")
          body should include("Continue")
        }
      }

      "must return OK and the correct view for a GET with pagination" in {

        val transportEquipmentList: List[ContainerDetails] = (1 to 26).map { number =>
          ContainerDetails(Some(s"containerId${number.toString}"), Some(number))
        }.toList

        val userAnswers = emptyUserAnswers
          .set(DiscrepancyTransportEquipment, transportEquipmentList)
          .success
          .value

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(application) {
          val request = FakeRequest(GET, createRoute.AddAnotherTransportController.onPageLoad(Some(1), NormalMode).url)

          val result = route(application, request).value

          status(result) mustEqual OK
          val body = contentAsString(result)
          body should include("You have added 26 transport equipment entries (page 1 of 2)")
          (1 to 25).foreach { number =>
            body should include(s"Transport equipment $number")
            body should include(s"""Change<span class="govuk-visually-hidden"> transport equipment $number</span>""")
            body should include(s"""Remove<span class="govuk-visually-hidden"> transport equipment $number</span>""")
          }
          body should not include "Transport equipment 26"
          body should include("Showing 1-25 of 26 items")
          body should not include "Previous"
          body should include(s"""href="${createRoute.AddAnotherTransportController.onPageLoad(Some(1), NormalMode).url}"""")
          body should include(s"""href="${createRoute.AddAnotherTransportController.onPageLoad(Some(2), NormalMode).url}"""")
          body should include("Next")
          body should include("You can add up to <strong>9,973</strong> more transport equipment entries.")
          body should include("<strong>Do you want to add another transport equipment entry?</strong>")
          body should include("Yes")
          body should include("No")
          body should include("Continue")
        }
      }

      "must return OK and the correct view for a GET when the max limit of items has been reached" - {
        "on page 1" in {

          val userAnswers = emptyUserAnswers
            .set(DiscrepancyTransportEquipment, transportEquipmentList)
            .success
            .value

          val application = applicationBuilder(userAnswers = Some(userAnswers))
            .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
            .build()

          running(application) {
            val request = FakeRequest(GET, createRoute.AddAnotherTransportController.onPageLoad(Some(1), NormalMode).url)

            val result = route(application, request).value

            status(result) mustEqual OK
            val body = contentAsString(result)
            body should include("You have added 9,999 transport equipment entries (page 1 of 400)")
            (1 to 25).foreach { number =>
              body should include(s"Transport equipment $number")
              body should include(s"""Change<span class="govuk-visually-hidden"> transport equipment $number</span>""")
              body should include(s"""Remove<span class="govuk-visually-hidden"> transport equipment $number</span>""")
            }
            body should not include "Transport equipment 26"
            body should include("Showing 1-25 of 9,999 items")
            body should not include "Previous"
            body should include(s"""href="${createRoute.AddAnotherTransportController.onPageLoad(Some(1), NormalMode).url}"""")
            body should include(s"""href="${createRoute.AddAnotherTransportController.onPageLoad(Some(2), NormalMode).url}"""")
            body should include("Next")
            body should not include "You can add up to"
            body should not include "<strong>Do you want to add another transport equipment entry?</strong>"
            body should not include "Yes"
            body should not include "No"
            body should include("You cannot add any more transport equipment entries. To add another, you need to remove one first.")
            body should include("Continue")
          }
        }

        "on page 200" in {

          val userAnswers = emptyUserAnswers
            .set(DiscrepancyTransportEquipment, transportEquipmentList)
            .success
            .value

          val application = applicationBuilder(userAnswers = Some(userAnswers))
            .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
            .build()

          running(application) {
            val request = FakeRequest(GET, createRoute.AddAnotherTransportController.onPageLoad(Some(200), NormalMode).url)

            val result = route(application, request).value

            status(result) mustEqual OK
            val body = contentAsString(result)
            body should include("You have added 9,999 transport equipment entries (page 200 of 400)")
            (4976 to 5000).foreach { number =>
              body should include(f"Transport equipment $number%,d")
              body should include(f"""Change<span class="govuk-visually-hidden"> transport equipment $number%,d</span>""")
              body should include(f"""Remove<span class="govuk-visually-hidden"> transport equipment $number%,d</span>""")
            }
            body should not include "Transport equipment 4,975"
            body should not include "Transport equipment 5,001"
            body should include("Showing 4,976-5,000 of 9,999 items")
            body should include("Previous")
            body should include(s"""href="${createRoute.AddAnotherTransportController.onPageLoad(Some(199), NormalMode).url}"""")
            body should include(s"""href="${createRoute.AddAnotherTransportController.onPageLoad(Some(200), NormalMode).url}"""")
            body should include(s"""href="${createRoute.AddAnotherTransportController.onPageLoad(Some(201), NormalMode).url}"""")
            body should include("Next")
            body should not include "You can add up to"
            body should not include "<strong>Do you want to add another transport equipment entry?</strong>"
            body should not include "Yes"
            body should not include "No"
            body should include("You cannot add any more transport equipment entries. To add another, you need to remove one first.")
            body should include("Continue")
          }
        }

        "on page 400" in {

          val userAnswers = emptyUserAnswers
            .set(DiscrepancyTransportEquipment, transportEquipmentList)
            .success
            .value

          val application = applicationBuilder(userAnswers = Some(userAnswers))
            .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
            .build()

          running(application) {
            val request = FakeRequest(GET, createRoute.AddAnotherTransportController.onPageLoad(Some(400), NormalMode).url)

            val result = route(application, request).value

            status(result) mustEqual OK
            val body = contentAsString(result)
            body should include("You have added 9,999 transport equipment entries (page 400 of 400)")
            (9976 to 9999).foreach { number =>
              body should include(f"Transport equipment $number%,d")
              body should include(f"""Change<span class="govuk-visually-hidden"> transport equipment $number%,d</span>""")
              body should include(f"""Remove<span class="govuk-visually-hidden"> transport equipment $number%,d</span>""")
            }
            body should not include "Transport equipment 9,975"
            body should include("Showing 9,976-9,999 of 9,999 items")
            body should include("Previous")
            body should include(s"""href="${createRoute.AddAnotherTransportController.onPageLoad(Some(399), NormalMode).url}"""")
            body should include(s"""href="${createRoute.AddAnotherTransportController.onPageLoad(Some(400), NormalMode).url}"""")
            body should not include "Next"
            body should not include "You can add up to"
            body should not include "<strong>Do you want to add another transport equipment entry?</strong>"
            body should not include "Yes"
            body should not include "No"
            body should include("You cannot add any more transport equipment entries. To add another, you need to remove one first.")
            body should include("Continue")
          }
        }
      }

      "must redirect to DiscrepancyTransportPage at the first index if zero items added for a GET" in {

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(application) {
          val request = FakeRequest(GET, createRoute.AddAnotherTransportController.onPageLoad(None, NormalMode).url)

          val result = route(application, request).value

          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe createRoute.DiscrepancyTransportController.onPageLoad(1, NormalMode).url
        }
      }

      "must redirect to Journey Recovery if the page doesn't exist for a GET" in {

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(application) {
          val request = FakeRequest(GET, createRoute.AddAnotherTransportController.onPageLoad(Some(0), NormalMode).url)

          val result = route(application, request).value

          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe problemRoute.JourneyRecoveryController.onPageLoad().url
        }
      }

      "must redirect back to AddAnother without a page when pagination has not started but page 1 is provided for a GET" in {

        val transportEquipmentList: List[ContainerDetails] = (1 to 25).map { number =>
          ContainerDetails(Some(s"containerId${number.toString}"), Some(number))
        }.toList

        val userAnswers = emptyUserAnswers
          .set(DiscrepancyTransportEquipment, transportEquipmentList)
          .success
          .value

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(application) {
          val request = FakeRequest(GET, createRoute.AddAnotherTransportController.onPageLoad(Some(1), NormalMode).url)

          val result = route(application, request).value

          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe createRoute.AddAnotherTransportController.onPageLoad(None, NormalMode).url
        }
      }

      "must redirect back to AddAnother with page 1 when pagination has started and no page is provided for a GET" in {

        val transportEquipmentList: List[ContainerDetails] = (1 to 26).map { number =>
          ContainerDetails(Some(s"containerId${number.toString}"), Some(number))
        }.toList

        val userAnswers = emptyUserAnswers
          .set(DiscrepancyTransportEquipment, transportEquipmentList)
          .success
          .value

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(application) {
          val request = FakeRequest(GET, createRoute.AddAnotherTransportController.onPageLoad(None, NormalMode).url)

          val result = route(application, request).value

          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe createRoute.AddAnotherTransportController.onPageLoad(Some(1), NormalMode).url
        }
      }

      "must redirect to Journey Recovery when there are no user answers data" in {

        val application = applicationBuilder(userAnswers = None)
          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(application) {
          val request = FakeRequest(GET, createRoute.AddAnotherTransportController.onPageLoad(None, NormalMode).url)

          val result = route(application, request).value

          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe problemRoute.JourneyRecoveryController.onPageLoad().url
        }
      }
    }

    "onSubmit" - {
      "must redirect to next DiscrepancyTransportPage at the correct index when Yes is selected" in {

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(application) {
          val request =
            FakeRequest(POST, createRoute.AddAnotherTransportController.onSubmit(None, NormalMode).url)
              .withFormUrlEncodedBody("value" -> "true")

          val result = route(application, request).value

          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe createRoute.DiscrepancyTransportController.onPageLoad(4, NormalMode).url
        }
      }

      "must redirect to next DiscrepancyTransportPage at the correct index and preserve mode in CheckMode when answer is Yes" in {

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(application) {
          val request =
            FakeRequest(POST, createRoute.AddAnotherTransportController.onSubmit(None, CheckMode).url).withFormUrlEncodedBody("value" -> "true")

          val result = route(application, request).value

          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe createRoute.DiscrepancyTransportController.onPageLoad(4, CheckMode).url
        }
      }

      "must redirect to DiscrepancySealsPage when No is selected" in {

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(application) {
          val request =
            FakeRequest(POST, createRoute.AddAnotherTransportController.onSubmit(None, NormalMode).url)
              .withFormUrlEncodedBody("value" -> "false")

          val result = route(application, request).value

          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe createRoute.DiscrepancySealsController.onPageLoad(1, NormalMode).url
        }
      }

      "must redirect to next DiscrepancySealsPage and preserve the mode in CheckMode when answer is No" in {

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(application) {
          val request =
            FakeRequest(POST, createRoute.AddAnotherTransportController.onSubmit(None, CheckMode).url)
              .withFormUrlEncodedBody("value" -> "false")

          val result = route(application, request).value

          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe createRoute.DiscrepancySealsController.onPageLoad(1, CheckMode).url
        }
      }

      "must return a Bad Request when no option chosen and max limit not reached while preserving pagination page number" in {

        val transportEquipmentList: List[ContainerDetails] = (1 to 51).map { number =>
          ContainerDetails(Some(s"containerId${number.toString}"), Some(number))
        }.toList

        val userAnswers = emptyUserAnswers
          .set(DiscrepancyTransportEquipment, transportEquipmentList)
          .success
          .value

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(application) {
          val request = FakeRequest(POST, createRoute.AddAnotherTransportController.onSubmit(Some(2), NormalMode).url)

          val result = route(application, request).value

          status(result) shouldBe BAD_REQUEST
          val body = contentAsString(result)
          body should include("Select yes if you want to add another transport equipment")
          body should not include "Transport equipment 25"
          body should include("Transport equipment 26")
          body should include("Transport equipment 50")
          body should not include "Transport equipment 51"
        }
      }

      "must redirect to DiscrepancySealsPage when maximum items reached" in {

        val userAnswers = emptyUserAnswers
          .set(DiscrepancyTransportEquipment, transportEquipmentList)
          .success
          .value

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(application) {
          val request = FakeRequest(POST, createRoute.AddAnotherTransportController.onSubmit(None, NormalMode).url)

          val result = route(application, request).value

          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe createRoute.DiscrepancySealsController.onPageLoad(1, NormalMode).url
        }
      }

      "must redirect to DiscrepancyTransportPage at the first index if zero items added for a POST" in {

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(application) {
          val request = FakeRequest(POST, createRoute.AddAnotherTransportController.onPageLoad(None, NormalMode).url)

          val result = route(application, request).value

          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe createRoute.DiscrepancyTransportController.onPageLoad(1, NormalMode).url
        }
      }

      "must redirect to Journey Recovery if the page doesn't exist for a POST" in {

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(application) {
          val request = FakeRequest(POST, createRoute.AddAnotherTransportController.onPageLoad(Some(2), NormalMode).url)

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
          val request = FakeRequest(POST, createRoute.AddAnotherTransportController.onSubmit(None, NormalMode).url)

          val result = route(application, request).value

          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe problemRoute.JourneyRecoveryController.onPageLoad().url
        }
      }
    }
  }
}
