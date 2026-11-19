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
import uk.gov.hmrc.automatedexportsystemfrontend.models.{CheckMode, NormalMode, UserAnswers, WhatHasChangedDetails}
import uk.gov.hmrc.automatedexportsystemfrontend.pages.create.DiscrepancyGoodsPage
import uk.gov.hmrc.automatedexportsystemfrontend.queries.DiscrepancyGoods

class AddAnotherGoodsItemControllerSpec extends SpecBase with MockitoSugar {

  val maxGoodsItem: Int = DiscrepancyGoods.maxDiscrepancyGoods

  val userAnswers: UserAnswers = emptyUserAnswers
    .set(DiscrepancyGoodsPage(1), WhatHasChangedDetails(Some(1), Some("5GB000000000000-123451"), "20", "10"))
    .flatMap(_.set(DiscrepancyGoodsPage(2), WhatHasChangedDetails(Some(2), Some("5GB000000000000-123452"), "20", "10")))
    .flatMap(_.set(DiscrepancyGoodsPage(3), WhatHasChangedDetails(Some(3), Some("5GB000000000000-123453"), "20", "10")))
    .success
    .value

  "AddAnotherGoodsItem Controller" - {

    "onPageLoad" - {
      "must return OK and the correct view for a GET when allowed to add more items" in {

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(application) {
          val request = FakeRequest(GET, createRoute.AddAnotherGoodsItemController.onPageLoad(None, NormalMode).url)

          val result = route(application, request).value

          status(result) mustEqual OK
          val body = contentAsString(result)
          body should include("You have added 3 goods items")
          body should include("Goods item 1")
          body should include("""Change<span class="govuk-visually-hidden"> goods item 1</span>""")
          body should include("""Remove<span class="govuk-visually-hidden"> goods item 1</span>""")
          body should include("Goods item 2")
          body should include("""Change<span class="govuk-visually-hidden"> goods item 2</span>""")
          body should include("""Remove<span class="govuk-visually-hidden"> goods item 2</span>""")
          body should include("Goods item 3")
          body should include("""Change<span class="govuk-visually-hidden"> goods item 3</span>""")
          body should include("""Remove<span class="govuk-visually-hidden"> goods item 3</span>""")
          body should include("You can add up to <strong>996</strong> more goods items.")
          body should include("<strong>Do you need to add another goods item?</strong>")
          body should include("Yes")
          body should include("No")
          body should include("Continue")
        }
      }

      "must return OK and the correct view for a GET without pagination" in {

        val goodsItemsList = (1 to 25).map { number =>
          WhatHasChangedDetails(Some(number), Some(s"5GB0000000-$number"), number.toString, number.toString)
        }.toList

        val userAnswers = emptyUserAnswers
          .set(DiscrepancyGoods, goodsItemsList)
          .success
          .value

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(application) {
          val request = FakeRequest(GET, createRoute.AddAnotherGoodsItemController.onPageLoad(None, NormalMode).url)

          val result = route(application, request).value

          status(result) mustEqual OK
          val body = contentAsString(result)
          body should include("You have added 25 goods items")
          (1 to 25).foreach { number =>
            body should include(s"Goods item $number")
            body should include(s"""Change<span class="govuk-visually-hidden"> goods item $number</span>""")
            body should include(s"""Remove<span class="govuk-visually-hidden"> goods item $number</span>""")
          }
          body should not include "Showing 1-25 of 25 items"
          body should not include "Previous"
          body should not include s"""href="${createRoute.AddAnotherGoodsItemController.onPageLoad(Some(1), NormalMode).url}""""
          body should not include s"""href="${createRoute.AddAnotherGoodsItemController.onPageLoad(Some(2), NormalMode).url}""""
          body should not include "Next"
          body should include("You can add up to <strong>974</strong> more goods items.")
          body should include("<strong>Do you need to add another goods item?</strong>")
          body should include("Yes")
          body should include("No")
          body should include("Continue")
        }
      }

      "must redirect to DiscrepancyGoodsPage at the first index if zero items added for a GET" in {

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(application) {
          val request = FakeRequest(GET, createRoute.AddAnotherGoodsItemController.onPageLoad(None, NormalMode).url)

          val result = route(application, request).value

          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe createRoute.DiscrepancyGoodsController.onPageLoad(1, NormalMode).url
        }
      }

      "must redirect to Journey Recovery if the page doesn't exist for a GET" in {

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(application) {
          val request = FakeRequest(GET, createRoute.AddAnotherGoodsItemController.onPageLoad(Some(0), NormalMode).url)

          val result = route(application, request).value

          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe problemRoute.JourneyRecoveryController.onPageLoad().url
        }
      }

      "must redirect back to AddAnother without a page when pagination has not started but page 1 is provided for a GET" in {

        val goodsItemsList = (1 to 25).map { number =>
          WhatHasChangedDetails(Some(number), Some(s"5GB0000000-$number"), number.toString, number.toString)
        }.toList

        val userAnswers = emptyUserAnswers
          .set(DiscrepancyGoods, goodsItemsList)
          .success
          .value

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(application) {
          val request = FakeRequest(GET, createRoute.AddAnotherGoodsItemController.onPageLoad(Some(1), NormalMode).url)

          val result = route(application, request).value

          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe createRoute.AddAnotherGoodsItemController.onPageLoad(None, NormalMode).url
        }
      }

      "must redirect back to AddAnother with page 1 when pagination has started and no page is provided for a GET" in {

        val goodsItemsList = (1 to 26).map { number =>
          WhatHasChangedDetails(Some(number), Some(s"5GB0000000-$number"), number.toString, number.toString)
        }.toList

        val userAnswers = emptyUserAnswers
          .set(DiscrepancyGoods, goodsItemsList)
          .success
          .value

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(application) {
          val request = FakeRequest(GET, createRoute.AddAnotherGoodsItemController.onPageLoad(None, NormalMode).url)

          val result = route(application, request).value

          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe createRoute.AddAnotherGoodsItemController.onPageLoad(Some(1), NormalMode).url
        }
      }

      "must redirect to Journey Recovery when there are no user answers data" in {

        val application = applicationBuilder(userAnswers = None)
          .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
          .build()

        running(application) {
          val request = FakeRequest(GET, createRoute.AddAnotherGoodsItemController.onPageLoad(None, NormalMode).url)

          val result = route(application, request).value

          status(result) shouldBe SEE_OTHER
          redirectLocation(result).value shouldBe problemRoute.JourneyRecoveryController.onPageLoad().url
        }
      }

      "onSubmit" - {
        "must redirect to next DiscrepancyGoodsPage at the correct index when Yes is selected" in {

          val application = applicationBuilder(userAnswers = Some(userAnswers))
            .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
            .build()

          running(application) {
            val request =
              FakeRequest(POST, createRoute.AddAnotherGoodsItemController.onSubmit(None, NormalMode).url)
                .withFormUrlEncodedBody("value" -> "true")

            val result = route(application, request).value

            status(result) shouldBe SEE_OTHER
            redirectLocation(result).value shouldBe createRoute.DiscrepancyGoodsController.onPageLoad(4, NormalMode).url
          }
        }

        "must redirect to next DiscrepancyGoodsPage at the correct index and preserve mode in CheckMode when answer is Yes" in {

          val application = applicationBuilder(userAnswers = Some(userAnswers))
            .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
            .build()

          running(application) {
            val request =
              FakeRequest(POST, createRoute.AddAnotherGoodsItemController.onSubmit(None, CheckMode).url).withFormUrlEncodedBody("value" -> "true")

            val result = route(application, request).value

            status(result) shouldBe SEE_OTHER
            redirectLocation(result).value shouldBe createRoute.DiscrepancyGoodsController.onPageLoad(4, CheckMode).url
          }
        }

        "must redirect to Discrepancy packing when No is selected" in {
          val application = applicationBuilder(userAnswers = Some(userAnswers))
            .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
            .build()

          running(application) {
            val request =
              FakeRequest(POST, createRoute.AddAnotherGoodsItemController.onSubmit(None, NormalMode).url)
                .withFormUrlEncodedBody("value" -> "false")

            val result = route(application, request).value

            status(result) shouldBe SEE_OTHER
            redirectLocation(result).value shouldBe createRoute.DiscrepancyPackingController.onPageLoad(1, NormalMode).url
          }
        }

// TODO:fix after pagination        "must return a Bad Request when no option chosen and max limit not reached while preserving pagination page number" in {
//
//          val goodsItemsList = (1 to 51).map { number =>
//            WhatHasChangedDetails(Some(number), Some(s"5GB0000000-$number"), number.toString, number.toString)
//          }.toList
//
//          val userAnswers = emptyUserAnswers
//            .set(DiscrepancyGoods, goodsItemsList)
//            .success
//            .value
//
//          val application = applicationBuilder(userAnswers = Some(userAnswers))
//            .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
//            .build()
//
//          running(application) {
//            val request = FakeRequest(POST, createRoute.AddAnotherGoodsItemController.onSubmit(Some(2), NormalMode).url)
//
//            val result = route(application, request).value
//
//            status(result) shouldBe BAD_REQUEST
//            val body = contentAsString(result)
//            body should include("Select yes if you want to add another goods item")
//            body should not include "Goods item 25"
//            body should include("Goods item 26")
//            body should include("Goods item 50")
//            body should not include "Goods item 51"
//          }
//        }

        "must redirect to packaging page when maximum items reached" in {

          val goodsItemsList = (1 to maxGoodsItem).map { number =>
            WhatHasChangedDetails(Some(number), Some(s"5GB0000000-$number"), number.toString, number.toString)
          }.toList

          val userAnswers = emptyUserAnswers
            .set(DiscrepancyGoods, goodsItemsList)
            .success
            .value

          val application = applicationBuilder(userAnswers = Some(userAnswers))
            .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
            .build()

          running(application) {
            val request = FakeRequest(POST, createRoute.AddAnotherGoodsItemController.onSubmit(None, NormalMode).url)

            val result = route(application, request).value

            status(result) shouldBe SEE_OTHER
            redirectLocation(result).value shouldBe createRoute.DiscrepancyPackingController.onPageLoad(1, NormalMode).url
          }
        }

        "must redirect to DiscrepancyGoodsPage at the first index if zero items added for a POST" in {

          val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
            .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
            .build()

          running(application) {
            val request = FakeRequest(POST, createRoute.AddAnotherGoodsItemController.onPageLoad(None, NormalMode).url)

            val result = route(application, request).value

            status(result) shouldBe SEE_OTHER
            redirectLocation(result).value shouldBe createRoute.DiscrepancyGoodsController.onPageLoad(1, NormalMode).url
          }
        }

        "must redirect to Journey Recovery if the page doesn't exist for a POST" in {

          val application = applicationBuilder(userAnswers = Some(userAnswers))
            .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
            .build()

          running(application) {
            val request = FakeRequest(POST, createRoute.AddAnotherGoodsItemController.onPageLoad(Some(2), NormalMode).url)

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
            val request = FakeRequest(POST, createRoute.AddAnotherGoodsItemController.onSubmit(None, NormalMode).url)

            val result = route(application, request).value

            status(result) shouldBe SEE_OTHER
            redirectLocation(result).value shouldBe problemRoute.JourneyRecoveryController.onPageLoad().url
          }
        }
      }
    }
  }
}
