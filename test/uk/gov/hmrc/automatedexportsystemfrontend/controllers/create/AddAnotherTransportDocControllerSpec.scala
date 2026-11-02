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
import uk.gov.hmrc.automatedexportsystemfrontend.models.{DocumentDetails, NormalMode, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.pages.create.DiscrepancyTransportDocPage
import uk.gov.hmrc.automatedexportsystemfrontend.queries.DiscrepancyTransportDoc

class AddAnotherTransportDocControllerSpec extends SpecBase with MockitoSugar {

  lazy val transportDocListRoute: String = createRoute.AddAnotherTransportDocController.onPageLoad(None, NormalMode).url
  val maxTransportDoc: Int = DiscrepancyTransportDoc.maxTransportDocuments
  val sealIdentifier = "testSealIdentifier"
  val userAnswers: UserAnswers = emptyUserAnswers
    .set(DiscrepancyTransportDocPage(1), DocumentDetails(Some(1), Some(1)))
    .flatMap(_.set(DiscrepancyTransportDocPage(2), DocumentDetails(Some(2), Some(2))))
    .flatMap(_.set(DiscrepancyTransportDocPage(3), DocumentDetails(Some(3), Some(3))))
    .success
    .value

  val pageSize = 25

  "AddAnotherTransportDoc Controller" - {

    "onPageLoad" - {
      "must return OK and the correct view for a GET when allowed to add more items" in {

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(application) {
          val request = FakeRequest(GET, transportDocListRoute)

          val result = route(application, request).value

          status(result) mustEqual OK
          val body = contentAsString(result)
          body should include("You have added 3 transport documents")
          body should include("transport document 1")
          body should include("""Change<span class="govuk-visually-hidden"> transport document 1</span>""")
          body should include("""Remove<span class="govuk-visually-hidden"> transport document 1</span>""")
          body should include("transport document 2")
          body should include("""Change<span class="govuk-visually-hidden"> transport document 2</span>""")
          body should include("""Remove<span class="govuk-visually-hidden"> transport document 2</span>""")
          body should include("transport document 3")
          body should include("""Change<span class="govuk-visually-hidden"> transport document 3</span>""")
          body should include("""Remove<span class="govuk-visually-hidden"> transport document 3</span>""")
          body should include("You can add up to <strong>96</strong> more transport documents.")
          body should include("<strong>Do you need to add another transport document?</strong>")
          body should include("Yes")
          body should include("No")
          body should include("Continue")
        }
      }

      "must return OK and the correct view for a GET without pagination" in {

        val transportDocList = (1 to pageSize).map { number =>
          DocumentDetails(Some(number), Some(number))
        }.toList

        val userAnswers = emptyUserAnswers
          .set(DiscrepancyTransportDoc, transportDocList)
          .success
          .value

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(application) {
          val request = FakeRequest(GET, createRoute.AddAnotherTransportDocController.onPageLoad(None, NormalMode).url)

          val result = route(application, request).value

          status(result) mustEqual OK
          val body = contentAsString(result)
          body should include("You have added 25 transport documents")
          (1 to pageSize).foreach { number =>
            body should include(s"Transport document $number")
            body should include(s"""Change<span class="govuk-visually-hidden"> transport document $number</span>""")
            body should include(s"""Remove<span class="govuk-visually-hidden"> transport document $number</span>""")
          }
          body should not include "Showing 1-25 of 25 items"
          body should not include "Previous"
          body should not include s"""href="${createRoute.AddAnotherTransportDocController.onPageLoad(Some(1), NormalMode).url}""""
          body should not include s"""href="${createRoute.AddAnotherTransportDocController.onPageLoad(Some(2), NormalMode).url}""""
          body should not include "Next"
          body should include("You can add up to <strong>74</strong> more transport documents")
          body should include("<strong>Do you need to add another transport document?</strong>")
          body should include("Yes")
          body should include("No")
          body should include("Continue")
        }
      }

      //      "must return OK and the correct view for a GET with pagination" in {
      //        val sealsList = (1 to pageSize + 1).map { number =>
      //          s"seal$number"
      //        }.toList
      //
      //        val userAnswers = emptyUserAnswers
      //          .set(DiscrepancyTransportDoc, sealsList)
      //          .success
      //          .value
      //
      //        val application = applicationBuilder(userAnswers = Some(userAnswers))
      //          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
      //          .build()
      //
      //        running(application) {
      //          val request = FakeRequest(GET, createRoute.AddAnotherTransportDocController.onPageLoad(Some(1), NormalMode).url)
      //
      //          val result = route(application, request).value
      //
      //          status(result) mustEqual OK
      //          val body = contentAsString(result)
      //          body should include("You have added 26 seals")
      //          (1 to pageSize).foreach { number =>
      //            body should include(s"seal$number")
      //            body should include(s"Change seal $number")
      //            body should include(s"Remove seal $number")
      //          }
      //          body should not include "seal26"
      //          body should not include "Change seal 26"
      //          body should not include "Remove seal 26"
      //          body should include("Showing 1-25 of 26 items")
      //          body should not include "Previous"
      //          body should include(s"""href="${createRoute.AddAnotherTransportDocController.onPageLoad(Some(1), NormalMode).url}"""")
      //          body should include(s"""href="${createRoute.AddAnotherTransportDocController.onPageLoad(Some(2), NormalMode).url}"""")
      //          body should include("Next")
      //          body should include("You can add up to <strong>73</strong> more seals")
      //          body should include("<strong>Do you need to add another seal?</strong>")
      //          body should include("Yes")
      //          body should include("No")
      //          body should include("Continue")
      //        }
      //      }

      //      "must return OK and the correct view for a GET when the max limit of items has been reached" - {
      //        val sealsList = (1 to maxSeals).map { number =>
      //          s"seal$number"
      //        }.toList
      //        val userAnswers = emptyUserAnswers
      //          .set(DiscrepancyTransportDoc, sealsList)
      //          .success
      //          .value
      //
      //        "on page 1" in {
      //
      //          val application = applicationBuilder(userAnswers = Some(userAnswers))
      //            .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
      //            .build()
      //
      //          running(application) {
      //            val request = FakeRequest(GET, createRoute.AddAnotherTransportDocController.onPageLoad(Some(1), NormalMode).url)
      //
      //            val result = route(application, request).value
      //
      //            status(result) mustEqual OK
      //            val body = contentAsString(result)
      //            body should include("You have added 99 seals")
      //            (1 to pageSize).foreach { number =>
      //              body should include(s"seal$number")
      //              body should include(s"Change seal $number")
      //              body should include(s"Remove seal $number")
      //            }
      //            body should not include "Seal26"
      //            body should not include "Change seal 26"
      //            body should not include "Remove seal 26"
      //            body should include("Showing 1-25 of 99 items")
      //            body should not include "Previous"
      //            body should include(s"""href="${createRoute.AddAnotherTransportDocController.onPageLoad(Some(1), NormalMode).url}"""")
      //            body should include(s"""href="${createRoute.AddAnotherTransportDocController.onPageLoad(Some(2), NormalMode).url}"""")
      //            body should include("Next")
      //            body should not include "You can add up to"
      //            body should not include "<strong>Do you need to add another seal?</strong>"
      //            body should not include "Yes"
      //            body should not include "No"
      //            body should include("You cannot add any more seals. To add another, you need to remove one first.")
      //            body should include("Continue")
      //          }
      //        }

      //      "on page 2" in {
      //
      //        val application = applicationBuilder(userAnswers = Some(userAnswers))
      //          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
      //          .build()
      //
      //        running(application) {
      //          val request = FakeRequest(GET, createRoute.AddAnotherTransportDocController.onPageLoad(Some(2), NormalMode).url)
      //
      //          val result = route(application, request).value
      //
      //          status(result) mustEqual OK
      //          val body = contentAsString(result)
      //          body should include("You have added 99 transport documents")
      //          (26 to 50).foreach { number =>
      //            body should include(s"transport document $number")
      //            body should include(s"Change transport document $number")
      //            body should include(s"Remove transport document $number")
      //          }
      //          body should not include "transport document 51"
      //          body should not include "Change transport document 51"
      //          body should not include "Remove transport document 51"
      //          body should include("Showing 26-50 of 99 items")
      //          body should include("Previous")
      //          body should include(s"""href="${createRoute.AddAnotherTransportDocController.onPageLoad(Some(1), NormalMode).url}"""")
      //          body should include(s"""href="${createRoute.AddAnotherTransportDocController.onPageLoad(Some(2), NormalMode).url}"""")
      //          body should include(s"""href="${createRoute.AddAnotherTransportDocController.onPageLoad(Some(3), NormalMode).url}"""")
      //          body should include("Next")
      //          body should not include "You can add up to"
      //          body should not include "<strong>Do you need to add another transport document?</strong>"
      //          body should not include "Yes"
      //          body should not include "No"
      //          body should include("You cannot add any more transport documents. To add another, you need to remove one first.")
      //          body should include("Continue")
      //        }
      //      }
      //
      //      "on page 3" in {
      //
      //        val application = applicationBuilder(userAnswers = Some(userAnswers))
      //          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
      //          .build()
      //
      //        running(application) {
      //          val request = FakeRequest(GET, createRoute.AddAnotherTransportDocController.onPageLoad(Some(3), NormalMode).url)
      //
      //          val result = route(application, request).value
      //
      //          status(result) mustEqual OK
      //          val body = contentAsString(result)
      //          body should include("You have added 99 transport documents")
      //          (51 to 75).foreach { number =>
      //            body should include(s"transport document $number")
      //            body should include(s"Change transport document $number")
      //            body should include(s"Remove transport document $number")
      //          }
      //          body should not include "transport document 76"
      //          body should not include "Change transport document 76"
      //          body should not include "Remove transport document 76"
      //          body should include("Showing 51-75 of 99 items")
      //          body should include("Previous")
      //          body should include(s"""href="${createRoute.AddAnotherTransportDocController.onPageLoad(Some(2), NormalMode).url}"""")
      //          body should include(s"""href="${createRoute.AddAnotherTransportDocController.onPageLoad(Some(3), NormalMode).url}"""")
      //          body should include(s"""href="${createRoute.AddAnotherTransportDocController.onPageLoad(Some(4), NormalMode).url}"""")
      //          body should include("Next")
      //          body should not include "You can add up to"
      //          body should not include "<strong>Do you need to add another transport document?</strong>"
      //          body should not include "Yes"
      //          body should not include "No"
      //          body should include("You cannot add any more transport documents. To add another, you need to remove one first.")
      //          body should include("Continue")
      //        }
      //      }
      //
      //      "on page 4" in {
      //
      //        val application = applicationBuilder(userAnswers = Some(userAnswers))
      //          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
      //          .build()
      //
      //        running(application) {
      //          val request = FakeRequest(GET, createRoute.AddAnotherTransportDocController.onPageLoad(Some(4), NormalMode).url)
      //
      //          val result = route(application, request).value
      //
      //          status(result) mustEqual OK
      //          val body = contentAsString(result)
      //          body should include("You have added 99 transport documents")
      //          (76 to 99).foreach { number =>
      //            body should include(s"transport document $number")
      //            body should include(s"Change transport document $number")
      //            body should include(s"Remove transport document $number")
      //          }
      //          body should not include "transport document 100"
      //          body should not include "Change transport document 100"
      //          body should not include "Remove transport document 100"
      //          body should include("Showing 76-99 of 99 items")
      //          body should include("Previous")
      //          body should include(s"""href="${createRoute.AddAnotherTransportDocController.onPageLoad(Some(3), NormalMode).url}"""")
      //          body should include(s"""href="${createRoute.AddAnotherTransportDocController.onPageLoad(Some(4), NormalMode).url}"""")
      //          body should not include "Next"
      //          body should not include "You can add up to"
      //          body should not include "<strong>Do you need to add another transport document?</strong>"
      //          body should not include "Yes"
      //          body should not include "No"
      //          body should include("You cannot add any more transport documents. To add another, you need to remove one first.")
      //          body should include("Continue")
      //        }
      //      }
      //    }

      "must redirect to DiscrepancyTransportDocPage at the first index if zero items added" in {

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(application) {
          val request = FakeRequest(GET, createRoute.AddAnotherTransportDocController.onPageLoad(None, NormalMode).url)

          val result = route(application, request).value

          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe createRoute.DiscrepancyTransportDocController.onPageLoad(1, NormalMode).url
        }
      }

      //      "must redirect back to AddAnother without a page when pagination has not started but page 1 is provided for a GET" in {
      //        val sealsList = (1 to pageSize).map { number =>
      //          s"seal$number"
      //        }.toList
      //
      //        val userAnswers = emptyUserAnswers
      //          .set(DiscrepancyTransportDoc, sealsList)
      //          .success
      //          .value
      //
      //        val application = applicationBuilder(userAnswers = Some(userAnswers))
      //          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
      //          .build()
      //
      //        running(application) {
      //          val request = FakeRequest(GET, createRoute.AddAnotherTransportDocController.onPageLoad(Some(1), NormalMode).url)
      //
      //          val result = route(application, request).value
      //          status(result) shouldBe SEE_OTHER
      //          redirectLocation(result).value shouldBe createRoute.AddAnotherTransportDocController.onPageLoad(None, NormalMode).url
      //        }
      //      }

      //      "must redirect back to AddAnother with page 1 when pagination has started and no page is provided for a GET" in {
      //        val sealsList = (1 to pageSize + 1).map { number =>
      //          s"seal$number"
      //        }.toList
      //
      //        val userAnswers = emptyUserAnswers
      //          .set(DiscrepancyTransportDoc, sealsList)
      //          .success
      //          .value
      //
      //        val application = applicationBuilder(userAnswers = Some(userAnswers))
      //          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
      //          .build()
      //
      //        running(application) {
      //          val request = FakeRequest(GET, createRoute.AddAnotherTransportDocController.onPageLoad(None, NormalMode).url)
      //
      //          val result = route(application, request).value
      //          status(result) shouldBe SEE_OTHER
      //          redirectLocation(result).value shouldBe createRoute.AddAnotherTransportDocController.onPageLoad(Some(1), NormalMode).url
      //        }
      //      }

      "must redirect to Journey Recovery if the page doesn't exist for a GET" in {

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(application) {
          val request = FakeRequest(GET, createRoute.AddAnotherTransportDocController.onPageLoad(Some(0), NormalMode).url)

          val result = route(application, request).value

          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe problemRoute.JourneyRecoveryController.onPageLoad().url
        }
      }
    }

    "must redirect to Journey Recovery when there are no user answers data" in {

      val application = applicationBuilder(userAnswers = None)
        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
        .build()

      running(application) {
        val request = FakeRequest(GET, createRoute.AddAnotherTransportDocController.onPageLoad(None, NormalMode).url)

        val result = route(application, request).value

        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe problemRoute.JourneyRecoveryController.onPageLoad().url
      }
    }

    "onSubmit" - {
      "must redirect to next DiscrepancyTransportDocPage at the correct index when Yes is selected" in {

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(application) {
          val request =
            FakeRequest(POST, createRoute.AddAnotherTransportDocController.onSubmit(None, NormalMode).url).withFormUrlEncodedBody("value" -> "true")

          val result = route(application, request).value

          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe createRoute.DiscrepancyTransportDocController.onPageLoad(4, NormalMode).url
        }
      }

      "must redirect to DiscrepancyGoods when No is selected" in {

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(application) {
          val request =
            FakeRequest(POST, createRoute.AddAnotherTransportDocController.onSubmit(None, NormalMode).url).withFormUrlEncodedBody("value" -> "false")

          val result = route(application, request).value

          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe createRoute.DiscrepancyGoodsController.onPageLoad(NormalMode).url
        }
      }

      "must redirect to next DiscrepancyTransportDocPage at the correct index and preserve the mode in CheckMode" in {

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(application) {
          val request =
            FakeRequest(POST, createRoute.AddAnotherTransportDocController.onSubmit(None, NormalMode).url).withFormUrlEncodedBody("value" -> "true")

          val result = route(application, request).value

          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe createRoute.DiscrepancyTransportDocController.onPageLoad(4, NormalMode).url
        }
      }

      //    "must return a Bad Request when no option chosen and max limit not reached while preserving pagination page number" in {
      //      val sealsList = (1 to 51).map { number =>
      //        s"seal$number"
      //      }.toList
      //
      //      val userAnswers = emptyUserAnswers
      //        .set(DiscrepancyTransportDoc, sealsList)
      //        .success
      //        .value
      //
      //      val application = applicationBuilder(userAnswers = Some(userAnswers))
      //        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
      //        .build()
      //
      //      running(application) {
      //        val request = FakeRequest(POST, createRoute.AddAnotherTransportDocController.onSubmit(Some(2), NormalMode).url)
      //
      //        val result = route(application, request).value
      //
      //        status(result) shouldBe BAD_REQUEST
      //        val body = contentAsString(result)
      //        body should include("Select yes if you want to add another seal")
      //        body should not include "seal25"
      //        body should include("seal26")
      //        body should include("seal50")
      //        body should not include "seal51"
      //      }
      //    }

      //    "must redirect to SubmissionCYA when maximum items reached" in {
      //
      //      val userAnswers = emptyUserAnswers
      //        .set(DiscrepancyTransportDoc, sealsList)
      //        .success
      //        .value
      //
      //      val application = applicationBuilder(userAnswers = Some(userAnswers))
      //        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
      //        .build()
      //
      //      running(application) {
      //        val request = FakeRequest(POST, createRoute.AddAnotherTransportDocController.onSubmit(None, NormalMode).url)
      //
      //        val result = route(application, request).value
      //
      //        status(result) shouldBe SEE_OTHER
      //        redirectLocation(result).value shouldBe createRoute.DiscrepancyReferenceController.onPageLoad(1, NormalMode).url
      //      }
      //    }

      "must redirect to DiscrepancyTransportDocPage at the first index if zero items added for a POST" in {

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(application) {
          val request = FakeRequest(POST, createRoute.AddAnotherTransportDocController.onPageLoad(None, NormalMode).url)

          val result = route(application, request).value

          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe createRoute.DiscrepancyTransportDocController.onPageLoad(1, NormalMode).url
        }
      }

      "must redirect to Journey Recovery if the page doesn't exist for a POST" in {

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(application) {
          val request = FakeRequest(POST, createRoute.AddAnotherTransportDocController.onPageLoad(Some(2), NormalMode).url)

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
          val request = FakeRequest(POST, createRoute.AddAnotherTransportDocController.onSubmit(None, NormalMode).url)

          val result = route(application, request).value

          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe problemRoute.JourneyRecoveryController.onPageLoad().url
        }
      }
    }
  }
}
