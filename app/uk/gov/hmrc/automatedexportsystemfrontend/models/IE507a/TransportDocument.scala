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
import uk.gov.hmrc.automatedexportsystemfrontend.xml.XmlWrites

case class TransportDocument(sequenceNumber: Int, typeOfDocument: Option[Int], referenceNumber: Option[Int])

object TransportDocument {
  given format: Format[TransportDocument] = Json.format[TransportDocument]

  given xmlWrites: XmlWrites[TransportDocument] = XmlWrites.instance { t =>
    XmlWrites.elem(
      "TransportDocument",
      XmlWrites.textElem("sequenceNumber", t.sequenceNumber.toString),
      XmlWrites.optElem("type", t.typeOfDocument),
      XmlWrites.optElem("referenceNumber", t.referenceNumber)
    )
  }
}
