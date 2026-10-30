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
import uk.gov.hmrc.automatedexportsystemfrontend.models.{CheckMode, DocumentDetails, NormalMode, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.pages.create.DiscrepancyTransportDocPage

class TransportDocCYAControllerSpec extends SpecBase with MockitoSugar {

  val userAnswers: UserAnswers = emptyUserAnswers
    .set(DiscrepancyTransportDocPage(1), DocumentDetails(Some(1), Some(1)))
    .flatMap(_.set(DiscrepancyTransportDocPage(2), DocumentDetails(Some(2), Some(2))))
    .flatMap(_.set(DiscrepancyTransportDocPage(3), DocumentDetails(Some(3), Some(3))))
    .success
    .value

  "TransportDocCYA Controller" - {

    "must return OK and the correct view for a GET with an existing index in NormalMode" in {

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
        .build()

      running(application) {
        val request = FakeRequest(GET, createRoute.TransportDocCYAController.onPageLoad(2, NormalMode).url)

        val result = route(application, request).value

        status(result) mustEqual OK
        val body = contentAsString(result)
        body should include("Transport document 2")
        body should include("Check your answers")
        body should include("Transport document type")
        body should include("2")
        body should include("Transport document reference")
        body should include("2")

        //        body should include(s"""href="${createRoute.AddAnotherPackagingDetailController.onPageLoad(None, NormalMode).url}"""")
        // TODO implement above for AddAnotherTransportDoc when this is created in a future ticket
      }
    }

    "must return OK and the correct view for a GET with an existing index in CheckMode" in {

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
        .build()

      running(application) {
        val request = FakeRequest(GET, createRoute.TransportDocCYAController.onPageLoad(2, CheckMode).url)

        val result = route(application, request).value

        status(result) mustEqual OK
        val body = contentAsString(result)
        body should include("Transport document 2")
        body should include("Check your answers")
        body should include("Transport document type")
        body should include("2")
        body should include("Transport document reference")
        body should include("2")

        //        body should include(s"""href="${createRoute.AddAnotherPackagingDetailController.onPageLoad(None, NormalMode).url}"""")
        // TODO implement above for AddAnotherTransportDoc when this is created in a future ticket      }
      }
    }

    "must redirect to Journey Recovery for a GET if it is an empty index" in {

      val userAnswers = emptyUserAnswers
        .set(DiscrepancyTransportDocPage(1), DocumentDetails(Some(1), Some(1)))
        .get

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[uk.gov.hmrc.auth.core.AuthConnector].toInstance(mockAuthConnector))
        .build()

      running(application) {
        val request = FakeRequest(GET, createRoute.TransportDocCYAController.onPageLoad(2, NormalMode).url)

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
        val request = FakeRequest(GET, createRoute.TransportDocCYAController.onPageLoad(1, NormalMode).url)

        val result = route(application, request).value

        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe problemRoute.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
