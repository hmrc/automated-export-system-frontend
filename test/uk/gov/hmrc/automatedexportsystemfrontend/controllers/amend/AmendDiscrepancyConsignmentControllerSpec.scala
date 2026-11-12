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

package uk.gov.hmrc.automatedexportsystemfrontend.controllers.amend

import org.jsoup.Jsoup
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.libs.json.Json
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.auth.core.AuthConnector
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.amend.routes as amendRoute
import uk.gov.hmrc.automatedexportsystemfrontend.controllers.problem.routes as problemRoute
import uk.gov.hmrc.automatedexportsystemfrontend.helpers.SpecBase
import uk.gov.hmrc.automatedexportsystemfrontend.models.codelists.CodeListEntry
import uk.gov.hmrc.automatedexportsystemfrontend.models.{NormalMode, UserAnswers}
import uk.gov.hmrc.automatedexportsystemfrontend.navigation.{AmendNavigator, FakeAmendNavigator}
import uk.gov.hmrc.automatedexportsystemfrontend.pages.amend.AmendDiscrepancyConsignmentPage
import uk.gov.hmrc.automatedexportsystemfrontend.repositories.SessionRepository
import uk.gov.hmrc.automatedexportsystemfrontend.services.CodeListService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future
import scala.jdk.CollectionConverters.*

class AmendDiscrepancyConsignmentControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  val submissionId = "12345"

  lazy val amendDiscrepancyConsignmentRoute: String =
    amendRoute.AmendDiscrepancyConsignmentController.onPageLoad(NormalMode, submissionId).url

  private val transportModes = Seq(
    CodeListEntry("1", "Sea transport", Json.obj()),
    CodeListEntry("3", "Road transport", Json.obj()),
    CodeListEntry("7", "Fixed transport installations", Json.obj())
  )

  private def stubCodeListService(): CodeListService = {
    val service = mock[CodeListService]

    when(service.getTransportModes()(using any[HeaderCarrier]()))
      .thenReturn(Future.successful(transportModes))

    service
  }

  "AmendDiscrepancyConsignment Controller" - {

    "must return OK and display the transport modes from the codelist" in {
      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[AuthConnector].toInstance(mockAuthConnector), bind[CodeListService].toInstance(stubCodeListService()))
        .build()

      running(application) {
        val request = FakeRequest(GET, amendDiscrepancyConsignmentRoute)
        val result = route(application, request).value

        status(result) shouldBe OK

        val document = Jsoup.parse(contentAsString(result))
        document.select("h1").text() shouldBe "How will the goods cross the border?"

        val options = document
          .select("select[name=value] option")
          .asScala
          .filter(_.attr("value").nonEmpty)
          .map(option => option.attr("value") -> option.text())
          .toSeq

        options shouldBe Seq("1" -> "Sea transport", "3" -> "Road transport", "7" -> "Fixed transport installations")
      }
    }

    "must select the saved transport mode on a GET" in {
      val userAnswers = UserAnswers(userAnswersId)
        .set(AmendDiscrepancyConsignmentPage(submissionId), "7")
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[AuthConnector].toInstance(mockAuthConnector), bind[CodeListService].toInstance(stubCodeListService()))
        .build()

      running(application) {
        val request = FakeRequest(GET, amendDiscrepancyConsignmentRoute)
        val result = route(application, request).value

        status(result) shouldBe OK

        val document = Jsoup.parse(contentAsString(result))
        val selectedOption = document.select("select[name=value] option[selected]")

        selectedOption.size() shouldBe 1
        selectedOption.attr("value") shouldBe "7"
        selectedOption.text() shouldBe "Fixed transport installations"
      }
    }

    "must redirect to the next page when valid data is submitted" in {
      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())).thenReturn(Future.successful(true))

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(
          bind[AmendNavigator].toInstance(new FakeAmendNavigator(onwardRoute)),
          bind[SessionRepository].toInstance(mockSessionRepository),
          bind[AuthConnector].toInstance(mockAuthConnector),
          bind[CodeListService].toInstance(stubCodeListService())
        )
        .build()

      running(application) {
        val request = FakeRequest(POST, amendDiscrepancyConsignmentRoute)
          .withFormUrlEncodedBody("value" -> "1")

        val result = route(application, request).value

        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe onwardRoute.url
      }
    }

    "must return a Bad Request and errors when an unknown transport code is submitted" in {
      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[AuthConnector].toInstance(mockAuthConnector), bind[CodeListService].toInstance(stubCodeListService()))
        .build()

      running(application) {
        val request = FakeRequest(POST, amendDiscrepancyConsignmentRoute)
          .withFormUrlEncodedBody("value" -> "99")

        val result = route(application, request).value

        status(result) shouldBe BAD_REQUEST

        val body = contentAsString(result)
        body should include("There is a problem")
        body should include("Select a valid mode of transport")
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {
      val application = applicationBuilder(userAnswers = None)
        .overrides(bind[AuthConnector].toInstance(mockAuthConnector), bind[CodeListService].toInstance(stubCodeListService()))
        .build()

      running(application) {
        val request = FakeRequest(GET, amendDiscrepancyConsignmentRoute)
        val result = route(application, request).value

        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe
          problemRoute.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in {
      val application = applicationBuilder(userAnswers = None)
        .overrides(bind[AuthConnector].toInstance(mockAuthConnector), bind[CodeListService].toInstance(stubCodeListService()))
        .build()

      running(application) {
        val request = FakeRequest(POST, amendDiscrepancyConsignmentRoute)
          .withFormUrlEncodedBody("value" -> "1")

        val result = route(application, request).value

        status(result) shouldBe SEE_OTHER
        redirectLocation(result).value shouldBe
          problemRoute.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
