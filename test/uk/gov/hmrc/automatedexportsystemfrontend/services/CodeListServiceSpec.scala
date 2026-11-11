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

package uk.gov.hmrc.automatedexportsystemfrontend.services

import org.mockito.Mockito.{reset, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar
import play.api.libs.json.Json
import uk.gov.hmrc.automatedexportsystemfrontend.connectors.CodeListConnector
import uk.gov.hmrc.automatedexportsystemfrontend.helpers.SpecBase
import uk.gov.hmrc.automatedexportsystemfrontend.models.codelists.CodeListEntry

import scala.concurrent.{ExecutionContext, Future}

class CodeListServiceSpec extends SpecBase with MockitoSugar with BeforeAndAfterEach {

  private given ExecutionContext = ExecutionContext.global

  private val connector = mock[CodeListConnector]

  private val sea =
    CodeListEntry("1", "Sea transport", Json.obj())

  private val road =
    CodeListEntry("3", "Road transport", Json.obj())

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(connector)
  }

  "getTransportModes" - {

    "must return the backend entries" in {
      when(connector.getTransportModes())
        .thenReturn(Future.successful(Seq(sea, road)))

      val service = new CodeListService(connector)

      service.getTransportModes().futureValue shouldBe Seq(sea, road)
    }

    "must reject an empty list" in {
      when(connector.getTransportModes())
        .thenReturn(Future.successful(Seq.empty))

      val service = new CodeListService(connector)

      service.getTransportModes().failed.futureValue.getMessage should include("TransportMode codelist is empty")
    }

    "must reject duplicate codes" in {
      when(connector.getTransportModes())
        .thenReturn(Future.successful(Seq(sea, sea.copy(value = "Duplicate"))))

      val service = new CodeListService(connector)

      service.getTransportModes().failed.futureValue.getMessage should include("duplicate codes")
    }

    "must reject an empty code" in {
      when(connector.getTransportModes())
        .thenReturn(Future.successful(Seq(sea.copy(key = " "))))

      val service = new CodeListService(connector)

      service.getTransportModes().failed.futureValue.getMessage should include("empty code or description")
    }

    "must reject an empty description" in {
      when(connector.getTransportModes())
        .thenReturn(Future.successful(Seq(sea.copy(value = " "))))

      val service = new CodeListService(connector)

      service.getTransportModes().failed.futureValue.getMessage should include("empty code or description")
    }

    "must propagate connector failures" in {
      val failure = new RuntimeException("Backend unavailable")

      when(connector.getTransportModes())
        .thenReturn(Future.failed(failure))

      val service = new CodeListService(connector)

      service.getTransportModes().failed.futureValue shouldBe failure
    }
  }

  "selectItems" - {

    "must display descriptions and use codes as submitted values" in {
      val items =
        CodeListService.selectItems(Seq(sea, road), "Select a mode of transport")

      items.map(_.text) shouldBe Seq("Select a mode of transport", "Sea transport", "Road transport")

      items.map(_.value) shouldBe Seq(Some(""), Some("1"), Some("3"))
    }
  }

  "description" - {

    "must resolve a saved code to its description" in {
      CodeListService.description("1", Seq(sea, road)) shouldBe
        Some("Sea transport")
    }

    "must return None for an unknown code" in {
      CodeListService.description("99", Seq(sea, road)) shouldBe None
    }
  }
}
