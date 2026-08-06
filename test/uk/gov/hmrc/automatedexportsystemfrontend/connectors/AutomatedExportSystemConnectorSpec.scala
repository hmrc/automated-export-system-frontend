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
}
