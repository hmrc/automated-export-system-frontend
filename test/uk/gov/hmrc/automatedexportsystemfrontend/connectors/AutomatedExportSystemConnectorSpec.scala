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

package uk.gov.hmrc.automatedexportsystemfrontend.connectors

import uk.gov.hmrc.automatedexportsystemfrontend.helpers.SpecBase
import com.github.tomakehurst.wiremock.client.WireMock.*
import org.apache.pekko.Done
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers.*
import play.api.Application
import uk.gov.hmrc.http.UpstreamErrorResponse

class AutomatedExportSystemConnectorSpec extends SpecBase with WireMockHelper {

  val url = "/automated-export-system/message"
  val cancelUrl = "/automated-export-system/cancel/test-submission-id"
  val submissionUrl = "/automated-export-system/submission/test-submission-id"

  private def application: Application =
    new GuiceApplicationBuilder()
      .configure("microservice.services.automated-export-system.port" -> server.port)
      .build()

  "submitIE507a" - {

    "must return Done when ACCEPTED returned" in {

      val app = application
      running(app) {
        val connector = app.injector.instanceOf[AutomatedExportSystemConnector]
        server.stubFor(
          post(urlEqualTo(url))
            .willReturn(aResponse.withStatus(ACCEPTED))
        )

        val result = connector
          .submitIE507a("someXMl")
          .futureValue

        result shouldBe an[Done]
      }

    }

    "must return an upstream error response when anything else" in {

      val app = application
      running(app) {
        val connector = app.injector.instanceOf[AutomatedExportSystemConnector]
        server.stubFor(
          post(urlEqualTo(url))
            .willReturn(aResponse.withStatus(400).withBody("boom"))
        )

        val result = connector
          .submitIE507a("someXml")
          .failed
          .futureValue

        result shouldBe an[UpstreamErrorResponse]
      }
    }
  }

  "getSubmission" - {

    "must return a full submission when OK returned" in {

      val app = application

      running(app) {

        val connector =
          app.injector.instanceOf[AutomatedExportSystemConnector]

        server.stubFor(
          get(urlEqualTo(submissionUrl))
            .willReturn(
              aResponse()
                .withStatus(OK)
                .withBody("""
                    |<Submission>
                    |  <submissionId>test-submission-id</submissionId>
                    |  <ExportOperation>
                    |    <type>1</type>
                    |    <MRN>26GB0000X6524786A9</MRN>
                    |    <discrepanciesExist>0</discrepanciesExist>
                    |    <splitIndicator>0</splitIndicator>
                    |  </ExportOperation>
                    |  <CustomsOfficeOfExitActual>
                    |    <referenceNumber>GB000051</referenceNumber>
                    |  </CustomsOfficeOfExitActual>
                    |  <updatedAt>2026-08-03T00:00:00</updatedAt>
                    |</Submission>
                    |""".stripMargin)
            )
        )

        val result =
          connector
            .getSingleSubmission("test-submission-id")
            .futureValue

        result.submissionId shouldBe "test-submission-id"
        result.exportOperation.exportOperationType shouldBe "1"
        result.exportOperation.mrn shouldBe "26GB0000X6524786A9"
        result.exportOperation.discrepanciesExist shouldBe 0
        result.exportOperation.splitIndicator shouldBe 0
        result.customsOfficeOfExitActual.referenceNumber shouldBe "GB000051"
        result.goodsShipment shouldBe None
      }
    }

    "must return an upstream error response when anything else is returned" in {

      val app = application

      running(app) {

        val connector =
          app.injector.instanceOf[AutomatedExportSystemConnector]

        server.stubFor(
          get(urlEqualTo(submissionUrl))
            .willReturn(
              aResponse()
                .withStatus(400)
                .withBody("boom")
            )
        )

        val result =
          connector
            .getSingleSubmission("test-submission-id")
            .failed
            .futureValue

        result shouldBe an[UpstreamErrorResponse]
      }
    }
  }

  "cancelSubmission" - {

    "must return Done when NO_CONTENT returned" in {

      val app = application

      running(app) {

        val connector =
          app.injector.instanceOf[AutomatedExportSystemConnector]

        server.stubFor(
          get(urlEqualTo(cancelUrl))
            .willReturn(aResponse.withStatus(NO_CONTENT))
        )

        val result =
          connector
            .cancelSubmission("test-submission-id")
            .futureValue

        result shouldBe Done
      }
    }

    "must return an upstream error response when anything else is returned" in {

      val app = application

      running(app) {

        val connector =
          app.injector.instanceOf[AutomatedExportSystemConnector]

        server.stubFor(
          get(urlEqualTo(cancelUrl))
            .willReturn(aResponse.withStatus(400).withBody("boom"))
        )

        val result =
          connector
            .cancelSubmission("test-submission-id")
            .failed
            .futureValue

        result shouldBe an[UpstreamErrorResponse]
      }
    }
  }
}
