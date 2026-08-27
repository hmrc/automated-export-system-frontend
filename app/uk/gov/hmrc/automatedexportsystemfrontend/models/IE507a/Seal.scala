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

case class Seal(
  sequenceNumber: Int, // Note: optional in the schema but we can easily provide it on behalf of the user
  identifier: String // Note: optional in the schema but mandatory in the journey
)

object Seal {
  given format: Format[Seal] = Json.format[Seal]

  given xmlWrites: XmlWrites[Seal] = XmlWrites.instance { s =>
    XmlWrites.elem("Seal", XmlWrites.textElem("sequenceNumber", s.sequenceNumber), XmlWrites.textElem("identifier", s.identifier))
  }
}
