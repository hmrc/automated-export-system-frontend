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

class GoodsReferenceCYAControllerSpec extends SpecBase with MockitoSugar {

  val userAnswers: UserAnswers = emptyUserAnswers
    .set(DiscrepancyReferencePage(1), "reference1")
    .flatMap(_.set(DiscrepancyReferencePage(2), "reference2"))
    .flatMap(_.set(DiscrepancyReferencePage(3), "reference3"))
    .success
    .value

  "GoodsReferenceCYA Controller" - {

    "must return OK and the correct view for a GET with an existing index in NormalMode" in {

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
        .build()

      running(application) {
        val request = FakeRequest(GET, createRoute.GoodsReferenceCYAController.onPageLoad(2, NormalMode).url)

        val result = route(application, request).value

        status(result) mustEqual OK
        val body = contentAsString(result)
        body should include("Declaration Goods Reference 2")
        body should include("Check your answers")
        body should include("Declaration Goods Reference")
        body should include("reference2")
//        body should include(s"""href="${createRoute.AddAnotherGoodsReferenceController.onPageLoad(None, NormalMode).url}"""") TODO: Uncomment when page built
      }
    }

    "must return OK and the correct view for a GET with an existing index in CheckMode" in {

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
        .build()

      running(application) {
        val request = FakeRequest(GET, createRoute.GoodsReferenceCYAController.onPageLoad(2, CheckMode).url)

        val result = route(application, request).value

        status(result) mustEqual OK
        val body = contentAsString(result)
        body should include("Declaration Goods Reference 2")
        body should include("Check your answers")
        body should include("Declaration Goods Reference")
        body should include("reference2")
//        body should include(s"""href="${createRoute.AddAnotherGoodsReferenceController.onPageLoad(None, CheckMode).url}"""") TODO: Uncomment when page built
      }
    }

    "must redirect to Journey Recovery for a GET if it is an empty index" in {

      val userAnswers: UserAnswers = emptyUserAnswers
        .set(DiscrepancyReferencePage(1), "reference1")
        .get

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
        .build()

      running(application) {
        val request = FakeRequest(GET, createRoute.GoodsReferenceCYAController.onPageLoad(2, NormalMode).url)

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
        val request = FakeRequest(GET, createRoute.GoodsReferenceCYAController.onPageLoad(1, NormalMode).url)

        val result = route(application, request).value

        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe problemRoute.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
