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
import uk.gov.hmrc.automatedexportsystemfrontend.models.PackingDetails
import uk.gov.hmrc.automatedexportsystemfrontend.pages.create.DiscrepancyPackingPage

class PackagingDetailsCYAControllerSpec extends SpecBase with MockitoSugar {

  "PackagingDetailsCYA Controller" - {

    "must return OK and the correct view for a GET with an existing index" in {

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
        val request = FakeRequest(GET, createRoute.PackagingDetailsCYAController.onPageLoad(2).url)

        val result = route(application, request).value

        status(result) mustEqual OK
        val body = contentAsString(result)
        body should include("Packaging detail 2")
        body should include("Check your answers")
        body should include("Package type")
        body should include("CT")
        body should include("Number of packages")
        body should include("2")
        body should include("Shipping marks")
        body should include("MARKS2")
        body should include(createRoute.CYASubmissionController.onPageLoad().url)
      }
    }

    "must redirect to Journey Recovery for a GET if it is an empty index" in {

      val userAnswers = emptyUserAnswers
        .set(DiscrepancyPackingPage(1), PackingDetails("BX", 1, "MARKS123"))
        .get

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
        .build()

      running(application) {
        val request = FakeRequest(GET, createRoute.PackagingDetailsCYAController.onPageLoad(2).url)

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
        val request = FakeRequest(GET, createRoute.PackagingDetailsCYAController.onPageLoad(1).url)

        val result = route(application, request).value

        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe problemRoute.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
