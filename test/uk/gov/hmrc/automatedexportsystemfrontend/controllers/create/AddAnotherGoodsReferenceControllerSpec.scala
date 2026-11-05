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
import uk.gov.hmrc.automatedexportsystemfrontend.models.{CheckMode, NormalMode, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.pages.create.DiscrepancyReferencePage
import uk.gov.hmrc.automatedexportsystemfrontend.queries.DiscrepancyReference

class AddAnotherGoodsReferenceControllerSpec extends SpecBase with MockitoSugar {

  val maxGoodsReferences: Int = DiscrepancyReference.maxDiscrepancyReferences

  val userAnswers: UserAnswers = emptyUserAnswers
    .set(DiscrepancyReferencePage(1), "12345")
    .flatMap(_.set(DiscrepancyReferencePage(2), "23456"))
    .flatMap(_.set(DiscrepancyReferencePage(3), "34567"))
    .success
    .value

  val goodsReferencesList: List[String] = (1 to maxGoodsReferences).map { number =>
    number.toString
  }.toList

  "AddAnotherGoodsReference Controller" - {

    "onPageLoad" - {
      "must return OK and the correct view for a GET when allowed to add more items" in {

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(application) {
          val request = FakeRequest(GET, createRoute.AddAnotherGoodsReferenceController.onPageLoad(None, NormalMode).url)

          val result = route(application, request).value

          status(result) mustEqual OK
          val body = contentAsString(result)
          body should include("You have added 3 Declaration Goods References")
          body should include("Declaration Goods Reference 12345")
          body should include("""Change<span class="govuk-visually-hidden"> Declaration Goods Reference 12345</span>""")
          body should include("""Remove<span class="govuk-visually-hidden"> Declaration Goods Reference 12345</span>""")
          body should include("Declaration Goods Reference 23456")
          body should include("""Change<span class="govuk-visually-hidden"> Declaration Goods Reference 23456</span>""")
          body should include("""Remove<span class="govuk-visually-hidden"> Declaration Goods Reference 23456</span>""")
          body should include("Declaration Goods Reference 34567")
          body should include("""Change<span class="govuk-visually-hidden"> Declaration Goods Reference 34567</span>""")
          body should include("""Remove<span class="govuk-visually-hidden"> Declaration Goods Reference 34567</span>""")
          body should include("You can add up to <strong>996</strong> more Declaration Goods References.")
          body should include("<strong>Do you need to add another Declaration Goods Reference?</strong>")
          body should include("Yes")
          body should include("No")
          body should include("Continue")
        }
      }

      "must return OK and the correct view for a GET without pagination" in {

        val goodsReferencesList = (1 to 25).map { number =>
          number.toString
        }.toList

        val userAnswers = emptyUserAnswers
          .set(DiscrepancyReference, goodsReferencesList)
          .success
          .value

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(application) {
          val request = FakeRequest(GET, createRoute.AddAnotherGoodsReferenceController.onPageLoad(None, NormalMode).url)

          val result = route(application, request).value

          status(result) mustEqual OK
          val body = contentAsString(result)
          body should include("You have added 25 Declaration Goods References")
          (1 to 25).foreach { number =>
            body should include(s"Declaration Goods Reference $number")
            body should include(s"""Change<span class="govuk-visually-hidden"> Declaration Goods Reference $number</span>""")
            body should include(s"""Remove<span class="govuk-visually-hidden"> Declaration Goods Reference $number</span>""")
          }
          body should not include "Showing 1-25 of 25 items"
          body should not include "Previous"
          body should not include s"""href="${createRoute.AddAnotherGoodsReferenceController.onPageLoad(Some(1), NormalMode).url}""""
          body should not include s"""href="${createRoute.AddAnotherGoodsReferenceController.onPageLoad(Some(2), NormalMode).url}""""
          body should not include "Next"
          body should include("You can add up to <strong>974</strong> more Declaration Goods References.")
          body should include("<strong>Do you need to add another Declaration Goods Reference?</strong>")
          body should include("Yes")
          body should include("No")
          body should include("Continue")
        }
      }

      "must return OK and the correct view for a GET with pagination" in {

        val goodsReferencesList = (1 to 26).map { number =>
          number.toString
        }.toList

        val userAnswers = emptyUserAnswers
          .set(DiscrepancyReference, goodsReferencesList)
          .success
          .value

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(application) {
          val request = FakeRequest(GET, createRoute.AddAnotherGoodsReferenceController.onPageLoad(Some(1), NormalMode).url)

          val result = route(application, request).value

          status(result) mustEqual OK
          val body = contentAsString(result)
          body should include("You have added 26 Declaration Goods References (page 1 of 2)")
          (1 to 25).foreach { number =>
            body should include(s"Declaration Goods Reference $number")
            body should include(s"""Change<span class="govuk-visually-hidden"> Declaration Goods Reference $number</span>""")
            body should include(s"""Remove<span class="govuk-visually-hidden"> Declaration Goods Reference $number</span>""")
          }
          body should not include "Declaration Goods Reference 26"
          body should include("Showing 1-25 of 26 items")
          body should not include "Previous"
          body should include(s"""href="${createRoute.AddAnotherGoodsReferenceController.onPageLoad(Some(1), NormalMode).url}"""")
          body should include(s"""href="${createRoute.AddAnotherGoodsReferenceController.onPageLoad(Some(2), NormalMode).url}"""")
          body should include("Next")
          body should include("You can add up to <strong>973</strong> more Declaration Goods References.")
          body should include("<strong>Do you need to add another Declaration Goods Reference?</strong>")
          body should include("Yes")
          body should include("No")
          body should include("Continue")
        }
      }

      "must return OK and the correct view for a GET when the max limit of items has been reached" - {
        "on page 1" in {

          val userAnswers = emptyUserAnswers
            .set(DiscrepancyReference, goodsReferencesList)
            .success
            .value

          val application = applicationBuilder(userAnswers = Some(userAnswers))
            .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
            .build()

          running(application) {
            val request = FakeRequest(GET, createRoute.AddAnotherGoodsReferenceController.onPageLoad(Some(1), NormalMode).url)

            val result = route(application, request).value

            status(result) mustEqual OK
            val body = contentAsString(result)
            body should include("You have added 999 Declaration Goods References (page 1 of 40)")
            (1 to 25).foreach { number =>
              body should include(s"Declaration Goods Reference $number")
              body should include(s"""Change<span class="govuk-visually-hidden"> Declaration Goods Reference $number</span>""")
              body should include(s"""Remove<span class="govuk-visually-hidden"> Declaration Goods Reference $number</span>""")
            }
            body should not include "Declaration Goods Reference 26"
            body should include("Showing 1-25 of 999 items")
            body should not include "Previous"
            body should include(s"""href="${createRoute.AddAnotherGoodsReferenceController.onPageLoad(Some(1), NormalMode).url}"""")
            body should include(s"""href="${createRoute.AddAnotherGoodsReferenceController.onPageLoad(Some(2), NormalMode).url}"""")
            body should include("Next")
            body should not include "You can add up to"
            body should not include "<strong>Do you need to add another Declaration Goods Reference?</strong>"
            body should not include "Yes"
            body should not include "No"
            body should include("You cannot add any more Declaration Goods References. To add another, you need to remove one first.")
            body should include("Continue")
          }
        }

        "on page 20" in {

          val userAnswers = emptyUserAnswers
            .set(DiscrepancyReference, goodsReferencesList)
            .success
            .value

          val application = applicationBuilder(userAnswers = Some(userAnswers))
            .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
            .build()

          running(application) {
            val request = FakeRequest(GET, createRoute.AddAnotherGoodsReferenceController.onPageLoad(Some(20), NormalMode).url)

            val result = route(application, request).value

            status(result) mustEqual OK
            val body = contentAsString(result)
            body should include("You have added 999 Declaration Goods References (page 20 of 40)")
            (476 to 500).foreach { number =>
              body should include(s"Declaration Goods Reference $number")
              body should include(s"""Change<span class="govuk-visually-hidden"> Declaration Goods Reference $number</span>""")
              body should include(s"""Remove<span class="govuk-visually-hidden"> Declaration Goods Reference $number</span>""")
            }
            body should not include "Declaration Goods Reference 475"
            body should not include "Declaration Goods Reference 501"
            body should include("Showing 476-500 of 999 items")
            body should include("Previous")
            body should include(s"""href="${createRoute.AddAnotherGoodsReferenceController.onPageLoad(Some(19), NormalMode).url}"""")
            body should include(s"""href="${createRoute.AddAnotherGoodsReferenceController.onPageLoad(Some(20), NormalMode).url}"""")
            body should include(s"""href="${createRoute.AddAnotherGoodsReferenceController.onPageLoad(Some(21), NormalMode).url}"""")
            body should include("Next")
            body should not include "You can add up to"
            body should not include "<strong>Do you need to add another Declaration Goods Reference?</strong>"
            body should not include "Yes"
            body should not include "No"
            body should include("You cannot add any more Declaration Goods References. To add another, you need to remove one first.")
            body should include("Continue")
          }
        }

        "on page 40" in {

          val userAnswers = emptyUserAnswers
            .set(DiscrepancyReference, goodsReferencesList)
            .success
            .value

          val application = applicationBuilder(userAnswers = Some(userAnswers))
            .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
            .build()

          running(application) {
            val request = FakeRequest(GET, createRoute.AddAnotherGoodsReferenceController.onPageLoad(Some(40), NormalMode).url)

            val result = route(application, request).value

            status(result) mustEqual OK
            val body = contentAsString(result)
            body should include("You have added 999 Declaration Goods References (page 40 of 40)")
            (976 to 999).foreach { number =>
              body should include(s"Declaration Goods Reference $number")
              body should include(s"""Change<span class="govuk-visually-hidden"> Declaration Goods Reference $number</span>""")
              body should include(s"""Remove<span class="govuk-visually-hidden"> Declaration Goods Reference $number</span>""")
            }
            body should not include "Declaration Goods Reference 975"
            body should include("Showing 976-999 of 999 items")
            body should include("Previous")
            body should include(s"""href="${createRoute.AddAnotherGoodsReferenceController.onPageLoad(Some(39), NormalMode).url}"""")
            body should include(s"""href="${createRoute.AddAnotherGoodsReferenceController.onPageLoad(Some(40), NormalMode).url}"""")
            body should not include "Next"
            body should not include "You can add up to"
            body should not include "<strong>Do you need to add another Declaration Goods Reference?</strong>"
            body should not include "Yes"
            body should not include "No"
            body should include("You cannot add any more Declaration Goods References. To add another, you need to remove one first.")
            body should include("Continue")
          }
        }
      }

      "must redirect to DiscrepancyReferencePage at the first index if zero items added for a GET" in {

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(application) {
          val request = FakeRequest(GET, createRoute.AddAnotherGoodsReferenceController.onPageLoad(None, NormalMode).url)

          val result = route(application, request).value

          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe createRoute.DiscrepancyReferenceController.onPageLoad(1, NormalMode).url
        }
      }

      "must redirect to Journey Recovery if the page doesn't exist for a GET" in {

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(application) {
          val request = FakeRequest(GET, createRoute.AddAnotherGoodsReferenceController.onPageLoad(Some(0), NormalMode).url)

          val result = route(application, request).value

          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe problemRoute.JourneyRecoveryController.onPageLoad().url
        }
      }

      "must redirect back to AddAnother without a page when pagination has not started but page 1 is provided for a GET" in {

        val goodsReferencesList: List[String] = (1 to 25).map { number =>
          number.toString
        }.toList

        val userAnswers = emptyUserAnswers
          .set(DiscrepancyReference, goodsReferencesList)
          .success
          .value

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(application) {
          val request = FakeRequest(GET, createRoute.AddAnotherGoodsReferenceController.onPageLoad(Some(1), NormalMode).url)

          val result = route(application, request).value

          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe createRoute.AddAnotherGoodsReferenceController.onPageLoad(None, NormalMode).url
        }
      }

      "must redirect back to AddAnother with page 1 when pagination has started and no page is provided for a GET" in {

        val goodsReferencesList: List[String] = (1 to 26).map { number =>
          number.toString
        }.toList

        val userAnswers = emptyUserAnswers
          .set(DiscrepancyReference, goodsReferencesList)
          .success
          .value

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(application) {
          val request = FakeRequest(GET, createRoute.AddAnotherGoodsReferenceController.onPageLoad(None, NormalMode).url)

          val result = route(application, request).value

          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe createRoute.AddAnotherGoodsReferenceController.onPageLoad(Some(1), NormalMode).url
        }
      }

      "must redirect to Journey Recovery when there are no user answers data" in {

        val application = applicationBuilder(userAnswers = None)
          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(application) {
          val request = FakeRequest(GET, createRoute.AddAnotherGoodsReferenceController.onPageLoad(None, NormalMode).url)

          val result = route(application, request).value

          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe problemRoute.JourneyRecoveryController.onPageLoad().url
        }
      }
    }

    "onSubmit" - {
      "must redirect to next DiscrepancyReferencePage at the correct index when Yes is selected" in {

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(application) {
          val request =
            FakeRequest(POST, createRoute.AddAnotherGoodsReferenceController.onSubmit(None, NormalMode).url)
              .withFormUrlEncodedBody("value" -> "true")

          val result = route(application, request).value

          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe createRoute.DiscrepancyReferenceController.onPageLoad(4, NormalMode).url
        }
      }

      "must redirect to next DiscrepancyReferencePage at the correct index and preserve mode in CheckMode when answer is Yes" in {

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(application) {
          val request =
            FakeRequest(POST, createRoute.AddAnotherGoodsReferenceController.onSubmit(None, CheckMode).url).withFormUrlEncodedBody("value" -> "true")

          val result = route(application, request).value

          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe createRoute.DiscrepancyReferenceController.onPageLoad(4, CheckMode).url
        }
      }

      "must redirect to DiscrepancyTransportMeansPage when No is selected" in {

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(application) {
          val request =
            FakeRequest(POST, createRoute.AddAnotherGoodsReferenceController.onSubmit(None, NormalMode).url)
              .withFormUrlEncodedBody("value" -> "false")

          val result = route(application, request).value

          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe createRoute.DiscrepancyTransportMeansController.onPageLoad(NormalMode).url
        }
      }

      "must redirect to next DiscrepancyTransportMeansPage and preserve the mode in CheckMode when answer is No" in {

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(application) {
          val request =
            FakeRequest(POST, createRoute.AddAnotherGoodsReferenceController.onSubmit(None, CheckMode).url)
              .withFormUrlEncodedBody("value" -> "false")

          val result = route(application, request).value

          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe createRoute.DiscrepancyTransportMeansController.onPageLoad(CheckMode).url
        }
      }

      "must return a Bad Request when no option chosen and max limit not reached while preserving pagination page number" in {

        val goodsReferencesList: List[String] = (1 to 51).map { number =>
          number.toString
        }.toList

        val userAnswers = emptyUserAnswers
          .set(DiscrepancyReference, goodsReferencesList)
          .success
          .value

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(application) {
          val request = FakeRequest(POST, createRoute.AddAnotherGoodsReferenceController.onSubmit(Some(2), NormalMode).url)

          val result = route(application, request).value

          status(result) shouldBe BAD_REQUEST
          val body = contentAsString(result)
          body should include("Select yes if you want to add another Declaration Goods Reference")
          body should not include "Declaration Goods Reference 25"
          body should include("Declaration Goods Reference 26")
          body should include("Declaration Goods Reference 50")
          body should not include "Declaration Goods Reference 51"
        }
      }

      "must redirect to DiscrepancyTransportMeansPage when maximum items reached" in {

        val userAnswers = emptyUserAnswers
          .set(DiscrepancyReference, goodsReferencesList)
          .success
          .value

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(application) {
          val request = FakeRequest(POST, createRoute.AddAnotherGoodsReferenceController.onSubmit(None, NormalMode).url)

          val result = route(application, request).value

          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe createRoute.DiscrepancyTransportMeansController.onPageLoad(NormalMode).url
        }
      }

      "must redirect to DiscrepancyReferencePage at the first index if zero items added for a POST" in {

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(application) {
          val request = FakeRequest(POST, createRoute.AddAnotherGoodsReferenceController.onPageLoad(None, NormalMode).url)

          val result = route(application, request).value

          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe createRoute.DiscrepancyReferenceController.onPageLoad(1, NormalMode).url
        }
      }

      "must redirect to Journey Recovery if the page doesn't exist for a POST" in {

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(application) {
          val request = FakeRequest(POST, createRoute.AddAnotherGoodsReferenceController.onPageLoad(Some(2), NormalMode).url)

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
          val request = FakeRequest(POST, createRoute.AddAnotherGoodsReferenceController.onSubmit(None, NormalMode).url)

          val result = route(application, request).value

          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe problemRoute.JourneyRecoveryController.onPageLoad().url
        }
      }
    }
  }
}
