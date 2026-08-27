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

import play.api.libs.json.{Format, Json}
import uk.gov.hmrc.automatedexportsystemfrontend.xml.{XmlOps, XmlWrites}

case class Consignment(
  modeOfTransportAtBorder: TransportMode,
  referenceNumberUCR: String,
  parentUCRID: Option[String],
  TransportEquipment: List[TransportEquipment],
  LocationOfGoods: LocationOfGoods
)

object Consignment {
  given format: Format[Consignment] = Json.format[Consignment]

  given xmlWrites: XmlWrites[Consignment] = XmlWrites.instance { c =>
    XmlWrites.elem(
      "Consignment",
      XmlWrites.textElem("modeOfTransportAtTheBorder", c.modeOfTransportAtBorder.toXml),
      XmlWrites.textElem("referenceNumberUCR", c.referenceNumberUCR),
      XmlWrites.optElem("parentUCRID", c.parentUCRID),
      c.TransportEquipment.toXml,
      c.LocationOfGoods.toXml
    )
  }
}
