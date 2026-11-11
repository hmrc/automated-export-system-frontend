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

import com.github.tomakehurst.wiremock.client.WireMock.*
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.Helpers.*
import uk.gov.hmrc.automatedexportsystemfrontend.helpers.SpecBase
import uk.gov.hmrc.automatedexportsystemfrontend.models.codelists.CodeListEntry
import uk.gov.hmrc.http.UpstreamErrorResponse

class CodeListConnectorSpec extends SpecBase with WireMockHelper {

  private val endpoint =
    "/automated-export-system/codelists/transportMode"

  private def application: Application =
    new GuiceApplicationBuilder()
      .configure(
        "microservice.services.automated-export-system.host" -> "localhost",
        "microservice.services.automated-export-system.port" -> server.port()
      )
      .build()

  "getTransportModes" - {

    "must return entries from the backend JSON response" in {
      val app = application

      running(app) {
        server.stubFor(
          get(urlEqualTo(endpoint))
            .willReturn(
              aResponse()
                .withStatus(OK)
                .withHeader("Content-Type", "application/json")
                .withBody("""[
                    |  {
                    |    "key": "1",
                    |    "value": "Sea transport",
                    |    "properties": {}
                    |  },
                    |  {
                    |    "key": "3",
                    |    "value": "Road transport",
                    |    "properties": {"exampleFlag": true}
                    |  }
                    |]""".stripMargin)
            )
        )

        val connector = app.injector.instanceOf[CodeListConnector]

        connector.getTransportModes().futureValue shouldBe Seq(
          CodeListEntry("1", "Sea transport", Json.obj()),
          CodeListEntry("3", "Road transport", Json.obj("exampleFlag" -> true))
        )

        server.verify(getRequestedFor(urlEqualTo(endpoint)))
      }
    }

    "must return an empty sequence when the backend returns no entries" in {
      val app = application

      running(app) {
        server.stubFor(
          get(urlEqualTo(endpoint))
            .willReturn(
              aResponse()
                .withStatus(OK)
                .withHeader("Content-Type", "application/json")
                .withBody("[]")
            )
        )

        val connector = app.injector.instanceOf[CodeListConnector]

        connector.getTransportModes().futureValue shouldBe empty
      }
    }

    "must fail when the backend returns a server error" in {
      val app = application

      running(app) {
        server.stubFor(
          get(urlEqualTo(endpoint))
            .willReturn(
              aResponse()
                .withStatus(INTERNAL_SERVER_ERROR)
                .withBody("Backend unavailable")
            )
        )

        val connector = app.injector.instanceOf[CodeListConnector]

        val error =
          connector.getTransportModes().failed.futureValue

        error shouldBe a[UpstreamErrorResponse]
        error
          .asInstanceOf[UpstreamErrorResponse]
          .statusCode shouldBe INTERNAL_SERVER_ERROR
      }
    }

    "must fail when the JSON does not match the expected entry structure" in {
      val app = application

      running(app) {
        server.stubFor(
          get(urlEqualTo(endpoint))
            .willReturn(
              aResponse()
                .withStatus(OK)
                .withHeader("Content-Type", "application/json")
                .withBody("""[{"key":"1"}]""")
            )
        )

        val connector = app.injector.instanceOf[CodeListConnector]

        connector.getTransportModes().failed.futureValue
      }
    }
  }
}
