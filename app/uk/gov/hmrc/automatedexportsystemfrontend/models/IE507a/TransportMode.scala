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

package uk.gov.hmrc.automatedexportsystemfrontend.models.IE507a

import play.api.libs.json.*
import uk.gov.hmrc.automatedexportsystemfrontend.models.ModeOfTransportAtBorder
import uk.gov.hmrc.automatedexportsystemfrontend.xml.XmlWrites

import scala.xml.Text

// TODO fetch this mapping from CRDL rather than hardcoding
enum TransportMode(val value: Int) extends Enum[TransportMode] {
  case Sea extends TransportMode(1)
  case Rail extends TransportMode(2)
  case Road extends TransportMode(3)
  case Air extends TransportMode(4)
  case Post extends TransportMode(5)
  // Mode 6 appears to be intentionally missing in Europa
  case FixedInstallations extends TransportMode(7)
  case InlandWaterways extends TransportMode(8)
  case OwnPropulsion extends TransportMode(9)
}

object TransportMode {
  private val nameMap: Map[String, TransportMode] = values.map(v => v.name() -> v).toMap

  def fromUserAnswers(mode: ModeOfTransportAtBorder): TransportMode =
    mode match {
      case ModeOfTransportAtBorder.Sea  => TransportMode.Sea
      case ModeOfTransportAtBorder.Rail => TransportMode.Rail
      case ModeOfTransportAtBorder.Road => TransportMode.Road
      case ModeOfTransportAtBorder.Air  => TransportMode.Air
    }

  given reads: Reads[TransportMode] = Reads.of[String].flatMapResult { value =>
    if (nameMap.contains(value)) JsSuccess(nameMap(value))
    else JsError(s"Invalid TransportMode: $value")
  }

  given writes: Writes[TransportMode] =
    Writes.of[String].contramap(_.value.toString)

  given format: Format[TransportMode] = Format(reads, writes)

  given xmlWrites: XmlWrites[TransportMode] =
    XmlWrites.instance(t => Text(t.value.toString))
}
